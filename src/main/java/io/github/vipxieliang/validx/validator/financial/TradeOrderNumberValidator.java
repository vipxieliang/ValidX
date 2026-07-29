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

package io.github.vipxieliang.validx.validator.financial;

import io.github.vipxieliang.validx.annotations.TradeOrderNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 交易订单号验证器
 * 验证金融交易订单号的格式
 * 
 * 支持的格式:
 * 1. 以T开头，后跟18位数字的格式 (如: T202510171234567890)
 * 2. 纯18位数字格式 (如: 202510171234567890)
 * 3. UUID格式 (如: 550e8400-e29b-41d4-a716-446655440000)
 */
public class TradeOrderNumberValidator implements ConstraintValidator<TradeOrderNumber, String> {
    
    // T开头+18位数字格式
    private static final Pattern PREFIX_T_PATTERN = Pattern.compile("^T\\d{18}$");
    
    // 纯18位数字格式
    private static final Pattern DIGITS_18_PATTERN = Pattern.compile("^\\d{18}$");
    
    // UUID格式（带连字符）
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    
    // UUID格式（不带连字符）
    private static final Pattern UUID_NO_HYPHEN_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");
    
    @Override
    public void initialize(TradeOrderNumber constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格和连字符
        String cleanValue = value.replaceAll("[\\s-]+", "");
        
        // 验证是否匹配任一支持的格式
        return PREFIX_T_PATTERN.matcher(cleanValue).matches() || 
               DIGITS_18_PATTERN.matcher(cleanValue).matches() || 
               UUID_PATTERN.matcher(cleanValue).matches() ||
               UUID_NO_HYPHEN_PATTERN.matcher(cleanValue).matches();
    }
}