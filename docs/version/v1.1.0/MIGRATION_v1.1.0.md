# ValidX Migration Guide: v1.0.0/v1.0.1 → v1.1.0

This document describes breaking changes and migration steps when upgrading from v1.0.0 or v1.0.1 to v1.1.0.

---

## Overview

Version 1.1.0 introduces **breaking changes** to `@FutureDate` and `@PastDate` annotations regarding date-time format support.

**Impact Level**: 🔴 **HIGH** - Applications using date-time strings (e.g., `"2025-12-31 12:00:00"`) with `@FutureDate` or `@PastDate` will break.

---

## Breaking Changes

### 1. @FutureDate - No Longer Supports Time Components

#### v1.0.0/v1.0.1 Behavior
```java
@FutureDate
private String date;

// ✅ Both formats worked automatically:
date = "2025-12-31";           // Parsed as LocalDate
date = "2025-12-31 12:00:00";  // Parsed as LocalDateTime, then converted to LocalDate
```

**How it worked:**
1. First attempted to parse as `yyyy-MM-dd` format
2. If failed, attempted to parse as `yyyy-MM-dd HH:mm:ss` format
3. Automatically supported both pure date and date-time strings

#### v1.1.0 Behavior
```java
@FutureDate
private String date;

// ✅ Pure date format still works:
date = "2025-12-31";

// ❌ Date-time format NO LONGER WORKS:
date = "2025-12-31 12:00:00";  // Validation FAILS
```

**What changed:**
- Only supports pure date formats (no time components)
- Default pattern: `yyyy-MM-dd`
- Custom patterns can be specified via `pattern` parameter, but **cannot contain time symbols** (HH, mm, ss, etc.)
- If `pattern` contains time symbols, throws `IllegalArgumentException` during initialization

---

### 2. @PastDate - No Longer Supports Time Components

#### v1.0.0/v1.0.1 Behavior
```java
@PastDate
private String date;

// ✅ Both formats worked automatically:
date = "2020-01-01";           // Parsed as LocalDate
date = "2020-01-01 12:00:00";  // Parsed as LocalDateTime, then converted to LocalDate
```

#### v1.1.0 Behavior
```java
@PastDate
private String date;

// ✅ Pure date format still works:
date = "2020-01-01";

// ❌ Date-time format NO LONGER WORKS:
date = "2020-01-01 12:00:00";  // Validation FAILS
```

**What changed:**
- Same as `@FutureDate` - only supports pure date formats
- Pattern parameter cannot contain time symbols

---

## Migration Steps

### Step 1: Identify Affected Code

Search your codebase for usages of `@FutureDate` and `@PastDate` with date-time strings:

```bash
# Search for potential date-time usage patterns
grep -r "FutureDate\|PastDate" --include="*.java" your-project/
```

Look for:
- String fields annotated with `@FutureDate` or `@PastDate`
- Values containing time components (e.g., `"2025-12-31 12:00:00"`)
- Chain validation using `isFutureDate()` or `isPastDate()` with time strings

### Step 2: Choose Migration Strategy

For each affected usage, choose one of the following strategies:

#### **Strategy A: Switch to @FutureDateTime / @PastDateTime** ⭐ **Recommended**

Use the new dedicated date-time annotations (added in v1.1.0):

**Before (v1.0.0/v1.0.1):**
```java
public class EventDTO {
    @FutureDate
    private String eventTime;  // "2025-12-31 12:00:00"
}
```

**After (v1.1.0):**
```java
public class EventDTO {
    @FutureDateTime  // ← Use the new annotation
    private String eventTime;  // "2025-12-31 12:00:00"
}
```

**Benefits:**
- ✅ Dedicated annotation designed for date-time validation
- ✅ Default pattern is `yyyy-MM-dd HH:mm:ss`
- ✅ Clearer semantic meaning
- ✅ No configuration required for common use cases

---

#### **Strategy B: Remove Time Components**

If you only need the date portion, strip the time before validation:

**Before (v1.0.0/v1.0.1):**
```java
@FutureDate
private String eventDate;  // "2025-12-31 12:00:00"
```

**After (v1.1.0):**
```java
@FutureDate
private String eventDate;  // "2025-12-31" (time removed)

// Or in your code:
String dateTime = "2025-12-31 12:00:00";
String dateOnly = dateTime.substring(0, 10);  // Extract "2025-12-31"
```

**Benefits:**
- ✅ No annotation changes needed
- ✅ Explicit about only validating the date portion

**Drawbacks:**
- ⚠️ Loses time information
- ⚠️ Requires data transformation

---

#### **Strategy C: Keep Using @FutureDate (Not Recommended)**

If you must keep using `@FutureDate` with date-only strings and custom patterns:

**Example:**
```java
// v1.1.0 - Only for pure date formats
@FutureDate(pattern = "MM/dd/yyyy")
private String usDate;  // "12/31/2025" - OK

@FutureDate(pattern = "yyyy-MM-dd HH:mm:ss")  // ❌ Will throw IllegalArgumentException!
private String dateTime;  // This will NOT work!
```

**Important:**
- ⚠️ Cannot use this strategy for date-time strings
- ⚠️ Pattern parameter does not support time symbols

---

### Step 3: Update Chain Validation Calls

If using chain validation API:

**Before (v1.0.0/v1.0.1):**
```java
ValidX validator = ValidX.init();
validator.isFutureDate("2025-12-31 12:00:00");  // Worked in v1.0.0
```

