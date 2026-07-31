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

import static org.junit.jupiter.api.Assertions.*;

/**
 * SWIFT/BIC代码链式验证测试类
 */
public class SWIFTValidationChainTest {

    @Test
    public void testValidSWIFTCode() {
        ValidX validator = ValidX.init();
        
        // 测试有效的SWIFT代码
        validator.isSWIFT("COBADEFF");
        assertTrue(validator.passed());
        assertEquals(0, validator.getErrors().size());
        
        validator.isSWIFT("DEUTDEFFXXX");
        assertTrue(validator.passed());
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testInvalidSWIFTCode() {
        ValidX validator = ValidX.init();
        
        // 测试无效的SWIFT代码 (长度不足)
        validator.isSWIFT("INVALID");
        assertFalse(validator.passed()); // 长度不足会触发验证器错误
        assertEquals(1, validator.getErrors().size());
        
        // 清除错误继续测试
        validator = ValidX.init();
        validator.isSWIFT("COBADEF$"); // 包含特殊字符
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullAndEmptyValues() {
        ValidX validator = ValidX.init();
        
        // 测试null值
        validator.isSWIFT(null);
        assertTrue(validator.passed());
        assertEquals(0, validator.getErrors().size());
        
        // 测试空字符串
        validator.isSWIFT("");
        assertTrue(validator.passed());
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testMultipleValidations() {
        ValidX validator = ValidX.init();
        
        // 测试多个验证
        validator.isSWIFT("COBADEFF")
                 .isSWIFT("DEUTDEFFXXX");
                 
        assertTrue(validator.passed());
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testMixedValidations() {
        ValidX validator = ValidX.init();
        
        // 测试混合验证（有效和无效）
        validator.isSWIFT("COBADEFF")  // 有效
                 .isSWIFT("COBADEF$");  // 无效，包含特殊字符
                 
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }
}