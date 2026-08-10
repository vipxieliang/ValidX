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

import io.github.vipxieliang.validx.annotations.Timestamp;
import io.github.vipxieliang.validx.annotations.Timestamp.TimestampUnit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Timestamp验证器
 * 验证值是否为有效的Unix时间戳格式（秒/毫秒）
 *
 * <p>支持 String 和 Long 两种类型：</p>
 * <ul>
 *   <li>String类型：通过正则匹配数字位数来验证（10位=秒，13位=毫秒）</li>
 *   <li>Long类型：通过数值范围来验证</li>
 * </ul>
 */
public class TimestampValidator implements ConstraintValidator<Timestamp, Object> {

    /**
     * 纯数字正则模式
     */
    private static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");

    /**
     * 秒级时间戳最大有效值（10位数字上限：9999999999，约公元2286年）
     */
    private static final long MAX_SECONDS = 9_999_999_999L;

    /**
     * 毫秒级时间戳最大有效值（13位数字上限：9999999999999）
     */
    private static final long MAX_MILLISECONDS = 9_999_999_999_999L;

    private TimestampUnit unit;

    @Override
    public void initialize(Timestamp constraintAnnotation) {
        initialize(constraintAnnotation.unit());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param unit 时间戳单位
     */
    public void initialize(TimestampUnit unit) {
        this.unit = unit;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 空值由@NotNull等其他注解处理
        }

        if (value instanceof String) {
            return isValidString((String) value);
        }

        if (value instanceof Long) {
            return isValidLong((Long) value);
        }

        if (value instanceof Number) {
            return isValidLong(((Number) value).longValue());
        }

        // 不支持的类型
        return false;
    }

    /**
     * 验证字符串类型的时间戳
     */
    private boolean isValidString(String value) {
        if (value.isEmpty()) {
            return true; // 空字符串由@NotEmpty等其他注解处理
        }

        if (!DIGITS_PATTERN.matcher(value).matches()) {
            return false;
        }

        int length = value.length();

        // 首先检查位数是否符合要求
        boolean validLength = false;
        switch (unit) {
            case SECONDS:
                validLength = (length == 10);
                break;
            case MILLISECONDS:
                validLength = (length == 13);
                break;
            case ANY:
                validLength = (length == 10 || length == 13);
                break;
        }

        if (!validLength) {
            return false;
        }

        // 位数正确后，再验证数值范围
        try {
            long longValue = Long.parseLong(value);
            return isValidLong(longValue);
        } catch (NumberFormatException e) {
            // 数字太大无法解析，肯定无效
            return false;
        }
    }

    /**
     * 验证Long类型的时间戳
     */
    private boolean isValidLong(long value) {
        if (value < 0) {
            return false;
        }

        switch (unit) {
            case SECONDS:
                return value <= MAX_SECONDS;
            case MILLISECONDS:
                // 毫秒值至少应大于秒的最大值
                return value > MAX_SECONDS && value <= MAX_MILLISECONDS;
            case ANY:
                return value <= MAX_MILLISECONDS;
            default:
                return false;
        }
    }
}
