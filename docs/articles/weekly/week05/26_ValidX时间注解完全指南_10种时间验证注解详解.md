# ValidX时间注解完全指南：10种时间验证注解详解

## 引言

时间是业务系统里最容易"出小错、藏大坑"的字段：生日填成 `2024-02-30`、签到时间漏了秒、时间戳少一位数、活动截止时间被当成过去时间……每一个错误都真实存在，而每一个错误都能让一次线上事故变成一个 Bug 工单。

ValidX 把时间验证拆成了 **10 个注解**，按职责分工：

- **格式验证**（值长什么样）：`@Date`、`@DateTime`、`@HourMinute`、`@HourMinuteSecond`
- **时间点验证**（值在过去还是未来）：`@PastDate`、`@FutureDate`、`@PastDateTime`、`@FutureDateTime`
- **特殊格式**（非人类可读的时间）：`@Timestamp`、`@Duration`

这篇文章不做"复制粘贴注解"式教程，而是逐行对照 10 个注解背后的 `Validator` 源码，把三个关键机制讲透：**严格解析（STRICT）到底严格在哪**、**过去/未来判断的边界语义**、**时间戳与时间段的正则/数值校验**。文章还会指出几个连官方文档都没写清楚的实现细节——比如"`@PastDate` 会悄悄把 `2024-02-30` 变成 `2024-02-29`"。

> 文中所有结论均对照 ValidX v1.2.0 源码逐一核实，关键行为附测试验证结果。

---

## 一、全景图：10 种注解的分类与定位

### 1.1 总览表

| 注解 | 验证内容 | 默认格式 | 是否含时间 | 支持类型 | 版本 |
|------|----------|----------|:---:|------|------|
| `@Date` | 纯日期格式 | `yyyy-MM-dd` | 否 | String | 1.1.0 |
| `@DateTime` | 日期时间格式 | `yyyy-MM-dd HH:mm:ss` | 是 | String | 1.1.0 |
| `@PastDate` | 过去日期 | `yyyy-MM-dd` | 否 | String | 1.0.0 |
| `@FutureDate` | 未来日期 | `yyyy-MM-dd` | 否 | String | 1.0.0 |
| `@PastDateTime` | 过去日期时间 | `yyyy-MM-dd HH:mm:ss` | 是 | String | 1.1.0 |
| `@FutureDateTime` | 未来日期时间 | `yyyy-MM-dd HH:mm:ss` | 是 | String | 1.1.0 |
| `@HourMinute` | `HH:mm` 时分 | 固定 `HH:mm` | 纯时间 | String | 1.0.0 |
| `@HourMinuteSecond` | `HH:mm:ss` 时分秒 | 固定 `HH:mm:ss` | 纯时间 | String | 1.0.0 |
| `@Timestamp` | Unix 时间戳（秒/毫秒） | 10 位或 13 位数字 | 时间戳 | String / Long | 1.0.0 |
| `@Duration` | 时间段（ISO 8601 / 简化） | 任意 | 时间段 | String | 1.0.0 |

### 1.2 三大设计原则

通读全部源码后，可以提炼出 ValidX 时间验证的三个贯穿性设计：

1. **空值一律放行**。10 个注解对 `null` 和空字符串都返回 `true`，把"是否必填"的职责完全交给 `@NotNull` / `@NotEmpty`。这样 `@Date` 可以叠加在可选字段上而不误伤。
2. **pattern 强自检**。`@Date` 的 pattern 不允许出现时间符号，`@DateTime` 的 pattern 必须出现时间符号——配置写错不会产生莫名其妙的解析失败，而是会得到明确的 pattern 错误：注解方式在每次校验时返回固定的错误消息，链式方式直接抛 `IllegalArgumentException`。
3. **严格优于宽松**。`@Date` / `@DateTime` 显式使用 `ResolverStyle.STRICT`，拒绝 `2024-2-5` 这类缺零填充、拒绝 `2024-02-30` 这类无效日期。但下面会看到，`@PastDate` / `@FutureDate` 系列走的却是默认的 SMART 模式，行为并不一致——这是本文想重点提醒的一个坑。

