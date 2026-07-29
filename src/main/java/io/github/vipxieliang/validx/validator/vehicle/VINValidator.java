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


import io.github.vipxieliang.validx.annotations.VIN;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * VIN码验证器
 * 验证车辆识别号码(VIN)格式和校验位
 */
public class VINValidator implements ConstraintValidator<VIN, String> {

    // VIN码字符到数值的映射
    private static final Map<Character, Integer> CHAR_TO_NUM = new HashMap<>();
    
    // VIN码各位置的权重
    private static final int[] WEIGHT = {8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2};
    
    // VIN码格式正则表达式（17位，不含I、O、Q）
    private static final Pattern VIN_PATTERN = Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$");

    static {
        // 初始化字符到数值的映射
        CHAR_TO_NUM.put('0', 0);
        CHAR_TO_NUM.put('1', 1);
        CHAR_TO_NUM.put('2', 2);
        CHAR_TO_NUM.put('3', 3);
        CHAR_TO_NUM.put('4', 4);
        CHAR_TO_NUM.put('5', 5);
        CHAR_TO_NUM.put('6', 6);
        CHAR_TO_NUM.put('7', 7);
        CHAR_TO_NUM.put('8', 8);
        CHAR_TO_NUM.put('9', 9);
        CHAR_TO_NUM.put('A', 1);
        CHAR_TO_NUM.put('B', 2);
        CHAR_TO_NUM.put('C', 3);
        CHAR_TO_NUM.put('D', 4);
        CHAR_TO_NUM.put('E', 5);
        CHAR_TO_NUM.put('F', 6);
        CHAR_TO_NUM.put('G', 7);
        CHAR_TO_NUM.put('H', 8);
        CHAR_TO_NUM.put('J', 1);
        CHAR_TO_NUM.put('K', 2);
        CHAR_TO_NUM.put('L', 3);
        CHAR_TO_NUM.put('M', 4);
        CHAR_TO_NUM.put('N', 5);
        CHAR_TO_NUM.put('P', 7);
        CHAR_TO_NUM.put('R', 9);
        CHAR_TO_NUM.put('S', 2);
        CHAR_TO_NUM.put('T', 3);
        CHAR_TO_NUM.put('U', 4);
        CHAR_TO_NUM.put('V', 5);
        CHAR_TO_NUM.put('W', 6);
        CHAR_TO_NUM.put('X', 7);
        CHAR_TO_NUM.put('Y', 8);
        CHAR_TO_NUM.put('Z', 9);
    }

    @Override
    public void initialize(VIN constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 检查是否为空
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查基本格式
        if (!VIN_PATTERN.matcher(value).matches()) {
            return false;
        }

        // 验证校验位（第9位）
        return validateCheckDigit(value);
    }

    /**
     * 验证VIN码的校验位
     * @param vin VIN码
     * @return 校验位是否正确
     */
    private boolean validateCheckDigit(String vin) {
        int sum = 0;
        
        // 计算前8位和后8位的加权和（跳过第9位校验位）
        for (int i = 0; i < 17; i++) {
            if (i == 8) continue; // 跳过校验位
            
            char c = vin.charAt(i);
            Integer numValue = CHAR_TO_NUM.get(c);
            if (numValue == null) {
                return false; // 不应该发生，因为前面已经通过正则验证
            }
            
            sum += numValue * WEIGHT[i];
        }
        
        // 计算校验位
        int checkDigit = sum % 11;
        char expectedCheckChar;
        if (checkDigit == 10) {
            expectedCheckChar = 'X';
        } else {
            expectedCheckChar = (char) ('0' + checkDigit);
        }
        
        // 获取VIN码中的校验位
        char actualCheckChar = vin.charAt(8);
        
        // 比较校验位
        return expectedCheckChar == actualCheckChar;
    }
}