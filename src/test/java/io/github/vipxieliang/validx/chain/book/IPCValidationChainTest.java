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

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IPCValidationChainTest {

    @Test
    void testValidIPC() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIPC("A01B1/00");
        assertTrue(chain.passed(), "有效的IPC应该通过验证");
    }

    @Test
    void testValidIPCWithSubgroup() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIPC("A01B1/01");
        assertTrue(chain.passed(), "有效的IPC（带分组）应该通过验证");
    }

    @Test
    void testValidIPCWithLongerMainGroup() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIPC("A01B12/00");
        assertTrue(chain.passed(), "有效的IPC（主组多位数）应该通过验证");
    }

    @Test
    void testValidIPCWithLongerSubgroup() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIPC("A01B1/1234");
        assertTrue(chain.passed(), "有效的IPC（分组多位数）应该通过验证");
    }

    @Test
    void testInvalidIPCWrongSection() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIPC("I01B1/00");
        assertFalse(chain.passed(), "无效的IPC（错误部）不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("IPC格式不正确", chain.getErrors().get(0));
    }

    @Test
    void testInvalidIPCWrongFormat() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIPC("A01B/00");
        assertFalse(chain.passed(), "格式错误的IPC不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("IPC格式不正确", chain.getErrors().get(0));
    }

    @Test
    void testNullAndEmptyIPC() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIPC(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isIPC("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}