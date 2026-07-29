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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base64链式验证测试类
 */
public class Base64ValidationChainTest {

    // === 标准Base64格式测试 ===

    @Test
    public void testValidBase64_Standard() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=");  // "Hello World"

        assertTrue(validator.passed(), "标准Base64格式应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidBase64_StandardWithPlus() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXowMTIzNDU2Nzg5Kysvfg==");

        assertTrue(validator.passed(), "包含+的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_StandardWithSlash() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("PDw/Pz8+Pg==");

        assertTrue(validator.passed(), "包含/的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_OnePadding() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("cGxlYXN1cmU=");  // "pleasure"

        assertTrue(validator.passed(), "带1个填充符的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_TwoPaddings() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("YW55IGNhcm5hbCBwbGVhc3VyZS4=");

        assertTrue(validator.passed(), "带2个填充符的Base64应该通过验证");
    }

    @Test
    public void testInvalidBase64_InvalidCharacters() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ@");

        assertFalse(validator.passed(), "包含非法字符的Base64应该验证失败");
        List<String> errors = validator.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Base64"), "错误信息应该包含Base64相关内容");
    }

    @Test
    public void testInvalidBase64_NoPadding_WhenNotAllowed() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ");  // No padding

        assertFalse(validator.passed(), "不允许时，不带填充符的Base64应该验证失败");
    }

    // === URL-safe Base64格式测试 ===

    @Test
    public void testValidBase64_UrlSafe() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=", true);

        assertTrue(validator.passed(), "URL-safe Base64格式应该通过验证");
    }

    @Test
    public void testValidBase64_UrlSafe_WithHyphen() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXowMTIzNDU2Nzg5Ky0tLn4=", true);

        assertTrue(validator.passed(), "包含-的URL-safe Base64应该通过验证");
    }

    @Test
    public void testValidBase64_UrlSafe_WithUnderscore() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("YWJjZGVmX2hpamtsbW5vcA==", true);

        assertTrue(validator.passed(), "包含_的URL-safe Base64应该通过验证");
    }

    @Test
    public void testInvalidBase64_UrlSafe_WithPlus() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("29zd3t/g4eLj5OXm5+jp6g==", true);  // Contains + and /

        assertFalse(validator.passed(), "URL-safe格式不应该包含+和/");
    }

    // === 允许不带填充符的测试 ===

    @Test
    public void testValidBase64_NoPadding_WhenAllowed() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ", false, true);

        assertTrue(validator.passed(), "允许时，不带填充符的Base64应该通过验证");
    }

    @Test
    public void testValidBase64_WithPadding_WhenFlexible() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=", false, true);

        assertTrue(validator.passed(), "允许不带填充时，标准格式也应该通过验证");
    }

    @Test
    public void testValidBase64_UrlSafeNoPadding() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ", true, true);

        assertTrue(validator.passed(), "URL-safe + 不带填充应该通过验证");
    }

    // === 空值测试 ===

    @Test
    public void testNullBase64() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64(null);

        assertTrue(validator.passed(), "null值应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testEmptyBase64() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("");

        assertTrue(validator.passed(), "空字符串应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 边界情况测试 ===

    @Test
    public void testBase64_VeryShort() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("YQ==");

        assertTrue(validator.passed(), "短Base64字符串应该通过验证");
    }

    @Test
    public void testBase64_VeryLong() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gU2VkIGRvIGVpdXNtb2QgdGVtcG9yIGluY2lkaWR1bnQgdXQgbGFib3JlIGV0IGRvbG9yZSBtYWduYSBhbGlxdWEu");

        assertTrue(validator.passed(), "长Base64字符串应该通过验证");
    }

    // === 链式调用测试 ===

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=")
                .isBase64("cGxlYXN1cmU=")
                .isBase64("YW55IGNhcm5hbCBwbGVhc3VyZQ==");

        assertTrue(validator.passed(), "多个有效Base64应该全部通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=")
                .isBase64("invalid-base64")
                .isBase64("cGxlYXN1cmU=");

        assertFalse(validator.passed(), "一个无效Base64应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_MixedFormats() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=", false)  // Standard
                .isBase64("SGVsbG8gV29ybGQ=", true)   // URL-safe
                .isBase64("SGVsbG8gV29ybGQ", false, true);  // No padding

        assertTrue(validator.passed(), "混合格式验证应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_AllFailing() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("invalid-base64-1")
                .isBase64("invalid-base64-2")
                .isBase64("not-valid");

        assertFalse(validator.passed(), "多个无效Base64应该全部验证失败");
        assertEquals(3, validator.getErrors().size());
    }

    // === 与其他验证混合测试 ===

    @Test
    public void testMixedValidation_Base64AndEmail() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=")
                .isEmail("test@example.com");

        assertTrue(validator.passed(), "Base64和Email验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testMixedValidation_InvalidBase64_ValidEmail() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("invalid-base64")
                .isEmail("test@example.com");

        assertFalse(validator.passed(), "无效Base64应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testMixedValidation_Base64AndUUID() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isBase64("SGVsbG8gV29ybGQ=")
                .isUUID("550e8400-e29b-41d4-a716-446655440000");

        assertTrue(validator.passed(), "Base64和UUID验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    // === 实际应用场景测试 ===

    @Test
    public void testRealWorld_ImageData() {
        ValidationPlus validator = ValidationPlus.init();
        // 模拟图片数据的Base64编码 (JPEG header)
        validator.isBase64("/9j/4AAQSkZJRgABAQAAAQABAAD/2w==");

        assertTrue(validator.passed(), "图片Base64数据应该通过验证");
    }

    @Test
    public void testRealWorld_JWTPayload() {
        ValidationPlus validator = ValidationPlus.init();
        // JWT payload typically uses URL-safe Base64
        validator.isBase64("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", true, true);

        assertTrue(validator.passed(), "JWT payload应该通过验证");
    }
}
