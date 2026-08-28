# ValidX @Age注解：从身份证号自动计算年龄的技术实现

## 📋 目录
- [引言](#引言)
- [一、@Age 注解：四个参数搞定年龄验证](#一age-注解四个参数搞定年龄验证)
- [二、三种输入类型：LocalDate / Date / String](#二三种输入类型localdate--date--string)
- [三、核心实现：从身份证号提取出生日期](#三核心实现从身份证号提取出生日期)
  - [1. 18 位身份证的结构与正则](#1-18-位身份证的结构与正则)
  - [2. 15 位身份证的兼容](#2-15-位身份证的兼容)
  - [3. 提取逻辑](#3-提取逻辑)
- [四、日期字符串解析：先自定义格式，再兜底常见格式](#四日期字符串解析先自定义格式再兜底常见格式)
- [五、年龄计算：Period.between 的周岁语义](#五年龄计算periodbetween-的周岁语义)
- [六、范围校验：min / max 的语义](#六范围校验min--max-的语义)
- [七、链式 API：四种重载](#七链式-api四种重载)
- [八、源码流程全景图](#八源码流程全景图)
- [九、实战：注册 / 实名 / 票务三场景](#九实战注册--实名--票务三场景)
- [十、边界与坑](#十边界与坑)
- [总结](#总结)

---

## 引言

"请输入您的出生日期"——每个注册页面都有这一行。

但更常见的是这种场景：用户直接上传**身份证号**，系统既要做实名认证，又要判断"是否年满 18 岁"。这时候你面临两个问题：

1. **怎么从身份证号里算出年龄？** 18 位身份证的第 7-14 位是出生日期（`YYYYMMDD`），但要小心 15 位老身份证（第 7-12 位是 `YYMMDD`）
2. **怎么算才算"周岁"？** 2000-08-28 出生，2026-08-27 到底是 25 岁还是 26 岁？——中国法律意义上的年龄是**周岁**：过完生日才 +1

手写的话，要处理身份证格式校验、日期提取、`Period` 计算、边界判断，至少 50 行。而 ValidX 的 `@Age` 注解一行搞定：

```java
@Age(min = 18, max = 65, fromIdCard = true)   // 从身份证号自动提取出生日期并算年龄
private String idCard;
```

本文对照 ValidX 源码（`AgeValidator.java`），完整拆解 `@Age` 的技术实现。

---

## 一、@Age 注解：四个参数搞定年龄验证

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
public @interface Age {

    int min() default 0;                    // 最小年龄（包含），0 表示不限制

    int max() default 0;                    // 最大年龄（包含），0 表示不限制

    boolean fromIdCard() default false;     // 是否从身份证号提取出生日期

    String dateFormat() default "yyyy-MM-dd"; // 日期字符串的解析格式
}
```

| 参数 | 默认值 | 语义 | 示例 |
|------|--------|------|------|
| `min` | `0` | 最小年龄（**包含**），0 表示不限制 | `min = 18` 要求 ≥18 周岁 |
| `max` | `0` | 最大年龄（**包含**），0 表示不限制 | `max = 60` 要求 ≤60 周岁 |
| `fromIdCard` | `false` | 输入是身份证号时置 `true`，自动提取出生日期 | `fromIdCard = true` |
| `dateFormat` | `"yyyy-MM-dd"` | 日期字符串的解析格式（仅 String 输入、非身份证时生效） | `dateFormat = "yyyy/MM/dd"` |

**典型用法**：

```java
// ① 直接用 LocalDate 生日
@Age(min = 18, max = 65)
private LocalDate birthDate;

// ② 用字符串生日
@Age(min = 18, max = 65)
private String birthDateStr;    // "1990-01-01"

// ③ 自定义日期格式
@Age(min = 18, dateFormat = "yyyy/MM/dd")
private String birthDate;       // "1990/06/15"

// ④ 从身份证号提取年龄
@Age(min = 18, max = 65, fromIdCard = true)
private String idCard;          // "11010119900101001X"
```

---

## 二、三种输入类型：LocalDate / Date / String

`AgeValidator` 的 `isValid` 方法对不同类型的输入做了分派（`AgeValidator.java:100-129`）：

```java
LocalDate birthDate = null;

if (value instanceof LocalDate) {
    birthDate = (LocalDate) value;
} else if (value instanceof Date) {
    birthDate = ((Date) value).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
} else if (value instanceof String) {
    String strValue = (String) value;
    if (strValue.trim().isEmpty()) {
        return true;                          // 空字符串放行
    }
    if (fromIdCard) {
        birthDate = extractBirthDateFromIdCard(strValue);   // 身份证路径
    } else {
        birthDate = parseDateString(strValue);              // 日期字符串路径
    }
    if (birthDate == null) {
        return false;                         // 提取/解析失败
    }
} else {
    return false;                             // 不支持的类型
}
```

| 输入类型 | 处理方式 | 说明 |
|---------|---------|------|
| `LocalDate` | 直接使用 | 最推荐，类型安全 |
| `java.util.Date` | `toInstant().atZone(...).toLocalDate()` 转换 | 兼容老代码 |
| `String` | `fromIdCard` 决定走"身份证提取"还是"日期解析" | 最灵活，也最容易踩坑 |
| 其他类型 | 返回 `false` | 不支持 |

> **关键行为**：`null` 放行（`AgeValidator.java:96-98`）、空字符串放行——遵循 JSR-380 惯例，非空校验交给 `@NotNull` / `@NotBlank`。这一点与 `AgeValidatorTest.testNullAge`、`testEmptyStringAge` 的断言完全一致。

---

## 三、核心实现：从身份证号提取出生日期

这是本篇的**技术重头戏**。`extractBirthDateFromIdCard` 方法（`AgeValidator.java:144-184`）先用**格式正则**校验身份证，再按位截取出生日期。

### 1. 18 位身份证的结构与正则

18 位身份证的结构：

```
110101 19900101 001 X
|------|--------|---|--|
 地区码  出生日期 顺序码 校验码
 6 位   8 位    3 位  1 位
```

对应正则（`AgeValidator.java:43-44`）：

```java
private static final Pattern ID_CARD_18_PATTERN =
    Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
```

| 正则片段 | 含义 | 说明 |
|---------|------|------|
| `^[1-9]\d{5}` | 6 位地区码 | 首位不能是 0 |
| `(19\|20)\d{2}` | 4 位年份 | 只支持 19xx / 20xx（符合现行身份证发放规律） |
| `((0[1-9])\|(1[0-2]))` | 2 位月份 | 严格限制 01-12 |
| `(([0-2][1-9])\|10\|20\|30\|31)` | 2 位日期 | 限制 01-29、10、20、30、31（非闰年 2 月 29 日等由 `LocalDate.of` 再拦截） |
| `\d{3}` | 3 位顺序码 | 同地同生日的顺序号，奇数为男、偶数为女 |
| `[0-9Xx]$` | 1 位校验码 | 可能为数字或 X |

### 2. 15 位身份证的兼容

15 位老身份证（2000 年前签发）结构：6 位地区码 + **6 位**出生日期（`YYMMDD`）+ 3 位顺序码，**无校验码**：

```java
private static final Pattern ID_CARD_15_PATTERN =
    Pattern.compile("^[1-9]\\d{5}\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}$");
```

提取时年份补 `"19"` 前缀（`AgeValidator.java:168-169`）：

```java
String yearStr = "19" + trimmed.substring(6, 8);   // 15位 → 19YY
```

> 例如 `110101800101001` → 出生日期 `1980-01-01`（测试 `testValidAge_IdCard15_Adult` 验证通过）。

### 3. 提取逻辑

18 位身份证按位置截取（`AgeValidator.java:152-164`）：

```java
String yearStr = trimmed.substring(6, 10);    // 第 7-10 位：年
String monthStr = trimmed.substring(10, 12);  // 第 11-12 位：月
String dayStr = trimmed.substring(12, 14);    // 第 13-14 位：日
int year = Integer.parseInt(yearStr);
int month = Integer.parseInt(monthStr);
int day = Integer.parseInt(dayStr);
return LocalDate.of(year, month, day);        // LocalDate.of 会拦截非法日期
```

**双层校验**值得注意：
- 第一层：正则保证格式（月份 01-12、日期在允许集合内）
- 第二层：`LocalDate.of(year, month, day)` 抛 `DateTimeException` 兜底（比如 2 月 30 日这种正则漏掉的日期）

---

## 四、日期字符串解析：先自定义格式，再兜底常见格式

当输入是日期字符串（`fromIdCard = false`）时，走 `parseDateString`（`AgeValidator.java:192-220`）：

```java
// 第一步：按注解指定的 dateFormat 解析
try {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
    return LocalDate.parse(trimmed, formatter);
} catch (DateTimeParseException e) {
    // 第二步：依次尝试三种常见格式兜底
    String[] commonFormats = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd"};
    for (String format : commonFormats) {
        if (format.equals(dateFormat)) {
            continue;   // 已经试过了，跳过
        }
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException ex) {
            // 继续尝试
        }
    }
}
return null;   // 全部失败
```

| 输入 | dateFormat | 结果 |
|------|-----------|------|
| `"1990-01-01"` | 默认 | ✅ 按指定格式直接解析 |
| `"1990/06/15"` | `"yyyy/MM/dd"` | ✅ 按指定格式直接解析 |
| `"1990-13-32"` | 默认 | ❌ 月份 13、日期 32 非法，全部解析失败 |
| `"01/01/1990"` | 默认 | ❌ 不符合任何兜底格式（`LocalDate` 解析也是失败） |

> 注意：兜底格式只覆盖 `yyyy-MM-dd`、`yyyy/MM/dd`、`yyyyMMdd` 三种。`"01/01/1990"`（日/月/年）这种欧洲格式**不会**被误判为 `1990-01-01`——测试 `testInvalidAge_String_WrongFormat` 明确断言它验证失败。这是有意为之：宁可拒绝，也不猜错格式。

---

## 五、年龄计算：Period.between 的周岁语义

年龄计算是"看起来简单、写错很常见"的地方。很多人会写：

```java
// ❌ 错误示范：直接用年份相减
int age = today.getYear() - birthDate.getYear();
// 2000-12-31 出生，2026-01-01 时算出 26 岁 —— 实际才过完 25 岁生日
```

ValidX 用的是 `java.time.Period`（`AgeValidator.java:228-241`）：

```java
private int calculateAge(LocalDate birthDate) {
    LocalDate today = LocalDate.now();
    if (birthDate.isAfter(today)) {
        return 0;                          // 未来出生日期 → 视为 0 岁
    }
    Period period = Period.between(birthDate, today);
    return period.getYears();              // 周岁
}
```

**`Period.between` 的周岁语义**：

```java
// 今天 2026-08-28
Period.between(LocalDate.of(2000, 8, 28), LocalDate.of(2026, 8, 28))
    .getYears();   // 26 —— 今天正好过生日
Period.between(LocalDate.of(2000, 8, 29), LocalDate.of(2026, 8, 28))
    .getYears();   // 25 —— 生日还没到，差一天也不行
Period.between(LocalDate.of(2000, 2, 29), LocalDate.of(2026, 2, 28))
    .getYears();   // 25 —— 闰年 2 月 29 日出生，平年按 2 月 28 日算
```

**为什么必须用 `Period`**：
- 它按"年月日"整体计算，正确处理生日未到、闰年 2 月 29 日等边界
- 这正是法律意义上的**周岁**：从出生日到当前日期的完整年数
- 测试 `testValidAge_LocalDate_MinAge`（刚好 18 岁通过）、`testInvalidAge_LocalDate_TooYoung`（17 岁失败）都依赖这个语义

**未来日期处理**：`birthDate.isAfter(today)` 时返回 0 岁，配合 `min > 0` 必然验证失败（测试 `testInvalidAge_LocalDate_FutureBirthDate`）。

---

## 六、范围校验：min / max 的语义

最后一步，`validateAgeRange`（`AgeValidator.java:249-261`）：

```java
private boolean validateAgeRange(int age) {
    if (minAge > 0 && age < minAge) {   // 只校验设置的 min
        return false;
    }
    if (maxAge > 0 && age > maxAge) {   // 只校验设置的 max
        return false;
    }
    return true;
}
```

| 配置 | 行为 | 测试证据 |
|------|------|---------|
| `@Age(min = 18, max = 65)` | 18 ≤ 年龄 ≤ 65 | 18 岁 ✓ / 65 岁 ✓ / 17 岁 ✗ / 66 岁 ✗ |
| `@Age(min = 18)` | 年龄 ≥ 18，**上限不限** | 100 岁也 ✓（`testValidAge_MinOnly_VeryOld`） |
| `@Age(max = 60)` | 年龄 ≤ 60，**下限不限** | 出生 1 天的婴儿也 ✓（`testValidAge_MaxOnly_VeryYoung`） |

> `min`/`max` 默认值 `0` 的语义是"不限制"，所以 `@Age` 直接标注但什么都不配，等于"只要出生日期合法就通过"——通常不是你想要的效果，**至少配一个 `min`**。

---

## 七、链式 API：四种重载

不依赖 Spring 的纯 Java 场景，链式 API 提供 4 个重载（`ValidX.java:959-1013`）：

```java
public ValidX isAge(Object value, int minAge)                                  // 只限最小
public ValidX isAge(Object value, int minAge, int maxAge)                      // 完整范围
public ValidX isAge(Object value, int minAge, int maxAge, boolean fromIdCard)  // 身份证提取
public ValidX isAge(Object value, int minAge, int maxAge, boolean fromIdCard,
                    String dateFormat)                                          // 全参数
```

```java
ValidX.create()
        .isAge("11010119900101001X", 18, 65, true)   // 身份证号，要求 18-65 岁
        .isEmail("user@example.com")                  // 混合其他验证
        .validate();                                  // 触发

// 或：收集错误而非抛异常
ValidX validator = ValidX.init();
validator.isAge(LocalDate.now().minusYears(17), 18, 65);
validator.passed();          // false
validator.getErrors();       // ["年龄验证失败" 之类，随 locale 变化]
```

链式与注解的**行为完全一致**——链式内部就是 `new AgeValidator()` 后调用同一套逻辑（`BaseValidation.validateAge` → `AgeValidator.initialize(...).isValid(...)`）。

---

## 八、源码流程全景图

```
@Age(min=18, max=65, fromIdCard=true)
        │
        ▼
AgeValidator.isValid(value)                     ── 注解路径
        │
        ├─ null / 空字符串 ──────────────► 放行 ✓
        │
        ├─ LocalDate / Date ─────────────► 转 LocalDate
        │
        └─ String
              ├─ fromIdCard=true ────────► extractBirthDateFromIdCard
              │                             ├─ 18位正则匹配 → 截取 [6,10)+[10,12)+[12,14)
              │                             ├─ 15位正则匹配 → "19"+[6,8)+[8,10)+[10,12)
              │                             └─ LocalDate.of() 兜底拦截非法日期
              │
              └─ fromIdCard=false ───────► parseDateString
                                            ├─ 按 dateFormat 解析
                                            └─ 失败 → 尝试 yyyy-MM-dd / yyyy/MM/dd / yyyyMMdd
        │
        ▼
calculateAge(birthDate)                    Period.between(birthDate, today).getYears()
        │                                     未来日期 → 0 岁
        ▼
validateAgeRange(age)                      min>0 && age<min → false
                                           max>0 && age>max → false
        │
        ▼
        通过 / 失败
```

---

## 九、实战：注册 / 实名 / 票务三场景

### 场景一：用户注册（要求年满 18 周岁）

```java
public class RegisterDTO {
    @NotBlank
    @Age(min = 18, fromIdCard = true)      // 实名注册：从身份证算年龄
    private String idCard;

    @NotBlank
    @ChinesePhone
    private String phone;
}
```

### 场景二：实名认证（先验身份证真伪，再验年龄）

```java
public class RealNameDTO {
    // 注意：@Age 只做"格式 + 提取生日"，不做身份证校验码验证！
    // 严格实名认证需要 @ChineseIdCard 配合（校验码算法）
    @ChineseIdCard                           // ① 身份证本身合法（含校验码）
    @Age(min = 18, max = 100, fromIdCard = true)  // ② 年龄在合法区间
    private String idCard;

    @ChineseName
    private String name;
}
```

> 这正是仓库测试 `testMixedValidation_AgeAndIdCard` 的用法：`isAge("110101199003072113", 18, 65, true).isChineseIdCard("110101199003072113")`。

### 场景三：景区票务（儿童票 / 老年票）

```java
// 链式 API 非常适合这种"资格判断"逻辑
public String checkTicketType(String idCard) {
    ValidX v = ValidX.init();
    v.isAge(idCard, 0, 12, true);            // 儿童票：≤12 岁
    if (v.passed()) return "儿童票";

    v = ValidX.init();
    v.isAge(idCard, 60, 0, true);            // 老年票：≥60 岁
    if (v.passed()) return "老年票";

    return "成人票";
}
```

---

## 十、边界与坑

| 边界/坑 | 说明 | 建议 |
|--------|------|------|
| **@Age 不校验身份证校验码** | 正则只验格式，`11010119900101001X` 中最后一位 X 的合法性**不验证** | 需要严格实名 → 叠加 `@ChineseIdCard` |
| **15 位身份证年份补 19** | `800101` → `1980-01-01`，2000 年后不可能再有 15 位证 | 无需处理，源码已兼容 |
| **`fromIdCard=true` 时 dateFormat 无效** | 身份证提取路径不读 `dateFormat` | 不要同时纠结两个参数 |
| **min/max 默认 0 = 不限制** | 裸 `@Age` 几乎不限制任何东西 | 至少配 `min` |
| **年龄是"当前时刻"算的** | `LocalDate.now()` 每天变化，跨年/生日当天边界敏感 | 测试用相对日期（`minusYears(18)`）而非写死 |
| **不支持 `LocalDateTime`/`Instant`** | 类型分派只认 `LocalDate`/`Date`/`String` | 先转 `LocalDate` 再传入 |
| **未来日期视为 0 岁** | 用户传了未来生日 → 必然不满足 `min` | 配合 `@PastDate` 提前拦截体验更好 |

---

## 总结

| 能力 | 实现 | 源码位置 |
|------|------|---------|
| 三类型输入 | `instanceof` 分派 + 类型转换 | `AgeValidator.java:100-129` |
| 身份证提取 | 18/15 位正则 + 按位截取 + `LocalDate.of` 兜底 | `AgeValidator.java:43-50, 144-184` |
| 日期解析 | 指定格式优先，3 种常见格式兜底 | `AgeValidator.java:192-220` |
| 周岁计算 | `Period.between(...).getYears()`，未来日期 → 0 岁 | `AgeValidator.java:228-241` |
| 范围校验 | `min`/`max`，0 = 不限制 | `AgeValidator.java:249-261` |
| 链式 API | 4 个 `isAge` 重载，与注解共用验证器 | `ValidX.java:959-1013` |

`@Age` 的设计哲学很清晰：**把"提取出生日期"和"计算周岁"这两个容易写错的细节封进框架，业务代码只留一个声明**。身份证提取、15 位兼容、`Period` 周岁语义、未来日期兜底——每一个细节都有对应测试锁住行为，这正是 ValidX 验证规则"可以放心使用"的底气。

> 下一篇文章预告：计划表第 9 周《银行卡 BIN 码识别：如何判断银行和卡种》——同样的"格式 + 算法"双引擎思路，敬请期待。

---

**文档版本**：v1.0
**创建日期**：2026-08-28
**源码版本**：ValidX v1.2.0（`AgeValidator.java` / `Age.java` / `ValidX.java`）
