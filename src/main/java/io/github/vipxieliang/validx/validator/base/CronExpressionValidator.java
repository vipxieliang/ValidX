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

import io.github.vipxieliang.validx.annotations.CronExpression;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Cron表达式验证器
 * 验证值是否为有效的Cron表达式格式
 *
 * <p>支持标准的6位或7位Cron表达式</p>
 * <ul>
 *   <li>6位格式：秒 分 时 日 月 周</li>
 *   <li>7位格式：秒 分 时 日 月 周 年</li>
 * </ul>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
public class CronExpressionValidator implements ConstraintValidator<CronExpression, Object> {

    // 秒：0-59
    private static final String SECOND_PATTERN = "([0-5]?\\d)";
    // 分钟：0-59
    private static final String MINUTE_PATTERN = "([0-5]?\\d)";
    // 小时：0-23
    private static final String HOUR_PATTERN = "([01]?\\d|2[0-3])";
    // 日：1-31
    private static final String DAY_PATTERN = "([1-9]|[12]\\d|3[01])";
    // 月：1-12 或 JAN-DEC
    private static final String MONTH_PATTERN = "([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
    // 周：0-7 或 SUN-SAT (0和7都代表周日)
    private static final String WEEK_PATTERN = "[0-7]|SUN|MON|TUE|WED|THU|FRI|SAT";
    // 年：1970-2099
    private static final String YEAR_PATTERN = "(19[7-9]\\d|20\\d{2})";

    // 通用的Cron字段模式（包含特殊字符）
    private static final String CRON_FIELD_PATTERN =
        "(?:\\*|\\?|(?:[0-9A-Z]+(?:-[0-9A-Z]+)?(?:,[0-9A-Z]+(?:-[0-9A-Z]+)?)*)(?:/\\d+)?|L|LW|[0-9]+W|[0-9]+#[1-5])";

    // 简化的Cron表达式验证模式
    private static final Pattern CRON_PATTERN = Pattern.compile(
        "^\\s*" + CRON_FIELD_PATTERN + "\\s+" +  // 秒
        CRON_FIELD_PATTERN + "\\s+" +            // 分
        CRON_FIELD_PATTERN + "\\s+" +            // 时
        CRON_FIELD_PATTERN + "\\s+" +            // 日
        CRON_FIELD_PATTERN + "\\s+" +            // 月
        CRON_FIELD_PATTERN +                     // 周
        "(?:\\s+" + CRON_FIELD_PATTERN + ")?" +  // 年（可选）
        "\\s*$",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 空值由@NotNull等其他注解处理
        }

        if (!(value instanceof String)) {
            return false;
        }

        String cronExpression = (String) value;

        if (cronExpression.trim().isEmpty()) {
            return true; // 空字符串由@NotEmpty等其他注解处理
        }

