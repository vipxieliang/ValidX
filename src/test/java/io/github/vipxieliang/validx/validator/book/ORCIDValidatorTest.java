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

import io.github.vipxieliang.validx.annotations.ORCID;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ORCIDValidatorTest {

    @Test
    void testValidORCIDWithHyphen() {
        ORCIDValidator validator = new ORCIDValidator();
        assertTrue(validator.isValid("0000-0002-1825-0097", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidORCIDWithoutHyphen() {
        ORCIDValidator validator = new ORCIDValidator();
        assertTrue(validator.isValid("0000000218250097", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidORCIDWithX() {
        ORCIDValidator validator = new ORCIDValidator();
        assertTrue(validator.isValid("0000-0001-5109-3700", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidORCIDWrongLength() {
        ORCIDValidator validator = new ORCIDValidator();
        assertFalse(validator.isValid("0000-0002-1825-009", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("0000-0002-1825-00971", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidORCIDInvalidCharacters() {
        ORCIDValidator validator = new ORCIDValidator();
        assertFalse(validator.isValid("0000-0002-1825-009A", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("0000-0002-1825-009!", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidORCIDWrongChecksum() {
        ORCIDValidator validator = new ORCIDValidator();
        assertFalse(validator.isValid("0000-0002-1825-0099", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testNullAndEmptyORCID() {
        ORCIDValidator validator = new ORCIDValidator();
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)), "null should return true");
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)), "empty string should return true");
    }
}