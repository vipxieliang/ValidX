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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.FixedLength;
import io.github.vipxieliang.validx.enums.LengthCountMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FixedLengthValidator 注解方式测试类。
 *
 * <p>通过 Bean Validation 的 {@link Validator} 对标注了 {@code @FixedLength} 的实体进行校验，
 * 覆盖：null / 空串放行、定长基本正反、trimStart / trimEnd 独立行为、
 * CHAR / CODE_POINT / GRAPHEME 三档计数口径、前导零保留、错误消息占位符。</p>
 */
class FixedLengthValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // -----------------------------------------------------------------------
    // 1. null / 空串放行（交由 @NotBlank / @NotNull 处理）
    // -----------------------------------------------------------------------

    @Test
    void testValid_NullString() {
        DefaultDto dto = new DefaultDto();
        Set<ConstraintViolation<DefaultDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null 应放行");
    }

    @Test
    void testValid_EmptyString() {
        DefaultDto dto = new DefaultDto();
        dto.value = "";
        Set<ConstraintViolation<DefaultDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空串应放行");
    }

    // -----------------------------------------------------------------------
    // 2. 精确等长（默认 CHAR 口径，trimStart/trimEnd 均关闭）
    // -----------------------------------------------------------------------

    @Test
    void testValid_ExactLength() {
        DefaultDto dto = new DefaultDto();
        dto.value = "12345678";
        assertTrue(validator.validate(dto).isEmpty(), "恰好 8 位应通过");
    }

    @Test
    void testInvalid_TooShort() {
        DefaultDto dto = new DefaultDto();
        dto.value = "1234567";
        assertEquals(1, validator.validate(dto).size(), "7 位应判定 ≠ 8");
    }

    @Test
    void testInvalid_TooLong() {
        DefaultDto dto = new DefaultDto();
        dto.value = "123456789";
        assertEquals(1, validator.validate(dto).size(), "9 位应判定 ≠ 8");
    }

    // -----------------------------------------------------------------------
    // 3. 前导零：默认不改输入
    // -----------------------------------------------------------------------

    @Test
    void testValid_LeadingZeros_DefaultNoTrim() {
        LeadingZeroDto dto = new LeadingZeroDto();
        dto.code = "00012345";
        assertTrue(validator.validate(dto).isEmpty(), "前导零默认不被 trim，应通过 length=8");
    }

    // -----------------------------------------------------------------------
    // 4. trimStart / trimEnd 开关
    // -----------------------------------------------------------------------

    @Test
    void testInvalid_DefaultNoTrim_LeadingSpaceIncreasesLength() {
        DefaultDto dto = new DefaultDto();
        dto.value = "  1234567"; // 9 字符（2 前导空格 + 7）
        assertEquals(1, validator.validate(dto).size(),
                "默认 trimStart=false，前导空格计入长度，应判定 9 ≠ 8");
    }

    @Test
    void testInvalid_DefaultNoTrim_TrailingSpaceIncreasesLength() {
        DefaultDto dto = new DefaultDto();
        dto.value = "1234567  "; // 9 字符（7 + 2 尾随空格）
        assertEquals(1, validator.validate(dto).size(),
                "默认 trimEnd=false，尾随空格计入长度，应判定 9 ≠ 8");
    }

    @Test
    void testValid_TrimEnd_StripsTrailingSpace() {
        TrimEndDto dto = new TrimEndDto();
        dto.code = "1234567 "; // trim 后 = "1234567" → 7 位
        assertTrue(validator.validate(dto).isEmpty(),
                "trimEnd=true，尾随空格被剥离，应通过 length=7");
    }

    @Test
    void testInvalid_TrimEnd_LeadingSpaceStillCounts() {
        TrimEndDto dto = new TrimEndDto();
        dto.code = " 1234567"; // trimEnd 只剥尾，前导空格保留 → 8 字符
        assertEquals(1, validator.validate(dto).size(),
                "trimEnd=true 不影响前导空格，应判定 8 ≠ 7");
    }

    @Test
    void testValid_TrimStart_StripsLeadingSpace() {
        TrimStartDto dto = new TrimStartDto();
        dto.code = " 1234567"; // trimStart 后 = "1234567" → 7 位
        assertTrue(validator.validate(dto).isEmpty(),
                "trimStart=true，前导空格被剥离，应通过 length=7");
    }

    @Test
    void testValid_TrimBoth_StripsBoth() {
        TrimBothDto dto = new TrimBothDto();
        dto.code = "  123456  "; // 10 字符，trim 后 = "123456" → 6 位
        assertTrue(validator.validate(dto).isEmpty(),
                "trimStart+trimEnd 双开，前后空格都剥离，应通过 length=6");
    }

    // -----------------------------------------------------------------------
    // 5. CHAR / CODE_POINT / GRAPHEME 三档计数
    // -----------------------------------------------------------------------

    @Test
    void testValid_CodePoint_AsciiSameAsChar() {
        CodePointDto dto = new CodePointDto();
        dto.code = "12345";
        assertTrue(validator.validate(dto).isEmpty(),
                "CODE_POINT 模式下 5 个 ASCII 字符算 5");
    }

    @Test
    void testValid_CodePoint_EmojiCountsPerCodePoint() {
        CodePointDto dto = new CodePointDto();
        dto.code = "😀😀😀😀😀"; // String.length()=10（surrogate pair），codePointCount=5
        assertTrue(validator.validate(dto).isEmpty(),
                "CODE_POINT 模式下 5 个 emoji 应算 5 个码点");
    }

    @Test
    void testInvalid_Char_EmojiOverCounts() {
        CharDto dto = new CharDto();
        dto.code = "😀😀😀😀😀";
        assertEquals(1, validator.validate(dto).size(),
                "CHAR 模式下 5 个 emoji 算 10 位，应失败");
    }

    @Test
    void testValid_Grapheme_EmojiCountsPerGrapheme() {
        GraphemeDto dto = new GraphemeDto();
        dto.code = "😀😀😀😀😀";
        assertTrue(validator.validate(dto).isEmpty(),
                "GRAPHEME 模式下 5 个 emoji 应算 5 个字素");
    }

    @Test
    void testValid_Grapheme_AsciiSameAsChar() {
        GraphemeDto dto = new GraphemeDto();
        dto.code = "12345";
        assertTrue(validator.validate(dto).isEmpty(),
                "GRAPHEME 模式下 ASCII 字符串与 CHAR 一致");
    }

    // -----------------------------------------------------------------------
    // 6. 错误消息占位符
    // -----------------------------------------------------------------------

    @Test
    void testErrorMessage_ContainsLength() {
        DefaultDto dto = new DefaultDto();
        dto.value = "abc"; // 3 位，期望 8 位
        Set<ConstraintViolation<DefaultDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        String msg = violations.iterator().next().getMessage();
        assertTrue(msg.contains("8"),
                "错误消息应包含 {length} 替换后的值 8，实际：" + msg);
    }

    // =======================================================================
    // DTO 定义
    // =======================================================================

    /** 默认配置：{@code @FixedLength(length = 8)}，CHAR 口径，trimStart/trimEnd 均关闭 */
    static class DefaultDto {
        @FixedLength(length = 8)
        String value;
    }

    /** 前导零场景：{@code "00012345"} 默认不应被 trim */
    static class LeadingZeroDto {
        @FixedLength(length = 8)
        String code;
    }

    /** 仅 trimEnd 开启 */
    static class TrimEndDto {
        @FixedLength(length = 7, trimEnd = true)
        String code;
    }

    /** 仅 trimStart 开启 */
    static class TrimStartDto {
        @FixedLength(length = 7, trimStart = true)
        String code;
    }

    /** trimStart + trimEnd 双开 */
    static class TrimBothDto {
        @FixedLength(length = 6, trimStart = true, trimEnd = true)
        String code;
    }

    /** CODE_POINT 口径 */
    static class CodePointDto {
        @FixedLength(length = 5, countMode = LengthCountMode.CODE_POINT)
        String code;
    }

    /** CHAR 口径（显式指定，便于测试 emoji 边界） */
    static class CharDto {
        @FixedLength(length = 5, countMode = LengthCountMode.CHAR)
        String code;
    }

    /** GRAPHEME 口径 */
    static class GraphemeDto {
        @FixedLength(length = 5, countMode = LengthCountMode.GRAPHEME)
        String code;
    }
}