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

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InValidationChainTest {

    @Test
    public void testNullValue() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIn(null, new String[]{"apple", "banana", "orange"});
        assertTrue(chain.passed(), "null值应该通过验证");
    }

    @Test
    public void testEmptyStringNotInArray() {
        // 测试空字符串（不在数组中）
        // 新行为：默认情况下，空字符串会跳过格式校验，验证通过
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIn((Object)"", new String[]{"apple", "banana", "orange"});
        assertTrue(chain.passed(), "空字符串默认跳过校验，应该通过");
    }

    @Test
    public void testEmptyStringInArray() {
        // 测试空字符串（在数组中）
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIn((Object)"", new String[]{"apple", "banana", ""});
        assertTrue(chain.passed(), "空字符串在数组中应该验证通过");
    }

    @Test
    public void testValidInWithDirectValue() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIn((Object)"apple", new String[]{"apple", "banana", "orange"});
        assertTrue(chain.passed(), "有效的值应该通过验证");
    }

    @Test
    public void testInvalidInWithDirectValue() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIn((Object)"grape", new String[]{"apple", "banana", "orange"});
        assertFalse(chain.passed(), "无效的值不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("无效值", chain.getErrors().get(0));
    }
}