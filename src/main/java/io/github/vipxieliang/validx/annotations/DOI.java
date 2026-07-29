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

import io.github.vipxieliang.validx.validator.book.DOIValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * DOI验证器
 * 验证字符串是否是有效的DOI（数字对象标识符）
 * 用于数字资源的唯一标识，广泛用于学术出版物
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/11
 */
@Documented
@Constraint(validatedBy = DOIValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DOI {
    String message() default "{io.github.vipxieliang.validx.annotation.doi}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}