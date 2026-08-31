# Shader Pack 资源路径兼容

## 调查结论

本地 OptiFine 的 `ShaderPackFolder.getResourceAsStream` 使用
`StrUtils.removePrefixSuffix(resName, "/", "/")`，`ShaderPackZip` 使用
`StrUtils.removePrefix(resName, "/")` 并探测单一顶层目录，因此资源路径可以带或不带
首个 `/`，并在资源不存在时直接返回 `null`。目标实现此前固定调用 `substring(1)`，
zip 还只查找根目录：无前导 `/` 的合法路径会丢失首字符，带有 `ExamplePack/shaders/`
顶层目录的常见压缩包也无法读取；缺失资源还会抛出并打印异常，在 shader 配置探测或
资源回退时产生无意义的异常输出。

复核三个提交时又确认两处明确边界缺陷：zip 的目录 entry 会被当作可读资源流返回，且
多个顶层 `*/shaders/` 时按 entry 顺序选择第一个，可能把资源解析到错误的 pack。宽泛的
`catch (Exception)` 还会吞掉 zip 文件参数为空等编程错误。

## 失败优先 fixture

`ShaderPackResourcePathTest` 在临时目录创建 folder、根目录 zip 和单一顶层目录 zip 三种
shader pack，验证两者的首个 `/` 可选、缺失资源返回 `null`、folder/zip 目录、非法 zip、
close 后重开和空参数异常边界。另以两个顶层 shader 目录构造歧义 zip；旧实现首先在
folder/zip 的无前导 `/` 读取断言失败，修复该边界后目录流和歧义选择断言继续失败，证明
fixture 覆盖本轮实际缺陷。

## 最小实现

`ShaderPackFolder.getResourceAsStream` 现在只去除一个可选首尾 `/`；
`ShaderPackZip.getResourceAsStream` 只去除一个可选首 `/`，首次打开时探测根目录或唯一
顶层目录下的 `shaders/`，歧义候选直接回退为 `null`。两者都对 `null`、缺失、目录或不可
访问资源返回 `null`；资源查找只捕获 `IOException` 与 `SecurityException`，其它编程错误
继续暴露。有效资源仍分别通过原有缓冲流或 `ZipFile` 流返回。输入流仍由调用方负责关闭，
zip 的惰性打开和 `close()` 生命周期不变；没有改变 shader 源码读取、GL 状态或线程行为。

## 验证边界

使用 Java 17 独立编译并运行 fixture 通过；完整主/测试源码编译、全部 fixture、Gradle
`test/build` 的实际结果记录在交付报告。没有启动客户端、加载 shader pack 或建立 OpenGL
上下文，真实 zip/folder shader pack 加载和 FPS 仍需运行时验证。
