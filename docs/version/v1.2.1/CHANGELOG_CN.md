# ValidX v1.2.1 更新日志

**发布日期：** 待定

本文档记录从 v1.2.0 到 v1.2.1 的变更内容。

## 变更概览

- ✨ [新增功能](#新增功能-)
  - 新增 `@IMSI` 国际移动用户识别码验证注解（ITU-T E.212 / 3GPP TS 23.003）
  - 新增 `@ICCID` 集成电路卡识别码验证注解（ITU-T E.118 / GSMA SGP.22，20 位 + Luhn 校验位）
  - 新增 `@Ascii` ASCII 字符验证注解（完整 ASCII 0x00~0x7F，含控制字符）
  - 新增 `@Printable` 可打印 ASCII 字符验证注解（固定为 0x20~0x7E，不允许任何控制字符）
- ✅ [无破坏性变更](#无破坏性变更-)
  - v1.2.1 与 v1.2.0 完全向后兼容，无需迁移
- ♻️ [内部改进](#内部改进-)
  - 抽取公共 `LuhnUtils`，统一 `@BankCard` / `@ICCID` / `@IMEI` 的 Luhn 校验实现（对外行为不变）
- 🌍 [国际化支持](#国际化支持-)
  - 新注解支持完整的 9 种语言消息

---

## 新增功能 ✨

### @IMSI 国际移动用户识别码验证注解

新增 IMSI 验证注解，用于验证字符串是否为有效的国际移动用户识别码（IMSI）。

**背景知识 —— IMSI 与 IMEI 的区别：**
- IMSI 标识"用户 / SIM 卡"，IMEI 标识"设备"
- 依据 ITU-T E.212 / 3GPP TS 23.003，IMSI 结构为：`MCC`（3 位，移动国家码）+ `MNC`（2~3 位，移动网络码）+ `MSIN`（最多 10 位，移动用户识别号）
- 中国大陆 MCC 为 `460`（如中国移动 MNC `00`、中国电信 MNC `03`）

**功能特性：**
- 验证字符串是否为 IMSI：先去除空格与连字符，剩余内容必须为 **14~15 位纯数字**
- 接受常见分隔格式，如 `4600-0123-4567-890`、`460 001 234 567 890`
- null 和空字符串默认通过验证
- 完整的国际化支持（9 种语言）
- IMSI **无内置校验位**（不同于 IMEI 的 Luhn 校验或身份证校验码），格式层只能校验长度与字符集；真实性（MCC/MNC 是否属于真实运营商、该签约是否真实存在）需运营商网络侧确认
- 链式 API：`isIMSI(Object value)`

**注解方式示例：**

```java
public class SimCardDTO {
    // 示例 1：标准 15 位 IMSI（中国移动：MCC 460 + MNC 00）
    @IMSI
    private String imsi;  // "460001234567890" 通过

    // 示例 2：14 位 IMSI（MNC 2 位 + MSIN 9 位）
    @IMSI
    private String shortImsi;  // "46001123456789" 通过

    // 示例 3：带分隔符的格式化输入
    @IMSI
    private String formattedImsi;  // "4600-0123-4567-890" 通过
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 基本用法
validator.field("IMSI").isIMSI("460001234567890");

// 格式化输入（验证前自动去除连字符 / 空格）
validator.isIMSI("4600-0123-4567-890");
validator.isIMSI("460 001 234 567 890");

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：SIM 卡开卡 / 激活登记
public class SimActivationDTO {
    @NotBlank(message = "IMSI 不能为空")
    @IMSI
    private String imsi;
}

// 场景 2：物联网 / eSIM 设备入网档案记录
public class IotDeviceDTO {
    @IMSI
    private String imsi;  // 设备绑定的用户识别码
}

// 场景 3：链式验证用户档案中的运营商签约号码
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("IMSI").isIMSI("460031234567890");
```

**注意事项：**
- 验证前自动去除空格与连字符；其他分隔符不接受
- 全角数字（如 `４６０...`）会被拒绝 —— 它们不属于 ASCII 数字集
- null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotBlank` 使用）
- IMSI 标准上无校验位，请勿套用 IMEI 的 Luhn 算法或身份证式校验码
- 常见应用场景：SIM/eSIM/UICC 开卡登记、物联网设备入网、用户识别码字段校验

---

### @ICCID 集成电路卡识别码验证注解

新增 ICCID 验证注解，用于验证字符串是否为有效的集成电路卡识别码（ICCID）。

**背景知识 —— ICCID 与 IMSI / IMEI 的区别：**
- ICCID 印刷在 SIM/eSIM 卡体上，唯一标识"卡"本身；IMSI 标识"签约用户/SIM"，IMEI 标识"设备"
- 依据 ITU-T E.118 / GSMA SGP.22，现代 UICC/eSIM 的 ICCID 为 20 位数字：`89`（电信行业标识符）+ 国家码（ISO 3166-1，中国 `86`）+ 运营商代码 + 用户账号 + Luhn 校验位
- 中国大陆 SIM 卡通常以 `8986` 开头（如 `8986 0115 2449 3909 2661`）

**功能特性：**
- 验证字符串是否为 ICCID：先去除空格与连字符，剩余内容必须为 **20 位纯数字**
- 整串必须通过 **Luhn** 校验（第 20 位为对前 19 位计算所得校验位，可防输入笔误 / 错位）
- 与 `@IMSI`（无内置校验位）不同，ICCID 自带 Luhn 校验位
- null 和空字符串默认通过验证
- 完整的国际化支持（9 种语言）
- `89`/`8986` 前缀仅作背景说明、不作强制规则；卡是否真实存在需运营商侧确认
- 链式 API：`isICCID(Object value)`

**注解方式示例：**

```java
public class SimCardDTO {
    // 示例 1：标准 20 位 ICCID（中国大陆卡，8986 开头）
    @ICCID
    private String iccid;  // "89860115244939092661" 通过

    // 示例 2：另一个合法 20 位 ICCID
    @ICCID
    private String otherIccid;  // "89866604876475938248" 通过
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 基本用法
validator.field("ICCID").isICCID("89860115244939092661");

// 格式化输入（验证前自动去除连字符 / 空格）
validator.isICCID("8986-0115-2449-3909-2661");
validator.isICCID("8986 0115 2449 3909 2661");

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：SIM / eSIM 卡开卡登记与档案管理
public class SimRegistrationDTO {
    @NotBlank(message = "ICCID 不能为空")
    @ICCID
    private String iccid;  // 印刷在卡体上的号码
}

// 场景 2：物联网模块 / eSIM 设备台账管理
public class IotModuleDTO {
    @ICCID
    private String iccid;
}

// 场景 3：链式验证扫码 / OCR 录入的 ICCID
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("ICCID").isICCID("89860115244939092661");
```

**注意事项：**
- 验证前自动去除空格与连字符；其他分隔符不接受
- 全角数字（如 `８９８６...`）会被拒绝 —— 它们不属于 ASCII 数字集
- null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotBlank` 使用）
- Luhn 校验可拦截笔误，但不能证明卡真实存在 —— 卡的真实性需运营商侧确认
- 常见应用场景：SIM/eSIM/UICC 开卡登记、物联网模块台账、ICCID 条码/OCR 录入校验

---

### @Ascii ASCII 字符验证注解

新增 ASCII 字符验证注解，用于验证字符串是否只包含 ASCII 字符（Unicode **0x00~0x7F**，含全部 33 个控制字符）。

**背景知识：**
- ASCII（美国信息交换标准代码）覆盖 7 位编码 0x00~0x7F 共 128 个码位
- 本注解采用**教科书定义**，对应 C 的 `isascii()`、Python 的 `str.isascii()`
- **可打印 ASCII**（0x20~0x7E）排除了 33 个控制字符（0x00~0x1F + 0x7F），该场景由独立的 `@Printable` 注解承担
- 多行文本需要"允许换行与制表符、但不允许中文 / Emoji"—— 这正是 0x00~0x7F 区间
- 大量系统对接场景要求"纯 ASCII 通道"：协议号、终端命令、SN/IMEI 标签等
- CSV / TXT 文件导入时，常需预先筛掉含中文 / 日文 / Emoji 等非 ASCII 字符的行

**功能特性：**
- 验证字符串只包含 ASCII 字符（0x00~0x7F，含控制字符）
- 换行（`\n`）、制表符（`\t`）、回车（`\r`）、NUL（0x00）、DEL（0x7F）均放行
- 非 ASCII 字符（≥0x80，如中文、日文、Emoji、全角数字）一律拒绝
- 无配置参数 —— 语义由 ASCII 的定义唯一确定
- null 和空字符串默认通过验证
- 完整的国际化支持（9 种语言）
- 链式 API：`isAscii(Object value)`

**与 `@Printable` 的关系：** 二者互补、不重叠：
- `@Ascii`（0x00~0x7F）—— 允许 `"\n"`、`"\t"`，但不允许中文 / Emoji（多行文本常用）
- `@Printable`（0x20~0x7E）—— 连 `"\n"` 也不允许，字符必须全部可见（单行文本 / 标签常用）

一句话：**多行文本用 `@Ascii`，单行 / 必须完全可见用 `@Printable`。**

**注解方式示例：**

```java
public class ContentDTO {
    // 多行文本：可以含换行，但不能含中文 / Emoji
    @Ascii
    private String description;  // "line1\nline2" 通过；"你好" 不通过

    // 需要字符完全可见时，改用 @Printable
    @Printable
    private String nickname;     // "Tom & Jerry" 通过；"Tom\tJerry" 不通过
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

validator.field("Description").isAscii("line1\nline2");   // 通过（控制字符属于 ASCII）
validator.field("Nickname").isPrintable("Tom & Jerry");   // 通过

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：多行描述 —— 允许换行，但不允许中文 / Emoji
public class ArticleDTO {
    @NotBlank(message = "描述不能为空")
    @Ascii
    private String description;
}

// 场景 2：协议 / 终端命令字段
public class CommandDTO {
    @NotBlank(message = "命令不能为空")
    @Ascii
    private String command;
}

// 场景 3：CSV 导入 —— 拒绝含中文 / Emoji 的行，避免静默乱码
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("CSV Cell").isAscii(row[0]);
```

**注意事项：**
- 采用教科书定义：ASCII = 0x00~0x7F（128 个字符，其中 33 个为控制字符）
- 仅放行 0x00~0x7F；所有非 ASCII 字符（≥0x80）一律拒绝
- null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotBlank` 使用）
- 常见应用场景：多行文本 / 描述 / 备注、协议号 / 终端命令字段、SN / IMEI 原始标签、CSV / TXT 导入非 ASCII 预检、系统对接"不应出现非 ASCII"接口契约校验

---

### @Printable 可打印 ASCII 字符验证注解

新增 `@Printable` 注解，用于校验字符串只包含<b>可打印 ASCII 字符</b>（Unicode 0x20~0x7E）。

**背景：**
- "可打印字符"（printable character）是一个标准概念——对应 PHP 的 `ctype_print()`、C 的 `isprint()`、Python 的 `str.isprintable()`
- 可打印 ASCII = 空格（0x20）+ 全部可见 ASCII 直到 `~`（0x7E），排除了 33 个控制字符
- 许多业务关心的是"字符是否可见、能否在屏幕上显示"，而非"字符是否在 ASCII 编码边界内"

**与 `@Ascii` 的关系：** 二者互补、不重叠 —— 各自承担了原先单个布尔开关所覆盖的一半语义：
- `@Ascii`（0x00~0x7F）—— 允许 `"\n"`、`"\t"`，但不允许中文 / Emoji（多行文本常用）
- `@Printable`（0x20~0x7E）—— 连 `"\n"` 也不允许，字符必须全部可见（单行文本 / 标签常用）

**特性：**
- 校验字符串只包含可打印 ASCII（0x20~0x7E）
- 始终拒绝全部 33 个 ASCII 控制字符（0x00~0x1F 和 0x7F）
- 始终拒绝非 ASCII 字符（中文、日文、Emoji、全角数字等）
- null 和空字符串默认通过验证
- 完整国际化支持（9 种语言）
- 链式 API：`isPrintable(Object value)`

**注解示例：**

```java
public class ProfileDTO {
    // 拒绝 TAB / LF / CR / DEL，仅允许可见字符
    @Printable
    private String nickname;  // "Tom & Jerry" 通过；"Tom\tJerry" 不通过

    @NotBlank(message = "comment is required")
    @Printable
    private String comment;
}
```

**链式 API 示例：**

```java
ValidX validator = ValidX.init();
validator.field("Nickname").isPrintable("Alice");
validator.field("Comment").isPrintable("Hello, world!");
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**典型应用场景：**
- 用户昵称 / 备注 / 评论内容（应当可显示，不应混入不可见的控制字节）
- 打印标签、票据、短信内容
- 导出 TXT / 日志文件时排除特殊控制字符，避免显示乱码
- UI 输入框的语义层面约束（"用户能看到的内容"）

**注意事项：**
- 与 `@Ascii` 互补而非重复：`@Ascii` 放行控制字符，`@Printable` 要求字符完全可见
- 全部 33 个 ASCII 控制字符（0x00~0x1F + 0x7F）都被拒绝
- 全部非 ASCII 字符（≥0x80）都被拒绝
- null 和空字符串默认通过（如需必填请配合 `@NotNull` 或 `@NotBlank` 使用）

---

## 无破坏性变更 ✅

v1.2.1 **无破坏性变更**，与 v1.2.0 完全向后兼容：

- 未修改或删除任何链式 API 签名（新增的 `isIMSI()`、`isICCID()`、`isAscii()`、`isPrintable()` 是纯增量）
- 未改变任何既有注解的语义
- 无依赖或配置变更
- 从 v1.2.0 升级为直接替换即可，无需任何迁移步骤

---

## 内部改进 ♻️

- 新增工具类 `io.github.vipxieliang.validx.util.LuhnUtils`，将此前分散在 `BankCardValidator`、`IMEIValidator`、`ICCIDValidator` 中的三段重复 Luhn 实现，统一为单一方法 `LuhnUtils.isValid(String)`
- `@BankCard`、`@ICCID`、`@IMEI` 现共用同一实现，消除重复代码
- 纯内部重构，**对外行为完全不变**（既有全部测试用例通过）

---

## 国际化支持 🌍

新注解支持以下 9 种语言：

- **简体中文** - `ValidationMessages.properties` 和 `ValidationMessages_zh.properties`
- **英语** - `ValidationMessages_en.properties`
- **日语** - `ValidationMessages_ja.properties`
- **韩语** - `ValidationMessages_ko.properties`
- **法语** - `ValidationMessages_fr.properties`
- **德语** - `ValidationMessages_de.properties`
- **西班牙语** - `ValidationMessages_es.properties`
- **俄语** - `ValidationMessages_ru.properties`

**错误消息：**
- `@IMSI`："IMSI号码格式不正确"（消息键：`io.github.vipxieliang.validx.annotation.imsi`）
- `@ICCID`："ICCID号码格式不正确"（消息键：`io.github.vipxieliang.validx.annotation.iccid`）
- `@Ascii`："只能包含ASCII字符（0x00-0x7F，含控制字符）"（消息键：`io.github.vipxieliang.validx.annotation.ascii`）
- `@Printable`："只能包含可打印ASCII字符（0x20-0x7E，不含控制字符）"（消息键：`io.github.vipxieliang.validx.annotation.printable`）

所有语言包的消息格式保持一致，采用正确的 Unicode 编码。

---

## 测试覆盖 🧪

新功能具有注解与链式双路径的全面测试覆盖：

**验证器测试（Bean Validation 框架）：**
- `IMSIValidatorTest`：3 个测试方法，覆盖 null/空值、6 个合法用例（中国移动 `46000...`、中国电信 `46003...`、美国 AT&T `310260...`、14 位格式、连字符分隔、空格分隔）与 7 个非法用例（13/16 位长度越界、含字母、全角数字）

**链式验证测试：**
- `IMSIValidationChainTest`：3 个测试方法，覆盖 null/空值、合法值（标准 15 位、连字符分隔、空格分隔、14 位）与非法值（过长、过短、含非数字字符）

**ICCID 验证器测试（Bean Validation 框架）：**
- `ICCIDValidatorTest`：3 个测试方法，覆盖 null/空值、7 个合法用例（`8986` 开头 20 位且 Luhn 通过、连字符分隔、空格分隔）与 7 个非法用例（19/21/22 位长度越界、Luhn 校验位错误、含字母、全角数字）

**ICCID 链式验证测试：**
- `ICCIDValidationChainTest`：3 个测试方法，覆盖 null/空值、合法值（标准 20 位、连字符分隔、空格分隔、另一个 Luhn 合法号）与非法值（过短、过长、校验位错误、含非数字字符）

**Ascii 验证器测试（Bean Validation 框架）：**
- `AsciiValidatorTest`：24 个测试方法（注解方式，通过 `Validator` 校验标注了 `@Ascii` 的 DTO），覆盖普通 ASCII（`Hello, World!`、字母数字、标点、空格）通过，控制字符（LF / TAB / CR / NUL 0x00 / DEL 0x7F）通过，完整 0x00~0x7F 区间与上下边界通过，拒绝非 ASCII（中文/日文/Emoji/0x80/0xFF/全角数字 U+FF11），以及与 `@Printable` 的边界对比（换行在 `@Ascii` 通过、在 `@Printable` 被拒）；null/空值通过

**Ascii 链式验证测试：**
- `AsciiValidationChainTest`：13 个测试方法，覆盖普通 ASCII、控制字符（TAB / LF）与多行文本通过、完整 0x00~0x7F 区间、拒绝非 ASCII（中文/Emoji/0x80/与合法控制字符混用）、与 `isPrintable` 的对比、null/空值、与其他规则链式调用

**Printable 验证器测试（Bean Validation 框架）：**
- `PrintableValidatorTest`：24 个测试方法（注解方式，通过 `Validator` 校验标注了 `@Printable` 的 DTO），覆盖合法的可打印 ASCII（`Hello, World!`、字母数字、标点、空格、首尾空格、完整 0x20~0x7E 区间、下边界 0x20、上边界 0x7E），拒绝全部 33 个 ASCII 控制字符（0x00~0x1F 与 0x7F：TAB、LF、CR、DEL、NUL、最后一个 C0 控制字符 0x1F 等），拒绝非 ASCII（中文、日文、Emoji、全角数字 0xFF11、0x80、0xFF）；null/空值通过

**Printable 链式验证测试：**
- `PrintableValidationChainTest`：20 个测试方法，覆盖 null/空值、合法的可打印 ASCII 字符串（含下/上边界与完整区间），拒绝控制字符（TAB / LF / CR / DEL / NUL），拒绝非 ASCII（中文、Emoji、0x80），以及链式调用（`isUpper`、多次 `isPrintable`、多次失败的累积）

**总计：** 81 个新增测试方法，全部通过 ✅

---

## 相关链接 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.2.1)
- 📖 [完整文档](../../../README.cn.md)
- 🐛 [问题反馈](https://github.com/vipxieliang/ValidX/issues)
- 💡 [功能建议](https://github.com/vipxieliang/ValidX/issues/new)

---

由 ValidX 团队用 ❤️ 发布
