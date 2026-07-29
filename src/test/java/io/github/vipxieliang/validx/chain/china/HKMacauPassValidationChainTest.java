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

public class HKMacauPassValidationChainTest {

    @Test
    public void testValidHKMacauPass() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isHKMacauPass("H1234567800");
        assertTrue(chain.passed(), "有效的回乡证号码应该通过验证");

        chain = ValidationPlus.init();
        chain = chain.isHKMacauPass("M1234567899");
        assertTrue(chain.passed(), "有效的回乡证号码应该通过验证");
    }

    @Test
    public void testInvalidHKMacauPass() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isHKMacauPass("H123456780"); // 太短
        assertFalse(chain.passed(), "无效的回乡证号码应该验证失败");

        chain = ValidationPlus.init();
        chain = chain.isHKMacauPass("A1234567800"); // 首字母错误
        assertFalse(chain.passed(), "无效的回乡证号码应该验证失败");
    }

    @Test
    public void testNullAndEmptyHKMacauPass() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isHKMacauPass(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isHKMacauPass("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
