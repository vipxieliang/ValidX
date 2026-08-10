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
 * StartsWithAny链式验证测试类
 *
 * @author vipxieliang
 * @since 1.1.1
 */
public class StartsWithAnyValidationChainTest {

    @Test
    void testStartsWithAnyValid() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("http://example.com", new String[]{"http://", "https://"});
        assertTrue(validator.passed(), "http:// should match");
    }

    @Test
    void testStartsWithAnyValidHttps() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("https://secure.example.com", new String[]{"http://", "https://"});
        assertTrue(validator.passed(), "https:// should match");
    }

    @Test
    void testStartsWithAnyInvalid() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});
        assertFalse(validator.passed(), "Should fail for ftp://");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testStartsWithAnyMultiplePrefixes() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("Mr. Smith", new String[]{"Mr.", "Mrs.", "Ms.", "Dr."});
        assertTrue(validator.passed());
    }

    @Test
    void testStartsWithAnyEmptyArray() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("anything", new String[]{});
        assertFalse(validator.passed(), "Empty array should fail");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testStartsWithAnyNullValue() {
        ValidX validator = ValidX.init()
                .isStartsWithAny(null, new String[]{"http://", "https://"});
        assertTrue(validator.passed(), "null value should pass");
    }

    @Test
    void testStartsWithAnyEmptyString() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("", new String[]{"http://", "https://"});
        assertTrue(validator.passed(), "empty string should pass");
    }

    @Test
    void testStartsWithAnyChinesePrefix() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("张三", new String[]{"张", "王", "李", "赵"});
        assertTrue(validator.passed());
    }

    @Test
    void testStartsWithAnyChinesePrefixInvalid() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("刘六", new String[]{"张", "王", "李", "赵"});
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testStartsWithAnyMultipleValidations() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("http://example.com", new String[]{"http://", "https://"})
                .isStartsWithAny("Mr. Smith", new String[]{"Mr.", "Mrs.", "Ms."});
        assertTrue(validator.passed());
    }

    @Test
    void testStartsWithAnyMixedValidAndInvalid() {
        ValidX validator = ValidX.init()
                .isStartsWithAny("http://example.com", new String[]{"http://", "https://"})
                .isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testStartsWithAnyWithFieldLabel() {
        ValidX validator = ValidX.init()
                .field("URL").isStartsWithAny("http://example.com", new String[]{"http://", "https://"});
        assertTrue(validator.passed(), "Should pass with field label");
    }

    @Test
    void testStartsWithAnyWithFieldLabelFailure() {
        ValidX validator = ValidX.init()
                .field("URL").isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});
        assertFalse(validator.passed(), "Should fail and include field label");
    }
}
