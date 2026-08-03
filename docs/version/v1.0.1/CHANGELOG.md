# ValidX v1.0.1 Changelog

**Release Date:** July 31, 2026

This document records the changes from v1.0.0 to v1.0.1.

## Change Overview

- ✨ Added @Contains validation annotation
- 🔄 Renamed core class ValidaX → ValidX
- 📖 Documentation improvements and version tracking
- 📜 Added open source license

---

## New Features ✨

### @Contains Validation Annotation

Added string substring matching validator with flexible matching strategies.

**Features:**
- OR logic: Match any one substring
- AND logic: Must match all substrings
- Case-insensitive option
- Full internationalization support (8 languages)

**Annotation Examples:**

```java
// Example 1: OR logic - match either "@" or ".com"
@Contains({"@", ".com"})
private String email;

// Example 2: AND logic - must contain both "@" and "."
@Contains(value = {"@", "."}, matchAll = true)
private String strictEmail;

// Example 3: Case-insensitive matching
@Contains(value = {"HELLO", "WORLD"}, ignoreCase = true)
private String greeting;

// Example 4: Custom error message
@Contains(value = {"product", "service"}, message = "Description must contain 'product' or 'service'")
private String description;
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// OR logic (default)
validator.field("Email")
    .isContains("test@example.com", new String[]{"@", ".com"});

// AND logic
validator.field("Email")
    .isContains("test@example.com", new String[]{"@", "."}, false, true);

// Case-insensitive
validator.field("Greeting")
    .isContains("Hello World", new String[]{"hello", "world"}, true, false);

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: Simple email format validation
public class UserDTO {
    @Contains(value = {"@", "."}, matchAll = true)
    private String email;
}

// Use Case 2: Password strength check - must contain letters and numbers
ValidX.init()
    .field("Password")
    .isContains(password, new String[]{"[a-zA-Z]", "[0-9]"}, false, true);

// Use Case 3: Content keyword filtering
@Contains({"keyword1", "keyword2", "keyword3"})
private String content; // Triggers if any keyword is found

// Use Case 4: URL parameter validation
ValidX.init()
    .field("Callback URL")
    .isContains(callbackUrl, new String[]{"https://", "callback"}, false, true);
```

### Version Tracking

Added "Version" column in documentation quick reference table to indicate when each validation annotation was introduced.

**Example:**

| Annotation | Description | Version |
|------------|-------------|---------|
| @Contains | String contains validation | 1.0.1 |
| @ChineseIdCard | ID card validation | 1.0.0 |
| @ChinesePhone | Phone validation | 1.0.0 |

### Open Source License

Added Apache License 2.0 license file.

---

## Changes 🔄

### Core Class Rename

Renamed main validation class from `ValidaX` to `ValidX` for cleaner, more consistent naming.

**Impact:**
- Core class: `ValidaX` → `ValidX`
- Test class: `ValidaXConfigTest` → `ValidXConfigTest`

**Migration Example:**

```java
// ❌ v1.0.0 (old)
ValidaX validator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("Email").isEmail(email)
    .field("Phone").isChinesePhone(phone);

// ✅ v1.0.1 (new)
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("Email").isEmail(email)
    .field("Phone").isChinesePhone(phone);
```

### Documentation Improvements

- Reduced marketing language, enhanced professionalism and objectivity
- Updated all dependency versions to 1.0.1
- Improved code example readability and practicality

---

## Upgrade Guide 📋

### Breaking Changes ⚠️

**Class Rename:** `ValidaX` → `ValidX`

This is the only breaking change requiring code modification.

### Upgrade Steps

**1. Update Maven Dependency**

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.0.1</version>
</dependency>
```

**2. Global Class Name Replacement**

Use your IDE's global find and replace:
- Find: `ValidaX`
- Replace with: `ValidX`

**3. Rebuild and Test**

```bash
mvn clean compile
mvn test
```

### Try New Features After Upgrade

```java
// Use the new @Contains annotation
public class ArticleDTO {

    @Contains(value = {"tech", "development", "programming"})
    private String title; // Title must contain tech-related keywords

    @Contains(value = {"http://", "https://"}, message = "Must be a valid URL")
    private String link;

    @Contains(value = {"<script", "javascript:"}, matchAll = false,
              message = "Content cannot contain script code")
    private String content;
}
```

---

## Technical Statistics 📊

- **New Code:** 673 lines
- **New Files:** 17 files
- **New Tests:** 185+ test cases
- **Documentation Updates:** 50+ locations

---

## Related Links 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.0.1)
- 📖 [Full Documentation](../../../README.md)
- 🐛 [Report Issues](https://github.com/vipxieliang/ValidX/issues)
- 💡 [Feature Requests](https://github.com/vipxieliang/ValidX/issues/new)

---

Released with ❤️ by the ValidX Team
