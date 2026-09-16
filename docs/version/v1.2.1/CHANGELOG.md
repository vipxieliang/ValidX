# ValidX v1.2.1 Changelog

**Release Date:** TBD

This document records the changes from v1.2.0 to v1.2.1.

## Change Overview

- ✨ [New Features](#new-features-)
  - New `@IMSI` International Mobile Subscriber Identity validation annotation (ITU-T E.212 / 3GPP TS 23.003)
  - New `@ICCID` Integrated Circuit Card Identifier validation annotation (ITU-T E.118 / GSMA SGP.22, 20 digits + Luhn check digit)
  - New `@Ascii` ASCII character validation annotation (full ASCII 0x00~0x7F, including control characters)
  - New `@Printable` printable ASCII character validation annotation (fixed at 0x20~0x7E, no control characters allowed)
- ✅ [No Breaking Changes](#no-breaking-changes-)
  - v1.2.1 is fully backward compatible with v1.2.0; no migration required
- ♻️ [Internal Improvements](#internal-improvements-)
  - Extracted a shared `LuhnUtils`, unifying the Luhn check used by `@BankCard` / `@ICCID` / `@IMEI` (no behavior change)
- 🌍 [Internationalization Support](#internationalization-support-)
  - Full 9-language message support for the new annotation

---

## New Features ✨

### @IMSI International Mobile Subscriber Identity Validation Annotation

Added an IMSI validation annotation to verify that a string is a valid International Mobile Subscriber Identity (IMSI).

**Background — IMSI vs IMEI:**
- IMSI identifies the **subscriber / SIM card**; IMEI identifies the **device**.
- IMSI structure per ITU-T E.212 / 3GPP TS 23.003: `MCC` (3 digits, Mobile Country Code) + `MNC` (2-3 digits, Mobile Network Code) + `MSIN` (up to 10 digits, Mobile Subscriber Identification Number).
- Mainland China uses MCC `460` (e.g., China Mobile MNC `00`, China Telecom MNC `03`).

**Features:**
- Validates that the string is an IMSI: spaces and hyphens are stripped first, then the remaining content must be **14-15 pure digits**
- Accepts common formatted input such as `4600-0123-4567-890` or `460 001 234 567 890`
- Null and empty strings pass validation by default
- Full internationalization support (9 languages)
- IMSI has **no built-in check digit** (unlike IMEI's Luhn or ID card numbers); format validation can only enforce length and character set. Real-world authenticity (whether the MCC/MNC belongs to a real carrier and the subscription actually exists) must be confirmed on the carrier network side.
- Chain API: `isIMSI(Object value)`

**Annotation Examples:**

```java
public class SimCardDTO {
    // Example 1: Standard 15-digit IMSI (China Mobile: MCC 460 + MNC 00)
    @IMSI
    private String imsi;  // "460001234567890" passes

    // Example 2: 14-digit IMSI (MNC 2 digits + MSIN 9 digits)
    @IMSI
    private String shortImsi;  // "46001123456789" passes

    // Example 3: Formatted input with separators
    @IMSI
    private String formattedImsi;  // "4600-0123-4567-890" passes
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Basic usage
validator.field("IMSI").isIMSI("460001234567890");

// Formatted input (hyphens / spaces are stripped before validation)
validator.isIMSI("4600-0123-4567-890");
validator.isIMSI("460 001 234 567 890");

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: SIM card registration / activation
public class SimActivationDTO {
    @NotBlank(message = "IMSI is required")
    @IMSI
    private String imsi;
}

// Use Case 2: IoT / eSIM device onboarding record validation
public class IotDeviceDTO {
    @IMSI
    private String imsi;  // Device-bound subscriber identity
}

// Use Case 3: Chain validation for user profile with carrier subscription
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("IMSI").isIMSI("460031234567890");
```

**Notes:**
- Spaces and hyphens are stripped before validation; other separators are not accepted
- Full-width digits (e.g., `４６０...`) are rejected — they are not part of the ASCII digit set
- Null and empty strings pass validation (use with `@NotNull` or `@NotBlank` for required fields)
- IMSI has no check digit by design; do not apply IMEI's Luhn algorithm or ID-card-style checksums to it
- Common use cases: SIM/eSIM/UICC card registration, IoT device access, subscriber identity field validation

---

### @ICCID Integrated Circuit Card Identifier Validation Annotation

Added an ICCID validation annotation to verify that a string is a valid Integrated Circuit Card Identifier (ICCID).

**Background — ICCID vs IMSI vs IMEI:**
- ICCID is printed on the SIM/eSIM card body and uniquely identifies the **card** itself; IMSI identifies the **subscriber/SIM subscription**; IMEI identifies the **device**
- Modern UICC/eSIM ICCIDs are 20 digits per ITU-T E.118 / GSMA SGP.22: `89` (telecom industry Major Industry Identifier) + country code (ISO 3166-1, `86` for China) + carrier code + account number + a Luhn check digit
- Mainland China SIM cards usually start with `8986` (e.g., `8986 0115 2449 3909 2661`)

**Features:**
- Validates that the string is an ICCID: spaces and hyphens are stripped first, then the remaining content must be **20 pure digits**
- The whole string must pass the **Luhn** check (the 20th digit is the check digit computed from the preceding 19 digits, guarding against input typos / transpositions)
- Unlike IMSI (which has no built-in check digit), ICCID carries a Luhn check digit
- Null and empty strings pass validation by default
- Full internationalization support (9 languages)
- The `89`/`8986` prefix is descriptive context, not an enforced rule; whether the card actually exists must be confirmed by the carrier side
- Chain API: `isICCID(Object value)`

**Annotation Examples:**

```java
public class SimCardDTO {
    // Example 1: Standard 20-digit ICCID (mainland China card, 8986 prefix)
    @ICCID
    private String iccid;  // "89860115244939092661" passes

    // Example 2: Another valid 20-digit ICCID
    @ICCID
    private String otherIccid;  // "89866604876475938248" passes
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

// Basic usage
validator.field("ICCID").isICCID("89860115244939092661");

// Formatted input (hyphens / spaces are stripped before validation)
validator.isICCID("8986-0115-2449-3909-2661");
validator.isICCID("8986 0115 2449 3909 2661");

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: SIM / eSIM card registration and archival
public class SimRegistrationDTO {
    @NotBlank(message = "ICCID is required")
    @ICCID
    private String iccid;  // Printed on the card body
}

// Use Case 2: IoT module / eSIM device inventory management
public class IotModuleDTO {
    @ICCID
    private String iccid;
}

// Use Case 3: Chain validation with input scanning (bar code / OCR)
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("ICCID").isICCID("89860115244939092661");
```

**Notes:**
- Spaces and hyphens are stripped before validation; other separators are not accepted
- Full-width digits (e.g., `８９８６...`) are rejected — they are not part of the ASCII digit set
- Null and empty strings pass validation (use with `@NotNull` or `@NotBlank` for required fields)
- The Luhn check catches typos but does not prove the card is real — card authenticity must be confirmed by the carrier side
- Common use cases: SIM/eSIM/UICC card registration, IoT module inventory, ICCID barcode/OCR input validation

---

### @Ascii ASCII Character Validation Annotation

Added an ASCII validation annotation to verify that a string contains only ASCII characters (Unicode **0x00~0x7F**, including all 33 control characters).

**Background:**
- ASCII (American Standard Code for Information Interchange) covers 7-bit code points 0x00~0x7F
- This annotation follows the **textbook definition**, equivalent to C's `isascii()` and Python's `str.isascii()`
- **Printable ASCII** (0x20~0x7E) excludes the 33 control characters (0x00~0x1F + 0x7F) such as TAB, LF, CR, DEL — that case is covered by the separate `@Printable` annotation
- Multi-line text needs "newlines and tabs allowed, but no Chinese or Emoji" — exactly the 0x00~0x7F range
- Many system integration scenarios require "pure ASCII channels": protocol codes, terminal commands, SN/IMEI labels, etc.
- During CSV / TXT file import, pre-screening for non-ASCII content (Chinese / Japanese / Emoji) is a common requirement

**Features:**
- Validates that the string contains only ASCII characters (0x00~0x7F, control characters included)
- Newlines (`\n`), tabs (`\t`), carriage returns (`\r`), NUL (0x00) and DEL (0x7F) are all allowed
- Non-ASCII characters (≥0x80, e.g., Chinese, Japanese, Emoji, full-width digits) are rejected
- No configurable parameters — the semantics are fixed by the definition of ASCII
- Null and empty strings pass validation by default
- Full internationalization support (9 languages)
- Chain API: `isAscii(Object value)`

**Relationship with `@Printable`:** complementary and non-overlapping:
- `@Ascii` (0x00~0x7F) — allows `"\n"` and `"\t"`, but rejects Chinese / Emoji (common for multi-line text)
- `@Printable` (0x20~0x7E) — even `"\n"` is rejected; characters must be fully visible (common for single-line text / labels)

In short: **use `@Ascii` for multi-line text, `@Printable` for single-line / must-be-visible content.**

**Annotation Examples:**

```java
public class ContentDTO {
    // Multi-line text: newlines allowed, but no Chinese / Emoji
    @Ascii
    private String description;  // "line1\nline2" passes; "你好" fails

    // Need fully visible characters? Use @Printable instead
    @Printable
    private String nickname;     // "Tom & Jerry" passes; "Tom\tJerry" fails
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();

validator.field("Description").isAscii("line1\nline2");   // passes (control chars are ASCII)
validator.field("Nickname").isPrintable("Tom & Jerry");   // passes

// Check validation result
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**

```java
// Use Case 1: multi-line description — newlines allowed, but no Chinese / Emoji
public class ArticleDTO {
    @NotBlank(message = "Description is required")
    @Ascii
    private String description;
}

// Use Case 2: protocol / terminal command field
public class CommandDTO {
    @NotBlank(message = "Command is required")
    @Ascii
    private String command;
}

// Use Case 3: CSV import — reject rows containing non-ASCII (Chinese / Emoji) to avoid silent garbling
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("CSV Cell").isAscii(row[0]);
```

**Notes:**
- Follows the textbook definition: ASCII = 0x00~0x7F (128 characters, 33 of which are control characters)
- Only the full 0x00~0x7F range is allowed; every non-ASCII character (≥0x80) is rejected
- Null and empty strings pass validation (use with `@NotNull` or `@NotBlank` for required fields)
- Common use cases: multi-line text / description / remarks, protocol code / terminal command fields, SN / IMEI raw labels, CSV / TXT import non-ASCII pre-check, system integration "no non-ASCII" contract verification

---

### @Printable Printable ASCII Character Validation Annotation

Added a `@Printable` annotation to verify that a string contains only **printable ASCII characters** (Unicode 0x20~0x7E).

**Background:**
- "Printable character" is a well-established concept: PHP's `ctype_print()`, C's `isprint()`, Python's `str.isprintable()`
- Printable ASCII = space (0x20) + all visible ASCII up to `~` (0x7E); it excludes the 33 control characters
- Many business scenarios care about "is the character visible / displayable on screen" rather than "is the character inside the ASCII encoding boundary"

**Relationship with `@Ascii`:** complementary and non-overlapping — each covers exactly one half of what used to be a single boolean switch:
- `@Ascii` (0x00~0x7F) — allows `"\n"` and `"\t"`, but rejects Chinese / Emoji (common for multi-line text)
- `@Printable` (0x20~0x7E) — even `"\n"` is rejected; characters must be fully visible (common for single-line text / labels)

**Features:**
- Validates that the string contains only printable ASCII (0x20~0x7E)
- Always rejects all 33 ASCII control characters (0x00~0x1F and 0x7F)
- Always rejects non-ASCII characters (Chinese, Japanese, Emoji, full-width digits, etc.)
- Null and empty strings pass validation by default
- Full internationalization support (9 languages)
- Chain API: `isPrintable(Object value)`

**Annotation Examples:**

```java
public class ProfileDTO {
    // Reject TAB / LF / CR / DEL — only visible characters allowed
    @Printable
    private String nickname;  // "Tom & Jerry" passes; "Tom\tJerry" fails

    @NotBlank(message = "comment is required")
    @Printable
    private String comment;
}
```

**Chain API Examples:**

```java
ValidX validator = ValidX.init();
validator.field("Nickname").isPrintable("Alice");
validator.field("Comment").isPrintable("Hello, world!");
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**Real-World Use Cases:**
- User nickname / comment / review content (must be visible, no invisible control bytes)
- Print labels, receipts, SMS body
- Export TXT / log files without control characters to avoid display glitches
- UI input box semantic-level constraint ("content the user can see")

**Notes:**
- Complements `@Ascii` instead of duplicating it: `@Ascii` allows control characters, `@Printable` requires fully visible characters
- All 33 ASCII control characters (0x00~0x1F + 0x7F) are rejected
- All non-ASCII characters (≥0x80) are rejected
- Null and empty strings pass validation (use with `@NotNull` or `@NotBlank` for required fields)

---

## No Breaking Changes ✅

v1.2.1 contains **no breaking changes** and is fully backward compatible with v1.2.0:

- No chain API signatures were changed or removed (the new `isIMSI()`, `isICCID()`, `isAscii()`, and `isPrintable()` are purely additive)
- No annotation semantics were altered
- No dependency or configuration changes
- Upgrade from v1.2.0 is a drop-in replacement; no migration steps required

---

## Internal Improvements ♻️

- Added the utility class `io.github.vipxieliang.validx.util.LuhnUtils`, consolidating three duplicated Luhn implementations previously scattered across `BankCardValidator`, `IMEIValidator`, and `ICCIDValidator` into a single method `LuhnUtils.isValid(String)`
- `@BankCard`, `@ICCID`, and `@IMEI` now share the same implementation, eliminating duplicated code
- Purely internal refactoring — **external behavior is unchanged** (all existing tests pass)

---

## Internationalization Support 🌍

The new annotation supports the following 9 languages:

- **Chinese (Simplified)** - `ValidationMessages.properties` and `ValidationMessages_zh.properties`
- **English** - `ValidationMessages_en.properties`
- **Japanese** - `ValidationMessages_ja.properties`
- **Korean** - `ValidationMessages_ko.properties`
- **French** - `ValidationMessages_fr.properties`
- **German** - `ValidationMessages_de.properties`
- **Spanish** - `ValidationMessages_es.properties`
- **Russian** - `ValidationMessages_ru.properties`

**Error Message:**
- `@IMSI`: "Invalid IMSI number format" (message key: `io.github.vipxieliang.validx.annotation.imsi`)
- `@ICCID`: "Invalid ICCID number format" (message key: `io.github.vipxieliang.validx.annotation.iccid`)
- `@Ascii`: "Can only contain ASCII characters (0x00-0x7F, including control characters)" (message key: `io.github.vipxieliang.validx.annotation.ascii`)
- `@Printable`: "Can only contain printable ASCII characters (0x20-0x7E, excluding control characters)" (message key: `io.github.vipxieliang.validx.annotation.printable`)

All language packs maintain consistent message format with proper Unicode encoding.

---

## Testing Coverage 🧪

Comprehensive test coverage for the new feature (both annotation and chain paths):

**Validator Tests (Bean Validation Framework):**
- `IMSIValidatorTest`: 3 test methods covering null/empty values, 6 valid cases (China Mobile `46000...`, China Telecom `46003...`, US AT&T `310260...`, 14-digit format, hyphen-separated, space-separated) and 7 invalid cases (13/16-digit out-of-range lengths, embedded letters, full-width digits)

**Chain Validation Tests:**
- `IMSIValidationChainTest`: 3 test methods covering null/empty values, valid values (standard 15-digit, hyphen-separated, space-separated, 14-digit) and invalid values (too long, too short, non-digit characters)

**ICCID Validator Tests (Bean Validation Framework):**
- `ICCIDValidatorTest`: 3 test methods covering null/empty values, 7 valid cases (`8986`-prefixed 20-digit numbers passing Luhn, hyphen-separated, space-separated) and 7 invalid cases (19/21/22-digit out-of-range lengths, broken Luhn check digit, embedded letters, full-width digits)

**ICCID Chain Validation Tests:**
- `ICCIDValidationChainTest`: 3 test methods covering null/empty values, valid values (standard 20-digit, hyphen-separated, space-separated, another Luhn-valid number) and invalid values (too short, too long, wrong check digit, non-digit characters)

**Ascii Validator Tests (Bean Validation Framework):**
- `AsciiValidatorTest`: 24 test methods (annotation-based, using `Validator` against DTOs annotated with `@Ascii`) covering acceptance of plain ASCII (`Hello, World!`, alphanumeric, punctuation, space), acceptance of control characters (LF / TAB / CR / NUL 0x00 / DEL 0x7F), acceptance of the full 0x00~0x7F range plus both boundaries, rejection of non-ASCII (Chinese / Japanese / Emoji / 0x80 / 0xFF / full-width digit U+FF11), the boundary contrast with `@Printable` (newline passes `@Ascii` but fails `@Printable`), and null / empty values passing

**Ascii Chain Validation Tests:**
- `AsciiValidationChainTest`: 13 test methods covering plain ASCII, control characters (TAB / LF) and multi-line text acceptance, the full 0x00~0x7F range, rejection of non-ASCII (Chinese / Emoji / 0x80 / mixed with control chars), the contrast with `isPrintable`, null / empty values, and chaining with other rules

**Printable Validator Tests (Bean Validation Framework):**
- `PrintableValidatorTest`: 24 test methods (annotation-based, using `Validator` against DTOs annotated with `@Printable`) covering valid printable ASCII (`Hello, World!`, alphanumeric, punctuation, space, leading/trailing space, the full 0x20~0x7E range, lower boundary 0x20, upper boundary 0x7E); rejection of all 33 ASCII control characters (0x00~0x1F and 0x7F: TAB, LF, CR, DEL, NUL, last C0 char 0x1F, etc.), rejection of non-ASCII (Chinese, Japanese, Emoji, full-width digit 0xFF11, 0x80, 0xFF); null / empty values pass

**Printable Chain Validation Tests:**
- `PrintableValidationChainTest`: 20 test methods covering null / empty values, valid printable ASCII strings (including lower/upper boundary and full range), rejection of control characters (TAB / LF / CR / DEL / NUL), rejection of non-ASCII (Chinese, Emoji, 0x80), and chaining with other rules (`isUpper`, multiple `isPrintable` calls, multi-failure accumulation)

**Total:** 81 new test methods, all passing ✅

---

## Related Links 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.2.1)
- 📖 [Full Documentation](../../../README.md)
- 🐛 [Report Issues](https://github.com/vipxieliang/ValidX/issues)
- 💡 [Feature Requests](https://github.com/vipxieliang/ValidX/issues/new)

---

Released with ❤️ by the ValidX Team
