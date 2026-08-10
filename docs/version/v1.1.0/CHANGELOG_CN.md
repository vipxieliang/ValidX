# ValidX v1.1.0 更新日志

**发布日期：** 2026年8月10日

本文档记录从 v1.0.1 到 v1.1.0 的变更内容。

## 变更概览

- ⚠️ [破坏性变更](#破坏性变更-️)
  - `@PastDate` 支持自定义格式（破坏性变更）
  - `@FutureDate` 支持自定义格式（破坏性变更）
- ✨ [新增功能](#新增功能-)
  - 新增 `@ChineseName` 中国人姓名验证注解
  - 新增 `@Date` 日期格式验证注解
  - 新增 `@DateTime` 日期时间格式验证注解
  - 新增 `@PastDateTime` 过去日期时间验证注解
  - 新增 `@FutureDateTime` 未来日期时间验证注解
  - 新增 `@NotContains` 字符串不包含验证注解
- 🔧 [功能增强](#功能增强-)：日期验证器支持自定义格式（pattern 参数）

---

## 破坏性变更 ⚠️
### 1. @PastDate 支持自定义格式（破坏性变更）

为现有的 `@PastDate` 注解新增可选的 `pattern` 参数，支持自定义日期格式。

**增强内容：**
- 新增可选的 `pattern` 参数
- 默认格式保持 `yyyy-MM-dd`，**向后兼容**
- 支持自定义日期格式（但不允许包含时间部分）
- 原有的 `includeToday` 参数保持不变
- 智能格式验证：pattern **不能**包含时间格式符号
- **采用严格验证模式（ResolverStyle.STRICT）**：自动拒绝无效日期（如 2024-02-30、2023-02-29）
- **严格格式匹配**：输入必须完全匹配 pattern 的长度和格式（如使用 `yyyy-MM-dd` 时必须补零）

**注解方式示例：**

```java
public class HistoryDTO {
    // 向后兼容：默认格式（yyyy-MM-dd）
    @PastDate
    private String birthDate;

    // 新功能：自定义格式
    @PastDate(pattern = "dd-MM-yyyy")
    private String europeanDate;

    @PastDate(pattern = "MM/dd/yyyy")
    private String usDate;

    // 包含今天 + 自定义格式
    @PastDate(includeToday = true, pattern = "yyyy年MM月dd日")
    private String chineseDate;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 三级 API 设计

// 级别 1：无参数（默认：includeToday=false，pattern="yyyy-MM-dd"）
validator.isPastDate("2020-01-01");

// 级别 2：自定义 includeToday（pattern="yyyy-MM-dd"）
validator.isPastDate("2020-01-01", false);

// 级别 3：完全自定义
validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");
validator.isPastDate("01-01-2020", false, "dd-MM-yyyy");
```

**向后兼容性：**

```java
// ✅ v1.0.1 代码在 v1.1.0 中无需修改，完全兼容
@PastDate  // 仍然默认使用 "yyyy-MM-dd"
private String birthDate;

@PastDate(includeToday = true)  // includeToday 参数正常工作
private String lastUpdate;
```

---

---

### 2. @FutureDate 支持自定义格式（破坏性变更）

为现有的 `@FutureDate` 注解新增可选的 `pattern` 参数，支持自定义日期格式。

**增强内容：**
- 新增可选的 `pattern` 参数
- 默认格式保持 `yyyy-MM-dd`，**向后兼容**
- 支持自定义日期格式（但不允许包含时间部分）
- 原有的 `includeToday` 参数保持不变
- 智能格式验证：pattern **不能**包含时间格式符号
- **采用严格验证模式（ResolverStyle.STRICT）**：自动拒绝无效日期（如 2024-02-30、2023-02-29）
- **严格格式匹配**：输入必须完全匹配 pattern 的长度和格式（如使用 `yyyy-MM-dd` 时必须补零）

**注解方式示例：**

```java
public class PlanDTO {
    // 向后兼容：默认格式（yyyy-MM-dd）
    @FutureDate
    private String eventDate;

    // 新功能：自定义格式
    @FutureDate(pattern = "MM/dd/yyyy")
    private String usDate;

    @FutureDate(pattern = "dd-MM-yyyy")
    private String europeanDate;

    // 包含今天 + 自定义格式
    @FutureDate(includeToday = true, pattern = "yyyy年MM月dd日")
    private String deadline;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 三级 API 设计

// 级别 1：无参数（默认：includeToday=false，pattern="yyyy-MM-dd"）
validator.isFutureDate("2025-12-31");

// 级别 2：自定义 includeToday（pattern="yyyy-MM-dd"）
validator.isFutureDate("2025-12-31", true);

// 级别 3：完全自定义
validator.isFutureDate("12/31/2025", false, "MM/dd/yyyy");
validator.isFutureDate("31-12-2025", false, "dd-MM-yyyy");
```

**向后兼容性：**

```java
// ✅ v1.0.1 代码在 v1.1.0 中无需修改，完全兼容
@FutureDate  // 仍然默认使用 "yyyy-MM-dd"
private String eventDate;

@FutureDate(includeToday = true)  // includeToday 参数正常工作
private String deadline;
```

---

---

## 新增功能 ✨

### 1. @ChineseName 中国人姓名验证注解

新增中国人姓名验证器，符合中国姓名规范。

**功能特性：**
- 仅允许中文字符
- 长度在 2-50 个字符之间（覆盖所有中文姓名，包括极长的少数民族姓名）
- 支持少数民族姓名中的间隔号 "·"
- 不允许数字、字母或特殊字符
- 完整的国际化支持（9 种语言）

**注解方式示例：**

```java
// 示例 1：基本使用
public class UserDTO {
    @ChineseName
    private String realName;
}

// 示例 2：必填字段
public class RegistrationDTO {
    @NotBlank(message = "姓名不能为空")
    @ChineseName
    private String name;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 汉族姓名
validator.field("姓名").isChineseName("张三");
validator.field("姓名").isChineseName("欧阳修");
validator.field("姓名").isChineseName("诸葛亮");

// 少数民族姓名（带间隔号）
validator.field("姓名").isChineseName("买买提·吐尔逊");
validator.field("姓名").isChineseName("迪丽热巴·迪力木拉提");

// 历史人物名
validator.field("姓名").isChineseName("爱新觉罗·玄烨");

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：用户注册
public class UserRegistrationDTO {
    @NotBlank(message = "真实姓名不能为空")
    @ChineseName
    private String realName;

    @ChineseIdCard
    private String idCard;
}

// 场景 2：身份验证
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("真实姓名").isChineseName(realName)
    .field("身份证号").isChineseIdCard(idCard);

// 场景 3：表单验证
@RestController
public class UserController {
    @PostMapping("/verify")
    public Result verify(@Valid @RequestBody UserVerifyDTO dto) {
        return verifyService.verify(dto);
    }
}
```

---

### 2. @Date 日期格式验证注解

新增纯日期格式验证注解，验证字符串是否符合指定的日期格式（不包含时间部分），不关心日期是过去还是未来。

**功能特性：**
- 验证字符串是否为有效的纯日期格式（不包含时间）
- 默认格式：`yyyy-MM-dd`
- 支持自定义格式（pattern 参数）
- 采用严格验证模式（ResolverStyle.STRICT）
- 自动拒绝无效日期（如 2024-02-30、2023-02-29）
- pattern 不能包含时间符号（H, h, K, k, m, s, S, a, A, n, N）
- 完整的国际化支持（9 种语言）
- **严格格式匹配**：输入必须完全匹配 pattern 的长度和格式（如使用 `yyyy-MM-dd` 时必须补零）

**注解方式示例：**

```java
public class EventDTO {
    // 示例 1：默认格式（yyyy-MM-dd）
    @Date
    private String eventDate;

    // 示例 2：自定义日期格式
    @Date(pattern = "dd/MM/yyyy")
    private String europeanDate;

    // 示例 3：紧凑格式
    @Date(pattern = "yyyyMMdd")
    private String compactDate;

    // 示例 4：美国格式
    @Date(pattern = "MM/dd/yyyy")
    private String usDate;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 默认格式（yyyy-MM-dd）
validator.field("日期").isDate("2024-01-15");

// 自定义格式
validator.field("日期").isDate("15/01/2024", "dd/MM/yyyy");
validator.field("紧凑日期").isDate("20240115", "yyyyMMdd");

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：API 参数验证
@RestController
public class OrderController {
    @PostMapping("/orders")
    public Result createOrder(@Valid @RequestBody OrderDTO dto) {
        return orderService.create(dto);
    }
}

public class OrderDTO {
    @Date(pattern = "yyyy-MM-dd")
    private String deliveryDate;  // 只验证日期格式，不限制过去/未来
}

// 场景 2：批量数据导入验证
ValidX validator = ValidX.init();
for (String date : importedDates) {
    validator.field("导入日期").isDate(date, userDefinedPattern);
}

// 场景 3：配置文件日期验证
@Configuration
public class AppConfig {
    @Date(pattern = "yyyy-MM-dd")
    private String systemStartDate;
}
```

---

### 3. @DateTime 日期时间格式验证注解

新增日期时间格式验证注解，验证字符串是否符合指定的日期时间格式（必须包含时间部分），不关心日期时间是过去还是未来。

**功能特性：**
- 验证字符串是否为有效的日期时间格式（必须包含时间）
- 默认格式：`yyyy-MM-dd HH:mm:ss`
- 支持自定义格式（pattern 参数）
- 采用严格验证模式（ResolverStyle.STRICT）
- 自动拒绝无效日期和时间（如 2024-02-30 13:00:00、2024-01-15 25:00:00）
- pattern 必须包含时间符号（H, h, K, k, m, s, S, a, A, n, N）
- 完整的国际化支持（9 种语言）
- **严格格式匹配**：输入必须完全匹配 pattern 包括时间部分（如缺少秒部分会验证失败）

**注解方式示例：**

```java
public class LogDTO {
    // 示例 1：默认格式（yyyy-MM-dd HH:mm:ss）
    @DateTime
    private String timestamp;

    // 示例 2：ISO 8601 格式
    @DateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String isoDateTime;

    // 示例 3：包含毫秒
    @DateTime(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String preciseTime;

    // 示例 4：12小时制
    @DateTime(pattern = "yyyy-MM-dd hh:mm:ss a")
    private String amPmTime;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 默认格式（yyyy-MM-dd HH:mm:ss）
validator.field("时间戳").isDateTime("2024-01-15 13:30:00");

// 自定义格式
validator.field("ISO时间").isDateTime("2024-01-15T13:30:00", "yyyy-MM-dd'T'HH:mm:ss");
validator.field("精确时间").isDateTime("2024-01-15 13:30:00.123", "yyyy-MM-dd HH:mm:ss.SSS");

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：日志记录
@RestController
public class LogController {
    @PostMapping("/logs")
    public Result saveLog(@Valid @RequestBody LogDTO dto) {
        return logService.save(dto);
    }
}

public class LogDTO {
    @DateTime
    private String occurredAt;  // 验证时间戳格式
}

// 场景 2：事件追踪
ValidX validator = ValidX.init();
validator.field("事件时间").isDateTime(eventTime, "yyyy-MM-dd HH:mm:ss");

// 场景 3：数据库导入
for (String timestamp : timestamps) {
    if (!validator.isDateTime(timestamp).passed()) {
        // 处理格式错误
    }
}
```

---

### 4. @PastDateTime 过去日期时间验证注解

新增专用的过去日期时间验证注解，用于验证包含时间部分的过去日期时间字符串。

**功能特性：**
- 验证日期时间是否为过去的时间（必须包含时间部分）
- 默认格式：`yyyy-MM-dd HH:mm:ss`
- 支持自定义格式（pattern 参数）
- 支持 `includeToday` 参数，控制是否包含当前时间
- 格式验证确保 pattern 必须包含时间组件（H, h, K, k, m, s, S, a, A, n, N）
- 完整的国际化支持（9 种语言）
- **采用严格验证模式（ResolverStyle.STRICT）**：自动拒绝无效日期时间
- **严格格式匹配**：输入必须完全匹配 pattern 包括时间部分（如缺少秒部分会验证失败）

**注解方式示例：**

```java
public class RecordDTO {
    // 示例 1：默认格式（yyyy-MM-dd HH:mm:ss）
    @PastDateTime
    private String createdAt;

    // 示例 2：自定义格式
    @PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String isoDateTime;

    // 示例 3：包含当前时间
    @PastDateTime(includeToday = true)
    private String lastModified;

    // 示例 4：自定义格式 + 包含当前时间
    @PastDateTime(includeToday = true, pattern = "yyyy/MM/dd HH:mm:ss")
    private String processedAt;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 默认格式（yyyy-MM-dd HH:mm:ss）
validator.field("创建时间").isPastDateTime("2024-01-01 10:30:00");

// 自定义格式
validator.field("ISO时间").isPastDateTime(
    "2024-01-01T10:30:00",
    false,
    "yyyy-MM-dd'T'HH:mm:ss"
);

// 包含当前时间
validator.field("最后修改").isPastDateTime(
    "2026-08-06 09:00:00",
    true  // 允许当前时间
);
```

---

### 5. @FutureDateTime 未来日期时间验证注解

新增专用的未来日期时间验证注解，用于验证包含时间部分的未来日期时间字符串。

**功能特性：**
- 验证日期时间是否为未来的时间（必须包含时间部分）
- 默认格式：`yyyy-MM-dd HH:mm:ss`
- 支持自定义格式（pattern 参数）
- 支持 `includeToday` 参数，控制是否包含当前时间
- 格式验证确保 pattern 必须包含时间组件（H, h, K, k, m, s, S, a, A, n, N）
- 完整的国际化支持（9 种语言）
- **采用严格验证模式（ResolverStyle.STRICT）**：自动拒绝无效日期时间
- **严格格式匹配**：输入必须完全匹配 pattern 包括时间部分（如缺少秒部分会验证失败）

**注解方式示例：**

```java
public class EventDTO {
    // 示例 1：默认格式（yyyy-MM-dd HH:mm:ss）
    @FutureDateTime
    private String scheduledAt;

    // 示例 2：自定义格式
    @FutureDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String isoSchedule;

    // 示例 3：包含当前时间
    @FutureDateTime(includeToday = true)
    private String appointmentTime;

    // 示例 4：自定义格式 + 包含当前时间
    @FutureDateTime(includeToday = true, pattern = "yyyy/MM/dd HH:mm:ss")
    private String meetingTime;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 默认格式（yyyy-MM-dd HH:mm:ss）
validator.field("预约时间").isFutureDateTime("2025-12-31 23:59:59");

// 自定义格式
validator.field("ISO时间").isFutureDateTime(
    "2025-12-31T23:59:59",
    false,
    "yyyy-MM-dd'T'HH:mm:ss"
);

// 包含当前时间
validator.field("会议时间").isFutureDateTime(
    "2026-08-06 10:00:00",
    true  // 允许当前时间
);
```

---

### 6. @NotContains 字符串不包含验证注解

新增专用的字符串不包含验证注解，用于验证字符串是否不包含指定的子字符串。适用于安全验证、内容过滤和防止敏感关键词。

**功能特性：**
- 验证字符串是否不包含禁止的子字符串
- 支持多个禁止子字符串的验证
- 支持忽略大小写匹配（`ignoreCase` 参数）
- 支持两种匹配模式：
  - **AND 逻辑**（默认，`matchAll = true`）：必须所有禁止的子字符串都不包含
  - **OR 逻辑**（`matchAll = false`）：至少有一个禁止的子字符串不包含即可
- 完整的国际化支持（9 种语言）
- 与 `@Contains` 注解互补，提供全面的字符串验证

**注解方式示例：**

```java
public class SecurityDTO {
    // 示例 1：安全验证 - 阻止保留关键字（AND 逻辑，默认）
    @NotContains(value = {"admin", "root", "system"}, ignoreCase = true)
    private String username;

    // 示例 2：内容过滤 - 阻止敏感词
    @NotContains(value = {"垃圾", "广告"}, ignoreCase = true)
    private String comment;

    // 示例 3：XSS防护 - 阻止脚本注入
    @NotContains(value = {"<script", "javascript:", "onerror="}, ignoreCase = true)
    private String userInput;

    // 示例 4：URL安全验证（AND 逻辑 - 必须都不包含）
    @NotContains(value = {"javascript:", "data:", "vbscript:"}, matchAll = true)
    private String url;

    // 示例 5：OR 逻辑 - 至少有一个不包含即可
    @NotContains(value = {"script", "alert"}, matchAll = false)
    private String description;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 基本用法（AND 逻辑 - 默认）
validator.field("用户名").isNotContains("user123", new String[]{"admin", "root"});

// 忽略大小写
validator.field("用户名").isNotContains("normaluser", new String[]{"ADMIN", "ROOT"}, true);

// OR 逻辑 - 至少有一个不包含即可
validator.field("内容").isNotContains("hello world", new String[]{"script", "alert"}, false, false);

// AND 逻辑 - 必须全都不包含
validator.field("URL").isNotContains("https://example.com", new String[]{"javascript:", "data:"}, false, true);
```

**实际应用场景：**

```java
// 场景 1：用户注册验证
@RestController
public class UserController {
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegistrationDTO dto) {
        return userService.register(dto);
    }
}

public class UserRegistrationDTO {
    @NotBlank(message = "用户名不能为空")
    @NotContains(value = {"admin", "root", "system", "test"}, ignoreCase = true)
    private String username;
}

// 场景 2：内容审核
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("评论内容").isNotContains(comment, new String[]{"垃圾", "广告", "spam"}, true);

// 场景 3：XSS防护
ValidX validator = ValidX.init();
validator.field("用户输入").isNotContains(
    userInput,
    new String[]{"<script", "javascript:", "onerror=", "onclick="},
    true,  // 忽略大小写
    true   // AND 逻辑：必须全部不包含
);

// 场景 4：文件名安全验证
public class FileUploadDTO {
    @NotContains(value = {"../", "..\\", "/etc/", "C:\\"}, matchAll = true)
    private String fileName;
}
```

**注意事项：**
- **AND 逻辑**（默认）：只有字符串不包含所有指定的子字符串时才通过验证
- **OR 逻辑**（`matchAll = false`）：字符串至少不包含一个指定的子字符串即可通过验证
- 默认区分大小写；使用 `ignoreCase = true` 可忽略大小写
- null 和空字符串默认通过验证（配合 `@NotNull` 或 `@NotEmpty` 使用）
- 常见应用场景：用户名验证（阻止保留关键字）、XSS防护、内容审核、URL安全验证
- 与 `@Contains` 互补：`@Contains` 验证必须包含，`@NotContains` 验证必须不包含

---

## 功能增强 🔧

### 1. 日期验证通用增强特性

所有日期/日期时间验证器（包括新增和增强的）都具备以下特性：

**智能格式验证**
- Date 验证器（`@PastDate`/`@FutureDate`）的 pattern **不能**包含时间格式符号
- DateTime 验证器（`@PastDateTime`/`@FutureDateTime`）的 pattern **必须**包含时间格式符号
- 违反规则时会在验证器初始化时抛出 `IllegalArgumentException` 并提供清晰的错误提示
- 避免了运行时的格式混淆问题

**严格日期验证**
- 使用 `ResolverStyle.STRICT` 模式
- 自动拒绝无效日期（如：2024-02-30、2023-02-29）
- 确保日期的合法性和准确性

**三级链式 API 设计**
- 无参数：使用默认值（includeToday=false，默认格式）
- 单参数：自定义 includeToday，使用默认格式
- 完整参数：自定义 includeToday 和自定义格式

**支持的日期格式符号：**

| 符号 | 含义 | 示例 |
|------|------|------|
| `yyyy` | 年份（4位） | `2024` |
| `MM` | 月份（补零） | `01`, `12` |
| `dd` | 日期（补零） | `05`, `25` |
| `HH` | 小时（24小时制，补零） | `00`, `23` |
| `mm` | 分钟（补零） | `00`, `59` |
| `ss` | 秒（补零） | `00`, `59` |

**常用格式示例：**
- 标准日期：`yyyy-MM-dd`
- 美国格式：`MM/dd/yyyy`
- 欧洲格式：`dd/MM/yyyy`
- ISO 8601：`yyyy-MM-dd'T'HH:mm:ss`
- 中文格式：`yyyy年MM月dd日`
- 紧凑格式：`yyyyMMdd`

---

### 2. 改进日期验证错误提示

增强了日期验证错误信息，提供更友好的国际化支持和清晰的错误提示。

**改进内容：**

1. **格式验证错误消息**
   - 当纯日期验证器（`@PastDate`/`@FutureDate`）的 pattern 包含时间格式符号时，提供清晰的错误信息
   - 当日期时间验证器（`@PastDateTime`/`@FutureDateTime`）的 pattern 缺少时间格式符号时，提供清晰的错误信息
   - 错误消息会明确指出问题所在，并建议使用正确的验证器

2. **完整的国际化支持（9 种语言）**
   - 所有错误信息支持以下语言：
     - 中文（默认）- `ValidationMessages.properties`
     - 简体中文 - `ValidationMessages_zh.properties`
     - 英语 - `ValidationMessages_en.properties`
     - 日语 - `ValidationMessages_ja.properties`
     - 韩语 - `ValidationMessages_ko.properties`
     - 法语 - `ValidationMessages_fr.properties`
     - 德语 - `ValidationMessages_de.properties`
     - 西班牙语 - `ValidationMessages_es.properties`
     - 俄语 - `ValidationMessages_ru.properties`
   - 所有语言包的消息格式保持一致
   - 属性文件采用正确的 Unicode 编码

## 相关链接 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.1.0)
- 📖 [完整文档](../../../README.cn.md)
- 🐛 [问题反馈](https://github.com/vipxieliang/ValidX/issues)
- 💡 [功能建议](https://github.com/vipxieliang/ValidX/issues/new)

---

由 ValidX 团队用 ❤️ 发布
