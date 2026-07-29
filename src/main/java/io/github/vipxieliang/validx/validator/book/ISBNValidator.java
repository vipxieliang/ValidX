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

import io.github.vipxieliang.validx.annotations.ISBN;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ISBN验证器
 * 验证字符串是否是有效的ISBN（国际标准书号）
 * 支持10位和13位ISBN格式
 */
public class ISBNValidator implements ConstraintValidator<ISBN, String> {

    @Override
    public void initialize(ISBN constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 移除所有连字符，只保留数字和可能的X
        String cleanedValue = value.replaceAll("-", "");

        // 检查长度
        if (cleanedValue.length() == 10) {
            return isValidISBN10(cleanedValue);
        } else if (cleanedValue.length() == 13) {
            return isValidISBN13(cleanedValue);
        }

        return false;
    }

    /**
     * 验证10位ISBN
     * @param isbn 10位ISBN字符串
     * @return 是否有效
     */
    private boolean isValidISBN10(String isbn) {
        if (isbn.length() != 10) {
            return false;
        }

        // 检查前9位是否都是数字
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(isbn.charAt(i))) {
                return false;
            }
        }

        // 最后一位可以是数字或X
        char lastChar = isbn.charAt(9);
        if (!Character.isDigit(lastChar) && lastChar != 'X') {
            return false;
        }

        // 计算加权和
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(isbn.charAt(i)) * (10 - i);
        }

        // 处理校验位
        int checksum = (lastChar == 'X') ? 10 : Character.getNumericValue(lastChar);
        sum += checksum;

        // 如果总和能被11整除，则为有效的ISBN-10
        return sum % 11 == 0;
    }

    /**
     * 验证13位ISBN
     * @param isbn 13位ISBN字符串
     * @return 是否有效
     */
    private boolean isValidISBN13(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }

        // 检查是否都是数字
        for (int i = 0; i < 13; i++) {
            if (!Character.isDigit(isbn.charAt(i))) {
                return false;
            }
        }

        // 计算加权和
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        // 计算校验位
        int checksum = Character.getNumericValue(isbn.charAt(12));
        int remainder = sum % 10;
        int calculatedChecksum = (remainder == 0) ? 0 : 10 - remainder;

        return checksum == calculatedChecksum;
    }
}