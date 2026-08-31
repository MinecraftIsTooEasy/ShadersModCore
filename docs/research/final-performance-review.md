# 累计性能移植最终阶段性审查

## 审查基线

累计审查重点覆盖 `7e553b3`、`cb013e8`、`e98eb2d` 和 `e8507b9`；本轮追加复核 `1cba805`，发现辅助纹理缺失异常回退缺口并创建中文 Conventional Commit `8a46189`。累计性能基线仍包含：

`a35a80c`、`e38b621`、`40f6a7f`、`4689e91`、`c9777c7`、`f3fddb4`。

逐个检查目标提交的 `git diff-tree --name-status` 后，变更均位于以下范围：

- `src/main/java`、`src/main/resources`：性能切片及其配置/UI/Mixin 接入；
- `src/test/java`：十八个无 OpenGL 上下文依赖的独立 fixture；
- `docs/research`：切片研究记录；
- `build.gradle`：十八个 fixture 的 `JavaExec` 任务及 `test` 依赖。

目标提交和本轮修复没有包含 `FishModLoader/`、`Optifine SRC Version [1.8.9 HD U M6 pre2]/`、`build/`、`logs/`、`.gradle/`，也没有把 `.class`、`.jar` 等生成物纳入提交。上述目录在当前工作树中的未提交状态按要求保留。

## 跨切片配置与任务

| 配置/任务 | 默认值与接入 | 关闭/异常语义 |
| --- | --- | --- |
| `dynamicLightsFast` | `OptimizeConfig` 读取并持久化，默认 `false`；`ShaderConfig.isDynamicLightsFast()` 直接委托；动态光照实体轮询节流为 500 ms | `dynamicLights=false` 时 `ChunkCacheMixin` 完全走原版返回值；节流只影响轮询频率，不改变亮度合并公式 |
| `smartAnimations` | `OptimizeConfig` 读取并持久化，默认 `false`；配置加载和 GUI 切换都调用 `SmartAnimations.setEnabled` | 未启用、shadow pass、尚无追踪快照、未知 texture ID 均保留动画原版路径；已追踪但本帧未绑定的 atlas 才跳过；tick 和资源重载会清理对应状态 |
| `skipEmptyParticleRender` | `OptimizeConfig` 读取并持久化，默认 `true`；粒子设置页可切换 | 关闭开关、`entity == null`、层数组不是四层、任一层为 `null` 或非空时均保留原版；第 3 层参与判定，避免发光粒子依赖的插值状态回归 |

十八个独立任务分别为 `smartAnimationsTest`、`dynamicLightQueryCacheTest`、`dynamicLightBoundaryTest`、`dynamicLightRevisionTest`、`dynamicLightScanTest`、`dynamicLightEntityFallbackTest`、`dynamicLightCoordinateQueryTest`、`dynamicLightChunkUpdateTest`、`shadersTexResourcePathTest`、`shadersTexResourceFallbackTest`、`shadersTexLayeredResourceTest`、`textureAtlasSpriteResourceLifecycleTest`、`shadersTexTextureLifecycleTest`、`textureManagerTextureReplacementTest`、`shaderPackResourcePathTest`、`configParsingTest`、`particleRenderOptimizerTest` 和 `tessellatorBufferGrowthTest`。每个任务只依赖共享的 `testClasses`；`test` 依赖这十八个任务，没有发现重复注册或循环依赖。

## 语义审查结论

