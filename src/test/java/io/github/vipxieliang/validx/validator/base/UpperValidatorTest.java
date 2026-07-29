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

import io.github.vipxieliang.validx.annotations.Upper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UpperValidator测试类
 */
public class UpperValidatorTest {
    
    private final UpperValidator validator = new UpperValidator();
    
    @Test
    public void testValidUpperStrings() {
        assertTrue(validator.isValid("ABC", null));
        assertTrue(validator.isValid("ABCDEFGHIJKLMNOPQRSTUVWXYZ", null));
        assertTrue(validator.isValid("", null)); // 空字符串
        assertTrue(validator.isValid(null, null)); // null值
    }
    
    @Test
    public void testInvalidUpperStrings() {
        assertFalse(validator.isValid("abc", null)); // 小写字母
        assertFalse(validator.isValid("Abc", null)); // 首字母小写
        assertFalse(validator.isValid("aBc", null)); // 中间有小写
        assertFalse(validator.isValid("abC", null)); // 末尾小写
        assertFalse(validator.isValid("ABC123", null)); // 包含数字
        assertFalse(validator.isValid("ABC-def", null)); // 包含破折号
        assertFalse(validator.isValid("ABC_def", null)); // 包含下划线
        assertFalse(validator.isValid("ABC DEF", null)); // 包含空格
        assertFalse(validator.isValid("ABC@DEF", null)); // 包含特殊字符
    }
}