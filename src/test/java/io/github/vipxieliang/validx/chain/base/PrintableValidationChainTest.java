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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Printable 链式验证测试类（{@code ValidX.init().isPrintable(value)}）。
 */
public class PrintableValidationChainTest {

    // ==================== 合法可打印 ASCII ====================

    @Test
    public void testValid_PlainAscii() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "Hello, World!");
        assertTrue(chain.passed(), "普通可打印 ASCII 应通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testValid_Punctuation() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "!@#$%^&*()_+-=");
        assertTrue(chain.passed(), "可打印标点应通过验证");
    }

    @Test
    public void testValid_Space() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) " ");
        assertTrue(chain.passed(), "空格应通过验证");
    }

    @Test
    public void testValid_LowerBoundary() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable(String.valueOf((char) 0x20));
        assertTrue(chain.passed(), "0x20（空格）边界应通过");
    }

    @Test
    public void testValid_UpperBoundary() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable(String.valueOf((char) 0x7E));
        assertTrue(chain.passed(), "0x7E（~）边界应通过");
    }

    @Test
    public void testValid_FullPrintableRange() {
        ValidX chain = ValidX.init();
        StringBuilder sb = new StringBuilder();
        for (int i = 0x20; i <= 0x7E; i++) {
            sb.append((char) i);
        }
        chain = chain.isPrintable(sb.toString());
        assertTrue(chain.passed(), "0x20~0x7E 全部字符应通过验证");
    }

    // ==================== 控制字符必须被拒绝 ====================

    @Test
    public void testInvalid_Tab() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "abc\tdef");
        assertFalse(chain.passed(), "TAB 应被拒绝");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalid_LineFeed() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "line1\nline2");
        assertFalse(chain.passed(), "LF 应被拒绝");
    }

    @Test
    public void testInvalid_CarriageReturn() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "abc\rdef");
        assertFalse(chain.passed(), "CR 应被拒绝");
    }

    @Test
    public void testInvalid_Del() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable("abc" + (char) 0x7F);
        assertFalse(chain.passed(), "DEL 0x7F 应被拒绝");
    }

    @Test
    public void testInvalid_Nul() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable("abc" + (char) 0x00);
        assertFalse(chain.passed(), "NUL 0x00 应被拒绝");
    }

    // ==================== 非 ASCII 必须被拒绝 ====================

    @Test
    public void testInvalid_Chinese() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "你好");
        assertFalse(chain.passed(), "含中文应验证失败");
        List<String> errors = chain.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ASCII"),
                "错误信息应提及 ASCII");
    }

    @Test
    public void testInvalid_Emoji() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "Hi 😀");
        assertFalse(chain.passed(), "含 Emoji 应验证失败");
    }

    @Test
    public void testInvalid_NonAsciiBoundary() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable(String.valueOf((char) 0x80));
        assertFalse(chain.passed(), "0x80 应被拒绝");
    }

    // ==================== null / 空字符串 ====================

    @Test
    public void testNull_PassesValidation() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable(null);
        assertTrue(chain.passed(), "null 值应通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testEmpty_PassesValidation() {
        ValidX chain = ValidX.init();
        chain = chain.isPrintable((Object) "");
        assertTrue(chain.passed(), "空字符串应通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    // ==================== 链式调用 ====================

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidX chain = ValidX.init();
        chain.isPrintable((Object) "Hello, World!")
                .isPrintable((Object) "abc123")
                .isPrintable((Object) "!@#$");
        assertTrue(chain.passed(), "多个合法可打印字符串链式应全部通过");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidX chain = ValidX.init();
        chain.isPrintable((Object) "Hello")
                .isPrintable((Object) "Bad\tTab")
                .isPrintable((Object) "Good");
        assertFalse(chain.passed(), "含 TAB 的中间项应导致验证失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testChainedValidation_AllFailing() {
        ValidX chain = ValidX.init();
        chain.isPrintable((Object) "Bad\n")
                .isPrintable((Object) "Bad\r")
                .isPrintable((Object) "你好");
        assertFalse(chain.passed(), "多个非法值应全部验证失败");
        assertEquals(3, chain.getErrors().size());
    }

    @Test
    public void testChainedValidation_MixedWithOtherRules() {
        ValidX chain = ValidX.init();
        chain.isPrintable((Object) "ABC")
                .isUpper((Object) "ABC");
        assertTrue(chain.passed(), "Printable + Upper 链式验证应通过");
        assertEquals(0, chain.getErrors().size());
    }
}