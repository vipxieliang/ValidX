# 语言

[中文](README.cn.md)

[English](README.md)

<div align="center">

<img src="logo.svg" alt="ValidX" width="300"/>

# ValidX

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vipxieliang/validx?color=blue)](https://central.sonatype.com/artifact/io.github.vipxieliang/validx)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/vipxieliang/ValidX/pulls)

**简单、优雅、可靠 - 100+ 开箱即用的中国业务验证器**

</div>

---

## 📑 目录

- [版本更新通知](#-版本更新通知)
- [介绍](#介绍)
- [为什么创作 ValidX？](#-为什么创作-validx)
- [为什么选择 ValidX？](#-为什么选择-validx)
- [5 分钟快速开始](#-5-分钟快速开始)
- [多语言支持](#多语言支持)
- [重要说明：Null/空字符串处理](#重要说明null空字符串处理)
- [线程安全](#线程安全)
- [支持的验证注解](#支持的验证注解)
  - [快速查询表](#快速查询表)
  - [基础验证](#基础验证)
  - [身份验证相关](#身份验证相关)
  - [金融验证相关](#金融验证相关)
  - [教育/职业资格验证](#教育职业资格验证认证相关的验证)
  - [网络相关](#网络相关)
  - [中国特定验证](#中国特定验证)
  - [汽车相关验证](#汽车相关的验证)
  - [图书相关验证](#图书相关的验证)
  - [手机相关验证](#手机相关的验证)
- [更多验证注解](#更多的的验证注解)
- [贡献](#贡献)

---

## 📢 版本更新通知

**当前版本：v1.2.0**（2026-08-25 发布）

### v1.2.0 主要更新

- ⚠️ **破坏性变更**：`isStartsWith()` 和 `isEndsWith()` 链式 API 参数从 `String[]` 改为 `String`；`isAlphaNum()` → `isAlphaNumber()`、`isMacAddress()` → `isMac()` 链式方法重命名（与注解命名 1:1 对齐）
- ✨ **新增功能**：`@StartsWithAny` 和 `@EndsWithAny` 多值验证注解；`@NationalityCode` 国籍国代码验证注解（ISO 3166-1）
- 🔧 **功能增强**：`@FileSize` 支持 MIME 类型验证（`allowedTypes` 参数）；`@Url` 支持协议白名单配置（`protocols` 参数，默认 http / https / ftp）
- 🎯 **代码优化**：简化 20+ 个验证器类的初始化代码

> **⚠️ 升级提醒**：从 v1.1.0 升级需注意链式 API 变更。单值用 `isStartsWith(value, "prefix")`，多值用 `isStartsWithAny(value, new String[]{"p1", "p2"})`；`isAlphaNum` 和 `isMacAddress` 已分别重命名为 `isAlphaNumber` 和 `isMac`（纯方法名变更，参数与行为不变）

**详细说明：** [v1.2.0 更新日志](docs/version/v1.2.0/CHANGELOG_CN.md) | [迁移指南](docs/version/v1.2.0/MIGRATION_v1.2.0.cn.md)

---

### 历史版本

| 版本 | 发布日期 | 主要特性 | 破坏性变更 | 说明文档 | 更新日志 | 迁移指南 |
|------|---------|---------|-----------|---------|---------|---------|
| **v1.2.0** | 2026-08-25 | 新增 `@StartsWithAny`、`@EndsWithAny` 注解；新增 `@NationalityCode` 注解（ISO 3166-1 国籍国代码）；`@FileSize` 支持 MIME 类型验证；`@Url` 支持协议白名单；代码重构优化 20+ 验证器 | 链式 API 参数变更：`isStartsWith()`/`isEndsWith()` 从 `String[]` 改为 `String`；方法重命名：`isAlphaNum()` → `isAlphaNumber()`、`isMacAddress()` → `isMac()` | - | [查看](docs/version/v1.2.0/CHANGELOG_CN.md) | [查看](docs/version/v1.2.0/MIGRATION_v1.2.0.cn.md) |
| **v1.1.0** | 2026-08-10 | 新增 6 个注解：`@Date`、`@DateTime`、`@PastDateTime`、`@FutureDateTime`、`@ChineseName`、`@NotContains`；增强日期验证严格性 | `@PastDate`/`@FutureDate` 不再支持时间格式，需使用 `@PastDateTime`/`@FutureDateTime` | [查看](docs/version/v1.1.0/README.cn.md) | [查看](docs/version/v1.1.0/CHANGELOG_CN.md) | [查看](docs/version/v1.1.0/MIGRATION_v1.1.0.cn.md) |
| **v1.0.1** | 2026-07-31 | 新增 `@Contains` 注解；核心类重命名 ValidaX → ValidX；文档优化；添加开源协议 | 无 | [查看](docs/version/v1.0.1/README_CN.md) | [查看](docs/version/v1.0.1/CHANGELOG_CN.md) | - |
| **v1.0.0** | 2026-05-01 | 首次发布，提供 100+ 验证注解，支持注解和链式两种使用方式 | 无 | [查看](docs/version/v1.0.0/README_CN.md) | - | - |

---

## 介绍

ValidX 是一个专注于中国业务场景的开源 Java 验证库，让验证变得简单、优雅、可靠。基于 JSR-380 标准构建，提供 100+ 个专门针对中国场景的验证注解，包括身份证、手机号、银行卡等。

## 💡 为什么创作 ValidX？

在开发面向中国用户的应用时，我们经常遇到这样的场景：

### 痛点一：Java 内置验证规则太少，远不如其他语言框架丰富

如果你用过其他语言的 Web 框架，比如 PHP 的 ThinkPHP、JavaScript 的 Validator.js，你会发现它们内置了非常丰富的验证规则：`mobile`、`idcard`、`zip`、`alphaNum` 等等，开箱即用，简单方便。

但在 Java 世界里，标准的 Bean Validation 只提供了 `@Email`、`@Pattern` 等少量通用验证注解。对于中国业务场景中常见的身份证、手机号、银行卡、统一社会信用代码等，却完全没有支持。

这导致每个 Java 项目都在重复造轮子：
- 自己编写复杂的正则表达式
- 实现 Luhn 算法验证银行卡
- 处理身份证的校验位计算
- 复制粘贴网上找到的验证代码

**为什么 Java 验证不能像其他框架那样开箱即用？** 这就是 ValidX 诞生的初衷。

### 痛点二：验证逻辑分散难以维护
随着项目发展，验证逻辑可能散落在：
- Controller 层的手动校验
- Service 层的业务校验
- 工具类中的静态方法
- 各个模块重复实现的验证代码

这导致代码重复、维护困难、容易出错。

### 痛点三：缺乏中文错误提示和多语言支持
使用标准注解时，错误消息通常是英文的，或者需要手动配置资源文件。对于中国用户，我们需要：
- 友好的中文错误提示
- 支持多语言切换
- 可自定义的错误消息模板

### ValidX 的解决方案

基于这些痛点，我们创建了 ValidX，目标是：**让 Java 验证简单、优雅、可靠**

1. **100+ 中国场景验证器** - 从身份证到快递单号，从 QQ 号到车牌号，覆盖中国业务的方方面面
2. **两种使用方式** - 注解式（适合 DTO 对象验证）和链式 API（适合动态验证），灵活应对不同场景
3. **零配置多语言** - 支持 8 种语言，自动适配用户语言环境
4. **企业级可靠性** - 1300+ 单元测试保证质量，生产环境验证
5. **简单易用** - 只需一个依赖，开箱即用，无需复杂配置

我们希望 ValidX 能成为每个面向中国用户的 Java 应用的标配工具，让开发者专注于业务逻辑，而不是重复编写验证代码。

**一次引入，终身受益。不再造轮子。**

## ✨ 为什么选择 ValidX？

### 🇨🇳 **为中国而生**
- **100+ 中国特色验证器**：身份证、手机号、银行卡、统一社会信用代码、车牌号等
- **支持 8 种语言**：简体中文、英语、日语、韩语、法语、德语、西班牙语、俄语
- **本土业务验证**：快递单号、QQ、微信、支付宝订单号

### 🚀 **开发者友好**
- **两种使用方式**：注解式（适用于 DTO）或链式 API（适用于动态验证）
- **零配置**：开箱即用，完美集成 Spring Boot 和标准 Bean Validation
- **智能空值处理**：可配置全局/局部的 null 和空字符串策略

### 🎯 **企业级**
- **类型安全验证**：注解方式提供编译时检查
- **丰富的错误消息**：自动国际化支持，可自定义字段标签
- **生产验证**：1300+ 单元测试全面覆盖

### 📦 **轻量高效**
- **单一依赖**：除 Bean Validation API 外无外部依赖
- **体积小巧**：~300KB JAR 包大小
- **高性能**：优化的验证器，最小化性能开销

---

## 🚀 5 分钟快速开始

### 第一步：添加依赖

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.2.1</version>
</dependency>
```

### 第二步：选择使用方式

#### 方式 A：注解方式（推荐用于 DTO）

适合 Spring Boot 控制器请求验证：

```java
public class UserRegistrationDTO {
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;

    @ChineseIdCard
    private String idCard;

    @Password(minLength = 8)
    private String password;

    // getters and setters...
}

@RestController
public class UserController {
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegistrationDTO dto) {
        // Spring 自动验证，失败返回 400
        return userService.register(dto);
    }
}
```

#### 方式 B：链式调用方式（推荐用于业务逻辑）

适合服务层动态验证：

```java
@Service
public class UserService {
    public void validateUserData(Map<String, Object> userData) {
        ValidX validator = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)  // 全局拒绝 null/空值
            .field("邮箱").isEmail(userData.get("email"))
            .field("手机号").isChinesePhone(userData.get("phone"))
            .field("身份证").isChineseIdCard(userData.get("idCard"))
            .field("QQ（可选）").allowNull().isQQ(userData.get("qq"));  // 可选字段允许空值

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

### 第三步：运行应用

就这么简单！ValidX 与现有 Spring Boot 设置无缝协作。错误消息会根据 `Accept-Language` 请求头自动适配用户语言。

## 多语言支持

ValidX 支持多语言错误消息，可以通过以下方式使用：

```java
// 使用系统默认语言
ValidX chain1 = ValidX.init()
        .isEmail("invalid-email");

// 使用中文
ValidX chain2 = ValidX.init()
        .withLocale(Locale.SIMPLIFIED_CHINESE)
        .isEmail("invalid-email");

// 使用英文
ValidX chain3 = ValidX.init()
        .withLocale(Locale.ENGLISH)
        .isEmail("invalid-email");
```

注解方式也支持多语言，错误消息会根据系统语言环境自动切换。要使用特定语言环境，可以配置Hibernate Validator：

```java
// 配置英文消息的语言环境
ValidatorFactory englishFactory = Validation.byDefaultProvider()
    .configure()
    .messageInterpolator(new ResourceBundleMessageInterpolator())
    .buildValidatorFactory();
Validator englishValidator = englishFactory.getValidator();

// 配置中文消息的语言环境
ValidatorFactory chineseFactory = Validation.byDefaultProvider()
    .configure()
    .messageInterpolator(new ResourceBundleMessageInterpolator())
    .buildValidatorFactory();
Validator chineseValidator = chineseFactory.getValidator();
```

```java
public class UserDTO {
    // 错误消息会根据当前语言环境自动切换为中文或英文
    @Email 
    private String email;
    
    @ChineseIdCard
    private String idCard;
}
```

### 自动语言环境切换

ValidX 还支持自动语言环境切换，无需显式指定语言环境：

```
// 全局设置语言环境（影响当前线程的所有验证操作）
MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);

// 验证操作会自动使用设置的语言环境
ValidX chain = ValidX.init()
        .isEmail("invalid-email");

// 清除全局语言环境设置
MessageManager.clearCurrentLocale();
```

目前支持的语言：
- 简体中文 (默认)
- 英文
- 日语
- 韩语
- 法语
- 德语
- 西班牙语
- 俄语

## 重要说明：Null/空字符串处理

**所有的验证注解和链式校验方法对 `null` 和空字符串（`""`）都会返回通过（`true`）**，这遵循 Bean Validation (JSR 380) 规范的设计原则。

这是为了实现**关注点分离**：
- `@NotNull` / `@NotEmpty` / `@NotBlank`：负责检查"字段是否存在"
- 格式验证注解/方法（如 `@Email`、`isEmail()` 等）：负责检查"如果字段有值，格式是否正确"

### 1. 注解方式的 Null/空字符串处理

#### 如何使用？

根据业务需求组合使用：

```java
public class UserDTO {
    // 必填字段：不能为 null 且格式必须正确
    @NotNull(message = "邮箱不能为空")
    @Email
    private String email;

    // 必填字段：不能为 null、不能为空字符串，且格式必须正确
    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;

    // 可选字段：可以为 null 或空字符串，但如果有值则格式必须正确
    @QQ
    private String qq;

    // 可选字段：可以为 null，但不能为空字符串，如果有值则格式必须正确
    @NotEmpty(message = "如果填写微信号，不能为空字符串")
    @WeChat
    private String wechat;
}
```

#### 常用组合示例

| 需求 | 注解组合 | 说明 |
|------|---------|------|
| **必填且格式正确** | `@NotBlank` + `@Email` | 最常用：不能为 null、空字符串、空白字符 |
| **必填且格式正确** | `@NotNull` + `@ChineseIdCard` | 不能为 null，但允许空字符串（较少用） |
| **可选但格式正确** | `@QQ` | 可以为 null/空，但如果有值必须正确 |
| **可选但非空** | `@NotEmpty` + `@WeChat` | 可以为 null，但不能是空字符串 |

#### Bean Validation 标准注解说明

- `@NotNull`：字段不能为 `null`（但可以是空字符串 `""`）
- `@NotEmpty`：字段不能为 `null` 且不能为空（字符串不能是 `""`，集合不能是空集合）
- `@NotBlank`：字符串不能为 `null`、`""`、`"   "`（空白字符）

#### 适用场景

注解方式适用于 **Controller 层的接口参数校验**：

```java
@RestController
public class UserController {
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserDTO dto) {
        // Spring 自动校验，失败返回 400
        return userService.register(dto);
    }
}
```

### 2. 链式调用的 Null/空字符串处理

链式校验（`ValidX.init()`）的默认行为与注解方式一致：**null 和空字符串会通过验证**。

#### 为什么这样设计？

链式校验主要用于**业务逻辑层的动态校验场景**，特别是处理 Map/JSON 数据时，字段可能不存在（返回 null）是正常情况。

例如：
- 解析外部 API 返回的 JSON 数据
- 处理前端传递的动态表单数据
- 校验数据库查询返回的 Map 结果

在这些场景中，某个字段为 null 通常表示"该字段不存在"或"不需要校验该字段"，而不是错误。

#### 适用场景

链式方式适用于**业务逻辑层的动态校验**：

```java
@Service
public class UserService {
    public void process(Map<String, Object> data) {
        ValidX validator = ValidX.init();

        // Map 中的字段可能不存在（null），这是正常情况
        // 链式校验会自动跳过 null 值
        validator.isEmail(data.get("email"))
                 .isChinesePhone(data.get("phone"));

        if (!validator.passed()) {
            throw new BusinessException(validator.getErrors());
        }
    }
}
```

#### 链式验证配置 API

ValidX 现在支持通过全局配置和局部状态控制来灵活处理 null/空值。

##### 全局配置

你可以使用 `ValidXConfig` 设置全局验证要求：

```java
// 创建带有全局 NOT_NULL 要求的验证器
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL);

// 所有验证方法现在都会拒绝 null 值
validator.isEmail(email)  // 如果 email 为 null 则失败
         .isPhone(phone); // 如果 phone 为 null 则失败

// 创建带有全局 NOT_EMPTY 要求的验证器
ValidX validator2 = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_EMPTY);

// 所有验证方法现在都会拒绝 null 和空字符串
validator2.isEmail(email)  // 如果 email 为 null 或 "" 则失败
          .isPhone(phone); // 如果 phone 为 null 或 "" 则失败
```

**可用的全局配置：**
- `ValidXConfig.DEFAULT` - 允许 null 和空字符串（默认行为）
- `ValidXConfig.GLOBAL_NOT_NULL` - 所有字段不能为 null
- `ValidXConfig.GLOBAL_NOT_EMPTY` - 所有字段不能为 null 或空字符串

**最佳实践：** 建议只在验证链的开头调用一次 `config()` 方法，以保持清晰和可维护性。

```java
// ✅ 推荐：在开头设置一次配置
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email)
    .isPhone(phone)
    .allowNull().isQQ(qq);  // 例外情况使用局部方法

// ⚠️ 不推荐：在中间多次调用 config()
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email)
    .config(ValidXConfig.DEFAULT)  // 令人困惑：难以跟踪配置变化
    .isPhone(phone);

// ✅ 如果需要不同配置，创建多个验证器实例
ValidX strictValidator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email1)
    .isPhone(phone1);

ValidX lenientValidator = ValidX.init()
    .config(ValidXConfig.DEFAULT)
    .isEmail(email2)
    .isPhone(phone2);
```

##### 局部状态控制

你可以使用局部状态方法来覆盖特定字段的全局配置：

```java
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL);  // 全局：拒绝 null

// 覆盖特定字段
validator.field("可选邮箱").allowNull().isEmail(optionalEmail)  // 此字段允许 null
         .field("必填手机").notEmpty().isPhone(phone)           // 要求非空
         .field("用户ID").isChineseIdCard(idCard);             // 使用全局 NOT_NULL
```

**可用的局部状态方法：**
- `.notNull()` - 字段不能为 null（但可以是空字符串）
- `.notEmpty()` - 字段不能为 null 或空字符串
- `.allowNull()` - 允许 null 值（如果为 null 则跳过验证）
- `.allowEmpty()` - 允许空字符串（但不允许 null）
- `.field("标签")` - 为错误消息设置自定义字段标签

##### 优先级规则

局部状态始终优先于全局配置：

```java
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_EMPTY);  // 全局：拒绝 null 和空字符串

validator.allowNull().isEmail(email);  // 局部 allowNull() 覆盖全局配置
```

**优先级：** 局部状态 > 全局配置 > 默认行为

##### 实际示例

**示例 1：API 请求验证**

```java
public void validateUserRegistration(Map<String, Object> request) {
    ValidX validator = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_EMPTY);  // 大部分字段必填

    validator.field("邮箱").isEmail(request.get("email"))
             .field("手机").isChinesePhone(request.get("phone"))
             .field("可选QQ").allowNull().isQQ(request.get("qq"))  // 可选字段
             .field("身份证").isChineseIdCard(request.get("idCard"));

    if (!validator.passed()) {
        throw new ValidationException(validator.getErrors());
    }
}
```

**示例 2：表单更新（部分更新）**

```java
public void updateUserProfile(String userId, Map<String, Object> updates) {
    // 只验证正在更新的字段
    ValidX validator = ValidX.init();  // 默认：允许 null/空

    // 只验证更新 Map 中存在的字段
    if (updates.containsKey("email")) {
        validator.field("邮箱").notEmpty().isEmail(updates.get("email"));
    }

    if (updates.containsKey("phone")) {
        validator.field("手机").notEmpty().isChinesePhone(updates.get("phone"));
    }

    if (!validator.passed()) {
        throw new ValidationException(validator.getErrors());
    }
}
```

**示例 3：混合要求**

```java
public void validateComplexForm(FormData data) {
    ValidX validator = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_NULL);  // 大部分字段必填

    validator.field("邮箱").notEmpty().isEmail(data.getEmail())        // 必填且非空
             .field("手机").isChinesePhone(data.getPhone())            // 必填（使用全局）
             .field("QQ").allowNull().isQQ(data.getQq())               // 可选
             .field("微信").allowEmpty().isWeChat(data.getWeChat())    // 可以为空但不能为 null
             .field("网站").allowNull().isUrl(data.getWebsite());      // 可选

    if (!validator.passed()) {
        // 错误消息包含自定义字段标签
        throw new ValidationException(validator.getErrors());
    }
}
```

##### 状态重置行为

**重要：** 局部状态（notNull/notEmpty/allowNull/allowEmpty）在每次验证方法调用后自动重置。这确保每个字段的验证是独立的。

```java
ValidX validator = ValidX.init();

validator.notNull().isEmail(email1)   // notNull 应用于 email1
         .isEmail(email2)              // email2 使用默认行为（状态已重置）
         .notEmpty().isPhone(phone);   // notEmpty 仅应用于 phone
```

##### 带字段标签的错误消息

使用 `.field("标签")` 时，错误消息将包含自定义标签：

```java
ValidX validator = ValidX.init();

validator.field("用户邮箱").notEmpty().isEmail("")
         .field("联系电话").notNull().isChinesePhone(null);

if (!validator.passed()) {
    List<String> errors = validator.getErrors();
    // 错误: ["用户邮箱: 值不能是空字符串",
    //       "联系电话: 值不能为null"]
}
```

> **注意：** 此配置 API 为链式验证中的 null/空值处理提供了细粒度控制，使其适用于复杂的业务场景，同时保持向后兼容性。

## 线程安全

**ValidX 实例不是线程安全的。** 每次校验都应该创建新实例：

```java
// ❌ 错误：跨线程共享实例
private static final ValidX VALIDATOR = ValidX.init();

public void validate(User user) {
    VALIDATOR.isEmail(user.getEmail());  // 线程不安全！
}

// ✅ 正确：每次校验创建新实例
public void validate(User user) {
    ValidX validator = ValidX.init()
        .isEmail(user.getEmail())
        .isPhone(user.getPhone());

    if (!validator.isValid()) {
        throw new ValidationException(validator.getErrorMessage());
    }
}
```

**为什么？** ValidX 内部使用可变状态（局部要求标志、字段标识、错误列表），这些状态在校验链执行过程中会被修改。跨线程共享实例会导致竞态条件和不正确的校验结果。

**线程安全的组件：**
- `ValidXConfig` 对象是不可变的，可以安全共享
- 各个验证器类（如 `ChineseIdCardValidator`）是无状态的，可以复用

这种设计遵循与其他流式 API 相同的模式，如 `StringBuilder`、Java 8 `Stream` 和 Lombok `Builder` - 它们都采用"创建-使用-丢弃"的使用模式。

## 支持的验证注解

ValidX 提供了丰富的验证注解，涵盖多种场景。以下是目前支持的所有验证注解及其功能说明：

### 快速查询表

点击注解名称可快速跳转到详细文档。

| 分类 | 注解 | 说明 | 新增版本 | 修改版本 |
|------|------|------|---------|---------|
| **基础验证** | [@Alpha](#alpha) | 纯英文字母验证 | 1.0.0 | - |
| **基础验证** | [@AlphaDash](#alphadash) | 字母数字下划线横线组合 | 1.0.0 | - |
| **基础验证** | [@AlphaNumber](#alphanumber) | 字母数字组合验证 | 1.0.0 | 1.2.0 |
| **基础验证** | [@Chinese](#chinese) | 纯中文字符验证 | 1.0.0 | - |
| **基础验证** | [@ChineseAlpha](#chinesealpha) | 中文字符和字母组合 | 1.0.0 | - |
| **基础验证** | [@ChineseAlphaNum](#chinesealphanum) | 中文字符、字母和数字组合 | 1.0.0 | - |
| **基础验证** | [@ChineseAlphaDash](#chinesealphadash) | 中文、字母、数字、下划线、横线组合 | 1.0.0 | - |
| **基础验证** | [@Lower](#lower) | 小写字符验证 | 1.0.0 | - |
| **基础验证** | [@Upper](#upper) | 大写字符验证 | 1.0.0 | - |
| **基础验证** | [@Xdigit](#xdigit) | 十六进制字符串验证 | 1.0.0 | - |
| **基础验证** | [@Longitude](#longitude) | 经度验证（-180到180） | 1.0.0 | - |
| **基础验证** | [@Latitude](#latitude) | 纬度验证（-90到90） | 1.0.0 | - |
| **基础验证** | [@GeoPoint](#geopoint) | 地理坐标对验证 | 1.0.0 | - |
| **基础验证** | [@Date](#date) | 日期格式验证（仅纯日期） | 1.1.0 | - |
| **基础验证** | [@DateTime](#datetime) | 日期时间格式验证（包含时间） | 1.1.0 | - |
| **基础验证** | [@FutureDate](#futuredate) | 未来日期验证 | 1.0.0 | 1.1.0 |
| **基础验证** | [@PastDate](#pastdate) | 过去日期验证 | 1.0.0 | 1.1.0 |
| **基础验证** | [@PastDateTime](#pastdatetime) | 过去日期时间验证 | 1.1.0 | - |
| **基础验证** | [@FutureDateTime](#futuredatetime) | 未来日期时间验证 | 1.1.0 | - |
| **基础验证** | [@HourMinute](#hourminute) | 时分格式（HH:mm） | 1.0.0 | - |
| **基础验证** | [@HourMinuteSecond](#hourminutesecond) | 时分秒格式（HH:mm:ss） | 1.0.0 | - |
| **基础验证** | [@Timestamp](#timestamp) | Unix时间戳验证 | 1.0.0 | - |
| **基础验证** | [@CronExpression](#cronexpression) | Cron表达式验证 | 1.0.0 | - |
| **基础验证** | [@Duration](#duration) | 时间段格式验证 | 1.0.0 | - |
| **基础验证** | [@ExpressNumber](#expressnumber) | 快递单号验证 | 1.0.0 | - |
| **基础验证** | [@StartsWith](#startswith) | 字符串前缀验证 | 1.0.0 | 1.2.0 |
| **基础验证** | [@StartsWithAny](#startswithany) | 多前缀验证 | 1.2.0 | - |
| **基础验证** | [@EndsWith](#endswith) | 字符串后缀验证 | 1.0.0 | 1.2.0 |
| **基础验证** | [@EndsWithAny](#endswithany) | 多后缀验证 | 1.2.0 | - |
| **基础验证** | [@Contains](#contains) | 字符串包含子串验证 | 1.0.1 | - |
| **基础验证** | [@NotContains](#notcontains) | 字符串不包含子串验证 | 1.1.0 | - |
| **基础验证** | [@In](#in) | 值必须在指定列表中 | 1.0.0 | - |
| **基础验证** | [@NotIn](#notin) | 值不能在指定列表中 | 1.0.0 | - |
| **基础验证** | [@Enum](#enum) | 枚举值验证 | 1.0.0 | - |
| **基础验证** | [@Color](#color) | 颜色格式（HEX/RGB/RGBA） | 1.0.0 | - |
| **基础验证** | [@Password](#password) | 密码强度验证 | 1.0.0 | - |
| **基础验证** | [@UUID](#uuid) | UUID格式验证 | 1.0.0 | - |
| **基础验证** | [@Base64](#base64) | Base64编码验证 | 1.0.0 | - |
| **基础验证** | [@JSON](#json) | JSON格式验证 | 1.0.0 | - |
| **基础验证** | [@JWT](#jwt) | JWT令牌格式验证 | 1.0.0 | - |
| **基础验证** | [@SemVer](#semver) | 语义化版本验证 | 1.0.0 | - |
| **基础验证** | [@FileExtension](#fileextension) | 文件扩展名验证 | 1.0.0 | - |
| **基础验证** | [@FileSize](#filesize) | 文件大小范围验证 | 1.0.0 | - |
| **基础验证** | [@Age](#age) | 年龄验证（从出生日期或身份证） | 1.0.0 | - |
| **基础验证** | [@Port](#port) | 端口号验证（0-65535） | 1.0.0 | - |
| **身份验证相关** | [@ChineseName](#chinesename) | 中国人姓名验证 | 1.1.0 | - |
| **身份验证相关** | [@ChineseIdCard](#chineseidcard) | 中国身份证验证 | 1.0.0 | - |
| **身份验证相关** | [@ChinesePassport](#chinesepassport) | 中国护照验证 | 1.0.0 | - |
| **身份验证相关** | [@ChineseMilitaryOfficer](#chinesemilitaryofficer) | 军官证验证 | 1.0.0 | - |
| **身份验证相关** | [@ChineseSoldier](#chinesesoldier) | 士兵证验证 | 1.0.0 | - |
| **身份验证相关** | [@ForeignerPermanentResidenceIdentity](#foreignerpermanentresidenceidentity) | 外国人永久居留身份证 | 1.0.0 | - |
| **身份验证相关** | [@HKMacauResidence](#hkmacauresidence) | 港澳居民居住证 | 1.0.0 | - |
| **身份验证相关** | [@HKMacauPass](#hkmacaupass) | 港澳居民来往内地通行证 | 1.0.0 | - |
| **身份验证相关** | [@TaiwanResidence](#taiwanresidence) | 台湾居民居住证 | 1.0.0 | - |
| **身份验证相关** | [@TaiwanPass](#taiwanpass) | 台湾居民来往大陆通行证 | 1.0.0 | - |
| **身份验证相关** | [@ForeignerWorkPermit](#foreignerworkpermit) | 外国人工作许可证 | 1.0.0 | - |
| **身份验证相关** | [@NationalityCode](#nationalitycode) | 国籍国代码（ISO 3166-1） | 1.2.0 | - |
| **身份验证相关** | [@UnifiedSocialCreditCode](#unifiedsocialcreditcode) | 统一社会信用代码 | 1.0.0 | - |
| **身份验证相关** | [@ChinesePhone](#chinesephone) | 中国手机号验证 | 1.0.0 | - |
| **身份验证相关** | [@ChineseLandline](#chineselandline) | 中国座机号验证 | 1.0.0 | - |
| **身份验证相关** | [@ChinesePhoneOrLandline](#chinesephoneorlandline) | 中国手机号或座机号 | 1.0.0 | - |
| **身份验证相关** | [@PhoneNumber](#phonenumber) | 国际电话号码验证 | 1.0.0 | - |
| **身份验证相关** | [@Email](#email) | 电子邮箱验证 | 1.0.0 | - |
| **金融验证相关** | [@BankCard](#bankcard) | 银行卡号验证（Luhn算法） | 1.0.0 | - |
| **金融验证相关** | [@CVV](#cvv) | CVV/CVC安全码验证 | 1.0.0 | - |
| **金融验证相关** | [@IBAN](#iban) | IBAN账号验证 | 1.0.0 | - |
| **金融验证相关** | [@SWIFT](#swift) | SWIFT/BIC代码验证 | 1.0.0 | - |
| **金融验证相关** | [@StockCode](#stockcode) | 股票代码验证 | 1.0.0 | - |
| **金融验证相关** | [@TradeOrderNumber](#tradeordernumber) | 交易订单号验证 | 1.0.0 | - |
| **金融验证相关** | [@FinancialProductCode](#financialproductcode) | 金融产品代码验证 | 1.0.0 | - |
| **教育/职业资格验证** | [@DegreeCertificate](#degreecertificate) | 学位证书编号 | 1.0.0 | - |
| **教育/职业资格验证** | [@Doctor](#doctor) | 医师资格证书 | 1.0.0 | - |
| **教育/职业资格验证** | [@Teacher](#teacher) | 教师资格证书 | 1.0.0 | - |
| **教育/职业资格验证** | [@Lawyer](#lawyer) | 法律职业资格证书 | 1.0.0 | - |
| **教育/职业资格验证** | [@PMP](#pmp) | PMP证书验证 | 1.0.0 | - |
| **教育/职业资格验证** | [@Constructor](#constructor) | 建造师证书 | 1.0.0 | - |
| **教育/职业资格验证** | [@Accountant](#accountant) | 会计资格证书 | 1.0.0 | - |
| **网络相关** | [@Domain](#domain) | 域名验证 | 1.0.0 | - |
| **网络相关** | [@Ip](#ip) | IP地址验证（IPv4/IPv6） | 1.0.0 | - |
| **网络相关** | [@Mac](#mac) | MAC地址验证 | 1.0.0 | 1.2.0 |
| **网络相关** | [@Url](#url) | URL地址验证 | 1.0.0 | 1.2.0 |
| **网络相关** | [@SubnetMask](#subnetmask) | 子网掩码验证 | 1.0.0 | - |
| **中国特定验证** | [@ChineseLicensePlate](#chineselicenseplate) | 中国车牌号验证 | 1.0.0 | - |
| **中国特定验证** | [@ChinesePatent](#chinesepatent) | 中国专利号验证 | 1.0.0 | - |
| **中国特定验证** | [@ChineseTrademark](#chinesetrademark) | 中国商标注册号 | 1.0.0 | - |
| **中国特定验证** | [@SoftwareCopyright](#softwarecopyright) | 软件著作权登记号 | 1.0.0 | - |
| **中国特定验证** | [@WorkCopyright](#workcopyright) | 一般作品著作权登记号 | 1.0.0 | - |
| **中国特定验证** | [@ChineseZipCode](#chinesezipcode) | 中国邮政编码验证 | 1.0.0 | - |
| **中国特定验证** | [@DrugApproval](#drugapproval) | 药品批准文号验证 | 1.0.0 | - |
| **中国特定验证** | [@DrugCode](#drugcode) | 药品本位码验证 | 1.0.0 | - |
| **中国特定验证** | [@MedicalDeviceRegistration](#medicaldeviceregistration) | 医疗器械注册证号 | 1.0.0 | - |
| **中国特定验证** | [@QQ](#qq) | QQ号验证 | 1.0.0 | - |
| **中国特定验证** | [@WeChat](#wechat) | 微信号验证 | 1.0.0 | - |
| **汽车相关验证** | [@VIN](#vin) | 车辆识别代码 | 1.0.0 | - |
| **汽车相关验证** | [@VehicleEngine](#vehicleengine) | 车辆发动机号验证 | 1.0.0 | - |
| **图书相关验证** | [@ISBN](#isbn) | ISBN书号验证 | 1.0.0 | - |
| **图书相关验证** | [@ISSN](#issn) | ISSN期刊号验证 | 1.0.0 | - |
| **图书相关验证** | [@DOI](#doi) | DOI标识符验证 | 1.0.0 | - |
| **图书相关验证** | [@CLC](#clc) | 中图分类号 | 1.0.0 | - |
| **图书相关验证** | [@DDC](#ddc) | 杜威十进分类法 | 1.0.0 | - |
| **图书相关验证** | [@ORCID](#orcid) | ORCID研究者ID验证 | 1.0.0 | - |
| **图书相关验证** | [@IPC](#ipc) | 国际专利分类号 | 1.0.0 | - |
| **手机相关验证** | [@IMEI](#imei) | IMEI设备号验证 | 1.0.0 | - |

---

### 基础验证

#### @Alpha
* 校验规则：纯英文字母验证，只允许大小写英文字母（a-z、A-Z）。
* 示例格式：`abcDEF`
* 使用示例：
  ```java
  // 注解方式使用
  @Alpha
  private String code;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isAlpha("abcDEF");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @AlphaDash
* 校验规则：字母数字下划线破折号验证，允许英文字母、数字、下划线和破折号。
* 示例格式：`abc-123_def`
* 使用示例：
  ```java
  // 注解方式使用
  @AlphaDash
  private String code;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isAlphaDash("abc-123_def");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @AlphaNumber
* 校验规则：字母和数字组合验证，只允许英文字母和数字。
* 示例格式：`abc123`
* 版本信息：
  - 新增版本：1.0.0
  - 修改版本：1.2.0（链式方法重命名：`isAlphaNum()` → `isAlphaNumber()`，注解本身不变）
* 使用示例：
  ```java
  // 注解方式使用
  @AlphaNumber
  private String code;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isAlphaNumber("abc123");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Chinese
* 校验规则：纯汉字验证，只允许中文字符（Unicode中文字符）。
* 示例格式：`汉字`
* 使用示例：
  ```java
  // 注解方式使用
  @Chinese
  private String name;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChinese("汉字");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseAlpha
* 校验规则：汉字字母验证，允许中文字符和英文字母。
* 示例格式：`汉字abc`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseAlpha
  private String name;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseAlpha("汉字abc");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseAlphaNum
* 校验规则：汉字字母数字验证，允许中文字符、英文字母和数字。
* 示例格式：`汉字abc123`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseAlphaNum
  private String code;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseAlphaNum("汉字abc123");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseAlphaDash
* 校验规则：汉字字母数字下划线破折号验证，允许中文字符、英文字母、数字、下划线和破折号。
* 示例格式：`汉字abc-123_def`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseAlphaDash
  private String code;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseAlphaDash("汉字abc-123_def");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Longitude
* 校验规则：经度验证，验证经度值是否在-180到180之间。
* 示例格式：`0`, `116.4074`, `-116.4074`, `180`, `-180`
* 使用示例：
  ```java
  // 注解方式使用
  @Longitude
  private String longitude;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isLongitude("116.4074");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Latitude
* 校验规则：纬度验证，验证纬度值是否在-90到90之间。
* 示例格式：`0`, `39.9042`, `-39.9042`, `90`, `-90`
* 使用示例：
  ```java
  // 注解方式使用
  @Latitude
  private String latitude;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isLatitude("39.9042");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @GeoPoint
* 校验规则：地理坐标对验证（经纬度），验证坐标格式是否正确且数值在有效范围内。
* 支持格式：
  - 逗号分隔：`"116.4074,39.9042"` (经度,纬度)
  - 空格分隔：`"116.4074 39.9042"`
  - 逗号+空格：`"116.4074, 39.9042"`
* 校验规则：
  - 经度范围：-180 到 180
  - 纬度范围：-90 到 90
  - 必须包含两个有效数值
* 配置选项：
  - `latitudeFirst`：坐标顺序，`false`（默认）表示经度在前，`true` 表示纬度在前
  - `separator`：分隔符类型 - `ANY`（默认）、`COMMA` 或 `SPACE`
* 使用示例：
  ```java
  // 注解方式使用 - 默认（经度,纬度）
  @GeoPoint
  private String location;  // "116.4074,39.9042"

  // 纬度在前
  @GeoPoint(latitudeFirst = true)
  private String position;  // "39.9042,116.4074"

  // 仅逗号分隔
  @GeoPoint(separator = GeoPoint.SeparatorType.COMMA)
  private String gps;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isGeoPoint("116.4074,39.9042");  // 默认：经度在前
  validator.isGeoPoint("39.9042,116.4074", true);  // 纬度在前
  validator.isGeoPoint("116.4074,39.9042", false, GeoPoint.SeparatorType.COMMA);  // 指定分隔符
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Date
* 校验规则：日期格式验证（仅纯日期，不含时间），验证字符串是否符合指定的日期格式，采用严格验证模式。
* 核心特性：
  - **严格验证**：拒绝无效日期（如 2024-02-30、2024-13-01）
  - **闰年识别**：正确处理闰年（2024-02-29 ✓, 1900-02-29 ✗）
  - **灵活格式**：支持所有 Java DateTimeFormatter 日期格式
  - **多语言支持**：支持9种语言的错误提示
  - **格式限制**：pattern 不能包含时间符号（如需验证日期时间格式请使用 @DateTime）
* 格式符号说明：

  **日期符号：**

  | 符号 | 含义 | 示例 | 说明 |
  |------|------|------|------|
  | `yyyy` | 年份（4位） | `2024` | 推荐使用，自动转换为严格模式 |
  | `yy` | 年份（2位） | `24` | 表示 2024 年 |
  | `MM` | 月份（补零） | `01`, `12` | 必须2位数字 |
  | `M` | 月份（不补零） | `1`, `12` | 1-12 |
  | `dd` | 日期（补零） | `05`, `25` | 必须2位数字 |
  | `d` | 日期（不补零） | `5`, `25` | 1-31 |
  | `DDD` | 年中的天数 | `365` | 1-366 |

* 支持格式示例：
  - 标准日期：`yyyy-MM-dd` → `2024-01-15`
  - 紧凑格式：`yyyyMMdd` → `20240115`
  - 美国格式：`MM/dd/yyyy` → `12/25/2024`
  - 欧洲格式：`dd/MM/yyyy` → `25/12/2024`
  - 中文格式：`yyyy年MM月dd日` → `2024年12月25日`
* 配置选项：
  - `pattern`：日期格式模式，默认为 `"yyyy-MM-dd"`（不能包含时间符号）
* 验证示例：
  - ✅ 有效：`2024-02-29`（闰年）
  - ✅ 有效：`2024-01-31`（1月31天）
  - ✅ 有效：`2024-04-30`（4月30天）
  - ❌ 无效：`2024-02-30`（2月没有30日）
  - ❌ 无效：`2023-02-29`（非闰年）
  - ❌ 无效：`2024-13-01`（月份范围1-12）
  - ❌ 无效：`2024-04-31`（4月没有31天）
* 使用示例：
  ```java
  // 注解方式使用 - 默认格式（yyyy-MM-dd）
  @Date
  private String birthDate;

  // 自定义格式
  @Date(pattern = "MM/dd/yyyy")
  private String usDate;

  @Date(pattern = "yyyy年MM月dd日")
  private String chineseDate;

  // 链式调用方式使用 - 默认格式
  ValidX validator = ValidX.init();
  validator.isDate("2024-01-15");

  // 自定义格式
  validator.isDate("12/25/2024", "MM/dd/yyyy");
  validator.isDate("2024年12月25日", "yyyy年MM月dd日");
  ```
* 注意事项：
  - pattern 不能包含时间符号（H、h、K、k、m、s、S、a、A、n、N）
  - 如需验证日期时间格式，请使用 @DateTime 注解
  - **严格格式匹配**：输入值必须完全匹配 pattern 的长度和格式
    - ✅ 有效：`@Date(pattern = "yyyy-MM-dd")` 输入 `"2024-01-15"`
    - ❌ 无效：`@Date(pattern = "yyyy-MM-dd")` 输入 `"2024-01-15 12:00:00"`（包含时间）
    - ❌ 无效：`@Date(pattern = "yyyy-MM-dd")` 输入 `"2024-1-5"`（缺少补零）
  - 使用 `yyyy-MM-dd` 等格式时，日期必须补零（如 `2024-01-05` 而非 `2024-1-5`）
  - 分隔符必须与格式完全匹配（如格式为 `yyyy-MM-dd` 时，`2024/01/15` 会验证失败）
  - null 和空字符串默认通过验证（配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @DateTime
* 校验规则：日期时间格式验证，验证字符串是否符合指定的日期时间格式（必须包含时间部分），采用严格验证模式。
* 核心特性：
  - **严格验证**：拒绝无效日期时间（如 2024-02-30 13:30:00、2024-01-15 25:00:00）
  - **闰年识别**：正确处理闰年
  - **时间验证**：验证小时（0-23）、分钟（0-59）、秒（0-59）
  - **灵活格式**：支持所有 Java DateTimeFormatter 日期时间格式
  - **多语言支持**：支持9种语言的错误提示
  - **格式要求**：pattern 必须包含时间符号（H、h、K、k、m、s、S、a、A、n、N）
* 格式符号说明：

  **时间符号：**

  | 符号 | 含义 | 示例 | 说明 |
  |------|------|------|------|
  | `HH` | 小时（24小时制，补零） | `00`, `23` | 00-23 |
  | `H` | 小时（24小时制，不补零） | `0`, `23` | 0-23 |
  | `hh` | 小时（12小时制，补零） | `01`, `12` | 01-12，需配合 `a` 使用 |
  | `h` | 小时（12小时制，不补零） | `1`, `12` | 1-12，需配合 `a` 使用 |
  | `mm` | 分钟（补零） | `00`, `59` | 00-59 |
  | `m` | 分钟（不补零） | `0`, `59` | 0-59 |
  | `ss` | 秒（补零） | `00`, `59` | 00-59 |
  | `s` | 秒（不补零） | `0`, `59` | 0-59 |
  | `SSS` | 毫秒 | `000`, `999` | 毫秒数 |
  | `a` | 上午/下午标记 | `AM`, `PM` | 配合12小时制使用 |

  **日期符号：** 参见 [@Date](#date) 的完整日期符号说明。

* 支持格式示例：
  - 标准格式：`yyyy-MM-dd HH:mm:ss` → `2024-01-15 13:30:00`
  - ISO 8601：`yyyy-MM-dd'T'HH:mm:ss` → `2024-01-15T13:30:00`
  - 带毫秒：`yyyy-MM-dd HH:mm:ss.SSS` → `2024-01-15 13:30:00.123`
  - 12小时制：`yyyy-MM-dd hh:mm:ss a` → `2024-01-15 02:30:00 PM`
  - 紧凑格式：`yyyyMMddHHmmss` → `20240115133000`
  - 中文格式：`yyyy年MM月dd日 HH时mm分ss秒` → `2024年12月25日 14时30分00秒`
* 配置选项：
  - `pattern`：日期时间格式模式，默认为 `"yyyy-MM-dd HH:mm:ss"`（必须包含时间符号）
* 验证示例：
  - ✅ 有效：`2024-01-15 13:30:00`
  - ✅ 有效：`2024-02-29 23:59:59`（闰年）
  - ✅ 有效：`2024-01-15 00:00:00`（午夜）
  - ❌ 无效：`2024-02-30 13:30:00`（无效日期）
  - ❌ 无效：`2024-01-15 24:00:00`（小时必须是 0-23）
  - ❌ 无效：`2024-01-15 12:60:00`（分钟必须是 0-59）
  - ❌ 无效：`2024-01-15 12:30:60`（秒必须是 0-59）
* 使用示例：
  ```java
  // 注解方式使用 - 默认格式（yyyy-MM-dd HH:mm:ss）
  @DateTime
  private String createdAt;

  // 自定义格式
  @DateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private String isoTimestamp;

  @DateTime(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private String preciseTime;

  @DateTime(pattern = "yyyy-MM-dd hh:mm:ss a")
  private String appointmentTime;

  // 链式调用方式使用 - 默认格式
  ValidX validator = ValidX.init();
  validator.isDateTime("2024-01-15 13:30:00");

  // 自定义格式
  validator.isDateTime("2024-01-15T13:30:00", "yyyy-MM-dd'T'HH:mm:ss");
  validator.isDateTime("2024-01-15 13:30:00.123", "yyyy-MM-dd HH:mm:ss.SSS");
  ```
* 注意事项：
  - pattern 必须包含至少一个时间符号（H、h、K、k、m、s、S、a、A、n、N）
  - 如需验证纯日期格式（不含时间），请使用 @Date 注解
  - 时间必须有效：小时 0-23、分钟 0-59、秒 0-59
  - **严格格式匹配**：输入值必须完全匹配 pattern 的长度和格式，包括时间部分
    - ✅ 有效：`@DateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2024-01-15 13:30:00"`
    - ❌ 无效：`@DateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2024-01-15"`（缺少时间）
    - ❌ 无效：`@DateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2024-01-15 13:30"`（时间不完整）
  - 使用补零格式时，值必须完全匹配
  - null 和空字符串默认通过验证（配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @FutureDate
* 校验规则：未来日期验证，验证日期是否为未来日期。
* 示例格式：`2025-12-31`（纯日期格式）
* 版本信息：
  - 新增版本：1.0.0
  - 修改版本：1.1.0（新增 `pattern` 参数支持自定义日期格式）
  - 兼容性：⚠️ **不完全向后兼容**
* **重要变更说明（v1.0.0 → v1.1.0）**：
  - **v1.0.0 行为**：自动支持两种格式
    - 优先尝试解析为 `yyyy-MM-dd` 格式
    - 如果失败，再尝试解析为 `yyyy-MM-dd HH:mm:ss` 格式
    - **支持包含时间**的日期字符串（如 `2025-12-31 12:00:00`）
  - **v1.1.0 行为**：仅支持纯日期格式
    - 默认格式为 `yyyy-MM-dd`
    - 通过 `pattern` 参数可自定义日期格式（如 `MM/dd/yyyy`）
    - **不再支持包含时间**的格式，pattern 中不能包含 HH、mm、ss 等时间符号
    - 如果 pattern 包含时间符号会抛出 `IllegalArgumentException`
  - **升级建议**：如需验证包含时间的未来日期，请使用新增的 @FutureDateTime 注解
* 参数说明：
  - `includeToday`：是否包含今天，默认为 `false`
  - `pattern`：日期格式模式，默认为 `"yyyy-MM-dd"`（v1.1.0 新增）。**注意：不能包含时间符号**
* 使用示例：
  ```java
  // 注解方式使用
  @FutureDate
  private String date;
  // 或包含今天
  @FutureDate(includeToday = true)
  private String deadline;
  // 自定义日期格式
  @FutureDate(pattern = "MM/dd/yyyy")
  private String usDate;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  // 默认格式（yyyy-MM-dd），不包含今天
  validator.isFutureDate("2025-12-31");
  // 包含今天
  validator.isFutureDate("2025-12-31", true);
  // 自定义格式
  validator.isFutureDate("12/31/2025", false, "MM/dd/yyyy");

  // 注意：如果需要验证包含时间的未来日期，请使用 @FutureDateTime
  @FutureDateTime
  private String futureDateTime;  // 支持 "2025-12-31 23:59:59"
  ```
* 注意事项：
  - pattern 不能包含时间符号（H、h、K、k、m、s、S、a、A、n、N）
  - 如需验证包含时间的未来日期，请使用 @FutureDateTime 注解
  - **严格格式匹配**：输入值必须完全匹配 pattern 的长度和格式
    - ✅ 有效：`@FutureDate(pattern = "yyyy-MM-dd")` 输入 `"2025-12-31"`
    - ❌ 无效：`@FutureDate(pattern = "yyyy-MM-dd")` 输入 `"2025-12-31 12:00:00"`（包含时间）
    - ❌ 无效：`@FutureDate(pattern = "yyyy-MM-dd")` 输入 `"2025-1-5"`（缺少补零）
  - null 和空字符串默认通过验证（配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @PastDate
* 校验规则：过去日期验证，验证日期是否为过去日期。
* 示例格式：`2020-01-01`（纯日期格式）
* 版本信息：
  - 新增版本：1.0.0
  - 修改版本：1.1.0（新增 `pattern` 参数支持自定义日期格式）
  - 兼容性：⚠️ **不完全向后兼容**
* **重要变更说明（v1.0.0 → v1.1.0）**：
  - **v1.0.0 行为**：自动支持两种格式
    - 优先尝试解析为 `yyyy-MM-dd` 格式
    - 如果失败，再尝试解析为 `yyyy-MM-dd HH:mm:ss` 格式
    - **支持包含时间**的日期字符串（如 `2020-01-01 12:00:00`）
  - **v1.1.0 行为**：仅支持纯日期格式
    - 默认格式为 `yyyy-MM-dd`
    - 通过 `pattern` 参数可自定义日期格式（如 `MM/dd/yyyy`）
    - **不再支持包含时间**的格式，pattern 中不能包含 HH、mm、ss 等时间符号
    - 如果 pattern 包含时间符号会抛出 `IllegalArgumentException`
  - **升级建议**：如需验证包含时间的过去日期，请使用新增的 @PastDateTime 注解
* 参数说明：
  - `includeToday`：是否包含今天，默认为 `false`
  - `pattern`：日期格式模式，默认为 `"yyyy-MM-dd"`（v1.1.0 新增）。**注意：不能包含时间符号**
* 使用示例：
  ```java
  // 注解方式使用
  @PastDate
  private String date;
  // 或包含今天
  @PastDate(includeToday = true)
  private String birthDate;
  // 自定义日期格式
  @PastDate(pattern = "yyyy/MM/dd")
  private String jpDate;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  // 默认格式（yyyy-MM-dd），不包含今天
  validator.isPastDate("2020-01-01");
  // 包含今天
  validator.isPastDate("2020-01-01", true);
  // 自定义格式
  validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");

  // 注意：如果需要验证包含时间的过去日期，请使用 @PastDateTime
  @PastDateTime
  private String pastDateTime;  // 支持 "2020-01-01 12:30:45"
  ```
* 注意事项：
  - pattern 不能包含时间符号（H、h、K、k、m、s、S、a、A、n、N）
  - 如需验证包含时间的过去日期，请使用 @PastDateTime 注解
  - **严格格式匹配**：输入值必须完全匹配 pattern 的长度和格式
    - ✅ 有效：`@PastDate(pattern = "yyyy-MM-dd")` 输入 `"2020-01-01"`
    - ❌ 无效：`@PastDate(pattern = "yyyy-MM-dd")` 输入 `"2020-01-01 12:30:45"`（包含时间）
    - ❌ 无效：`@PastDate(pattern = "yyyy-MM-dd")` 输入 `"2020-1-1"`（缺少补零）
  - null 和空字符串默认通过验证（配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @PastDateTime
* 校验规则：过去日期时间验证，验证日期时间是否为过去（必须包含时间部分）。
* 示例格式：`2020-01-01 12:30:45`, `2020/01/01 12:30:45`
* 参数说明：
  - `includeToday`：是否包含今天，默认为 `false`
  - `pattern`：日期时间格式模式，默认为 `"yyyy-MM-dd HH:mm:ss"`（必须包含时间部分）
* 使用示例：
  ```java
  // 注解方式使用
  @PastDateTime
  private String timestamp;
  // 或包含今天
  @PastDateTime(includeToday = true)
  private String createdAt;
  // 自定义日期时间格式
  @PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private String isoDateTime;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  // 默认格式（yyyy-MM-dd HH:mm:ss），不包含今天
  validator.isPastDateTime("2020-01-01 12:30:45");
  // 包含今天
  validator.isPastDateTime("2020-01-01 12:30:45", true);
  // 自定义格式
  validator.isPastDateTime("2020-01-01T12:30:45", false, "yyyy-MM-dd'T'HH:mm:ss");
  ```
* 注意事项：
  - pattern 必须包含至少一个时间符号（H、h、K、k、m、s、S、a、A、n、N）
  - 如需验证纯日期格式（不含时间），请使用 @PastDate 注解
  - **严格格式匹配**：输入值必须完全匹配 pattern 的长度和格式，包括时间部分
    - ✅ 有效：`@PastDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2020-01-01 12:30:45"`
    - ❌ 无效：`@PastDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2020-01-01"`（缺少时间）
    - ❌ 无效：`@PastDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2020-01-01 12:30"`（时间不完整）
  - null 和空字符串默认通过验证（配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @FutureDateTime
* 校验规则：未来日期时间验证，验证日期时间是否为未来（必须包含时间部分）。
* 示例格式：`2025-12-31 23:59:59`, `2025/12/31 23:59:59`
* 参数说明：
  - `includeToday`：是否包含今天，默认为 `false`
  - `pattern`：日期时间格式模式，默认为 `"yyyy-MM-dd HH:mm:ss"`（必须包含时间部分）
* 使用示例：
  ```java
  // 注解方式使用
  @FutureDateTime
  private String scheduledTime;
  // 或包含今天
  @FutureDateTime(includeToday = true)
  private String deadline;
  // 自定义日期时间格式
  @FutureDateTime(pattern = "MM/dd/yyyy HH:mm:ss")
  private String usDateTime;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  // 默认格式（yyyy-MM-dd HH:mm:ss），不包含今天
  validator.isFutureDateTime("2025-12-31 23:59:59");
  // 包含今天
  validator.isFutureDateTime("2025-12-31 23:59:59", true);
  // 自定义格式
  validator.isFutureDateTime("12/31/2025 23:59:59", false, "MM/dd/yyyy HH:mm:ss");
  ```
* 注意事项：
  - pattern 必须包含至少一个时间符号（H、h、K、k、m、s、S、a、A、n、N）
  - 如需验证纯日期格式（不含时间），请使用 @FutureDate 注解
  - **严格格式匹配**：输入值必须完全匹配 pattern 的长度和格式，包括时间部分
    - ✅ 有效：`@FutureDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2025-12-31 23:59:59"`
    - ❌ 无效：`@FutureDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2025-12-31"`（缺少时间）
    - ❌ 无效：`@FutureDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` 输入 `"2025-12-31 23:59"`（时间不完整）
  - null 和空字符串默认通过验证（配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @HourMinute
* 校验规则：小时分钟时间格式验证，验证时间格式是否为HH:mm。
* 示例格式：`23:20`, `09:30`
* 使用示例：
  ```java
  // 注解方式使用
  @HourMinute
  private String time;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isHourMinute("23:20");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @HourMinuteSecond
* 校验规则：时分秒时间格式验证，验证时间格式是否为HH:mm:ss。
* 示例格式：`23:50:29`, `09:30:05`
* 使用示例：
  ```java
  // 注解方式使用
  @HourMinuteSecond
  private String time;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isHourMinuteSecond("23:50:29");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @In
* 校验规则：单个元素、或者多个元素匹配验证，验证值是否在指定的值列表中。
* 示例格式：任意指定值
* 使用示例：
  ```java
  // 注解方式使用 - 单个值验证
  @In({"value1", "value2"})
  private String status;

  // 注解方式使用 - 集合/数组验证（每个元素都必须在指定值列表中）
  @In({"admin", "user", "guest"})
  private List<String> roles;
  
  // 链式调用方式使用 - 单个值验证
  ValidX validator = ValidX.init();
  validator.isIn("value1", new String[]{"value1", "value2"});
  
  // 链式调用方式使用 - 集合验证
  List<String> roles = Arrays.asList("admin", "user");
  validator.isIn(roles, new String[]{"admin", "user", "guest"});
  ```

[↑ 返回快速查询表](#快速查询表)

#### @NotIn
* 校验规则：单个元素、或者多个元素不匹配验证，验证值是否不在指定的值列表中。
* 示例格式：任意指定值之外的值
* 使用示例：
  ```java
  // 注解方式使用 - 单个值验证
  @NotIn({"value1", "value2"})
  private String status;

  // 注解方式使用 - 集合/数组验证（每个元素都不能在指定值列表中）
  @NotIn({"admin", "root", "superuser"})
  private List<String> forbiddenRoles;
  
  // 链式调用方式使用 - 单个值验证
  ValidX validator = ValidX.init();
  validator.isNotIn("value3", new String[]{"value1", "value2"});
  
  // 链式调用方式使用 - 集合验证
  List<String> roles = Arrays.asList("user", "guest");
  validator.isNotIn(roles, new String[]{"admin", "root", "superuser"});
  ```

[↑ 返回快速查询表](#快速查询表)

#### @FileExtension
* 校验规则：文件后缀名验证，验证文件名的后缀是否在指定的后缀列表中。
* 示例格式：指定的文件后缀名
* 使用示例：
  ```java
  // 注解方式使用 不忽略大小写 默认是false
  @FileExtension(value = {"xls", "xlsx"})
  private String fileName;
  // 注解方式使用 忽略大小写
  @FileExtension(value = {"xls", "xlsx"}, ignoreCase = true)
  private String documentName;
  ```
* 使用链式调用时也可以指定是否忽略大小写：
  ```java
  // 链式调用方式使用
  ValidX validator = ValidX.init();
  
  // 默认忽略大小写
  validator.isFileExtension("document.xls", new String[]{"XLS"});
  
  // 明确指定忽略大小写
  validator.isFileExtension("document.xls", new String[]{"XLS"}, true);
  
  // 不忽略大小写
  validator.isFileExtension("document.xls", new String[]{"XLS"}, false);
  ```

[↑ 返回快速查询表](#快速查询表)

#### @FileSize
* 校验规则：文件大小验证，验证文件大小是否在指定范围内。
* 支持的类型：
  - `java.io.File` - File 对象
  - `java.nio.file.Path` - NIO Path 对象
  - `byte[]` - 字节数组
  - `org.springframework.web.multipart.MultipartFile` - Spring 文件上传对象（需要 Spring 依赖）
* 配置选项：
  - `min`：最小文件大小（人性化格式，如 "1KB"、"10MB"），默认为 "0B"
  - `max`：最大文件大小（人性化格式，如 "1KB"、"10MB"），默认不限制
  - `allowedTypes`：允许的 MIME 类型（仅对 MultipartFile 有效）
* 示例格式：带单位的文件大小，如 "10KB"、"5MB"、"1GB"
* 使用示例：
  ```java
  // 指定最小和最大值
  @FileSize(min = "1KB", max = "10MB")
  private File document;

  // 只指定最大值
  @FileSize(max = "5MB")
  private Path filePath;

  // 使用字节数组
  @FileSize(max = "1MB")
  private byte[] imageData;

  // 使用 MultipartFile（Spring）- 并限制 MIME 类型
  @FileSize(min = "100KB", max = "5MB", allowedTypes = {"image/jpeg", "image/png"})
  private MultipartFile avatar;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 只指定最大值
  validator.isFileSize(file, "10MB");

  // 指定最小和最大值
  validator.isFileSize(file, "1KB", "10MB");
  ```
* 注意事项：
  - 支持的大小单位：B（字节）、KB（千字节）、MB（兆字节）、GB（千兆字节）、TB（太字节）
  - 1KB = 1024 字节（二进制单位）
  - 支持小数值："1.5GB"、"0.5MB"
  - MIME 类型验证仅对 MultipartFile 有效
  - MultipartFile 支持使用反射实现，不需要强依赖 Spring


[↑ 返回快速查询表](#快速查询表)

#### @Lower
* 校验规则：小写字符验证，只允许小写英文字母。
* 示例格式：`abcdef`
* 使用示例：
  ```java
  // 注解方式使用
  @Lower
  private String text;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isLower("abcdef");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Upper
* 校验规则：大写字符验证，只允许大写英文字母。
* 示例格式：`ABCDEF`
* 使用示例：
  ```java
  // 注解方式使用
  @Upper
  private String text;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isUpper("ABCDEF");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Xdigit
* 校验规则：十六进制字符串验证，只允许十六进制字符（0-9, a-f, A-F）。
* 示例格式：`0a1B2c3D`
* 使用示例：
  ```java
  // 注解方式使用
  @Xdigit
  private String hex;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isXdigit("0a1B2c3D");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Password
* 校验规则：密码强度验证，验证密码是否满足指定的强度要求。
* 验证规则：
  - 最小长度--默认8位
  - 是否包含字母(大写)、字母(小写)、数字和特殊符号（默认都必须有）
  - requireUppercase:是否包含大写字母（默认值是）
  - requireLowercase:是否包含小写字母（默认值是）
  - requireDigit：是否包含数字（默认值是）
  - requireSpecialChar:是否包含特殊字符（默认值是）
* 使用示例：
  ```java
  // 使用默认规则（最小长度8位，必须包含大小写字母、数字和特殊字符）
  @Password
  private String password;

  // 指定最小长度
  @Password(minLength = 6)
  private String simplePassword;

  // 指定最小长度，并且不要求特殊字符
  @Password(minLength = 6, requireSpecialChar = false)
  private String customPassword;
  ```
* 使用链式调用时也可以指定密码强度要求：
  ```java
  // 链式调用方式使用
  ValidX validator = ValidX.init();
  
  // 使用默认规则（最小长度8位，必须包含大小写字母、数字和特殊字符）
  validator.isPassword("MyPassword123!");
  
  // 指定最小长度
  validator.isPassword("mypassword123", 8);
  
  // 完全自定义规则（最小长度8位，不要求大写字母，要求小写字母、数字，不要求特殊字符）
  validator.isPassword("mypassword123", 8, false, true, true, false);
  ```

[↑ 返回快速查询表](#快速查询表)

#### @UUID
* 校验规则：UUID（通用唯一识别码）格式验证，支持标准格式（带连字符）和紧凑格式（不带连字符）。
* 支持的格式：
  - 标准格式（带连字符）：`550e8400-e29b-41d4-a716-446655440000`
  - 紧凑格式（不带连字符）：`550e8400e29b41d4a716446655440000`（当 `allowWithoutHyphens = true` 时）
* 配置选项：
  - `allowWithoutHyphens`：是否允许不带连字符的格式，默认为 `false`（仅允许标准格式）
* 使用示例：
  ```java
  // 只允许标准格式（带连字符）
  @UUID
  private String id;

  // 允许两种格式（带连字符和不带连字符）
  @UUID(allowWithoutHyphens = true)
  private String transactionId;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 验证标准格式
  validator.isUUID("550e8400-e29b-41d4-a716-446655440000");

  // 允许紧凑格式
  validator.isUUID("550e8400e29b41d4a716446655440000", true);
  ```
* 注意事项：
  - UUID验证不区分大小写（大小写十六进制字符都允许）
  - 标准格式必须在特定位置包含恰好4个连字符
  - 紧凑格式必须是恰好32个十六进制字符
  - 支持所有常见的UUID版本（v1、v4等）


[↑ 返回快速查询表](#快速查询表)

#### @Base64
* 校验规则：Base64编码格式验证，支持标准Base64和URL-safe Base64格式。
* 支持的格式：
  - 标准格式：A-Z, a-z, 0-9, +, / 加上填充符（=）
  - URL-safe格式：A-Z, a-z, 0-9, -, _ 加上填充符（=）
* 配置选项：
  - `urlSafe`：是否使用URL-safe格式，默认为 `false`（使用标准格式）
  - `allowNoPadding`：是否允许不带填充符的格式，默认为 `false`（必须有填充）
* 使用示例：
  ```java
  // 只允许标准Base64格式
  @Base64
  private String data;

  // URL-safe Base64格式
  @Base64(urlSafe = true)
  private String urlSafeData;

  // 允许不带填充符的格式
  @Base64(allowNoPadding = true)
  private String noPaddingData;

  // URL-safe + 允许不带填充符
  @Base64(urlSafe = true, allowNoPadding = true)
  private String jwtPayload;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 验证标准格式
  validator.isBase64("SGVsbG8gV29ybGQ=");

  // 验证URL-safe格式
  validator.isBase64("SGVsbG8gV29ybGQ=", true);

  // 允许不带填充符的格式
  validator.isBase64("SGVsbG8gV29ybGQ", false, true);
  ```
* 注意事项：
  - 标准Base64使用+/字符，URL-safe使用-_字符
  - 填充符=只能出现在末尾，最多2个
  - 字符串长度必须是4的倍数（除非启用allowNoPadding）
  - 常见应用场景：文件上传、JWT令牌、图片数据传输


[↑ 返回快速查询表](#快速查询表)

#### @Age
* 校验规则：基于出生日期或身份证号码的年龄验证，支持最小年龄和最大年龄限制。
* 支持的类型：
  - `java.time.LocalDate` - 出生日期
  - `java.util.Date` - 出生日期
  - `String` - 出生日期字符串或身份证号码
* 配置选项：
  - `min`：最小年龄（包含），0表示不限制，默认为0
  - `max`：最大年龄（包含），0表示不限制，默认为0
  - `fromIdCard`：是否从身份证号码中提取出生日期，默认为 `false`
  - `dateFormat`：日期格式（仅当字段类型为String且fromIdCard=false时有效），默认为"yyyy-MM-dd"
* 使用示例：
  ```java
  // 验证年龄在18到65岁之间
  @Age(min = 18, max = 65)
  private LocalDate birthDate;

  // 只验证最小年龄
  @Age(min = 18)
  private String birthDateStr;  // "1990-01-01"

  // 从身份证号提取年龄验证
  @Age(min = 18, max = 65, fromIdCard = true)
  private String idCard;

  // 指定日期格式
  @Age(min = 18, dateFormat = "yyyy/MM/dd")
  private String birthDate;  // "1990/01/01"

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 只验证最小年龄
  validator.isAge(LocalDate.now().minusYears(25), 18);

  // 验证年龄范围
  validator.isAge("1990-01-01", 18, 65);

  // 从身份证提取
  validator.isAge("11010119900101001X", 18, 65, true);

  // 自定义日期格式
  validator.isAge("1990/06/15", 18, 65, false, "yyyy/MM/dd");
  ```
* 注意事项：
  - 年龄按周岁计算（从出生日期到当前日期的完整年数）
  - 支持15位和18位中国身份证号码格式
  - null或空值通过验证（由@NotNull/@NotEmpty处理）
  - 未来的出生日期视为0岁
  - 自动尝试常见日期格式:yyyy-MM-dd、yyyy/MM/dd、yyyyMMdd


[↑ 返回快速查询表](#快速查询表)

#### @JSON
* 校验规则：JSON格式验证，支持标准JSON语法，可配置类型限制、深度限制和长度限制。
* 支持的类型：
  - OBJECT：仅JSON对象（如：`{"key":"value"}`）
  - ARRAY：仅JSON数组（如：`[1,2,3]`）
  - ANY：对象和数组都允许（默认）
* 配置选项：
  - `type`：JSON类型限制（ANY/OBJECT/ARRAY），默认为ANY
  - `strict`：是否强制严格JSON语法，默认为 `true`
  - `maxDepth`：最大嵌套深度（0表示不限制），默认为0
  - `maxLength`：最大字符串长度（0表示不限制），默认为0
* 示例格式：`{"name":"John","age":30}`, `[1,2,3]`, `{"users":[{"id":1}]}`
* 使用示例：
  ```java
  // 只允许有效的JSON（任意类型）
  @JSON
  private String data;

  // 只允许JSON对象
  @JSON(type = JSON.JSONType.OBJECT)
  private String config;

  // 只允许JSON数组
  @JSON(type = JSON.JSONType.ARRAY)
  private String items;

  // 限制嵌套深度以防止深度嵌套结构
  @JSON(maxDepth = 5)
  private String jsonData;

  // 限制字符串长度以防止超大JSON
  @JSON(maxLength = 1000)
  private String jsonPayload;

  // 组合多个限制
  @JSON(type = JSON.JSONType.OBJECT, strict = true, maxDepth = 10, maxLength = 5000)
  private String apiRequest;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 验证任意JSON类型
  validator.isJSON("{\"name\":\"John\",\"age\":30}");

  // 验证特定类型
  validator.isJSON("[1,2,3]", JSON.JSONType.ARRAY);

  // 控制严格模式
  validator.isJSON("{\"key\":\"value\"}", JSON.JSONType.OBJECT, true);

  // 完全控制所有选项
  validator.isJSON("{\"data\":{\"nested\":true}}", JSON.JSONType.OBJECT, true, 5, 1000);
  ```
* 注意事项：
  - 使用轻量级内置JSON解析器，无需外部依赖
  - 支持所有JSON类型：对象、数组、字符串、数字、布尔值、null
  - 处理转义序列（\n, \t, \", \\等）和Unicode转义（\uXXXX）
  - 严格模式强制执行正确的JSON语法（无尾随逗号、引号键名）
  - 深度限制有助于防止深度嵌套结构导致的栈溢出
  - 长度限制有助于防止大型JSON字符串导致的内存问题
  - 常见应用场景：API请求/响应验证、配置文件验证、数据序列化


[↑ 返回快速查询表](#快速查询表)

#### @PhoneNumber
* 校验规则：国际电话号码验证，支持多种国际电话号码格式，包括E.164标准格式。
* 支持的格式：
  - E.164格式：`+8613812345678`, `+14155552671`
  - 带空格：`+86 138 1234 5678`, `+1 415 555 2671`
  - 带连字符：`+1-415-555-2671`
  - 带括号：`+1 (415) 555-2671`, `(555) 123-4567`
  - 本地格式：`13812345678`, `138 1234 5678`
  - 带分机号：`+1-415-555-2671 ext. 123`, `+14155552671 x123`, `+14155552671#456`
* 配置选项：
  - `countryCode`：限制特定国家代码（如 "+86", "+1"），默认为空（接受所有国家）
  - `allowExtension`：是否允许分机号（ext., x, #），默认为 `true`
  - `strict`：严格模式要求必须包含国家代码（以+开头），默认为 `false`
* 示例格式：`+8613812345678`, `+1-415-555-2671`, `(555) 123-4567 ext. 123`
* 使用示例：
  ```java
  // 允许任何有效的国际电话号码
  @PhoneNumber
  private String phoneNumber;

  // 只允许特定国家代码（中国）
  @PhoneNumber(countryCode = "+86")
  private String chinaPhone;

  // 只允许特定国家代码（美国）
  @PhoneNumber(countryCode = "+1")
  private String usaPhone;

  // 不允许分机号
  @PhoneNumber(allowExtension = false)
  private String directPhone;

  // 严格模式：必须包含国家代码
  @PhoneNumber(strict = true)
  private String internationalPhone;

  // 组合多个限制
  @PhoneNumber(countryCode = "+1", allowExtension = true, strict = true)
  private String companyPhone;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 验证任意电话号码
  validator.isPhoneNumber("+8613812345678");

  // 验证特定国家代码
  validator.isPhoneNumber("+14155552671", "+1");

  // 控制分机号
  validator.isPhoneNumber("+1-415-555-2671 ext. 123", "", true);

  // 完全控制所有选项
  validator.isPhoneNumber("+1-415-555-2671 ext. 123", "+1", true, true);
  ```
* 注意事项：
  - 支持E.164国际标准格式（+后跟4-15位数字）
  - 接受多种格式字符：空格、连字符、括号、点号
  - 分机号格式：ext., extension, x, #
  - 国家代码长度：1-3位数字
  - 电话号码长度（仅数字）：4-15个字符
  - 严格模式强制执行国际格式（必须以+开头）
  - 常见应用场景：用户注册、联系人管理、国际通信


[↑ 返回快速查询表](#快速查询表)

#### @JWT
* 校验规则：JWT（JSON Web Token）格式验证，验证JWT Token的基本格式是否正确。
* JWT格式说明：
  - JWT由三部分组成，用点（.）分隔：`header.payload.signature`
  - Header：Base64URL编码的JSON对象，描述令牌类型和签名算法
  - Payload：Base64URL编码的JSON对象，包含声明（claims）
  - Signature：签名，用于验证令牌的完整性
  - 每部分都使用Base64URL编码（A-Z, a-z, 0-9, -, _）
* 示例格式：
  - `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c`
* 使用示例：
  ```java
  // 注解方式使用
  @JWT
  private String token;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isJWT("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U");
  ```
* 注意事项：
  - 此验证器只验证JWT的基本格式（三部分结构和Base64URL编码）
  - 不验证签名的有效性（需要密钥）
  - 不验证过期时间等声明
  - 常见应用场景：API认证、单点登录(SSO)、信息交换


[↑ 返回快速查询表](#快速查询表)

#### @SemVer
* 校验规则：语义化版本号（Semantic Versioning）格式验证，验证版本号是否符合SemVer 2.0.0规范。
* 版本号格式说明：
  - 基础格式：`MAJOR.MINOR.PATCH`（主版本号.次版本号.修订号）
  - MAJOR：主版本号，不兼容的API修改时递增
  - MINOR：次版本号，向下兼容的功能性新增时递增
  - PATCH：修订号，向下兼容的问题修正时递增
  - 预发布版本：`MAJOR.MINOR.PATCH-prerelease`（如：1.0.0-alpha, 1.0.0-beta.1）
  - 构建元数据：`MAJOR.MINOR.PATCH+build`（如：1.0.0+20130313144700）
  - 完整格式：`MAJOR.MINOR.PATCH-prerelease+build`
* 示例格式：
  - 基础版本：`1.0.0`, `2.1.3`, `10.20.30`
  - 预发布版本：`1.0.0-alpha`, `1.0.0-beta.1`, `2.1.0-rc.2`
  - 带构建元数据：`1.0.0+20130313144700`, `1.0.0+001`
  - 完整格式：`1.0.0-alpha+001`, `1.0.0-beta+exp.sha.5114f85`
  - 带v前缀（需开启）：`v1.0.0`, `v2.1.3-beta`
* 使用示例：
  ```java
  // 注解方式使用 - 标准格式
  @SemVer
  private String version;

  // 注解方式使用 - 允许v前缀
  @SemVer(allowVPrefix = true)
  private String versionWithPrefix;

  // 链式调用方式使用 - 标准格式
  ValidX validator = ValidX.init();
  validator.isSemVer("1.0.0");
  validator.isSemVer("2.1.3-beta.1");

  // 链式调用方式使用 - 允许v前缀
  ValidX validator2 = ValidX.init();
  validator2.isSemVer("v1.0.0", true);
  validator2.isSemVer("v2.1.3-rc.1", true);
  ```
* 注意事项：
  - 严格遵循SemVer 2.0.0规范（ https://semver.org/ ）
  - 版本号各部分不能有前导零（0除外），如：`01.0.0`是无效的
  - 版本号必须包含三个部分，如：`1.0`是无效的
  - 预发布标识符由字母数字和连字符组成，用点号分隔
  - 构建元数据不影响版本优先级，仅用于标识构建信息
  - 默认不允许v前缀，需要时通过`allowVPrefix=true`开启
  - 常见应用场景：软件版本管理、npm包版本、API版本控制、Git标签


[↑ 返回快速查询表](#快速查询表)

#### @Timestamp
* 校验规则：Unix时间戳格式验证，验证值是否为有效的Unix时间戳（支持秒和毫秒）。
* 时间戳格式说明：
  - 秒级时间戳：10位数字字符串或Long值（如：`1700000000`）
  - 毫秒级时间戳：13位数字字符串或Long值（如：`1700000000000`）
  - 支持String和Long两种类型进行校验
* 参数说明：
  - `unit`：指定时间戳单位，默认为`ANY`（同时接受秒和毫秒）
    - `TimestampUnit.SECONDS` — 仅接受10位（秒级）时间戳
    - `TimestampUnit.MILLISECONDS` — 仅接受13位（毫秒级）时间戳
    - `TimestampUnit.ANY` — 同时接受10位（秒级）和13位（毫秒级）时间戳
* 使用示例：
  ```java
  // 注解方式使用 - 同时接受秒和毫秒
  @Timestamp
  private String createTime;

  // 注解方式使用 - 仅接受秒级
  @Timestamp(unit = Timestamp.TimestampUnit.SECONDS)
  private String createTimeSec;

  // 注解方式使用 - 仅接受毫秒级
  @Timestamp(unit = Timestamp.TimestampUnit.MILLISECONDS)
  private Long createTimeMs;

  // 链式调用方式使用 - 默认ANY模式
  ValidX validator = ValidX.init();
  validator.isTimestamp("1700000000");
  validator.isTimestamp("1700000000000");

  // 链式调用方式使用 - 指定单位
  ValidX validator2 = ValidX.init();
  validator2.isTimestamp("1700000000", Timestamp.TimestampUnit.SECONDS);
  validator2.isTimestamp(1700000000000L, Timestamp.TimestampUnit.MILLISECONDS);
  ```
* 注意事项：
  - 秒级时间戳必须恰好为10位数字（范围：0 ~ 9999999999）
  - 毫秒级时间戳必须恰好为13位数字（范围：0 ~ 99999999999）
  - 不接受负数值（Unix时间戳为非负数）
  - 不接受非数字字符（字母、特殊字符、小数、空格等）
  - null值不由本注解校验（需要null检查请搭配`@NotNull`使用）
  - 任何模式下，位数不正确的值（如9位、11位、12位）都会被拒绝
  - 常见应用场景：API时间戳参数、数据库时间字段、消息队列时间戳


[↑ 返回快速查询表](#快速查询表)

#### @CronExpression
* 校验规则：Cron表达式格式验证，验证值是否为有效的Cron表达式。
* 支持的格式：
  - 6位格式：秒 分 时 日 月 周（如：`0 0 12 * * ?`）
  - 7位格式：秒 分 时 日 月 周 年（如：`0 0 12 * * ? 2025`）
* 支持的特殊字符：
  - `*` : 匹配任意值
  - `?` : 不指定值（仅用于日和周字段）
  - `-` : 范围（例如：`1-5`）
  - `,` : 列举（例如：`1,3,5`）
  - `/` : 步长（例如：`0/15`）
  - `L` : 最后（例如：`L`表示月的最后一天）
  - `W` : 工作日（例如：`15W`）
  - `#` : 第几个星期几（例如：`6#3`表示第3个星期五）
* 示例格式：`0 0 12 * * ?`
* 使用示例：
  ```java
  // 注解方式使用
  @CronExpression
  private String schedule;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isCronExpression("0 0 12 * * ?");
  validator.isCronExpression("0 0/15 * * * ?");
  validator.isCronExpression("0 0 9 ? * MON-FRI");
  ```
* 注意事项：
  - 日和周字段不能同时为非`?`值
  - 支持月份英文缩写（JAN-DEC）和星期英文缩写（SUN-SAT）
  - 秒、分：0-59；时：0-23；日：1-31；月：1-12；周：0-7（0和7都代表周日）
  - 年份范围：1970-2099（可选字段）
  - 常见应用场景：定时任务、作业调度、定时器触发


[↑ 返回快速查询表](#快速查询表)

#### @Duration
* 校验规则：时间段格式验证，验证值是否为有效的时间段格式。
* 支持的格式：
  - ISO 8601格式：以P开头的标准格式（例如：`PT2H30M`、`P1Y2M3D`、`P1DT12H`）
  - 简化格式：数字+单位组合（例如：`2h30m`、`1y2mo3d`、`1d12h`）
* 支持的时间单位：
  - `y/Y` - 年（例如：`P1Y` 或 `1y`）
  - `mo/MO` - 月（简化格式使用"mo"以区分分钟"m"）（例如：`P2M` 或 `2mo`）
  - `d/D` - 天（例如：`P3D` 或 `3d`）
  - `h/H` - 小时（例如：`PT4H` 或 `4h`）
  - `m/M` - 分钟（例如：`PT30M` 或 `30m`）
  - `s/S` - 秒（例如：`PT45S` 或 `45s`）
* 配置选项：
  - `format`：指定时间段格式类型 - `ISO_8601`、`SIMPLE` 或 `ANY`（默认）
* 示例格式：
  - ISO 8601：`PT2H30M`（2小时30分钟）、`P1Y2M3D`（1年2月3天）、`P1DT12H`（1天12小时）
  - 简化格式：`2h30m`（2小时30分钟）、`1y2mo3d`（1年2月3天）、`1d12h`（1天12小时）
* 使用示例：
  ```java
  // 注解方式使用 - 接受任意格式
  @Duration
  private String duration;

  // 仅接受ISO 8601格式
  @Duration(format = Duration.DurationFormat.ISO_8601)
  private String isoDuration;

  // 仅接受简化格式
  @Duration(format = Duration.DurationFormat.SIMPLE)
  private String simpleDuration;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 验证任意格式
  validator.isDuration("PT2H30M");
  validator.isDuration("2h30m");
  validator.isDuration("P1Y2M3D");
  validator.isDuration("1y2mo3d");

  // 指定格式类型
  validator.isDuration("PT2H30M", Duration.DurationFormat.ISO_8601);
  validator.isDuration("2h30m", Duration.DurationFormat.SIMPLE);
  ```
* 注意事项：
  - ISO 8601格式：`P[nY][nM][nD][T[nH][nM][nS]]` 其中P是必需的，T用于分隔日期和时间部分
  - 简化格式使用"mo"表示月份，以避免与"m"（分钟）混淆
  - 两种格式都不区分大小写
  - 必须至少指定一个时间单位
  - 年是ISO 8601标准支持的最大单位
  - 常见应用场景：任务持续时间、时间段配置、超时设置


[↑ 返回快速查询表](#快速查询表)

#### @ExpressNumber
* 校验规则：快递单号格式验证，验证值是否为有效的快递单号。
* 支持的快递公司：
  - 顺丰速运 (SF_EXPRESS)：12位数字
  - 圆通速递 (YTO_EXPRESS)：YT开头+11-13位数字，或10-13位纯数字
  - 申通快递 (STO_EXPRESS)：12位数字
  - 中通快递 (ZTO_EXPRESS)：12位数字或字母+数字组合
  - 韵达快递 (YUNDA_EXPRESS)：13位数字
  - 邮政EMS (EMS)：E字母+9位数字+CN，或2位字母+9位数字+CN
  - 京东物流 (JD_LOGISTICS)：JD开头+13-15位数字
  - 德邦快递 (DEPPON)：8-9位数字
  - 天天快递 (TTKD_EXPRESS)：12-14位数字
  - 百世快递 (BEST_EXPRESS)：10-12位数字或字母
* 配置选项：
  - `companies`：指定快递公司类型（默认为所有支持的公司）
* 示例格式：
  - 顺丰速运：`123456789012`
  - 圆通速递：`YT1234567890123`、`1234567890`
  - 邮政EMS：`E123456789CN`、`EA123456789CN`
  - 京东物流：`JD1234567890123`
  - 德邦快递：`12345678`、`123456789`
* 使用示例：
  ```java
  // 注解方式使用 - 接受所有支持的快递公司
  @ExpressNumber
  private String trackingNumber;

  // 仅接受顺丰速运
  @ExpressNumber(companies = {ExpressNumber.ExpressCompany.SF_EXPRESS})
  private String sfNumber;

  // 接受顺丰速运或圆通速递
  @ExpressNumber(companies = {ExpressNumber.ExpressCompany.SF_EXPRESS, ExpressNumber.ExpressCompany.YTO_EXPRESS})
  private String mixedNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 验证任意支持的快递公司
  validator.isExpressNumber("123456789012");
  validator.isExpressNumber("E123456789CN");
  validator.isExpressNumber("JD1234567890123");

  // 指定快递公司类型
  validator.isExpressNumber("123456789012", ExpressNumber.ExpressCompany.SF_EXPRESS);
  validator.isExpressNumber("E123456789CN", ExpressNumber.ExpressCompany.EMS);

  // 多个快递公司
  validator.isExpressNumber("123456789012", ExpressNumber.ExpressCompany.SF_EXPRESS, ExpressNumber.ExpressCompany.STO_EXPRESS);
  ```
* 注意事项：
  - 格式验证基于各快递公司的常见模式
  - 验证不会检查快递单号是否在快递公司系统中真实存在
  - 某些格式可能在不同快递公司之间重叠（例如12位数字）
  - EMS格式不区分大小写
  - 常见应用场景：电商订单管理、物流跟踪、发货验证


[↑ 返回快速查询表](#快速查询表)

#### @StartsWith
* 校验规则：前缀验证，验证字符串是否以指定的前缀开头。
* 示例格式：以指定字符串开头
* 配置选项：
  - `startsWith`：要匹配的前缀字符串
  - `ignoreCase`：是否忽略大小写，默认为 `false`（区分大小写）
* 版本信息：
  - 新增版本：1.0.0
  - 修改版本：1.2.0（链式 API 参数从 `String[]` 改为 `String`；新增 `ignoreCase` 参数）
* **重要变更（v1.0.0 → v1.2.0）**：
  - **链式 API 变更**：`isStartsWith()` 方法参数从 `String[]` 改为 `String`
  - **v1.0.0 行为**：`validator.isStartsWith("text", new String[]{"prefix"})`
  - **v1.2.0 行为**：`validator.isStartsWith("text", "prefix")`
  - **注解使用**：未变更，仍然使用 `@StartsWith(startsWith = "prefix")`
  - **新增功能**：支持 `ignoreCase` 参数进行忽略大小写匹配
  - **迁移指南**：单个前缀验证时移除数组包装；多个前缀验证请使用新的 `@StartsWithAny` 注解或 `isStartsWithAny()` 方法
* 使用示例：
  ```java
  // 注解方式使用（未变更）
  @StartsWith(startsWith = "prefix")
  private String code;

  // 忽略大小写验证
  @StartsWith(startsWith = "http://", ignoreCase = true)
  private String url;

  // 链式调用方式使用（v1.2.0 - 简化为单值参数）
  ValidX validator = ValidX.init();

  // 区分大小写（默认）
  validator.isStartsWith("prefix_string", "prefix");

  // 忽略大小写
  validator.isStartsWith("PREFIX_string", "prefix", true);
  ```
* 注意事项：
  - 默认区分大小写
  - 设置 `ignoreCase = true` 可以忽略大小写进行匹配
  - null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @StartsWithAny
* 校验规则：多前缀验证，验证字符串是否以指定的任意一个前缀开头。
* 示例格式：`"http://example.com"` 以 `"http://"` 或 `"https://"` 开头，`"张先生"` 以 `"Mr."`、`"Mrs."`、`"Ms."` 或 `"Dr."` 开头
* 配置选项：
  - `value`：要匹配的前缀数组（至少匹配其中一个）
  - `ignoreCase`：是否忽略大小写，默认为 `false`（区分大小写）
* 示例格式：
  - URL 协议：`"http://example.com"` 匹配 `{"http://", "https://"}`
  - 称谓：`"Dr. Smith"` 匹配 `{"Mr.", "Mrs.", "Ms.", "Dr."}`
  - 中文姓氏：`"张三"` 匹配 `{"张", "王", "李", "赵"}`
  - 文件路径：`"/home/user"` 匹配 `{"/home/", "/usr/", "/opt/"}`
* 使用示例：
  ```java
  // 注解方式使用 - URL 协议验证
  @StartsWithAny({"http://", "https://"})
  private String url;

  // 称谓验证
  @StartsWithAny({"Mr.", "Mrs.", "Ms.", "Dr."})
  private String title;

  // 中文姓氏验证
  @StartsWithAny({"张", "王", "李", "赵"})
  private String chineseName;

  // 文件路径验证
  @StartsWithAny({"/home/", "/usr/", "/opt/"})
  private String filePath;

  // 忽略大小写验证（不区分大小写）
  @StartsWithAny(value = {"http://", "https://"}, ignoreCase = true)
  private String urlCaseInsensitive;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 基本用法（区分大小写）
  validator.isStartsWithAny("http://example.com", new String[]{"http://", "https://"});

  // 忽略大小写（不区分大小写）
  validator.isStartsWithAny("HTTP://example.com", new String[]{"http://", "https://"}, true);

  // 多个前缀选项
  validator.isStartsWithAny("张先生", new String[]{"Mr.", "Mrs.", "Ms.", "Dr."});

  // 中文文本验证
  validator.isStartsWithAny("张三", new String[]{"张", "王", "李", "赵"});

  // 检查验证结果
  if (!validator.passed()) {
      System.out.println(validator.getErrors());
  }
  ```
* 注意事项：
  - 默认区分大小写（如："HTTP://" 不会匹配 "http://"）
  - 设置 `ignoreCase = true` 可以忽略大小写进行匹配
  - null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty` 使用）
  - 空前缀数组会导致验证失败
  - 空字符串前缀会匹配所有字符串（任何字符串都以空字符串开头）
  - 常见应用场景：URL 验证、文件路径验证、称谓/前缀验证、中文姓氏验证

[↑ 返回快速查询表](#快速查询表)

#### @EndsWith
* 校验规则：后缀验证，验证字符串是否以指定的后缀结尾。
* 示例格式：以指定字符串结尾
* 配置选项：
  - `endsWith`：要匹配的后缀字符串
  - `ignoreCase`：是否忽略大小写，默认为 `false`（区分大小写）
* 版本信息：
  - 新增版本：1.0.0
  - 修改版本：1.2.0（链式 API 参数从 `String[]` 改为 `String`；新增 `ignoreCase` 参数）
* **重要变更（v1.0.0 → v1.2.0）**：
  - **链式 API 变更**：`isEndsWith()` 方法参数从 `String[]` 改为 `String`
  - **v1.0.0 行为**：`validator.isEndsWith("text", new String[]{"suffix"})`
  - **v1.2.0 行为**：`validator.isEndsWith("text", "suffix")`
  - **注解使用**：未变更，仍然使用 `@EndsWith(endsWith = "suffix")`
  - **新增功能**：支持 `ignoreCase` 参数进行忽略大小写匹配
  - **迁移指南**：单个后缀验证时移除数组包装；多个后缀验证请使用新的 `@EndsWithAny` 注解或 `isEndsWithAny()` 方法
* 使用示例：
  ```java
  // 注解方式使用（未变更）
  @EndsWith(endsWith = "suffix")
  private String code;

  // 忽略大小写验证
  @EndsWith(endsWith = ".txt", ignoreCase = true)
  private String filename;

  // 链式调用方式使用（v1.2.0 - 简化为单值参数）
  ValidX validator = ValidX.init();

  // 区分大小写（默认）
  validator.isEndsWith("string_suffix", "suffix");

  // 忽略大小写
  validator.isEndsWith("file.TXT", ".txt", true);
  ```
* 注意事项：
  - 默认区分大小写
  - 设置 `ignoreCase = true` 可以忽略大小写进行匹配
  - null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty` 使用）

[↑ 返回快速查询表](#快速查询表)

#### @EndsWithAny
* 校验规则：多后缀验证，验证字符串是否以指定的任意一个后缀结尾。
* 示例格式：`"photo.jpg"` 以 `".jpg"`、`".jpeg"`、`".png"` 或 `".gif"` 结尾，`"report.pdf"` 以 `".txt"`、`".doc"`、`".docx"` 或 `".pdf"` 结尾
* 配置选项：
  - `value`：要匹配的后缀数组（至少匹配其中一个）
  - `ignoreCase`：是否忽略大小写，默认为 `false`（区分大小写）
* 示例格式：
  - 图片文件：`"photo.jpg"` 匹配 `{".jpg", ".jpeg", ".png", ".gif"}`
  - 文档文件：`"report.pdf"` 匹配 `{".txt", ".doc", ".docx", ".pdf"}`
  - 中文姓名后缀：`"张先生"` 匹配 `{"先生", "女士", "小姐"}`
  - 压缩文件：`"data.tar.gz"` 匹配 `{".zip", ".rar", ".7z", ".tar.gz"}`
* 使用示例：
  ```java
  // 注解方式使用 - 图片文件验证
  @EndsWithAny({".jpg", ".jpeg", ".png", ".gif"})
  private String imageFile;

  // 文档文件验证
  @EndsWithAny({".txt", ".doc", ".docx", ".pdf"})
  private String documentFile;

  // 中文姓名后缀验证
  @EndsWithAny({"先生", "女士", "小姐"})
  private String chineseName;

  // 压缩文件验证
  @EndsWithAny({".zip", ".rar", ".7z", ".tar.gz"})
  private String archiveFile;

  // 忽略大小写验证（不区分大小写）
  @EndsWithAny(value = {".jpg", ".jpeg", ".png"}, ignoreCase = true)
  private String imageCaseInsensitive;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 基本用法（区分大小写）
  validator.isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png", ".gif"});

  // 忽略大小写（不区分大小写）
  validator.isEndsWithAny("photo.JPG", new String[]{".jpg", ".jpeg", ".png"}, true);

  // 多个后缀选项
  validator.isEndsWithAny("report.pdf", new String[]{".txt", ".doc", ".docx", ".pdf"});

  // 中文文本验证
  validator.isEndsWithAny("张先生", new String[]{"先生", "女士", "小姐"});

  // 检查验证结果
  if (!validator.passed()) {
      System.out.println(validator.getErrors());
  }
  ```
* 注意事项：
  - 默认区分大小写（如：".JPG" 不会匹配 ".jpg"）
  - 设置 `ignoreCase = true` 可以忽略大小写进行匹配
  - null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty` 使用）
  - 空后缀数组会导致验证失败
  - 空字符串后缀会匹配所有字符串（任何字符串都以空字符串结尾）
  - 常见应用场景：文件扩展名验证、压缩格式验证、名称后缀验证、中文称谓验证

[↑ 返回快速查询表](#快速查询表)

#### @Contains
* 校验规则：包含验证，验证字符串是否包含指定的子字符串。支持多种匹配模式（OR/AND）和忽略大小写。
* 示例格式：`"hello world"` 包含 `"hello"`，`"test@example.com"` 同时包含 `"@"` 和 `"."`
* 配置选项：
  - `value`：要匹配的子字符串数组
  - `ignoreCase`：是否忽略大小写，默认为 `false`
  - `matchAll`：匹配模式，默认为 `false`
    - `false`（默认）：OR 逻辑 - 包含任意一个子字符串即可
    - `true`：AND 逻辑 - 必须包含所有子字符串
* 使用示例：
  ```java
  // 注解方式使用 - 单个子字符串（OR 逻辑）
  @Contains({"@"})
  private String email;

  // 多个子字符串（OR 逻辑 - 满足任意一个）
  @Contains({"产品", "服务"})
  private String description;

  // 多个子字符串（AND 逻辑 - 必须全部满足）
  @Contains(value = {"@", "."}, matchAll = true)
  private String emailStrict;

  // 忽略大小写匹配
  @Contains(value = {"HELLO"}, ignoreCase = true)
  private String greeting;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 基本用法（OR 逻辑）
  validator.isContains("hello world", new String[]{"hello"});

  // 多个子字符串（OR 逻辑）
  validator.isContains("test@example.com", new String[]{"@", ".com"});

  // 忽略大小写（OR 逻辑）
  validator.isContains("Hello World", new String[]{"hello"}, true);

  // AND 逻辑 - 必须包含所有子字符串
  validator.isContains("test@example.com", new String[]{"@", "."}, false, true);
  ```
* 注意事项：
  - **OR 逻辑**（默认）：只要包含数组中的任意一个子字符串即可通过验证
  - **AND 逻辑**（`matchAll = true`）：必须包含数组中的所有子字符串才能通过验证
  - 子字符串可以出现在任何位置（开头、中间或结尾）
  - 默认区分大小写；使用 `ignoreCase = true` 可忽略大小写
  - 常见应用场景：邮箱验证（`@`）、严格邮箱验证（`@` 和 `.`）、URL检查（`http://`）、密码强度验证（必须包含多种字符类型）、内容过滤

[↑ 返回快速查询表](#快速查询表)

#### @NotContains
* 校验规则：不包含验证，验证字符串是否不包含指定的子字符串。适用于安全验证、内容过滤和防止敏感关键词。
* 示例格式：`"user123"` 不包含 `"admin"`，`"https://example.com"` 不包含 `"javascript:"`
* 配置选项：
  - `value`：禁止的子字符串数组
  - `ignoreCase`：是否忽略大小写，默认为 `false`
  - `matchAll`：匹配模式，默认为 `true`
    - `true`（默认）：AND 逻辑 - 必须所有禁止的子字符串都不包含
    - `false`：OR 逻辑 - 至少有一个禁止的子字符串不包含即可
* 使用示例：
  ```java
  // 注解方式使用 - 安全验证
  @NotContains(value = {"admin", "root", "system"}, ignoreCase = true)
  private String username;

  // 内容过滤
  @NotContains(value = {"垃圾", "广告"}, ignoreCase = true)
  private String comment;

  // XSS防护
  @NotContains(value = {"<script", "javascript:", "onerror="}, ignoreCase = true)
  private String userInput;

  // URL安全验证（AND 逻辑 - 必须都不包含）
  @NotContains(value = {"javascript:", "data:", "vbscript:"}, matchAll = true)
  private String url;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 基本用法（AND 逻辑 - 默认）
  validator.isNotContains("user123", new String[]{"admin", "root"});

  // 忽略大小写
  validator.isNotContains("normaluser", new String[]{"ADMIN", "ROOT"}, true);

  // OR 逻辑 - 至少有一个不包含即可
  validator.isNotContains("hello world", new String[]{"script", "alert"}, false, false);

  // AND 逻辑 - 必须全都不包含
  validator.isNotContains("https://example.com", new String[]{"javascript:", "data:"}, false, true);
  ```
* 注意事项：
  - **AND 逻辑**（默认）：只有字符串不包含所有指定的子字符串时才通过验证
  - **OR 逻辑**（`matchAll = false`）：字符串至少不包含一个指定的子字符串即可通过验证
  - 默认区分大小写；使用 `ignoreCase = true` 可忽略大小写
  - 常见应用场景：用户名验证（阻止保留关键字）、XSS防护、内容审核、URL安全验证
  - 与 `@Contains` 互补，提供全面的字符串验证

[↑ 返回快速查询表](#快速查询表)

#### @Enum
* 校验规则：单个元素或者多个元素的枚举值验证，验证其是否为指定枚举中的有效值。
* 示例格式：指定枚举值之一
* 使用示例：
  ```java
  // 验证单个枚举值（默认验证code()值）
  @Enum(target = MyEnum.class)
  private String status;
  
  // 验证枚举的特定字段值
  @Enum(target = MyEnum.class, field = "type")
  private String statusCode;
  
  // 验证枚举值集合（每个元素都必须是指定枚举的有效值）
  @Enum(target = MyEnum.class)
  private List<String> statuses;
  
  // 验证特定字段值的枚举值集合
  @Enum(target = MyEnum.class, field = "type")
  private List<String> statusCodes;
  ```
* 使用链式调用时也可以指定枚举字段：
  ```java
  // 链式调用方式使用 - 单个值验证
  ValidX validator = ValidX.init();
  
  // 验证枚举的name()值（默认）
  validator.isEnum("VALUE1", MyEnum.class);
  
  // 验证枚举的特定字段值（如code字段）
  validator.isEnum("code001", MyEnum.class, "code");
  
  // 链式调用方式使用 - 集合验证
  List<String> statuses = Arrays.asList("VALUE1", "VALUE2");
  validator.isEnum(statuses, MyEnum.class);
  
  // 链式调用方式使用 - 数组验证
  String[] statusArray = {"VALUE1", "VALUE2"};
  validator.isEnum(statusArray, MyEnum.class);
  ```
* 示例枚举类：
  ```java
  public enum StatusEnum {
      ACTIVE("active"),
      INACTIVE("inactive");
      
      private final String code;
      
      StatusEnum(String code) {
          this.code = code;
      }
      
      public String getCode() {
          return code;
      }
  }
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Color
* 校验规则：颜色格式验证，验证字符串是否为有效的HEX颜色值，支持 #FFF 或 #FFFFFF 格式。
* 示例格式：`#FF0000`, `#F00`, `#ffffff`, `#000`
* 使用示例：
  ```java
  // 注解方式使用
  @Color
  private String color;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isColor("#FF0000");
  ```

[↑ 返回快速查询表](#快速查询表)

### 身份验证相关

#### @ChineseName
* 校验规则：中国人姓名验证，验证字符串是否符合中国人姓名规范。
* 验证要求：
  - 只能包含中文字符
  - 长度在 2-50 个字符之间（覆盖所有中文姓名包括极长的少数民族姓名）
  - 支持少数民族姓名中的间隔号 "·"
  - 不能包含数字、字母、特殊字符
* 示例格式：
  - 汉族姓名：`张三`、`李四`、`欧阳修`、`诸葛亮`
  - 少数民族姓名：`买买提·吐尔逊`、`迪丽热巴·迪力木拉提`
  - 历史人物名：`爱新觉罗·玄烨`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseName
  private String realName;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseName("张三");
  validator.isChineseName("买买提·吐尔逊");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseIdCard
* 校验规则：中国大陆身份证号码验证，支持18位和15位身份证号码。
* 示例格式：`11010119900307211X` (18位) 或 `11010119900307211` (15位)
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseIdCard
  private String idCard;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseIdCard("11010119900307211X");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChinesePassport
* 校验规则：中国护照号码验证，支持各种类型的中国护照号码。
* 示例格式：`G12345678`, `E12345678`, `S12345678`, `D1234567`, `P1234567`
* 使用示例：
  ```java
  // 注解方式使用
  @ChinesePassport
  private String passportNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChinesePassport("G12345678");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseMilitaryOfficer
* 校验规则：中国军官证验证，支持各种类型的中国军官证。
* 示例格式：`军字第1234567号`, `海字第1234567号`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseMilitaryOfficer
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseMilitaryOfficer("军字第1234567号");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseSoldier
* 校验规则：中国士兵证验证，支持各种类型的中国士兵证。
* 示例格式：`沈字第0100000号`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseSoldier
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseSoldier("沈字第0100000号");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ForeignerPermanentResidenceIdentity
* 校验规则：外国人永久居留身份证验证，验证外国人永久居留身份证号码。
* 示例格式：`911124198108030028`
* 使用示例：
  ```java
  // 注解方式使用
  @ForeignerPermanentResidenceIdentity
  private String identityNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isForeignerPermanentResidenceIdentity("911124198108030028");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @HKMacauResidence
* 校验规则：港澳居民居住证验证，验证港澳居民居住证号码。
* 示例格式：`810000000000000001`, `82000000000000000X`
* 使用示例：
  ```java
  // 注解方式使用
  @HKMacauResidence
  private String residenceNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isHKMacauResidence("810000000000000001");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @HKMacauPass
* 校验规则：港澳居民来往内地通行证(回乡证)验证，验证港澳居民来往内地通行证号码。
* 示例格式：`H1234567800`, `M1234567801`
* 使用示例：
  ```java
  // 注解方式使用
  @HKMacauPass
  private String passNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isHKMacauPass("H1234567800");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @TaiwanResidence
* 校验规则：台湾居民居住证验证，验证台湾居民居住证号码。
* 示例格式：`830000000000000001`
* 使用示例：
  ```java
  // 注解方式使用
  @TaiwanResidence
  private String residenceNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isTaiwanResidence("830000000000000001");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @TaiwanPass
* 校验规则：台湾居民来往大陆通行证(台胞证)验证，验证台湾居民来往大陆通行证号码。
* 示例格式：`1234567800`
* 使用示例：
  ```java
  // 注解方式使用
  @TaiwanPass
  private String passNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isTaiwanPass("1234567800");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ForeignerWorkPermit
* 校验规则：外国人工作许可证验证，验证外国人工作许可证号码。
* 示例格式：包含字母和数字的组合
* 使用示例：
  ```java
  // 注解方式使用
  @ForeignerWorkPermit
  private String permitNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isForeignerWorkPermit(" foreigners work permit number ");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @NationalityCode
* 校验规则：国籍国代码验证，验证 ISO 3166-1 国家/地区代码（两字母、三字母或三位数字）。
* 示例格式：`CA`、`CAN`、`124`
* 使用示例：
  ```java
  // 注解方式使用（默认三种形式均可）
  @NationalityCode
  private String countryCode;

  // 注解方式使用（仅数字形式，用于五星卡第 4~6 位复核）
  // 单值可省略花括号，等价于 formats = {NUMERIC}
  @NationalityCode(formats = NationalityCode.NationalityCodeType.NUMERIC)
  private String nationalityCode;

  // 注解方式使用（仅两字母和三字母两种形式，多个值必须使用花括号）
  @NationalityCode(formats = {NationalityCode.NationalityCodeType.ALPHA_2, NationalityCode.NationalityCodeType.ALPHA_3})
  private String alphaCode;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isNationalityCode("124");
  validator.isNationalityCode("124", new NationalityCode.NationalityCodeType[]{NationalityCode.NationalityCodeType.NUMERIC});
  ```

> 说明：`formats` 为数组类型，单个值可省略花括号（`formats = ALPHA_2`，等价于 `formats = {ALPHA_2}`）；两个及以上值必须使用花括号（`formats = {ALPHA_2, ALPHA_3}`）。

[↑ 返回快速查询表](#快速查询表)

#### @UnifiedSocialCreditCode
* 校验规则：统一社会信用代码验证，验证统一社会信用代码。
* 示例格式：`91350100M000100Y43`
* 使用示例：
  ```java
  // 注解方式使用
  @UnifiedSocialCreditCode
  private String creditCode;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isUnifiedSocialCreditCode("91350100M000100Y43");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChinesePhoneOrLandline
* 校验规则：中国电话号码验证，支持手机号和固定电话。
* 示例格式：支持手机号和固定电话
* 使用示例：
  ```java
  // 注解方式使用
  @ChinesePhoneOrLandline
  private String phoneNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChinesePhoneOrLandline("010-12345678");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChinesePhone
* 校验规则：中国手机号码验证，验证中国手机号码。
* 示例格式：11位手机号码
* 使用示例：
  ```java
  // 注解方式使用
  @ChinesePhone
  private String phoneNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChinesePhone("13812345678");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseLandline
* 校验规则：中国固定电话验证，验证中国固定电话号码。
* 示例格式：支持区号和分机号
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseLandline
  private String phoneNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseLandline("010-12345678");
  ```

[↑ 返回快速查询表](#快速查询表)

### 金融验证相关

#### @BankCard
* 校验规则：银行卡验证，使用Luhn算法验证银行卡号码的有效性。
* 示例格式：
  - Visa卡号：`4012888888881881`
  - MasterCard卡号：`5555555555554444`
  - 带空格的卡号：`4012 8888 8888 1881`
  - 带连字符的卡号：`4012-8888-8888-1881`
* 使用示例：
  ```java
  // 注解方式使用
  @BankCard
  private String cardNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isBankCard("4012888888881881");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @CVV
* 校验规则：CVV/CVC安全码验证，验证信用卡背面的3位或4位安全码。
* 示例格式：`123`, `1234`
* 使用示例：
  ```java
  // 注解方式使用
  @CVV
  private String cvv;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isCVV("123");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @IBAN
* 校验规则：IBAN国际银行账户号码验证，验证国际银行账户号码(IBAN)格式和校验位。
* 示例格式：`DE44500800000123456789`, `GB29NWBK60161331926819`
* 使用示例：
  ```java
  // 注解方式使用
  @IBAN
  private String iban;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isIBAN("DE44500800000123456789");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @SWIFT
* 校验规则：SWIFT/BIC代码验证，验证SWIFT/BIC银行代码格式，用于国际电汇中识别特定银行。
* 示例格式：`COBADEFF`, `DEUTDEFFXXX`
* 使用示例：
  ```java
  // 注解方式使用
  @SWIFT
  private String swiftCode;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isSWIFT("COBADEFF");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @StockCode
* 校验规则：股票代码验证，验证不同交易所的股票代码格式。
* 支持的交易所及格式：
  - 上海证券交易所: 6位数字，以6开头 (如: 600000)
  - 深圳证券交易所: 6位数字，以0、3或4开头 (如: 000001, 300001, 400001)
  - 香港联合交易所: 4-5位数字 (如: 00700, 3690)
  - 纽约证券交易所: 1-5个字母，可能包含点号 (如: AAPL, BRK.A, BRK.B)
* 使用示例：
  ```java
  // 默认支持所有交易所
  @StockCode
  private String stockCode;

  // 只支持上海证券交易所
  @StockCode(exchanges = {StockCode.Exchange.SHANGHAI})
  private String shanghaiStock;

  // 支持上海证券交易所和纽约证券交易所
  @StockCode(exchanges = {StockCode.Exchange.SHANGHAI, StockCode.Exchange.NEW_YORK})
  private String mixedStock;

  // 只支持港股和美股
  @StockCode(exchanges = {StockCode.Exchange.HONG_KONG, StockCode.Exchange.NEW_YORK})
  private String internationalStock;
  ```

* 使用链式调用时也可以指定交易所范围：
  
  ```java
  // 链式调用方式使用
  ValidX validator = ValidX.init();
  
  // 默认支持所有交易所
  validator.isStockCode("600000");
  
  // 只验证上海证券交易所
  validator.isStockCode("600000", StockCode.Exchange.SHANGHAI);
  
  // 验证上海或深圳交易所
  validator.isStockCode("000001", StockCode.Exchange.SHANGHAI, StockCode.Exchange.SHENZHEN);
  
  // 验证香港或纽约交易所
  validator.isStockCode("00700", StockCode.Exchange.HONG_KONG, StockCode.Exchange.NEW_YORK);
  ```

[↑ 返回快速查询表](#快速查询表)

#### @TradeOrderNumber
* 校验规则：交易订单号验证，验证金融交易订单号的格式。
* 支持的格式：
  - T开头+18位数字格式 (如: T123456789012345678)
  - 纯18位数字格式 (如: 123456789012345678)
  - UUID格式（带连字符或不带连字符）(如: 550e8400-e29b-41d4-a716-446655440000 或 550e8400e29b41d4a716446655440000)
* 使用示例：
  ```java
  // 默认验证所有支持的格式
  @TradeOrderNumber
  private String orderNumber;
  ```

* 使用链式调用方式：
  
  ```java
  // 链式调用方式使用
  ValidX validator = ValidX.init();
  
  // 验证T开头+18位数字格式
  validator.isTradeOrderNumber("T123456789012345678");
  
  // 验证纯18位数字格式
  validator.isTradeOrderNumber("123456789012345678");
  
  // 验证UUID格式（带连字符）
  validator.isTradeOrderNumber("550e8400-e29b-41d4-a716-446655440000");
  
  // 验证UUID格式（不带连字符）
  validator.isTradeOrderNumber("550e8400e29b41d4a716446655440000");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @FinancialProductCode
* 校验规则：金融产品代码验证，验证基金代码、债券代码等金融产品的代码格式。
* 支持的产品类型及格式：
  - 基金产品：
    - 上海证券交易所基金：以5开头的6位数字 (如: 500001, 510000)
    - 深圳证券交易所基金：以1开头的6位数字 (如: 100001, 150000)
  - 债券产品：
    - 国债：以10开头的6位数字 (如: 100001, 101234)
    - 企业债：以11开头的6位数字 (如: 110001, 111234)
    - 可转债：以12开头的6位数字 (如: 120001, 121234)
    - 公司债：以13开头的6位数字 (如: 130001, 131234)
* 使用示例：
  ```java
  // 默认支持所有产品类型
  @FinancialProductCode
  private String productCode;

  // 只支持基金产品
  @FinancialProductCode(productTypes = {FinancialProductCode.ProductType.FUND})
  private String fundCode;

  // 只支持债券产品
  @FinancialProductCode(productTypes = {FinancialProductCode.ProductType.BOND})
  private String bondCode;

  // 支持基金和债券产品
  @FinancialProductCode(productTypes = {FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND})
  private String mixedProductCode;
  ```

* 使用链式调用时也可以指定产品类型范围：
  
  ```java
  // 链式调用方式使用
  ValidX validator = ValidX.init();
  
  // 默认支持所有产品类型
  validator.isFinancialProductCode("500001");
  
  // 只验证基金产品
  validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND);
  
  // 只验证债券产品
  validator.isFinancialProductCode("100001", FinancialProductCode.ProductType.BOND);
  
  // 验证基金和债券产品
  validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND);
  ```

[↑ 返回快速查询表](#快速查询表)

### 教育/职业资格验证/认证相关的验证

#### @DegreeCertificate
* 校验规则：学位证书编号验证，验证中国学位证书编号格式。支持两种格式：
  - 普通学位证书：16位数字格式
  - 特殊学位证书：以特定字母开头后跟16位字符的格式
* 示例格式：`1075522008000001`, `C1047642016057017`
* 使用示例：
  ```java
  // 注解方式使用
  @DegreeCertificate
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isDegreeCertificate("1075522008000001");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Doctor
* 校验规则：医师资格证编号验证，验证医师资格证编号。
* 规则说明：医师资格证编号由24位或27位字符组成，包括年度代码、省级行政区代码、执业医师级别代码、执业医师类别代码和居民身份证号码
* 示例格式：`20251111014406081973100014` (24位) 或 `20251111014406081973100014123` (27位)
* 使用示例：
  ```java
  // 注解方式使用
  @Doctor
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isDoctor("20251111014406081973100014");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Teacher
* 校验规则：教师资格证编号验证，验证教师资格证编号。
* 规则说明：共17位数字，分别表示年度代码、省级行政区代码、认定机构代码、资格类型代码、性别代码和序号代码
* 示例格式：`20253412345678901`
* 使用示例：
  ```java
  // 注解方式使用
  @Teacher
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isTeacher("20253412345678901");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Lawyer
* 校验规则：法律职业资格证书/律师执业证验证，验证法律职业资格证书或律师执业证。
* 格式说明：
  * 律师执业证：17位数字，以1开头，格式为`1 + 省代码(2位) + 市代码(2位) + 年份(4位) + 类别代码(1位) + 性别代码(1位) + 序列号(6位)`
  * 法律职业资格证书：14位或16位数字
* 示例格式：
  * 律师执业证：`11101201810123456` (1+北京市11+朝阳区01+2018年+专职律师1+男0+序列号123456)
  * 法律职业资格证书：`2010130103210001` (年份2010+省代码13+市代码01+区代码03+序列号210001)
* 使用示例：
  ```java
  // 注解方式使用
  @Lawyer
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isLawyer("11101201810123456");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @PMP
* 校验规则：PMP证书编号验证，验证PMP（Project Management Professional）证书编号格式
* 规则说明：PMP证书编号通常为7位数字或包含特定前缀的组合
* 示例格式：`1234567`, `PMP123456`
* 使用示例：
  ```java
  // 注解方式使用
  @PMP
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isPMP("1234567");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Constructor
* 校验规则：建造师证书编号验证，验证一级/二级建造师证书编号格式
* 规则说明：建造师证书编号由一个汉字和12位阿拉伯数字组成，总共13位
* 示例格式：`京111050700001`, `鄂242050700001`
* 使用示例：
  ```java
  // 注解方式使用
  @Constructor
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isConstructor("京111050700001");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Accountant
* 校验规则：会计资格证书编号验证，验证会计资格证书编号格式
* 规则说明：会计资格证书编号由11位数字组成，包含年份代码、地区代码等信息
* 示例格式：`21010203451`, `22310512342`
* 使用示例：
  ```java
  // 注解方式使用
  @Accountant
  private String certificateNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isAccountant("21010203451");
  ```

[↑ 返回快速查询表](#快速查询表)

### 网络相关

#### @Domain
* 校验规则：域名验证，验证域名格式。
* 示例格式：`example.com`, `www.example.com`
* 使用示例：
  ```java
  // 注解方式使用
  @Domain
  private String domain;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isDomain("example.com");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Ip
* 校验规则：IP地址验证，支持IPv4和IPv6地址验证，可通过参数指定验证的IP版本。
* 支持的版本：
  - `Ip.IpVersion.V4`：只验证IPv4地址（如：`192.168.1.1`）
  - `Ip.IpVersion.V6`：只验证IPv6地址（如：`2001:0db8:85a3::8a2e:0370:7334`）
  - `Ip.IpVersion.ANY`：同时验证IPv4和IPv6地址（默认）
* 示例格式：
  - IPv4: `192.168.1.1`, `10.0.0.1`
  - IPv6: `2001:0db8:85a3:0000:0000:8a2e:0370:7334`, `::1`, `fe80::1`
* 使用示例：
  ```java
  // 注解方式使用 - 同时支持IPv4和IPv6（默认）
  @Ip
  private String ipAddress;

  // 只验证IPv4地址
  @Ip(version = Ip.IpVersion.V4)
  private String ipv4Address;

  // 只验证IPv6地址
  @Ip(version = Ip.IpVersion.V6)
  private String ipv6Address;

  // 链式调用方式使用
  ValidX validator = ValidX.init();

  // 验证任意IP地址（默认）
  validator.isIp("192.168.1.1");

  // 只验证IPv4地址
  validator.isIp("192.168.1.1", Ip.IpVersion.V4);

  // 只验证IPv6地址
  validator.isIp("2001:0db8:85a3::8a2e:0370:7334", Ip.IpVersion.V6);

  // 同时支持IPv4和IPv6
  validator.isIp("192.168.1.1", Ip.IpVersion.ANY);
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Mac
* 校验规则：MAC地址验证，验证MAC地址。
* 示例格式：`00:1A:2B:3C:4D:5E`, `00-1A-2B-3C-4D-5E`
* 版本信息：
  - 新增版本：1.0.0
  - 修改版本：1.2.0（链式方法重命名：`isMacAddress()` → `isMac()`，注解本身不变）
* 使用示例：
  ```java
  // 注解方式使用
  @Mac
  private String macAddress;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isMac("00:1A:2B:3C:4D:5E");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Url
* 校验规则：URL地址验证，验证URL地址格式。支持可配置的协议白名单（默认：http / https / ftp）。
* 示例格式：`http://example.com`, `https://example.com/path`, `ftp://example.com/resource`
* 配置项：
  - `protocols`：允许的协议白名单，默认为 `{"http", "https", "ftp"}`（与历史版本兼容）。如 `@Url(protocols = {"https"})` 仅允许 HTTPS，或 `{"http", "https"}` 收紧为仅 Web 协议。
* 版本信息：
  - 新增版本：1.0.0
  - 修改版本：1.2.0（新增 `protocols` 参数支持协议白名单配置）
* 使用示例：
  ```java
  // 注解方式使用（默认白名单：http / https / ftp）
  @Url
  private String url;

  // 仅 HTTPS
  @Url(protocols = {"https"})
  private String secureUrl;

  // 收紧为仅 Web 协议
  @Url(protocols = {"http", "https"})
  private String webUrl;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isUrl("http://example.com");

  // 链式调用指定协议白名单
  validator.isUrl("https://example.com", "https");
  ```
* 注意事项：
  - null 和空字符串通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty`）
  - 协议匹配大小写不敏感
  - 默认白名单允许 http / https / ftp（向下兼容），如需收紧或扩展可通过 `protocols` 配置

[↑ 返回快速查询表](#快速查询表)

#### @Email
* 校验规则：邮箱地址验证，验证邮箱地址格式。
* 示例格式：`test@example.com`, `user.name@domain.co.uk`
* 使用示例：
  ```java
  // 注解方式使用
  @Email
  private String email;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isEmail("test@example.com");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @SubnetMask
* 校验规则：子网掩码验证，验证子网掩码格式。
* 示例格式：`255.255.255.0`, `255.0.0.0`
* 使用示例：
  ```java
  // 注解方式使用
  @SubnetMask
  private String subnetMask;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isSubnetMask("255.255.255.0");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @Port
* 校验规则：端口号验证，验证端口号是否在0-65535范围内。
* 示例格式：0-65535之间的整数
* 使用示例：
  ```java
  // 注解方式使用
  @Port
  private String port;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isPort("8080");
  ```

[↑ 返回快速查询表](#快速查询表)

### 中国特定验证

#### @ChineseLicensePlate
* 校验规则：中国车牌号验证，验证中国车牌号码。
* 示例格式：`京A12345`, `京A12345D`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseLicensePlate
  private String licensePlate;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseLicensePlate("京A12345");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChinesePatent
* 校验规则：中国专利号验证，验证中国专利号。
* 示例格式：`ZL2013106997442`
* 使用示例：
  ```java
  // 注解方式使用
  @ChinesePatent
  private String patentNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChinesePatent("ZL2013106997442");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseTrademark
* 校验规则：中国商标注册号验证，验证中国商标注册号。
* 示例格式：`1234567`, `第1234567号`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseTrademark
  private String trademarkNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseTrademark("1234567");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @SoftwareCopyright
* 校验规则：计算机软件著作权登记号验证，验证计算机软件著作权登记号。
* 示例格式：`软著登字第2023001234号`
* 使用示例：
  ```java
  // 注解方式使用
  @SoftwareCopyright
  private String copyrightNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isSoftwareCopyright("软著登字第2023001234号");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @WorkCopyright
* 校验规则：一般作品著作权登记号验证，验证一般作品著作权登记号。
* 示例格式：`作登字22-2023-A-0018号`
* 使用示例：
  ```java
  // 注解方式使用
  @WorkCopyright
  private String copyrightNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isWorkCopyright("作登字22-2023-A-0018号");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ChineseZipCode
* 校验规则：中国邮政编码验证，验证中国邮政编码。
* 示例格式：`100000`, `200000`
* 使用示例：
  ```java
  // 注解方式使用
  @ChineseZipCode
  private String zipCode;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isChineseZipCode("100000");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @DrugApproval
* 校验规则：验证字符串是否是有效的中国药品批准文号.药品批准文号是国家药品监督管理部门批准药品生产企业生产药品的文号
* 示例格式： 国药准字H20210039, 国药准字ZC20171003, 国药准字HJ20233150
* 使用示例：
  ```java
  // 注解方式使用
  @DrugApproval
  private String approvalNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isDrugApproval("国药准字H20210039");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @DrugCode
* 校验规则：验证字符串是否是有效的中国药品本位码.药品本位码是以69开头，20位数字，最后一位为GS1校验位
* 示例格式： 69012345678901234563, 69123456789012345678
* 使用示例：
  ```java
  // 注解方式使用
  @DrugCode
  private String drugCode;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isDrugCode("69012345678901234563");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @MedicalDeviceRegistration
* 校验规则：医疗器械注册证号验证，用于验证中国医疗器械注册证号格式。
* 示例格式：`国械注准20243010001`, `粤械注准20242020002`, `国械注进20242030003`
* 使用示例：
  ```java
  // 注解方式使用
  @MedicalDeviceRegistration
  private String registrationNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isMedicalDeviceRegistration("国械注准20243010001");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @QQ
* 校验规则：QQ号码验证，验证QQ号码。
* 示例格式：`123456789`
* 使用示例：
  ```java
  // 注解方式使用
  @QQ
  private String qqNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isQQ("123456789");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @WeChat
* 校验规则：微信账号验证，验证微信账号格式。
* 规则说明：
  * 长度为6-20个字符
  * 必须以字母开头
  * 只能包含字母、数字、下划线和减号
* 示例格式：`wechat123`, `WeChat_123`, `WeChat-123`
* 使用示例：
  ```java
  // 注解方式使用
  @WeChat
  private String wechatId;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isWeChat("wechat123");
  ```

[↑ 返回快速查询表](#快速查询表)

### 汽车相关的验证

#### @VIN
* 校验规则：验证车辆识别号码(VIN)格式和校验位。
* 示例格式：`WP0AJ2972LL122844`
* 使用示例：
  ```java
  // 注解方式使用
  @VIN
  private String vin;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isVIN("WP0AJ2972LL122844");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @VehicleEngine
* 校验规则：验证车辆发动机编码格式。
* 示例格式：`123456`, `ABC123`, `123ABC456`
* 使用示例：
  ```java
  // 注解方式使用
  @VehicleEngine
  private String engineCode;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isVehicleEngine("123456");
  ```

[↑ 返回快速查询表](#快速查询表)

### 图书相关的验证

#### @ISBN
* 校验规则：国际标准书号验证，支持10位和13位ISBN格式。
* 示例格式：`9780306406157` (13位) 或 `0306406152` (10位)
* 使用示例：
  ```java
  // 注解方式使用
  @ISBN
  private String isbn;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isISBN("9780306406157");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ISSN
* 校验规则：国际标准连续出版物号验证，支持8位ISSN格式。
* 示例格式：`0317-8471` 或 `03178471`
* 使用示例：
  ```java
  // 注解方式使用
  @ISSN
  private String issn;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isISSN("0317-8471");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @DOI
* 校验规则：数字对象标识符验证，用于数字资源的唯一标识，广泛用于学术出版物。
* 示例格式：以"10."开头，如 `10.1000/182`
* 使用示例：
  ```java
  // 注解方式使用
  @DOI
  private String doi;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isDOI("10.1000/182");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @CLC
* 校验规则：验证字符串是否是有效的中国图书馆分类法（CLC）分类号。中国图书馆分类法是中国图书馆普遍采用的图书分类法
* 示例格式： A, B, TP, TP3, TP311, TP311.1, TP311.138, TP311.138.S6, O175.2, R329.2, F272.3
* 使用示例：
  ```java
  // 注解方式使用
  @CLC
  private String clcNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isCLC("TP311.138");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @DDC
* 校验规则：验证字符串是否是有效的杜威十进制分类法（DDC）分类号。杜威十进制分类法是广泛应用于图书馆的分类系统
* 示例格式： 000, 100, 200, ..., 999, 510, 516.3, 330.94
* 使用示例：
  ```java
  // 注解方式使用
  @DDC
  private String ddcNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isDDC("516.3");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @ORCID
* 校验规则：开放研究者与贡献者身份识别码验证，用于唯一标识学术作者和贡献者。
* 示例格式：`0000-0002-1825-0097` 或 `0000000218250097`
* 使用示例：
  ```java
  // 注解方式使用
  @ORCID
  private String orcidId;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isORCID("0000-0002-1825-0097");
  ```

[↑ 返回快速查询表](#快速查询表)

#### @IPC
* 校验规则：国际专利分类号验证，用于标识专利技术领域。
* 示例格式：`A01B1/00`, `A01B1/01`, `H01B12/00`
* 使用示例：
  ```java
  // 注解方式使用
  @IPC
  private String ipcNumber;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isIPC("A01B1/00");
  ```

[↑ 返回快速查询表](#快速查询表)

### 手机相关的验证

#### @IMEI
* 校验规则：IMEI验证，验证字符串是否为有效的国际移动设备识别码。
* 示例格式：`123412341234564` 或 `123412-341234564`
* 使用示例：
  ```java
  // 注解方式使用
  @IMEI
  private String imei;

  // 链式调用方式使用
  ValidX validator = ValidX.init();
  validator.isIMEI("123412341234564");
  ```

[↑ 返回快速查询表](#快速查询表)

## 更多的的验证注解
如果你需要更多的验证。可以联系我们进行扩展和支持。联系方式：

Sharif

[vipxieliang@126.com](mailto:vipxieliang@126.com)

## 贡献
您可以通过多种方式参与这个项目，不限于以下方式：

* 反馈使用中遇到的问题
* 分享成功的喜悦
* 更新和完善文档
* 解决和讨论问题