# 配置读取调查与低风险切片

## 调查结论

| 候选 | 本地 OptiFine 参考 | 目标调用链 | 结论 |
| --- | --- | --- | --- |
| 运行时属性缓存 | `net/minecraft/src/Config.java` 的解析辅助 | `OptimizeConfig` 和 `Shaders` 启动时读取一次，运行时只访问 primitive 字段 | 没有可证明的热路径收益，不引入缓存 |
| `PropertiesOrdered` | `net/optifine/util/PropertiesOrdered.java` 只维护保存顺序 | 目标配置没有依赖稳定键顺序 | 不复制；顺序不会减少 I/O 或解析开销 |
| `ArrayCache` / `CacheLocal` | `net/optifine/util/ArrayCache.java`、`CacheLocal.java` 服务纹理/区块临时数组 | 目标配置路径没有数组分配或对应生命周期 | 无安全映射，留作渲染切片候选 |
| 配置文件加载 | OptiFine 每次加载建立干净属性集，并用 `Config.parseInt/parseFloat/parseBoolean` 回退 | 目标在首次启动写入配置前尚未设置默认值；重复加载会保留旧键；数值异常会中断启动 | 采用本轮最小切片 |

## 实际调用链

`MinecraftMixin.startGame` 的尾部先调用 `Shaders.startup`（其中执行 `Shaders.loadConfig`），随后执行 `OptimizeConfig.loadConfig`。设置页只在完成操作时调用对应 `storeConfig`；渲染循环直接读取 `OptimizeConfig` 的 primitive 字段，没有每帧 `Properties` 查找。

因此，直接添加 map/数组缓存不能降低运行时成本。可确认的浪费和风险位于启动配置 I/O：`OptimizeConfig` 首次启动会先把 Java 字段的零值写入 `optimize.txt`，再读取这些值，导致默认的粒子、动态光照等开关被持久化为 `false`；同时重复加载不会清理已删除的旧键。

## 选定切片

- `ConfigUtils` 提供无 Minecraft 依赖的 OptiFine 风格整数、浮点和布尔解析；非法整数/浮点回退到调用方默认值，布尔值保留 `Boolean.parseBoolean` 的兼容语义。
- `ShaderConfig` 保留现有 API，并委托给 `ConfigUtils`；`OptimizeConfig`、`Shaders` 的配置字段统一使用该解析器。
- `OptimizeConfig.loadConfig` 在读取前清空属性集合；配置不存在时先应用默认值再保存，避免首次启动写入 Java 零值。合法配置值和未知属性的保存行为不变。
- `Shaders.loadConfig` 同样在读取前清空属性集合，缓存一次 `exists()` 结果，并使用 try-with-resources 关闭读写器。

没有新增开关；文件不存在、读取失败或单个值损坏时自然回退到已有默认值。渲染线程、GL 调用和 GUI 行为未改动。

## 验证范围与未验证项

`ConfigParsingTest` 是不依赖 Minecraft/GL 的 Java 17 fixture，覆盖 trim、null、非法整数/浮点和 OptiFine 布尔语义。完整 Gradle 验证需要本机 wrapper/native 缓存权限；本轮记录实际命令结果于交付报告，不宣称客户端启动或 FPS 收益。首次启动落盘路径仍需在真实客户端数据目录中验证文件权限和默认值。
