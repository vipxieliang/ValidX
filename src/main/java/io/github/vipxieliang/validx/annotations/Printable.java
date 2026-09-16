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


import io.github.vipxieliang.validx.validator.base.PrintableValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 可打印字符验证器
 * 验证字符串是否只包含<b>可打印 ASCII 字符</b>（Unicode 0x20 ~ 0x7E）。
 * </p>
 *
 * <p>
 * 可打印 ASCII 含空格、字母、数字、标点等可见字符，排除 33 个控制字符（0x00~0x1F、0x7F）。
 * "可打印字符"（printable character）是一个标准概念——
 * 对应 PHP 的 {@code ctype_print()}、C 的 {@code isprint()}、Python 的 {@code str.isprintable()}。
 * </p>
 *
 * <p>
 * 与 {@link Ascii} 职责互补、不重叠：
 * <ul>
 *     <li>{@link Ascii}（0x00~0x7F）—— 允许 {@code "\n"}、{@code "\t"}，但不允许中文 / Emoji；</li>
 *     <li>{@code Printable}（0x20~0x7E）—— 连 {@code "\n"} 也不允许，字符必须全部可见。</li>
 * </ul>
 * 简单记：<b>多行文本用 {@code @Ascii}，单行 / 必须完全可见用 {@code @Printable}</b>。
 * </p>
 *
 * <p>
 * 典型场景：
 * <ul>
 *     <li>用户昵称 / 备注 / 评论内容（应当可显示，不应混入不可见的控制字节）；</li>
 *     <li>打印标签、票据、短信内容；</li>
 *     <li>导出 TXT / 日志时排除特殊控制字符，避免显示乱码；</li>
 *     <li>UI 输入框的语义层面约束（"用户能看到的内容"）。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 空值放行，是否必填请配合 {@code @NotBlank} 使用。
 * </p>
 *
 * <pre>
 * &#64;Printable
 * private String nickname;        // "Tom &amp; Jerry" 通过；"Tom\tJerry" 不通过
 * </pre>
 *
 * @author vipxieliang
 * @since 2026/09/16
 * @see Ascii
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PrintableValidator.class)
public @interface Printable {
    String message() default "{io.github.vipxieliang.validx.annotation.printable}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}