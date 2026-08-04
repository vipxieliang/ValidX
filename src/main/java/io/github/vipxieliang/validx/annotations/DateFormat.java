/*
 * Copyright 2025-2026 vipxieliang
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

import io.github.vipxieliang.validx.validator.base.DateFormatValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 日期格式验证注解
 * <p>
 * 验证字符串是否为有效的日期格式，不关心日期是过去还是未来。
 * 支持自定义日期格式模式，采用严格验证模式。
 * </p>
 *
 * <p>常用格式模式：
 * <ul>
 *   <li>yyyy-MM-dd - 标准日期格式（如：2024-01-15）</li>
 *   <li>yyyy/MM/dd - 斜杠分隔（如：2024/01/15）</li>
 *   <li>yyyyMMdd - 紧凑格式（如：20240115）</li>
 *   <li>yyyy-MM-dd HH:mm:ss - 日期时间格式（如：2024-01-15 13:30:00）</li>
 *   <li>yyyy-MM-dd'T'HH:mm:ss - ISO 8601 格式（如：2024-01-15T13:30:00）</li>
 * </ul>
 *
 * <p>验证规则（严格模式）：
 * <ul>
 *   <li>格式必须完全匹配：2024-2-5 无效，必须是 2024-02-05</li>
 *   <li>日期必须有效：2024-02-30 无效（2月没有30号）</li>
 *   <li>闰年正确处理：2024-02-29 有效，2023-02-29 无效</li>
 *   <li>null 和空字符串视为有效（需配合 @NotNull/@NotEmpty 使用）</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * // 基础用法：验证 yyyy-MM-dd 格式
 * {@code @DateFormat}
 * private String eventDate;
 *
 * // 自定义格式
 * {@code @DateFormat(pattern = "yyyy/MM/dd")}
 * private String birthDate;
 *
 * // 日期时间格式
 * {@code @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss")}
 * private String appointmentTime;
 *
 * // 紧凑格式
 * {@code @DateFormat(pattern = "yyyyMMdd")}
 * private String compactDate;
 * </pre>
 *
 * @author vipxieliang
 * @since 1.0.2
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateFormatValidator.class)
public @interface DateFormat {

    /**
     * 日期格式模式
     * <p>
     * 使用 Java DateTimeFormatter 语法，支持的格式符号：
     * <p><b>日期部分：</b>
     * <ul>
     *   <li>y - 年份（yy=两位数，yyyy=四位数）</li>
     *   <li>M - 月份（M=1-12，MM=01-12，MMM=Jan，MMMM=January）</li>
     *   <li>d - 日期（d=1-31，dd=01-31）</li>
     *   <li>D - 一年中的第几天（D=1-366，DDD=001-366）</li>
     *   <li>E - 星期（E/EE/EEE=Mon，EEEE=Monday）</li>
     *   <li>u - 星期数字（1=Monday，7=Sunday）</li>
     * </ul>
     * <p><b>时间部分：</b>
     * <ul>
     *   <li>H - 24小时制（H=0-23，HH=00-23）</li>
     *   <li>h - 12小时制（h=1-12，hh=01-12）</li>
     *   <li>m - 分钟（m=0-59，mm=00-59）</li>
     *   <li>s - 秒（s=0-59，ss=00-59）</li>
     *   <li>S - 毫秒/纳秒（S=0-9，SS=00-99，SSS=000-999）</li>
     *   <li>a - 上午/下午标记（AM/PM）</li>
     * </ul>
     * <p><b>常用示例：</b>
     * <ul>
     *   <li>yyyy-MM-dd → 2024-12-25</li>
     *   <li>yyyy/MM/dd → 2024/12/25</li>
     *   <li>yyyyMMdd → 20241225</li>
     *   <li>yyyy-MM-dd HH:mm:ss → 2024-12-25 14:30:00</li>
     *   <li>yyyy-MM-dd'T'HH:mm:ss → 2024-12-25T14:30:00</li>
     *   <li>yyyy年MM月dd日 → 2024年12月25日</li>
     * </ul>
     *
     * @return 日期格式模式字符串
     */
    String pattern() default "yyyy-MM-dd";

    /**
     * 验证失败时的错误消息
     *
     * @return 错误消息模板
     */
    String message() default "{io.github.vipxieliang.validx.annotation.date.format}";

    /**
     * 验证分组
     *
     * @return 分组类数组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载类数组
     */
    Class<? extends Payload>[] payload() default {};
}
