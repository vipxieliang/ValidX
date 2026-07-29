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

import io.github.vipxieliang.validx.validator.china.DrugCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 药品本位码验证器
 * 验证字符串是否是有效的中国药品本位码
 * 药品本位码是以69开头，20位数字，最后一位为GS1校验位
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/13
 */
@Documented
@Constraint(validatedBy = DrugCodeValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DrugCode {
    String message() default "{io.github.vipxieliang.validx.annotation.drug.code}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}