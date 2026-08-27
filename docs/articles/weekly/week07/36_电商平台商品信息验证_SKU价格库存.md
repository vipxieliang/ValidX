# 电商平台商品信息验证：SKU、价格、库存

## 引言

假设你是电商后端负责人，下面三个问题可能就在你眼前发生过：

- **SKU 格式失控**：运营在后台手工录入商品，同一家店出现 `SKU-2024-A001`、`sku2024001`、`TD_2024001` 三种写法。订单系统按 SKU 匹配库存时，前两种匹配成功、第三种匹配失败，导致一件商品被当成两件卖，库存对不上账，财务深夜来敲门。
- **负价格商品**：改价接口没做校验，运营把促销价填成了 `-9.90`。系统"宽容"地接受了，用户下单后计算器给出负总价，营销团队半夜上线活动，早上收到一堆"负金额退款"工单。
- **库存爆仓**：库存字段用的是 `int`，运营一次导入 30 亿库存，`Integer.MAX_VALUE` 溢出变成负数，商品瞬间"超卖"到无法发货。

这三个问题的共同点：**格式没把关、数值没设界、状态没白名单**。电商的商品信息验证，本质上就是把这三层守好。本文用 ValidX + Bean Validation 标准注解，从 SKU、价格、库存三个维度给出可直接照抄的完整方案。

> 说明：文中所有 ValidX 能力均对照 v1.2.0 源码与测试用例核实（`@AlphaDash`、`@Contains`、`@In`、`@Enum`、`@TradeOrderNumber` 等），示例代码可自行复现。项目基于 `javax.validation 2.0.1` + `Hibernate Validator 6.1.5`，标准数值注解开箱可用。

---

## 一、商品信息验证全景：三层模型

一份商品数据，从录入到上架，需要守住三个层面：

| 层级 | 验证对象 | 例子 | 谁来守 |
|------|---------|------|--------|
| **格式层** | SKU、类目码、图片扩展名、订单号 | 只允许字母数字连字符、必须带 `SKU-` 前缀 | ValidX 格式注解 |
| **状态层** | 商品状态、类目、支付方式 | 必须属于 `{草稿, 待审核, 在售, 售罄, 下架}` | ValidX `@In` / `@Enum` |
| **数值层** | 价格、库存、重量、评分 | 价格 ≥ 0.01 且最多两位小数；库存 0~999999 | Bean Validation 标准注解 |

关键认知：**ValidX 是"格式校验库"——它负责"长得像不像"，不负责"数值在不在合理范围"**。项目里没有 `@Min`/`@Max`/`@DecimalMin`/`@Digits`，这是刻意为之的边界：100+ 个格式校验器已经够多，数值范围属于 Bean Validation 标准能力，不该重复造轮子。

所以电商场景的最佳姿势是**组合拳**：

```
ValidX 格式注解（@AlphaDash / @Contains / @In / @Enum ...）
        +
javax.validation 标准注解（@NotBlank / @Size / @DecimalMin / @Digits / @Min / @Max ...）
        =
一个注解双管齐下的完整商品 DTO
```

下面按 SKU → 状态 → 价格 → 库存 → 全链路，逐层展开。

---

## 二、SKU 编码验证：先定规则，再上注解

### 2.1 先定一套 SKU 规则

SKU 验证的前提是**规则先行**。给一个可供参考的规则：

```
[品牌前缀] - [类目码] - [规格码] - [随机序号]
    TD       -   C01     -   S02     -   88A1
```

规则：
1. 品牌前缀：`TD`、`JD`、`PDD` 三选一，**前缀必须存在**（`@StartsWithAny`）；
2. 全字符只允许**字母、数字、连字符**（`@AlphaDash`，字母数字下划线连字符均可）；
3. 长度 8~32（标准注解 `@Size`）；
4. 不能包含保留词 `TEST`、`DEMO`（`@NotContains`）；
5. SKU 不能为空（`@NotBlank`）。

