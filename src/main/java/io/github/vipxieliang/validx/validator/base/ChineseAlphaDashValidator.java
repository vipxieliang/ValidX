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

import io.github.vipxieliang.validx.annotations.ChineseAlphaDash;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * ChineseAlphaDash验证器
 * 验证字符串是否只包含汉字、字母、数字、下划线(_)和破折号(-)
 */
public class ChineseAlphaDashValidator implements ConstraintValidator<ChineseAlphaDash, String> {
    
    private static final Pattern CHINESE_ALPHA_DASH_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9_-]+$");
    
    @Override
    public void initialize(ChineseAlphaDash constraintAnnotation) {
        // 初始化操作
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        return CHINESE_ALPHA_DASH_PATTERN.matcher(value).matches();
    }
}