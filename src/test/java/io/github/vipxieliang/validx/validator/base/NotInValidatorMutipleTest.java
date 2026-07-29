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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NotInValidatorMutipleTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @NotIn({"value1", "value2", "value3"})
        private String singleValue;

        @NotIn({"value1", "value2", "value3"})
        private List<String> listValue;

        @NotIn({"value1", "value2", "value3"})
        private String[] arrayValue;

        @NotIn({"value1", "value2", "value3"})
        private Set<String> setValue;

        @NotIn({"1", "2", "3"})
        private int[] intArrayValue;

        public void setSingleValue(String singleValue) {
            this.singleValue = singleValue;
        }

        public void setListValue(List<String> listValue) {
            this.listValue = listValue;
        }

        public void setArrayValue(String[] arrayValue) {
            this.arrayValue = arrayValue;
        }

        public void setSetValue(Set<String> setValue) {
            this.setValue = setValue;
        }

        public void setIntArrayValue(int[] intArrayValue) {
            this.intArrayValue = intArrayValue;
        }
    }

    @Test
    public void testSingleValueValidation() {
        TestEntity entity = new TestEntity();

        // 测试单个有效值（不在数组中）
        entity.setSingleValue("validValue");
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试单个无效值（在数组中）
        entity.setSingleValue("value1");
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");
        assertEquals("{io.github.vipxieliang.validx.annotation.not.in}", violations.iterator().next().getMessageTemplate());
    }

    @Test
    public void testListValidation() {
        TestEntity entity = new TestEntity();

        // 测试列表中所有元素都有效（不在数组中）
        entity.setListValue(Arrays.asList("valid1", "valid2", "valid3"));
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试列表中有一个元素无效（在数组中）
        entity.setListValue(Arrays.asList("valid1", "value2", "valid3"));
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试列表中所有元素都无效（都在数组中）
        entity.setListValue(Arrays.asList("value1", "value2"));
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试空列表
        entity.setListValue(Collections.emptyList());
        violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试null列表
        entity.setListValue(null);
        violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");
    }

    @Test
    public void testArrayValidation() {
        TestEntity entity = new TestEntity();

        // 测试数组中所有元素都有效（不在数组中）
        entity.setArrayValue(new String[]{"valid1", "valid2", "valid3"});
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试数组中有一个元素无效（在数组中）
        entity.setArrayValue(new String[]{"valid1", "value2", "valid3"});
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试数组中所有元素都无效（都在数组中）
        entity.setArrayValue(new String[]{"value1", "value2"});
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试空数组
        entity.setArrayValue(new String[]{});
        violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试null数组
        entity.setArrayValue(null);
        violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");
    }

    @Test
    public void testSetValidation() {
        TestEntity entity = new TestEntity();

        // 测试集合中所有元素都有效（不在数组中）
        entity.setSetValue(new HashSet<>(Arrays.asList("valid1", "valid2", "valid3")));
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试集合中有一个元素无效（在数组中）
        entity.setSetValue(new HashSet<>(Arrays.asList("valid1", "value2", "valid3")));
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试集合中所有元素都无效（都在数组中）
        entity.setSetValue(new HashSet<>(Arrays.asList("value1", "value2")));
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试空集合
        entity.setSetValue(Collections.emptySet());
        violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试null集合
        entity.setSetValue(null);
        violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");
    }

    @Test
    public void testIntArrayValidation() {
        TestEntity entity = new TestEntity();

        // 测试整型数组中所有元素都有效（不在数组中）
        entity.setIntArrayValue(new int[]{4, 5, 6});
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");

        // 测试整型数组中有一个元素无效（在数组中）
        entity.setIntArrayValue(new int[]{1, 5, 6});
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试整型数组中所有元素都无效（都在数组中）
        entity.setIntArrayValue(new int[]{1, 2});
        violations = validator.validate(entity);
        assertEquals(1, violations.size(), "应该有一个验证错误");

        // 测试空整型数组
        entity.setIntArrayValue(new int[]{});
        violations = validator.validate(entity);
        assertEquals(0, violations.size(), "应该没有验证错误");
    }
}