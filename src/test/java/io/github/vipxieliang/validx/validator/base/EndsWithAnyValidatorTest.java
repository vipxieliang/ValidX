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

import io.github.vipxieliang.validx.annotations.EndsWithAny;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EndsWithAny验证器测试类
 *
 * @author vipxieliang
 * @since 1.1.1
 */
public class EndsWithAnyValidatorTest {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    static class TestBean {
        @EndsWithAny({".jpg", ".jpeg", ".png", ".gif"})
        private String imageFile;

        @EndsWithAny({".txt", ".doc", ".docx", ".pdf"})
        private String documentFile;

        @EndsWithAny({"先生", "女士", "小姐"})
        private String chineseName;

        public TestBean(String imageFile, String documentFile, String chineseName) {
            this.imageFile = imageFile;
            this.documentFile = documentFile;
            this.chineseName = chineseName;
        }
    }

    @Test
    public void testValidEndsWithAny() {
        TestBean bean = new TestBean("photo.jpg", "document.pdf", "张先生");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass when strings end with any of the specified suffixes");
    }

    @Test
    public void testValidEndsWithAnyPng() {
        TestBean bean = new TestBean("image.png", "report.docx", "李女士");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Should pass for .png suffix");
    }

    @Test
    public void testInvalidEndsWithAny() {
        TestBean bean = new TestBean("file.pdf", "image.jpg", "王教授");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(3, violations.size(), "Should fail when strings don't end with any specified suffix");
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
        TestBean bean = new TestBean("photo.jpg", "file.zip", "张先生");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should have one violation for documentFile field");

        ConstraintViolation<TestBean> violation = violations.iterator().next();
        assertEquals("documentFile", violation.getPropertyPath().toString());
    }

    @Test
    public void testCaseSensitive() {
        TestBean bean = new TestBean("photo.JPG", "document.PDF", "张先生");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(2, violations.size(), "Should be case sensitive");
    }

    static class EmptySuffixBean {
        @EndsWithAny({})
        private String value;

        public EmptySuffixBean(String value) {
            this.value = value;
        }
    }

    @Test
    public void testEmptySuffixArray() {
        EmptySuffixBean bean = new EmptySuffixBean("anything");
        Set<ConstraintViolation<EmptySuffixBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size(), "Should fail with empty suffix array");
    }

    static class EmptyStringSuffixBean {
        @EndsWithAny({""})
        private String value;

        public EmptyStringSuffixBean(String value) {
            this.value = value;
        }
    }

    @Test
    public void testEmptyStringSuffix() {
        EmptyStringSuffixBean bean = new EmptyStringSuffixBean("anything");
        Set<ConstraintViolation<EmptyStringSuffixBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Any string ends with empty string");
    }
}
