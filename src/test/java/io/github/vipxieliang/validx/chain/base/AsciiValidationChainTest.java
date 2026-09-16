/*
* Copyright 2025-2026 vipxieliang
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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AsciiValidationChainTest {

    @Test
    public void testValidPrintableAscii() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "Hello, World!");
        assertTrue(chain.passed(), "可打印 ASCII 字符串应通过验证");
    }

    @Test
    public void testInvalidPrintableAscii() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "Hello 你好");
        assertFalse(chain.passed(), "包含中文的字符串不应通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("只能包含ASCII字符（0x20-0x7E，不含控制字符）", chain.getErrors().get(0));
    }

    @Test
    public void testRejectControlCharByDefault() {
        // 默认仅允许可打印 ASCII，控制字符（如换行）应被拒绝
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "abc\n");
        assertFalse(chain.passed(), "默认情况下换行符应被拒绝");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testAllowControlChar() {
        // 显式放行控制字符
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "abc\tdef", true);
        assertTrue(chain.passed(), "allowControlChar=true 时 TAB 应被允许");
    }

    @Test
    public void testAllowControlCharStillRejectsNonAscii() {
        // 即便放行控制字符，非 ASCII（中文等）仍应拒绝
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "abc\t中文", true);
        assertFalse(chain.passed(), "allowControlChar=true 时仍应拒绝中文");
    }

    @Test
    public void testNullAndEmpty() {
        // null 值应通过（交给 @NotNull 处理）
        ValidX chain = ValidX.init();
        chain = chain.isAscii(null);
        assertTrue(chain.passed(), "null 值应通过验证");

        // 空字符串应通过（交给 @NotEmpty 处理）
        chain = ValidX.init();
        chain = chain.isAscii((Object) "");
        assertTrue(chain.passed(), "空字符串应通过验证");
    }

    @Test
    public void testChainableWithOtherRules() {
        // 演示与其他规则链式调用（isUpper 要求全为大写字母）
        ValidX chain = ValidX.init();
        chain.isAscii((Object) "ABC").isUpper((Object) "ABC");
        assertTrue(chain.passed(), "可打印 ASCII + 大写字母链式验证应通过");
    }
}
