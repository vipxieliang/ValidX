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

import io.github.vipxieliang.validx.annotations.ISSN;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ISSN验证器
 * 验证字符串是否是有效的ISSN（国际标准连续出版物号）
 * 支持格式：XXXX-XXXX 或 XXXXXXXX
 */
public class ISSNValidator implements ConstraintValidator<ISSN, String> {

    @Override
    public void initialize(ISSN constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 移除所有连字符，只保留数字和可能的X
        String cleanedValue = value.replaceAll("-", "");

        // 检查长度是否为8位
        if (cleanedValue.length() != 8) {
            return false;
        }

        // 检查前7位是否都是数字
        for (int i = 0; i < 7; i++) {
            if (!Character.isDigit(cleanedValue.charAt(i))) {
                return false;
            }
        }

        // 最后一位可以是数字或X
        char lastChar = cleanedValue.charAt(7);
        if (!Character.isDigit(lastChar) && lastChar != 'X') {
            return false;
        }

        // 计算加权和
        int sum = 0;
        for (int i = 0; i < 7; i++) {
            sum += Character.getNumericValue(cleanedValue.charAt(i)) * (8 - i);
        }

        // 处理校验位
        int checksum = (lastChar == 'X') ? 10 : Character.getNumericValue(lastChar);
        sum += checksum;

        // 如果总和能被11整除，则为有效的ISSN
        return sum % 11 == 0;
    }
}