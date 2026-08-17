# ValidX Migration Guide: v1.1.0 → v1.2.0

This document describes the breaking changes and migration steps when upgrading from v1.1.0 to v1.2.0.

---

## Overview

Version 1.2.0 introduces a **breaking change** to the `isStartsWith()` and `isEndsWith()` methods in the chain API: the parameter type has changed from `String[]` to `String` to better align with their single-value validation purpose.

**Impact Level**: 🟡 **MEDIUM** - Affects only users of the `isStartsWith()` or `isEndsWith()` chain API methods; annotation-based validation (`@StartsWith`, `@EndsWith`) is completely unaffected.

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

## Migration Steps

### Step 1: Identify Affected Code

Search your codebase for `isStartsWith` and `isEndsWith` calls in the chain API:

```bash
# Search for chain API usage
grep -rn "isStartsWith\|isEndsWith" --include="*.java" your-project/
```

Look for:
- Chain validation calls using `isStartsWith()` or `isEndsWith()`
- Calls passing `new String[]{...}` arrays as the second parameter (these need migration)
- Optionally search for `@StartsWith` / `@EndsWith` annotations (no migration needed, but confirms the scope)

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

## Quick Reference: API Mapping

| Use Case | v1.1.0 | v1.2.0 | Notes |
|----------|--------|--------|-------|
| Single prefix | `isStartsWith(value, new String[]{p})` | `isStartsWith(value, p)` | ⚠️ **Remove array wrapper** |
| Single suffix | `isEndsWith(value, new String[]{s})` | `isEndsWith(value, s)` | ⚠️ **Remove array wrapper** |
| Multiple prefixes | `isStartsWith(value, new String[]{p1, p2})` | `isStartsWithAny(value, new String[]{p1, p2})` | ⚠️ **Use *Any methods** |
| Multiple suffixes | `isEndsWith(value, new String[]{s1, s2})` | `isEndsWithAny(value, new String[]{s1, s2})` | ⚠️ **Use *Any methods** |
| Case-insensitive | Not supported | `isStartsWith(value, p, true)` / `isStartsWithAny(value, prefixes, true)` | ✅ New feature |
| Annotation (single) | `@StartsWith` / `@EndsWith` | `@StartsWith` / `@EndsWith` | ✅ No change needed |
| Annotation (multiple) | Not supported | `@StartsWithAny` / `@EndsWithAny` | ✅ New feature |

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
1. Search and count all `isStartsWith` / `isEndsWith` calls before upgrading
2. Batch-replace following the rules: "remove array wrapper for single values, use *Any for multiple values"
3. Run the full test suite after upgrading to verify no regressions

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

- [ ] Searched codebase for all `isStartsWith` and `isEndsWith` calls
- [ ] Identified all calls passing `new String[]{...}`
- [ ] Single prefix/suffix: removed the array wrapper (`new String[]{"x"}` → `"x"`)
- [ ] Multiple prefixes/suffixes: switched to `isStartsWithAny()` / `isEndsWithAny()`
- [ ] Added the `ignoreCase` parameter where case-insensitive matching is needed
- [ ] Updated all chain validation test cases
- [ ] Ran full test suite to verify no regressions
- [ ] Tested validation behavior in staging environment
- [ ] Confirmed annotation validation (`@StartsWith` / `@EndsWith`) is unaffected

---

**Last Updated:** 2026-08-17
**Applies To:** ValidX v1.2.0+
