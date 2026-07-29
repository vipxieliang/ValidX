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

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IBAN验证器链测试类
 */
public class IBANValidationChainTest {

    @Test
    public void testValidIBAN() {
        ValidationPlus validation = ValidationPlus.init();
        
        // 测试有效的IBAN码
        validation.isIBAN((Object)"DE89370400440532013000"); // 德国IBAN
        assertTrue(validation.passed(), "有效的德国IBAN应该通过验证");
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"FR1420041010050500013M02606"); // 法国IBAN
        assertTrue(validation.passed(), "有效的法国IBAN应该通过验证");
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"GB29NWBK60161331926819"); // 英国IBAN
        assertTrue(validation.passed(), "有效的英国IBAN应该通过验证");
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"NL91ABNA0417164300"); // 荷兰IBAN
        assertTrue(validation.passed(), "有效的荷兰IBAN应该通过验证");
    }

    @Test
    public void testInvalidIBAN() {
        ValidationPlus validation = ValidationPlus.init();
        
        // 测试无效的IBAN码
        validation.isIBAN((Object)"DE8937040044053201300"); // 德国IBAN长度错误
        assertFalse(validation.passed(), "无效的德国IBAN不应该通过验证");
        assertEquals(1, validation.getErrors().size());
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"FR1420041010050500013M0260"); // 法国IBAN长度错误
        assertFalse(validation.passed(), "无效的法国IBAN不应该通过验证");
        assertEquals(1, validation.getErrors().size());
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"GB29NWBK6016133192681"); // 英国IBAN长度错误
        assertFalse(validation.passed(), "无效的英国IBAN不应该通过验证");
        assertEquals(1, validation.getErrors().size());
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"XX29NWBK60161331926819"); // 无效国家代码
        assertFalse(validation.passed(), "无效国家代码的IBAN不应该通过验证");
        assertEquals(1, validation.getErrors().size());
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"DE90370400440532013000"); // 校验位错误
        assertFalse(validation.passed(), "校验位错误的IBAN不应该通过验证");
        assertEquals(1, validation.getErrors().size());
    }
    
    @Test
    public void testNullValue() {
        ValidationPlus validation = ValidationPlus.init();

        // 测试null值
        validation.isIBAN(null);
        assertTrue(validation.passed(), "null值应该通过验证");

        // 测试空字符串
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"");
        assertTrue(validation.passed(), "空字符串应该通过验证");
    }
    
    @Test
    public void testIBANWithSpaces() {
        ValidationPlus validation = ValidationPlus.init();
        
        // 测试带空格的IBAN
        validation.isIBAN((Object)"DE89 3704 0044 0532 0130 00"); // 带空格的德国IBAN
        assertTrue(validation.passed(), "带空格的有效德国IBAN应该通过验证");
        
        validation = ValidationPlus.init();
        validation.isIBAN((Object)"GB29 NWBK 6016 1331 9268 19"); // 带空格的英国IBAN
        assertTrue(validation.passed(), "带空格的有效英国IBAN应该通过验证");
    }
}