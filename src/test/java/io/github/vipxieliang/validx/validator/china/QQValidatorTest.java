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

import io.github.vipxieliang.validx.annotations.QQ;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class QQValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @QQ
        private String qq;

        public TestEntity(String qq) {
            this.qq = qq;
        }

        public String getQq() {
            return qq;
        }
    }

    @Test
    public void testValidQQ() {
        // 测试有效的QQ号
        TestEntity entity1 = new TestEntity("10000");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的QQ号应该通过验证: 10000");

        TestEntity entity2 = new TestEntity("123456789");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的QQ号应该通过验证: 123456789");
    }

    @Test
    public void testInvalidQQ() {
        // 测试无效的QQ号
        TestEntity entity1 = new TestEntity("1234"); // 太短
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的QQ号应该不通过验证: 1234");

        TestEntity entity2 = new TestEntity("01234"); // 以0开头
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的QQ号应该不通过验证: 01234");

        TestEntity entity3 = new TestEntity("12345a"); // 包含字母
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的QQ号应该不通过验证: 12345a");
    }

    @Test
    public void testNullAndEmptyQQ() {
        // 直接测试验证器，null 和空字符串应该返回 true
        QQValidator qqValidator = new QQValidator();
        assertTrue(qqValidator.isValid(null, null), "null should return true");
        assertTrue(qqValidator.isValid("", null), "empty string should return true");
    }
}
