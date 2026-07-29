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

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 纬度验证器链测试类
 */
public class LatitudeValidationChainTest {

    @Test
    public void testNullAndEmptyLatitude() {
        // 测试 null 值
        ValidationPlus validator = ValidationPlus.init();
        validator.isLatitude(null);
        assertTrue(validator.passed());

        // 测试空字符串
        validator = ValidationPlus.init();
        validator.isLatitude("");
        assertTrue(validator.passed());
    }

    @Test
    public void testValidLatitude() {
        ValidationPlus validator = ValidationPlus.init();

        // 测试有效的纬度值
        validator.isLatitude("0");
        assertTrue(validator.passed());
        
        validator = ValidationPlus.init();
        validator.isLatitude("39.9042"); // 北京纬度
        assertTrue(validator.passed());
        
        validator = ValidationPlus.init();
        validator.isLatitude("-39.9042");
        assertTrue(validator.passed());
        
        validator = ValidationPlus.init();
        validator.isLatitude("90");
        assertTrue(validator.passed());
        
        validator = ValidationPlus.init();
        validator.isLatitude("-90");
        assertTrue(validator.passed());
        
        validator = ValidationPlus.init();
        validator.isLatitude("89.999999");
        assertTrue(validator.passed());
        
        validator = ValidationPlus.init();
        validator.isLatitude("-89.999999");
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidLatitude() {
        ValidationPlus validator = ValidationPlus.init();
        
        // 测试无效的纬度值
        validator.isLatitude("91");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidationPlus.init();
        validator.isLatitude("-91");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidationPlus.init();
        validator.isLatitude("100");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidationPlus.init();
        validator.isLatitude("-100");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidationPlus.init();
        validator.isLatitude("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidationPlus.init();
        validator.isLatitude("90.1");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        
        validator = ValidationPlus.init();
        validator.isLatitude("-90.1");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidationPlus validator = ValidationPlus.init().withLocale(Locale.ENGLISH);
        
        validator.isLatitude("91");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Latitude value is not within valid range"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidationPlus validator = ValidationPlus.init().withLocale(Locale.CHINESE);
        
        validator.isLatitude("91");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("纬度值不在有效范围内"));
    }
}