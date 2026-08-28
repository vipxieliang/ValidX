# ValidX分组验证详解：不同场景使用不同验证规则

## 引言

一个 DTO 引发的连环事故：

- **创建时必填、更新时禁止**：`UserDTO` 里用户名注册时必须填、编辑资料时却不用改；ID 新增时不能传、修改时必须带。于是团队拆了 `CreateUserDTO` 和 `UpdateUserDTO` 两个类，字段复制一份，改字段时漏改一处，线上悄悄出现"更新把用户名清空了"的 bug。
- **同一字段两套规则**：订单号在"下单"和"查询"两个接口里一个必填、一个可空。为了一个字段写两个 DTO，Controller 里到处是 `if (dto.getX() != null)` 的补丁式判断。
- **审核流程校验崩溃**：运营后台把"草稿"状态的商品直接提交审核，DTO 里"审核人必须存在"的规则把运营卡死——因为那条规则在草稿阶段本不该执行。

这三个问题的根源是同一个：**一份数据在生命周期不同阶段，需要满足的规则不一样，却只有一套验证**。

ValidX 基于 Jakarta Bean Validation 规范（项目实际使用 `javax.validation 2.0.1` + `Hibernate Validator 6.1.5`），所有注解原生支持标准分组机制。本文对照源码讲透分组验证的完整用法：从"分组是什么"到"Default 组/组继承/@GroupSequence"的进阶玩法，再落到 Spring Boot 与链式 API 的边界，最后给出可直接照抄的全场景 DTO。

> 说明：文中 ValidX 相关内容对照 v1.2.0 源码（全部 100+ 注解的 `groups` 属性、`chain/ValidX.java` 链式实现）与测试用例（`HourMinuteSecondValidatorTest`、`NotContainsValidatorTest` 等使用 `Validation.buildDefaultValidatorFactory().getValidator().validate(...)` 的标准触发方式）核实。

---

## 一、为什么需要分组验证

### 1.1 一套数据，多种身份

同一个业务对象在不同阶段，规则不同：

| 场景 | 对象 | 典型规则差异 |
|------|------|-------------|
| 创建 | 新数据 | 必填字段最多、ID 必须为空 |
| 更新 | 存量数据 | ID 必须存在、部分字段禁止修改 |
| 审核 | 提审数据 | 只有审核相关字段被校验 |
| 查询 | 查询条件 | 全部可空，只校验"填了的"格式 |

没有分组时，要么拆 DTO（类爆炸、字段重复维护），要么写 `if` 补丁（校验逻辑散落、容易漏）。

### 1.2 分组验证的本质

分组验证是 Bean Validation 规范的**标准机制**，核心就三件事：

```
1. 定义分组接口（一个空接口即可）
2. 注解上用 groups 指定它属于哪个分组
3. 触发验证时传入要执行的分组
```

ValidX 的每个注解都原生带 `groups()` 属性，与标准注解 `@NotBlank`、`@Size` 等完全混用，不需要任何额外配置。看一个真实源码中的注解定义（`annotations/ChineseName.java`）：

```java
@Constraint(validatedBy = ChineseNameValidator.class)
public @interface ChineseName {

    String message() default "{io.github.vipxieliang.validx.annotation.chinese.name}";

    Class<?>[] groups() default {};          // ← 分组：每个 ValidX 注解都有

    Class<? extends Payload>[] payload() default {};
}
```

`groups` 是标准的 `Class<?>[]` 数组——**一个约束可以同时属于多个分组**。

---

## 二、三分钟上手：定义分组并用起来

### 2.1 定义分组

分组就是**空接口**，没有方法、没有继承约束，纯粹是"标记"。习惯上作为嵌套接口放在 DTO 里，就近管理：

```java
public class UserDTO {

    /** 创建场景 */
    public interface CreateGroup {}

    /** 更新场景 */
    public interface UpdateGroup {}

    // ===== 以下字段按场景挂分组 =====

    @Null(groups = CreateGroup.class, message = "创建时 ID 必须为空")
    @NotNull(groups = UpdateGroup.class, message = "更新时 ID 不能为空")
    private Long id;

    @NotBlank(groups = CreateGroup.class, message = "创建时用户名必填")
    @Size(min = 2, max = 20, message = "用户名长度 2~20")
    private String username;

    @Email
    private String email;
}
```

