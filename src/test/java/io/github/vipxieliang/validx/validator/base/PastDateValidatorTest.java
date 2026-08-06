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

import io.github.vipxieliang.validx.annotations.PastDate;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PastDateValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @PastDate
        private String date;

        public TestEntity(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }
    }
    
    // 创建一个支持includeToday=true的测试实体
    public static class TestEntityIncludeToday {
        @PastDate(includeToday = true)
        private String date;

        public TestEntityIncludeToday(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }
    }

    @Test
    public void testPastDateValidatorDirect() {
        // 直接测试验证器的逻辑
        PastDateValidator validator = new PastDateValidator();

        // 创建一个模拟的PastDate注解实例
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        validator.initialize(mockAnnotation);

        // 测试有效的过去日期 (yyyy-MM-dd格式)
        String pastDate1 = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(validator.isValid(pastDate1, null), "有效的过去日期应该通过验证: " + pastDate1);

        // 测试无效的未来日期 (yyyy-MM-dd格式)
        String futureDate1 = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertFalse(validator.isValid(futureDate1, null), "未来的日期不应该通过验证: " + futureDate1);

        // 测试今天的日期应该不通过验证 (因为今天不是过去)
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertFalse(validator.isValid(today, null), "今天的日期不应该通过验证: " + today);

        // 测试过去的时间应该通过验证
        String now = LocalDateTime.of(2020, 1, 1, 12, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(validator.isValid(now, null), "过去的时间应该通过验证: " + now);

        // 测试null值应该通过验证
        assertTrue(validator.isValid(null, null), "null值应该通过验证");

        // 测试空字符串应该通过验证
        assertTrue(validator.isValid("", null), "空字符串应该通过验证");

        // 测试无效格式的日期
        assertFalse(validator.isValid("invalid-date", null), "无效格式的日期不应该通过验证");

        // 测试无效格式的日期时间
        assertFalse(validator.isValid("2023-13-40", null), "无效格式的日期不应该通过验证");
    }
    
    @Test
    public void testPastDateValidatorWithIncludeToday() {
        // 创建一个模拟的PastDate注解实例，设置includeToday为true
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(true);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        PastDateValidator includeTodayValidator = new PastDateValidator();
        includeTodayValidator.initialize(mockAnnotation);

        // 测试今天的日期应该通过验证（包含今天）
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(includeTodayValidator.isValid(today, null), "今天的日期应该通过验证（包含今天）: " + today);

        // 测试昨天的日期应该通过验证
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(includeTodayValidator.isValid(yesterday, null), "昨天的日期应该通过验证: " + yesterday);

        // 测试明天的日期不应该通过验证
        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertFalse(includeTodayValidator.isValid(tomorrow, null), "明天的日期不应该通过验证: " + tomorrow);
    }

    @Test
    public void testValidPastDates() {
        // 测试有效的过去日期
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TestEntity entity1 = new TestEntity(pastDate);
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的过去日期应该通过验证: " + pastDate);
    }

    @Test
    public void testInvalidFutureDates() {
        // 测试无效的未来日期
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TestEntity entity1 = new TestEntity(futureDate);
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "未来的日期不应该通过验证: " + futureDate);

        // 测试无效的未来日期时间
        String futureDateTime = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        TestEntity entity2 = new TestEntity(futureDateTime);
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "未来的日期时间不应该通过验证: " + futureDateTime);
    }
    
    @Test
    public void testValidPastDatesWithIncludeToday() {
        // 测试今天的日期应该通过验证（包含今天）
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TestEntityIncludeToday entity1 = new TestEntityIncludeToday(today);
        Set<ConstraintViolation<TestEntityIncludeToday>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "今天的日期应该通过验证（包含今天）: " + today);
        
        // 测试昨天的日期应该通过验证
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TestEntityIncludeToday entity2 = new TestEntityIncludeToday(yesterday);
        Set<ConstraintViolation<TestEntityIncludeToday>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "昨天的日期应该通过验证: " + yesterday);
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试null值
        TestEntity entity1 = new TestEntity(null);
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "null值应该通过验证");

        // 测试空字符串
        TestEntity entity2 = new TestEntity("");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "空字符串应该通过验证");
    }

    @Test
    public void testInvalidFormats() {
        // 测试无效格式的日期
        TestEntity entity1 = new TestEntity("invalid-date");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效格式的日期不应该通过验证");

        // 测试无效格式的日期时间
        TestEntity entity2 = new TestEntity("2023-13-40 25:70:80");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效格式的日期时间不应该通过验证");
    }

    @Test
    public void testCustomPatternSlashFormat() {
        PastDateValidator customValidator = new PastDateValidator();

        // 创建使用斜杠格式的验证器
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy/MM/dd");

        customValidator.initialize(mockAnnotation);

        // 测试有效的过去日期（斜杠格式）
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertTrue(customValidator.isValid(pastDate, null), "过去日期应该通过验证: " + pastDate);

        // 测试无效的未来日期（斜杠格式）
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertFalse(customValidator.isValid(futureDate, null), "未来日期不应该通过验证: " + futureDate);

        // 测试错误的格式不应该通过验证
        assertFalse(customValidator.isValid("2020-12-31", null), "连字符格式不应该通过斜杠验证器");
    }

    // 删除此测试 - 日期时间格式应该使用 @PastDateTime

    @Test
    public void testCustomPatternCompactFormat() {
        PastDateValidator customValidator = new PastDateValidator();

        // 创建使用紧凑格式的验证器
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyyMMdd");

        customValidator.initialize(mockAnnotation);

        // 测试有效的过去日期（紧凑格式）
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertTrue(customValidator.isValid(pastDate, null), "过去日期应该通过验证: " + pastDate);

        // 测试无效的未来日期（紧凑格式）
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertFalse(customValidator.isValid(futureDate, null), "未来日期不应该通过验证: " + futureDate);

        // 测试带分隔符的格式不应该通过验证
        assertFalse(customValidator.isValid("2020-12-31", null), "带分隔符的格式不应该通过紧凑验证器");
    }

    @Test
    public void testCustomPatternWithIncludeToday() {
        PastDateValidator customValidator = new PastDateValidator();

        // 创建使用斜杠格式并包含今天的验证器
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(true);
        when(mockAnnotation.pattern()).thenReturn("yyyy/MM/dd");

        customValidator.initialize(mockAnnotation);

        // 测试今天的日期应该通过验证
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertTrue(customValidator.isValid(today, null), "今天的日期应该通过验证（包含今天）: " + today);

        // 测试过去日期应该通过验证
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertTrue(customValidator.isValid(pastDate, null), "过去日期应该通过验证: " + pastDate);

        // 测试未来日期不应该通过验证
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertFalse(customValidator.isValid(futureDate, null), "未来日期不应该通过验证: " + futureDate);
    }
}