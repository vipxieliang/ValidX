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


import io.github.vipxieliang.validx.annotations.ChineseIdCard;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 身份证验证器
 * 验证字符串是否为有效的身份证号码
 */
public class ChineseIdCardValidator implements ConstraintValidator<ChineseIdCard, String> {

    //身份证前17位数字依次乘以对应的权重因子
    public static final Integer[] idCardWeight= {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
    //身份证最后一位对应的校验码
    public static final String[] idCardCheck= {"1","0","X","9","8","7","6","5","4","3","2"};
    //组装根据余数，对应一个指定的校验码
    static Map<Integer,String> idCardMap=new HashMap<>();
    
    // 省份编码映射
    static Map<String, String> provinceMap = new HashMap<String, String>(){{
        put("11", "北京市");
        put("12", "天津市");
        put("13", "河北省");
        put("14", "山西省");
        put("15", "内蒙古自治区");
        put("21", "辽宁省");
        put("22", "吉林省");
        put("23", "黑龙江省");
        put("31", "上海市");
        put("32", "江苏省");
        put("33", "浙江省");
        put("34", "安徽省");
        put("35", "福建省");
        put("36", "江西省");
        put("37", "山东省");
        put("41", "河南省");
        put("42", "湖北省");
        put("43", "湖南省");
        put("44", "广东省");
        put("45", "广西壮族自治区");
        put("46", "海南省");
        put("50", "重庆市");
        put("51", "四川省");
        put("52", "贵州省");
        put("53", "云南省");
        put("54", "西藏自治区");
        put("61", "陕西省");
        put("62", "甘肃省");
        put("63", "青海省");
        put("64", "宁夏回族自治区");
        put("65", "新疆维吾尔自治区");
        put("71", "台湾省");
        put("81", "香港特别行政区");
        put("82", "澳门特别行政区");
    }};
    
    // 预编译正则表达式以提高性能
    private static final Pattern FIFTEEN_ID_PATTERN = Pattern.compile("[0-9]{15}");
    private static final Pattern EIGHTEEN_ID_PATTERN = Pattern.compile("[0-9]{17}[0-9X]");
    
    // 日期格式化器
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    // 静态初始化块
    static {
        for(int i=0;i<idCardCheck.length;i++){
            idCardMap.put(i,idCardCheck[i]);
        }
        // 设置日期格式化器不宽松解析
        DATE_FORMAT.setLenient(false);
    }

    @Override
    public void initialize(ChineseIdCard constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 实现身份证校验逻辑
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        String idCard = value.toUpperCase();
        
        // 根据身份证长度进行不同处理
        if (value.length() == 15) {
            // 15位身份证号码验证
            if (!FIFTEEN_ID_PATTERN.matcher(value).matches()) {
                return false;
            }
            // 将15位身份证转换为18位
            idCard = convert15To18(value);
        } else if (value.length() == 18) {
            // 18位身份证号码验证
            if (!EIGHTEEN_ID_PATTERN.matcher(value).matches()) {
                return false;
            }
        } else {
            // 长度既不是15位也不是18位，直接返回false
            return false;
        }

        // 验证省份编码
        String provinceCode = idCard.substring(0, 2);
        if (!provinceMap.containsKey(provinceCode)) {
            return false;
        }

        // 验证出生日期
        String birthDateStr = idCard.substring(6, 14); // 18位身份证的第7到14位是出生日期
        if (!isValidDate(birthDateStr)) {
            return false;
        }

        // 获取身份证最后一位进行验证
        String lastStr = idCard.substring(idCard.length() - 1);
        // 获取身份证前17位
        String firstStr = idCard.substring(0,17);
        
        // 验证身份证前17位是否为数字
        if (!firstStr.matches("^\\d{17}")) {
            return false;
        }

        // 计算前17位数字加权和
        char[] idCardCharNumber = firstStr.toCharArray();
        int resultSum = 0;
        for(int i = 0; i < idCardCharNumber.length; i++){
            resultSum += Character.getNumericValue(idCardCharNumber[i]) * idCardWeight[i];
        }
        
        // 将相加的前17位数字依次乘以对应的权重因子相加，相加的结果除以11，得到余数
        int lastResult = resultSum % 11;
        
        // 根据余数，对应一个指定的校验码。最终得到的校验码就是身份证号码的最后一位数字。
        // 通过这个校验码，可以验证前面17位数字是否正确，从而提高身份证号码的准确性
        return idCardMap.get(lastResult).equals(lastStr);
    }
    /**
     * 验证日期字符串是否为有效日期
     * @param dateStr 日期字符串，格式为yyyyMMdd
     * @return 是否为有效日期
     */
    private boolean isValidDate(String dateStr) {
        try {
            Date date = DATE_FORMAT.parse(dateStr);
            // 检查日期是否合理（不能是未来的日期，也不能太古老）
            Date now = new Date();
            if (date.after(now)) {
                return false;
            }
            // 检查是否在合理范围内（比如不能早于1900年）
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            int year = Integer.parseInt(yearFormat.format(date));
            if (year < 1900) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 将15位身份证号码转换为18位
     * @param idCard15 15位身份证号码
     * @return 18位身份证号码
     */
    private String convert15To18(String idCard15) {
        // 15位转换为18位
        String s2 = idCard15.substring(0, 6);
        String s3 = idCard15.substring(6, 15);
        String changed = s2.concat("19").concat(s3);
        
        // 计算校验码
        char[] idCardCharNumber = changed.toCharArray();
        int resultSum = 0;
        for(int i = 0; i < idCardCharNumber.length; i++){
            resultSum += Character.getNumericValue(idCardCharNumber[i]) * idCardWeight[i];
        }
        
        int lastResult = resultSum % 11;
        changed = changed.concat(idCardMap.get(lastResult));
        
        return changed.toUpperCase();
    }
}