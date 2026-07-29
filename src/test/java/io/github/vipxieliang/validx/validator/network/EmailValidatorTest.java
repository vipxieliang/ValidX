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

package io.github.vipxieliang.validx.validator.network;

import io.github.vipxieliang.validx.annotations.Email;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EmailValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @Email
        private String email;

        public TestEntity(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    @Test
    public void testValidEmails() {
        // 测试有效的邮箱
        TestEntity entity1 = new TestEntity("test@example.com");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的邮箱应该通过验证: test@example.com");

        TestEntity entity2 = new TestEntity("user.name@domain.co.uk");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的邮箱应该通过验证: user.name@domain.co.uk");
        
        TestEntity entity3 = new TestEntity("user+tag@example.org");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的邮箱应该通过验证: user+tag@example.org");
    }

    @Test
    public void testInvalidEmails() {
        // 测试无效的邮箱
        TestEntity entity1 = new TestEntity("invalid-email");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的邮箱不应该通过验证: invalid-email");

        TestEntity entity2 = new TestEntity("@example.com");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的邮箱不应该通过验证: @example.com");

        TestEntity entity3 = new TestEntity("user@");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "无效的邮箱不应该通过验证: user@");

        TestEntity entity4 = new TestEntity("user@domain");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "无效的邮箱不应该通过验证: user@domain");
    }

    @Test
    public void testNullAndEmptyEmail() {
        // 测试null和空字符串
        TestEntity entity1 = new TestEntity("");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "空字符串应该通过验证");

        TestEntity entity2 = new TestEntity(null);
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "null值应该通过验证");
    }
}