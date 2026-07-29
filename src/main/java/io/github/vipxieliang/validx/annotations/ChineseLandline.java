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


import io.github.vipxieliang.validx.validator.china.ChineseLandlineValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 中国固话验证器
 * 验证输入的字符串是否符合中国固话格式
 * 例如：010-12345678、010-12345678-1234、010-12345678-1234-1234
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ChineseLandlineValidator.class)
public @interface ChineseLandline {
    String message() default "{io.github.vipxieliang.validx.annotation.chinese.landline}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
