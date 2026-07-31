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

import io.github.vipxieliang.validx.annotations.StockCode;
import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 股票代码链式验证测试类
 */
public class StockCodeValidationChainTest {

    @Test
    public void testValidShanghaiStockCode() {
        ValidX validator = ValidX.init();
        
        // 测试有效的上海证券交易所股票代码
        validator.isStockCode("600000");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isStockCode("600001", StockCode.Exchange.SHANGHAI);
        assertTrue(validator.passed());
    }

    @Test
    public void testValidShenzhenStockCode() {
        ValidX validator = ValidX.init();
        
        // 测试有效的深圳证券交易所股票代码
        validator.isStockCode("000001");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isStockCode("300001", StockCode.Exchange.SHENZHEN);
        assertTrue(validator.passed());
    }

    @Test
    public void testValidHongKongStockCode() {
        ValidX validator = ValidX.init();
        
        // 测试有效的香港联合交易所股票代码
        validator.isStockCode("00700");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isStockCode("00700", StockCode.Exchange.HONG_KONG);
        assertTrue(validator.passed());
    }

    @Test
    public void testValidNewYorkStockCode() {
        ValidX validator = ValidX.init();
        
        // 测试有效的纽约证券交易所股票代码
        validator.isStockCode("AAPL");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isStockCode("AAPL", StockCode.Exchange.NEW_YORK);
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidStockCode() {
        ValidX validator = ValidX.init();
        
        // 测试无效的股票代码
        validator.isStockCode("700000");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidX.init();
        validator.isStockCode("700000", StockCode.Exchange.SHANGHAI);
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testValidStockCodeWithMultipleExchanges() {
        ValidX validator = ValidX.init();
        
        // 测试指定多个交易所的股票代码
        validator.isStockCode("600000", StockCode.Exchange.SHANGHAI, StockCode.Exchange.SHENZHEN);
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        validator.isStockCode("00700", StockCode.Exchange.SHANGHAI, StockCode.Exchange.HONG_KONG);
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidStockCodeWithLimitedExchanges() {
        ValidX validator = ValidX.init();
        
        // 测试在限制交易所范围时，其他交易所的代码应该失败
        validator.isStockCode("00700", StockCode.Exchange.SHANGHAI);
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullValue() {
        ValidX validator = ValidX.init();

        // 测试null值
        validator.isStockCode(null);
        assertTrue(validator.passed());

        // 测试空字符串
        validator = ValidX.init();
        validator.isStockCode("");
        assertTrue(validator.passed());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isStockCode("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Invalid stock code"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isStockCode("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("无效的股票代码"));
    }
}