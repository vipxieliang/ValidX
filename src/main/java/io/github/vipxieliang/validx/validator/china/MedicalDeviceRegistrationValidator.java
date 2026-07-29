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

import io.github.vipxieliang.validx.annotations.MedicalDeviceRegistration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 医疗器械注册证号验证器
 * 验证字符串是否是有效的中国医疗器械注册证号
 * 格式：X1械注X2XXXX3X4XX5XXXX6
 * 其中：
 * X1：注册审批部门所在地的简称
 * X2：注册形式（准、进、许）
 * XXXX3：首次注册年份
 * X4：产品管理类别
 * XX5：产品分类编码
 * XXXX6：首次注册流水号
 */
public class MedicalDeviceRegistrationValidator implements ConstraintValidator<MedicalDeviceRegistration, String> {
    
    // 医疗器械注册证号的正则表达式
    // 格式示例：
    // 国械注准20243010001 (境内第三类医疗器械)
    // 粤械注准20242020002 (境内第二类医疗器械)
    // 国械注进20242030003 (进口医疗器械)
    // 国械注许20242040004 (港澳台地区医疗器械)
    private static final String MEDICAL_DEVICE_REGISTRATION_PATTERN = 
        "^[\\u4e00-\\u9fa5]{1,2}械注[进许准]\\d{4}[123]\\d{2}\\d{4}$";
    private final Pattern pattern = Pattern.compile(MEDICAL_DEVICE_REGISTRATION_PATTERN);

    @Override
    public void initialize(MedicalDeviceRegistration constraintAnnotation) {
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