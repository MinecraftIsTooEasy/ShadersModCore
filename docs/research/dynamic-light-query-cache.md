# 动态光照查询缓存调查与切片设计

## 候选对照

| 候选 | OptiFine 参考 | 当前映射状态 | 本轮结论 |
| --- | --- | --- | --- |
| 区块编译缓存 | `net/optifine/override/ChunkCacheOF.java` 的 `combinedLights`/`blockStates` 数组，`RegionRenderCache` 复用数组 | 目标只有旧式 `ChunkCache`，坐标是三个 `int`，没有 `IBlockState`、`RegionRenderCache` 或 `RenderChunk` | 不直接复制包装类；可抽取其中“渲染期间按坐标缓存亮度”的边界 |
| 渲染区块迭代 | `net/optifine/shaders/IteratorRenderChunks.java` | 目标使用 `WorldRenderer` 显示列表和 `RenderGlobal.sortedWorldRenderers`，没有 `ViewFrustum`/`RenderChunk` | 缺少对应 API，暂不移植 |
| 顶点格式/法线 | `SVertexBuilder`、`SVertexFormat` | `ShadersTess` 已在 `TessellatorMixin` 接管顶点、法线、中间 UV 和实体属性 | 当前实现已覆盖核心路径，改变步长会触及 GL 指针布局，暂不移植 |
| Smart Leaves | `net/optifine/SmartLeaves.java` | 目标没有 baked model、`BlockState` 或模型管理链；`RenderGlobalMixin` 只有 leavesQuality 对 fancy graphics 的包装 | 只读调查，不能安全复制模型替换 |

## 当前调用链

1. 目标 `WorldRenderer.updateRenderer`（映射类 `bfa`）每次编译区块时构造 `ChunkCache`（映射类 `acl`），再把它传给 `RenderBlocks`。
2. `RenderBlocks` 通过 `IBlockAccess.getLightBrightnessForSkyBlocks(int,int,int,int)`（接口映射 `acf.h`）为方块顶点和 AO 重复查询相同坐标。
3. `ChunkCacheMixin.modifyCout` 在该方法返回后检查动态光照，并调用 `DynamicLights.getCombinedLightCached(int,int,int,int)`；世界直连的 `WorldMixin` 也使用同一 primitive-coordinate overload，避免为每次查询创建 `BlockPos` record。
4. 动态光照实体位置在 `RenderGlobalMixin.updateRenderers` 注入点由 `DynamicLights.update` 更新；`DynamicLight` 的位置、光强和水下状态是 volatile，查询线程读取不可变的 copy-on-write 列表。

## 选择的最小切片

在 `DynamicLights` 增加固定大小、线程本地的坐标查询缓存，并从 `ChunkCacheMixin` 与 `WorldMixin` 的动态光照分支调用它。键包含 `(x,y,z,原始合并亮度,更新纪元)`；未命中仍执行原有计算，返回值不改变。动态光照每次 50 ms 更新、实体移除或全量清空时递增纪元，旧缓存因此失效。线程本地实例避免渲染工作线程之间共享可变数组，也不需要锁或 GL 调用。

缓存没有单独的配置项：关闭既有 `dynamicLights` 开关即完全绕过该分支并保持原版路径；缓存只在现有 `DynamicLights.getCount() > 0` 条件成立时使用。世界直连路径现在复用同一缓存，不改变开关和 revision 语义。

## 风险边界

- **线程**：缓存是 `ThreadLocal`，纪元是 volatile；动态光照快照仍由现有不可变列表提供。查询在更新边界重新检查纪元，跨边界的缓存项会安全未命中并重算。
- **GL**：缓存不调用 OpenGL，只包裹 CPU 侧亮度计算；两个 Mixin 仍在原调用返回后修改整数。
- **正确性**：键保留原始 `combinedLight`，覆盖四参数方法的最低方块光值差异；负坐标和哈希碰撞只会导致未命中重算，不会返回错误值。缓存容量固定，满时采用直接映射替换。
- **生命周期**：`ChunkCache` 本身按区块编译创建，但缓存不依赖其生命周期；更新纪元处理动态光照移动、移除、世界切换和清空。
- **未覆盖**：未实现 OptiFine 的 `ChunkCacheOF` 方块状态缓存、区块/精灵 BitSet、`IteratorRenderChunks`、Smart Leaves 模型重写；目标缺少相应 API。

## 验证方法

- `DynamicLightQueryCacheTest`：无 Minecraft/GL 的 fixture，验证空缓存、命中、原始亮度隔离、纪元失效和同槽碰撞替换。
- `./gradlew --no-daemon dynamicLightQueryCacheTest` 与 `./gradlew --no-daemon test`：执行夹具和既有 Smart Animations 夹具。
- `./gradlew --no-daemon build`：Java 17 编译及打包；另外运行 `git diff --check`。
- 本轮不宣称 FPS 或 OpenGL 收益；需要实际客户端 profiling 才能确认查询命中率和帧时间变化。
