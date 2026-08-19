/*
 * Copyright 2025-2026 vipxieliang
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

import io.github.vipxieliang.validx.validator.foreign.NationalityCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 国籍国代码验证器
 * 验证字符串是否为有效的 ISO 3166-1 国家/地区代码
 * </p>
 *
 * <p>
 * 通过 {@link #formats()} 指定允许的编码形式，默认两字母、三字母、数字三种形式均可。
 * 校验外国人永久居留身份证（五星卡）号码第 4~6 位时，应指定为 {@link NationalityCodeType#NUMERIC}。
 * </p>
 *
 * <pre>
 * // 通用场景：任一形式均可
 * &#64;NationalityCode
 * private String countryCode;
 *
 * // 五星卡复核：仅接受三位数字代码
 * &#64;NationalityCode(formats = NationalityCode.NationalityCodeType.NUMERIC)
 * private String nationalityCode;
 * </pre>
 *
 * @author vipxieliang
 * @since 2026/08/18
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NationalityCodeValidator.class)
public @interface NationalityCode {
    String message() default "{io.github.vipxieliang.validx.annotation.nationality.code}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * 允许的编码形式，默认三种形式均可
     * @return 编码形式数组
     */
    NationalityCodeType[] formats() default {
            NationalityCodeType.ALPHA_2, NationalityCodeType.ALPHA_3, NationalityCodeType.NUMERIC
    };

    /**
     * 国籍国代码格式（ISO 3166-1 三种编码形式）
     * <p>
     * 用于 {@link NationalityCode} 注解和链式 API 中指定允许的编码形式。
     * 五星卡号码第 4~6 位为三位数字，因此该场景应指定 {@link #NUMERIC}。
     * </p>
     *
     * @author vipxieliang
     * @since 2026/08/18
     */
    enum NationalityCodeType {

        /**
         * 两字母代码（ISO 3166-1 alpha-2），如 CA、CN、US
         */
        ALPHA_2,

        /**
         * 三字母代码（ISO 3166-1 alpha-3），如 CAN、CHN、USA
         */
        ALPHA_3,

        /**
         * 三位数字代码（ISO 3166-1 numeric），如 124、156、840
         */
        NUMERIC
    }
}
