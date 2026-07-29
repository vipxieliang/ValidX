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


import io.github.vipxieliang.validx.annotations.SoftwareCopyright;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 软件著作权登记号验证器
 * 专门用于验证中国大陆软件著作权登记号格式
 */
public class SoftwareCopyrightValidator implements ConstraintValidator<SoftwareCopyright, String> {

    // 软件著作权登记号格式:
    // 1. 软著登字第XXXXXX号 (6-12位数字)
    // 2. 国（版）著登字第XXXXXX号 (6-12位数字)
    // 3. 10-YYYY-XXXXXX (10代表计算机软件著作权登记，YYYY是年份，XXXXXX是顺序号)
    private static final Pattern COPYRIGHT_PATTERN1 = Pattern.compile("^软著登字第\\d{6,12}号$");
    private static final Pattern COPYRIGHT_PATTERN2 = Pattern.compile("^国\\（版\\）著登字第\\d{6,12}号$");
    private static final Pattern COPYRIGHT_PATTERN3 = Pattern.compile("^10-\\d{4}-\\d{6}$");

    @Override
    public void initialize(SoftwareCopyright constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 检查是否为空
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查基本格式
        return COPYRIGHT_PATTERN1.matcher(value).matches()
            || COPYRIGHT_PATTERN2.matcher(value).matches()
            || COPYRIGHT_PATTERN3.matcher(value).matches();
    }
}