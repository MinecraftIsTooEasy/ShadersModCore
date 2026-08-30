# 实体与粒子渲染低风险优化调查

## 调查范围

本轮对照目标版本 `1.6.4-MITE` 的实体、粒子和纹理调用链，以及本地 OptiFine `1.8.9 HD U M6 pre2` 参考中的 `EntityUtils`、`SmartAnimations`、粒子渲染和 `RenderGlobal` 相关代码。目标侧没有可直接复用的 `net/optifine/render/RenderUtils.java`；本地参考的 `net/optifine/render` 目录只包含 `AabbFrame`、`Blender`、`ChunkVisibility`、`CloudRenderer`、`RenderEnv` 等类。

## 目标调用链与热路径

- `EntityRenderer.renderWorld` 在实体/地形阶段之后，先调用 `EffectRenderer.renderLitParticles(entity, partialTicks)`，再设置粒子雾并调用 `EffectRenderer.renderParticles(entity, partialTicks)`。这两个调用由目标 `1.6.4-MITE.jar` 的字节码和 `FishModLoader/src/mappings.tiny` 的 `EffectRenderer` 映射共同确认。
- 目标 `EffectRenderer` 的 `fxLayers` 是长度为 4 的一维 `List[]`。`renderParticles` 先读取相机旋转和 `EntityFX.interpPosX/Y/Z`，执行混合/透明度 GL 状态与视点方块查询，然后遍历第 0、1、2 层；每个非空层还会绑定粒子、方块或物品纹理并提交 Tessellator。
- `renderLitParticles` 只遍历第 3 层，不写入 `EntityFX.interpPosX/Y/Z`。目标 `EntityLargeExplodeFX.getFXLayer()` 返回 3，而其 `renderParticle` 使用这三个静态插值坐标。因此“只检查 0..2 层后跳过普通方法”会留下上一帧插值，造成可见位置错误。
- `RenderGlobal.renderEntities` 的现有注入只包围实体和方块实体的 shader pass；`RenderGlobalMixin.updateRenderers` 更新动态光照。`RenderMixin` 只控制实体阴影，`RenderParticleMixin` 只控制爆炸粒子生成，`EffectRendererMixin` 已控制方块破坏粒子生成，`EntityLivingBaseMixin` 控制药水粒子。这些位置包含 GL 状态或生成/更新副作用，不适合仅凭 OptiFine 代码复制做通用跳过。

## OptiFine 对照与兼容边界

- OptiFine 的 `EntityUtils` 主要建立实体名称到类/ID 的映射，调用点在资源/配置解析（例如 `EntityClassLocator`、自定义颜色）而非每帧实体渲染热路径。目标 `shadersmodcore.util.EntityUtils` 已有同等的启动期静态映射，本轮不改动。
- OptiFine Smart Animations 同时维护纹理和 sprite 的 BitSet，并依赖编译区块/渲染容器提供 sprite 使用信息。目标只有 `TextureManager` 绑定入口和 atlas tick，没有对应的 sprite animation index 或 chunk BitSet；当前 Smart Animations 切片只在纹理级追踪，并排除 shadow pass。把 OptiFine 的 sprite/区块逻辑直接移植会改变资源重载和动画更新契约，因此不纳入本轮实体候选。
- OptiFine `RenderGlobal` 的实体分层、相机剔除和新式渲染容器 API 与目标旧式 `WorldRenderer`/显示列表链不相容。没有能在目标 API 上证明等价的实体循环缓存或剔除切片。

## 选择的候选

目标 `renderParticles` 在四层都为空时仍会读取相机状态、写入插值坐标、查询视点方块并执行多次 GL 状态操作。新增 `ParticleRenderOptimizer.shouldRender(List<?>[] layers, boolean skipEmpty)`，仅在以下条件同时成立时取消 `renderParticles`：

1. 调用参数 `entity` 非空；
2. `OptimizeConfig.skipEmptyParticleRender` 开启；
3. `fxLayers` 恰好是四层，且四个列表都明确为空。

空检查是渲染线程上的 `List.isEmpty()`，不触碰粒子更新、生成、删除或 OpenGL。第 3 层也参与判断，以保证只存在发光/实体粒子时仍进入原版方法并更新插值坐标。目标字段若为空、长度变化或包含空列表，工具方法返回“继续原版”，以保留未知布局和异常行为的安全回退；`entity == null` 也直接落回原版，而不是改变其潜在异常。

开关 `skipEmptyParticleRender` 默认值为 `true`，写入现有 `optimize.txt`，并在粒子设置页提供关闭按钮。关闭开关会完全绕过取消逻辑；即使开关开启，任一层有粒子也保持原版方法和原有 GL/粒子行为。

## 实现文件

- `src/main/java/shadersmodcore/client/optimize/ParticleRenderOptimizer.java`：纯逻辑判断及未知数组回退。
- `src/main/java/shadersmodcore/mixin/particle/EffectRendererMixin.java`：`renderParticles` 入口取消注入，保留既有方块破坏粒子注入。
- `src/main/java/shadersmodcore/config/OptimizeConfig.java`、`GuiParticle.java`、中英文语言文件：开关的读取、持久化和 UI。
- `src/test/java/shadersmodcore/client/optimize/ParticleRenderOptimizerTest.java`：失败优先 fixture 覆盖空层、普通层、第 3 层、关闭开关、空/短/超长数组回退。

## 线程、GL 与风险

`fxLayers` 只在客户端渲染线程被读取；新增代码不创建线程、不共享可变缓存，也不调用 GL。注入点位于原版插值计算和所有 GL 状态修改之前。完全为空时没有粒子可消费这些状态，取消不会跳过粒子更新或发射；非空和布局未知时仍执行原版。实际客户端的 shader pass 状态、模组粒子实现和帧时间收益尚未通过游戏运行或 profiler 验证。

## 验证记录

Java 17 直接编译并执行以下无 Minecraft/GL fixture：

```text
SmartAnimationsTest passed
DynamicLightQueryCacheTest passed
ConfigParsingTest passed
ParticleRenderOptimizerTest passed
```

变更的 Mixin、配置和 GUI 类也用 Java 17 `javac --release 17 -proc:none` 做过语法/类型编译；独立注解处理无法在缺少 Loom 映射上下文的裸调用中完成。`git diff --check` 通过。

Gradle 验证未成功：默认 Java 25 在缓存脚本上报 class major version 69；切换 Java 17 的 wrapper 无法访问用户 Gradle 缓存锁；使用工作区 Gradle 用户目录时 daemon 因绑定 socket 收到 `java.net.SocketException: Operation not permitted`。因此没有把现有 `build/` 目录中的旧 class/jar 视为本轮构建结果，也不宣称 `test`/`build` 成功。
