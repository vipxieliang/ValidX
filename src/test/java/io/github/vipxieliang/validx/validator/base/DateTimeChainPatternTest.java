package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试链式API中日期和日期时间验证器的自定义pattern功能
 */
public class DateTimeChainPatternTest {

    @Test
    public void testPastDateWithDefaultPattern() {
        ValidX validator = ValidX.init();
        validator.isPastDate("2020-01-01", false);
        assertTrue(validator.passed(), "默认格式 yyyy-MM-dd 应该通过验证");
    }

    @Test
    public void testPastDateWithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");
        assertTrue(validator.passed(), "自定义格式 MM/dd/yyyy 应该通过验证");
    }

    @Test
    public void testPastDateWithCustomPatternInvalid() {
        ValidX validator = ValidX.init();
        // 使用自定义格式，但提供的是默认格式的日期
        validator.isPastDate("2020-01-01", false, "MM/dd/yyyy");
        assertFalse(validator.passed(), "格式不匹配应该失败");
    }

    @Test
    public void testFutureDateWithDefaultPattern() {
        ValidX validator = ValidX.init();
        validator.isFutureDate("2099-12-31", false);
        assertTrue(validator.passed(), "默认格式 yyyy-MM-dd 应该通过验证");
    }

    @Test
    public void testFutureDateWithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isFutureDate("31/12/2099", false, "dd/MM/yyyy");
        assertTrue(validator.passed(), "自定义格式 dd/MM/yyyy 应该通过验证");
    }

    @Test
    public void testPastDateTimeWithDefaultPattern() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("2020-01-01 12:30:45", false);
        assertTrue(validator.passed(), "默认格式 yyyy-MM-dd HH:mm:ss 应该通过验证");
    }

    @Test
    public void testPastDateTimeWithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("01/01/2020 12:30:45", false, "MM/dd/yyyy HH:mm:ss");
        assertTrue(validator.passed(), "自定义格式 MM/dd/yyyy HH:mm:ss 应该通过验证");
    }

    @Test
    public void testPastDateTimeWithCustomPattern24Hour() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("2020-01-01 23:59:59", false, "yyyy-MM-dd HH:mm:ss");
        assertTrue(validator.passed(), "24小时制格式应该通过验证");
    }

    @Test
    public void testFutureDateTimeWithDefaultPattern() {
        ValidX validator = ValidX.init();
        validator.isFutureDateTime("2099-12-31 23:59:59", false);
        assertTrue(validator.passed(), "默认格式 yyyy-MM-dd HH:mm:ss 应该通过验证");
    }

    @Test
    public void testFutureDateTimeWithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isFutureDateTime("31-12-2099 23:59:59", false, "dd-MM-yyyy HH:mm:ss");
        assertTrue(validator.passed(), "自定义格式 dd-MM-yyyy HH:mm:ss 应该通过验证");
    }

    @Test
    public void testPastDateTimeWithISO8601Pattern() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("2020-01-01T12:30:45", false, "yyyy-MM-dd'T'HH:mm:ss");
        assertTrue(validator.passed(), "ISO 8601格式应该通过验证");
    }

    @Test
    public void testFutureDateTimeWithISO8601Pattern() {
        ValidX validator = ValidX.init();
        validator.isFutureDateTime("2099-12-31T23:59:59", false, "yyyy-MM-dd'T'HH:mm:ss");
        assertTrue(validator.passed(), "ISO 8601格式应该通过验证");
    }

    @Test
    public void testPastDateCannotContainTimePattern() {
        ValidX validator = ValidX.init();
        // 日期验证器不能包含时间符号
        assertThrows(IllegalArgumentException.class, () -> {
            validator.isPastDate("2020-01-01 12:30:45", false, "yyyy-MM-dd HH:mm:ss");
        }, "日期验证器的pattern包含时间符号应该抛出异常");
    }

    @Test
    public void testFutureDateCannotContainTimePattern() {
        ValidX validator = ValidX.init();
        // 日期验证器不能包含时间符号
        assertThrows(IllegalArgumentException.class, () -> {
            validator.isFutureDate("2099-12-31 23:59:59", false, "yyyy-MM-dd HH:mm:ss");
        }, "日期验证器的pattern包含时间符号应该抛出异常");
    }

    @Test
    public void testPastDateTimeMustContainTimePattern() {
        ValidX validator = ValidX.init();
        // 日期时间验证器必须包含时间符号
        assertThrows(IllegalArgumentException.class, () -> {
            validator.isPastDateTime("2020-01-01", false, "yyyy-MM-dd");
        }, "日期时间验证器的pattern不包含时间符号应该抛出异常");
    }

    @Test
    public void testFutureDateTimeMustContainTimePattern() {
        ValidX validator = ValidX.init();
        // 日期时间验证器必须包含时间符号
        assertThrows(IllegalArgumentException.class, () -> {
            validator.isFutureDateTime("2099-12-31", false, "yyyy-MM-dd");
        }, "日期时间验证器的pattern不包含时间符号应该抛出异常");
    }

    @Test
    public void testChainMultipleDatesWithDifferentPatterns() {
        ValidX validator = ValidX.init();
        validator.isPastDate("2020-01-01", false)  // 默认格式
                 .isPastDate("01/01/2020", false, "MM/dd/yyyy")  // 自定义格式
                 .isFutureDate("2099-12-31", false)  // 默认格式
                 .isFutureDate("31/12/2099", false, "dd/MM/yyyy");  // 自定义格式

        assertTrue(validator.passed(), "链式调用多个不同格式的日期验证应该全部通过");
    }

    @Test
    public void testChainMultipleDateTimesWithDifferentPatterns() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("2020-01-01 12:30:45", false)  // 默认格式
                 .isPastDateTime("01/01/2020 12:30:45", false, "MM/dd/yyyy HH:mm:ss")  // 自定义格式
                 .isFutureDateTime("2099-12-31 23:59:59", false)  // 默认格式
                 .isFutureDateTime("31-12-2099 23:59:59", false, "dd-MM-yyyy HH:mm:ss");  // 自定义格式

        assertTrue(validator.passed(), "链式调用多个不同格式的日期时间验证应该全部通过");
    }
}
