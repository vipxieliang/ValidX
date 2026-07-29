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

import io.github.vipxieliang.validx.annotations.CronExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CronExpression验证器测试类
 */
public class CronExpressionValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === 有效的Cron表达式测试 ===

    @Test
    public void testValidCronExpression_EveryDayAtNoon() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "每天中午12点的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_EveryMinute() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 * * * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "每分钟执行的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_Every15Minutes() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0/15 * * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "每15分钟执行的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_WeekdaysAt9AM() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 9 ? * MON-FRI";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "工作日早上9点的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_FirstDayOfMonth() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 0 1 * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "每月第一天的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_LastDayOfMonth() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 0 L * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "每月最后一天的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_WithYear() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 * * ? 2025";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带年份的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_SpecificWeekday() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 10 ? * 6#3";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "每月第三个星期五的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_Workday() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 15W * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "最接近15号的工作日的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_MultipleValues() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 8,12,18 * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "多个时间点的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_Range() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 9-17 * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "时间范围的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_MonthNames() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 1 JAN,FEB,MAR ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "使用月份英文缩写的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_DayNames() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 ? * SUN,SAT";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "使用星期英文缩写的Cron表达式应该通过验证");
    }

    // === 无效的Cron表达式测试 ===

    @Test
    public void testInvalidCronExpression_TooFewFields() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 * *";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "字段数不足的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_TooManyFields() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 * * ? 2025 extra";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "字段数过多的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_InvalidSecond() {
        TestDTO dto = new TestDTO();
        dto.schedule = "60 0 12 * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "秒数超出范围的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_InvalidMinute() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 60 12 * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "分钟超出范围的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_InvalidHour() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 24 * * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "小时超出范围的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_InvalidDay() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 32 * ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "日期超出范围的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_InvalidMonth() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 1 13 ?";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "月份超出范围的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_InvalidWeek() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 ? * 8";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "星期超出范围的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_BothDayAndWeek() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 15 * MON";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "日和周同时指定的Cron表达式应该失败");
    }

    @Test
    public void testInvalidCronExpression_InvalidCharacter() {
        TestDTO dto = new TestDTO();
        dto.schedule = "0 0 12 * * @ ";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "包含无效字符的Cron表达式应该失败");
    }

    // === Null和空值测试 ===

    @Test
    public void testValidCronExpression_Null() {
        TestDTO dto = new TestDTO();
        dto.schedule = null;

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证（由@NotNull处理）");
    }

    @Test
    public void testValidCronExpression_Empty() {
        TestDTO dto = new TestDTO();
        dto.schedule = "";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证（由@NotEmpty处理）");
    }

    // === 静态方法测试 ===

    @Test
    public void testStaticValidation_ValidExpression() {
        assertTrue(CronExpressionValidator.isValid("0 0 12 * * ?"),
            "静态方法应该验证有效的Cron表达式");
    }

    @Test
    public void testStaticValidation_InvalidExpression() {
        assertFalse(CronExpressionValidator.isValid("invalid cron"),
            "静态方法应该拒绝无效的Cron表达式");
    }

    @Test
    public void testStaticValidation_Null() {
        assertTrue(CronExpressionValidator.isValid(null),
            "静态方法对null应该返回true");
    }

    // === 测试DTO ===

    static class TestDTO {
        @CronExpression
        public String schedule;
    }
}
