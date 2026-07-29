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

package io.github.vipxieliang.validx.chain.education;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DegreeCertificateValidationChainTest {

    @Test
    public void testValidDegreeCertificate() {
        ValidaX chain = ValidaX.init();
        chain = chain.isDegreeCertificate((Object)"1075522008000001");
        assertTrue(chain.passed(), "有效的学位证书应该通过验证");
    }

    @Test
    public void testInvalidDegreeCertificate() {
        ValidaX chain = ValidaX.init();
        chain = chain.isDegreeCertificate((Object)"107552200800000");
        assertFalse(chain.passed(), "无效的学位证书不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("学位证书编号格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyDegreeCertificate() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isDegreeCertificate(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidaX.init();
        chain = chain.isDegreeCertificate((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}