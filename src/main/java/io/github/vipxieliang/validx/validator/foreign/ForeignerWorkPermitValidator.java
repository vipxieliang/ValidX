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

package io.github.vipxieliang.validx.validator.foreign;

import io.github.vipxieliang.validx.annotations.ForeignerWorkPermit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 外国人工作许可证验证器
 * 验证字符串是否符合外国人工作许可证的格式
 */
public class ForeignerWorkPermitValidator implements ConstraintValidator<ForeignerWorkPermit, String> {

    // 外国人工作许可证号码格式：至少包含字母和数字的组合
    private static final Pattern FOREIGNER_WORK_PERMIT_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,20}$");

    @Override
    public void initialize(ForeignerWorkPermit constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合外国人工作许可证基本格式
        return FOREIGNER_WORK_PERMIT_PATTERN.matcher(value).matches();
    }
}