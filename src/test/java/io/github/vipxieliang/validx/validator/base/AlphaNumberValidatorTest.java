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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlphaNumberValidatorTest {

    private final AlphaNumberValidator validator = new AlphaNumberValidator();

    @Test
    public void testValidAlphaNumber() {
        // 测试有效的字母数字组合
        assertTrue(validator.isValid("abc123", null), "字母和数字组合应该通过验证");
        assertTrue(validator.isValid("ABC123", null), "大写字母和数字组合应该通过验证");
        assertTrue(validator.isValid("AbC123", null), "混合大小写字母和数字应该通过验证");
        assertTrue(validator.isValid("123", null), "纯数字应该通过验证");
        assertTrue(validator.isValid("abc", null), "纯字母应该通过验证");
    }

    @Test
    public void testInvalidAlphaNumber() {
        // 测试无效的字符串（包含特殊字符）
        assertFalse(validator.isValid("abc-123", null), "包含横线的字符串不应该通过验证");
        assertFalse(validator.isValid("abc_123", null), "包含下划线的字符串不应该通过验证");
        assertFalse(validator.isValid("abc 123", null), "包含空格的字符串不应该通过验证");
        assertFalse(validator.isValid("abc@123", null), "包含@符号的字符串不应该通过验证");
        assertFalse(validator.isValid("abc.123", null), "包含点号的字符串不应该通过验证");
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试null值应该通过验证（交给@NotNull处理）
        assertTrue(validator.isValid(null, null), "null值应该通过验证");

        // 测试空字符串应该通过验证（交给@NotEmpty处理）
        assertTrue(validator.isValid("", null), "空字符串应该通过验证");
    }
}
