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

import io.github.vipxieliang.validx.annotations.Lawyer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 律师资格验证器
 * 验证字符串是否符合律师资格证件格式
 * 
 * 律师执业证编号规则：
 * 1. 共17位数字
 * 2. 第1位为执业证书文本种类代码，1代表律师执业证文本
 * 3. 第2-3位为持证人执业机构所在的省（自治区、直辖市）代码
 * 4. 第4-5位为持证人执业机构所在的市(地、州、盟) 或者直辖市的区（县）代码
 * 5. 第6-9位为首次批准律师执业的年度代码
 * 6. 第10位为律师执业类别代码(专职律师1、兼职律师2、香港居民律师3、澳门居民律师4、台湾居民律师5、公职律师6、公司律师7、法律援助律师8、军队律师9)
 * 7. 第11位为性别代码(男0，女1)
 * 8. 第12-17位为律师执业证序列号代码
 * 
 * 法律职业资格证书编号规则：
 * 1. 共14位数字（不含前缀字母A/B/C）
 * 2. 第1-4位为考试年份
 * 3. 第5-6位为省、自治区、直辖市代码
 * 4. 第7-8位为省直辖市、地区（州、盟）代码
 * 5. 第9-10位为县（市辖区、地辖市、旗、省直辖县级市）代码
 * 6. 第11-14位为证书编排序号
 * 
 * 法律职业资格证书编号规则（另一种格式）：
 * 1. 共16位数字
 * 2. 第1-2位为年份后两位
 * 3. 第3-16位为其他编码信息
 */
public class LawyerValidator implements ConstraintValidator<Lawyer, String> {

    // 律师执业证格式：17位数字
    private static final Pattern LAWYER_LICENSE_PATTERN = Pattern.compile("^1\\d{16}$");
    
    // 法律职业资格证书格式：14位数字
    private static final Pattern LEGAL_QUALIFICATION_CERTIFICATE_PATTERN_14 = Pattern.compile("^\\d{14}$");
    
    // 法律职业资格证书格式：16位数字
    private static final Pattern LEGAL_QUALIFICATION_CERTIFICATE_PATTERN_16 = Pattern.compile("^\\d{16}$");
    
    // 省、自治区、直辖市代码映射
    private static final Map<String, String> VALID_PROVINCE_CODES = new HashMap<>();
    
    // 律师执业类别代码映射
    private static final Map<String, String> VALID_CATEGORY_CODES = new HashMap<>();
    
    // 性别代码映射
    private static final Map<String, String> VALID_GENDER_CODES = new HashMap<>();

    static {
        // 初始化省级行政区代码映射
        VALID_PROVINCE_CODES.put("11", "北京市");
        VALID_PROVINCE_CODES.put("12", "天津市");
        VALID_PROVINCE_CODES.put("13", "河北省");
        VALID_PROVINCE_CODES.put("14", "山西省");
        VALID_PROVINCE_CODES.put("15", "内蒙古自治区");
        VALID_PROVINCE_CODES.put("21", "辽宁省");
        VALID_PROVINCE_CODES.put("22", "吉林省");
        VALID_PROVINCE_CODES.put("23", "黑龙江省");
        VALID_PROVINCE_CODES.put("31", "上海市");
        VALID_PROVINCE_CODES.put("32", "江苏省");
        VALID_PROVINCE_CODES.put("33", "浙江省");
        VALID_PROVINCE_CODES.put("34", "安徽省");
        VALID_PROVINCE_CODES.put("35", "福建省");
        VALID_PROVINCE_CODES.put("36", "江西省");
        VALID_PROVINCE_CODES.put("37", "山东省");
        VALID_PROVINCE_CODES.put("41", "河南省");
        VALID_PROVINCE_CODES.put("42", "湖北省");
        VALID_PROVINCE_CODES.put("43", "湖南省");
        VALID_PROVINCE_CODES.put("44", "广东省");
        VALID_PROVINCE_CODES.put("45", "广西壮族自治区");
        VALID_PROVINCE_CODES.put("46", "海南省");
        VALID_PROVINCE_CODES.put("50", "重庆市");
        VALID_PROVINCE_CODES.put("51", "四川省");
        VALID_PROVINCE_CODES.put("52", "贵州省");
        VALID_PROVINCE_CODES.put("53", "云南省");
        VALID_PROVINCE_CODES.put("54", "西藏自治区");
        VALID_PROVINCE_CODES.put("61", "陕西省");
        VALID_PROVINCE_CODES.put("62", "甘肃省");
        VALID_PROVINCE_CODES.put("63", "青海省");
        VALID_PROVINCE_CODES.put("64", "宁夏回族自治区");
        VALID_PROVINCE_CODES.put("65", "新疆维吾尔自治区");
        
        // 初始化律师执业类别代码映射
        VALID_CATEGORY_CODES.put("1", "专职律师");
        VALID_CATEGORY_CODES.put("2", "兼职律师");
        VALID_CATEGORY_CODES.put("3", "香港居民律师");
        VALID_CATEGORY_CODES.put("4", "澳门居民律师");
        VALID_CATEGORY_CODES.put("5", "台湾居民律师");
        VALID_CATEGORY_CODES.put("6", "公职律师");
        VALID_CATEGORY_CODES.put("7", "公司律师");
        VALID_CATEGORY_CODES.put("8", "法律援助律师");
        VALID_CATEGORY_CODES.put("9", "军队律师");
        
        // 初始化性别代码映射
        VALID_GENDER_CODES.put("0", "男");
        VALID_GENDER_CODES.put("1", "女");
    }

