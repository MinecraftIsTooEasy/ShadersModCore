# 动态光照扫描无 Iterator 分配

## 调查结论

`DynamicLights.getLightLevel(BlockPos)` 位于区块编译的缓存未命中路径。当前实现对 `DynamicLightsMap.valueList()` 使用增强 `for`，每次亮度查询都会创建一个 Iterator；本地 OptiFine `DynamicLights` 使用索引循环。目标的 `DynamicLightsMap` 已通过 volatile 不可变快照发布列表，快照在扫描期间不会被修改，适合直接使用 `size/get`。

## 选择的最小切片

将方块亮度扫描抽为同包重载 `getLightLevel(List<DynamicLight>, BlockPos)`，缓存入口继续传入现有快照；循环缓存 `size` 并按索引读取。扫描顺序、距离平方判断、水下衰减、最大值合并和最终限制均保持原实现，未触碰 shader framebuffer、多纹理页或 Mixin 生命周期。

## 失败优先 fixture

`DynamicLightScanTest` 使用一个空的自定义 `AbstractList`：其 `iterator()` 主动抛错，而 `size()` 返回 0。索引扫描应在不请求 Iterator 的情况下返回零；旧的增强 `for` 会在进入循环前触发该失败。

## 风险边界

- 仅查询线程读取不可变快照；更新线程仍通过 `DynamicLightsMap` 的 copy-on-write 发布列表。
- 空列表、负坐标和动态光照关闭路径不改变；光照计算仍只在现有缓存未命中时执行。
- 该切片不移植缺少目标 API 的 `RenderChunk`、Smart Leaves 或完整 sprite/chunk BitSet，也不声称已测得 FPS。

## 验证

使用 Java 17 对全部主/测试源码执行 `javac --release 17 -proc:none`，并运行 `DynamicLightScanTest` 及其余独立 fixture。未启动客户端或 OpenGL；实际查询分配和帧时间仍需 profiler 验证。
