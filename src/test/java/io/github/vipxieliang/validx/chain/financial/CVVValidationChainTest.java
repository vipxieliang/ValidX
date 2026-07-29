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

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CVV验证器链测试类
 */
public class CVVValidationChainTest {

    @Test
    public void testValidCVV() {
        ValidaX validation = ValidaX.init();
        
        // 测试有效的CVV码
        validation.isCVV((Object)"123");
        assertTrue(validation.passed(), "有效的3位CVV码应该通过验证");
        
        validation = ValidaX.init();
        validation.isCVV((Object)"1234");
        assertTrue(validation.passed(), "有效的4位CVV码应该通过验证");
    }

    @Test
    public void testInvalidCVV() {
        ValidaX validation = ValidaX.init();
        
        // 测试无效的CVV码
        validation.isCVV((Object)"12");
        assertFalse(validation.passed(), "无效的2位CVV码不应该通过验证");
        assertEquals(1, validation.getErrors().size());
        
        validation = ValidaX.init();
        validation.isCVV((Object)"12345");
        assertFalse(validation.passed(), "无效的5位CVV码不应该通过验证");
        assertEquals(1, validation.getErrors().size());
        
        validation = ValidaX.init();
        validation.isCVV((Object)"12a");
        assertFalse(validation.passed(), "包含字母的CVV码不应该通过验证");
        assertEquals(1, validation.getErrors().size());
        
        validation = ValidaX.init();
        validation.isCVV((Object)"12#");
        assertFalse(validation.passed(), "包含特殊字符的CVV码不应该通过验证");
        assertEquals(1, validation.getErrors().size());
    }
    
    @Test
    public void testNullValue() {
        ValidaX validation = ValidaX.init();

        // 测试null值
        validation.isCVV(null);
        assertTrue(validation.passed(), "null值应该通过验证");

        // 测试空字符串
        validation = ValidaX.init();
        validation.isCVV((Object)"");
        assertTrue(validation.passed(), "空字符串应该通过验证");
    }
}