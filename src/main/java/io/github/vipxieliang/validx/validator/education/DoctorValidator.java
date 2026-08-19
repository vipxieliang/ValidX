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

package io.github.vipxieliang.validx.validator.education;

import io.github.vipxieliang.validx.annotations.Doctor;
import io.github.vipxieliang.validx.enums.ChinaMainlandProvince;
import io.github.vipxieliang.validx.enums.DoctorCategory;
import io.github.vipxieliang.validx.enums.DoctorLevel;
import io.github.vipxieliang.validx.validator.china.ChineseIdCardValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 医师资格证验证器
 * 验证医师资格证编号的格式
 * 
 * 医师资格证编号规则:
 * 1. 通常由24位或27位字符组成（数字，最后一位身份证号可能包含X）
 * 2. 前4位: 年度代码 (取得医师资格证书年度)
 * 3. 第5-6位: 省、自治区、直辖市代码
 * 4. 第7位: 执业医师级别代码
 * 5. 第8-9位: 执业医师类别代码
 * 6. 第10-24位(或27位): 居民或公民身份证代码（可能包含X）
 */
public class DoctorValidator implements ConstraintValidator<Doctor, String> {
    
    // 医师资格证编号格式: 24位或27位（前23/26位为数字，最后1位可以是数字或X）
    private static final Pattern DOCTOR_QUALIFICATION_PATTERN = Pattern.compile("^\\d{23}[\\dX]$|^\\d{26}[\\dX]$");
    
    @Override
    public void initialize(Doctor constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格
        String cleanValue = value.replaceAll("\\s+", "");
        
        // 验证是否为24位或27位（前23/26位为数字，最后1位可以是数字或X）
        if (!DOCTOR_QUALIFICATION_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 提取各部分进行详细验证
        String yearCode = cleanValue.substring(0, 4);         // 年度代码
        String provinceCode = cleanValue.substring(4, 6);     // 省级行政区代码
        String levelCode = cleanValue.substring(6, 7);        // 执业医师级别代码
        String categoryCode = cleanValue.substring(7, 9);     // 执业医师类别代码
        
        // 验证年度代码 (应该是一个合理的年份，不能是未来年份)
        try {
            int year = Integer.parseInt(yearCode);
            int currentYear = java.time.Year.now().getValue();
            if (year < 1900 || year > currentYear) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        // 验证省级行政区代码（大陆 31 省）
        if (!ChinaMainlandProvince.fromCode(provinceCode).isPresent()) {
            return false;
        }
        
        // 验证执业医师级别代码
        if (!DoctorLevel.fromCode(levelCode).isPresent()) {
            return false;
        }
        
        // 验证执业医师类别代码
        if (!DoctorCategory.fromCode(categoryCode).isPresent()) {
            return false;
        }
        
        // 验证身份证号码部分 (前23/26位是数字，最后1位是数字或X)
        String chineseIdCard = cleanValue.substring(9);
        ChineseIdCardValidator chineseIdCardValidator = new ChineseIdCardValidator();
        if (!chineseIdCardValidator.isValid(chineseIdCard, null)) {
            return false;
        }
        
        return true;
    }
}