---

## 二、格式验证四件套：@Date / @DateTime / @HourMinute / @HourMinuteSecond

### 2.1 @Date：纯日期 + 严格模式

`@Date` 只验证"值是不是一个合法日期"，不关心过去还是未来：

```java
// 基础用法
@Date
private String eventDate;              // 期望 2024-01-15

// 自定义格式
@Date(pattern = "yyyy/MM/dd")
private String birthDate;

// 中文格式也支持
@Date(pattern = "yyyy年MM月dd日")
private String chineseDate;
```

它的核心在 `DateValidator` 的 `createStrictFormatter`：

```java
private static DateTimeFormatter createStrictFormatter(String pattern) {
    // 将 yyyy 替换为 uuuu 以支持 STRICT 模式
    String strictPattern = pattern.replace("yyyy", "uuuu")
                                 .replace("yy", "uu");

    return DateTimeFormatter.ofPattern(strictPattern, Locale.US)
            .withResolverStyle(ResolverStyle.STRICT);
}
```

这里藏着一个非常容易被忽略的细节：**为什么要把 `yyyy` 替换成 `uuuu`？**

`yyyy` 是"纪元年份"（year-of-era），`uuuu` 是"公历年"（proleptic year）。在 `ResolverStyle.STRICT` 模式下，解析 `yyyy` 字段**必须同时提供纪元（era）字段**——也就是 AD/BC。而用户输入的是纯数字字符串，没有纪元信息，所以 `LocalDate.parse("2024-02-05", ofPattern("yyyy-MM-dd").withResolverStyle(STRICT))` 会直接抛异常，连合法日期都过不了。

我们做了一组实测验证这个替换的必要性（`yyyy-MM-dd` 模式、`ResolverStyle.STRICT`）：

| 输入 | STRICT + `yyyy`（替换前） | STRICT + `uuuu`（替换后，即 ValidX 实际行为） |
|------|:---:|:---:|
| `2024-02-05`（合法） | ❌ 拒绝（缺 era 字段） | ✅ 通过 |
| `2024-02-29`（闰年） | ❌ 拒绝（缺 era 字段） | ✅ 通过 |
| `2024-02-30`（无效） | ❌ 拒绝 | ❌ 拒绝（2月没有30号） |
| `2023-02-29`（非闰年） | ❌ 拒绝 | ❌ 拒绝 |

第一列证明了 `yyyy→uuuu` 替换的必要性：不替换，STRICT 模式下**连合法日期都解析不了**（`yyyy` 是 year-of-era，STRICT 要求纪元字段）。第二列说明替换后的行为符合直觉：合法日期（含闰年）通过，无效日期依然被严格拒绝——严格性没有因替换而打折。

顺带一提：替换顺序是先 `yyyy→uuuu` 再 `yy→uu`。如果反过来，`yyyy` 里的 `yy` 会先被替换成 `uu`，得到 `uuuu` 就无从替换了。

### 2.2 @DateTime：pattern 必须包含时间

`@DateTime` 与 `@Date` 是一对镜像：验证值必须同时包含日期与时间，pattern 必须包含时间符号，否则初始化失败：

```java
@DateTime                                        // 2024-01-15 13:30:00
private String meetingTime;

@DateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")     // ISO 8601
private String isoTime;

@DateTime(pattern = "yyyy-MM-dd hh:mm:ss a")     // 12 小时制
private String appointmentTime;
```

它同样走 STRICT + `uuuu` 替换，只是把 `LocalDate.parse` 换成 `LocalDateTime.parse`。

### 2.3 @HourMinute / @HourMinuteSecond：正则派

这两个注解不做解析，只做正则匹配，因此**没有 pattern 参数**，格式固定：

```java
// HourMinuteValidator
private static final String HOUR_MINUTE_PATTERN = "^([01]\\d|2[0-3]):[0-5]\\d$";

// HourMinuteSecondValidator
private static final String HOUR_MINUTE_SECOND_PATTERN = "^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$";
```

