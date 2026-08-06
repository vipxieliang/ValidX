package io.github.vipxieliang.validx.validator.base;


import io.github.vipxieliang.validx.annotations.FutureDate;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * FutureDate验证器
 * 验证字符串是否为未来的日期（不包含时间）
 * 支持自定义日期格式
 */
public class FutureDateValidator extends BaseDateValidator<FutureDate> {

    private boolean patternInvalid = false;
    private String patternErrorMessage = null;

    @Override
    public void initialize(FutureDate constraintAnnotation) {
        initialize(constraintAnnotation.includeToday(), constraintAnnotation.pattern());
    }

    /**
     * 重载的初始化方法，用于链式API调用
     * @param includeToday 是否包含今天
     * @param pattern 日期格式
     */
    public void initialize(boolean includeToday, String pattern) {
        this.includeToday = includeToday;
        this.pattern = pattern;

        // 检查：pattern 不能包含时间符号
        if (containsTimePattern(pattern)) {
            this.patternInvalid = true;
            this.patternErrorMessage = MessageManager.getMessage("io.github.vipxieliang.validx.validator.date.pattern.contains.time");
            return;
        }

        // 将 yyyy 替换为 uuuu 以支持严格模式
        String strictPattern = pattern.replace("yyyy", "uuuu")
                                     .replace("yy", "uu");
        this.formatter = DateTimeFormatter.ofPattern(strictPattern);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果 pattern 配置错误，返回验证失败
        if (patternInvalid) {
            if (context != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(patternErrorMessage)
                       .addConstraintViolation();
            }
            return false;
        }

        // 调用父类的验证逻辑
        return super.isValid(value, context);
    }

    @Override
    protected LocalDate parseDate(String value) throws DateTimeParseException {
        // 只解析为 LocalDate（不包含时间）
        return LocalDate.parse(value, formatter);
    }

    @Override
    protected boolean isValidDate(LocalDate date, LocalDate today) {
        if (includeToday) {
            return !date.isBefore(today);
        } else {
            return date.isAfter(today);
        }
    }
}