# ValidX v1.1.0 Changelog

**Release Date:** August 6, 2026

This document records the changes from v1.0.1 to v1.1.0.

## Change Overview

- ⚠️ [Breaking Changes](#breaking-changes-️)
  - `@PastDate` supports custom format (breaking change)
  - `@FutureDate` supports custom format (breaking change)
- ✨ [New Features](#new-features-)
  - New `@ChineseName` Chinese name validation annotation
  - New `@Date` date format validation annotation
  - New `@DateTime` date-time format validation annotation
  - New `@PastDateTime` past date-time validation annotation
  - New `@FutureDateTime` future date-time validation annotation
- 🔧 [Enhancements](#enhancements-): Date validators support custom format (pattern parameter)

---

## Breaking Changes ⚠️
### 1. @PastDate Supports Custom Format (Breaking Change)

Added optional `pattern` parameter to existing `@PastDate` annotation to support custom date formats.

**Enhancement Details:**
- Added optional `pattern` parameter
- Default format remains `yyyy-MM-dd`, **backward compatible**
- Supports custom date formats (but does not allow time components)
- Existing `includeToday` parameter remains unchanged
- Smart format validation: pattern **cannot** contain time format symbols
- **Uses strict validation mode (ResolverStyle.STRICT)**: Automatically rejects invalid dates (e.g., 2024-02-30, 2023-02-29)
- **Strict format matching**: Input must exactly match the pattern length and format (e.g., zero-padding required when using `yyyy-MM-dd`)

**Annotation Examples:**

```java
public class HistoryDTO {
    // Backward compatible: default format (yyyy-MM-dd)
    @PastDate
    private String birthDate;

    // New feature: custom format
    @PastDate(pattern = "dd-MM-yyyy")
    private String europeanDate;

    @PastDate(pattern = "MM/dd/yyyy")
    private String usDate;

    // Include today + custom format
    @PastDate(includeToday = true, pattern = "yyyy年MM月dd日")
    private String chineseDate;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Three-level API design

// Level 1: No parameters (default: includeToday=false, pattern="yyyy-MM-dd")
validator.isPastDate("2020-01-01");

// Level 2: Custom includeToday (pattern="yyyy-MM-dd")
validator.isPastDate("2020-01-01", false);

// Level 3: Full customization
validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");
validator.isPastDate("01-01-2020", false, "dd-MM-yyyy");
```

**Backward Compatibility:**

```java
// ✅ v1.0.1 code works in v1.1.0 without modification, fully compatible
@PastDate  // Still uses "yyyy-MM-dd" by default
private String birthDate;

@PastDate(includeToday = true)  // includeToday parameter works normally
private String lastUpdate;
```

---

---

### 2. @FutureDate Supports Custom Format (Breaking Change)

Added optional `pattern` parameter to existing `@FutureDate` annotation to support custom date formats.

**Enhancement Details:**
- Added optional `pattern` parameter
- Default format remains `yyyy-MM-dd`, **backward compatible**
- Supports custom date formats (but does not allow time components)
- Existing `includeToday` parameter remains unchanged
- Smart format validation: pattern **cannot** contain time format symbols
- **Uses strict validation mode (ResolverStyle.STRICT)**: Automatically rejects invalid dates (e.g., 2024-02-30, 2023-02-29)
- **Strict format matching**: Input must exactly match the pattern length and format (e.g., zero-padding required when using `yyyy-MM-dd`)

**Annotation Examples:**

```java
public class PlanDTO {
    // Backward compatible: default format (yyyy-MM-dd)
    @FutureDate
    private String eventDate;

    // New feature: custom format
    @FutureDate(pattern = "MM/dd/yyyy")
    private String usDate;

    @FutureDate(pattern = "dd-MM-yyyy")
    private String europeanDate;

    // Include today + custom format
    @FutureDate(includeToday = true, pattern = "yyyy年MM月dd日")
    private String deadline;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Three-level API design

// Level 1: No parameters (default: includeToday=false, pattern="yyyy-MM-dd")
validator.isFutureDate("2025-12-31");

// Level 2: Custom includeToday (pattern="yyyy-MM-dd")
validator.isFutureDate("2025-12-31", true);

// Level 3: Full customization
validator.isFutureDate("12/31/2025", false, "MM/dd/yyyy");
validator.isFutureDate("31-12-2025", false, "dd-MM-yyyy");
```

**Backward Compatibility:**

```java
// ✅ v1.0.1 code works in v1.1.0 without modification, fully compatible
@FutureDate  // Still uses "yyyy-MM-dd" by default
private String eventDate;

@FutureDate(includeToday = true)  // includeToday parameter works normally
private String deadline;
```

---

---

## New Features ✨

### 1. @ChineseName Chinese Name Validation Annotation

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
validator.field("Name").isChineseName("张三");
validator.field("Name").isChineseName("欧阳修");
validator.field("Name").isChineseName("诸葛亮");

// Minority names with middle dot
validator.field("Name").isChineseName("买买提·吐尔逊");
validator.field("Name").isChineseName("迪丽热巴·迪力木拉提");

// Historical names
validator.field("Name").isChineseName("爱新觉罗·玄烨");

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

### 2. @Date Date Format Validation Annotation

Added pure date format validation annotation to verify if a string conforms to a specified date format (without time component), regardless of whether the date is in the past or future.

**Features:**
- Validates if a string is a valid pure date format (no time component)
- Default format: `yyyy-MM-dd`
- Supports custom patterns (pattern parameter)
- Uses strict validation mode (ResolverStyle.STRICT)
- Automatically rejects invalid dates (e.g., 2024-02-30, 2023-02-29)
- Pattern must NOT contain time symbols (H, h, K, k, m, s, S, a, A, n, N)
- Full internationalization support (9 languages)
- **Strict format matching**: Input must exactly match the pattern length and format (e.g., zero-padding required when using `yyyy-MM-dd`)

**Annotation Examples:**

```java
public class EventDTO {
    // Example 1: Default format (yyyy-MM-dd)
    @Date
    private String eventDate;

    // Example 2: Custom date format
    @Date(pattern = "dd/MM/yyyy")
    private String europeanDate;

    // Example 3: Compact format
    @Date(pattern = "yyyyMMdd")
    private String compactDate;

    // Example 4: US format
    @Date(pattern = "MM/dd/yyyy")
    private String usDate;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Default format (yyyy-MM-dd)
validator.field("Date").isDate("2024-01-15");

// Custom formats
validator.field("Date").isDate("15/01/2024", "dd/MM/yyyy");
validator.field("Compact Date").isDate("20240115", "yyyyMMdd");

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: API parameter validation
@RestController
public class OrderController {
    @PostMapping("/orders")
    public Result createOrder(@Valid @RequestBody OrderDTO dto) {
        return orderService.create(dto);
    }
}

public class OrderDTO {
    @Date(pattern = "yyyy-MM-dd")
    private String deliveryDate;  // Validates date format only, no past/future restriction
}

// Use Case 2: Bulk data import validation
ValidX validator = ValidX.init();
for (String date : importedDates) {
    validator.field("Import Date").isDate(date, userDefinedPattern);
}

// Use Case 3: Configuration file date validation
@Configuration
public class AppConfig {
    @Date(pattern = "yyyy-MM-dd")
    private String systemStartDate;
}
```

---

### 3. @DateTime Date-Time Format Validation Annotation

Added date-time format validation annotation to verify if a string conforms to a specified date-time format (must include time component), regardless of whether the date-time is in the past or future.

**Features:**
- Validates if a string is a valid date-time format (must include time)
- Default format: `yyyy-MM-dd HH:mm:ss`
- Supports custom patterns (pattern parameter)
- Uses strict validation mode (ResolverStyle.STRICT)
- Automatically rejects invalid dates and times (e.g., 2024-02-30 13:00:00, 2024-01-15 25:00:00)
- Pattern must contain time symbols (H, h, K, k, m, s, S, a, A, n, N)
- Full internationalization support (9 languages)
- **Strict format matching**: Input must exactly match the pattern including time components (e.g., missing seconds will fail validation)

**Annotation Examples:**

```java
public class LogDTO {
    // Example 1: Default format (yyyy-MM-dd HH:mm:ss)
    @DateTime
    private String timestamp;

    // Example 2: ISO 8601 format
    @DateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String isoDateTime;

    // Example 3: With milliseconds
    @DateTime(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String preciseTime;

    // Example 4: 12-hour format
    @DateTime(pattern = "yyyy-MM-dd hh:mm:ss a")
    private String amPmTime;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Default format (yyyy-MM-dd HH:mm:ss)
validator.field("Timestamp").isDateTime("2024-01-15 13:30:00");

// Custom formats
validator.field("ISO Time").isDateTime("2024-01-15T13:30:00", "yyyy-MM-dd'T'HH:mm:ss");
validator.field("Precise Time").isDateTime("2024-01-15 13:30:00.123", "yyyy-MM-dd HH:mm:ss.SSS");

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: Log recording
@RestController
public class LogController {
    @PostMapping("/logs")
    public Result saveLog(@Valid @RequestBody LogDTO dto) {
        return logService.save(dto);
    }
}

public class LogDTO {
    @DateTime
    private String occurredAt;  // Validates timestamp format
}

// Use Case 2: Event tracking
ValidX validator = ValidX.init();
validator.field("Event Time").isDateTime(eventTime, "yyyy-MM-dd HH:mm:ss");

// Use Case 3: Database import
for (String timestamp : timestamps) {
    if (!validator.isDateTime(timestamp).passed()) {
        // Handle format error
    }
}
```

---

### 4. @PastDateTime Past Date-Time Validation Annotation

Added dedicated past date-time validation annotation for validating past date-time strings that include time components.

**Features:**
- Validates if date-time is in the past (must include time component)
- Default format: `yyyy-MM-dd HH:mm:ss`
- Supports custom patterns (pattern parameter)
- Supports `includeToday` parameter to control whether current time is included
- Pattern validation ensures pattern must contain time components (H, h, K, k, m, s, S, a, A, n, N)
- Full internationalization support (9 languages)
- **Uses strict validation mode (ResolverStyle.STRICT)**: Automatically rejects invalid date-times
- **Strict format matching**: Input must exactly match the pattern including time components (e.g., missing seconds will fail validation)

**Annotation Examples:**

```java
public class RecordDTO {
    // Example 1: Default format (yyyy-MM-dd HH:mm:ss)
    @PastDateTime
    private String createdAt;

    // Example 2: Custom format
    @PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String isoDateTime;

    // Example 3: Include current time
    @PastDateTime(includeToday = true)
    private String lastModified;

    // Example 4: Custom format + include current time
    @PastDateTime(includeToday = true, pattern = "yyyy/MM/dd HH:mm:ss")
    private String processedAt;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Default format (yyyy-MM-dd HH:mm:ss)
validator.field("Created Time").isPastDateTime("2024-01-01 10:30:00");

// Custom format
validator.field("ISO Time").isPastDateTime(
    "2024-01-01T10:30:00",
    false,
    "yyyy-MM-dd'T'HH:mm:ss"
);

// Include current time
validator.field("Last Modified").isPastDateTime(
    "2026-08-06 09:00:00",
    true  // Allow current time
);
```

---

### 5. @FutureDateTime Future Date-Time Validation Annotation

Added dedicated future date-time validation annotation for validating future date-time strings that include time components.

**Features:**
- Validates if date-time is in the future (must include time component)
- Default format: `yyyy-MM-dd HH:mm:ss`
- Supports custom patterns (pattern parameter)
- Supports `includeToday` parameter to control whether current time is included
- Pattern validation ensures pattern must contain time components (H, h, K, k, m, s, S, a, A, n, N)
- Full internationalization support (9 languages)
- **Uses strict validation mode (ResolverStyle.STRICT)**: Automatically rejects invalid date-times
- **Strict format matching**: Input must exactly match the pattern including time components (e.g., missing seconds will fail validation)

**Annotation Examples:**

```java
public class EventDTO {
    // Example 1: Default format (yyyy-MM-dd HH:mm:ss)
    @FutureDateTime
    private String scheduledAt;

    // Example 2: Custom format
    @FutureDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String isoSchedule;

    // Example 3: Include current time
    @FutureDateTime(includeToday = true)
    private String appointmentTime;

    // Example 4: Custom format + include current time
    @FutureDateTime(includeToday = true, pattern = "yyyy/MM/dd HH:mm:ss")
    private String meetingTime;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Default format (yyyy-MM-dd HH:mm:ss)
validator.field("Appointment Time").isFutureDateTime("2025-12-31 23:59:59");

// Custom format
validator.field("ISO Time").isFutureDateTime(
    "2025-12-31T23:59:59",
    false,
    "yyyy-MM-dd'T'HH:mm:ss"
);

// Include current time
validator.field("Meeting Time").isFutureDateTime(
    "2026-08-06 10:00:00",
    true  // Allow current time
);
```

---

## Enhancements 🔧

### 1. Date Validation General Enhancement Features

All date/datetime validators (both new and enhanced) have the following features:

**Smart Format Validation**
- Date validators (`@PastDate`/`@FutureDate`) pattern **cannot** contain time format symbols
- DateTime validators (`@PastDateTime`/`@FutureDateTime`) pattern **must** contain time format symbols
- When rules are violated, throws `IllegalArgumentException` during validator initialization with clear error message
- Avoids runtime format confusion issues

**Strict Date Validation**
- Uses `ResolverStyle.STRICT` mode
- Automatically rejects invalid dates (e.g., 2024-02-30, 2023-02-29)
- Ensures date legality and accuracy

**Three-Level Chain API Design**
- No parameters: Uses defaults (includeToday=false, default pattern)
- Single parameter: Custom includeToday, uses default pattern
- Full parameters: Custom includeToday and custom pattern

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
- Standard date: `yyyy-MM-dd`
- US format: `MM/dd/yyyy`
- European format: `dd/MM/yyyy`
- ISO 8601: `yyyy-MM-dd'T'HH:mm:ss`
- Chinese format: `yyyy年MM月dd日`
- Compact format: `yyyyMMdd`

---

### 2. Improved Date Validation Error Messages

Enhanced date validation error messages with better internationalization support and clear error hints.

**Improvements:**

1. **Format Validation Error Messages**
   - When pure date validators (`@PastDate`/`@FutureDate`) pattern contains time format symbols, provides clear error message
   - When datetime validators (`@PastDateTime`/`@FutureDateTime`) pattern lacks time format symbols, provides clear error message
   - Error messages clearly point out the issue and suggest using the correct validator

2. **Full Internationalization Support (9 Languages)**
   - All error messages support the following languages:
     - Chinese (default) - `ValidationMessages.properties`
     - Simplified Chinese - `ValidationMessages_zh.properties`
     - English - `ValidationMessages_en.properties`
     - Japanese - `ValidationMessages_ja.properties`
     - Korean - `ValidationMessages_ko.properties`
     - French - `ValidationMessages_fr.properties`
     - German - `ValidationMessages_de.properties`
     - Spanish - `ValidationMessages_es.properties`
     - Russian - `ValidationMessages_ru.properties`
   - All language packs maintain consistent message format
   - Property files use correct Unicode encoding

## Related Links 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.1.0)
- 📖 [Full Documentation](../../../README.md)
- 🐛 [Report Issues](https://github.com/vipxieliang/ValidX/issues)
- 💡 [Feature Requests](https://github.com/vipxieliang/ValidX/issues/new)

---

Released with ❤️ by the ValidX Team
