# 着色器 Tessellator 顶点缓冲与 scratch 优化

## 调查范围

对照本地 OptiFine `SVertexBuilder`、目标 `ShadersTess` 和 `TessellatorExtra`，本轮只处理纯 CPU 顶点写入前的数组容量状态。目标的 16-word 顶点布局、颜色/法线/中间 UV 打包和 OpenGL 指针布局均保持不变；RenderChunk、Smart Leaves 及 sprite/chunk BitSet 仍因缺少对应 API 不移植。

## 发现

`ShadersTess.addVertex` 原先用共享静态 `TessellatorExtra.bufferSize` 判断实例数组是否需要扩容。某个 tessellator 扩容后，后续新实例仍以旧的更大静态值判断，而新实例的 `rawBuffer` 仍按构造参数分配，接近实例数组尾部时可能越界。共享值也会让不同渲染方案之间的扩容状态互相影响。

四顶点法线计算只读取每个顶点的前三个坐标；原实现却按 `par1` 分配 `float[] vertexPos`，默认构造一次就额外保留 2,097,152 个 float。该 scratch 实际只需要 16 个 float。

纹理辅助路径也做了核对：`ShadersTex.getIntBuffer`/`getIntArray` 已按容量复用，`extractFrame` 返回的数组会被 `TextureAtlasSprite.framesTextureData` 持有，直接复用共享数组会让动画帧互相覆盖，因此没有在本轮强行改变其所有权语义。实体和粒子路径已有独立切片，本轮不重复触碰。

## 最小切片

- 新增 `TessellatorBufferGrowth`，以 `rawBuffer.length` 为唯一容量来源，保留末尾 64 words 的 quad 扩展区和 16,777,216 words 上限；达到上限时 flush 后显式 reset，再自然回退到当前实例的起始位置。
- `ShadersTess.addVertex` 按实例数组长度扩容，缓存 `IShaderTessellator` 引用并复用局部法线/中间 UV 打包值，避免同一顶点的重复接口访问。
- `TessellatorExtra` 将位置 scratch 固定为 16 个 float；没有新增配置键，关闭或未触发扩容时路径与原实现一致。

## 失败优先 fixture

`TessellatorBufferGrowthTest` 先在 helper 不存在时以编译失败，随后覆盖初始容量、按实例容量翻倍、最大值钳制、末尾保留区和 16-float scratch 常量。该 fixture 不创建 Minecraft 或 OpenGL 对象。

## 复核修复

对提交 `e8507b9` 继续做最大容量边界复核时，发现一个只在非标准初始容量下出现的明确越界：例如实例容量为 `MAX_CAPACITY - 47` 时，扩容触发点可能正好是 `rawBufferIndex == MAX_CAPACITY - 64` 且位于四边形起点。旧路径把数组扩到 `MAX_CAPACITY` 后没有重新检查，就写入该起点；完成第四个顶点的 quad-to-triangle 复制时，第二次复制的目标区间会越过数组末尾。默认 2,097,152 words 的幂次扩容序列不会落入该状态，但 `Tessellator` 构造器允许其它容量，不能依赖默认值。

本轮先把该状态加入 `TessellatorBufferGrowthTest`，确认 helper 缺失时编译失败；随后在 `TessellatorBufferGrowth` 增加 `needsFlushAfterGrowth`，并让 `ShadersTess.addVertex` 在扩容到最大容量后复用同一保留区/四边形边界判定，立即执行现有 `draw()`、`reset()`、恢复 `isDrawing` 的流程，再写入新顶点。部分四边形仍保留在 64-word 尾部安全区，不会被提前丢弃。修复提交为 `398a18f`（`fix: 修复顶点缓冲扩容至上限时的四边形越界`）。

## 验证边界

已先验证失败优先编译错误，再使用 Java 17 编译并运行通过该 fixture；全量源码编译使用本地 merged jar 和 FishModLoader all-in jar 也通过。Gradle wrapper 仍受网络/权限限制，未把其结果当作成功。没有启动客户端、shader pack 或 OpenGL，也没有声称已测得帧时间/FPS；真实运行时需观察大批量区块编译、达到最大缓冲区时的 flush 行为及顶点颜色/法线视觉一致性。
