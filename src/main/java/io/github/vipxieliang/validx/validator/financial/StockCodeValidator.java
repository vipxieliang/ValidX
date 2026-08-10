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

package io.github.vipxieliang.validx.validator.financial;

import io.github.vipxieliang.validx.annotations.StockCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 股票代码验证器
 * 验证不同交易所的股票代码格式
 * 
 * 支持的交易所及格式:
 * 1. 上海证券交易所: 6位数字，以6开头 (如: 600000)
 * 2. 深圳证券交易所: 6位数字，以0、3或4开头 (如: 000001, 300001, 400001)
 * 3. 香港联合交易所: 5位数字 (如: 00700)
 * 4. 纽约证券交易所: 1-4个字母 (如: AAPL, BRK.A, BRK.B)
 */
public class StockCodeValidator implements ConstraintValidator<StockCode, String> {
    
    // 上海证券交易所股票代码格式 (6位数字，以6开头)
    private static final Pattern SHANGHAI_PATTERN = Pattern.compile("^6\\d{5}$");
    
    // 深圳证券交易所股票代码格式 (6位数字，以0、3或4开头)
    private static final Pattern SHENZHEN_PATTERN = Pattern.compile("^[034]\\d{5}$");
    
    // 香港联合交易所股票代码格式 (4-5位数字，可能以0开头)
    private static final Pattern HONG_KONG_PATTERN = Pattern.compile("^\\d{4,5}$");
    
    // 纽约证券交易所股票代码格式 (1-5个字母，可能包含点号)
    private static final Pattern NEW_YORK_PATTERN = Pattern.compile("^[A-Z]{1,5}(\\.[A-Z])?$");
    
    private Set<StockCode.Exchange> supportedExchanges;

    @Override
    public void initialize(StockCode constraintAnnotation) {
        initialize(constraintAnnotation.exchanges());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param exchanges 支持的交易所数组
     */
    public void initialize(StockCode.Exchange[] exchanges) {
        // 初始化支持的交易所列表
        supportedExchanges = new HashSet<>(Arrays.asList(exchanges));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格并转换为大写
        String cleanValue = value.replaceAll("\\s+", "").toUpperCase();
        
        // 根据支持的交易所进行验证
        boolean isValid = false;
        
        // 如果没有初始化supportedExchanges，则使用默认值
        Set<StockCode.Exchange> exchanges = supportedExchanges != null ? 
            supportedExchanges : 
            new HashSet<>(Arrays.asList(StockCode.Exchange.values()));
        
        if (exchanges.contains(StockCode.Exchange.SHANGHAI) && SHANGHAI_PATTERN.matcher(cleanValue).matches()) {
            isValid = true;
        }
        
        if (exchanges.contains(StockCode.Exchange.SHENZHEN) && SHENZHEN_PATTERN.matcher(cleanValue).matches()) {
            isValid = true;
        }
        
        if (exchanges.contains(StockCode.Exchange.HONG_KONG) && HONG_KONG_PATTERN.matcher(cleanValue).matches()) {
            isValid = true;
        }
        
        if (exchanges.contains(StockCode.Exchange.NEW_YORK) && NEW_YORK_PATTERN.matcher(cleanValue).matches()) {
            isValid = true;
        }
        
        return isValid;
    }
}