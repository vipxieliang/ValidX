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

import io.github.vipxieliang.validx.validator.base.CronExpressionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * Cron表达式验证器
 * 验证值是否为有效的Cron表达式格式
 * </p>
 *
 * <p>支持标准的6位或7位Cron表达式：</p>
 * <ul>
 *   <li>6位格式：秒 分 时 日 月 周 (例如: 0 0 12 * * ?)</li>
 *   <li>7位格式：秒 分 时 日 月 周 年 (例如: 0 0 12 * * ? 2025)</li>
 * </ul>
 *
 * <p>支持的特殊字符：</p>
 * <ul>
 *   <li>* : 匹配任意值</li>
 *   <li>? : 不指定值（仅用于日和周）</li>
 *   <li>- : 范围（例如: 1-5）</li>
 *   <li>, : 列举（例如: 1,3,5）</li>
 *   <li>/ : 步长（例如: 0/15）</li>
 *   <li>L : 最后（例如: L表示月的最后一天）</li>
 *   <li>W : 工作日（例如: 15W）</li>
 *   <li># : 第几个星期几（例如: 6#3表示第3个星期五）</li>
 * </ul>
 *
 * <p>支持的类型：String</p>
 * <p>使用示例：</p>
 * <pre>
 *     &#064;CronExpression
 *     private String schedule;  // "0 0 12 * * ?"
 * </pre>
 *
 * @author vipxieliang
 * @since 2025/10/01
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {CronExpressionValidator.class})
public @interface CronExpression {

    String message() default "{io.github.vipxieliang.validx.annotation.cron.expression}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
