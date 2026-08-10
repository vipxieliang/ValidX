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


import io.github.vipxieliang.validx.annotations.StartsWith;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * StartsWith验证器
 * 验证字符串是否以指定的字符串开头
 */
public class StartsWithValidator implements ConstraintValidator<StartsWith, String> {
    protected String startsWith;
    private boolean ignoreCase;

    @Override
    public void initialize(StartsWith constraintAnnotation) {
        initialize(constraintAnnotation.startsWith(), constraintAnnotation.ignoreCase());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param startsWith 起始字符串
     */
    public void initialize(String startsWith) {
        initialize(startsWith, false);
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用，支持忽略大小写）
     *
     * @param startsWith 起始字符串
     * @param ignoreCase 是否忽略大小写
     */
    public void initialize(String startsWith, boolean ignoreCase) {
        this.startsWith = startsWith;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }

        if (ignoreCase) {
            // 忽略大小写比较
            return value.toLowerCase().startsWith(startsWith.toLowerCase());
        } else {
            // 区分大小写比较
            return value.startsWith(startsWith);
        }
    }
}