- Smart Animations 的纹理级 BitSet、shadow 排除、启停重置和 `try/finally` 更新上下文均保持关闭/回退安全。当前切片没有移植 OptiFine 的 sprite/chunk BitSet，因此收益范围小于完整 OptiFine，但未发现明确的正确性回归。
- 动态光照查询缓存使用线程本地直映射表，键包含坐标、原始合并亮度和光照 revision；碰撞只导致 miss，实体移动/亮度变化/移除和世界清空会推进 revision，稳定检查周期不再无条件失效。关闭动态光照时缓存入口不会执行。
- 配置读取在加载前清空属性集；文件不存在时先应用默认值再写盘；整数/浮点损坏值回退，布尔值保留 `Boolean.parseBoolean` 的兼容语义。未发现需要在本轮修复的明确异常路径。
- 空粒子优化只在四层都明确为空时取消普通粒子渲染，未触碰粒子更新、生成、删除或 OpenGL 状态；非空和未知布局均回退原版。
- 辅助纹理资源加载对空位置、实际 `FileNotFoundException`、空资源/空流、非图像流和不可读输入均回退默认页，并关闭输入流；资源查找/读取仅捕获 `IOException`，manager/stream 编程异常和像素数组边界错误不被吞掉。尺寸匹配的有效图像仍按原偏移复制。
- 动态光照实体 overload 对空实体保留原始亮度；世界坐标查询复用线程本地缓存，区块标记使用实例级 24-int scratch，均不改变亮度公式、revision 或标记顺序。
- 分层纹理读取使用 try-with-resources；空/非图像层安全跳过，全部无效时不触发空数组上传；有效层的三页合成保持原路径。
- shader pack 的 folder/zip 资源路径接受可选首 `/`（folder 另接受尾 `/`）；缺失、目录和空路径安静回退为 `null`，zip 仅接受根 `shaders/` 或唯一顶层目录，歧义候选不会按顺序误选；有效资源流仍交给调用方关闭。
- 纹理替换生命周期按 `ordinal = 0/1`、各 `require = 1`/`expect = 1` 包装 MITE `loadTexture` 内的两次 `Map.put`；尾部通过 `@Local(index = 3)` 的加载标志确认成功后才清理。IOException 仍返回 `false`；已有旧对象时两次 missing 写入均保留旧 map/list 纹理，避免新加载失败破坏可用回退，首次失败仍保留原版 missing 回退。旧对象存在其它资源位置的身份别名、是同一对象或为共享 `missingTexture` 时不释放，map/list 清理均按对象身份比较；`ShadersTex.deleteTextures` 只对正的运行时 GL ID 发出删除调用。

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

## 复核 1cba805：辅助纹理资源回退

对照 OptiFine 的 `loadNSMapFile` 复核 `1cba805` 时确认：该提交虽处理了空位置、空图像和部分运行时异常，但实际 MITE `FallbackResourceManager` 抛出的 `FileNotFoundException` 不是 `RuntimeException`，真实缺失资源仍会中断；宽泛 `RuntimeException` 捕获还会吞掉 manager/stream 的编程错误。先扩展失败优先 `ShadersTexResourceFallbackTest` 并在目标提交上确认真实缺失失败，再以声明 `throws IOException` 的 helper 捕获资源查找/读取异常、显式处理空输入流，并保留 try-with-resources。`getRGB` 和默认 `Arrays.fill` 在捕获范围外，数组边界错误继续抛出；原有偏移、三页布局和 OpenGL 路径未改变。详细调查记录见 `docs/research/texture-resource-fallback.md`。

本轮后独立 fixture 共九个，新增 `shadersTexResourceFallbackTest`；随后顶点缓冲 fixture 使当前总数达到十个。

## 本轮交付

审查结论：`1cba805` 未覆盖实际 `FileNotFoundException`，且宽泛 `RuntimeException` 捕获会吞掉 manager/stream 编程错误，属于明确缺陷。

实际修改：`ShadersTex.loadNSMap1` 仅捕获资源查找/读取 `IOException`，显式处理空输入流并保留 try-with-resources；新增失败优先边界 fixture；未改变 OpenGL 调用、纹理页偏移或布局。研究记录同步更新。

提交：`8a46189`（`fix: 修复辅助纹理资源缺失异常回退`）。

## 已验证证据

Java 17（`Zulu 17.0.20.1`）使用本地 Loom merged Minecraft jar、FishModLoader classpath 和 `javac --release 17 -proc:none` 已完成全部 `src/main/java` 与 `src/test/java` 独立编译；随后以同一 classpath 运行十八个 fixture，全部通过：

