# ValidX自定义验证注解开发入门

## 引言

ValidX 内置了 100+ 验证注解，覆盖身份证、手机号、邮箱、银行卡、地址等高频场景。但业务规则是无限的：公司内部的工单编号、商品的颜色值、订单的状态机……总有内置注解管不到的地方。

这时候有三条路：

1. **写 if-else**：散落在 service 里，重复、难维护、没有声明式语义；
2. **写自定义验证器类**：逻辑独立了，但要在 controller/service 手动调用，和 Spring 校验框架脱节；
3. **写自定义验证注解**：声明式、复用、和 `@Valid` 无缝集成——**Bean Validation（JSR-380）从设计上就支持这条路**。

本文用完整的可运行代码，手把手演示如何在 ValidX 生态下开发一个自定义验证注解，并对照 ValidX v1.2.0 源码（`annotations/In.java`、`validator/base/InValidator.java`、`annotations/ChineseZipCode.java`、`validator/china/ChineseZipCodeValidator.java`、`chain/base/BaseValidation.java`）核实写法，保证文中模式与库内真实实现一致。

---

## 一、自定义验证注解的原理：一个接口，两件东西

Bean Validation 的自定义校验由两部分组成，缺一不可：

| 部分 | 作用 | 对应 ValidX 源码 |
|------|------|------|
| **注解** `@interface Xxx` | 声明规则、携带参数、指定错误消息 | `annotations/In.java` |
| **验证器** `XxxValidator` | 实现真正的校验逻辑 | `validator/base/InValidator.java` |

两者通过 `@Constraint(validatedBy = XxxValidator.class)` 绑定：

```java
@Constraint(validatedBy = InValidator.class)   // ← 注解指向验证器
public @interface In { ... }
```

校验时容器（Hibernate Validator）会：**根据注解找到验证器 → 调用 `initialize()` 注入注解参数 → 调用 `isValid()` 判断值是否合法**。整个过程对业务代码完全透明，你只需要在 DTO 字段上加注解即可。

先看两个真实例子，直观感受"简单注解"和"带参数注解"的差别：

`ChineseZipCode`（无参数，纯正则）：

```java
@Constraint(validatedBy = ChineseZipCodeValidator.class)
public @interface ChineseZipCode {
    String message() default "{io.github.vipxieliang.validx.annotation.chinese.zip.code}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

`In`（带参数，白名单数组）：

```java
@Constraint(validatedBy = InValidator.class)
public @interface In {
    String[] value();                          // ← 自定义参数
    String message() default "{io.github.vipxieliang.validx.annotation.in}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

注意共性：

- `@Target` 声明注解能用在哪些位置（字段、方法、类型……）；
- `@Retention(RetentionPolicy.RUNTIME)` 必须保留到运行时，验证器才能读到；
- `message()` / `groups()` / `payload()` **三个方法缺一不可**，这是 Bean Validation 规范的要求。

---

## 二、实战：从零开发第一个自定义注解

目标：开发一个 `@HexColor` 注解，校验字符串是不是合法的十六进制颜色（`#FF0000`、`00ff00`、`336699` 等）。

### 2.1 定义注解

```java
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = HexColorValidator.class)
public @interface HexColor {

    String message() default "颜色必须是十六进制格式，如 #FF0000";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

对照 ValidX 的 `ChineseZipCode`，结构完全一致：`message` 的默认值可以写成消息 key（`{...}` 形式，见第五节），也可以直接写字面文案——入门阶段先写字面值，跑通再说。

### 2.2 实现验证器

```java
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class HexColorValidator implements ConstraintValidator<HexColor, String> {

    /** 正则预编译：颜色 = 可选 # + 6 位十六进制 */
    private static final Pattern COLOR_PATTERN = Pattern.compile("^#?[0-9A-Fa-f]{6}$");

    @Override
    public void initialize(HexColor constraintAnnotation) {
        // 本例无参数，无需处理（对照 ChineseZipCodeValidator 的写法）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;                          // 空值放行，必填交给 @NotNull
        }
        return COLOR_PATTERN.matcher(value).matches();
    }
}
```

对照 ValidX 的 `ChineseZipCodeValidator` 逐行看：

| 写法 | ValidX 源码 | 说明 |
|------|------|------|
| `Pattern` 字段预编译 | `private static final String ZIP_CODE_PATTERN = "^\\d{6}$";` + `pattern` 字段 | 避免每次校验都编译正则 |
| `initialize` 空实现 | `initialize(ChineseZipCode)` 空方法体 | 无参数注解不需要初始化 |
| `null/空串` 返回 `true` | `if (value == null \|\| value.isEmpty()) return true;` | **ValidX 所有格式注解的约定**：空值放行 |

### 2.3 使用

```java
public class ProductDTO {

