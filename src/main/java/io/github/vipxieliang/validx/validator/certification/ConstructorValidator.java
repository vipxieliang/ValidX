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

import io.github.vipxieliang.validx.annotations.Constructor;
import io.github.vipxieliang.validx.enums.ChinaMainlandProvince;
import io.github.vipxieliang.validx.enums.ConstructorLevel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 建造师证书验证器
 * 验证一级/二级建造师证书编号格式
 * 
 * 建造师证书编号规则:
 * 1. 由一个汉字和12位阿拉伯数字组成，总共13位
 * 2. 首位汉字表示现注册省份简称，如北京为"京"
 * 3. 第2位表示注册建造师级别，一级为1，二级为2
 * 4. 第3、4位表示初始注册时受聘企业所在地省级行政区划代码
 * 5. 第5、6位表示取得资格证书年份
 * 6. 第7、8位表示初始注册年份
 * 7. 第9-13位表示初始注册时申请人在注册申请地省级注册流水号
 */
public class ConstructorValidator implements ConstraintValidator<Constructor, String> {
    
    // 建造师证书编号格式: 1个汉字 + 12位数字
    private static final Pattern CONSTRUCTOR_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5][12]\\d{11}$");
    
    @Override
    public void initialize(Constructor constraintAnnotation) {
        // 初始化逻辑（如果需要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格
        String cleanValue = value.replaceAll("\\s+", "");
        
        // 验证是否为13位（1个汉字+12位数字）
        if (!CONSTRUCTOR_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 提取各部分进行详细验证
        String levelCode = cleanValue.substring(1, 2);        // 级别代码 (1或2)
        String provinceCode = cleanValue.substring(2, 4);     // 省级行政区划代码
        String yearCode = cleanValue.substring(4, 6);         // 取得资格证书年份
        String registerYearCode = cleanValue.substring(6, 8); // 初始注册年份
        
        // 验证级别代码 (必须是1或2)
        if (!ConstructorLevel.fromCode(levelCode).isPresent()) {
            return false;
        }
        
        // 验证省级行政区划代码（大陆 31 省，另允许中央/特殊代码 99）
        if (!ChinaMainlandProvince.fromCode(provinceCode).isPresent() && !"99".equals(provinceCode)) {
            return false;
        }
        
        // 验证年份代码 (00-99)
        try {
            int year = Integer.parseInt(yearCode);
            if (year < 0 || year > 99) {
                return false;
            }
            
            int registerYear = Integer.parseInt(registerYearCode);
            if (registerYear < 0 || registerYear > 99) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        return true;
    }
}