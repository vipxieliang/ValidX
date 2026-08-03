# ValidX 快速开始指南

本指南将帮助你在 5 分钟内开始使用 ValidX。

## 目录

- [添加依赖](#添加依赖)
- [第一个示例](#第一个示例)
- [常见场景](#常见场景)
- [常见问题（FAQ）](#常见问题faq)

---

## 添加依赖

### Maven

在你的 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Gradle

在你的 `build.gradle` 文件中添加：

```gradle
implementation 'io.github.vipxieliang:validx:1.0.1'
```

**说明：**
- ValidX 已经包含了 `hibernate-validator` 和 `validation-api` 依赖
- 如果你的 Spring Boot 项目中已有 `spring-boot-starter-web` 或 `spring-boot-starter-validation`，Maven 会自动处理版本冲突
- 无需额外配置，ValidX 的注解可以直接配合 `@Valid` 使用

---

## 第一个示例

让我们从最简单的身份证验证开始。

### 方式一：注解方式（推荐用于 DTO）

```java
import io.github.vipxieliang.validx.annotations.ChineseIdCard;
import javax.validation.constraints.NotBlank;

public class UserDTO {
    @NotBlank(message = "身份证号不能为空")
    @ChineseIdCard
    private String idCard;

    // getter 和 setter
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}
```

在 Spring Boot Controller 中使用：

```java
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping("/register")
    public String register(@Valid @RequestBody UserDTO userDTO) {
        // 如果验证失败，Spring 会自动返回 400 错误
        return "注册成功！身份证号：" + userDTO.getIdCard();
    }
}
```

### 方式二：链式调用（推荐用于业务逻辑）

```java
import io.github.vipxieliang.validx.chain.ValidX;

public class UserService {

    public void validateIdCard(String idCard) {
        ValidX validator = ValidX.init()
                .field("身份证号").notEmpty().isChineseIdCard(idCard);

        if (!validator.passed()) {
            // 验证失败
            String errorMessage = validator.getErrorMessage();
            throw new IllegalArgumentException(errorMessage);
        }

        // 验证成功，继续业务逻辑
        System.out.println("身份证验证通过：" + idCard);
    }
}
```

### 测试一下

```java
public class QuickStartTest {

    public static void main(String[] args) {
        ValidX validator = ValidX.init();

        // 验证有效的身份证号
        validator.isChineseIdCard("110101199001011234");
        System.out.println("验证结果：" + (validator.passed() ? "通过" : "失败"));

        // 验证无效的身份证号
        ValidX validator2 = ValidX.init();
        validator2.isChineseIdCard("123456");
        System.out.println("验证结果：" + (validator2.passed() ? "通过" : "失败"));
        System.out.println("错误信息：" + validator2.getErrorMessage());
    }
}
```

输出：
```
验证结果：通过
验证结果：失败
错误信息：身份证号码格式不正确
```

---

## 常见场景

### 场景 1：用户注册表单验证

注册表单通常需要验证多个字段：邮箱、手机号、密码等。

#### 使用注解方式

```java
import io.github.vipxieliang.validx.annotations.*;
import javax.validation.constraints.*;

public class UserRegistrationDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 之间")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Password(minLength = 8, requireSpecialChar = true)
    private String password;

    @ChineseIdCard
    private String idCard;  // 可选字段

    // getters and setters...
}
```

Controller 使用：

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO dto) {
        // Spring 自动验证，失败会返回 400 和详细错误信息
        userService.register(dto);
        return ResponseEntity.ok("注册成功");
    }
}
```

#### 使用链式调用

```java
@Service
public class UserService {

    public void validateRegistration(Map<String, Object> data) {
        ValidX validator = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)  // 所有字段不允许为空

            .field("用户名").isAlphaNumber(data.get("username"))
            .field("邮箱").isEmail(data.get("email"))
            .field("手机号").isChinesePhone(data.get("phone"))
            .field("密码").isPassword(data.get("password"), 8)
            .field("身份证").allowNull().isChineseIdCard(data.get("idCard"));  // 允许为空

        if (!validator.passed()) {
            throw new IllegalArgumentException(String.join(", ", validator.getErrors()));
        }
    }
}
```

---

### 场景 2：订单信息验证

电商订单需要验证收货地址、联系方式等。

```java
import io.github.vipxieliang.validx.annotations.*;
import javax.validation.constraints.*;

public class OrderDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Chinese
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    @ChinesePhone
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String address;

    @NotBlank(message = "邮政编码不能为空")
    @ChineseZipCode
    private String zipCode;

    @Email
    private String email;  // 可选

    @TradeOrderNumber
    private String orderNumber;

    // getters and setters...
}
```

---

### 场景 3：企业信息验证

企业认证需要验证统一社会信用代码、银行卡等。

```java
import io.github.vipxieliang.validx.annotations.*;
import javax.validation.constraints.*;