### 2.2 注解组合完整示例

```java
import io.github.vipxieliang.validx.annotations.AlphaDash;
import io.github.vipxieliang.validx.annotations.Contains;
import io.github.vipxieliang.validx.annotations.NotContains;
import io.github.vipxieliang.validx.annotations.StartsWithAny;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProductDTO {

    /** 商品编码（SKU） */
    @NotBlank(message = "SKU 不能为空")
    @Size(min = 8, max = 32, message = "SKU 长度须为 8~32 位")
    @AlphaDash(message = "SKU 只能包含字母、数字、下划线和连字符")
    @StartsWithAny(value = {"TD-", "JD-", "PDD-"}, message = "SKU 必须以 TD- / JD- / PDD- 开头")
    @NotContains(value = {"TEST", "DEMO"}, message = "SKU 不能包含保留词 TEST/DEMO")
    private String sku;

    /** 图片文件：必须匹配支持的后缀 */
    @Contains(value = {".jpg", ".png", ".webp"}, message = "商品图片仅支持 jpg/png/webp")
    private String image;

    // getter / setter 省略
}
```

**逐注解拆解**（均对照源码）：

- `@AlphaDash`：验证字符串**只包含字母、数字、下划线和连字符**。它不限制长度、不限制前缀——所以要和 `@Size`、`@StartsWithAny` 叠用；
- `@StartsWithAny(value={"TD-","JD-","PDD-"})`：前缀命中**任意一个**即通过（OR 逻辑），`ignoreCase` 默认 `false` 区分大小写；
- `@Contains(value={".jpg",".png",".webp"})`：默认 OR 语义（命中任一子串即通过）。如果想要求"必须同时包含多个子串"，用 `matchAll = true`：
  ```java
  @Contains(value = {"TD", "2024"}, matchAll = true, message = "SKU 必须同时包含 TD 和 2024")
  ```
- `@NotContains(value={"TEST","DEMO"})`：**排除**保留词，防止测试数据混入生产。

> **重要**：ValidX 的格式注解遵循"空值放行"原则（README 明确说明：`null` 和部分空串场景交给使用者决定），所以**必须叠加 `@NotBlank`** 兜住"空 SKU"，否则 `""` 会绕过所有格式校验。

### 2.3 链式 API 版本

不写 DTO、想在 Service 里快速校验单值时，用链式 API：

```java
import io.github.vipxieliang.validx.chain.ValidX;

boolean checkSku(String sku) {
    ValidX v = ValidX.init()
        .field("SKU").notEmpty()   // 局部状态：要求非 null 且非空，由下一个校验器消费
        .isAlphaDash(sku)
        .isStartsWithAny(sku, new String[]{"TD-", "JD-", "PDD-"})
        .isNotContains(sku, new String[]{"TEST", "DEMO"});
    if (!v.passed()) {
        System.out.println(v.getErrorMessage()); // 所有错误用逗号拼接
        return false;
    }
    return true;
}
```

**链式 API 的"必填"用法**：`notNull()` / `notEmpty()` / `allowNull()` / `allowEmpty()` 都是**无参的局部状态标记**——它们设置下一个校验器的空值要求，随后由 `isXxx(value)` 消费并自动重置。所以必填字段的链式写法是 `field("SKU").notEmpty().isAlphaDash(sku)`，而不是给 `notEmpty` 传参。

校验失败时 `getErrors()` 返回所有错误列表、`getErrorMessage()` 返回拼接串，`passed()` 判断是否全部通过——多条件累积，一行一条，便于日志排查。

---

## 三、商品状态验证：白名单与枚举

商品状态、类目码、支付方式这类"取值必须来自固定集合"的字段，是 `@In` / `@Enum` / `@NotIn` 的主场。

### 3.1 用 @In 做白名单

```java
import io.github.vipxieliang.validx.annotations.In;

/** 商品状态：只允许这 5 个值 */
@In(value = {"草稿", "待审核", "在售", "售罄", "下架"}, message = "商品状态非法")
private String status;

/** 支付方式白名单 */
@In(value = {"ALIPAY", "WECHAT", "CARD"}, message = "不支持的支付方式")
private String payType;
```