注意 `email` 字段**没写 groups**——它属于默认的 `Default` 组（详见第三章），不限定场景时所有场景都校验。

### 2.2 触发验证：传入分组

非 Spring 环境（工具类、单元测试、服务层手动校验），用标准 Bean Validation 入口：

```java
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;

Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

// 只执行 CreateGroup 分组的约束
Set<ConstraintViolation<UserDTO>> violations =
        validator.validate(dto, UserDTO.CreateGroup.class);

if (!violations.isEmpty()) {
    String msg = violations.iterator().next().getMessage();
    // 创建时：id 必须为 null、用户名必填……email 的 @Email 也生效
}
```

`validate(obj, Class<?>... groups)` 支持传**多个分组**，用逗号并列即可：

```java
// 同时执行 CreateGroup 和 UpdateGroup
validator.validate(dto, UserDTO.CreateGroup.class, UserDTO.UpdateGroup.class);
```

**关键行为**：传入的分组决定"哪些约束执行"。没传分组（`validator.validate(dto)`）等价于只传 `Default` 组。

### 2.3 对照源码：这就是 ValidX 测试的调用方式

分组验证不是 ValidX 发明的，但 ValidX 的注解要能被 `validate(dto, groups...)` 驱动，前提是每个注解都规范实现了 `message/groups/payload`。这一点在仓库测试里可以验证——所有注解测试都用同一套标准入口：

```java
// 摘自 ValidX 测试用例（如 HourMinuteSecondValidatorTest）
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
```

把 `validate(model)` 换成 `validate(model, XxxGroup.class)`，就是分组验证。**注解方式的分组支持是天然可用的，0 配置。**

---

## 三、Default 组：没写 groups 的注解去哪了

### 3.1 隐式归属

不写 `groups` 的约束，隐式属于 `javax.validation.groups.Default` 组。这是最容易踩的坑：

```java
public class UserDTO {
    @NotBlank(groups = CreateGroup.class, message = "创建时必填")
    private String username;

    @Email                               // 属于 Default 组
    private String email;
}
```

调用 `validate(dto, CreateGroup.class)` 时，**只有 username 的 @NotBlank 执行，email 的 @Email 不执行**——因为触发的是 `CreateGroup`，而 `@Email` 挂在 `Default` 上。

### 3.2 三个判定规则

| 调用方式 | 执行的约束 | 说明 |
|---------|-----------|------|
| `validate(dto)` | 只执行 `Default` 组 | 未标 groups 的约束全部执行 |
| `validate(dto, CreateGroup.class)` | 只执行 `CreateGroup` | 未标 groups 的约束**全部跳过** |
| `validate(dto, Default.class, CreateGroup.class)` | `Default` + `CreateGroup` | 传多个组即可合并 |

> 也就是说：**一旦指定了任意非 Default 分组，那些"忘了写 groups"的约束就会悄悄失效。** 这是分组验证最常见的"看起来没生效"的原因。

### 3.3 让分组"包含"默认规则

希望"用 CreateGroup 时默认规则也执行"，让分组接口**继承 Default**：

```java
public class UserDTO {
    /** 让 CreateGroup 包含默认规则 */
    public interface CreateGroup extends Default {}
}
```

这样 `validate(dto, CreateGroup.class)` 会同时执行 `CreateGroup` 组约束和 `Default` 组约束。命名上更推荐 `CreateGroup extends Default` 而不是反过来，语义是"创建时包含通用规则"。

---

## 四、组继承：子组天然拥有父组规则

分组接口之间可以互相继承，**验证子组时，父组的约束也一并执行**：

