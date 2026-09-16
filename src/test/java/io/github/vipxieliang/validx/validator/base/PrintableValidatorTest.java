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
 * PrintableValidator 注解方式测试类。
 *
 * <p>通过 Bean Validation 的 {@link Validator} 对标注了 {@code @Printable} 的实体进行校验，
 * 覆盖可打印 ASCII（0x20~0x7E）与全部 33 个 ASCII 控制字符的拒绝行为。</p>
 */
public class PrintableValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== 合法可打印 ASCII（0x20~0x7E） ====================

    @Test
    public void testValid_PlainAscii() {
        TestDTO dto = new TestDTO();
        dto.value = "Hello, World!";
        assertTrue(validator.validate(dto).isEmpty(), "普通可打印 ASCII 应通过验证");
    }

    @Test
    public void testValid_Alphanumeric() {
        TestDTO dto = new TestDTO();
        dto.value = "abc123XYZ";
        assertTrue(validator.validate(dto).isEmpty(), "字母数字组合应通过验证");
    }

    @Test
    public void testValid_Punctuation() {
        TestDTO dto = new TestDTO();
        dto.value = "!#$%&()*+,-./,;:<=>?@[]^_`{|}~";
        assertTrue(validator.validate(dto).isEmpty(), "可打印标点应通过验证");
    }

    @Test
    public void testValid_Space() {
        TestDTO dto = new TestDTO();
        dto.value = " "; // 0x20 空格
        assertTrue(validator.validate(dto).isEmpty(), "空格 0x20 应通过验证");
    }

    @Test
    public void testValid_LeadingAndTrailingSpace() {
        TestDTO dto = new TestDTO();
        dto.value =  "  abc  "; // 前后空格
        assertTrue(validator.validate(dto).isEmpty(), "首尾空格应通过验证");
    }

    @Test
    public void testValid_LowerBoundary() {
        TestDTO dto = new TestDTO();
        dto.value = " "; // 0x20
        assertTrue(validator.validate(dto).isEmpty(), "0x20（空格）边界应通过");
    }

    @Test
    public void testValid_UpperBoundary() {
        TestDTO dto = new TestDTO();
        dto.value = String.valueOf((char) 0x7E); // '~'
        assertTrue(validator.validate(dto).isEmpty(), "0x7E（~）上边界应通过");
    }

    @Test
    public void testValid_FullPrintableRange() {
        TestDTO dto = new TestDTO();
        // 0x20~0x7E 全部可打印字符应通过
        StringBuilder sb = new StringBuilder();
        for (int i = 0x20; i <= 0x7E; i++) {
            sb.append((char) i);
        }
        dto.value = sb.toString();
        assertTrue(validator.validate(dto).isEmpty(), "0x20~0x7E 全部字符应通过验证");
    }

    // ==================== 控制字符（0x00~0x1F、0x7F）必须被拒绝 ====================

    @Test
    public void testInvalid_Nul() {
        TestDTO dto = new TestDTO();
        dto.value = "abc" + (char) 0x00; // NUL
        assertEquals(1, validator.validate(dto).size(), "默认应拒绝 NUL 0x00");
    }

    @Test
    public void testInvalid_StartOfHeading() {
        TestDTO dto = new TestDTO();
        dto.value = "abc" + (char) 0x01;
        assertEquals(1, validator.validate(dto).size(), "应拒绝 0x01 控制字符");
    }

    @Test
    public void testInvalid_Tab() {
        TestDTO dto = new TestDTO();
        dto.value = "abc\tdef"; // TAB 0x09
        assertEquals(1, validator.validate(dto).size(), "应拒绝 TAB");
    }

    @Test
    public void testInvalid_LineFeed() {
        TestDTO dto = new TestDTO();
        dto.value = "abc\ndef"; // LF 0x0A
        assertEquals(1, validator.validate(dto).size(), "应拒绝 LF");
    }

    @Test
    public void testInvalid_CarriageReturn() {
        TestDTO dto = new TestDTO();
        dto.value = "abc\rdef"; // CR 0x0D
        assertEquals(1, validator.validate(dto).size(), "应拒绝 CR");
    }

    @Test
    public void testInvalid_LastControlChar() {
        TestDTO dto = new TestDTO();
        dto.value = "abc" + (char) 0x1F; // US（Unit Separator）
        assertEquals(1, validator.validate(dto).size(), "应拒绝 0x1F（最后一个 C0 控制字符）");
    }

    @Test
    public void testInvalid_Del() {
        TestDTO dto = new TestDTO();
        dto.value = "abc" + (char) 0x7F; // DEL
        assertEquals(1, validator.validate(dto).size(), "应拒绝 DEL 0x7F");
    }

    @Test
    public void testInvalid_AllControlCharsAtOnce() {
        TestDTO dto = new TestDTO();
        // 0x00~0x1F、0x7F 全部 33 个控制字符拼在一起，必须被拒
        StringBuilder sb = new StringBuilder("abc");
        for (int i = 0x00; i <= 0x1F; i++) {
            sb.append((char) i);
        }
        sb.append((char) 0x7F);
        dto.value = sb.toString();
        assertEquals(1, validator.validate(dto).size(), "含任何控制字符即应被拒");
    }

    // ==================== 非 ASCII 字符（≥0x80）必须被拒绝 ====================

    @Test
    public void testInvalid_Chinese() {
        TestDTO dto = new TestDTO();
        dto.value = "你好";
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "含中文应验证失败");
        assertFalse(violations.iterator().next().getMessage().startsWith("{"),
                "错误消息应已被解析为具体文案");
    }

    @Test
    public void testInvalid_Japanese() {
        TestDTO dto = new TestDTO();
        dto.value = "こんにちは";
        assertEquals(1, validator.validate(dto).size(), "含日文应验证失败");
    }

    @Test
    public void testInvalid_Emoji() {
        TestDTO dto = new TestDTO();
        dto.value = "Hi 😀";
        assertEquals(1, validator.validate(dto).size(), "含 Emoji 应验证失败");
    }

    @Test
    public void testInvalid_FirstNonAsciiByte() {
        TestDTO dto = new TestDTO();
        dto.value = String.valueOf((char) 0x80); // 0x80 第一个非 ASCII
        assertEquals(1, validator.validate(dto).size(), "应拒绝 0x80");
    }

    @Test
    public void testInvalid_FullWidthDigit() {
        TestDTO dto = new TestDTO();
        dto.value = String.valueOf((char) 0xFF11); // 全角数字 1（U+FF11）
        assertEquals(1, validator.validate(dto).size(), "全角数字应被拒绝");
    }

    @Test
    public void testInvalid_FullByteMax() {
        TestDTO dto = new TestDTO();
        dto.value = String.valueOf((char) 0xFF);
        assertEquals(1, validator.validate(dto).size(), "应拒绝 0xFF");
    }

    // ==================== null / 空字符串 ====================

    @Test
    public void testNull_PassesValidation() {
        TestDTO dto = new TestDTO();
        dto.value = null;
        assertTrue(validator.validate(dto).isEmpty(), "null 值应通过验证");
    }

    @Test
    public void testEmpty_PassesValidation() {
        TestDTO dto = new TestDTO();
        dto.value = "";
        assertTrue(validator.validate(dto).isEmpty(), "空字符串应通过验证");
    }

    // ==================== 测试实体 ====================

    static class TestDTO {
        @Printable
        String value;
    }
}