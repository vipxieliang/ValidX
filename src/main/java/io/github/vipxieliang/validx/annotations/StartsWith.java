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


import io.github.vipxieliang.validx.validator.base.StartsWithValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 前缀验证器
 * 验证字符串是否以指定前缀开头
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = StartsWithValidator.class)
public @interface StartsWith {
    String startsWith();

    /**
     * 是否忽略大小写，默认为 false（区分大小写）
     *
     * @return true 表示忽略大小写，false 表示区分大小写
     */
    boolean ignoreCase() default false;

    String message() default "{io.github.vipxieliang.validx.annotation.starts.with}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}