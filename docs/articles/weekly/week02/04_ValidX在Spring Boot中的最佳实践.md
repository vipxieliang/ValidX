# ValidX 在 Spring Boot 中的最佳实践

## 引言

Spring Boot 是 Java 后端的事实标准，而**数据校验是每个接口的刚需**。很多项目在验证上走了两个极端：要么只靠 Controller 里的一堆 `if` 判断，逻辑散落各处、难以维护；要么只用了 `@NotNull`/`@Size` 等标准注解，面对身份证、手机号、银行卡这类中国业务场景，还得自己写正则、自己写算法。

ValidX 的出现正是为了解决这个问题：它在标准 Bean Validation 之上，提供了 100+ 针对中国业务场景的验证注解，并且与 Spring Boot **零配置无缝集成**。

本文从真实项目出发，给出 ValidX 在 Spring Boot 中的一套完整最佳实践：**Controller 层注解验证 + Service 层链式验证 + 全局异常处理 + 多语言错误消息**，并总结常见坑与规避方案。

---

## 一、为什么选择 ValidX？

### 1.1 与 Hibernate Validator 是互补，不是替代

Spring Boot 自带 Hibernate Validator（JSR-380 参考实现），提供了 `@NotNull`、`@Size`、`@Pattern` 等标准约束。ValidX **完全兼容这些标准注解**，同时补充了标准库没有的中国业务规则：

| 场景 | 标准库做法 | ValidX 做法 |
|------|-----------|------------|
| 身份证 | 自己写正则 + 校验位算法 | `@ChineseIdCard` |
| 手机号 | 自己维护号段正则 | `@ChinesePhone` |
| 银行卡 | 自己实现 Luhn 算法 | `@BankCard` |
| 统一社会信用代码 | 自己实现 18 位加权算法 | `@UnifiedSocialCreditCode` |
| 邮箱/URL/时间格式 | ✅ 标准可用 | 更严格的时间格式验证 |

### 1.2 两种使用方式，正好对应分层架构

| 使用方式 | 适用位置 | 典型场景 |
|---------|---------|---------|
| **注解式** | Controller 层 DTO | 接口参数校验，Spring 自动触发 |
| **链式 API** | Service 层业务逻辑 | 动态数据（Map/JSON）、跨字段校验、条件校验 |

---

## 二、快速集成：3 步接入 Spring Boot

### 2.1 添加依赖

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.1.0</version>
</dependency>
```

> **注意**：建议使用最新版本（当前最新为 v1.2.0）。ValidX 的唯一外部依赖是 Bean Validation API，与 Spring Boot 已内置的 Validator 完全兼容，**无需任何额外配置**。

### 2.2 编写 DTO 并标注注解

```java
public class UserRegistrationDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度需在 2-20 位之间")
    private String username;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @ChineseIdCard(message = "身份证号格式不正确")
    private String idCard;

    @Password(minLength = 8, message = "密码至少 8 位且需包含大小写字母、数字")
    private String password;

    // getters and setters...
}
```

### 2.3 Controller 中启用自动校验

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegistrationDTO dto) {
        // Spring 自动校验，失败时抛出 MethodArgumentNotValidException
        return Result.success(userService.register(dto));
    }
}
```

**三步完成集成。** 校验失败时 Spring 自动抛出 `MethodArgumentNotValidException`，配合第五节中的全局异常处理器，即可返回统一格式的错误响应。

---

## 三、最佳实践一：Controller 层注解验证

### 3.1 请求 DTO 与实体分离

**不要让实体直接暴露给 Controller**。实体的校验规则（如数据库唯一性、业务状态）与接口入参规则往往不同：

```java
// ❌ 错误：直接用实体接收请求
@PostMapping("/create")
public Result create(@Valid @RequestBody User user) { ... }

// ✅ 正确：独立 DTO，按接口语义定义规则
@PostMapping("/create")
public Result create(@Valid @RequestBody UserCreateDTO dto) { ... }
```

不同接口使用不同 DTO，规则互不污染：`UserCreateDTO`（创建必填全部字段）、`UserUpdateDTO`（部分字段可选）、`UserLoginDTO`（只需账号密码）。

### 3.2 必填与可选字段的正确组合

ValidX 与 Bean Validation 的 Null/空值语义一致，**组合注解时务必区分三种必填语义**：

