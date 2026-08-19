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

import io.github.vipxieliang.validx.annotations.ForeignerPermanentResidenceIdentity;
import io.github.vipxieliang.validx.enums.ChinaProvince;
import io.github.vipxieliang.validx.enums.IsoCountry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 外国人永久居留身份证验证器
 * 验证字符串是否是有效的外国人永久居留身份证
 */
public class ForeignerPermanentResidenceIdentityValidator implements ConstraintValidator<ForeignerPermanentResidenceIdentity, String> {

    // 身份证前17位数字依次乘以对应的权重因子
    public static final Integer[] idCardWeight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    // 身份证最后一位对应的校验码
    public static final String[] idCardCheck = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    // 组装根据余数，对应一个指定的校验码
    static Map<Integer, String> idCardMap = new HashMap<>();

    // 外国人永久居留身份证格式：9 + 2位申领地代码 + 3位国籍国代码 + 8位出生日期 + 3位顺序码 + 1位校验码
    private static final Pattern FOREIGNER_PERMANENT_RESIDENCE_IDENTITY_PATTERN = Pattern.compile("^9\\d{17}$");

    // 日期格式化器
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    // 静态初始化块
    static {
        for (int i = 0; i < idCardCheck.length; i++) {
            idCardMap.put(i, idCardCheck[i]);
        }
        // 设置日期格式化器不宽松解析
        DATE_FORMAT.setLenient(false);
    }

    @Override
    public void initialize(ForeignerPermanentResidenceIdentity constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查是否符合外国人永久居留身份证基本格式（18位且以9开头）
        if (!FOREIGNER_PERMANENT_RESIDENCE_IDENTITY_PATTERN.matcher(value).matches()) {
            return false;
        }

        // 首位必须是9
        if (value.charAt(0) != '9') {
            return false;
        }

        // 验证申领地代码（第2-3位）
        String applicationAreaCode = value.substring(1, 3);
        if (!isValidApplicationAreaCode(applicationAreaCode)) {
            return false;
        }

        // 验证国籍国代码（第4-6位，ISO 3166-1 numeric 三位数字代码）
        String nationalityCode = value.substring(3, 6);
        if (!IsoCountry.fromNumeric(nationalityCode).isPresent()) {
            return false;
        }

        // 验证出生日期（第7-14位）
        String birthDateStr = value.substring(6, 14);
        if (!isValidDate(birthDateStr)) {
            return false;
        }

        // 获取身份证最后一位进行验证
        String lastStr = value.substring(value.length() - 1);
        // 获取身份证前17位
        String firstStr = value.substring(0, 17);

        // 验证身份证前17位是否为数字
        if (!firstStr.matches("^\\d{17}")) {
            return false;
        }

        // 计算前17位数字加权和
        char[] idCardCharNumber = firstStr.toCharArray();
        int resultSum = 0;
        for (int i = 0; i < idCardCharNumber.length; i++) {
            resultSum += Character.getNumericValue(idCardCharNumber[i]) * idCardWeight[i];
        }

        // 将相加的前17位数字依次乘以对应的权重因子相加，相加的结果除以11，得到余数
        int lastResult = resultSum % 11;

        // 根据余数，对应一个指定的校验码。最终得到的校验码就是身份证号码的最后一位数字。
        // 通过这个校验码，可以验证前面17位数字是否正确，从而提高身份证号码的准确性
        return idCardMap.get(lastResult).equalsIgnoreCase(lastStr);
    }
    
    /**
     * 验证申领地代码是否有效
     * @param applicationAreaCode 申领地代码
     * @return 是否有效
     */
    private boolean isValidApplicationAreaCode(String applicationAreaCode) {
        return ChinaProvince.fromCode(applicationAreaCode).isPresent();
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
}