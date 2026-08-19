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

import io.github.vipxieliang.validx.annotations.Teacher;
import io.github.vipxieliang.validx.enums.ChinaMainlandProvince;
import io.github.vipxieliang.validx.enums.TeacherGender;
import io.github.vipxieliang.validx.enums.TeacherType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 教师资格证验证器
 * 验证教师资格证编号的格式
 * 
 * 教师资格证编号规则:
 * 1. 共17位数字
 * 2. 前4位: 年度代码 (认定教师资格年度编号)
 * 3. 第5-6位: 省级行政区代码
 * 4. 第7-9位: 教师资格认定机构代码
 * 5. 第10位: 教师资格类型代码 (1-7)
 * 6. 第11位: 性别代码 (0-2)
 * 7. 第12-17位: 序号代码
 */
public class TeacherValidator implements ConstraintValidator<Teacher, String> {
    
    // 教师资格证编号格式: 17位数字
    private static final Pattern TEACHER_QUALIFICATION_PATTERN = Pattern.compile("^\\d{17}$");
    
    @Override
    public void initialize(Teacher constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格
        String cleanValue = value.replaceAll("\\s+", "");
        
        // 验证是否为17位数字
        if (!TEACHER_QUALIFICATION_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 提取各部分进行详细验证
        String yearCode = cleanValue.substring(0, 4);         // 年度代码
        String provinceCode = cleanValue.substring(4, 6);     // 省级行政区代码
        String orgCode = cleanValue.substring(6, 9);          // 认定机构代码
        String typeCode = cleanValue.substring(9, 10);        // 教师资格类型代码
        String genderCode = cleanValue.substring(10, 11);     // 性别代码
        String serialCode = cleanValue.substring(11, 17);     // 序号代码
        
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
        
        // 验证教师资格类型代码
        if (!TeacherType.fromCode(typeCode).isPresent()) {
            return false;
        }
        
        // 验证性别代码
        if (!TeacherGender.fromCode(genderCode).isPresent()) {
            return false;
        }
        
        // 验证序号代码 (应该都是数字，这部分已经在正则中验证过了)
        
        return true;
    }
}