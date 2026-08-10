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


import io.github.vipxieliang.validx.annotations.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 密码验证器
 * 验证字符串是否符合密码要求
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private int minLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;

    @Override
    public void initialize(Password constraintAnnotation) {
        initialize(
            constraintAnnotation.minLength(),
            constraintAnnotation.requireUppercase(),
            constraintAnnotation.requireLowercase(),
            constraintAnnotation.requireDigit(),
            constraintAnnotation.requireSpecialChar()
        );
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param minLength 最小长度
     * @param requireUppercase 是否需要大写字母
     * @param requireLowercase 是否需要小写字母
     * @param requireDigit 是否需要数字
     * @param requireSpecialChar 是否需要特殊字符
     */
    public void initialize(int minLength, boolean requireUppercase, boolean requireLowercase,
                          boolean requireDigit, boolean requireSpecialChar) {
        this.minLength = minLength;
        this.requireUppercase = requireUppercase;
        this.requireLowercase = requireLowercase;
        this.requireDigit = requireDigit;
        this.requireSpecialChar = requireSpecialChar;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull/@NotEmpty等其他注解处理
        }

        // 检查长度
        if (value.length() < minLength) {
            return false;
        }

        // 检查是否包含大写字母
        if (requireUppercase && !value.matches(".*[A-Z].*")) {
            return false;
        }

        // 检查是否包含小写字母
        if (requireLowercase && !value.matches(".*[a-z].*")) {
            return false;
        }

        // 检查是否包含数字
        if (requireDigit && !value.matches(".*\\d.*")) {
            return false;
        }

        // 检查是否包含特殊字符
        if (requireSpecialChar && !value.matches(".*[^A-Za-z0-9].*")) {
            return false;
        }

        return true;
    }
}