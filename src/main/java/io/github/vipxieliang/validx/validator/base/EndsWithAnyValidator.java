/*
 * Copyright 2025-2026 vipxieliang
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

import io.github.vipxieliang.validx.annotations.EndsWithAny;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * EndsWithAny验证器
 * 验证字符串是否以指定后缀数组中的任意一个结尾
 *
 * @author vipxieliang
 * @since 1.1.1
 */
public class EndsWithAnyValidator implements ConstraintValidator<EndsWithAny, String> {

    private String[] suffixes;
    private boolean ignoreCase;

    @Override
    public void initialize(EndsWithAny constraintAnnotation) {
        initialize(constraintAnnotation.value(), constraintAnnotation.ignoreCase());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param suffixes 后缀数组
     */
    public void initialize(String[] suffixes) {
        initialize(suffixes, false);
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用，支持忽略大小写）
     *
     * @param suffixes 后缀数组
     * @param ignoreCase 是否忽略大小写
     */
    public void initialize(String[] suffixes, boolean ignoreCase) {
        this.suffixes = suffixes != null ? suffixes : new String[0];
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 空值应该由@NotNull等其他注解处理
        if (value == null || value.isEmpty()) {
            return true;
        }

        // 没有可匹配的后缀
        if (suffixes.length == 0) {
            return false;
        }

        // OR 逻辑：任意一个匹配即通过
        for (String suffix : suffixes) {
            if (suffix != null) {
                if (ignoreCase) {
                    // 忽略大小写比较
                    if (value.toLowerCase().endsWith(suffix.toLowerCase())) {
                        return true;
                    }
                } else {
                    // 区分大小写比较
                    if (value.endsWith(suffix)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
