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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChineseAlphaValidationChainTest {

    @Test
    public void testValidChineseAlpha() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChineseAlpha((Object)"汉字abc");
        assertTrue(chain.passed(), "有效的汉字字母组合应该通过验证");
    }

    @Test
    public void testInvalidChineseAlpha() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChineseAlpha((Object)"汉字123");
        assertFalse(chain.passed(), "包含数字的字符串不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("只能包含汉字和字母", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyChineseAlpha() {
        // 测试null值应该通过验证（交给@NotNull处理）
        ValidaX chain = ValidaX.init();
        chain = chain.isChineseAlpha(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证（交给@NotEmpty处理）
        chain = ValidaX.init();
        chain = chain.isChineseAlpha((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}