注意这个正则的严谨之处：小时用 `([01]\d|2[0-3])`，把 `00-23` 拆成 `00-19` 和 `20-23` 两段，确保 `24:00`、`25:xx` 进不来；分钟/秒用 `[0-5]\d`，确保 `60` 进不来。这是一个**严格 24 小时制**的写法，`12:00 AM` 这类 12 小时制表示会被拒绝。

### 2.4 pattern 自检机制：containsTimePatternStatic

`@Date` 拒绝时间 pattern、`@DateTime` 强制时间 pattern，判断逻辑共用 `BaseDateValidator.containsTimePatternStatic`：

```java
public static boolean containsTimePatternStatic(String pattern) {
    boolean inQuote = false;
    for (int i = 0; i < pattern.length(); i++) {
        char c = pattern.charAt(i);
        if (c == '\'') {
            // 处理单引号转义：'' 是字面量单引号
            if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
                i++;
            } else {
                inQuote = !inQuote;
            }
            continue;
        }
        if (!inQuote) {
            if (c == 'H' || c == 'h' || c == 'K' || c == 'k' ||
                c == 'm' || c == 's' || c == 'S' || c == 'a' ||
                c == 'A' || c == 'n' || c == 'N') {
                return true;
            }
        }
    }
    return false;
}
```

这段代码处理了一个非常刁钻的场景：**单引号内的字面量不算时间符号**。例如 pattern `yyyy-MM-dd 'at noon'`——`noon` 里的字母 `n` 与时间符号 `n`（纳秒）撞车，但因为它被单引号包裹，是字面量文本，不算数。`''`（两个连续单引号）表示一个字面量单引号的转义形式，同样被正确处理。

测试用例 `PastDateStrictValidationTest.testLiteralOnlyNoTime` 专门验证了这一点：`pattern = "yyyy-MM-dd 'The date'"`，输入 `"2020-01-15 The date"` 应该被当作纯日期格式通过。

---

## 三、时间点验证四件套：@PastDate / @FutureDate / @PastDateTime / @FutureDateTime

### 3.1 includeToday 的边界语义

四个"过去/未来"注解都只有一个布尔参数 `includeToday`，默认 `false`。它的语义在不同注解上是**不对称**的，从源码看得很清楚：

```java
// FutureDateValidator：未来
if (includeToday) {
    return !date.isBefore(today);   // 今天 或 今天之后 ✅
} else {
    return date.isAfter(today);     // 严格今天之后 ✅
}

// PastDateValidator：过去
if (includeToday) {
    return !date.isAfter(today);    // 今天 或 今天之前 ✅
} else {
    return date.isBefore(today);    // 严格今天之前 ✅
}
```

| includeToday | @FutureDate 语义 | @PastDate 语义 |
|:---:|------|------|
| `false`（默认） | 严格晚于今天 | 严格早于今天 |
| `true` | 今天或之后 | 今天或之前 |

注意 `@PastDateTime` / `@FutureDateTime` 的语义完全相同，但比较的是**日期**而不是精确到时分秒——`FutureDateTimeValidator.parseDate` 先把输入解析成 `LocalDateTime`，然后 `.toLocalDate()` 只取日期部分再比较。也就是说：

> 今天 `23:59:59` 在 `@FutureDateTime(includeToday = false)` 下会被**拒绝**（它属于今天，不属于"严格晚于今天"）。如果你需要"未来 5 分钟"这种精确到时刻的判断，`includeToday` 帮不了你——这正是 Hibernate Validator 的 `@Future` 用 `Instant` 比较的差异点，也是 ValidX 与 Hibernate 对比专题（第 47 篇）的核心素材。

### 3.2 模板方法模式：BaseDateValidator

四个验证器共享一个抽象基类，把"判空 → 解析 → 比较"的骨架固定下来，把变化的部分留给子类：

