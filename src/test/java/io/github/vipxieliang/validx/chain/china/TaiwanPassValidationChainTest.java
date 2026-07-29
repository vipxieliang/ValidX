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

public class TaiwanPassValidationChainTest {

    @Test
    public void testValidTaiwanPass() {
        ValidaX chain = ValidaX.init();
        chain = chain.isTaiwanPass("1234567800");
        assertTrue(chain.passed(), "有效的台胞证号码应该通过验证");

        chain = ValidaX.init();
        chain = chain.isTaiwanPass("9876543299");
        assertTrue(chain.passed(), "有效的台胞证号码应该通过验证");
    }

    @Test
    public void testInvalidTaiwanPass() {
        ValidaX chain = ValidaX.init();
        chain = chain.isTaiwanPass("123456780"); // 太短
        assertFalse(chain.passed(), "无效的台胞证号码应该验证失败");

        chain = ValidaX.init();
        chain = chain.isTaiwanPass("12345678001"); // 太长
        assertFalse(chain.passed(), "无效的台胞证号码应该验证失败");
    }

    @Test
    public void testNullAndEmptyTaiwanPass() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isTaiwanPass(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidaX.init();
        chain = chain.isTaiwanPass("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