`@In` 支持 `String`、`String[]`、`List<String>`、`int[]`——列表字段会**逐个元素校验**：

```java
@In(value = {"ALIPAY", "WECHAT"}, message = "存在不支持的支付方式")
private List<String> payTypes;
```

### 3.2 用 @Enum 绑定枚举（推荐）

字符串写死在注解里，改一处要改所有引用处。**更稳的做法是把状态定义成枚举，用 `@Enum` 绑定**：

```java
import io.github.vipxieliang.validx.annotations.Enum;

/** 状态码枚举：code 是落库值 */
public enum ProductStatus {
    DRAFT("10"), PENDING("20"), ON_SALE("30"), SOLD_OUT("40"), OFF_SALE("50");

    private final String code;
    ProductStatus(String code) { this.code = code; }
    public String getCode() { return code; }
}
```

```java
/** 校验 statusCode 必须是枚举中某个 code（默认字段就叫 code，可不写） */
@Enum(target = ProductStatus.class, field = "code", message = "商品状态码非法")
private String statusCode;
```

`@Enum` 的 `field` 默认是 `"code"`，也可以传 `"name"` 或枚举里的任意属性/getter 名。集合字段同样逐个校验：

```java
@Enum(target = ProductStatus.class, field = "code")
private List<String> statusCodes;
```

**`@Enum` vs `@In` 怎么选**：值集合会被多端复用（前端下拉、后端校验、报表枚举）时用 `@Enum`（单一事实来源）；一次性白名单（如接口只临时收 3 个值）用 `@In` 即可，不引入枚举类。

### 3.3 用 @NotIn 排除保留值

```java
import io.github.vipxieliang.validx.annotations.NotIn;

/** 商品名称不允许出现保留词 */
@NotIn(value = {"admin", "root", "测试"}, message = "商品名称包含保留词")
private String name;
```

---

## 四、价格验证：金额不是普通数字

### 4.1 金额的三条铁律

1. **用 `BigDecimal`，不用 `double`/`float`**：`0.1 + 0.2` 在 double 里是 `0.30000000000000004`，价格差一分钱都是事故；
2. **单位统一**：对外展示用"元"，内部计算统一"分"（整数），避免小数乘除丢精度；
3. **边界明确**：最低价（0.01）、最高价、小数位（2 位）三条线必须可配置、可追溯。

### 4.2 ValidX 没有金额注解——这正是组合的价值

源码层面确认：ValidX 的注解体系（`@Accountant` 是"会计资格证书编号"、`@DDC` 是图书馆分类号，均与金额无关）**没有内置金额/数值范围注解**。这是刻意的职责划分——数值范围交给 Bean Validation 标准注解，它们在任何 Java 项目里都可移植：

```java
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/** 商品售价：0.01 ~ 9999999.99，最多 7 位整数 2 位小数 */
@NotNull(message = "价格不能为空")
@DecimalMin(value = "0.01", message = "价格不能低于 0.01 元")
@DecimalMax(value = "9999999.99", message = "价格不能超过 9999999.99 元")
@Digits(integer = 7, fraction = 2, message = "价格最多 7 位整数、2 位小数")
private BigDecimal price;

/** 划线价（原价）：必须大于等于售价，这个"两者比较"用自定义约束或业务校验 */
@DecimalMin(value = "0.00", message = "划线价不能为负")
@Digits(integer = 7, fraction = 2)
private BigDecimal originalPrice;
```

**为什么用 `@DecimalMin(value="0.01")` 而不是 `@Min(1)`**：`@Min` 只认整数（丢小数），`@DecimalMin` 的字符串参数按 `BigDecimal` 语义比较，天然避免 double 比较的精度陷阱。同理，比较时**永远用 `compareTo`**：

