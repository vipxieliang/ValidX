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


import io.github.vipxieliang.validx.annotations.ChinesePassport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 中国护照验证器
 * 专门用于验证中国大陆护照格式
 */
public class ChinesePassportValidator implements ConstraintValidator<ChinesePassport, String> {

    // 普通护照（因私护照）格式：G+8位数字 或 E+8位数字（电子护照）
    private static final Pattern REGULAR_PASSPORT_PATTERN = Pattern.compile("^(G|E)[0-9]{8}$");
    
    // 公务护照格式：S+7位或8位数字
    private static final Pattern SERVICE_PASSPORT_PATTERN = Pattern.compile("^S[0-9]{7,8}$");
    
    // 外交护照格式：D+7位数字
    private static final Pattern DIPLOMATIC_PASSPORT_PATTERN = Pattern.compile("^D[0-9]{7}$");
    
    // 公务普通护照格式：P+7位数字
    private static final Pattern SERVICE_REGULAR_PASSPORT_PATTERN = Pattern.compile("^P[0-9]{7}$");

    @Override
    public void initialize(ChinesePassport constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合任何一种护照格式
        return REGULAR_PASSPORT_PATTERN.matcher(value).matches() ||
               SERVICE_PASSPORT_PATTERN.matcher(value).matches() ||
               DIPLOMATIC_PASSPORT_PATTERN.matcher(value).matches() ||
               SERVICE_REGULAR_PASSPORT_PATTERN.matcher(value).matches();
    }
}