```java
public abstract class BaseDateValidator<A extends Annotation>
        implements ConstraintValidator<A, String> {

    protected DateTimeFormatter formatter;
    protected boolean includeToday;
    protected String pattern;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;                      // 空值放行
        }
        try {
            LocalDate date = parseDate(value); // 子类决定：解析成 LocalDate 还是 LocalDateTime
            LocalDate today = LocalDate.now();
            return isValidDate(date, today);   // 子类决定：过去还是未来
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    protected abstract LocalDate parseDate(String value) throws DateTimeParseException;
    protected abstract boolean isValidDate(LocalDate date, LocalDate today);
}
```

四个子类只实现两个抽象方法，职责非常干净：

| 子类 | parseDate（解析成什么） | isValidDate（比较规则） |
|------|------|------|
| `PastDateValidator` | `LocalDate` | 过去（含/不含今天） |
| `FutureDateValidator` | `LocalDate` | 未来（含/不含今天） |
| `PastDateTimeValidator` | `LocalDateTime` → `.toLocalDate()` | 过去（含/不含今天） |
| `FutureDateTimeValidator` | `LocalDateTime` → `.toLocalDate()` | 未来（含/不含今天） |

### 3.3 实测发现：SMART 模式的"静默纠正"陷阱

这是本文最值得记住的一个发现。`@Date` / `@DateTime` 显式设置了 `ResolverStyle.STRICT`，但 `@PastDate` / `@FutureDate` 系列的 `initialize` 是：

```java
this.formatter = DateTimeFormatter.ofPattern(strictPattern);
```

**没有调用 `.withResolverStyle(ResolverStyle.STRICT)`**，用的是 `DateTimeFormatter` 的默认 `ResolverStyle.SMART`。

两种模式对无效日期的处理截然不同。我们实际跑了一组验证：

| 输入 | SMART（@PastDate / @FutureDate 默认） | STRICT（@Date / @DateTime） |
|------|:---:|:---:|
| `2024-02-30`（2月只有29天） | ⚠️ **通过**，被解析为 `2024-02-29` | ❌ 拒绝 |
| `2023-02-29`（非闰年） | ⚠️ **通过**，被解析为 `2023-02-28` | ❌ 拒绝 |
| `2024-02-05`（合法） | ✅ 通过 | ✅ 通过 |
| `2024-13-01`（月份越界） | ❌ 拒绝 | ❌ 拒绝 |

结论很明确：**`@PastDate` / `@FutureDate` 会把 `2024-02-30` 静默当作 `2024-02-29` 处理**，而不是拒绝它。

这在业务上意味着什么？举个例子：某系统用 `@FutureDate` 校验"活动截止日"，用户提交 `2026-02-30`（手滑或前端 JS 日期组件 bug），SMART 模式会把它静默解析成 `2026-02-28`（2026 不是闰年，2 月只有 28 天）放行——如果后端再按这个值存储，就存进去一个**用户根本没填过的日期**。

这是实现层面 Date/DateTime 与 Past/Future 两个家族的差异，目前源码和文档都没有说明。规避方式有三种：

1. **叠加验证**：`@FutureDate` 字段同时加 `@Date`，先让 `@Date`（STRICT）把无效日期拦掉，再由 `@FutureDate` 判断过去未来；
2. 有 `@PastDateTime` / `@FutureDateTime` 需求的场景，可以组合 `@DateTime` + 自定义校验；
3. 已经识别该问题，后续版本建议统一 `withResolverStyle(ResolverStyle.STRICT)`——这属于库自身的改进空间。

### 3.4 v1.1.0 迁移：为什么 @PastDate 不再支持时间

v1.1.0 之前，`@PastDate` / `@FutureDate` 的 pattern 允许包含时间符号；v1.1.0 之后被强制禁止，新增 `@PastDateTime` / `@FutureDateTime` 承担带时间的场景。`PastDateValidator.initialize` 里有这样一段：

```java
if (containsTimePattern(pattern)) {
    this.patternInvalid = true;
    this.patternErrorMessage = MessageManager.getMessage(
        "io.github.vipxieliang.validx.validator.date.pattern.contains.time");
    return;
}
```

