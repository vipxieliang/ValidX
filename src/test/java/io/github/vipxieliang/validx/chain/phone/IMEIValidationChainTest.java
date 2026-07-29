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

package io.github.vipxieliang.validx.chain.phone;

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IMEIValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIMEI(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isIMEI("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidIMEI() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIMEI("123412341234564"); // Valid IMEI
        assertTrue(chain.passed(), "有效的IMEI应该通过验证");
        
        chain = ValidationPlus.init();
        chain = chain.isIMEI("123412-341234564"); // Valid IMEI with dashes
        assertTrue(chain.passed(), "带连字符的有效IMEI应该通过验证");
    }

    @Test
    public void testInvalidIMEI() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIMEI("123412341234567"); // Invalid IMEI (wrong checksum)
        assertFalse(chain.passed(), "无效的IMEI不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        
        chain = ValidationPlus.init();
        chain = chain.isIMEI("12345"); // Too short
        assertFalse(chain.passed(), "过短的IMEI不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        
        chain = ValidationPlus.init();
        chain = chain.isIMEI("12345678901234567890"); // Too long
        assertFalse(chain.passed(), "过长的IMEI不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        
        chain = ValidationPlus.init();
        chain = chain.isIMEI("abc123xyz456def"); // Contains non-digits (except dashes)
        assertFalse(chain.passed(), "包含非数字字符的IMEI不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }
}