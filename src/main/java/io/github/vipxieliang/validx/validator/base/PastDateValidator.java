package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.PastDate;
import io.github.vipxieliang.validx.i18n.MessageManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * PastDate验证器
 * 验证字符串是否是过去的日期（不包含时间）
 * 支持自定义日期格式
 */
public class PastDateValidator extends BaseDateValidator<PastDate> {

    @Override
    public void initialize(PastDate constraintAnnotation) {
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
            throw new IllegalArgumentException(
                MessageManager.getMessage("io.github.vipxieliang.validx.validator.date.pattern.contains.time")
            );
        }

        // 将 yyyy 替换为 uuuu 以支持严格模式
        String strictPattern = pattern.replace("yyyy", "uuuu")
                                     .replace("yy", "uu");
        this.formatter = DateTimeFormatter.ofPattern(strictPattern);
    }

    @Override
    protected LocalDate parseDate(String value) throws DateTimeParseException {
        // 只解析为 LocalDate（不包含时间）
        return LocalDate.parse(value, formatter);
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