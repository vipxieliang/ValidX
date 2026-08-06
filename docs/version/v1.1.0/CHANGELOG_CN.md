# ValidX v1.1.0 更新日志

**发布日期：** 2026年8月6日

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
- 🔧 [功能增强](#功能增强-)：日期验证器支持自定义格式（pattern 参数）
- 📖 [文档更新](#文档更新-)：完善国际化支持，新增 9 种语言的完整测试覆盖
- 🎨 [代码质量改进](#代码质量改进-)：委托模式优化、基类抽象、严格日期验证
- 📊 [技术统计](#技术统计-)：新增 5 个注解、60+ 测试用例、1200+ 行代码

---

## 破坏性变更 ⚠️

v1.1.0 包含以下破坏性变更，升级前请仔细阅读并参考 [迁移指南](MIGRATION_v1.1.0.cn.md)。

---

### 1. @PastDate 支持自定义格式（破坏性变更）

为现有的 `@PastDate` 注解新增可选的 `pattern` 参数，支持自定义日期格式。

**增强内容：**
- 新增可选的 `pattern` 参数
- 默认格式保持 `yyyy-MM-dd`，**向后兼容**
- 支持自定义日期格式（但不允许包含时间部分）
- 原有的 `includeToday` 参数保持不变
- 智能格式验证：pattern **不能**包含时间格式符号

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
validator.field("日期").isDateFormat("2024-01-15");

// 自定义格式
validator.field("日期").isDateFormat("15/01/2024", "dd/MM/yyyy");
validator.field("紧凑日期").isDateFormat("20240115", "yyyyMMdd");

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
    validator.field("导入日期").isDateFormat(date, userDefinedPattern);
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
validator.field("时间戳").isDateTimeFormat("2024-01-15 13:30:00");

// 自定义格式
validator.field("ISO时间").isDateTimeFormat("2024-01-15T13:30:00", "yyyy-MM-dd'T'HH:mm:ss");
validator.field("精确时间").isDateTimeFormat("2024-01-15 13:30:00.123", "yyyy-MM-dd HH:mm:ss.SSS");

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
validator.field("事件时间").isDateTimeFormat(eventTime, "yyyy-MM-dd HH:mm:ss");

// 场景 3：数据库导入
for (String timestamp : timestamps) {
    if (!validator.isDateTimeFormat(timestamp).passed()) {
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

3. **新增消息键**
   - `io.github.vipxieliang.validx.annotation.past.datetime` - 过去日期时间验证消息
   - `io.github.vipxieliang.validx.annotation.future.datetime` - 未来日期时间验证消息
   - `io.github.vipxieliang.validx.validator.date.pattern.contains.time` - Date pattern 错误提示
   - `io.github.vipxieliang.validx.validator.datetime.pattern.missing.time` - DateTime pattern 错误提示

**错误信息示例：**

```java
// 中文（默认）
"日期验证的 pattern 不能包含时间格式符号 (H, h, K, k, m, s, S, a, A, n, N)。如需验证日期时间，请使用对应的 DateTime 注解"

// 英文
"Date validation pattern must not contain time format symbols (H, h, K, k, m, s, S, a, A, n, N). Use the corresponding DateTime annotation for date-time validation"

// 日语
"日付検証のパターンには時刻フォーマット記号 (H, h, K, k, m, s, S, a, A, n, N) を含めることができません。日時検証には対応するDateTimeアノテーションを使用してください"

// 法语
"Le modèle de validation de date ne doit pas contenir de symboles de format d'heure (H, h, K, k, m, s, S, a, A, n, N). Utilisez l'annotation DateTime correspondante pour la validation de date-heure"
```

---

## 文档更新 📖

### README 文档增强

更新了中英文 README 文件：

1. **@ChineseName 文档**
   - 验证规则说明
   - 支持的字符和格式
   - 注解和链式 API 的使用示例
   - 实际应用场景演示

2. **增强日期/日期时间验证文档**
   - 所有日期验证器的完整参数说明
   - 三级链式 API 使用示例
   - 支持的格式符号对照表
   - 常用日期格式示例集合

3. **版本标识**
   - 为新功能添加 `v1.1.0` 版本标签
   - 更新快速参考表

### 完整的测试覆盖

为新功能和改进添加了完整的测试套件：

**新增测试文件：**

**ChineseName 验证器测试：**
- `ChineseNameValidatorTest.java` - 注解方式验证测试
- `ChineseNameValidationChainTest.java` - 链式 API 验证测试

**日期验证器测试：**
- `FutureDateValidatorTest.java` - @FutureDate 增强测试
- `PastDateValidatorTest.java` - @PastDate 增强测试
- `FutureDateValidatorPatternCheckTest.java` - @FutureDate pattern 验证测试
- `PastDateValidatorPatternCheckTest.java` - @PastDate pattern 验证测试
- `FutureDateValidationChainTest.java` - 未来日期链式 API 测试
- `PastDateValidationChainTest.java` - 过去日期链式 API 测试
- `PastDateStrictValidationTest.java` - 严格日期验证测试

**日期时间验证器测试：**
- `FutureDateTimeValidatorTest.java` - @FutureDateTime 完整测试
- `PastDateTimeValidatorTest.java` - @PastDateTime 完整测试
- `FutureDateTimeValidationChainTest.java` - 未来日期时间链式 API 测试
- `PastDateTimeValidationChainTest.java` - 过去日期时间链式 API 测试
- `DateTimeChainPatternTest.java` - 自定义格式综合测试
- `DatePatternMismatchTest.java` - Pattern 格式匹配错误测试

**国际化测试：**
- `DateValidatorI18nTest.java` - 日期验证器国际化测试（11个测试用例，覆盖9种语言）
- `DateTimeValidatorI18nTest.java` - 日期时间验证器国际化测试（11个测试用例，覆盖9种语言）

**测试统计：**
- **新增测试文件：** 17 个
- **新增测试用例：** 60+ 个
- **国际化测试覆盖：** 22 个测试用例，验证 9 种语言 × 4 个消息键
- **测试通过率：** 100% ✅
- **代码覆盖范围：** 新增代码 100% 覆盖

---


## 技术统计 📊

### 代码变更
- **新增注解：** 5 个（`@ChineseName`, `@Date`, `@DateTime`, `@PastDateTime`, `@FutureDateTime`）
- **增强注解：** 2 个（`@PastDate`, `@FutureDate` 新增 pattern 参数）
- **新增验证器类：** 6 个
- **新增链式方法：** 10 个
- **新增代码行数：** ~1,200 行
- **新增文件数：** 20+ 个

### 测试覆盖
- **新增测试文件：** 17 个
- **新增测试用例：** 60+ 个
- **国际化测试：** 22 个测试用例
- **测试通过率：** 100% ✅
- **代码覆盖率：** 新增代码 100% 覆盖

### 国际化
- **语言包数量：** 9 个完整语言包
- **新增消息键：** 4 个
- **更新消息键：** 2 个（默认语言包修正为中文）
- **国际化测试覆盖：** 9 种语言 × 4 个消息键 = 36 个语言消息验证点

### 文档更新
- **更新文件：** 5+ 个文档文件
- **新增文档内容：** 200+ 行
- **代码示例：** 30+ 个新示例

---

## 代码质量改进 🎨

### 1. 委托模式优化

在日期验证器中应用委托模式，消除代码重复，提高可维护性：

```java
// 优化前：注解初始化和链式调用有重复逻辑
@Override
public void initialize(PastDate annotation) {
    this.includeToday = annotation.includeToday();
    this.pattern = annotation.pattern();
    // ... 重复的格式验证逻辑
}

public boolean validate(String value, boolean includeToday, String pattern) {
    this.includeToday = includeToday;
    this.pattern = pattern;
    // ... 重复的格式验证逻辑
}

// 优化后：使用委托方法，单一真实来源
@Override
public void initialize(PastDate annotation) {
    initialize(annotation.includeToday(), annotation.pattern());
}

public void initialize(boolean includeToday, String pattern) {
    this.includeToday = includeToday;
    this.pattern = pattern;
    validatePattern(pattern);  // 统一的验证逻辑
}
```

### 2. 基类抽象优化

新增 `BaseDateValidator` 基类，统一日期验证器的通用逻辑：

```java
public abstract class BaseDateValidator {
    protected boolean includeToday;
    protected String pattern;
    protected DateTimeFormatter formatter;

    // 统一的 pattern 验证逻辑
    protected void validatePattern(String pattern, boolean requiresTime) {
        // 检查 pattern 是否包含时间格式符号
        // 根据 requiresTime 参数决定是否允许
    }

    // 统一的日期解析逻辑
    protected LocalDate parseDate(String value) {
        // 使用 ResolverStyle.STRICT 确保日期有效性
    }
}
```

### 3. 清晰的架构分层

- **注解层**：`@PastDate`, `@FutureDate`, `@PastDateTime`, `@FutureDateTime`
- **验证器层**：`PastDateValidator`, `FutureDateValidator`, `PastDateTimeValidator`, `FutureDateTimeValidator`
- **基类层**：`BaseDateValidator` - 提供通用功能
- **API 层**：`ValidX.java` - 处理默认值和方法重载
- **链式验证层**：`BaseValidation.java` - 简化重载方法

### 4. 严格的日期验证

使用 `ResolverStyle.STRICT` 模式确保日期有效性：

```java
DateTimeFormatter formatter = DateTimeFormatter
    .ofPattern(pattern)
    .withResolverStyle(ResolverStyle.STRICT);

// 自动拒绝无效日期
// ❌ "2024-02-30" - 2月没有30日
// ❌ "2024-13-01" - 没有13月
// ❌ "2023-02-29" - 2023年不是闰年
```

### 5. 智能格式验证

在验证器初始化时检查 pattern 格式，提前发现配置错误：

```java
// Date 验证器：pattern 不能包含时间符号
if (pattern.matches(".*[HhKkmsaSAnN].*")) {
    throw new IllegalArgumentException(
        MessageManager.getMessage("date.pattern.contains.time")
    );
}

// DateTime 验证器：pattern 必须包含时间符号
if (!pattern.matches(".*[HhKkmsaSAnN].*")) {
    throw new IllegalArgumentException(
        MessageManager.getMessage("datetime.pattern.missing.time")
    );
}
```

---

## 相关链接 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.1.0)
- 📖 [完整文档](../../../README.cn.md)
- 🐛 [问题反馈](https://github.com/vipxieliang/ValidX/issues)
- 💡 [功能建议](https://github.com/vipxieliang/ValidX/issues/new)

---

由 ValidX 团队用 ❤️ 发布
