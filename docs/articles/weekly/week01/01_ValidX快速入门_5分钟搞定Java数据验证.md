# ValidX快速入门：5分钟搞定Java数据验证

## 前言

在Java开发中，数据验证是每个项目都必须面对的问题。传统的手动验证代码冗长且容易出错，而ValidX作为一款专注于中国业务场景的验证框架，可以让你用最少的代码完成最完善的验证。

本文将用5分钟时间，带你快速上手ValidX。

---

## 为什么选择ValidX？

### 传统验证方式的痛点

```java
@PostMapping("/register")
public Result register(@RequestBody UserDTO dto) {
    // 验证身份证号
    String idCard = dto.getIdCard();
    if (idCard == null || idCard.isEmpty()) {
        return Result.error("身份证号不能为空");
    }
    if (!idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$")) {
        return Result.error("身份证号格式不正确");
    }
    // 还需要验证校验码...100+行代码

    // 验证手机号
    String phone = dto.getPhone();
    if (phone == null || phone.isEmpty()) {
        return Result.error("手机号不能为空");
    }
    if (!phone.matches("^1[3-9]\\d{9}$")) {
        return Result.error("手机号格式不正确");
    }

    // 还有邮箱、地址、日期...
    // 代码越来越长，维护越来越困难
}
```

**问题**：
- 代码冗长，可读性差
- 正则表达式难以维护
- 验证逻辑分散在各处
- 每个项目都要重复写

### ValidX的解决方案

```java
public class UserDTO {
    @NotBlank
    @ChineseIdCard
    private String idCard;

    @NotBlank
    @ChinesePhone
    private String phone;

    @Email
    private String email;
}

@PostMapping("/register")
public Result register(@Valid @RequestBody UserDTO dto) {
    // ValidX自动完成所有验证
    // 验证失败会自动返回错误信息
    return userService.register(dto);
}
```

**优势**：
- ✅ 一行注解搞定复杂验证
- ✅ 代码简洁，语义清晰
- ✅ 统一管理，易于维护
- ✅ 自动国际化错误消息

---

## 快速开始

### 第1步：添加依赖

**Maven**：
```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.2.0</version>
</dependency>

<!-- Spring Boot项目还需要 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Gradle**：
```gradle
implementation 'io.github.vipxieliang:validx:1.2.0'
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### 第2步：定义DTO

```java
import io.github.vipxieliang.validx.annotations.*;
import javax.validation.constraints.NotBlank;

public class UserRegistrationDTO {

    // 身份证验证
    @NotBlank(message = "身份证号不能为空")
    @ChineseIdCard
    private String idCard;

    // 手机号验证
    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;

    // 邮箱验证
    @Email
    private String email;

    // 真实姓名验证（中文姓名）
    @NotBlank
    @ChineseName
    private String realName;

    // 出生日期验证
    @NotBlank
    @Date
    private String birthDate;

    // getter和setter省略...
}
```

### 第3步：使用验证

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegistrationDTO dto) {
        // @Valid 触发验证
        // 如果验证失败，Spring会自动返回400错误
        // 如果验证通过，继续执行业务逻辑

        return userService.register(dto);
    }
}
```

### 第4步：配置全局异常处理（可选）

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        return Result.error("参数验证失败", errors);
    }
}
```

---

## 常用注解速查

### 中国业务专用注解

| 注解 | 说明 | 示例 |
|------|------|------|
| `@ChineseIdCard` | 中国身份证号（15/18位） | 110101199001011234 |
| `@ChinesePhone` | 中国手机号（11位） | 13812345678 |
| `@ChineseName` | 中文姓名 | 张三 |
| `@BankCard` | 银行卡号（Luhn算法） | 6222021234567890123 |
| `@UnifiedSocialCreditCode` | 统一社会信用代码 | 91110000600000000D |
| `@ChineseLicensePlate` | 车牌号（含新能源） | 京A12345 |

### 时间日期注解

| 注解 | 说明 | 示例 |
|------|------|------|
| `@Date` | 日期格式验证 | 2024-01-15 |
| `@DateTime` | 日期时间格式验证 | 2024-01-15 13:30:00 |
| `@PastDate` | 过去的日期 | 2000-01-01 |
| `@FutureDate` | 未来的日期 | 2025-12-31 |
| `@Timestamp` | 时间戳（秒/毫秒） | 1705305000 |
| `@Age` | 年龄验证 | 基于出生日期 |

### 通用验证注解（Hibernate Validator）

| 注解 | 说明 |
|------|------|
| `@NotNull` | 不能为null |
| `@NotBlank` | 不能为空字符串 |
| `@Email` | 邮箱格式 |
| `@Size(min, max)` | 字符串长度 |
| `@Min` / `@Max` | 数值范围 |
| `@Pattern` | 正则表达式 |

---

## 实战示例

### 示例1：用户注册

