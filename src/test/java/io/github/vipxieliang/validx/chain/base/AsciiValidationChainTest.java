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

/**
 * Ascii 链式验证测试类（{@code ValidX.init().isAscii(value)}）。
 *
 * <p>{@code isAscii} 校验完整 ASCII（0x00~0x7F，含控制字符），
 * 与 {@code isPrintable}（0x20~0x7E）职责互补。</p>
 */
public class AsciiValidationChainTest {

    // ==================== 合法 ASCII ====================

    @Test
    public void testValidPlainAscii() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "Hello, World!");
        assertTrue(chain.passed(), "普通 ASCII 字符串应通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testValidControlChar_Tab() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "abc\tdef");
        assertTrue(chain.passed(), "制表符属于 ASCII，应通过验证");
    }

    @Test
    public void testValidControlChar_Newline() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "abc\n");
        assertTrue(chain.passed(), "换行符属于 ASCII，应通过验证");
    }

    @Test
    public void testValidMultiLineText() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "line1\n\tline2\r\nline3");
        assertTrue(chain.passed(), "多行 ASCII 文本应通过验证");
    }

    @Test
    public void testValid_FullAsciiRange() {
        ValidX chain = ValidX.init();
        StringBuilder sb = new StringBuilder();
        for (int i = 0x00; i <= 0x7F; i++) {
            sb.append((char) i);
        }
        chain = chain.isAscii(sb.toString());
        assertTrue(chain.passed(), "0x00~0x7F 全部字符应通过验证");
    }

    // ==================== 非法：非 ASCII ====================

    @Test
    public void testInvalidChinese() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "Hello 你好");
        assertFalse(chain.passed(), "包含中文的字符串不应通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("只能包含ASCII字符（0x00-0x7F，含控制字符）", chain.getErrors().get(0));
    }

    @Test
    public void testInvalidEmoji() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "Hello 😀");
        assertFalse(chain.passed(), "包含 Emoji 的字符串不应通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidFirstNonAsciiByte() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii(String.valueOf((char) 0x80));
        assertFalse(chain.passed(), "0x80 应被拒绝");
    }

    @Test
    public void testInvalid_MixedWithControlChar() {
        ValidX chain = ValidX.init();
        chain = chain.isAscii((Object) "abc\t中文");
        assertFalse(chain.passed(), "控制字符合法但中文仍应被拒绝");
    }

    // ==================== 与 isPrintable 的分工对比 ====================

    @Test
    public void testAsciiAllowsNewlineButPrintableRejects() {
        String multiLine = "line1\nline2";

        ValidX asciiChain = ValidX.init();
        asciiChain.isAscii((Object) multiLine);
        assertTrue(asciiChain.passed(), "isAscii 应放行换行符");

        ValidX printableChain = ValidX.init();
        printableChain.isPrintable((Object) multiLine);
        assertFalse(printableChain.passed(), "isPrintable 应拒绝换行符");
    }

    // ==================== null / 空字符串 ====================

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

    // ==================== 链式调用 ====================

    @Test
    public void testChainableWithOtherRules() {
        // 演示与其他规则链式调用（isUpper 要求全为大写字母）
        ValidX chain = ValidX.init();
        chain.isAscii((Object) "ABC").isUpper((Object) "ABC");
        assertTrue(chain.passed(), "ASCII + 大写字母链式验证应通过");
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidX chain = ValidX.init();
        chain.isAscii((Object) "Hello")
                .isAscii((Object) "你好")
                .isAscii((Object) "World");
        assertFalse(chain.passed(), "含中文的中间项应导致验证失败");
        assertEquals(1, chain.getErrors().size());
    }
}