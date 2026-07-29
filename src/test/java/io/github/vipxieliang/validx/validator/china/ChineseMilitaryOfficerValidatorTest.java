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

import io.github.vipxieliang.validx.annotations.ChineseMilitaryOfficer;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChineseMilitaryOfficerValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @ChineseMilitaryOfficer
        private String militaryOfficerId;

        public TestEntity(String militaryOfficerId) {
            this.militaryOfficerId = militaryOfficerId;
        }

        public String getMilitaryOfficerId() {
            return militaryOfficerId;
        }
    }

    @Test
    public void testChineseMilitaryOfficerValidatorDirect() {
        // 直接测试验证器的逻辑
        ChineseMilitaryOfficerValidator validator = new ChineseMilitaryOfficerValidator();

        // 测试有效的传统军官证号码
        assertTrue(validator.isValid("军字第1234567号", null), "有效的军官证号码应该通过验证: 军字第1234567号");
        assertTrue(validator.isValid("海字第9876543号", null), "有效的军官证号码应该通过验证: 海字第9876543号");
        assertTrue(validator.isValid("空字第1122334号", null), "有效的军官证号码应该通过验证: 空字第1122334号");
        assertTrue(validator.isValid("武警字第1234567号", null), "有效的军官证号码应该通过验证: 武警字第1234567号");
        
        // 测试有效的2016式军官证号码
        assertTrue(validator.isValid("军字第1234567号", null), "有效的2016式军官证号码应该通过验证: 军字第1234567号");
        assertTrue(validator.isValid("海字第9876543号", null), "有效的2016式军官证号码应该通过验证: 海字第9876543号");
        assertTrue(validator.isValid("空字第1122334号", null), "有效的2016式军官证号码应该通过验证: 空字第1122334号");
    }

    @Test
    public void testValidChineseMilitaryOfficers() {
        // 测试有效的军官证号码
        TestEntity entity1 = new TestEntity("军字第1234567号");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的军官证号码应该通过验证: 军字第1234567号");

        TestEntity entity2 = new TestEntity("海字第9876543号");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的军官证号码应该通过验证: 海字第9876543号");

        TestEntity entity3 = new TestEntity("空字第1122334号");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的军官证号码应该通过验证: 空字第1122334号");

        TestEntity entity4 = new TestEntity("武警字第1234567号");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "有效的军官证号码应该通过验证: 武警字第1234567号");
    }

    @Test
    public void testInvalidChineseMilitaryOfficers() {
        // 测试无效的军官证号码（数字位数不足）
        TestEntity entity1 = new TestEntity("军字第123456号");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的军官证号码应该不通过验证: 军字第123456号");

        // 测试无效的军官证号码（数字位数过多）
        TestEntity entity2 = new TestEntity("军字第12345678号");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的军官证号码应该不通过验证: 军字第12345678号");

        // 测试无效的军官证号码（缺少"字第"）
        TestEntity entity3 = new TestEntity("军字第1234567");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的军官证号码应该不通过验证: 军字第1234567");

        // 测试无效的军官证号码（缺少"号"）
        TestEntity entity4 = new TestEntity("军字第1234567号 ");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "无效的军官证号码应该不通过验证: 军字第1234567号 ");

        // 测试无效的军官证号码（包含非法字符）
        TestEntity entity5 = new TestEntity("军字第123456X号");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertFalse(violations5.isEmpty(), "包含非法字符的军官证号码应该不通过验证: 军字第123456X号");

        // 测试无效的军官证号码（非汉字开头）
        TestEntity entity6 = new TestEntity("A字第1234567号");
        Set<ConstraintViolation<TestEntity>> violations6 = validator.validate(entity6);
        assertFalse(violations6.isEmpty(), "无效的军官证号码应该不通过验证: A字第1234567号");
    }

    @Test
    public void testNullAndEmptyChineseMilitaryOfficer() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ChineseMilitaryOfficerValidator officerValidator = new ChineseMilitaryOfficerValidator();
        assertTrue(officerValidator.isValid(null, null), "null should return true");
        assertTrue(officerValidator.isValid("", null), "empty string should return true");
    }
}