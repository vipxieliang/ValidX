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

import io.github.vipxieliang.validx.annotations.Ascii;
import io.github.vipxieliang.validx.annotations.Printable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AsciiValidator 注解方式测试类。
 *
 * <p>通过 Bean Validation 的 {@link Validator} 对标注了 {@code @Ascii} 的实体进行校验。
 * {@code @Ascii} 采用教科书定义：<b>完整 ASCII 0x00~0x7F</b>（含 33 个控制字符），
 * 与 {@code @Printable}（0x20~0x7E，要求完全可见）职责互补。</p>
 */
public class AsciiValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== 合法 ASCII：可打印部分（0x20~0x7E） ====================

    @Test
    public void testValid_PlainAscii() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "Hello, World!";
        assertTrue(validator.validate(dto).isEmpty(), "普通 ASCII 应通过验证");
    }

    @Test
    public void testValid_Alphanumeric() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "abc123XYZ";
        assertTrue(validator.validate(dto).isEmpty(), "字母数字组合应通过验证");
    }

    @Test
    public void testValid_Punctuation() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "!#$%&()*+,-./:;<=>?@[]^_`{|}~";
        assertTrue(validator.validate(dto).isEmpty(), "ASCII 标点应通过验证");
    }

    @Test
    public void testValid_Space() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = " "; // 0x20 空格
        assertTrue(validator.validate(dto).isEmpty(), "空格 0x20 应通过验证");
    }

    // ==================== 合法 ASCII：控制字符（0x00~0x1F、0x7F）也应放行 ====================

    @Test
    public void testValid_Newline() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "line1\nline2"; // LF 0x0A
        assertTrue(validator.validate(dto).isEmpty(), "@Ascii 应放行换行符 0x0A");
    }

    @Test
    public void testValid_Tab() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "abc\tdef"; // TAB 0x09
        assertTrue(validator.validate(dto).isEmpty(), "@Ascii 应放行制表符 0x09");
    }

    @Test
    public void testValid_CarriageReturn() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "abc\rdef"; // CR 0x0D
        assertTrue(validator.validate(dto).isEmpty(), "@Ascii 应放行回车 0x0D");
    }

    @Test
    public void testValid_Nul() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "abc" + (char) 0x00; // NUL 0x00
        assertTrue(validator.validate(dto).isEmpty(), "@Ascii 应放行 NUL 0x00");
    }

    @Test
    public void testValid_Del() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "abc" + (char) 0x7F; // DEL 0x7F
        assertTrue(validator.validate(dto).isEmpty(), "@Ascii 应放行 DEL 0x7F");
    }

    @Test
    public void testValid_MultiLineText() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "line1\n\tline2\r\nline3"; // 多行文本，允许换行与制表符
        assertTrue(validator.validate(dto).isEmpty(), "多行 ASCII 文本应通过验证");
    }

    @Test
    public void testValid_LowerBoundary() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = String.valueOf((char) 0x00); // 下边界
        assertTrue(validator.validate(dto).isEmpty(), "0x00 下边界应通过");
    }

    @Test
    public void testValid_UpperBoundary() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = String.valueOf((char) 0x7F); // 上边界
        assertTrue(validator.validate(dto).isEmpty(), "0x7F 上边界应通过");
    }

    @Test
    public void testValid_FullAsciiRange() {
        AsciiDTO dto = new AsciiDTO();
        // 0x00 ~ 0x7F 全部 128 个 ASCII 字符都应通过
        StringBuilder sb = new StringBuilder();
        for (int i = 0x00; i <= 0x7F; i++) {
            sb.append((char) i);
        }
        dto.value = sb.toString();
        assertTrue(validator.validate(dto).isEmpty(), "0x00~0x7F 全部字符应通过验证");
    }

    // ==================== 非法：非 ASCII（≥0x80）必须被拒绝 ====================

    @Test
    public void testInvalid_Chinese() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "你好";
        Set<ConstraintViolation<AsciiDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "含中文应验证失败");
        assertFalse(violations.iterator().next().getMessage().startsWith("{"),
                "错误消息应已被解析为具体文案");
    }

    @Test
    public void testInvalid_Japanese() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "こんにちは";
        assertEquals(1, validator.validate(dto).size(), "含日文应验证失败");
    }

    @Test
    public void testInvalid_Emoji() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "Hello 😀";
        assertEquals(1, validator.validate(dto).size(), "含 Emoji 应验证失败");
    }

    @Test
    public void testInvalid_FirstNonAsciiByte() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = String.valueOf((char) 0x80); // 0x80 第一个非 ASCII
        assertEquals(1, validator.validate(dto).size(), "应拒绝 0x80");
    }

    @Test
    public void testInvalid_MaxBoundary() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = String.valueOf((char) 0xFF);
        assertEquals(1, validator.validate(dto).size(), "应拒绝 0xFF");
    }

    @Test
    public void testInvalid_FullWidthDigit() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = String.valueOf((char) 0xFF11); // 全角数字 1（U+FF11）
        assertEquals(1, validator.validate(dto).size(), "全角数字应被拒绝");
    }

    @Test
    public void testInvalid_MixedAsciiAndChinese() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "abc中文def";
        assertEquals(1, validator.validate(dto).size(), "混入中文即应验证失败");
    }

    // ==================== 与 @Printable 的分工对比 ====================

    @Test
    public void testAsciiAllowsNewlineButPrintableRejects() {
        // @Ascii：允许换行（0x0A 属于 ASCII）
        AsciiDTO asciiDto = new AsciiDTO();
        asciiDto.value = "line1\nline2";
        assertTrue(validator.validate(asciiDto).isEmpty(), "@Ascii 应放行换行符");

        // @Printable：拒绝换行（要求字符完全可见）
        PrintableDTO printableDto = new PrintableDTO();
        printableDto.value = "line1\nline2";
        assertEquals(1, validator.validate(printableDto).size(), "@Printable 应拒绝换行符");
    }

    @Test
    public void testAsciiAndPrintableBothRejectNonAscii() {
        AsciiDTO asciiDto = new AsciiDTO();
        asciiDto.value = "你好";
        assertEquals(1, validator.validate(asciiDto).size(), "@Ascii 应拒绝中文");

        PrintableDTO printableDto = new PrintableDTO();
        printableDto.value = "你好";
        assertEquals(1, validator.validate(printableDto).size(), "@Printable 应拒绝中文");
    }

    // ==================== null / 空字符串 ====================

    @Test
    public void testNull_PassesValidation() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = null;
        assertTrue(validator.validate(dto).isEmpty(), "null 值应通过验证");
    }

    @Test
    public void testEmpty_PassesValidation() {
        AsciiDTO dto = new AsciiDTO();
        dto.value = "";
        assertTrue(validator.validate(dto).isEmpty(), "空字符串应通过验证");
    }

    // ==================== 测试实体 ====================

    static class AsciiDTO {
        @Ascii
        String value;
    }

    static class PrintableDTO {
        @Printable
        String value;
    }
}