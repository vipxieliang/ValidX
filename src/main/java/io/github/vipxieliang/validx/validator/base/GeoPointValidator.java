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

import io.github.vipxieliang.validx.annotations.GeoPoint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 坐标验证器，用于验证经纬度坐标对
 *
 * @author vipxieliang
 * @since 1.0.7
 */
public class GeoPointValidator implements ConstraintValidator<GeoPoint, Object> {

    private boolean latitudeFirst;
    private GeoPoint.SeparatorType separatorType;

    @Override
    public void initialize(GeoPoint constraintAnnotation) {
        this.latitudeFirst = constraintAnnotation.latitudeFirst();
        this.separatorType = constraintAnnotation.separator();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isValid(value, latitudeFirst, separatorType);
    }

    /**
     * 验证坐标字符串
     *
     * @param value          要验证的值
     * @param latitudeFirst  是否纬度在前
     * @param separatorType  分隔符类型
     * @return 是否有效
     */
    public static boolean isValid(Object value, boolean latitudeFirst, GeoPoint.SeparatorType separatorType) {
        if (value == null) {
            return true;
        }

        if (!(value instanceof String)) {
            return false;
        }

        String coordinate = ((String) value).trim();
        if (coordinate.isEmpty()) {
            return true;
        }

        // 根据分隔符类型分割坐标
        String[] parts = splitGeoPoint(coordinate, separatorType);
        if (parts == null || parts.length != 2) {
            return false;
        }

        try {
            double first = Double.parseDouble(parts[0].trim());
            double second = Double.parseDouble(parts[1].trim());

            // 根据顺序验证经纬度范围
            if (latitudeFirst) {
                // 纬度在前，经度在后
                return isValidLatitude(first) && isValidLongitude(second);
            } else {
                // 经度在前，纬度在后 (默认)
                return isValidLongitude(first) && isValidLatitude(second);
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 根据分隔符类型分割坐标字符串
     */
    private static String[] splitGeoPoint(String coordinate, GeoPoint.SeparatorType separatorType) {
        switch (separatorType) {
            case COMMA:
                // 仅逗号分隔
                if (!coordinate.contains(",")) {
                    return null;
                }
                return coordinate.split(",");

            case SPACE:
                // 仅空格分隔
                if (coordinate.contains(",")) {
                    return null;
                }
                return coordinate.split("\\s+");

            case ANY:
            default:
                // 任意分隔符：优先尝试逗号，然后空格
                if (coordinate.contains(",")) {
                    return coordinate.split(",");
                } else {
                    return coordinate.split("\\s+");
                }
        }
    }

    /**
     * 验证经度范围
     */
    private static boolean isValidLongitude(double longitude) {
        return longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * 验证纬度范围
     */
    private static boolean isValidLatitude(double latitude) {
        return latitude >= -90.0 && latitude <= 90.0;
    }
}
