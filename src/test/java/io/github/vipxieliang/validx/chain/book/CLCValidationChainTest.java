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

package io.github.vipxieliang.validx.chain.book;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLC验证器链测试类
 */
public class CLCValidationChainTest {

    @Test
    public void testValidCLC() {
        ValidaX validator = ValidaX.init();
        validator.isCLC("TP311.138.S6");
        
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidCLC() {
        ValidaX validator = ValidaX.init();
        validator.isCLC("TP.311");
        
        assertFalse(validator.passed());
    }
    
    @Test
    public void testMultipleValidations() {
        ValidaX validator = ValidaX.init();
        validator.isCLC("TP311.138.S6")
                 .isCLC("A")
                 .isCLC("TP311.138.S63");
        
        assertTrue(validator.passed());
    }
    
    @Test
    public void testMixedValidations() {
        ValidaX validator = ValidaX.init();
        validator.isCLC("TP311.138.S6")
                 .isCLC("TP.311")  // 无效的
                 .isCLC("F272.3");

        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullAndEmptyCLC() {
        // 测试 null 值
        ValidaX validator = ValidaX.init();
        validator.isCLC(null);
        assertTrue(validator.passed(), "null 应该通过验证");

        // 测试空字符串
        validator = ValidaX.init();
        validator.isCLC("");
        assertTrue(validator.passed(), "空字符串应该通过验证");
    }
}