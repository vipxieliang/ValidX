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

package io.github.vipxieliang.validx.validator.certification;

import io.github.vipxieliang.validx.annotations.PMP;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * PMP证书验证器测试类
 */
public class PMPValidatorTest {

    @Test
    public void testValidPMPNumbers() {
        PMPValidator validator = new PMPValidator();
        PMP pmp = mock(PMP.class);
        validator.initialize(pmp);

        // 测试有效的7位数字PMP证书编号
        assertTrue(validator.isValid("1234567", mock(ConstraintValidatorContext.class)));
        
        // 测试有效的带前缀PMP证书编号
        assertTrue(validator.isValid("PMP123456", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("PMI123456", mock(ConstraintValidatorContext.class)));
        
        // 测试包含空格但有效的PMP证书编号
        assertTrue(validator.isValid("1 2 3 4 5 6 7", mock(ConstraintValidatorContext.class)));
        
        // 测试空值
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void testInvalidPMPNumbers() {
        PMPValidator validator = new PMPValidator();
        PMP pmp = mock(PMP.class);
        validator.initialize(pmp);

        // 测试少于7位数字
        assertFalse(validator.isValid("123456", mock(ConstraintValidatorContext.class)));
        
        // 测试非数字字符（除前缀外）
        assertFalse(validator.isValid("123456a", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("123 45@6", mock(ConstraintValidatorContext.class)));
        
        // 测试只有前缀没有数字
        assertFalse(validator.isValid("PMP", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void testNullAndEmptyPMP() {
        // 直接测试验证器，null 和空字符串应该返回 true
        PMPValidator validator = new PMPValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}