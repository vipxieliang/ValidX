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

import io.github.vipxieliang.validx.annotations.ChineseIdCard;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChineseIdCardValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @ChineseIdCard
        private String idCard;

        public TestEntity(String idCard) {
            this.idCard = idCard;
        }

        public String getIdCard() {
            return idCard;
        }
    }

    @Test
    public void testChineseIdCardValidatorDirect() {
        // 直接测试验证器的逻辑
        ChineseIdCardValidator validator = new ChineseIdCardValidator();

        // 测试有效的18位身份证号码
        boolean result1 = validator.isValid("440608197310039910", null);
        assertTrue(result1, "有效的身份证号码应该通过验证: 440608197310039910");

        // 测试另一个有效的18位身份证号码
        boolean result2 = validator.isValid("440524198001010013", null);
        assertTrue(result2, "有效的身份证号码应该通过验证: 440524198001010013");
    }

    @Test
    public void testValidChineseIdCard() {
        // 测试有效的18位身份证号码
        TestEntity entity1 = new TestEntity("440608197310039910");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的身份证号码应该通过验证");

        // 测试另一个有效的18位身份证号码
        TestEntity entity2 = new TestEntity("440524198001010013");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的身份证号码应该通过验证");
    }

    @Test
    public void testValidChineseIdCard15() {
        // 测试有效的15位身份证号码（会自动转换为18位）
        TestEntity entity = new TestEntity("110101900307123"); // 正确的15位身份证号码
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "有效的15位身份证号码应该通过验证");
    }

    @Test
    public void testInvalidChineseIdCard() {
        // 测试无效的身份证号码（校验位错误）
        TestEntity entity1 = new TestEntity("110101199003072110");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的身份证号码应该不通过验证");

        // 测试无效的身份证号码（包含非法字符）
        TestEntity entity2 = new TestEntity("11010119900307211X");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "包含非法字符的身份证号码应该不通过验证");

        // 测试长度不正确的身份证号码
        TestEntity entity3 = new TestEntity("11010119900307211");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "长度不正确的身份证号码应该不通过验证");
    }

    @Test
    public void testNullAndEmptyChineseIdCard() {
        // 直接测试验证器，null 应该返回 true
        ChineseIdCardValidator idCardValidator = new ChineseIdCardValidator();
        assertTrue(idCardValidator.isValid(null, null), "null should return true");
        assertTrue(idCardValidator.isValid("", null), "empty string should return true");
    }
}