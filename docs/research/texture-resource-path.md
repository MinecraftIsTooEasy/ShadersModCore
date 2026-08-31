# 着色器纹理法线/高光资源路径修复

## 调查结论

`ShadersTex.getNSMapLocation` 原先使用 `String.split(".png")` 去除扩展名。`.` 在正则中是通配符，因此合法的 `textures/block/pngstone.png` 会在目录的 `/png` 处提前截断，生成错误的法线/高光资源路径。

实现改为仅在路径末尾存在字面 `.png` 时用 `substring` 去除四个字符；无 `.png` 后缀的路径保持原 basename，资源域和已有 `_n.png`/`_s.png` 输入的命名规则不变。审查发现该修复曾遗漏 OptiFine 对空 `ResourceLocation` 的兼容返回，现已恢复 `null` 直接返回 `null`。

## 回归 fixture

`ShadersTexResourcePathTest` 使用 `pngstone.png` 覆盖通配符回归，并验证资源域、无后缀路径、已有 `_n`/`_s` 后缀和空位置；旧实现会在这些回归点失败，修复后通过。

## 验证范围

Java 17 直接编译全部主源码和测试源码后，六个独立 fixture 全部通过。没有启动客户端、加载 shader pack 或建立 OpenGL 上下文；本修复的真实资源包加载仍需运行时验证。