```text
SmartAnimationsTest passed
DynamicLightQueryCacheTest passed
DynamicLightBoundaryTest passed
DynamicLightRevisionTest passed
DynamicLightScanTest passed
DynamicLightEntityFallbackTest passed
DynamicLightCoordinateQueryTest passed
DynamicLightChunkUpdateTest passed
ShadersTexResourcePathTest passed
ShadersTexResourceFallbackTest passed
ShadersTexLayeredResourceTest passed
TextureAtlasSpriteResourceLifecycleTest passed
ShadersTexTextureLifecycleTest passed
TextureManagerTextureReplacementTest passed
ShaderPackResourcePathTest passed
ConfigParsingTest passed
ParticleRenderOptimizerTest passed
TessellatorBufferGrowthTest passed
```

`git diff --check` 通过。

项目 Gradle 任务也做了真实尝试，但受环境阻塞：

- 默认 Java 25 执行 `./gradlew --no-daemon test` 在配置阶段报 `Unsupported class file major version 69`。
- Java 17 执行 `./gradlew --no-daemon test` 时 wrapper 无法打开全局 `gradle-8.5-bin.zip.lck`，报 `FileNotFoundException (Operation not permitted)`；将 `GRADLE_USER_HOME` 隔离到工作区后，wrapper 下载又因网络限制报 `UnknownHostException: services.gradle.org`。
- 直接调用本机缓存的 Gradle 8.9、Java 17 并使用全局缓存时，初始化 native service 报 `Failed to load native library 'libnative-platform.dylib'`；改用工作区 `.gradle` 执行 `test` 与 `build` 时，daemon 均因沙箱禁止绑定 socket 报 `java.net.SocketException: Operation not permitted`。

因此没有把 `build/` 中已有的 class/jar 当作本轮 Gradle 产物，也没有宣称 Gradle `test` 或 `build` 成功。

## 未验证范围与后续建议

本轮没有启动游戏、加载 shader pack 或建立 OpenGL 上下文，未验证 Mixin 运行时织入、实际渲染状态、动态光照节流观感、动画帧连续性、粒子视觉结果、缓存命中率、帧时间或 FPS。完整验收仍需要在可运行 Java 17/Gradle 环境中执行 `./gradlew test build`，再用真实客户端和 profiler 对比：

1. Smart Animations 开关、资源重载、shadow pass 与多个 atlas 的动画连续性；
2. 动态光照实体移动/熄灭时的更新延迟及查询缓存命中率；
3. 四层粒子为空、仅有第 3 层粒子以及混合层时的渲染状态和位置；
4. 首次启动、重复加载和损坏配置文件的默认值与落盘权限；
5. 动态光照实体静止与移动场景的 revision 稳定性、缓存命中率和区块编译帧时间；
6. Tessellator 非标准初始容量、quad-to-triangle 转换及最大容量 flush 后的顶点连续性和颜色/法线/UV 视觉一致性。
7. 分层纹理资源重载时的文件句柄回收、空层行为和实际 OpenGL 纹理上传结果；实体为空时的渲染入口调用兼容性。
8. 世界坐标查询缓存命中率，以及大量动态光源变化时区块标记 scratch 对更新耗时的影响。

## 后续切片：Tessellator 顶点缓冲容量与 scratch

继续对照本地 OptiFine `SVertexBuilder` 和目标 `ShadersTess` 时发现，目标用共享静态容量判断每个实例的 `rawBuffer`，实例扩容后可能使新实例在数组尾部越界；四顶点法线计算还为只使用的 16 个坐标槽位分配了完整构造容量的 float 数组。本轮新增 `TessellatorBufferGrowth`，按实例 `rawBuffer.length` 扩容、达到上限 flush 后 reset，并固定 16-float scratch，同时把法线/中间 UV 打包值缓存到局部变量。顶点步长、颜色编码、法线计算和 OpenGL 布局没有改变。失败优先 fixture 为 `TessellatorBufferGrowthTest`，对应研究记录见 `docs/research/tessellator-buffer-growth.md`。

## 复核修复：最大容量扩容边界