    @NotNull(message = "主色不能为空")
    @HexColor(message = "主色格式不正确，如 #FF0000")
    private String mainColor;
}
```

Controller 里正常触发即可，校验失败自动返回 `ConstraintViolation`：

```java
@PostMapping("/product")
public Result create(@RequestBody @Valid ProductDTO dto) {
    // 校验通过才会走到这里
}
```

### 2.4 行为表（与 ValidX 约定一致）

| 输入 | 结果 | 说明 |
|------|:---:|------|
| `"#FF0000"` | ✅ | 标准写法 |
| `"00ff00"` | ✅ | 允许不带 `#` |
| `"336699"` | ✅ | 小写也合法 |
| `"#FFF"` | ❌ | 缩写 3 位不支持 |
| `"red"` | ❌ | 颜色名不支持 |
| `"#FF00001"` | ❌ | 7 位超长 |
| `null` / `""` | ✅ 放行 | 必填请叠加 `@NotNull` |

---

## 三、带参数的注解：拆解 @In 的实现

大多数业务规则需要**参数**：白名单范围、允许的长度、匹配的枚举……`@In` 是 ValidX 里带参数注解的典型代表，拆开它，自定义注解的进阶就通了。

### 3.1 注解如何声明参数

```java
@Constraint(validatedBy = InValidator.class)
public @interface In {
    String[] value();                          // 白名单数组，使用方必须提供
    String message() default "{io.github.vipxieliang.validx.annotation.in}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

`String[] value()` 就是参数——使用注解时传入，如 `@In({"北京市", "上海市", "广州市"})`。参数可以是 `String`、`int`、枚举、数组，甚至是其他注解。

### 3.2 验证器如何读取参数

```java
public class InValidator implements ConstraintValidator<In, Object> {

    private String[] arrays;                     // 缓存注解参数