| 组合 | 语义 | 适用字段 |
|------|------|---------|
| `@NotBlank` + `@Email` | 必填且格式正确 | 手机号、邮箱 |
| `@NotNull` + `@ChineseIdCard` | 必填但不拒绝空字符串（少用） | 特殊业务 |
| 仅 `@QQ` | 可选，有值才校验 | 昵称、QQ、微信号 |

```java
public class UserUpdateDTO {
    // 必填：非 null、非空、非空白
    @NotBlank
    @ChinesePhone
    private String phone;

    // 可选：为 null/空 时跳过校验，有值则必须合法
    @QQ
    private String qq;

    // 可选但非空：可以有 null，但不能是空字符串
    @NotEmpty
    @WeChat
    private String weChat;
}
```

### 3.3 分组验证：不同场景不同规则

同一个 DTO 在"创建"和"更新"场景往往要求不同，用标准 `groups` 机制解决：

```java
public class UserDTO {

    public interface CreateGroup {}
    public interface UpdateGroup {}

    // 创建时必须填 id（如为 null 则表示新增，这里语义按业务定义）
    @NotNull(groups = UpdateGroup.class, message = "更新时 ID 不能为空")
    private Long id;

    @NotBlank(groups = CreateGroup.class, message = "创建时用户名必填")
    @Size(min = 2, max = 20)
    private String username;

    @ChinesePhone
    private String phone;
}
```

```java
@PostMapping("/create")
public Result create(@Validated(UserDTO.CreateGroup.class) @RequestBody UserDTO dto) { ... }

@PutMapping("/update")
public Result update(@Validated(UserDTO.UpdateGroup.class) @RequestBody UserDTO dto) { ... }
```

> **注意**：分组验证请使用 `@Validated`（支持指定分组），普通 `@Valid` 不支持传分组参数。

### 3.4 嵌套对象验证：@Valid

DTO 内部嵌套对象时，必须在内层字段上加 `@Valid` 才能触发递归校验：

```java
public class OrderCreateDTO {
    @NotBlank
    private String orderNo;

    @Valid           // 关键：触发嵌套校验
    private AddressDTO address;
}

public class AddressDTO {
    @NotBlank
    private String province;

    @NotBlank
    private String detailAddress;
}
```

### 3.5 集合与 Map 校验

```java
public class BatchDTO {
    @NotEmpty(message = "ID 列表不能为空")
    @Size(max = 100, message = "单次最多提交 100 个")
    private List<@Valid @Positive Long> ids;  // 元素级校验

    @NotEmpty
    private Map<@NotBlank String, @NotBlank String> attributes;
}
```

---

## 四、最佳实践二：Service 层链式验证

注解验证适合"结构已知"的 DTO，但当数据是 **Map / JSON / 外部系统返回** 时，注解无能为力——这正是链式 API 的主场。

### 4.1 动态数据验证

```java
@Service
public class UserService {

    public void validateRegistration(Map<String, Object> request) {
        ValidX validator = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)   // 全局：大部分字段必填
            .field("邮箱").isEmail(request.get("email"))
            .field("手机号").isChinesePhone(request.get("phone"))
            .field("身份证").isChineseIdCard(request.get("idCard"))
            .field("QQ（可选）").allowNull().isQQ(request.get("qq"));

        if (!validator.passed()) {
            throw new BusinessException(validator.getErrors());
        }
    }
}
```

**为什么 Service 层需要链式校验？** 注解只能写在类字段上，而 Map 的 key 是动态的、字段可缺失（返回 null 是正常情况）。链式 API 天然适配这种"字段可能存在也可能不存在"的数据。

### 4.2 全局配置 + 局部覆盖

链式校验的默认行为是 **null 和空字符串直接通过**（与注解一致，符合 JSR-380）。在 Service 层通过配置精确控制：

```java
// 全局严格 + 局部宽松
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_EMPTY)      // 全局拒绝 null/空字符串
    .field("邮箱").isEmail(data.get("email"))    // 必填且格式正确
    .field("QQ").allowNull().isQQ(data.get("qq")); // 例外：可选字段
```

**优先级规则：局部状态 > 全局配置 > 默认行为。**

可用的全局配置与局部状态：

