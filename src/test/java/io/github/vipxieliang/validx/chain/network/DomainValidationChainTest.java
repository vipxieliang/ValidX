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

package io.github.vipxieliang.validx.chain.network;

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DomainValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isDomain(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isDomain((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidDomain() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isDomain((Object)"example.com");
        assertTrue(chain.passed(), "有效的域名应该通过验证");
    }

    @Test
    public void testInvalidDomain() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isDomain((Object)"example");
        assertFalse(chain.passed(), "无效的域名不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("域名格式不正确", chain.getErrors().get(0));
    }
}