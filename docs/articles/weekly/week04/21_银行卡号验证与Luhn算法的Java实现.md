# 银行卡号验证与Luhn算法的Java实现

## 引言

"绑卡"是电商、支付、理财类应用的标配功能。用户输入 16 位银行卡号时，如果手一抖打错一位，系统是等到银行接口报"卡号不存在"才返回错误，还是能在**前端/本地**就拦截下来？

业界标准答案是 Luhn 算法（也叫模 10 算法）：它在**不查任何数据库**的前提下，仅凭卡号本身就能识别绝大多数录入错误。1954 年，IBM 工程师 Hans Peter Luhn 申请了这项专利，六十年后的今天，全球几乎所有银行卡号、部分手机 IMEI、ISBN-13 书号、甚至一些优惠券码都在用它。

本文不讲"背一段代码"式的速成，而是从算法规则出发，推导它的数学本质，再给出可以直接落地的 Java 实现，最后对照 ValidX 中 `@BankCard` / `isBankCard()` 的真实源码，讲清楚生产环境里银行卡号校验应该怎么做。

---

## 一、Luhn 算法的原理

### 1.1 算法规则

Luhn 校验一个**完整卡号**是否有效，规则只有四条：

1. 从**最右侧的数字（校验位）**开始，从右往左遍历每一位；
2. 位于**第 1、3、5…（从右数）奇数位**的数字**不加倍**，原样累加；
3. 位于**第 2、4、6…（从右数）偶数位**的数字**乘以 2**，若结果超过 9 则**减去 9**，再累加；
4. 所有位累加之和能被 10 整除，则校验通过。

用一句话概括：**"奇数位原样、偶数位加倍、超 9 减 9、总和模 10"**。

### 1.2 手工验算一：经典例子 79927398713

`79927398713` 是 Luhn 算法最常见的教学示例。逐位计算：

| 从右数位置 | 11 | 10 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 数字 | 7 | 9 | 9 | 2 | 7 | 3 | 9 | 8 | 7 | 1 | 3 |
| 处理 | 7 | 9×2=18→9 | 9 | 2×2=4 | 7 | 3×2=6 | 9 | 8×2=16→7 | 7 | 1×2=2 | 3 |

```
和 = 7 + 9 + 9 + 4 + 7 + 6 + 9 + 7 + 7 + 2 + 3 = 70
70 mod 10 = 0  ✅ 校验通过
```

### 1.3 手工验算二：中国银联卡 6222021234567892

62 开头是银联标识，`622202` 是工商银行借记卡前缀。`6222021234567892` 是 16 位完整卡号（最后一位 2 为校验位）：

| 从右数位置 | 16 | 15 | 14 | 13 | 12 | 11 | 10 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 数字 | 6 | 2 | 2 | 2 | 0 | 2 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 2 |
| 处理 | 6×2=12→3 | 2 | 2×2=4 | 2 | 0 | 2×2=4 | 1 | 2×2=4 | 3 | 4×2=8 | 5 | 6×2=12→3 | 7 | 8×2=16→7 | 9×2=18→9 | 2 |

```
和 = 3 + 2 + 4 + 2 + 0 + 4 + 1 + 4 + 3 + 8 + 5 + 3 + 7 + 7 + 9 + 2 = 60
60 mod 10 = 0  ✅ 校验通过
```

> 尝试把最后一位校验位 2 改成 3，和会变成 61，模 10 不为 0——这就是"校验位"的价值：**错一位，立刻暴露**。

---

## 二、Luhn 的数学本质

### 2.1 "加倍后减 9"到底是什么：一张双射表

规则里最反直觉的一步是"乘以 2 后，若超过 9 就减 9"。它其实是在做**数字根（digital root）意义上的翻倍**。把所有可能的输入输出列成表：

| 原数字 a | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
|---|---|---|---|---|---|---|---|---|---|---|
| f(a) = 2a，超 9 减 9 | 0 | 2 | 4 | 6 | 8 | 1 | 3 | 5 | 7 | 9 |

观察这张表，两个关键性质：

1. **f 是双射**（一一对应）：10 个输入得到 10 个互不相同的输出，没有任何两个数字映射到同一个值；
2. **输出恰好是"0~9 中所有偶数 + 所有奇数"的完整排列**。