**After (v1.1.0):**
```java
ValidX validator = ValidX.init();

// Option A: Use the new date-time method
validator.isFutureDateTime("2025-12-31 12:00:00");  // ✅ Recommended

// Option B: Remove time component
validator.isFutureDate("2025-12-31");  // ✅ Works
```

---

### Step 4: Update Tests

Update your test cases to reflect the new behavior:

**Before (v1.0.0/v1.0.1):**
```java
@Test
void testFutureDate() {
    UserDTO dto = new UserDTO();
    dto.setEventDate("2025-12-31 12:00:00");  // Was valid

    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty());  // Passed
}
```

**After (v1.1.0):**
```java
@Test
void testFutureDateTime() {
    UserDTO dto = new UserDTO();
    dto.setEventTime("2025-12-31 12:00:00");

    // Update annotation in UserDTO to @FutureDateTime
    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty());  // Still passes
}

@Test
void testFutureDateWithTimeFormat_ShouldFail() {
    UserDTO dto = new UserDTO();
    dto.setEventDate("2025-12-31 12:00:00");  // Date-time string

    // If still using @FutureDate, this will now FAIL
    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty());  // Fails in v1.1.0
}
```

---

## Quick Reference: Annotation Mapping

| Use Case | v1.0.0/v1.0.1 | v1.1.0 | Notes |
|----------|---------------|--------|-------|
| Pure date, future | `@FutureDate` | `@FutureDate` | ✅ No change needed |
| Pure date, past | `@PastDate` | `@PastDate` | ✅ No change needed |
| Date-time, future | `@FutureDate` | `@FutureDateTime` | ⚠️ **Change required** |
| Date-time, past | `@PastDate` | `@PastDateTime` | ⚠️ **Change required** |
| Custom date format | N/A (auto-detect) | `@FutureDate(pattern="...")` | ✅ New feature |
| Custom date-time format | N/A | `@FutureDateTime(pattern="...")` | ✅ New feature |

---

## Example: Complete Migration

### Before (v1.0.0/v1.0.1)

```java
public class EventDTO {
    @NotNull
    @FutureDate
    private String eventDate;  // Accepts "2025-12-31" or "2025-12-31 12:00:00"

    @NotNull
    @PastDate
    private String registrationDate;  // Accepts "2020-01-01" or "2020-01-01 09:30:00"
}

@Service
public class EventService {
    public void validateEvent(Map<String, Object> data) {
        ValidX validator = ValidX.init();
        validator.isFutureDate(data.get("startTime"))  // Worked with time strings
                 .isPastDate(data.get("createdAt"));    // Worked with time strings

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

### After (v1.1.0)

```java
public class EventDTO {
    // Changed: Now using @FutureDateTime for time-aware validation
    @NotNull
    @FutureDateTime  // ← Changed from @FutureDate
    private String eventDate;  // "2025-12-31 12:00:00"

    // Changed: Now using @PastDateTime for time-aware validation
    @NotNull
    @PastDateTime  // ← Changed from @PastDate
    private String registrationDate;  // "2020-01-01 09:30:00"
}

@Service
public class EventService {
    public void validateEvent(Map<String, Object> data) {
        ValidX validator = ValidX.init();
        // Changed: Using new date-time methods
        validator.isFutureDateTime(data.get("startTime"))  // ← Changed
                 .isPastDateTime(data.get("createdAt"));    // ← Changed

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

---

## FAQ

### Q1: Why was this breaking change introduced?

**A:** To provide clearer semantics and better validation:
- `@FutureDate` / `@PastDate` → Pure date validation (no time)
- `@FutureDateTime` / `@PastDateTime` → Date-time validation (with time)

This separation makes the intent explicit and prevents ambiguity.

### Q2: Will v1.1.0 validate my existing date-only strings?

**A:** ✅ Yes! If you're using pure date strings (e.g., `"2025-12-31"`), no changes are needed.

### Q3: Can I use custom patterns with date-time?

**A:** Yes, but use the appropriate annotation:
```java
// ✅ For date-time with custom pattern:
@FutureDateTime(pattern = "MM/dd/yyyy HH:mm:ss")
private String usDateTime;

// ❌ This will throw an exception:
@FutureDate(pattern = "MM/dd/yyyy HH:mm:ss")  // IllegalArgumentException!
```

### Q4: What happens if I don't migrate?

If your code passes date-time strings to `@FutureDate` or `@PastDate`:
- ❌ Validation will **fail** (string won't match the date-only pattern)
- ❌ Your application may reject valid data
- ❌ Tests may start failing

### Q5: Is there a deprecation period?

**No.** This is an immediate breaking change in v1.1.0. We recommend:
1. Review your codebase before upgrading
2. Run comprehensive tests after upgrading
3. Use the migration strategies above

---

## Need Help?

If you encounter issues during migration:

1. **Check the documentation**: Refer to the updated annotation docs in README.md
2. **Review examples**: See the usage examples in this guide
3. **Contact support**: Email vipxieliang@126.com with:
   - Your current version
   - Code snippet showing the issue
   - Error messages (if any)

---

## Summary Checklist

Before deploying v1.1.0 to production:

- [ ] Searched codebase for `@FutureDate` and `@PastDate` usages
- [ ] Identified all fields/validations using date-time strings
- [ ] Updated annotations to `@FutureDateTime` / `@PastDateTime` where needed
- [ ] Updated chain validation calls (`.isFutureDate()` → `.isFutureDateTime()`)
- [ ] Updated all test cases
- [ ] Ran full test suite to verify no regressions
- [ ] Tested validation behavior in staging environment
- [ ] Updated API documentation if exposing these fields

---

**Last Updated:** 2026-08-04
**Applies To:** ValidX v1.1.0+
