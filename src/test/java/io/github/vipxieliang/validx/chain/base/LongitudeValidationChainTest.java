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

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 经度验证器链测试类
 */
public class LongitudeValidationChainTest {

    @Test
    public void testNullAndEmptyLongitude() {
        // 测试 null 值
        ValidaX validator = ValidaX.init();
        validator.isLongitude(null);
        assertTrue(validator.passed());

        // 测试空字符串
        validator = ValidaX.init();
        validator.isLongitude("");
        assertTrue(validator.passed());
    }

    @Test
    public void testValidLongitude() {
        ValidaX validator = ValidaX.init();

        // 测试有效的经度值
        validator.isLongitude("0");
        assertTrue(validator.passed());
        
        validator = ValidaX.init();
        validator.isLongitude("116.4074"); // 北京经度
        assertTrue(validator.passed());
        
        validator = ValidaX.init();
        validator.isLongitude("-116.4074");
        assertTrue(validator.passed());
        
        validator = ValidaX.init();
        validator.isLongitude("180");
        assertTrue(validator.passed());
        
        validator = ValidaX.init();
        validator.isLongitude("-180");
        assertTrue(validator.passed());
        
        validator = ValidaX.init();
        validator.isLongitude("179.999999");
        assertTrue(validator.passed());
        
        validator = ValidaX.init();
        validator.isLongitude("-179.999999");
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidLongitude() {
        ValidaX validator = ValidaX.init();
        
        // 测试无效的经度值
        validator.isLongitude("181");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidaX.init();
        validator.isLongitude("-181");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidaX.init();
        validator.isLongitude("200");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidaX.init();
        validator.isLongitude("-200");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidaX.init();
        validator.isLongitude("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidaX.init();
        validator.isLongitude("180.1");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidaX.init();
        validator.isLongitude("-180.1");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidaX validator = ValidaX.init().withLocale(Locale.ENGLISH);
        
        validator.isLongitude("181");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Longitude value is not within valid range"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidaX validator = ValidaX.init().withLocale(Locale.CHINESE);
        
        validator.isLongitude("181");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("经度值不在有效范围内"));
    }
}