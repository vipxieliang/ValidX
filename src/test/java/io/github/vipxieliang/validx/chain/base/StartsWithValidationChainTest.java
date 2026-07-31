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

public class StartsWithValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isStartsWith(null, new String[]{"test"});
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isStartsWith((Object)"", new String[]{"test"});
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidStartsWith() {
        ValidX chain = ValidX.init();
        chain = chain.isStartsWith((Object)"test_file.txt", new String[]{"test"});
        assertTrue(chain.passed(), "以指定前缀开头的字符串应该通过验证");
        
        chain = ValidX.init();
        chain = chain.isStartsWith((Object)"document.pdf", new String[]{"doc", "test"});
        assertTrue(chain.passed(), "以指定前缀开头的字符串应该通过验证");
    }

    @Test
    public void testInvalidStartsWith() {
        ValidX chain = ValidX.init();
        chain = chain.isStartsWith((Object)"file_test.txt", new String[]{"test"});
        assertFalse(chain.passed(), "不以指定前缀开头的字符串不应该通过验证");
        
        chain = ValidX.init();
        chain = chain.isStartsWith((Object)"_testfile", new String[]{"test"});
        assertFalse(chain.passed(), "不以指定前缀开头的字符串不应该通过验证");
    }
}