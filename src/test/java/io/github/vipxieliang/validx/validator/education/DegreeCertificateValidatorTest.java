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

package io.github.vipxieliang.validx.validator.education;

import io.github.vipxieliang.validx.annotations.DegreeCertificate;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DegreeCertificateValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @DegreeCertificate
        private String certificateNumber;

        public TestEntity(String certificateNumber) {
            this.certificateNumber = certificateNumber;
        }

        public String getCertificateNumber() {
            return certificateNumber;
        }
    }

    @Test
    public void testDegreeCertificateValidatorDirect() {
        // 直接测试验证器的逻辑
        DegreeCertificateValidator validator = new DegreeCertificateValidator();

        // 测试有效的普通学位证书编号
        assertTrue(validator.isValid("1075522008000001", null), "有效的普通学位证书编号应该通过验证: 1075522008000001");
        assertTrue(validator.isValid("1047642016057017", null), "有效的普通学位证书编号应该通过验证: 1047642016057017");
        
        // 测试有效的特殊学位证书编号
        assertTrue(validator.isValid("C1047642016057017", null), "有效的成人高等教育学位证书编号应该通过验证: C1047642016057017");
        assertTrue(validator.isValid("Z1048632006F00001", null), "有效的自考生学位证书编号应该通过验证: Z1048632006F00001");
        assertTrue(validator.isValid("Q1000100000000000", null), "有效的高校教师学位证书编号应该通过验证: Q1000100000000000");
        assertTrue(validator.isValid("T1000100000000000", null), "有效的同等学力人员学位证书编号应该通过验证: T1000100000000000");
        assertTrue(validator.isValid("L1000100000000000", null), "有效的来华留学人员学位证书编号应该通过验证: L1000100000000000");
    }

    @Test
    public void testValidDegreeCertificates() {
        // 测试有效的普通学位证书编号
        TestEntity entity1 = new TestEntity("1075522008000001");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的普通学位证书编号应该通过验证: 1075522008000001");

        TestEntity entity2 = new TestEntity("1047642016057017");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的普通学位证书编号应该通过验证: 1047642016057017");
        
        // 测试有效的特殊学位证书编号
        TestEntity entity3 = new TestEntity("C1047642016057017");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的成人高等教育学位证书编号应该通过验证: C1047642016057017");
        
        TestEntity entity4 = new TestEntity("Z1048632006F00001");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "有效的自考生学位证书编号应该通过验证: Z1048632006F00001");
    }

    @Test
    public void testInvalidDegreeCertificates() {
        // 测试无效的学位证书编号（长度不足）
        TestEntity entity1 = new TestEntity("107552200800000");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "长度不足的学位证书编号应该不通过验证: 107552200800000");

        // 测试无效的学位证书编号（长度过长）
        TestEntity entity2 = new TestEntity("10755220080000011");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "长度过长的学位证书编号应该不通过验证: 10755220080000011");

        // 测试无效的学位证书编号（包含字母）
        TestEntity entity3 = new TestEntity("10755A2008000001");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "包含非法字母的普通学位证书编号应该不通过验证: 10755A2008000001");

        // 测试无效的学位证书编号（特殊类型但格式错误）
        TestEntity entity4 = new TestEntity("X1047642016057017");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "非法开头的特殊学位证书编号应该不通过验证: X1047642016057017");
    }

    @Test
    public void testNullAndEmptyDegreeCertificate() {
        // 直接测试验证器，null 和空字符串应该返回 true
        DegreeCertificateValidator validator = new DegreeCertificateValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}