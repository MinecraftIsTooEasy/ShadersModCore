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
close 后重开和空参数异常边界；folder 另以 pack 外同级文件验证父目录逃逸回退。另以两个顶层
shader 目录构造歧义 zip；旧实现首先在 folder/zip 的无前导 `/` 读取断言失败，修复该边界后
目录流、歧义选择和 folder 逃逸断言继续失败，证明 fixture 覆盖本轮实际缺陷。

## 最小实现

`ShaderPackFolder.getResourceAsStream` 现在只去除一个可选首尾 `/`；
`ShaderPackZip.getResourceAsStream` 只去除一个可选首 `/`，首次打开时探测根目录或唯一
顶层目录下的 `shaders/`，歧义候选直接回退为 `null`。两者都对 `null`、缺失、目录或不可
访问资源返回 `null`；资源查找只捕获 `IOException` 与 `SecurityException`，其它编程错误
继续暴露。有效资源仍分别通过原有缓冲流或 `ZipFile` 流返回。输入流仍由调用方负责关闭，
zip 的惰性打开和 `close()` 生命周期不变；没有改变 shader 源码读取、GL 状态或线程行为。

## 后续兼容性修复：zip 父目录段

对照 OptiFine 的 `ShaderPackZip.resolveRelative` 复核 shader include 和程序路径时发现，目标
实现只去除首个 `/`，没有处理路径中的 `..`。因此 `/shaders/program/../test.vsh` 会直接查找
不存在的 zip entry，合法资源被误判为缺失。补回基于 `StringTokenizer` 的分段归一化：普通段按
顺序保留，`..` 移除上一段；试图越过 pack 根目录的路径返回 `null`。不含 `..` 的路径保持原
字面语义，目录 entry、歧义顶层目录和异常回退规则不变。

失败优先 fixture 在压缩包中验证带父目录段的成功读取及越过根目录的安全回退；实现后
`ShaderPackResourcePathTest` 通过。

## 后续安全修复：folder 路径根边界

复核 folder 与 zip 的路径规范化时发现，folder 仅去除可选首尾 `/` 后直接构造 `File`；`..`
或多重首 `/` 可以把读取目标带出 shader pack 根目录。扩展同一 fixture 在 pack 外创建同级
文件，基线会错误返回该文件的流。

`ShaderPackFolder` 现在将 pack 根和资源路径解析为 canonical file，并要求资源路径严格位于
pack 根下且不是根本身；越界、绝对逃逸、缺失和目录均回退为 `null`。有效资源仍返回原有
`BufferedInputStream`，首尾 `/` 兼容及调用方关闭责任不变。

## 验证边界

使用 Java 17 独立编译并运行 fixture 通过；完整主/测试源码编译、全部 fixture、Gradle
`test/build` 的实际结果记录在交付报告。没有启动客户端、加载 shader pack 或建立 OpenGL
上下文，真实 zip/folder shader pack 加载和 FPS 仍需运行时验证。
