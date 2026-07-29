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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base64验证器测试类
 */
public class Base64ValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === 标准Base64格式测试 ===

    @Test
    public void testValidBase64_Standard() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "SGVsbG8gV29ybGQ=";  // "Hello World"

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "标准Base64格式应该通过验证");
    }

    @Test
    public void testValidBase64_StandardWithPlus() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXowMTIzNDU2Nzg5Kysvfg==";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含+的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_StandardWithSlash() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "PDw/Pz8+Pg==";  // Contains /

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含/的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_OnePadding() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "cGxlYXN1cmU=";  // "pleasure"

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带1个填充符的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_TwoPaddings() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "YW55IGNhcm5hbCBwbGVhc3VyZQ==";  // "any carnal pleasure"

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带2个填充符的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_NoPadding_WhenAllowed() {
        TestDTOFlexible dto = new TestDTOFlexible();
        dto.data = "SGVsbG8gV29ybGQ";  // No padding

        Set<ConstraintViolation<TestDTOFlexible>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "允许时，不带填充符的Base64应该通过验证");
    }

    @Test
    public void testInvalidBase64_NoPadding_WhenNotAllowed() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "SGVsbG8gV29ybGQ";  // No padding, length not multiple of 4

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "不允许时，不带填充符的Base64应该验证失败");
    }

    @Test
    public void testInvalidBase64_InvalidCharacters() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "SGVsbG8gV29ybGQ@";  // Contains @ which is invalid

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "包含非法字符的Base64应该验证失败");
    }

    @Test
    public void testInvalidBase64_InvalidPaddingPosition() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "SGVs=G8gV29ybGQ=";  // Padding in wrong position

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "填充符位置错误的Base64应该验证失败");
    }

    @Test
    public void testInvalidBase64_TooManyPaddings() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "SGVsbG8gV29ybGQ===";  // Three padding characters

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "填充符过多的Base64应该验证失败");
    }

    // === URL-safe Base64格式测试 ===

    @Test
    public void testValidBase64_UrlSafe() {
        TestDTOUrlSafe dto = new TestDTOUrlSafe();
        dto.data = "SGVsbG8gV29ybGQ=";

        Set<ConstraintViolation<TestDTOUrlSafe>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "URL-safe Base64格式应该通过验证");
    }

    @Test
    public void testValidBase64_UrlSafe_WithHyphen() {
        TestDTOUrlSafe dto = new TestDTOUrlSafe();
        dto.data = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXowMTIzNDU2Nzg5Ky0tLn4=";

        Set<ConstraintViolation<TestDTOUrlSafe>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含-的URL-safe Base64应该通过验证");
    }

    @Test
    public void testValidBase64_UrlSafe_WithUnderscore() {
        TestDTOUrlSafe dto = new TestDTOUrlSafe();
        dto.data = "YWJjZGVmX2hpamtsbW5vcA==";

        Set<ConstraintViolation<TestDTOUrlSafe>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含_的URL-safe Base64应该通过验证");
    }

    @Test
    public void testInvalidBase64_UrlSafe_WithPlus() {
        TestDTOUrlSafe dto = new TestDTOUrlSafe();
        dto.data = "29zd3t/g4eLj5OXm5+jp6g==";  // Contains + and /

        Set<ConstraintViolation<TestDTOUrlSafe>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "URL-safe格式不应该包含+和/");
    }

    @Test
    public void testInvalidBase64_UrlSafe_WithSlash() {
        TestDTOUrlSafe dto = new TestDTOUrlSafe();
        dto.data = "PDw/Pz8+Pg==";  // Contains /

        Set<ConstraintViolation<TestDTOUrlSafe>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "URL-safe格式不应该包含/");
    }

    // === 空值测试 ===

    @Test
    public void testNullBase64() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = null;

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证");
    }

    @Test
    public void testEmptyBase64() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证");
    }

    // === 边界情况测试 ===

    @Test
    public void testBase64_VeryShort() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "YQ==";  // Single character "a"

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "短Base64字符串应该通过验证");
    }

    @Test
    public void testBase64_VeryLong() {
        TestDTOStandard dto = new TestDTOStandard();
        // Very long Base64 string (valid concatenated string without padding in middle)
        dto.data = "TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gU2VkIGRvIGVpdXNtb2QgdGVtcG9yIGluY2lkaWR1bnQgdXQgbGFib3JlIGV0IGRvbG9yZSBtYWduYSBhbGlxdWEu";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "长Base64字符串应该通过验证");
    }

    @Test
    public void testBase64_AllUpperCase() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo=";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "全大写Base64应该通过验证");
    }

    @Test
    public void testBase64_AllLowerCase() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo=";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "全小写Base64应该通过验证");
    }

    @Test
    public void testBase64_AllNumbers() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.data = "MDEyMzQ1Njc4OQ==";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "全数字Base64应该通过验证");
    }

    // === 测试DTO类 ===

    static class TestDTOStandard {
        @Base64
        String data;
    }

    static class TestDTOUrlSafe {
        @Base64(urlSafe = true)
        String data;
    }

    static class TestDTOFlexible {
        @Base64(allowNoPadding = true)
        String data;
    }
}
