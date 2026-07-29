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

package io.github.vipxieliang.validx.validator.book;

import io.github.vipxieliang.validx.annotations.DOI;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * DOI验证器
 * 验证字符串是否是有效的DOI（数字对象标识符）
 * 用于数字资源的唯一标识，广泛用于学术出版物
 */
public class DOIValidator implements ConstraintValidator<DOI, String> {

    @Override
    public void initialize(DOI constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 转换为小写进行统一处理
        String doi = value.trim();

        // 如果以"doi:"开头，则移除该前缀（大小写不敏感）
        if (doi.toLowerCase().startsWith("doi:")) {
            doi = doi.substring(4).trim();
        }

        // 检查是否以"10."开头
        if (!doi.startsWith("10.")) {
            return false;
        }

        // 必须包含斜杠分隔符
        if (!doi.contains("/")) {
            return false;
        }

        // 分割前缀和后缀
        String[] parts = doi.split("/", 2);
        if (parts.length != 2) {
            return false;
        }

        String prefix = parts[0];
        String suffix = parts[1];

        // 验证前缀（以10.开头，后面跟数字和点）
        if (!prefix.matches("^10\\.\\d+(\\.\\d+)*$")) {
            return false;
        }

        // 验证后缀（不能为空）
        if (suffix.isEmpty()) {
            return false;
        }

        // DOI后缀不能以"."结尾
        if (suffix.endsWith(".")) {
            return false;
        }

        return true;
    }
}