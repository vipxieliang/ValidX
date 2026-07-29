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

import io.github.vipxieliang.validx.annotations.Accountant;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 会计资格证书验证器
 * 验证会计资格证书编号格式
 * 
 * 会计资格证书编号规则 (以中级会计职称证书为例):
 * 1. 由11位数字组成
 * 2. 第1、2位为证书核发年份代码，取核发年份的后两位数字
 * 3. 第3、4位为省、自治区、直辖市或国务院行业部门代码
 * 4. 第5、6位为发证地市级代码
 * 5. 第7位为机构识别代码，取值为0-4
 * 6. 第8-10位为鉴定机构序列号，由三位数字组成
 * 7. 第11位为证书类别（等级）代码，取值为1-5
 */
public class AccountantValidator implements ConstraintValidator<Accountant, String> {
    
    // 会计资格证书编号格式: 11位数字
    private static final Pattern ACCOUNTANT_PATTERN = Pattern.compile("^\\d{11}$");
    
    // 省、自治区、直辖市或国务院行业部门代码映射
    private static final Map<String, String> VALID_PROVINCE_CODES = new HashMap<>();
    
    // 机构识别代码映射
    private static final Map<String, String> VALID_INSTITUTION_CODES = new HashMap<>();
    
    // 证书类别（等级）代码映射
    private static final Map<String, String> VALID_CATEGORY_CODES = new HashMap<>();
    
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
        
        // 初始化机构识别代码映射
        VALID_INSTITUTION_CODES.put("0", "财政部门");
        VALID_INSTITUTION_CODES.put("1", "人力资源社会保障部门");
        VALID_INSTITUTION_CODES.put("2", "教育部门");
        VALID_INSTITUTION_CODES.put("3", "卫生部门");
        VALID_INSTITUTION_CODES.put("4", "其他部门");
        
        // 初始化证书类别代码映射
        VALID_CATEGORY_CODES.put("1", "初级会计师");
        VALID_CATEGORY_CODES.put("2", "中级会计师");
        VALID_CATEGORY_CODES.put("3", "高级会计师");
        VALID_CATEGORY_CODES.put("4", "注册会计师");
        VALID_CATEGORY_CODES.put("5", "税务师");
    }
    
    @Override
    public void initialize(Accountant constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格
        String cleanValue = value.replaceAll("\\s+", "");
        
        // 验证是否为11位数字
        if (!ACCOUNTANT_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 提取各部分进行详细验证
        String yearCode = cleanValue.substring(0, 2);           // 证书核发年份代码
        String provinceCode = cleanValue.substring(2, 4);       // 省、自治区、直辖市或国务院行业部门代码
        String cityCode = cleanValue.substring(4, 6);           // 发证地市级代码
        String institutionCode = cleanValue.substring(6, 7);    // 机构识别代码
        String sequenceNumber = cleanValue.substring(7, 10);    // 鉴定机构序列号
        String categoryCode = cleanValue.substring(10, 11);     // 证书类别（等级）代码
        
        // 验证年份代码 (00-99)
        try {
            int year = Integer.parseInt(yearCode);
            if (year < 0 || year > 99) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        // 验证省、自治区、直辖市或国务院行业部门代码
        if (!VALID_PROVINCE_CODES.containsKey(provinceCode)) {
            return false;
        }
        
        // 验证机构识别代码
        if (!VALID_INSTITUTION_CODES.containsKey(institutionCode)) {
            return false;
        }
        
        // 验证证书类别（等级）代码
        if (!VALID_CATEGORY_CODES.containsKey(categoryCode)) {
            return false;
        }
        
        // 城市代码和序列号只要是数字即可，不做额外验证
        
        return true;
    }
}