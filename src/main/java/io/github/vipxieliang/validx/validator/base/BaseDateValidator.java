/*
 * Copyright 2025-2025 vipxieliang
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期验证器基类
 * 提供日期验证的通用逻辑
 *
 * @param <A> 注解类型
 */
public abstract class BaseDateValidator<A extends Annotation> implements ConstraintValidator<A, String> {

    protected DateTimeFormatter formatter;
    protected boolean includeToday;
    protected String pattern;

    /**
     * 验证字符串是否符合日期要求
     *
     * @param value 待验证的字符串
     * @param context 验证上下文
     * @return true 如果验证通过
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 空值应该由@NotNull/@NotEmpty等其他注解处理
        }

        try {
            LocalDate date = parseDate(value);
            LocalDate today = LocalDate.now();

            return isValidDate(date, today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 解析日期字符串
     * 子类实现具体的解析逻辑（LocalDate 或 LocalDateTime）
     *
     * @param value 日期字符串
     * @return LocalDate 对象
     * @throws DateTimeParseException 如果解析失败
     */
    protected abstract LocalDate parseDate(String value) throws DateTimeParseException;

    /**
     * 验证日期是否满足要求
     * 子类实现具体的验证逻辑（过去或未来）
     *
     * @param date 解析后的日期
     * @param today 当前日期
     * @return true 如果日期满足要求
     */
    protected abstract boolean isValidDate(LocalDate date, LocalDate today);

    /**
     * 检查 pattern 中是否包含时间相关的格式符号
     * 排除单引号内的字面量
     *
     * @param pattern 日期格式 pattern
     * @return true 如果包含时间格式符号
     */
    protected boolean containsTimePattern(String pattern) {
        boolean inQuote = false;

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            // 处理单引号
            if (c == '\'') {
                // 检查是否是转义的单引号 ''
                if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
                    i++; // 跳过转义的单引号
                } else {
                    inQuote = !inQuote;
                }
                continue;
            }

            // 如果在引号外，检查是否是时间相关符号
            if (!inQuote) {
                // 检查所有时间相关的格式符号
                // H, h, K, k - 小时
                // m - 分钟
                // s, S - 秒/毫秒
                // a - AM/PM
                // A, n, N - 毫秒/纳秒
                if (c == 'H' || c == 'h' || c == 'K' || c == 'k' ||
                    c == 'm' ||
                    c == 's' || c == 'S' ||
                    c == 'a' ||
                    c == 'A' || c == 'n' || c == 'N') {
                    return true;
                }
            }
        }

        return false;
    }
}
