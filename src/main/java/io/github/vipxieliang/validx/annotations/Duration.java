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

import io.github.vipxieliang.validx.validator.base.DurationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 时间段格式验证器
 * 验证值是否为有效的时间段格式
 * </p>
 *
 * <p>支持两种格式：</p>
 * <ul>
 *   <li>ISO 8601格式：PT开头的标准格式（例如: PT2H30M, PT1H, PT45M, PT1H30M15S）</li>
 *   <li>简化格式：数字+单位组合（例如: 2h30m, 1h, 45m, 90s, 1d12h）</li>
 * </ul>
 *
 * <p>支持的时间单位：</p>
 * <ul>
 *   <li>y/Y - 年 (year)</li>
 *   <li>mo/MO - 月 (month, 简化格式使用mo以区分分钟m)</li>
 *   <li>d/D - 天 (day)</li>
 *   <li>h/H - 小时 (hour)</li>
 *   <li>m/M - 分钟 (minute, ISO格式中M可能表示月，需要根据位置判断)</li>
 *   <li>s/S - 秒 (second)</li>
 * </ul>
 *
 * <p>支持的类型：String</p>
 * <p>使用示例：</p>
 * <pre>
 *     &#064;Duration
 *     private String duration;  // "P1Y2M3D" 或 "1y2mo3d" 或 "PT2H30M" 或 "2h30m"
 *
 *     &#064;Duration(format = DurationFormat.ISO_8601)
 *     private String isoDuration;  // 仅接受 "P1Y2M3D" 或 "PT2H30M" 格式
 *
 *     &#064;Duration(format = DurationFormat.SIMPLE)
 *     private String simpleDuration;  // 仅接受 "1y2mo3d" 或 "2h30m" 格式
 * </pre>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {DurationValidator.class})
public @interface Duration {

    /**
     * 时间段格式类型
     * @return 格式类型
     */
    DurationFormat format() default DurationFormat.ANY;

    String message() default "{io.github.vipxieliang.validx.annotation.duration}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 时间段格式枚举
     */
    enum DurationFormat {
        /**
         * ISO 8601标准格式（例如: PT2H30M）
         */
        ISO_8601,
        /**
         * 简化格式（例如: 2h30m）
         */
        SIMPLE,
        /**
         * 任意格式（ISO 8601或简化格式均可）
         */
        ANY
    }
}
