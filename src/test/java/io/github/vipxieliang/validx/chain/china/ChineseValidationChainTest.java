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

public class ChineseValidationChainTest {

    @Test
    public void testValidChinese() {
        ValidX chain = ValidX.init();
        chain = chain.isChinese((Object)"中文测试");
        assertTrue(chain.passed(), "有效的中文字符应该通过验证");
    }

    @Test
    public void testInvalidChinese() {
        ValidX chain = ValidX.init();
        chain = chain.isChinese((Object)"123");
        assertFalse(chain.passed(), "纯数字不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("输入不是有效的中文字符", chain.getErrors().get(0));

        chain = ValidX.init();
        chain = chain.isChinese((Object)"!@#$%");
        assertFalse(chain.passed(), "特殊字符不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("输入不是有效的中文字符", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyChinese() {
        // 测试null值应该通过验证（交给@NotNull处理）
        ValidX chain = ValidX.init();
        chain = chain.isChinese(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证（交给@NotEmpty处理）
        chain = ValidX.init();
        chain = chain.isChinese((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}