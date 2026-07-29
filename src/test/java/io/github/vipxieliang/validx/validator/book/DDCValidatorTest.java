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

package io.github.vipxieliang.validx.validator.book;

import io.github.vipxieliang.validx.annotations.DDC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DDC验证器测试类
 */
public class DDCValidatorTest {

    private final DDCValidator validator = new DDCValidator();

    @Test
    public void testValidDDC() {
        // 有效的DDC分类号
        assertTrue(validator.isValid("000", null), "000 should be valid");
        assertTrue(validator.isValid("100", null), "100 should be valid");
        assertTrue(validator.isValid("200", null), "200 should be valid");
        assertTrue(validator.isValid("500", null), "500 should be valid");
        assertTrue(validator.isValid("510", null), "510 should be valid");
        assertTrue(validator.isValid("516", null), "516 should be valid");
        assertTrue(validator.isValid("516.3", null), "516.3 should be valid");
        assertTrue(validator.isValid("001", null), "001 should be valid");
        assertTrue(validator.isValid("020", null), "020 should be valid");
        assertTrue(validator.isValid("330.94", null), "330.94 should be valid");
        assertTrue(validator.isValid("658.404", null), "658.404 should be valid");
    }

    @Test
    public void testInvalidDDC() {
        // 无效的DDC分类号
        assertFalse(validator.isValid("12", null), "少于3位数字应该返回false"); // 少于3位数字
        assertFalse(validator.isValid("1234", null), "没有小数点的4位数字应该返回false"); // 没有小数点的4位数字
        assertFalse(validator.isValid("12.34", null), "少于3位数字加小数应该返回false"); // 少于3位数字加小数
        assertFalse(validator.isValid("abc", null), "字母应该返回false"); // 字母
        assertFalse(validator.isValid("12a", null), "包含字母应该返回false"); // 包含字母
        assertFalse(validator.isValid("123.", null), "以点号结尾应该返回false"); // 以点号结尾
        assertFalse(validator.isValid(".123", null), "以点号开头应该返回false"); // 以点号开头
        assertFalse(validator.isValid("123.456.789", null), "多个点号应该返回false"); // 多个点号
        assertFalse(validator.isValid("123-456", null), "包含连字符应该返回false"); // 包含连字符
        assertFalse(validator.isValid("123 456", null), "包含空格应该返回false"); // 包含空格
    }

    @Test
    public void testNullAndEmptyDDC() {
        // 直接测试验证器，null 和空字符串应该返回 true
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}