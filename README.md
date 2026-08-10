# Language

[中文](README.cn.md)

[English](README.md)

<div align="center">

# ValidX

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vipxieliang/validx?color=blue)](https://central.sonatype.com/artifact/io.github.vipxieliang/validx)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/vipxieliang/ValidX/pulls)

**Simple, Elegant, Reliable - 100+ ready-to-use validators for Chinese business scenarios**

</div>

---

## 📑 Table of Contents

- [Version Update Notice](#-version-update-notice)
- [Introduction](#introduction)
- [Why We Created ValidX?](#-why-we-created-validx)
- [Why Choose ValidX?](#-why-choose-validx)
- [5-Minute Quick Start](#-5-minute-quick-start)
- [Multilingual Support](#multilingual-support)
- [Important: Null/Empty String Handling](#important-nullempty-string-handling)
- [Thread Safety](#thread-safety)
- [Supported Validation Annotations](#supported-validation-annotations)
  - [Quick Reference Table](#quick-reference-table)
  - [Basic Validation](#basic-validation)
  - [Identity Validation](#identity-verification-related)
  - [Financial Validation](#financial-validation-related)
  - [Education/Professional Qualification](#educationprofessional-qualificationcertification-related-validation)
  - [Network Validation](#network-related)
  - [China-Specific Validation](#china-specific-validation)
  - [Automotive Validation](#automotive-related-validation)
  - [Book-Related Validation](#book-related-validation)
  - [Mobile Device Validation](#mobile-phone-related-validation)
- [More Validation Annotations](#more-validation-annotations)
- [Contribution](#contribution)

---

## 📢 Version Update Notice

**Current Version: v1.2.0** (Released August 10, 2026)

### v1.2.0 Key Updates

- ⚠️ **Breaking Change**: `isStartsWith()` and `isEndsWith()` chain API parameter changed from `String[]` to `String`
- ✨ **New Features**: `@StartsWithAny` and `@EndsWithAny` annotations for multiple value validation
- 🔧 **Enhancement**: `@FileSize` now supports MIME type validation (`allowedTypes` parameter)
- 🎯 **Code Optimization**: Simplified initialization code across 20+ validator classes

> **⚠️ Upgrade Notice**: When upgrading from v1.1.0, note the chain API changes. Use `isStartsWith(value, "prefix")` for single value, `isStartsWithAny(value, new String[]{"p1", "p2"})` for multiple values.

**Details:** [v1.2.0 Changelog](docs/version/v1.2.0/CHANGELOG.md)

---

### Version History

| Version | Release Date | Key Features | Breaking Changes | Documentation |
|---------|-------------|--------------|------------------|---------------|
| **v1.2.0** | 2026-08-10 | `@StartsWithAny`, `@EndsWithAny`, `@FileSize` enhancement | Chain API parameter change | [Changelog](docs/version/v1.2.0/CHANGELOG.md) |
| **v1.1.0** | 2026-08-10 | 6 new annotations: `@Date`, `@DateTime`, `@ChineseName`, `@NotContains`, etc. | `@PastDate`/`@FutureDate` no longer support time formats → Use `@PastDateTime`/`@FutureDateTime` | [Changelog](docs/version/v1.1.0/CHANGELOG.md) \| [Migration Guide](docs/version/v1.1.0/MIGRATION_v1.1.0.md) |
| **v1.0.1** | 2026-06-15 | Bug fixes and performance improvements | None | [Changelog](docs/version/v1.0.1/CHANGELOG.md) |
| **v1.0.0** | 2026-05-01 | Initial release with 100+ validation annotations | - | [Documentation](docs/version/v1.0.0/README.md) |

---

## Introduction

ValidX is an open-source Java validation library focused on Chinese business scenarios, making validation simple, elegant, and reliable. Built on JSR-380 standards with 100+ specialized annotations for Chinese identity cards, phone numbers, bank cards, and more.

## 💡 Why We Created ValidX?

When developing applications for Chinese users, we frequently encountered these challenges:

### Pain Point 1: Java Has Too Few Built-in Validation Rules, Far Less Than Other Language Frameworks

If you've used web frameworks in other languages, such as PHP's ThinkPHP or JavaScript's Validator.js, you'll notice they come with incredibly rich built-in validation rules: `mobile`, `idcard`, `zip`, `alphaNum`, etc.—ready to use out of the box, simple and convenient.

But in the Java world, standard Bean Validation only provides a handful of generic annotations like `@Email` and `@Pattern`. For common Chinese business scenarios—identity cards, phone numbers, bank cards, unified social credit codes—there's absolutely no support.

This forces every Java project to reinvent the wheel:
- Writing complex regular expressions yourself
- Implementing Luhn algorithm for bank card validation
- Handling identity card check digit calculations
- Copy-pasting validation code found online

**Why can't Java validation be as ready-to-use as other frameworks?** This is why ValidX was born.

### Pain Point 2: Scattered Validation Logic Difficult to Maintain
As projects grow, validation logic becomes scattered across:
- Manual validation in Controller layer
- Business validation in Service layer
- Static methods in utility classes
- Duplicate validation implementations in different modules

This leads to code duplication, maintenance difficulties, and error-prone implementations.

### Pain Point 3: Lack of Chinese Error Messages and Multi-language Support
When using standard annotations, error messages are typically in English, or require manual configuration of resource files. For Chinese users, we need:
- Friendly Chinese error messages
- Multi-language switching support
- Customizable error message templates

### ValidX's Solution

Based on these pain points, we created ValidX with the goal: **Make Java validation simple, elegant, and reliable**

1. **100+ Chinese scenario validators** - From ID cards to express tracking numbers, from QQ numbers to license plates, covering all aspects of Chinese business
2. **Two usage styles** - Annotation-based (for DTO object validation) and fluent chain API (for dynamic validation), flexible for different scenarios
3. **Zero-config multi-language** - Supports 8 languages, automatically adapts to user language environment
4. **Enterprise-grade reliability** - 1300+ unit tests ensure quality, production-validated
5. **Simple to use** - Just one dependency, works out of the box, no complex configuration needed

We hope ValidX can become the standard tool for every Java application serving Chinese users, allowing developers to focus on business logic rather than repeatedly writing validation code.

**Import once, benefit forever. Stop reinventing the wheel.**

## ✨ Why Choose ValidX?

### 🇨🇳 **Built for China**
- **100+ Chinese-specific validators**: ID cards, phone numbers, bank cards, social credit codes, license plates, and more
- **8 languages supported**: Simplified Chinese, English, Japanese, Korean, French, German, Spanish, Russian
- **Local business validation**: Express tracking, QQ, WeChat, Alipay order numbers

### 🚀 **Developer Friendly**
- **Two usage styles**: Annotation-based (for DTOs) or fluent chain API (for dynamic validation)
- **Zero configuration**: Works out of the box with Spring Boot and standard Bean Validation
- **Smart null handling**: Configurable global/local null and empty string policies

### 🎯 **Enterprise Ready**
- **Type-safe validation**: Compile-time checks with annotation-based approach
- **Rich error messages**: Automatic i18n support with custom field labels
- **Production tested**: Comprehensive test coverage with 1300+ unit tests

### 📦 **Lightweight & Fast**
- **Single dependency**: No external dependencies beyond Bean Validation API
- **Small footprint**: ~300KB JAR size
- **High performance**: Optimized validators with minimal overhead

---

## 🚀 5-Minute Quick Start

### Step 1: Add Dependency

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Step 2: Choose Your Style

#### Option A: Annotation Style (Recommended for DTOs)

Perfect for controller request validation with Spring Boot:

```java
public class UserRegistrationDTO {
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Phone is required")
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
        // Spring automatically validates, returns 400 on failure
        return userService.register(dto);
    }
}
```

#### Option B: Fluent Chain Style (Recommended for Business Logic)

Perfect for dynamic validation in service layers:

```java
@Service
public class UserService {
    public void validateUserData(Map<String, Object> userData) {
        ValidX validator = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)  // Reject null/empty globally
            .field("Email").isEmail(userData.get("email"))
            .field("Phone").isChinesePhone(userData.get("phone"))
            .field("ID Card").isChineseIdCard(userData.get("idCard"))
            .field("Optional QQ").allowNull().isQQ(userData.get("qq"));  // Override for optional field

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

### Step 3: Run Your Application

That's it! ValidX works seamlessly with your existing Spring Boot setup. Error messages automatically adapt to the user's language via `Accept-Language` header.

## Multilingual Support

ValidX supports multilingual error messages, which can be used in the following ways:

```java
// Use system default language
ValidX chain1 = ValidX.init()
        .isEmail("invalid-email");

// Use Chinese
ValidX chain2 = ValidX.init()
        .withLocale(Locale.SIMPLIFIED_CHINESE)
        .isEmail("invalid-email");

// Use English
ValidX chain3 = ValidX.init()
        .withLocale(Locale.ENGLISH)
        .isEmail("invalid-email");
```

Annotation-based usage also supports multilingualism, and error messages will automatically switch according to the system language environment. To use a specific locale, you can configure Hibernate Validator:

```java
// Configure language locale for English messages
ValidatorFactory englishFactory = Validation.byDefaultProvider()
    .configure()
    .messageInterpolator(new ResourceBundleMessageInterpolator())
    .buildValidatorFactory();
Validator englishValidator = englishFactory.getValidator();

// Configure language locale for Chinese messages
ValidatorFactory chineseFactory = Validation.byDefaultProvider()
    .configure()
    .messageInterpolator(new ResourceBundleMessageInterpolator())
    .buildValidatorFactory();
Validator chineseValidator = chineseFactory.getValidator();
```

```java
public class UserDTO {
    // Error messages will automatically switch between Chinese and English based on the current language environment
    @Email 
    private String email;
    
    @ChineseIdCard
    private String idCard;
}
```

### Automatic Language Environment Switching

ValidX also supports automatic language environment switching without explicitly specifying the language environment:

```
// Set language environment globally (affects all validation operations in the current thread)
MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);

// Validation operations will automatically use the set language environment
ValidX chain = ValidX.init()
        .isEmail("invalid-email");

// Clear global language environment settings
MessageManager.clearCurrentLocale();
```

Currently supported languages:
- Simplified Chinese (default)
- English
- Japanese
- Korean
- French
- German
- Spanish
- Russian

## Important Note: Handling Null/Empty Strings

**All validation annotations and chain validation methods return pass (`true`) for `null` and empty strings (`""`)**. This follows the design principle of the Bean Validation (JSR 380) specification.

This is to achieve **separation of concerns**:
- `@NotNull` / `@NotEmpty` / `@NotBlank`: Check "whether the field exists"
- Format validation annotations/methods (e.g., `@Email`, `isEmail()`): Check "if the field has a value, whether the format is correct"

### 1. Handling Null/Empty Strings in Annotation-based Validation

#### How to use?

Combine annotations according to business requirements:

```java
public class UserDTO {
    // Required field: cannot be null and format must be correct
    @NotNull(message = "Email cannot be null")
    @Email
    private String email;

    // Required field: cannot be null, empty string, and format must be correct
    @NotBlank(message = "Phone cannot be blank")
    @ChinesePhone
    private String phone;

    // Optional field: can be null or empty string, but if has value, format must be correct
    @QQ
    private String qq;

    // Optional field: can be null, but cannot be empty string, if has value, format must be correct
    @NotEmpty(message = "If WeChat ID is provided, it cannot be empty")
    @WeChat
    private String wechat;
}
```

#### Common Combination Examples

| Requirement | Annotation Combination | Description |
|-------------|------------------------|-------------|
| **Required and Format Correct** | `@NotBlank` + `@Email` | Most common: cannot be null, empty string, or whitespace |
| **Required and Format Correct** | `@NotNull` + `@ChineseIdCard` | Cannot be null, but allows empty string (rarely used) |
| **Optional but Format Correct** | `@QQ` | Can be null/empty, but if has value must be correct |
| **Optional but Non-empty** | `@NotEmpty` + `@WeChat` | Can be null, but cannot be empty string |

#### Bean Validation Standard Annotations

- `@NotNull`: Field cannot be `null` (but can be empty string `""`)
- `@NotEmpty`: Field cannot be `null` and cannot be empty (string cannot be `""`, collection cannot be empty)
- `@NotBlank`: String cannot be `null`, `""`, or `"   "` (whitespace)

#### Use Cases

Annotation-based validation is suitable for **Controller layer interface parameter validation**:

```java
@RestController
public class UserController {
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserDTO dto) {
        // Spring automatically validates, returns 400 on failure
        return userService.register(dto);
    }
}
```

### 2. Handling Null/Empty Strings in Chain Validation

Chain validation (`ValidX.init()`) has the same default behavior as annotation-based: **null and empty strings pass validation**.

#### Why this design?

Chain validation is mainly used for **dynamic validation scenarios in the business logic layer**, especially when processing Map/JSON data, where fields may not exist (returning null) is a normal situation.

For example:
- Parsing JSON data returned from external APIs
- Processing dynamic form data from frontend
- Validating Map results from database queries

In these scenarios, a field being null typically means "the field does not exist" or "no need to validate this field", rather than an error.

#### Use Cases

Chain-based validation is suitable for **business logic layer dynamic validation**:

```java
@Service
public class UserService {
    public void process(Map<String, Object> data) {
        ValidX validator = ValidX.init();

        // Fields in Map may not exist (null), this is normal
        // Chain validation automatically skips null values
        validator.isEmail(data.get("email"))
                 .isChinesePhone(data.get("phone"));

        if (!validator.passed()) {
            throw new BusinessException(validator.getErrors());
        }
    }
}
```

#### Chain Validation Configuration API

ValidX now supports flexible configuration for handling null/empty values through both global configuration and local state control.

##### Global Configuration

You can set global validation requirements using `ValidXConfig`:

```java
// Create validator with global NOT_NULL requirement
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL);

// All validation methods will now reject null values
validator.isEmail(email)  // Fails if email is null
         .isPhone(phone); // Fails if phone is null

// Create validator with global NOT_EMPTY requirement
ValidX validator2 = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_EMPTY);

// All validation methods will now reject null and empty strings
validator2.isEmail(email)  // Fails if email is null or ""
          .isPhone(phone); // Fails if phone is null or ""
```

**Available Global Configurations:**
- `ValidXConfig.DEFAULT` - Allows null and empty strings (default behavior)
- `ValidXConfig.GLOBAL_NOT_NULL` - All fields cannot be null
- `ValidXConfig.GLOBAL_NOT_EMPTY` - All fields cannot be null or empty strings

**Best Practice:** Call `config()` only once at the beginning of the validation chain for clarity and maintainability.

```java
// ✅ Recommended: Set config once at the start
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email)
    .isPhone(phone)
    .allowNull().isQQ(qq);  // Use local method for exceptions

// ⚠️ Not recommended: Multiple config() calls in the middle
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email)
    .config(ValidXConfig.DEFAULT)  // Confusing: hard to track config changes
    .isPhone(phone);

// ✅ If you need different configs, create separate validators
ValidX strictValidator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email1)
    .isPhone(phone1);

ValidX lenientValidator = ValidX.init()
    .config(ValidXConfig.DEFAULT)
    .isEmail(email2)
    .isPhone(phone2);
```

##### Local State Control

You can override global configuration for specific fields using local state methods:

```java
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL);  // Global: reject null

// Override for specific fields
validator.field("Optional Email").allowNull().isEmail(optionalEmail)  // Allow null for this field
         .field("Required Phone").notEmpty().isPhone(phone)           // Require non-empty
         .field("User ID").isChineseIdCard(idCard);                   // Use global NOT_NULL
```

**Available Local State Methods:**
- `.notNull()` - Field cannot be null (but can be empty string)
- `.notEmpty()` - Field cannot be null or empty string
- `.allowNull()` - Allow null values (skip validation if null)
- `.allowEmpty()` - Allow empty strings (but not null)
- `.field("label")` - Set custom field label for error messages

##### Priority Rules

Local state always takes precedence over global configuration:

```java
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_EMPTY);  // Global: reject null and empty

validator.allowNull().isEmail(email);  // Local allowNull() overrides global
```

**Priority:** Local State > Global Config > Default Behavior

##### Practical Examples

**Example 1: API Request Validation**

```java
public void validateUserRegistration(Map<String, Object> request) {
    ValidX validator = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_EMPTY);  // Most fields are required

    validator.field("Email").isEmail(request.get("email"))
             .field("Phone").isChinesePhone(request.get("phone"))
             .field("Optional QQ").allowNull().isQQ(request.get("qq"))  // Optional field
             .field("ID Card").isChineseIdCard(request.get("idCard"));

    if (!validator.passed()) {
        throw new ValidationException(validator.getErrors());
    }
}
```

**Example 2: Form Update (Partial Updates)**

```java
public void updateUserProfile(String userId, Map<String, Object> updates) {
    // Only validate fields that are being updated
    ValidX validator = ValidX.init();  // Default: allow null/empty

    // Only validate fields present in the update map
    if (updates.containsKey("email")) {
        validator.field("Email").notEmpty().isEmail(updates.get("email"));
    }

    if (updates.containsKey("phone")) {
        validator.field("Phone").notEmpty().isChinesePhone(updates.get("phone"));
    }

    if (!validator.passed()) {
        throw new ValidationException(validator.getErrors());
    }
}
```

**Example 3: Mixed Requirements**

```java
public void validateComplexForm(FormData data) {
    ValidX validator = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_NULL);  // Most fields required

    validator.field("Email").notEmpty().isEmail(data.getEmail())      // Required and non-empty
             .field("Phone").isChinesePhone(data.getPhone())          // Required (use global)
             .field("QQ").allowNull().isQQ(data.getQq())              // Optional
             .field("WeChat").allowEmpty().isWeChat(data.getWeChat()) // Can be empty but not null
             .field("Website").allowNull().isUrl(data.getWebsite());  // Optional

    if (!validator.passed()) {
        // Error messages include custom field labels
        throw new ValidationException(validator.getErrors());
    }
}
```

##### State Reset Behavior

**Important:** Local state (notNull/notEmpty/allowNull/allowEmpty) is automatically reset after each validation method call. This ensures each field's validation is independent.

```java
ValidX validator = ValidX.init();

validator.notNull().isEmail(email1)   // notNull applies to email1
         .isEmail(email2)              // email2 uses default behavior (state reset)
         .notEmpty().isPhone(phone);   // notEmpty applies to phone only
```

##### Error Messages with Field Labels

When using `.field("label")`, error messages will include the custom label:

```java
ValidX validator = ValidX.init();

validator.field("User Email").notEmpty().isEmail("")
         .field("Contact Phone").notNull().isChinesePhone(null);

if (!validator.passed()) {
    List<String> errors = validator.getErrors();
    // Errors: ["User Email: Value cannot be an empty string",
    //          "Contact Phone: Value cannot be null"]
}
```

> **Note:** This configuration API provides fine-grained control over null/empty handling in chain validation, making it suitable for complex business scenarios while maintaining backward compatibility.

## Thread Safety

**ValidX instances are not thread-safe.** Each validation should create a new instance:

```java
// ❌ Wrong: Sharing instance across threads
private static final ValidX VALIDATOR = ValidX.init();

public void validate(User user) {
    VALIDATOR.isEmail(user.getEmail());  // Not thread-safe!
}

// ✅ Correct: Create new instance per validation
public void validate(User user) {
    ValidX validator = ValidX.init()
        .isEmail(user.getEmail())
        .isPhone(user.getPhone());

    if (!validator.isValid()) {
        throw new ValidationException(validator.getErrorMessage());
    }
}
```

**Why?** ValidX uses internal mutable state (local requirement flags, field labels, error lists) that is modified during the validation chain. Sharing instances across threads can lead to race conditions and incorrect validation results.

**Thread-safe components:**
- `ValidXConfig` objects are immutable and can be safely shared
- Individual validator classes (e.g., `ChineseIdCardValidator`) are stateless and can be reused

This design follows the same pattern as other fluent APIs like `StringBuilder`, Java 8 `Stream`, and Lombok `Builder` - they are meant to be used in a "create-use-discard" pattern.

## Supported Validation Annotations

ValidX provides rich validation annotations covering various scenarios. The following are all currently supported validation annotations and their function descriptions:

### Quick Reference Table

Click on the annotation name to jump to its detailed documentation.

| Category | Annotation | Description | Added Version | Modified Version |
|----------|------------|-------------|---------------|------------------|
| **Basic Validation** | [@Alpha](#alpha) | Pure English letter validation | 1.0.0   | - |
| **Basic Validation** | [@AlphaDash](#alphadash) | Alphanumeric with underscore and hyphen | 1.0.0   | - |
| **Basic Validation** | [@AlphaNumber](#alphanumber) | Alphanumeric combination | 1.0.0   | - |
| **Basic Validation** | [@Chinese](#chinese) | Pure Chinese character validation | 1.0.0   | - |
| **Basic Validation** | [@ChineseAlpha](#chinesealpha) | Chinese characters and letters | 1.0.0   | - |
| **Basic Validation** | [@ChineseAlphaNum](#chinesealphanum) | Chinese characters, letters and numbers | 1.0.0   | - |
| **Basic Validation** | [@ChineseAlphaDash](#chinesealphadash) | Chinese, letters, numbers, underscore, hyphen | 1.0.0   | - |
| **Basic Validation** | [@Lower](#lower) | Lowercase character validation | 1.0.0   | - |
| **Basic Validation** | [@Upper](#upper) | Uppercase character validation | 1.0.0   | - |
| **Basic Validation** | [@Xdigit](#xdigit) | Hexadecimal string validation | 1.0.0   | - |
| **Basic Validation** | [@Longitude](#longitude) | Longitude validation (-180 to 180) | 1.0.0   | - |
| **Basic Validation** | [@Latitude](#latitude) | Latitude validation (-90 to 90) | 1.0.0   | - |
| **Basic Validation** | [@GeoPoint](#geopoint) | Geographic coordinate pair validation | 1.0.0   | - |
| **Basic Validation** | [@Date](#date) | Date format validation (custom formats) | 1.1.0   | - |
| **Basic Validation** | [@DateTime](#datetime) | Date-time format validation (with time) | 1.1.0   | - |
| **Basic Validation** | [@FutureDate](#futuredate) | Future date validation | 1.0.0   | 1.1.0 |
| **Basic Validation** | [@PastDate](#pastdate) | Past date validation | 1.0.0   | 1.1.0 |
| **Basic Validation** | [@PastDateTime](#pastdatetime) | Past date-time validation | 1.1.0   | - |
| **Basic Validation** | [@FutureDateTime](#futuredatetime) | Future date-time validation | 1.1.0   | - |
| **Basic Validation** | [@HourMinute](#hourminute) | Hour:minute format (HH:mm) | 1.0.0   | - |
| **Basic Validation** | [@HourMinuteSecond](#hourminutesecond) | Hour:minute:second format (HH:mm:ss) | 1.0.0   | - |
| **Basic Validation** | [@Timestamp](#timestamp) | Unix timestamp validation | 1.0.0   | - |
| **Basic Validation** | [@CronExpression](#cronexpression) | Cron expression validation | 1.0.0   | - |
| **Basic Validation** | [@Duration](#duration) | Duration format validation | 1.0.0   | - |
| **Basic Validation** | [@ExpressNumber](#expressnumber) | Express tracking number validation | 1.0.0   | - |
| **Basic Validation** | [@StartsWith](#startswith) | String prefix validation | 1.0.0   | - |
| **Basic Validation** | [@Contains](#contains) | String contains substring validation | 1.0.1   | - |
| **Basic Validation** | [@NotContains](#notcontains) | String does not contain substring validation | 1.1.0   | - |
| **Basic Validation** | [@EndsWith](#endswith) | String suffix validation | 1.0.0   | - |
| **Basic Validation** | [@In](#in) | Value in specified list | 1.0.0   | - |
| **Basic Validation** | [@NotIn](#notin) | Value not in specified list | 1.0.0   | - |
| **Basic Validation** | [@Enum](#enum) | Enumeration value validation | 1.0.0   | - |
| **Basic Validation** | [@Color](#color) | Color format (HEX/RGB/RGBA) | 1.0.0   | - |
| **Basic Validation** | [@Password](#password) | Password strength validation | 1.0.0   | - |
| **Basic Validation** | [@UUID](#uuid) | UUID format validation | 1.0.0   | - |
| **Basic Validation** | [@Base64](#base64) | Base64 encoding validation | 1.0.0   | - |
| **Basic Validation** | [@JSON](#json) | JSON format validation | 1.0.0   | - |
| **Basic Validation** | [@JWT](#jwt) | JWT token format validation | 1.0.0   | - |
| **Basic Validation** | [@SemVer](#semver) | Semantic versioning validation | 1.0.0   | - |
| **Basic Validation** | [@FileExtension](#fileextension) | File extension validation | 1.0.0   | - |
| **Basic Validation** | [@FileSize](#filesize) | File size range validation | 1.0.0   | - |
| **Basic Validation** | [@Age](#age) | Age validation from birth date or ID | 1.0.0   | - |
| **Basic Validation** | [@Port](#port) | Port number validation (0-65535) | 1.0.0   | - |
| **Identity Validation** | [@ChineseName](#chinesename) | Chinese name validation | 1.1.0   | - |
| **Identity Validation** | [@ChineseIdCard](#chineseidcard) | Chinese ID card validation | 1.0.0   | - |
| **Identity Validation** | [@ChinesePassport](#chinesepassport) | Chinese passport validation | 1.0.0   | - |
| **Identity Validation** | [@ChineseMilitaryOfficer](#chinesemilitaryofficer) | Military officer certificate | 1.0.0   | - |
| **Identity Validation** | [@ChineseSoldier](#chinesesoldier) | Soldier certificate validation | 1.0.0   | - |
| **Identity Validation** | [@ForeignerPermanentResidenceIdentity](#foreignerpermanentresidenceidentity) | Foreigner permanent residence ID | 1.0.0   | - |
| **Identity Validation** | [@HKMacauResidence](#hkmacauresidence) | HK/Macau residence permit | 1.0.0   | - |
| **Identity Validation** | [@HKMacauPass](#hkmacaupass) | HK/Macau travel permit | 1.0.0   | - |
| **Identity Validation** | [@TaiwanResidence](#taiwanresidence) | Taiwan residence permit | 1.0.0   | - |
| **Identity Validation** | [@TaiwanPass](#taiwanpass) | Taiwan travel permit | 1.0.0   | - |
| **Identity Validation** | [@ForeignerWorkPermit](#foreignerworkpermit) | Foreigner work permit | 1.0.0   | - |
| **Identity Validation** | [@UnifiedSocialCreditCode](#unifiedsocialcreditcode) | Unified Social Credit Code | 1.0.0   | - |
| **Identity Validation** | [@ChinesePhone](#chinesephone) | Chinese mobile phone | 1.0.0   | - |
| **Identity Validation** | [@ChineseLandline](#chineselandline) | Chinese landline | 1.0.0   | - |
| **Identity Validation** | [@ChinesePhoneOrLandline](#chinesephoneorlandline) | Chinese phone or landline | 1.0.0   | - |
| **Identity Validation** | [@PhoneNumber](#phonenumber) | International phone number | 1.0.0   | - |
| **Identity Validation** | [@Email](#email) | Email address validation | 1.0.0   | - |
| **Financial Validation** | [@BankCard](#bankcard) | Bank card number (Luhn) | 1.0.0   | - |
| **Financial Validation** | [@CVV](#cvv) | CVV/CVC security code | 1.0.0   | - |
| **Financial Validation** | [@IBAN](#iban) | IBAN account number | 1.0.0   | - |
| **Financial Validation** | [@SWIFT](#swift) | SWIFT/BIC code | 1.0.0   | - |
| **Financial Validation** | [@StockCode](#stockcode) | Stock code validation | 1.0.0   | - |
| **Financial Validation** | [@TradeOrderNumber](#tradeordernumber) | Trade order number | 1.0.0   | - |
| **Financial Validation** | [@FinancialProductCode](#financialproductcode) | Financial product code | 1.0.0   | - |
| **Education/Professional Qualification** | [@DegreeCertificate](#degreecertificate) | Degree certificate number | 1.0.0   | - |
| **Education/Professional Qualification** | [@Doctor](#doctor) | Doctor qualification | 1.0.0   | - |
| **Education/Professional Qualification** | [@Teacher](#teacher) | Teacher qualification | 1.0.0   | - |
| **Education/Professional Qualification** | [@Lawyer](#lawyer) | Legal professional qualification | 1.0.0   | - |
| **Education/Professional Qualification** | [@PMP](#pmp) | PMP certificate | 1.0.0   | - |
| **Education/Professional Qualification** | [@Constructor](#constructor) | Constructor certificate | 1.0.0   | - |
| **Education/Professional Qualification** | [@Accountant](#accountant) | Accountant certificate | 1.0.0   | - |
| **Network Validation** | [@Domain](#domain) | Domain name validation | 1.0.0   | - |
| **Network Validation** | [@Ip](#ip) | IP address (IPv4/IPv6) | 1.0.0   | - |
| **Network Validation** | [@Mac](#mac) | MAC address validation | 1.0.0   | - |
| **Network Validation** | [@Url](#url) | URL address validation | 1.0.0   | - |
| **Network Validation** | [@SubnetMask](#subnetmask) | Subnet mask validation | 1.0.0   | - |
| **China-Specific Validation** | [@ChineseLicensePlate](#chineselicenseplate) | Chinese license plate | 1.0.0   | - |
| **China-Specific Validation** | [@ChinesePatent](#chinesepatent) | Chinese patent number | 1.0.0   | - |
| **China-Specific Validation** | [@ChineseTrademark](#chinesetrademark) | Chinese trademark registration | 1.0.0   | - |
| **China-Specific Validation** | [@SoftwareCopyright](#softwarecopyright) | Software copyright registration | 1.0.0   | - |
| **China-Specific Validation** | [@WorkCopyright](#workcopyright) | Work copyright registration | 1.0.0   | - |
| **China-Specific Validation** | [@ChineseZipCode](#chinesezipcode) | Chinese postal code | 1.0.0   | - |
| **China-Specific Validation** | [@DrugApproval](#drugapproval) | Drug approval number | 1.0.0   | - |
| **China-Specific Validation** | [@DrugCode](#drugcode) | Drug code validation | 1.0.0   | - |
| **China-Specific Validation** | [@MedicalDeviceRegistration](#medicaldeviceregistration) | Medical device registration | 1.0.0   | - |
| **China-Specific Validation** | [@QQ](#qq) | QQ number validation | 1.0.0   | - |
| **China-Specific Validation** | [@WeChat](#wechat) | WeChat ID validation | 1.0.0   | - |
| **Automotive Validation** | [@VIN](#vin) | Vehicle Identification Number | 1.0.0   | - |
| **Automotive Validation** | [@VehicleEngine](#vehicleengine) | Vehicle engine number | 1.0.0   | - |
| **Book-Related Validation** | [@ISBN](#isbn) | ISBN book number | 1.0.0   | - |
| **Book-Related Validation** | [@ISSN](#issn) | ISSN serial number | 1.0.0   | - |
| **Book-Related Validation** | [@DOI](#doi) | DOI identifier | 1.0.0   | - |
| **Book-Related Validation** | [@CLC](#clc) | Chinese Library Classification | 1.0.0   | - |
| **Book-Related Validation** | [@DDC](#ddc) | Dewey Decimal Classification | 1.0.0   | - |
| **Book-Related Validation** | [@ORCID](#orcid) | ORCID researcher ID | 1.0.0   | - |
| **Book-Related Validation** | [@IPC](#ipc) | International Patent Classification | 1.0.0   | - |
| **Mobile Device Validation** | [@IMEI](#imei) | IMEI device number | 1.0.0   | - |

---

### Basic Validation

#### @Alpha
* Validation Rule: Pure English letter validation, only allowing uppercase and lowercase English letters (a-z, A-Z).
* Example Format: `abcDEF`
* Usage Example:
  ```java
  // Annotation-based usage
  @Alpha
  private String code;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isAlpha("abcDEF");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @AlphaDash
* Validation Rule: Alphanumeric underscore hyphen validation, allowing English letters, numbers, underscores, and hyphens.
* Example Format: `abc-123_def`
* Usage Example:
  ```java
  // Annotation-based usage
  @AlphaDash
  private String code;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isAlphaDash("abc-123_def");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @AlphaNumber
* Validation Rule: Alphanumeric combination validation, only allowing English letters and numbers.
* Example Format: `abc123`
* Usage Example:
  ```java
  // Annotation-based usage
  @AlphaNumber
  private String code;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isAlphaNumber("abc123");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Chinese
* Validation Rule: Pure Chinese character validation, only allowing Chinese characters (Unicode Chinese characters).
* Example Format: `汉字`
* Usage Example:
  ```java
  // Annotation-based usage
  @Chinese
  private String name;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChinese("汉字");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseAlpha
* Validation Rule: Chinese character letter validation, allowing Chinese characters and English letters.
* Example Format: `汉字abc`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseAlpha
  private String name;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseAlpha("汉字abc");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseAlphaNum
* Validation Rule: Chinese character letter number validation, allowing Chinese characters, English letters, and numbers.
* Example Format: `汉字abc123`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseAlphaNum
  private String code;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseAlphaNum("汉字abc123");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseAlphaDash
* Validation Rule: Chinese character letter number underscore hyphen validation, allowing Chinese characters, English letters, numbers, underscores, and hyphens.
* Example Format: `汉字abc-123_def`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseAlphaDash
  private String code;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseAlphaDash("汉字abc-123_def");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Longitude
* Validation Rule: Longitude validation, validating whether the longitude value is between -180 and 180.
* Example Format: `0`, `116.4074`, `-116.4074`, `180`, `-180`
* Usage Example:
  ```java
  // Annotation-based usage
  @Longitude
  private String longitude;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isLongitude("116.4074");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Latitude
* Validation Rule: Latitude validation, validating whether the latitude value is between -90 and 90.
* Example Format: `0`, `39.9042`, `-39.9042`, `90`, `-90`
* Usage Example:
  ```java
  // Annotation-based usage
  @Latitude
  private String latitude;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isLatitude("39.9042");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @GeoPoint
* Validation Rule: Geographic coordinate pair validation (longitude and latitude), validating whether the coordinate format is correct and values are within valid ranges.
* Supported Formats:
  - Comma-separated: `"116.4074,39.9042"` (longitude,latitude)
  - Space-separated: `"116.4074 39.9042"`
  - Comma + space: `"116.4074, 39.9042"`
* Validation Rules:
  - Longitude range: -180 to 180
  - Latitude range: -90 to 90
  - Must contain two valid numeric values
* Configuration Options:
  - `latitudeFirst`: Coordinate order, `false` (default) for longitude first, `true` for latitude first
  - `separator`: Separator type - `ANY` (default), `COMMA`, or `SPACE`
* Usage Example:
  ```java
  // Annotation-based usage - default (longitude,latitude)
  @GeoPoint
  private String location;  // "116.4074,39.9042"

  // Latitude first
  @GeoPoint(latitudeFirst = true)
  private String position;  // "39.9042,116.4074"

  // Comma separator only
  @GeoPoint(separator = GeoPoint.SeparatorType.COMMA)
  private String gps;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isGeoPoint("116.4074,39.9042");  // Default: longitude first
  validator.isGeoPoint("39.9042,116.4074", true);  // Latitude first
  validator.isGeoPoint("116.4074,39.9042", false, GeoPoint.SeparatorType.COMMA);  // Specify separator
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Date
* Validation Rule: Date format validation, validates whether a string conforms to the specified date format (pure date, no time component) using strict validation mode.
* Core Features:
  - **Strict Validation**: Rejects invalid dates (e.g., 2024-02-30, 2024-13-01)
  - **Leap Year Recognition**: Correctly handles leap years (2024-02-29 ✓, 1900-02-29 ✗)
  - **Flexible Formats**: Supports all Java DateTimeFormatter date formats
  - **Multi-language Support**: Error messages in 9 languages
  - **Pattern Restriction**: Pattern must NOT contain time symbols (use @DateTime for date-time formats)
* Format Symbol Reference:

  **Date Symbols:**

  | Symbol | Meaning | Example | Description |
  |--------|---------|---------|-------------|
  | `yyyy` | Year (4 digits) | `2024` | Recommended, auto-converted for strict mode |
  | `yy` | Year (2 digits) | `24` | Represents 2024 |
  | `MM` | Month (zero-padded) | `01`, `12` | Must be 2 digits |
  | `M` | Month (no padding) | `1`, `12` | 1-12 |
  | `dd` | Day (zero-padded) | `05`, `25` | Must be 2 digits |
  | `d` | Day (no padding) | `5`, `25` | 1-31 |
  | `DDD` | Day of year | `365` | 1-366 |

* Supported Format Examples:
  - Standard date: `yyyy-MM-dd` → `2024-01-15`
  - Compact format: `yyyyMMdd` → `20240115`
  - US format: `MM/dd/yyyy` → `12/25/2024`
  - European format: `dd/MM/yyyy` → `25/12/2024`
  - Chinese format: `yyyy年MM月dd日` → `2024年12月25日`
* Configuration Options:
  - `pattern`: Date format pattern, defaults to `"yyyy-MM-dd"` (must not contain time symbols)
* Validation Examples:
  - ✅ Valid: `2024-02-29` (leap year)
  - ✅ Valid: `2024-01-31` (January has 31 days)
  - ✅ Valid: `2024-04-30` (April has 30 days)
  - ❌ Invalid: `2024-02-30` (February doesn't have 30 days)
  - ❌ Invalid: `2023-02-29` (not a leap year)
  - ❌ Invalid: `2024-13-01` (month range is 1-12)
  - ❌ Invalid: `2024-04-31` (April doesn't have 31 days)
* Usage Example:
  ```java
  // Annotation-based usage - default format (yyyy-MM-dd)
  @Date
  private String birthDate;

  // Custom format
  @Date(pattern = "MM/dd/yyyy")
  private String usDate;

  @Date(pattern = "yyyy年MM月dd日")
  private String chineseDate;

  // Chain call usage - default format
  ValidX validator = ValidX.init();
  validator.isDate("2024-01-15");

  // Custom format
  validator.isDate("12/25/2024", "MM/dd/yyyy");
  validator.isDate("2024年12月25日", "yyyy年MM月dd日");
  ```
* Important Notes:
  - Pattern must NOT contain time symbols (H, h, K, k, m, s, S, a, A, n, N)
  - For date-time formats, use @DateTime annotation instead
  - **Strict format matching**: Input must exactly match the pattern length and format
    - ✅ Valid: `@Date(pattern = "yyyy-MM-dd")` with input `"2024-01-15"`
    - ❌ Invalid: `@Date(pattern = "yyyy-MM-dd")` with input `"2024-01-15 12:00:00"` (contains time)
    - ❌ Invalid: `@Date(pattern = "yyyy-MM-dd")` with input `"2024-1-5"` (missing zero-padding)
  - When using formats like `yyyy-MM-dd`, dates must be zero-padded (e.g., `2024-01-05` not `2024-1-5`)
  - Separators must exactly match the format (e.g., `2024/01/15` will fail with format `yyyy-MM-dd`)
  - null and empty strings pass validation by default (use with `@NotNull` or `@NotEmpty`)

[↑ Back to Quick Reference](#quick-reference-table)

#### @DateTime
* Validation Rule: Date-time format validation, validates whether a string conforms to the specified date-time format (must include time component) using strict validation mode.
* Core Features:
  - **Strict Validation**: Rejects invalid date-times (e.g., 2024-02-30 13:30:00, 2024-01-15 25:00:00)
  - **Leap Year Recognition**: Correctly handles leap years
  - **Time Validation**: Validates hours (0-23), minutes (0-59), seconds (0-59)
  - **Flexible Formats**: Supports all Java DateTimeFormatter date-time formats
  - **Multi-language Support**: Error messages in 9 languages
  - **Pattern Requirement**: Pattern must contain time symbols (H, h, K, k, m, s, S, a, A, n, N)
* Format Symbol Reference:

  **Time Symbols:**

  | Symbol | Meaning | Example | Description |
  |--------|---------|---------|-------------|
  | `HH` | Hour (24-hour, zero-padded) | `00`, `23` | 00-23 |
  | `H` | Hour (24-hour, no padding) | `0`, `23` | 0-23 |
  | `hh` | Hour (12-hour, zero-padded) | `01`, `12` | 01-12, use with `a` |
  | `h` | Hour (12-hour, no padding) | `1`, `12` | 1-12, use with `a` |
  | `mm` | Minute (zero-padded) | `00`, `59` | 00-59 |
  | `m` | Minute (no padding) | `0`, `59` | 0-59 |
  | `ss` | Second (zero-padded) | `00`, `59` | 00-59 |
  | `s` | Second (no padding) | `0`, `59` | 0-59 |
  | `SSS` | Millisecond | `000`, `999` | Milliseconds |
  | `a` | AM/PM marker | `AM`, `PM` | Use with 12-hour format |

  **Date Symbols:** See [@Date](#date) for complete date symbol reference.

* Supported Format Examples:
  - Standard: `yyyy-MM-dd HH:mm:ss` → `2024-01-15 13:30:00`
  - ISO 8601: `yyyy-MM-dd'T'HH:mm:ss` → `2024-01-15T13:30:00`
  - With milliseconds: `yyyy-MM-dd HH:mm:ss.SSS` → `2024-01-15 13:30:00.123`
  - 12-hour format: `yyyy-MM-dd hh:mm:ss a` → `2024-01-15 02:30:00 PM`
  - Compact format: `yyyyMMddHHmmss` → `20240115133000`
  - Chinese format: `yyyy年MM月dd日 HH时mm分ss秒` → `2024年12月25日 14时30分00秒`
* Configuration Options:
  - `pattern`: Date-time format pattern, defaults to `"yyyy-MM-dd HH:mm:ss"` (must contain time symbols)
* Validation Examples:
  - ✅ Valid: `2024-01-15 13:30:00`
  - ✅ Valid: `2024-02-29 23:59:59` (leap year)
  - ✅ Valid: `2024-01-15 00:00:00` (midnight)
  - ❌ Invalid: `2024-02-30 13:30:00` (invalid date)
  - ❌ Invalid: `2024-01-15 24:00:00` (hour must be 0-23)
  - ❌ Invalid: `2024-01-15 12:60:00` (minute must be 0-59)
  - ❌ Invalid: `2024-01-15 12:30:60` (second must be 0-59)
* Usage Example:
  ```java
  // Annotation-based usage - default format (yyyy-MM-dd HH:mm:ss)
  @DateTime
  private String createdAt;

  // Custom format
  @DateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private String isoTimestamp;

  @DateTime(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private String preciseTime;

  @DateTime(pattern = "yyyy-MM-dd hh:mm:ss a")
  private String appointmentTime;

  // Chain call usage - default format
  ValidX validator = ValidX.init();
  validator.isDateTime("2024-01-15 13:30:00");

  // Custom format
  validator.isDateTime("2024-01-15T13:30:00", "yyyy-MM-dd'T'HH:mm:ss");
  validator.isDateTime("2024-01-15 13:30:00.123", "yyyy-MM-dd HH:mm:ss.SSS");
  ```
* Important Notes:
  - Pattern must contain at least one time symbol (H, h, K, k, m, s, S, a, A, n, N)
  - For pure date formats (no time), use @Date annotation instead
  - **Strict format matching**: Input must exactly match the pattern including time components
    - ✅ Valid: `@DateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2024-01-15 13:30:00"`
    - ❌ Invalid: `@DateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2024-01-15"` (missing time)
    - ❌ Invalid: `@DateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2024-01-15 13:30"` (incomplete time)
  - Time must be valid: hour 0-23, minute 0-59, second 0-59
  - When using zero-padded formats, values must match exactly
  - null and empty strings pass validation by default (use with `@NotNull` or `@NotEmpty`)

[↑ Back to Quick Reference](#quick-reference-table)

#### @FutureDate
* Validation Rule: Future date validation, validating whether the date is a future date.
* Example Format: `2025-12-31` (pure date format)
* Version Information:
  - Added Version: 1.0.0
  - Modified Version: 1.1.0 (Added `pattern` parameter for custom date format support)
  - Compatibility: ⚠️ **Not fully backward compatible**
* **Important Breaking Changes (v1.0.0 → v1.1.0)**:
  - **v1.0.0 Behavior**: Automatically supports two formats
    - First attempts to parse as `yyyy-MM-dd` format
    - If fails, attempts to parse as `yyyy-MM-dd HH:mm:ss` format
    - **Supports date strings with time** (e.g., `2025-12-31 12:00:00`)
  - **v1.1.0 Behavior**: Only supports pure date format
    - Default format is `yyyy-MM-dd`
    - Custom date formats can be specified via `pattern` parameter (e.g., `MM/dd/yyyy`)
    - **No longer supports formats with time**, pattern cannot contain HH, mm, ss or other time symbols
    - Throws `IllegalArgumentException` if pattern contains time symbols
  - **Migration Recommendation**: For validating future dates with time, use the new @FutureDateTime annotation
* Parameters:
  - `includeToday`: Whether to include today, defaults to `false`
  - `pattern`: Date format pattern, defaults to `"yyyy-MM-dd"` (added in v1.1.0). **Note: Cannot contain time symbols**
* Usage Example:
  ```java
  // Annotation-based usage
  @FutureDate
  private String date;
  // Or include today
  @FutureDate(includeToday = true)
  private String deadline;
  // Custom date format
  @FutureDate(pattern = "MM/dd/yyyy")
  private String usDate;

  // Chain call usage
  ValidX validator = ValidX.init();
  // Default format (yyyy-MM-dd), exclude today
  validator.isFutureDate("2025-12-31");
  // Include today
  validator.isFutureDate("2025-12-31", true);
  // Custom format
  validator.isFutureDate("12/31/2025", false, "MM/dd/yyyy");
  ```
* Important Notes:
  - Pattern must NOT contain time symbols (H, h, K, k, m, s, S, a, A, n, N)
  - For validating future dates with time, use @FutureDateTime annotation instead
  - **Strict format matching**: Input must exactly match the pattern length and format
    - ✅ Valid: `@FutureDate(pattern = "yyyy-MM-dd")` with input `"2025-12-31"`
    - ❌ Invalid: `@FutureDate(pattern = "yyyy-MM-dd")` with input `"2025-12-31 12:00:00"` (contains time)
    - ❌ Invalid: `@FutureDate(pattern = "yyyy-MM-dd")` with input `"2025-1-5"` (missing zero-padding)
  - null and empty strings pass validation by default (use with `@NotNull` or `@NotEmpty`)

[↑ Back to Quick Reference](#quick-reference-table)

#### @PastDate
* Validation Rule: Past date validation, validating whether the date is a past date.
* Example Format: `2020-01-01` (pure date format)
* Version Information:
  - Added Version: 1.0.0
  - Modified Version: 1.1.0 (Added `pattern` parameter for custom date format support)
  - Compatibility: ⚠️ **Not fully backward compatible**
* **Important Breaking Changes (v1.0.0 → v1.1.0)**:
  - **v1.0.0 Behavior**: Automatically supports two formats
    - First attempts to parse as `yyyy-MM-dd` format
    - If fails, attempts to parse as `yyyy-MM-dd HH:mm:ss` format
    - **Supports date strings with time** (e.g., `2020-01-01 12:00:00`)
  - **v1.1.0 Behavior**: Only supports pure date format
    - Default format is `yyyy-MM-dd`
    - Custom date formats can be specified via `pattern` parameter (e.g., `MM/dd/yyyy`)
    - **No longer supports formats with time**, pattern cannot contain HH, mm, ss or other time symbols
    - Throws `IllegalArgumentException` if pattern contains time symbols
  - **Migration Recommendation**: For validating past dates with time, use the new @PastDateTime annotation
* Parameters:
  - `includeToday`: Whether to include today, defaults to `false`
  - `pattern`: Date format pattern, defaults to `"yyyy-MM-dd"` (added in v1.1.0). **Note: Cannot contain time symbols**
* Usage Example:
  ```java
  // Annotation-based usage
  @PastDate
  private String date;
  // Or include today
  @PastDate(includeToday = true)
  private String birthDate;
  // Custom date format
  @PastDate(pattern = "yyyy/MM/dd")
  private String jpDate;

  // Chain call usage
  ValidX validator = ValidX.init();
  // Default format (yyyy-MM-dd), exclude today
  validator.isPastDate("2020-01-01");
  // Include today
  validator.isPastDate("2020-01-01", true);
  // Custom format
  validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");
  ```
* Important Notes:
  - Pattern must NOT contain time symbols (H, h, K, k, m, s, S, a, A, n, N)
  - For validating past dates with time, use @PastDateTime annotation instead
  - **Strict format matching**: Input must exactly match the pattern length and format
    - ✅ Valid: `@PastDate(pattern = "yyyy-MM-dd")` with input `"2020-01-01"`
    - ❌ Invalid: `@PastDate(pattern = "yyyy-MM-dd")` with input `"2020-01-01 12:30:45"` (contains time)
    - ❌ Invalid: `@PastDate(pattern = "yyyy-MM-dd")` with input `"2020-1-1"` (missing zero-padding)
  - null and empty strings pass validation by default (use with `@NotNull` or `@NotEmpty`)

[↑ Back to Quick Reference](#quick-reference-table)

#### @PastDateTime
* Validation Rule: Past date-time validation, validating whether the date-time is in the past (must include time component).
* Example Format: `2020-01-01 12:30:45`, `2020/01/01 12:30:45`
* Parameters:
  - `includeToday`: Whether to include today, defaults to `false`
  - `pattern`: Date-time format pattern, defaults to `"yyyy-MM-dd HH:mm:ss"` (must include time component)
* Usage Example:
  ```java
  // Annotation-based usage
  @PastDateTime
  private String timestamp;
  // Or include today
  @PastDateTime(includeToday = true)
  private String createdAt;
  // Custom date-time format
  @PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private String isoDateTime;

  // Chain call usage
  ValidX validator = ValidX.init();
  // Default format (yyyy-MM-dd HH:mm:ss), exclude today
  validator.isPastDateTime("2020-01-01 12:30:45");
  // Include today
  validator.isPastDateTime("2020-01-01 12:30:45", true);
  // Custom format
  validator.isPastDateTime("2020-01-01T12:30:45", false, "yyyy-MM-dd'T'HH:mm:ss");
  ```
* Important Notes:
  - Pattern must contain at least one time symbol (H, h, K, k, m, s, S, a, A, n, N)
  - For pure date formats (no time), use @PastDate annotation instead
  - **Strict format matching**: Input must exactly match the pattern including time components
    - ✅ Valid: `@PastDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2020-01-01 12:30:45"`
    - ❌ Invalid: `@PastDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2020-01-01"` (missing time)
    - ❌ Invalid: `@PastDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2020-01-01 12:30"` (incomplete time)
  - null and empty strings pass validation by default (use with `@NotNull` or `@NotEmpty`)

[↑ Back to Quick Reference](#quick-reference-table)

#### @FutureDateTime
* Validation Rule: Future date-time validation, validating whether the date-time is in the future (must include time component).
* Example Format: `2025-12-31 23:59:59`, `2025/12/31 23:59:59`
* Parameters:
  - `includeToday`: Whether to include today, defaults to `false`
  - `pattern`: Date-time format pattern, defaults to `"yyyy-MM-dd HH:mm:ss"` (must include time component)
* Usage Example:
  ```java
  // Annotation-based usage
  @FutureDateTime
  private String scheduledTime;
  // Or include today
  @FutureDateTime(includeToday = true)
  private String deadline;
  // Custom date-time format
  @FutureDateTime(pattern = "MM/dd/yyyy HH:mm:ss")
  private String usDateTime;

  // Chain call usage
  ValidX validator = ValidX.init();
  // Default format (yyyy-MM-dd HH:mm:ss), exclude today
  validator.isFutureDateTime("2025-12-31 23:59:59");
  // Include today
  validator.isFutureDateTime("2025-12-31 23:59:59", true);
  // Custom format
  validator.isFutureDateTime("12/31/2025 23:59:59", false, "MM/dd/yyyy HH:mm:ss");
  ```
* Important Notes:
  - Pattern must contain at least one time symbol (H, h, K, k, m, s, S, a, A, n, N)
  - For pure date formats (no time), use @FutureDate annotation instead
  - **Strict format matching**: Input must exactly match the pattern including time components
    - ✅ Valid: `@FutureDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2025-12-31 23:59:59"`
    - ❌ Invalid: `@FutureDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2025-12-31"` (missing time)
    - ❌ Invalid: `@FutureDateTime(pattern = "yyyy-MM-dd HH:mm:ss")` with input `"2025-12-31 23:59"` (incomplete time)
  - null and empty strings pass validation by default (use with `@NotNull` or `@NotEmpty`)

[↑ Back to Quick Reference](#quick-reference-table)

#### @HourMinute
* Validation Rule: Hour minute time format validation, validating whether the time format is HH:mm.
* Example Format: `23:20`, `09:30`
* Usage Example:
  ```java
  // Annotation-based usage
  @HourMinute
  private String time;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isHourMinute("23:20");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @HourMinuteSecond
* Validation Rule: Hour minute second time format validation, validating whether the time format is HH:mm:ss.
* Example Format: `23:50:29`, `09:30:05`
* Usage Example:
  ```java
  // Annotation-based usage
  @HourMinuteSecond
  private String time;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isHourMinuteSecond("23:50:29");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @In
* Validation Rule: Single element or multiple element matching validation, validating whether the value is in the specified value list.
* Example Format: Any specified value
* Usage Example:
  ```java
  // Annotation-based usage - single value validation
  @In({"value1", "value2"})
  private String status;

  // Annotation-based usage - collection/array validation (each element must be in the specified value list)
  @In({"admin", "user", "guest"})
  private List<String> roles;
  
  // Chain call usage - single value validation
  ValidX validator = ValidX.init();
  validator.isIn("value1", new String[]{"value1", "value2"});
  
  // Chain call usage - collection validation
  List<String> roles = Arrays.asList("admin", "user");
  validator.isIn(roles, new String[]{"admin", "user", "guest"});
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @NotIn
* Validation Rule: Single element or multiple element non-matching validation, validating whether the value is not in the specified value list.
* Example Format: Values outside any specified value
* Usage Example:
  ```java
  // Annotation-based usage - single value validation
  @NotIn({"value1", "value2"})
  private String status;

  // Annotation-based usage - collection/array validation (each element must not be in the specified value list)
  @NotIn({"admin", "root", "superuser"})
  private List<String> forbiddenRoles;
  
  // Chain call usage - single value validation
  ValidX validator = ValidX.init();
  validator.isNotIn("value3", new String[]{"value1", "value2"});
  
  // Chain call usage - collection validation
  List<String> roles = Arrays.asList("user", "guest");
  validator.isNotIn(roles, new String[]{"admin", "root", "superuser"});
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @FileExtension
* Validation Rule: File extension validation, validating whether the file name's extension is in the specified extension list.
* Example Format: Specified file extensions
* Usage Example:
  ```java
  // Annotation-based usage case insensitive default is false
  @FileExtension(value = {"xls", "xlsx"})
  private String fileName;
  // Annotation-based usage case sensitive
  @FileExtension(value = {"xls", "xlsx"}, ignoreCase = true)
  private String documentName;
  ```
* When using chain calls, you can also specify whether to ignore case:
  ```java
  // Chain call usage
  ValidX validator = ValidX.init();
  
  // Default case insensitive
  validator.isFileExtension("document.xls", new String[]{"XLS"});
  
  // Explicitly specify case insensitive
  validator.isFileExtension("document.xls", new String[]{"XLS"}, true);
  
  // Case sensitive
  validator.isFileExtension("document.xls", new String[]{"XLS"}, false);
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @FileSize
* Validation Rule: File size validation, validating whether the file size is within the specified range.
* Supported Types:
  - `java.io.File` - File objects
  - `java.nio.file.Path` - NIO Path objects
  - `byte[]` - Byte arrays
  - `org.springframework.web.multipart.MultipartFile` - Spring multipart files (requires Spring dependency)
* Configuration Options:
  - `min`: Minimum size (human-readable format, e.g., "1KB", "10MB"), default is "0B"
  - `max`: Maximum size (human-readable format, e.g., "1KB", "10MB"), default is unlimited
  - `allowedTypes`: Allowed MIME types (only effective for MultipartFile)
* Example Format: File sizes with units like "10KB", "5MB", "1GB"
* Usage Example:
  ```java
  // Specify both minimum and maximum
  @FileSize(min = "1KB", max = "10MB")
  private File document;

  // Only specify maximum
  @FileSize(max = "5MB")
  private Path filePath;

  // Usage with byte array
  @FileSize(max = "1MB")
  private byte[] imageData;

  // Usage with MultipartFile (Spring) - with MIME type restriction
  @FileSize(min = "100KB", max = "5MB", allowedTypes = {"image/jpeg", "image/png"})
  private MultipartFile avatar;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Only specify maximum
  validator.isFileSize(file, "10MB");

  // Specify both minimum and maximum
  validator.isFileSize(file, "1KB", "10MB");
  ```
* Notes:
  - Supported size units: B (bytes), KB (kilobytes), MB (megabytes), GB (gigabytes), TB (terabytes)
  - 1KB = 1024 bytes (binary units)
  - Decimal values are supported: "1.5GB", "0.5MB"
  - MIME type validation is only available for MultipartFile
  - MultipartFile support uses reflection, no strong Spring dependency required


[↑ Back to Quick Reference](#quick-reference-table)

#### @Lower
* Validation Rule: Lowercase character validation, only allowing lowercase English letters.
* Example Format: `abcdef`
* Usage Example:
  ```java
  // Annotation-based usage
  @Lower
  private String text;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isLower("abcdef");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Upper
* Validation Rule: Uppercase character validation, only allowing uppercase English letters.
* Example Format: `ABCDEF`
* Usage Example:
  ```java
  // Annotation-based usage
  @Upper
  private String text;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isUpper("ABCDEF");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Xdigit
* Validation Rule: Hexadecimal string validation, only allowing hexadecimal characters (0-9, a-f, A-F).
* Example Format: `0a1B2c3D`
* Usage Example:
  ```java
  // Annotation-based usage
  @Xdigit
  private String hex;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isXdigit("0a1B2c3D");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Password
* Validation Rule: Password strength validation, validating whether the password meets the specified strength requirements.
* Validation Rules:
  - Minimum length--default 8 characters
  - Whether to include uppercase letters, lowercase letters, digits, and special symbols (all must be included by default)
  - requireUppercase: Whether to include uppercase letters (default is yes)
  - requireLowercase: Whether to include lowercase letters (default is yes)
  - requireDigit: Whether to include digits (default is yes)
  - requireSpecialChar: Whether to include special characters (default is yes)
* Usage Example:
  ```java
  // Use default rules (minimum length 8 characters, must include uppercase and lowercase letters, digits, and special characters)
  @Password
  private String password;

  // Specify minimum length
  @Password(minLength = 6)
  private String simplePassword;

  // Specify minimum length, and do not require special characters
  @Password(minLength = 6, requireSpecialChar = false)
  private String customPassword;
  ```
* When using chain calls, you can also specify password strength requirements:
  ```java
  // Chain call usage
  ValidX validator = ValidX.init();
  
  // Use default rules (minimum length 8 characters, must include uppercase and lowercase letters, digits, and special characters)
  validator.isPassword("MyPassword123!");
  
  // Specify minimum length
  validator.isPassword("mypassword123", 8);
  
  // Fully customized rules (minimum length 8 characters, do not require uppercase letters, require lowercase letters and digits, do not require special characters)
  validator.isPassword("mypassword123", 8, false, true, true, false);
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @UUID
* Validation Rule: UUID (Universally Unique Identifier) format validation, supporting standard format (with hyphens) and compact format (without hyphens).
* Supported Formats:
  - Standard format (with hyphens): `550e8400-e29b-41d4-a716-446655440000`
  - Compact format (without hyphens): `550e8400e29b41d4a716446655440000` (when `allowWithoutHyphens = true`)
* Configuration Options:
  - `allowWithoutHyphens`: Whether to allow format without hyphens, default is `false` (only allow standard format)
* Usage Example:
  ```java
  // Only allow standard format (with hyphens)
  @UUID
  private String id;

  // Allow both formats (with and without hyphens)
  @UUID(allowWithoutHyphens = true)
  private String transactionId;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate standard format
  validator.isUUID("550e8400-e29b-41d4-a716-446655440000");

  // Allow compact format
  validator.isUUID("550e8400e29b41d4a716446655440000", true);
  ```
* Notes:
  - UUID validation is case-insensitive (both uppercase and lowercase hexadecimal characters are allowed)
  - Standard format must contain exactly 4 hyphens at specific positions
  - Compact format must be exactly 32 hexadecimal characters
  - Common UUID versions (v1, v4, etc.) are all supported


[↑ Back to Quick Reference](#quick-reference-table)

#### @Base64
* Validation Rule: Base64 encoding format validation, supporting standard Base64 and URL-safe Base64 formats.
* Supported Formats:
  - Standard format: A-Z, a-z, 0-9, +, / with padding (=)
  - URL-safe format: A-Z, a-z, 0-9, -, _ with padding (=)
* Configuration Options:
  - `urlSafe`: Whether to use URL-safe format, default is `false` (use standard format)
  - `allowNoPadding`: Whether to allow format without padding, default is `false` (padding required)
* Usage Example:
  ```java
  // Standard Base64 format only
  @Base64
  private String data;

  // URL-safe Base64 format
  @Base64(urlSafe = true)
  private String urlSafeData;

  // Allow format without padding
  @Base64(allowNoPadding = true)
  private String noPaddingData;

  // URL-safe + allow no padding
  @Base64(urlSafe = true, allowNoPadding = true)
  private String jwtPayload;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate standard format
  validator.isBase64("SGVsbG8gV29ybGQ=");

  // Validate URL-safe format
  validator.isBase64("SGVsbG8gV29ybGQ=", true);

  // Allow format without padding
  validator.isBase64("SGVsbG8gV29ybGQ", false, true);
  ```
* Notes:
  - Standard Base64 uses +/ characters, URL-safe uses -_ characters
  - Padding character = can only appear at the end, maximum 2 characters
  - String length must be a multiple of 4 (unless allowNoPadding is enabled)
  - Common use cases: file upload, JWT tokens, image data transmission


[↑ Back to Quick Reference](#quick-reference-table)

#### @Age
* Validation Rule: Age validation based on birth date or ID card number, supporting minimum age and maximum age constraints.
* Supported Types:
  - `java.time.LocalDate` - Birth date
  - `java.util.Date` - Birth date
  - `String` - Birth date string or ID card number
* Configuration Options:
  - `min`: Minimum age (inclusive), 0 means no limit, default is 0
  - `max`: Maximum age (inclusive), 0 means no limit, default is 0
  - `fromIdCard`: Whether to extract birth date from ID card number, default is `false`
  - `dateFormat`: Date format (only effective when field is String and fromIdCard=false), default is "yyyy-MM-dd"
* Usage Example:
  ```java
  // Validate age between 18 and 65
  @Age(min = 18, max = 65)
  private LocalDate birthDate;

  // Only validate minimum age
  @Age(min = 18)
  private String birthDateStr;  // "1990-01-01"

  // Extract age from ID card number
  @Age(min = 18, max = 65, fromIdCard = true)
  private String idCard;

  // Specify date format
  @Age(min = 18, dateFormat = "yyyy/MM/dd")
  private String birthDate;  // "1990/01/01"

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate minimum age only
  validator.isAge(LocalDate.now().minusYears(25), 18);

  // Validate age range
  validator.isAge("1990-01-01", 18, 65);

  // Extract from ID card
  validator.isAge("11010119900101001X", 18, 65, true);

  // Custom date format
  validator.isAge("1990/06/15", 18, 65, false, "yyyy/MM/dd");
  ```
* Notes:
  - Age is calculated in full years (周岁) from birth date to current date
  - Supports both 15-digit and 18-digit Chinese ID card formats
  - Null or empty values pass validation (handled by @NotNull/@NotEmpty)
  - Future birth dates are treated as age 0
  - Common date formats are automatically tried: yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd


[↑ Back to Quick Reference](#quick-reference-table)

#### @JSON
* Validation Rule: JSON format validation, supporting standard JSON syntax with configurable type restrictions, depth limits, and length limits.
* Supported Types:
  - OBJECT: JSON objects only (e.g., `{"key":"value"}`)
  - ARRAY: JSON arrays only (e.g., `[1,2,3]`)
  - ANY: Both objects and arrays (default)
* Configuration Options:
  - `type`: JSON type restriction (ANY/OBJECT/ARRAY), default is ANY
  - `strict`: Whether to enforce strict JSON syntax, default is `true`
  - `maxDepth`: Maximum nesting depth (0 means unlimited), default is 0
  - `maxLength`: Maximum string length (0 means unlimited), default is 0
* Example Format: `{"name":"John","age":30}`, `[1,2,3]`, `{"users":[{"id":1}]}`
* Usage Example:
  ```java
  // Only allow valid JSON (any type)
  @JSON
  private String data;

  // Only allow JSON objects
  @JSON(type = JSON.JSONType.OBJECT)
  private String config;

  // Only allow JSON arrays
  @JSON(type = JSON.JSONType.ARRAY)
  private String items;

  // Limit nesting depth to prevent deeply nested structures
  @JSON(maxDepth = 5)
  private String jsonData;

  // Limit string length to prevent oversized JSON
  @JSON(maxLength = 1000)
  private String jsonPayload;

  // Combine multiple restrictions
  @JSON(type = JSON.JSONType.OBJECT, strict = true, maxDepth = 10, maxLength = 5000)
  private String apiRequest;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate any JSON type
  validator.isJSON("{\"name\":\"John\",\"age\":30}");

  // Validate specific type
  validator.isJSON("[1,2,3]", JSON.JSONType.ARRAY);

  // Validate with strict mode control
  validator.isJSON("{\"key\":\"value\"}", JSON.JSONType.OBJECT, true);

  // Full control with all options
  validator.isJSON("{\"data\":{\"nested\":true}}", JSON.JSONType.OBJECT, true, 5, 1000);
  ```
* Notes:
  - Uses a lightweight built-in JSON parser without external dependencies
  - Supports all JSON types: objects, arrays, strings, numbers, booleans, null
  - Handles escape sequences (\n, \t, \", \\, etc.) and Unicode escapes (\uXXXX)
  - Strict mode enforces proper JSON syntax (no trailing commas, quoted keys)
  - Depth limits help prevent stack overflow from deeply nested structures
  - Length limits help prevent memory issues from large JSON strings
  - Common use cases: API request/response validation, config file validation, data serialization


[↑ Back to Quick Reference](#quick-reference-table)

#### @PhoneNumber
* Validation Rule: International phone number validation, supporting multiple international phone number formats including E.164 standard format.
* Supported Formats:
  - E.164 format: `+8613812345678`, `+14155552671`
  - With spaces: `+86 138 1234 5678`, `+1 415 555 2671`
  - With hyphens: `+1-415-555-2671`
  - With parentheses: `+1 (415) 555-2671`, `(555) 123-4567`
  - Local format: `13812345678`, `138 1234 5678`
  - With extensions: `+1-415-555-2671 ext. 123`, `+14155552671 x123`, `+14155552671#456`
* Configuration Options:
  - `countryCode`: Restrict to specific country code (e.g., "+86", "+1"), default is empty (accepts all)
  - `allowExtension`: Whether to allow extension numbers (ext., x, #), default is `true`
  - `strict`: Strict mode requires country code (starts with +), default is `false`
* Example Format: `+8613812345678`, `+1-415-555-2671`, `(555) 123-4567 ext. 123`
* Usage Example:
  ```java
  // Allow any valid international phone number
  @PhoneNumber
  private String phoneNumber;

  // Only allow specific country code (China)
  @PhoneNumber(countryCode = "+86")
  private String chinaPhone;

  // Only allow specific country code (USA)
  @PhoneNumber(countryCode = "+1")
  private String usaPhone;

  // Do not allow extension numbers
  @PhoneNumber(allowExtension = false)
  private String directPhone;

  // Strict mode: must include country code
  @PhoneNumber(strict = true)
  private String internationalPhone;

  // Combine multiple restrictions
  @PhoneNumber(countryCode = "+1", allowExtension = true, strict = true)
  private String companyPhone;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate any phone number
  validator.isPhoneNumber("+8613812345678");

  // Validate with specific country code
  validator.isPhoneNumber("+14155552671", "+1");

  // Validate with extension control
  validator.isPhoneNumber("+1-415-555-2671 ext. 123", "", true);

  // Full control with all options
  validator.isPhoneNumber("+1-415-555-2671 ext. 123", "+1", true, true);
  ```
* Notes:
  - Supports E.164 international standard format (+ followed by 4-15 digits)
  - Accepts various formatting characters: spaces, hyphens, parentheses, dots
  - Extension formats: ext., extension, x, #
  - Country code length: 1-3 digits
  - Phone number length (digits only): 4-15 characters
  - Strict mode enforces international format (must start with +)
  - Common use cases: User registration, contact management, international communication


[↑ Back to Quick Reference](#quick-reference-table)

#### @JWT
* Validation Rule: JWT (JSON Web Token) format validation, validating whether the JWT Token has the correct basic format.
* JWT Format Description:
  - JWT consists of three parts separated by dots (.): `header.payload.signature`
  - Header: Base64URL encoded JSON object describing the token type and signature algorithm
  - Payload: Base64URL encoded JSON object containing claims
  - Signature: Signature used to verify the integrity of the token
  - Each part uses Base64URL encoding (A-Z, a-z, 0-9, -, _)
* Example Format:
  - `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c`
* Usage Example:
  ```java
  // Annotation-based usage
  @JWT
  private String token;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isJWT("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U");
  ```
* Notes:
  - This validator only verifies the basic format of JWT (three-part structure and Base64URL encoding)
  - Does not verify signature validity (requires secret key)
  - Does not verify expiration time and other claims
  - Common use cases: API authentication, Single Sign-On (SSO), information exchange


[↑ Back to Quick Reference](#quick-reference-table)

#### @SemVer
* Validation Rule: Semantic Versioning format validation, validating whether the version number conforms to the SemVer 2.0.0 specification.
* Version Number Format Description:
  - Basic Format: `MAJOR.MINOR.PATCH` (major version.minor version.patch version)
  - MAJOR: Major version number, incremented for incompatible API changes
  - MINOR: Minor version number, incremented for backward-compatible functionality additions
  - PATCH: Patch version number, incremented for backward-compatible bug fixes
  - Pre-release Version: `MAJOR.MINOR.PATCH-prerelease` (e.g., 1.0.0-alpha, 1.0.0-beta.1)
  - Build Metadata: `MAJOR.MINOR.PATCH+build` (e.g., 1.0.0+20130313144700)
  - Complete Format: `MAJOR.MINOR.PATCH-prerelease+build`
* Example Formats:
  - Basic Versions: `1.0.0`, `2.1.3`, `10.20.30`
  - Pre-release Versions: `1.0.0-alpha`, `1.0.0-beta.1`, `2.1.0-rc.2`
  - With Build Metadata: `1.0.0+20130313144700`, `1.0.0+001`
  - Complete Format: `1.0.0-alpha+001`, `1.0.0-beta+exp.sha.5114f85`
  - With v Prefix (when enabled): `v1.0.0`, `v2.1.3-beta`
* Usage Example:
  ```java
  // Annotation-based usage - Standard format
  @SemVer
  private String version;

  // Annotation-based usage - Allow v prefix
  @SemVer(allowVPrefix = true)
  private String versionWithPrefix;

  // Chain call usage - Standard format
  ValidX validator = ValidX.init();
  validator.isSemVer("1.0.0");
  validator.isSemVer("2.1.3-beta.1");

  // Chain call usage - Allow v prefix
  ValidX validator2 = ValidX.init();
  validator2.isSemVer("v1.0.0", true);
  validator2.isSemVer("v2.1.3-rc.1", true);
  ```
* Notes:
  - Strictly follows the SemVer 2.0.0 specification ( https://semver.org/ )
  - Version number parts cannot have leading zeros (except 0 itself), e.g., `01.0.0` is invalid
  - Version number must contain three parts, e.g., `1.0` is invalid
  - Pre-release identifiers consist of alphanumerics and hyphens, separated by dots
  - Build metadata does not affect version precedence, used only for build information
  - By default, v prefix is not allowed, enable it with `allowVPrefix=true` when needed
  - Common use cases: Software version management, npm package versions, API version control, Git tags


[↑ Back to Quick Reference](#quick-reference-table)

#### @Timestamp
* Validation Rule: Unix timestamp format validation, validating whether the value is a valid Unix timestamp (supports seconds and milliseconds).
* Timestamp Format Description:
  - Seconds Timestamp: 10-digit numeric string or Long value (e.g., `1700000000`)
  - Milliseconds Timestamp: 13-digit numeric string or Long value (e.g., `1700000000000`)
  - Accepts both String and Long types for validation
* Parameters:
  - `unit`: Specifies the timestamp unit, defaults to `ANY` (accepts both seconds and milliseconds)
    - `TimestampUnit.SECONDS` — Only accepts 10-digit (seconds) timestamps
    - `TimestampUnit.MILLISECONDS` — Only accepts 13-digit (milliseconds) timestamps
    - `TimestampUnit.ANY` — Accepts both 10-digit (seconds) and 13-digit (milliseconds) timestamps
* Usage Example:
  ```java
  // Annotation-based usage - Accept both seconds and milliseconds
  @Timestamp
  private String createTime;

  // Annotation-based usage - Only accept seconds
  @Timestamp(unit = Timestamp.TimestampUnit.SECONDS)
  private String createTimeSec;

  // Annotation-based usage - Only accept milliseconds
  @Timestamp(unit = Timestamp.TimestampUnit.MILLISECONDS)
  private Long createTimeMs;

  // Chain call usage - Default ANY mode
  ValidX validator = ValidX.init();
  validator.isTimestamp("1700000000");
  validator.isTimestamp("1700000000000");

  // Chain call usage - Specify unit
  ValidX validator2 = ValidX.init();
  validator2.isTimestamp("1700000000", Timestamp.TimestampUnit.SECONDS);
  validator2.isTimestamp(1700000000000L, Timestamp.TimestampUnit.MILLISECONDS);
  ```
* Notes:
  - Seconds timestamps must be exactly 10 digits (range: 0 ~ 9999999999)
  - Milliseconds timestamps must be exactly 13 digits (range: 0 ~ 99999999999)
  - Negative values are not accepted (Unix timestamps are non-negative)
  - Non-numeric characters (letters, special characters, decimals, spaces) are rejected
  - Null values are not validated by this annotation (use `@NotNull` for null checks)
  - Invalid digit lengths (e.g., 9-digit, 11-digit, 12-digit) are rejected regardless of unit mode
  - Common use cases: API timestamp parameters, database time fields, message queue timestamps


[↑ Back to Quick Reference](#quick-reference-table)

#### @CronExpression
* Validation Rule: Cron expression format validation, validating whether the value is a valid Cron expression.
* Supported Formats:
  - 6-field format: seconds minutes hours day month week (e.g., `0 0 12 * * ?`)
  - 7-field format: seconds minutes hours day month week year (e.g., `0 0 12 * * ? 2025`)
* Supported Special Characters:
  - `*` : Match any value
  - `?` : No specific value (only for day and week fields)
  - `-` : Range (e.g., `1-5`)
  - `,` : List (e.g., `1,3,5`)
  - `/` : Step (e.g., `0/15`)
  - `L` : Last (e.g., `L` for last day of month)
  - `W` : Weekday (e.g., `15W`)
  - `#` : Nth weekday (e.g., `6#3` for 3rd Friday)
* Example Format: `0 0 12 * * ?`
* Usage Example:
  ```java
  // Annotation-based usage
  @CronExpression
  private String schedule;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isCronExpression("0 0 12 * * ?");
  validator.isCronExpression("0 0/15 * * * ?");
  validator.isCronExpression("0 0 9 ? * MON-FRI");
  ```
* Notes:
  - Day and week fields cannot both be non-`?` values
  - Supports month abbreviations (JAN-DEC) and day abbreviations (SUN-SAT)
  - Second, minute: 0-59; Hour: 0-23; Day: 1-31; Month: 1-12; Week: 0-7 (0 and 7 both represent Sunday)
  - Year range: 1970-2099 (optional field)
  - Common use cases: Scheduled tasks, job scheduling, timer triggers


[↑ Back to Quick Reference](#quick-reference-table)

#### @Duration
* Validation Rule: Duration format validation, validating whether the value is a valid time duration format.
* Supported Formats:
  - ISO 8601 format: Standard format starting with P (e.g., `PT2H30M`, `P1Y2M3D`, `P1DT12H`)
  - Simplified format: Number + unit combinations (e.g., `2h30m`, `1y2mo3d`, `1d12h`)
* Supported Time Units:
  - `y/Y` - Year (e.g., `P1Y` or `1y`)
  - `mo/MO` - Month (simplified format uses "mo" to distinguish from minute "m") (e.g., `P2M` or `2mo`)
  - `d/D` - Day (e.g., `P3D` or `3d`)
  - `h/H` - Hour (e.g., `PT4H` or `4h`)
  - `m/M` - Minute (e.g., `PT30M` or `30m`)
  - `s/S` - Second (e.g., `PT45S` or `45s`)
* Configuration Options:
  - `format`: Specify duration format type - `ISO_8601`, `SIMPLE`, or `ANY` (default)
* Example Formats:
  - ISO 8601: `PT2H30M` (2 hours 30 minutes), `P1Y2M3D` (1 year 2 months 3 days), `P1DT12H` (1 day 12 hours)
  - Simplified: `2h30m` (2 hours 30 minutes), `1y2mo3d` (1 year 2 months 3 days), `1d12h` (1 day 12 hours)
* Usage Example:
  ```java
  // Annotation-based usage - accepts any format
  @Duration
  private String duration;

  // Only accept ISO 8601 format
  @Duration(format = Duration.DurationFormat.ISO_8601)
  private String isoDuration;

  // Only accept simplified format
  @Duration(format = Duration.DurationFormat.SIMPLE)
  private String simpleDuration;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate any format
  validator.isDuration("PT2H30M");
  validator.isDuration("2h30m");
  validator.isDuration("P1Y2M3D");
  validator.isDuration("1y2mo3d");

  // Specify format type
  validator.isDuration("PT2H30M", Duration.DurationFormat.ISO_8601);
  validator.isDuration("2h30m", Duration.DurationFormat.SIMPLE);
  ```
* Notes:
  - ISO 8601 format: `P[nY][nM][nD][T[nH][nM][nS]]` where P is required, T separates date and time components
  - Simplified format uses "mo" for month to avoid confusion with "m" for minute
  - Both formats are case-insensitive
  - At least one time unit must be specified
  - Year is the largest unit supported in ISO 8601 standard
  - Common use cases: Task duration, time period configuration, timeout settings


[↑ Back to Quick Reference](#quick-reference-table)

#### @ExpressNumber
* Validation Rule: Express tracking number format validation, validating whether the value is a valid express tracking number.
* Supported Express Companies:
  - SF Express (SF_EXPRESS): 12-digit number
  - YTO Express (YTO_EXPRESS): YT prefix + 11-13 digits, or 10-13 pure digits
  - STO Express (STO_EXPRESS): 12-digit number
  - ZTO Express (ZTO_EXPRESS): 12-digit number or alphanumeric combination
  - Yunda Express (YUNDA_EXPRESS): 13-digit number
  - China Post EMS (EMS): E + 9 digits + CN, or 2 letters + 9 digits + CN
  - JD Logistics (JD_LOGISTICS): JD prefix + 13-15 digits
  - Deppon (DEPPON): 8-9 digit number
  - TTKD Express (TTKD_EXPRESS): 12-14 digit number
  - Best Express (BEST_EXPRESS): 10-12 digits or letters
* Configuration Options:
  - `companies`: Specify express company types (default: all supported companies)
* Example Formats:
  - SF Express: `123456789012`
  - YTO Express: `YT1234567890123`, `1234567890`
  - EMS: `E123456789CN`, `EA123456789CN`
  - JD Logistics: `JD1234567890123`
  - Deppon: `12345678`, `123456789`
* Usage Example:
  ```java
  // Annotation-based usage - accepts all supported companies
  @ExpressNumber
  private String trackingNumber;

  // Only accept SF Express
  @ExpressNumber(companies = {ExpressNumber.ExpressCompany.SF_EXPRESS})
  private String sfNumber;

  // Accept SF Express or YTO Express
  @ExpressNumber(companies = {ExpressNumber.ExpressCompany.SF_EXPRESS, ExpressNumber.ExpressCompany.YTO_EXPRESS})
  private String mixedNumber;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate any supported express company
  validator.isExpressNumber("123456789012");
  validator.isExpressNumber("E123456789CN");
  validator.isExpressNumber("JD1234567890123");

  // Specify express company type
  validator.isExpressNumber("123456789012", ExpressNumber.ExpressCompany.SF_EXPRESS);
  validator.isExpressNumber("E123456789CN", ExpressNumber.ExpressCompany.EMS);

  // Multiple companies
  validator.isExpressNumber("123456789012", ExpressNumber.ExpressCompany.SF_EXPRESS, ExpressNumber.ExpressCompany.STO_EXPRESS);
  ```
* Notes:
  - Format validation is based on common patterns for each express company
  - Validation does not verify whether the tracking number actually exists in the carrier's system
  - Some formats may overlap between different express companies (e.g., 12-digit numbers)
  - EMS format is case-insensitive
  - Common use cases: E-commerce order management, logistics tracking, shipping validation


[↑ Back to Quick Reference](#quick-reference-table)

#### @StartsWith
* Validation Rule: Prefix validation, validating whether the string starts with the specified prefix.
* Example Format: Starting with specified string
* Usage Example:
  ```java
  // Annotation-based usage
  @StartsWith(startsWith = "prefix")
  private String code;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isStartsWith("prefix_string", new String[]{"prefix"});
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Contains
* Validation Rule: Contains validation, validating whether the string contains the specified substring(s). Supports multiple substrings matching modes (OR/AND) and case-insensitive matching.
* Example Format: `"hello world"` contains `"hello"`, `"test@example.com"` contains both `"@"` and `"."`
* Configuration Options:
  - `value`: Array of substrings to match
  - `ignoreCase`: Whether to ignore case, default is `false`
  - `matchAll`: Matching mode, default is `false`
    - `false` (default): OR logic - matches if ANY substring is found
    - `true`: AND logic - matches only if ALL substrings are found
* Usage Example:
  ```java
  // Annotation-based usage - single substring (OR logic)
  @Contains({"@"})
  private String email;

  // Multiple substrings (OR logic - matches any)
  @Contains({"product", "service"})
  private String description;

  // Multiple substrings (AND logic - must match all)
  @Contains(value = {"@", "."}, matchAll = true)
  private String emailStrict;

  // Case-insensitive matching
  @Contains(value = {"HELLO"}, ignoreCase = true)
  private String greeting;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Basic usage (OR logic)
  validator.isContains("hello world", new String[]{"hello"});

  // Multiple substrings (OR logic)
  validator.isContains("test@example.com", new String[]{"@", ".com"});

  // Case-insensitive (OR logic)
  validator.isContains("Hello World", new String[]{"hello"}, true);

  // AND logic - must contain all substrings
  validator.isContains("test@example.com", new String[]{"@", "."}, false, true);
  ```
* Notes:
  - **OR logic** (default): Matches if the string contains ANY of the specified substrings
  - **AND logic** (`matchAll = true`): Matches only if the string contains ALL of the specified substrings
  - Substring can appear at any position (beginning, middle, or end)
  - Default is case-sensitive; use `ignoreCase = true` for case-insensitive matching
  - Common use cases: email validation (`@`), strict email validation (`@` and `.`), URL checking (`http://`), password strength (must contain multiple character types), content filtering

[↑ Back to Quick Reference](#quick-reference-table)

#### @NotContains
* Validation Rule: Not contains validation, validating whether the string does NOT contain the specified substring(s). Useful for security validation, content filtering, and preventing sensitive keywords.
* Example Format: `"user123"` does not contain `"admin"`, `"https://example.com"` does not contain `"javascript:"`
* Configuration Options:
  - `value`: Array of forbidden substrings
  - `ignoreCase`: Whether to ignore case, default is `false`
  - `matchAll`: Matching mode, default is `true`
    - `true` (default): AND logic - passes only if ALL forbidden substrings are absent
    - `false`: OR logic - passes if ANY forbidden substring is absent
* Usage Example:
  ```java
  // Annotation-based usage - security validation
  @NotContains(value = {"admin", "root", "system"}, ignoreCase = true)
  private String username;

  // Content filtering
  @NotContains(value = {"spam", "offensive"}, ignoreCase = true)
  private String comment;

  // XSS prevention
  @NotContains(value = {"<script", "javascript:", "onerror="}, ignoreCase = true)
  private String userInput;

  // URL security validation (AND logic - must not contain any)
  @NotContains(value = {"javascript:", "data:", "vbscript:"}, matchAll = true)
  private String url;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Basic usage (AND logic - default)
  validator.isNotContains("user123", new String[]{"admin", "root"});

  // Case-insensitive
  validator.isNotContains("normaluser", new String[]{"ADMIN", "ROOT"}, true);

  // OR logic - passes if at least one is absent
  validator.isNotContains("hello world", new String[]{"script", "alert"}, false, false);

  // AND logic - must not contain all
  validator.isNotContains("https://example.com", new String[]{"javascript:", "data:"}, false, true);
  ```
* Notes:
  - **AND logic** (default): Passes only if the string does NOT contain ALL of the specified substrings
  - **OR logic** (`matchAll = false`): Passes if the string does NOT contain AT LEAST ONE of the specified substrings
  - Default is case-sensitive; use `ignoreCase = true` for case-insensitive matching
  - Common use cases: username validation (block reserved keywords), XSS prevention, content moderation, URL security validation
  - Complements `@Contains` for comprehensive string validation

[↑ Back to Quick Reference](#quick-reference-table)

#### @EndsWith
* Validation Rule: Suffix validation, validating whether the string ends with the specified suffix.
* Example Format: Ending with specified string
* Usage Example:
  ```java
  // Annotation-based usage
  @EndsWith(endsWith = "suffix")
  private String code;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isEndsWith("string_suffix", new String[]{"suffix"});
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Enum
* Validation Rule: Single element or multiple element enumeration value validation, validating whether it is a valid value in the specified enumeration.
* Example Format: One of the specified enumeration values
* Usage Example:
  ```java
  // Validate single enumeration value (validate code() value by default)
  @Enum(target = MyEnum.class)
  private String status;
  
  // Validate specific field value of enumeration
  @Enum(target = MyEnum.class, field = "type")
  private String statusCode;
  
  // Validate enumeration value collection (each element must be a valid value of the specified enumeration)
  @Enum(target = MyEnum.class)
  private List<String> statuses;
  
  // Validate enumeration value collection of specific field values
  @Enum(target = MyEnum.class, field = "type")
  private List<String> statusCodes;
  ```
* When using chain calls, you can also specify enumeration fields:
  ```java
  // Chain call usage - single value validation
  ValidX validator = ValidX.init();
  
  // Validate enumeration's name() value (default)
  validator.isEnum("VALUE1", MyEnum.class);
  
  // Validate specific field value of enumeration (such as code field)
  validator.isEnum("code001", MyEnum.class, "code");
  
  // Chain call usage - collection validation
  List<String> statuses = Arrays.asList("VALUE1", "VALUE2");
  validator.isEnum(statuses, MyEnum.class);
  
  // Chain call usage - array validation
  String[] statusArray = {"VALUE1", "VALUE2"};
  validator.isEnum(statusArray, MyEnum.class);
  ```
* Example enumeration class:
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

[↑ Back to Quick Reference](#quick-reference-table)

#### @Color
* Validation Rule: Color format validation, validating whether the string is a valid HEX color value, supporting #FFF or #FFFFFF format.
* Example Format: `#FF0000`, `#F00`, `#ffffff`, `#000`
* Usage Example:
  ```java
  // Annotation-based usage
  @Color
  private String color;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isColor("#FF0000");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### Identity Verification Related

#### @ChineseName
* Validation Rule: Chinese name validation, verifies if a string conforms to Chinese naming conventions.
* Validation Requirements:
  - Only Chinese characters allowed
  - Length between 2-50 characters (covers all Chinese names including very long minority names)
  - Supports middle dot "·" in minority names
  - No numbers, letters, or special characters allowed
* Example Formats:
  - Han Chinese names: `张三`, `李四`, `欧阳修`, `诸葛亮`
  - Minority names: `买买提·吐尔逊`, `迪丽热巴·迪力木拉提`
  - Historical names: `爱新觉罗·玄烨`
* Usage Examples:
  ```java
  // Annotation-based usage
  @ChineseName
  private String realName;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseName("张三");
  validator.isChineseName("买买提·吐尔逊");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseIdCard
* Validation Rule: Mainland China ID card number validation, supporting 18-digit and 15-digit ID card numbers.
* Example Format: `11010119900307211X` (18-digit) or `11010119900307211` (15-digit)
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseIdCard
  private String idCard;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseIdCard("11010119900307211X");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChinesePassport
* Validation Rule: Chinese passport number validation, supporting various types of Chinese passport numbers.
* Example Format: `G12345678`, `E12345678`, `S12345678`, `D1234567`, `P1234567`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePassport
  private String passportNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChinesePassport("G12345678");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseMilitaryOfficer
* Validation Rule: Chinese military officer certificate validation, supporting various types of Chinese military officer certificates.
* Example Format: `军字第1234567号`, `海字第1234567号`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseMilitaryOfficer
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseMilitaryOfficer("军字第1234567号");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseSoldier
* Validation Rule: Chinese soldier certificate validation, supporting various types of Chinese soldier certificates.
* Example Format: `沈字第0100000号`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseSoldier
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseSoldier("沈字第0100000号");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ForeignerPermanentResidenceIdentity
* Validation Rule: Foreigner permanent residence identity card validation, validating foreigner permanent residence identity card numbers.
* Example Format: `911124198108030028`
* Usage Example:
  ```java
  // Annotation-based usage
  @ForeignerPermanentResidenceIdentity
  private String identityNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isForeignerPermanentResidenceIdentity("911124198108030028");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @HKMacauResidence
* Validation Rule: Hong Kong and Macau residents' residence permit validation, validating Hong Kong and Macau residents' residence permit numbers.
* Example Format: `810000000000000001`, `82000000000000000X`
* Usage Example:
  ```java
  // Annotation-based usage
  @HKMacauResidence
  private String residenceNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isHKMacauResidence("810000000000000001");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @HKMacauPass
* Validation Rule: Hong Kong and Macau residents' travel permit to Mainland China (Home Return Permit) validation, validating Hong Kong and Macau residents' travel permit numbers.
* Example Format: `H1234567800`, `M1234567801`
* Usage Example:
  ```java
  // Annotation-based usage
  @HKMacauPass
  private String passNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isHKMacauPass("H1234567800");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @TaiwanResidence
* Validation Rule: Taiwan residents' residence permit validation, validating Taiwan residents' residence permit numbers.
* Example Format: `830000000000000001`
* Usage Example:
  ```java
  // Annotation-based usage
  @TaiwanResidence
  private String residenceNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isTaiwanResidence("830000000000000001");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @TaiwanPass
* Validation Rule: Taiwan residents' travel permit to Mainland China (Taiwan Compatriot Pass) validation, validating Taiwan residents' travel permit numbers.
* Example Format: `1234567800`
* Usage Example:
  ```java
  // Annotation-based usage
  @TaiwanPass
  private String passNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isTaiwanPass("1234567800");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ForeignerWorkPermit
* Validation Rule: Foreigner work permit validation, validating foreigner work permit numbers.
* Example Format: Combination of letters and numbers
* Usage Example:
  ```java
  // Annotation-based usage
  @ForeignerWorkPermit
  private String permitNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isForeignerWorkPermit(" foreigners work permit number ");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @UnifiedSocialCreditCode
* Validation Rule: Unified Social Credit Code validation, validating Unified Social Credit Codes.
* Example Format: `91350100M000100Y43`
* Usage Example:
  ```java
  // Annotation-based usage
  @UnifiedSocialCreditCode
  private String creditCode;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isUnifiedSocialCreditCode("91350100M000100Y43");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChinesePhoneOrLandline
* Validation Rule: Chinese phone number validation, supporting mobile phones and landlines.
* Example Format: Supporting mobile phones and landlines
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePhoneOrLandline
  private String phoneNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChinesePhoneOrLandline("010-12345678");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChinesePhone
* Validation Rule: Chinese mobile phone number validation, validating Chinese mobile phone numbers.
* Example Format: 11-digit mobile phone numbers
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePhone
  private String phoneNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChinesePhone("13812345678");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseLandline
* Validation Rule: Chinese landline validation, validating Chinese landline numbers.
* Example Format: Supporting area codes and extensions
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseLandline
  private String phoneNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseLandline("010-12345678");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### Financial Validation Related

#### @BankCard
* Validation Rule: Bank card validation, using the Luhn algorithm to validate the validity of bank card numbers.
* Example Format:
  - Visa card number: `4012888888881881`
  - MasterCard card number: `5555555555554444`
  - Card number with spaces: `4012 8888 8888 1881`
  - Card number with hyphens: `4012-8888-8888-1881`
* Usage Example:
  ```java
  // Annotation-based usage
  @BankCard
  private String cardNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isBankCard("4012888888881881");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @CVV
* Validation Rule: CVV/CVC security code validation, validating the 3-digit or 4-digit security code on the back of credit cards.
* Example Format: `123`, `1234`
* Usage Example:
  ```java
  // Annotation-based usage
  @CVV
  private String cvv;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isCVV("123");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @IBAN
* Validation Rule: IBAN international bank account number validation, validating the format and check digits of international bank account numbers (IBAN).
* Example Format: `DE44500800000123456789`, `GB29NWBK60161331926819`
* Usage Example:
  ```java
  // Annotation-based usage
  @IBAN
  private String iban;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isIBAN("DE44500800000123456789");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @SWIFT
* Validation Rule: SWIFT/BIC code validation, validating the format of SWIFT/BIC bank codes, used to identify specific banks in international wire transfers.
* Example Format: `COBADEFF`, `DEUTDEFFXXX`
* Usage Example:
  ```java
  // Annotation-based usage
  @SWIFT
  private String swiftCode;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isSWIFT("COBADEFF");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @StockCode
* Validation Rule: Stock code validation, validating the format of stock codes from different exchanges.
* Supported exchanges and formats:
  - Shanghai Stock Exchange: 6-digit numbers starting with 6 (e.g.: 600000)
  - Shenzhen Stock Exchange: 6-digit numbers starting with 0, 3, or 4 (e.g.: 000001, 300001, 400001)
  - Hong Kong Stock Exchange: 4-5 digit numbers (e.g.: 00700, 3690)
  - New York Stock Exchange: 1-5 letters, may contain periods (e.g.: AAPL, BRK.A, BRK.B)
* Usage Example:
  ```java
  // Default supports all exchanges
  @StockCode
  private String stockCode;

  // Only supports Shanghai Stock Exchange
  @StockCode(exchanges = {StockCode.Exchange.SHANGHAI})
  private String shanghaiStock;

  // Supports Shanghai Stock Exchange and New York Stock Exchange
  @StockCode(exchanges = {StockCode.Exchange.SHANGHAI, StockCode.Exchange.NEW_YORK})
  private String mixedStock;

  // Only supports Hong Kong and US stocks
  @StockCode(exchanges = {StockCode.Exchange.HONG_KONG, StockCode.Exchange.NEW_YORK})
  private String internationalStock;
  ```

* When using chain calls, you can also specify the exchange scope:
  
  ```java
  // Chain call usage
  ValidX validator = ValidX.init();
  
  // Default supports all exchanges
  validator.isStockCode("600000");
  
  // Only validate Shanghai Stock Exchange
  validator.isStockCode("600000", StockCode.Exchange.SHANGHAI);
  
  // Validate Shanghai or Shenzhen exchanges
  validator.isStockCode("000001", StockCode.Exchange.SHANGHAI, StockCode.Exchange.SHENZHEN);
  
  // Validate Hong Kong or New York exchanges
  validator.isStockCode("00700", StockCode.Exchange.HONG_KONG, StockCode.Exchange.NEW_YORK);
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @TradeOrderNumber
* Validation Rule: Trade order number validation, validating the format of financial trade order numbers.
* Supported formats:
  - T prefix + 18-digit number format (e.g.: T123456789012345678)
  - Pure 18-digit number format (e.g.: 123456789012345678)
  - UUID format (with hyphens or without hyphens) (e.g.: 550e8400-e29b-41d4-a716-446655440000 or 550e8400e29b41d4a716446655440000)
* Usage Example:
  ```java
  // Default validates all supported formats
  @TradeOrderNumber
  private String orderNumber;
  ```

* Chain call usage:
  
  ```java
  // Chain call usage
  ValidX validator = ValidX.init();
  
  // Validate T prefix + 18-digit number format
  validator.isTradeOrderNumber("T123456789012345678");
  
  // Validate pure 18-digit number format
  validator.isTradeOrderNumber("123456789012345678");
  
  // Validate UUID format (with hyphens)
  validator.isTradeOrderNumber("550e8400-e29b-41d4-a716-446655440000");
  
  // Validate UUID format (without hyphens)
  validator.isTradeOrderNumber("550e8400e29b41d4a716446655440000");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @FinancialProductCode
* Validation Rule: Financial product code validation, validating the format of fund codes, bond codes, and other financial product codes.
* Supported product types and formats:
  - Fund products:
    - Shanghai Stock Exchange funds: 6-digit numbers starting with 5 (e.g.: 500001, 510000)
    - Shenzhen Stock Exchange funds: 6-digit numbers starting with 1 (e.g.: 100001, 150000)
  - Bond products:
    - Government bonds: 6-digit numbers starting with 10 (e.g.: 100001, 101234)
    - Corporate bonds: 6-digit numbers starting with 11 (e.g.: 110001, 111234)
    - Convertible bonds: 6-digit numbers starting with 12 (e.g.: 120001, 121234)
    - Company bonds: 6-digit numbers starting with 13 (e.g.: 130001, 131234)
* Usage Example:
  ```java
  // Default supports all product types
  @FinancialProductCode
  private String productCode;

  // Only supports fund products
  @FinancialProductCode(productTypes = {FinancialProductCode.ProductType.FUND})
  private String fundCode;

  // Only supports bond products
  @FinancialProductCode(productTypes = {FinancialProductCode.ProductType.BOND})
  private String bondCode;

  // Supports fund and bond products
  @FinancialProductCode(productTypes = {FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND})
  private String mixedProductCode;
  ```

* When using chain calls, you can also specify the product type scope:
  
  ```java
  // Chain call usage
  ValidX validator = ValidX.init();
  
  // Default supports all product types
  validator.isFinancialProductCode("500001");
  
  // Only validate fund products
  validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND);
  
  // Only validate bond products
  validator.isFinancialProductCode("100001", FinancialProductCode.ProductType.BOND);
  
  // Validate fund and bond products
  validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND);
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### Education/Professional Qualification/Certification Related Validation

#### @DegreeCertificate
* Validation Rule: Degree certificate number validation, validating the format of Chinese degree certificate numbers. Supports two formats:
  - Regular degree certificates: 16-digit number format
  - Special degree certificates: Format starting with specific letters followed by 16 characters
* Example Format: `1075522008000001`, `C1047642016057017`
* Usage Example:
  ```java
  // Annotation-based usage
  @DegreeCertificate
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isDegreeCertificate("1075522008000001");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Doctor
* Validation Rule: Doctor qualification certificate number validation, validating doctor qualification certificate numbers.
* Rule Description: Doctor qualification certificate numbers consist of 24 or 27 characters, including annual codes, provincial administrative region codes, practicing doctor level codes, practicing doctor category codes, and resident ID card numbers
* Example Format: `20251111014406081973100014` (24-digit) or `20251111014406081973100014123` (27-digit)
* Usage Example:
  ```java
  // Annotation-based usage
  @Doctor
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isDoctor("20251111014406081973100014");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Teacher
* Validation Rule: Teacher qualification certificate number validation, validating teacher qualification certificate numbers.
* Rule Description: Total of 17 digits, representing annual codes, provincial administrative region codes, certification agency codes, qualification type codes, gender codes, and serial number codes respectively
* Example Format: `20253412345678901`
* Usage Example:
  ```java
  // Annotation-based usage
  @Teacher
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isTeacher("20253412345678901");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Lawyer
* Validation Rule: Legal professional qualification certificate/lawyer practice certificate validation, validating legal professional qualification certificates or lawyer practice certificates.
* Format Description:
  * Lawyer practice certificate: 17-digit number, starting with 1, format `1 + province code(2 digits) + city code(2 digits) + year(4 digits) + category code(1 digit) + gender code(1 digit) + serial number(6 digits)`
  * Legal professional qualification certificate: 14-digit or 16-digit number
* Example Format:
  * Lawyer practice certificate: `11101201810123456` (1+Beijing 11+Chaoyang District 01+2018+Full-time lawyer 1+Male 0+Serial number 123456)
  * Legal professional qualification certificate: `2010130103210001` (Year 2010+Province code 13+City code 01+District code 03+Serial number 210001)
* Usage Example:
  ```java
  // Annotation-based usage
  @Lawyer
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isLawyer("11101201810123456");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @PMP
* Validation Rule: PMP certificate number validation, validating the format of PMP (Project Management Professional) certificate numbers
* Rule Description: PMP certificate numbers are typically 7 digits or combinations with specific prefixes
* Example Format: `1234567`, `PMP123456`
* Usage Example:
  ```java
  // Annotation-based usage
  @PMP
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isPMP("1234567");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Constructor
* Validation Rule: Constructor certificate number validation, validating the format of first-class/second-class constructor certificate numbers
* Rule Description: Constructor certificate numbers consist of one Chinese character and 12 Arabic numerals, totaling 13 digits
* Example Format: `京111050700001`, `鄂242050700001`
* Usage Example:
  ```java
  // Annotation-based usage
  @Constructor
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isConstructor("京111050700001");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Accountant
* Validation Rule: Accounting qualification certificate number validation, validating the format of accounting qualification certificate numbers
* Rule Description: Accounting qualification certificate numbers consist of 11 digits, containing year codes, region codes, and other information
* Example Format: `21010203451`, `22310512342`
* Usage Example:
  ```java
  // Annotation-based usage
  @Accountant
  private String certificateNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isAccountant("21010203451");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### Network Related

#### @Domain
* Validation Rule: Domain validation, validating domain format.
* Example Format: `example.com`, `www.example.com`
* Usage Example:
  ```java
  // Annotation-based usage
  @Domain
  private String domain;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isDomain("example.com");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Ip
* Validation Rule: IP address validation, supporting IPv4 and IPv6 address validation with configurable version parameter.
* Supported Versions:
  - `Ip.IpVersion.V4`: Validate IPv4 addresses only (e.g., `192.168.1.1`)
  - `Ip.IpVersion.V6`: Validate IPv6 addresses only (e.g., `2001:0db8:85a3::8a2e:0370:7334`)
  - `Ip.IpVersion.ANY`: Validate both IPv4 and IPv6 addresses (default)
* Example Formats:
  - IPv4: `192.168.1.1`, `10.0.0.1`
  - IPv6: `2001:0db8:85a3:0000:0000:8a2e:0370:7334`, `::1`, `fe80::1`
* Usage Example:
  ```java
  // Annotation-based usage - supports both IPv4 and IPv6 (default)
  @Ip
  private String ipAddress;

  // Validate IPv4 addresses only
  @Ip(version = Ip.IpVersion.V4)
  private String ipv4Address;

  // Validate IPv6 addresses only
  @Ip(version = Ip.IpVersion.V6)
  private String ipv6Address;

  // Chain call usage
  ValidX validator = ValidX.init();

  // Validate any IP address (default)
  validator.isIp("192.168.1.1");

  // Validate IPv4 addresses only
  validator.isIp("192.168.1.1", Ip.IpVersion.V4);

  // Validate IPv6 addresses only
  validator.isIp("2001:0db8:85a3::8a2e:0370:7334", Ip.IpVersion.V6);

  // Support both IPv4 and IPv6
  validator.isIp("192.168.1.1", Ip.IpVersion.ANY);
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Mac
* Validation Rule: MAC address validation, validating MAC addresses.
* Example Format: `00:1A:2B:3C:4D:5E`, `00-1A-2B-3C-4D-5E`
* Usage Example:
  ```java
  // Annotation-based usage
  @Mac
  private String macAddress;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isMac("00:1A:2B:3C:4D:5E");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Url
* Validation Rule: URL address validation, validating URL address format.
* Example Format: `http://example.com`, `https://example.com/path`
* Usage Example:
  ```java
  // Annotation-based usage
  @Url
  private String url;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isUrl("http://example.com");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Email
* Validation Rule: Email address validation, validating email address format.
* Example Format: `test@example.com`, `user.name@domain.co.uk`
* Usage Example:
  ```java
  // Annotation-based usage
  @Email
  private String email;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isEmail("test@example.com");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @SubnetMask
* Validation Rule: Subnet mask validation, validating subnet mask format.
* Example Format: `255.255.255.0`, `255.0.0.0`
* Usage Example:
  ```java
  // Annotation-based usage
  @SubnetMask
  private String subnetMask;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isSubnetMask("255.255.255.0");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @Port
* Validation Rule: Port number validation, validating whether the port number is within the range of 0-65535.
* Example Format: Integer between 0-65535
* Usage Example:
  ```java
  // Annotation-based usage
  @Port
  private String port;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isPort("8080");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### China-specific Validation

#### @ChineseLicensePlate
* Validation Rule: Chinese license plate validation, validating Chinese license plate numbers.
* Example Format: `京A12345`, `京A12345D`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseLicensePlate
  private String licensePlate;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseLicensePlate("京A12345");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChinesePatent
* Validation Rule: Chinese patent number validation, validating Chinese patent numbers.
* Example Format: `ZL2013106997442`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePatent
  private String patentNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChinesePatent("ZL2013106997442");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseTrademark
* Validation Rule: Chinese trademark registration number validation, validating Chinese trademark registration numbers.
* Example Format: `1234567`, `第1234567号`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseTrademark
  private String trademarkNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseTrademark("1234567");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @SoftwareCopyright
* Validation Rule: Computer software copyright registration number validation, validating computer software copyright registration numbers.
* Example Format: `软著登字第2023001234号`
* Usage Example:
  ```java
  // Annotation-based usage
  @SoftwareCopyright
  private String copyrightNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isSoftwareCopyright("软著登字第2023001234号");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @WorkCopyright
* Validation Rule: General work copyright registration number validation, validating general work copyright registration numbers.
* Example Format: `作登字22-2023-A-0018号`
* Usage Example:
  ```java
  // Annotation-based usage
  @WorkCopyright
  private String copyrightNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isWorkCopyright("作登字22-2023-A-0018号");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ChineseZipCode
* Validation Rule: Chinese postal code validation, validating Chinese postal codes.
* Example Format: `100000`, `200000`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseZipCode
  private String zipCode;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isChineseZipCode("100000");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @DrugApproval
* Validation Rule: Validate whether the string is a valid Chinese drug approval number. Drug approval numbers are the numbers approved by the national drug regulatory authorities for pharmaceutical manufacturers to produce drugs
* Example Format: 国药准字H20210039, 国药准字ZC20171003, 国药准字HJ20233150
* Usage Example:
  ```java
  // Annotation-based usage
  @DrugApproval
  private String approvalNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isDrugApproval("国药准字H20210039");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @DrugCode
* Validation Rule: Validate whether the string is a valid Chinese drug code. Drug codes start with 69, are 20 digits, and the last digit is the GS1 check digit
* Example Format: 69012345678901234563, 69123456789012345678
* Usage Example:
  ```java
  // Annotation-based usage
  @DrugCode
  private String drugCode;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isDrugCode("69012345678901234563");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @MedicalDeviceRegistration
* Validation Rule: Medical device registration certificate number validation, used to validate the format of Chinese medical device registration certificate numbers.
* Example Format: `国械注准20243010001`, `粤械注准20242020002`, `国械注进20242030003`
* Usage Example:
  ```java
  // Annotation-based usage
  @MedicalDeviceRegistration
  private String registrationNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isMedicalDeviceRegistration("国械注准20243010001");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @QQ
* Validation Rule: QQ number validation, validating QQ numbers.
* Example Format: `123456789`
* Usage Example:
  ```java
  // Annotation-based usage
  @QQ
  private String qqNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isQQ("123456789");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @WeChat
* Validation Rule: WeChat account validation, validating WeChat account format.
* Rule Description:
  * Length of 6-20 characters
  * Must start with a letter
  * Can only contain letters, numbers, underscores, and hyphens
* Example Format: `wechat123`, `WeChat_123`, `WeChat-123`
* Usage Example:
  ```java
  // Annotation-based usage
  @WeChat
  private String wechatId;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isWeChat("wechat123");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### Automotive Related Validation

#### @VIN
* Validation Rule: Validate vehicle identification number (VIN) format and check digits.
* Example Format: `WP0AJ2972LL122844`
* Usage Example:
  ```java
  // Annotation-based usage
  @VIN
  private String vin;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isVIN("WP0AJ2972LL122844");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @VehicleEngine
* Validation Rule: Validate vehicle engine code format.
* Example Format: `123456`, `ABC123`, `123ABC456`
* Usage Example:
  ```java
  // Annotation-based usage
  @VehicleEngine
  private String engineCode;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isVehicleEngine("123456");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### Book Related Validation

#### @ISBN
* Validation Rule: International Standard Book Number validation, supporting 10-digit and 13-digit ISBN formats.
* Example Format: `9780306406157` (13-digit) or `0306406152` (10-digit)
* Usage Example:
  ```java
  // Annotation-based usage
  @ISBN
  private String isbn;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isISBN("9780306406157");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ISSN
* Validation Rule: International Standard Serial Number validation, supporting 8-digit ISSN format.
* Example Format: `0317-8471` or `03178471`
* Usage Example:
  ```java
  // Annotation-based usage
  @ISSN
  private String issn;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isISSN("0317-8471");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @DOI
* Validation Rule: Digital Object Identifier validation, used for unique identification of digital resources, widely used in academic publications.
* Example Format: Starting with "10.", such as `10.1000/182`
* Usage Example:
  ```java
  // Annotation-based usage
  @DOI
  private String doi;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isDOI("10.1000/182");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @CLC
* Validation Rule: Validate whether the string is a valid Chinese Library Classification (CLC) number. The Chinese Library Classification is a book classification system widely used in Chinese libraries
* Example Format: A, B, TP, TP3, TP311, TP311.1, TP311.138, TP311.138.S6, O175.2, R329.2, F272.3
* Usage Example:
  ```java
  // Annotation-based usage
  @CLC
  private String clcNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isCLC("TP311.138");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @DDC
* Validation Rule: Validate whether the string is a valid Dewey Decimal Classification (DDC) number. The Dewey Decimal Classification is a classification system widely used in libraries
* Example Format: 000, 100, 200, ..., 999, 510, 516.3, 330.94
* Usage Example:
  ```java
  // Annotation-based usage
  @DDC
  private String ddcNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isDDC("516.3");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @ORCID
* Validation Rule: Open Researcher and Contributor ID validation, used to uniquely identify academic authors and contributors.
* Example Format: `0000-0002-1825-0097` or `0000000218250097`
* Usage Example:
  ```java
  // Annotation-based usage
  @ORCID
  private String orcidId;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isORCID("0000-0002-1825-0097");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

#### @IPC
* Validation Rule: International Patent Classification number validation, used to identify patent technical fields.
* Example Format: `A01B1/00`, `A01B1/01`, `H01B12/00`
* Usage Example:
  ```java
  // Annotation-based usage
  @IPC
  private String ipcNumber;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isIPC("A01B1/00");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

### Mobile Phone Related Validation

#### @IMEI
* Validation Rule: IMEI validation, validating whether the string is a valid International Mobile Equipment Identity.
* Example Format: `123412341234564` or `123412-341234564`
* Usage Example:
  ```java
  // Annotation-based usage
  @IMEI
  private String imei;

  // Chain call usage
  ValidX validator = ValidX.init();
  validator.isIMEI("123412341234564");
  ```

[↑ Back to Quick Reference](#quick-reference-table)

## More Validation Annotations
If you need more validations, you can contact us for expansion and support. Contact information:

Sharif

[vipxieliang@126.com](mailto:vipxieliang@126.com)

## Contribution
You can participate in this project in various ways, not limited to the following:

* Feedback on problems encountered during use
* Share successful experiences
* Update and improve documentation
* Solve and discuss issues