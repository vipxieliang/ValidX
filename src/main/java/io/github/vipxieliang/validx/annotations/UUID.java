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

import io.github.vipxieliang.validx.validator.base.UUIDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * UUID 格式验证注解
 * 验证字符串是否为有效的 UUID 格式
 *
 * 支持的格式：
 * - 标准格式（带连字符）：550e8400-e29b-41d4-a716-446655440000
 * - 不带连字符格式：550e8400e29b41d4a716446655440000（当 allowWithoutHyphens = true 时）
 *
 * 使用示例：
 * <pre>
 * // 只允许标准格式（带连字符）
 * {@literal @}UUID
 * private String id;
 *
 * // 允许标准格式和不带连字符格式
 * {@literal @}UUID(allowWithoutHyphens = true)
 * private String transactionId;
 * </pre>
 */
@Documented
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = UUIDValidator.class)
public @interface UUID {

    /**
     * 是否允许不带连字符的格式
     * 默认值为 false，只允许标准格式（带连字符）
     * 设置为 true 时，同时允许带连字符和不带连字符的格式
     */
    boolean allowWithoutHyphens() default false;

    /**
     * 错误消息
     */
    String message() default "{io.github.vipxieliang.validx.annotation.uuid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
