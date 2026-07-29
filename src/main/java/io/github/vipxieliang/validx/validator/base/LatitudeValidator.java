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

import io.github.vipxieliang.validx.annotations.Latitude;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * 纬度验证器
 * 验证纬度值是否在-90到90之间
 * 
 * 纬度规则:
 * 1. 纬度范围应在-90到90之间（包含边界值）
 * 2. 支持整数和小数格式
 */
public class LatitudeValidator implements ConstraintValidator<Latitude, String> {
    
    // 纬度最小值
    private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90");
    
    // 纬度最大值
    private static final BigDecimal MAX_LATITUDE = new BigDecimal("90");
    
    @Override
    public void initialize(Latitude constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        
        try {
            BigDecimal latitude = new BigDecimal(value);
            
            // 检查纬度是否在有效范围内
            return latitude.compareTo(MIN_LATITUDE) >= 0 && latitude.compareTo(MAX_LATITUDE) <= 0;
        } catch (NumberFormatException e) {
            return false; // 无法解析为数字格式
        }
    }
}