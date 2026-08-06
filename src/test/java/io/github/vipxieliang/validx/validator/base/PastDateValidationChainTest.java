package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 isPastDate 链式API的各个版本
 */
public class PastDateValidationChainTest {

    @Test
    public void testIsPastDate_NoParams_Valid() {
        ValidX validator = ValidX.init();
        validator.isPastDate("2020-01-01");
        assertTrue(validator.passed(), "过去的日期（无参数，默认不包含今天）应该通过验证");
    }

    @Test
    public void testIsPastDate_NoParams_Invalid() {
        ValidX validator = ValidX.init();
        validator.isPastDate("2099-12-31");
        assertFalse(validator.passed(), "未来的日期应该验证失败");
    }

    @Test
    public void testIsPastDate_WithIncludeToday_False() {
        ValidX validator = ValidX.init();
        validator.isPastDate("2020-01-01", false);
        assertTrue(validator.passed(), "过去的日期（不包含今天）应该通过验证");
    }

    @Test
    public void testIsPastDate_WithIncludeToday_True() {
        ValidX validator = ValidX.init();
        // 使用今天的日期
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayStr = today.toString();

        validator.isPastDate(todayStr, true);
        assertTrue(validator.passed(), "今天的日期（包含今天）应该通过验证");
    }

    @Test
    public void testIsPastDate_WithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");
        assertTrue(validator.passed(), "自定义格式的过去日期应该通过验证");
    }

    @Test
    public void testDefaultBehaviorIsNotIncludeToday() {
        ValidX validator = ValidX.init();
        // 使用今天的日期，无参数版本（默认不包含今天）
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayStr = today.toString();

        validator.isPastDate(todayStr);
        assertFalse(validator.passed(), "无参数版本默认不包含今天，今天的日期应该验证失败");
    }
}
