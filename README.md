# Language

[中文](README.cn.md)

[English](README.md)

<div align="center">

# ValidX

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vipxieliang/validx?color=blue)](https://central.sonatype.com/artifact/io.github.vipxieliang/validx)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/vipxieliang/ValidX/pulls)

**A comprehensive Java validation library designed for Chinese business scenarios**

[Quick Start](#-5-minute-quick-start) | [Why ValidX?](#-why-choose-validx) | [Documentation](#supported-validation-annotations) | [Contributing](#contribution)

</div>

---

## Introduction

ValidX is an open-source Java validation library focused on providing comprehensive validation solutions for Chinese business scenarios. Built on JSR-380 standards with 90+ specialized annotations for Chinese identity cards, phone numbers, bank cards, and more.

## ✨ Why Choose ValidX?

### 🇨🇳 **Built for China**
- **90+ Chinese-specific validators**: ID cards, phone numbers, bank cards, social credit codes, license plates, and more
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

## 🚀 5-Minute Quick Start

### Step 1: Add Dependency

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.0.0</version>
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
        ValidaX validator = ValidaX.init()
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

## Installation and Usage

### Maven Dependency

Just add a single dependency to use all features:

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Annotation-based Usage

```java
public class UserDTO {
    @ChineseIdCard
    private String idCard;

    @Email
    private String email;

    @Url
    private String website;

    @Password
    private String password;

    @Password(minLength = 6)
    private String password2;

    @Password(minLength = 6, requireSpecialChar = false)
    private String password3;

    @StartsWith(startsWith = "USER_")
    private String userCode;

    @In({"admin", "user", "guest"})
    private String role;

    @In({"admin", "user", "guest"})
    private List<String> roles;

    // getters and setters
}
```

### Chain Call Usage

```java
@Test
public void testInstanceMethodWithDirectValue() {
    // Test using instance method to directly pass values for validation
    ValidaX chain = ValidaX.init(); // Create an empty chain
    chain = chain.isChineseIdCard((Object)"440608197310039910")
            .isUrl((Object)"http://example.com")
            .isIp((Object)"192.168.1.1");

    assertTrue(chain.passed(), "All validations should pass");
}
```

```java
@Test
public void testFutureDateWithIncludeToday() {
    // Test using isFutureDate method including today's date
    ValidaX chain = ValidaX.init(); // Create an empty chain
    chain = chain.isFutureDate((Object)LocalDate.now().toString(), true)
            .isFutureDate((Object)LocalDate.now().plusDays(1).toString());

    assertTrue(chain.passed(), "All validations should pass");
}
```

## Multilingual Support

ValidX supports multilingual error messages, which can be used in the following ways:

```java
// Use system default language
ValidaX chain1 = ValidaX.init()
        .isEmail("invalid-email");

// Use Chinese
ValidaX chain2 = ValidaX.init()
        .withLocale(Locale.SIMPLIFIED_CHINESE)
        .isEmail("invalid-email");

// Use English
ValidaX chain3 = ValidaX.init()
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
ValidaX chain = ValidaX.init()
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

Chain validation (`ValidaX.init()`) has the same default behavior as annotation-based: **null and empty strings pass validation**.

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
        ValidaX validator = ValidaX.init();

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

ValidaX now supports flexible configuration for handling null/empty values through both global configuration and local state control.

##### Global Configuration

You can set global validation requirements using `ValidXConfig`:

```java
// Create validator with global NOT_NULL requirement
ValidaX validator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL);

// All validation methods will now reject null values
validator.isEmail(email)  // Fails if email is null
         .isPhone(phone); // Fails if phone is null

// Create validator with global NOT_EMPTY requirement
ValidaX validator2 = ValidaX.init()
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
ValidaX validator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email)
    .isPhone(phone)
    .allowNull().isQQ(qq);  // Use local method for exceptions

// ⚠️ Not recommended: Multiple config() calls in the middle
ValidaX validator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email)
    .config(ValidXConfig.DEFAULT)  // Confusing: hard to track config changes
    .isPhone(phone);

// ✅ If you need different configs, create separate validators
ValidaX strictValidator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail(email1)
    .isPhone(phone1);

ValidaX lenientValidator = ValidaX.init()
    .config(ValidXConfig.DEFAULT)
    .isEmail(email2)
    .isPhone(phone2);
```

##### Local State Control

You can override global configuration for specific fields using local state methods:

```java
ValidaX validator = ValidaX.init()
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
ValidaX validator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_EMPTY);  // Global: reject null and empty

