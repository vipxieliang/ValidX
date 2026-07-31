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

package io.github.vipxieliang.validx.chain.china;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChineseIdCardValidationChainTest {

    @Test
    public void testValidIdCard() {
        ValidX chain = ValidX.init();
        chain = chain.isChineseIdCard((Object)"110101199003072113");
        assertTrue(chain.passed(), "有效身份证号码应该通过验证");
    }

    @Test
    public void testInvalidIdCard() {
        ValidX chain = ValidX.init();
        chain = chain.isChineseIdCard((Object)"123456789012345678");
        assertFalse(chain.passed(), "无效身份证号码不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("身份证号码不正确", chain.getErrors().get(0));
    }
    
    @Test
    public void testNullAndEmptyValues() {
        // 测试null值
        ValidX validator = ValidX.init();
        validator.isChineseIdCard((Object)null);
        assertTrue(validator.passed(), "null should pass validation");

        // 测试空字符串
        validator = ValidX.init();
        validator.isChineseIdCard((Object)"");
        assertTrue(validator.passed(), "empty string should pass validation");
    }
}