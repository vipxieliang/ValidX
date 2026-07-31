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

public class HKMacauResidenceValidationChainTest {

    @Test
    public void testValidHKMacauResidence() {
        ValidX chain = ValidX.init();
        chain = chain.isHKMacauResidence("81000000000000001");
        assertTrue(chain.passed(), "有效的港澳居民居住证号码应该通过验证");

        chain = ValidX.init();
        chain = chain.isHKMacauResidence("8200000000000000X");
        assertTrue(chain.passed(), "有效的港澳居民居住证号码应该通过验证");
    }

    @Test
    public void testInvalidHKMacauResidence() {
        ValidX chain = ValidX.init();
        chain = chain.isHKMacauResidence("830000000000000001"); // 前缀错误
        assertFalse(chain.passed(), "无效的港澳居民居住证号码应该验证失败");

        chain = ValidX.init();
        chain = chain.isHKMacauResidence("810000000000001"); // 太短
        assertFalse(chain.passed(), "无效的港澳居民居住证号码应该验证失败");
    }

    @Test
    public void testNullAndEmptyHKMacauResidence() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isHKMacauResidence(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isHKMacauResidence("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
