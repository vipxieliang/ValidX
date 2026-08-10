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

import io.github.vipxieliang.validx.validator.base.AgeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 年龄验证注解
 * 支持基于出生日期或身份证号码的年龄验证
 *
 * <p>支持的类型：
 * <ul>
 *   <li>java.time.LocalDate - 出生日期</li>
 *   <li>java.util.Date - 出生日期</li>
 *   <li>String - 出生日期字符串或身份证号码</li>
 * </ul>
 *
 * <p>示例：
 * <pre>
 * // 验证年龄在18到65岁之间
 * {@code @Age(min = 18, max = 65)}
 * private LocalDate birthDate;
 *
 * // 只验证最小年龄
 * {@code @Age(min = 18)}
 * private String birthDateStr;  // "1990-01-01"
 *
 * // 从身份证号提取年龄验证
 * {@code @Age(min = 18, max = 65, fromIdCard = true)}
 * private String idCard;
 *
 * // 指定日期格式
 * {@code @Age(min = 18, dateFormat = "yyyy/MM/dd")}
 * private String birthDate;
 * </pre>
 *
 * @author vipxieliang
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
@Documented
public @interface Age {

    /**
     * 最小年龄（包含），0表示不限制
     *
     * @return 最小年龄
     */
    int min() default 0;

    /**
     * 最大年龄（包含），0表示不限制
     *
     * @return 最大年龄
     */
    int max() default 0;

    /**
     * 是否从身份证号码中提取出生日期
     * 仅当字段类型为String且值为身份证号码格式时有效
     *
     * @return true表示从身份证号提取，false表示直接解析日期
     */
    boolean fromIdCard() default false;

    /**
     * 日期格式（仅当字段类型为String且fromIdCard=false时有效）
     * 默认支持：yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd
     *
     * @return 日期格式字符串
     */
    String dateFormat() default "yyyy-MM-dd";

    /**
     * 验证失败时的错误消息
     *
     * @return 错误消息模板
     */
    String message() default "{io.github.vipxieliang.validx.annotation.age}";

    /**
     * 验证分组
     *
     * @return 验证组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}