第一条性质直接决定了检测能力（见 2.4），第二条性质保证了"减 9"和"逐位相加"两种写法完全等价：

```
10 → 1+0 = 1，  10 − 9 = 1   ✔
14 → 1+4 = 5，  14 − 9 = 5   ✔
18 → 1+8 = 9，  18 − 9 = 9   ✔
```

所以 `digit > 9 ? digit - 9 : digit` 与 `digit / 10 + digit % 10` 是同一个操作——这是后面代码优化的数学依据。

### 2.2 为什么从右往左

因为**校验位永远在卡号最右侧**。从右往左编号后，校验位恰好是"第 1 位"（不加倍），它的左侧紧邻位是"第 2 位"（加倍），如此交替。

这带来一个实现上的便利：**生成校验位和验证完整卡号可以共用同一套"奇偶交替"逻辑**，只是起始状态不同（验证从"不加倍"开始，生成从"加倍"开始）。

### 2.3 校验位生成：算法反推

已知卡号主体（去掉最后一位），求校验位 x。把 x 当作 0 参与计算，得到主体部分的和 S，则：

```
校验位 x = (10 − S mod 10) mod 10
```

以 `622202123456789`（15 位主体）生成为例，主体从右往左计算（最右一位 9 处于"加倍"位置）：

| 从右数位置 | 16 | 15 | 14 | 13 | 12 | 11 | 10 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 数字 | 6 | 2 | 2 | 2 | 0 | 2 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
| 处理 | 6×2=12→3 | 2 | 2×2=4 | 2 | 0 | 2×2=4 | 1 | 2×2=4 | 3 | 4×2=8 | 5 | 6×2=12→3 | 7 | 8×2=16→7 | 9×2=18→9 |

```
S = 3+2+4+2+0+4+1+4+3+8+5+3+7+7+9 = 58
x = (10 − 58 mod 10) mod 10 = (10 − 8) mod 10 = 2
```

拼上校验位得到 `6222021234567892`——正是 1.3 节验算通过的那个号码。**验证与生成互为逆运算**，这也是 Luhn 最优雅的地方。

### 2.4 检测能力：能抓住哪些错误

参照身份证校验码那篇的分析框架，把 Luhn 的检测能力精确化：

| 错误类型 | 检测条件 | 检测率 | 说明 |
|---|---|---|---|
| 单字符替换（非加倍位） | `b − a ≢ 0 (mod 10)` | **100%** | `b−a` 非零且绝对值小于 10 |
| 单字符替换（加倍位） | `f(b) − f(a) ≢ 0 (mod 10)` | **100%** | 表 2.1 中 f 是双射，不同输入必得不同输出 |
| 相邻两位交换 | `(a−b)·(权重差) ≢ 0` | ≈ **98%** | 唯一例外：`09 ↔ 90` |
| `09 ↔ 90` 交换 | 恒等于 0 | **0%** | 见下方推导 |
| 增/删一位 | — | 不一定 | 卡号长度变化后可能碰巧仍满足校验 |

**为什么只有 `09 ↔ 90` 检测不出？** 看 2.1 表：`f(0) = 0`，`f(9) = 9`。无论 0 和 9 谁在加倍位：

```
加倍位 0 + 非加倍位 9 = 0 + 9 = 9
加倍位 9 + 非加倍位 0 = 9 + 0 = 9     ← 交换前后贡献完全相同
```

因为 0 和 9 恰好是 2.1 表里"加倍前后不变"的两个数字（`f(0)=0`、`f(9)=9`），所以它们相邻交换时总和不变。这是 Luhn 唯一的结构性盲区，业界公认。

> 对比身份证的 MOD 11-2：素数模 11 让任意单字符替换 100% 可检测；Luhn 用模 10 + 双射变换 f 达到了同样的效果，但代价就是 09/90 这一个小盲区——**10 是合数，f 的双射性只能靠"超 9 减 9"这个非线性操作补回来**。

---

## 三、Java 实现

### 3.1 最小验证实现

把 1.1 的规则直接翻译成代码，全程一次遍历，O(n)：

