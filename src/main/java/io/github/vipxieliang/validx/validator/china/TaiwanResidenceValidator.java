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

import io.github.vipxieliang.validx.annotations.TaiwanResidence;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 台湾居民居住证验证器
 * 专门用于验证中国大陆居民居住证格式
 */
public class TaiwanResidenceValidator implements ConstraintValidator<TaiwanResidence, String> {

    // 台湾居民居住证格式：
    // 830000开头的18位数字，最后一位可能是数字或X
    // 例如：830000000000000001、83000000000000000X
    private static final Pattern TAIWAN_RESIDENCE_PATTERN = Pattern.compile("^830000\\d{11}[0-9Xx]$");

    @Override
    public void initialize(TaiwanResidence constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合台湾居民居住证格式
        return TAIWAN_RESIDENCE_PATTERN.matcher(value).matches();
    }
}