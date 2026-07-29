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

/**
 * ChineseValidator测试类
 */
public class ChineseValidatorTest {

    private final ChineseValidator validator = new ChineseValidator();

    @Test
    public void testValidChineseStrings() {
        assertTrue(validator.isValid("中文", null));
        assertTrue(validator.isValid("测试", null));
        assertTrue(validator.isValid("你好世界", null));
    }

    @Test
    public void testInvalidChineseStrings() {
        assertFalse(validator.isValid("123", null)); // 数字
        assertFalse(validator.isValid("abc", null)); // 英文
        assertFalse(validator.isValid("中文123", null)); // 中文+数字
        assertFalse(validator.isValid("中文abc", null)); // 中文+英文
        assertFalse(validator.isValid("!@#$%", null)); // 特殊字符
    }

    @Test
    public void testNullAndEmptyValues() {
        assertTrue(validator.isValid(null, null), "null值应该返回true");
        assertTrue(validator.isValid("", null), "空字符串应该返回true");
    }
}
