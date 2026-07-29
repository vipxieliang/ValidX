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

import io.github.vipxieliang.validx.annotations.Constructor;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * 建造师证书验证器测试类
 */
public class ConstructorValidatorTest {

    @Test
    public void testValidConstructorNumbers() {
        ConstructorValidator validator = new ConstructorValidator();
        Constructor constructor = mock(Constructor.class);
        validator.initialize(constructor);

        // 测试有效的一级建造师证书编号
        assertTrue(validator.isValid("京111050700001", mock(ConstraintValidatorContext.class)));
        
        // 测试有效的二级建造师证书编号
        assertTrue(validator.isValid("鄂242050700001", mock(ConstraintValidatorContext.class)));
        
        // 测试空值
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void testInvalidConstructorNumbers() {
        ConstructorValidator validator = new ConstructorValidator();
        Constructor constructor = mock(Constructor.class);
        validator.initialize(constructor);

        // 测试位数不正确的编号
        assertFalse(validator.isValid("京11105070000", mock(ConstraintValidatorContext.class))); // 12位
        assertFalse(validator.isValid("京1110507000012", mock(ConstraintValidatorContext.class))); // 14位
        
        // 测试包含非数字字符（除第一个汉字外）
        assertFalse(validator.isValid("京A11050700001", mock(ConstraintValidatorContext.class))); // 包含字母
        assertFalse(validator.isValid("京11105070000!", mock(ConstraintValidatorContext.class))); // 包含特殊字符
        
        // 测试无效的级别代码
        assertFalse(validator.isValid("京311050700001", mock(ConstraintValidatorContext.class))); // 级别代码为3
        assertFalse(validator.isValid("京011050700001", mock(ConstraintValidatorContext.class))); // 级别代码为0
        
        // 测试无效的省级代码 (使用不在VALID_PROVINCE_CODES集合中的代码)
        assertFalse(validator.isValid("京188050700001", mock(ConstraintValidatorContext.class))); // 无效省级代码88
        
        // 测试无效的年份代码 (虽然00-99在技术上是有效的，但我们可以测试一些边界情况)
        // 这里我们不测试年份为99的情况，因为这在实际中可能是有效的
    }

    @Test
    public void testNullAndEmptyConstructor() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ConstructorValidator validator = new ConstructorValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}