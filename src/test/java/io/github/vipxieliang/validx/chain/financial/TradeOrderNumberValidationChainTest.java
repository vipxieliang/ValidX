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

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易订单号链式验证测试类
 */
public class TradeOrderNumberValidationChainTest {

    @Test
    public void testValidTradeOrderNumbers() {
        ValidX validator = ValidX.init();
        
        // 测试有效的T开头+18位数字格式
        validator.isTradeOrderNumber("T202510171234567890");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        // 测试有效的纯18位数字格式
        validator.isTradeOrderNumber("202510171234567890");
        assertTrue(validator.passed());
        
        validator = ValidX.init();
        // 测试有效的UUID格式
        validator.isTradeOrderNumber("550e8400-e29b-41d4-a716-446655440000");
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidTradeOrderNumbers() {
        ValidX validator = ValidX.init();
        
        // 测试无效的T开头格式（不是18位数字）
        validator.isTradeOrderNumber("T20251017123456789");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidX.init();
        // 测试无效的纯数字格式（不是18位）
        validator.isTradeOrderNumber("20251017123456789");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullValue() {
        ValidX validator = ValidX.init();

        // 测试null值
        validator.isTradeOrderNumber(null);
        assertTrue(validator.passed());

        // 测试空字符串
        validator = ValidX.init();
        validator.isTradeOrderNumber("");
        assertTrue(validator.passed());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isTradeOrderNumber("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Invalid trade order number"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidX validator = ValidX.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isTradeOrderNumber("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("无效的交易订单号"));
    }
}