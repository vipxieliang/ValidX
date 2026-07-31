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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.annotations.Timestamp;
import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Timestamp链式验证测试类
 */
public class TimestampValidationChainTest {

    // === ANY模式（默认）测试 ===

    @Test
    public void testValidTimestampSeconds_DefaultMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000");

        assertTrue(validator.passed(), "10位秒级时间戳应该通过默认验证");
    }

    @Test
    public void testValidTimestampMilliseconds_DefaultMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000000");

        assertTrue(validator.passed(), "13位毫秒级时间戳应该通过默认验证");
    }

    @Test
    public void testValidTimestampBoundaryMaxSeconds() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("9999999999");

        assertTrue(validator.passed(), "最大10位秒级时间戳应该通过验证");
    }

    @Test
    public void testValidTimestampBoundaryMaxMilliseconds() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("9999999999999");

        assertTrue(validator.passed(), "最大13位毫秒级时间戳应该通过验证");
    }

    // === SECONDS模式测试 ===

    @Test
    public void testValidTimestampSeconds_SpecificMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000", Timestamp.TimestampUnit.SECONDS);

        assertTrue(validator.passed(), "10位秒级时间戳应该通过SECONDS模式验证");
    }

    @Test
    public void testInvalidMillisecondsInSecondsMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000000", Timestamp.TimestampUnit.SECONDS);

        assertFalse(validator.passed(), "13位时间戳不应该通过SECONDS模式验证");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidWrongLengthInSecondsMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("170000000", Timestamp.TimestampUnit.SECONDS);

        assertFalse(validator.passed(), "9位时间戳不应该通过SECONDS模式验证");
    }

    // === MILLISECONDS模式测试 ===

    @Test
    public void testValidTimestampMilliseconds_SpecificMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000000", Timestamp.TimestampUnit.MILLISECONDS);

        assertTrue(validator.passed(), "13位毫秒级时间戳应该通过MILLISECONDS模式验证");
    }

    @Test
    public void testInvalidSecondsInMillisecondsMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000", Timestamp.TimestampUnit.MILLISECONDS);

        assertFalse(validator.passed(), "10位时间戳不应该通过MILLISECONDS模式验证");
        assertEquals(1, validator.getErrors().size());
    }

    // === ANY模式显式指定测试 ===

    @Test
    public void testValidTimestampSeconds_ExplicitAnyMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000", Timestamp.TimestampUnit.ANY);

        assertTrue(validator.passed(), "10位时间戳应该通过显式ANY模式验证");
    }

    @Test
    public void testValidTimestampMilliseconds_ExplicitAnyMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000000", Timestamp.TimestampUnit.ANY);

        assertTrue(validator.passed(), "13位时间戳应该通过显式ANY模式验证");
    }

    // === Long类型测试 ===

    @Test
    public void testValidTimestampLongSeconds() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(1700000000L);

        assertTrue(validator.passed(), "Long秒级时间戳应该通过验证");
    }

    @Test
    public void testValidTimestampLongMilliseconds() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(1700000000000L);

        assertTrue(validator.passed(), "Long毫秒级时间戳应该通过验证");
    }

    @Test
    public void testValidTimestampLongSeconds_SpecificMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(1700000000L, Timestamp.TimestampUnit.SECONDS);

        assertTrue(validator.passed(), "Long秒级时间戳应该通过SECONDS模式验证");
    }

    @Test
    public void testValidTimestampLongMilliseconds_SpecificMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(1700000000000L, Timestamp.TimestampUnit.MILLISECONDS);

        assertTrue(validator.passed(), "Long毫秒级时间戳应该通过MILLISECONDS模式验证");
    }

    @Test
    public void testInvalidTimestampLongNegative() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(-1L);

        assertFalse(validator.passed(), "Long负数不应该通过验证");
    }

    @Test
    public void testInvalidTimestampLongMilliseconds_InSecondsMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(1700000000000L, Timestamp.TimestampUnit.SECONDS);

        assertFalse(validator.passed(), "Long毫秒级不应该通过SECONDS模式");
    }

    @Test
    public void testInvalidTimestampLongSeconds_InMillisecondsMode() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(1700000000L, Timestamp.TimestampUnit.MILLISECONDS);

        assertFalse(validator.passed(), "Long秒级不应该通过MILLISECONDS模式");
    }

    // === Integer类型测试 ===

    @Test
    public void testValidTimestampInteger() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(1700000000);

        assertTrue(validator.passed(), "Integer秒级时间戳应该通过验证");
    }

    @Test
    public void testInvalidTimestampIntegerNegative() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(-1);

        assertFalse(validator.passed(), "Integer负数不应该通过验证");
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidTimestampWithLetters() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000abc");

        assertFalse(validator.passed(), "包含字母的时间戳不应该通过验证");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidTimestampWithSpecialChars() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000-000");

        assertFalse(validator.passed(), "包含特殊字符的时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampWithSpaces() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(" 1700000000 ");

        assertFalse(validator.passed(), "包含空格的时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampWithDecimalPoint() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000.0");

        assertFalse(validator.passed(), "包含小数点的时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampNegative() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("-1700000000");

        assertFalse(validator.passed(), "负数时间戳不应该通过验证");
    }

    @Test
    public void testInvalidTimestampWrongLength() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("123456789");

        assertFalse(validator.passed(), "9位数字不应该通过验证");
    }

    @Test
    public void testInvalidTimestamp12Digits() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("123456789012");

        assertFalse(validator.passed(), "12位数字不应该通过验证");
    }

    @Test
    public void testInvalidTimestamp14Digits() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("12345678901234");

        assertFalse(validator.passed(), "14位数字不应该通过验证");
    }

    // === 空值测试 ===

    @Test
    public void testNullTimestamp() {
        ValidX validator = ValidX.init();
        validator.isTimestamp((Object) null);

        assertTrue(validator.passed(), "null值应该通过链式验证（由@NotNull处理）");
    }

    @Test
    public void testNullTimestampWithUnit() {
        ValidX validator = ValidX.init();
        validator.isTimestamp(null, Timestamp.TimestampUnit.SECONDS);

        assertTrue(validator.passed(), "null值带unit参数应该通过验证");
    }

    @Test
    public void testEmptyTimestamp() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("");

        assertTrue(validator.passed(), "空字符串应该通过验证（由@NotEmpty处理）");
    }

    // === 链式调用测试 ===

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000")
                .isTimestamp("1700000000000")
                .isTimestamp("9999999999");

        assertTrue(validator.passed(), "多个有效时间戳应该全部通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000")
                .isTimestamp("invalid")
                .isTimestamp("1700000000000");

        assertFalse(validator.passed(), "一个无效时间戳应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_AllFailing() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("123456789")
                .isTimestamp("abc")
                .isTimestamp("-1");

        assertFalse(validator.passed(), "多个无效时间戳应该全部验证失败");
        assertEquals(3, validator.getErrors().size());
    }

    // === 混合类型链式调用 ===

    @Test
    public void testChainedValidation_StringAndLong() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("1700000000")
                .isTimestamp(1700000000000L)
                .isTimestamp("9999999999", Timestamp.TimestampUnit.SECONDS);

        assertTrue(validator.passed(), "String和Long混合时间戳应该通过验证");
    }

    // === 实际应用场景测试 ===

    @Test
    public void testRealWorldTimestamps() {
        ValidX validator = ValidX.init();

        // 常见时间戳：2023-2025年的秒级和毫秒级时间戳
        validator.isTimestamp("1672531200")   // 2023-01-01 (秒)
                .isTimestamp("1704067200")    // 2024-01-01 (秒)
                .isTimestamp("1735689600")    // 2025-01-01 (秒)
                .isTimestamp("1672531200000") // 2023-01-01 (毫秒)
                .isTimestamp("1704067200000") // 2024-01-01 (毫秒)
                .isTimestamp("1735689600000") // 2025-01-01 (毫秒)
                .isTimestamp(1700000000L);    // Long类型

        assertTrue(validator.passed(), "实际常用时间戳应该通过验证");
    }

    @Test
    public void testRealWorldTimestampsWithSpecificUnits() {
        ValidX validator = ValidX.init();

        validator.isTimestamp("1672531200", Timestamp.TimestampUnit.SECONDS)
                .isTimestamp("1735689600000", Timestamp.TimestampUnit.MILLISECONDS);

        assertTrue(validator.passed(), "指定单位的实际时间戳应该通过验证");
    }

    // === 错误信息验证 ===

    @Test
    public void testErrorMessageContent() {
        ValidX validator = ValidX.init();
        validator.isTimestamp("abc");

        assertFalse(validator.passed());
        assertNotNull(validator.getErrors().get(0));
        assertFalse(validator.getErrors().get(0).isEmpty(), "错误信息不应为空");
    }
}
