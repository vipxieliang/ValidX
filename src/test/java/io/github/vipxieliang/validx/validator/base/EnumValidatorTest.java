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

import io.github.vipxieliang.validx.annotations.Enum;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnumValidatorTest {

    // 测试枚举类
    public enum TestEnum {
        VALUE1("code1"),
        VALUE2("code2"),
        VALUE3("code3");

        private final String code;

        TestEnum(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    @Test
    @SuppressWarnings("unused")
    void testValidEnumValueWithDefaultField() throws Exception {
        EnumValidator validator = new EnumValidator();
        
        // 创建模拟的Enum注解实例
        Enum enumAnnotation = mock(Enum.class);
        when(enumAnnotation.target()).thenReturn((Class) TestEnum.class);
        when(enumAnnotation.field()).thenReturn("name"); // 使用name字段进行测试
        
        validator.initialize(enumAnnotation);
        
        // 测试有效的枚举值
        assertTrue(validator.isValid("VALUE1", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("VALUE2", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("VALUE3", mock(ConstraintValidatorContext.class)));
        
        // 测试无效的枚举值
        assertFalse(validator.isValid("VALUE4", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
    }

    @Test
    @SuppressWarnings("unused")
    void testValidEnumValueWithCodeField() throws Exception {
        EnumValidator validator = new EnumValidator();
        
        // 创建模拟的Enum注解实例，使用code字段
        Enum enumAnnotation = mock(Enum.class);
        when(enumAnnotation.target()).thenReturn((Class) TestEnum.class);
        when(enumAnnotation.field()).thenReturn("getCode");
        
        validator.initialize(enumAnnotation);
        
        // 测试有效的枚举值 (code)
        assertTrue(validator.isValid("code1", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("code2", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("code3", mock(ConstraintValidatorContext.class)));
        
        // 测试无效的枚举值
        assertFalse(validator.isValid("code4", mock(ConstraintValidatorContext.class)));
    }
    
    @Test
    @SuppressWarnings("unused")
    void testValidEnumValueWithNameField() throws Exception {
        EnumValidator validator = new EnumValidator();
        
        // 创建模拟的Enum注解实例，使用name字段
        Enum enumAnnotation = mock(Enum.class);
        when(enumAnnotation.target()).thenReturn((Class) TestEnum.class);
        when(enumAnnotation.field()).thenReturn("name");
        
        validator.initialize(enumAnnotation);
        
        // 测试有效的枚举值 (name)
        assertTrue(validator.isValid("VALUE1", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("VALUE2", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("VALUE3", mock(ConstraintValidatorContext.class)));
        
        // 测试无效的枚举值
        assertFalse(validator.isValid("VALUE4", mock(ConstraintValidatorContext.class)));
    }
    
    @Test
    @SuppressWarnings("unused")
    void testValidEnumValueWithCollection() throws Exception {
        EnumValidator validator = new EnumValidator();
        
        // 创建模拟的Enum注解实例，使用code字段
        Enum enumAnnotation = mock(Enum.class);
        when(enumAnnotation.target()).thenReturn((Class) TestEnum.class);
        when(enumAnnotation.field()).thenReturn("getCode");
        
        validator.initialize(enumAnnotation);
        
        // 测试有效的枚举值集合
        List<String> validValues = Arrays.asList("code1", "code2");
        assertTrue(validator.isValid(validValues, mock(ConstraintValidatorContext.class)));
        
        // 测试包含无效值的集合
        List<String> invalidValues = Arrays.asList("code1", "invalid_code");
        assertFalse(validator.isValid(invalidValues, mock(ConstraintValidatorContext.class)));
        
        // 测试空集合
        List<String> emptyValues = Arrays.asList();
        assertTrue(validator.isValid(emptyValues, mock(ConstraintValidatorContext.class)));
        
        // 测试null集合
        assertTrue(validator.isValid((List<String>) null, mock(ConstraintValidatorContext.class)));
    }
    
    @Test
    @SuppressWarnings("unused")
    void testValidEnumValueWithArray() throws Exception {
        EnumValidator validator = new EnumValidator();
        
        // 创建模拟的Enum注解实例，使用code字段
        Enum enumAnnotation = mock(Enum.class);
        when(enumAnnotation.target()).thenReturn((Class) TestEnum.class);
        when(enumAnnotation.field()).thenReturn("getCode");
        
        validator.initialize(enumAnnotation);
        
        // 测试有效的枚举值数组
        String[] validValues = {"code1", "code2"};
        assertTrue(validator.isValid(validValues, mock(ConstraintValidatorContext.class)));
        
        // 测试包含无效值的数组
        String[] invalidValues = {"code1", "invalid_code"};
        assertFalse(validator.isValid(invalidValues, mock(ConstraintValidatorContext.class)));
        
        // 测试空数组
        String[] emptyValues = {};
        assertTrue(validator.isValid(emptyValues, mock(ConstraintValidatorContext.class)));
        
        // 测试null数组
        assertTrue(validator.isValid((String[]) null, mock(ConstraintValidatorContext.class)));
    }
    
    @Test
    @SuppressWarnings("unused")
    void testValidEnumValueWithIntArray() throws Exception {
        EnumValidator validator = new EnumValidator();
        
        // 创建一个使用int值的测试枚举类
        Enum enumAnnotation = mock(Enum.class);
        when(enumAnnotation.target()).thenReturn((Class) IntTestEnum.class);
        when(enumAnnotation.field()).thenReturn("getValue");
        
        validator.initialize(enumAnnotation);
        
        // 测试有效的int枚举值数组
        int[] validValues = {1, 2};
        assertTrue(validator.isValid(validValues, mock(ConstraintValidatorContext.class)));
        
        // 测试包含无效值的int数组
        int[] invalidValues = {1, 4};
        assertFalse(validator.isValid(invalidValues, mock(ConstraintValidatorContext.class)));
    }
    
    // 用于测试int值的枚举类
    public enum IntTestEnum {
        ONE(1),
        TWO(2),
        THREE(3);

        private final int value;

        IntTestEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}