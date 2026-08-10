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

import io.github.vipxieliang.validx.annotations.UUID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * UUID 验证器
 * 验证字符串是否为有效的 UUID 格式
 */
public class UUIDValidator implements ConstraintValidator<UUID, String> {

    /**
     * UUID 标准格式正则表达式（带连字符）
     * 格式：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     * 示例：550e8400-e29b-41d4-a716-446655440000
     */
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    /**
     * UUID 不带连字符格式正则表达式
     * 格式：xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * 示例：550e8400e29b41d4a716446655440000
     */
    private static final Pattern UUID_WITHOUT_HYPHENS_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{32}$"
    );

    private boolean allowWithoutHyphens;

    @Override
    public void initialize(UUID constraintAnnotation) {
        initialize(constraintAnnotation.allowWithoutHyphens());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param allowWithoutHyphens 是否允许不带连字符的格式
     */
    public void initialize(boolean allowWithoutHyphens) {
        this.allowWithoutHyphens = allowWithoutHyphens;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果值为空，则视为通过验证（将由@NotNull等其他注解处理）
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // 验证标准格式（带连字符）
        if (UUID_PATTERN.matcher(value).matches()) {
            return true;
        }

        // 如果允许不带连字符的格式，继续验证
        if (allowWithoutHyphens && UUID_WITHOUT_HYPHENS_PATTERN.matcher(value).matches()) {
            return true;
        }

        return false;
    }
}
