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

import io.github.vipxieliang.validx.annotations.ChineseLandline;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 中国固定电话号码验证器
 * 专门用于验证中国大陆固定电话号码格式
 */
public class ChineseLandlineValidator implements ConstraintValidator<ChineseLandline, String> {

    /**
     * 固定电话号码正则表达式
     * 支持区号-号码格式或者号码格式
     */
    private static final Pattern LANDLINE_PATTERN = Pattern.compile(
        "^(0\\d{2,3}[-\\s]?)?\\d{7,8}([-\\s]\\d{1,6})?$"
    );

    @Override
    public void initialize(ChineseLandline constraintAnnotation) {
        // 初始化操作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 去除空格和横线后进行验证
        String cleanValue = value.replaceAll("[\\s-]", "");
        
        // 检查是否包含非数字字符（除了开头的0）
        if (!cleanValue.matches("^0?\\d+$")) {
            return false;
        }
        
        // 对于固定电话，检查长度是否符合要求
        if (cleanValue.length() >= 10 && cleanValue.length() <= 13 && cleanValue.startsWith("0")) {
            // 如果去除分隔符后符合固定电话长度要求，认为是有效的
            return true;
        }
        
        // 验证固定电话（使用原始值，但允许空格和横线）
        return LANDLINE_PATTERN.matcher(value).matches();
    }
}