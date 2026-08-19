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

package io.github.vipxieliang.validx.validator.certification;

import io.github.vipxieliang.validx.annotations.PMP;
import io.github.vipxieliang.validx.enums.PMPPrefix;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * PMP证书验证器
 * 验证PMP（Project Management Professional）证书编号格式
 * 
 * PMP证书编号规则:
 * 1. 通常由英文字母+数字组成
 * 2. 前缀字母表示PMP认证的类型，如"PMP"代表项目管理专业人士认证
 * 3. 在某些情况下，可能包含特定机构的标识，如"CITEF"
 * 4. 证书序号由一组数字组成，通常为6-8位数字
 * 5. 特殊情况：也可以是7位纯数字格式
 * 6. 整体格式为：前缀(2-6个大写字母)+数字(6-8位) 或 7位纯数字
 */
public class PMPValidator implements ConstraintValidator<PMP, String> {
    
    // PMP证书编号格式: 7位纯数字 或 前缀(2-6个大写字母)+数字(6-8位)
    private static final Pattern PMP_PATTERN = Pattern.compile("^\\d{7}$|^[A-Z]{2,6}\\d{6,8}$");
    
    @Override
    public void initialize(PMP constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格
        String cleanValue = value.replaceAll("\\s+", "");
        
        // 验证是否符合PMP证书编号格式
        if (!PMP_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 如果是7位纯数字格式，直接返回true
        if (cleanValue.matches("^\\d{7}$")) {
            return true;
        }
        
        // 如果是带前缀的格式，验证前缀是否有效
        for (PMPPrefix prefix : PMPPrefix.values()) {
            if (cleanValue.startsWith(prefix.getValue())) {
                return true;
            }
        }
        
        // 如果没有匹配的有效前缀，则验证失败
        return false;
    }
}