对 `e8507b9` 的最大容量路径做完整顶点序列复核时，发现非标准初始容量可能在扩容到 `MAX_CAPACITY` 后留下 `rawBufferIndex == MAX_CAPACITY - 64` 的四边形起点。原路径在同一次 `addVertex` 中直接写入，第四个顶点的 quad-to-triangle 第二次复制会越过数组尾部；该状态不由已有 helper fixture 覆盖。新增失败优先断言后，`ShadersTess.addVertex` 在扩容到最大容量后重新使用保留区和四边形边界判定，必要时先 `draw()`、`reset()` 并恢复 `isDrawing`，再继续写入。部分四边形不在边界时仍留在 64-word 保留区中。修复提交为 `398a18f`（`fix: 修复顶点缓冲扩容至上限时的四边形越界`）。详细记录见 `docs/research/tessellator-buffer-growth.md`。

## 后续修复：动态光照实体空值回退

继续复核实体光照 Mixin 边界时发现，`DynamicLights.getCombinedLight(Entity, int)` 对空实体只绕过最大亮度早退，随后仍调用 `getLightLevel(null)`；在 Minecraft 单例尚未初始化或调用方没有实体时会抛出 `NullPointerException`，而不是保留原版亮度。新增 `DynamicLightEntityFallbackTest` 先复现该失败，再让实体 overload 对 `null` 直接返回输入亮度；非空实体和动态光照合并公式未改变。详细记录见 `docs/research/dynamic-light-entity-fallback.md`。

## 后续修复：分层纹理资源流

继续复核纹理资源生命周期时发现，`ShadersTex.loadLayeredTexture` 每层读取后没有关闭输入流，且空/非图像资源会在读取尺寸时抛出 `NullPointerException`。新增 `ShadersTexLayeredResourceTest` 先确认缺少 helper 时编译失败，再以 try-with-resources 实现分层图像读取；空层跳过、全部无效时不上传空数组，有效图像的三页合成和 OpenGL 上传顺序保持不变。详细记录见 `docs/research/texture-layered-resource.md`。

## 后续切片：动态光照世界坐标查询

继续复核动态光照查询入口时发现，`WorldMixin` 尚未复用区块缓存路径，每次动态光照
查询都会新建 `BlockPos` 并扫描快照。本轮新增四整数坐标 overload，直接委托已有
线程本地查询缓存，并让 `WorldMixin` 传入原始坐标；`DynamicLightCoordinateQueryTest`
先覆盖缺少 overload 的编译失败，再验证无光源和最大亮度边界原样返回。缓存键、revision、
动态光照开关和亮度合并公式未改变。详细记录见 `docs/research/dynamic-light-coordinate-query.md`。

## 本轮修复：Shader Pack 资源路径

对照本地 OptiFine `ShaderPackFolder` 与 `ShaderPackZip` 时发现两个实现都固定
`substring(1)`，无前导 `/` 的合法 shader 路径会丢失首字符；zip 还只查找根目录，带有单一
顶层目录的常见压缩包无法读取；缺失资源还会打印异常。先扩展 `ShaderPackResourcePathTest`
覆盖 folder/zip 失败边界，再让 folder 去除可选首尾 `/`、zip 去除可选首 `/` 并探测根目录或
单一顶层目录下的 `shaders/`，同时对空/缺失/目录/不可访问资源返回 `null`。有效文件的
`BufferedInputStream`、zip 的惰性 `ZipFile` 和调用方关闭责任保持不变；详细记录见
`docs/research/shader-pack-resource-path.md`。

## Shader Pack 专项复核：f463c11 / 57c60f5 / e6dfeb0

### 审查结论

复核确认 folder 的可选首尾 `/`、zip 的可选首 `/`、缺失/非法 zip 回退和流关闭责任均
符合现有 OptiFine 兼容语义。新增失败优先边界发现：zip 目录 entry 原实现可被当作资源
流返回；`detectBaseFolder` 对多个顶层 `*/shaders/` 按 entry 顺序取第一个，可能选错资源；
`catch (Exception)` 会把空 zip 文件参数等编程错误吞成资源缺失。

### 文件修改

`ShaderPackFolder.java` 与 `ShaderPackZip.java` 仅捕获 `IOException`/`SecurityException`，
拒绝目录 entry，并要求顶层 shader 目录候选唯一；`ShaderPackResourcePathTest.java` 覆盖
目录、歧义顶层目录、非法 zip、close 后惰性重开和异常传播。`shader-pack-resource-path.md`
与本报告同步调查证据；`build.gradle` 的 `shaderPackResourcePathTest` 任务及 `test` 依赖
保持现有 fixture 接线，并新增纹理替换回退任务。

