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

import io.github.vipxieliang.validx.validator.base.DateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 日期格式验证注解
 * <p>
 * 验证字符串是否为有效的日期格式（纯日期，不包含时间部分），不关心日期是过去还是未来。
 * 支持自定义日期格式模式，采用严格验证模式。
 * </p>
 *
 * <p><b>与 @DateTime 的区别：</b>
 * <ul>
 *   <li>@Date - 验证纯日期格式（如：2024-01-15），pattern 不能包含时间符号</li>
 *   <li>@DateTime - 验证日期时间格式（如：2024-01-15 13:30:00），pattern 必须包含时间符号</li>
 * </ul>
 *
 * <p>常用格式模式：
 * <ul>
 *   <li>yyyy-MM-dd - 标准日期格式（如：2024-01-15）</li>
 *   <li>yyyy/MM/dd - 斜杠分隔（如：2024/01/15）</li>
 *   <li>yyyyMMdd - 紧凑格式（如：20240115）</li>
 *   <li>dd-MM-yyyy - 欧洲格式（如：15-01-2024）</li>
 *   <li>MM/dd/yyyy - 美国格式（如：01/15/2024）</li>
 * </ul>
 *
 * <p>验证规则（严格模式）：
 * <ul>
 *   <li>格式必须完全匹配：2024-2-5 无效，必须是 2024-02-05</li>
 *   <li>日期必须有效：2024-02-30 无效（2月没有30号）</li>
 *   <li>闰年正确处理：2024-02-29 有效，2023-02-29 无效</li>
 *   <li>pattern 不能包含时间符号（H, h, K, k, m, s, S, a, A, n, N）</li>
 *   <li>null 和空字符串视为有效（需配合 @NotNull/@NotEmpty 使用）</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * // 基础用法：验证 yyyy-MM-dd 格式
 * {@code @Date}
 * private String eventDate;
 *
 * // 自定义格式
 * {@code @Date(pattern = "yyyy/MM/dd")}
 * private String birthDate;
 *
 * // 紧凑格式
 * {@code @Date(pattern = "yyyyMMdd")}
 * private String compactDate;
 *
 * // 欧洲格式
 * {@code @Date(pattern = "dd-MM-yyyy")}
 * private String europeanDate;
 * </pre>
 *
 * <p><b>常见错误：</b>
 * <pre>
 * // ❌ 错误：使用 @Date 但 pattern 包含时间
 * {@code @Date(pattern = "yyyy-MM-dd HH:mm:ss")}  // 初始化时会抛出 IllegalArgumentException
 *
 * // ✅ 正确：应使用 @DateTime
 * {@code @DateTime(pattern = "yyyy-MM-dd HH:mm:ss")}
 * </pre>
 *
 * @see DateTime
 * @see io.github.vipxieliang.validx.annotations.PastDate
 * @see io.github.vipxieliang.validx.annotations.FutureDate
 * @author vipxieliang
 * @since 1.1.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateValidator.class)
public @interface Date {

    /**
     * 日期格式模式
     * <p>
     * 使用 Java DateTimeFormatter 语法，<b>不能包含时间部分</b>。
     * <p><b>日期部分：</b>
     * <ul>
     *   <li>y - 年份（yy=两位数，yyyy=四位数）</li>
     *   <li>M - 月份（M=1-12，MM=01-12，MMM=Jan，MMMM=January）</li>
     *   <li>d - 日期（d=1-31，dd=01-31）</li>
     *   <li>D - 一年中的第几天（D=1-366，DDD=001-366）</li>
     *   <li>E - 星期（E/EE/EEE=Mon，EEEE=Monday）</li>
     *   <li>u - 星期数字（1=Monday，7=Sunday）</li>
     * </ul>
     * <p><b>常用示例：</b>
     * <ul>
     *   <li>yyyy-MM-dd → 2024-12-25</li>
     *   <li>yyyy/MM/dd → 2024/12/25</li>
     *   <li>yyyyMMdd → 20241225</li>
     *   <li>dd-MM-yyyy → 25-12-2024</li>
     *   <li>MM/dd/yyyy → 12/25/2024</li>
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
