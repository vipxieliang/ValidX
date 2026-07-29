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

import io.github.vipxieliang.validx.annotations.JWT;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * JWT验证器
 * 验证字符串是否是有效的JWT Token格式
 * <p>
 * JWT格式：header.payload.signature
 * 每部分都是Base64URL编码的字符串
 * </p>
 */
public class JWTValidator implements ConstraintValidator<JWT, String> {

    /**
     * Base64URL字符正则表达式
     * 允许的字符：A-Z, a-z, 0-9, -, _
     * 不包含填充符 =
     */
    private static final Pattern BASE64_URL_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    /**
     * JWT格式正则表达式
     * 格式：xxxxx.yyyyy.zzzzz
     * 每部分至少有一个字符
     */
    private static final Pattern JWT_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$"
    );

    @Override
    public void initialize(JWT jwt) {
        // 无需初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull/@NotEmpty等其他注解处理
        }

        // 先用正则快速检查整体格式
        if (!JWT_PATTERN.matcher(value).matches()) {
            return false;
        }

        // 分割成三部分进行详细验证
        String[] parts = value.split("\\.");

        // JWT必须正好有3部分
        if (parts.length != 3) {
            return false;
        }

        // 验证每一部分都是有效的Base64URL编码
        for (String part : parts) {
            if (part.isEmpty() || !BASE64_URL_PATTERN.matcher(part).matches()) {
                return false;
            }
        }

        return true;
    }
}
