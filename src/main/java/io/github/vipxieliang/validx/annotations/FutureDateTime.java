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


import io.github.vipxieliang.validx.validator.base.FutureDateTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 未来日期时间验证器
 * 验证日期时间是否为未来的日期时间
 * </p>
 *
 * <p>使用示例：
 * <pre>
 * // 默认格式 yyyy-MM-dd HH:mm:ss
 * {@code @FutureDateTime}
 * private String meetingTime;
 *
 * // 包含今天
 * {@code @FutureDateTime(includeToday = true)}
 * private String appointmentTime;
 *
 * // 自定义格式
 * {@code @FutureDateTime(pattern = "yyyy/MM/dd HH:mm:ss")}
 * private String customDateTime;
 *
 * // 带毫秒
 * {@code @FutureDateTime(pattern = "yyyy-MM-dd HH:mm:ss.SSS")}
 * private String preciseTime;
 * </pre>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FutureDateTimeValidator.class)
public @interface FutureDateTime {

    /**
     * 是否包含今天
     * @return true 表示今天及之后的日期时间都有效，false 表示只有今天之后的日期时间有效
     */
    boolean includeToday() default false;

    /**
     * 日期时间格式模式
     * <p>
     * 默认格式为 yyyy-MM-dd HH:mm:ss，必须包含时间部分。支持的格式：
     * <ul>
     *   <li>yyyy-MM-dd HH:mm:ss - 标准日期时间格式（默认）</li>
     *   <li>yyyy-MM-dd HH:mm - 不含秒</li>
     *   <li>yyyy-MM-dd HH:mm:ss.SSS - 含毫秒</li>
     *   <li>yyyy/MM/dd HH:mm:ss - 斜杠分隔</li>
     *   <li>yyyyMMddHHmmss - 紧凑格式</li>
     * </ul>
     * <p>
     * 注意：pattern 必须包含时间格式符号（H, m, s 等），否则会抛出 IllegalArgumentException。
     * 如只需验证日期（不含时间），请使用 @FutureDate 注解。
     *
     * @return 日期时间格式模式字符串
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";

    String message() default "{io.github.vipxieliang.validx.annotation.future.datetime}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
