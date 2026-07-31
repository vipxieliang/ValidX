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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码链式验证测试类
 */
public class PasswordValidationChainTest {

    @Test
    public void testValidPasswords() {
        ValidX validator = ValidX.init();
        
        // 测试有效的密码
        validator.isPassword("Password123!");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isPassword("MySecurePass123@", 8);
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isPassword("mypassword123", 8, false, true, true, false);
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidPasswords() {
        ValidX validator = ValidX.init();
        
        // 测试无效的密码（太短）
        validator.isPassword("pass");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidX.init();
        // 测试无效的密码（缺少大写字母）
        validator.isPassword("password123!");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidX.init();
        // 测试无效的密码（缺少小写字母）
        validator.isPassword("PASSWORD123!");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidX.init();
        // 测试无效的密码（缺少数字）
        validator.isPassword("Password!");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidX.init();
        // 测试无效的密码（缺少特殊字符）
        validator.isPassword("Password123");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullAndEmptyValues() {
        ValidX validator = ValidX.init();

        // 测试null值应该通过验证（交给@NotNull处理）
        validator.isPassword(null);
        assertTrue(validator.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证（交给@NotEmpty处理）
        validator = ValidX.init();
        validator.isPassword("");
        assertTrue(validator.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testNullValue() {
        ValidX validator = ValidX.init();

        // 测试null值
        validator.isPassword(null);
        assertTrue(validator.passed(), "null值应该通过验证");
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isPassword("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Password does not meet complexity requirements"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isPassword("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("密码不符合复杂度要求"));
    }
}