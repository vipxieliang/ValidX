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

import io.github.vipxieliang.validx.validator.base.ExpressNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 快递单号验证注解
 * 验证值是否为有效的快递单号
 * </p>
 *
 * <p>支持的快递公司：</p>
 * <ul>
 *   <li>顺丰速运 (SF_EXPRESS): 12位数字</li>
 *   <li>圆通速递 (YTO_EXPRESS): YT开头+11-13位数字，或10-13位纯数字</li>
 *   <li>申通快递 (STO_EXPRESS): 12位数字</li>
 *   <li>中通快递 (ZTO_EXPRESS): 12位数字或字母+数字组合</li>
 *   <li>韵达快递 (YUNDA_EXPRESS): 13位数字</li>
 *   <li>邮政EMS (EMS): E字母+9位数字+CN，或2位字母+9位数字+CN</li>
 *   <li>京东物流 (JD_LOGISTICS): JD开头+13-15位数字</li>
 *   <li>德邦快递 (DEPPON): 8-9位数字</li>
 *   <li>天天快递 (TTKD_EXPRESS): 12-14位数字</li>
 *   <li>百世快递 (BEST_EXPRESS): 10-12位数字或字母</li>
 * </ul>
 *
 * <p>支持的类型：String</p>
 * <p>使用示例：</p>
 * <pre>
 *     &#064;ExpressNumber
 *     private String trackingNumber;  // 验证所有支持的快递公司
 *
 *     &#064;ExpressNumber(companies = {ExpressNumber.ExpressCompany.SF_EXPRESS})
 *     private String sfNumber;  // 仅验证顺丰快递单号
 *
 *     &#064;ExpressNumber(companies = {ExpressNumber.ExpressCompany.SF_EXPRESS, ExpressNumber.ExpressCompany.YTO_EXPRESS})
 *     private String mixedNumber;  // 验证顺丰或圆通快递单号
 * </pre>
 *
 * @author vipxieliang
 * @since 2025/01/20
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ExpressNumberValidator.class})
public @interface ExpressNumber {

    /**
     * 快递公司类型，默认为所有支持的公司
     * @return 快递公司数组
     */
    ExpressCompany[] companies() default {};

    String message() default "{io.github.vipxieliang.validx.annotation.express.number}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 快递公司枚举
     */
    enum ExpressCompany {
        /**
         * 顺丰速运
         */
        SF_EXPRESS,
        /**
         * 圆通速递
         */
        YTO_EXPRESS,
        /**
         * 申通快递
         */
        STO_EXPRESS,
        /**
         * 中通快递
         */
        ZTO_EXPRESS,
        /**
         * 韵达快递
         */
        YUNDA_EXPRESS,
        /**
         * 邮政EMS
         */
        EMS,
        /**
         * 京东物流
         */
        JD_LOGISTICS,
        /**
         * 德邦快递
         */
        DEPPON,
        /**
         * 天天快递
         */
        TTKD_EXPRESS,
        /**
         * 百世快递
         */
        BEST_EXPRESS
    }
}