```java
// 错误示范：BigDecimal.equals 会因 scale 不同判不相等（0.10 != 0.1）
if (price.equals(BigDecimal.ZERO)) { ... }

// 正确示范：compareTo 只比数值
if (price.compareTo(BigDecimal.ZERO) <= 0) { ... }
```

### 4.3 价格校验的效果表

| 输入 | 校验结果 | 命中的约束 |
|------|---------|-----------|
| `null` | 失败 | `@NotNull` |
| `-9.90` | 失败 | `@DecimalMin("0.01")` |
| `0` | 失败 | `@DecimalMin("0.01")` |
| `0.01` | 通过 | — |
| `9999999.99` | 通过 | — |
| `10000000`（8 位整数） | 失败 | `@DecimalMax` / `@Digits(integer=7)` |
| `19.999`（3 位小数） | 失败 | `@Digits(fraction=2)` |
| `19.9` | 通过 | — |

---

## 五、库存验证：整数与边界

### 5.1 库存三问

- **能不能为负**？不能。负库存 = 超卖，`@Min(0)`；
- **上限多少**？物理仓库存放不下无限库存，设个业务上限 `@Max(999999)`；
- **是不是整数**？库存不存在"1.5 件"，`@Digits(integer=6, fraction=0)` 强制整数字段。

```java
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/** 可售库存：0 ~ 999999，且必须为整数 */
@NotNull(message = "库存不能为空")
@Min(value = 0, message = "库存不能为负")
@Max(value = 999999, message = "库存超出上限")
@Digits(integer = 6, fraction = 0, message = "库存必须是不超过 6 位的整数")
private Integer stock;
```

### 5.2 库存类型的两个坑

**坑一：字段类型用 `Integer`，别用 `int`。** 基础类型 `int` 默认值是 0，前端漏传时"0 库存"会静默通过 `@NotNull`，把商品直接变售罄。用包装类型 `Integer` + `@NotNull`，漏传会明确报"库存不能为空"。

**坑二：库存是"字符串"时，别让字符串漏网。** 部分老系统库存走 String 传输，字符串 `"1.5"` 或 `"-3"` 需要先转换再校验，或者在 DTO 层就声明成 `Integer`，把类型转换错误交给框架统一处理。

### 5.3 联动校验：库存与状态

