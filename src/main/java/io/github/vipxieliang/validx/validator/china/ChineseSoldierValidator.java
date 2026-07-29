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


import io.github.vipxieliang.validx.annotations.ChineseSoldier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 士兵证验证器
 * 专门用于验证中国大陆士兵证格式
 */
public class ChineseSoldierValidator implements ConstraintValidator<ChineseSoldier, String> {

    // 士兵证格式：汉字+字第+7位数字+号
    // 例如：沈字第0100000号
    private static final Pattern SOLDIER_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]+字第\\d{7}号$");

    @Override
    public void initialize(ChineseSoldier constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合士兵证格式
        return SOLDIER_PATTERN.matcher(value).matches();
    }
}