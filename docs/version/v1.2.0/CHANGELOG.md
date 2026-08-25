# ValidX v1.2.0 Changelog

**Release Date:** 2026-08-25

This document records the changes from v1.1.0 to v1.2.0.

## Change Overview

- ⚠️ [Breaking Changes](#breaking-changes-️)
  - `isStartsWith()` and `isEndsWith()` chain API parameter changed from `String[]` to `String`
  - Chain API method renames: `isAlphaNum()` → `isAlphaNumber()`, `isMacAddress()` → `isMac()` (aligned 1:1 with annotation names)
- ✨ [New Features](#new-features-)
  - New `@StartsWithAny` multiple prefix validation annotation
  - New `@EndsWithAny` multiple suffix validation annotation
  - New `@NationalityCode` nationality code validation annotation (ISO 3166-1)
- 🔧 [Enhancements](#enhancements-)
  - `@FileSize` annotation now supports MIME type validation with `allowedTypes` parameter
  - `@StartsWith`, `@EndsWith` now support `ignoreCase` parameter for case-insensitive matching
  - `@Url` annotation now supports `protocols` parameter for protocol whitelist configuration (default: http / https / ftp)
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

### Chain API Method Renames: isAlphaNum() → isAlphaNumber(), isMacAddress() → isMac()

The `isAlphaNum()` and `isMacAddress()` methods in the chain API have been renamed to align 1:1 with their corresponding annotations (`@AlphaNumber` ↔ `isAlphaNumber`, `@Mac` ↔ `isMac`).

**What Changed:**

**Before (v1.1.0):**
```java
ValidX validator = ValidX.init();
validator.isAlphaNum("abc123");                        // old name
validator.isMacAddress("00:1A:2B:3C:4D:5E");           // old name
```

**After (v1.2.0):**
```java
ValidX validator = ValidX.init();
validator.isAlphaNumber("abc123");                     // aligned with @AlphaNumber
validator.isMac("00:1A:2B:3C:4D:5E");                  // aligned with @Mac
```

**Migration Guide:**

```java
// v1.1.0 code
validator.isAlphaNum(value);
validator.isMacAddress(value);

// v1.2.0 migration - simply replace method names; parameters and behavior unchanged
validator.isAlphaNumber(value);
validator.isMac(value);
```

**Rationale:**

- **Annotation/Chain 1:1 Alignment**: Chain method names now match annotation names, eliminating confusion between matching annotations and chain methods
- **API Consistency**: Whether using annotations or the chain API, rule names correspond exactly, reducing lookup friction

**Impact:**
- Affects only chain API users who use `isAlphaNum()` or `isMacAddress()` methods
- Annotation-based validation (`@AlphaNumber`, `@Mac`) remains unchanged
- Simple migration: replace the method name directly; parameters and behavior unchanged

---

## New Features ✨

### 1. @StartsWithAny Multiple Prefix Validation Annotation

Added dedicated multiple prefix validation annotation to verify that strings start with any one of the specified prefixes.

**Features:**
- Validates if a string starts with any of the specified prefixes
- Supports validation of multiple prefixes (e.g., URL protocol validation, title validation)
- Supports `ignoreCase` parameter for case-insensitive matching
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

    // Example 5: Case-insensitive validation
    @StartsWithAny(value = {"http://", "https://"}, ignoreCase = true)
    private String urlCaseInsensitive;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Basic usage
validator.field("URL").isStartsWithAny("http://example.com", new String[]{"http://", "https://"});

// Case-insensitive
validator.field("URL").isStartsWithAny("HTTP://example.com", new String[]{"http://", "https://"}, true);

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
- Case-sensitive by default (e.g., "HTTP://" will not match "http://"), use `ignoreCase = true` for case-insensitive matching
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
- Supports `ignoreCase` parameter for case-insensitive matching
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

    // Example 5: Case-insensitive validation
    @EndsWithAny(value = {".jpg", ".jpeg", ".png"}, ignoreCase = true)
    private String imageCaseInsensitive;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Basic usage
validator.field("File").isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png", ".gif"});

// Case-insensitive
validator.field("File").isEndsWithAny("photo.JPG", new String[]{".jpg", ".jpeg", ".png"}, true);

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
- Case-sensitive by default (e.g., ".JPG" will not match ".jpg"), use `ignoreCase = true` for case-insensitive matching
- Null and empty strings pass validation (use with `@NotNull` or `@NotEmpty` if required)
- Empty suffix array will cause validation to fail
- Empty string suffix matches all strings (any string ends with empty string)
- Common use cases: file extension validation, archive format validation, name suffix validation, Chinese honorific validation

---

### 3. @NationalityCode Nationality Code Validation Annotation

Added a nationality code validation annotation to verify that a string is a valid ISO 3166-1 country/region code (two-letter, three-letter, or three-digit).

**Features:**
- Validates ISO 3166-1 country/region codes in three encoding forms: two-letter (alpha-2), three-letter (alpha-3), and three-digit (numeric)
- Built-in `NationalityCodeType` enum; the `formats` parameter specifies allowed encoding forms, defaulting to all three
- Backed by the `IsoCountry` enum (249 countries/regions)
- Case-insensitive matching; values are uppercased before matching
- Null and empty strings pass validation by default
- Full internationalization support (9 languages)
- Typical use case: the 4th-6th digits of the Foreigner's Permanent Residence ID (Five-Star Card) are a three-digit nationality code; specify `NUMERIC` when validating them

**Annotation Examples:**

```java
public class PersonDTO {
    // Example 1: All three forms accepted by default
    @NationalityCode
    private String countryCode;  // "CA", "CAN", and "124" all pass

    // Example 2: Five-Star Card validation - numeric code only (braces may be omitted for a single value)
    @NationalityCode(formats = NationalityCode.NationalityCodeType.NUMERIC)
    private String nationalityCode;  // Only numeric codes like "124" pass

    // Example 3: Two-letter and three-letter only (braces required for multiple values)
    @NationalityCode(formats = {NationalityCode.NationalityCodeType.ALPHA_2, NationalityCode.NationalityCodeType.ALPHA_3})
    private String alphaCode;  // Only "CA" and "CAN" pass
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// All three forms accepted by default
validator.isNationalityCode("124");

// Numeric code only (for Five-Star Card 4th-6th digit validation)
validator.isNationalityCode("124", new NationalityCode.NationalityCodeType[]{NationalityCode.NationalityCodeType.NUMERIC});

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: Foreigner's Permanent Residence ID (Five-Star Card) validation
public class ForeignerResidenceDTO {
    @NationalityCode(formats = NationalityCode.NationalityCodeType.NUMERIC)
    private String nationalityCode;  // 4th-6th digits of the Five-Star Card number
}

// Use Case 2: Generic country/region code entry (any form)
public class CountryDTO {
    @NationalityCode
    private String countryCode;
}

// Use Case 3: Chain validation
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("Country Code").isNationalityCode("CAN");
```

**Notes:**
- `formats` is an array type; braces may be omitted for a single value (`formats = ALPHA_2` is equivalent to `formats = {ALPHA_2}`), but are required for two or more values
- Matching is case-insensitive; values are uppercased before matching
- Null and empty strings pass validation (use with `@NotNull` or `@NotEmpty` for required fields)
- Common use cases: nationality/country code entry validation, Foreigner's Permanent Residence ID (Five-Star Card) number validation

---

## Enhancements 🔧

### 1. @StartsWith, @EndsWith Support Case-Insensitive Matching

Added `ignoreCase` parameter to existing prefix and suffix validation annotations, enabling case-insensitive string matching.

**New Feature:**
- Added optional `ignoreCase` parameter, defaults to `false` (case-sensitive)
- Adds new functionality to v1.0.0 `@StartsWith` and `@EndsWith` annotations
- Chain API also supports `ignoreCase` parameter
- Maintains backward compatibility, default behavior unchanged

**Note:** The new `@StartsWithAny` and `@EndsWithAny` annotations also support the `ignoreCase` parameter, see [New Features](#new-features-) section.

**Annotation Examples:**

```java
public class RequestDTO {
    // Example 1: URL protocol validation (case-insensitive)
    @StartsWith(startsWith = "http://", ignoreCase = true)
    private String url;  // Both "HTTP://example.com" and "http://example.com" pass

    // Example 2: File extension validation (case-insensitive)
    @EndsWith(endsWith = ".jpg", ignoreCase = true)
    private String imageFile;  // Both "photo.JPG" and "photo.jpg" pass
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// StartsWith - case-insensitive
validator.isStartsWith("HTTP://example.com", "http://", true);  // passes

// EndsWith - case-insensitive
validator.isEndsWith("file.TXT", ".txt", true);  // passes

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-world Use Cases:**

```java
// Use Case 1: User-entered URL validation (users may input uppercase)
@RestController
public class LinkController {
    @PostMapping("/links")
    public Result addLink(@Valid @RequestBody LinkDTO dto) {
        return linkService.add(dto);
    }
}

public class LinkDTO {
    @NotBlank(message = "URL cannot be empty")
    @StartsWith(startsWith = "http://", ignoreCase = true)
    private String url;  // Accepts "HTTP://", "Http://", "http://", etc.
}

// Use Case 2: File extension validation (Windows users may use uppercase extensions)
public class FileDTO {
    @NotBlank(message = "Filename cannot be empty")
    @EndsWith(endsWith = ".txt", ignoreCase = true)
    private String fileName;  // Accepts ".TXT", ".Txt", ".txt", etc.
}

// Use Case 3: Chain validation for file paths
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("File Path").isStartsWith(filePath, "/home/", true);  // Accepts "/HOME/", "/Home/", etc.
```

**Notes:**
- `ignoreCase` parameter defaults to `false`, maintaining original case-sensitive behavior
- When `ignoreCase = true` is set, uses `toLowerCase()` for case-insensitive comparison
- Suitable for scenarios requiring tolerance for inconsistent user input casing
- Minimal performance impact (only adds one `toLowerCase()` call)

---

### 2. @FileSize Supports MIME Type Validation

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

### 3. @Url Supports Protocol Whitelist

Added a `protocols` parameter to the `@Url` annotation, allowing configuration of the allowed URL protocol whitelist to restrict or extend accepted URL protocols.

**New Feature:**
- Added optional `protocols` parameter to configure the allowed protocol whitelist
- Default whitelist is `{"http", "https", "ftp"}`, consistent with historical behavior (backward compatible)
- Chain API supports new `isUrl(value, protocols...)` overload
- Protocol matching is case-insensitive
- Whitelist can restrict (e.g., HTTPS only) or extend allowed protocols

**Annotation Examples:**

```java
public class RequestDTO {
    // Default whitelist: http / https / ftp (backward compatible)
    @Url
    private String url;

    // HTTPS only
    @Url(protocols = {"https"})
    private String secureUrl;

    // Web protocols only
    @Url(protocols = {"http", "https"})
    private String webUrl;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Default whitelist (http / https / ftp)
validator.isUrl("http://example.com");

// Specified protocol whitelist (https only)
validator.isUrl("https://example.com", "https");
```

**Notes:**
- The `protocols` parameter is optional; default `{"http", "https", "ftp"}` maintains backward compatibility with no breaking changes
- Protocol matching is case-insensitive
- Null and empty strings still pass validation (use with `@NotNull` or `@NotEmpty` for required fields)

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

All three new annotations support the following 9 languages:

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
- `@NationalityCode`: "Invalid ISO 3166-1 country code (two-letter, three-letter, or three-digit)"

All language packs maintain consistent message format with proper Unicode encoding.

---

## Testing Coverage 🧪

Comprehensive test coverage for all three new features:

**Validator Tests (Bean Validation Framework):**
- `StartsWithAnyValidatorTest`: 9 test cases covering valid/invalid scenarios, null/empty values, case sensitivity, empty arrays
- `EndsWithAnyValidatorTest`: 9 test cases with the same comprehensive coverage

**Chain Validation Tests:**
- `StartsWithAnyValidationChainTest`: 13 test cases for chain API usage
- `EndsWithAnyValidationChainTest`: 13 test cases for chain API usage

**Internationalization Tests:**
- `StartsWithAnyI18nTest`: 8 test cases (one per language)
- `EndsWithAnyI18nTest`: 8 test cases (one per language)

**Nationality Code Validation Tests:**
- `NationalityCodeValidatorTest`: 6 test cases covering valid/invalid codes, various `formats` combinations, and null/empty values
- `NationalityCodeValidationChainTest`: 4 test cases for chain API usage

**Total:** 70 new test cases, all passing ✅

---

## Related Links 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.2.0)
- 📖 [Full Documentation](../../../README.md)
- 🐛 [Report Issues](https://github.com/vipxieliang/ValidX/issues)
- 💡 [Feature Requests](https://github.com/vipxieliang/ValidX/issues/new)

---

Released with ❤️ by the ValidX Team
