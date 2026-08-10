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

package io.github.vipxieliang.validx.annotations;

import io.github.vipxieliang.validx.validator.base.GeoPointValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 坐标验证注解，用于验证经纬度坐标对的格式和有效性
 * <p>
 * 支持的格式：
 * <ul>
 *   <li>逗号分隔: "116.4074,39.9042" (经度,纬度)</li>
 *   <li>空格分隔: "116.4074 39.9042"</li>
 *   <li>逗号+空格: "116.4074, 39.9042"</li>
 * </ul>
 * <p>
 * 验证规则：
 * <ul>
 *   <li>经度范围: -180 到 180</li>
 *   <li>纬度范围: -90 到 90</li>
 *   <li>必须包含两个数值（经度和纬度）</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>
 * public class LocationDTO {
 *     // 验证经纬度坐标对
 *     {@literal @}GeoPoint
 *     private String location; // "116.4074,39.9042"
 *
 *     // 指定顺序为纬度在前
 *     {@literal @}GeoPoint(latitudeFirst = true)
 *     private String position; // "39.9042,116.4074"
 * }
 * </pre>
 *
 * @author vipxieliang
 * @since 1.0.7
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GeoPointValidator.class)
@Documented
public @interface GeoPoint {

    /**
     * 错误消息
     *
     * @return 错误消息模板
     */
    String message() default "{io.github.vipxieliang.validx.annotation.coordinate}";

    /**
     * 分组
     *
     * @return 验证组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 坐标顺序是否为纬度在前
     * <p>
     * false (默认): 经度在前，纬度在后 (longitude,latitude) - "116.4074,39.9042"
     * true: 纬度在前，经度在后 (latitude,longitude) - "39.9042,116.4074"
     *
     * @return 是否纬度在前
     */
    boolean latitudeFirst() default false;

    /**
     * 分隔符类型
     *
     * @return 分隔符类型
     */
    SeparatorType separator() default SeparatorType.ANY;

    /**
     * 坐标分隔符类型
     */
    enum SeparatorType {
        /**
         * 任意分隔符 (逗号、空格、逗号+空格)
         */
        ANY,

        /**
         * 仅逗号分隔
         */
        COMMA,

        /**
         * 仅空格分隔
         */
        SPACE
    }
}
