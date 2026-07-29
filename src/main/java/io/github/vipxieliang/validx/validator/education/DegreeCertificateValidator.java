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

package io.github.vipxieliang.validx.validator.education;

import io.github.vipxieliang.validx.annotations.DegreeCertificate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * DegreeCertificate验证器
 * 学位证书验证器
 */
public class DegreeCertificateValidator implements ConstraintValidator<DegreeCertificate, String> {

    // 普通学位证书格式：16位数字
    private static final Pattern DEGREE_CERTIFICATE_PATTERN = Pattern.compile("^\\d{16}$");
    
    // 特殊学位证书格式：17位（以特定字母开头后跟16位数字）
    private static final Pattern SPECIAL_DEGREE_CERTIFICATE_PATTERN = Pattern.compile("^[CZQTL][A-Z0-9]{16}$");

    @Override
    public void initialize(DegreeCertificate constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合普通学位证书格式（16位数字）
        if (DEGREE_CERTIFICATE_PATTERN.matcher(value).matches()) {
            return true;
        }

        // 检查是否符合特殊学位证书格式（以特定字母开头的17位字符）
        if (SPECIAL_DEGREE_CERTIFICATE_PATTERN.matcher(value).matches()) {
            return true;
        }

        return false;
    }
}