```java
public static boolean isLuhnValid(String cardNumber) {
    int sum = 0;
    boolean doubleDigit = false;          // 从右数第 1 位不加倍
    for (int i = cardNumber.length() - 1; i >= 0; i--) {
        int digit = Character.getNumericValue(cardNumber.charAt(i));
        if (doubleDigit) {
            digit *= 2;
            if (digit > 9) {
                digit -= 9;               // 超 9 减 9，等价于逐位相加
            }
        }
        sum += digit;
        doubleDigit = !doubleDigit;       // 奇偶交替
    }
    return sum % 10 == 0;
}
```

`Character.getNumericValue()` 而非 `charAt(i) - '0'`：前者对任意 Unicode 数字字符都能正确解析，后者遇到非数字字符会产生无意义结果（见 3.4 的完整版：进入 Luhn 之前已过滤过）。

### 3.2 校验位生成

与验证共享同一套交替逻辑，只是起始状态翻转（校验位左侧第一位要加倍）：

```java
public static int calculateCheckDigit(String cardBody) {
    int sum = 0;
    boolean doubleDigit = true;           // 校验位在最右，其左侧第 1 位加倍
    for (int i = cardBody.length() - 1; i >= 0; i--) {
        int digit = Character.getNumericValue(cardBody.charAt(i));
        if (doubleDigit) {
            digit *= 2;
            if (digit > 9) {
                digit -= 9;
            }
        }
        sum += digit;
        doubleDigit = !doubleDigit;
    }
    return (10 - sum % 10) % 10;          // 公式：x = (10 − S mod 10) mod 10
}
```

用途：生成测试卡号、给已有卡号补校验位、批量造数。

### 3.3 生产级完整实现

真实场景里，用户输入的卡号可能带着空格（"6222 0211 3400 0012"）或连字符，也可能包含字母。生产代码必须**先清洗、再过滤、再校验**：

```java
public class BankCardValidator {

    public static boolean isValid(String value) {
        if (value == null || value.isEmpty()) {
            return false;                 // 空值策略由调用方决定（@NotNull 或其他）
        }

        // 1. 去除空格与连字符：6222 0211 3400 0012 → 6222021134000012
        String clean = value.replaceAll("[\\s-]", "");

        // 2. 必须全部是数字
        if (!clean.matches("\\d+")) {
            return false;
        }

        // 3. 长度符合 ISO/IEC 7812：13 ~ 19 位
        if (clean.length() < 13 || clean.length() > 19) {
            return false;
        }

        // 4. 最后一道防线：Luhn 校验
        return isLuhnValid(clean);
    }

    private static boolean isLuhnValid(String cardNumber) {
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }
}
```

**为什么顺序是"清洗 → 数字检查 → 长度检查 → Luhn"？** 每一步都在用最低成本排除上一类错误：正则过滤非数字 O(n)，长度比较 O(1)，而 Luhn 需要完整遍历——把最贵的计算放在最后，脏数据在更早的阶段就被丢弃。

### 3.4 实现细节与常见坑

| 坑 | 说明 | 正确做法 |
|---|---|---|
| `charAt(i) - '0'` 收到非数字 | 输入含字母时产生乱值，可能误判通过 | 先 `matches("\\d+")`，或直接 `Character.getNumericValue()` |
| 忘记去空格/连字符 | 用户习惯按 4 位一组输入 | `replaceAll("[\\s-]", "")` 预处理 |
| 只做 Luhn 不做长度检查 | 12 位数字串可能恰好满足 Luhn | 先校验长度 13~19 |
| 把"加倍后减 9"写成"除以 2" | 纯属笔误但隐蔽 | 见 2.1 表，用 `digit > 9 ? digit - 9 : digit` |
| 空值直接返回 `true` | 某些框架把空值交给 `@NotNull`，但独立使用时易漏判 | 明确空值策略，别"顺手"返回 true |

---

## 四、完整的银行卡号校验流程

Luhn 是**最后一道防线**，不是全部。生产环境里完整的卡号校验是这样一条流水线：

```
输入 → 清洗 → 纯数字检查 → 长度检查(13~19) → BIN 前缀检查 → Luhn 校验 → 通过
```

### 4.1 BIN 前缀：识别发卡行

卡号前 6 位是 **IIN/BIN**（发卡行识别码），配合长度可以初步识别卡组织。常见的国际前缀：

