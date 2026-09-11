# ValidX v1.2.1 更新日志

**发布日期：** 待定

本文档记录从 v1.2.0 到 v1.2.1 的变更内容。

## 变更概览

- ✨ [新增功能](#新增功能-)
  - 新增 `@IMSI` 国际移动用户识别码验证注解（ITU-T E.212 / 3GPP TS 23.003）
  - 新增 `@ICCID` 集成电路卡识别码验证注解（ITU-T E.118 / GSMA SGP.22，20 位 + Luhn 校验位）
- ✅ [无破坏性变更](#无破坏性变更-)
  - v1.2.1 与 v1.2.0 完全向后兼容，无需迁移
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

## 无破坏性变更 ✅

v1.2.1 **无破坏性变更**，与 v1.2.0 完全向后兼容：

- 未修改或删除任何链式 API 签名（新增的 `isIMSI()`、`isICCID()` 是纯增量）
- 未改变任何既有注解的语义
- 无依赖或配置变更
- 从 v1.2.0 升级为直接替换即可，无需任何迁移步骤

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

**总计：** 12 个新增测试方法，全部通过 ✅

---

## 相关链接 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.2.1)
- 📖 [完整文档](../../../README.cn.md)
- 🐛 [问题反馈](https://github.com/vipxieliang/ValidX/issues)
- 💡 [功能建议](https://github.com/vipxieliang/ValidX/issues/new)

---

由 ValidX 团队用 ❤️ 发布
