# 累计性能移植最终阶段性审查

## 审查基线

本次审查重点覆盖 `7e553b3`、`cb013e8`、`e98eb2d` 和 `e8507b9`，并创建了独立的中文 Conventional Commit 修复空值回归；累计性能基线仍包含：

`a35a80c`、`e38b621`、`40f6a7f`、`4689e91`、`c9777c7`、`f3fddb4`。

逐个检查目标提交的 `git diff-tree --name-status` 后，变更均位于以下范围：

- `src/main/java`、`src/main/resources`：性能切片及其配置/UI/Mixin 接入；
- `src/test/java`：十个无 OpenGL 上下文依赖的独立 fixture；
- `docs/research`：切片研究记录；
- `build.gradle`：十个 fixture 的 `JavaExec` 任务及 `test` 依赖。

目标提交和本轮修复没有包含 `FishModLoader/`、`Optifine SRC Version [1.8.9 HD U M6 pre2]/`、`build/`、`logs/`、`.gradle/`，也没有把 `.class`、`.jar` 等生成物纳入提交。上述目录在当前工作树中的未提交状态按要求保留。

## 跨切片配置与任务

| 配置/任务 | 默认值与接入 | 关闭/异常语义 |
| --- | --- | --- |
| `dynamicLightsFast` | `OptimizeConfig` 读取并持久化，默认 `false`；`ShaderConfig.isDynamicLightsFast()` 直接委托；动态光照实体轮询节流为 500 ms | `dynamicLights=false` 时 `ChunkCacheMixin` 完全走原版返回值；节流只影响轮询频率，不改变亮度合并公式 |
| `smartAnimations` | `OptimizeConfig` 读取并持久化，默认 `false`；配置加载和 GUI 切换都调用 `SmartAnimations.setEnabled` | 未启用、shadow pass、尚无追踪快照、未知 texture ID 均保留动画原版路径；已追踪但本帧未绑定的 atlas 才跳过；tick 和资源重载会清理对应状态 |
| `skipEmptyParticleRender` | `OptimizeConfig` 读取并持久化，默认 `true`；粒子设置页可切换 | 关闭开关、`entity == null`、层数组不是四层、任一层为 `null` 或非空时均保留原版；第 3 层参与判定，避免发光粒子依赖的插值状态回归 |

十个独立任务分别为 `smartAnimationsTest`、`dynamicLightQueryCacheTest`、`dynamicLightBoundaryTest`、`dynamicLightRevisionTest`、`dynamicLightScanTest`、`shadersTexResourcePathTest`、`shadersTexResourceFallbackTest`、`configParsingTest`、`particleRenderOptimizerTest` 和 `tessellatorBufferGrowthTest`。每个任务只依赖共享的 `testClasses`；`test` 依赖这十个任务，没有发现重复注册或循环依赖。

## 语义审查结论

- Smart Animations 的纹理级 BitSet、shadow 排除、启停重置和 `try/finally` 更新上下文均保持关闭/回退安全。当前切片没有移植 OptiFine 的 sprite/chunk BitSet，因此收益范围小于完整 OptiFine，但未发现明确的正确性回归。
- 动态光照查询缓存使用线程本地直映射表，键包含坐标、原始合并亮度和光照 revision；碰撞只导致 miss，实体移动/亮度变化/移除和世界清空会推进 revision，稳定检查周期不再无条件失效。关闭动态光照时缓存入口不会执行。
- 配置读取在加载前清空属性集；文件不存在时先应用默认值再写盘；整数/浮点损坏值回退，布尔值保留 `Boolean.parseBoolean` 的兼容语义。未发现需要在本轮修复的明确异常路径。
- 空粒子优化只在四层都明确为空时取消普通粒子渲染，未触碰粒子更新、生成、删除或 OpenGL 状态；非空和未知布局均回退原版。
- 辅助纹理资源加载对空位置、缺失资源、非图像流和不可读输入均回退默认页，并关闭输入流；尺寸匹配的有效图像仍按原偏移复制，像素数组边界错误不被吞掉。

早期性能切片未发现明确正确性回归；本次目标提交的额外审查发现并修复了纹理资源路径的空值兼容回归，后续切片及其实现记录如下。

## 后续切片：动态光照最大亮度边界

