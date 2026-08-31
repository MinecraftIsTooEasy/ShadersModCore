# 分层纹理资源流生命周期

## 调查结论

`ShadersTex.loadLayeredTexture` 会为每个分层名称取得 `InputStream` 并交给
`ImageIO.read`，但原路径没有关闭输入流。资源重载或多层纹理加载时，文件型资源
管理器可能长期持有句柄；当输入流为空或不是图像时，后续直接读取尺寸还会抛出
`NullPointerException`，导致着色器分层纹理加载中断。

## 失败优先 fixture

`ShadersTexLayeredResourceTest` 首先调用尚不存在的分层读取 helper，确认旧实现无法
编译该边界。修复后的 fixture 使用内存资源管理器验证一像素 PNG 能正常解码、输入流
一定关闭，以及空输入流返回空图像而不抛异常；fixture 不创建 Minecraft 或 OpenGL
上下文。

## 最小实现与边界

新增同包 `loadLayeredImage` helper：manager、位置、资源或输入流为空时返回 `null`，
有效输入使用 try-with-resources 解码并保证关闭。`loadLayeredTexture` 对空/非图像层
跳过；若所有层都无效则不调用 `setupTexture`，避免上传空数组。有效图像的尺寸、三页
像素合成、辅助法线/高光读取和 OpenGL 上传顺序保持不变；资源 manager 的运行时编程
异常仍向上传播，不被宽泛捕获。

## 验证范围

使用 Java 17 编译并运行该 fixture，随后运行全部独立 fixture、全量主/测试源码编译
尝试和 `git diff --check`。全量裸 classpath 仍受既有 access widener 缺失影响；未
启动客户端、shader pack 或 OpenGL，真实资源包句柄回收和视觉结果仍需运行时验证。
