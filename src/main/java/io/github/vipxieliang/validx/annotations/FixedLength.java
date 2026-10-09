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

import io.github.vipxieliang.validx.enums.LengthCountMode;
import io.github.vipxieliang.validx.validator.base.FixedLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 定长字符串验证器，验证字符串长度是否<b>恰好等于</b> {@link #length()}。
 * 与字符集校验（{@link Ascii} / {@link Printable} / {@code @AlphaNumber} 等）正交、可叠加。
 * </p>
 *
 * <h3>典型场景</h3>
 * <ul>
 *     <li><b>验证码</b>：短信 / 邮箱 / TOTP 固定 6 位；图形验证码 4-6 位；PIN 码 6 位</li>
 *     <li><b>物流</b>：京东快递 13 位、中通 12 位、顺丰 12 位</li>
 *     <li><b>条码</b>：EAN-13 / UPC-A / EAN-8</li>
 *     <li><b>协议标识</b>：API Key 32 / 40 / 64、邀请码 6-8、短链 6-8</li>
 *     <li><b>企业内部</b>：工号 6-10、合同号 16-24、部门编号 6-12</li>
 * </ul>
 *
 * <h3>设计要点</h3>
 * <ul>
 *     <li><b>trim 开关拆为 {@code trimStart} 与 {@code trimEnd} 两个独立 boolean</b>（默认均关）：
 *         校验器不改输入，所见即所得；调用方按需开启其一或两者皆开</li>
 *     <li><b>长度计数口径 {@link #countMode()}</b>：默认 {@link LengthCountMode#CHAR}（等同 {@code String.length()}），
 *         可选 {@link LengthCountMode#CODE_POINT}（按 Unicode 码点，emoji 友好）、
 *         {@link LengthCountMode#GRAPHEME}（按字素簇，组合字符友好）</li>
 *     <li>若集合有严格长度要求而字符集不限，单独使用本注解；若有额外字符集要求，与 {@link Ascii}
 *         / {@link Printable} / {@code @Numeric} 等字符集注解叠加使用</li>
 * </ul>
 *
 * <h3>空值放行</h3>
 * 是否必填请配合 {@code @NotBlank} / {@code @NotNull} 使用。
 *
 * <h3>示例</h3>
 * <pre>
 * &#64;FixedLength(length = 6)
 * private String smsCode;                 // "123456" 通过；"12345" / "1234567" 不通过
 *
 * &#64;FixedLength(length = 13)
 * private String expressNumber;           // 京东快递单号
 *
 * // Excel/OCR 来的字符串常带尾随空格 / 换行
 * &#64;FixedLength(length = 8, trimEnd = true)
 * private String barcodeFromExcel;
 *
 * // 严格：字符必须完全可见（叠加 @Printable）
 * &#64;FixedLength(length = 8)
 * &#64;Printable
 * private String licenseCode;
 * </pre>
 *
 * @author vipxieliang
 * @since 2026/09/17
 * @see LengthCountMode
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FixedLengthValidator.class)
public @interface FixedLength {

    /**
     * 必填：定长位数（按 {@link #countMode()} 口径计数）。
     */
    int length();

    /**
     * 是否先 trimStart（默认关：保留前导有效字符）。
     *
     * <p>校验器是<b>纯函数</b>，默认不改输入。需要"忽略前导空白"时显式开启。</p>
     */
    boolean trimStart() default false;

    /**
     * 是否先 trimEnd（默认关：保留尾部有效字符）。
     *
     * <p>校验器是<b>纯函数</b>，默认不改输入。处理 Excel 单元格、CSV 行尾 {@code \r\n}、
     * OCR 末尾空白等"尾随污染"时单独开启即可，不必同时开 {@code trimStart}。</p>
     */
    boolean trimEnd() default false;

    /**
     * 长度计数口径，默认 {@link LengthCountMode#CHAR}（UTF-16 代码单元，等价 {@code String.length()}）。
     */
    LengthCountMode countMode() default LengthCountMode.CHAR;

    String message() default "{io.github.vipxieliang.validx.annotation.fixed.length}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}