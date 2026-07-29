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

import io.github.vipxieliang.validx.annotations.NotIn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotInValidator测试类
 */
public class NotInValidatorTest {
    
    private final NotInValidator validator = new NotInValidator();
    
    @Test
    public void testValidValues() {
        // 创建一个模拟的NotIn注解实例
        NotIn notInAnnotation = new NotIn() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return NotIn.class;
            }
            
            @Override
            public String[] value() {
                return new String[]{"apple", "banana", "orange"};
            }
            
            @Override
            public String message() {
                return "值不能在指定范围内";
            }
            
            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }
            
            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };
        
        validator.initialize(notInAnnotation);
        
        assertTrue(validator.isValid("grape", null));
        assertTrue(validator.isValid("watermelon", null));
        assertTrue(validator.isValid(null, null)); // null值应该返回true
    }
    
    @Test
    public void testInvalidValues() {
        // 创建一个模拟的NotIn注解实例
        NotIn notInAnnotation = new NotIn() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return NotIn.class;
            }
            
            @Override
            public String[] value() {
                return new String[]{"apple", "banana", "orange"};
            }
            
            @Override
            public String message() {
                return "值不能在指定范围内";
            }
            
            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }
            
            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };
        
        validator.initialize(notInAnnotation);
        
        assertFalse(validator.isValid("apple", null));
        assertFalse(validator.isValid("banana", null));
        assertFalse(validator.isValid("orange", null));
    }
    
    @Test
    public void testEmptyArray() {
        // 创建一个模拟的NotIn注解实例，包含空数组
        NotIn notInAnnotation = new NotIn() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return NotIn.class;
            }
            
            @Override
            public String[] value() {
                return new String[]{};
            }
            
            @Override
            public String message() {
                return "值不能在指定范围内";
            }
            
            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }
            
            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };
        
        validator.initialize(notInAnnotation);
        
        assertTrue(validator.isValid("apple", null));
        assertTrue(validator.isValid(null, null));
    }
}