### 提交

本轮创建中文 Conventional Commit：`fix: 收紧着色器资源路径回退边界`。

### 运行时/OpenGL/FPS 验证

未启动游戏、加载真实 shader pack 或建立 OpenGL 上下文；未测量资源句柄、帧时间或 FPS。
folder/zip 的真实客户端加载、shader 编译失败回退和运行时视觉结果仍需在可运行 Java 17
客户端中验证。

## b210da9 纹理生命周期专项复核

### 审查结论

MITE `TextureManager.loadTexture` 的 map 写入发生在新对象加载之后：成功路径在方法尾部
写入，IOException 路径先写共享 `TextureUtil.missingTexture`、随后仍执行方法尾部写入，
其它异常包装为 `ReportedException` 抛出。Loom merged jar 的 `javap` 显示这两个调用分别
是 ordinal 0/1，`var3` 在尾部表示加载是否成功。b210da9 原实现从 HEAD 移除并释放旧对象，因而
新对象 IOException 时旧纹理已不可回退，属于明确生命周期 bug。`multiTexMap`、身份别名、
`listTickables` 和 `missingTexture` 的释放边界也需要在成功结果之后
判断。primitive dynamic-light cache miss 与 `BlockPos` 路径逐项复核，坐标转换、距离、
水下衰减、亮度合并和 revision 键完全等价，未发现 dynamic-light 数学回归。
Loom merged jar 的 `loadTexture(ResourceLocation, TextureObject): boolean`、
`mappings.tiny` 字段名和 `AbstractTextureAccessor` 方法签名均与 Mixin 声明一致；真实
织入仍未在客户端执行。

### 修改

`TextureManagerMixin` 现在分别以 ordinal 0/1 包装两次 map put（各 `expect = 1`），并由
尾部 `@Local(index = 3)` 的 `var3` 区分 IOException 与成功。missing 回退时跳过覆盖已有
旧对象，成功替换后才移除未被其它资源位置身份别名引用的旧 tick 项并调用
`ShadersTex.deleteTextures`。首次加载仍保留原版 missing 回退；同一对象重载、资源位置
别名和 shared missing 均不会误释放，也不依赖不存在的 `THROW` injection point。
FishModLoader 内置 MixinExtras 为 0.3.5，与当前 `WrapOperation` 和 `@Local` 签名匹配。
`ShadersTex.deleteMultiTex` 的非 `AbstractTexture` 分支也增加正 ID 守卫；`ShadersTexTextureLifecycleTest` 新增 0/负 GL ID 边界，`TextureManagerTextureReplacementTest`
覆盖失败回退、失败后恢复、成功替换、同一对象、shared missing、别名、重复 tick 项和身份
比较边界，`DynamicLightCoordinateQueryTest` 增加
真实动态光源下的对象/primitive 结果等价断言。`build.gradle` 接入第十七个 fixture 任务。

### 测试

Java 17 对本轮相关源码和上述 fixture 的独立编译/执行通过；`git diff --check` 通过。
全量编译使用 `-proc:none`，未执行 Mixin 织入或 access widener 处理；它证明源码和 fixture
在 Java 17 classpath 下可编译，不等同于运行时注入验证。

### Gradle

已真实尝试 `./gradlew --no-daemon test` 和 `build`。默认 Java 25 在配置阶段报
`Unsupported class file major version 69`；Java 17 wrapper 受全局锁文件权限、隔离缓存
网络不可用（`UnknownHostException: services.gradle.org`）阻塞。使用本机 Gradle 缓存时，
全局缓存报 native platform 动态库加载失败，工作区缓存的 daemon 又因沙箱禁止绑定 socket
失败。因此本轮不宣称 Gradle `test` 或 `build` 成功，也未把现有 `build/` 产物计入证据。

### 提交

提交：`fix: 修复纹理替换失败回退`（中文 Conventional Commit）。提交仅包含
本轮 8 个目标文件；用户已有的 `gradlew`、参考目录和生成目录均未纳入。

### 运行时/OpenGL/FPS 验证