validator.allowNull().isEmail(email);  // Local allowNull() overrides global
```

**Priority:** Local State > Global Config > Default Behavior

##### Practical Examples

**Example 1: API Request Validation**

```java
public void validateUserRegistration(Map<String, Object> request) {
    ValidaX validator = ValidaX.init()
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
    ValidaX validator = ValidaX.init();  // Default: allow null/empty

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
    ValidaX validator = ValidaX.init()
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
ValidaX validator = ValidaX.init();

validator.notNull().isEmail(email1)   // notNull applies to email1
         .isEmail(email2)              // email2 uses default behavior (state reset)
         .notEmpty().isPhone(phone);   // notEmpty applies to phone only
```

##### Error Messages with Field Labels

When using `.field("label")`, error messages will include the custom label:

```java
ValidaX validator = ValidaX.init();

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

**ValidaX instances are not thread-safe.** Each validation should create a new instance:

```java
// ❌ Wrong: Sharing instance across threads
private static final ValidaX VALIDATOR = ValidaX.init();

public void validate(User user) {
    VALIDATOR.isEmail(user.getEmail());  // Not thread-safe!
}

// ✅ Correct: Create new instance per validation
public void validate(User user) {
    ValidaX validator = ValidaX.init()
        .isEmail(user.getEmail())
        .isPhone(user.getPhone());

    if (!validator.isValid()) {
        throw new ValidationException(validator.getErrorMessage());
    }
}
```

**Why?** ValidaX uses internal mutable state (local requirement flags, field labels, error lists) that is modified during the validation chain. Sharing instances across threads can lead to race conditions and incorrect validation results.

**Thread-safe components:**
- `ValidXConfig` objects are immutable and can be safely shared
- Individual validator classes (e.g., `ChineseIdCardValidator`) are stateless and can be reused

This design follows the same pattern as other fluent APIs like `StringBuilder`, Java 8 `Stream`, and Lombok `Builder` - they are meant to be used in a "create-use-discard" pattern.

## Supported Validation Annotations

ValidX provides rich validation annotations covering various scenarios. The following are all currently supported validation annotations and their function descriptions:

### Quick Reference Table

Click on the annotation name to jump to its detailed documentation.

| Category | Annotation | Description |
|----------|------------|-------------|
| **Basic Validation** | [@Alpha](#alpha) | Pure English letter validation |
| **Basic Validation** | [@AlphaDash](#alphadash) | Alphanumeric with underscore and hyphen |
| **Basic Validation** | [@AlphaNumber](#alphanumber) | Alphanumeric combination |
| **Basic Validation** | [@Chinese](#chinese) | Pure Chinese character validation |
| **Basic Validation** | [@ChineseAlpha](#chinesealpha) | Chinese characters and letters |
| **Basic Validation** | [@ChineseAlphaNum](#chinesealphanum) | Chinese characters, letters and numbers |
| **Basic Validation** | [@ChineseAlphaDash](#chinesealphadash) | Chinese, letters, numbers, underscore, hyphen |
| **Basic Validation** | [@Lower](#lower) | Lowercase character validation |
| **Basic Validation** | [@Upper](#upper) | Uppercase character validation |
| **Basic Validation** | [@Xdigit](#xdigit) | Hexadecimal string validation |
| **Basic Validation** | [@Longitude](#longitude) | Longitude validation (-180 to 180) |
| **Basic Validation** | [@Latitude](#latitude) | Latitude validation (-90 to 90) |
| **Basic Validation** | [@GeoPoint](#geopoint) | Geographic coordinate pair validation |
| **Basic Validation** | [@FutureDate](#futuredate) | Future date validation |
| **Basic Validation** | [@PastDate](#pastdate) | Past date validation |
| **Basic Validation** | [@HourMinute](#hourminute) | Hour:minute format (HH:mm) |
| **Basic Validation** | [@HourMinuteSecond](#hourminutesecond) | Hour:minute:second format (HH:mm:ss) |
| **Basic Validation** | [@Timestamp](#timestamp) | Unix timestamp validation |
| **Basic Validation** | [@CronExpression](#cronexpression) | Cron expression validation |
| **Basic Validation** | [@Duration](#duration) | Duration format validation |
| **Basic Validation** | [@ExpressNumber](#expressnumber) | Express tracking number validation |
| **Basic Validation** | [@StartsWith](#startswith) | String prefix validation |
| **Basic Validation** | [@EndsWith](#endswith) | String suffix validation |
| **Basic Validation** | [@In](#in) | Value in specified list |
| **Basic Validation** | [@NotIn](#notin) | Value not in specified list |
| **Basic Validation** | [@Enum](#enum) | Enumeration value validation |
| **Basic Validation** | [@Color](#color) | Color format (HEX/RGB/RGBA) |
| **Basic Validation** | [@Password](#password) | Password strength validation |
| **Basic Validation** | [@UUID](#uuid) | UUID format validation |
| **Basic Validation** | [@Base64](#base64) | Base64 encoding validation |
| **Basic Validation** | [@JSON](#json) | JSON format validation |
| **Basic Validation** | [@JWT](#jwt) | JWT token format validation |
| **Basic Validation** | [@SemVer](#semver) | Semantic versioning validation |
| **Basic Validation** | [@FileExtension](#fileextension) | File extension validation |
| **Basic Validation** | [@FileSize](#filesize) | File size range validation |
| **Basic Validation** | [@Age](#age) | Age validation from birth date or ID |
| **Basic Validation** | [@Port](#port) | Port number validation (0-65535) |
| **Identity Validation** | [@ChineseIdCard](#chineseidcard) | Chinese ID card validation |
| **Identity Validation** | [@ChinesePassport](#chinesepassport) | Chinese passport validation |
| **Identity Validation** | [@ChineseMilitaryOfficer](#chinesemilitaryofficer) | Military officer certificate |
| **Identity Validation** | [@ChineseSoldier](#chinesesoldier) | Soldier certificate validation |
| **Identity Validation** | [@ForeignerPermanentResidenceIdentity](#foreignerpermanentresidenceidentity) | Foreigner permanent residence ID |
| **Identity Validation** | [@HKMacauResidence](#hkmacauresidence) | HK/Macau residence permit |
| **Identity Validation** | [@HKMacauPass](#hkmacaupass) | HK/Macau travel permit |
| **Identity Validation** | [@TaiwanResidence](#taiwanresidence) | Taiwan residence permit |
| **Identity Validation** | [@TaiwanPass](#taiwanpass) | Taiwan travel permit |
| **Identity Validation** | [@ForeignerWorkPermit](#foreignerworkpermit) | Foreigner work permit |
| **Identity Validation** | [@UnifiedSocialCreditCode](#unifiedsocialcreditcode) | Unified Social Credit Code |
| **Identity Validation** | [@ChinesePhone](#chinesephone) | Chinese mobile phone |
| **Identity Validation** | [@ChineseLandline](#chineselandline) | Chinese landline |
| **Identity Validation** | [@ChinesePhoneOrLandline](#chinesephoneorlandline) | Chinese phone or landline |
| **Identity Validation** | [@PhoneNumber](#phonenumber) | International phone number |
| **Identity Validation** | [@Email](#email) | Email address validation |
| **Financial Validation** | [@BankCard](#bankcard) | Bank card number (Luhn) |
| **Financial Validation** | [@CVV](#cvv) | CVV/CVC security code |
| **Financial Validation** | [@IBAN](#iban) | IBAN account number |
| **Financial Validation** | [@SWIFT](#swift) | SWIFT/BIC code |
| **Financial Validation** | [@StockCode](#stockcode) | Stock code validation |
| **Financial Validation** | [@TradeOrderNumber](#tradeordernumber) | Trade order number |
| **Financial Validation** | [@FinancialProductCode](#financialproductcode) | Financial product code |
| **Education/Professional Qualification** | [@DegreeCertificate](#degreecertificate) | Degree certificate number |
| **Education/Professional Qualification** | [@Doctor](#doctor) | Doctor qualification |
| **Education/Professional Qualification** | [@Teacher](#teacher) | Teacher qualification |
| **Education/Professional Qualification** | [@Lawyer](#lawyer) | Legal professional qualification |
| **Education/Professional Qualification** | [@PMP](#pmp) | PMP certificate |
| **Education/Professional Qualification** | [@Constructor](#constructor) | Constructor certificate |
| **Education/Professional Qualification** | [@Accountant](#accountant) | Accountant certificate |
| **Network Validation** | [@Domain](#domain) | Domain name validation |
| **Network Validation** | [@Ip](#ip) | IP address (IPv4/IPv6) |
| **Network Validation** | [@Mac](#mac) | MAC address validation |
| **Network Validation** | [@Url](#url) | URL address validation |
| **Network Validation** | [@SubnetMask](#subnetmask) | Subnet mask validation |
| **China-Specific Validation** | [@ChineseLicensePlate](#chineselicenseplate) | Chinese license plate |
| **China-Specific Validation** | [@ChinesePatent](#chinesepatent) | Chinese patent number |
| **China-Specific Validation** | [@ChineseTrademark](#chinesetrademark) | Chinese trademark registration |
| **China-Specific Validation** | [@SoftwareCopyright](#softwarecopyright) | Software copyright registration |
| **China-Specific Validation** | [@WorkCopyright](#workcopyright) | Work copyright registration |
| **China-Specific Validation** | [@ChineseZipCode](#chinesezipcode) | Chinese postal code |
| **China-Specific Validation** | [@DrugApproval](#drugapproval) | Drug approval number |
| **China-Specific Validation** | [@DrugCode](#drugcode) | Drug code validation |
| **China-Specific Validation** | [@MedicalDeviceRegistration](#medicaldeviceregistration) | Medical device registration |
| **China-Specific Validation** | [@QQ](#qq) | QQ number validation |
| **China-Specific Validation** | [@WeChat](#wechat) | WeChat ID validation |
| **Automotive Validation** | [@VIN](#vin) | Vehicle Identification Number |
| **Automotive Validation** | [@VehicleEngine](#vehicleengine) | Vehicle engine number |
| **Book-Related Validation** | [@ISBN](#isbn) | ISBN book number |
| **Book-Related Validation** | [@ISSN](#issn) | ISSN serial number |
| **Book-Related Validation** | [@DOI](#doi) | DOI identifier |
| **Book-Related Validation** | [@CLC](#clc) | Chinese Library Classification |
| **Book-Related Validation** | [@DDC](#ddc) | Dewey Decimal Classification |
| **Book-Related Validation** | [@ORCID](#orcid) | ORCID researcher ID |
| **Book-Related Validation** | [@IPC](#ipc) | International Patent Classification |
| **Mobile Device Validation** | [@IMEI](#imei) | IMEI device number |

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
  ValidaX validator = ValidaX.init();
  validator.isAlpha("abcDEF");
  ```

#### @AlphaDash
* Validation Rule: Alphanumeric underscore hyphen validation, allowing English letters, numbers, underscores, and hyphens.
* Example Format: `abc-123_def`
* Usage Example:
  ```java
  // Annotation-based usage
  @AlphaDash
  private String code;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isAlphaDash("abc-123_def");
  ```

#### @AlphaNumber
* Validation Rule: Alphanumeric combination validation, only allowing English letters and numbers.
* Example Format: `abc123`
* Usage Example:
  ```java
  // Annotation-based usage
  @AlphaNumber
  private String code;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isAlphaNumber("abc123");
  ```

#### @Chinese
* Validation Rule: Pure Chinese character validation, only allowing Chinese characters (Unicode Chinese characters).
* Example Format: `汉字`
* Usage Example:
  ```java
  // Annotation-based usage
  @Chinese
  private String name;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChinese("汉字");
  ```

#### @ChineseAlpha
* Validation Rule: Chinese character letter validation, allowing Chinese characters and English letters.
* Example Format: `汉字abc`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseAlpha
  private String name;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseAlpha("汉字abc");
  ```

#### @ChineseAlphaNum
* Validation Rule: Chinese character letter number validation, allowing Chinese characters, English letters, and numbers.
* Example Format: `汉字abc123`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseAlphaNum
  private String code;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseAlphaNum("汉字abc123");
  ```

#### @ChineseAlphaDash
* Validation Rule: Chinese character letter number underscore hyphen validation, allowing Chinese characters, English letters, numbers, underscores, and hyphens.
* Example Format: `汉字abc-123_def`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseAlphaDash
  private String code;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseAlphaDash("汉字abc-123_def");
  ```

#### @Longitude
* Validation Rule: Longitude validation, validating whether the longitude value is between -180 and 180.
* Example Format: `0`, `116.4074`, `-116.4074`, `180`, `-180`
* Usage Example:
  ```java
  // Annotation-based usage
  @Longitude
  private String longitude;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isLongitude("116.4074");
  ```

#### @Latitude
* Validation Rule: Latitude validation, validating whether the latitude value is between -90 and 90.
* Example Format: `0`, `39.9042`, `-39.9042`, `90`, `-90`
* Usage Example:
  ```java
  // Annotation-based usage
  @Latitude
  private String latitude;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isLatitude("39.9042");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isGeoPoint("116.4074,39.9042");  // Default: longitude first
  validator.isGeoPoint("39.9042,116.4074", true);  // Latitude first
  validator.isGeoPoint("116.4074,39.9042", false, GeoPoint.SeparatorType.COMMA);  // Specify separator
  ```

#### @FutureDate
* Validation Rule: Future date validation, validating whether the date is a future date.
* Example Format: `2025-12-31`, `2025-12-31 12:00:00`
* Usage Example:
  ```java
  // Annotation-based usage
  @FutureDate
  private String date;
  // Or include today
  @FutureDate(includeToday = true)
  private String deadline;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isFutureDate("2025-12-31");
  // Or include today
  validator.isFutureDate("2025-12-31", true);
  ```

#### @PastDate
* Validation Rule: Past date validation, validating whether the date is a past date.
* Example Format: `2020-01-01`, `2020-01-01 12:00:00`
* Usage Example:
  ```java
  // Annotation-based usage
  @PastDate
  private String date;
  // Or include today
  @PastDate(includeToday = true)
  private String birthDate;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isPastDate("2020-01-01");
  // Or include today
  validator.isPastDate("2020-01-01", true);
  ```

#### @HourMinute
* Validation Rule: Hour minute time format validation, validating whether the time format is HH:mm.
* Example Format: `23:20`, `09:30`
* Usage Example:
  ```java
  // Annotation-based usage
  @HourMinute
  private String time;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isHourMinute("23:20");
  ```

#### @HourMinuteSecond
* Validation Rule: Hour minute second time format validation, validating whether the time format is HH:mm:ss.
* Example Format: `23:50:29`, `09:30:05`
* Usage Example:
  ```java
  // Annotation-based usage
  @HourMinuteSecond
  private String time;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isHourMinuteSecond("23:50:29");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isIn("value1", new String[]{"value1", "value2"});
  
  // Chain call usage - collection validation
  List<String> roles = Arrays.asList("admin", "user");
  validator.isIn(roles, new String[]{"admin", "user", "guest"});
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isNotIn("value3", new String[]{"value1", "value2"});
  
  // Chain call usage - collection validation
  List<String> roles = Arrays.asList("user", "guest");
  validator.isNotIn(roles, new String[]{"admin", "root", "superuser"});
  ```

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
  ValidaX validator = ValidaX.init();
  
  // Default case insensitive
  validator.isFileExtension("document.xls", new String[]{"XLS"});
  
  // Explicitly specify case insensitive
  validator.isFileExtension("document.xls", new String[]{"XLS"}, true);
  
  // Case sensitive
  validator.isFileExtension("document.xls", new String[]{"XLS"}, false);
  ```

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
  ValidaX validator = ValidaX.init();

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

#### @Lower
* Validation Rule: Lowercase character validation, only allowing lowercase English letters.
* Example Format: `abcdef`
* Usage Example:
  ```java
  // Annotation-based usage
  @Lower
  private String text;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isLower("abcdef");
  ```

#### @Upper
* Validation Rule: Uppercase character validation, only allowing uppercase English letters.
* Example Format: `ABCDEF`
* Usage Example:
  ```java
  // Annotation-based usage
  @Upper
  private String text;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isUpper("ABCDEF");
  ```

#### @Xdigit
* Validation Rule: Hexadecimal string validation, only allowing hexadecimal characters (0-9, a-f, A-F).
* Example Format: `0a1B2c3D`
* Usage Example:
  ```java
  // Annotation-based usage
  @Xdigit
  private String hex;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isXdigit("0a1B2c3D");
  ```

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
  ValidaX validator = ValidaX.init();
  
  // Use default rules (minimum length 8 characters, must include uppercase and lowercase letters, digits, and special characters)
  validator.isPassword("MyPassword123!");
  
  // Specify minimum length
  validator.isPassword("mypassword123", 8);
  
  // Fully customized rules (minimum length 8 characters, do not require uppercase letters, require lowercase letters and digits, do not require special characters)
  validator.isPassword("mypassword123", 8, false, true, true, false);
  ```

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
  ValidaX validator = ValidaX.init();

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
  ValidaX validator = ValidaX.init();

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
  ValidaX validator = ValidaX.init();

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
  ValidaX validator = ValidaX.init();

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
  ValidaX validator = ValidaX.init();

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
  ValidaX validator = ValidaX.init();
  validator.isJWT("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U");
  ```
* Notes:
  - This validator only verifies the basic format of JWT (three-part structure and Base64URL encoding)
  - Does not verify signature validity (requires secret key)
  - Does not verify expiration time and other claims
  - Common use cases: API authentication, Single Sign-On (SSO), information exchange

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
  ValidaX validator = ValidaX.init();
  validator.isSemVer("1.0.0");
  validator.isSemVer("2.1.3-beta.1");

  // Chain call usage - Allow v prefix
  ValidaX validator2 = ValidaX.init();
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
  ValidaX validator = ValidaX.init();
  validator.isTimestamp("1700000000");
  validator.isTimestamp("1700000000000");

  // Chain call usage - Specify unit
  ValidaX validator2 = ValidaX.init();
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
  ValidaX validator = ValidaX.init();
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
  ValidaX validator = ValidaX.init();

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
  ValidaX validator = ValidaX.init();

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

#### @StartsWith
* Validation Rule: Prefix validation, validating whether the string starts with the specified prefix.
* Example Format: Starting with specified string
* Usage Example:
  ```java
  // Annotation-based usage
  @StartsWith(startsWith = "prefix")
  private String code;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isStartsWith("prefix_string", new String[]{"prefix"});
  ```

#### @EndsWith
* Validation Rule: Suffix validation, validating whether the string ends with the specified suffix.
* Example Format: Ending with specified string
* Usage Example:
  ```java
  // Annotation-based usage
  @EndsWith(endsWith = "suffix")
  private String code;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isEndsWith("string_suffix", new String[]{"suffix"});
  ```

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
  ValidaX validator = ValidaX.init();
  
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

#### @Color
* Validation Rule: Color format validation, validating whether the string is a valid HEX color value, supporting #FFF or #FFFFFF format.
* Example Format: `#FF0000`, `#F00`, `#ffffff`, `#000`
* Usage Example:
  ```java
  // Annotation-based usage
  @Color
  private String color;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isColor("#FF0000");
  ```

### Identity Verification Related

#### @ChineseIdCard
* Validation Rule: Mainland China ID card number validation, supporting 18-digit and 15-digit ID card numbers.
* Example Format: `11010119900307211X` (18-digit) or `11010119900307211` (15-digit)
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseIdCard
  private String idCard;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseIdCard("11010119900307211X");
  ```

#### @ChinesePassport
* Validation Rule: Chinese passport number validation, supporting various types of Chinese passport numbers.
* Example Format: `G12345678`, `E12345678`, `S12345678`, `D1234567`, `P1234567`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePassport
  private String passportNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChinesePassport("G12345678");
  ```

#### @ChineseMilitaryOfficer
* Validation Rule: Chinese military officer certificate validation, supporting various types of Chinese military officer certificates.
* Example Format: `军字第1234567号`, `海字第1234567号`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseMilitaryOfficer
  private String certificateNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseMilitaryOfficer("军字第1234567号");
  ```

#### @ChineseSoldier
* Validation Rule: Chinese soldier certificate validation, supporting various types of Chinese soldier certificates.
* Example Format: `沈字第0100000号`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseSoldier
  private String certificateNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseSoldier("沈字第0100000号");
  ```

#### @ForeignerPermanentResidenceIdentity
* Validation Rule: Foreigner permanent residence identity card validation, validating foreigner permanent residence identity card numbers.
* Example Format: `911124198108030028`
* Usage Example:
  ```java
  // Annotation-based usage
  @ForeignerPermanentResidenceIdentity
  private String identityNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isForeignerPermanentResidenceIdentity("911124198108030028");
  ```

#### @HKMacauResidence
* Validation Rule: Hong Kong and Macau residents' residence permit validation, validating Hong Kong and Macau residents' residence permit numbers.
* Example Format: `810000000000000001`, `82000000000000000X`
* Usage Example:
  ```java
  // Annotation-based usage
  @HKMacauResidence
  private String residenceNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isHKMacauResidence("810000000000000001");
  ```

#### @HKMacauPass
* Validation Rule: Hong Kong and Macau residents' travel permit to Mainland China (Home Return Permit) validation, validating Hong Kong and Macau residents' travel permit numbers.
* Example Format: `H1234567800`, `M1234567801`
* Usage Example:
  ```java
  // Annotation-based usage
  @HKMacauPass
  private String passNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isHKMacauPass("H1234567800");
  ```

#### @TaiwanResidence
* Validation Rule: Taiwan residents' residence permit validation, validating Taiwan residents' residence permit numbers.
* Example Format: `830000000000000001`
* Usage Example:
  ```java
  // Annotation-based usage
  @TaiwanResidence
  private String residenceNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isTaiwanResidence("830000000000000001");
  ```

#### @TaiwanPass
* Validation Rule: Taiwan residents' travel permit to Mainland China (Taiwan Compatriot Pass) validation, validating Taiwan residents' travel permit numbers.
* Example Format: `1234567800`
* Usage Example:
  ```java
  // Annotation-based usage
  @TaiwanPass
  private String passNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isTaiwanPass("1234567800");
  ```

#### @ForeignerWorkPermit
* Validation Rule: Foreigner work permit validation, validating foreigner work permit numbers.
* Example Format: Combination of letters and numbers
* Usage Example:
  ```java
  // Annotation-based usage
  @ForeignerWorkPermit
  private String permitNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isForeignerWorkPermit(" foreigners work permit number ");
  ```

#### @UnifiedSocialCreditCode
* Validation Rule: Unified Social Credit Code validation, validating Unified Social Credit Codes.
* Example Format: `91350100M000100Y43`
* Usage Example:
  ```java
  // Annotation-based usage
  @UnifiedSocialCreditCode
  private String creditCode;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isUnifiedSocialCreditCode("91350100M000100Y43");
  ```

#### @ChinesePhoneOrLandline
* Validation Rule: Chinese phone number validation, supporting mobile phones and landlines.
* Example Format: Supporting mobile phones and landlines
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePhoneOrLandline
  private String phoneNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChinesePhoneOrLandline("010-12345678");
  ```

#### @ChinesePhone
* Validation Rule: Chinese mobile phone number validation, validating Chinese mobile phone numbers.
* Example Format: 11-digit mobile phone numbers
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePhone
  private String phoneNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChinesePhone("13812345678");
  ```

#### @ChineseLandline
* Validation Rule: Chinese landline validation, validating Chinese landline numbers.
* Example Format: Supporting area codes and extensions
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseLandline
  private String phoneNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseLandline("010-12345678");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isBankCard("4012888888881881");
  ```

#### @CVV
* Validation Rule: CVV/CVC security code validation, validating the 3-digit or 4-digit security code on the back of credit cards.
* Example Format: `123`, `1234`
* Usage Example:
  ```java
  // Annotation-based usage
  @CVV
  private String cvv;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isCVV("123");
  ```

#### @IBAN
* Validation Rule: IBAN international bank account number validation, validating the format and check digits of international bank account numbers (IBAN).
* Example Format: `DE44500800000123456789`, `GB29NWBK60161331926819`
* Usage Example:
  ```java
  // Annotation-based usage
  @IBAN
  private String iban;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isIBAN("DE44500800000123456789");
  ```

#### @SWIFT
* Validation Rule: SWIFT/BIC code validation, validating the format of SWIFT/BIC bank codes, used to identify specific banks in international wire transfers.
* Example Format: `COBADEFF`, `DEUTDEFFXXX`
* Usage Example:
  ```java
  // Annotation-based usage
  @SWIFT
  private String swiftCode;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isSWIFT("COBADEFF");
  ```

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
  ValidaX validator = ValidaX.init();
  
  // Default supports all exchanges
  validator.isStockCode("600000");
  
  // Only validate Shanghai Stock Exchange
  validator.isStockCode("600000", StockCode.Exchange.SHANGHAI);
  
  // Validate Shanghai or Shenzhen exchanges
  validator.isStockCode("000001", StockCode.Exchange.SHANGHAI, StockCode.Exchange.SHENZHEN);
  
  // Validate Hong Kong or New York exchanges
  validator.isStockCode("00700", StockCode.Exchange.HONG_KONG, StockCode.Exchange.NEW_YORK);
  ```

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
  ValidaX validator = ValidaX.init();
  
  // Validate T prefix + 18-digit number format
  validator.isTradeOrderNumber("T123456789012345678");
  
  // Validate pure 18-digit number format
  validator.isTradeOrderNumber("123456789012345678");
  
  // Validate UUID format (with hyphens)
  validator.isTradeOrderNumber("550e8400-e29b-41d4-a716-446655440000");
  
  // Validate UUID format (without hyphens)
  validator.isTradeOrderNumber("550e8400e29b41d4a716446655440000");
  ```

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
  ValidaX validator = ValidaX.init();
  
  // Default supports all product types
  validator.isFinancialProductCode("500001");
  
  // Only validate fund products
  validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND);
  
  // Only validate bond products
  validator.isFinancialProductCode("100001", FinancialProductCode.ProductType.BOND);
  
  // Validate fund and bond products
  validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND);
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isDegreeCertificate("1075522008000001");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isDoctor("20251111014406081973100014");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isTeacher("20253412345678901");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isLawyer("11101201810123456");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isPMP("1234567");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isConstructor("京111050700001");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isAccountant("21010203451");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isDomain("example.com");
  ```

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
  ValidaX validator = ValidaX.init();

  // Validate any IP address (default)
  validator.isIp("192.168.1.1");

  // Validate IPv4 addresses only
  validator.isIp("192.168.1.1", Ip.IpVersion.V4);

  // Validate IPv6 addresses only
  validator.isIp("2001:0db8:85a3::8a2e:0370:7334", Ip.IpVersion.V6);

  // Support both IPv4 and IPv6
  validator.isIp("192.168.1.1", Ip.IpVersion.ANY);
  ```

#### @Mac
* Validation Rule: MAC address validation, validating MAC addresses.
* Example Format: `00:1A:2B:3C:4D:5E`, `00-1A-2B-3C-4D-5E`
* Usage Example:
  ```java
  // Annotation-based usage
  @Mac
  private String macAddress;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isMac("00:1A:2B:3C:4D:5E");
  ```

#### @Url
* Validation Rule: URL address validation, validating URL address format.
* Example Format: `http://example.com`, `https://example.com/path`
* Usage Example:
  ```java
  // Annotation-based usage
  @Url
  private String url;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isUrl("http://example.com");
  ```

#### @Email
* Validation Rule: Email address validation, validating email address format.
* Example Format: `test@example.com`, `user.name@domain.co.uk`
* Usage Example:
  ```java
  // Annotation-based usage
  @Email
  private String email;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isEmail("test@example.com");
  ```

#### @SubnetMask
* Validation Rule: Subnet mask validation, validating subnet mask format.
* Example Format: `255.255.255.0`, `255.0.0.0`
* Usage Example:
  ```java
  // Annotation-based usage
  @SubnetMask
  private String subnetMask;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isSubnetMask("255.255.255.0");
  ```

#### @Port
* Validation Rule: Port number validation, validating whether the port number is within the range of 0-65535.
* Example Format: Integer between 0-65535
* Usage Example:
  ```java
  // Annotation-based usage
  @Port
  private String port;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isPort("8080");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isChineseLicensePlate("京A12345");
  ```

#### @ChinesePatent
* Validation Rule: Chinese patent number validation, validating Chinese patent numbers.
* Example Format: `ZL2013106997442`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChinesePatent
  private String patentNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChinesePatent("ZL2013106997442");
  ```

#### @ChineseTrademark
* Validation Rule: Chinese trademark registration number validation, validating Chinese trademark registration numbers.
* Example Format: `1234567`, `第1234567号`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseTrademark
  private String trademarkNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseTrademark("1234567");
  ```

#### @SoftwareCopyright
* Validation Rule: Computer software copyright registration number validation, validating computer software copyright registration numbers.
* Example Format: `软著登字第2023001234号`
* Usage Example:
  ```java
  // Annotation-based usage
  @SoftwareCopyright
  private String copyrightNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isSoftwareCopyright("软著登字第2023001234号");
  ```

#### @WorkCopyright
* Validation Rule: General work copyright registration number validation, validating general work copyright registration numbers.
* Example Format: `作登字22-2023-A-0018号`
* Usage Example:
  ```java
  // Annotation-based usage
  @WorkCopyright
  private String copyrightNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isWorkCopyright("作登字22-2023-A-0018号");
  ```

#### @ChineseZipCode
* Validation Rule: Chinese postal code validation, validating Chinese postal codes.
* Example Format: `100000`, `200000`
* Usage Example:
  ```java
  // Annotation-based usage
  @ChineseZipCode
  private String zipCode;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isChineseZipCode("100000");
  ```

#### @DrugApproval
* Validation Rule: Validate whether the string is a valid Chinese drug approval number. Drug approval numbers are the numbers approved by the national drug regulatory authorities for pharmaceutical manufacturers to produce drugs
* Example Format: 国药准字H20210039, 国药准字ZC20171003, 国药准字HJ20233150
* Usage Example:
  ```java
  // Annotation-based usage
  @DrugApproval
  private String approvalNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isDrugApproval("国药准字H20210039");
  ```

#### @DrugCode
* Validation Rule: Validate whether the string is a valid Chinese drug code. Drug codes start with 69, are 20 digits, and the last digit is the GS1 check digit
* Example Format: 69012345678901234563, 69123456789012345678
* Usage Example:
  ```java
  // Annotation-based usage
  @DrugCode
  private String drugCode;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isDrugCode("69012345678901234563");
  ```

#### @MedicalDeviceRegistration
* Validation Rule: Medical device registration certificate number validation, used to validate the format of Chinese medical device registration certificate numbers.
* Example Format: `国械注准20243010001`, `粤械注准20242020002`, `国械注进20242030003`
* Usage Example:
  ```java
  // Annotation-based usage
  @MedicalDeviceRegistration
  private String registrationNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isMedicalDeviceRegistration("国械注准20243010001");
  ```

#### @QQ
* Validation Rule: QQ number validation, validating QQ numbers.
* Example Format: `123456789`
* Usage Example:
  ```java
  // Annotation-based usage
  @QQ
  private String qqNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isQQ("123456789");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isWeChat("wechat123");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isVIN("WP0AJ2972LL122844");
  ```

#### @VehicleEngine
* Validation Rule: Validate vehicle engine code format.
* Example Format: `123456`, `ABC123`, `123ABC456`
* Usage Example:
  ```java
  // Annotation-based usage
  @VehicleEngine
  private String engineCode;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isVehicleEngine("123456");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isISBN("9780306406157");
  ```

#### @ISSN
* Validation Rule: International Standard Serial Number validation, supporting 8-digit ISSN format.
* Example Format: `0317-8471` or `03178471`
* Usage Example:
  ```java
  // Annotation-based usage
  @ISSN
  private String issn;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isISSN("0317-8471");
  ```

#### @DOI
* Validation Rule: Digital Object Identifier validation, used for unique identification of digital resources, widely used in academic publications.
* Example Format: Starting with "10.", such as `10.1000/182`
* Usage Example:
  ```java
  // Annotation-based usage
  @DOI
  private String doi;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isDOI("10.1000/182");
  ```

#### @CLC
* Validation Rule: Validate whether the string is a valid Chinese Library Classification (CLC) number. The Chinese Library Classification is a book classification system widely used in Chinese libraries
* Example Format: A, B, TP, TP3, TP311, TP311.1, TP311.138, TP311.138.S6, O175.2, R329.2, F272.3
* Usage Example:
  ```java
  // Annotation-based usage
  @CLC
  private String clcNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isCLC("TP311.138");
  ```

#### @DDC
* Validation Rule: Validate whether the string is a valid Dewey Decimal Classification (DDC) number. The Dewey Decimal Classification is a classification system widely used in libraries
* Example Format: 000, 100, 200, ..., 999, 510, 516.3, 330.94
* Usage Example:
  ```java
  // Annotation-based usage
  @DDC
  private String ddcNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isDDC("516.3");
  ```

#### @ORCID
* Validation Rule: Open Researcher and Contributor ID validation, used to uniquely identify academic authors and contributors.
* Example Format: `0000-0002-1825-0097` or `0000000218250097`
* Usage Example:
  ```java
  // Annotation-based usage
  @ORCID
  private String orcidId;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isORCID("0000-0002-1825-0097");
  ```

#### @IPC
* Validation Rule: International Patent Classification number validation, used to identify patent technical fields.
* Example Format: `A01B1/00`, `A01B1/01`, `H01B12/00`
* Usage Example:
  ```java
  // Annotation-based usage
  @IPC
  private String ipcNumber;

  // Chain call usage
  ValidaX validator = ValidaX.init();
  validator.isIPC("A01B1/00");
  ```

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
  ValidaX validator = ValidaX.init();
  validator.isIMEI("123412341234564");
  ```

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