public class CompanyDTO {

    @NotBlank(message = "企业名称不能为空")
    private String companyName;

    @NotBlank(message = "统一社会信用代码不能为空")
    @UnifiedSocialCreditCode
    private String creditCode;

    @NotBlank(message = "企业邮箱不能为空")
    @Email
    private String companyEmail;

    @NotBlank(message = "法人身份证不能为空")
    @ChineseIdCard
    private String legalPersonIdCard;

    @BankCard
    private String bankAccount;

    @NotBlank(message = "联系电话不能为空")
    @ChinesePhoneOrLandline
    private String contactPhone;

    // getters and setters...
}
```

---

### 场景 4：金融交易验证

金融应用需要严格的卡号、金额验证。

```java
@Service
public class PaymentService {

    public void validatePayment(PaymentRequest request) {
        ValidX validator = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)

            .field("银行卡号").isBankCard(request.getBankCardNumber())
            .field("CVV 安全码").isCVV(request.getCvv())
            .field("交易订单号").isTradeOrderNumber(request.getOrderNumber());

        if (!validator.passed()) {
            throw new IllegalArgumentException("支付信息验证失败: " + String.join(", ", validator.getErrors()));
        }

        // 继续支付逻辑...
    }
}
```

链式调用示例：

```java
ValidX validator = ValidX.init();

validator.field("银行卡号").notEmpty().isBankCard("6222021234567890123")
         .field("CVV").notEmpty().isCVV("123")
         .field("SWIFT 代码").allowNull().isSWIFT("COBADEFF")
         .field("IBAN").allowNull().isIBAN("DE44500800000123456789");

if (validator.passed()) {
    System.out.println("所有金融信息验证通过");
} else {
    System.out.println("验证失败：");
    for (String error : validator.getErrors()) {
        System.out.println("  - " + error);
    }
}
```

---

### 场景 5：动态表单验证

处理动态表单数据，例如从 JSON 解析的 Map。

```java
@Service
public class DynamicFormService {

    public void validateDynamicForm(Map<String, Object> formData) {
        ValidX validator = ValidX.init();

        // 只验证存在的字段
        if (formData.containsKey("email")) {
            validator.field("邮箱").notEmpty().isEmail(formData.get("email"));
        }

        if (formData.containsKey("phone")) {
            validator.field("手机号").notEmpty().isChinesePhone(formData.get("phone"));
        }

        if (formData.containsKey("idCard")) {
            validator.field("身份证").notEmpty().isChineseIdCard(formData.get("idCard"));
        }

        if (formData.containsKey("website")) {
            validator.field("网站").allowEmpty().isUrl(formData.get("website"));
        }

        if (!validator.passed()) {
            throw new IllegalArgumentException("表单验证失败: " + String.join(", ", validator.getErrors()));
        }
    }
}
```

---

## 常见问题（FAQ）

### 1. 为什么 null 值通过了验证？

**问题：**
```java
ValidX validator = ValidX.init();
validator.isEmail(null);
System.out.println(validator.passed());  // 输出：true ？？
```

**答案：**

这是 **JSR-380 Bean Validation 规范的设计**，也是 ValidX 的默认行为。原因是：

- **职责分离**：`@NotNull` 负责检查"字段是否存在"，格式验证注解负责检查"如果存在，格式是否正确"
- **可选字段支持**：很多业务场景中，字段是可选的（比如"备用邮箱"），null 值应该跳过验证

**解决方案：**

根据业务需求选择合适的方式：

#### 方式 1：注解组合（推荐）

```java
public class UserDTO {
    // 必填字段
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;

    // 可选字段
    @Email
    private String backupEmail;
}
```

#### 方式 2：链式调用配置

```java
// 全局配置：所有字段不允许 null
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("邮箱").isEmail(email)
    .field("手机号").isChinesePhone(phone);

// 或者单个字段配置
ValidX validator2 = ValidX.init()
    .field("邮箱").notEmpty().isEmail(email)  // 不允许 null 和空字符串
    .field("备用邮箱").allowNull().isEmail(backupEmail);  // 允许 null
```

---

### 2. 如何获取详细的错误信息？

**问题：** 我想知道具体哪个字段验证失败了。

**答案：**

#### 注解方式（Spring Boot）

Spring Boot 会自动返回详细的验证错误：

```java
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody UserDTO dto, BindingResult result) {
    if (result.hasErrors()) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
    return ResponseEntity.ok("注册成功");
}
```

#### 链式调用方式

```java
ValidX validator = ValidX.init()
    .field("邮箱").notEmpty().isEmail(email)
    .field("手机号").notEmpty().isChinesePhone(phone)
    .field("身份证").isChineseIdCard(idCard);

