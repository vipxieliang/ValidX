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

package io.github.vipxieliang.validx.validator.certification;

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 会计资格证书链式验证测试类
 */
public class AccountantValidationChainTest {

    @Test
    public void testValidAccountant() {
        ValidationPlus validator = ValidationPlus.init();
        
        // 测试有效的会计资格证书编号 (使用有效的省级代码)
        validator.isAccountant("21110203451");
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidAccountant() {
        ValidationPlus validator = ValidationPlus.init();
        
        // 测试无效的会计资格证书编号
        validator.isAccountant("2101020345"); // 10位数字
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullValue() {
        ValidationPlus validator = ValidationPlus.init();

        // 测试null值
        validator.isAccountant(null);
        assertTrue(validator.passed());
    }

    @Test
    public void testEmptyValue() {
        ValidationPlus validator = ValidationPlus.init();

        // 测试空字符串
        validator.isAccountant("");
        assertTrue(validator.passed());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidationPlus validator = ValidationPlus.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isAccountant("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Invalid accountant certificate number"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidationPlus validator = ValidationPlus.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isAccountant("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("无效的会计资格证书编号"));
    }
}