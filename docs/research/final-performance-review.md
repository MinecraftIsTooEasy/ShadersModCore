# 累计性能移植最终阶段性审查

## 审查基线

本次审查基于 `f3fddb4`，累计提交链为：

`a35a80c`、`e38b621`、`40f6a7f`、`4689e91`、`c9777c7`、`f3fddb4`。

逐个检查六个提交的 `git diff-tree --name-status` 后，变更均位于以下范围：

- `src/main/java`、`src/main/resources`：性能切片及其配置/UI/Mixin 接入；
- `src/test/java`：四个无 Minecraft/OpenGL 依赖的独立 fixture；
- `docs/research`：切片研究记录；
- `build.gradle`：四个 fixture 的 `JavaExec` 任务及 `test` 依赖。

六个提交没有包含 `FishModLoader/`、`Optifine SRC Version [1.8.9 HD U M6 pre2]/`、`build/`、`logs/`、`.gradle/`，也没有 `.class`、`.jar` 等生成物。上述目录在当前工作树中的未提交状态按要求保留。

## 跨切片配置与任务

| 配置/任务 | 默认值与接入 | 关闭/异常语义 |
| --- | --- | --- |
| `dynamicLightsFast` | `OptimizeConfig` 读取并持久化，默认 `false`；`ShaderConfig.isDynamicLightsFast()` 直接委托；动态光照实体轮询节流为 500 ms | `dynamicLights=false` 时 `ChunkCacheMixin` 完全走原版返回值；节流只影响轮询频率，不改变亮度合并公式 |
| `smartAnimations` | `OptimizeConfig` 读取并持久化，默认 `false`；配置加载和 GUI 切换都调用 `SmartAnimations.setEnabled` | 未启用、shadow pass、尚无追踪快照、未知 texture ID 均保留动画原版路径；已追踪但本帧未绑定的 atlas 才跳过；tick 和资源重载会清理对应状态 |
| `skipEmptyParticleRender` | `OptimizeConfig` 读取并持久化，默认 `true`；粒子设置页可切换 | 关闭开关、`entity == null`、层数组不是四层、任一层为 `null` 或非空时均保留原版；第 3 层参与判定，避免发光粒子依赖的插值状态回归 |

四个独立任务分别为 `smartAnimationsTest`、`dynamicLightQueryCacheTest`、`configParsingTest` 和 `particleRenderOptimizerTest`。每个任务只依赖共享的 `testClasses`；`test` 依赖这四个任务，没有发现重复注册或循环依赖。

## 语义审查结论

- Smart Animations 的纹理级 BitSet、shadow 排除、启停重置和 `try/finally` 更新上下文均保持关闭/回退安全。当前切片没有移植 OptiFine 的 sprite/chunk BitSet，因此收益范围小于完整 OptiFine，但未发现明确的正确性回归。
- 动态光照查询缓存使用线程本地直映射表，键包含坐标、原始合并亮度和光照 revision；碰撞只导致 miss，实体移动/移除、更新周期和世界清空会推进 revision。关闭动态光照时缓存入口不会执行。
- 配置读取在加载前清空属性集；文件不存在时先应用默认值再写盘；整数/浮点损坏值回退，布尔值保留 `Boolean.parseBoolean` 的兼容语义。未发现需要在本轮修复的明确异常路径。
- 空粒子优化只在四层都明确为空时取消普通粒子渲染，未触碰粒子更新、生成、删除或 OpenGL 状态；非空和未知布局均回退原版。

本次审查未发现明确 bug，因此没有新增失败测试、生产代码或代码提交。

## 已验证证据

使用 Java 17（`Zulu 17.0.20.1`）和已有的映射 Minecraft jar，以 `javac --release 17 -proc:none` 编译全部 `src/main/java` 与 `src/test/java`；主源码类型编译和测试源码编译均成功。随后四个 fixture 全部通过：

```text
SmartAnimationsTest passed
DynamicLightQueryCacheTest passed
ConfigParsingTest passed
ParticleRenderOptimizerTest passed
```

`git diff --check` 通过。

项目 Gradle 任务也做了真实尝试，但受环境阻塞：

- `./gradlew --no-daemon test` 和 `./gradlew --no-daemon build` 在默认 Java 25 下配置阶段报 `Unsupported class file major version 69`；wrapper 直接探测还遇到用户 Gradle 缓存锁 `Operation not permitted`。
- 切换 Java 17 并使用工作区 `.gradle` 后，Gradle 8.5 daemon 因沙箱禁止绑定 socket 报 `java.net.SocketException: Operation not permitted`。

因此没有把 `build/` 中已有的 class/jar 当作本轮 Gradle 产物，也没有宣称 Gradle `test` 或 `build` 成功。

## 未验证范围与后续建议

本轮没有启动游戏、加载 shader pack 或建立 OpenGL 上下文，未验证 Mixin 运行时织入、实际渲染状态、动态光照节流观感、动画帧连续性、粒子视觉结果、命中率、帧时间或 FPS。完整验收仍需要在可运行 Java 17/Gradle 环境中执行 `./gradlew test build`，再用真实客户端和 profiler 对比：

1. Smart Animations 开关、资源重载、shadow pass 与多个 atlas 的动画连续性；
2. 动态光照实体移动/熄灭时的更新延迟及查询缓存命中率；
3. 四层粒子为空、仅有第 3 层粒子以及混合层时的渲染状态和位置；
4. 首次启动、重复加载和损坏配置文件的默认值与落盘权限。
