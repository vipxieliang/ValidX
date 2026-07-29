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

import io.github.vipxieliang.validx.validator.base.Base64Validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Base64编码格式验证注解
 * 支持标准Base64和URL-safe Base64格式
 *
 * <p>示例：
 * <pre>
 * // 标准Base64格式（默认）
 * {@code @Base64}
 * private String encodedData;
 *
 * // URL-safe Base64格式
 * {@code @Base64(urlSafe = true)}
 * private String urlSafeData;
 *
 * // 允许不带填充符
 * {@code @Base64(allowNoPadding = true)}
 * private String noPaddingData;
 * </pre>
 *
 * @author vipxieliang
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Base64Validator.class)
@Documented
public @interface Base64 {

    /**
     * 是否为URL-safe格式
     * 标准Base64: A-Za-z0-9+/=
     * URL-safe: A-Za-z0-9-_=
     *
     * @return true表示使用URL-safe格式，false表示使用标准格式
     */
    boolean urlSafe() default false;

    /**
     * 是否允许不带填充符（=）
     * Base64标准要求使用=进行填充，但某些场景下可以省略
     *
     * @return true表示允许不带填充符，false表示必须有正确的填充
     */
    boolean allowNoPadding() default false;

    /**
     * 验证失败时的错误消息
     */
    String message() default "{io.github.vipxieliang.validx.annotation.base64}";

    /**
     * 验证分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};
}
