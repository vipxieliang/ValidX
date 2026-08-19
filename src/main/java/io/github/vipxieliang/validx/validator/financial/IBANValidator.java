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

import io.github.vipxieliang.validx.annotations.IBAN;
import io.github.vipxieliang.validx.enums.IbanCountry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * IBAN国际银行账户号码验证器
 * 验证国际银行账户号码(IBAN)的格式和校验位
 */
public class IBANValidator implements ConstraintValidator<IBAN, String> {
    
    // IBAN格式正则表达式: 2个字母的国家代码 + 2位校验码 + 最多30位的BBAN
    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]{0,30}$");

    @Override
    public void initialize(IBAN constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格并转换为大写
        String cleanValue = value.replaceAll("\\s+", "").toUpperCase();
        
        // 基本格式检查
        if (!IBAN_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 国家代码及长度检查
        String countryCode = cleanValue.substring(0, 2);
        Optional<IbanCountry> country = IbanCountry.fromCode(countryCode);
        if (!country.isPresent() || cleanValue.length() != country.get().getLength()) {
            return false;
        }
        
        // 校验位检查 (MOD-97-10算法)
        return validateIBANChecksum(cleanValue);
    }
    
    /**
     * 使用MOD-97-10算法验证IBAN校验位
     * @param iban IBAN字符串
     * @return 校验结果
     */
    private boolean validateIBANChecksum(String iban) {
        // 将前4位移到末尾
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        
        // 将字母转换为数字 (A=10, B=11, ..., Z=35)
        StringBuilder numericString = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numericString.append((int) c - 'A' + 10);
            } else {
                numericString.append(c);
            }
        }
        
        // 使用MOD-97算法计算校验结果
        return mod97(numericString.toString()) == 1;
    }
    
    /**
     * MOD-97算法实现
     * @param input 数字字符串
     * @return 余数
     */
    private int mod97(String input) {
        int remainder = 0;
        // 分块处理以避免大数问题
        for (int i = 0; i < input.length(); i += 7) {
            int endIndex = Math.min(i + 7, input.length());
            String chunk = input.substring(i, endIndex);
            // 根据块的长度计算乘数
            int power = endIndex - i;
            long multiplier = (long) Math.pow(10, power);
            remainder = (int) ((remainder * multiplier + Long.parseLong(chunk)) % 97);
        }
        return remainder;
    }
}