迁移路径：`@PastDate(pattern = "yyyy-MM-dd HH:mm:ss")` → `@PastDateTime(pattern = "yyyy-MM-dd HH:mm:ss")`，参数 `includeToday` 语义不变。

---

## 四、特殊时间格式：@Timestamp / @Duration

### 4.1 @Timestamp：秒与毫秒的"位数"与"数值"双重校验

`@Timestamp` 是 10 个注解里唯一同时支持 `String` 和 `Long` 的，校验逻辑因类型而异：

```java
// 注解用法
@Timestamp                                   // 秒或毫秒均可
private String createTime;

@Timestamp(unit = TimestampUnit.SECONDS)     // 仅秒级（10位）
private String createTimeSec;

@Timestamp(unit = TimestampUnit.MILLISECONDS) // 仅毫秒级（13位）
private Long createTimeMs;
```

**String 类型走"位数"校验**：

| unit | 允许位数 |
|------|:---:|
| `SECONDS` | 10 |
| `MILLISECONDS` | 13 |
| `ANY` | 10 或 13 |

**Long 类型走"数值范围"校验**：

```java
private static final long MAX_SECONDS     = 9_999_999_999L;        // ≈ 2286-11-20
private static final long MAX_MILLISECONDS = 9_999_999_999_999L;

switch (unit) {
    case SECONDS:      return value <= MAX_SECONDS;                                   // ①
    case MILLISECONDS: return value > MAX_SECONDS && value <= MAX_MILLISECONDS;        // ②
    case ANY:          return value <= MAX_MILLISECONDS;                               // ③
}
```

这里有两个值得注意的边界行为：

1. **秒级上限是 9,999,999,999，约等于公元 2286 年**。这不是随手写的数字，而是 10 位十进制秒时间戳的最大值——`9999999999` 秒 ≈ 2286-11-20，所以理论上它能覆盖"现在 + 260 年"，日常够用。
2. **类型不同，行为不同**。`Long` 类型下 `ANY` 模式只检查 `value <= MAX_MILLISECONDS`，**不检查位数**——`long 100L`（3 位数字）在 `ANY` 模式下会**通过**；而同样的字符串 `"100"`（3 位数字）在 `ANY` 模式下因位数不对会被**拒绝**。同一个值，String 与 Long 结果相反。所以如果你的时间戳字段是 `Long` 类型，`@Timestamp(unit = SECONDS)` 只能拦住超大值，拦不住"3 位小数字"。

另外，秒级时间戳 `value <= 9_999_999_999` 意味着 10 位秒时间戳全都合法；而毫秒模式要求 `value > 9_999_999_999`，13 位毫秒最小值 `1,000,000,000,000` 恰好满足——这两个范围边界是自洽的。

### 4.2 @Duration：ISO 8601 与简化格式的"双正则"

`@Duration` 验证时间段字符串，支持两种格式：

```java
// 任意格式
@Duration
private String duration;   // 如 "PT2H30M" 或 "2h30m"

// 只接受 ISO 8601
@Duration(format = DurationFormat.ISO_8601)
private String isoDuration;  // 如 "P1DT2H"

// 只接受简化格式
@Duration(format = DurationFormat.SIMPLE)
private String simpleDuration;  // 如 "1y2mo3d"
```

两个正则都在 `DurationValidator` 里：

```java
// ISO 8601：P[nY][nM][nD][T[nH][nM][n.S]]
private static final Pattern ISO_8601_PATTERN = Pattern.compile(
    "^P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?)?$",
    Pattern.CASE_INSENSITIVE
);

// 简化格式：[数字]y[数字]mo[数字]d[数字]h[数字]m[数字]s，单位顺序固定
private static final Pattern SIMPLE_PATTERN = Pattern.compile(
    "^(?:(\\d+)y)?(?:(\\d+)mo)?(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$",
    Pattern.CASE_INSENSITIVE
);
```

结合 `isValidIso8601Duration` 的附加逻辑，有几个边界行为需要留意：

**① 无 `T` 的 ISO 格式只接受纯天数 `P\d+D`。**

