package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.PastDateTime;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * PastDateTime验证器
 * 验证字符串是否是过去的日期时间（包含时间）
 * 支持自定义日期时间格式
 */
public class PastDateTimeValidator extends BaseDateValidator<PastDateTime> {

    private boolean patternInvalid = false;
    private String patternErrorMessage = null;

    @Override
    public void initialize(PastDateTime constraintAnnotation) {
        initialize(constraintAnnotation.includeToday(), constraintAnnotation.pattern());
    }

    /**
     * 重载的初始化方法，用于链式API调用
     * @param includeToday 是否包含今天
     * @param pattern 日期时间格式
     */
    public void initialize(boolean includeToday, String pattern) {
        this.includeToday = includeToday;
        this.pattern = pattern;

        // 检查：pattern 必须包含时间符号
        if (!containsTimePattern(pattern)) {
            this.patternInvalid = true;
            this.patternErrorMessage = MessageManager.getMessage("io.github.vipxieliang.validx.validator.datetime.pattern.missing.time");
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
        // 解析为 LocalDateTime（包含时间）
        LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
        return dateTime.toLocalDate();
    }

    @Override
    protected boolean isValidDate(LocalDate date, LocalDate today) {
        if (includeToday) {
            return !date.isAfter(today);
        } else {
            return date.isBefore(today);
        }
    }
}
