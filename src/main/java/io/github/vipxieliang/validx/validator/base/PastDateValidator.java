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

import io.github.vipxieliang.validx.annotations.PastDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * PastDate验证器
 * 验证字符串是否是过去时间
 */
public class PastDateValidator implements ConstraintValidator<PastDate, String> {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    
    private boolean includeToday = false;

    @Override
    public void initialize(PastDate constraintAnnotation) {
        // 初始化逻辑，获取includeToday参数值
        this.includeToday = constraintAnnotation.includeToday();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull/@NotEmpty等其他注解处理
        }

        try {
            // 尝试解析为LocalDateTime (yyyy-MM-dd HH:mm:ss)
            LocalDateTime dateTime = LocalDateTime.parse(value, DATE_TIME_FORMATTER);
            LocalDate date = dateTime.toLocalDate();
            LocalDate today = LocalDate.now();
            if (includeToday) {
                return !date.isAfter(today);
            } else {
                return date.isBefore(today);
            }
        } catch (DateTimeParseException e) {
            try {
                // 尝试解析为LocalDate (yyyy-MM-dd)
                LocalDate date = LocalDate.parse(value, DATE_FORMATTER);
                LocalDate today = LocalDate.now();
                if (includeToday) {
                    return !date.isAfter(today);
                } else {
                    return date.isBefore(today);
                }
            } catch (DateTimeParseException ex) {
                return false;
            }
        }
    }
}