未启动游戏、未执行 Mixin 运行时织入、未建立 OpenGL 上下文或加载真实资源；GL 删除调用、
资源重载视觉结果、dynamic-light 命中率、帧时间和 FPS 均未测量。需要在可运行 Java 17
客户端中继续验证真实纹理替换/重载、异常回退及渲染性能。

## b086d02 最终纹理生命周期专项审查（2026-08-31）

### 审查

未发现新的明确 bug，未改动 `src/main`、未新增失败 fixture。未映射 Loom merged jar 的
`javap` 类名是 `bim`；`bim.a(bjo, bio):Z` 在 IOException catch 内和方法尾部各有一次
`Map.put`（ordinal 0/1），LVT slot 3 为 `var3:boolean`。映射 jar 将同一方法显示为
`net.minecraft.TextureManager.loadTexture(ResourceLocation, TextureObject): boolean`，
`mapTextureObjects`/`listTickables` 字段与 Mixin 声明一致。FishModLoader 的
MixinExtras 版本枚举包含 `0.3.5`；`Operation<Object>` 的接收者、两个 Map 参数和尾随
`Operation` 签名符合 MixinExtras 0.3.5，Java 17 注解处理器编译 `TextureManagerMixin`
通过。真实 Mixin 织入仍未执行。

失败回退、成功替换、同一对象、shared `missingTexture`、资源位置身份别名、重复
`listTickables` 及 `equals` 相等但非同一对象均按身份和成功标志检查，map/list/GL 清理
时序正确。`ShadersTex.deleteTextures`/`deleteMultiTex` 对 `-1`、`0` 只做状态解绑或
跳过 GL 删除，对正 ID 才调用删除；`MultiTexID.base` 不一致会报告并仅删除正的异常 ID，
非 `AbstractTexture` 分支同样有正 ID 守卫。primitive dynamic-light cache miss 与
`BlockPos` 路径的坐标、距离、水下衰减、合并公式和 revision 语义一致。

### 修改

本轮仅更新本报告和 `texture-lifecycle.md` 的事实记录；没有代码修复，因此没有失败优先
fixture 或新的修复提交。

### 测试

Java 17（Zulu 17.0.20.1）使用映射 Loom merged jar、FishModLoader 3.4.4-all 和
legacy classpath，对全部 `src/main/java` 与 `src/test/java` 执行
`javac --release 17 -proc:none`，通过；按 `build.gradle` 接线运行全部 17 个 fixture，
全部通过；`git diff --check` 通过。fixture 未创建 Minecraft/OpenGL 上下文。

### Gradle

`JAVA_HOME=.../zulu-17 ./gradlew --no-daemon test` 与 `build` 均因全局
`gradle-8.5-bin.zip.lck` 权限失败；本地缓存 Gradle 8.9、隔离 `.gradle`、Java 17、
`--offline` 的 `test`/`build` 又因沙箱禁止 daemon 绑定 socket 失败。故不宣称 Gradle
`test` 或 `build` 成功，也未把现有 `build/` 产物计入证据。

### 提交

无代码 bug，未创建代码修复提交；本轮只提交报告事实更新，使用中文 Conventional Commit
`docs: 更新纹理生命周期最终审查报告`。未 push。

### 未验证的运行时/Mixin/OpenGL/FPS

未启动游戏或服务，未执行真实 Mixin 织入、资源重载、OpenGL 删除调用、渲染视觉、动态
光照命中率、帧时间或 FPS 测量；这些仍需可运行 Java 17 客户端和 profiler 验证。

## 后续修复：TextureAtlasSprite 资源流生命周期（2026-08-31）

### 调查

继续复核 atlas 纹理资源路径时，`javap -c -p` 确认本地 MITE merged jar 的
`TextureAtlasSprite.loadSprite(Resource)` 取得 `Resource.getInputStream()` 后调用
`ImageIO.read`，正常返回与异常路径均没有 `close()`。`TextureMap.loadTextureAtlas` 对每个
注册 sprite 调用该方法，资源重载会因此累积未关闭的输入流。这个缺口与前述分层纹理、
辅助 normal/specular 资源流不同，原有 fixture 未覆盖。

### 修改

