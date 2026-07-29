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

package io.github.vipxieliang.validx.validator.phone;

import io.github.vipxieliang.validx.annotations.IMEI;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * IMEI验证器
 * 验证字符串是否为有效的IMEI号码
 */
public class IMEIValidator implements ConstraintValidator<IMEI, String> {

    @Override
    public void initialize(IMEI constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }

        // 移除可能存在的分隔符（空格、连字符等）
        String cleanIMEI = value.replaceAll("[\\s\\-]+", "");

        // 检查长度是否为15位或17位
        if (cleanIMEI.length() != 15 && cleanIMEI.length() != 17) {
            return false;
        }

        // 检查是否全为数字
        if (!cleanIMEI.matches("\\d+")) {
            return false;
        }

        // 如果是17位，只需要验证前15位
        String imeiToCheck = cleanIMEI.substring(0, 15);

        // 使用Luhn算法验证校验位
        return isValidIMEI(imeiToCheck);
    }

    /**
     * 使用Luhn算法验证IMEI
     * @param imei 15位IMEI字符串
     * @return 是否有效
     */
    private boolean isValidIMEI(String imei) {
        if (imei.length() != 15) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 14; i++) {
            int digit = Character.getNumericValue(imei.charAt(i));
            
            // 奇数位置（从0开始计数，所以是偶数索引）
            if (i % 2 == 0) {
                sum += digit;
            } else {
                // 偶数位置（奇数索引），需要乘以2
                int doubled = digit * 2;
                // 如果结果是两位数，则相加各位数字
                sum += (doubled / 10) + (doubled % 10);
            }
        }

        // 计算校验位
        int checkDigit = (10 - (sum % 10)) % 10;
        
        // 比较计算出的校验位与实际的校验位
        return checkDigit == Character.getNumericValue(imei.charAt(14));
    }
}