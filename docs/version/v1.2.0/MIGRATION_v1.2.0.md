# ValidX Migration Guide: v1.1.0 → v1.2.0

This document describes the breaking changes and migration steps when upgrading from v1.1.0 to v1.2.0.

---

## Overview

Version 1.2.0 introduces two **breaking changes** to the chain API:

1. The `isStartsWith()` and `isEndsWith()` methods: the parameter type has changed from `String[]` to `String` to better align with their single-value validation purpose.
2. `isAlphaNum()` has been renamed to `isAlphaNumber()`, and `isMacAddress()` has been renamed to `isMac()`, aligned 1:1 with their corresponding annotation names.

**Impact Level**: 🟡 **MEDIUM** - Affects only users of the `isStartsWith()`, `isEndsWith()`, `isAlphaNum()` or `isMacAddress()` chain API methods; annotation-based validation (`@StartsWith`, `@EndsWith`, `@AlphaNumber`, `@Mac`) is completely unaffected.

---

## Breaking Change

### isStartsWith() / isEndsWith() - Parameter Changed from String[] to String

#### v1.1.0 Behavior
```java
ValidX validator = ValidX.init();

// Old API - accepted String[] for single prefix/suffix
validator.isStartsWith("http://example.com", new String[]{"http://"});
validator.isEndsWith("photo.jpg", new String[]{".jpg"});
```

#### v1.2.0 Behavior
```java
ValidX validator = ValidX.init();

// New API - accepts String for single prefix/suffix
validator.isStartsWith("http://example.com", "http://");
validator.isEndsWith("photo.jpg", ".jpg");

// For multiple prefixes/suffixes, use the new *Any methods
validator.isStartsWithAny("http://example.com", new String[]{"http://", "https://"});
validator.isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
```

**What changed:**
- `isStartsWith(Object value, String prefix)` - first parameter is the value to validate, second is a single prefix
- `isEndsWith(Object value, String suffix)` - first parameter is the value to validate, second is a single suffix
- New `isStartsWithAny(Object value, String[] prefixes)` / `isEndsWithAny(Object value, String[] suffixes)` methods handle multiple values
- All methods now have `ignoreCase` overloads (e.g., `isStartsWith(value, prefix, true)`)

---

### isAlphaNum() → isAlphaNumber(), isMacAddress() → isMac() - Chain Method Renames

The `isAlphaNum()` and `isMacAddress()` methods in the chain API have been renamed to align 1:1 with their corresponding annotations (`@AlphaNumber` ↔ `isAlphaNumber`, `@Mac` ↔ `isMac`).

#### v1.1.0 Behavior
```java
ValidX validator = ValidX.init();

// Old API - method names did not match annotation names
validator.isAlphaNum("abc123");
validator.isMacAddress("00:1A:2B:3C:4D:5E");
```

#### v1.2.0 Behavior
```java
ValidX validator = ValidX.init();

// New API - method names aligned 1:1 with annotation names
validator.isAlphaNumber("abc123");
validator.isMac("00:1A:2B:3C:4D:5E");
```

**What changed:**
- `isAlphaNum(Object value)` renamed to `isAlphaNumber(Object value)`, aligned with the `@AlphaNumber` annotation
- `isMacAddress(Object value)` renamed to `isMac(Object value)`, aligned with the `@Mac` annotation
- Parameters and validation behavior are completely unchanged; only the method names changed

**Migration Guide:**

```java
// v1.1.0 code
validator.isAlphaNum(value);
validator.isMacAddress(value);

// v1.2.0 migration - simply replace method names; parameters and behavior unchanged
validator.isAlphaNumber(value);
validator.isMac(value);
```

---

## Migration Steps

### Step 1: Identify Affected Code

Search your codebase for calls in the chain API that need migration:

```bash
# Search for chain API usage
grep -rn "isStartsWith\|isEndsWith\|isAlphaNum\|isMacAddress" --include="*.java" your-project/
```

Look for:
- Chain validation calls using `isStartsWith()` or `isEndsWith()`
- Calls passing `new String[]{...}` arrays as the second parameter (these need migration)
- Calls using `isAlphaNum()` or `isMacAddress()` (these need method rename migration, see Step 4)
- Optionally search for `@StartsWith` / `@EndsWith` / `@AlphaNumber` / `@Mac` annotations (no migration needed, but confirms the scope)

### Step 2: Choose Migration Strategy

For each affected call, choose one of the following strategies based on your validation needs:

#### **Strategy A: Single Prefix/Suffix - Remove the Array Wrapper** ⭐ **Most Common**

If you only validate one prefix or suffix, simply remove the array wrapper:

**Before (v1.1.0):**
```java
// Single prefix validation
validator.isStartsWith(url, new String[]{"http://"});

// Single suffix validation
validator.isEndsWith(file, new String[]{".jpg"});
```

**After (v1.2.0):**
```java
// Single prefix validation - pass the string directly
validator.isStartsWith(url, "http://");

// Single suffix validation - pass the string directly
validator.isEndsWith(file, ".jpg");
```

