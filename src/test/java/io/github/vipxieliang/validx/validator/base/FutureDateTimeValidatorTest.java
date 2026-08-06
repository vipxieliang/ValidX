package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.FutureDateTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 FutureDateTime 验证器
 */
public class FutureDateTimeValidatorTest {

    @Test
    public void testValidFutureDateTime() {
        FutureDateTimeValidator validator = new FutureDateTimeValidator();

        FutureDateTime mockAnnotation = mock(FutureDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试未来的日期时间
        String futureDateTime = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(futureDateTime, null), "未来的日期时间应该通过验证");
    }

    @Test
    public void testInvalidPastDateTime() {
        FutureDateTimeValidator validator = new FutureDateTimeValidator();

        FutureDateTime mockAnnotation = mock(FutureDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试过去的日期时间
        String pastDateTime = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(pastDateTime, null), "过去的日期时间不应该通过验证");
    }

    @Test
    public void testPatternWithoutTimeThrowsException() {
        FutureDateTimeValidator validator = new FutureDateTimeValidator();

        FutureDateTime mockAnnotation = mock(FutureDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        // 不应该抛出异常，而是在验证时返回 false
        assertDoesNotThrow(() -> {
            validator.initialize(mockAnnotation);
        }, "初始化时不应该抛出异常");

        // 验证时应该返回 false（pattern 配置错误）
        javax.validation.ConstraintValidatorContext mockContext = mock(javax.validation.ConstraintValidatorContext.class);
        javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder =
            mock(javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.addConstraintViolation()).thenReturn(mockContext);

        assertFalse(validator.isValid("2099-12-31", mockContext),
            "pattern 不含时间符号应该验证失败");
    }

    @Test
    public void testCustomPattern() {
        FutureDateTimeValidator validator = new FutureDateTimeValidator();

        FutureDateTime mockAnnotation = mock(FutureDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy/MM/dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试自定义格式
        String futureDateTime = "2099/12/31 23:59:59";
        assertTrue(validator.isValid(futureDateTime, null), "自定义格式应该通过验证");
    }

    @Test
    public void testIncludeToday() {
        FutureDateTimeValidator validator = new FutureDateTimeValidator();

        FutureDateTime mockAnnotation = mock(FutureDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(true);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试今天的日期时间
        String todayDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(todayDateTime, null), "includeToday=true 时今天应该通过验证");
    }

    @Test
    public void testNullAndEmptyValues() {
        FutureDateTimeValidator validator = new FutureDateTimeValidator();

        FutureDateTime mockAnnotation = mock(FutureDateTime.class);
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
        FutureDateTimeValidator validator = new FutureDateTimeValidator();

        FutureDateTime mockAnnotation = mock(FutureDateTime.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

        validator.initialize(mockAnnotation);

        // 测试格式错误
        assertFalse(validator.isValid("2099-12-31", null), "纯日期格式不应该通过验证");
        assertFalse(validator.isValid("invalid", null), "无效格式不应该通过验证");
    }
}
