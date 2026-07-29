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

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PortValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isPort(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串
        // 新行为：默认情况下，空字符串会跳过格式校验，验证通过
        chain = ValidaX.init();
        chain = chain.isPort("");
        assertTrue(chain.passed(), "空字符串默认跳过校验，应该通过");
    }

    @Test
    public void testValidPort() {
        ValidaX chain = ValidaX.init();
        chain = chain.isPort("8080");
        assertTrue(chain.passed(), "有效的端口号应该通过验证");
        
        chain = ValidaX.init();
        chain = chain.isPort("22");
        assertTrue(chain.passed(), "有效的端口号应该通过验证");
        
        chain = ValidaX.init();
        chain = chain.isPort("65535");
        assertTrue(chain.passed(), "有效的端口号应该通过验证");
    }

    @Test
    public void testInvalidPort() {
        ValidaX chain = ValidaX.init();
        chain = chain.isPort("70000"); // 超出最大端口号范围
        assertFalse(chain.passed(), "超出范围的端口号不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        
        chain = ValidaX.init();
        chain = chain.isPort("-1"); // 负数端口号
        assertFalse(chain.passed(), "负数端口号不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        
        chain = ValidaX.init();
        chain = chain.isPort("abc"); // 非数字端口号
        assertFalse(chain.passed(), "非数字端口号不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }
}