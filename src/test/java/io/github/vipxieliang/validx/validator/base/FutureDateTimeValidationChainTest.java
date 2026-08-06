package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 isFutureDateTime 链式API的各个版本
 */
public class FutureDateTimeValidationChainTest {

    @Test
    public void testIsFutureDateTime_NoParams_Valid() {
        ValidX validator = ValidX.init();
        validator.isFutureDateTime("2099-12-31 23:59:59");
        assertTrue(validator.passed(), "未来的日期时间（无参数，默认不包含今天）应该通过验证");
    }

    @Test
    public void testIsFutureDateTime_NoParams_Invalid() {
        ValidX validator = ValidX.init();
        validator.isFutureDateTime("2020-01-01 12:30:45");
        assertFalse(validator.passed(), "过去的日期时间应该验证失败");
    }

    @Test
    public void testIsFutureDateTime_WithIncludeToday_False() {
        ValidX validator = ValidX.init();
        validator.isFutureDateTime("2099-12-31 23:59:59", false);
        assertTrue(validator.passed(), "未来的日期时间（不包含今天）应该通过验证");
    }

    @Test
    public void testIsFutureDateTime_WithIncludeToday_True() {
        ValidX validator = ValidX.init();
        // 使用今天的日期和未来的时间
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayDateTimeStr = today.toString() + " 23:59:59";

        validator.isFutureDateTime(todayDateTimeStr, true);
        assertTrue(validator.passed(), "今天的日期时间（包含今天）应该通过验证");
    }

    @Test
    public void testIsFutureDateTime_WithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isFutureDateTime("31-12-2099 23:59:59", false, "dd-MM-yyyy HH:mm:ss");
        assertTrue(validator.passed(), "自定义格式的未来日期时间应该通过验证");
    }
}
