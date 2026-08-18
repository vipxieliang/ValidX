# 一篇文章搞懂ValidX的所有注解

ValidX 提供 **100+ 个验证注解**，覆盖 9 大分类，全面适配中国业务场景（身份证、手机号、银行卡、统一社会信用代码等）。本文是速查手册。

## 使用前必读

1. **null 和空字符串默认通过验证**（JSR-380 规范）。必填场景务必搭配 `@NotNull` / `@NotEmpty` / `@NotBlank`，格式注解只负责"有值时的格式校验"。
2. **两种用法**：注解用于 DTO 字段校验，链式 API 用于动态数据（Map/JSON）校验。
3. **三个标准注解的区别**：

| 注解 | 规则 |
|------|------|
| `@NotNull` | 不能为 `null`（可为空串 `""`） |
| `@NotEmpty` | 不能为 `null` 且不能为空（字符串/集合/数组） |
| `@NotBlank` | 不能为 `null`、`""`、`"   "`（仅字符串） |

```java
public class UserDTO {
    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;   // 必填 + 格式

    @QQ
    private String qq;      // 可选：有值则必须格式正确
}
```

## 分类总览

| 分类 | 数量 | 说明 |
|------|------|------|
| 基础验证 | 45 | 字符、坐标、日期时间、字符串匹配、枚举、编码格式 |
| 身份验证 | 17 | 身份证、护照、港澳台证件、手机号、邮箱 |
| 金融验证 | 7 | 银行卡、CVV、IBAN、SWIFT、股票代码 |
| 教育/职业资格 | 7 | 学位证、医师证、教师证、律师证等 |
| 网络相关 | 5 | 域名、IP、MAC、URL、子网掩码 |
| 中国特定 | 11 | 车牌、专利、商标、邮编、药品、QQ、微信 |
| 汽车相关 | 2 | VIN 车架号、发动机号 |
| 图书相关 | 7 | ISBN、ISSN、DOI、分类号 |
| 手机相关 | 1 | IMEI 设备号 |

---

## 一、基础验证（45 个）

### 字符组合（10）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@Alpha` | 纯英文字母 | `abcDEF` |
| `@AlphaDash` | 字母、数字、下划线、破折号 | `abc-123_def` |
| `@AlphaNumber` | 字母 + 数字 | `abc123` |
| `@Chinese` | 纯中文字符 | `汉字` |
| `@ChineseAlpha` | 中文 + 英文字母 | `汉字abc` |
| `@ChineseAlphaNum` | 中文 + 字母 + 数字 | `汉字abc123` |
| `@ChineseAlphaDash` | 中文、字母、数字、下划线、破折号 | `汉字abc-123_def` |
| `@Lower` | 仅小写英文字母 | `abcdef` |
| `@Upper` | 仅大写英文字母 | `ABCDEF` |
| `@Xdigit` | 十六进制字符 | `0a1B2c3D` |

### 坐标地理（3）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@Longitude` | 经度（-180 到 180） | `116.4074` |
| `@Latitude` | 纬度（-90 到 90） | `39.9042` |
| `@GeoPoint` | 经纬度坐标对，支持逗号/空格分隔；可配 `latitudeFirst`（默认经度在前）、`separator`（ANY/COMMA/SPACE） | `116.4074,39.9042` |

### 日期时间（11）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@Date` | 纯日期，默认 `yyyy-MM-dd`；可配 `pattern`，**不能含时间符号**；严格校验（拒绝 `2024-02-30`，正确处理闰年） | `2024-01-15` |
| `@DateTime` | 日期时间，默认 `yyyy-MM-dd HH:mm:ss`；**必须含时间符号** | `2024-01-15 13:30:00` |
| `@PastDate` | 过去的日期（不含今天）；可配 `includeToday`、`pattern` | `2024-01-15` |
| `@FutureDate` | 未来的日期（含今天）；可配 `includeToday` | `2026-01-15` |
| `@PastDateTime` | 过去的日期时间 | `2024-01-15 13:30:00` |
| `@FutureDateTime` | 未来的日期时间 | `2026-01-15 13:30:00` |
| `@HourMinute` | 时分，`HH:mm` | `09:30` |
| `@HourMinuteSecond` | 时分秒，`HH:mm:ss` | `18:00:00` |
| `@Timestamp` | 时间戳，`unit` 支持 SECONDS（10 位）/MILLISECONDS（13 位）/ANY（默认），支持 String 和 Long | `1737000000` |
| `@CronExpression` | Cron 表达式，支持 6 位和 7 位 | `0 0 12 * * ?` |
| `@Duration` | 时间段，`format` 支持 ISO_8601 / SIMPLE（默认 ANY，两者皆可） | `PT2H30M` / `2h30m` |

