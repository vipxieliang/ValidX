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

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 建造师证书链式验证测试类
 */
public class ConstructorValidationChainTest {

    @Test
    public void testValidConstructor() {
        ValidaX validator = ValidaX.init();
        
        // 测试有效的一级建造师证书编号
        validator.isConstructor("京111050700001");
        assertTrue(validator.passed());
        
        validator = ValidaX.init();
        // 测试有效的二级建造师证书编号
        validator.isConstructor("鄂242050700001");
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidConstructor() {
        ValidaX validator = ValidaX.init();
        
        // 测试无效的建造师证书编号
        validator.isConstructor("京311050700001"); // 级别代码为3
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullValue() {
        ValidaX validator = ValidaX.init();

        // 测试null值
        validator.isConstructor(null);
        assertTrue(validator.passed());
    }

    @Test
    public void testEmptyValue() {
        ValidaX validator = ValidaX.init();

        // 测试空字符串
        validator.isConstructor("");
        assertTrue(validator.passed());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidaX validator = ValidaX.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isConstructor("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Invalid constructor certificate number"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidaX validator = ValidaX.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isConstructor("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("无效的建造师证书编号"));
    }
}