    @Override
    public void initialize(In constraintAnnotation) {
        arrays = constraintAnnotation.value();   // ← 从注解里取白名单
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;                         // null 放行
        }
        if (value instanceof Collection) {       // 集合：逐个元素校验
            for (Object item : (Collection<?>) value) {
                if (!isInArray(item)) return false;
            }
            return true;
        }
        if (value.getClass().isArray()) {        // 数组：逐个元素校验
            // ... 完整源码见 InValidator，含基本类型数组处理
        }
        return isInArray(value);                 // 普通值：精确匹配
    }

    private boolean isInArray(Object value) {
        for (String s : arrays) {
            if (value != null && s != null && s.equals(String.valueOf(value))) {
                return true;
            }
        }
        return false;
    }
}
```

要点：

- **`initialize()` 只调用一次**（每次校验前由容器调用），把注解参数缓存到字段，`isValid()` 里直接使用——不要在 `isValid()` 里反复读注解；
- 注意 ValidX 的 `InValidator` 泛型是 `ConstraintValidator<In, Object>`，**泛型第二个参数决定了能校验什么类型**。`Object` 意味着 String、集合、数组都能接住，属于"通用型"验证器；`ChineseZipCodeValidator` 用 `String`，属于"专用型"。

### 3.3 行为表（对照源码可复现）

| 输入 | 白名单 | 结果 | 说明 |
|------|------|:---:|------|
| `"上海"` | `{"北京","上海","广州"}` | ✅ | 精确匹配 |
| `"深圳"` | `{"北京","上海","广州"}` | ❌ | 不在白名单 |
| `["北京","广州"]` | 同上 | ✅ | 数组逐元素通过 |
| `["北京","杭州"]` | 同上 | ❌ | 有一个不匹配即失败 |
| `null` | 任意 | ✅ 放行 | 必填叠加 `@NotNull` |

---

## 四、进阶：让自定义注解同时支持链式 API（ValidX 特色）

ValidX 的另一大特色是**链式 API**——`ValidX.init().field("省份").isIn(value, new String[]{...})`。它和注解验证**共用同一套验证器**，这也是 ValidX 源码设计上的一个精妙点。

### 4.1 链式 API 如何复用注解验证器

看 `chain/base/BaseValidation.java` 里 `validateIn` 的实现：

```java
public void validateIn(Object value, String[] values, List<String> errors, Locale locale) {
    InValidator validator = new InValidator();   // 1. 直接 new 验证器
    validator.initialize(values);                // 2. 关键：initialize 的参数重载！
    if (!validator.isValid(value, null)) {       // 3. 复用同一套 isValid 逻辑
        errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.in", locale));
    }
}
```

而 `InValidator` 里多了一个**非注解的 `initialize` 重载**：

```java
/** 直接使用参数初始化验证器（用于链式调用） */
public void initialize(String[] values) {
    this.arrays = values != null ? values : new String[0];
}
```

这就是全部秘密：**验证器把"参数注入"和"校验逻辑"解耦，注解走 `initialize(注解)`，链式走 `initialize(参数)`，最终都汇聚到同一个 `isValid()`**。一个实现，双模式复用。

### 4.2 动手：给 @HexColor 也加上链式能力

模拟这个模式，给 `HexColorValidator` 加一个 `initialize` 重载，让正则规则可以被编程式复用：

```java
public class HexColorValidator implements ConstraintValidator<HexColor, String> {

    private static final Pattern COLOR_PATTERN = Pattern.compile("^#?[0-9A-Fa-f]{6}$");

    public void initialize(String dummy) {       // 参数重载：链式/编程式调用入口
        // 无参数规则，无需缓存；这里仅为对齐 ValidX 的双模式约定
    }

    @Override
    public void initialize(HexColor constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return COLOR_PATTERN.matcher(value).matches();
    }
}
```

然后就能在业务代码里编程式复用了：

```java
HexColorValidator validator = new HexColorValidator();
validator.initialize(null);                      // 对齐双模式初始化
if (!validator.isValid("#FF0000", null)) {
    throw new IllegalArgumentException("颜色格式不正确");
}
```

> 说明：链式 API 的方法定义在 ValidX 库内部（`ValidX.java`、`BaseValidation.java`），普通业务项目不能直接往库里加方法。本节的核心价值是**理解这个"验证器与调用方式解耦"的架构**——阅读 ValidX 源码不迷路，将来想给 ValidX 提 PR 扩展链式方法时，照这个模式写即可。

### 4.3 如果是在 ValidX 库内扩展

假设你要给 ValidX 贡献一个新的链式方法 `isHexColor`，流程是：

1. `annotations` 包加注解 `HexColor.java`；
2. `validator` 包加 `HexColorValidator`（带 `initialize(参数)` 重载）；
3. `chain` 包在对应分组类（如 `base/BaseValidation.java`）加 `validateHexColor`；
4. `chain/ValidX.java` 加入口方法 `isHexColor`，内部调 `validateHexColor`；
5. `ValidationMessages*.properties` 加消息 key（见第五节）。

---

## 五、错误消息与国际化

### 5.1 message 的两种写法

```java
// 写法一：字面文案（入门够用）
String message() default "颜色格式不正确";

