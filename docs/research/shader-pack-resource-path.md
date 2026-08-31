# Shader Pack 资源路径兼容

## 调查结论

本地 OptiFine 的 `ShaderPackFolder.getResourceAsStream` 使用
`StrUtils.removePrefixSuffix(resName, "/", "/")`，`ShaderPackZip` 使用
`StrUtils.removePrefix(resName, "/")`，因此资源路径可以带或不带首个 `/`，并在资源
不存在时直接返回 `null`。当前两个实现都固定调用 `substring(1)`：无前导 `/` 的合法
路径会丢失首字符；缺失资源还会抛出并打印异常，在 shader 配置探测或资源回退时产生
无意义的异常输出。

## 失败优先 fixture

`ShaderPackResourcePathTest` 在临时目录创建 folder 和 zip 两种 shader pack，验证两者的
首个 `/` 可选、缺失资源返回 `null`、folder 目录和空路径不抛异常。旧实现分别在 folder
和 zip 的无前导 `/` 读取断言失败，证明 fixture 覆盖实际兼容性边界。

## 最小实现

`ShaderPackFolder.getResourceAsStream` 现在只去除一个可选首尾 `/`；
`ShaderPackZip.getResourceAsStream` 只去除一个可选首 `/`。两者都对 `null`、缺失或不可访问
资源返回 `null`，folder 目录也不会被当作文件打开；有效资源仍分别通过原有缓冲流或
`ZipFile` 流返回。输入流仍由调用方负责关闭，zip 的惰性打开和 `close()` 生命周期不变；
没有改变 shader 源码读取、GL 状态或线程行为。

## 验证边界

使用 Java 17 独立编译并运行 fixture 通过；完整主/测试源码编译、全部 fixture、Gradle
`test/build` 的实际结果记录在交付报告。没有启动客户端、加载 shader pack 或建立 OpenGL
上下文，真实 zip/folder shader pack 加载和 FPS 仍需运行时验证。
