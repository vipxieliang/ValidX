/*
 * Copyright 2025-2026 vipxieliang
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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Contains;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contains验证器测试类
 *
 * @author vipxieliang
 * @since 2026/07/31
 */
public class ContainsValidatorTest {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    static class TestBean {
        @Contains({"hello"})
        private String singleSubstring;

        @Contains({"world", "earth"})
        private String multipleSubstrings;

        @Contains(value = {"HELLO"}, ignoreCase = true)
        private String ignoreCaseSubstring;

        @Contains({"test"})
        private String optionalField;

        @Contains(value = {"@", "."}, matchAll = true)
        private String matchAllSubstrings;

        public TestBean(String single, String multiple, String ignoreCase, String optional, String matchAll) {
            this.singleSubstring = single;
            this.multipleSubstrings = multiple;
            this.ignoreCaseSubstring = ignoreCase;
            this.optionalField = optional;
            this.matchAllSubstrings = matchAll;
        }
    }

    @Test
    public void testValidContainsSingle() {
        TestBean bean = new TestBean("say hello world", "the world is beautiful", "HELLO world", "testing", "test@email.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string contains the specified substring");
    }

    @Test
    public void testInvalidContainsSingle() {
        TestBean bean = new TestBean("goodbye", "the earth", "HELLO", "sample", "test@email.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(2, violations.size(), "Should fail when string doesn't contain the specified substring");
    }

    @Test
    public void testValidContainsMultiple() {
        TestBean bean = new TestBean("hello", "earth is our home", "hello", "test", "test@email.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string contains any of the specified substrings");
    }

    @Test
    public void testValidContainsIgnoreCase() {
        TestBean bean = new TestBean("hello", "world", "hello world", "test", "test@email.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when ignoreCase is true and string contains substring (case insensitive)");
    }

    @Test
    public void testInvalidContainsIgnoreCase() {
        TestBean bean = new TestBean("hello", "world", "goodbye", "test", "test@email.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail when string doesn't contain substring even with ignoreCase");
    }

    @Test
    public void testValidMatchAll() {
        TestBean bean = new TestBean("hello", "world", "hello", "test", "test@example.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string contains all specified substrings");
    }

    @Test
    public void testInvalidMatchAll() {
        TestBean bean = new TestBean("hello", "world", "hello", "test", "test@example");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail when string doesn't contain all specified substrings");
    }

    @Test
    public void testNullValue() {
        TestBean bean = new TestBean(null, null, null, null, null);
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass for null values (handled by @NotNull)");
    }

    @Test
    public void testEmptyString() {
        TestBean bean = new TestBean("", "", "", "", "");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass for empty strings (handled by @NotEmpty/@NotBlank)");
    }

    @Test
    public void testContainsAtBeginning() {
        TestBean bean = new TestBean("hello there", "world peace", "HELLO friend", "testing", "@example.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when substring is at the beginning");
    }

    @Test
    public void testContainsAtEnd() {
        TestBean bean = new TestBean("say hello", "welcome to the world", "say HELLO", "unit test", "test@.com");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when substring is at the end");
    }

    @Test
    public void testContainsInMiddle() {
        TestBean bean = new TestBean("say hello world", "the world is round", "say HELLO there", "this is a test", "a@b.c");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when substring is in the middle");
    }
}