```java
public class UserDTO {

    /** 基础规则：所有写操作都要校验 */
    public interface BasicGroup {}

    /** 创建 = 基础 + 创建专属 */
    public interface CreateGroup extends BasicGroup {}

    /** 更新 = 基础 + 更新专属 */
    public interface UpdateGroup extends BasicGroup {}

    @NotBlank(groups = BasicGroup.class, message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度 2~20")
    private String username;

    @Null(groups = CreateGroup.class, message = "创建时 ID 必须为空")
    @NotNull(groups = UpdateGroup.class, message = "更新时 ID 不能为空")
    private Long id;
}
```

行为：

| 触发分组 | 执行的约束 |
|---------|-----------|
| `BasicGroup` | username 必填/长度 |
| `CreateGroup` | username 规则 + id 必须为 null |
| `UpdateGroup` | username 规则 + id 不能为 null |

**继承解决的是"公共规则只写一次"**：把每写必用的约束挂到父组，各场景组继承它，不用在子组里重复标注。

---

## 五、@GroupSequence：控制验证顺序与短路

### 5.1 为什么需要顺序

默认情况下分组验证**无序**，全部约束都跑一遍。但有些场景要求"先轻后重"：

- 先做**格式校验**（字段格式对不对），再做**业务校验**（值在不在合理范围）——格式都错了，业务校验白算；
- 先做**成本低的校验**，再做**成本高的校验**（如查库）。

`@GroupSequence` 让分组按指定顺序验证，**前一个分组有约束失败，后面分组直接跳过**（短路）。

```java
import javax.validation.GroupSequence;

/** 先校验基础格式，再校验业务规则 */
@GroupSequence({BasicGroup.class, BusinessGroup.class})
public class UserDTO {

    public interface BasicGroup {}

    public interface BusinessGroup {}

    @NotBlank(groups = BasicGroup.class, message = "用户名不能为空")
    private String username;

    @NotNull(groups = BusinessGroup.class, message = "用户名不能重复")
    private String usernameUniqueCheck;   // 业务校验示例（实际校验逻辑由自定义验证器承担）
}
```

注意：顺序组只有在**验证 `Default` 组**时才生效——规范规定，触发顺序组的正确姿势是把它作为 `Default` 组的定义（类上标注的 `@GroupSequence` 会把 `Default` 组重定义为序列），所以最稳妥的写法是把默认组加进序列尾部：

```java
@GroupSequence({BasicGroup.class, BusinessGroup.class, Default.class})
public class UserDTO { ... }
```

验证时照常 `validate(dto, Default.class)`（等价于 `validate(dto)`），就会按 `BasicGroup → BusinessGroup → Default` 的顺序执行，前一组的约束有失败，后面的组直接跳过。

### 5.2 实用建议

`@GroupSequence` 适合**同一字段上"格式 → 业务"分级**，以及**多字段校验成本差异大**的场景。如果只是"创建/更新规则不同"，用普通分组即可，不要为用而用。

---

## 六、分组验证在 Spring Boot 中的落地

### 6.1 @Validated 指定分组

Spring 里区分两类注解：

| 注解 | 支持分组 | 适用 |
|------|:---:|------|
| `@Valid` | ❌ | 无分组的常规校验、嵌套对象递归 |
| `@Validated` | ✅ | 需要指定分组时（写在方法参数上） |

```java
@RestController
public class UserController {

    @PostMapping("/users")
    public Result create(@Validated(UserDTO.CreateGroup.class) @RequestBody UserDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/users/{id}")
    public Result update(@Validated(UserDTO.UpdateGroup.class) @RequestBody UserDTO dto) {
        return userService.update(dto);
    }
}
```

**坑点**：Controller 方法参数上用了 `@Validated` 后，类上的 `@Validated` 就不必再写；方法参数级 `@Validated(分组)` 只对该参数生效。

### 6.2 嵌套对象的分组：@ConvertGroup

嵌套对象默认沿用外层分组，需要"把外层分组映射成内层分组"时用 `@ConvertGroup`：

```java
public class OrderDTO {

    @NotNull(groups = SubmitGroup.class, message = "收货地址不能为空")
    @Valid
    @ConvertGroup(from = SubmitGroup.class, to = AddressDTO.SubmitGroup.class)
    private AddressDTO address;
}
```

