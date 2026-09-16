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
 * 默认仅允许 <b>可打印 ASCII</b>（0x20 ~ 0x7E，排除制表符/换行等控制字符），
 * 与 {@link Lower} / {@link Upper} / {@link Xdigit} 等字符类验证器保持同一档位；
 * 通过 {@code allowControlChar=true} 可放行全部 ASCII（含控制字符）。
 * </p>
 *
 * <p>
 * 典型场景：
 * <ul>
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
 * // 默认：仅可打印 ASCII（0x20~0x7E）
 * &#64;Ascii
 * private String protocolCode;
 *
 * // 允许控制字符（0x00~0x7F 全部 ASCII）
 * &#64;Ascii(allowControlChar = true)
 * private String rawSerial;
 * </pre>
 *
 * @author vipxieliang
 * @since 2026/09/15
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AsciiValidator.class)
public @interface Ascii {
    /**
     * 是否允许 ASCII 控制字符（0x00~0x1F、0x7F）。
     * 默认 {@code false}，仅允许可打印 ASCII（0x20~0x7E）。
     */
    boolean allowControlChar() default false;

    String message() default "{io.github.vipxieliang.validx.annotation.ascii}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}