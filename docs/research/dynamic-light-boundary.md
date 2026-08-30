# 动态光照最大亮度边界早退

## 调查范围

本轮继续对照本地 `Optifine SRC Version [1.8.9 HD U M6 pre2]` 的
`net/optifine/DynamicLights` 与当前 1.6.4-MITE/FishModLoader 接入点。
现有 `DynamicLightQueryCache` 已把重复坐标查询缓存到线程本地，但缓存未命中时仍会扫描全部动态光源。

## 结论

组合光照值的方块亮度位于低字节的 `0xF0`（方块亮度 15）。动态光照只会提高这部分亮度，不能改变天空光位或其他高位；因此该值已经达到可表示的最大方块亮度，不需要进入动态光源扫描或查询缓存。

`DynamicLights.canSkipDynamicLightQuery` 仅接受非负且低字节恰为 `0xF0` 的标准编码。关闭动态光照时，现有 Mixin 仍直接返回原版结果；负值、低于最大值和非标准低字节均回退到原始动态光照路径。

## 接入点

- `DynamicLights.getCombinedLightCached` 在读取线程本地缓存前执行边界早退；共享的 `getCombinedLight(BlockPos, int)` 也执行同一早退，因此未缓存的 `WorldMixin` 入口同样不会扫描动态光源。
- `ChunkCacheMixin` 的动态光照入口继续负责开关和动态光源数量判定；本切片没有改变亮度合并公式、缓存 revision 或实体更新时序。
- 未尝试移植缺少对应 API 的 RenderChunk、SmartLeaves 或完整 sprite/chunk BitSet。

## 失败优先 fixture

`DynamicLightBoundaryTest` 覆盖：

- 标准最大方块亮度 `0xF0`；
- 同时带天空光高位的最大方块亮度；
- 低于最大值的正常编码；
- 负值和非标准低字节的安全回退。

## 验证

使用本地映射 Minecraft merged jar、FishModLoader 编译类和 Java 17：

```text
javac --release 17 -proc:none  # 全部 src/main/java 与 src/test/java
SmartAnimationsTest passed
DynamicLightQueryCacheTest passed
DynamicLightBoundaryTest passed
ConfigParsingTest passed
ParticleRenderOptimizerTest passed
```

`git diff --check` 通过。没有启动客户端、加载 shader pack、建立 OpenGL 上下文或测量 FPS；Mixin 运行时织入和实际渲染收益仍需真实 Java 17 客户端环境验证。

Gradle 任务仍受环境限制：Java 25 默认配置会报 class file major version 69，wrapper 默认用户目录锁文件不可访问；隔离用户目录的尝试结果记录在交付报告中。
