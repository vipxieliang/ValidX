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

import io.github.vipxieliang.validx.validator.base.JWTValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * JWT Token验证器
 * 验证JWT（JSON Web Token）格式是否正确
 * </p>
 * <p>
 * JWT 由三部分组成，用点（.）分隔：
 * - Header: Base64URL编码的JSON对象，描述令牌类型和签名算法
 * - Payload: Base64URL编码的JSON对象，包含声明（claims）
 * - Signature: 签名，用于验证令牌的完整性
 * </p>
 * <p>
 * 格式示例：xxxxx.yyyyy.zzzzz
 * </p>
 *
 * @author vipxieliang
 * @since 2025/01/05
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = JWTValidator.class)
public @interface JWT {

    String message() default "{io.github.vipxieliang.validx.annotation.jwt}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
