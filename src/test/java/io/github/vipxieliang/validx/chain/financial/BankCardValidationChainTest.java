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
import io.github.vipxieliang.validx.i18n.MessageManager;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class BankCardValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidX validation = ValidX.init();
        validation.isBankCard(null);
        assertTrue(validation.passed(), "null值应该通过验证");

        // 测试空字符串
        validation = ValidX.init();
        validation.isBankCard("");
        assertTrue(validation.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidBankCard() {
        ValidX validation = ValidX.init();
        
        // 测试有效的银行卡号
        validation.isBankCard("4012888888881881"); // Visa
        validation.isBankCard("5555555555554444"); // MasterCard
        validation.isBankCard("4012 8888 8888 1881"); // 带空格的Visa
        validation.isBankCard("5555-5555-5555-4444"); // 带连字符的MasterCard
        
        assertTrue(validation.passed(), "所有有效的银行卡号应该通过验证");
        assertEquals(0, validation.getErrors().size(), "应该没有错误信息");
    }
    
    @Test
    public void testInvalidBankCard() {
        ValidX validation = ValidX.init();

        // 测试无效的银行卡号
        validation.isBankCard("1234567890123456"); // Luhn校验失败
        validation.isBankCard("12345"); // 过短
        validation.isBankCard("12345678901234567890"); // 过长
        validation.isBankCard("401288888888188a"); // 包含字母
        validation.isBankCard("4012888888881881!"); // 包含特殊字符

        assertFalse(validation.passed(), "所有无效的银行卡号不应该通过验证");
        assertEquals(5, validation.getErrors().size(), "应该有5个错误信息");

        // 检查错误信息
        String expectedMessage = MessageManager.getMessage("io.github.vipxieliang.validx.annotation.bank.card", Locale.getDefault());
        for (String error : validation.getErrors()) {
            assertEquals(expectedMessage, error, "错误信息应该匹配预期消息");
        }
    }
}