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

import io.github.vipxieliang.validx.annotations.ChinesePassport;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChinesePassportValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @ChinesePassport
        private String passportNumber;

        public TestEntity(String passportNumber) {
            this.passportNumber = passportNumber;
        }

        public String getPassportNumber() {
            return passportNumber;
        }
    }

    @Test
    public void testChinesePassportValidatorDirect() {
        // 直接测试验证器的逻辑
        ChinesePassportValidator validator = new ChinesePassportValidator();

        // 测试有效的普通护照号码（G开头）
        assertTrue(validator.isValid("G12345678", null), "有效的普通护照号码应该通过验证: G12345678");
        
        // 测试有效的电子护照号码（E开头）
        assertTrue(validator.isValid("E12345678", null), "有效的电子护照号码应该通过验证: E12345678");
        
        // 测试有效的公务护照号码（S开头，7位数字）
        assertTrue(validator.isValid("S1234567", null), "有效的公务护照号码应该通过验证: S1234567");
        
        // 测试有效的公务护照号码（S开头，8位数字）
        assertTrue(validator.isValid("S12345678", null), "有效的公务护照号码应该通过验证: S12345678");
        
        // 测试有效的外交护照号码（D开头）
        assertTrue(validator.isValid("D1234567", null), "有效的外交护照号码应该通过验证: D1234567");
        
        // 测试有效的公务普通护照号码（P开头）
        assertTrue(validator.isValid("P1234567", null), "有效的公务普通护照号码应该通过验证: P1234567");
    }

    @Test
    public void testValidChinesePassports() {
        // 测试有效的普通护照号码（G开头）
        TestEntity entity1 = new TestEntity("G12345678");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的普通护照号码应该通过验证: G12345678");

        // 测试有效的电子护照号码（E开头）
        TestEntity entity2 = new TestEntity("E87654321");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的电子护照号码应该通过验证: E87654321");

        // 测试有效的公务护照号码（S开头）
        TestEntity entity3 = new TestEntity("S12345678");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的公务护照号码应该通过验证: S12345678");

        // 测试有效的外交护照号码（D开头）
        TestEntity entity4 = new TestEntity("D8765432");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "有效的外交护照号码应该通过验证: D8765432");

        // 测试有效的公务普通护照号码（P开头）
        TestEntity entity5 = new TestEntity("P1122334");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertTrue(violations5.isEmpty(), "有效的公务普通护照号码应该通过验证: P1122334");
    }

    @Test
    public void testInvalidChinesePassports() {
        // 测试无效的护照号码（G开头但只有7位数字）
        TestEntity entity1 = new TestEntity("G1234567");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的护照号码应该不通过验证: G1234567");

        // 测试无效的护照号码（E开头但有9位数字）
        TestEntity entity2 = new TestEntity("E123456789");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的护照号码应该不通过验证: E123456789");

        // 测试无效的护照号码（S开头但有6位数字）
        TestEntity entity3 = new TestEntity("S123456");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的护照号码应该不通过验证: S123456");

        // 测试无效的护照号码（包含非法字符）
        TestEntity entity4 = new TestEntity("G1234567X");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "包含非法字符的护照号码应该不通过验证: G1234567X");

        // 测试无效的护照号码（D开头但有8位数字）
        TestEntity entity5 = new TestEntity("D12345678");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertFalse(violations5.isEmpty(), "无效的护照号码应该不通过验证: D12345678");

        // 测试无效的护照号码（P开头但有8位数字）
        TestEntity entity6 = new TestEntity("P12345678");
        Set<ConstraintViolation<TestEntity>> violations6 = validator.validate(entity6);
        assertFalse(violations6.isEmpty(), "无效的护照号码应该不通过验证: P12345678");
    }

    @Test
    public void testNullAndEmptyChinesePassport() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ChinesePassportValidator passportValidator = new ChinesePassportValidator();
        assertTrue(passportValidator.isValid(null, null), "null should return true");
        assertTrue(passportValidator.isValid("", null), "empty string should return true");
    }
}