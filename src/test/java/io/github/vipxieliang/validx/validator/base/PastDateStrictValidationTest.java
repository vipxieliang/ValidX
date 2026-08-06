package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.PastDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 PastDate 的严格格式验证
 */
public class PastDateStrictValidationTest {

    @Test
    public void testStrictDateFormatOnly() {
        PastDateValidator validator = new PastDateValidator();

        // 创建只接受日期格式的验证器
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        validator.initialize(mockAnnotation);

        // 测试纯日期格式 - 应该通过
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(validator.isValid(pastDate, null), "纯日期格式应该通过验证: " + pastDate);

        // 测试带时间的格式 - 应该失败
        String pastDateTime = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 10:30:00";
        assertFalse(validator.isValid(pastDateTime, null), "带时间的格式不应该通过验证: " + pastDateTime);
    }

    // 删除此测试 - 日期时间格式应该使用 @PastDateTime

    @Test
    public void testCustomPatternStrictValidation() {
        PastDateValidator validator = new PastDateValidator();

        // 创建使用斜杠格式的验证器
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy/MM/dd");

        validator.initialize(mockAnnotation);

        // 测试斜杠格式 - 应该通过
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertTrue(validator.isValid(pastDate, null), "斜杠格式应该通过验证: " + pastDate);

        // 测试连字符格式 - 应该失败
        String wrongFormat = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertFalse(validator.isValid(wrongFormat, null), "连字符格式不应该通过验证（期望斜杠）: " + wrongFormat);
    }

    // 删除此测试 - 各种时间符号应该在 PastDateTime 中测试

    // 删除此测试 - 带时间的字面量应该在 PastDateTime 中测试

    @Test
    public void testLiteralOnlyNoTime() {
        PastDateValidator validator = new PastDateValidator();

        // pattern 包含字面量 h 但不是时间格式
        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd 'The date'");

        validator.initialize(mockAnnotation);

        // 应该识别为纯日期格式（字面量中的 h 不算）
        String pastDate = "2020-01-15 The date";
        assertTrue(validator.isValid(pastDate, null), "应该识别为纯日期格式: " + pastDate);
    }

    /**
     * 辅助方法：测试指定 pattern 是否能正确识别为时间格式
     */
    private void testTimePattern(String pattern, String value, boolean shouldHaveTime) {
        PastDateValidator validator = new PastDateValidator();

        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn(pattern);

        validator.initialize(mockAnnotation);

        // 如果 pattern 包含时间，value 应该能通过验证
        boolean isValid = validator.isValid(value, null);
        if (shouldHaveTime) {
            assertTrue(isValid, "Pattern '" + pattern + "' 应该能解析: " + value);
        }
    }
}
