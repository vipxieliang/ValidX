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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Age;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 年龄验证器
 * 支持基于出生日期或身份证号码的年龄验证
 *
 * @author vipxieliang
 * @since 1.0.0
 */
public class AgeValidator implements ConstraintValidator<Age, Object> {

    /**
     * 18位身份证号码格式
     */
    private static final Pattern ID_CARD_18_PATTERN =
        Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    /**
     * 15位身份证号码格式
     */
    private static final Pattern ID_CARD_15_PATTERN =
        Pattern.compile("^[1-9]\\d{5}\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}$");

    /**
     * 最小年龄
     */
    private int minAge;

    /**
     * 最大年龄
     */
    private int maxAge;

    /**
     * 是否从身份证号提取
     */
    private boolean fromIdCard;

    /**
     * 日期格式
     */
    private String dateFormat;

    @Override
    public void initialize(Age constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
        this.maxAge = constraintAnnotation.max();
        this.fromIdCard = constraintAnnotation.fromIdCard();
        this.dateFormat = constraintAnnotation.dateFormat();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // null值由@NotNull处理
        if (value == null) {
            return true;
        }

        LocalDate birthDate = null;

        // 根据类型提取出生日期
        if (value instanceof LocalDate) {
            birthDate = (LocalDate) value;
        } else if (value instanceof Date) {
            birthDate = ((Date) value).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        } else if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.trim().isEmpty()) {
                return true;
            }

            if (fromIdCard) {
                // 从身份证号提取出生日期
                birthDate = extractBirthDateFromIdCard(strValue);
            } else {
                // 解析日期字符串
                birthDate = parseDateString(strValue);
            }

            if (birthDate == null) {
                return false;
            }
        } else {
            // 不支持的类型
            return false;
        }

        // 计算年龄
        int age = calculateAge(birthDate);

        // 验证年龄范围
        return validateAgeRange(age);
    }

    /**
     * 从身份证号码提取出生日期
     *
     * @param idCard 身份证号码
     * @return 出生日期，如果无法提取则返回null
     */
    private LocalDate extractBirthDateFromIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return null;
        }

        String trimmed = idCard.trim();

        // 18位身份证
        if (ID_CARD_18_PATTERN.matcher(trimmed).matches()) {
            String yearStr = trimmed.substring(6, 10);
            String monthStr = trimmed.substring(10, 12);
            String dayStr = trimmed.substring(12, 14);

            try {
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);
                return LocalDate.of(year, month, day);
            } catch (Exception e) {
                return null;
            }
        }

        // 15位身份证
        if (ID_CARD_15_PATTERN.matcher(trimmed).matches()) {
            String yearStr = "19" + trimmed.substring(6, 8);
            String monthStr = trimmed.substring(8, 10);
            String dayStr = trimmed.substring(10, 12);

            try {
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);
                return LocalDate.of(year, month, day);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @return 解析后的日期，如果解析失败则返回null
     */
    private LocalDate parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        String trimmed = dateStr.trim();

        // 尝试使用指定格式解析
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            return LocalDate.parse(trimmed, formatter);
        } catch (DateTimeParseException e) {
            // 尝试常见格式
            String[] commonFormats = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd"};
            for (String format : commonFormats) {
                if (format.equals(dateFormat)) {
                    continue; // 已经尝试过
                }
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    return LocalDate.parse(trimmed, formatter);
                } catch (DateTimeParseException ex) {
                    // 继续尝试下一个格式
                }
            }
        }

        return null;
    }

    /**
     * 计算年龄
     *
     * @param birthDate 出生日期
     * @return 年龄（周岁）
     */
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            // 出生日期在未来，视为0岁
            return 0;
        }

        Period period = Period.between(birthDate, today);
        return period.getYears();
    }

    /**
     * 验证年龄范围
     *
     * @param age 年龄
     * @return true表示在有效范围内，false表示超出范围
     */
    private boolean validateAgeRange(int age) {
        // 检查最小年龄
        if (minAge > 0 && age < minAge) {
            return false;
        }

        // 检查最大年龄
        if (maxAge > 0 && age > maxAge) {
            return false;
        }

        return true;
    }

    /**
     * 静态验证方法，供链式调用使用
     *
     * @param value 待验证的值
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @param fromIdCard 是否从身份证提取
     * @param dateFormat 日期格式
     * @return true表示验证通过，false表示验证失败
     */
    public static boolean isValidAge(Object value, int minAge, int maxAge,
                                     boolean fromIdCard, String dateFormat) {
        AgeValidator validator = new AgeValidator();
        validator.minAge = minAge;
        validator.maxAge = maxAge;
        validator.fromIdCard = fromIdCard;
        validator.dateFormat = dateFormat;

        return validator.isValid(value, null);
    }
}
