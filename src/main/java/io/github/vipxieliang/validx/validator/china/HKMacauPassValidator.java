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


import io.github.vipxieliang.validx.annotations.HKMacauPass;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * HKMacauPass验证器
 * 验证字符串是否是港澳居民来往内地通行证（回乡证）格式
 */
public class HKMacauPassValidator implements ConstraintValidator<HKMacauPass, String> {

    // 港澳居民来往内地通行证（回乡证）格式：
    // H/M + 8位数字 + 2位换证次数（首次发证为00，此后依次递增）
    // 例如：H1234567800、M1234567801
    private static final Pattern HK_MACAU_PASS_PATTERN = Pattern.compile("^[HM]\\d{8}\\d{2}$");

    @Override
    public void initialize(HKMacauPass constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合回乡证格式
        return HK_MACAU_PASS_PATTERN.matcher(value).matches();
    }
}