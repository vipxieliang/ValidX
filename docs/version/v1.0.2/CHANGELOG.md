# ValidX v1.0.2 Changelog

**Release Date:** August 4, 2026

This document records the changes from v1.0.1 to v1.0.2.

## Change Overview

- ✨ Added @ChineseName validation annotation
- 🔧 Enhanced date/datetime validators with custom pattern support
- 📖 Added comprehensive internationalization tests
- 🎯 Improved error messages for date validation

---

## New Features ✨

### @ChineseName Validation Annotation

Added Chinese name validator that conforms to Chinese naming conventions.

**Features:**
- Only Chinese characters allowed
- Length between 2-50 characters (covers all Chinese names including very long minority names)
- Supports middle dot "·" in minority names
- No numbers, letters, or special characters allowed
- Full internationalization support (9 languages)

**Annotation Examples:**

```java
// Example 1: Basic usage
public class UserDTO {
    @ChineseName
    private String realName;
}

// Example 2: Required field
public class RegistrationDTO {
    @NotBlank(message = "Name is required")
    @ChineseName
    private String name;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Han Chinese names
validator.field("姓名").isChineseName("张三");
validator.field("姓名").isChineseName("欧阳修");
validator.field("姓名").isChineseName("诸葛亮");

// Minority names with middle dot
validator.field("姓名").isChineseName("买买提·吐尔逊");
validator.field("姓名").isChineseName("迪丽热巴·迪力木拉提");

// Historical names
validator.field("姓名").isChineseName("爱新觉罗·玄烨");

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: User registration
public class UserRegistrationDTO {
    @NotBlank(message = "Real name is required")
    @ChineseName
    private String realName;

    @ChineseIdCard
    private String idCard;
}

// Use Case 2: Identity verification
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("Real Name").isChineseName(realName)
    .field("ID Card").isChineseIdCard(idCard);

// Use Case 3: Form validation
@RestController
public class UserController {
    @PostMapping("/verify")
    public Result verify(@Valid @RequestBody UserVerifyDTO dto) {
        return verifyService.verify(dto);
    }
}
```

---

## Enhancements 🔧

### Date/DateTime Validators - Custom Pattern Support

Enhanced `@FutureDate`, `@PastDate`, `@FutureDateTime`, and `@PastDateTime` validators with custom date pattern support.

**What's New:**

1. **Custom Pattern Parameter**
   - Users can now specify custom date/datetime formats
   - Default patterns remain unchanged for backward compatibility
   - Strict validation mode ensures date validity (e.g., rejects 2024-02-30)

2. **New Annotations: @PastDateTime and @FutureDateTime**
   - Dedicated validators for date-time validation (must include time component)
   - Separate from date-only validators for better type safety
   - Pattern validation ensures time components are present

3. **Three-Level API Design**
   - No parameters: Uses defaults (includeToday=false, default pattern)
   - Single parameter: Custom includeToday, default pattern
   - Full parameters: Custom includeToday and custom pattern

**Annotation Examples:**

```java
public class EventDTO {
    // Example 1: Default pattern (yyyy-MM-dd)
    @FutureDate
    private String eventDate;

    // Example 2: Custom pattern
    @FutureDate(pattern = "MM/dd/yyyy")
    private String usDate;

    // Example 3: Include today
    @FutureDate(includeToday = true, pattern = "yyyy-MM-dd")
    private String deadline;

    // Example 4: Past datetime with custom pattern
    @PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String createdAt;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Level 1: No parameters (default: includeToday=false, pattern="yyyy-MM-dd")
validator.isFutureDate("2025-12-31");
validator.isPastDate("2020-01-01");

// Level 2: Custom includeToday (pattern="yyyy-MM-dd")
validator.isFutureDate("2025-12-31", true);
validator.isPastDate("2020-01-01", false);

// Level 3: Full customization
validator.isFutureDate("12/31/2025", false, "MM/dd/yyyy");
validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");

// DateTime validators (default pattern="yyyy-MM-dd HH:mm:ss")
validator.isFutureDateTime("2025-12-31 23:59:59");
validator.isPastDateTime("2020-01-01 12:30:45");

// DateTime with custom pattern
validator.isFutureDateTime("12/31/2025 23:59:59", false, "MM/dd/yyyy HH:mm:ss");
validator.isPastDateTime("01/01/2020 12:30:45", false, "MM/dd/yyyy HH:mm:ss");
```

**Supported Date Format Symbols:**

| Symbol | Meaning | Example |
|--------|---------|---------|
| `yyyy` | Year (4 digits) | `2024` |
| `MM` | Month (zero-padded) | `01`, `12` |
| `dd` | Day (zero-padded) | `05`, `25` |
| `HH` | Hour (24-hour, zero-padded) | `00`, `23` |
| `mm` | Minute (zero-padded) | `00`, `59` |
| `ss` | Second (zero-padded) | `00`, `59` |

**Common Pattern Examples:**
- US format: `MM/dd/yyyy`
- European format: `dd/MM/yyyy`
- ISO 8601: `yyyy-MM-dd'T'HH:mm:ss`
- Chinese format: `yyyy年MM月dd日`
- Compact format: `yyyyMMdd`

### Improved Error Messages

Enhanced date validation error messages with better internationalization support.

**What's Improved:**

1. **Pattern Validation Errors**
   - Clear error messages when date pattern contains time components (for date-only validators)
   - Clear error messages when datetime pattern missing time components (for datetime validators)

2. **9-Language Support**
   - All error messages available in 9 languages
   - Proper Unicode encoding in properties files
   - Consistent message formatting across languages

**Example Error Messages:**

