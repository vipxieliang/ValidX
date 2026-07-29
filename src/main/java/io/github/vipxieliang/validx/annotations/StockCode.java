/*
 * Copyright 2025-2025 vipxieliang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You you may obtain a copy of the License at
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

import io.github.vipxieliang.validx.validator.financial.StockCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 股票代码校验注解
 * 验证不同交易所的股票代码格式
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/17
 */
@Documented
@Constraint(validatedBy = { StockCodeValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface StockCode {
    String message() default "{io.github.vipxieliang.validx.annotation.stock.code}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
    
    /**
     * 支持的交易所类型
     * 默认支持所有交易所
     */
    Exchange[] exchanges() default { Exchange.SHANGHAI, Exchange.SHENZHEN, Exchange.HONG_KONG, Exchange.NEW_YORK };
    
    /**
     * 交易所枚举
     */
    enum Exchange {
        /**
         * 上海证券交易所
         */
        SHANGHAI,
        /**
         * 深圳证券交易所
         */
        SHENZHEN,
        /**
         * 香港联合交易所
         */
        HONG_KONG,
        /**
         * 纽约证券交易所
         */
        NEW_YORK
    }
}