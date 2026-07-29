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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Duration;
import io.github.vipxieliang.validx.annotations.Duration.DurationFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Duration验证器测试类
 */
public class DurationValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === ISO 8601格式测试 ===

    @Test
    public void testValidIso8601_Hours() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT2H";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "PT2H应该是有效的ISO 8601格式");
    }

    @Test
    public void testValidIso8601_Minutes() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT30M";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "PT30M应该是有效的ISO 8601格式");
    }

    @Test
    public void testValidIso8601_Seconds() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT45S";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "PT45S应该是有效的ISO 8601格式");
    }

    @Test
    public void testValidIso8601_HoursMinutes() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT2H30M";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "PT2H30M应该是有效的ISO 8601格式");
    }

    @Test
    public void testValidIso8601_Full() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT1H30M15S";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "PT1H30M15S应该是有效的ISO 8601格式");
    }

    @Test
    public void testValidIso8601_WithDays() {
        TestDTO dto = new TestDTO();
        dto.duration = "P1DT2H30M";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "P1DT2H30M应该是有效的ISO 8601格式");
    }

    @Test
    public void testValidIso8601_OnlyDays() {
        TestDTO dto = new TestDTO();
        dto.duration = "P5D";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "P5D应该是有效的ISO 8601格式");
    }

    @Test
    public void testValidIso8601_DecimalSeconds() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT1.5S";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "PT1.5S应该是有效的ISO 8601格式");
    }

    // === 简化格式测试 ===

    @Test
    public void testValidSimple_Hours() {
        TestDTO dto = new TestDTO();
        dto.duration = "2h";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "2h应该是有效的简化格式");
    }

    @Test
    public void testValidSimple_Minutes() {
        TestDTO dto = new TestDTO();
        dto.duration = "30m";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "30m应该是有效的简化格式");
    }

    @Test
    public void testValidSimple_Seconds() {
        TestDTO dto = new TestDTO();
        dto.duration = "45s";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "45s应该是有效的简化格式");
    }

    @Test
    public void testValidSimple_HoursMinutes() {
        TestDTO dto = new TestDTO();
        dto.duration = "2h30m";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "2h30m应该是有效的简化格式");
    }

    @Test
    public void testValidSimple_Full() {
        TestDTO dto = new TestDTO();
        dto.duration = "1h30m15s";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "1h30m15s应该是有效的简化格式");
    }

    @Test
    public void testValidSimple_WithDays() {
        TestDTO dto = new TestDTO();
        dto.duration = "1d12h30m";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "1d12h30m应该是有效的简化格式");
    }

    @Test
    public void testValidSimple_OnlyDays() {
        TestDTO dto = new TestDTO();
        dto.duration = "5d";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "5d应该是有效的简化格式");
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidFormat_NoUnits() {
        TestDTO dto = new TestDTO();
        dto.duration = "123";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "纯数字应该失败");
    }

    @Test
    public void testInvalidFormat_EmptyPT() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "PT应该失败");
    }

    @Test
    public void testInvalidFormat_EmptyP() {
        TestDTO dto = new TestDTO();
        dto.duration = "P";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "P应该失败");
    }

    @Test
    public void testInvalidFormat_WrongOrder() {
        TestDTO dto = new TestDTO();
        dto.duration = "PT30M2H";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "错误顺序应该失败");
    }

    @Test
    public void testInvalidFormat_InvalidCharacters() {
        TestDTO dto = new TestDTO();
        dto.duration = "2h30x";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "无效字符应该失败");
    }

    @Test
    public void testInvalidFormat_ZeroAll() {
        TestDTO dto = new TestDTO();
        dto.duration = "0h0m0s";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "全为0应该失败");
    }

    // === 格式限制测试 ===

    @Test
    public void testIso8601Only_AcceptsIso() {
        IsoOnlyDTO dto = new IsoOnlyDTO();
        dto.duration = "PT2H30M";

        Set<ConstraintViolation<IsoOnlyDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "ISO_8601模式应该接受ISO格式");
    }

    @Test
    public void testIso8601Only_RejectsSimple() {
        IsoOnlyDTO dto = new IsoOnlyDTO();
        dto.duration = "2h30m";

        Set<ConstraintViolation<IsoOnlyDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "ISO_8601模式应该拒绝简化格式");
    }

    @Test
    public void testSimpleOnly_AcceptsSimple() {
        SimpleOnlyDTO dto = new SimpleOnlyDTO();
        dto.duration = "2h30m";

        Set<ConstraintViolation<SimpleOnlyDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "SIMPLE模式应该接受简化格式");
    }

    @Test
    public void testSimpleOnly_RejectsIso() {
        SimpleOnlyDTO dto = new SimpleOnlyDTO();
        dto.duration = "PT2H30M";

        Set<ConstraintViolation<SimpleOnlyDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "SIMPLE模式应该拒绝ISO格式");
    }

    // === Null和空值测试 ===

    @Test
    public void testValidDuration_Null() {
        TestDTO dto = new TestDTO();
        dto.duration = null;

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证");
    }

    @Test
    public void testValidDuration_Empty() {
        TestDTO dto = new TestDTO();
        dto.duration = "";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证");
    }

    // === 静态方法测试 ===

    @Test
    public void testStatic_ValidIso() {
        assertTrue(DurationValidator.isValid("PT2H30M"), "静态方法应该接受有效的ISO格式");
    }

    @Test
    public void testStatic_ValidSimple() {
        assertTrue(DurationValidator.isValid("2h30m"), "静态方法应该接受有效的简化格式");
    }

    @Test
    public void testStatic_Invalid() {
        assertFalse(DurationValidator.isValid("invalid"), "静态方法应该拒绝无效格式");
    }

    @Test
    public void testStatic_WithFormat_Iso() {
        assertTrue(DurationValidator.isValid("PT2H30M", DurationFormat.ISO_8601),
            "静态方法应该在ISO_8601模式下接受ISO格式");
    }

    @Test
    public void testStatic_WithFormat_Simple() {
        assertTrue(DurationValidator.isValid("2h30m", DurationFormat.SIMPLE),
            "静态方法应该在SIMPLE模式下接受简化格式");
    }

    @Test
    public void testStatic_Null() {
        assertTrue(DurationValidator.isValid(null), "静态方法对null应该返回true");
    }

    // === 测试DTO ===

    static class TestDTO {
        @Duration
        public String duration;
    }

    static class IsoOnlyDTO {
        @Duration(format = DurationFormat.ISO_8601)
        public String duration;
    }

    static class SimpleOnlyDTO {
        @Duration(format = DurationFormat.SIMPLE)
        public String duration;
    }
}