if (!validator.passed()) {
    // 方式 1：获取所有错误列表
    List<String> errors = validator.getErrors();
    for (String error : errors) {
        System.out.println(error);
    }

    // 方式 2：获取第一个错误信息
    String firstError = validator.getErrorMessage();
    System.out.println(firstError);
}
```

输出示例：
```
邮箱: 邮箱地址格式不正确
手机号: 值不能为空字符串
身份证: 身份证号码格式不正确
```

---

### 3. 如何切换错误消息的语言？

**问题：** 我想让错误消息显示英文而不是中文。

**答案：**

#### 方式 1：Spring Boot 自动识别（推荐）

Spring Boot 会根据 HTTP 请求头 `Accept-Language` 自动切换语言：

```bash
# 中文消息
curl -H "Accept-Language: zh-CN" http://localhost:8080/api/user/register

# 英文消息
curl -H "Accept-Language: en-US" http://localhost:8080/api/user/register
```

#### 方式 2：链式调用手动指定

```java
import java.util.Locale;

// 使用英文
ValidX validator = ValidX.init()
    .withLocale(Locale.ENGLISH)
    .field("Email").isEmail("invalid-email");

System.out.println(validator.getErrorMessage());
// 输出：Email: Invalid email address format

// 使用中文
ValidX validator2 = ValidX.init()
    .withLocale(Locale.SIMPLIFIED_CHINESE)
    .field("邮箱").isEmail("invalid-email");

System.out.println(validator2.getErrorMessage());
// 输出：邮箱: 邮箱地址格式不正确
```

#### 方式 3：全局设置语言

```java
import io.github.vipxieliang.validx.i18n.MessageManager;

// 设置全局语言（影响当前线程）
MessageManager.setCurrentLocale(Locale.ENGLISH);

ValidX validator = ValidX.init()
    .field("Email").isEmail("invalid-email");

System.out.println(validator.getErrorMessage());
// 输出：Email: Invalid email address format

// 清除全局设置
MessageManager.clearCurrentLocale();
```

**支持的语言：**
- 简体中文 (`Locale.SIMPLIFIED_CHINESE`)
- 英语 (`Locale.ENGLISH`)
- 日语 (`Locale.JAPANESE`)
- 韩语 (`Locale.KOREAN`)
- 法语 (`Locale.FRENCH`)
- 德语 (`Locale.GERMAN`)
- 西班牙语 (`new Locale("es")`)
- 俄语 (`new Locale("ru")`)

---

### 4. ValidX 是线程安全的吗？

**问题：** 我可以在多线程环境中使用 ValidX 吗？

**答案：**

**ValidX 实例不是线程安全的**，但使用方式很简单：

#### ❌ 错误用法

```java
// 不要共享 ValidX 实例！
private static final ValidX VALIDATOR = ValidX.init();

public void validate(User user) {
    VALIDATOR.isEmail(user.getEmail());  // 线程不安全！
}
```

#### ✅ 正确用法

```java
// 每次验证创建新实例
public void validate(User user) {
    ValidX validator = ValidX.init()
        .isEmail(user.getEmail())
        .isPhone(user.getPhone());

    if (!validator.passed()) {
        throw new IllegalArgumentException(String.join(", ", validator.getErrors()));
    }
}
```

**为什么这样设计？**

- ValidX 采用流式 API，内部维护可变状态（错误列表、字段标签等）
- 这种设计与 `StringBuilder`、Java 8 `Stream` 等流式 API 一致
- 创建实例的开销很小，无需担心性能问题

**线程安全的组件：**
- `ValidXConfig` 对象是不可变的，可以安全共享
- 各个验证器类（如 `ChineseIdCardValidator`）是无状态的，可以复用

---

### 5. 如何验证集合或数组？

**问题：** 我有一个字符串列表，想验证每个元素是否都是有效的邮箱。

**答案：**

#### 注解方式

```java
import javax.validation.constraints.NotEmpty;
import io.github.vipxieliang.validx.annotations.Email;

public class EmailListDTO {

    @NotEmpty(message = "邮箱列表不能为空")
    private List<@Email String> emails;

    // getters and setters...
}
```

#### 链式调用方式

ValidX 的 `@In` 和 `@NotIn` 注解支持集合验证：

```java
List<String> roles = Arrays.asList("admin", "user", "guest");

ValidX validator = ValidX.init()
    .isIn(roles, new String[]{"admin", "user", "guest", "moderator"});

if (validator.passed()) {
    System.out.println("所有角色都是有效的");
}
```

对于其他类型的集合验证，可以使用循环：

```java
List<String> emails = Arrays.asList("test@example.com", "invalid-email", "user@domain.com");
ValidX validator = ValidX.init();

