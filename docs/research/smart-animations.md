# Smart Animations 移植调查与切片设计

## 参考行为

- `net/optifine/SmartAnimations.java` 维护 `spritesRendered` 与 `texturesRendered` 两个 `BitSet`。`isActive()` 还要求不在 `Shaders.isShadowPass`。
- `GlStateManager.bindTexture` 在非 shadow pass 记录 GL texture ID；`TextureAnimations.updateAnimations` 在动画 tick 后清空纹理快照。
- `TextureAtlasSprite.updateAnimation` 每次 tick 先根据 `animationIndex` 判定 `animationActive`，未渲染 sprite 直接保留计数器/帧；`TextureMap.updateAnimations` 只在 atlas tick 尾部清空 sprite 快照。
- OptiFine 的 sprite BitSet 来自 `WorldRenderer`/`ChunkRenderContainer` 编译和绘制链，不能由当前 MITE 目标的现有类直接复用。

## 当前管线映射

- `TextureMapMixin.tick` 包装目标 atlas 对 `updateAnimations` 的调用，在实际 tick 入口设置 `ShadersTex.updatingTex`，并以 `try/finally` 恢复嵌套状态；Smart Animations 跳过时直接不调用原版动画方法。atlas bind 仍重定向到 base 页。
- `TextureAtlasSpriteMixin.updateAnimation` 只重定向原版上传调用到 `ShadersTex.updateSubImage`，不再重复推进计数器，因此每个 sprite 每 tick 只执行一次原版帧推进，并同步 base/norm/spec 三页。
- `ShadersTex.updateAnimationTextureMap` 当前没有调用方。此前它对三个 shader 页各调用一次 `TextureAtlasSprite.updateAnimation`，会把同一个 sprite 的 `tickCounter/frameCounter` 推进三次；本轮改为只在 base 页执行一次，避免跨页 frame boundary 漂移。
- `TextureManagerMixin.bindTexture` 是当前统一的纹理对象绑定入口，`ShadersTex.bindTextures` 绑定 base/norm/spec；本轮在此记录 base texture ID。TextureManager 的 `tick` 依次驱动 `TextureMap.tick`，其 TAIL 是快照清空边界；资源重载 HEAD 也清空，避免旧 GL ID 泄漏到新 atlas。

## 安全范围与保留行为

- 新增 `optimize.txt` 的 `smartAnimations` 开关，默认 `false`，并接入 Plus Video 设置页。
- 启用后只做纹理级 atlas 跳过：只有曾经经过本绑定入口、且上一渲染阶段没有再次绑定的 atlas 才会跳过；从未被追踪的 texture ID 始终保留原版路径。当前帧没有任何追踪记录时也保留原版路径，覆盖启动阶段和绕过本 Mixin 的其他渲染器。
- 记录明确排除 shadow pass，且所有状态都在 GL 所在线程的绑定/tick 回调中访问；没有新增线程或 GL 调用。
- 未实现 OptiFine 的 sprite animation index、WorldRenderer/CompiledChunk BitSet 和自定义 `TextureAnimation(s)`（`mcpatcher/anim`）系统。当前缺少对应渲染数据链，贸然添加会改变资源重载和 chunk 编译契约，因此留作后续独立候选。

## 验证夹具

`src/test/java/shadersmodcore/client/shader/SmartAnimationsTest.java` 覆盖关闭开关、空追踪回退、未知/已知 texture、shadow pass 排除、tick 边界和资源重载清空；`smartAnimationsTest` Gradle 任务可单独执行，并由 `test` 任务依赖执行。
