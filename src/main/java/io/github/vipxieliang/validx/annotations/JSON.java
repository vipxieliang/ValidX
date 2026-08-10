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

import io.github.vipxieliang.validx.validator.base.JSONValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * JSON格式验证注解
 * 验证字符串是否为合法的JSON格式，支持JSON对象和数组
 *
 * <p>功能特性：
 * <ul>
 *   <li>支持JSON对象和JSON数组验证</li>
 *   <li>支持严格模式和宽松模式</li>
 *   <li>支持最大深度限制</li>
 *   <li>支持最大长度限制</li>
 *   <li>支持指定JSON类型（Object/Array/Any）</li>
 * </ul>
 *
 * <p>示例：
 * <pre>
 * // 基本JSON验证（对象或数组）
 * {@code @JSON}
 * private String jsonData;
 *
 * // 只允许JSON对象
 * {@code @JSON(type = JSONType.OBJECT)}
 * private String jsonObject;
 *
 * // 只允许JSON数组
 * {@code @JSON(type = JSONType.ARRAY)}
 * private String jsonArray;
 *
 * // 严格模式（不允许尾随逗号、注释等）
 * {@code @JSON(strict = true)}
 * private String strictJson;
 *
 * // 限制嵌套深度
 * {@code @JSON(maxDepth = 5)}
 * private String shallowJson;
 *
 * // 限制长度
 * {@code @JSON(maxLength = 1024)}
 * private String smallJson;
 * </pre>
 *
 * @author vipxieliang
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JSONValidator.class)
@Documented
public @interface JSON {

    /**
     * JSON类型枚举
     */
    enum JSONType {
        /**
         * 任意JSON类型（对象或数组）
         */
        ANY,

        /**
         * 只允许JSON对象 {...}
         */
        OBJECT,

        /**
         * 只允许JSON数组 [...]
         */
        ARRAY
    }

    /**
     * 指定JSON类型
     * ANY: 允许对象或数组
     * OBJECT: 只允许对象
     * ARRAY: 只允许数组
     *
     * @return JSON类型
     */
    JSONType type() default JSONType.ANY;

    /**
     * 是否启用严格模式
     * 严格模式下：
     * - 不允许尾随逗号
     * - 不允许注释
     * - 不允许单引号
     * - 字符串必须用双引号
     * - 键名必须用双引号
     *
     * @return true表示严格模式，false表示宽松模式
     */
    boolean strict() default true;

    /**
     * 最大嵌套深度（0表示不限制）
     * 用于防止过深的嵌套导致性能问题
     *
     * @return 最大嵌套深度
     */
    int maxDepth() default 0;

    /**
     * 最大JSON字符串长度（0表示不限制）
     * 用于防止过大的JSON导致内存问题
     *
     * @return 最大长度
     */
    int maxLength() default 0;

    /**
     * 验证失败时的错误消息
     *
     * @return 错误消息模板
     */
    String message() default "{io.github.vipxieliang.validx.annotation.json}";

    /**
     * 验证分组
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
}
