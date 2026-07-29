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

import io.github.vipxieliang.validx.annotations.TaiwanPass;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TaiwanPassValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @TaiwanPass
        private String passNumber;

        public TestEntity(String passNumber) {
            this.passNumber = passNumber;
        }

        public String getPassNumber() {
            return passNumber;
        }
    }

    @Test
    public void testTaiwanPassValidatorDirect() {
        // 直接测试验证器的逻辑
        TaiwanPassValidator validator = new TaiwanPassValidator();

        // 测试有效的台胞证号码
        assertTrue(validator.isValid("1234567800", null), "有效的台胞证号码应该通过验证: 1234567800");
        assertTrue(validator.isValid("1234567801", null), "有效的台胞证号码应该通过验证: 1234567801");
        assertTrue(validator.isValid("9876543200", null), "有效的台胞证号码应该通过验证: 9876543200");
        assertTrue(validator.isValid("9876543299", null), "有效的台胞证号码应该通过验证: 9876543299");
    }

    @Test
    public void testValidTaiwanPasses() {
        // 测试有效的台胞证号码
        TestEntity entity1 = new TestEntity("1234567800");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的台胞证号码应该通过验证: 1234567800");

        TestEntity entity2 = new TestEntity("1234567801");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的台胞证号码应该通过验证: 1234567801");

        TestEntity entity3 = new TestEntity("9876543200");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的台胞证号码应该通过验证: 9876543200");

        TestEntity entity4 = new TestEntity("9876543299");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "有效的台胞证号码应该通过验证: 9876543299");
    }

    @Test
    public void testInvalidTaiwanPasses() {
        // 测试无效的台胞证号码（数字位数不足）
        TestEntity entity1 = new TestEntity("123456780");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的台胞证号码应该不通过验证: 123456780");

        // 测试无效的台胞证号码（数字位数过多）
        TestEntity entity2 = new TestEntity("12345678001");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的台胞证号码应该不通过验证: 12345678001");

        // 测试无效的台胞证号码（包含非法字符）
        TestEntity entity3 = new TestEntity("123456780X");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "包含非法字符的台胞证号码应该不通过验证: 123456780X");

        // 测试无效的台胞证号码（包含字母）
        TestEntity entity4 = new TestEntity("A234567800");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "无效的台胞证号码应该不通过验证: A234567800");
    }

    @Test
    public void testNullAndEmptyTaiwanPass() {
        // 直接测试验证器，null 和空字符串应该返回 true
        TaiwanPassValidator taiwanPassValidator = new TaiwanPassValidator();
        assertTrue(taiwanPassValidator.isValid(null, null), "null should return true");
        assertTrue(taiwanPassValidator.isValid("", null), "empty string should return true");
    }
}