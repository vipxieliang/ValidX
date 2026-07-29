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

import io.github.vipxieliang.validx.annotations.In;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InValidator测试类
 */
public class InValidatorTest {
    
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    
    // 基本测试模型类
    public static class FruitModel {
        @In({"apple", "banana", "orange"})
        @NotNull
        private String fruit;

        public FruitModel(String fruit) {
            this.fruit = fruit;
        }

        public String getFruit() {
            return fruit;
        }
    }
    
    // 用于测试不包含null值的情况
    public static class SimpleFruitModel {
        @In({"apple", "banana", "orange"})
        private String fruit;  // 不使用@NotNull，允许null值

        public SimpleFruitModel(String fruit) {
            this.fruit = fruit;
        }

        public String getFruit() {
            return fruit;
        }
    }
    
    // 测试模型类 - 数组
    public static class ArrayModel {
        @In({"apple", "banana", "orange"})
        @NotNull
        private String[] fruits;

        public ArrayModel(String[] fruits) {
            this.fruits = fruits;
        }

        public String[] getFruits() {
            return fruits;
        }
    }
    
    // 测试模型类 - 集合
    public static class CollectionModel {
        @In({"apple", "banana", "orange"})
        @NotNull
        private List<String> fruits;

        public CollectionModel(List<String> fruits) {
            this.fruits = fruits;
        }

        public List<String> getFruits() {
            return fruits;
        }
    }
    
    // 测试模型类 - 基本类型数组
    public static class IntArrayModel {
        @In({"1", "2", "3"})
        @NotNull
        private int[] numbers;

        public IntArrayModel(int[] numbers) {
            this.numbers = numbers;
        }

        public int[] getNumbers() {
            return numbers;
        }
    }

    @Test
    public void testValidValues() {
        FruitModel model1 = new FruitModel("apple");
        Set<ConstraintViolation<FruitModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "apple应该通过验证");
        
