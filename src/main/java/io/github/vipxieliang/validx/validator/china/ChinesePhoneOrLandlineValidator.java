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

import io.github.vipxieliang.validx.annotations.ChinesePhoneOrLandline;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 中国电话号及固话码验证器
 * 支持手机号码和固定电话号码格式验证
 */
public class ChinesePhoneOrLandlineValidator implements ConstraintValidator<ChinesePhoneOrLandline, String> {

    private final ChinesePhoneValidator phoneValidator = new ChinesePhoneValidator();
    private final ChineseLandlineValidator landlineValidator = new ChineseLandlineValidator();

    @Override
    public void initialize(ChinesePhoneOrLandline constraintAnnotation) {
        // 初始化操作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 先尝试手机号码验证，再尝试固定电话验证
        return phoneValidator.isValid(value, null) || landlineValidator.isValid(value, null);
    }
}