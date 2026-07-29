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

package io.github.vipxieliang.validx.chain.foreign;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ForeignerWorkPermitValidationChainTest {

    @Test
    public void testValidForeignerWorkPermit() {
        ValidaX chain = ValidaX.init();
        chain = chain.isForeignerWorkPermit((Object)"123456");
        assertTrue(chain.passed(), "有效的外国人工作许可证应该通过验证");
    }

    @Test
    public void testInvalidForeignerWorkPermit() {
        ValidaX chain = ValidaX.init();
        chain = chain.isForeignerWorkPermit((Object)"12345"); // 长度不足6位
        assertFalse(chain.passed(), "无效的外国人工作许可证不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("外国人工作许可证号码格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyForeignerWorkPermit() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isForeignerWorkPermit((Object)null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidaX.init();
        chain = chain.isForeignerWorkPermit((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}