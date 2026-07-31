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

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlphaDashValidationChainTest {

    @Test
    public void testValidAlphaDash() {
        ValidX chain = ValidX.init();
        chain = chain.isAlphaDash((Object)"abc-123_def");
        assertTrue(chain.passed(), "有效的字母数字下划线连字符组合应该通过验证");
        
        chain = ValidX.init();
        chain = chain.isAlphaDash((Object)"ABCDEF");
        assertTrue(chain.passed(), "有效的字母组合应该通过验证");
    }

    @Test
    public void testInvalidAlphaDash() {
        ValidX chain = ValidX.init();
        chain = chain.isAlphaDash((Object)"abc.def");
        assertFalse(chain.passed(), "包含点号的字符串不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("只能包含字母、数字、下划线和破折号", chain.getErrors().get(0));

        chain = ValidX.init();
        chain = chain.isAlphaDash((Object)"abc 123");
        assertFalse(chain.passed(), "包含空格的字符串不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("只能包含字母、数字、下划线和破折号", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyAlphaDash() {
        // 测试null值应该通过验证（交给@NotNull处理）
        ValidX chain = ValidX.init();
        chain = chain.isAlphaDash(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证（交给@NotEmpty处理）
        chain = ValidX.init();
        chain = chain.isAlphaDash((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}