在上述审查后继续对照本地 OptiFine SRC，确认动态光照只会提高组合光照值低字节中的方块亮度。标准方块亮度 15 编码为 `0xF0`；在该边界上继续扫描动态光源或查询线程本地缓存不可能改变返回值。本轮新增 `DynamicLights.canSkipDynamicLightQuery`，在 `getCombinedLightCached` 的缓存读取前、共享的 `getCombinedLight(BlockPos, int)` 以及实体 overload 中对标准 `0xF0` 做早退。

早退只接受非负且低字节恰为 `0xF0` 的编码，负值、低于最大值和非标准低字节均回退原路径；动态光照关闭时 Mixin 仍直接返回原版亮度。新增失败优先 fixture `DynamicLightBoundaryTest`，覆盖天空光高位、低值和异常输入。详细调查记录见 `docs/research/dynamic-light-boundary.md`。

动态光照切片后独立 fixture 共七个：`smartAnimationsTest`、`dynamicLightQueryCacheTest`、`dynamicLightBoundaryTest`、`dynamicLightRevisionTest`、`dynamicLightScanTest`、`configParsingTest` 和 `particleRenderOptimizerTest`。

## 后续切片：动态光照 revision 惰性失效

继续检查查询缓存的失效频率时发现，`DynamicLights.update` 每 50 ms 无条件递增 `lightRevision`。即使实体位置、亮度和水下状态均未变化，所有线程本地坐标缓存也会被迫失效，下一轮区块编译重新扫描动态光源。

本轮保留 `DynamicLight.update(RenderGlobal)` 的公开 `void` 签名，增加同包 `updateAndReport` 变化报告；实体映射新增/移除也返回变化标记。`DynamicLights.update` 仅在光源快照实际变化时推进 revision；实体移除和全量清空继续显式推进。详细调查记录见 `docs/research/dynamic-light-revision.md`。

新增失败优先 fixture `DynamicLightRevisionTest`，覆盖稳定 revision、变化递增和无变化边界；动态光照查询缓存、亮度边界和其它既有 fixture 保持不变。

## 后续切片：动态光照扫描无 Iterator 分配

继续检查缓存未命中路径时发现，`DynamicLights.getLightLevel(BlockPos)` 对不可变光源快照使用增强 `for`，每次查询都会创建 Iterator。对照 OptiFine 的索引循环，本轮新增同包列表重载并按 `size/get` 扫描，消除该分配；光照计算和快照发布语义不变。详细调查记录见 `docs/research/dynamic-light-scan.md`。

新增失败优先 fixture `DynamicLightScanTest`，用拒绝 `iterator()` 的列表验证扫描只使用索引访问。

## 后续修复：法线/高光纹理资源路径

继续检查纹理更新路径时发现 `ShadersTex.getNSMapLocation` 的 `split(".png")` 正则通配符会错误截断包含 `png` 片段的合法文件名，例如 `pngstone.png`。本轮新增 `ShadersTexResourcePathTest` 先确认旧实现失败，再改为字面后缀移除；资源域、无后缀路径及已有 `_n.png`/`_s.png` 命名规则保持不变。审查另发现字面后缀修复遗漏了 OptiFine 的空位置回退，已补回 `null` 保护并以失败优先断言覆盖。详细记录见 `docs/research/texture-resource-path.md`。

本轮后独立 fixture 共八个，新增 `shadersTexResourcePathTest`。

## 后续修复：辅助纹理资源回退

继续检查法线/高光辅助图的 CPU 资源路径时发现，目标 `ShadersTex.loadNSMap1` 对 `ResourceManager.getResource` 的运行时缺失异常和 `ImageIO.read` 返回的空图像没有保护；空位置也会在默认填充前中止。对照 OptiFine 的 `loadNSMapFile`，本轮先加入失败优先 `ShadersTexResourceFallbackTest`，再让查找/解码失败回退原有默认颜色，并以 try-with-resources 关闭输入流。详细记录见 `docs/research/texture-resource-fallback.md`。

本轮后独立 fixture 共十个，新增 `shadersTexResourceFallbackTest`。

## 已验证证据

既有环境记录中，Java 17（`Zulu 17.0.20.1`）配合 Loom 映射 classpath 曾完成全部主/测试源码编译。本轮使用本地 merged jar 与 FishModLoader all-in jar，以 `javac --release 17 -proc:none` 编译全部 `src/main/java` 与 `src/test/java`，主源码和测试源码均成功；此前仅使用未应用 access widener 的 merged jar 的尝试仍会报告 `Tessellator.convertQuadsToTriangles`、`RenderManager.entityRenderMap`、`RendererLivingEntity` 模型字段和 `ModelRenderer` 字段访问错误，这些是 classpath 限制而非本轮新增。随后使用同一 Java 17 和本地依赖运行十个 fixture，全部通过：

