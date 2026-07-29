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


import io.github.vipxieliang.validx.validator.network.IpValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * IP地址验证器
 * 验证IPv4和IPv6地址格式
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IpValidator.class)
public @interface Ip {
    /**
     * IP地址版本
     * 默认为 ANY，表示同时支持 IPv4 和 IPv6
     */
    IpVersion version() default IpVersion.ANY;

    String message() default "{io.github.vipxieliang.validx.annotation.ip}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * IP地址版本枚举
     */
    enum IpVersion {
        /**
         * 仅验证IPv4地址
         */
        V4,
        /**
         * 仅验证IPv6地址
         */
        V6,
        /**
         * 同时验证IPv4和IPv6地址（默认）
         */
        ANY
    }
}
