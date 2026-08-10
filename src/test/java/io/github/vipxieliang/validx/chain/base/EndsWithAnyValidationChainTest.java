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
 * EndsWithAny链式验证测试类
 *
 * @author vipxieliang
 * @since 1.1.1
 */
public class EndsWithAnyValidationChainTest {

    @Test
    void testEndsWithAnyValid() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png", ".gif"});
        assertTrue(validator.passed(), ".jpg should match");
    }

    @Test
    void testEndsWithAnyValidPng() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("image.png", new String[]{".jpg", ".jpeg", ".png", ".gif"});
        assertTrue(validator.passed(), ".png should match");
    }

    @Test
    void testEndsWithAnyInvalid() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("document.pdf", new String[]{".jpg", ".jpeg", ".png", ".gif"});
        assertFalse(validator.passed(), "Should fail for .pdf");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testEndsWithAnyMultipleSuffixes() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("document.txt", new String[]{".txt", ".doc", ".docx", ".pdf"});
        assertTrue(validator.passed());
    }

    @Test
    void testEndsWithAnyEmptyArray() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("anything", new String[]{});
        assertFalse(validator.passed(), "Empty array should fail");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testEndsWithAnyNullValue() {
        ValidX validator = ValidX.init()
                .isEndsWithAny(null, new String[]{".jpg", ".png"});
        assertTrue(validator.passed(), "null value should pass");
    }

    @Test
    void testEndsWithAnyEmptyString() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("", new String[]{".jpg", ".png"});
        assertTrue(validator.passed(), "empty string should pass");
    }

    @Test
    void testEndsWithAnyChineseSuffix() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("张先生", new String[]{"先生", "女士", "小姐"});
        assertTrue(validator.passed());
    }

    @Test
    void testEndsWithAnyChineseSuffixInvalid() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("李教授", new String[]{"先生", "女士", "小姐"});
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testEndsWithAnyMultipleValidations() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("photo.jpg", new String[]{".jpg", ".png"})
                .isEndsWithAny("document.txt", new String[]{".txt", ".doc"});
        assertTrue(validator.passed());
    }

    @Test
    void testEndsWithAnyMixedValidAndInvalid() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("photo.jpg", new String[]{".jpg", ".png"})
                .isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testEndsWithAnyCaseSensitive() {
        ValidX validator = ValidX.init()
                .isEndsWithAny("photo.JPG", new String[]{".jpg", ".png"});
        assertFalse(validator.passed(), "should be case sensitive");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    void testEndsWithAnyWithFieldLabel() {
        ValidX validator = ValidX.init()
                .field("Image").isEndsWithAny("photo.jpg", new String[]{".jpg", ".png"});
        assertTrue(validator.passed(), "Should pass with field label");
    }

    @Test
    void testEndsWithAnyWithFieldLabelFailure() {
        ValidX validator = ValidX.init()
                .field("Image").isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});
        assertFalse(validator.passed(), "Should fail and include field label");
    }
}
