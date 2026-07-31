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

package io.github.vipxieliang.validx.chain.vehicle;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VinValidationChainTest {

    @Test
    public void testValidVin() {
        ValidX chain = ValidX.init();
        chain = chain.isVIN("1M8GDM9AXKP042788");
        assertTrue(chain.passed());
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testInvalidVin() {
        ValidX chain = ValidX.init();
        chain = chain.isVIN("invalid-vin");
        assertFalse(chain.passed());
        assertEquals(1, chain.getErrors().size());
        assertEquals("VIN码格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyVIN() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isVIN(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isVIN("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}