---

#### **Strategy B: Multiple Prefixes/Suffixes - Use the New *Any Methods**

If you validate against multiple candidates, switch to the new `isStartsWithAny()` / `isEndsWithAny()` methods. **The parameter structure stays identical:**

**Before (v1.1.0):**
```java
// Multiple prefix validation
validator.isStartsWith(url, new String[]{"http://", "https://"});

// Multiple suffix validation
validator.isEndsWith(file, new String[]{".jpg", ".jpeg", ".png"});
```

**After (v1.2.0):**
```java
// Multiple prefix validation - only the method name changed
validator.isStartsWithAny(url, new String[]{"http://", "https://"});

// Multiple suffix validation - only the method name changed
validator.isEndsWithAny(file, new String[]{".jpg", ".jpeg", ".png"});
```

**Benefits:**
- ✅ Minimal change - parameter structure stays the same
- ✅ Clearer semantics: `isStartsWith` for single value / `isStartsWithAny` for multiple values
- ✅ Aligns with annotation behavior (`@StartsWith` vs `@StartsWithAny`)

---

#### **Strategy C: Combine with ignoreCase Parameter (New in v1.2.0)**

v1.2.0 also adds case-insensitive matching. You can upgrade in one step:

```java
// Case-insensitive single-value validation
validator.isStartsWith("HTTP://example.com", "http://", true);  // passes
validator.isEndsWith("file.TXT", ".txt", true);                // passes

// Case-insensitive multi-value validation
validator.isStartsWithAny("HTTP://example.com", new String[]{"http://", "https://"}, true);
validator.isEndsWithAny("photo.JPG", new String[]{".jpg", ".jpeg"}, true);
```

### Step 3: Update Tests

Update your chain validation test cases:

**Before (v1.1.0):**
```java
@Test
void testStartsWith() {
    ValidX validator = ValidX.init();
    validator.isStartsWith("http://example.com", new String[]{"http://"});
    assertTrue(validator.passed());
}

@Test
void testEndsWithMultiple() {
    ValidX validator = ValidX.init();
    validator.isEndsWith("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
    assertTrue(validator.passed());
}
```

**After (v1.2.0):**
```java
@Test
void testStartsWith() {
    ValidX validator = ValidX.init();
    validator.isStartsWith("http://example.com", "http://");  // Removed array wrapper
    assertTrue(validator.passed());
}

@Test
void testEndsWithMultiple() {
    ValidX validator = ValidX.init();
    // Multi-value scenarios now use *Any methods
    validator.isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
    assertTrue(validator.passed());
}

@Test
void testStartsWithIgnoreCase() {
    ValidX validator = ValidX.init();
    validator.isStartsWith("HTTP://example.com", "http://", true);  // New capability
    assertTrue(validator.passed());
}
```

---

### Step 4: Chain Method Rename Migration

Directly replace calls to `isAlphaNum()` / `isMacAddress()` with the new method names:

```bash
# Globally search for the old method names
grep -rn "isAlphaNum\|isMacAddress" --include="*.java" your-project/
```

**Before (v1.1.0):**
```java
validator.isAlphaNum("abc123");
validator.isMacAddress("00:1A:2B:3C:4D:5E");
```

**After (v1.2.0):**
```java
validator.isAlphaNumber("abc123");
validator.isMac("00:1A:2B:3C:4D:5E");
```

> 💡 Pure method name change: parameters and validation behavior are completely unchanged. A global replacement is sufficient; no call-logic adjustment is needed.

---

## Quick Reference: API Mapping

| Use Case | v1.1.0 | v1.2.0 | Notes |
|----------|--------|--------|-------|
| Single prefix | `isStartsWith(value, new String[]{p})` | `isStartsWith(value, p)` | ⚠️ **Remove array wrapper** |
| Single suffix | `isEndsWith(value, new String[]{s})` | `isEndsWith(value, s)` | ⚠️ **Remove array wrapper** |
| Multiple prefixes | `isStartsWith(value, new String[]{p1, p2})` | `isStartsWithAny(value, new String[]{p1, p2})` | ⚠️ **Use *Any methods** |
| Multiple suffixes | `isEndsWith(value, new String[]{s1, s2})` | `isEndsWithAny(value, new String[]{s1, s2})` | ⚠️ **Use *Any methods** |
| Case-insensitive | Not supported | `isStartsWith(value, p, true)` / `isStartsWithAny(value, prefixes, true)` | ✅ New feature |
| Alphanumeric | `isAlphaNum(value)` | `isAlphaNumber(value)` | ⚠️ **Method rename** |
| MAC address | `isMacAddress(value)` | `isMac(value)` | ⚠️ **Method rename** |
| Annotation (single) | `@StartsWith` / `@EndsWith` | `@StartsWith` / `@EndsWith` | ✅ No change needed |
| Annotation (multiple) | Not supported | `@StartsWithAny` / `@EndsWithAny` | ✅ New feature |
| Annotation (alphanumeric) | `@AlphaNumber` | `@AlphaNumber` | ✅ No change needed |
| Annotation (MAC) | `@Mac` | `@Mac` | ✅ No change needed |

