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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * 日期格式验证器
 * <p>
 * 验证字符串是否符合指定的日期格式，采用严格验证模式。
 * 支持所有 DateTimeFormatter 支持的格式，包括：
 * <ul>
 *   <li>纯日期：yyyy-MM-dd, yyyyMMdd</li>
 *   <li>纯时间：HH:mm:ss</li>
 *   <li>日期时间：yyyy-MM-dd HH:mm:ss</li>
 *   <li>年月：yyyy-MM</li>
 *   <li>星期日期：yyyy-'W'ww-e</li>
 *   <li>以及更多自定义格式</li>
 * </ul>
 * </p>
 *
 * @author vipxieliang
 * @since 1.1.0
 */
public class DateValidator implements ConstraintValidator<Date, String> {

    /**
     * 日期时间格式化器（严格模式）
     */
    private DateTimeFormatter formatter;

    @Override
    public void initialize(Date annotation) {
        this.formatter = createStrictFormatter(annotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
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
            // 尝试解析为 LocalDateTime（包含日期和时间）
            LocalDateTime.parse(value, formatter);
            return true;
        } catch (Exception e1) {
            try {
                // 尝试解析为 LocalDate（仅日期）
                LocalDate.parse(value, formatter);
                return true;
            } catch (Exception e2) {
                try {
                    // 尝试解析为 LocalTime（仅时间）
                    LocalTime.parse(value, formatter);
                    return true;
                } catch (Exception e3) {
                    try {
                        // 尝试解析为 YearMonth（年月）
                        YearMonth.parse(value, formatter);
                        return true;
                    } catch (Exception e4) {
                        // 所有解析方式都失败
                        return false;
                    }
                }
            }
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
            DateTimeFormatter formatter = createStrictFormatter(pattern);
            return isValid(value, formatter);
        } catch (Exception e) {
            return false;
        }
    }
}
