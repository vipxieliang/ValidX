# ValidX 应用于CRM系统客户信息验证：企业客户 vs 个人客户

## 📋 目录
- [引言](#引言)
- [一、CRM客户数据验证的全景](#一crm客户数据验证的全景)
  - [1.1 两类客户：两套完全不同的数据模型](#11-两类客户两套完全不同的数据模型)
  - [1.2 CRM客户验证的 4 个层次](#12-crm客户验证的-4-个层次)
  - [1.3 核心挑战：类型决定规则](#13-核心挑战类型决定规则)
- [二、企业客户验证：统一社会信用代码是核心](#二企业客户验证统一社会信用代码是核心)
  - [2.1 企业身份标识：统一社会信用代码](#21-企业身份标识统一社会信用代码)
  - [2.2 企业名称与法人信息](#22-企业名称与法人信息)
  - [2.3 企业对公账户信息](#23-企业对公账户信息)
  - [2.4 纳税人资质信息](#24-纳税人资质信息)
  - [2.5 ValidX 实现](#25-validx-实现)
- [三、个人客户验证：身份证 + 手机号是核心](#三个人客户验证身份证--手机号是核心)
  - [3.1 个人身份标识：身份证 + 护照](#31-个人身份标识身份证--护照)
  - [3.2 联系方式：手机、邮箱、微信](#32-联系方式手机邮箱微信)
  - [3.3 个人信息：姓名、年龄、地址](#33-个人信息姓名年龄地址)
  - [3.4 ValidX 实现](#34-validx-实现)
- [四、通用字段验证：合同、金额、联系人](#四通用字段验证合同金额联系人)
  - [4.1 合同日期：时间线约束](#41-合同日期时间线约束)
  - [4.2 合同金额：范围与精度](#42-合同金额范围与精度)
  - [4.3 客户等级与状态：枚举校验](#43-客户等级与状态枚举校验)
  - [4.4 联系人信息：多联系人与主联系人](#44-联系人信息多联系人与主联系人)
  - [4.5 地址信息：省市区 + 邮编 + 详细地址](#45-地址信息省市区--邮编--详细地址)
- [五、企业客户 vs 个人客户：统一建模方案](#五企业客户-vs-个人客户统一建模方案)
  - [5.1 为什么不能"两套 DTO"](#51-为什么不能两套-dto)
  - [5.2 统一 DTO + 分支校验](#52-统一-dto--分支校验)
  - [5.3 继承体系方案](#53-继承体系方案)
  - [5.4 两种方案的对比](#54-两种方案的对比)
- [六、完整实现：CRM DTO + Service](#六完整实现crm-dto--service)
- [七、常见坑与规避](#七常见坑与规避)
- [八、总结](#八总结)
- [项目地址](#项目地址)

---

## 引言

在 CRM / 销售管理 / 客户管理系统里，**客户**是最核心的数据对象。和前面几篇文章的场景不同，CRM 系统有一个独特的挑战——**客户分两种**：

- **企业客户（B2B）**：公司、企业、个体工商户、事业单位……需要校验统一社会信用代码、企业名称、法人信息、纳税人资质；
- **个人客户（B2C）**：自然人消费者、个人用户……需要校验身份证、手机号、姓名。

两类客户的校验规则**几乎完全不同**，但它们在 CRM 系统里又共用一套接口、一张表、一套业务流程。这就带来了一个工程难题：**如何在"统一建模"和"差异化校验"之间取得平衡？**

本文把 CRM 客户验证拆成四块讲透——**企业客户（信用代码 + 法人 + 纳税人资质）、个人客户（身份证 + 手机号 + 姓名）、通用字段（合同 + 金额 + 联系人）、统一建模（分支校验 vs 继承体系）**，并给出可以直接落地的 ValidX 实现。文章最后会重点讨论一个架构选择：**统一 DTO + 分支校验** vs **继承体系 + 多态校验**——两种方案各有利弊，需要根据团队规模和业务复杂度选择。

---

## 一、CRM客户数据验证的全景

### 1.1 两类客户：两套完全不同的数据模型

先看全局。一个 CRM 系统里，企业客户和个人客户的数据模型差异之大，几乎可以认为是两个不同的实体：

```
┌──────────────────────────────┐     ┌──────────────────────────────┐
│        企业客户（B2B）         │     │        个人客户（B2C）         │
│                              │     │                              │
│  · 企业名称                   │     │  · 姓名                      │
│  · 统一社会信用代码            │     │  · 身份证号                   │
│  · 法定代表人                  │     │  · 手机号                    │
│  · 法人身份证                  │     │  · 邮箱                      │
│  · 对公银行账户                │     │  · 微信/QQ                   │
│  · 纳税人资质（一般/小规模）      │     │  · 出生日期                   │
│  · 行业分类                    │     │  · 收货地址                   │
│  · 注册资本                    │     │  · 会员等级                   │
│  · 营业执照有效期               │     │  · 积分/余额                  │
│  · 对公联系人（可多个）          │     │  · 紧急联系人                  │
└──────────────────────────────┘     └──────────────────────────────┘
     主体 = 企业                          主体 = 自然人
     校验 = 信用代码算法                   校验 = 身份证算法
     金额 = 合同额 / 信用额度             金额 = 订单额 / 消费额
```

| 维度 | 企业客户 | 个人客户 |
|------|---------|---------|
| **身份标识** | 统一社会信用代码（18 位，mod 31） | 身份证号（18 位，mod 11） |
| **名称** | 企业名称（中文，含"有限公司"等后缀） | 个人姓名（中文，2-20 字） |
| **联系方式** | 对公座机 + 企业邮箱 | 手机号 + 个人邮箱 |
| **地址** | 注册地址 + 经营地址（可能不同） | 收货地址 / 通讯地址 |
| **金额** | 合同金额、信用额度、应收账款 | 订单金额、消费金额、余额 |
| **日期** | 营业执照有效期、合同起止日期 | 出生日期、会员到期日期 |

### 1.2 CRM客户验证的 4 个层次

| 层次 | 做什么 | 能挡住什么 | 需要什么 |
|------|--------|-----------|---------|
| **L1 格式校验** | 长度、字符集、正则 | 手输错位、粘贴乱码 | 正则 / ValidX |
| **L2 算法校验** | 信用代码 mod 31、身份证 mod 11、银行卡 Luhn | 伪造编号、输错一位 | 校验位算法 |
| **L3 关系校验** | 合同日期先后、金额勾稽、类型与标识一致性 | 日期倒挂、类型错配 | 跨字段校验 |
| **L4 业务合规** | 信用额度上限、合同金额审批阈值、客户去重 | 超额授信、重复录入 | 业务规则引擎 |

**关键认知**：CRM 系统最独特的是 **L3 的"类型与标识一致性"**——`customerType = ENTERPRISE` 时，`creditCode` 必须通过 `@UnifiedSocialCreditCode` 校验；`customerType = INDIVIDUAL` 时，`idCard` 必须通过 `@ChineseIdCard` 校验。这种**"字段 A 的值决定字段 B 的校验规则"**的模式，在 CRM 里无处不在。

### 1.3 核心挑战：类型决定规则

用一个例子说明问题的复杂性：

```java
// 反例：不区分客户类型，统一校验
@NotBlank(message = "信用代码不能为空")
@UnifiedSocialCreditCode(message = "信用代码格式不正确")
private String creditCode;

@NotBlank(message = "身份证号不能为空")
@ChineseIdCard(message = "身份证号格式不正确")
private String idCard;
```

**问题**：企业客户没有身份证号，个人客户没有信用代码——两个字段都标 `@NotBlank` 会导致对方的数据被拒。

**正确的做法**：根据客户类型**分支校验**——这正是本文的核心主题。

---

## 二、企业客户验证：统一社会信用代码是核心

### 2.1 企业身份标识：统一社会信用代码

三证合一之后，**统一社会信用代码**就是企业的"身份证号"——营业执照号、税务登记证号、组织机构代码，全部合并为一个 18 位代码。

```
┌────┬────┬──────────────────┬──────────┬────┐
│ 1位 │ 1位 │     6位          │   9位     │ 1位 │
│ 登记│ 机构 │  登记管理机关     │  主体标识  │校验│
│ 部门│ 类别 │  行政区划码       │  序号     │ 位 │
└────┴────┴──────────────────┴──────────┴────┘
```

- **第 1 位**：登记管理部门代码（`9` = 工商，最常见）；
- **第 2 位**：机构类别代码；
- **第 3-8 位**：登记管理机关行政区划码（与身份证前 6 位同源）；
- **第 9-17 位**：主体标识码（组织机构代码）；
- **第 18 位**：校验位（mod 31 算法）。

**ValidX 的 `@UnifiedSocialCreditCode` 一行搞定**：

```java
@NotBlank(message = "统一社会信用代码不能为空")
@UnifiedSocialCreditCode(message = "统一社会信用代码格式不正确")
private String creditCode;
```

**链式 API 版本**：

```java
ValidX v = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);
v.field("统一社会信用代码").isUnifiedSocialCreditCode(companyData.get("creditCode"));
```

#### 2.1.1 校验位算法回顾：mod 31 + 17 个加权因子

`@UnifiedSocialCreditCode` 背后的算法（详见第 42 篇《财务系统发票验证》的税号章节）：

```
字符集（31 个字符，去掉 I、O、S、V、Z）：
0123456789ABCDEFGHJKLMNPQRTUWXY

加权因子（前 17 位）：
{1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28}

计算步骤：
1. 前 17 位 → 按字符集索引映射为 0-30 的数值
2. 逐位乘以对应加权因子，求和
3. 校验码索引 = 31 - (sum mod 31)，若结果 = 31 则取 0
4. 第 18 位必须等于字符集[校验码索引]
```

**算法的检错能力**：

| 错误类型 | 能否检出 |
|---------|---------|
| 单字符替换 | ✅ 100% |
| 相邻两字符互换 | ✅ 绝大多数 |
| 长度错（17 / 19 位） | ✅ 100% |
| 含禁用字符（I/O/S/V/Z） | ✅ 100% |

#### 2.1.2 从信用代码提取信息

和身份证类似，统一社会信用代码里也藏着可提取的信息：

```java
@Service
public class CreditCodeInfoExtractor {

    /**
     * 提取行政区划码（第 3-8 位）
     * 与身份证前 6 位同源（GB/T 2260）
     */
    public String extractRegionCode(String creditCode) {
        if (creditCode == null || creditCode.length() != 18) {
            return null;
        }
        return creditCode.substring(2, 8);
    }

    /**
     * 判断是否为市场主体（第 1 位 = '9' = 工商登记）
     * 这是业务规则，不是格式规则
     */
    public boolean isBusinessEntity(String creditCode) {
        return creditCode != null && creditCode.length() == 18
                && creditCode.charAt(0) == '9';
    }

    /**
     * 判断登记管理部门（第 1 位）
     * 1 = 机构编制（政府/事业单位）
     * 5 = 民政（社会团体）
     * 9 = 工商（企业，最常见）
     * Y = 其他
     */
    public String getDepartmentCode(String creditCode) {
        if (creditCode == null || creditCode.length() != 18) {
            return null;
        }
        return String.valueOf(creditCode.charAt(0));
    }
}
```

> **洞察**：信用代码的行政区划段（第 3-8 位）和身份证前 6 位**同源**——都来自 GB/T 2260。这意味着如果你在做身份证校验时已经建了一套"行政区划码注册表"，**可以直接复用**，不需要再维护一份。这也是把校验逻辑做成"公共库"的价值所在。

### 2.2 企业名称与法人信息

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 企业名称 | 中文、含公司类型后缀、长度合理 | `@Chinese` + `@Size` 或 `@Pattern` |
| 法定代表人姓名 | 中文字符、2-20 字 | `@ChineseName` |
| 法人身份证号 | 18 位 + mod 11 校验位 | `@ChineseIdCard` |
| 营业执照有效期 | 日期格式、不能已过期 | `@Date` + `@FutureDate` |
| 注册资本 | 正数、最多 2 位小数 | `@DecimalMin` + `@Digits` |

```java
@NotBlank(message = "企业名称不能为空")
@Size(min = 2, max = 100, message = "企业名称长度需在 2-100 字之间")
private String companyName;

@NotBlank(message = "法定代表人姓名不能为空")
@ChineseName(message = "法定代表人姓名格式不正确")
private String legalRepresentativeName;

@NotBlank(message = "法人身份证号不能为空")
@ChineseIdCard(message = "法人身份证号格式不正确")
private String legalRepresentativeIdCard;
```

**关于企业名称的常见误解**：很多人用 `@Chinese` 校验企业名称——但 `@Chinese` 只允许**纯中文字符**，而企业名称里经常包含括号、数字（如"北京XX科技有限公司(海淀分公司)"）。更合适的做法是：

```java
// 方案 A：用 @Pattern 允许中文、字母、数字、括号
@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9()（）]+$",
         message = "企业名称包含非法字符")
private String companyName;

// 方案 B：用 @ChineseAlphaNum 做宽松校验（中文 + 字母 + 数字）
// 再配合业务规则检查公司后缀
@ChineseAlphaNum(message = "企业名称格式不正确")
private String companyName;
```

### 2.3 企业对公账户信息

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 开户银行 | 非空、长度合理 | `@NotBlank` + `@Size` |
| 银行账号 | Luhn 算法校验 | `@BankCard` |
| 开户名 | 应与企业名称一致（跨字段） | `@Size` + Service 层校验 |
| SWIFT 代码（跨境） | SWIFT/BIC 格式 | `@SWIFT` |
| IBAN（跨境） | IBAN 格式 | `@IBAN` |

```java
@NotBlank(message = "开户银行不能为空")
@Size(min = 2, max = 50, message = "开户银行长度需在 2-50 字之间")
private String bankName;

@NotBlank(message = "银行账号不能为空")
@BankCard(message = "银行账号格式不正确")
private String bankAccountNo;

@NotBlank(message = "开户名不能为空")
@Size(min = 2, max = 100, message = "开户名长度需在 2-100 字之间")
private String bankAccountName;

// 跨境支付场景
@SWIFT(message = "SWIFT 代码格式不正确")
private String swiftCode;

@IBAN(message = "IBAN 格式不正确")
private String iban;
```

**跨字段校验**（开户名 = 企业名称）：

```java
public void validateBankAccount(ValidX v, String bankAccountName,
                                 String companyName) {
    if (bankAccountName != null && companyName != null
            && !bankAccountName.equals(companyName)) {
        v.field("开户名").getErrors()
         .add("对公账户开户名（" + bankAccountName
            + "）与企业名称（" + companyName + "）不一致");
    }
}
```

### 2.4 纳税人资质信息

企业客户的纳税人资质直接影响发票开具规则：

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 纳税人类型 | 枚举：一般纳税人 / 小规模纳税人 | `@In` |
| 开户行行号 | 纯数字、12 位 | `@FixedLength` 或 `@Pattern` |
| 注册地址 | 中文 + 字母 + 数字 | `@ChineseAlphaDash` |
| 注册电话 | 手机号或座机号 | `@ChinesePhoneOrLandline` |

```java
@NotBlank(message = "纳税人类型不能为空")
@In({"GENERAL_TAXPAYER", "SMALL_SCALE_TAXPAYER"},
    message = "纳税人类型不在合法范围内")
private String taxpayerType;

@Pattern(regexp = "^\\d{12}$", message = "开户行行号必须为 12 位数字")
private String bankBranchCode;

@ChineseAlphaDash(message = "注册地址格式不正确")
private String registeredAddress;

@ChinesePhoneOrLandline(message = "注册电话格式不正确")
private String registeredPhone;
```

> **纳税人资质与发票的关系**：一般纳税人可以开具增值税专用发票（可抵扣），小规模纳税人只能开普通发票（不可抵扣）。这个分类直接影响第 42 篇《财务系统发票验证》中的发票校验逻辑——**CRM 的纳税人资质是发票校验的前置依赖**。

### 2.5 ValidX 实现

完整的企业客户 DTO：

```java
@Data
public class EnterpriseCustomerDTO {

    // ==================== 企业身份 ====================

    @NotBlank(message = "统一社会信用代码不能为空")
    @UnifiedSocialCreditCode(message = "统一社会信用代码格式不正确")
    private String creditCode;

    @NotBlank(message = "企业名称不能为空")
    @Size(min = 2, max = 100, message = "企业名称长度需在 2-100 字之间")
    private String companyName;

    // ==================== 法人信息 ====================

    @NotBlank(message = "法定代表人姓名不能为空")
    @ChineseName(message = "法定代表人姓名格式不正确")
    private String legalRepName;

    @NotBlank(message = "法人身份证号不能为空")
    @ChineseIdCard(message = "法人身份证号格式不正确")
    private String legalRepIdCard;

    // ==================== 对公账户 ====================

    @NotBlank(message = "开户银行不能为空")
    @Size(min = 2, max = 50)
    private String bankName;

    @NotBlank(message = "银行账号不能为空")
    @BankCard(message = "银行账号格式不正确")
    private String bankAccountNo;

    @NotBlank(message = "开户名不能为空")
    @Size(min = 2, max = 100)
    private String bankAccountName;

    // ==================== 纳税人资质 ====================

    @NotBlank(message = "纳税人类型不能为空")
    @In({"GENERAL_TAXPAYER", "SMALL_SCALE_TAXPAYER"})
    private String taxpayerType;

    @Pattern(regexp = "^\\d{12}$", message = "开户行行号必须为 12 位数字")
    private String bankBranchCode;

    @ChineseAlphaDash(message = "注册地址格式不正确")
    private String registeredAddress;

    @ChinesePhoneOrLandline(message = "注册电话格式不正确")
    private String registeredPhone;

    // ==================== 营业执照 ====================

    @NotNull(message = "营业执照有效期不能为空")
    @Date(pattern = "yyyy-MM-dd")
    @FutureDate(includeToday = true, message = "营业执照已过期")
    private String businessLicenseExpiry;

    @DecimalMin(value = "0.01", message = "注册资本必须大于 0")
    @Digits(integer = 14, fraction = 2, message = "注册资本最多 2 位小数")
    private BigDecimal registeredCapital;

    // ==================== 行业 ====================

    @NotBlank(message = "行业分类不能为空")
    @In({"IT", "MANUFACTURING", "FINANCE", "RETAIL",
         "EDUCATION", "HEALTHCARE", "REAL_ESTATE", "OTHER"},
        message = "行业分类不在合法范围内")
    private String industry;
}
```

---

## 三、个人客户验证：身份证 + 手机号是核心

### 3.1 个人身份标识：身份证 + 护照

个人客户的身份校验相对直接——中国籍用身份证，外籍用护照：

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 身份证号 | 18 位 + mod 11 校验位 | `@ChineseIdCard` |
| 护照号（外籍客户） | 特定格式 | `@ChinesePassport` |
| 港澳居民居住证 | 特定格式 | `@HKMacauResidence` |
| 台湾居民居住证 | 特定格式 | `@TaiwanResidence` |
| 外国人永久居留身份证 | 特定格式 + 校验位 | `@ForeignerPermanentResidenceIdentity` |

```java
@NotBlank(message = "证件号码不能为空")
private String idNumber;  // 根据证件类型分支校验
```

### 3.2 联系方式：手机、邮箱、微信

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 手机号 | 11 位、号段合法 | `@ChinesePhone` |
| 座机号（可选） | 区号 + 号码 | `@ChineseLandline` |
| 手机或座机 | 满足其一即可 | `@ChinesePhoneOrLandline` |
| 个人邮箱 | RFC 5322 格式 | `@Email` |
| 微信号（可选） | 微信号规则 | `@WeChat` |
| QQ号（可选） | QQ 号规则 | `@QQ` |

```java
@Data
public class IndividualContactDTO {

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;

    @ChineseLandline(message = "座机号格式不正确")
    private String landline;

    @Email(message = "邮箱格式不正确")
    private String email;

    @WeChat(message = "微信号格式不正确")
    private String wechat;

    @QQ(message = "QQ号格式不正确")
    private String qq;
}
```

> **关于 `@WeChat` 和 `@QQ`**：这两个注解是 ValidX 针对中国社交场景的特色验证器。在 CRM 系统里，客户的社交账号是重要的沟通渠道——尤其是个人客户，微信往往是首选联系方式。

### 3.3 个人信息：姓名、年龄、地址

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 姓名 | 中文字符、2-20 字 | `@ChineseName` |
| 出生日期 | 过去日期、与身份证一致 | `@PastDate` |
| 年龄 | 法定年龄（≥18 岁签合同） | `@Age(min = 18)` |
| 性别 | 与身份证第 17 位一致 | 跨字段校验 |
| 邮政编码 | 6 位数字 | `@ChineseZipCode` |
| 通讯地址 | 中文 + 字母 + 数字 | `@ChineseAlphaDash` |

```java
@Data
public class IndividualCustomerDTO {

    @NotBlank(message = "姓名不能为空")
    @ChineseName(message = "姓名格式不正确")
    private String name;

    @NotBlank(message = "身份证号不能为空")
    @ChineseIdCard(message = "身份证号格式不正确")
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "出生日期不能为空")
    @PastDate(includeToday = true, message = "出生日期不能是未来日期")
    @Date(pattern = "yyyy-MM-dd")
    private String birthDate;

    @Age(min = 18, message = "客户年龄必须满 18 周岁")
    private String ageFromIdCard;

    @ChineseZipCode(message = "邮政编码格式不正确")
    private String zipCode;

    @ChineseAlphaDash(message = "通讯地址格式不正确")
    private String address;

    @WeChat(message = "微信号格式不正确")
    private String wechat;

    @In({"NORMAL", "VIP", "SVIP", "BRAND_AMBASSADOR"},
        message = "会员等级不在合法范围内")
    private String memberLevel;
}
```

**跨字段校验**（出生日期与身份证一致性、性别与身份证一致性）：

```java
public void validateIndividualIdentity(ValidX v, String idCard,
                                        String birthDate,
                                        String gender) {
    if (idCard == null || idCard.length() != 18) {
        return;  // 格式校验交给 @ChineseIdCard
    }

    // 出生日期一致性
    String expectedBirth = idCard.substring(6, 10) + "-"
            + idCard.substring(10, 12) + "-"
            + idCard.substring(12, 14);
    if (birthDate != null && !birthDate.equals(expectedBirth)) {
        v.field("出生日期").getErrors()
         .add("出生日期与身份证号中的出生日期不一致");
    }

    // 性别一致性
    int seq = Character.getNumericValue(idCard.charAt(16));
    String expectedGender = seq % 2 == 1 ? "MALE" : "FEMALE";
    if (gender != null && !gender.equals(expectedGender)) {
        v.field("性别").getErrors()
         .add("性别与身份证第 17 位不一致");
    }
}
```

### 3.4 ValidX 实现

（见上方 DTO 代码）

---

## 四、通用字段验证：合同、金额、联系人

无论是企业客户还是个人客户，CRM 系统都有一些**通用字段**需要校验。

### 4.1 合同日期：时间线约束

合同是客户关系的核心载体。合同日期之间存在严格的**时间线约束**：

```
合同开始日期 ≤ 合同结束日期
合同签署日期 ≤ 合同开始日期
合同签署日期 ≤ 今天（过去日期）
```

```java
@Data
public class ContractDTO {

    @NotNull(message = "合同签署日期不能为空")
    @PastDate(includeToday = true, message = "合同签署日期不能是未来日期")
    @Date(pattern = "yyyy-MM-dd")
    private String signDate;

    @NotNull(message = "合同开始日期不能为空")
    @Date(pattern = "yyyy-MM-dd")
    private String startDate;

    @NotNull(message = "合同结束日期不能为空")
    @Date(pattern = "yyyy-MM-dd")
    private String endDate;

    /** 合同期限（月） */
    @DecimalMin(value = "1", message = "合同期限至少 1 个月")
    @DecimalMax(value = "120", message = "合同期限不能超过 120 个月")
    private Integer contractMonths;
}
```

**跨字段校验**（Service 层）：

```java
public void validateContractTimeline(ContractDTO dto) {
    ValidX v = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

    LocalDate sign = LocalDate.parse(dto.getSignDate());
    LocalDate start = LocalDate.parse(dto.getStartDate());
    LocalDate end = LocalDate.parse(dto.getEndDate());

    // 签署日期 ≤ 开始日期
    if (sign.isAfter(start)) {
        v.field("合同签署日期").getErrors()
         .add("合同签署日期不能晚于开始日期");
    }

    // 开始日期 < 结束日期
    if (!start.isBefore(end)) {
        v.field("合同结束日期").getErrors()
         .add("合同结束日期必须晚于开始日期");
    }

    // 合同期限与日期范围一致性
    if (dto.getContractMonths() != null) {
        long actualMonths = ChronoUnit.MONTHS.between(start, end);
        if (actualMonths != dto.getContractMonths()) {
            v.field("合同期限").getErrors()
             .add("合同期限（" + dto.getContractMonths()
                + " 个月）与起止日期不一致（实际 "
                + actualMonths + " 个月）");
        }
    }

    if (!v.passed()) {
        throw new CRMValidationException(v.getErrors());
    }
}
```

### 4.2 合同金额：范围与精度

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 合同金额 | 正数、最多 2 位小数 | `@DecimalMin` + `@Digits` |
| 信用额度（企业客户） | 正数、上限控制 | `@DecimalMin` + `@DecimalMax` |
| 已付金额 | ≤ 合同金额（跨字段） | Service 层 |
| 折扣率 | 0 ~ 1 之间 | `@DecimalMin` + `@DecimalMax` |

```java
@Data
public class ContractAmountDTO {

    @NotNull(message = "合同金额不能为空")
    @DecimalMin(value = "0.01", message = "合同金额必须大于 0")
    @Digits(integer = 12, fraction = 2, message = "合同金额最多 2 位小数")
    private BigDecimal contractAmount;

    @DecimalMin(value = "0.00", message = "信用额度不能为负数")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal creditLimit;

    @DecimalMin(value = "0.00", message = "折扣率不能小于 0")
    @DecimalMax(value = "1.00", message = "折扣率不能超过 1")
    private BigDecimal discountRate;

    @DecimalMin(value = "0.00")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal paidAmount;
}
```

**跨字段校验**（已付金额 ≤ 合同金额）：

```java
public void validatePayment(ValidX v, BigDecimal paidAmount,
                             BigDecimal contractAmount) {
    if (paidAmount != null && contractAmount != null
            && paidAmount.compareTo(contractAmount) > 0) {
        v.field("已付金额").getErrors()
         .add("已付金额（" + paidAmount
            + "）不能超过合同金额（" + contractAmount + "）");
    }
}
```

> **`compareTo` 而不是 `equals`**：和前面几篇文章一样，`BigDecimal` 比较**必须用 `compareTo`**——`equals` 会比较精度，`new BigDecimal("100.0").equals(new BigDecimal("100.00"))` 返回 `false`。

### 4.3 客户等级与状态：枚举校验

```java
@NotBlank(message = "客户等级不能为空")
@In({"NORMAL", "SILVER", "GOLD", "PLATINUM", "DIAMOND"},
    message = "客户等级不在合法范围内")
private String customerLevel;

@NotBlank(message = "客户状态不能为空")
@In({"PROSPECT",      // 潜在客户
     "ACTIVE",        // 活跃客户
     "INACTIVE",      // 非活跃客户
     "CHURNED",       // 流失客户
     "BLACKLISTED"},  // 黑名单
    message = "客户状态不在合法范围内")
private String customerStatus;

@NotBlank(message = "客户来源不能为空")
@In({"ONLINE", "OFFLINE", "REFERRAL", "ADVERTISING",
     "EXHIBITION", "PARTNER", "OTHER"},
    message = "客户来源不在合法范围内")
private String customerSource;
```

### 4.4 联系人信息：多联系人与主联系人

CRM 系统的一个客户可能有**多个联系人**，但只有一个是**主联系人**：

```java
@Data
public class ContactPersonDTO {

    @NotBlank(message = "联系人姓名不能为空")
    @ChineseName(message = "联系人姓名格式不正确")
    private String name;

    @NotBlank(message = "联系人手机号不能为空")
    @ChinesePhone(message = "联系人手机号格式不正确")
    private String phone;

    @Email(message = "联系人邮箱格式不正确")
    private String email;

    @In({"CEO", "CTO", "CFO", "PURCHASE_MANAGER",
         "TECHNICAL_CONTACT", "FINANCE_CONTACT", "OTHER"},
        message = "职务不在合法范围内")
    private String position;

    @NotNull(message = "是否主联系人标识不能为空")
    private Boolean isPrimary;
}
```

**业务规则**：一个客户**必须有且只有一个**主联系人：

```java
public void validatePrimaryContact(List<ContactPersonDTO> contacts) {
    ValidX v = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

    if (contacts == null || contacts.isEmpty()) {
        v.field("联系人").getErrors().add("至少需要一个联系人");
    } else {
        long primaryCount = contacts.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()))
                .count();

        if (primaryCount == 0) {
            v.field("主联系人").getErrors()
             .add("必须指定一个主联系人");
        } else if (primaryCount > 1) {
            v.field("主联系人").getErrors()
             .add("只能有一个主联系人（当前有 " + primaryCount + " 个）");
        }

        // 主联系人手机号不能重复
        Set<String> phones = new HashSet<>();
        for (ContactPersonDTO c : contacts) {
            if (c.getPhone() != null && !phones.add(c.getPhone())) {
                v.field("联系人手机号").getErrors()
                 .add("联系人手机号重复：" + c.getPhone());
            }
        }
    }

    if (!v.passed()) {
        throw new CRMValidationException(v.getErrors());
    }
}
```

### 4.5 地址信息：省市区 + 邮编 + 详细地址

| 字段 | 校验要点 | ValidX 注解 |
|------|---------|------------|
| 省份 | 中文字符 | `@Chinese` |
| 城市 | 中文字符 | `@Chinese` |
| 区县 | 中文字符 | `@Chinese` |
| 邮政编码 | 6 位数字 | `@ChineseZipCode` |
| 详细地址 | 中文 + 字母 + 数字 + 括号等 | `@ChineseAlphaDash` |

```java
@Data
public class AddressDTO {

    @NotBlank(message = "省份不能为空")
    @Chinese(message = "省份格式不正确")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Chinese(message = "城市格式不正确")
    private String city;

    @Chinese(message = "区县格式不正确")
    private String district;

    @ChineseZipCode(message = "邮政编码格式不正确")
    private String zipCode;

    @NotBlank(message = "详细地址不能为空")
    @ChineseAlphaDash(message = "详细地址格式不正确")
    private String detailAddress;
}
```

> **注意**：省市区三级联动通常由前端控件保证合法性，后端只需做**格式校验**（中文字符）。如果需要校验"省份 + 城市 + 区县"是否属于合法的行政区划组合，那是**业务规则**（需要查行政区划表），不是格式规则。

---

## 五、企业客户 vs 个人客户：统一建模方案

这是本文的**架构核心**部分。CRM 系统最大的特殊性在于：两类客户共用一套接口，但校验规则完全不同。如何在"统一"和"差异"之间取得平衡？

### 5.1 为什么不能"两套 DTO"

最简单的想法是：企业客户和个人客户各写一套 DTO、各写一套校验逻辑。

**问题**：

```java
// 反例：两套接口
@PostMapping("/enterprise")
public Result createEnterprise(@Valid EnterpriseCustomerDTO dto) { ... }

@PostMapping("/individual")
public Result createIndividual(@Valid IndividualCustomerDTO dto) { ... }
```

- 前端需要调两个不同的接口，但客户列表、搜索、合同管理等功能又要统一处理；
- 数据库里客户表是一张表还是两张表？一张表的话，大量 nullable 字段；两张表的话，关联查询复杂；
- 客户类型可能**变更**（个人客户升级为企业客户）——数据迁移成本高。

### 5.2 统一 DTO + 分支校验

**推荐方案**：一个 DTO 包含所有可能的字段，根据 `customerType` 分支校验。

```java
@Data
public class CustomerDTO {

    // ==================== 通用字段 ====================

    @NotBlank(message = "客户类型不能为空")
    @In({"ENTERPRISE", "INDIVIDUAL"}, message = "客户类型不在合法范围内")
    private String customerType;

    @NotBlank(message = "客户名称不能为空")
    @Size(min = 2, max = 100, message = "客户名称长度需在 2-100 字之间")
    private String customerName;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    // ==================== 企业客户专有字段 ====================

    @UnifiedSocialCreditCode(message = "统一社会信用代码格式不正确")
    private String creditCode;          // 企业客户必填

    @ChineseName(message = "法定代表人姓名格式不正确")
    private String legalRepName;        // 企业客户必填

    @ChineseIdCard(message = "法人身份证号格式不正确")
    private String legalRepIdCard;      // 企业客户必填

    @BankCard(message = "银行账号格式不正确")
    private String bankAccountNo;       // 企业客户必填

    @In({"GENERAL_TAXPAYER", "SMALL_SCALE_TAXPAYER"})
    private String taxpayerType;        // 企业客户必填

    // ==================== 个人客户专有字段 ====================

    @ChineseIdCard(message = "身份证号格式不正确")
    private String idCard;              // 个人客户必填

    @PastDate(includeToday = true, message = "出生日期不能是未来日期")
    @Date(pattern = "yyyy-MM-dd")
    private String birthDate;           // 个人客户选填

    @WeChat(message = "微信号格式不正确")
    private String wechat;              // 个人客户选填

    // ==================== 通用业务字段 ====================

    @In({"NORMAL", "SILVER", "GOLD", "PLATINUM", "DIAMOND"})
    private String customerLevel;

    @In({"PROSPECT", "ACTIVE", "INACTIVE", "CHURNED", "BLACKLISTED"})
    private String customerStatus;

    @DecimalMin(value = "0.01", message = "合同金额必须大于 0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal contractAmount;

    @Valid
    @Size(min = 1, max = 20, message = "联系人数量需在 1-20 之间")
    private List<ContactPersonDTO> contacts;

    @Valid
    private AddressDTO address;
}
```

**Service 层分支校验**：

```java
@Service
public class CustomerValidationService {

    public void validate(CustomerDTO dto) {
        ValidX v = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

        // ============ 按客户类型分支校验 ============
        if ("ENTERPRISE".equals(dto.getCustomerType())) {
            validateEnterpriseFields(v, dto);
        } else if ("INDIVIDUAL".equals(dto.getCustomerType())) {
            validateIndividualFields(v, dto);
        }

        // ============ 通用校验 ============
        validateCommonFields(v, dto);

        if (!v.passed()) {
            throw new CRMValidationException(v.getErrors());
        }
    }

    private void validateEnterpriseFields(ValidX v, CustomerDTO dto) {
        // 企业客户：专有字段必填
        if (isBlank(dto.getCreditCode())) {
            v.field("统一社会信用代码").getErrors()
             .add("企业客户的统一社会信用代码不能为空");
        } else {
            v.field("统一社会信用代码")
             .isUnifiedSocialCreditCode(dto.getCreditCode());
        }

        if (isBlank(dto.getLegalRepName())) {
            v.field("法定代表人姓名").getErrors()
             .add("企业客户的法定代表人不能为空");
        } else {
            v.field("法定代表人姓名")
             .isChineseName(dto.getLegalRepName());
        }

        if (isBlank(dto.getLegalRepIdCard())) {
            v.field("法人身份证号").getErrors()
             .add("企业客户的法人身份证号不能为空");
        } else {
            v.field("法人身份证号")
             .isChineseIdCard(dto.getLegalRepIdCard());
        }

        if (isBlank(dto.getBankAccountNo())) {
            v.field("银行账号").getErrors()
             .add("企业客户的银行账号不能为空");
        } else {
            v.field("银行账号").isBankCard(dto.getBankAccountNo());
        }

        if (isBlank(dto.getTaxpayerType())) {
            v.field("纳税人类型").getErrors()
             .add("企业客户的纳税人类型不能为空");
        }

        // 企业名称应包含公司类型后缀（业务规则，非格式规则）
        // 如"有限公司"、"股份有限公司"、"集团"等
    }

    private void validateIndividualFields(ValidX v, CustomerDTO dto) {
        // 个人客户：身份证号必填
        if (isBlank(dto.getIdCard())) {
            v.field("身份证号").getErrors()
             .add("个人客户的身份证号不能为空");
        } else {
            v.field("身份证号").isChineseIdCard(dto.getIdCard());
        }

        // 个人客户不应填写企业专有字段
        if (isNotBlank(dto.getCreditCode())) {
            v.field("统一社会信用代码").getErrors()
             .add("个人客户不需要填写统一社会信用代码");
        }
        if (isNotBlank(dto.getLegalRepName())) {
            v.field("法定代表人姓名").getErrors()
             .add("个人客户不需要填写法定代表人");
        }

        // 年龄合规：个人客户必须满 18 岁
        if (isNotBlank(dto.getIdCard())) {
            v.field("年龄").isAge(dto.getIdCard(), 18, 120);
        }

        // 出生日期与身份证一致性
        if (isNotBlank(dto.getIdCard()) && isNotBlank(dto.getBirthDate())) {
            String expectedBirth = dto.getIdCard().substring(6, 10) + "-"
                    + dto.getIdCard().substring(10, 12) + "-"
                    + dto.getIdCard().substring(12, 14);
            if (!dto.getBirthDate().equals(expectedBirth)) {
                v.field("出生日期").getErrors()
                 .add("出生日期与身份证号中的出生日期不一致");
            }
        }
    }

    private void validateCommonFields(ValidX v, CustomerDTO dto) {
        // 合同金额 > 0
        if (dto.getContractAmount() != null
                && dto.getContractAmount().compareTo(BigDecimal.ZERO) <= 0) {
            v.field("合同金额").getErrors()
             .add("合同金额必须大于 0");
        }

        // 联系人列表
        if (dto.getContacts() != null) {
            validatePrimaryContact(v, dto.getContacts());
        }
    }

    private void validatePrimaryContact(ValidX v,
                                         List<ContactPersonDTO> contacts) {
        long primaryCount = contacts.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()))
                .count();
        if (primaryCount == 0) {
            v.field("主联系人").getErrors().add("必须指定一个主联系人");
        } else if (primaryCount > 1) {
            v.field("主联系人").getErrors()
             .add("只能有一个主联系人");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isNotBlank(String s) {
        return !isBlank(s);
    }
}
```

### 5.3 继承体系方案

另一种方案是用**继承**来区分两类客户：

```java
@Data
public abstract class BaseCustomerDTO {
    @NotBlank(message = "客户名称不能为空")
    @Size(min = 2, max = 100)
    private String customerName;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;

    @Email
    private String email;

    @In({"NORMAL", "SILVER", "GOLD", "PLATINUM", "DIAMOND"})
    private String customerLevel;

    @DecimalMin(value = "0.01")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal contractAmount;

    @Valid
    private List<ContactPersonDTO> contacts;

    @Valid
    private AddressDTO address;
}

@Data
@EqualsAndHashCode(callSuper = true)
public class EnterpriseCustomerDTO extends BaseCustomerDTO {

    @NotBlank(message = "统一社会信用代码不能为空")
    @UnifiedSocialCreditCode(message = "统一社会信用代码格式不正确")
    private String creditCode;

    @NotBlank(message = "法定代表人姓名不能为空")
    @ChineseName
    private String legalRepName;

    @NotBlank(message = "法人身份证号不能为空")
    @ChineseIdCard
    private String legalRepIdCard;

    @BankCard
    private String bankAccountNo;

    @NotBlank
    @In({"GENERAL_TAXPAYER", "SMALL_SCALE_TAXPAYER"})
    private String taxpayerType;
}

@Data
@EqualsAndHashCode(callSuper = true)
public class IndividualCustomerDTO extends BaseCustomerDTO {

    @NotBlank(message = "身份证号不能为空")
    @ChineseIdCard(message = "身份证号格式不正确")
    private String idCard;

    @PastDate(includeToday = true)
    @Date(pattern = "yyyy-MM-dd")
    private String birthDate;

    @WeChat
    private String wechat;
}
```

**Controller 层使用 `@JsonTypeInfo` 实现多态反序列化**：

```java
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public Result<CustomerVO> create(
            @Valid @RequestBody BaseCustomerDTO dto) {
        // Spring 会根据 customerType 自动反序列化为对应的子类
        // @Valid 会触发子类自己的注解校验
        return Result.success(customerService.create(dto));
    }
}
```

### 5.4 两种方案的对比

| 维度 | 统一 DTO + 分支校验 | 继承体系 + 多态校验 |
|------|-------------------|-------------------|
| **代码量** | 一个 DTO，Service 层分支多 | 三个 DTO，Service 层简洁 |
| **注解利用率** | 低（很多字段按类型可为空） | 高（每个注解都在该类型下必填） |
| **前端对接** | 一个接口，字段多 | 需要 `@JsonTypeInfo` 配置 |
| **数据库映射** | 一张表，大量 nullable | 可以用 JPA 继承映射 |
| **类型切换** | 改一个字段值即可 | 需要数据迁移 |
| **团队门槛** | 低（简单直白） | 中（需要理解多态序列化） |
| **推荐场景** | 中小项目、快速迭代 | 大项目、DDD 建模 |

**实战建议**：

- 如果团队规模小、业务变化快，用**统一 DTO + 分支校验**——简单、灵活、改起来快；
- 如果团队规模大、需要严格建模，用**继承体系**——类型安全、注解利用率高、符合 DDD 思想；
- 如果客户类型可能**频繁切换**（个人 → 企业），统一 DTO 更方便（改一个字段就行，不需要迁移数据）。

---

## 六、完整实现：CRM DTO + Service

### 6.1 Controller

```java
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerValidationService validationService;
    @Autowired
    private CustomerService customerService;

    /** 创建客户 */
    @PostMapping
    public Result<CustomerVO> create(@RequestBody CustomerDTO dto) {
        // 第一层：跨字段校验（类型分支）
        validationService.validate(dto);
        // 第二层：业务处理
        return Result.success(customerService.create(dto));
    }

    /** 更新客户 */
    @PutMapping("/{id}")
    public Result<CustomerVO> update(@PathVariable String id,
                                      @RequestBody CustomerDTO dto) {
        validationService.validate(dto);
        return Result.success(customerService.update(id, dto));
    }

    /** 签署合同 */
    @PostMapping("/{id}/contract")
    public Result<ContractVO> signContract(
            @PathVariable String id,
            @Valid @RequestBody ContractDTO contract) {
        // 合同时间线校验
        validationService.validateContractTimeline(contract);
        return Result.success(customerService.signContract(id, contract));
    }
}
```

### 6.2 客户去重校验

CRM 系统的一个经典需求：**防止重复录入客户**。

```java
@Service
public class CustomerDeduplicationService {

    /**
     * 企业客户去重：统一社会信用代码唯一
     */
    public void checkEnterpriseDuplicate(String creditCode,
                                          CustomerRepository repo) {
        if (repo.existsByCreditCode(creditCode)) {
            throw new CRMValidationException(
                "该企业客户已存在（统一社会信用代码：" + creditCode + "）");
        }
    }

    /**
     * 个人客户去重：手机号 + 身份证号唯一
     */
    public void checkIndividualDuplicate(String phone, String idCard,
                                          CustomerRepository repo) {
        if (repo.existsByPhoneAndIdCard(phone, idCard)) {
            throw new CRMValidationException(
                "该个人客户已存在（手机号：" + phone + "）");
        }
    }
}
```

> **注意**：去重是**业务规则**（需要查数据库），不是格式校验。但它和格式校验紧密相关——如果统一社会信用代码格式不正确（没通过 `@UnifiedSocialCreditCode`），就不应该进入去重查询环节（否则可能误判）。**校验顺序**应该是：格式校验 → 去重校验 → 业务处理。

---

## 七、常见坑与规避

### 7.1 坑一：企业客户和个人客户的必填字段搞反

**现象**：个人客户注册时，系统要求填写"统一社会信用代码"——个人客户哪来的信用代码？

**根因**：DTO 里所有字段都标了 `@NotBlank`，没有按类型区分。

**规避**：
- 用**统一 DTO + 分支校验**（本文方案），在 Service 层按 `customerType` 判断哪些字段必填；
- 或者用**继承体系**，让每个子类的注解自然表达必填性；
- **不要**在一个 DTO 里对所有字段都标 `@NotBlank`。

### 7.2 坑二：统一社会信用代码含禁用字符

**现象**：`91110000MA01ABCDEI` 被 `@UnifiedSocialCreditCode` 拒绝——因为末位是 `I`。

**根因**：统一社会信用代码的字符集**去掉了 I、O、S、V、Z** 五个易混淆字符。如果从外部系统（如工商数据库）导入的数据包含这些字符，说明数据本身有问题。

**规避**：
- 数据导入时做**字符清洗**（去除空格、替换全角字符）；
- 如果外部数据确实包含 `I`（可能是 `1` 的 OCR 误识别），在导入层做纠正，不要让格式校验器背锅；
- 在错误提示中明确说明"信用代码不能包含 I、O、S、V、Z"。

### 7.3 坑三：企业名称用了 @Chinese

**现象**：`"北京XX科技有限公司(海淀分公司)"` 被 `@Chinese` 拒绝——因为括号不是中文字符。

**根因**：`@Chinese` 只允许纯中文字符（Unicode 汉字），不允许括号、数字、字母。

**规避**：
- 用 `@ChineseAlphaDash`（中文 + 字母 + 数字 + 下划线 + 横线）做宽松校验；
- 或用 `@Pattern` 精确控制允许的字符集（见 2.2 节）；
- 不要用 `@Chinese` 校验企业名称。

### 7.4 坑四：个人客户签合同时没校验年龄

**现象**：16 岁的未成年人签了购房合同——合同无效。

**根因**：没有校验个人客户的年龄是否满足签约条件。

**规避**：
- 个人客户签合同前，用 `@Age(min = 18)` 校验年龄；
- 或从身份证号提取出生日期，手动计算年龄（见 3.3 节）；
- 18 岁是**签合同**的法定年龄，某些特殊场景（如保险）可能要求更高。

### 7.5 坑五：客户类型变更后校验规则没跟着变

**现象**：个人客户升级为企业客户后，系统仍然要求填"身份证号"而不是"统一社会信用代码"。

**根因**：客户类型变更后，校验分支没更新——可能是缓存了旧的校验逻辑，或者前端表单没刷新。

**规避**：
- 后端校验**始终根据当前请求的 `customerType`** 分支，不依赖历史数据；
- 前端表单在切换客户类型时**清空不适用的字段**；
- 客户类型变更记录审计日志（谁在什么时候把类型从 A 改成了 B）。

### 7.6 坑六：合同金额用了 double

**现象**：合同金额 `123456789.12` 入库后变成了 `123456789.119999999`。

**根因**：用 `double` 存储金额，浮点精度丢失。

**规避**：
- DTO 用 `BigDecimal`；
- 数据库用 `DECIMAL(14, 2)`；
- 计算用 `BigDecimal` 的 `multiply`、`add`，不用 `*`、`+`。

### 7.7 坑七：联系人列表为空时没校验

**现象**：客户创建成功但没有联系人——后续业务流程（如发送合同）找不到联系人。

**根因**：`@Size(min = 1)` 在 `contacts` 为 `null` 时不会触发（null 不等于空列表）。

**规避**：
- 用 `@NotNull` + `@Size(min = 1)` 组合，确保列表不为 null 且至少有一个元素；
- 或在 Service 层做显式判断。

### 7.8 坑八：企业客户开户名和企业名称不一致

**现象**：企业名称是"北京XX科技有限公司"，银行开户名是"北京XX科技公司"——少了一个"有限"，导致打款失败。

**根因**：没有做开户名与企业名称的一致性校验。

**规避**：
- 在 Service 层做**开户名 = 企业名称**的校验（见 2.3 节）；
- 如果允许简称，至少做**关键词包含**校验（开户名包含企业名称的核心部分）。

---

## 八、总结

CRM 系统的客户验证是"两类客户、一套规则"的典型。本文把核心内容梳理如下：

### 核心知识点回顾

| 模块 | 关键结论 |
|------|---------|
| **企业客户标识** | 统一社会信用代码（18 位，mod 31），用 `@UnifiedSocialCreditCode` |
| **信用代码信息** | 行政区划段与身份证同源（GB/T 2260），可复用注册表 |
| **企业名称** | 含括号、数字，**不能用 `@Chinese`**，用 `@ChineseAlphaDash` 或 `@Pattern` |
| **个人客户标识** | 身份证号（18 位，mod 11），用 `@ChineseIdCard` |
| **个人联系方式** | `@ChinesePhone` + `@Email` + `@WeChat` + `@QQ` |
| **合同日期** | 签署 ≤ 开始 < 结束，跨字段校验 |
| **合同金额** | `BigDecimal` + `compareTo`，不用 `double` + `equals` |
| **联系人** | 必须有且只有一个主联系人，手机号不能重复 |
| **客户去重** | 企业按信用代码、个人按手机号 + 身份证号 |
| **核心架构** | 统一 DTO + 分支校验 vs 继承体系 + 多态校验 |

### ValidX 在本场景的使用矩阵

| 需求 | 推荐用法 |
|------|---------|
| 客户类型 | `@In({"ENTERPRISE", "INDIVIDUAL"})` |
| 企业信用代码 | `@UnifiedSocialCreditCode` |
| 个人身份证 | `@ChineseIdCard` |
| 法人姓名 | `@ChineseName` |
| 企业座机 | `@ChineseLandline` |
| 个人手机 | `@ChinesePhone` |
| 手机或座机 | `@ChinesePhoneOrLandline` |
| 银行账号 | `@BankCard` |
| SWIFT 代码 | `@SWIFT` |
| IBAN | `@IBAN` |
| 纳税人资质 | `@In({"GENERAL_TAXPAYER", "SMALL_SCALE_TAXPAYER"})` |
| 营业执照有效期 | `@Date` + `@FutureDate` |
| 合同日期 | `@PastDate` + `@Date` |
| 合同金额 | `@DecimalMin` + `@Digits` |
| 折扣率 | `@DecimalMin("0")` + `@DecimalMax("1")` |
| 客户等级/状态 | `@In({...})` |
| 联系人姓名 | `@ChineseName` |
| 联系人手机 | `@ChinesePhone` |
| 邮编 | `@ChineseZipCode` |
| 详细地址 | `@ChineseAlphaDash` |
| 微信号 | `@WeChat` |
| QQ 号 | `@QQ` |
| 年龄合规 | `@Age(min = 18)` |
| 类型 → 标识一致性 | **链式 API**（跨字段分支校验） |
| 合同时间线 | **链式 API**（跨字段日期比较） |
| 开户名一致性 | **链式 API**（跨字段字符串比较） |
| 客户去重 | **Service 层**（数据库查询） |

**最关键的一条设计原则**：

> **客户类型决定校验规则——统一 DTO 里用分支校验，继承体系里用多态校验。两种方案各有利弊，根据团队规模和业务复杂度选择。**
---

## 项目地址

- GitHub: <https://github.com/vipxieliang/validx>
- Gitee: <https://gitee.com/vipxieliang/validx>