```java
// 如果只有P，没有T，必须有天数
if (!duration.toUpperCase().contains("T") && !duration.toUpperCase().matches("^P\\d+D$")) {
    return false;
}
```

于是 `P1D` 通过，但 `P1Y`、`P1Y2M3D`（都是合法的 ISO 8601 时长，`1年2月3天`）会被**拒绝**。有趣的是 `@Duration` 的 Javadoc 示例恰好写了 `"P1Y2M3D"` 是合法输入——**文档与实现不一致**。实际写法应改为 `P1Y2M3DT0H`（带 `T`）或 `1y2mo3d`（简化格式）。

**② ISO 正则的单位顺序是固定的**：`Y → M → D → T → H → M → S`，必须从大到小排列。`PT30M2H`（30分2小时，非降序）会被拒绝——这符合 ISO 8601 的规范（时间单位必须按降序），不算 bug。

**③ 简化格式也有顺序要求**：`y → mo → d → h → m → s`，且要求至少一个非零值（`0h` 不通过）。`mo` 与 `m` 的区分也做了特殊处理：判断 `m` 时排除掉紧跟 `o` 的 `mo`。

**④ 空值放行规则同其他注解**：`null` 和空字符串返回 `true`。

---

## 五、链式 API：22 个方法的完全对照

### 5.1 方法签名总表

每一个时间注解都有对应的链式 API，`ValidX.java` 中一共 22 个方法：

| 注解 | 链式方法（ValidX） | 默认 pattern |
|------|------|------|
| `@Date` | `isDate(value)` / `isDate(value, pattern)` | `yyyy-MM-dd` |
| `@DateTime` | `isDateTime(value)` / `isDateTime(value, pattern)` | `yyyy-MM-dd HH:mm:ss` |
| `@PastDate` | `isPastDate(value[, includeToday[, pattern]])` | `yyyy-MM-dd` |
| `@FutureDate` | `isFutureDate(value[, includeToday[, pattern]])` | `yyyy-MM-dd` |
| `@PastDateTime` | `isPastDateTime(value[, includeToday[, pattern]])` | `yyyy-MM-dd HH:mm:ss` |
| `@FutureDateTime` | `isFutureDateTime(value[, includeToday[, pattern]])` | `yyyy-MM-dd HH:mm:ss` |
| `@HourMinute` | `isHourMinute(value)` | 固定 `HH:mm` |
| `@HourMinuteSecond` | `isHourMinuteSecond(value)` | 固定 `HH:mm:ss` |
| `@Timestamp` | `isTimestamp(value)` / `isTimestamp(value, unit)` | 任意单位 |
| `@Duration` | `isDuration(value)` / `isDuration(value, format)` | 任意格式 |

注意 `@Date` / `@DateTime` 支持 `PARAMETER` 目标（可以注解方法参数），其余 8 个只支持 `METHOD` / `FIELD`——这是写 API 层参数校验时的实际差异。

### 5.2 链式验证示例

```java
ValidX vx = ValidX.init();                          // 注意：构造器是私有的，入口是 init()
vx.isPastDate(birthDate)                            // 默认 yyyy-MM-dd
  .isFutureDate(eventDate, true)                    // 包含今天
  .isDateTime(meetingTime, "yyyy-MM-dd HH:mm:ss")
  .isHourMinute(businessHours)
  .isTimestamp(createTime, TimestampUnit.SECONDS)
  .isDuration(meetingDuration, DurationFormat.ISO_8601);

if (!vx.passed()) {                                 // passed() 返回 boolean
    throw new IllegalArgumentException(String.join("; ", vx.getErrors()));
}
```

注意 `ValidX` 的构造器是私有的，链式入口是静态工厂 `ValidX.init()`；终结判断用 `passed()`（布尔）或 `getErrors()`（错误列表），不存在 `check()` 方法——这几个 API 名称与 Hibernate Validator 的 `validate()` 习惯不同，初次上手容易写错。

### 5.3 注解 vs 链式：错误消息的两套机制

这两条路径的错误消息机制完全不同，排查线上问题时容易被误导：

