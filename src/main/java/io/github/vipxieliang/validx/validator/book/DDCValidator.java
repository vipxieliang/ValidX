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

import io.github.vipxieliang.validx.annotations.DDC;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * DDC验证器
 * 验证字符串是否是有效的杜威十进制分类法（DDC）分类号
 * 杜威十进制分类法是广泛应用于图书馆的分类系统
 */
public class DDCValidator implements ConstraintValidator<DDC, String> {
    
    // DDC分类号的正则表达式
    // 格式示例：
    // 000, 100, 200, ..., 999
    // 000.1, 100.2, 200.33, ..., 999.999
    // 510, 516, 516.3, 330.94, etc.
    private static final String DDC_PATTERN = "^\\d{3}(\\.\\d+)?$";
    private final Pattern pattern = Pattern.compile(DDC_PATTERN);

    @Override
    public void initialize(DDC constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 验证格式
        return pattern.matcher(value).matches();
    }
}