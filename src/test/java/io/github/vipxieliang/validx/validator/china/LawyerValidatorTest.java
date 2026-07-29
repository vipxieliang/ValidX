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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.Lawyer;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LawyerValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @Lawyer
        private String lawyerNumber;

        public TestEntity(String lawyerNumber) {
            this.lawyerNumber = lawyerNumber;
        }

        public String getLawyerNumber() {
            return lawyerNumber;
        }
    }

    @Test
    public void testValidLawyerNumbers() {
        // 测试有效的律师执业证编号（17位）
        TestEntity entity1 = new TestEntity("11101200010000001");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的律师执业证编号应该通过验证: 11101200010000001");

        // 测试有效的法律职业资格证书（14位）
        TestEntity entity2 = new TestEntity("20201101010001");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的法律职业资格证书应该通过验证: 20201101010001");

        // 测试有效的法律职业资格证书（16位）
        TestEntity entity3 = new TestEntity("2020110101000101");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的法律职业资格证书应该通过验证: 2020110101000101");
    }

    @Test
    public void testInvalidLawyerNumbers() {
        // 测试无效的律师证编号
        TestEntity entity1 = new TestEntity("1234567890"); // 长度不足
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的律师证编号应该不通过验证: 1234567890");

        TestEntity entity2 = new TestEntity("21101200010000001"); // 第1位不是1
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的律师证编号应该不通过验证: 21101200010000001");

        TestEntity entity3 = new TestEntity("1110120001000000a"); // 包含字母
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的律师证编号应该不通过验证: 1110120001000000a");
    }

    @Test
    public void testNullAndEmptyLawyerNumber() {
        // 直接测试验证器，null 和空字符串应该返回 true
        LawyerValidator lawyerValidator = new LawyerValidator();
        assertTrue(lawyerValidator.isValid(null, null), "null should return true");
        assertTrue(lawyerValidator.isValid("", null), "empty string should return true");
    }
}