"库存为 0 时状态必须是售罄"这类**跨字段联动**，`@In`/`@Min` 单独都搞不定。标准做法是自定义 `ConstraintValidator`（属于 Bean Validation 能力，与 ValidX 风格一致）：

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StockStatusCheckValidator.class)
public @interface StockStatusCheck {
    String message() default "库存为 0 时状态必须为售罄";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

```java
public class StockStatusCheckValidator
        implements ConstraintValidator<StockStatusCheck, ProductDTO> {

    @Override
    public boolean isValid(ProductDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null || dto.getStock() == null) return true;
        boolean zeroStock = dto.getStock() == 0;
        boolean onSale = "30".equals(dto.getStatusCode()); // ON_SALE 的 code
        return !(zeroStock && onSale);
    }
}
```

---

## 六、全链路实战：一个商品发布接口

### 6.1 完整 DTO（注解版）

把上面所有约束合并成一个可落地的商品发布 DTO：

```java
import io.github.vipxieliang.validx.annotations.AlphaDash;
import io.github.vipxieliang.validx.annotations.Contains;
import io.github.vipxieliang.validx.annotations.Enum;
import io.github.vipxieliang.validx.annotations.NotContains;
import io.github.vipxieliang.validx.annotations.StartsWithAny;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {

    @NotBlank(message = "SKU 不能为空")
    @Size(min = 8, max = 32, message = "SKU 长度须为 8~32 位")
    @AlphaDash(message = "SKU 只能包含字母、数字、下划线和连字符")
    @StartsWithAny(value = {"TD-", "JD-", "PDD-"}, message = "SKU 必须以 TD-/JD-/PDD- 开头")
    @NotContains(value = {"TEST", "DEMO"}, message = "SKU 不能包含保留词")
    private String sku;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不能超过 100 字")
    private String name;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格不能低于 0.01 元")
    @DecimalMax(value = "9999999.99", message = "价格超出上限")
    @Digits(integer = 7, fraction = 2, message = "价格最多 7 位整数、2 位小数")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负")
    @Max(value = 999999, message = "库存超出上限")
    @Digits(integer = 6, fraction = 0, message = "库存必须是不超过 6 位的整数")
    private Integer stock;

    @Enum(target = ProductStatus.class, field = "code", message = "商品状态码非法")
    private String statusCode;

    @Contains(value = {".jpg", ".png", ".webp"}, message = "商品图片仅支持 jpg/png/webp")
    private String image;

    /** 多规格：级联校验每个 SKU */
    @Valid
    @Size(max = 20, message = "单商品最多 20 个规格")
    private List<SkuItem> skus;

    // getter / setter 省略
}

/** 规格项：每个规格都是一个"迷你商品" */
public class SkuItem {
    @NotBlank @AlphaDash
    private String sku;

    @NotNull @DecimalMin("0.01") @Digits(integer = 7, fraction = 2)
    private BigDecimal price;

    @NotNull @Min(0) @Max(999999) @Digits(integer = 6, fraction = 0)
    private Integer stock;

    // getter / setter 省略
}
```

**要点**：`@Valid` 让"多规格"列表里的每个 `SkuItem` 也被递归校验——一个商品带 20 个规格，任一规格价格非法都能精确定位。

### 6.2 Controller 接入

```java
@RestController
public class ProductController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @PostMapping("/products")
    public ResponseEntity<?> publish(@RequestBody @Valid ProductDTO dto) {
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(msg);
        }
        return ResponseEntity.ok("商品校验通过，准备落库");
    }
}
```

（Spring Boot 项目里 `@Valid` + 全局 `@RestControllerAdvice` 自动处理，上面的手动 `validate` 只是演示最小可运行形态。）

### 6.3 链式 API 版本（工具类/批量导入场景）

电商后台的**批量导入 Excel** 场景不适合注解——数据还没进 DTO。用链式 API 逐行校验：

```java
import io.github.vipxieliang.validx.chain.ValidX;

/** 批量导入时逐行校验，返回错误行号与原因 */
String validateImportRow(int rowNum, String sku, BigDecimal price, Integer stock, String statusCode) {
    ValidX v = ValidX.init()
        .field("SKU").notEmpty().isAlphaDash(sku)
        .field("价格").notNull()          // BigDecimal 只有 null 概念，用 notNull
        .field("库存").notNull();
    if (!v.passed()) {
        return "第 " + rowNum + " 行：" + v.getErrorMessage();
    }
    // 数值范围仍需标准校验，可在导入时单独判：
    if (price.compareTo(new BigDecimal("0.01")) < 0) return "第 " + rowNum + " 行：价格不能低于 0.01 元";
    if (stock < 0 || stock > 999999) return "第 " + rowNum + " 行：库存超出 0~999999";
    return null; // 通过
}
```

`field("SKU")` 设置当前字段标签，错误消息自动带上前缀，批量导入日志一眼定位是哪一行哪个字段。

### 6.4 全链路效果验证表

| 输入数据（SKU / 价格 / 库存 / 状态） | 校验结果 | 命中的约束 |
|-------------------------------------|---------|-----------|
| `SKU-2024-A001` / `99.90` / `50` / `30` | ✅ 通过 | — |
| `sku2024001`（前缀缺失） / `99.90` / `50` / `30` | ❌ | `@StartsWithAny` |
| `SKU 2024 A001`（含空格） / `99.90` / `50` / `30` | ❌ | `@AlphaDash` |
| `SKU-2024-TEST`（含保留词） / `99.90` / `50` / `30` | ❌ | `@NotContains` |
| `SKU-2024-A001` / `0` / `50` / `30` | ❌ | `@DecimalMin("0.01")` |
| `SKU-2024-A001` / `99.999` / `50` / `30` | ❌ | `@Digits(fraction=2)` |
| `SKU-2024-A001` / `99.90` / `-5` / `30` | ❌ | `@Min(0)` |
| `SKU-2024-A001` / `99.90` / `50` / `99`（状态码非法） | ❌ | `@Enum` |
| `SKU-2024-A001` / `99.90` / `0` / `30`（0 库存却在售） | ❌ | `@StockStatusCheck` |

---

## 七、常见坑与 FAQ

**Q1：SKU 校验通过了，为什么入库时还是报唯一键冲突？**
校验只管格式，不保证唯一性。SKU 唯一约束要在数据库建唯一索引，写入前再查一次库。"格式合法 + 唯一存在"是两道独立的关。

**Q2：为什么 `@AlphaDash` 都加了，空字符串还能通过？**
ValidX 遵循 Bean Validation 的约定：`null` 视为"不需要校验"，空串交给 `@NotBlank`/`@Size` 这类标准注解去管。所以**凡是必填的格式字段，`@NotBlank` 必须和 ValidX 格式注解叠用**，缺一不可。

**Q3：价格为什么不用 `@Min`/`@Max`？**
`@Min`/`@Max` 只支持 `long` 语义，用于金额会丢小数（`@Min(1)` 直接否掉 `0.5`）。金额必须用 `@DecimalMin`/`@DecimalMax`（字符串参数、`BigDecimal` 语义），小数位控制交给 `@Digits(integer=, fraction=)`。

**Q4：库存上限 999999 写死在注解里，想改怎么办？**
把上限收进配置类，再用自定义注解或 Service 层校验读取配置。注解里的字面量适合"稳定不变的规则"，会变动的阈值放配置。

**Q5：`@Enum` 和 `@In` 到底选哪个？**
状态值会被前端下拉、后端校验、报表统计多处引用 → `@Enum`（单一事实来源）；只在本接口用一次的临时白名单 → `@In` 更轻。

**Q6：价格、库存这种数值校验，ValidX 到底管不管？**
不管，也不该管。ValidX 的定位是**格式校验库**（100+ 格式校验器），数值范围是 Bean Validation 标准能力。两者的分工：`@AlphaDash` 保证 SKU"长得对"，`@DecimalMin` 保证价格"值合理"。组合使用才是完整方案。

---

## 八、最佳实践清单

1. **规则先行**：SKU 编码规则、价格边界、库存上限先写成设计文档，再落成注解——注解只是规则的表达；
2. **分层守关**：格式（ValidX）→ 数值（标准注解）→ 状态（枚举）→ 跨字段联动（自定义约束），每层管一件事；
3. **必填用 `@NotBlank`**：所有 ValidX 格式注解都必须配 `@NotBlank`（null/空串放行原则）；
4. **金额只信 `BigDecimal`**：DTO 用 `BigDecimal`、比较用 `compareTo`、范围用 `@DecimalMin/@DecimalMax/@Digits`；
5. **库存用包装类型**：`Integer` + `@NotNull`，宁可报错也不静默 0；
6. **集合级联校验**：多规格商品用 `@Valid` + 内嵌 DTO，每个规格独立把关；
7. **批量导入走链式 API**：`field()` 打标签 + `getErrors()` 全量收集，逐行定位；
8. **唯一性不入校验**：SKU 唯一靠数据库索引，别把"查库"塞进注解校验。

---

项目地址：[https://github.com/vipxieliang/ValidX](https://github.com/vipxieliang/ValidX)

更多场景实战：`@TradeOrderNumber` 校验交易订单号（支持 T 开头 18 位数字 / 纯 18 位数字 / UUID 三种格式，是电商订单中心现成的订单号校验器）；`@ExpressNumber` 校验快递单号，配合本文的 SKU/价格/库存组合，可覆盖"商品发布 → 下单 → 发货"的完整电商链路。
