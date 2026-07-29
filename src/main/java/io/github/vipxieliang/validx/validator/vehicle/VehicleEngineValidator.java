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

package io.github.vipxieliang.validx.validator.vehicle;


import io.github.vipxieliang.validx.annotations.VehicleEngine;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 车辆发动机编码验证器
 * 验证车辆发动机编码格式
 */
public class VehicleEngineValidator implements ConstraintValidator<VehicleEngine, String> {

    // 发动机编号格式正则表达式
    // 通常由字母和数字组成，长度在6-17位之间
    private static final Pattern ENGINE_NO_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,17}$");

    @Override
    public void initialize(VehicleEngine constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 检查是否为空
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查基本格式
        return ENGINE_NO_PATTERN.matcher(value).matches();
    }
}