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

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChineseSoldierValidationChainTest {

    @Test
    public void testValidChineseSoldier() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChineseSoldier((Object)"沈字第0100000号");
        assertTrue(chain.passed(), "有效的士兵证应该通过验证");
    }

    @Test
    public void testInvalidChineseSoldier() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChineseSoldier((Object)"沈字第010000号");
        assertFalse(chain.passed(), "无效的士兵证不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("士兵证号码格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试null值
        ValidaX validator = ValidaX.init();
        validator.isChineseSoldier((Object)null);
        assertTrue(validator.passed(), "null should pass validation");

        // 测试空字符串
        validator = ValidaX.init();
        validator.isChineseSoldier((Object)"");
        assertTrue(validator.passed(), "empty string should pass validation");
    }
}