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
import io.github.vipxieliang.validx.validator.china.ChineseIdCardValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    
    // 省、自治区、直辖市代码映射
    private static final Map<String, String> VALID_PROVINCE_CODES;
    
    // 执业医师级别代码映射
    private static final Map<String, String> VALID_LEVEL_CODES;
    
    // 执业医师类别代码映射
    private static final Map<String, String> VALID_CATEGORY_CODES;
    
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
        
        // 初始化执业医师级别代码映射
        Map<String, String> levelCodes = new HashMap<>();
        levelCodes.put("1", "执业医师");
        levelCodes.put("2", "执业助理医师");
        levelCodes.put("3", "师承或确有专长执业医师");
        levelCodes.put("4", "师承或确有专长执业助理医师");
        VALID_LEVEL_CODES = Collections.unmodifiableMap(levelCodes);
        
        // 初始化执业医师类别代码映射
        Map<String, String> categoryCodes = new HashMap<>();
        categoryCodes.put("10", "临床");
        categoryCodes.put("20", "口腔");
        categoryCodes.put("30", "公共卫生");
        categoryCodes.put("41", "中医");
        categoryCodes.put("42", "中西医结合");
        categoryCodes.put("43", "蒙医");
        categoryCodes.put("44", "藏医");
        categoryCodes.put("45", "维医");
        categoryCodes.put("46", "傣医");
        categoryCodes.put("47", "朝医");
        categoryCodes.put("48", "壮医");
        VALID_CATEGORY_CODES = Collections.unmodifiableMap(categoryCodes);
    }
    
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
        
        // 验证省级行政区代码
        if (!VALID_PROVINCE_CODES.containsKey(provinceCode)) {
            return false;
        }
        
        // 验证执业医师级别代码
        if (!VALID_LEVEL_CODES.containsKey(levelCode)) {
            return false;
        }
        
        // 验证执业医师类别代码
        if (!VALID_CATEGORY_CODES.containsKey(categoryCode)) {
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