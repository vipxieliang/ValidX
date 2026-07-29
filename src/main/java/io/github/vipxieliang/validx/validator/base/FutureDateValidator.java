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


import io.github.vipxieliang.validx.annotations.FutureDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * FutureDate验证器
 * 验证字符串是否为未来的日期
 */
public class FutureDateValidator implements ConstraintValidator<FutureDate, String> {
    
    private boolean includeToday = false;

    @Override
    public void initialize(FutureDate constraintAnnotation) {
        // 初始化逻辑，获取includeToday参数值
        this.includeToday = constraintAnnotation.includeToday();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果值为null或空字符串，视为有效（可以配合@NotNull/@NotEmpty等其他注解使用）
        if (value == null || value.isEmpty()) {
            return true;
        }

        try {
            // 尝试解析为LocalDate（yyyy-MM-dd格式）
            LocalDate date = LocalDate.parse(value);
            LocalDate today = LocalDate.now();
            if (includeToday) {
                return !date.isBefore(today);
            } else {
                return date.isAfter(today);
            }
        } catch (DateTimeParseException e) {
            try {
                // 尝试解析为LocalDateTime（yyyy-MM-dd HH:mm:ss格式）
                LocalDateTime dateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDate date = dateTime.toLocalDate();
                LocalDate today = LocalDate.now();
                if (includeToday) {
                    return !date.isBefore(today);
                } else {
                    return date.isAfter(today);
                }
            } catch (DateTimeParseException ex) {
                // 如果两种格式都无法解析，则视为无效日期格式
                return false;
            }
        }
    }
}