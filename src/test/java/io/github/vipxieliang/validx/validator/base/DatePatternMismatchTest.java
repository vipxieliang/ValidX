package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试日期格式不匹配的情况
 * 例如：数据包含时分秒，但pattern只定义了年月日
 */
public class DatePatternMismatchTest {

    @Test
    public void testFutureDate_WithTimeButPatternIsDateOnly() {
        ValidX validator = ValidX.init();

        // 输入包含时分秒，但pattern只是年月日
        String dateTimeValue = "2099-12-31 12:30:45";
        String dateOnlyPattern = "yyyy-MM-dd";

        validator.isFutureDate(dateTimeValue, false, dateOnlyPattern);

        System.out.println("输入: " + dateTimeValue);
        System.out.println("Pattern: " + dateOnlyPattern);
        System.out.println("验证结果: " + (validator.passed() ? "通过" : "失败"));
        System.out.println("错误信息: " + validator.getErrors());
    }

    @Test
    public void testPastDate_WithTimeButPatternIsDateOnly() {
        ValidX validator = ValidX.init();

        // 输入包含时分秒，但pattern只是年月日
        String dateTimeValue = "2020-01-01 12:30:45";
        String dateOnlyPattern = "yyyy-MM-dd";

        validator.isPastDate(dateTimeValue, false, dateOnlyPattern);

        System.out.println("输入: " + dateTimeValue);
        System.out.println("Pattern: " + dateOnlyPattern);
        System.out.println("验证结果: " + (validator.passed() ? "通过" : "失败"));
        System.out.println("错误信息: " + validator.getErrors());
    }

    @Test
    public void testFutureDate_DateOnlyWithDateOnlyPattern_ShouldPass() {
        ValidX validator = ValidX.init();

        // 输入和pattern都是年月日格式
        String dateValue = "2099-12-31";
        String dateOnlyPattern = "yyyy-MM-dd";

        validator.isFutureDate(dateValue, false, dateOnlyPattern);

        System.out.println("输入: " + dateValue);
        System.out.println("Pattern: " + dateOnlyPattern);
        System.out.println("验证结果: " + (validator.passed() ? "通过" : "失败"));
    }
}
