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

import io.github.vipxieliang.validx.annotations.FutureDate;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FutureDateValidator测试类
 */
public class FutureDateValidatorTest {

    private final FutureDateValidator validator = new FutureDateValidator();
    
    // 用于测试includeToday=true的验证器
    private FutureDateValidator createIncludeTodayValidator() {
        FutureDateValidator includeTodayValidator = new FutureDateValidator();

        // 创建模拟的FutureDate注解实例，设置includeToday为true
        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(true);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        includeTodayValidator.initialize(mockAnnotation);
        return includeTodayValidator;
    }

    // 用于测试includeToday=false的验证器（默认行为）
    private FutureDateValidator createDefaultValidator() {
        FutureDateValidator defaultValidator = new FutureDateValidator();

        // 创建模拟的FutureDate注解实例，设置includeToday为false
        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        defaultValidator.initialize(mockAnnotation);
        return defaultValidator;
    }

    @Test
    public void testValidFutureDate() {
        FutureDateValidator defaultValidator = createDefaultValidator();

        // 测试有效的未来日期
        String futureDate = LocalDate.now().plusDays(1).toString();
        assertTrue(defaultValidator.isValid(futureDate, null), futureDate + " should be valid");
    }

    @Test
    public void testInvalidPastDate() {
        FutureDateValidator defaultValidator = createDefaultValidator();

        // 测试无效的过去日期
        String pastDate = LocalDate.now().minusDays(1).toString();
        assertFalse(defaultValidator.isValid(pastDate, null), pastDate + " should be invalid");
    }

    @Test
    public void testInvalidToday() {
        FutureDateValidator defaultValidator = createDefaultValidator();
        
        // 测试今天的日期（不是未来日期）
        String today = LocalDate.now().toString();
        assertFalse(defaultValidator.isValid(today, null), today + " should be invalid as it's not a future date");
    }

    @Test
    public void testValidTodayWithIncludeToday() {
        FutureDateValidator includeTodayValidator = createIncludeTodayValidator();

        // 测试今天的日期（包含今天）
        String today = LocalDate.now().toString();
        assertTrue(includeTodayValidator.isValid(today, null), today + " should be valid when includeToday=true");
    }

    @Test
    public void testInvalidYesterdayWithIncludeToday() {
        FutureDateValidator includeTodayValidator = createIncludeTodayValidator();
        
        // 测试昨天的日期（即使包含今天，昨天也应该是无效的）
        String yesterday = LocalDate.now().minusDays(1).toString();
        assertFalse(includeTodayValidator.isValid(yesterday, null), yesterday + " should be invalid even when includeToday=true");
    }

    @Test
    public void testInvalidDateFormat() {
        FutureDateValidator defaultValidator = createDefaultValidator();

        // 测试无效的日期格式
        assertFalse(defaultValidator.isValid("invalid-date", null), "Invalid date format should be invalid");
        assertFalse(defaultValidator.isValid("2023/12/31", null), "Wrong date format should be invalid");
        assertTrue(defaultValidator.isValid("", null), "Empty string should be valid (handled by @NotEmpty)");
        assertTrue(defaultValidator.isValid(null, null), "Null value should be valid (handled by @NotNull)");
    }

    @Test
    public void testCustomPatternSlashFormat() {
        FutureDateValidator customValidator = new FutureDateValidator();

        // 创建使用斜杠格式的验证器
        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy/MM/dd");

        customValidator.initialize(mockAnnotation);

        // 测试有效的未来日期（斜杠格式）
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertTrue(customValidator.isValid(futureDate, null), "未来日期应该通过验证: " + futureDate);

        // 测试无效的过去日期（斜杠格式）
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertFalse(customValidator.isValid(pastDate, null), "过去日期不应该通过验证: " + pastDate);

        // 测试错误的格式不应该通过验证
        assertFalse(customValidator.isValid("2025-12-31", null), "连字符格式不应该通过斜杠验证器");
    }

    // 删除此测试 - 日期时间格式应该使用 @FutureDateTime

    @Test
    public void testCustomPatternCompactFormat() {
        FutureDateValidator customValidator = new FutureDateValidator();

        // 创建使用紧凑格式的验证器
        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyyMMdd");

        customValidator.initialize(mockAnnotation);

        // 测试有效的未来日期（紧凑格式）
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertTrue(customValidator.isValid(futureDate, null), "未来日期应该通过验证: " + futureDate);

        // 测试无效的过去日期（紧凑格式）
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertFalse(customValidator.isValid(pastDate, null), "过去日期不应该通过验证: " + pastDate);

        // 测试带分隔符的格式不应该通过验证
        assertFalse(customValidator.isValid("2025-12-31", null), "带分隔符的格式不应该通过紧凑验证器");
    }

    @Test
    public void testCustomPatternWithIncludeToday() {
        FutureDateValidator customValidator = new FutureDateValidator();

        // 创建使用斜杠格式并包含今天的验证器
        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(true);
        when(mockAnnotation.pattern()).thenReturn("yyyy/MM/dd");

        customValidator.initialize(mockAnnotation);

        // 测试今天的日期应该通过验证
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertTrue(customValidator.isValid(today, null), "今天的日期应该通过验证（包含今天）: " + today);

        // 测试未来日期应该通过验证
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertTrue(customValidator.isValid(futureDate, null), "未来日期应该通过验证: " + futureDate);

        // 测试过去日期不应该通过验证
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertFalse(customValidator.isValid(pastDate, null), "过去日期不应该通过验证: " + pastDate);
    }
}