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

import io.github.vipxieliang.validx.annotations.Xdigit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XdigitValidator测试类
 */
public class XdigitValidatorTest {
    
    private final XdigitValidator validator = new XdigitValidator();
    
    @Test
    public void testValidXdigitStrings() {
        assertTrue(validator.isValid("0123456789", null));
        assertTrue(validator.isValid("abcdef", null));
        assertTrue(validator.isValid("ABCDEF", null));
        assertTrue(validator.isValid("0a1B2c3D4e5F", null));
        assertTrue(validator.isValid("", null)); // 空字符串
        assertTrue(validator.isValid(null, null)); // null值
    }
    
    @Test
    public void testInvalidXdigitStrings() {
        assertFalse(validator.isValid("xyz", null)); // 包含非法字符
        assertFalse(validator.isValid("ghij", null)); // 包含非法字符
        assertFalse(validator.isValid("123-", null)); // 包含破折号
        assertFalse(validator.isValid("abc_def", null)); // 包含下划线
        assertFalse(validator.isValid("123 456", null)); // 包含空格
        assertFalse(validator.isValid("abc@def", null)); // 包含特殊字符
        assertFalse(validator.isValid("123.G", null)); // 包含点号
    }
}