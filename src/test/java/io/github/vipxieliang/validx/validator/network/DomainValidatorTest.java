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

package io.github.vipxieliang.validx.validator.network;

import io.github.vipxieliang.validx.annotations.Domain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DomainValidator测试类
 */
public class DomainValidatorTest {
    
    private final DomainValidator validator = new DomainValidator();
    
    @Test
    public void testValidDomainStrings() {
        assertTrue(validator.isValid("example.com", null));
        assertTrue(validator.isValid("www.example.com", null));
        assertTrue(validator.isValid("subdomain.example.com", null));
        assertTrue(validator.isValid("example.org", null));
        assertTrue(validator.isValid("example.net", null));
        assertTrue(validator.isValid("example.co.uk", null));
        assertTrue(validator.isValid("example.io", null));
        assertTrue(validator.isValid("my-example.com", null));
        assertTrue(validator.isValid("123example.com", null));
    }
    
    @Test
    public void testInvalidDomainStrings() {
        assertFalse(validator.isValid("example", null)); // 没有顶级域名
        assertFalse(validator.isValid(".example.com", null)); // 以点开头
        assertFalse(validator.isValid("example..com", null)); // 连续的点
        assertFalse(validator.isValid("example.com.", null)); // 以点结尾
        assertFalse(validator.isValid("-example.com", null)); // 以连字符开头
        assertFalse(validator.isValid("example-.com", null)); // 以连字符结尾
        assertFalse(validator.isValid("example.c", null)); // 顶级域名太短
        assertFalse(validator.isValid("example.123", null)); // 顶级域名为纯数字
    }

    @Test
    public void testNullAndEmptyDomain() {
        // 测试null和空字符串
        assertTrue(validator.isValid("", null), "空字符串应该通过验证");
        assertTrue(validator.isValid(null, null), "null值应该通过验证");
    }
}