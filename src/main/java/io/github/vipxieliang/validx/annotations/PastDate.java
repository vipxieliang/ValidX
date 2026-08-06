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


import io.github.vipxieliang.validx.validator.base.PastDateValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 过去日期验证器
 * 验证日期是否为过去日期
 * </p>
 *
 * <p>使用示例：
 * <pre>
 * // 默认格式 yyyy-MM-dd
 * {@code @PastDate}
 * private String birthDate;
 *
 * // 包含今天
 * {@code @PastDate(includeToday = true)}
 * private String eventDate;
 *
 * // 自定义日期格式
 * {@code @PastDate(pattern = "yyyy/MM/dd")}
 * private String customDate;
 *
 * // 紧凑格式
 * {@code @PastDate(pattern = "yyyyMMdd")}
 * private String compactDate;
 * </pre>
 * <p>注意：此注解仅用于纯日期格式验证，pattern 不能包含时间部分。
 * 如需验证过去的日期时间，请使用 {@link PastDateTime} 注解。
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PastDateValidator.class)
public @interface PastDate {

    /**
     * 是否包含今天
     * @return true 表示今天及之前的日期都有效，false 表示只有今天之前的日期有效
     */
    boolean includeToday() default false;

    /**
     * 日期格式模式
     * <p>
     * 默认格式为 yyyy-MM-dd，支持自定义格式：
     * <ul>
     *   <li>yyyy-MM-dd - 标准日期格式（默认）</li>
     *   <li>yyyy/MM/dd - 斜杠分隔</li>
     *   <li>dd-MM-yyyy - 欧洲格式</li>
     *   <li>MM/dd/yyyy - 美国格式</li>
     *   <li>yyyyMMdd - 紧凑格式</li>
     * </ul>
     * <p>
     * 注意：pattern 不能包含时间符号（H, h, K, k, m, s, S, a, A, n, N）
     *
     * @return 日期格式模式字符串
     */
    String pattern() default "yyyy-MM-dd";

    String message() default "{io.github.vipxieliang.validx.annotation.past.date}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}