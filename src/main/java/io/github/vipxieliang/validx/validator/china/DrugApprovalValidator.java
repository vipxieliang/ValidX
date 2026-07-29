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

import io.github.vipxieliang.validx.annotations.DrugApproval;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 药品批准文号验证器
 * 验证字符串是否是有效的中国药品批准文号
 * 药品批准文号是国家药品监督管理部门批准药品生产企业生产药品的文号
 */
public class DrugApprovalValidator implements ConstraintValidator<DrugApproval, String> {
    
    // 药品批准文号的正则表达式
    // 格式示例：
    // 国药准字H20210039 (境内生产药品)
    // 国药准字ZC20171003 (港澳台地区生产药品)
    // 国药准字HJ20233150 (境外生产药品)
    // 国药准字S20210039 (生物制品)
    private static final String DRUG_APPROVAL_PATTERN = 
        "^国药准字[HZSJ][CJ]?\\d{8}$";
    private final Pattern pattern = Pattern.compile(DRUG_APPROVAL_PATTERN);

    @Override
    public void initialize(DrugApproval constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 验证格式
        return pattern.matcher(value).matches();
    }
}