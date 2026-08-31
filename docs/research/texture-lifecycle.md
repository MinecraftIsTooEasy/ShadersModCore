# 纹理替换生命周期

## 调查结论

MITE 的 `TextureManager.loadTexture(ResourceLocation, TextureObject)` 先调用新对象的
`loadTexture`，成功或 `IOException` 回退后才把对象写入 `mapTextureObjects`；其它
`Throwable` 会包装为 `ReportedException` 并重新抛出。替换旧对象时，若先释放旧对象却
保留 map 引用，编程异常路径会留下指向已释放纹理的缓存项，因此入口清理同时移除
旧 map 项和 `listTickables` 项。正常重载同一对象时跳过清理，保留 tick 列表和原版
重载语义。

MITE 的 `AbstractTexture` 构造函数把 `glTextureId` 初始化为 `-1`，`getGlTextureId`
只在值为 `-1` 时调用 `TextureUtil.glGenTextures`。因此解绑必须恢复 `-1`；写入 `0`
会让后续绑定把无效的 0 当作已分配纹理。

`TextureUtil.missingTexture` 是共享的静态 `DynamicTexture`，失败回退时可能被多个
资源位置共同引用，替换清理明确跳过它，避免删除共享 GL 状态。旧对象是
`AbstractTexture` 时，解绑使用非懒惰的 `getMultiTexID0`，从 `multiTexMap` 移除 base
键，清除对象的辅助状态并释放 base/norm/spec；无辅助状态时不分配新 `MultiTexID`。
`MultiTexID.base` 与对象 base ID 不一致仍输出 stderr 并额外删除异常 base，保持编程
错误可见。同一纹理对象若被多个资源位置别名引用，清理只移除当前 key；确认没有
其它 map 引用后才移除全部重复 tick 项并释放 GL 状态。

## 失败优先 fixture

`ShadersTexTextureLifecycleTest` 使用不创建 Minecraft 或 OpenGL 上下文的 accessor
替身，覆盖已有辅助 ID 的解绑、map 移除、`-1` sentinel、无辅助状态和重复解绑的
幂等性。替身的懒惰 accessor 会主动失败，确保释放路径不会通过 `getMultiTexID()`
重新分配辅助纹理。

## 验证范围

使用本地 Loom merged Minecraft jar、已有主类输出和 Java 17 对本 fixture 做最小编译
和执行；`git diff --check` 通过。补齐本地 GLU 依赖后，全量 `src/main/java` 与
`src/test/java` 也以 `javac --release 17 -proc:none` 编译成功，独立 fixture 全部通过。
直接调用缓存的 Gradle 8.5 执行 Java 17 `test` 和 `build` 均在 daemon 启动时因沙箱
禁止绑定 socket 失败，不能据此宣称 Gradle 通过。未启动客户端、Mixin 织入、资源
重载或 OpenGL 上下文，实际驱动删除调用和渲染/FPS 结果仍需运行时验证。
