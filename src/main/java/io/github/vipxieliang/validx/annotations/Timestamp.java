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

import io.github.vipxieliang.validx.validator.base.TimestampValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * Unix时间戳验证器
 * 验证值是否为有效的Unix时间戳格式（秒/毫秒）
 * </p>
 *
 * <p>支持的类型：String、Long</p>
 * <p>使用示例：</p>
 * <pre>
 *     &#064;Timestamp                   // 秒或毫秒均可
 *     private String createTime;
 *
 *     &#064;Timestamp(unit = TimestampUnit.SECONDS)      // 仅秒级时间戳
 *     private String createTimeSec;
 *
 *     &#064;Timestamp(unit = TimestampUnit.MILLISECONDS)  // 仅毫秒级时间戳
 *     private String createTimeMs;
 * </pre>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {TimestampValidator.class})
public @interface Timestamp {

    /**
     * 时间戳单位
     * @return 时间戳单位
     */
    TimestampUnit unit() default TimestampUnit.ANY;

    String message() default "{io.github.vipxieliang.validx.annotation.timestamp}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 时间戳单位枚举
     */
    enum TimestampUnit {
        /**
         * 秒级时间戳（10位数字）
         */
        SECONDS,
        /**
         * 毫秒级时间戳（13位数字）
         */
        MILLISECONDS,
        /**
         * 任意单位（10位或13位数字均可）
         */
        ANY
    }
}