### 字符串匹配（6）

| 注解 | 规则 | 关键参数 |
|------|------|---------|
| `@StartsWith` | 以指定前缀开头 | `startsWith`、`ignoreCase` |
| `@StartsWithAny` | 以任一前缀开头（v1.2.0 新增） | `value`、`ignoreCase` |
| `@EndsWith` | 以指定后缀结尾 | `endsWith`、`ignoreCase` |
| `@EndsWithAny` | 以任一后缀结尾（v1.2.0 新增） | `value`、`ignoreCase` |
| `@Contains` | 包含指定子串（v1.0.1 新增） | `value`、`ignoreCase`、`matchAll`（默认 false=OR，任一即可） |
| `@NotContains` | 不包含指定子串（v1.1.0 新增） | `value`、`ignoreCase`、`matchAll`（默认 true=AND，全都不含） |

```java
@StartsWithAny({"http://", "https://"})   // OR：任一前缀
@Contains(value = {"@", "."}, matchAll = true)  // AND：全部包含
@NotContains(value = {"<script", "javascript:"}, ignoreCase = true)  // XSS 防护
```

> v1.2.0 起，`@StartsWith`/`@EndsWith` 链式 API 参数改为 `String`，多值匹配请用 `@StartsWithAny`/`@EndsWithAny`。

### 值列表与枚举（3）

| 注解 | 规则 | 关键参数 |
|------|------|---------|
| `@In` | 值必须在列表中（支持集合/数组） | `value` |
| `@NotIn` | 值不能在列表中 | `value` |
| `@Enum` | 值必须是指定枚举的有效值 | `target`、`field`（默认验证 `code()` 值） |

```java
@In({"admin", "user", "guest"})
@Enum(target = StatusEnum.class, field = "type")
```

### 格式编码（12）

| 注解 | 规则 | 关键参数 |
|------|------|---------|
| `@Password` | 密码强度，默认最小 8 位，**必须同时含大写、小写、数字、特殊字符** | `minLength`（默认 8）、`requireUppercase`/`requireLowercase`/`requireDigit`/`requireSpecialChar`（默认均 true） |
| `@UUID` | UUID 格式（标准/紧凑） | `allowWithoutHyphens` |
| `@Base64` | Base64 编码（标准/URL-safe） | `urlSafe`、`allowNoPadding` |
| `@JSON` | JSON 格式，内置解析器无需外部依赖 | `type`（OBJECT/ARRAY/ANY）、`strict`、`maxDepth`、`maxLength` |
| `@JWT` | JWT 令牌（三部分 + Base64URL） | - |
| `@SemVer` | 语义化版本号（SemVer 2.0.0） | `allowVPrefix` |
| `@Color` | 颜色（`#FFF`/`#FFFFFF`） | - |
| `@FileExtension` | 文件扩展名白名单 | `value`、`ignoreCase` |
| `@FileSize` | 文件大小范围，支持 File/Path/byte[]/MultipartFile | `min`、`max`（B/KB/MB/GB/TB）、`allowedTypes`（MIME） |
| `@ExpressNumber` | 快递单号，支持顺丰/圆通/申通/中通/韵达/EMS/京东/德邦/天天/百世 | `companies` |
| `@Age` | 年龄验证（周岁），支持 LocalDate/Date/String（出生日期或身份证号） | `min`、`max`、`fromIdCard`、`dateFormat` |
| `@Port` | 端口号（0-65535） | - |

