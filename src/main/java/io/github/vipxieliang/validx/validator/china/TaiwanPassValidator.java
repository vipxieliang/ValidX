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


import io.github.vipxieliang.validx.annotations.TaiwanPass;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 台胞证验证器
 * 专门用于验证中国大陆台胞证格式
 */
public class TaiwanPassValidator implements ConstraintValidator<TaiwanPass, String> {

    // 台湾居民来往大陆通行证（台胞证）格式：
    // 8位阿拉伯数字 + 2位签发次数
    // 例如：1234567800、1234567801
    private static final Pattern TAIWAN_PASS_PATTERN = Pattern.compile("^\\d{8}\\d{2}$");

    @Override
    public void initialize(TaiwanPass constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合台胞证格式
        return TAIWAN_PASS_PATTERN.matcher(value).matches();
    }
}