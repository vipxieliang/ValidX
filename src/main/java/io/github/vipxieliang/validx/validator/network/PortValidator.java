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

package io.github.vipxieliang.validx.validator.network;


import io.github.vipxieliang.validx.annotations.Port;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 端口号验证器
 * 验证字符串是否是有效的端口号
 */
public class PortValidator implements ConstraintValidator<Port, Object> {

    @Override
    public void initialize(Port constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 认为null是有效的，除非有@NotNull注解
        }

        try {
            int port;
            if (value instanceof Number) {
                port = ((Number) value).intValue();
            } else {
                port = Integer.parseInt(String.valueOf(value));
            }

            // 端口号范围是0-65535
            return port >= 0 && port <= 65535;
        } catch (NumberFormatException e) {
            // 空字符串或无效格式会抛出异常，返回false
            // 这与 Hibernate Validator 的 Port 实现一致
            return false;
        }
    }
}