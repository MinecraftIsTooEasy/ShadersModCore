# 动态光照世界坐标查询缓存

## 调查结论

`ChunkCacheMixin` 已经使用 `DynamicLights.getCombinedLightCached(int, int, int, int)`，
但 `WorldMixin` 的动态光照入口仍先创建 `BlockPos`，再调用对象 overload。世界直连
查询同样位于方块亮度读取路径，重复坐标会在动态光照快照稳定期间反复扫描并产生
临时 record；该路径没有必须保留的对象身份。

## 失败优先 fixture

`DynamicLightCoordinateQueryTest` 在修复前调用四整数坐标 overload，旧实现因缺少
该方法编译失败。修复后在空动态光照快照下验证普通亮度和最大方块亮度均原样返回；
fixture 不创建 Minecraft 世界或 OpenGL 上下文。

## 最小实现

新增 `DynamicLights.getCombinedLight(int, int, int, int)`，直接委托已有线程本地
查询缓存；`WorldMixin` 改为传入三个坐标整数，消除每次调用的 `BlockPos` 分配。缓存
键仍包含坐标、原始合并亮度和 revision，动态光照关闭时 Mixin 仍直接返回原版值。
对象 overload、边界早退、动态光照合并公式和更新时序未改变。

## 风险边界与验证

缓存仍为线程本地直映射，revision 变化时安全失效；世界入口与区块编译现在共享同一
缓存策略，不引入线程、GL 或新依赖。使用 Java 17 编译并运行该 fixture，随后运行
全部独立 fixture、全量主/测试源码编译尝试和 `git diff --check`。全量裸 classpath
仍受既有 access widener 缺失影响；未启动客户端、shader pack 或 OpenGL，实际命中率
和 FPS 仍需 profiler 验证。
