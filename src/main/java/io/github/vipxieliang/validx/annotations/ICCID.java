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

import io.github.vipxieliang.validx.validator.phone.ICCIDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 集成电路卡识别码（ICCID）验证器
 * 验证字符串是否为有效的 ICCID（ITU-T E.118 / GSMA SGP.22）
 * </p>
 *
 * <p>
 * ICCID 印刷在 SIM 卡 / eSIM 卡体上，唯一标识一张"物理卡"，
 * 与标识"设备"的 {@link IMEI}、标识"用户/SIM 签约"的 {@link IMSI} 相对：
 * </p>
 *
 * <ul>
 *     <li>ICCID：卡的识别码（印在卡上，发卡时写入卡体）；</li>
 *     <li>IMSI：签约用户识别码（存在卡内，不对外印刷）；</li>
 *     <li>IMEI：终端设备识别码（标识手机本体）。</li>
 * </ul>
 *
 * <p>
 * 现代 UICC / eSIM 的 ICCID 为 <b>20 位数字</b>，结构（ITU-T E.118）：
 * {@code 89(MII 电信行业标识) + 国家码(ISO 3166-1，中国 86) + 运营商代码 + 用户账号 + Luhn 校验位(1位)}。
 * 中国大陆 SIM 卡通常以 {@code 8986} 开头。
 * </p>
 *
 * <p>
 * 校验规则：去除空格与连字符后为 <b>20 位纯数字</b>，且整串通过 <b>Luhn</b> 校验
 * （第 20 位为前 19 位经 Luhn 算法计算所得校验位，可防笔误/输入错位）。
 * 与 {@link IMSI}（无内置校验位）不同，ICCID 自带 Luhn 校验位。
 * 空值放行，是否必填请配合 {@code @NotBlank} 使用。
 * </p>
 *
 * <pre>
 * // 通用场景
 * &#64;ICCID
 * private String iccid;         // 例：89860115244939092661
 * </pre>
 *
 * @author vipxieliang
 * @since 2026/09/09
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ICCIDValidator.class)
public @interface ICCID {
    String message() default "{io.github.vipxieliang.validx.annotation.iccid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
