package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 isPastDateTime 链式API的各个版本
 */
public class PastDateTimeValidationChainTest {

    @Test
    public void testIsPastDateTime_NoParams_Valid() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("2020-01-01 12:30:45");
        assertTrue(validator.passed(), "过去的日期时间（无参数，默认不包含今天）应该通过验证");
    }

    @Test
    public void testIsPastDateTime_NoParams_Invalid() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("2099-12-31 23:59:59");
        assertFalse(validator.passed(), "未来的日期时间应该验证失败");
    }

    @Test
    public void testIsPastDateTime_WithIncludeToday_False() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("2020-01-01 12:30:45", false);
        assertTrue(validator.passed(), "过去的日期时间（不包含今天）应该通过验证");
    }

    @Test
    public void testIsPastDateTime_WithIncludeToday_True() {
        ValidX validator = ValidX.init();
        // 使用今天的日期和过去的时间
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayDateTimeStr = today.toString() + " 00:00:01";

        validator.isPastDateTime(todayDateTimeStr, true);
        assertTrue(validator.passed(), "今天的日期时间（包含今天）应该通过验证");
    }

    @Test
    public void testIsPastDateTime_WithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isPastDateTime("01/01/2020 12:30:45", false, "MM/dd/yyyy HH:mm:ss");
        assertTrue(validator.passed(), "自定义格式的过去日期时间应该通过验证");
    }

    @Test
    public void testDateTimeDefaultBehaviorIsNotIncludeToday() {
        ValidX validator = ValidX.init();
        // 使用今天的日期时间，无参数版本（默认不包含今天）
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayDateTimeStr = today.toString() + " 12:00:00";

        validator.isPastDateTime(todayDateTimeStr);
        assertFalse(validator.passed(), "无参数版本默认不包含今天，今天的日期时间应该验证失败");
    }
}
