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

import io.github.vipxieliang.validx.annotations.IPC;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class IPCValidatorTest {

    @Test
    void testValidIPC() {
        IPCValidator validator = new IPCValidator();
        assertTrue(validator.isValid("A01B1/00", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidIPCWithSubgroup() {
        IPCValidator validator = new IPCValidator();
        assertTrue(validator.isValid("A01B1/01", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidIPCWithLongerMainGroup() {
        IPCValidator validator = new IPCValidator();
        assertTrue(validator.isValid("A01B12/00", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidIPCWithLongerSubgroup() {
        IPCValidator validator = new IPCValidator();
        assertTrue(validator.isValid("A01B1/1234", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidIPCWrongSection() {
        IPCValidator validator = new IPCValidator();
        assertFalse(validator.isValid("I01B1/00", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidIPCWrongFormat() {
        IPCValidator validator = new IPCValidator();
        assertFalse(validator.isValid("A01B/00", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidIPCInvalidCharacters() {
        IPCValidator validator = new IPCValidator();
        assertFalse(validator.isValid("A01B1/A0", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidIPCTooLongMainGroup() {
        IPCValidator validator = new IPCValidator();
        assertFalse(validator.isValid("A01B1234/00", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidIPCTooLongSubgroup() {
        IPCValidator validator = new IPCValidator();
        assertFalse(validator.isValid("A01B1/12345", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testNullAndEmptyIPC() {
        IPCValidator validator = new IPCValidator();
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)), "null should return true");
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)), "empty string should return true");
    }
}