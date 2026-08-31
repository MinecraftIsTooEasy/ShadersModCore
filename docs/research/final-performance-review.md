# 累计性能移植最终阶段性审查

## 审查基线

本次审查重点覆盖 `7e553b3`、`cb013e8` 和 `e98eb2d`，并创建了独立的中文 Conventional Commit 修复空值回归；累计性能基线仍包含：

`a35a80c`、`e38b621`、`40f6a7f`、`4689e91`、`c9777c7`、`f3fddb4`。

逐个检查目标提交的 `git diff-tree --name-status` 后，变更均位于以下范围：

- `src/main/java`、`src/main/resources`：性能切片及其配置/UI/Mixin 接入；
- `src/test/java`：六个无 OpenGL 上下文依赖的独立 fixture；
- `docs/research`：切片研究记录；
- `build.gradle`：六个 fixture 的 `JavaExec` 任务及 `test` 依赖。

目标提交和本轮修复没有包含 `FishModLoader/`、`Optifine SRC Version [1.8.9 HD U M6 pre2]/`、`build/`、`logs/`、`.gradle/`，也没有把 `.class`、`.jar` 等生成物纳入提交。上述目录在当前工作树中的未提交状态按要求保留。

## 跨切片配置与任务

| 配置/任务 | 默认值与接入 | 关闭/异常语义 |
| --- | --- | --- |
| `dynamicLightsFast` | `OptimizeConfig` 读取并持久化，默认 `false`；`ShaderConfig.isDynamicLightsFast()` 直接委托；动态光照实体轮询节流为 500 ms | `dynamicLights=false` 时 `ChunkCacheMixin` 完全走原版返回值；节流只影响轮询频率，不改变亮度合并公式 |
| `smartAnimations` | `OptimizeConfig` 读取并持久化，默认 `false`；配置加载和 GUI 切换都调用 `SmartAnimations.setEnabled` | 未启用、shadow pass、尚无追踪快照、未知 texture ID 均保留动画原版路径；已追踪但本帧未绑定的 atlas 才跳过；tick 和资源重载会清理对应状态 |
| `skipEmptyParticleRender` | `OptimizeConfig` 读取并持久化，默认 `true`；粒子设置页可切换 | 关闭开关、`entity == null`、层数组不是四层、任一层为 `null` 或非空时均保留原版；第 3 层参与判定，避免发光粒子依赖的插值状态回归 |

六个独立任务分别为 `smartAnimationsTest`、`dynamicLightQueryCacheTest`、`dynamicLightBoundaryTest`、`shadersTexResourcePathTest`、`configParsingTest` 和 `particleRenderOptimizerTest`。每个任务只依赖共享的 `testClasses`；`test` 依赖这六个任务，没有发现重复注册或循环依赖。

## 语义审查结论

- Smart Animations 的纹理级 BitSet、shadow 排除、启停重置和 `try/finally` 更新上下文均保持关闭/回退安全。当前切片没有移植 OptiFine 的 sprite/chunk BitSet，因此收益范围小于完整 OptiFine，但未发现明确的正确性回归。
- 动态光照查询缓存使用线程本地直映射表，键包含坐标、原始合并亮度和光照 revision；碰撞只导致 miss，实体移动/移除、更新周期和世界清空会推进 revision。关闭动态光照时缓存入口不会执行。
- 配置读取在加载前清空属性集；文件不存在时先应用默认值再写盘；整数/浮点损坏值回退，布尔值保留 `Boolean.parseBoolean` 的兼容语义。未发现需要在本轮修复的明确异常路径。
- 空粒子优化只在四层都明确为空时取消普通粒子渲染，未触碰粒子更新、生成、删除或 OpenGL 状态；非空和未知布局均回退原版。

早期性能切片未发现明确正确性回归；本次目标提交的额外审查发现并修复了纹理资源路径的空值兼容回归，后续切片及其实现记录如下。

## 后续切片：动态光照最大亮度边界

