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

import io.github.vipxieliang.validx.annotations.UnifiedSocialCreditCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一社会信用代码校验器
 */
public class UnifiedSocialCreditCodeValidator implements ConstraintValidator<UnifiedSocialCreditCode, String> {
    
    // 字符对应的值映射
    private static final Map<Character, Integer> CODE_MAP = new HashMap<>();
    
    // 加权因子
    private static final int[] WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
    
    // 校验码字符集
    private static final char[] CHECK_CODES = "0123456789ABCDEFGHJKLMNPQRTUWXY".toCharArray();
    
    static {
        // 初始化字符值映射
        for (int i = 0; i < CHECK_CODES.length; i++) {
            CODE_MAP.put(CHECK_CODES[i], i);
        }
    }

    @Override
    public void initialize(UnifiedSocialCreditCode constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 统一社会信用代码应该是18位
        if (value.length() != 18) {
            return false;
        }

        // 检查所有字符是否符合规则
        for (int i = 0; i < 18; i++) {
            char c = value.charAt(i);
            if (!CODE_MAP.containsKey(c)) {
                return false;
            }
        }

        // 计算校验码
        return checkCode(value);
    }
    
    /**
     * 检查校验码是否正确
     * @param value 统一社会信用代码
     * @return 校验码是否正确
     */
    private boolean checkCode(String value) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = value.charAt(i);
            Integer codeValue = CODE_MAP.get(c);
            if (codeValue == null) {
                return false;
            }
            sum += codeValue * WEIGHT[i];
        }
        
        // 计算校验码
        int remainder = sum % 31;
        int checkCodeIndex = 31 - remainder;
        if (checkCodeIndex == 31) {
            checkCodeIndex = 0;
        }
        
        // 获取校验码字符
        char checkChar = CHECK_CODES[checkCodeIndex];
        return checkChar == value.charAt(17);
    }
}