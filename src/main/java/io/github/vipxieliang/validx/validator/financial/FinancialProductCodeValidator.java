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

import io.github.vipxieliang.validx.annotations.FinancialProductCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 金融产品代码验证器
 * 验证基金代码、债券代码等金融产品的代码格式
 * 
 * 支持的产品类型及格式:
 * 1. 基金产品: 6位数字
 *    - 上海证券交易所基金: 以5开头的6位数字 (如: 500001)
 *    - 深圳证券交易所基金: 以1开头的6位数字 (如: 150001)
 * 2. 债券产品: 6位数字
 *    - 国债: 以10打头的6位数字 (如: 100001)
 *    - 企业债: 以11打头的6位数字 (如: 110001)
 *    - 可转债: 以12打头的6位数字 (如: 120001)
 *    - 公司债: 以13打头的6位数字 (如: 130001)
 */
public class FinancialProductCodeValidator implements ConstraintValidator<FinancialProductCode, String> {
    
    // 基金代码格式 (6位数字)
    // 上海证券交易所基金代码格式 (以5开头)
    private static final Pattern FUND_SHANGHAI_PATTERN = Pattern.compile("^5\\d{5}$");
    
    // 深圳证券交易所基金代码格式 (以1开头)
    private static final Pattern FUND_SHENZHEN_PATTERN = Pattern.compile("^1\\d{5}$");
    
    // 债券代码格式 (6位数字)
    // 国债代码格式 (以10开头)
    private static final Pattern BOND_NATIONAL_PATTERN = Pattern.compile("^10\\d{4}$");
    
    // 企业债代码格式 (以11开头)
    private static final Pattern BOND_CORPORATE_PATTERN = Pattern.compile("^11\\d{4}$");
    
    // 可转债代码格式 (以12开头)
    private static final Pattern BOND_CONVERTIBLE_PATTERN = Pattern.compile("^12\\d{4}$");
    
    // 公司债代码格式 (以13开头)
    private static final Pattern BOND_COMPANY_PATTERN = Pattern.compile("^13\\d{4}$");
    
    private Set<FinancialProductCode.ProductType> supportedProductTypes;

    @Override
    public void initialize(FinancialProductCode constraintAnnotation) {
        initialize(constraintAnnotation.productTypes());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param productTypes 支持的产品类型数组
     */
    public void initialize(FinancialProductCode.ProductType[] productTypes) {
        // 初始化支持的产品类型列表
        supportedProductTypes = new HashSet<>(Arrays.asList(productTypes));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格
        String cleanValue = value.replaceAll("\\s+", "");
        
        // 根据支持的产品类型进行验证
        boolean isValid = false;
        
        // 如果没有初始化supportedProductTypes，则使用默认值
        Set<FinancialProductCode.ProductType> productTypes = supportedProductTypes != null ? 
            supportedProductTypes : 
            new HashSet<>(Arrays.asList(FinancialProductCode.ProductType.values()));
        
        // 基金验证
        if (productTypes.contains(FinancialProductCode.ProductType.FUND)) {
            if (FUND_SHANGHAI_PATTERN.matcher(cleanValue).matches() || 
                FUND_SHENZHEN_PATTERN.matcher(cleanValue).matches()) {
                isValid = true;
            }
        }
        
        // 债券验证
        if (productTypes.contains(FinancialProductCode.ProductType.BOND)) {
            if (BOND_NATIONAL_PATTERN.matcher(cleanValue).matches() || 
                BOND_CORPORATE_PATTERN.matcher(cleanValue).matches() || 
                BOND_CONVERTIBLE_PATTERN.matcher(cleanValue).matches() || 
                BOND_COMPANY_PATTERN.matcher(cleanValue).matches()) {
                isValid = true;
            }
        }
        
        return isValid;
    }
}