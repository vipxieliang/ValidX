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


import io.github.vipxieliang.validx.annotations.Contains;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Contains验证器
 * 验证字符串是否包含指定的子字符串
 *
 * @author vipxieliang
 * @since 2026/07/31
 */
public class ContainsValidator implements ConstraintValidator<Contains, String> {
    protected String[] substrings;
    protected boolean ignoreCase;
    protected boolean matchAll;

    @Override
    public void initialize(Contains constraintAnnotation) {
        initialize(constraintAnnotation.value(), constraintAnnotation.ignoreCase(), constraintAnnotation.matchAll());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param substrings 要匹配的子字符串数组
     * @param ignoreCase 是否忽略大小写
     * @param matchAll 是否匹配所有子字符串（true=AND逻辑，false=OR逻辑）
     */
    public void initialize(String[] substrings, boolean ignoreCase, boolean matchAll) {
        this.substrings = substrings;
        this.ignoreCase = ignoreCase;
        this.matchAll = matchAll;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }

        if (substrings == null || substrings.length == 0) {
            return true;
        }

        if (matchAll) {
            // AND 逻辑：必须包含所有子字符串
            for (String substring : substrings) {
                if (substring == null || substring.isEmpty()) {
                    continue;
                }

                boolean found;
                if (ignoreCase) {
                    found = value.toLowerCase().contains(substring.toLowerCase());
                } else {
                    found = value.contains(substring);
                }

                if (!found) {
                    return false; // 只要有一个不包含就返回false
                }
            }
            return true; // 所有都包含才返回true
        } else {
            // OR 逻辑：包含任意一个即可
            for (String substring : substrings) {
                if (substring == null || substring.isEmpty()) {
                    continue;
                }

                if (ignoreCase) {
                    if (value.toLowerCase().contains(substring.toLowerCase())) {
                        return true;
                    }
                } else {
                    if (value.contains(substring)) {
                        return true;
                    }
                }
            }
            return false; // 一个都不包含才返回false
        }
    }
}