for (int i = 0; i < emails.size(); i++) {
    validator.field("邮箱[" + i + "]").isEmail(emails.get(i));
}

if (!validator.passed()) {
    System.out.println("验证失败：");
    validator.getErrors().forEach(System.out::println);
}
```

---

### 6. 如何自定义验证规则？

**问题：** ValidX 没有我需要的验证规则，如何扩展？

**答案：**

#### 方式 1：组合现有验证器

```java
public void validateUsername(String username) {
    ValidX validator = ValidX.init()
        .field("用户名").notEmpty()
        .isAlphaNumber(username);

    // 额外的业务规则
    if (validator.passed() && (username.length() < 3 || username.length() > 20)) {
        validator = ValidX.init()
            .field("用户名").notEmpty();
        // 手动添加错误
    }
}
```

#### 方式 2：使用正则表达式（通过 @Pattern）

```java
import javax.validation.constraints.Pattern;

public class CustomDTO {

    // 自定义格式：必须以 USER_ 开头
    @Pattern(regexp = "^USER_[A-Z0-9]{6}$", message = "用户编号格式不正确")
    private String userCode;

    // getters and setters...
}
```

#### 方式 3：创建自定义注解验证器

```java
// 1. 定义注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomValidator.class)
public @interface CustomValidation {
    String message() default "自定义验证失败";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. 实现验证器
public class CustomValidator implements ConstraintValidator<CustomValidation, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;  // null 值交给 @NotNull 处理
        }

        // 你的自定义验证逻辑
        return value.startsWith("CUSTOM_");
    }
}

// 3. 使用
public class MyDTO {
    @CustomValidation
    private String customField;
}
```

---

### 7. ValidX 与 Hibernate Validator 有什么区别？

**问题：** 我已经在用 Hibernate Validator，还需要 ValidX 吗？

**答案：**

**ValidX 是 Hibernate Validator 的补充，而不是替代品。**

| 特性 | Hibernate Validator | ValidX |
|------|---------------------|--------|
| 标准验证 | ✅ `@NotNull`, `@Size`, `@Min`, `@Max` 等 | ❌ 不提供（使用标准注解） |
| 中国特色验证 | ❌ 不支持 | ✅ 身份证、手机号、银行卡等 90+ |
| 链式 API | ❌ 不支持 | ✅ 流式验证 API |
| 国际化 | ✅ 支持 | ✅ 8 种语言 |
| 动态验证 | ❌ 需要编程式 API | ✅ 链式调用方式 |

**推荐搭配使用：**

```java
import javax.validation.constraints.*;  // Hibernate Validator
import io.github.vipxieliang.validx.annotations.*;  // ValidX

public class UserDTO {
    // 使用标准注解
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20)
    private String username;

    // 使用 ValidX 中国特色注解
    @ChineseIdCard
    private String idCard;

    @ChinesePhone
    private String phone;

    // 标准注解 + ValidX 注解
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;
}
```

---

### 8. 性能如何？会影响应用速度吗？

**问题：** ValidX 的性能开销大吗？

**答案：**

ValidX 经过优化，性能开销极小：

- **轻量级**：JAR 包大小约 300KB
- **无反射**：大部分验证器使用正则表达式或算法，无反射开销
- **单一依赖**：除 Bean Validation API 外无其他依赖
- **优化算法**：身份证、银行卡等验证使用高效的校验算法

**性能对比：**

```java
// 简单性能测试
long start = System.currentTimeMillis();

for (int i = 0; i < 10000; i++) {
    ValidX validator = ValidX.init()
        .isChineseIdCard("110101199001011234")
        .isEmail("test@example.com")
        .isChinesePhone("13812345678");
}

long end = System.currentTimeMillis();
System.out.println("10000 次验证耗时：" + (end - start) + "ms");
// 通常 < 100ms
```

**建议：**

- 在 Web 应用中，验证开销远小于网络 I/O 和数据库操作
- 对于极高性能要求的场景，可以考虑缓存验证结果
- 使用注解方式比链式调用略快（编译时优化）

---

## 下一步

- 📖 查看[完整文档](README.md)了解所有验证注解
- 🎯 查看[高级用法](README.md#handling-nullempty-strings)了解空值处理策略
- 🌍 了解[多语言支持](README.md#multilingual-support)
- 💬 加入[讨论](https://github.com/vipxieliang/ValidX/discussions)
- 🐛 [报告问题](https://github.com/vipxieliang/ValidX/issues)

---

## 联系我们

如有问题或建议，欢迎联系：

📧 Email: [vipxieliang@126.com](mailto:vipxieliang@126.com)

⭐ 如果 ValidX 对你有帮助，请在 [GitHub](https://github.com/vipxieliang/ValidX) 或 [Gitee](https://gitee.com/vipxieliang/ValidX) 上给我们一个星标！
