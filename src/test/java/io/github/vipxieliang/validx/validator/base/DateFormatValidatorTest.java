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

import io.github.vipxieliang.validx.annotations.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateValidator 单元测试 - 注解方式
 * 使用 JSR-380 Bean Validation 注解进行测试
 *
 * @author vipxieliang
 * @since 1.1.0
 */
class DateFormatValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== 测试用的实体类 ====================

    static class TestEntity {
        @Date(pattern = "yyyy-MM-dd")
        private String standardDate;

        @Date(pattern = "yyyy-MM-dd HH:mm:ss")
        private String dateTime;

        @Date(pattern = "yyyy-MM")
        private String yearMonth;

        @Date(pattern = "HH:mm:ss")
        private String time;

        @Date(pattern = "yyyy-MM-dd hh:mm:ss a")
        private String time12Hour;

        @Date(pattern = "MM/dd/yyyy")
        private String usFormat;

        public TestEntity() {}

        public void setStandardDate(String standardDate) {
            this.standardDate = standardDate;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public void setYearMonth(String yearMonth) {
            this.yearMonth = yearMonth;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setTime12Hour(String time12Hour) {
            this.time12Hour = time12Hour;
        }

        public void setUsFormat(String usFormat) {
            this.usFormat = usFormat;
        }
    }

    // ==================== 基础格式测试 ====================

    @Test
    void testAnnotation_ValidStandardDate() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-01-15");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_ValidDateRange() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-12-31");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    // ==================== 无效日期测试 ====================

    @Test
    void testAnnotation_InvalidDate_February30() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-02-30");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_InvalidDate_February29NonLeapYear() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2023-02-29");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_ValidLeapYear() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-02-29");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_InvalidDate_Month13() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-13-01");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_InvalidDate_Day32() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-01-32");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_InvalidDate_Day00() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-01-00");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_InvalidDate_Month00() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-00-01");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    // ==================== 日期时间格式测试 ====================

    @Test
    void testAnnotation_ValidDateTime() {
        TestEntity entity = new TestEntity();
        entity.setDateTime("2024-01-15 13:30:00");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_ValidDateTime_Boundary() {
        TestEntity entity = new TestEntity();
        entity.setDateTime("2024-01-15 23:59:59");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_InvalidTime_Hour24() {
        TestEntity entity = new TestEntity();
        entity.setDateTime("2024-01-15 24:00:00");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_InvalidTime_Minute60() {
        TestEntity entity = new TestEntity();
        entity.setDateTime("2024-01-15 12:60:00");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_InvalidTime_Second60() {
        TestEntity entity = new TestEntity();
        entity.setDateTime("2024-01-15 12:30:60");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    // ==================== 特殊格式测试 ====================

    @Test
    void testAnnotation_ValidYearMonth() {
        TestEntity entity = new TestEntity();
        entity.setYearMonth("2024-01");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_InvalidYearMonth() {
        TestEntity entity = new TestEntity();
        entity.setYearMonth("2024-13");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_ValidTime() {
        TestEntity entity = new TestEntity();
        entity.setTime("14:30:00");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_InvalidTime() {
        TestEntity entity = new TestEntity();
        entity.setTime("25:00:00");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_Valid12HourFormat() {
        TestEntity entity = new TestEntity();
        entity.setTime12Hour("2024-01-15 02:30:00 PM");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_Valid12HourFormat_AM() {
        TestEntity entity = new TestEntity();
        entity.setTime12Hour("2024-01-15 11:30:00 AM");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_ValidUSFormat() {
        TestEntity entity = new TestEntity();
        entity.setUsFormat("12/25/2024");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    // ==================== 格式不匹配测试 ====================

    @Test
    void testAnnotation_InvalidFormat_WrongSeparator() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024/01/15");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    @Test
    void testAnnotation_InvalidFormat_NoPadding() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-1-5");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size());
    }

    // ==================== null 和空值测试 ====================

    @Test
    void testAnnotation_NullValue() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate(null);

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty()); // null is valid
    }

    @Test
    void testAnnotation_EmptyString() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty()); // empty is valid
    }

    // ==================== 边界测试 ====================

    @Test
    void testAnnotation_LeapYear_1900() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("1900-02-29");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size()); // 1900不是闰年
    }

    @Test
    void testAnnotation_LeapYear_2000() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2000-02-29");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty()); // 2000是闰年
    }

    @Test
    void testAnnotation_MonthEnd_January() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-01-31");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_MonthEnd_April() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-04-30");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAnnotation_InvalidMonthEnd_April31() {
        TestEntity entity = new TestEntity();
        entity.setStandardDate("2024-04-31");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertEquals(1, violations.size()); // 4月没有31天
    }

    // ==================== 国际化测试 ====================

    @Test
    void testAnnotation_I18n_English() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("Invalid date format", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testAnnotation_I18n_Chinese() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("日期格式不正确", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testAnnotation_I18n_Japanese() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.JAPANESE);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("日付形式が正しくありません", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testAnnotation_I18n_Korean() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.KOREAN);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("날짜 형식이 올바르지 않습니다", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testAnnotation_I18n_French() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRENCH);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("Format de date invalide", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testAnnotation_I18n_German() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMAN);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("Ungültiges Datumsformat", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testAnnotation_I18n_Russian() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale russianLocale = new Locale("ru");
            Locale.setDefault(russianLocale);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("Неверный формат даты", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testAnnotation_I18n_Spanish() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale spanishLocale = new Locale("es");
            Locale.setDefault(spanishLocale);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            TestEntity entity = new TestEntity();
            entity.setStandardDate("2024-02-30");

            Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
            assertEquals(1, violations.size());

            ConstraintViolation<TestEntity> violation = violations.iterator().next();
            assertEquals("Formato de fecha inválido", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