| 卡组织 | 前缀 | 常见长度 |
|---|---|---|
| Visa | 4 开头 | 16 |
| MasterCard | 51~55 | 16 |
| 银联 | 62 | 16~19 |
| American Express | 34、37 | 15 |
| JCB | 3528~3589 | 16~19 |
| Discover | 6011、65 | 16~19 |

```java
public enum CardBrand {
    VISA, MASTERCARD, UNIONPAY, AMEX, JCB, DISCOVER, UNKNOWN;

    public static CardBrand of(String cardNumber) {
        if (cardNumber.startsWith("62")) return UNIONPAY;
        if (cardNumber.startsWith("4"))  return VISA;
        if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) return AMEX;
        if (cardNumber.startsWith("51") || cardNumber.startsWith("52")
                || cardNumber.startsWith("53") || cardNumber.startsWith("54")
                || cardNumber.startsWith("55")) return MASTERCARD;
        if (cardNumber.matches("35(2[89]|[3-8][0-9]).*")) return JCB;   // 3528~3589
        if (cardNumber.startsWith("6011") || cardNumber.startsWith("65")) return DISCOVER;
        return UNKNOWN;
    }
}
```

> 注意：BIN 识别是"格式层面"的启发式判断，**不是银行实名核验**。真要验证卡号归属、余额、可用性，必须走银联/银行接口。

### 4.2 Luhn 是"防错"，不是"防伪"

这一点必须反复强调：

- **防错**：检测手误、录入错误、传输差错——这是 Luhn 的本职，本地零成本完成；
- **防伪**：算法完全公开，任何人都能对任意 15 位数字算出合法校验位。**Luhn 通过 ≠ 卡真实存在**，更 ≠ 卡能用。

所以校验通过后，业务上仍需通过银行侧接口做实名/验卡。Luhn 的价值在于：**把绝大多数手误和无效号码挡在系统之外，减少对银行接口的无效调用**。

---

## 五、ValidX 中的实现

ValidX 把整套逻辑封装成了开箱即用的能力，注解与链式 API 两种方式都支持。

### 5.1 注解方式：@BankCard

```java
public class BindCardDTO {
    @NotBlank(message = "银行卡号不能为空")
    @BankCard  // 默认消息：无效的银行卡号
    private String cardNo;
}
```

配合 Spring 的 `@Valid` 自动触发。默认错误消息来自资源文件：`io.github.vipxieliang.validx.annotation.bank.card = 无效的银行卡号`（支持多语言，已内置中/英/日/德等 9 种）。

### 5.2 链式 API：isBankCard

```java
ValidX validator = ValidX.init()
        .field("银行卡号").isBankCard(cardNo);

if (!validator.passed()) {
    throw new BusinessException(validator.getErrors());
}
```

`isBankCard(Object)` 接收 `Object`，因此 `Map` 取值、DTO 字段、方法参数都可以直接传入，错误信息自动带上 `field()` 指定的字段名。

### 5.3 源码对照

`ValidX` 内部的 `BankCardValidator`（`validator/financial/BankCardValidator.java`）与 3.3 节的生产级实现完全同构：

```java
public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
        return true; // 空值处理交给 @NotNull 等其他注解
    }

    // 1. 移除所有空格和连字符
    String cleanValue = value.replaceAll("[\\s-]", "");

    // 2. 检查是否全部为数字
    if (!cleanValue.matches("\\d+")) {
        return false;
    }

    // 3. 检查长度是否符合银行卡号规范（通常为 13-19 位）
    if (cleanValue.length() < 13 || cleanValue.length() > 19) {
        return false;
    }

    // 4. 使用 Luhn 算法验证银行卡号
    return isLuhnValid(cleanValue);
}

private boolean isLuhnValid(String cardNumber) {
    int sum = 0;
    boolean isEven = false;

    // 从右向左遍历
    for (int i = cardNumber.length() - 1; i >= 0; i--) {
        int digit = Character.getNumericValue(cardNumber.charAt(i));

        if (isEven) {
            digit *= 2;
            if (digit > 9) {
                digit -= 9;
            }
        }
        sum += digit;
        isEven = !isEven;
    }
    return sum % 10 == 0;
}
```

和 3.1 的最小实现逐行对应：`isEven` 就是 `doubleDigit`，`digit -= 9` 就是"超 9 减 9"。注意源码中**空值返回 `true`**——这是 Bean Validation 的惯例：空值交给 `@NotBlank` 等注解处理，`@BankCard` 只负责"非空时的格式与校验位"。

