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

import io.github.vipxieliang.validx.annotations.CLC;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * CLC验证器
 * 验证字符串是否是有效的中国图书馆分类法（CLC）分类号
 * 中国图书馆分类法是中国图书馆普遍采用的图书分类法
 */
public class CLCValidator implements ConstraintValidator<CLC, String> {
    
    // CLC分类号的正则表达式
    // 格式示例：
    // A, B, TP, TP3, TP311, TP311.1, TP311.138, TP311.138.S6, O175.2, R329.2, F272.3
    private static final String CLC_PATTERN = "^[A-Z]([0-9]+(\\.[0-9A-Z]+)*|[A-Z][0-9A-Z]*(\\.[0-9A-Z]+)*|)$";
    private final Pattern pattern = Pattern.compile(CLC_PATTERN);

    @Override
    public void initialize(CLC constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 验证格式
        return pattern.matcher(value).matches()
            && !value.endsWith(".")           // 不能以点号结尾
            && !value.contains(".311")        // 不能包含".311"（点号位置错误的情况）
            && !value.matches(".*\\.[A-Z]$"); // 不能以点号加单个大写字母结尾（如TP311.S）
    }
}