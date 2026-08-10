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

import io.github.vipxieliang.validx.annotations.Duration;
import io.github.vipxieliang.validx.annotations.Duration.DurationFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Duration时间段验证器
 * 验证值是否为有效的时间段格式
 *
 * <p>支持两种格式：</p>
 * <ul>
 *   <li>ISO 8601格式：PT2H30M, PT1H, PT45M等</li>
 *   <li>简化格式：2h30m, 1h, 45m等</li>
 * </ul>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
public class DurationValidator implements ConstraintValidator<Duration, Object> {

    // ISO 8601 Duration格式正则
    // 完整格式: P[nY][nM][nD][T[nH][nM][nS]]
    // 例如: P1Y2M3D, PT2H30M, P1Y2M3DT4H5M6S, P1DT2H
    private static final Pattern ISO_8601_PATTERN = Pattern.compile(
        "^P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?)?$",
        Pattern.CASE_INSENSITIVE
    );

    // 简化格式正则
    // 格式: [数字][单位][数字][单位]...
    // 单位: y(年), mo(月), d(天), h(小时), m(分钟), s(秒)
    // 例如: 1y2mo3d, 2h30m, 1h, 45m, 90s, 1y6mo
    private static final Pattern SIMPLE_PATTERN = Pattern.compile(
        "^(?:(\\d+)y)?(?:(\\d+)mo)?(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$",
        Pattern.CASE_INSENSITIVE
    );

    private DurationFormat format;

    @Override
    public void initialize(Duration constraintAnnotation) {
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 空值由@NotNull等其他注解处理
        }

        if (!(value instanceof String)) {
            return false;
        }

        String duration = (String) value;

        if (duration.trim().isEmpty()) {
            return true; // 空字符串由@NotEmpty等其他注解处理
        }

        return isValidDuration(duration, format);
    }

    /**
     * 验证时间段格式（静态方法，供链式调用使用）
     *
     * @param value 要验证的值
     * @return 如果值为有效的时间段格式则返回true，否则返回false
     */
    public static boolean isValid(Object value) {
        return isValid(value, DurationFormat.ANY);
    }

    /**
     * 验证时间段格式（静态方法，指定格式）
     *
     * @param value 要验证的值
     * @param format 时间段格式类型
     * @return 如果值为有效的时间段格式则返回true，否则返回false
     */
    public static boolean isValid(Object value, DurationFormat format) {
        if (value == null) {
            return true;
        }

        if (!(value instanceof String)) {
            return false;
        }

        String duration = (String) value;

        if (duration.trim().isEmpty()) {
            return true;
        }

        return isValidDuration(duration, format);
    }

    /**
     * 验证时间段格式
     */
    private static boolean isValidDuration(String duration, DurationFormat format) {
        if (duration == null || duration.trim().isEmpty()) {
            return false;
        }

        duration = duration.trim();

        switch (format) {
            case ISO_8601:
                return isValidIso8601Duration(duration);
            case SIMPLE:
                return isValidSimpleDuration(duration);
            case ANY:
                return isValidIso8601Duration(duration) || isValidSimpleDuration(duration);
            default:
                return false;
        }
    }

    /**
     * 验证ISO 8601格式的时间段
     */
    private static boolean isValidIso8601Duration(String duration) {
        if (!ISO_8601_PATTERN.matcher(duration).matches()) {
            return false;
        }

        // 必须包含至少一个时间单位
        // PT 后面必须有至少一个数字+单位
        if (duration.equalsIgnoreCase("P") || duration.equalsIgnoreCase("PT")) {
            return false;
        }

        // 如果只有P，没有T，必须有天数
        if (!duration.toUpperCase().contains("T") && !duration.toUpperCase().matches("^P\\d+D$")) {
            return false;
        }

        // 如果有T，T后面必须有至少一个时间单位
        if (duration.toUpperCase().contains("T")) {
            String afterT = duration.substring(duration.toUpperCase().indexOf("T") + 1);
            if (afterT.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 验证简化格式的时间段
     */
    private static boolean isValidSimpleDuration(String duration) {
        if (!SIMPLE_PATTERN.matcher(duration).matches()) {
            return false;
        }

        // 必须包含至少一个时间单位
        // 检查是否包含 y, mo, d, h, m, s 中的任意一个
        String lowerDuration = duration.toLowerCase();
        if (!lowerDuration.matches(".*[ydhms].*") && !lowerDuration.contains("mo")) {
            return false;
        }

        // 验证是否至少有一个非零的时间值
        boolean hasValue = false;

        // 检查是否有年
        if (lowerDuration.contains("y") && !lowerDuration.contains("mo")) {
            int yIndex = lowerDuration.indexOf("y");
            String years = extractNumberBefore(lowerDuration, yIndex);
            if (!years.isEmpty() && Integer.parseInt(years) > 0) {
                hasValue = true;
            }
        }

        // 检查是否有月 (mo)
        if (lowerDuration.contains("mo")) {
            int moIndex = lowerDuration.indexOf("mo");
            String months = extractNumberBefore(lowerDuration, moIndex);
            if (!months.isEmpty() && Integer.parseInt(months) > 0) {
                hasValue = true;
            }
        }

        // 检查是否有天
        if (lowerDuration.contains("d")) {
            int dIndex = lowerDuration.indexOf("d");
            String days = extractNumberBefore(lowerDuration, dIndex);
            if (!days.isEmpty() && Integer.parseInt(days) > 0) {
                hasValue = true;
            }
        }

        // 检查是否有小时
        if (lowerDuration.contains("h")) {
            int hIndex = lowerDuration.indexOf("h");
            String hours = extractNumberBefore(lowerDuration, hIndex);
            if (!hours.isEmpty() && Integer.parseInt(hours) > 0) {
                hasValue = true;
            }
        }

        // 检查是否有分钟 (注意要排除mo中的m)
        if (lowerDuration.contains("m")) {
            // 找到所有m的位置
            int mIndex = lowerDuration.indexOf("m");
            // 如果m不是mo的一部分
            if (mIndex == lowerDuration.length() - 1 ||
                (mIndex < lowerDuration.length() - 1 && lowerDuration.charAt(mIndex + 1) != 'o')) {
                String minutes = extractNumberBefore(lowerDuration, mIndex);
                if (!minutes.isEmpty() && Integer.parseInt(minutes) > 0) {
                    hasValue = true;
                }
            }
        }

        // 检查是否有秒
        if (lowerDuration.contains("s")) {
            int sIndex = lowerDuration.indexOf("s");
            String seconds = extractNumberBefore(lowerDuration, sIndex);
            if (!seconds.isEmpty() && Integer.parseInt(seconds) > 0) {
                hasValue = true;
            }
        }

        return hasValue;
    }

    /**
     * 从字符串中提取指定位置之前的数字
     */
    private static String extractNumberBefore(String str, int index) {
        if (index <= 0) {
            return "";
        }

        int startIndex = index - 1;
        while (startIndex >= 0 && Character.isDigit(str.charAt(startIndex))) {
            startIndex--;
        }
        startIndex++;

        if (startIndex >= index) {
            return "";
        }

        return str.substring(startIndex, index);
    }
}
