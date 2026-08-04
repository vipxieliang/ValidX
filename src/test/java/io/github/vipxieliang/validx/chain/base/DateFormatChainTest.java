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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateFormat 链式调用测试
 * 使用 ValidX 链式API进行测试
 *
 * @author vipxieliang
 * @since 1.0.2
 */
class DateFormatChainTest {

    // ==================== 基础格式测试 ====================

    @Test
    void testChain_ValidStandardDate() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_ValidDateRange() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-12-31", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    // ==================== 无效日期测试 ====================

    @Test
    void testChain_InvalidDate_February30() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
    }

    @Test
    void testChain_InvalidDate_February29NonLeapYear() {
        List<String> errors = ValidX.init()
                .isDateFormat("2023-02-29", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_ValidLeapYear() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-02-29", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_InvalidDate_Month13() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-13-01", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_InvalidDate_Day32() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-32", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_InvalidDate_Day00() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-00", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_InvalidDate_Month00() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-00-01", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    // ==================== 日期时间格式测试 ====================

    @Test
    void testChain_ValidDateTime() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15 13:30:00", "yyyy-MM-dd HH:mm:ss")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_ValidDateTime_Boundary() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15 23:59:59", "yyyy-MM-dd HH:mm:ss")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_InvalidTime_Hour24() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15 24:00:00", "yyyy-MM-dd HH:mm:ss")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_InvalidTime_Minute60() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15 12:60:00", "yyyy-MM-dd HH:mm:ss")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_InvalidTime_Second60() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15 12:30:60", "yyyy-MM-dd HH:mm:ss")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    // ==================== 特殊格式测试 ====================

    @Test
    void testChain_ValidYearMonth() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01", "yyyy-MM")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_InvalidYearMonth() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-13", "yyyy-MM")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_ValidTimeOnly() {
        List<String> errors = ValidX.init()
                .isDateFormat("14:30:00", "HH:mm:ss")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_InvalidTime() {
        List<String> errors = ValidX.init()
                .isDateFormat("25:00:00", "HH:mm:ss")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_Valid12HourFormat() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15 02:30:00 PM", "yyyy-MM-dd hh:mm:ss a")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_Valid12HourFormat_AM() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15 11:30:00 AM", "yyyy-MM-dd hh:mm:ss a")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_ValidUSFormat() {
        List<String> errors = ValidX.init()
                .isDateFormat("12/25/2024", "MM/dd/yyyy")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_ValidEuropeanFormat() {
        List<String> errors = ValidX.init()
                .isDateFormat("25/12/2024", "dd/MM/yyyy")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_ValidCompactFormat() {
        List<String> errors = ValidX.init()
                .isDateFormat("20240115", "yyyyMMdd")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    // ==================== 格式不匹配测试 ====================

    @Test
    void testChain_InvalidFormat_WrongSeparator() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024/01/15", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    @Test
    void testChain_InvalidFormat_NoPadding() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-1-5", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    // ==================== 默认格式测试 ====================

    @Test
    void testChain_DefaultPattern() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_DefaultPattern_Invalid() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-02-30")
                .getErrors();

        assertFalse(errors.isEmpty());
    }

    // ==================== null 和空值测试 ====================

    @Test
    void testChain_NullValue() {
        List<String> errors = ValidX.init()
                .isDateFormat(null, "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty()); // null is valid
    }

    @Test
    void testChain_EmptyString() {
        List<String> errors = ValidX.init()
                .isDateFormat("", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty()); // empty is valid
    }

    @Test
    void testChain_WhitespaceString() {
        List<String> errors = ValidX.init()
                .isDateFormat("   ", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty()); // whitespace is valid
    }

    // ==================== 链式多个验证 ====================

    @Test
    void testChain_MultipleValidations_AllValid() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15", "yyyy-MM-dd")
                .isDateFormat("12/25/2024", "MM/dd/yyyy")
                .isDateFormat("14:30:00", "HH:mm:ss")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_MultipleValidations_MixedValidAndInvalid() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-15", "yyyy-MM-dd")  // valid
                .isDateFormat("2024-02-30", "yyyy-MM-dd")  // invalid
                .isDateFormat("2024-13-01", "yyyy-MM-dd")  // invalid
                .getErrors();

        assertEquals(2, errors.size());
    }

    @Test
    void testChain_MultipleValidations_AllInvalid() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-02-30", "yyyy-MM-dd")  // invalid
                .isDateFormat("2024-13-01", "yyyy-MM-dd")  // invalid
                .isDateFormat("2024-04-31", "yyyy-MM-dd")  // invalid
                .getErrors();

        assertEquals(3, errors.size());
    }

    // ==================== 边界测试 ====================

    @Test
    void testChain_LeapYear_1900() {
        List<String> errors = ValidX.init()
                .isDateFormat("1900-02-29", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty()); // 1900不是闰年
    }

    @Test
    void testChain_LeapYear_2000() {
        List<String> errors = ValidX.init()
                .isDateFormat("2000-02-29", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty()); // 2000是闰年
    }

    @Test
    void testChain_MonthEnd_January() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-01-31", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_MonthEnd_April() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-04-30", "yyyy-MM-dd")
                .getErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    void testChain_InvalidMonthEnd_April31() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-04-31", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty()); // 4月没有31天
    }

    @Test
    void testChain_InvalidMonthEnd_February31() {
        List<String> errors = ValidX.init()
                .isDateFormat("2024-02-31", "yyyy-MM-dd")
                .getErrors();

        assertFalse(errors.isEmpty()); // 2月没有31天
    }

    // ==================== 国际化测试 ====================

    @Test
    void testChain_I18n_English() {
        List<String> errors = ValidX.init()
                .withLocale(Locale.ENGLISH)
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("Invalid date format", errors.get(0));
    }

    @Test
    void testChain_I18n_Chinese() {
        List<String> errors = ValidX.init()
                .withLocale(Locale.SIMPLIFIED_CHINESE)
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("日期格式不正确", errors.get(0));
    }

    @Test
    void testChain_I18n_Japanese() {
        List<String> errors = ValidX.init()
                .withLocale(Locale.JAPANESE)
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("日付形式が正しくありません", errors.get(0));
    }

    @Test
    void testChain_I18n_Korean() {
        List<String> errors = ValidX.init()
                .withLocale(Locale.KOREAN)
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("날짜 형식이 올바르지 않습니다", errors.get(0));
    }

    @Test
    void testChain_I18n_French() {
        List<String> errors = ValidX.init()
                .withLocale(Locale.FRENCH)
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("Format de date invalide", errors.get(0));
    }

    @Test
    void testChain_I18n_German() {
        List<String> errors = ValidX.init()
                .withLocale(Locale.GERMAN)
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("Ungültiges Datumsformat", errors.get(0));
    }

    @Test
    void testChain_I18n_Russian() {
        List<String> errors = ValidX.init()
                .withLocale(new Locale("ru"))
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("Неверный формат даты", errors.get(0));
    }

    @Test
    void testChain_I18n_Spanish() {
        List<String> errors = ValidX.init()
                .withLocale(new Locale("es"))
                .isDateFormat("2024-02-30", "yyyy-MM-dd")
                .getErrors();

        assertEquals(1, errors.size());
        assertEquals("Formato de fecha inválido", errors.get(0));
    }
}