### 6.3 统一异常处理

分组验证失败同样抛 `MethodArgumentNotValidException`（参数校验失败），与无分组完全一致，全局异常处理器无需改动：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        return Result.fail(400, msg);
    }
}
```

---

## 七、ValidX 的能力边界：注解支持分组，链式 API 不支持

### 7.1 两种模式的分组支持对比

这是理解 ValidX 必须记住的分工：

| 能力 | 注解方式 | 链式 API |
|------|:---:|:---:|
| 分组验证 | ✅ `groups` 原生支持 | ❌ 无分组概念 |
| 触发方式 | `validate(dto, Group.class)` / `@Validated(Group.class)` | `field(...).isXxx(...)` 全部当场执行 |
| 适用 | DTO 数据契约、多场景复用 | 动态数据、临时校验、非 Spring 环境 |

对照源码可以确认：ValidX 全部注解（`ChineseName`、`ChineseIdCard`、`PhoneNumber`、`Email`、`Date`、`Timestamp`……100+ 个）都实现了标准的 `groups()`/`payload()`；而链式入口 `chain/ValidX.java` 中没有任何 group 相关方法——`isValid()` 就是全部校验结果，**不支持按分组选择性执行**。

### 7.2 链式 API 需要"分组"怎么办

链式 API 没有分组，两种替代方案：

**方案一：职责拆分**。把"必填/格式"这类场景差异放到注解 DTO 上走分组，链式 API 只做流程内的临时校验，各司其职：

```java
// 注解 DTO：分组管"什么场景校验什么"
// 链式 API：管"动态数据当场校验"
ValidX validator = ValidX.init()
        .field("回调时间戳").isTimestamp(notify.getTimestamp(), Timestamp.TimestampUnit.SECONDS);
```

**方案二：if 分支模拟**。业务场景本身可以用分支表达：

```java
if (isCreate) {
    validator.field("用户名").isRequired(username);
} else {
    validator.field("用户名").isAlphaNum(username);
}
```

两种方案都不完美，但足够清晰。**凡是"同一 DTO 多场景复用"，优先用注解方式 + 分组**——这正是本文的适用面。

---

## 八、实战：用户资料 DTO 全场景覆盖

把前面的知识串起来，写一个覆盖"注册 / 更新 / 审核"三个场景的完整 DTO：

```java
import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.groups.Default;
import io.github.vipxieliang.validx.annotations.*;

public class UserDTO {

    // ===== 分组定义 =====

    /** 公共规则（所有写操作） */
    public interface BasicGroup {}

    /** 注册 */
    public interface CreateGroup extends BasicGroup, Default {}

    /** 更新资料 */
    public interface UpdateGroup extends BasicGroup, Default {}

    /** 运营审核 */
    public interface AuditGroup extends Default {}

    // ===== 字段 =====

    @Null(groups = CreateGroup.class, message = "注册时 ID 必须为空")
    @NotNull(groups = UpdateGroup.class, message = "更新时 ID 不能为空")
    private Long id;

