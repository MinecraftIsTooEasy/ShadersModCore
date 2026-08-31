# 着色器辅助纹理资源回退

## 对照与边界

本轮复核提交 `1cba805`，继续对照本地 OptiFine `1.8.9 HD U M6 pre2` 的 `ShadersTex.loadNSMapFile`，检查目标 `ShadersTex.loadNSMap1` 的法线/高光辅助图加载。该路径只处理资源查找、图像解码和 CPU 像素填充，不改变纹理页布局或 OpenGL 调用。

OptiFine 对空位置直接返回失败，对 `ImageIO.read` 返回 `null` 的非图像资源回退默认颜色，并只将资源查找/读取的 `IOException` 视为失败。`1cba805` 虽增加了空位置、空图像和 `RuntimeException` 回退，但目标 MITE 的实际 `FallbackResourceManager`/`SimpleReloadableResourceManager` 会从省略 checked 声明的接口调用中抛出 `FileNotFoundException`；该异常不是 `RuntimeException`，所以真实缺失资源仍会在默认填充前中止。与此同时，宽泛捕获 `RuntimeException` 会吞掉资源管理器、输入流或解码器中的编程错误。

## 失败优先 fixture

`ShadersTexResourceFallbackTest` 使用内存及真实 `FallbackResourceManager`，覆盖以下语义：

- 空 `ResourceLocation` 使用默认颜色；
- 空 `ResourceManager`、空 `Resource` 和真实 `FileNotFoundException` 使用默认颜色；
- 非图像输入流导致 `ImageIO.read` 返回 `null` 时使用默认颜色；
- 空输入流和尺寸不匹配使用默认颜色；
- 合法的一像素 PNG 按偏移写入且只修改目标页，资源域原样传递，并确认输入流关闭；
- manager/stream 的 `IllegalStateException` 与目标数组越界继续抛出。

在修复前运行扩展 fixture 时，真实缺失用例以 `java.io.FileNotFoundException` 失败，证明 `1cba805` 的运行时捕获并未覆盖实际资源管理器。

## 最小实现

本轮在 `loadNSMap1` 中通过声明 `throws IOException` 的适配 helper 捕获实际资源缺失/读取异常；对输入流先处理 `null`，再用 try-with-resources，并仅捕获 `IOException`。非图像的 `ImageIO.read == null`、尺寸不匹配和空流继续使用默认颜色。只有尺寸完全匹配时才调用 `getRGB`，否则按原有偏移和默认颜色填充；像素数组边界和其它编程错误不被吞掉。

关闭或未知辅助资源状态仍得到原有默认法线色 `-8421377` 或高光色 `0`；成功资源的像素和 OpenGL 上传路径保持不变。

## 验证范围

使用 Java 17 编译并运行该 fixture；同时执行全部已有 fixture、全量主/测试源码编译和 `git diff --check`。未启动客户端、shader pack 或 OpenGL，真实资源包加载、纹理视觉结果和 FPS 仍需运行时验证。