| 级别 | API | 语义 |
|------|-----|------|
| 全局 | `ValidXConfig.DEFAULT` | 允许 null 和空字符串（默认） |
| 全局 | `ValidXConfig.GLOBAL_NOT_NULL` | 所有字段不能为 null |
| 全局 | `ValidXConfig.GLOBAL_NOT_EMPTY` | 所有字段不能为 null 或空字符串 |
| 局部 | `.notNull()` | 当前字段不能为 null |
| 局部 | `.notEmpty()` | 当前字段不能为 null 或空字符串 |
| 局部 | `.allowNull()` | 当前字段允许 null（跳过验证） |
| 局部 | `.allowEmpty()` | 当前字段允许空字符串（不允许 null） |
| 局部 | `.field("标签")` | 错误消息携带自定义字段名 |

> **最佳实践**：`config()` 只在链的开头调用一次；不同配置需求请拆成多个验证器实例，避免在链中间反复切换导致难以追踪。

### 4.3 局部状态自动重置

`.notNull()`/`.allowNull()` 等局部状态在每次验证方法调用后**自动重置**，因此每个字段独立生效：

```java
validator.notEmpty().isEmail(email1)   // notEmpty 只作用于 email1
         .isEmail(email2)              // email2 回到默认行为
         .notEmpty().isPhone(phone);   // notEmpty 只作用于 phone
```

### 4.4 校验时机：写入数据库之前

链式校验的代码应放在**业务逻辑执行前**（尤其是数据库写入、外部调用之前），遵循"fail fast"原则：

```java
public User register(UserRegistrationDTO dto) {
    // 1. 先校验（链式或注解结果）
    validateRegistration(dto);

    // 2. 再执行业务（避免无效写入产生脏数据）
    User user = new User();
    BeanUtils.copyProperties(dto, user);
    return userRepository.save(user);
}
```

---

## 五、最佳实践三：统一异常处理

