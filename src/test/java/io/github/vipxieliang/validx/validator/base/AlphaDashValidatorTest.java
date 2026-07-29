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

import io.github.vipxieliang.validx.annotations.AlphaDash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AlphaDashValidator测试类
 */
public class AlphaDashValidatorTest {
    
    private final AlphaDashValidator validator = new AlphaDashValidator();
    
    @Test
    public void testValidAlphaDashStrings() {
        assertTrue(validator.isValid("abc", null));
        assertTrue(validator.isValid("ABC", null));
        assertTrue(validator.isValid("123", null));
        assertTrue(validator.isValid("abc123", null));
        assertTrue(validator.isValid("ABC123", null));
        assertTrue(validator.isValid("abc_123", null));
        assertTrue(validator.isValid("ABC-123", null));
        assertTrue(validator.isValid("abc-123_def", null));
        assertTrue(validator.isValid("_", null));
        assertTrue(validator.isValid("-", null));
        assertTrue(validator.isValid("_-", null));
        assertTrue(validator.isValid("", null)); // 空字符串
        assertTrue(validator.isValid(null, null)); // null值
    }
    
    @Test
    public void testInvalidAlphaDashStrings() {
        assertFalse(validator.isValid("abc 123", null)); // 包含空格
        assertFalse(validator.isValid("abc.123", null)); // 包含点号
        assertFalse(validator.isValid("abc@123", null)); // 包含@符号
        assertFalse(validator.isValid("abc#123", null)); // 包含#符号
        assertFalse(validator.isValid("abc$123", null)); // 包含$符号
        assertFalse(validator.isValid("abc%123", null)); // 包含%符号
    }
}