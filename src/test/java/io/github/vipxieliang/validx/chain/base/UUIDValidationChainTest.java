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

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUID链式验证测试类
 */
public class UUIDValidationChainTest {

    // === 标准格式测试（带连字符） ===

    @Test
    public void testValidUUID_StandardFormat() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-446655440000");

        assertTrue(validator.passed(), "标准UUID格式应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidUUID_Lowercase() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-446655440000");

        assertTrue(validator.passed(), "小写UUID应该通过验证");
    }

    @Test
    public void testValidUUID_Uppercase() {
        ValidX validator = ValidX.init();
        validator.isUUID("550E8400-E29B-41D4-A716-446655440000");

        assertTrue(validator.passed(), "大写UUID应该通过验证");
    }

    @Test
    public void testValidUUID_MixedCase() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-E29b-41D4-a716-446655440000");

        assertTrue(validator.passed(), "大小写混合UUID应该通过验证");
    }

    @Test
    public void testInvalidUUID_NoHyphens_DefaultBehavior() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400e29b41d4a716446655440000");

        assertFalse(validator.passed(), "不带连字符的UUID在默认情况下应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidUUID_WrongFormat() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716");

        assertFalse(validator.passed(), "错误格式的UUID应该验证失败");
        List<String> errors = validator.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("UUID"), "错误信息应该包含UUID相关内容");
    }

    @Test
    public void testInvalidUUID_InvalidCharacters() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-44665544000g");

        assertFalse(validator.passed(), "包含非十六进制字符的UUID应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    // === 允许不带连字符格式的测试 ===

    @Test
    public void testValidUUID_WithoutHyphens_WhenAllowed() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400e29b41d4a716446655440000", true);

        assertTrue(validator.passed(), "允许时，不带连字符的UUID应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidUUID_WithHyphens_WhenFlexible() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-446655440000", true);

        assertTrue(validator.passed(), "允许不带连字符时，标准格式也应该通过验证");
    }

    @Test
    public void testInvalidUUID_WrongLength_WithoutHyphens() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400e29b41d4a716", true);

        assertFalse(validator.passed(), "长度错误的UUID应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    // === 空值测试 ===

    @Test
    public void testNullUUID() {
        ValidX validator = ValidX.init();
        validator.isUUID(null);

        assertTrue(validator.passed(), "null值应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testEmptyUUID() {
        ValidX validator = ValidX.init();
        validator.isUUID("");

        assertTrue(validator.passed(), "空字符串应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 边界情况测试 ===

    @Test
    public void testUUID_AllZeros() {
        ValidX validator = ValidX.init();
        validator.isUUID("00000000-0000-0000-0000-000000000000");

        assertTrue(validator.passed(), "全零UUID应该通过验证");
    }

    @Test
    public void testUUID_AllFs() {
        ValidX validator = ValidX.init();
        validator.isUUID("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");

        assertTrue(validator.passed(), "全F UUID应该通过验证");
    }

    // === 链式调用测试 ===

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-446655440000")
                .isUUID("123e4567-e89b-12d3-a456-426614174000")
                .isUUID("00000000-0000-0000-0000-000000000000");

        assertTrue(validator.passed(), "多个有效UUID应该全部通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-446655440000")
                .isUUID("invalid-uuid")
                .isUUID("123e4567-e89b-12d3-a456-426614174000");

        assertFalse(validator.passed(), "一个无效UUID应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_MixedFormats() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-446655440000", false)
                .isUUID("123e4567e89b12d3a456426614174000", true)
                .isUUID("00000000-0000-0000-0000-000000000000", true);

        assertTrue(validator.passed(), "混合格式验证应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_AllFailing() {
        ValidX validator = ValidX.init();
        validator.isUUID("invalid-uuid-1")
                .isUUID("invalid-uuid-2")
                .isUUID("not-a-uuid");

        assertFalse(validator.passed(), "多个无效UUID应该全部验证失败");
        assertEquals(3, validator.getErrors().size());
    }

    // === 与其他验证混合测试 ===

    @Test
    public void testMixedValidation_UUIDAndEmail() {
        ValidX validator = ValidX.init();
        validator.isUUID("550e8400-e29b-41d4-a716-446655440000")
                .isEmail("test@example.com");

        assertTrue(validator.passed(), "UUID和Email验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testMixedValidation_InvalidUUID_ValidEmail() {
        ValidX validator = ValidX.init();
        validator.isUUID("invalid-uuid")
                .isEmail("test@example.com");

        assertFalse(validator.passed(), "无效UUID应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }
}
