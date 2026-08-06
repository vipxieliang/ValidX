package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.PastDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 PastDate 验证器 - 只接受纯日期格式
 */
public class PastDateValidatorPatternCheckTest {

    @Test
    public void testPatternWithTimeThrowsException() {
        PastDateValidator validator = new PastDateValidator();

        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        // 应该抛出异常：pattern 包含时间符号
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.initialize(mockAnnotation);
        }, "pattern 包含时间符号应该抛出异常");

        // 验证消息包含关键信息（pattern 和时间符号列表）
        String message = exception.getMessage();
        assertTrue(message.contains("pattern") &&
                   (message.contains("H, h, K") || message.contains("time format")),
                   "异常消息应该说明 pattern 的问题，实际消息: " + message);
    }

    @Test
    public void testValidDateOnlyPattern() {
        PastDateValidator validator = new PastDateValidator();

        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        // 不应该抛出异常
        assertDoesNotThrow(() -> {
            validator.initialize(mockAnnotation);
        }, "纯日期格式不应该抛出异常");

        // 测试验证
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(validator.isValid(pastDate, null), "过去的日期应该通过验证");
    }

    @Test
    public void testDateTimeInputShouldFail() {
        PastDateValidator validator = new PastDateValidator();

        PastDate mockAnnotation = mock(PastDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        validator.initialize(mockAnnotation);

        // 输入带时间的字符串应该失败
        String dateTimeInput = "2020-01-15 10:30:00";
        assertFalse(validator.isValid(dateTimeInput, null), "带时间的输入不应该通过验证");
    }
}
