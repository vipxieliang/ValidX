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

import io.github.vipxieliang.validx.validator.phone.IMSIValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 国际移动用户识别码（IMSI）验证器
 * 验证字符串是否为有效的 IMSI（ITU-T E.212 / 3GPP TS 23.003）
 * </p>
 *
 * <p>
 * IMSI 存储在 SIM 卡中，用于在全球移动网络中唯一标识一个签约用户，
 * 与标识"设备"的 {@link IMEI} 相对，IMSI 标识"用户/SIM 卡"。
 * 结构为 {@code MCC(3位) + MNC(2~3位) + MSIN(≤10位)}：
 * </p>
 *
 * <ul>
 *     <li>MCC：移动国家码，3 位，中国大陆为 460；</li>
 *     <li>MNC：移动网络码，2~3 位（中国大陆为 2 位，如 00 中国移动 / 01 中国联通 / 03 中国电信）；</li>
 *     <li>MSIN：移动用户识别号，最长 10 位。</li>
 * </ul>
 *
 * <p>
 * 校验规则：去除空格与连字符后为 <b>14~15 位纯数字</b>（IMSI 无内置校验位，
 * 格式层只能校验长度与字符集，真实性需由运营商网络侧确认）。
 * 空值放行，是否必填请配合 {@code @NotBlank} 使用。
 * </p>
 *
 * <pre>
 * // 通用场景
 * &#64;IMSI
 * private String imsi;          // 例：460001234567890
 * </pre>
 *
 * @author vipxieliang
 * @since 2026/09/09
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IMSIValidator.class)
public @interface IMSI {
    String message() default "{io.github.vipxieliang.validx.annotation.imsi}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
