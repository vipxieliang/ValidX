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


import io.github.vipxieliang.validx.annotations.ChinesePatent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 专利号验证器
 * 专门用于验证中国大陆专利号格式
 */
public class ChinesePatentValidator implements ConstraintValidator<ChinesePatent, String> {

    // 专利号格式: ZL + 申请号（12位数字）+ 校验位（1位数字或X）= 总共15位
    // 申请号格式：年份(4位) + 类型(1位) + 序号(7位) = 总共12位
    // 类型: 1=发明专利, 2=实用新型, 3=外观设计, 8=PCT发明专利, 9=PCT实用新型
    private static final Pattern PATENT_PATTERN = Pattern.compile("^ZL\\d{12}[0-9X]$");

    @Override
    public void initialize(ChinesePatent constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 检查是否为空
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查基本格式
        if (!PATENT_PATTERN.matcher(value).matches()) {
            return false;
        }

        // 检查专利类型
        char type = value.charAt(6);
        if (type != '1' && type != '2' && type != '3' && type != '8' && type != '9') {
            return false;
        }

        // 检查校验位
        return validateChecksum(value);
    }

    /**
     * 验证专利号的校验位
     * 校验规则：前12位数字分别乘以系数2,3,4,5,6,7,8,9,2,3,4,5，
     * 然后将乘积相加，用和除以11取余数，余数即为校验位
     * 如果余数为10，则校验位为X
     *
     * @param patent 专利号（ZL+12位数字+校验位）
     * @return 是否通过校验
     */
    private boolean validateChecksum(String patent) {
        // 移除ZL前缀
        String digits = patent.substring(2, 14); // 取前12位数字

        // 确保长度正确（应该为12位）
        if (digits.length() != 12) {
            return false;
        }

        // 权重因子
        int[] weights = {2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5};

        // 计算加权和
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * weights[i];
        }

        // 计算校验位
        int remainder = sum % 11;
        String expectedCheckDigit;
        if (remainder == 10) {
            expectedCheckDigit = "X";
        } else {
            expectedCheckDigit = String.valueOf(remainder);
        }

        // 获取实际校验位
        String actualCheckDigit = String.valueOf(patent.charAt(14)); // 第15位是校验位

        // 比较校验位
        return expectedCheckDigit.equals(actualCheckDigit);
    }
}