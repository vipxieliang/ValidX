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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Password;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPasswordWithDefaultLength() {
        PasswordValidator validator = new PasswordValidator();
        
        // 创建模拟的Password注解实例
        Password passwordAnnotation = mock(Password.class);
        when(passwordAnnotation.minLength()).thenReturn(8); // 默认值
        when(passwordAnnotation.requireUppercase()).thenReturn(false);
        when(passwordAnnotation.requireLowercase()).thenReturn(false);
        when(passwordAnnotation.requireDigit()).thenReturn(false);
        when(passwordAnnotation.requireSpecialChar()).thenReturn(false);
        
        validator.initialize(passwordAnnotation);
        
        // 测试有效的密码（至少8个字符）
        assertTrue(validator.isValid("password123", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("testpass12", mock(ConstraintValidatorContext.class)));

        // 测试无效的密码
        assertFalse(validator.isValid("short1", mock(ConstraintValidatorContext.class))); // 太短
    }

    @Test
    void testNullAndEmptyValues() {
        PasswordValidator validator = new PasswordValidator();

        Password passwordAnnotation = mock(Password.class);
        when(passwordAnnotation.minLength()).thenReturn(8);
        when(passwordAnnotation.requireUppercase()).thenReturn(false);
        when(passwordAnnotation.requireLowercase()).thenReturn(false);
        when(passwordAnnotation.requireDigit()).thenReturn(false);
        when(passwordAnnotation.requireSpecialChar()).thenReturn(false);

        validator.initialize(passwordAnnotation);

        // null值应该返回true（交给@NotNull处理）
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
        // 空字符串应该返回true（交给@NotEmpty处理）
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
    }
    
    @Test
    void testValidPasswordWithCustomLength() {
        PasswordValidator validator = new PasswordValidator();
        
        // 创建模拟的Password注解实例，设置自定义最小长度为6
        Password passwordAnnotation = mock(Password.class);
        when(passwordAnnotation.minLength()).thenReturn(6);
        when(passwordAnnotation.requireUppercase()).thenReturn(false);
        when(passwordAnnotation.requireLowercase()).thenReturn(false);
        when(passwordAnnotation.requireDigit()).thenReturn(false);
        when(passwordAnnotation.requireSpecialChar()).thenReturn(false);
        
        validator.initialize(passwordAnnotation);
        
        // 测试有效的密码（至少6个字符）
        assertTrue(validator.isValid("pass12", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("test123", mock(ConstraintValidatorContext.class)));
        
        // 测试无效的密码
        assertFalse(validator.isValid("pas12", mock(ConstraintValidatorContext.class))); // 太短
    }
    
    @Test
    void testValidPasswordWithAllRequirements() {
        PasswordValidator validator = new PasswordValidator();
        
        // 创建模拟的Password注解实例，设置所有要求
        Password passwordAnnotation = mock(Password.class);
        when(passwordAnnotation.minLength()).thenReturn(8);
        when(passwordAnnotation.requireUppercase()).thenReturn(true);
        when(passwordAnnotation.requireLowercase()).thenReturn(true);
        when(passwordAnnotation.requireDigit()).thenReturn(true);
        when(passwordAnnotation.requireSpecialChar()).thenReturn(true);
        
        validator.initialize(passwordAnnotation);
        
        // 测试有效的密码（至少8个字符，包含大写字母、小写字母、数字和特殊字符）
        assertTrue(validator.isValid("Password123!", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("TestPass12#", mock(ConstraintValidatorContext.class)));
        
        // 测试无效的密码
        assertFalse(validator.isValid("password123!", mock(ConstraintValidatorContext.class))); // 缺少大写字母
        assertFalse(validator.isValid("PASSWORD123!", mock(ConstraintValidatorContext.class))); // 缺少小写字母
        assertFalse(validator.isValid("Password!", mock(ConstraintValidatorContext.class))); // 缺少数字
        assertFalse(validator.isValid("Password123", mock(ConstraintValidatorContext.class))); // 缺少特殊字符
        assertFalse(validator.isValid("Pass12!", mock(ConstraintValidatorContext.class))); // 太短
    }
    
    @Test
    void testValidPasswordWithMixedRequirements() {
        PasswordValidator validator = new PasswordValidator();
        
        // 创建模拟的Password注解实例，只设置部分要求
        Password passwordAnnotation = mock(Password.class);
        when(passwordAnnotation.minLength()).thenReturn(6);
        when(passwordAnnotation.requireUppercase()).thenReturn(true);
        when(passwordAnnotation.requireLowercase()).thenReturn(false);
        when(passwordAnnotation.requireDigit()).thenReturn(true);
        when(passwordAnnotation.requireSpecialChar()).thenReturn(false);
        
        validator.initialize(passwordAnnotation);
        
        // 测试有效的密码（至少6个字符，包含大写字母和数字）
        assertTrue(validator.isValid("PASS123", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("Test123", mock(ConstraintValidatorContext.class)));
        
        // 测试无效的密码
        assertFalse(validator.isValid("pass12", mock(ConstraintValidatorContext.class))); // 缺少大写字母
        assertFalse(validator.isValid("PASS", mock(ConstraintValidatorContext.class))); // 缺少数字
        assertFalse(validator.isValid("PA1", mock(ConstraintValidatorContext.class))); // 太短
    }
}