```text
SmartAnimationsTest passed
DynamicLightQueryCacheTest passed
DynamicLightBoundaryTest passed
DynamicLightRevisionTest passed
DynamicLightScanTest passed
ShadersTexResourcePathTest passed
ShadersTexResourceFallbackTest passed
ConfigParsingTest passed
ParticleRenderOptimizerTest passed
TessellatorBufferGrowthTest passed
```

`git diff --check` 通过。

项目 Gradle 任务也做了真实尝试，但受环境阻塞：

- 默认 Java 25 执行 `./gradlew --no-daemon test` 在配置阶段报 `Unsupported class file major version 69`。
- Java 17 执行 `./gradlew --no-daemon test` 时 wrapper 无法打开全局 `gradle-8.5-bin.zip.lck`，报 `FileNotFoundException (Operation not permitted)`；将 `GRADLE_USER_HOME` 隔离到工作区后，wrapper 下载又因网络限制报 `UnknownHostException: services.gradle.org`。
- 直接调用本机缓存的 Gradle 8.5、Java 17 并使用工作区 `.gradle` 执行 `test` 与 `build` 时，daemon 均因沙箱禁止绑定 socket 报 `java.net.SocketException: Operation not permitted`；改用全局 Gradle 缓存还会在 native service 初始化时报 `Failed to load native library 'libnative-platform.dylib'`。

因此没有把 `build/` 中已有的 class/jar 当作本轮 Gradle 产物，也没有宣称 Gradle `test` 或 `build` 成功。

## 未验证范围与后续建议

本轮没有启动游戏、加载 shader pack 或建立 OpenGL 上下文，未验证 Mixin 运行时织入、实际渲染状态、动态光照节流观感、动画帧连续性、粒子视觉结果、缓存命中率、帧时间或 FPS。完整验收仍需要在可运行 Java 17/Gradle 环境中执行 `./gradlew test build`，再用真实客户端和 profiler 对比：

1. Smart Animations 开关、资源重载、shadow pass 与多个 atlas 的动画连续性；
2. 动态光照实体移动/熄灭时的更新延迟及查询缓存命中率；
3. 四层粒子为空、仅有第 3 层粒子以及混合层时的渲染状态和位置；
4. 首次启动、重复加载和损坏配置文件的默认值与落盘权限；
5. 动态光照实体静止与移动场景的 revision 稳定性、缓存命中率和区块编译帧时间；
6. Tessellator 非标准初始容量、quad-to-triangle 转换及最大容量 flush 后的顶点连续性和颜色/法线/UV 视觉一致性。

## 后续切片：Tessellator 顶点缓冲容量与 scratch

继续对照本地 OptiFine `SVertexBuilder` 和目标 `ShadersTess` 时发现，目标用共享静态容量判断每个实例的 `rawBuffer`，实例扩容后可能使新实例在数组尾部越界；四顶点法线计算还为只使用的 16 个坐标槽位分配了完整构造容量的 float 数组。本轮新增 `TessellatorBufferGrowth`，按实例 `rawBuffer.length` 扩容、达到上限 flush 后 reset，并固定 16-float scratch，同时把法线/中间 UV 打包值缓存到局部变量。顶点步长、颜色编码、法线计算和 OpenGL 布局没有改变。失败优先 fixture 为 `TessellatorBufferGrowthTest`，对应研究记录见 `docs/research/tessellator-buffer-growth.md`。

## 复核修复：最大容量扩容边界

对 `e8507b9` 的最大容量路径做完整顶点序列复核时，发现非标准初始容量可能在扩容到 `MAX_CAPACITY` 后留下 `rawBufferIndex == MAX_CAPACITY - 64` 的四边形起点。原路径在同一次 `addVertex` 中直接写入，第四个顶点的 quad-to-triangle 第二次复制会越过数组尾部；该状态不由已有 helper fixture 覆盖。新增失败优先断言后，`ShadersTess.addVertex` 在扩容到最大容量后重新使用保留区和四边形边界判定，必要时先 `draw()`、`reset()` 并恢复 `isDrawing`，再继续写入。部分四边形不在边界时仍留在 64-word 保留区中。修复提交为 `398a18f`（`fix: 修复顶点缓冲扩容至上限时的四边形越界`）。详细记录见 `docs/research/tessellator-buffer-growth.md`。
