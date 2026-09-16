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

import io.github.vipxieliang.validx.annotations.Ascii;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * ASCII 验证器
 * <p>
 * 校验字符串是否只包含 ASCII 字符（Unicode 0x00 ~ 0x7F）。
 * 默认仅允许可打印 ASCII（0x20 ~ 0x7E）；
 * 通过 {@link Ascii#allowControlChar()} 可放行全部 ASCII（含控制字符 0x00~0x1F、0x7F）。
 * </p>
 */
public class AsciiValidator implements ConstraintValidator<Ascii, String> {

    /** 可打印 ASCII：0x20~0x7E（含空格，剔除制表符/换行/回车等 7 个常用控制字符） */
    private static final Pattern PRINTABLE_ASCII_PATTERN = Pattern.compile("^[\\x20-\\x7E]+$");

    /** 完整 ASCII：0x00~0x7F（含所有控制字符） */
    private static final Pattern FULL_ASCII_PATTERN = Pattern.compile("^[\\x00-\\x7F]+$");

    private boolean allowControlChar;

    @Override
    public void initialize(Ascii constraintAnnotation) {
        initialize(constraintAnnotation.allowControlChar());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）。
     *
     * @param allowControlChar 是否允许 ASCII 控制字符（0x00~0x1F、0x7F）
     */
    public void initialize(boolean allowControlChar) {
        this.allowControlChar = allowControlChar;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值放行，由 @NotBlank / @NotNull 处理
        }
        Pattern pattern = allowControlChar ? FULL_ASCII_PATTERN : PRINTABLE_ASCII_PATTERN;
        return pattern.matcher(value).matches();
    }
}