```java
// English
"The date validation pattern cannot contain time components (H, m, s, a). Please use PastDateTime or FutureDateTime for datetime validation"

// Simplified Chinese
"日期验证的 pattern 不能包含时间部分（H、m、s、a）。如果需要验证日期时间，请使用 PastDateTime 或 FutureDateTime"

// Japanese
"日付検証のパターンには時間部分（H、m、s、a）を含めることはできません。日時を検証する場合は、PastDateTime または FutureDateTime を使用してください"
```

---

## Documentation Updates 📖

### README Enhancements

Updated both English and Chinese README files with:

1. **@ChineseName Documentation**
   - Validation rules
   - Example formats
   - Usage examples for both annotation and chain API
   - Real-world use cases

2. **Enhanced Date/DateTime Validation Documentation**
   - Parameter descriptions for all date validators
   - Three-level API usage examples
   - Supported format symbols table
   - Common pattern examples

3. **Version Tracking**
   - Added version 1.0.2 tag for new features
   - Updated quick reference table

### Comprehensive Test Coverage

Added extensive test suites for new functionality:

**New Test Files:**
- `ChineseNameValidatorTest.java` - Annotation-based validation tests
- `ChineseNameValidationChainTest.java` - Chain API validation tests
- `DateValidatorI18nTest.java` - Internationalization tests (9 languages)
- `PastDateValidationChainTest.java` - Past date chain API tests
- `FutureDateValidationChainTest.java` - Future date chain API tests
- `PastDateTimeValidationChainTest.java` - Past datetime chain API tests
- `FutureDateTimeValidationChainTest.java` - Future datetime chain API tests
- `DateTimeChainPatternTest.java` - Custom pattern validation tests

**Test Statistics:**
- New tests: 50+ test cases
- Total test coverage: 1554 tests
- All tests passing ✅

---

## Compatibility 🔄

### Backward Compatibility ✅

**100% backward compatible with v1.0.1**

All existing code will continue to work without any modifications:

```java
// ✅ All v1.0.1 code works in v1.0.2
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("Email").isEmail(email)
    .field("Phone").isChinesePhone(phone);

// ✅ Existing date validators use default patterns
@FutureDate  // Still uses "yyyy-MM-dd" by default
private String date;

@PastDate    // Still uses "yyyy-MM-dd" by default
private String birthDate;
```

### New Optional Parameters

New `pattern` parameter is **optional** for all date validators:

```java
// ✅ Works exactly as before (uses default pattern)
@FutureDate
private String date;

// ✅ New feature (custom pattern)
@FutureDate(pattern = "MM/dd/yyyy")
private String usDate;
```

### Chain API - Overloaded Methods

Chain API maintains full backward compatibility through method overloading:

```java
// ✅ v1.0.1 code - still works
validator.isFutureDate(date, false);

// ✅ v1.0.2 enhancement - new functionality
validator.isFutureDate(date, false, "MM/dd/yyyy");
```

### No Breaking Changes ⚠️

This release contains **zero breaking changes**:
- ✅ No API removals
- ✅ No parameter type changes
- ✅ No behavior changes for existing features
- ✅ All default values preserved
- ✅ All error messages unchanged (only additions)

---

## Migration Guide 📋

### No Migration Required ✅

If you're using v1.0.1, you can upgrade to v1.0.2 without any code changes.

### Upgrade Steps

**1. Update Maven Dependency**

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.0.2</version>
</dependency>
```

**2. Rebuild Project**

```bash
mvn clean install
```

**3. Try New Features (Optional)**

```java
// New feature 1: Chinese name validation
public class UserDTO {
    @ChineseName
    private String realName;
}

// New feature 2: Custom date patterns
@FutureDate(pattern = "MM/dd/yyyy")
private String usDate;

// New feature 3: DateTime validators
@PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
private String createdAt;

// New feature 4: Three-level chain API
ValidX.init()
    .isFutureDate("12/31/2025", false, "MM/dd/yyyy")
    .isPastDateTime("2020-01-01T12:30:45", false, "yyyy-MM-dd'T'HH:mm:ss");
```

---

## Technical Statistics 📊

- **New Code:** ~800 lines
- **New Files:** 15 files
- **New Tests:** 50+ test cases
- **Total Tests:** 1554 tests (all passing ✅)
- **Documentation Updates:** 100+ locations
- **New Validators:** 5 (ChineseName + 4 datetime chain methods)
- **Enhanced Validators:** 4 (FutureDate, PastDate, FutureDateTime, PastDateTime)

---

## Code Quality Improvements 🎨

### Delegation Pattern

Applied delegation pattern to eliminate code duplication in validators:

```java
// Before: Duplicate logic in both methods
@Override
public void initialize(PastDate annotation) {
    this.includeToday = annotation.includeToday();
    this.pattern = annotation.pattern();
    // ... validation logic
}

// After: Delegation eliminates duplication
@Override
public void initialize(PastDate annotation) {
    initialize(annotation.includeToday(), annotation.pattern());
}

public void initialize(boolean includeToday, String pattern) {
    this.includeToday = includeToday;
    this.pattern = pattern;
    // ... validation logic (single source of truth)
}
```

### Clean Architecture

Simplified BaseValidation.java by removing overloaded methods and handling defaults at the API layer (ValidX.java).

---

## Related Links 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.0.2)
- 📖 [Full Documentation](../../../README.md)
- 🐛 [Report Issues](https://github.com/vipxieliang/ValidX/issues)
- 💡 [Feature Requests](https://github.com/vipxieliang/ValidX/issues/new)
- 📝 [v1.0.1 Changelog](../v1.0.1/CHANGELOG.md)

---

Released with ❤️ by the ValidX Team