`TextureAtlasSpriteMixin` 仅包装 `ImageIO.read(InputStream)`，以 try-with-resources 关闭
atlas 图像输入流；图像解码结果、动画帧拆分、normal/specular 页填充和 GL 上传顺序不变。
新增 `TextureAtlasSpriteResourceLifecycleTest`，并将其接入 `build.gradle` 的 `test` 依赖。
详细证据见 `docs/research/texture-atlas-sprite-resource-lifecycle.md`。

### 测试

失败优先阶段在 helper 尚不存在时按预期编译失败；实现后 Java 17 独立 fixture 通过，覆盖
成功解码和异常退出两条关闭路径。使用一次性 access-widener 转换后的本地 MITE 类路径，
`javac --release 17 -proc:none` 全量编译主/测试源码通过；全部十八个 fixture 和
`git diff --check` 通过。

### Gradle

Java 17 wrapper 执行 `./gradlew --no-daemon test` 受全局 `gradle-8.5-bin.zip.lck`
权限阻塞；隔离 `GRADLE_USER_HOME` 后因网络限制无法解析 `services.gradle.org`。直接使用
本机缓存 Gradle 8.9 时，全局缓存无法加载 `libnative-platform.dylib`，工作区 `.gradle`
执行 `test build` 又因沙箱禁止 daemon 绑定 socket（`Operation not permitted`）失败。因此
不宣称 Gradle `test` 或 `build` 成功，也未把已有 `build/` 产物计入证据。

### 提交与未验证范围

本轮目标提交应只包含 atlas 生命周期源码、fixture、研究报告和 `build.gradle`；不会包含
`gradlew`、参考目录或生成目录。当前沙箱将 `.git` 目录设为只读，`git add` 因无法创建
`.git/index.lock`（`Operation not permitted`）失败，因此本轮未创建 Conventional Commit，
目标文件保持未暂存；拟用中文 Conventional Commit `fix: 修复图集精灵资源流泄漏`，待可写
Git 元数据环境中提交。未启动游戏、未执行真实 Mixin 织入、未建立
OpenGL 上下文，未测量资源句柄、渲染视觉、帧时间或 FPS；这些仍需可运行 Java 17 客户端验证。

## 后续兼容性修复：ShaderPackZip 父目录段（2026-08-31）

### 调查

继续对照本地 OptiFine `ShaderPackZip` 时发现，目标实现虽然已恢复可选首 `/`、顶层目录
探测和目录 entry 回退，但遗漏了 OptiFine 的 `resolveRelative`。包含 `..` 的合法 shader
路径（例如 `/shaders/program/../test.vsh`）因此直接查找错误的 zip entry，表现为资源缺失。

### 修改

`ShaderPackZip.getResourceAsStream` 现在仅对包含 `..` 的路径执行分段归一化，普通段保持
顺序，父目录段移除上一段；尝试越过 pack 根目录时返回 `null`。没有改变不含父目录段的
路径、顶层目录判定、目录 entry 拒绝、惰性 `ZipFile` 打开或调用方关闭流的责任。

### 测试

先扩展现有 `ShaderPackResourcePathTest` 并在基线上复现父目录段读取失败；实现后使用 Java 17
独立编译运行通过，同时覆盖越过根目录的安全回退。该改动复用既有第十八个 fixture 任务，
没有增加任务数量；`git diff --check` 在最终验证时执行。

### Gradle 与提交

Java 17 下实际执行 `./gradlew --no-daemon test` 与 `./gradlew --no-daemon build` 均在 wrapper
启动阶段因全局 `gradle-8.5-bin.zip.lck` 权限被拒绝；隔离 `GRADLE_USER_HOME` 重试时因网络
限制无法解析 `services.gradle.org`。因此没有把已有 `build/` 产物计作本轮结果，也不宣称
Gradle 任务成功。本轮提交为 `3a4fadb`（`fix: 修复压缩着色器父目录路径`），未 push。

### 未验证范围

本轮未启动客户端、未加载真实 shader pack、未执行 Mixin 运行时织入或 OpenGL/FPS 测量；
实际 shader include 使用父目录段的客户端行为仍需在可运行 Java 17 环境中验证。
