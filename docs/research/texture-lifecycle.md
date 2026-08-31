# 纹理替换生命周期

## 调查结论

MITE 的 `TextureManager.loadTexture(ResourceLocation, TextureObject)` 先调用新对象的
`loadTexture`，成功路径在方法尾部写入 `mapTextureObjects`；IOException 路径先写入
共享 `missingTexture`，随后仍会执行方法尾部写入；其它 `Throwable` 包装为
`ReportedException` 并重新抛出。原 HEAD 注入在加载开始时移除并释放旧对象，因此
IOException 会先破坏旧纹理，再把同一位置改成 missing texture，旧对象无法作为回退
继续绑定。

当前实现以 `ordinal = 0`、`require = 1`、`expect = 1` 包装 IOException catch 内的第一次
`Map.put`，以 `ordinal = 1`、`require = 1`、`expect = 1` 包装方法尾部的第二次 `Map.put`。前者在已有
有效旧对象时跳过 missing 覆盖并返回旧值（即原 `Map.put` 应返回的 previous value）；
后者通过 `@Local(index = 3)` 读取字节码中的 `var3`，仅在 `loaded == true` 且尾部 put
完成后清理旧对象。首次加载失败仍调用两次原版 put，保留 missing 回退；替换失败不释放
旧 map/list。成功替换若没有其它资源位置的同一对象身份别名，则移除全部重复 tick 项并
调用 `ShadersTex.deleteTextures`。同一对象重新加载和 shared missing 不会误释放；别名和
tick 清理按 `==` 身份比较，避免自定义 `equals` 误判。该实现不依赖不存在的 `THROW`
injection point。

`javap` 对 Loom merged jar 确认目标签名为
`loadTexture(ResourceLocation, TextureObject): boolean`，`mappings.tiny` 中
两次目标 `Map.put` 的调用顺序、`var3` 局部变量和 `mapTextureObjects`/`listTickables`
字段名与 Mixin 声明一致；FishModLoader 内置 MixinExtras 版本为 `0.3.5`，支持当前
`WrapOperation`/`@Local` 签名；`AbstractTextureMixin`
提供的 `getMultiTexID0`、`setMultiTexID` 和 `setGlTextureId` 也与 accessor 接口匹配。
独立编译不能替代真实 Mixin 织入验证，故仍需客户端启动检查注入日志。

MITE 的 `AbstractTexture` 构造函数把 `glTextureId` 初始化为 `-1`，`getGlTextureId`
只在值为 `-1` 时调用 `TextureUtil.glGenTextures`。因此解绑必须恢复 `-1`；写入 `0`
会让后续绑定把无效的 0 当作已分配纹理。

`TextureUtil.missingTexture` 是共享的静态 `DynamicTexture`，失败回退时可能被多个
资源位置共同引用，替换清理明确跳过它，避免删除共享 GL 状态。旧对象是
`AbstractTexture` 时，解绑使用非懒惰的 `getMultiTexID0`，从 `multiTexMap` 移除 base
键，清除对象的辅助状态并释放 base/norm/spec；无辅助状态时不分配新 `MultiTexID`。
`MultiTexID.base` 与对象 base ID 不一致仍输出 stderr，并且只对大于 0 的运行时 ID
调用 `glDeleteTextures`；`deleteMultiTex` 的非 `AbstractTexture` 分支也遵守该守卫。
同一纹理对象若被多个资源位置别名引用，清理只移除当前 key；确认没有其它 map 引用
后才移除全部重复 tick 项并释放 GL 状态。

## 失败优先 fixture

`ShadersTexTextureLifecycleTest` 使用不创建 Minecraft 或 OpenGL 上下文的 accessor
替身，覆盖已有辅助 ID 的解绑、map 移除、`-1` sentinel、无辅助状态、重复解绑和无效
0/负 ID，以及未分配的非 `AbstractTexture` 对象。替身的懒惰 accessor 会主动失败，确保释放路径不会通过 `getMultiTexID()`
重新分配辅助纹理。`TextureManagerTextureReplacementTest` 覆盖 IOException 保留旧映射、
失败后恢复已写入对象、成功替换、同一对象、shared missing、资源位置别名、重复 tick
项和自定义 `equals` 身份边界；它通过可注入的 missing sentinel 运行，不创建 GL 上下文。

## 验证范围

使用本地 Loom merged Minecraft jar、FishModLoader classpath 和 Java 17 对全部主/测试
源码做 `javac --release 17 -proc:none` 编译，并运行十七个独立 fixture（含 primitive
动态光照等价性），均通过；`git diff --check` 通过。Gradle 任务和运行时边界记录在最终
报告中。未启动客户端、Mixin 织入、资源重载或 OpenGL 上下文，实际驱动删除调用和
渲染/FPS 结果仍需运行时验证。
