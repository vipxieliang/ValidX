package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.PastDateTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 PastDateTime 验证器
 */
public class PastDateTimeValidatorTest {

    @Test
    public void testValidPastDateTime() {
        PastDateTimeValidator validator = new PastDateTimeValidator();

        PastDateTime mockAnnotation = mock(PastDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试过去的日期时间
        String pastDateTime = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(pastDateTime, null), "过去的日期时间应该通过验证");
    }

    @Test
    public void testInvalidFutureDateTime() {
        PastDateTimeValidator validator = new PastDateTimeValidator();

        PastDateTime mockAnnotation = mock(PastDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试未来的日期时间
        String futureDateTime = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(futureDateTime, null), "未来的日期时间不应该通过验证");
    }

    @Test
    public void testPatternWithoutTimeThrowsException() {
        PastDateTimeValidator validator = new PastDateTimeValidator();

        PastDateTime mockAnnotation = mock(PastDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        // 应该抛出异常：pattern 没有时间符号
        assertThrows(IllegalArgumentException.class, () -> {
            validator.initialize(mockAnnotation);
        }, "pattern 不含时间符号应该抛出异常");
    }

    @Test
    public void testCustomPattern() {
        PastDateTimeValidator validator = new PastDateTimeValidator();

        PastDateTime mockAnnotation = mock(PastDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy/MM/dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试自定义格式
        String pastDateTime = "2020/01/15 10:30:00";
        assertTrue(validator.isValid(pastDateTime, null), "自定义格式应该通过验证");
    }

    @Test
    public void testIncludeToday() {
        PastDateTimeValidator validator = new PastDateTimeValidator();

        PastDateTime mockAnnotation = mock(PastDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(true);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试今天的日期时间
        String todayDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(todayDateTime, null), "includeToday=true 时今天应该通过验证");
    }

    @Test
    public void testNullAndEmptyValues() {
        PastDateTimeValidator validator = new PastDateTimeValidator();

        PastDateTime mockAnnotation = mock(PastDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // null 应该通过
        assertTrue(validator.isValid(null, null), "null 应该通过验证");

        // 空字符串应该通过
        assertTrue(validator.isValid("", null), "空字符串应该通过验证");

        // 空白字符串应该通过
        assertTrue(validator.isValid("   ", null), "空白字符串应该通过验证");
    }

    @Test
    public void testInvalidFormat() {
        PastDateTimeValidator validator = new PastDateTimeValidator();

        PastDateTime mockAnnotation = mock(PastDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试格式错误
        assertFalse(validator.isValid("2020-01-15", null), "纯日期格式不应该通过验证");
        assertFalse(validator.isValid("invalid", null), "无效格式不应该通过验证");
    }
}
