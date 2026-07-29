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

package io.github.vipxieliang.validx.validator.education;

import io.github.vipxieliang.validx.annotations.Doctor;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * 医师资格证验证器测试类
 */
public class DoctorQualificationValidatorTest {

    @Test
    public void testValidDoctorQualificationNumbers() {
        DoctorValidator validator = new DoctorValidator();
        Doctor doctorQualification = mock(Doctor.class);
        validator.initialize(doctorQualification);

        // 测试有效的医师资格证编号 (27位数字，使用18位身份证号码)
        assertTrue(validator.isValid("202511110440608197310039910", mock(ConstraintValidatorContext.class)));
        
        // 测试有效的医师资格证编号 (27位，包含X)
        assertTrue(validator.isValid("202511110440524198001010013", mock(ConstraintValidatorContext.class)));
        
        // 测试空值
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void testInvalidDoctorQualificationNumbers() {
        DoctorValidator validator = new DoctorValidator();
        Doctor doctorQualification = mock(Doctor.class);
        validator.initialize(doctorQualification);

        // 测试无效的医师资格证编号（不是24或27位）
        assertFalse(validator.isValid("20251111012345678901234", mock(ConstraintValidatorContext.class))); // 23位
        assertFalse(validator.isValid("2025111101234567890123456", mock(ConstraintValidatorContext.class))); // 25位
        assertFalse(validator.isValid("2025111101234567890123456789", mock(ConstraintValidatorContext.class))); // 28位
        
        // 测试包含非数字字符（除最后一位X外）
        assertFalse(validator.isValid("2025111101234567890123A5", mock(ConstraintValidatorContext.class))); // 包含字母
        assertFalse(validator.isValid("20251111012345678901234!", mock(ConstraintValidatorContext.class))); // 包含特殊字符
        
        // 测试无效的年份
        assertFalse(validator.isValid("180011110123456789012345", mock(ConstraintValidatorContext.class))); // 年份过早
        assertFalse(validator.isValid("210011110123456789012345", mock(ConstraintValidatorContext.class))); // 年份过晚
        
        // 测试无效的省级代码
        assertFalse(validator.isValid("202599110123456789012345", mock(ConstraintValidatorContext.class))); // 无效省级代码99
        
        // 测试无效的级别代码
        assertFalse(validator.isValid("202511510123456789012345", mock(ConstraintValidatorContext.class))); // 无效级别代码5
        
        // 测试无效的类别代码
        assertFalse(validator.isValid("202511199123456789012345", mock(ConstraintValidatorContext.class))); // 无效类别代码99
    }

    @Test
    public void testNullAndEmptyDoctor() {
        // 直接测试验证器，null 和空字符串应该返回 true
        DoctorValidator validator = new DoctorValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}