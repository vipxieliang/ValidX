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


import io.github.vipxieliang.validx.annotations.ChineseTrademark;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 营业执照注册号验证器
 * 专门用于验证中国大陆营业执照注册号格式
 */
public class ChineseTrademarkValidator implements ConstraintValidator<ChineseTrademark, String> {

    // 商标注册号格式: 纯数字形式(7-9位) 或 完整形式(第+数字+号)
    private static final Pattern TRADMARK_PATTERN = Pattern.compile("^(\\d{7,9}|第\\d{7,9}号)$");

    @Override
    public void initialize(ChineseTrademark constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 检查是否为空
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查基本格式
        return TRADMARK_PATTERN.matcher(value).matches();
    }
}