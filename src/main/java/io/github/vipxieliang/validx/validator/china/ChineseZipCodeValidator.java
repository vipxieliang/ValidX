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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.ChineseZipCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * ChineseZipCode验证器
 * 验证字符串是否是有效的中国邮政编码（6位数字）
 */
public class ChineseZipCodeValidator implements ConstraintValidator<ChineseZipCode, String> {
    
    // 中国邮政编码正则表达式（6位数字）
    private static final String ZIP_CODE_PATTERN = "^\\d{6}$";
    
    private final Pattern pattern = Pattern.compile(ZIP_CODE_PATTERN);

    @Override
    public void initialize(ChineseZipCode constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 使用正则表达式验证邮政编码格式
        return pattern.matcher(value).matches();
    }
}