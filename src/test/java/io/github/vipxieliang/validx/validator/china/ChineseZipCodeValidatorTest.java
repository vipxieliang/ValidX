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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.ChineseZipCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChineseZipCodeValidator测试类
 */
public class ChineseZipCodeValidatorTest {
    
    private final ChineseZipCodeValidator validator = new ChineseZipCodeValidator();
    
    @Test
    public void testValidChineseZipCode() {
        assertTrue(validator.isValid("100000", null)); // 北京市东城区
        assertTrue(validator.isValid("200000", null)); // 上海市黄浦区
        assertTrue(validator.isValid("518000", null)); // 广东省深圳市
        assertTrue(validator.isValid("610000", null)); // 四川省成都市
        assertTrue(validator.isValid("830000", null)); // 新疆乌鲁木齐
    }
    
    @Test
    public void testInvalidChineseZipCode() {
        assertFalse(validator.isValid("12345", null)); // 位数不足
        assertFalse(validator.isValid("1234567", null)); // 位数过多
        assertFalse(validator.isValid("12345a", null)); // 包含字母
        assertFalse(validator.isValid("123 45", null)); // 包含空格
        assertFalse(validator.isValid("123-456", null)); // 包含连字符
        assertFalse(validator.isValid("12345@", null)); // 包含特殊字符
    }

    @Test
    public void testNullAndEmptyChineseZipCode() {
        // 直接测试验证器，null 和空字符串应该返回 true
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}