- **注解方式**走 Bean Validation 的消息插值：`@Date` 失败显示 `日期格式不正确`，`@PastDate` 失败显示 `日期必须是过去的日期`，消息模板定义在 `ValidationMessages*.properties`（9 种语言）。
- **链式方式**走 `BaseValidation` + `MessageManager`：把校验器实例化后直接 `isValid(value, null)`，失败时把消息 key 翻译成文案塞进 `errors` 列表。

两条路径共用的是**同一批 Validator**——链式方法里 `baseValidation.validateXxx(...)` 内部就是 `new PastDateValidator()` 再调 `initialize` / `isValid`。所以校验逻辑一致，只是错误消息的组装方式不同。

---

## 六、实战：会议预约系统

把 10 个注解放进一个真实场景：会议预约的 `MeetingRequest`。

```java
public class MeetingRequest {

    @NotNull
    @Date(pattern = "yyyy-MM-dd")                 // ① 格式：必须是合法日期（STRICT）
    private String meetingDate;

    @NotNull
    @HourMinute                                   // ② 格式：HH:mm
    private String startTime;

    @NotNull
    @HourMinute                                   // ③ 格式：HH:mm
    private String endTime;

    @FutureDate(includeToday = false)             // ④ 时间点：必须严格晚于今天
    private String publishDate;

    @DateTime(pattern = "yyyy-MM-dd HH:mm:ss")    // ⑤ 格式：日期时间
    private String createdAt;

    @PastDateTime(includeToday = true)            // ⑥ 时间点：注册时间
    private String registeredAt;

    @Timestamp(unit = TimestampUnit.SECONDS)      // ⑦ 特殊：秒级时间戳
    private Long syncTime;

    @Duration(format = DurationFormat.ISO_8601)   // ⑧ 特殊：提醒提前量
    private String remindBefore;
}
```

针对 3.3 节发现的问题，一个更稳妥的写法是让格式校验和时间点校验**分开**：

```java
// 先用 STRICT 的 @Date 卡格式，再用 @FutureDate 判早晚
@Date(pattern = "yyyy-MM-dd")
@FutureDate(includeToday = true)
private String publishDate;
```

这样 `2026-02-30` 会在第一层被拒绝，不会进入 SMART 的"静默纠正"。

---

## 七、踩坑指南与 FAQ

### 7.1 十个注解，五个高频坑

| # | 坑 | 说明 | 对策 |
|---|-----|------|------|
| 1 | 空值放行 | 所有注解对 `null`/空串返回 `true` | 必填字段必须叠加 `@NotNull` / `@NotEmpty` |
| 2 | SMART 静默纠正 | `@PastDate` / `@FutureDate` 会把 `2024-02-30` 解析成 `2024-02-29` | 叠加 `@Date`（STRICT）先卡格式 |
| 3 | `datetime.format` 消息 key 缺失 | 见 7.3，`@DateTime` 默认消息无法解析 | 显式指定 `message`，或关注后续修复 |
| 4 | Timestamp 类型双标 | `Long 100L` 在 `ANY` 下通过，`"100"` 被拒 | 明确 `unit`，并自行加范围校验 |
| 5 | pattern 写错不报编译错 | `@Date(pattern = "yyyy-MM-dd HH:mm:ss")` 运行期才暴露 | 链式方式会抛异常，注解方式返回固定错误——建议写单测 |

### 7.2 FAQ

**Q1：`@Date` 与 `@DateTime` 到底怎么选？**
看 pattern 是否包含时间符号。一句话：`@Date` 的 pattern 不能含时间符号，`@DateTime` 必须含——这是硬性校验，写错会在初始化时被拦截。

**Q2：`includeToday` 为什么默认是 `false`？**
语义上"未来的会议日期"通常要求严格晚于今天。如果你的业务允许当天（比如"活动从今天开始"），显式传 `includeToday = true` 即可。但注意它只精确到"天"，精确到时刻的场景它管不了。

