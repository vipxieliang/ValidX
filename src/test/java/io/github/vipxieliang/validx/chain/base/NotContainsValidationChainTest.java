/*
 * Copyright 2025-2026 vipxieliang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotContains链式验证测试类
 *
 * @author vipxieliang
 * @since 2026/08/07
 */
public class NotContainsValidationChainTest {

    @Test
    public void testValidNotContainsSingle() {
        ValidX validator = ValidX.init()
                .isNotContains("user123", new String[]{"admin"});
        assertTrue(validator.passed(), "Should pass when string doesn't contain the specified substring");
    }

    @Test
    public void testInvalidNotContainsSingle() {
        ValidX validator = ValidX.init()
                .isNotContains("admin123", new String[]{"admin"});
        assertFalse(validator.passed(), "Should fail when string contains the specified substring");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testValidNotContainsMultiple() {
        ValidX validator = ValidX.init()
                .isNotContains("good content", new String[]{"bad", "evil"});
        assertTrue(validator.passed(), "Should pass when string doesn't contain any of the specified substrings");
    }

    @Test
    public void testInvalidNotContainsMultiple() {
        ValidX validator = ValidX.init()
                .isNotContains("this is bad content", new String[]{"bad", "evil"});
        assertFalse(validator.passed(), "Should fail when string contains any of the specified substrings");
    }

    @Test
    public void testValidNotContainsIgnoreCase() {
        ValidX validator = ValidX.init()
                .isNotContains("user123", new String[]{"ADMIN"}, true);
        assertTrue(validator.passed(), "Should pass when string doesn't contain substring (case insensitive)");
    }

    @Test
    public void testInvalidNotContainsIgnoreCase() {
        ValidX validator = ValidX.init()
                .isNotContains("Admin123", new String[]{"admin"}, true);
        assertFalse(validator.passed(), "Should fail when substring found with ignoreCase");
    }

    @Test
    public void testNotContainsCaseSensitive() {
        ValidX validator = ValidX.init()
                .isNotContains("Admin User", new String[]{"admin"}, false);
        assertTrue(validator.passed(), "Should pass when case doesn't match and ignoreCase is false");
    }

    @Test
    public void testNullValue() {
        ValidX validator = ValidX.init()
                .isNotContains(null, new String[]{"admin"});
        assertTrue(validator.passed(), "Should pass for null values");
    }

    @Test
    public void testEmptyString() {
        ValidX validator = ValidX.init()
                .isNotContains("", new String[]{"admin"});
        assertTrue(validator.passed(), "Should pass for empty strings");
    }

    @Test
    public void testMultipleValidations() {
        ValidX validator = ValidX.init()
                .isNotContains("user123", new String[]{"admin"})
                .isNotContains("good content", new String[]{"bad"})
                .isNotContains("safe text", new String[]{"forbidden"});
        assertTrue(validator.passed(), "Should pass all validations");
    }

    @Test
    public void testMultipleValidationsWithFailure() {
        ValidX validator = ValidX.init()
                .isNotContains("user123", new String[]{"admin"})
                .isNotContains("bad content", new String[]{"bad"})
                .isNotContains("safe text", new String[]{"forbidden"});
        assertFalse(validator.passed(), "Should fail when one validation fails");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainWithFieldLabel() {
        ValidX validator = ValidX.init()
                .field("Username").isNotContains("john_doe", new String[]{"admin"})
                .field("Email").isNotContains("test@example.com", new String[]{"temp"});
        assertTrue(validator.passed(), "Should pass with field labels");
    }

    @Test
    public void testChainWithFieldLabelFailure() {
        ValidX validator = ValidX.init()
                .field("Description").isNotContains("This is admin content", new String[]{"admin"});
        assertFalse(validator.passed(), "Should fail and include field label");
    }

    @Test
    public void testNotContainsAtBeginning() {
        ValidX validator = ValidX.init()
                .isNotContains("admin user", new String[]{"admin"});
        assertFalse(validator.passed(), "Should fail when forbidden substring is at the beginning");
    }

    @Test
    public void testNotContainsAtEnd() {
        ValidX validator = ValidX.init()
                .isNotContains("user admin", new String[]{"admin"});
        assertFalse(validator.passed(), "Should fail when forbidden substring is at the end");
    }

    @Test
    public void testNotContainsInMiddle() {
        ValidX validator = ValidX.init()
                .isNotContains("user admin role", new String[]{"admin"});
        assertFalse(validator.passed(), "Should fail when forbidden substring is in the middle");
    }

    @Test
    public void testEmptySubstringArray() {
        ValidX validator = ValidX.init()
                .isNotContains("any content", new String[]{});
        assertTrue(validator.passed(), "Should pass when substring array is empty");
    }

    @Test
    public void testValidMatchAll() {
        ValidX validator = ValidX.init()
                .isNotContains("https://example.com", new String[]{"javascript:", "data:"}, false, true);
        assertTrue(validator.passed(), "Should pass when string doesn't contain all specified substrings");
    }

    @Test
    public void testInvalidMatchAll() {
        ValidX validator = ValidX.init()
                .isNotContains("javascript:alert(1)", new String[]{"javascript:", "data:"}, false, true);
        assertFalse(validator.passed(), "Should fail when string contains any of the specified substrings with matchAll=true");
    }

    @Test
    public void testMatchAllWithIgnoreCase() {
        ValidX validator = ValidX.init()
                .isNotContains("Hello World", new String[]{"ADMIN", "ROOT"}, true, true);
        assertTrue(validator.passed(), "Should pass when none of the substrings match with ignoreCase");
    }

    @Test
    public void testMatchAllPartialMatch() {
        ValidX validator = ValidX.init()
                .isNotContains("hello world", new String[]{"hello", "goodbye"}, false, true);
        assertFalse(validator.passed(), "Should fail when at least one substring is present (matchAll=true)");
    }

    @Test
    public void testValidMatchAny() {
        ValidX validator = ValidX.init()
                .isNotContains("hello world", new String[]{"script", "alert"}, false, false);
        assertTrue(validator.passed(), "Should pass when at least one substring is not present (matchAll=false)");
    }

    @Test
    public void testInvalidMatchAny() {
        ValidX validator = ValidX.init()
                .isNotContains("script alert", new String[]{"script", "alert"}, false, false);
        assertFalse(validator.passed(), "Should fail when all substrings are present (matchAll=false)");
    }

    @Test
    public void testSecurityValidationUsername() {
        ValidX validator = ValidX.init()
                .field("Username").isNotContains("normaluser", new String[]{"admin", "root", "system"}, true);
        assertTrue(validator.passed(), "Should pass security validation for normal username");
    }

    @Test
    public void testSecurityValidationUsernameFail() {
        ValidX validator = ValidX.init()
                .field("Username").isNotContains("Admin_User", new String[]{"admin", "root", "system"}, true);
        assertFalse(validator.passed(), "Should fail security validation for admin username");
    }

    @Test
    public void testSecurityValidationUrl() {
        ValidX validator = ValidX.init()
                .field("URL").isNotContains("https://example.com", new String[]{"javascript:", "data:", "vbscript:"}, false, true);
        assertTrue(validator.passed(), "Should pass security validation for safe URL");
    }

    @Test
    public void testSecurityValidationUrlFail() {
        ValidX validator = ValidX.init()
                .field("URL").isNotContains("javascript:alert(1)", new String[]{"javascript:", "data:", "vbscript:"}, false, true);
        assertFalse(validator.passed(), "Should fail security validation for malicious URL");
    }

    @Test
    public void testContentModerationValid() {
        ValidX validator = ValidX.init()
                .field("Comment").isNotContains("This is a good comment", new String[]{"spam", "offensive"}, true, true);
        assertTrue(validator.passed(), "Should pass content moderation for clean comment");
    }

    @Test
    public void testContentModerationInvalid() {
        ValidX validator = ValidX.init()
                .field("Comment").isNotContains("This is SPAM content", new String[]{"spam", "offensive"}, true, true);
        assertFalse(validator.passed(), "Should fail content moderation for spam comment");
    }
}
