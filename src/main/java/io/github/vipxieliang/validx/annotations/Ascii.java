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


import io.github.vipxieliang.validx.validator.base.AsciiValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * ASCII 字符验证器
 * 验证字符串是否只包含 ASCII 字符（Unicode 0x00 ~ 0x7F）。
 * </p>
 *
 * <p>
 * 这里采用 <b>教科书定义</b>：ASCII 共 128 个字符，包含 33 个控制字符
 * （0x00~0x1F、0x7F，如 NUL / TAB / LF / CR / DEL）。
 * 对应 C 的 {@code isascii()}、Python 的 {@code str.isascii()}。
 * </p>
 *
 * <p>
 * 若业务上要求"字符完全可见、不允许任何控制字符"，请改用 {@link Printable}（0x20~0x7E）。
 * 二者职责互补、不重叠：
 * <ul>
 *     <li>{@code @Ascii}（0x00~0x7F）—— 允许 {@code "\n"}、{@code "\t"}，但不允许中文 / Emoji 等非 ASCII 字符；</li>
 *     <li>{@link Printable}（0x20~0x7E）—— 连 {@code "\n"} 也不允许，字符必须全部可见。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 典型场景：
 * <ul>
 *     <li>多行文本 / 描述 / 备注："可以有换行与制表符，但不能出现中文、Emoji"；</li>
 *     <li>协议号、终端命令、SN/IMEI 等需要"纯 ASCII 通道"的字段；</li>
 *     <li>导入 CSV/TXT 文件前，识别是否含中文/表情/Emoji 等非 ASCII 字符；</li>
 *     <li>系统间对接时校验"不应出现非 ASCII 字符"的接口契约。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 空值放行，是否必填请配合 {@code @NotBlank} 使用。
 * </p>
 *
 * <pre>
 * // 允许换行、制表符，但不允许中文 / Emoji
 * &#64;Ascii
 * private String description;
 *
 * // 若要求"连换行都不能有、字符必须完全可见"，改用：
 * &#64;Printable
 * private String nickname;
 * </pre>
 *
 * @author vipxieliang
 * @since 2026/09/15
 * @see Printable
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AsciiValidator.class)
public @interface Ascii {
    String message() default "{io.github.vipxieliang.validx.annotation.ascii}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}