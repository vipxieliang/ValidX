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


import io.github.vipxieliang.validx.annotations.NotContains;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * NotContains验证器
 * 验证字符串是否不包含指定的子字符串
 *
 * @author vipxieliang
 * @since 2026/08/07
 */
public class NotContainsValidator implements ConstraintValidator<NotContains, String> {
    protected String[] substrings;
    protected boolean ignoreCase;
    protected boolean matchAll;

    @Override
    public void initialize(NotContains constraintAnnotation) {
        initialize(constraintAnnotation.value(), constraintAnnotation.ignoreCase(), constraintAnnotation.matchAll());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param substrings 禁止的子字符串数组
     * @param ignoreCase 是否忽略大小写
     * @param matchAll 是否匹配所有子字符串（true=AND逻辑，全部不包含；false=OR逻辑，至少一个不包含）
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
            // AND 逻辑：必须全都不包含
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

                if (found) {
                    return false; // 只要有一个包含就返回false
                }
            }
            return true; // 所有都不包含才返回true
        } else {
            // OR 逻辑：只要有一个不包含即可
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
                    return true; // 只要有一个不包含就返回true
                }
            }
            return false; // 所有都包含才返回false
        }
    }
}