        FruitModel model2 = new FruitModel("banana");
        Set<ConstraintViolation<FruitModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "banana应该通过验证");
        
        FruitModel model3 = new FruitModel("orange");
        Set<ConstraintViolation<FruitModel>> violations3 = validator.validate(model3);
        assertTrue(violations3.isEmpty(), "orange应该通过验证");
    }
    
    @Test
    public void testInvalidValues() {
        FruitModel model1 = new FruitModel("grape");
        Set<ConstraintViolation<FruitModel>> violations1 = validator.validate(model1);
        assertFalse(violations1.isEmpty(), "grape应该验证失败");
        
        FruitModel model2 = new FruitModel("");
        Set<ConstraintViolation<FruitModel>> violations2 = validator.validate(model2);
        assertFalse(violations2.isEmpty(), "空字符串应该验证失败");
        
        FruitModel model3 = new FruitModel("Apple");
        Set<ConstraintViolation<FruitModel>> violations3 = validator.validate(model3);
        assertFalse(violations3.isEmpty(), "大小写敏感，Apple应该验证失败");
    }
    
    @Test 
    public void testNullValue() {
        SimpleFruitModel model = new SimpleFruitModel(null);
        Set<ConstraintViolation<SimpleFruitModel>> violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "null值应该验证通过");
        
        FruitModel model2 = new FruitModel(null);
        Set<ConstraintViolation<FruitModel>> violations2 = validator.validate(model2);
        assertFalse(violations2.isEmpty(), "null值应该不通过@NotNull验证");
    }
    
    // 对于需要测试包含null值的情况，我们仍然需要使用原始方式
    @Test
    public void testDirectValidatorWithNullInArray() {
        // 直接测试验证器的行为，包含null值情况
        InValidator validator = new InValidator();
        
        // 创建一个模拟的In注解实例，包含null元素
        In inAnnotation = new In() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return In.class;
            }
            
            @Override
            public String[] value() {
                return new String[]{"apple", "banana", null};
            }
            
            @Override
            public String message() {
                return "无效值";
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
        
        validator.initialize(inAnnotation);
        
        assertTrue(validator.isValid(null, null), "null值在包含null的数组中应该验证通过");
        assertTrue(validator.isValid("apple", null), "apple应该验证通过");
        assertFalse(validator.isValid("grape", null), "grape应该验证失败");
    }
    
    @Test
    public void testValidStringArray() {
        ArrayModel model1 = new ArrayModel(new String[]{"apple", "banana"});
        Set<ConstraintViolation<ArrayModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "所有元素都在范围内的数组应该通过验证");
        
        ArrayModel model2 = new ArrayModel(new String[]{"orange"});
        Set<ConstraintViolation<ArrayModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "单个有效元素应该通过验证");
    }
    
    @Test
    public void testInvalidStringArray() {
        ArrayModel model1 = new ArrayModel(new String[]{"apple", "grape"});
        Set<ConstraintViolation<ArrayModel>> violations1 = validator.validate(model1);
        assertFalse(violations1.isEmpty(), "包含无效元素的数组应该验证失败");
        
        ArrayModel model2 = new ArrayModel(new String[]{"apple", "APPLE"});
        Set<ConstraintViolation<ArrayModel>> violations2 = validator.validate(model2);
        assertFalse(violations2.isEmpty(), "大小写敏感，包含无效元素应该验证失败");
    }
    
    @Test
    public void testValidCollection() {
        CollectionModel model1 = new CollectionModel(Arrays.asList("apple", "banana"));
        Set<ConstraintViolation<CollectionModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "所有元素都在范围内的集合应该通过验证");
        
        CollectionModel model2 = new CollectionModel(Collections.singletonList("orange"));
        Set<ConstraintViolation<CollectionModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "单个有效元素应该通过验证");
    }
    
    @Test
    public void testInvalidCollection() {
        CollectionModel model1 = new CollectionModel(Arrays.asList("apple", "grape"));
        Set<ConstraintViolation<CollectionModel>> violations1 = validator.validate(model1);
        assertFalse(violations1.isEmpty(), "包含无效元素的集合应该验证失败");
    }
    
    @Test
    public void testValidIntArray() {
        IntArrayModel model1 = new IntArrayModel(new int[]{1, 2});
        Set<ConstraintViolation<IntArrayModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "所有元素都在范围内的int数组应该通过验证");
        
        IntArrayModel model2 = new IntArrayModel(new int[]{3});
        Set<ConstraintViolation<IntArrayModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "单个有效元素应该通过验证");
    }
    
    @Test
    public void testInvalidIntArray() {
        IntArrayModel model1 = new IntArrayModel(new int[]{1, 4});
        Set<ConstraintViolation<IntArrayModel>> violations1 = validator.validate(model1);
        assertFalse(violations1.isEmpty(), "包含无效元素的int数组应该验证失败");
    }
    
    // 直接测试验证器
    @Test
    public void testDirectValidator() {
        InValidator validator = new InValidator();
        
        // 创建一个模拟的In注解实例
        In inAnnotation = new In() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return In.class;
            }
            
            @Override
            public String[] value() {
                return new String[]{"1", "2", "3"};
            }
            
            @Override
            public String message() {
                return "无效值";
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
        
        validator.initialize(inAnnotation);
        
        // 测试字符串数组
        assertTrue(validator.isValid(new String[]{"1", "2"}, null), "所有元素有效应该验证通过");
        assertFalse(validator.isValid(new String[]{"1", "4"}, null), "包含无效元素应该验证失败");
        
        // 测试集合
        assertTrue(validator.isValid(Arrays.asList("1", "2"), null), "所有元素有效应该验证通过");
        assertFalse(validator.isValid(Arrays.asList("1", "4"), null), "包含无效元素应该验证失败");
        
        // 测试基本类型数组
        assertTrue(validator.isValid(new int[]{1, 2, 3}, null), "所有元素有效应该验证通过");
        assertFalse(validator.isValid(new int[]{1, 4}, null), "包含无效元素应该验证失败");
        
        // 测试null值
        assertTrue(validator.isValid(null, null), "null值应该验证通过");
        
        // 测试单个值（向后兼容）
        assertTrue(validator.isValid("1", null), "单个有效值应该验证通过");
        assertFalse(validator.isValid("4", null), "单个无效值应该验证失败");
    }
}