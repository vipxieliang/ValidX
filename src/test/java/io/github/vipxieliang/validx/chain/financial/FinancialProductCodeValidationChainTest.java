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

package io.github.vipxieliang.validx.chain.financial;

import io.github.vipxieliang.validx.annotations.FinancialProductCode;
import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 金融产品代码链式验证测试类
 */
public class FinancialProductCodeValidationChainTest {

    @Test
    public void testValidFundCodes() {
        ValidX validator = ValidX.init();
        
        // 测试有效的基金代码
        validator.isFinancialProductCode("500001");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND);
        assertTrue(validator.passed());
    }

    @Test
    public void testValidBondCodes() {
        ValidX validator = ValidX.init();
        
        // 测试有效的债券代码
        validator.isFinancialProductCode("100001");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isFinancialProductCode("100001", FinancialProductCode.ProductType.BOND);
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidFinancialProductCodes() {
        ValidX validator = ValidX.init();
        
        // 测试无效的金融产品代码
        validator.isFinancialProductCode("600001");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidX.init();
        validator.isFinancialProductCode("600001", FinancialProductCode.ProductType.FUND);
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testValidFinancialProductCodeWithMultipleTypes() {
        ValidX validator = ValidX.init();
        
        // 测试指定多个产品类型的金融产品代码
        validator.isFinancialProductCode("500001", FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND);
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isFinancialProductCode("100001", FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND);
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidFinancialProductCodeWithLimitedTypes() {
        ValidX validator = ValidX.init();
        
        // 测试在限制产品类型范围时，其他类型的产品代码应该失败
        // "510001"是上海基金代码，但我们只允许债券类型，所以应该验证失败
        validator.isFinancialProductCode("510001", FinancialProductCode.ProductType.BOND);
        assertFalse(validator.passed(), "上海基金代码'510001'在只允许债券类型时应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试null值
        ValidX validator = ValidX.init();
        validator.isFinancialProductCode(null);
        assertTrue(validator.passed(), "null should pass validation");

        // 测试空字符串
        validator = ValidX.init();
        validator.isFinancialProductCode("");
        assertTrue(validator.passed(), "empty string should pass validation");
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isFinancialProductCode("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Invalid financial product code"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isFinancialProductCode("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("无效的金融产品代码"));
    }
}