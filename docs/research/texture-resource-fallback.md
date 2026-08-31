# 着色器辅助纹理资源回退

## 对照与边界

本轮继续对照本地 OptiFine `1.8.9 HD U M6 pre2` 的 `ShadersTex.loadNSMapFile`，检查目标 `ShadersTex.loadNSMap1` 的法线/高光辅助图加载。该路径只处理资源查找、图像解码和 CPU 像素填充，不改变纹理页布局或 OpenGL 调用。

OptiFine 对空位置直接返回失败，对 `ImageIO.read` 返回 `null` 的非图像资源回退默认颜色；目标原实现直接调用 `manager.getResource(location)` 和 `bufferedimage.getWidth()`，只捕获 `IOException`。因此资源管理器报告缺失资源的运行时异常、空位置和非图像流都会在默认填充前中止。原实现还没有关闭成功打开的输入流。

## 失败优先 fixture

`ShadersTexResourceFallbackTest` 使用内存 `ResourceManager`，覆盖以下语义：

- 空 `ResourceLocation` 使用默认颜色；
- 资源查找抛运行时缺失异常时使用默认颜色；
- 非图像输入流导致 `ImageIO.read` 返回 `null` 时使用默认颜色；
- 合法的一像素 PNG 仍复制到目标数组。

在实现前运行 fixture 时，第二个用例以 `RuntimeException: resource missing` 失败，证明现状没有安全回退。

## 最小实现

`loadNSMap1` 现在先检查 `manager` 和 `location`，将资源查找的运行时缺失异常视为未加载；对资源流使用 try-with-resources，并把空图像、IO/解码运行时异常统一视为失败。只有尺寸完全匹配时才调用 `getRGB`，否则按原有偏移和默认颜色填充。像素数组本身的边界错误不被吞掉。

关闭或未知辅助资源状态仍得到原有默认法线色 `-8421377` 或高光色 `0`；成功资源的像素和 OpenGL 上传路径保持不变。

## 验证范围

使用 Java 17 编译并运行该 fixture；同时执行全部已有 fixture、全量主/测试源码编译和 `git diff --check`。未启动客户端、shader pack 或 OpenGL，真实资源包加载、纹理视觉结果和 FPS 仍需运行时验证。
