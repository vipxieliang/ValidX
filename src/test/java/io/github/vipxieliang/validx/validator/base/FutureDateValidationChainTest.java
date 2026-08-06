package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 isFutureDate 链式API的各个版本
 */
public class FutureDateValidationChainTest {

    @Test
    public void testIsFutureDate_NoParams_Valid() {
        ValidX validator = ValidX.init();
        validator.isFutureDate("2099-12-31");
        assertTrue(validator.passed(), "未来的日期（无参数，默认不包含今天）应该通过验证");
    }

    @Test
    public void testIsFutureDate_NoParams_Invalid() {
        ValidX validator = ValidX.init();
        validator.isFutureDate("2020-01-01");
        assertFalse(validator.passed(), "过去的日期应该验证失败");
    }

    @Test
    public void testIsFutureDate_WithIncludeToday_False() {
        ValidX validator = ValidX.init();
        validator.isFutureDate("2099-12-31", false);
        assertTrue(validator.passed(), "未来的日期（不包含今天）应该通过验证");
    }

    @Test
    public void testIsFutureDate_WithIncludeToday_True() {
        ValidX validator = ValidX.init();
        // 使用今天的日期
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayStr = today.toString();

        validator.isFutureDate(todayStr, true);
        assertTrue(validator.passed(), "今天的日期（包含今天）应该通过验证");
    }

    @Test
    public void testIsFutureDate_WithCustomPattern() {
        ValidX validator = ValidX.init();
        validator.isFutureDate("31/12/2099", false, "dd/MM/yyyy");
        assertTrue(validator.passed(), "自定义格式的未来日期应该通过验证");
    }
}
