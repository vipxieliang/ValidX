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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.MedicalDeviceRegistration;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MedicalDeviceRegistrationValidatorTest {

    @Test
    void testValidDomesticClass3MedicalDevice() {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        assertTrue(validator.isValid("国械注准20243010001", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidDomesticClass2MedicalDevice() {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        assertTrue(validator.isValid("粤械注准20242020002", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidImportedMedicalDevice() {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        assertTrue(validator.isValid("国械注进20242030003", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidHKMacauTaiwanMedicalDevice() {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        assertTrue(validator.isValid("国械注许20242040004", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidFormat() {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        assertFalse(validator.isValid("国械注准20244010001", mock(ConstraintValidatorContext.class))); // 错误的管理类别
    }

    @Test
    void testInvalidRegistrationForm() {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        assertFalse(validator.isValid("国械注出20243010001", mock(ConstraintValidatorContext.class))); // 错误的注册形式
    }

    @Test
    void testNullAndEmptyValues() {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)), "null should return true");
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)), "empty string should return true");
    }
}