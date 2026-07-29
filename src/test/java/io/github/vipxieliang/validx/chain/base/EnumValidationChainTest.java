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

public class EnumValidationChainTest {

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
    public void testValidEnumWithNameField() {
        ValidaX chain = ValidaX.init();
        chain = chain.isEnum("VALUE1", TestEnum.class);
        assertTrue(chain.passed(), "有效的枚举值应该通过验证");

        chain = ValidaX.init();
        chain = chain.isEnum("VALUE2", TestEnum.class);
        assertTrue(chain.passed(), "有效的枚举值应该通过验证");
    }

    @Test
    public void testInvalidEnumWithNameField() {
        ValidaX chain = ValidaX.init();
        chain = chain.isEnum("INVALID_VALUE", TestEnum.class);
        assertFalse(chain.passed(), "无效的枚举值不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("无效的枚举值", chain.getErrors().get(0));
    }

    @Test
    public void testValidEnumWithCustomField() {
        ValidaX chain = ValidaX.init();
        chain = chain.isEnum("code1", TestEnum.class, "code");
        assertTrue(chain.passed(), "有效的枚举值应该通过验证");

        chain = ValidaX.init();
        chain = chain.isEnum("code2", TestEnum.class, "code");
        assertTrue(chain.passed(), "有效的枚举值应该通过验证");
    }

    @Test
    public void testInvalidEnumWithCustomField() {
        ValidaX chain = ValidaX.init();
        chain = chain.isEnum("invalid_code", TestEnum.class, "code");
        assertFalse(chain.passed(), "无效的枚举值不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("无效的枚举值", chain.getErrors().get(0));
    }

    @Test
    public void testNullValue() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isEnum(null, TestEnum.class);
        assertTrue(chain.passed(), "null值应该通过验证");
    }

    @Test
    public void testEmptyStringValue() {
        // 测试空字符串
        ValidaX chain = ValidaX.init();
        chain = chain.isEnum("", TestEnum.class);
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}