# TextureAtlasSprite 资源流生命周期复核

## 调查

本轮对照本地 MITE merged jar 与 OptiFine 1.8.9 源码，复核 `TextureAtlasSprite` 的 atlas
资源加载入口。MITE `TextureAtlasSprite.loadSprite(Resource)` 在方法内取得
`Resource.getInputStream()`，交给 `ImageIO.read` 后继续拆分动画帧；`javap -c -p` 显示该
方法的正常返回和异常分支都没有 `InputStream.close()`。`TextureMap.loadTextureAtlas` 为每个
注册 sprite 调用该入口，因此一次资源重载可能留下大量资源管理器流。

当前 `TextureAtlasSpriteMixin` 已在 `loadSprite` 内拦截图像读取以填充 normal/specular
页，但原拦截没有改变流所有权，仍会继承上述泄漏。这个边界不依赖 OpenGL，关闭流不会改变
解码后的 `BufferedImage`、动画帧拆分或 atlas 上传顺序。

## 失败优先验证

新增 `TextureAtlasSpriteResourceLifecycleTest`，先在未实现 helper 的基线上编译，确认缺少
资源关闭边界。fixture 随后覆盖：

- 有效 PNG 解码后流已关闭，图像尺寸保持 1x1；
- 解码操作抛出运行时异常时流仍关闭，异常继续向调用方传播。

## 修改

`TextureAtlasSpriteMixin` 新增 `@WrapOperation`，只包装
`ImageIO.read(InputStream)`，通过 `readImageAndClose` 的 try-with-resources 关闭 atlas
输入流。其它资源查找、帧数据提取、normal/specular 页和 GL 调用不变。

## 边界

空流仍交由原始解码调用处理；关闭异常按 try-with-resources 语义传播。该切片只负责
`TextureAtlasSprite.loadSprite` 的图像输入流，`Resource` 元数据流和调用方未进入该入口的
其它资源由各自生命周期负责。
