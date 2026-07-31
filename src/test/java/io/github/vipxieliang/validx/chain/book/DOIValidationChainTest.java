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

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DOI验证器链测试类
 */
public class DOIValidationChainTest {

    @Test
    public void testValidDOI() {
        ValidX validator = ValidX.init();
        validator.isDOI("10.1000/182");
        
        assertTrue(validator.passed());
    }

    @Test
    public void testValidDOIPrefix() {
        ValidX validator = ValidX.init();
        validator.isDOI("doi:10.1000/182");
        
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidDOI() {
        ValidX validator = ValidX.init();
        validator.isDOI("invalid-doi");

        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullAndEmptyDOI() {
        // 测试 null 值
        ValidX validator = ValidX.init();
        validator.isDOI(null);
        assertTrue(validator.passed(), "null 应该通过验证");

        // 测试空字符串
        validator = ValidX.init();
        validator.isDOI("");
        assertTrue(validator.passed(), "空字符串应该通过验证");
    }
}