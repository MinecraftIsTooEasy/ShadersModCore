# 动态光照查询 revision 惰性失效

## 调查结论

OptiFine 的 `DynamicLights.update` 以 50 ms 为周期检查实体，但查询缓存的失效条件应当是动态光照快照实际改变，而不是检查周期本身。当前移植在每次周期结束时无条件递增 `lightRevision`；实体静止且亮度不变时，区块编译仍会重新扫描全部动态光源。

## 选择的最小切片

`DynamicLight.update` 保留原有公开 `void` 方法签名，并通过同包的 `updateAndReport` 返回本轮位置、亮度或水下状态是否发生变化。实体映射更新同时报告新增/移除；`DynamicLights.update` 只在任一光源变化时递增 `lightRevision`。实体移除和全量清空继续显式失效缓存。

revision 稳定时，`DynamicLightQueryCache` 中已有条目可跨越多个 50 ms 检查周期复用；移动、亮度变化、实体增删和世界清空仍会在下一次查询前安全失效。该切片只改变 CPU 侧缓存失效频率，不改变亮度合并公式、动态光照节流策略、shader framebuffer 或多纹理页。

## 失败优先 fixture

`DynamicLightRevisionTest` 验证：

- 未变化状态保持 revision 不变；
- 变化状态只递增一次；
- 未变化的 `Long.MAX_VALUE` 不发生无意义溢出。

fixture 不创建 Minecraft 实体或 OpenGL 上下文，直接验证失效策略的纯 Java 边界。

## 风险边界

- `DynamicLight.update(...)` 的 JVM 方法描述符保持不变，外部调用者仍可按原 `void` 签名调用；报告方法只由同包更新循环使用。
- 变化标记在渲染更新线程内消费；动态光照位置、亮度和水下字段仍按原设计由查询线程读取，revision 仍为 volatile 发布边界。
- 快速动态光照模式提前返回时不会误报变化；首次创建的光源会通过映射新增和首次位置更新失效缓存。
- 没有移植缺少目标 API 的 `RenderChunk`、Smart Leaves 或完整 sprite/chunk BitSet。

## 验证

使用 Java 17 对全部主/测试源码执行 `javac --release 17 -proc:none`，并运行 7 个独立 fixture。未启动客户端、shader pack 或 OpenGL；实际命中率和 FPS 仍需运行时 profiler 验证。
