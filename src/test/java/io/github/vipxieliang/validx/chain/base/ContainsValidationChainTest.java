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
 * Contains链式验证测试类
 *
 * @author vipxieliang
 * @since 2026/07/31
 */
public class ContainsValidationChainTest {

    @Test
    public void testValidContainsSingle() {
        ValidX validator = ValidX.init()
                .isContains("hello world", new String[]{"hello"});
        assertTrue(validator.passed(), "Should pass when string contains the specified substring");
    }

    @Test
    public void testInvalidContainsSingle() {
        ValidX validator = ValidX.init()
                .isContains("goodbye world", new String[]{"hello"});
        assertFalse(validator.passed(), "Should fail when string doesn't contain the specified substring");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testValidContainsMultiple() {
        ValidX validator = ValidX.init()
                .isContains("hello world", new String[]{"hello", "goodbye"});
        assertTrue(validator.passed(), "Should pass when string contains any of the specified substrings");
    }

    @Test
    public void testInvalidContainsMultiple() {
        ValidX validator = ValidX.init()
                .isContains("test string", new String[]{"hello", "world"});
        assertFalse(validator.passed(), "Should fail when string doesn't contain any of the specified substrings");
    }

    @Test
    public void testValidContainsIgnoreCase() {
        ValidX validator = ValidX.init()
                .isContains("Hello World", new String[]{"hello"}, true);
        assertTrue(validator.passed(), "Should pass when ignoreCase is true");
    }

    @Test
    public void testInvalidContainsIgnoreCase() {
        ValidX validator = ValidX.init()
                .isContains("Hello World", new String[]{"goodbye"}, true);
        assertFalse(validator.passed(), "Should fail when substring not found even with ignoreCase");
    }

    @Test
    public void testContainsCaseSensitive() {
        ValidX validator = ValidX.init()
                .isContains("Hello World", new String[]{"hello"}, false);
        assertFalse(validator.passed(), "Should fail when case doesn't match and ignoreCase is false");
    }

    @Test
    public void testNullValue() {
        ValidX validator = ValidX.init()
                .isContains(null, new String[]{"hello"});
        assertTrue(validator.passed(), "Should pass for null values");
    }

    @Test
    public void testEmptyString() {
        ValidX validator = ValidX.init()
                .isContains("", new String[]{"hello"});
        assertTrue(validator.passed(), "Should pass for empty strings (following null handling pattern)");
    }

    @Test
    public void testMultipleValidations() {
        ValidX validator = ValidX.init()
                .isContains("hello world", new String[]{"hello"})
                .isContains("goodbye world", new String[]{"world"})
                .isContains("test string", new String[]{"test"});
        assertTrue(validator.passed(), "Should pass all validations");
    }

    @Test
    public void testMultipleValidationsWithFailure() {
        ValidX validator = ValidX.init()
                .isContains("hello world", new String[]{"hello"})
                .isContains("goodbye world", new String[]{"missing"})
                .isContains("test string", new String[]{"test"});
        assertFalse(validator.passed(), "Should fail when one validation fails");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainWithFieldLabel() {
        ValidX validator = ValidX.init()
                .field("Username").isContains("john_doe", new String[]{"_"})
                .field("Email").isContains("test@example.com", new String[]{"@"});
        assertTrue(validator.passed(), "Should pass with field labels");
    }

    @Test
    public void testChainWithFieldLabelFailure() {
        ValidX validator = ValidX.init()
                .field("Description").isContains("This is a test", new String[]{"missing"});
        assertFalse(validator.passed(), "Should fail and include field label");
    }

    @Test
    public void testContainsAtBeginning() {
        ValidX validator = ValidX.init()
                .isContains("hello world", new String[]{"hello"});
        assertTrue(validator.passed(), "Should pass when substring is at the beginning");
    }

    @Test
    public void testContainsAtEnd() {
        ValidX validator = ValidX.init()
                .isContains("say hello", new String[]{"hello"});
        assertTrue(validator.passed(), "Should pass when substring is at the end");
    }

    @Test
    public void testContainsInMiddle() {
        ValidX validator = ValidX.init()
                .isContains("say hello world", new String[]{"hello"});
        assertTrue(validator.passed(), "Should pass when substring is in the middle");
    }

    @Test
    public void testEmptySubstringArray() {
        ValidX validator = ValidX.init()
                .isContains("hello world", new String[]{});
        assertTrue(validator.passed(), "Should pass when substring array is empty");
    }

    @Test
    public void testValidMatchAll() {
        ValidX validator = ValidX.init()
                .isContains("test@example.com", new String[]{"@", "."}, false, true);
        assertTrue(validator.passed(), "Should pass when string contains all specified substrings");
    }

    @Test
    public void testInvalidMatchAll() {
        ValidX validator = ValidX.init()
                .isContains("test@example", new String[]{"@", "."}, false, true);
        assertFalse(validator.passed(), "Should fail when string doesn't contain all specified substrings");
    }

    @Test
    public void testMatchAllWithIgnoreCase() {
        ValidX validator = ValidX.init()
                .isContains("Hello@World.Com", new String[]{"HELLO", "@", ".COM"}, true, true);
        assertTrue(validator.passed(), "Should pass when all substrings match with ignoreCase");
    }

    @Test
    public void testMatchAllPartialMatch() {
        ValidX validator = ValidX.init()
                .isContains("hello world", new String[]{"hello", "goodbye"}, false, true);
        assertFalse(validator.passed(), "Should fail when not all substrings are present (matchAll=true)");
    }
}
