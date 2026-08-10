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


import io.github.vipxieliang.validx.validator.base.ContainsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 包含验证器
 * 验证字符串是否包含指定的子字符串
 * </p>
 *
 * @author vipxieliang
 * @since 2026/07/31
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ContainsValidator.class)
public @interface Contains {
    /**
     * 要包含的子字符串数组
     *
     * @return 子字符串数组
     */
    String[] value();

    /**
     * 是否忽略大小写，默认为 false（区分大小写）
     *
     * @return 是否忽略大小写
     */
    boolean ignoreCase() default false;

    /**
     * 匹配模式：true-必须包含所有子字符串(AND)，false-包含任意一个即可(OR)
     * 默认为 false（OR逻辑，满足任意一个即可）
     *
     * @return 匹配模式
     */
    boolean matchAll() default false;

    /**
     * @return 错误消息模板
     */
    String message() default "{io.github.vipxieliang.validx.annotation.contains}";

    /**
     * @return 验证组
     */
    Class<?>[] groups() default {};

    /**
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}
