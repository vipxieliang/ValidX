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

import io.github.vipxieliang.validx.annotations.DOI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DOI验证器测试类
 */
public class DOIValidatorTest {

    private final DOIValidator validator = new DOIValidator();

    @Test
    public void testValidDOI() {
        // 有效的DOI
        assertTrue(validator.isValid("10.1000/182", null));
        assertTrue(validator.isValid("10.1000/abc123", null));
        assertTrue(validator.isValid("10.1000/xyz.abc/123", null));
        assertTrue(validator.isValid("10.1000/123.456.789", null));
        assertTrue(validator.isValid("10.1038/nphys1170", null));
        assertTrue(validator.isValid("10.1000/ISBN1234567890", null));
        assertTrue(validator.isValid("10.1000/1234567890", null));
        // 带doi:前缀的
        assertTrue(validator.isValid("doi:10.1000/182", null));
        assertTrue(validator.isValid("DOI:10.1000/182", null));
        // 带空格的
        assertTrue(validator.isValid(" 10.1000/182 ", null));
    }

    @Test
    public void testInvalidDOI() {
        // 无效的DOI
        assertFalse(validator.isValid("10.123", null)); // 缺少斜杠
        assertFalse(validator.isValid("9.1000/182", null)); // 不以10.开头
        assertFalse(validator.isValid("10./182", null)); // 缺少注册机构标识符
        assertFalse(validator.isValid("10.1000/", null)); // 缺少后缀
        assertFalse(validator.isValid("10.1000/182.", null)); // 后缀以点结尾
    }

    @Test
    public void testNullAndEmptyDOI() {
        // 直接测试验证器，null 和空字符串应该返回 true
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }

    @Test
    public void testCaseInsensitive() {
        // 大小写不敏感测试
        assertTrue(validator.isValid("10.1000/ABC123", null));
        assertTrue(validator.isValid("10.1000/abc123", null));
        assertTrue(validator.isValid("DOI:10.1000/abc123", null));
        assertTrue(validator.isValid("doi:10.1000/ABC123", null));
    }

    @Test
    public void testWhitespaceHandling() {
        // 空白字符处理测试
        assertTrue(validator.isValid(" 10.1000/182 ", null));
        assertTrue(validator.isValid("\t10.1000/182\t", null));
        assertTrue(validator.isValid("\n10.1000/182\n", null));
        assertTrue(validator.isValid(" doi:10.1000/182 ", null));
    }
}