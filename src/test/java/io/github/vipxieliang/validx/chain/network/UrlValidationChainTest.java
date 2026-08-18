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

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isUrl(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isUrl((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidUrl() {
        ValidX chain = ValidX.init();
        chain = chain.isUrl((Object)"http://example.com");
        assertTrue(chain.passed(), "有效的URL应该通过验证");
    }

    @Test
    public void testInvalidUrl() {
        ValidX chain = ValidX.init();
        chain = chain.isUrl((Object)"invalid-url");
        assertFalse(chain.passed(), "无效的URL不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("URL格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testUrlWithProtocolWhitelist() {
        // https 在白名单内
        ValidX chain = ValidX.init();
        chain = chain.isUrl((Object)"https://example.com", "https");
        assertTrue(chain.passed(), "https 在 https 白名单内应该通过验证");

        // http 不在 https 白名单内
        chain = ValidX.init();
        chain = chain.isUrl((Object)"http://example.com", "https");
        assertFalse(chain.passed(), "http 不在 https 白名单内不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("URL格式不正确", chain.getErrors().get(0));

        // 多协议白名单
        chain = ValidX.init();
        chain = chain.isUrl((Object)"ftp://example.com/resource", "http", "https", "ftp");
        assertTrue(chain.passed(), "ftp 在多协议白名单内应该通过验证");
    }
}