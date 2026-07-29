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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChinesePhoneOrLandlineValidatorTest {

    private final ChinesePhoneOrLandlineValidator validator = new ChinesePhoneOrLandlineValidator();

    @Test
    public void testValidMobileNumbers() {
        // 测试有效的手机号码
        assertTrue(validator.isValid("13812345678", null), "有效的中国移动手机号码应该通过验证");
        assertTrue(validator.isValid("15912345678", null), "有效的中国联通手机号码应该通过验证");
        assertTrue(validator.isValid("18812345678", null), "有效的中国电信手机号码应该通过验证");
        assertTrue(validator.isValid("19912345678", null), "有效的199号段手机号码应该通过验证");
        assertTrue(validator.isValid("17712345678", null), "有效的177号段手机号码应该通过验证");
    }

    @Test
    public void testValidLandlineNumbers() {
        // 测试有效的固定电话号码
        assertTrue(validator.isValid("010-12345678", null), "有效的北京固定电话应该通过验证");
        assertTrue(validator.isValid("021-12345678", null), "有效的上海固定电话应该通过验证");
        assertTrue(validator.isValid("0755-1234567", null), "有效的深圳固定电话应该通过验证");
        assertTrue(validator.isValid("02012345678", null), "有效的广州固定电话(无横线)应该通过验证");
        assertTrue(validator.isValid("020-12345678-123", null), "有效的带分机号的固定电话应该通过验证");
    }

    @Test
    public void testInvalidNumbers() {
        // 测试无效的号码
        assertFalse(validator.isValid("12312345678", null), "无效的手机号码前缀不应该通过验证");
        assertFalse(validator.isValid("1381234567", null), "位数不足的手机号码不应该通过验证");
        assertFalse(validator.isValid("138123456789", null), "位数过多的手机号码不应该通过验证");
        assertFalse(validator.isValid("1381234567a", null), "包含非数字的手机号码不应该通过验证");
        assertFalse(validator.isValid("12345", null), "过短的号码不应该通过验证");
        assertFalse(validator.isValid("0123456789012345", null), "过长的号码不应该通过验证");
        assertFalse(validator.isValid("010-12345abc", null), "包含字母的固定电话不应该通过验证");
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试空值和null值
        assertTrue(validator.isValid(null, null), "null值应该通过验证");
        assertTrue(validator.isValid("", null), "空字符串应该通过验证");
    }

    @Test
    public void testWithSpacesAndHyphens() {
        // 测试包含空格和横线的号码
        assertTrue(validator.isValid("138 1234 5678", null), "带空格的手机号码应该通过验证");
        assertTrue(validator.isValid("010-12345678", null), "带横线的固定电话应该通过验证");
        assertTrue(validator.isValid("021 1234 5678", null), "带空格的固定电话应该通过验证");
    }
}