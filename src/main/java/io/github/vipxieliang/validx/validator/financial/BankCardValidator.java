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

import io.github.vipxieliang.validx.annotations.BankCard;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 银行卡号验证器
 * 使用Luhn算法验证银行卡号的有效性
 */
public class BankCardValidator implements ConstraintValidator<BankCard, String> {

    @Override
    public void initialize(BankCard constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 移除所有空格和连字符
        String cleanValue = value.replaceAll("[\\s-]", "");
        
        // 检查是否全部为数字
        if (!cleanValue.matches("\\d+")) {
            return false;
        }
        
        // 检查长度是否符合银行卡号规范（通常为13-19位）
        if (cleanValue.length() < 13 || cleanValue.length() > 19) {
            return false;
        }
        
        // 使用Luhn算法验证银行卡号
        return isLuhnValid(cleanValue);
    }
    
    /**
     * 使用Luhn算法验证银行卡号
     * @param cardNumber 银行卡号
     * @return 是否有效
     */
    private boolean isLuhnValid(String cardNumber) {
        int sum = 0;
        boolean isEven = false;
        
        // 从右向左遍历
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (isEven) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            isEven = !isEven;
        }
        
        return sum % 10 == 0;
    }
}