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


import io.github.vipxieliang.validx.annotations.WorkCopyright;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * WorkCopyright验证器
 * 验证字符串是否是标准的作品著作权登记号格式
 */
public class WorkCopyrightValidator implements ConstraintValidator<WorkCopyright, String> {

    // 一般作品著作权登记号格式:
    // 作登字(地区编号)-(年代)-(作品分类号)-(顺序号)号
    // 例如: 作登字22-2023-A-0018号
    private static final Pattern WORK_COPYRIGHT_PATTERN = Pattern.compile("^作登字(\\d{1,4})-(\\d{4})-([A-Z])-([0-9]{1,6})号$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 检查是否为空
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查基本格式
        return WORK_COPYRIGHT_PATTERN.matcher(value).matches();
    }
}