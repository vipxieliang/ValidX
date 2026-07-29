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

package io.github.vipxieliang.validx.validator.book;

import io.github.vipxieliang.validx.annotations.ORCID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ORCID验证器
 * 验证字符串是否是有效的ORCID（开放研究者与贡献者身份识别码）
 * 支持格式：XXXX-XXXX-XXXX-XXXX (X为0-9或末位为大写X)
 */
public class ORCIDValidator implements ConstraintValidator<ORCID, String> {

    @Override
    public void initialize(ORCID constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 移除所有连字符，只保留数字和可能的X
        String cleanedValue = value.replaceAll("-", "");
        
        // 检查长度是否为16位
        if (cleanedValue.length() != 16) {
            return false;
        }

        // 检查前15位是否都是数字
        for (int i = 0; i < 15; i++) {
            if (!Character.isDigit(cleanedValue.charAt(i))) {
                return false;
            }
        }

        // 最后一位可以是数字或X
        char lastChar = cleanedValue.charAt(15);
        if (!Character.isDigit(lastChar) && lastChar != 'X') {
            return false;
        }

        // 计算校验和（使用MOD 11-2算法）
        int sum = 0;
        for (int i = 0; i < 15; i++) {
            sum = (sum + Character.getNumericValue(cleanedValue.charAt(i))) * 2;
        }
        
        // 处理校验位
        int remainder = sum % 11;
        int checksum = (12 - remainder) % 11;
        
        // 如果校验位是10，则应该用X表示
        if (checksum == 10) {
            return lastChar == 'X';
        } else {
            return checksum == Character.getNumericValue(lastChar);
        }
    }
}