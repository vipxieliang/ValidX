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

package io.github.vipxieliang.validx.validator.network;

import io.github.vipxieliang.validx.annotations.SubnetMask;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * 子网掩码验证器
 * 验证字符串是否是有效的子网掩码
 */
public class SubnetMaskValidator implements ConstraintValidator<SubnetMask, String> {
    
    // 有效的子网掩码段
    private static final List<Integer> VALID_OCTETS = Arrays.asList(
        255, 254, 252, 248, 240, 224, 192, 128, 0
    );

    @Override
    public void initialize(SubnetMask constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }

        return isValidSubnetMask(value);
    }
    
    /**
     * 验证子网掩码是否有效
     * @param subnetMask 子网掩码字符串
     * @return 是否有效
     */
    private boolean isValidSubnetMask(String subnetMask) {
        String[] parts = subnetMask.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        // 检查每一部分是否是有效的数字
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        // 将子网掩码转换为32位二进制字符串
        StringBuilder binary = new StringBuilder();
        for (String part : parts) {
            int num = Integer.parseInt(part);
            String binaryPart = String.format("%8s", Integer.toBinaryString(num)).replace(' ', '0');
            binary.append(binaryPart);
        }
        
        // 检查是否是连续的1后跟连续的0
        String binaryString = binary.toString();
        int firstZeroIndex = binaryString.indexOf('0');
        if (firstZeroIndex == -1) {
            // 全为1的情况，有效
            return true;
        }
        
        // 检查从第一个0开始后面是否全为0
        String suffix = binaryString.substring(firstZeroIndex);
        return !suffix.contains("1");
    }
}