    @NotBlank(groups = BasicGroup.class, message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度 2~20")
    @AlphaNum                            // ValidX：字母数字
    private String username;

    @Email                               // Default 组：所有场景都校验格式
    private String email;

    @NotBlank(groups = CreateGroup.class, message = "注册时密码必填")
    @Password(minLength = 8, groups = CreateGroup.class, message = "密码至少 8 位")
    private String password;

    @PhoneNumber                         // Default 组：有值才校验格式
    private String phone;

    @NotBlank(groups = AuditGroup.class, message = "审核意见不能为空")
    @Size(max = 200, message = "审核意见最多 200 字")
    private String auditRemark;
}
```

三个场景触发：

```java
// 注册：创建专属 + 公共 + 默认（email 格式、phone 格式也校验）
validator.validate(dto, UserDTO.CreateGroup.class);

// 更新：更新专属 + 公共 + 默认
validator.validate(dto, UserDTO.UpdateGroup.class);

// 审核：只校验审核意见 + 默认格式
validator.validate(dto, UserDTO.AuditGroup.class);
```

这个 DTO 在 Spring 中对应三个接口：

```java
@PostMapping("/register")
public Result register(@Validated(UserDTO.CreateGroup.class) @RequestBody UserDTO dto) { ... }

@PutMapping("/profile")
public Result updateProfile(@Validated(UserDTO.UpdateGroup.class) @RequestBody UserDTO dto) { ... }

@PostMapping("/audit")
public Result audit(@Validated(UserDTO.AuditGroup.class) @RequestBody UserDTO dto) { ... }
```

一个 DTO、三套规则、零 `if` 分支。字段规则变更只改一处，新增场景只加一个分组接口。

---

## 九、常见坑清单

1. **指定分组后"忘了写 groups"的约束全失效**——`@Email` 没写 `groups`，用 `CreateGroup` 触发时它不执行。解决办法：让分组 `extends Default`，或明确给约束标组。
2. **Spring 里用 `@Valid` 传分组**——`@Valid` 不支持分组参数，指定分组必须用 `@Validated(分组.class)`。
3. **类上和方法参数上同时写 `@Validated`**——方法参数级会覆盖类级，避免混淆，分组写在需要的那一层。
4. **嵌套对象分组没对齐**——内层 DTO 的约束挂了内层分组，外层触发的是外层分组，用 `@ConvertGroup` 显式映射。
5. **`@GroupSequence` 不生效**——顺序组必须通过 `Default` 触发（把顺序组作为 `Default` 的定义），直接 `validate(dto, 某顺序组.class)` 不会按序列执行。
6. **链式 API 里找分组**——链式 API 没有分组，别在 `field(...)` 上找 `group()` 方法，那是注解方式的特性。
7. **分组接口误加方法**——分组接口必须是空接口（纯标记），加方法会破坏分组语义。

---

## 十、最佳实践清单

1. **分组接口就近嵌套**：放在 DTO 内部（`UserDTO.CreateGroup`），字段分组一目了然；
2. **公共规则上提父组**：多场景共用的约束挂父组，子组 `extends` 父组，避免重复标注；
3. **必填差异用分组，格式校验放 Default**：格式类约束（`@Email`、`@PhoneNumber`、`@AlphaNum`）默认挂 Default 全场景生效，只有"必填与否"的差异才用分组区分；
4. **一个字段一个语义**：同一字段多分组标注时，`groups` 数组一次写全，别用两行相同约束制造歧义；
5. **Spring 中分组只写在方法参数**：`@Validated(CreateGroup.class)` 直接标注在方法参数上，作用域最清晰；
6. **顺序校验才用 @GroupSequence**：普通创建/更新差异不需要顺序，别为用而用；
7. **文档写明分组触发点**：接口文档标注"创建/更新/审核分别校验哪些分组"，让调用方知道错误信息的范围；
8. **与链式 API 分工**：多场景 DTO 用注解 + 分组，动态数据临时校验用链式 API，各司其职。

---

## 总结

- 分组验证是 Bean Validation 标准机制，ValidX 所有注解原生支持 `groups()`，与 `@NotBlank`/`@Size` 等标准注解完全混用，0 配置；
- 核心三要素：**定义分组接口 → 注解标 `groups` → 验证时传分组**；
- 不写 `groups` 的约束属于 `Default` 组，**指定非 Default 分组后它们会失效**，可用 `分组 extends Default` 合并；
- 组继承让"公共规则只写一次"，`@GroupSequence` 提供顺序验证与短路；
- Spring 中用 `@Validated(分组.class)` 指定分组，`@Valid` 不支持；
- **ValidX 的能力边界**：注解方式支持分组，链式 API（`chain/ValidX.java`）不支持——多场景 DTO 复用请走注解方式。

> ValidX 是基于 Jakarta Bean Validation 规范的 Java 验证库，注解与链式 API 双模式，内置 100+ 验证规则。项目地址：`github.com/vipxieliang/validx`