> ValidX 的 `FinancialValidation` 同样封装了 `@CVV`、`@IBAN`、`@SWIFT` 等金融类校验，`isBankCard` 与它们共用一套错误消息与字段名机制。

### 5.4 同源应用：IMEI 也用 Luhn

Luhn 不止用于银行卡。手机 IMEI 是 15 位：前 14 位含机型信息，第 15 位就是 Luhn 校验位。ValidX 的 `IMEIValidator` 用同一套算法校验：

```java
// 前 14 位：奇数索引（从左数第 1、3、5…位）原样，偶数索引加倍
if (i % 2 == 0) {
    sum += digit;
} else {
    int doubled = digit * 2;
    sum += (doubled / 10) + (doubled % 10);   // 两位数字相加
}
// 校验位 = (10 − sum % 10) % 10，与第 15 位比对
```

这段代码里"`doubled / 10 + doubled % 10`"正是 2.1 节证明过的等价写法——与"超 9 减 9"完全等价。15 位 IMEI 从右往左编号，第 15 位（校验位）不加倍、第 14 位加倍，恰好对应"从左数奇数索引加倍"的规则。**同一个算法，银行卡和手机设备号共用。**

---

## 六、常见问题

### Q1：Luhn 能防伪造银行卡号吗？

不能。算法公开，任何人可以给任意数字串算出合法校验位。Luhn 只做"防错"（拦截手误和传输错误），真正验卡必须走银行/银联接口。

### Q2：为什么 `09 ↔ 90` 相邻交换检测不出？

因为 `f(0) = 0`、`f(9) = 9`——0 和 9 是仅有的两个"加倍后不变"的数字。无论它们在交换中谁处于加倍位，总贡献都是 9（见 2.4 推导）。这是 Luhn 唯一的结构性盲区。

### Q3：Luhn 和身份证校验码（MOD 11-2）是一回事吗？

同属"加权和 + 取模"家族，但设计不同：身份证用**素数模 11 + 2 的幂权重**，靠"位置敏感"检测错误；Luhn 用**模 10 + 双射变换**（超 9 减 9），靠"值变换"检测错误。Luhn 更简单、更老（1954），身份证的 MOD 11-2（ISO 7064）是 1983 年标准化，检测能力更强（没有 09/90 盲区）。

### Q4：卡号里带空格/连字符怎么处理？

先 `replaceAll("[\\s-]", "")` 清洗再校验。ValidX 的 `@BankCard` 内部已自动处理。

### Q5：怎么生成一个能通过校验的测试卡号？

用 3.2 节的 `calculateCheckDigit`：取 15 位前缀（如测试号 `622202123456789`），算出校验位 2，拼成 `6222021234567892`。注意测试卡号**只能用于本地联调，不要提交到真实支付环境**。

### Q6：为什么卡号有的 13 位、有的 19 位？

ISO/IEC 7812 规定卡号 8~19 位，主流卡组织实际使用 13~19 位。长度本身不构成校验依据，但可以作为**第一道廉价过滤**（ValidX 中为 13~19 位）。

---

## 七、总结

把全文压缩成一条逻辑链：

```
校验位放在最右侧
        │  从右往左：奇数位原样、偶数位加倍
        ▼
加倍后超 9 减 9 = 一张双射表 f
        │  双射 → 单字符替换 100% 检测；仅 09↔90 相邻交换漏检
        ▼
和 ≡ 0 (mod 10) → 校验通过
        │  验证与生成互为逆运算
        ▼
Java 实现：清洗 → 纯数字 → 长度(13~19) → Luhn
        │
        ▼
ValidX：@BankCard / isBankCard() 已内置全流程
```

三个必须记住的结论：

1. **Luhn 是"防错"算法**——拦截手误零成本，但通过 ≠ 卡真实存在，验卡仍要依赖银行接口；
2. **"超 9 减 9"不是拍脑袋**——它构造了一张双射表，让"偶数位加倍"也能 100% 检测单字符错误，代价仅是 09/90 一个盲区；
3. **验证与生成是同一算法的两个方向**——理解了这一点，Luhn 在银行卡、IMEI、ISBN 里的各种变体就都能一眼看穿。
