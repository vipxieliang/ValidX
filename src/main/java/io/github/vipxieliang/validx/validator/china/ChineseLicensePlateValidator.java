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


import io.github.vipxieliang.validx.annotations.ChineseLicensePlate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 中国车牌号验证器
 * 专门用于验证中国大陆车牌号格式
 */
public class ChineseLicensePlateValidator implements ConstraintValidator<ChineseLicensePlate, String> {

    private Pattern pattern7 = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{4}[A-Z0-9挂学警港澳]$");
    private Pattern pattern8 = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z]([0-9]{5}[DF])|([DF][A-HJ-NP-Z0-9][0-9]{4})$");

    @Override
    public void initialize(ChineseLicensePlate licensePlate) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String licensePlate, ConstraintValidatorContext context) {
        if (licensePlate == null || licensePlate.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        if (licensePlate.length() == 7) {
            return pattern7.matcher(licensePlate).matches();
        } else if (licensePlate.length() == 8) {
            return pattern8.matcher(licensePlate).matches();
        } else {
            return false;
        }
    }
}