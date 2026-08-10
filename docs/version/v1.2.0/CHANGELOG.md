# ValidX v1.2.0 Changelog

**Release Date:** TBD

This document records the changes from v1.1.0 to v1.2.0.

## Change Overview

- ⚠️ [Breaking Changes](#breaking-changes-️)
  - `isStartsWith()` and `isEndsWith()` chain API parameter changed from `String[]` to `String`
- ✨ [New Features](#new-features-)
  - New `@StartsWithAny` multiple prefix validation annotation
  - New `@EndsWithAny` multiple suffix validation annotation
- 🔧 [Enhancements](#enhancements-)
  - `@FileSize` annotation now supports MIME type validation with `allowedTypes` parameter
- 🎯 [Code Refactoring](#code-refactoring-)
  - Simplified validator initialization code across multiple validators
  - Removed verbose anonymous annotation instance creation
  - Improved code maintainability and readability

---

## Breaking Changes ⚠️

### Chain API Parameter Change: isStartsWith() and isEndsWith()

The `isStartsWith()` and `isEndsWith()` methods in the chain API have been refactored to accept a single `String` parameter instead of `String[]` to better align with their single-value validation purpose.

**What Changed:**

**Before (v1.1.0):**
```java
ValidX validator = ValidX.init();
// Old API - accepted String[] for single prefix/suffix
validator.isStartsWith("http://example.com", new String[]{"http://"});
validator.isEndsWith("photo.jpg", new String[]{".jpg"});
```

**After (v1.2.0):**
```java
ValidX validator = ValidX.init();
// New API - accepts String for single prefix/suffix
validator.isStartsWith("http://example.com", "http://");
validator.isEndsWith("photo.jpg", ".jpg");

// For multiple prefixes/suffixes, use the new *Any methods
validator.isStartsWithAny("http://example.com", new String[]{"http://", "https://"});
validator.isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
```

**Migration Guide:**

1. **Single Prefix/Suffix Validation:**
   ```java
   // v1.1.0 code
   validator.isStartsWith(url, new String[]{"http://"});
   validator.isEndsWith(file, new String[]{".jpg"});

   // v1.2.0 migration - remove array wrapper
   validator.isStartsWith(url, "http://");
   validator.isEndsWith(file, ".jpg");
   ```

2. **Multiple Prefix/Suffix Validation:**
   ```java
   // v1.1.0 code
   validator.isStartsWith(url, new String[]{"http://", "https://"});
   validator.isEndsWith(file, new String[]{".jpg", ".jpeg", ".png"});

   // v1.2.0 migration - use new *Any methods
   validator.isStartsWithAny(url, new String[]{"http://", "https://"});
   validator.isEndsWithAny(file, new String[]{".jpg", ".jpeg", ".png"});
   ```

**Rationale:**

- **Semantic Clarity**: `isStartsWith()` for single value, `isStartsWithAny()` for multiple values
- **API Consistency**: Aligns with the annotation behavior (`@StartsWith` vs `@StartsWithAny`)
- **Better Developer Experience**: More intuitive and less verbose for single-value cases
- **Type Safety**: Eliminates confusion between single and multiple value validation

**Impact:**
- Affects only chain API users who use `isStartsWith()` or `isEndsWith()` methods
- Annotation-based validation (`@StartsWith`, `@EndsWith`) remains unchanged
- Simple migration: remove array wrapper for single values, or use `*Any` methods for multiple values

---

## New Features ✨

### 1. @StartsWithAny Multiple Prefix Validation Annotation

Added dedicated multiple prefix validation annotation to verify that strings start with any one of the specified prefixes.

**Features:**
- Validates if a string starts with any of the specified prefixes
- Supports validation of multiple prefixes (e.g., URL protocol validation, title validation)
- Case-sensitive matching by default
- Null and empty strings pass validation by default
- Full internationalization support (9 languages)
- Complements the `@StartsWith` annotation for flexible prefix validation

**Annotation Examples:**

```java
public class RequestDTO {
    // Example 1: URL protocol validation
    @StartsWithAny({"http://", "https://"})
    private String url;

    // Example 2: Title validation
    @StartsWithAny({"Mr.", "Mrs.", "Ms.", "Dr."})
    private String title;

    // Example 3: Chinese surname validation
    @StartsWithAny({"张", "王", "李", "赵"})
    private String chineseName;

    // Example 4: File path validation
    @StartsWithAny({"/home/", "/usr/", "/opt/"})
    private String filePath;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Basic usage
validator.field("URL").isStartsWithAny("http://example.com", new String[]{"http://", "https://"});

// Multiple prefix options
validator.field("Name").isStartsWithAny("Mr. Smith", new String[]{"Mr.", "Mrs.", "Ms.", "Dr."});

// Chinese text validation
validator.field("Name").isStartsWithAny("张三", new String[]{"张", "王", "李", "赵"});

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: URL security validation
@RestController
public class LinkController {
    @PostMapping("/links")
    public Result addLink(@Valid @RequestBody LinkDTO dto) {
        return linkService.add(dto);
    }
}

public class LinkDTO {
    @NotBlank(message = "URL is required")
    @StartsWithAny({"http://", "https://"})
    private String url;
}

// Use Case 2: Form validation with titles
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("Title").isStartsWithAny(fullName, new String[]{"Mr.", "Mrs.", "Ms.", "Dr."});

// Use Case 3: File path security validation
public class FileAccessDTO {
    @StartsWithAny({"/home/", "/tmp/", "/var/log/"})
    private String allowedPath;
}
```

**Notes:**
- Case-sensitive by default (e.g., "HTTP://" will not match "http://")
- Null and empty strings pass validation (use with `@NotNull` or `@NotEmpty` if required)
- Empty prefix array will cause validation to fail
- Empty string prefix matches all strings (any string starts with empty string)
- Common use cases: URL validation, file path validation, title/prefix validation, Chinese surname validation

---

### 2. @EndsWithAny Multiple Suffix Validation Annotation

Added dedicated multiple suffix validation annotation to verify that strings end with any one of the specified suffixes.

**Features:**
- Validates if a string ends with any of the specified suffixes
- Supports validation of multiple suffixes (e.g., file extension validation, name suffix validation)
- Case-sensitive matching by default
- Null and empty strings pass validation by default
- Full internationalization support (9 languages)
- Complements the `@EndsWith` annotation for flexible suffix validation

**Annotation Examples:**

```java
public class FileDTO {
    // Example 1: Image file validation
    @EndsWithAny({".jpg", ".jpeg", ".png", ".gif"})
    private String imageFile;

    // Example 2: Document file validation
    @EndsWithAny({".txt", ".doc", ".docx", ".pdf"})
    private String documentFile;

    // Example 3: Chinese name suffix validation
    @EndsWithAny({"先生", "女士", "小姐"})
    private String chineseName;

    // Example 4: Compressed file validation
    @EndsWithAny({".zip", ".rar", ".7z", ".tar.gz"})
    private String archiveFile;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Basic usage
validator.field("File").isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png", ".gif"});

// Multiple suffix options
validator.field("Document").isEndsWithAny("report.pdf", new String[]{".txt", ".doc", ".docx", ".pdf"});

// Chinese text validation
validator.field("Name").isEndsWithAny("张先生", new String[]{"先生", "女士", "小姐"});

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: File upload validation
@RestController
public class UploadController {
    @PostMapping("/upload")
    public Result upload(@Valid @RequestBody UploadDTO dto) {
        return uploadService.handle(dto);
    }
}

public class UploadDTO {
    @NotBlank(message = "Filename is required")
    @EndsWithAny({".jpg", ".jpeg", ".png", ".gif"})
    private String fileName;
}

// Use Case 2: Document type validation
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("Document").isEndsWithAny(fileName, new String[]{".txt", ".doc", ".docx", ".pdf"});

// Use Case 3: Chinese name suffix validation
public class PersonDTO {
    @EndsWithAny({"先生", "女士", "小姐", "教授", "博士"})
    private String fullName;
}
```

**Notes:**
- Case-sensitive by default (e.g., ".JPG" will not match ".jpg")
- Null and empty strings pass validation (use with `@NotNull` or `@NotEmpty` if required)
- Empty suffix array will cause validation to fail
- Empty string suffix matches all strings (any string ends with empty string)
- Common use cases: file extension validation, archive format validation, name suffix validation, Chinese honorific validation

---

## Enhancements 🔧

### @FileSize Supports MIME Type Validation

Enhanced the `@FileSize` annotation with a new `allowedTypes` parameter to validate file MIME types, particularly useful for `MultipartFile` validation in Spring applications.

**New Feature:**
- Added optional `allowedTypes` parameter to restrict allowed MIME types
- Works seamlessly with Spring's `MultipartFile`
- Validates both file size and MIME type in a single annotation
- Continues to support `java.io.File`, `java.nio.file.Path`, and `byte[]` types

**Annotation Examples:**

```java
public class FileUploadDTO {
    // Example 1: Image file with size and type validation
    @FileSize(max = "5MB", allowedTypes = {"image/jpeg", "image/png", "image/gif"})
    private MultipartFile avatar;

    // Example 2: Document file with type restrictions
    @FileSize(min = "1KB", max = "10MB", allowedTypes = {"application/pdf", "application/msword"})
    private MultipartFile document;

    // Example 3: Multiple image formats
    @FileSize(max = "2MB", allowedTypes = {"image/jpeg", "image/jpg", "image/png", "image/webp"})
    private MultipartFile photo;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// With MIME type validation
validator.field("Avatar").isFileSize(
    avatarFile,
    "0B",           // min size
    "5MB",          // max size
    new String[]{"image/jpeg", "image/png"}  // allowed MIME types
);
```

**Real-World Use Cases:**

```java
// Use Case 1: Profile avatar upload
@RestController
public class ProfileController {
    @PostMapping("/avatar")
    public Result uploadAvatar(@Valid @RequestBody AvatarDTO dto) {
        return profileService.updateAvatar(dto);
    }
}

public class AvatarDTO {
    @NotNull(message = "Avatar is required")
    @FileSize(max = "5MB", allowedTypes = {"image/jpeg", "image/png", "image/gif"})
    private MultipartFile avatar;
}

// Use Case 2: Document upload with strict type control
public class DocumentDTO {
    @FileSize(
        min = "1KB",
        max = "20MB",
        allowedTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}
    )
    private MultipartFile document;
}
```

**Notes:**
- `allowedTypes` parameter is optional; when empty, only size is validated
- MIME type validation only works with file types that provide MIME information (e.g., `MultipartFile`)
- For `File`, `Path`, and `byte[]` types, `allowedTypes` is ignored
- Common MIME types: `image/jpeg`, `image/png`, `image/gif`, `application/pdf`, `text/plain`, etc.

---

## Code Refactoring 🎯

### Validator Initialization Simplification

Significantly simplified validator initialization code across the codebase, removing verbose anonymous annotation instance creation and improving maintainability.

**Improvements:**

1. **Removed Verbose Anonymous Classes**
   - Eliminated repetitive anonymous annotation implementations
   - Validators now support direct parameter initialization
   - Reduced code size by approximately 500+ lines

2. **Affected Validators:**
   - `InValidator` and `NotInValidator`
   - `EnumValidator`
   - `FileExtensionValidator`
   - `FileSizeValidator` family (all 4 variants)
   - `PasswordValidator`
   - `StartsWith` and `EndsWithValidator`
   - `UUIDValidator`, `Base64Validator`, `AgeValidator`
   - `JSONValidator`, `PhoneNumberValidator`
   - `TimestampValidator`, `IpValidator`
   - `StockCodeValidator`, `FinancialProductCodeValidator`

3. **Benefits:**
   - **Cleaner Code**: Removed 30+ lines per validator on average
   - **Better Maintainability**: Easier to understand and modify
   - **Improved Performance**: Slightly faster initialization
   - **Consistent Pattern**: All validators now follow the same initialization approach

**Before (Old Code):**

```java
public void validateIn(Object value, String[] values, List<String> errors, Locale locale) {
    InValidator validator = new InValidator();

    // Create mock annotation instance (30+ lines of boilerplate)
    In inAnnotation = new In() {
        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return In.class;
        }

        @Override
        public String[] value() {
            return values != null ? values : new String[0];
        }

        // ... more boilerplate methods
    };

    validator.initialize(inAnnotation);
    if (!validator.isValid(value, null)) {
        errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.in", locale));
    }
}
```

**After (New Code):**

```java
public void validateIn(Object value, String[] values, List<String> errors, Locale locale) {
    InValidator validator = new InValidator();
    validator.initialize(values);
    if (!validator.isValid(value, null)) {
        errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.in", locale));
    }
}
```

**Impact:**
- Total lines reduced: 500+ lines
- Files refactored: 20+ validator classes
- Improved code readability across the entire validation chain
- No functional changes - all existing tests pass without modification

---

## Internationalization Support 🌍

Both new annotations support the following 9 languages:

- **Chinese (Simplified)** - `ValidationMessages.properties` and `ValidationMessages_zh.properties`
- **English** - `ValidationMessages_en.properties`
- **Japanese** - `ValidationMessages_ja.properties`
- **Korean** - `ValidationMessages_ko.properties`
- **French** - `ValidationMessages_fr.properties`
- **German** - `ValidationMessages_de.properties`
- **Spanish** - `ValidationMessages_es.properties`
- **Russian** - `ValidationMessages_ru.properties`

**Error Messages:**
- `@StartsWithAny`: "Does not start with any of the specified strings"
- `@EndsWithAny`: "Does not end with any of the specified strings"

All language packs maintain consistent message format with proper Unicode encoding.

---

## Testing Coverage 🧪

Comprehensive test coverage for both new features:

**Validator Tests (Bean Validation Framework):**
- `StartsWithAnyValidatorTest`: 9 test cases covering valid/invalid scenarios, null/empty values, case sensitivity, empty arrays
- `EndsWithAnyValidatorTest`: 9 test cases with the same comprehensive coverage

**Chain Validation Tests:**
- `StartsWithAnyValidationChainTest`: 13 test cases for chain API usage
- `EndsWithAnyValidationChainTest`: 13 test cases for chain API usage

**Internationalization Tests:**
- `StartsWithAnyI18nTest`: 8 test cases (one per language)
- `EndsWithAnyI18nTest`: 8 test cases (one per language)

**Total:** 60 new test cases, all passing ✅

---

## Related Links 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.2.0)
- 📖 [Full Documentation](../../../README.md)
- 🐛 [Report Issues](https://github.com/vipxieliang/ValidX/issues)
- 💡 [Feature Requests](https://github.com/vipxieliang/ValidX/issues/new)

---

Released with ❤️ by the ValidX Team
