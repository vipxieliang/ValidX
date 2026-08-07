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


import io.github.vipxieliang.validx.validator.base.NotContainsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 不包含验证器
 * 验证字符串是否不包含指定的子字符串
 * </p>
 *
 * @author vipxieliang
 * @since 2026/08/07
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NotContainsValidator.class)
public @interface NotContains {
    /**
     * 不能包含的子字符串数组
     */
    String[] value();

    /**
     * 是否忽略大小写，默认为 false（区分大小写）
     */
    boolean ignoreCase() default false;

    /**
     * 匹配模式：true-必须全都不包含(AND)，false-只要有一个不包含即可(OR)
     * 默认为 true（AND逻辑，所有子字符串都不能包含）
     */
    boolean matchAll() default true;

    String message() default "{io.github.vipxieliang.validx.annotation.not.contains}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
