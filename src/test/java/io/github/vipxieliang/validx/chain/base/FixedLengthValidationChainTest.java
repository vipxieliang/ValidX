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
import io.github.vipxieliang.validx.enums.LengthCountMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FixedLength 链式验证测试类（{@code ValidX.init().isFixedLength(...)}）。
 *
 * <p>覆盖：null / 空串放行、定长基本正反、4 个重载的 trim / countMode 行为、错误消息格式、
 * 链式调用。</p>
 */
public class FixedLengthValidationChainTest {

    // -----------------------------------------------------------------------
    // 1. null / 空串放行
    // -----------------------------------------------------------------------

    @Test
    public void testValid_NullString() {
        ValidX chain = ValidX.init().isFixedLength(null, 6);
        assertTrue(chain.passed(), "null 应放行");
    }

    @Test
    public void testValid_EmptyString() {
        ValidX chain = ValidX.init().isFixedLength("", 6);
        assertTrue(chain.passed(), "空串应放行");
    }

    // -----------------------------------------------------------------------
    // 2. 默认重载：CHAR 口径，不 trim
    // -----------------------------------------------------------------------

    @Test
    public void testValid_DefaultExactLength() {
        ValidX chain = ValidX.init().isFixedLength("123456", 6);
        assertTrue(chain.passed(), "恰好 6 位应通过");
    }

    @Test
    public void testInvalid_DefaultTooShort() {
        ValidX chain = ValidX.init().isFixedLength("12345", 6);
        assertFalse(chain.passed());
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalid_DefaultTooLong() {
        ValidX chain = ValidX.init().isFixedLength("1234567", 6);
        assertFalse(chain.passed());
    }

    // -----------------------------------------------------------------------
    // 3. 前导零：默认不改输入
    // -----------------------------------------------------------------------

    @Test
    public void testValid_LeadingZeros_Default() {
        ValidX chain = ValidX.init().isFixedLength("000123", 6);
        assertTrue(chain.passed(), "前导零默认不被 trim，应通过");
    }

    // -----------------------------------------------------------------------
    // 4. trim 重载：trimStart / trimEnd 独立行为
    // -----------------------------------------------------------------------

    @Test
    public void testValid_TrimEnd_StripsTrailingSpace() {
        ValidX chain = ValidX.init().isFixedLength("12345  ", 5, false, true);
        assertTrue(chain.passed(), "trimEnd=true，尾随空格剥离后等于 5");
    }

    @Test
    public void testValid_TrimStart_StripsLeadingSpace() {
        ValidX chain = ValidX.init().isFixedLength("  12345", 5, true, false);
        assertTrue(chain.passed(), "trimStart=true，前导空格剥离后等于 5");
    }

    @Test
    public void testValid_TrimBoth_StripsBoth() {
        ValidX chain = ValidX.init().isFixedLength("  123  ", 3, true, true);
        assertTrue(chain.passed(), "双 trim 后等于 3");
    }

    @Test
    public void testInvalid_Default_LeadingSpaceIncreasesLength() {
        ValidX chain = ValidX.init().isFixedLength("  1234", 5);
        assertFalse(chain.passed(), "默认不 trim，前导空格计入长度");
    }

    @Test
    public void testInvalid_TrimEnd_LeadingSpaceStillCounts() {
        ValidX chain = ValidX.init().isFixedLength(" 12345", 5, false, true);
        assertFalse(chain.passed(), "trimEnd=true 不影响前导空格");
    }

    // -----------------------------------------------------------------------
    // 5. countMode 重载
    // -----------------------------------------------------------------------

    @Test
    public void testValid_CodePoint_Emoji() {
        ValidX chain = ValidX.init().isFixedLength("😀😀😀😀😀", 5, LengthCountMode.CODE_POINT);
        assertTrue(chain.passed(), "CODE_POINT 模式下 5 个 emoji 算 5 个码点");
    }

    @Test
    public void testInvalid_Char_EmojiOverCounts() {
        ValidX chain = ValidX.init().isFixedLength("😀😀😀😀😀", 5, LengthCountMode.CHAR);
        assertFalse(chain.passed(), "CHAR 模式下 5 个 emoji 算 10 位");
    }

    @Test
    public void testValid_Grapheme_Emoji() {
        ValidX chain = ValidX.init().isFixedLength("😀😀😀😀😀", 5, LengthCountMode.GRAPHEME);
        assertTrue(chain.passed(), "GRAPHEME 模式下 5 个 emoji 算 5 个字素");
    }

    // -----------------------------------------------------------------------
    // 6. 全配重载：trim + countMode
    // -----------------------------------------------------------------------

    @Test
    public void testValid_FullOverload_TrimStartAndCodePoint() {
        ValidX chain = ValidX.init()
                .isFixedLength("  😀😀😀😀😀", 5, true, false, LengthCountMode.CODE_POINT);
        assertTrue(chain.passed(), "trimStart + CODE_POINT，剥离前导空格后 5 个 emoji 算 5 码点");
    }

    // -----------------------------------------------------------------------
    // 7. 错误消息占位符
    // -----------------------------------------------------------------------

    @Test
    public void testErrorMessage_ContainsLength() {
        ValidX chain = ValidX.init().isFixedLength("abc", 8);
        assertFalse(chain.passed());
        List<String> errors = chain.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("8"),
                "错误消息应包含 {length} 替换后的值 8，实际：" + errors.get(0));
    }

    // -----------------------------------------------------------------------
    // 8. 链式调用
    // -----------------------------------------------------------------------

    @Test
    public void testChained_MultipleFixedLengthChecks() {
        ValidX chain = ValidX.init()
                .isFixedLength("123456", 6)
                .isFixedLength("abcdef", 6);
        assertTrue(chain.passed(), "连续两次定长校验都通过");
    }

    @Test
    public void testChained_OnePassOneFail() {
        ValidX chain = ValidX.init()
                .isFixedLength("123456", 6)
                .isFixedLength("short", 10);
        assertFalse(chain.passed());
        assertEquals(1, chain.getErrors().size(), "只有第二次失败");
    }
}