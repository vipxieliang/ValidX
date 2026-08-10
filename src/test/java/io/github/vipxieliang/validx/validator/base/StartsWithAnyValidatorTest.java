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

import io.github.vipxieliang.validx.annotations.StartsWithAny;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StartsWithAny验证器测试类
 *
 * @author vipxieliang
 * @since 1.1.1
 */
public class StartsWithAnyValidatorTest {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    static class TestBean {
        @StartsWithAny({"http://", "https://"})
        private String url;

        @StartsWithAny({"Mr.", "Mrs.", "Ms.", "Dr."})
        private String title;

        @StartsWithAny({"张", "王", "李", "赵"})
        private String chineseName;

        public TestBean(String url, String title, String chineseName) {
            this.url = url;
            this.title = title;
            this.chineseName = chineseName;
        }
    }

    @Test
    public void testValidStartsWithAny() {
        TestBean bean = new TestBean("http://example.com", "Mr. Smith", "张三");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when strings start with any of the specified prefixes");
    }

    @Test
    public void testValidStartsWithAnyHttps() {
        TestBean bean = new TestBean("https://example.com", "Dr. Johnson", "王五");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass for https:// prefix");
    }

    @Test
    public void testInvalidStartsWithAny() {
        TestBean bean = new TestBean("ftp://example.com", "Prof. Brown", "刘六");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(3, violations.size(), "Should fail when strings don't start with any specified prefix");
    }

    @Test
    public void testNullValue() {
        TestBean bean = new TestBean(null, null, null);
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Null values should pass validation");
    }

    @Test
    public void testEmptyString() {
        TestBean bean = new TestBean("", "", "");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Empty strings should pass validation");
    }

    @Test
    public void testMixedValidAndInvalid() {
        TestBean bean = new TestBean("http://example.com", "Prof. Brown", "张三");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should have one violation for title field");

        ConstraintViolation<TestBean> violation = violations.iterator().next();
        assertEquals("title", violation.getPropertyPath().toString());
    }

    @Test
    public void testCaseSensitive() {
        TestBean bean = new TestBean("HTTP://example.com", "mr. Smith", "张三");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(2, violations.size(), "Should be case sensitive");
    }

    static class EmptyPrefixBean {
        @StartsWithAny({})
        private String value;

        public EmptyPrefixBean(String value) {
            this.value = value;
        }
    }

    @Test
    public void testEmptyPrefixArray() {
        EmptyPrefixBean bean = new EmptyPrefixBean("anything");
        Set<ConstraintViolation<EmptyPrefixBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail with empty prefix array");
    }

    static class EmptyStringPrefixBean {
        @StartsWithAny({""})
        private String value;

        public EmptyStringPrefixBean(String value) {
            this.value = value;
        }
    }

    @Test
    public void testEmptyStringPrefix() {
        EmptyStringPrefixBean bean = new EmptyStringPrefixBean("anything");
        Set<ConstraintViolation<EmptyStringPrefixBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Any string starts with empty string");
    }

    static class IgnoreCaseBean {
        @StartsWithAny(value = {"http://", "https://"}, ignoreCase = true)
        private String url;

        @StartsWithAny(value = {".jpg", ".png"}, ignoreCase = true)
        private String extension;

        public IgnoreCaseBean(String url, String extension) {
            this.url = url;
            this.extension = extension;
        }
    }

    @Test
    public void testIgnoreCaseTrue() {
        IgnoreCaseBean bean = new IgnoreCaseBean("HTTP://example.com", ".JPG");
        Set<ConstraintViolation<IgnoreCaseBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when ignoreCase is true");
    }

    @Test
    public void testIgnoreCaseMixedCase() {
        IgnoreCaseBean bean = new IgnoreCaseBean("HtTp://example.com", ".JpG");
        Set<ConstraintViolation<IgnoreCaseBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass with mixed case when ignoreCase is true");
    }

    @Test
    public void testIgnoreCaseLowerCase() {
        IgnoreCaseBean bean = new IgnoreCaseBean("http://example.com", ".jpg");
        Set<ConstraintViolation<IgnoreCaseBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass with lowercase when ignoreCase is true");
    }

    @Test
    public void testIgnoreCaseInvalid() {
        IgnoreCaseBean bean = new IgnoreCaseBean("ftp://example.com", ".gif");
        Set<ConstraintViolation<IgnoreCaseBean>> violations = validator.validate(bean);
        assertEquals(2, violations.size(), "Should fail when prefix doesn't match even with ignoreCase");
    }
}
