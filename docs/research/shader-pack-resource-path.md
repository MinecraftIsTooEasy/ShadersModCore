# 文件型 Shader Pack 资源路径兼容

## 调查结论

本地 OptiFine `ShaderPackFolder.getResourceAsStream` 使用
`StrUtils.removePrefixSuffix(resName, "/", "/")`，因此资源路径可以带或不带首个 `/`，
并在文件不存在时直接返回 `null`。当前实现固定调用 `substring(1)`：无前导 `/` 的合法
路径会丢失首字符；缺失文件还会抛出并打印 `FileNotFoundException`，在 shader 配置探测
或资源回退时产生无意义的异常输出。

## 失败优先 fixture

`ShaderPackResourcePathTest` 在临时目录创建一个 `shaders/test.vsh`，验证首个 `/` 可选、
缺失资源和目录返回 `null`、空路径不抛异常。旧实现首先在无前导 `/` 的读取断言失败，
证明 fixture 覆盖实际兼容性边界。

## 最小实现

`ShaderPackFolder.getResourceAsStream` 现在只去除一个可选首尾 `/`，对 `null`、目录、缺失
或不可访问文件返回 `null`，存在的普通文件仍通过 `BufferedInputStream` 返回。输入流仍由
调用方负责关闭；没有改变 shader 源码读取、GL 状态或线程行为。

## 验证边界

使用 Java 17 独立编译并运行 fixture 通过；完整主/测试源码编译、全部 fixture、Gradle
`test/build` 的实际结果记录在交付报告。没有启动客户端、加载 shader pack 或建立 OpenGL
上下文，真实 zip/folder shader pack 加载和 FPS 仍需运行时验证。