```java
@Password(minLength = 6, requireSpecialChar = false)
@UUID(allowWithoutHyphens = true)
@FileSize(min = "1KB", max = "10MB", allowedTypes = {"image/jpeg", "image/png"})
@Age(min = 18, max = 65, fromIdCard = true)
```

---

## 二、身份验证相关（17 个）

### 中国大陆证件（5）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@ChineseIdCard` | 身份证号，支持 18 位（末位可为 X）和 15 位，校验地址码、出生日期、顺序码及校验码 | `11010119900307211X` |
| `@ChineseName` | 中国人姓名，仅中文，支持 "·"，**长度 2-50，无配置参数** | `买买提·吐尔逊` |
| `@ChinesePassport` | 中国护照 | `G12345678` |
| `@ChineseMilitaryOfficer` | 军官证 | `军字第1234567号` |
| `@ChineseSoldier` | 士兵证 | `沈字第0100000号` |

### 港澳台及外籍证件（6）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@ForeignerPermanentResidenceIdentity` | 外国人永久居留身份证 | `911124198108030028` |
| `@HKMacauResidence` | 港澳居民居住证 | `810000000000000001` |
| `@HKMacauPass` | 港澳居民来往内地通行证（回乡证） | `H1234567800` |
| `@TaiwanResidence` | 台湾居民居住证 | `830000000000000001` |
| `@TaiwanPass` | 台湾居民来往大陆通行证（台胞证） | `1234567800` |
| `@ForeignerWorkPermit` | 外国人工作许可证 | 字母和数字组合 |

### 企业与联系方式（6）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@UnifiedSocialCreditCode` | 统一社会信用代码（18 位） | `91350100M000100Y43` |
| `@ChinesePhone` | 中国手机号（11 位） | `13812345678` |
| `@ChineseLandline` | 中国座机号（区号 + 分机） | `010-12345678` |
| `@ChinesePhoneOrLandline` | 手机号或座机号 | `010-12345678` |
| `@PhoneNumber` | 国际电话（E.164），支持空格/连字符/括号/分机号 | `+8613812345678` |
| `@Email` | 电子邮箱 | `test@example.com` |

```java
// @PhoneNumber 参数：countryCode（限定国家）、allowExtension（分机号，默认 true）、strict（必须含国家代码）
@PhoneNumber(countryCode = "+86")   // 仅中国
@PhoneNumber(strict = true)         // 必须带国家代码
```

---

## 三、金融验证相关（7 个）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@BankCard` | 银行卡号（Luhn 算法），允许带空格或连字符 | `4012 8888 8888 1881` |
| `@CVV` | CVV/CVC 安全码（3-4 位） | `123`、`1234` |
| `@IBAN` | 国际银行账户号（格式 + 校验位） | `DE44500800000123456789` |
| `@SWIFT` | SWIFT/BIC 银行代码 | `COBADEFF` |
| `@StockCode` | 股票代码，可配 `exchanges` 限定交易所 | `600000`、`00700`、`AAPL` |
| `@TradeOrderNumber` | 交易订单号（T+18 位数字 / 纯 18 位数字 / UUID） | `T123456789012345678` |
| `@FinancialProductCode` | 基金/债券代码，可配 `productTypes`（FUND/BOND） | `500001`、`100001` |

**`@StockCode` 交易所规则**：上交所 6 位以 6 开头（`600000`）；深交所 6 位以 0/3/4 开头（`000001`、`300001`）；港交所 4-5 位数字（`00700`）；纽交所 1-5 个字母可含点号（`AAPL`、`BRK.A`）。

---

## 四、教育/职业资格验证（7 个）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@DegreeCertificate` | 学位证书编号 | `1075522008000001` |
| `@Doctor` | 医师资格证编号（24/27 位） | `20251111014406081973100014` |
| `@Teacher` | 教师资格证编号（17 位数字） | `20253412345678901` |
| `@Lawyer` | 法律职业资格证书/律师执业证 | `11101201810123456` |
| `@PMP` | PMP 证书编号 | `1234567`、`PMP123456` |
| `@Constructor` | 建造师证书编号（汉字 + 12 位数字） | `京111050700001` |
| `@Accountant` | 会计资格证书编号（11 位数字） | `21010203451` |

