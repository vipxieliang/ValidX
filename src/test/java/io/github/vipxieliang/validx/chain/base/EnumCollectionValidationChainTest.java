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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnumCollectionValidationChainTest {

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
    public void testValidEnumListWithNameField() {
        ValidaX chain = ValidaX.init();
        List<String> validValues = Arrays.asList("VALUE1", "VALUE2");
        chain = chain.isEnum(validValues, TestEnum.class);
        assertTrue(chain.passed(), "有效的枚举值列表应该通过验证");
    }

    @Test
    public void testInvalidEnumListWithNameField() {
        ValidaX chain = ValidaX.init();
        List<String> invalidValues = Arrays.asList("VALUE1", "INVALID_VALUE");
        chain = chain.isEnum(invalidValues, TestEnum.class);
        assertFalse(chain.passed(), "包含无效枚举值的列表不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("无效的枚举值", chain.getErrors().get(0));
    }

    @Test
    public void testValidEnumArrayWithCustomField() {
        ValidaX chain = ValidaX.init();
        String[] validValues = {"code1", "code2"};
        chain = chain.isEnum(validValues, TestEnum.class, "code");
        assertTrue(chain.passed(), "有效的枚举值数组应该通过验证");
    }

    @Test
    public void testInvalidEnumArrayWithCustomField() {
        ValidaX chain = ValidaX.init();
        String[] invalidValues = {"code1", "invalid_code"};
        chain = chain.isEnum(invalidValues, TestEnum.class, "code");
        assertFalse(chain.passed(), "包含无效枚举值的数组不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("无效的枚举值", chain.getErrors().get(0));
    }

    @Test
    public void testEmptyList() {
        ValidaX chain = ValidaX.init();
        List<String> emptyValues = Arrays.asList();
        chain = chain.isEnum(emptyValues, TestEnum.class);
        assertTrue(chain.passed(), "空列表应该通过验证");
    }

    @Test
    public void testEmptyArray() {
        ValidaX chain = ValidaX.init();
        String[] emptyValues = {};
        chain = chain.isEnum(emptyValues, TestEnum.class);
        assertTrue(chain.passed(), "空数组应该通过验证");
    }
}