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

public class UnifiedSocialCreditCodeValidationChainTest {

    @Test
    public void testValidUnifiedSocialCreditCode() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isUnifiedSocialCreditCode("91350100M000100Y43");
        assertTrue(chain.passed(), "有效的统一社会信用代码应该通过验证");
    }

    @Test
    public void testInvalidUnifiedSocialCreditCode() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isUnifiedSocialCreditCode("91350100M000100Y4"); // 长度不足
        assertFalse(chain.passed(), "无效的统一社会信用代码应该验证失败");

        chain = ValidationPlus.init();
        chain = chain.isUnifiedSocialCreditCode("91350100M000100Y4I"); // 包含非法字符
        assertFalse(chain.passed(), "无效的统一社会信用代码应该验证失败");
    }

    @Test
    public void testNullAndEmptyUnifiedSocialCreditCode() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isUnifiedSocialCreditCode(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isUnifiedSocialCreditCode("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