---

## 五、网络相关（5 个）

| 注解 | 规则 | 关键参数/示例 |
|------|------|---------|
| `@Domain` | 域名 | `example.com` |
| `@Ip` | IP 地址，`version` 支持 V4 / V6（默认两者） | `192.168.1.1`、`2001:0db8:85a3::8a2e:0370:7334` |
| `@Mac` | MAC 地址（冒号/连字符） | `00:1A:2B:3C:4D:5E` |
| `@Url` | URL 地址 | `https://example.com/path` |
| `@SubnetMask` | 子网掩码 | `255.255.255.0` |

> 注意：ValidX 只有 `@Ip` 一个注解（没有 `@IPv4`/`@IPv6`），通过 `version` 参数区分。

---

## 六、中国特定验证（11 个）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@ChineseLicensePlate` | 中国车牌号 | `京A12345`、`京A12345D` |
| `@ChinesePatent` | 中国专利号 | `ZL2013106997442` |
| `@ChineseTrademark` | 中国商标注册号 | `1234567`、`第1234567号` |
| `@SoftwareCopyright` | 软件著作权登记号 | `软著登字第2023001234号` |
| `@WorkCopyright` | 一般作品著作权登记号 | `作登字22-2023-A-0018号` |
| `@ChineseZipCode` | 中国邮政编码（6 位） | `100000` |
| `@DrugApproval` | 药品批准文号 | `国药准字H20210039` |
| `@DrugCode` | 药品本位码（69 开头，20 位） | `69012345678901234563` |
| `@MedicalDeviceRegistration` | 医疗器械注册证号 | `国械注准20243010001` |
| `@QQ` | QQ 号码 | `123456789` |
| `@WeChat` | 微信号，6-20 个字符，字母开头，仅含字母/数字/下划线/减号 | `wechat123` |

---

## 七、汽车相关验证（2 个）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@VIN` | 车辆识别码（格式 + 校验位） | `WP0AJ2972LL122844` |
| `@VehicleEngine` | 车辆发动机号 | `123456`、`ABC123` |

## 八、图书相关验证（7 个）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@ISBN` | 国际标准书号（10/13 位） | `9780306406157` |
| `@ISSN` | 国际标准连续出版物号（8 位） | `0317-8471` |
| `@DOI` | 数字对象标识符 | `10.1000/182` |
| `@CLC` | 中国图书馆分类法 | `TP311.138` |
| `@DDC` | 杜威十进制分类法 | `516.3` |
| `@ORCID` | 研究者身份识别码（支持不带连字符） | `0000-0002-1825-0097` |
| `@IPC` | 国际专利分类号 | `A01B1/00` |

## 九、手机相关验证（1 个）

| 注解 | 规则 | 示例 |
|------|------|------|
| `@IMEI` | 国际移动设备识别码（15 位） | `123412341234564`、`123412-341234564` |

---

## 实战：注解组合

```java
public interface Create {}  // 分组标记接口

public class UserDTO {
    // 必填 + 格式 + 业务规则
    @NotBlank(message = "身份证号不能为空")
    @ChineseIdCard
    @Age(min = 18, max = 65, fromIdCard = true)  // 从身份证号自动算年龄
    private String idCard;

    @NotBlank(message = "密码不能为空", groups = Create.class)  // 分组：仅创建时验证
    @Password
    private String password;

    @ChineseIdCard(message = "请输入正确的身份证号码")  // 自定义错误消息
    private String backupCard;
}
```

## 总结

ValidX 的 **100+ 个注解**覆盖从通用（邮箱、URL、日期、密码）到中国特色（身份证、港澳台证件、车牌、药品文号、统一社会信用代码）的完整验证场景。使用原则：

1. 牢记 null/空字符串默认通过验证，必填场景务必搭配 `@NotNull`/`@NotEmpty`/`@NotBlank`；
2. 注解方式用于 DTO（Controller 层），链式 API 用于动态数据（Service 层）；
3. 善用 `message` 自定义错误消息、`groups` 分组验证；
4. 不确定注解参数时，以官方 README 为准。