---

## Example: Complete Migration

### Before (v1.1.0)

```java
@Service
public class UrlService {
    private final ValidX validator = ValidX.init();

    public void validateLink(String url, String fileName) {
        validator
            // Single prefix (array wrapper)
            .isStartsWith(url, new String[]{"http://"})
            // Multiple suffixes
            .isEndsWith(fileName, new String[]{".jpg", ".jpeg", ".png"});

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

### After (v1.2.0)

```java
@Service
public class UrlService {
    private final ValidX validator = ValidX.init();

    public void validateLink(String url, String fileName) {
        validator
            // Single prefix: pass the string directly, and enable ignoreCase
            .isStartsWith(url, "http://", true)
            // Multiple suffixes: use *Any method, and enable ignoreCase
            .isEndsWithAny(fileName, new String[]{".jpg", ".jpeg", ".png"}, true);

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

---

## FAQ

### Q1: Why was this breaking change introduced?

**A:** For clearer semantics and better type safety:
- `isStartsWith()` / `isEndsWith()` → Single-value validation, accepting a `String` directly
- `isStartsWithAny()` / `isEndsWithAny()` → Multi-value validation, accepting a `String[]`

Separating single and multi-value methods eliminates confusion between the two and aligns with annotation behavior (`@StartsWith` vs `@StartsWithAny`).

### Q2: Do I need to change my annotation code (@StartsWith, @EndsWith)?

**A:** ❌ No. This change **only affects the chain API** (`isStartsWith()` / `isEndsWith()` methods). Annotation-based validation remains completely unchanged.

### Q3: Must I use *Any methods for multiple prefixes/suffixes?

**A:** ✅ Yes. In v1.2.0, `isStartsWith()` / `isEndsWith()` only accept a single `String`, so multi-value validation must use `isStartsWithAny()` / `isEndsWithAny()`. The good news: the `*Any` methods have the exact same parameter structure as the old API - you only need to change the method name.

### Q4: What happens if I don't migrate?

- ❌ The code will **fail to compile** (a `String` cannot match a `String[]` parameter)
- ❌ Application builds will fail

### Q5: Can I use ignoreCase after upgrading?

**A:** ✅ Yes. All four methods (`isStartsWith` / `isEndsWith` / `isStartsWithAny` / `isEndsWithAny`) provide an `ignoreCase` overload, defaulting to `false` (case-sensitive), so the original behavior is preserved.

### Q6: Is there a deprecation period?

**No.** This is an immediate breaking change in v1.2.0. We recommend:
1. Search and count all `isStartsWith` / `isEndsWith` / `isAlphaNum` / `isMacAddress` calls before upgrading
2. Batch-replace following the rules: "remove array wrapper for single values, use *Any for multiple values, align method names"
3. Run the full test suite after upgrading to verify no regressions

### Q7: Do I need to migrate isAlphaNum() and isMacAddress()?

**A:** ✅ Yes. These two methods have been renamed in v1.2.0: `isAlphaNum()` → `isAlphaNumber()`, `isMacAddress()` → `isMac()`. This is a pure method name change (parameters and behavior unchanged), so a simple global replacement is sufficient. The corresponding annotations (`@AlphaNumber`, `@Mac`) need no changes.

---

## Need Help?

If you encounter issues during migration:

1. **Check the documentation**: Refer to the updated chain API docs in README.md
2. **Review the changelog**: See the breaking change description in [CHANGELOG.md](./CHANGELOG.md)
3. **Review examples**: Check the test code `StartsWithAnyValidationChainTest` and `EndsWithAnyValidationChainTest`
4. **Contact support**: Email vipxieliang@126.com with:
   - Your current version
   - Code snippet showing the issue
   - Compile error messages (if any)

---

## Summary Checklist

Before deploying v1.2.0 to production:

- [ ] Searched codebase for all `isStartsWith`, `isEndsWith`, `isAlphaNum`, `isMacAddress` calls
- [ ] Identified all calls passing `new String[]{...}`
- [ ] Single prefix/suffix: removed the array wrapper (`new String[]{"x"}` → `"x"`)
- [ ] Multiple prefixes/suffixes: switched to `isStartsWithAny()` / `isEndsWithAny()`
- [ ] Method renames: `isAlphaNum()` → `isAlphaNumber()`, `isMacAddress()` → `isMac()`
- [ ] Added the `ignoreCase` parameter where case-insensitive matching is needed
- [ ] Updated all chain validation test cases
- [ ] Ran full test suite to verify no regressions
- [ ] Tested validation behavior in staging environment
- [ ] Confirmed annotation validation (`@StartsWith` / `@EndsWith` / `@AlphaNumber` / `@Mac`) is unaffected

---

**Last Updated:** 2026-08-17
**Applies To:** ValidX v1.2.0+