```java
public class UserRegistrationDTO {
    @NotBlank
    @ChineseIdCard
    private String idCard;

    @NotBlank
    @ChineseName
    private String realName;

    @NotBlank
    @ChinesePhone
    private String phone;

    @Email
    private String email;

    @NotBlank
    @Date
    private String birthDate;

    @Age(min = 18, max = 120)
    private String birthDateForAge;
}
```

### 示例2：订单提交

```java
public class OrderDTO {
    @NotBlank
    @ChinesePhone
    private String receiverPhone;

    @NotBlank
    private String receiverAddress;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @Min(0)
    private BigDecimal totalAmount;

    @FutureDate(includeToday = true)
    private String deliveryDate;
}
```

### 示例3：企业信息

```java
public class CompanyDTO {
    @NotBlank
    private String companyName;

    @NotBlank
    @UnifiedSocialCreditCode
    private String creditCode;

    @NotBlank
    @ChinesePhone
    private String contactPhone;

    @BankCard
    private String bankAccount;
}
```

---

## 链式API验证（动态数据）

除了注解方式，ValidX还提供链式API用于验证动态数据：

```java
// 验证Map数据
Map<String, Object> userData = getUserDataFromRequest();

ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_EMPTY)
    .field("身份证号").isChineseIdCard(userData.get("idCard"))
    .field("手机号").isChinesePhone(userData.get("phone"))
    .field("邮箱").allowNull().isEmail(userData.get("email"));

if (!validator.passed()) {
    List<String> errors = validator.getErrors();
    return Result.error("验证失败", errors);
}
```

**适用场景**：
- 验证Map、JSON等动态数据
- 需要灵活的验证逻辑
- 不想定义DTO的临时验证

---

## 自定义错误消息

### 方式1：注解参数

```java
@ChineseIdCard(message = "请输入正确的身份证号码")
private String idCard;

@ChinesePhone(message = "手机号格式不正确")
private String phone;
```

### 方式2：国际化资源文件

创建 `ValidationMessages_zh_CN.properties`：
```properties
io.github.vipxieliang.validx.annotation.idcard=身份证号格式不正确
io.github.vipxieliang.validx.annotation.phone=手机号格式不正确
```

ValidX会自动根据请求的语言（Accept-Language）返回对应的错误消息。

---

## 常见问题

### Q1：ValidX与Hibernate Validator冲突吗？

**不冲突**。ValidX基于JSR-380标准，与Hibernate Validator完全兼容，可以混合使用：

```java
public class UserDTO {
    // Hibernate Validator的注解
    @NotNull
    @Size(min = 2, max = 20)
    private String username;

    // ValidX的注解
    @ChineseIdCard
    private String idCard;
}
```

### Q2：如何验证嵌套对象？

使用 `@Valid` 注解：

```java
public class OrderDTO {
    @NotNull
    @Valid  // 级联验证
    private AddressDTO address;
}

public class AddressDTO {
    @NotBlank
    @ChinesePhone
    private String phone;
}
```

### Q3：如何进行分组验证？

```java
public class UserDTO {
    @NotBlank(groups = {Create.class, Update.class})
    private String username;

    @NotBlank(groups = Create.class)  // 仅创建时验证
    private String password;
}

// Controller中指定分组
@PostMapping
public Result create(@Validated(Create.class) @RequestBody UserDTO dto) {
    return userService.create(dto);
}
```

### Q4：性能如何？

ValidX经过深度优化：
- 验证器实例缓存
- 正则表达式预编译
- 单次验证耗时：微秒级
- 支持百万级QPS

---

## 下一步

恭喜你完成了ValidX的快速入门！接下来你可以：

1. **深入学习**：阅读《ValidX完整注解指南》了解所有注解
2. **实战演练**：参考《用户注册表单验证最佳实践》
3. **性能优化**：查看《ValidX性能优化指南》
4. **源码探索**：研究《ValidX源码解析系列》

---

## 总结

ValidX让Java数据验证变得简单：

- ✅ **5分钟上手**：添加依赖 → 加注解 → 使用@Valid
- ✅ **100+注解**：覆盖中国业务的方方面面
- ✅ **零学习成本**：注解即文档，见名知义
- ✅ **完全兼容**：与Hibernate Validator无缝集成
- ✅ **企业级可靠**：1300+单元测试保障

现在就开始使用ValidX，告别繁琐的验证代码吧！

---

**相关文章**：
- [ValidX vs Hibernate Validator：你应该选择哪一个？](./02_ValidX_vs_Hibernate_Validator_选择指南.md)
- [中国身份证号验证完全指南](./03_身份证验证完全指南.md)
- [ValidX在Spring Boot中的最佳实践](../week02/04_Spring_Boot_最佳实践.md)

---

**文档版本**：v1.0
**发布日期**：2026-08-11
**作者**：ValidX Team
