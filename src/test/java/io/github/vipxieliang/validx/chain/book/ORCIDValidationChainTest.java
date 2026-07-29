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

class ORCIDValidationChainTest {

    @Test
    void testValidORCID() {
        ValidaX chain = ValidaX.init();
        chain = chain.isORCID("0000-0002-1825-0097");
        assertTrue(chain.passed(), "有效的ORCID应该通过验证");
    }

    @Test
    void testValidORCIDWithoutHyphen() {
        ValidaX chain = ValidaX.init();
        chain = chain.isORCID("0000000218250097");
        assertTrue(chain.passed(), "有效的ORCID（无连字符）应该通过验证");
    }

    @Test
    void testValidORCIDWithX() {
        ValidaX chain = ValidaX.init();
        chain = chain.isORCID("0000-0001-5109-3700");
        assertTrue(chain.passed(), "有效的ORCID（带X校验位）应该通过验证");
    }

    @Test
    void testInvalidORCID() {
        ValidaX chain = ValidaX.init();
        chain = chain.isORCID("0000-0002-1825-0099");
        assertFalse(chain.passed(), "无效的ORCID不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("ORCID格式不正确", chain.getErrors().get(0));
    }

    @Test
    void testInvalidORCIDWrongLength() {
        ValidaX chain = ValidaX.init();
        chain = chain.isORCID("0000-0002-1825-009");
        assertFalse(chain.passed(), "长度错误的ORCID不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("ORCID格式不正确", chain.getErrors().get(0));
    }

    @Test
    void testNullAndEmptyORCID() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isORCID(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidaX.init();
        chain = chain.isORCID("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}