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

import io.github.vipxieliang.validx.annotations.Color;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * <p>
 * 颜色验证器实现类
 * 仅验证HEX颜色格式（如 #FF0000 或 #F00）
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/13
 */
public class ColorValidator implements ConstraintValidator<Color, String> {

    /**
     * HEX颜色格式的正则表达式
     * 支持 #FFF 或 #FFFFFF 格式
     */
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile(
            "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
    );

    @Override
    public void initialize(Color constraintAnnotation) {
        // 初始化操作，此处无需特殊处理
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果值为null或空，则认为有效（可以使用@NotNull/@NotEmpty进行非空验证）
        if (value == null || value.isEmpty()) {
            return true;
        }

        // 检查是否匹配HEX颜色格式
        return HEX_COLOR_PATTERN.matcher(value).matches();
    }
}