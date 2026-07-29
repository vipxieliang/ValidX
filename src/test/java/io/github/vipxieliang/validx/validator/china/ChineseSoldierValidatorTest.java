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

import io.github.vipxieliang.validx.annotations.ChineseSoldier;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChineseSoldierValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @ChineseSoldier
        private String soldierId;

        public TestEntity(String soldierId) {
            this.soldierId = soldierId;
        }

        public String getSoldierId() {
            return soldierId;
        }
    }

    @Test
    public void testChineseSoldierValidatorDirect() {
        // 直接测试验证器的逻辑
        ChineseSoldierValidator validator = new ChineseSoldierValidator();

        // 测试有效的士兵证号码
        assertTrue(validator.isValid("沈字第0100000号", null), "有效的士兵证号码应该通过验证: 沈字第0100000号");
        assertTrue(validator.isValid("京字第1234567号", null), "有效的士兵证号码应该通过验证: 京字第1234567号");
        assertTrue(validator.isValid("军字第9876543号", null), "有效的士兵证号码应该通过验证: 军字第9876543号");
    }

    @Test
    public void testValidChineseSoldiers() {
        // 测试有效的士兵证号码
        TestEntity entity1 = new TestEntity("沈字第0100000号");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的士兵证号码应该通过验证: 沈字第0100000号");

        TestEntity entity2 = new TestEntity("京字第1234567号");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的士兵证号码应该通过验证: 京字第1234567号");

        TestEntity entity3 = new TestEntity("军字第9876543号");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的士兵证号码应该通过验证: 军字第9876543号");
    }

    @Test
    public void testInvalidChineseSoldiers() {
        // 测试无效的士兵证号码（数字位数不足）
        TestEntity entity1 = new TestEntity("沈字第010000号");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的士兵证号码应该不通过验证: 沈字第010000号");

        // 测试无效的士兵证号码（数字位数过多）
        TestEntity entity2 = new TestEntity("沈字第01000001号");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的士兵证号码应该不通过验证: 沈字第01000001号");

        // 测试无效的士兵证号码（缺少"字第"）
        TestEntity entity3 = new TestEntity("沈第0100000号");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的士兵证号码应该不通过验证: 沈第0100000号");

        // 测试无效的士兵证号码（缺少"号"）
        TestEntity entity4 = new TestEntity("沈字第0100000");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "无效的士兵证号码应该不通过验证: 沈字第0100000");

        // 测试无效的士兵证号码（包含非法字符）
        TestEntity entity5 = new TestEntity("沈字第010000X号");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertFalse(violations5.isEmpty(), "包含非法字符的士兵证号码应该不通过验证: 沈字第010000X号");

        // 测试无效的士兵证号码（非汉字开头）
        TestEntity entity6 = new TestEntity("A字第0100000号");
        Set<ConstraintViolation<TestEntity>> violations6 = validator.validate(entity6);
        assertFalse(violations6.isEmpty(), "无效的士兵证号码应该不通过验证: A字第0100000号");
    }

    @Test
    public void testNullAndEmptyChineseSoldier() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ChineseSoldierValidator soldierValidator = new ChineseSoldierValidator();
        assertTrue(soldierValidator.isValid(null, null), "null should return true");
        assertTrue(soldierValidator.isValid("", null), "empty string should return true");
    }
}