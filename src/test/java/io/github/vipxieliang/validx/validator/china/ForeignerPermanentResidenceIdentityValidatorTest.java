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

import io.github.vipxieliang.validx.annotations.ForeignerPermanentResidenceIdentity;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ForeignerPermanentResidenceIdentityValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @ForeignerPermanentResidenceIdentity
        private String identityNumber;

        public TestEntity(String identityNumber) {
            this.identityNumber = identityNumber;
        }

        public String getIdentityNumber() {
            return identityNumber;
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityValidatorDirect() {
        // 直接测试验证器的逻辑
        ForeignerPermanentResidenceIdentityValidator validator = new ForeignerPermanentResidenceIdentityValidator();

        // 测试有效的外国人永久居留身份证号码
        assertTrue(validator.isValid("911124198108030024", null), "有效的外国人永久居留身份证号码应该通过验证: 911124198108030024");
        assertTrue(validator.isValid("931012199012010018", null), "有效的外国人永久居留身份证号码应该通过验证: 931012199012010018");
    }

    @Test
    public void testValidForeignerPermanentResidenceIdentities() {
        // 测试有效的外国人永久居留身份证号码
        TestEntity entity1 = new TestEntity("911124198108030024");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的外国人永久居留身份证号码应该通过验证: 911124198108030024");

        TestEntity entity2 = new TestEntity("931012199012010018");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的外国人永久居留身份证号码应该通过验证: 931012199012010018");
    }

    @Test
    public void testInvalidForeignerPermanentResidenceIdentities() {
        // 测试无效的外国人永久居留身份证号码（不是以9开头）
        TestEntity entity1 = new TestEntity("111124198108030024");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的外国人永久居留身份证号码应该不通过验证: 111124198108030024");

        // 测试无效的外国人永久居留身份证号码（数字位数不足）
        TestEntity entity2 = new TestEntity("91112419810803002");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的外国人永久居留身份证号码应该不通过验证: 91112419810803002");

        // 测试无效的外国人永久居留身份证号码（数字位数过多）
        TestEntity entity3 = new TestEntity("9111241981080300241");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的外国人永久居留身份证号码应该不通过验证: 9111241981080300241");

        // 测试无效的外国人永久居留身份证号码（包含非法字符）
        TestEntity entity4 = new TestEntity("91112419810803002A");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "包含非法字符的外国人永久居留身份证号码应该不通过验证: 91112419810803002A");

        // 测试无效的外国人永久居留身份证号码（校验码错误）
        TestEntity entity5 = new TestEntity("931012199012010012");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertFalse(violations5.isEmpty(), "校验码错误的外国人永久居留身份证号码应该不通过验证: 931012199012010012");
        
        // 测试无效的外国人永久居留身份证号码（无效的申领地代码）
        TestEntity entity6 = new TestEntity("999012199012010018");
        Set<ConstraintViolation<TestEntity>> violations6 = validator.validate(entity6);
        assertFalse(violations6.isEmpty(), "无效申领地代码的外国人永久居留身份证号码应该不通过验证: 999012199012010018");
        
        // 测试无效的外国人永久居留身份证号码（无效的出生日期）
        TestEntity entity7 = new TestEntity("911124199013400015");
        Set<ConstraintViolation<TestEntity>> violations7 = validator.validate(entity7);
        assertFalse(violations7.isEmpty(), "无效出生日期的外国人永久居留身份证号码应该不通过验证: 911124199013400015");

        // 测试无效的外国人永久居留身份证号码（无效的国籍国代码）
        TestEntity entity8 = new TestEntity("931000199012010012");
        Set<ConstraintViolation<TestEntity>> violations8 = validator.validate(entity8);
        assertFalse(violations8.isEmpty(), "无效国籍国代码的外国人永久居留身份证号码应该不通过验证: 931000199012010012");
    }

    @Test
    public void testNullAndEmptyForeignerPermanentResidenceIdentity() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ForeignerPermanentResidenceIdentityValidator validator = new ForeignerPermanentResidenceIdentityValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}