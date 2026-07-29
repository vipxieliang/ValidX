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

import io.github.vipxieliang.validx.annotations.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Timestamp验证器测试类
 */
public class TimestampValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === ANY模式（默认）String类型测试 ===

    @Test
    public void testValidTimestampSeconds_AnyMode() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "1700000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "10位秒级时间戳应该通过ANY模式验证");
    }

    @Test
    public void testValidTimestampMilliseconds_AnyMode() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "1700000000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "13位毫秒级时间戳应该通过ANY模式验证");
    }

    @Test
    public void testValidTimestampMinSeconds_AnyMode() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "0000000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "最小秒级时间戳应该通过ANY模式验证");
    }

    @Test
    public void testValidTimestampMinMilliseconds_AnyMode() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "0000000000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "最小毫秒级时间戳应该通过ANY模式验证");
    }

    @Test
    public void testValidTimestampMaxSeconds_AnyMode() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "9999999999";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "最大秒级时间戳应该通过ANY模式验证");
    }

    @Test
    public void testValidTimestampMaxMilliseconds_AnyMode() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "9999999999999";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "最大毫秒级时间戳应该通过ANY模式验证");
    }

    // === SECONDS模式 String类型测试 ===

    @Test
    public void testValidTimestamp_SecondsMode() {
        TestDTOSeconds dto = new TestDTOSeconds();
        dto.timestamp = "1700000000";

        Set<ConstraintViolation<TestDTOSeconds>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "10位小时级时间戳应该通过SECONDS模式验证");
    }

    @Test
    public void testInvalidMillisecondsInSecondsMode() {
        TestDTOSeconds dto = new TestDTOSeconds();
        dto.timestamp = "1700000000000";

        Set<ConstraintViolation<TestDTOSeconds>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "13位毫秒级时间戳不应该通过SECONDS模式验证");
    }

    @Test
    public void testInvalidWrongLengthInSecondsMode() {
        TestDTOSeconds dto = new TestDTOSeconds();
        dto.timestamp = "170000000";

        Set<ConstraintViolation<TestDTOSeconds>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "9位时间戳不应该通过SECONDS模式验证");
    }

    // === MILLISECONDS模式 String类型测试 ===

    @Test
    public void testValidTimestamp_MillisecondsMode() {
        TestDTOMilliseconds dto = new TestDTOMilliseconds();
        dto.timestamp = "1700000000000";

        Set<ConstraintViolation<TestDTOMilliseconds>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "13位毫秒级时间戳应该通过MILLISECONDS模式验证");
    }

    @Test
    public void testInvalidSecondsInMillisecondsMode() {
        TestDTOMilliseconds dto = new TestDTOMilliseconds();
        dto.timestamp = "1700000000";

        Set<ConstraintViolation<TestDTOMilliseconds>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "10位秒级时间戳不应该通过MILLISECONDS模式验证");
    }

    @Test
    public void testInvalidWrongLengthInMillisecondsMode() {
        TestDTOMilliseconds dto = new TestDTOMilliseconds();
        dto.timestamp = "17000000000";

        Set<ConstraintViolation<TestDTOMilliseconds>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "11位时间戳不应该通过MILLISECONDS模式验证");
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidTimestampWithLetters() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "1700000abc";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "包含字母的时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampWithSpecialChars() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "1700000-000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "包含特殊字符的时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampWithSpaces() {
        TestDTO dto = new TestDTO();
        dto.timestamp = " 1700000000 ";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "包含空格的时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampWithDecimalPoint() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "1700000000.0";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "包含小数点的时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampNegative() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "-1700000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "负数时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampWrongLength_8digits() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "17000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "8位时间戳不应该通过ANY模式验证");
    }

    @Test
    public void testInvalidTimestampWrongLength_12digits() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "170000000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "12位时间戳不应该通过ANY模式验证");
    }

    @Test
    public void testInvalidTimestampWrongLength_14digits() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "17000000000000";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "14位时间戳不应该通过ANY模式验证");
    }

    // === 空值和边界测试 ===

    @Test
    public void testNullTimestamp() {
        TestDTO dto = new TestDTO();
        dto.timestamp = null;

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值由@NotNull注解处理");
    }

    @Test
    public void testEmptyTimestamp() {
        TestDTO dto = new TestDTO();
        dto.timestamp = "";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证（由@NotEmpty处理）");
    }

    // === Long类型测试 ===

    @Test
    public void testValidTimestampLong_AnyMode() {
        TestDTOLong dto = new TestDTOLong();
        dto.timestamp = 1700000000L;

        Set<ConstraintViolation<TestDTOLong>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Long秒级时间戳应该通过ANY模式验证");
    }

    @Test
    public void testValidTimestampLongMilliseconds_AnyMode() {
        TestDTOLong dto = new TestDTOLong();
        dto.timestamp = 1700000000000L;

        Set<ConstraintViolation<TestDTOLong>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Long毫秒级时间戳应该通过ANY模式验证");
    }

    @Test
    public void testValidTimestampLong_SecondsMode() {
        TestDTOLongSeconds dto = new TestDTOLongSeconds();
        dto.timestamp = 1700000000L;

        Set<ConstraintViolation<TestDTOLongSeconds>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Long秒级时间戳应该通过SECONDS模式验证");
    }

    @Test
    public void testInvalidTimestampLongMilliseconds_InSecondsMode() {
        TestDTOLongSeconds dto = new TestDTOLongSeconds();
        dto.timestamp = 1700000000000L;

        Set<ConstraintViolation<TestDTOLongSeconds>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Long毫秒级时间戳不应该通过SECONDS模式验证");
    }

    @Test
    public void testValidTimestampLong_MillisecondsMode() {
        TestDTOLongMilliseconds dto = new TestDTOLongMilliseconds();
        dto.timestamp = 1700000000000L;

        Set<ConstraintViolation<TestDTOLongMilliseconds>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Long毫秒级时间戳应该通过MILLISECONDS模式验证");
    }

    @Test
    public void testInvalidTimestampLongNegative() {
        TestDTOLong dto = new TestDTOLong();
        dto.timestamp = -1L;

        Set<ConstraintViolation<TestDTOLong>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Long负数不应该通过验证");
    }

    @Test
    public void testNullTimestampLong() {
        TestDTOLong dto = new TestDTOLong();
        dto.timestamp = null;

        Set<ConstraintViolation<TestDTOLong>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Long null值由@NotNull注解处理");
    }

    // === Integer类型测试（自动转换为Long处理） ===

    @Test
    public void testValidTimestampInteger_AnyMode() {
        TestDTOInteger dto = new TestDTOInteger();
        dto.timestamp = 1700000000;

        Set<ConstraintViolation<TestDTOInteger>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Integer秒级时间戳应该通过验证");
    }

    @Test
    public void testInvalidTimestampIntegerNegative() {
        TestDTOInteger dto = new TestDTOInteger();
        dto.timestamp = -1;

        Set<ConstraintViolation<TestDTOInteger>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Integer负数不应该通过验证");
    }

    // === 测试DTO ===

    static class TestDTO {
        @Timestamp
        String timestamp;
    }

    static class TestDTOSeconds {
        @Timestamp(unit = Timestamp.TimestampUnit.SECONDS)
        String timestamp;
    }

    static class TestDTOMilliseconds {
        @Timestamp(unit = Timestamp.TimestampUnit.MILLISECONDS)
        String timestamp;
    }

    static class TestDTOLong {
        @Timestamp
        Long timestamp;
    }

    static class TestDTOLongSeconds {
        @Timestamp(unit = Timestamp.TimestampUnit.SECONDS)
        Long timestamp;
    }

    static class TestDTOLongMilliseconds {
        @Timestamp(unit = Timestamp.TimestampUnit.MILLISECONDS)
        Long timestamp;
    }

    static class TestDTOInteger {
        @Timestamp
        Integer timestamp;
    }
}
