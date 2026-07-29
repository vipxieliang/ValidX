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

import io.github.vipxieliang.validx.annotations.TaiwanResidence;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TaiwanResidenceValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @TaiwanResidence
        private String residenceNumber;

        public TestEntity(String residenceNumber) {
            this.residenceNumber = residenceNumber;
        }

        public String getResidenceNumber() {
            return residenceNumber;
        }
    }

    @Test
    public void testTaiwanResidenceValidatorDirect() {
        // 直接测试验证器的逻辑
        TaiwanResidenceValidator validator = new TaiwanResidenceValidator();

        // 测试有效的台湾居民居住证号码
        assertTrue(validator.isValid("830000000000000001", null), "有效的台湾居民居住证号码应该通过验证: 830000000000000001");
        assertTrue(validator.isValid("83000000000000001X", null), "有效的台湾居民居住证号码应该通过验证: 83000000000000001X");
        assertTrue(validator.isValid("83000000000000002x", null), "有效的台湾居民居住证号码应该通过验证: 83000000000000002x");
    }

    @Test
    public void testValidTaiwanResidences() {
        // 测试有效的台湾居民居住证号码
        TestEntity entity1 = new TestEntity("830000000000000001");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的台湾居民居住证号码应该通过验证: 830000000000000001");

        TestEntity entity2 = new TestEntity("83000000000000001X");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的台湾居民居住证号码应该通过验证: 83000000000000001X");

        TestEntity entity3 = new TestEntity("83000000000000002x");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的台湾居民居住证号码应该通过验证: 83000000000000002x");
    }

    @Test
    public void testInvalidTaiwanResidences() {
        // 测试无效的台湾居民居住证号码（不是以83开头）
        TestEntity entity1 = new TestEntity("810000000000000001");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的台湾居民居住证号码应该不通过验证: 810000000000000001");

        // 测试无效的台湾居民居住证号码（数字位数不足）
        TestEntity entity2 = new TestEntity("830000000000001");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的台湾居民居住证号码应该不通过验证: 830000000000001");

        // 测试无效的台湾居民居住证号码（数字位数过多）
        TestEntity entity3 = new TestEntity("8300000000000000001");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的台湾居民居住证号码应该不通过验证: 8300000000000000001");

        // 测试无效的台湾居民居住证号码（包含非法字符）
        TestEntity entity4 = new TestEntity("83000000000000000A");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "包含非法字符的台湾居民居住证号码应该不通过验证: 83000000000000000A");

        // 测试无效的台湾居民居住证号码（前缀不正确）
        TestEntity entity5 = new TestEntity("800000000000000001");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertFalse(violations5.isEmpty(), "无效的台湾居民居住证号码应该不通过验证: 800000000000000001");
    }

    @Test
    public void testNullAndEmptyTaiwanResidence() {
        // 直接测试验证器，null 和空字符串应该返回 true
        TaiwanResidenceValidator taiwanResidenceValidator = new TaiwanResidenceValidator();
        assertTrue(taiwanResidenceValidator.isValid(null, null), "null should return true");
        assertTrue(taiwanResidenceValidator.isValid("", null), "empty string should return true");
    }
}