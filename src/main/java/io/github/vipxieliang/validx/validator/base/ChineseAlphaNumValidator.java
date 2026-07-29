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

import io.github.vipxieliang.validx.annotations.ChineseAlphaNum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * ChineseAlphaNum验证器
 * 验证字符串是否只包含汉字、字母和数字
 */
public class ChineseAlphaNumValidator implements ConstraintValidator<ChineseAlphaNum, String> {
    
    private static final Pattern CHINESE_ALPHA_NUM_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9]+$");
    
    @Override
    public void initialize(ChineseAlphaNum constraintAnnotation) {
        // 初始化操作
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        return CHINESE_ALPHA_NUM_PATTERN.matcher(value).matches();
    }
}