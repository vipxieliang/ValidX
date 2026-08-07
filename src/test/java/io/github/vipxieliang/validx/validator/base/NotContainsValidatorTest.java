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

import io.github.vipxieliang.validx.annotations.NotContains;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotContains验证器测试类
 *
 * @author vipxieliang
 * @since 2026/08/07
 */
public class NotContainsValidatorTest {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    static class TestBean {
        @NotContains({"admin"})
        private String singleSubstring;

        @NotContains({"bad", "evil"})
        private String multipleSubstrings;

        @NotContains(value = {"ADMIN"}, ignoreCase = true)
        private String ignoreCaseSubstring;

        @NotContains({"forbidden"})
        private String optionalField;

        @NotContains(value = {"javascript:", "data:"}, matchAll = true)
        private String matchAllSubstrings;

        @NotContains(value = {"script", "alert"}, matchAll = false)
        private String matchAnySubstrings;

        public TestBean(String single, String multiple, String ignoreCase, String optional, String matchAll, String matchAny) {
            this.singleSubstring = single;
            this.multipleSubstrings = multiple;
            this.ignoreCaseSubstring = ignoreCase;
            this.optionalField = optional;
            this.matchAllSubstrings = matchAll;
            this.matchAnySubstrings = matchAny;
        }
    }

    @Test
    public void testValidNotContainsSingle() {
        TestBean bean = new TestBean("user123", "good content", "user", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string doesn't contain the specified substring");
    }

    @Test
    public void testInvalidNotContainsSingle() {
        TestBean bean = new TestBean("admin123", "good", "user", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail when string contains the specified substring");
    }

    @Test
    public void testValidNotContainsMultiple() {
        TestBean bean = new TestBean("user", "good content", "user", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string doesn't contain any of the specified substrings");
    }

    @Test
    public void testInvalidNotContainsMultiple() {
        TestBean bean = new TestBean("user", "this is bad", "user", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail when string contains any of the specified substrings");
    }

    @Test
    public void testValidNotContainsIgnoreCase() {
        TestBean bean = new TestBean("user", "good", "user123", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string doesn't contain substring (case insensitive)");
    }

    @Test
    public void testInvalidNotContainsIgnoreCase() {
        TestBean bean = new TestBean("user", "good", "admin123", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail when string contains substring (case insensitive)");
    }

    @Test
    public void testValidMatchAll() {
        TestBean bean = new TestBean("user", "good", "user", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string doesn't contain all specified substrings");
    }

    @Test
    public void testInvalidMatchAll() {
        TestBean bean = new TestBean("user", "good", "user", "allowed", "javascript:alert(1)", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail when string contains any of the specified substrings with matchAll=true");
    }

    @Test
    public void testValidMatchAny() {
        TestBean bean = new TestBean("user", "good", "user", "allowed", "https://example.com", "hello world");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when string doesn't contain at least one of the specified substrings");
    }

    @Test
    public void testInvalidMatchAny() {
        TestBean bean = new TestBean("user", "good", "user", "allowed", "https://example.com", "script alert");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail when string contains all of the specified substrings with matchAll=false");
    }

    @Test
    public void testNullValue() {
        TestBean bean = new TestBean(null, null, null, null, null, null);
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass for null values (handled by @NotNull)");
    }

    @Test
    public void testEmptyString() {
        TestBean bean = new TestBean("", "", "", "", "", "");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass for empty strings (handled by @NotEmpty/@NotBlank)");
    }

    @Test
    public void testNotContainsAtBeginning() {
        TestBean bean = new TestBean("username", "content", "username", "allowed", "https://example.com", "content");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when forbidden substring is not at the beginning");
    }

    @Test
    public void testNotContainsAtEnd() {
        TestBean bean = new TestBean("user", "content here", "user", "text", "https://example.com", "text");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when forbidden substring is not at the end");
    }

    @Test
    public void testNotContainsInMiddle() {
        TestBean bean = new TestBean("username", "good content here", "username", "some text", "https://example.com", "some text");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when forbidden substring is not in the middle");
    }

    @Test
    public void testSecurityUseCase() {
        TestBean bean = new TestBean("normaluser", "safe content", "regularuser", "safe", "https://safe-site.com", "safe text");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass for security validation when no forbidden keywords present");
    }

    @Test
    public void testSecurityFailCase() {
        TestBean bean = new TestBean("admin_user", "safe content", "regularuser", "safe", "https://safe-site.com", "safe text");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail for security validation when forbidden keyword is present");
    }
}