// 写法二：消息 key（生产推荐，支持国际化）
String message() default "{com.example.validation.hex.color}";
```

`{...}` 是消息插值语法：Bean Validation 会把 key 放到 `ValidationMessages.properties`（以及各语言版本）里查找。查不到就用 key 原文当消息兜底。

### 5.2 ValidX 的消息文件是怎么组织的

看 `src/main/resources` 下的文件：

```
ValidationMessages.properties        # 默认（英文兜底）
ValidationMessages_zh.properties     # 简体中文
ValidationMessages_en.properties     # 英文
ValidationMessages_de.properties     # 德语
...                                  # 共 9 种语言
```

条目格式（key 采用**包名风格**避免冲突）：

```properties
io.github.vipxieliang.validx.annotation.in=无效值
io.github.vipxieliang.validx.annotation.chinese.zip.code=邮政编码格式不正确
```

你的业务注解照抄这个风格：`com.example.validation.hex.color=颜色必须是十六进制格式`。

### 5.3 为什么要用 key 而不是字面量

| 维度 | 字面量 | 消息 key |
|------|:---:|:---:|
| 多语言 | 不支持 | 9 语言资源文件即换即生效 |
| 统一改文案 | 逐处改注解 | 改一处 properties |
| 与 ValidX 体系一致 | 否 | 是（`MessageManager` 自动加载） |

生产环境建议：注解里写 key，文案全部收进 `ValidationMessages*.properties`。

---

## 六、最佳实践清单

1. **空值放行、必填显式声明**：验证器里 `null`/空串一律 `return true`，必填语义用 `@NotNull`/`@NotBlank` 叠加——这是 ValidX 所有内置注解的一致约定，自定义时保持一致，行为才不意外；
2. **正则预编译为 `Pattern` 常量**：不要每次 `isValid` 都 `Pattern.compile`，性能差别在高频接口下很明显（对照 `ChineseZipCodeValidator`）；
3. **参数在 `initialize()` 里缓存**：注解参数只在初始化时读取一次，`isValid()` 直接复用字段；
4. **泛型参数决定校验范围**：只验字符串用 `ConstraintValidator<Xxx, String>`，要接集合/数组用 `Object`（对照 `InValidator`）；
5. **消息 key 用包名风格**：`{com.example.xxx.rule}` 形式，避免和 ValidX 内置 key（`io.github.vipxieliang.*`）冲突；
6. **双模式复用**：验证器加一个 `initialize(参数)` 重载，让规则既能被注解驱动、也能被编程式/链式调用（ValidX 内部架构就是这么设计的）；
7. **跨字段规则用类级约束**：需要同时看多个字段时，把 `@Constraint` 放在类上、验证器泛型用 DTO 类型，在 `isValid` 里拿到整个对象做判断（类似服务层的跨字段校验）；
8. **Spring 环境可注入依赖**：`ConstraintValidator` 在 Spring Boot 下由容器创建，可以在构造函数里注入 `Service`/`Mapper`，让"数据库白名单"这类自定义规则落地（详见《收货地址验证完整方案》中的 `@Region` 示例）。

---

## 总结

- 自定义验证注解 = **注解（声明规则）** + **验证器（实现逻辑）**，通过 `@Constraint(validatedBy = ...)` 绑定，这是 Bean Validation 规范的标准扩展点；
- 对照 ValidX 源码：`ChineseZipCode`（无参数 + 正则）是入门模板，`In`（带参数 + 集合处理）是进阶模板，两个都值得精读；
- ValidX 的链式 API 与注解**共用验证器**，靠的是 `initialize(参数)` 重载——理解这个解耦，就能读懂 ValidX 一半的源码架构；
- 消息用 `{key}` + `ValidationMessages*.properties`，与国际化的 ValidX 体系无缝衔接；
- 空值放行、正则预编译、参数缓存、包名风格 key——四个习惯让你的自定义注解和 ValidX 内置注解"行为同构"。

> ValidX 是基于 Jakarta Bean Validation 规范的 Java 验证库，注解与链式 API 双模式，内置 100+ 验证规则。

## 项目地址

- **GitHub**：<https://github.com/vipxieliang/ValidX>
- **Gitee**：<https://gitee.com/vipxieliang/ValidX>
- **Maven Central**：<https://central.sonatype.com/artifact/io.github.vipxieliang/validx>

Maven 引入：

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.2.0</version>
</dependency>
```
