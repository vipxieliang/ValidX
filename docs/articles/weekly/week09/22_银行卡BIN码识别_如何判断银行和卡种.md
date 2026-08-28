# 银行卡BIN码识别：如何判断银行和卡种

## 📋 目录
- [引言](#引言)
- [一、BIN 码是什么](#一bin-码是什么)
- [二、银行卡号的结构：MII + BIN + 账号 + 校验位](#二银行卡号的结构mii--bin--账号--校验位)
- [三、如何判断卡组织](#三如何判断卡组织)
- [四、如何判断银行和卡种](#四如何判断银行和卡种)
- [五、匹配实现：四种算法选型](#五匹配实现四种算法选型)
- [六、与 ValidX 结合：校验与识别各司其职](#六与-validx-结合校验与识别各司其职)
- [七、实战：一个完整的 BIN 识别工具类](#七实战一个完整的-bin-识别工具类)
- [八、边界与坑](#八边界与坑)
- [总结](#总结)

---

## 引言

支付表单、绑卡页面、收银台……几乎每个金融场景都要面对一张银行卡号。

输入框里敲下 16 位卡号，你往往需要立刻知道三件事：

1. **卡号格式对不对**？是不是手滑多敲了一位（Luhn 校验位兜底）
2. **哪个银行发的**？显示"中国工商银行"而不是"未知卡号"
3. **什么卡种**？借记卡还是信用卡？Visa、MasterCard 还是银联？

第 1 件事靠 **Luhn 算法**（ValidX 的 `@BankCard` 已内置），第 2、3 件事靠 **BIN 码识别**。本文拆解 BIN 码的原理与实现，并给出与 ValidX 组合使用的完整方案。

---

## 一、BIN 码是什么

**BIN（Bank Identification Number，银行识别码）** 是银行卡号的开头 **6 位数字**，用来标识发卡机构。它由 ISO 组织统一分配，标准依据是 **ISO/IEC 7812**。

| 概念 | 全称 | 位数 | 作用 |
|------|------|------|------|
| MII | Major Industry Identifier（主要行业标识符） | 1 位 | 标识发卡行业（金融/航空/旅游…） |
| IIN / BIN | Issuer Identification Number（发卡机构标识） | 6 位 | 标识具体发卡行 |
| 个人账号 | Individual Account Number | 6-12 位 | 银行内部账号 |
| 校验位 | Check Digit | 1 位 | Luhn 算法校验 |

看一张真实卡号 `6228480402564890018` 的拆解：

```
6   228480   40256489   001   8
│   │        │          │    └── 校验位（Luhn）
│   │        │          └─────── 账号
│   │        └────────────────── 银行内部账号（可变长度）
│   └─────────────────────────── BIN（前 6 位 = 农行金穗借记卡）
└─────────────────────────────── MII = 6（金融业 / 银联）
```

> 注：BIN 严格定义是前 6 位，但 2017 年起 ISO 标准允许 IIN 扩展到 8 位。国内银联卡实际分配以 6 位为主，识别时取前 6 位即可覆盖绝大多数场景。

**为什么是"前 6 位"？** 因为发卡行给卡号编号时有严格的前缀分配制度——每家银行的每种卡产品都锁定了固定的 BIN 段。比如农行的金穗借记卡固定以 `622848` 开头，看到这 6 位就知道是农行。

---

## 二、银行卡号的结构：MII + BIN + 账号 + 校验位

对照上面那张拆解图，逐段说清楚：

**① MII（第 1 位）—— 先判断大行业**

ISO/IEC 7812 规定了第一位数字的行业归属：

| MII | 行业 | 代表卡组织 |
|-----|------|-----------|
| 1、2 | 航空业 | UATP |
| 3 | 旅游、娱乐 | Amex（34/37）、Diners（36/38） |
| 4 | 银行业 | Visa |
| 5 | 银行业 | MasterCard（51-55） |
| 6 | 银行业、商品 | Discover、**中国银联（62）** |
| 9 | 国内分配 | 银联早期 955 段 |

看到 `4` 开头就知道大概率是 Visa，看到 `62` 开头就是银联——这就是"先按 MII 分行业，再按 BIN 分机构"的两级识别思路。

**② BIN（第 2-7 位）—— 锁死发卡行和卡种**

同一家银行的不同 BIN 段代表不同卡产品，这是"卡种识别"的根据。例如：

- 工行 `622202` → 牡丹灵通卡（借记卡）
- 工行 `622230` → 牡丹信用卡（贷记卡）
- 招行 `622588` → 一卡通（借记卡）
- 招行 `439188` → 信用卡（Visa）

**③ 账号 + 校验位 —— 银行内部逻辑，识别时不用关心**，但校验位必须参与格式校验（Luhn）。

---

## 三、如何判断卡组织

只靠**前 2-4 位**就能判断卡组织（卡品牌），这是 BIN 识别最基础的一层：

```java
public enum CardBrand {
    VISA, MASTERCARD, AMEX, DINERS, JCB, UNIONPAY, DISCOVER, UNKNOWN
}

public static CardBrand detectBrand(String cardNumber) {
    if (cardNumber == null) return CardBrand.UNKNOWN;
    String p = cardNumber.replaceAll("[\\s-]", "");
    if (p.startsWith("4"))              return CardBrand.VISA;         // 4 开头
    if (p.startsWith("51") || p.startsWith("52")
        || p.startsWith("53") || p.startsWith("54") || p.startsWith("55"))
                                        return CardBrand.MASTERCARD;   // 51-55
    if (p.startsWith("34") || p.startsWith("37"))
                                        return CardBrand.AMEX;         // 34/37
    if (p.startsWith("36") || p.startsWith("38"))
                                        return CardBrand.DINERS;       // 36/38
    if (p.startsWith("35"))             return CardBrand.JCB;          // 35
    if (p.startsWith("62"))             return CardBrand.UNIONPAY;     // 62 银联
    if (p.startsWith("6011") || p.startsWith("65"))
                                        return CardBrand.DISCOVER;
    return CardBrand.UNKNOWN;
}
```

关键规律只有一条：**银联 = `62` 开头**（16-19 位）。判断银联卡，`startsWith("62")` 就够了。

---

## 四、如何判断银行和卡种

卡组织是"大标签"，银行和卡种是"精确标签"。这层就要靠 **BIN 前缀表**。

### 1. 常见银行 BIN 速查表

以下是中国大陆常见银行的借记卡 BIN 前缀（**示例，非完整清单**）：

| 银行 | 常见 BIN 前缀 | 卡种示例 |
|------|--------------|---------|
| 中国工商银行 | `622202` `622200` | 牡丹灵通卡（借记卡） |
| 中国建设银行 | `621700` `622700` `436742` | 龙卡通（借记卡） |
| 中国农业银行 | `622848` `622845` | 金穗借记卡 |
| 中国银行 | `621661` `622260` `622262` | 长城电子借记卡 |
| 招商银行 | `622588` `621485` | 一卡通（借记卡） |
| 交通银行 | `622258` `622259` | 太平洋卡（借记卡） |
| 中国邮政储蓄银行 | `621098` `622188` | 绿卡（借记卡） |
| 兴业银行 | `622908` `622909` | 借记卡 |
| 浦发银行 | `622521` `622522` | 借记卡 |
| 中信银行 | `622690` `622691` | 借记卡 |
| 民生银行 | `622622` `622623` | 借记卡 |
| 光大银行 | `622662` `622663` | 借记卡 |
| 平安银行 | `622155` `622156` | 借记卡 |

> ⚠️ 表中前缀仅用于理解原理。**真实生产环境必须维护完整 BIN 库**（可用银联官方 BIN 表，约数万条记录），否则会大量误判。

### 2. 借记卡 vs 信用卡：怎么区分

卡种识别**不能靠卡号长度**（借记卡和信用卡都是 16/19 位，长度无法区分），必须查 BIN 表。BIN 表每条记录应包含"卡品牌 + 卡种"两个字段：

```java
// 同一银行的两个 BIN，卡种不同
622202 → 工商银行 / 借记卡（牡丹灵通卡）
622230 → 工商银行 / 贷记卡（牡丹信用卡）
```

卡号 `6222021234567890` 和 `6222301234567890` 只差 6 位，卡种截然不同——这就是"为什么要精确匹配 BIN 而不是匹配前 4 位"的原因。

### 3. 匹配规则：最长前缀优先

`622848` 同时是 `62`、`6228`、`622848` 的前缀，识别时必须**取最长匹配**，否则会把农行卡误判成"银联/未知银行"：

```java
// 错误：先匹配 62 就停 → 只能判断出"银联"
// 正确：遍历所有 BIN，取能匹配的最长前缀 → 判断出"农行金穗卡"
```

---

## 五、匹配实现：四种算法选型

BIN 表规模从小到大的四种实现：

| 方案 | 适用规模 | 复杂度 | 特点 |
|------|---------|--------|------|
| **线性遍历** | < 100 条 | O(n) | 最简单，正则逐条匹配，适合教学 |
| **HashMap 精确匹配** | 任意 | O(1) | 将卡号前 6 位截出直接查表，仅适用等长 BIN |
| **前缀树（Trie）** | > 1000 条 | O(k)，k=卡号长度 | 天然支持变长前缀，取最长匹配，工业首选 |
| **二分查找** | 排序表 | O(log n) | 前缀区间排序后二分，省内存 |

**工业实现推荐 Trie 或 HashMap**。银联官方 BIN 表约 4 万条，Trie 的查询耗时与 BIN 数量无关（只跟卡号长度有关），且天然解决"最长前缀"问题。

HashMap 版本最直观（BIN 等长时）：

```java
Map<String, BankInfo> binMap = new HashMap<>();
binMap.put("622848", new BankInfo("中国农业银行", "借记卡"));

// 识别：截前 6 位查表
String bin = cleanValue.substring(0, Math.min(6, cleanValue.length()));
BankInfo info = binMap.get(bin);
```

Trie 版本的核心思路：把每个 BIN 作为路径插入字典树，查询时沿卡号逐字符向下走，**记录最后一个命中银行节点的深度**，即为最长匹配。

---

## 六、与 ValidX 结合：校验与识别各司其职

很多人会混淆两件事：

| 任务 | 算法 | ValidX 对应能力 |
|------|------|----------------|
| **卡号格式校验**（是否可被发卡行接受） | Luhn 校验位 + 长度 + 纯数字 | `@BankCard` 注解 / 链式 `isBankCard()` |
| **银行卡种识别**（哪家银行、什么卡） | BIN 前缀匹配 | 无内置 BIN 库，需自定义实现 |

**ValidX 的 `@BankCard` 只做校验、不做识别**，看源码（`BankCardValidator.java`）就明白：

```java
public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
        return true; // 空值处理交给@NotNull等其他注解处理
    }
    // 移除所有空格和连字符
    String cleanValue = value.replaceAll("[\\s-]", "");
    // 检查是否全部为数字
    if (!cleanValue.matches("\\d+")) {
        return false;
    }
    // 检查长度是否符合银行卡号规范（通常为13-19位）
    if (cleanValue.length() < 13 || cleanValue.length() > 19) {
        return false;
    }
    // 使用Luhn算法验证银行卡号
    return isLuhnValid(cleanValue);
}
```

处理流程清晰：**去空格连字符 → 纯数字 → 13-19 位 → Luhn**。校验通过不代表卡一定存在，但校验失败一定无效——这是 BIN 识别的前置关卡。

正确的组合链路是**先校验、后识别**：

```
用户输入卡号
   │
   ▼
① @BankCard / isBankCard()   ──失败──→ 提示"卡号格式不正确"
   │ 通过
   ▼
② BIN 识别（自定义）          ──未知──→ 显示"该卡号暂不支持"
   │ 命中
   ▼
③ 返回 { 银行, 卡种, 卡组织 }
```

校验保证"号码本身合法"，识别负责"告诉用户这是什么卡"，两者互补、缺一不可。

---

## 七、实战：一个完整的 BIN 识别工具类

结合 ValidX 链式 API，给出一个可直接运行的完整示例：

```java
import io.github.vipxieliang.validx.chain.ValidX;

import java.util.HashMap;
import java.util.Map;

/**
 * 银行卡识别工具：先 Luhn 校验，再 BIN 识别
 */
public class BankCardInfoUtil {

    /** BIN 表：前缀 -> (银行, 卡种, 卡组织) */
    private static final Map<String, CardInfo> BIN_TABLE = new HashMap<>();

    static {
        BIN_TABLE.put("622202", new CardInfo("中国工商银行", "借记卡", "银联"));
        BIN_TABLE.put("622230", new CardInfo("中国工商银行", "贷记卡", "银联"));
        BIN_TABLE.put("621700", new CardInfo("中国建设银行", "借记卡", "银联"));
        BIN_TABLE.put("622848", new CardInfo("中国农业银行", "借记卡", "银联"));
        BIN_TABLE.put("622588", new CardInfo("招商银行", "借记卡", "银联"));
        BIN_TABLE.put("621098", new CardInfo("中国邮政储蓄银行", "借记卡", "银联"));
        BIN_TABLE.put("401288", new CardInfo("Visa 测试卡", "贷记卡", "Visa"));
        BIN_TABLE.put("542523", new CardInfo("MasterCard 测试卡", "贷记卡", "MasterCard"));
    }

    public static CardInfo parse(String cardNumber) {
        // ① 先用 ValidX 做 Luhn 校验，不合法直接返回 null
        if (!ValidX.init().isBankCard(cardNumber).passed()) {
            return null;
        }
        // ② 归一化后取前 6 位查 BIN 表
        String clean = cardNumber.replaceAll("[\\s-]", "");
        String bin = clean.substring(0, Math.min(6, clean.length()));
        return BIN_TABLE.get(bin);
    }

    public static void main(String[] args) {
        // 合法且 BIN 命中
        System.out.println(parse("6228480402564890018")); // 中国农业银行/借记卡/银联
        // 合法但 BIN 未收录
        System.out.println(parse("6227008888888888888")); // null（Luhn 通过，BIN 表没有）
        // Luhn 校验失败（改错一位校验位）
        System.out.println(parse("6228480402564890019")); // null
        // 长度不足
        System.out.println(parse("12345"));               // null
    }

    static class CardInfo {
        final String bankName, cardType, brand;
        CardInfo(String bankName, String cardType, String brand) {
            this.bankName = bankName; this.cardType = cardType; this.brand = brand;
        }
        @Override public String toString() {
            return bankName + "/" + cardType + "/" + brand;
        }
    }
}
```

**示例输出**：

```
中国农业银行/借记卡/银联
null
null
null
```

三个关键点：

1. **`isBankCard()` 返回 `ValidX` 对象，`passed()` 判断结果**——链式 API 天然适合"先校验后识别"的编排
2. **识别失败返回 `null`**，前端可降级显示"该卡号暂不支持"，而不是直接拒绝——BIN 表永远在增长，别把"未收录"当"非法"
3. **测试卡号也放进 BIN 表**：`401288`（Visa 测试）、`542523`（MasterCard 测试）与 ValidX 测试用例中的卡号呼应，便于联调

### 进阶：注解方式识别

BIN 识别不依赖 Bean Validation，但你可以把它包成自定义注解，与 `@BankCard` 叠加使用：

```java
@BankCard
@BankCardInfo(bank = "中国农业银行")   // 自定义约束：Luhn 通过后再查 BIN
private String cardNumber;
```

自定义注解的 `ConstraintValidator` 内部直接调用 `BankCardInfoUtil.parse()` 判断银行是否匹配，`@BankCard` 与 `@BankCardInfo` 各自职责单一，任一失败都会产生违规。

---

## 八、边界与坑

### 1. BIN 表有时效性，必须定期更新
新卡种、新 BIN 段每年都在增加，一次入库管终身必然误判。生产建议：银联官方 BIN 表 + 每月增量更新 + 未命中时记录日志供分析。

### 2. 别用"卡号长度"判断卡种
16 位既有借记卡也有信用卡，长度判断必然出错。**卡种只能查 BIN 表**。

### 3. 最长前缀匹配
`622848` 同时命中 `62`（银联）、`6228`、`622848`（农行），必须取最长。用 Trie 或"先排长度降序再逐条 startsWith"都能解决。

### 4. 空格、连字符必须先行归一化
用户输入的卡号常带 `-` 或空格（ValidX 测试用例 `"5425-2334-3010-9903"` 就是典型），识别前必须 `replaceAll("[\\s-]", "")`。注意顺序：**先归一化，再取 BIN**。

### 5. 测试卡号与真实卡号
`4012888888881881`（Visa 测试卡）、`5555555555554444`（MasterCard 测试卡）、`4532015112830366` 等是公开测试卡号，Luhn 合法但没有真实银行账户。别把它们当"有效卡"放行支付，BIN 识别应加"测试卡号"黑名单。

### 6. "未收录"不等于"非法"
BIN 表命中失败，应提示"暂不支持该卡"而非"卡号错误"。Luhn 校验失败才是"卡号错误"——两件事信号含义不同，错误提示也别混。

### 7. 私有 BIN 与预发卡
部分银行内部卡（如特定行业卡、内部结算卡）的 BIN 不在公开表内。如果业务需要覆盖，需向银行或银联申请私有 BIN 段说明。

---

## 总结

| 层级 | 输入 | 输出 | 算法 | ValidX 支持 |
|------|------|------|------|------------|
| 卡组织识别 | 卡号前 2-4 位 | Visa / MasterCard / 银联… | MII + 前缀规则 | ❌（需自定义，极简单） |
| 银行识别 | 卡号前 6 位 | 具体银行 | BIN 前缀表匹配 | ❌（需自定义 + BIN 库） |
| 卡种识别 | BIN 表记录 | 借记卡 / 贷记卡 | BIN 表字段查询 | ❌（需自定义 + BIN 库） |
| 格式校验 | 整张卡号 | 是否合法 | Luhn + 长度 + 纯数字 | ✅ `@BankCard` / `isBankCard()` |

一句话总结：

- **`@BankCard` 回答"这个号码写对没有"**（Luhn 校验，ValidX 已内置）
- **BIN 识别回答"这是什么卡"**（前缀匹配，自己维护 BIN 表）

两者组合，就能在绑卡页面上做到"输入即识别、错误即拦截"的完整体验。BIN 识别本身不复杂——本质就是**截前 6 位、查表、取最长前缀**，真正的工程量在于维护一份完整且新鲜的 BIN 库。
