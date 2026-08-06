package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.FutureDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 FutureDate 验证器 - 只接受纯日期格式
 */
public class FutureDateValidatorPatternCheckTest {

    @Test
    public void testPatternWithTimeThrowsException() {
        FutureDateValidator validator = new FutureDateValidator();

        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd HH:mm:ss");

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
            "pattern 包含时间符号应该验证失败");
    }

    @Test
    public void testValidDateOnlyPattern() {
        FutureDateValidator validator = new FutureDateValidator();

        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        // 不应该抛出异常
        assertDoesNotThrow(() -> {
            validator.initialize(mockAnnotation);
        }, "纯日期格式不应该抛出异常");

        // 测试验证
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(validator.isValid(futureDate, null), "未来的日期应该通过验证");
    }

    @Test
    public void testDateTimeInputShouldFail() {
        FutureDateValidator validator = new FutureDateValidator();

        FutureDate mockAnnotation = mock(FutureDate.class);
        when(mockAnnotation.includeToday()).thenReturn(false);
        when(mockAnnotation.pattern()).thenReturn("yyyy-MM-dd");

        validator.initialize(mockAnnotation);

        // 输入带时间的字符串应该失败
        String dateTimeInput = "2099-12-31 23:59:59";
        assertFalse(validator.isValid(dateTimeInput, null), "带时间的输入不应该通过验证");
    }
}