在上述审查后继续对照本地 OptiFine SRC，确认动态光照只会提高组合光照值低字节中的方块亮度。标准方块亮度 15 编码为 `0xF0`；在该边界上继续扫描动态光源或查询线程本地缓存不可能改变返回值。本轮新增 `DynamicLights.canSkipDynamicLightQuery`，在 `getCombinedLightCached` 的缓存读取前、共享的 `getCombinedLight(BlockPos, int)` 以及实体 overload 中对标准 `0xF0` 做早退。

早退只接受非负且低字节恰为 `0xF0` 的编码，负值、低于最大值和非标准低字节均回退原路径；动态光照关闭时 Mixin 仍直接返回原版亮度。新增失败优先 fixture `DynamicLightBoundaryTest`，覆盖天空光高位、低值和异常输入。详细调查记录见 `docs/research/dynamic-light-boundary.md`。

动态光照切片后独立 fixture 共五个：`smartAnimationsTest`、`dynamicLightQueryCacheTest`、`dynamicLightBoundaryTest`、`configParsingTest` 和 `particleRenderOptimizerTest`。

## 后续修复：法线/高光纹理资源路径

继续检查纹理更新路径时发现 `ShadersTex.getNSMapLocation` 的 `split(".png")` 正则通配符会错误截断包含 `png` 片段的合法文件名，例如 `pngstone.png`。本轮新增 `ShadersTexResourcePathTest` 先确认旧实现失败，再改为字面后缀移除；资源域、无后缀路径及已有 `_n.png`/`_s.png` 命名规则保持不变。审查另发现字面后缀修复遗漏了 OptiFine 的空位置回退，已补回 `null` 保护并以失败优先断言覆盖。详细记录见 `docs/research/texture-resource-path.md`。

本轮后独立 fixture 共六个，新增 `shadersTexResourcePathTest`。

## 已验证证据

使用 Java 17（`Zulu 17.0.20.1`）、本地映射 Minecraft merged jar 和 FishModLoader all-in jar，以 `javac --release 17 -proc:none` 编译全部 `src/main/java` 与 `src/test/java`；主源码和测试源码均成功。随后六个 fixture 全部通过：

```text
SmartAnimationsTest passed
DynamicLightQueryCacheTest passed
DynamicLightBoundaryTest passed
ShadersTexResourcePathTest passed
ConfigParsingTest passed
ParticleRenderOptimizerTest passed
```

`git diff --check` 通过。

项目 Gradle 任务也做了真实尝试，但受环境阻塞：

- 默认 Java 25 执行 `./gradlew --no-daemon test` 在配置阶段报 `Unsupported class file major version 69`。
- 使用本机 Gradle 8.5、Java 17 和工作区 `.gradle` 执行 `test` 与 `build` 时，daemon 均因沙箱禁止绑定 socket 报 `java.net.SocketException: Operation not permitted`；改用全局 Gradle 缓存还会在 native service 初始化时报 `Failed to load native library 'libnative-platform.dylib'`。

因此没有把 `build/` 中已有的 class/jar 当作本轮 Gradle 产物，也没有宣称 Gradle `test` 或 `build` 成功。

## 未验证范围与后续建议

本轮没有启动游戏、加载 shader pack 或建立 OpenGL 上下文，未验证 Mixin 运行时织入、实际渲染状态、动态光照节流观感、动画帧连续性、粒子视觉结果、命中率、帧时间或 FPS。完整验收仍需要在可运行 Java 17/Gradle 环境中执行 `./gradlew test build`，再用真实客户端和 profiler 对比：

1. Smart Animations 开关、资源重载、shadow pass 与多个 atlas 的动画连续性；
2. 动态光照实体移动/熄灭时的更新延迟及查询缓存命中率；
3. 四层粒子为空、仅有第 3 层粒子以及混合层时的渲染状态和位置；
4. 首次启动、重复加载和损坏配置文件的默认值与落盘权限。