    @Override
    public void initialize(Lawyer constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合律师执业证格式（17位数字，以1开头）
        if (LAWYER_LICENSE_PATTERN.matcher(value).matches()) {
            return validateLawyerLicense(value);
        }

        // 检查是否符合法律职业资格证书格式（14位数字）
        if (LEGAL_QUALIFICATION_CERTIFICATE_PATTERN_14.matcher(value).matches()) {
            return validateLegalQualificationCertificate14(value);
        }

        // 检查是否符合法律职业资格证书格式（16位数字）
        if (LEGAL_QUALIFICATION_CERTIFICATE_PATTERN_16.matcher(value).matches()) {
            return true; // 16位数字格式的验证相对宽松
        }

        return false;
    }
    
    /**
     * 验证律师执业证编号
     * @param value 律师执业证编号
     * @return 是否有效
     */
    private boolean validateLawyerLicense(String value) {
        // 第1位已经通过正则验证为1
        
        // 第2-3位为省（自治区、直辖市）代码
        String provinceCode = value.substring(1, 3);
        if (!VALID_PROVINCE_CODES.containsKey(provinceCode)) {
            return false;
        }
        
        // 第4-5位为市(地、州、盟) 或者直辖市的区（县）代码（这里只验证是否为数字）
        
        // 第6-9位为首次批准律师执业的年度代码（验证年份范围）
        String yearCode = value.substring(5, 9);
        try {
            int year = Integer.parseInt(yearCode);
            // 假设合理的年份范围为1980-当前年份
            if (year < 1980 || year > java.time.Year.now().getValue()) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        // 第10位为律师执业类别代码
        String categoryCode = value.substring(9, 10);
        if (!VALID_CATEGORY_CODES.containsKey(categoryCode)) {
            return false;
        }
        
        // 第11位为性别代码
        String genderCode = value.substring(10, 11);
        if (!VALID_GENDER_CODES.containsKey(genderCode)) {
            return false;
        }
        
        // 第12-17位为律师执业证序列号代码（这里只验证是否为数字）
        
        return true;
    }
    
    /**
     * 验证14位法律职业资格证书编号
     * @param value 法律职业资格证书编号
     * @return 是否有效
     */
    private boolean validateLegalQualificationCertificate14(String value) {
        // 第1-4位为考试年份
        String yearCode = value.substring(0, 4);
        try {
            int year = Integer.parseInt(yearCode);
            // 假设合理的年份范围为1980-当前年份
            if (year < 1980 || year > java.time.Year.now().getValue()) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        // 第5-6位为省、自治区、直辖市代码
        String provinceCode = value.substring(4, 6);
        if (!VALID_PROVINCE_CODES.containsKey(provinceCode)) {
            return false;
        }
        
        // 第7-8位为省直辖市、地区（州、盟）代码（这里只验证是否为数字）
        
        // 第9-10位为县（市辖区、地辖市、旗、省直辖县级市）代码（这里只验证是否为数字）
        
        // 第11-14位为证书编排序号（这里只验证是否为数字）
        
        return true;
    }
}