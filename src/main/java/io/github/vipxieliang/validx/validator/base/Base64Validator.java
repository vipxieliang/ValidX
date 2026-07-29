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

import io.github.vipxieliang.validx.annotations.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Base64格式验证器
 * 支持标准Base64和URL-safe Base64格式验证
 *
 * @author vipxieliang
 * @since 1.0.0
 */
public class Base64Validator implements ConstraintValidator<Base64, String> {

    /**
     * 标准Base64格式（带填充）
     * 字符集: A-Z, a-z, 0-9, +, /
     * 填充: =
     * 长度必须是4的倍数
     */
    private static final Pattern STANDARD_BASE64_PATTERN =
        Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");

    /**
     * URL-safe Base64格式（带填充）
     * 字符集: A-Z, a-z, 0-9, -, _
     * 填充: =
     * 长度必须是4的倍数
     */
    private static final Pattern URL_SAFE_BASE64_PATTERN =
        Pattern.compile("^[A-Za-z0-9_-]*={0,2}$");

    /**
     * 是否为URL-safe格式
     */
    private boolean urlSafe;

    /**
     * 是否允许不带填充符
     */
    private boolean allowNoPadding;

    @Override
    public void initialize(Base64 constraintAnnotation) {
        this.urlSafe = constraintAnnotation.urlSafe();
        this.allowNoPadding = constraintAnnotation.allowNoPadding();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null或空字符串由@NotNull/@NotEmpty等注解处理
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // 选择对应的正则表达式
        Pattern pattern = urlSafe ? URL_SAFE_BASE64_PATTERN : STANDARD_BASE64_PATTERN;

        // 基本格式检查
        if (!pattern.matcher(value).matches()) {
            return false;
        }

        // 检查填充符的正确性
        return validatePadding(value);
    }

    /**
     * 验证Base64填充符的正确性
     *
     * @param value Base64字符串
     * @return true表示填充正确，false表示填充错误
     */
    private boolean validatePadding(String value) {
        int length = value.length();

        // 如果允许不带填充，长度可以不是4的倍数
        if (allowNoPadding) {
            // 检查填充符只能出现在末尾
            int paddingIndex = value.indexOf('=');
            if (paddingIndex != -1) {
                // 如果有填充符，必须在末尾，且后面不能有非填充符
                for (int i = paddingIndex; i < length; i++) {
                    if (value.charAt(i) != '=') {
                        return false;
                    }
                }
                // 有填充的情况下，长度必须是4的倍数
                return length % 4 == 0;
            }
            // 无填充符的情况下，长度可以不是4的倍数
            return true;
        }

        // 不允许省略填充的情况下，长度必须是4的倍数
        if (length % 4 != 0) {
            return false;
        }

        // 检查填充符的位置和数量
        int paddingCount = 0;

        for (int i = length - 1; i >= 0 && i >= length - 2; i--) {
            if (value.charAt(i) == '=') {
                paddingCount++;
            } else {
                // 找到非填充符，停止检查
                break;
            }
        }

        // 填充符最多2个
        return paddingCount <= 2;
    }

    /**
     * 静态验证方法，供链式调用使用
     *
     * @param value 待验证的字符串
     * @param urlSafe 是否为URL-safe格式
     * @param allowNoPadding 是否允许不带填充符
     * @return true表示是有效的Base64格式，false表示无效
     */
    public static boolean isValidBase64(String value, boolean urlSafe, boolean allowNoPadding) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        Base64Validator validator = new Base64Validator();
        validator.urlSafe = urlSafe;
        validator.allowNoPadding = allowNoPadding;

        return validator.isValid(value, null);
    }
}
