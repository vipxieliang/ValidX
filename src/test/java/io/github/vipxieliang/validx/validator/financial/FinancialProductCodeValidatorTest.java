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
 * limitations with the License.
 */

package io.github.vipxieliang.validx.validator.financial;

import io.github.vipxieliang.validx.annotations.FinancialProductCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 金融产品代码验证器测试类
 */
public class FinancialProductCodeValidatorTest {

    private final FinancialProductCodeValidator validator = new FinancialProductCodeValidator();

    @Test
    public void testValidFundCodes() {
        // 测试有效的基金代码
        // 上海证券交易所基金代码 (以5开头)
        assertTrue(validator.isValid("500001", null));
        assertTrue(validator.isValid("510000", null));
        assertTrue(validator.isValid("580000", null));
        
        // 深圳证券交易所基金代码 (以1开头)
        assertTrue(validator.isValid("100001", null));
        assertTrue(validator.isValid("150000", null));
        assertTrue(validator.isValid("180000", null));
    }

    @Test
    public void testValidBondCodes() {
        // 测试有效的债券代码
        // 国债 (以10开头)
        assertTrue(validator.isValid("100001", null));
        assertTrue(validator.isValid("101234", null));
        assertTrue(validator.isValid("109999", null));
        
        // 企业债 (以11开头)
        assertTrue(validator.isValid("110001", null));
        assertTrue(validator.isValid("111234", null));
        assertTrue(validator.isValid("119999", null));
        
        // 可转债 (以12开头)
        assertTrue(validator.isValid("120001", null));
        assertTrue(validator.isValid("121234", null));
        assertTrue(validator.isValid("129999", null));
        
        // 公司债 (以13开头)
        assertTrue(validator.isValid("130001", null));
        assertTrue(validator.isValid("131234", null));
        assertTrue(validator.isValid("139999", null));
    }

    @Test
    public void testInvalidFundCodes() {
        // 测试无效的基金代码
        assertFalse(validator.isValid("600001", null)); // 上海股票代码
        assertFalse(validator.isValid("000001", null)); // 深圳股票代码
        assertFalse(validator.isValid("50000", null));  // 5位数字
        assertFalse(validator.isValid("5000001", null)); // 7位数字
        assertFalse(validator.isValid("5A0001", null)); // 包含字母
    }

    @Test
    public void testInvalidBondCodes() {
        // 创建只验证债券代码的验证器
        FinancialProductCodeValidator bondOnlyValidator = new FinancialProductCodeValidator();
        // 通过反射设置只验证债券类型
        try {
            java.lang.reflect.Field supportedProductTypesField = FinancialProductCodeValidator.class.getDeclaredField("supportedProductTypes");
            supportedProductTypesField.setAccessible(true);
            java.util.Set<FinancialProductCode.ProductType> productTypes = new java.util.HashSet<>();
            productTypes.add(FinancialProductCode.ProductType.BOND);
            supportedProductTypesField.set(bondOnlyValidator, productTypes);
        } catch (Exception e) {
            fail("Failed to set up bond-only validator: " + e.getMessage());
        }
        
        // 测试无效的债券代码
        assertFalse(bondOnlyValidator.isValid("140001", null), "140001 should be invalid - it's not a supported bond type"); // 不支持的债券类型
        assertFalse(bondOnlyValidator.isValid("10000", null), "10000 should be invalid - it's only 5 digits");  // 5位数字
        assertFalse(bondOnlyValidator.isValid("1000001", null), "1000001 should be invalid - it's 7 digits"); // 7位数字
        assertFalse(bondOnlyValidator.isValid("10A001", null), "10A001 should be invalid - it contains letters"); // 包含字母
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试null和空值
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("", null));
    }

    @Test
    public void testWithSpaces() {
        // 测试包含空格的金融产品代码
        assertTrue(validator.isValid(" 500001 ", null));
        assertTrue(validator.isValid(" 100001 ", null));
        assertTrue(validator.isValid(" 110001 ", null));
    }
}