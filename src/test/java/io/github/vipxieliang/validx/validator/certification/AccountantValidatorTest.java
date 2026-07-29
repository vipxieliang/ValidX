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

import io.github.vipxieliang.validx.annotations.Accountant;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * 会计资格证书验证器测试类
 */
public class AccountantValidatorTest {

    @Test
    public void testValidAccountantNumbers() {
        AccountantValidator validator = new AccountantValidator();
        Accountant accountant = mock(Accountant.class);
        validator.initialize(accountant);

        // 测试有效的会计资格证书编号 (使用有效的省级代码)
        assertTrue(validator.isValid("21110203451", mock(ConstraintValidatorContext.class)));
        
        // 测试另一个有效的会计资格证书编号 (使用有效的省级代码)
        assertTrue(validator.isValid("22310512342", mock(ConstraintValidatorContext.class)));
        
        // 测试空值
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void testInvalidAccountantNumbers() {
        AccountantValidator validator = new AccountantValidator();
        Accountant accountant = mock(Accountant.class);
        validator.initialize(accountant);

        // 测试位数不正确的编号
        assertFalse(validator.isValid("2101020345", mock(ConstraintValidatorContext.class))); // 10位
        assertFalse(validator.isValid("210102034512", mock(ConstraintValidatorContext.class))); // 12位
        
        // 测试包含非数字字符
        assertFalse(validator.isValid("2101020345a", mock(ConstraintValidatorContext.class))); // 包含字母
        assertFalse(validator.isValid("2101020345!", mock(ConstraintValidatorContext.class))); // 包含特殊字符
        
        // 测试无效的省级代码
        assertFalse(validator.isValid("21990203451", mock(ConstraintValidatorContext.class))); // 无效省级代码99
        
        // 测试无效的机构识别代码
        assertFalse(validator.isValid("21010253451", mock(ConstraintValidatorContext.class))); // 机构识别代码为5
        
        // 测试无效的证书类别代码
        assertFalse(validator.isValid("21010203456", mock(ConstraintValidatorContext.class))); // 证书类别代码为6
    }

    @Test
    public void testNullAndEmptyAccountant() {
        // 直接测试验证器，null 和空字符串应该返回 true
        AccountantValidator validator = new AccountantValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}