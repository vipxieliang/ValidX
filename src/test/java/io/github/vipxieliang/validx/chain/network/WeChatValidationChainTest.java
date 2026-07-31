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

package io.github.vipxieliang.validx.chain.network;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 微信账号链式验证测试类
 */
public class WeChatValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidX validator = ValidX.init();
        validator.isWeChat(null);
        assertTrue(validator.passed(), "null值应该通过验证");

        // 测试空字符串
        validator = ValidX.init();
        validator.isWeChat("");
        assertTrue(validator.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidWeChat() {
        ValidX validator = ValidX.init();
        validator.isWeChat("wechat123");
        
        assertTrue(validator.passed(), "有效的微信号应该通过验证");
    }

    @Test
    public void testInvalidWeChat() {
        ValidX validator = ValidX.init();
        validator.isWeChat("123456"); // 数字开头，无效

        assertFalse(validator.passed(), "无效的微信号不应该通过验证");
        assertEquals(1, validator.getErrors().size());
        assertEquals("微信号格式不正确", validator.getErrors().get(0));
    }
}