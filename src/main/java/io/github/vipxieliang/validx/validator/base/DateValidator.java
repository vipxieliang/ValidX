/*
 * Copyright 2025-2026 vipxieliang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Date;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * 日期格式验证器
 * <p>
 * 验证字符串是否符合指定的日期格式（不含时间部分），采用严格验证模式。
 * </p>
 *
 * <p><b>与 DateTimeValidator 的区别：</b>
 * <ul>
 *   <li>DateValidator - 验证纯日期格式，pattern 不能包含时间符号</li>
 *   <li>DateTimeValidator - 验证日期时间格式，pattern 必须包含时间符号</li>
 * </ul>
 *
 * @author vipxieliang
 * @since 1.1.0
 */
public class DateValidator implements ConstraintValidator<Date, String> {

    /**
     * 日期格式化器（严格模式）
     */
    private DateTimeFormatter formatter;

    /**
     * 日期格式模式
     */
    private String pattern;

    /**
     * pattern 验证是否失败
     */
    private boolean patternInvalid = false;

    /**
     * pattern 验证失败的错误消息
     */
    private String patternErrorMessage = null;

    @Override
    public void initialize(Date annotation) {
        this.pattern = annotation.pattern();

        // 验证 pattern 不能包含时间格式符号
        if (BaseDateValidator.containsTimePatternStatic(pattern)) {
            this.patternInvalid = true;
            this.patternErrorMessage = MessageManager.getMessage("io.github.vipxieliang.validx.validator.date.pattern.contains.time");
            return;
        }

        // 只有在 pattern 有效时才创建 formatter
        this.formatter = createStrictFormatter(pattern);
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

        return isValid(value, formatter);
    }

    /**
     * 核心验证逻辑（私有静态方法，代码复用）
     *
     * @param value 待验证的字符串
     * @param formatter 日期格式化器
     * @return true 表示验证通过，false 表示验证失败
     */
    private static boolean isValid(String value, DateTimeFormatter formatter) {
        // null 和空字符串认为有效（由 @NotNull/@NotEmpty 处理）
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        try {
            // 只解析为 LocalDate（纯日期）
            LocalDate.parse(value, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 创建严格模式的格式化器
     *
     * @param pattern 日期格式模式
     * @return 严格模式的 DateTimeFormatter
     */
    private static DateTimeFormatter createStrictFormatter(String pattern) {
        // 将 yyyy 替换为 uuuu 以支持 STRICT 模式
        // yyyy 是 year-of-era，在 STRICT 模式下需要 era 字段
        // uuuu 是 year，可以直接使用
        String strictPattern = pattern.replace("yyyy", "uuuu")
                                     .replace("yy", "uu");

        return DateTimeFormatter.ofPattern(strictPattern, Locale.US)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * 静态验证方法（供链式调用使用）
     *
     * @param value 待验证的字符串
     * @param pattern 日期格式模式
     * @return true 表示验证通过，false 表示验证失败
     */
    public static boolean isValidDateFormat(String value, String pattern) {
        try {
            // 验证 pattern 不能包含时间符号
            if (BaseDateValidator.containsTimePatternStatic(pattern)) {
                throw new IllegalArgumentException(
                    MessageManager.getMessage("io.github.vipxieliang.validx.validator.date.pattern.contains.time")
                );
            }

            DateTimeFormatter formatter = createStrictFormatter(pattern);
            return isValid(value, formatter);
        } catch (Exception e) {
            return false;
        }
    }
}