Spring 校验失败抛出的异常类型不同，但业务方只关心**统一的错误响应格式**。用 `@RestControllerAdvice` 集中处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 注解校验失败：@Valid/@Validated 触发 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResult> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ErrorResult.of(400, message));
    }

    /** 方法参数/路径变量校验失败 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResult> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ErrorResult.of(400, message));
    }

    /** 链式校验：业务侧主动抛出 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResult> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest().body(ErrorResult.of(e.getCode(), e.getMessage()));
    }

    /** 兜底 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleException(Exception e) {
        return ResponseEntity.status(500).body(ErrorResult.of(500, "系统繁忙，请稍后重试"));
    }
}
```

统一的错误响应体：

```java
public record ErrorResult(int code, String message) {
    public static ErrorResult of(int code, String message) {
        return new ErrorResult(code, message);
    }
}
```

Service 层链式校验建议**自定义业务异常**（如 `BusinessException`），携带错误码和错误列表，让 Controller 层与异常处理层解耦：

```java
public class BusinessException extends RuntimeException {
    private final int code;
    private final List<String> errors;

    public BusinessException(List<String> errors) {
        super(String.join("; ", errors));
        this.code = 400;
        this.errors = errors;
    }
    // getters...
}
```

---

## 六、最佳实践四：多语言错误消息

ValidX 内置 9 种语言的错误消息，在 Spring Boot 中**自动跟随请求头 `Accept-Language`**：

```http
POST /api/users/register
Accept-Language: zh-CN    → 中文错误消息
Accept-Language: en-US    → 英文错误消息
```

Controller 层无需任何额外代码。链式 API 中可显式指定语言：

```java
// 自动使用系统默认语言
ValidX v1 = ValidX.init().isEmail("invalid-email");

// 显式指定中文
ValidX v2 = ValidX.init()
        .withLocale(Locale.SIMPLIFIED_CHINESE)
        .isEmail("invalid-email");

// 显式指定英文
ValidX v3 = ValidX.init()
        .withLocale(Locale.ENGLISH)
        .isEmail("invalid-email");
```

> **最佳实践**：注解自定义 `message` 时注意与多语言配置的关系——自定义消息优先级最高，会覆盖 ValidX 的内置多语言消息。

---

## 七、最佳实践五：常见坑与规避

### 7.1 不要共享 ValidX 实例

**ValidX 实例不是线程安全的**，内部使用可变状态（局部要求标志、字段标识、错误列表）。跨线程共享会导致竞态条件和错误结果：

```java
// ❌ 错误：静态共享实例（Spring 默认单例，高并发下必出问题）
@Service
public class UserService {
    private static final ValidX VALIDATOR = ValidX.init();

    public void validate(User user) {
        VALIDATOR.isEmail(user.getEmail());  // 线程不安全！
    }
}

// ✅ 正确：每次校验创建新实例
@Service
public class UserService {
    public void validate(User user) {
        ValidX validator = ValidX.init()
                .isEmail(user.getEmail())
                .isPhone(user.getPhone());

        if (!validator.isValid()) {
            throw new BusinessException(validator.getErrors());
        }
    }
}
```

> 这与 `StringBuilder`、Java 8 `Stream` 的"创建-使用-丢弃"模式一致。`ValidXConfig` 对象和各个 Validator 类是无状态的，可以安全复用。

### 7.2 @Valid 与 @Validated 的区别

| 对比项 | @Valid | @Validated |
|--------|--------|------------|
| 来源 | javax.validation（标准） | Spring 框架 |
| 分组支持 | 不支持指定分组 | ✅ 支持 `groups` |
| 类级校验 | 支持嵌套 | 支持嵌套 + 类级约束 |
| 建议 | 方法参数 / 嵌套字段 | 需要分组、类级约束时 |

**经验法则**：纯字段校验用 `@Valid`；需要分组验证或类级约束（如密码确认这类跨字段规则）时，在类上标 `@Validated`。

### 7.3 理解 Null/空值语义，避免"必填没生效"

ValidX 所有验证注解对 **null 和空字符串默认放行**（遵循 JSR-380）。所以"必填"必须显式声明：

```java
// ❌ 错误：期望"必填手机号"，但 null/"" 都会通过
@ChinesePhone
private String phone;

// ✅ 正确：@NotBlank 负责必填，@ChinesePhone 负责格式
@NotBlank(message = "手机号不能为空")
@ChinesePhone
private String phone;
```

### 7.4 Lombok 与校验

校验通过 getter/setter 反射触发，使用 Lombok 时必须生成对应方法：

```java
@Data                     // 生成 getter/setter，校验才能生效
public class UserDTO {
    @NotBlank
    private String username;
}
```

若字段缺失 getter，Spring 校验会静默跳过（不报错、不校验），排查时很隐蔽。

### 7.5 性能：单次校验的额外开销可忽略

ValidX 的 Validator 类是无状态、可复用的，注解校验在 Spring 中默认有缓存（`Validator` 单例），链式 API 每实例的开销也极小。**无需做任何缓存优化，正常使用即可**。

---

## 八、总结：最佳实践检查清单

| 层 | 最佳实践 | 状态 |
|----|---------|------|
| 依赖 | 使用最新版本 ValidX，无需额外配置 | ☐ |
| DTO | 请求 DTO 与实体分离，按接口定义独立 DTO | ☐ |
| DTO | 必填字段显式加 `@NotBlank`/`@NotEmpty`，可选字段只用格式注解 | ☐ |
| DTO | 嵌套对象字段加 `@Valid` | ☐ |
| DTO | 需要分组时用 `@Validated(groups = ...)` | ☐ |
| Controller | 方法参数加 `@Valid` 或 `@Validated` | ☐ |
| Service | 动态数据（Map/JSON）用链式 API + `.field()` 标签 | ☐ |
| Service | `config()` 只调用一次，局部状态只修饰单个字段 | ☐ |
| Service | 校验在数据库写入之前执行（fail fast） | ☐ |
| Service | 每次校验新建 ValidX 实例，绝不共享静态实例 | ☐ |
| 异常 | `@RestControllerAdvice` 统一处理三类校验异常 | ☐ |
| i18n | 依赖 `Accept-Language` 自动适配，或链式 `withLocale()` | ☐ |
| 测试 | 为关键接口补充校验失败的单元/集成测试 | ☐ |

---

## 结语

ValidX 在 Spring Boot 中的定位非常清晰：**注解方式接管 Controller 层的"标准可预测"校验，链式 API 接管 Service 层的"动态不可预测"校验**，两者配合统一异常处理，就能覆盖一个项目 90% 以上的数据校验需求。

如果你的项目面向中国用户，还在为身份证、手机号、银行卡的校验逻辑发愁，不妨给 ValidX 一次机会——**一次引入，终身受益，不再造轮子**。
