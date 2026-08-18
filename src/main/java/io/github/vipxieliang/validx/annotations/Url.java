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


import io.github.vipxieliang.validx.validator.network.UrlValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * URL验证器
 * 验证URL地址格式
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UrlValidator.class)
public @interface Url {
    /**
     * 允许的协议白名单
     * 默认允许 http / https / ftp（与历史版本行为一致，向下兼容）；如需收紧可自定义（如 {"http", "https"}）
     *
     * @return 协议白名单（不含 "://" 后缀）
     */
    String[] protocols() default {"http", "https", "ftp"};

    String message() default "{io.github.vipxieliang.validx.annotation.url}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}