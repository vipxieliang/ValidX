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

public class InValidationMessageTest {

    @Test
    public void testInValidationMessage() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIn((Object)"grape", new String[]{"apple", "banana", "orange"});
        assertFalse(chain.passed(), "无效的值不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertTrue(chain.getErrors().get(0).contains("无效值"), "错误消息应包含'无效值'");
    }

    @Test
    public void testNotInValidationMessage() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isNotIn((Object)"apple", new String[]{"apple", "banana", "orange"});
        assertFalse(chain.passed(), "包含禁止值不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertTrue(chain.getErrors().get(0).contains("值不能在指定范围内"), "错误消息应包含'值不能在指定范围内'");
    }
}