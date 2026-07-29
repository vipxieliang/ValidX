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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    
    // 省级行政区代码映射
    private static final Map<String, String> VALID_PROVINCE_CODES;
    
    // 教师资格类型代码映射
    private static final Map<String, String> VALID_TYPE_CODES;
    
    // 性别代码映射
    private static final Map<String, String> VALID_GENDER_CODES;
    
    static {
        // 初始化省级行政区代码映射
        Map<String, String> provinceCodes = new HashMap<>();
        provinceCodes.put("11", "北京市");
        provinceCodes.put("12", "天津市");
        provinceCodes.put("13", "河北省");
        provinceCodes.put("14", "山西省");
        provinceCodes.put("15", "内蒙古自治区");
        provinceCodes.put("21", "辽宁省");
        provinceCodes.put("22", "吉林省");
        provinceCodes.put("23", "黑龙江省");
        provinceCodes.put("31", "上海市");
        provinceCodes.put("32", "江苏省");
        provinceCodes.put("33", "浙江省");
        provinceCodes.put("34", "安徽省");
        provinceCodes.put("35", "福建省");
        provinceCodes.put("36", "江西省");
        provinceCodes.put("37", "山东省");
        provinceCodes.put("41", "河南省");
        provinceCodes.put("42", "湖北省");
        provinceCodes.put("43", "湖南省");
        provinceCodes.put("44", "广东省");
        provinceCodes.put("45", "广西壮族自治区");
        provinceCodes.put("46", "海南省");
        provinceCodes.put("50", "重庆市");
        provinceCodes.put("51", "四川省");
        provinceCodes.put("52", "贵州省");
        provinceCodes.put("53", "云南省");
        provinceCodes.put("54", "西藏自治区");
        provinceCodes.put("61", "陕西省");
        provinceCodes.put("62", "甘肃省");
        provinceCodes.put("63", "青海省");
        provinceCodes.put("64", "宁夏回族自治区");
        provinceCodes.put("65", "新疆维吾尔自治区");
        VALID_PROVINCE_CODES = Collections.unmodifiableMap(provinceCodes);
        
        // 初始化教师资格类型代码映射
        Map<String, String> typeCodes = new HashMap<>();
        typeCodes.put("1", "幼儿园教师资格");
        typeCodes.put("2", "小学教师资格");
        typeCodes.put("3", "初级中学教师资格");
        typeCodes.put("4", "高级中学教师资格");
        typeCodes.put("5", "中等职业学校教师资格");
        typeCodes.put("6", "中等职业学校实习指导教师资格");
        typeCodes.put("7", "高等学校教师资格");
        VALID_TYPE_CODES = Collections.unmodifiableMap(typeCodes);
        
        // 初始化性别代码映射
        Map<String, String> genderCodes = new HashMap<>();
        genderCodes.put("0", "男性");
        genderCodes.put("1", "女性");
        genderCodes.put("2", "未知");
        VALID_GENDER_CODES = Collections.unmodifiableMap(genderCodes);
    }
    
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
        
        // 验证省级行政区代码
        if (!VALID_PROVINCE_CODES.containsKey(provinceCode)) {
            return false;
        }
        
        // 验证教师资格类型代码
        if (!VALID_TYPE_CODES.containsKey(typeCode)) {
            return false;
        }
        
        // 验证性别代码
        if (!VALID_GENDER_CODES.containsKey(genderCode)) {
            return false;
        }
        
        // 验证序号代码 (应该都是数字，这部分已经在正则中验证过了)
        
        return true;
    }
}