        return isValidCronExpression(cronExpression);
    }

    /**
     * 验证Cron表达式是否有效
     *
     * @param value 要验证的值
     * @return 如果值为有效的Cron表达式则返回true，否则返回false
     */
    public static boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        if (!(value instanceof String)) {
            return false;
        }

        String cronExpression = (String) value;

        if (cronExpression.trim().isEmpty()) {
            return true;
        }

        return isValidCronExpression(cronExpression);
    }

    /**
     * 验证Cron表达式格式
     */
    private static boolean isValidCronExpression(String cron) {
        if (cron == null || cron.trim().isEmpty()) {
            return false;
        }

        // 首先进行基本的格式匹配
        if (!CRON_PATTERN.matcher(cron).matches()) {
            return false;
        }

        // 分割字段进行更详细的验证
        String[] fields = cron.trim().split("\\s+");

        // Cron表达式必须是6位或7位
        if (fields.length != 6 && fields.length != 7) {
            return false;
        }

        // 验证每个字段
        try {
            // 秒 (0-59)
            if (!isValidField(fields[0], 0, 59, false)) {
                return false;
            }

            // 分 (0-59)
            if (!isValidField(fields[1], 0, 59, false)) {
                return false;
            }

            // 时 (0-23)
            if (!isValidField(fields[2], 0, 23, false)) {
                return false;
            }

            // 日 (1-31)
            if (!isValidDayField(fields[3])) {
                return false;
            }

            // 月 (1-12)
            if (!isValidMonthField(fields[4])) {
                return false;
            }

            // 周 (0-7)
            if (!isValidWeekField(fields[5])) {
                return false;
            }

            // 年 (可选, 1970-2099)
            if (fields.length == 7) {
                if (!isValidYearField(fields[6])) {
                    return false;
                }
            }

            // 日和周字段不能同时为非?值
            if (!fields[3].equals("?") && !fields[5].equals("?")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证通用字段
     */
    private static boolean isValidField(String field, int min, int max, boolean allowQuestion) {
        if (field.equals("*")) {
            return true;
        }

        if (field.equals("?")) {
            return allowQuestion;
        }

        // 处理步长 (例如: 0/15, */5)
        if (field.contains("/")) {
            String[] parts = field.split("/");
            if (parts.length != 2) {
                return false;
            }

            String range = parts[0];
            String step = parts[1];

            // 验证步长值
            if (!isNumeric(step)) {
                return false;
            }
            int stepValue = Integer.parseInt(step);
            if (stepValue <= 0 || stepValue > max) {
                return false;
            }

            // 如果范围是*，直接返回true
            if (range.equals("*")) {
                return true;
            }

            // 验证范围
            return isValidRangeOrList(range, min, max);
        }

        return isValidRangeOrList(field, min, max);
    }

    /**
     * 验证范围或列表
     */
    private static boolean isValidRangeOrList(String field, int min, int max) {
        // 处理列表 (例如: 1,3,5)
        if (field.contains(",")) {
            String[] values = field.split(",");
            for (String value : values) {
                if (!isValidRangeOrList(value.trim(), min, max)) {
                    return false;
                }
            }
            return true;
        }

        // 处理范围 (例如: 1-5)
        if (field.contains("-")) {
            String[] parts = field.split("-");
            if (parts.length != 2) {
                return false;
            }

            if (!isNumeric(parts[0]) || !isNumeric(parts[1])) {
                return false;
            }

            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);

            return start >= min && start <= max && end >= min && end <= max && start <= end;
        }

        // 单个数值
        if (isNumeric(field)) {
            int value = Integer.parseInt(field);
            return value >= min && value <= max;
        }

        return false;
    }

    /**
     * 验证日字段
     */
    private static boolean isValidDayField(String field) {
        if (field.equals("?") || field.equals("*")) {
            return true;
        }

        // L: 最后一天
        if (field.equals("L") || field.equals("LW")) {
            return true;
        }

        // W: 工作日 (例如: 15W)
        if (field.endsWith("W")) {
            String day = field.substring(0, field.length() - 1);
            if (isNumeric(day)) {
                int dayValue = Integer.parseInt(day);
                return dayValue >= 1 && dayValue <= 31;
            }
            return false;
        }

        return isValidField(field, 1, 31, true);
    }

    /**
     * 验证月字段
     */
    private static boolean isValidMonthField(String field) {
        if (field.equals("*")) {
            return true;
        }

        // 支持月份英文缩写
        String upperField = field.toUpperCase();
        if (upperField.matches("^(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)$")) {
            return true;
        }

        // 处理包含月份缩写的范围或列表
        if (field.contains(",") || field.contains("-")) {
            return field.matches("^([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)([,-]([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))*$");
        }

        return isValidField(field, 1, 12, false);
    }

    /**
     * 验证周字段
     */
    private static boolean isValidWeekField(String field) {
        if (field.equals("?") || field.equals("*")) {
            return true;
        }

        // L: 最后一个星期X (例如: 6L表示最后一个星期五)
        if (field.endsWith("L")) {
            String week = field.substring(0, field.length() - 1);
            return isValidWeekValue(week);
        }

        // #: 第几个星期几 (例如: 6#3表示第3个星期五)
        if (field.contains("#")) {
            String[] parts = field.split("#");
            if (parts.length != 2) {
                return false;
            }

            if (!isValidWeekValue(parts[0])) {
                return false;
            }

            if (!isNumeric(parts[1])) {
                return false;
            }

            int occurrence = Integer.parseInt(parts[1]);
            return occurrence >= 1 && occurrence <= 5;
        }

        // 支持周英文缩写
        String upperField = field.toUpperCase();
        if (upperField.matches("^(SUN|MON|TUE|WED|THU|FRI|SAT)$")) {
            return true;
        }

        // 处理包含周缩写的范围或列表
        if (field.contains(",") || field.contains("-")) {
            return field.matches("^([0-7]|SUN|MON|TUE|WED|THU|FRI|SAT)([,-]([0-7]|SUN|MON|TUE|WED|THU|FRI|SAT))*$");
        }

        return isValidField(field, 0, 7, true);
    }

    /**
     * 验证周值（数字或英文缩写）
     */
    private static boolean isValidWeekValue(String week) {
        String upperWeek = week.toUpperCase();
        if (upperWeek.matches("^(SUN|MON|TUE|WED|THU|FRI|SAT)$")) {
            return true;
        }

        if (isNumeric(week)) {
            int value = Integer.parseInt(week);
            return value >= 0 && value <= 7;
        }

        return false;
    }

    /**
     * 验证年字段
     */
    private static boolean isValidYearField(String field) {
        if (field.equals("*")) {
            return true;
        }

        return isValidField(field, 1970, 2099, false);
    }

    /**
     * 判断字符串是否为数字
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+");
    }
}
