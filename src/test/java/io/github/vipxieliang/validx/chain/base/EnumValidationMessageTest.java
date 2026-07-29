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

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnumValidationMessageTest {

    // 测试枚举类
    public enum TestEnum {
        VALUE1("code1"),
        VALUE2("code2"),
        VALUE3("code3");

        private final String code;

        TestEnum(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    @Test
    public void testEnumValidationMessage() {
        ValidaX chain = ValidaX.init();
        chain = chain.isEnum("INVALID_VALUE", TestEnum.class);
        assertFalse(chain.passed(), "无效的枚举值不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertTrue(chain.getErrors().get(0).contains("无效的枚举值"), "错误消息应包含'无效的枚举值'");
    }
}