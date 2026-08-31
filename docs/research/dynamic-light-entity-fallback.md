# 动态光照实体空值回退

## 调查结论

`ItemRendererMixin` 的实体光照合并入口通常传入 `renderViewEntity`，但
`DynamicLights.getCombinedLight(Entity, int)` 是公开 API，也可能被其它渲染器
以空实体调用。原实现对空实体只跳过最大亮度早退，随后仍进入
`getLightLevel(null)`；该方法会访问 `ShaderConfig.getMinecraft().renderViewEntity`，
在客户端尚未完成初始化或调用方没有实体时抛出 `NullPointerException`，而不是保留
原版已经计算出的合并亮度。

本地 OptiFine 版本没有空值保护，但目标移植的实体 overload 位于兼容边界：动态光照
只是对已有亮度做可选合并，实体未知时没有可查询的额外光源信息。安全行为应当是直接
返回输入亮度；非空实体的最大亮度早退和动态光照公式保持不变。

## 失败优先 fixture

`DynamicLightEntityFallbackTest` 在修复前直接调用
`getCombinedLight((Entity) null, 0xA00020)`，复现 `NullPointerException`；修复后断言
返回值仍为 `0xA00020`。fixture 不创建实体、世界或 OpenGL 上下文。

## 最小实现与边界

实体 overload 在任何其它逻辑前对 `null` 返回原始 `combinedLight`。该分支不访问
Minecraft 单例、不扫描动态光源，也不改变动态光照开关；非空实体（包括标准最大方块
亮度编码）的既有路径完全保留。

## 验证范围

使用 Java 17 直接编译并运行本 fixture，随后运行全部独立 fixture；全量主/测试源码
编译已真实尝试，但当前无 access widener 的 MITE classpath 在既有私有字段访问处失败，
不能将该次尝试计为成功。`git diff --check` 通过。未启动客户端、shader pack 或
OpenGL，实体渲染入口的 Mixin 织入和 FPS 仍需真实运行时验证。