**Q3：`@Timestamp(unit = SECONDS)` 能拦住多大的值？**
秒级上限 `9_999_999_999`（约 2286-11-20）。更实际的问题是**位数**：String 类型 10 位秒 / 13 位毫秒，位数不对直接拒绝，这个比范围校验更早生效。

**Q4：`@Duration` 能验证"时间段"的业务含义吗？**
不能。它只验证格式合法性（`2h30m`、`PT2H30M` 这种），不解析成毫秒数。要比较时长（比如"不少于 1 小时"），需要自行解析或配合其他逻辑。第 29 篇《时间段验证：ISO 8601 与简化格式的支持》会深入讲。

**Q5：和 Hibernate Validator 的 `@Past` / `@Future` 有什么区别？**
Hibernate 的 `@Past` / `@Future` 比较的是"当前时刻"，要求目标类型是 `Date` / `LocalDate` 等时间对象；ValidX 的这套注解**主要处理字符串**（`@Timestamp` 额外支持 `Long`），且自带格式解析能力。所以 ValidX 更适合"前端传字符串、后端做入参清洗"的典型 Web 场景。详细对比见第 47 篇《ValidX vs Hibernate Validator：时间注解功能对比》。

**Q6：时区问题怎么处理？**
注意 `BaseDateValidator` 用的是 `LocalDate.now()`——服务器本地时区。如果前端是东八区、后端部署在 UTC，边界日期（比如"今天"）会有一天的偏差。多时区系统需要先统一入参时区或自行处理（第 120 篇《ValidX时区处理》预告）。

### 7.3 一个值得知道的现状：@DateTime 的默认消息 key 缺失

核对 9 个语言文件（`ValidationMessages*.properties`）时发现一个现状：`@Date` 的默认消息 key `annotation.date.format` 有定义，但 `@DateTime` 的默认消息 key **`annotation.datetime.format` 在所有语言文件中均未定义**。

后果是分路径的：

- **注解方式**：`@DateTime` 校验失败时，默认消息插值器无法解析 `{io.github.vipxieliang.validx.annotation.datetime.format}`，最终会**原样输出这个大括号模板**（Hibernate Validator 对未解析模板的默认行为）；
- **链式方式**：`isDateTime(...)` 失败时，`MessageManager.getMessage` 按"本地包 → 英文包 → 返回 key 本身"逐级回退，`errors` 里会直接出现 `io.github.vipxieliang.validx.annotation.datetime.format` 这一整串 key。

如果你在生产环境见到 `@DateTime` 相关的错误消息是一长串 key 而不是中文文案，原因就在这里。临时对策是给 `@DateTime` 显式写 `message = "日期时间格式不正确"`，或者链式调用后自行映射错误。

---

## 八、总结

ValidX 的 10 个时间注解，本质上是把 `java.time` 的解析能力和三套自定义规则做了一层声明式封装：

1. **格式派**（`@Date` / `@DateTime` / `@HourMinute` / `@HourMinuteSecond`）管"值长什么样"，用 STRICT 解析或正则严格匹配，辅以 pattern 自检；
2. **时间点派**（`@PastDate` / `@FutureDate` / `@PastDateTime` / `@FutureDateTime`）管"值在哪一端"，用模板方法模式统一骨架，`includeToday` 控制边界；
3. **特殊派**（`@Timestamp` / `@Duration`）管"机器读的时间"，用位数/数值双重校验和双正则处理。

三个容易被忽略的结论，值得记住：

- `@Date` / `@DateTime` 的 STRICT 模式必须依赖 `yyyy→uuuu` 替换，否则连合法日期都会解析失败；
- `@PastDate` / `@FutureDate` 走 SMART 模式，`2024-02-30` 会被静默纠正为 `2024-02-29`——需要严格性的场景请叠加 `@Date`；
- `@DateTime` 的默认错误消息 key 在语言资源文件中缺失，遇到"错误消息是一串 key"时不要慌，显式指定 `message` 即可。

时间验证没有银弹，但把这 10 个注解的边界行为摸清，你就能在"用户瞎填"与"系统崩坏"之间，立起一道足够严密的防线。
