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

import io.github.vipxieliang.validx.annotations.Longitude;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * 经度验证器
 * 验证经度值是否在-180到180之间
 * 
 * 经度规则:
 * 1. 经度范围应在-180到180之间（包含边界值）
 * 2. 支持整数和小数格式
 */
public class LongitudeValidator implements ConstraintValidator<Longitude, String> {
    
    // 经度最小值
    private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180");
    
    // 经度最大值
    private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180");
    
    @Override
    public void initialize(Longitude constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        
        try {
            BigDecimal longitude = new BigDecimal(value);
            
            // 检查经度是否在有效范围内
            return longitude.compareTo(MIN_LONGITUDE) >= 0 && longitude.compareTo(MAX_LONGITUDE) <= 0;
        } catch (NumberFormatException e) {
            return false; // 无法解析为数字格式
        }
    }
}