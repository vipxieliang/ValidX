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

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QQValidationChainTest {

    @Test
    public void testValidQQ() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isQQ("10000");
        assertTrue(chain.passed(), "有效的QQ号应该通过验证");

        chain = ValidationPlus.init();
        chain = chain.isQQ("123456789");
        assertTrue(chain.passed(), "有效的QQ号应该通过验证");
    }

    @Test
    public void testInvalidQQ() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isQQ("1234"); // 太短
        assertFalse(chain.passed(), "无效的QQ号应该验证失败");

        chain = ValidationPlus.init();
        chain = chain.isQQ("01234"); // 以0开头
        assertFalse(chain.passed(), "无效的QQ号应该验证失败");
    }

    @Test
    public void testNullAndEmptyQQ() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isQQ(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isQQ("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
