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

package io.github.vipxieliang.validx.chain.book;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ISSNValidationChainTest {

    @Test
    void testValidISSN() {
        ValidaX chain = ValidaX.init();
        chain = chain.isISSN("0024-9319");
        assertTrue(chain.passed(), "有效的ISSN应该通过验证");
    }

    @Test
    void testValidISSNWithoutHyphen() {
        ValidaX chain = ValidaX.init();
        chain = chain.isISSN("00249319");
        assertTrue(chain.passed(), "有效的ISSN（无连字符）应该通过验证");
    }

    @Test
    void testValidISSNWithX() {
        ValidaX chain = ValidaX.init();
        chain = chain.isISSN("0317-8471");
        assertTrue(chain.passed(), "有效的ISSN（带X校验位）应该通过验证");
    }

    @Test
    void testInvalidISSN() {
        ValidaX chain = ValidaX.init();
        chain = chain.isISSN("1234-5678");
        assertFalse(chain.passed(), "无效的ISSN不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("ISSN格式不正确", chain.getErrors().get(0));
    }

    @Test
    void testInvalidISSNWrongLength() {
        ValidaX chain = ValidaX.init();
        chain = chain.isISSN("1234567");
        assertFalse(chain.passed(), "长度错误的ISSN不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("ISSN格式不正确", chain.getErrors().get(0));
    }

    @Test
    void testNullAndEmptyISSN() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isISSN(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidaX.init();
        chain = chain.isISSN("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}