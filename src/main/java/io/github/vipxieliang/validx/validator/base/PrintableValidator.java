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

import io.github.vipxieliang.validx.annotations.Printable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Printable 验证器
 * <p>
 * 验证字符串是否只包含可打印 ASCII 字符（Unicode 0x20 ~ 0x7E）。
 * </p>
 *
 * <p>
 * 33 个被拒绝的控制字符包括：0x00~0x1F（TAB / LF / CR / ESC / ...）以及 0x7F（DEL）。
 * </p>
 *
 * <p>
 * 与 {@link AsciiValidator} 职责互补：{@code AsciiValidator} 放行全部 ASCII（0x00~0x7F，含控制字符），
 * 本验证器则要求字符完全可见（0x20~0x7E）。
 * </p>
 */
public class PrintableValidator implements ConstraintValidator<Printable, String> {

    /** 可打印 ASCII：0x20~0x7E（含空格、剔除全部控制字符） */
    private static final Pattern PRINTABLE_PATTERN = Pattern.compile("^[\\x20-\\x7E]+$");

    @Override
    public void initialize(Printable constraintAnnotation) {
        // 无可配置参数
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值放行，由 @NotBlank / @NotNull 处理
        }
        return PRINTABLE_PATTERN.matcher(value).matches();
    }
}