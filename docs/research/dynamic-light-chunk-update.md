# 动态光照区块标记坐标 scratch

## 调查结论

动态光照实体位置或亮度变化时，`DynamicLight.updateChunkLight` 会遍历 17 个坐标，
每轮构造基础 `BlockPos` 及 7 个偏移对象，再调用八次
`markBlockForRenderUpdate`。该更新只需要整数坐标，原对象不向外保存；在多个持光
实体同时变化时会产生大量短命 record。

## 失败优先 fixture

`DynamicLightChunkUpdateTest` 在修复前调用尚不存在的
`fillChunkUpdateCoordinates` helper，确认旧实现编译失败。修复后的 fixture 验证
水平、垂直及组合偏移均为 16 方块，并检查八个坐标的原有顺序。

## 最小实现

每个 `DynamicLight` 实例复用一个 24-int scratch。坐标 helper 预先计算三个方向
偏移，按原 `BlockPos` 顺序写出八组三元坐标；更新循环直接消费该数组调用
`markBlockForRenderUpdate`，不再为每轮创建临时对象。实体变化判定、方向选择、17
轮扫描、渲染器失效调用和动态光照数值计算均未改变。

## 风险边界与验证

scratch 只由渲染更新线程使用，不跨线程发布，也不引入 GL 或新依赖；坐标数组长度
错误仍自然抛出。使用 Java 17 编译并运行该 fixture，随后运行全部独立 fixture、
全量主/测试源码编译尝试和 `git diff --check`。全量裸 classpath 仍受既有 access
widener 缺失影响；未启动客户端、shader pack 或 OpenGL，实际区块更新耗时和 FPS
仍需 profiler 验证。
