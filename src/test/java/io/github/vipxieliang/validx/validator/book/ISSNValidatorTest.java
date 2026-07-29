/*
 * Copyright 2025-2025 vipxieliang
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

package io.github.vipxieliang.validx.validator.book;

import io.github.vipxieliang.validx.annotations.ISSN;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ISSNValidatorTest {

    @Test
    void testValidISSNWithHyphen() {
        ISSNValidator validator = new ISSNValidator();
        assertTrue(validator.isValid("0024-9319", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidISSNWithoutHyphen() {
        ISSNValidator validator = new ISSNValidator();
        assertTrue(validator.isValid("00249319", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidISSNWithX() {
        ISSNValidator validator = new ISSNValidator();
        assertTrue(validator.isValid("0317-8471", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidISSNWrongLength() {
        ISSNValidator validator = new ISSNValidator();
        assertFalse(validator.isValid("1234567", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("123456789", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidISSNInvalidCharacters() {
        ISSNValidator validator = new ISSNValidator();
        assertFalse(validator.isValid("1234-567A", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("1234-567!", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidISSNWrongChecksum() {
        ISSNValidator validator = new ISSNValidator();
        assertFalse(validator.isValid("1234-5678", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testNullAndEmptyISSN() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ISSNValidator validator = new ISSNValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}