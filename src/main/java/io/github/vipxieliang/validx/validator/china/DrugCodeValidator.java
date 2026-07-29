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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.DrugCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 药品本位码验证器
 * 验证字符串是否是有效的中国药品本位码
 * 药品本位码是以69开头，20位数字，最后一位为GS1校验位
 */
public class DrugCodeValidator implements ConstraintValidator<DrugCode, String> {
    
    // 药品本位码的正则表达式
    // 格式示例：69开头的20位数字
    private static final String DRUG_CODE_PATTERN = "^69\\d{18}$";
    private final Pattern pattern = Pattern.compile(DRUG_CODE_PATTERN);

    @Override
    public void initialize(DrugCode constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 验证基本格式
        if (!pattern.matcher(value).matches()) {
            return false;
        }

        // 验证GS1校验位
        return isValidGS1CheckDigit(value);
    }
    
    /**
     * 验证GS1校验位
     * @param code 20位药品本位码
     * @return 校验位是否正确
     */
    private boolean isValidGS1CheckDigit(String code) {
        // 提取前19位数字用于计算校验位
        String baseCode = code.substring(0, 19);
        int checkDigit = Character.getNumericValue(code.charAt(19));
        
        // 计算GS1校验位
        int sum = 0;
        for (int i = 0; i < baseCode.length(); i++) {
            int digit = Character.getNumericValue(baseCode.charAt(i));
            // 从右到左，奇数位置权重为3，偶数位置权重为1
            // 由于我们从左到右遍历，位置需要相应调整
            // 第1位(索引0)对应从右数第19位，是奇数位置，权重为3
            // 第2位(索引1)对应从右数第18位，是偶数位置，权重为1
            int weight = (i % 2 == 0) ? 3 : 1;
            sum += digit * weight;
        }
        
        int calculatedCheckDigit = (10 - (sum % 10)) % 10;
        return calculatedCheckDigit == checkDigit;
    }
}