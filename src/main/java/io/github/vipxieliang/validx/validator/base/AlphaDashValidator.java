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

import io.github.vipxieliang.validx.annotations.AlphaDash;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * AlphaDash验证器
 * 验证字符串是否只包含字母、数字、下划线(_)和破折号(-)
 */
public class AlphaDashValidator implements ConstraintValidator<AlphaDash, String> {
    
    private static final Pattern ALPHA_DASH_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    
    @Override
    public void initialize(AlphaDash constraintAnnotation) {
        // 初始化操作
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        return ALPHA_DASH_PATTERN.matcher(value).matches();
    }
}