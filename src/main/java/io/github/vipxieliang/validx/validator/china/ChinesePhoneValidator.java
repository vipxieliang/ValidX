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

import io.github.vipxieliang.validx.annotations.ChinesePhone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 中国手机号码验证器
 * 专门用于验证中国大陆手机号码格式
 */
public class ChinesePhoneValidator implements ConstraintValidator<ChinesePhone, String> {

    /**
     * 手机号码正则表达式
     * 支持中国移动、中国联通、中国电信的主要号段
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
        "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$"
    );

    @Override
    public void initialize(ChinesePhone constraintAnnotation) {
        // 初始化操作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 去除空格和横线后进行验证
        String cleanValue = value.replaceAll("[\\s-]", "");
        
        // 验证手机号码
        return cleanValue.length() == 11 && MOBILE_PATTERN.matcher(cleanValue).matches();
    }
}