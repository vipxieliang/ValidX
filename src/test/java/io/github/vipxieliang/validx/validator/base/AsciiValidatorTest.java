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
 * <p>通过 Bean Validation 的 {@link Validator} 对标注了 {@code @Ascii} 的实体进行校验，
 * 覆盖默认（仅可打印 ASCII 0x20~0x7E）与 {@code allowControlChar = true}（0x00~0x7F）两种模式。</p>
 */
public class AsciiValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== 默认模式：可打印 ASCII（0x20~0x7E） ====================

    @Test
    public void testValid_PrintableAscii() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "Hello, World!";
        assertTrue(validator.validate(dto).isEmpty(), "普通可打印 ASCII 应通过验证");
    }

    @Test
    public void testValid_PrintableAscii_Alphanumeric() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "abc123XYZ";
        assertTrue(validator.validate(dto).isEmpty(), "字母数字组合应通过验证");
    }

    @Test
    public void testValid_PrintableAscii_Punctuation() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "!#$%&()*+,-./:;<=>?@[]^_`{|}~";
        assertTrue(validator.validate(dto).isEmpty(), "可打印标点应通过验证");
    }

    @Test
    public void testValid_PrintableAscii_Space() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = " "; // 0x20 空格
        assertTrue(validator.validate(dto).isEmpty(), "空格 0x20 应通过验证");
    }

    @Test
    public void testValid_PrintableAscii_Boundary() {
        TestDTOPrintable dto = new TestDTOPrintable();
        // 0x20 ~ 0x7E 全部可打印字符都应通过
        StringBuilder sb = new StringBuilder();
        for (int i = 0x20; i <= 0x7E; i++) {
            sb.append((char) i);
        }
        dto.value = sb.toString();
        assertTrue(validator.validate(dto).isEmpty(), "0x20~0x7E 全部字符应通过验证");
    }

    @Test
    public void testInvalid_Chinese() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "你好";
        Set<ConstraintViolation<TestDTOPrintable>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "含中文应验证失败");
        assertFalse(violations.iterator().next().getMessage().startsWith("{"),
                "错误消息应已被解析为具体文案");
    }

    @Test
    public void testInvalid_Japanese() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "こんにちは";
        assertEquals(1, validator.validate(dto).size(), "含日文应验证失败");
    }

    @Test
    public void testInvalid_Emoji() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "Hello 😀";
        assertEquals(1, validator.validate(dto).size(), "含 Emoji 应验证失败");
    }

    @Test
    public void testInvalid_Tab() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "abc\t"; // TAB 0x09
        assertEquals(1, validator.validate(dto).size(), "默认应拒绝 TAB");
    }

    @Test
    public void testInvalid_LineFeed() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "abc\n"; // LF 0x0A
        assertEquals(1, validator.validate(dto).size(), "默认应拒绝换行");
    }

    @Test
    public void testInvalid_CarriageReturn() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "abc\r"; // CR 0x0D
        assertEquals(1, validator.validate(dto).size(), "默认应拒绝回车");
    }

    @Test
    public void testInvalid_Del() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "abc" + (char) 0x7F; // DEL 0x7F
        assertEquals(1, validator.validate(dto).size(), "默认应拒绝 DEL 0x7F");
    }

    @Test
    public void testInvalid_Nul() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "abc" + (char) 0x00; // NUL 0x00
        assertEquals(1, validator.validate(dto).size(), "默认应拒绝 NUL 0x00");
    }

    // ==================== 允许控制字符：完整 ASCII（0x00~0x7F） ====================

    @Test
    public void testValid_ControlChar_Tab_WhenAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        dto.value = "abc\tdef";
        assertTrue(validator.validate(dto).isEmpty(), "allowControlChar=true 时 TAB 应通过");
    }

    @Test
    public void testValid_ControlChar_LineFeed_WhenAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        dto.value = "line1\nline2";
        assertTrue(validator.validate(dto).isEmpty(), "allowControlChar=true 时换行应通过");
    }

    @Test
    public void testValid_ControlChar_Del_WhenAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        dto.value = String.valueOf((char) 0x7F); // DEL 0x7F
        assertTrue(validator.validate(dto).isEmpty(), "allowControlChar=true 时 DEL 应通过");
    }

    @Test
    public void testValid_ControlChar_Nul_WhenAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        dto.value = String.valueOf((char) 0x00); // NUL 0x00
        assertTrue(validator.validate(dto).isEmpty(), "allowControlChar=true 时 NUL 应通过");
    }

    @Test
    public void testValid_ControlChar_FullAsciiRange_WhenAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        // 0x00 ~ 0x7F 全部字符都应通过
        StringBuilder sb = new StringBuilder();
        for (int i = 0x00; i <= 0x7F; i++) {
            sb.append((char) i);
        }
        dto.value = sb.toString();
        assertTrue(validator.validate(dto).isEmpty(), "allowControlChar=true 时 0x00~0x7F 应全部通过");
    }

    @Test
    public void testInvalid_Chinese_WhenControlAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        dto.value = "abc中文";
        assertEquals(1, validator.validate(dto).size(), "即使允许控制字符，中文仍应被拒绝");
    }

    @Test
    public void testInvalid_HighBoundary_WhenControlAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        dto.value = String.valueOf((char) 0x80); // 0x80 已超出 ASCII
        assertEquals(1, validator.validate(dto).size(), "0x80 应被拒绝");
    }

    @Test
    public void testInvalid_MaxBoundary_WhenControlAllowed() {
        TestDTOAllowControl dto = new TestDTOAllowControl();
        dto.value = String.valueOf((char) 0xFF); // 0xFF 已超出 ASCII
        assertEquals(1, validator.validate(dto).size(), "0xFF 应被拒绝");
    }

    // ==================== null / 空字符串 ====================

    @Test
    public void testNull_PassesValidation() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = null;
        assertTrue(validator.validate(dto).isEmpty(), "null 值应通过验证");
    }

    @Test
    public void testEmpty_PassesValidation() {
        TestDTOPrintable dto = new TestDTOPrintable();
        dto.value = "";
        assertTrue(validator.validate(dto).isEmpty(), "空字符串应通过验证");
    }

    // ==================== 测试实体 ====================

    static class TestDTOPrintable {
        @Ascii
        String value;
    }

    static class TestDTOAllowControl {
        @Ascii(allowControlChar = true)
        String value;
    }
}
