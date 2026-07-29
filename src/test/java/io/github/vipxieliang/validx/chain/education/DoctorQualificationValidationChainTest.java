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

package io.github.vipxieliang.validx.chain.education;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 医师资格证链式验证测试类
 */
public class DoctorQualificationValidationChainTest {

    @Test
    public void testValidDoctorQualification() {
        ValidaX validator = ValidaX.init();
        
        // 测试有效的医师资格证编号
        validator.isDoctor("202511110440608197310039910");
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidDoctorQualification() {
        ValidaX validator = ValidaX.init();
        
        // 测试无效的医师资格证编号
        validator.isDoctor("20251111012345678901234"); // 23位
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullValue() {
        ValidaX validator = ValidaX.init();

        // 测试null值
        validator.isDoctor(null);
        assertTrue(validator.passed());
    }

    @Test
    public void testEmptyValue() {
        ValidaX validator = ValidaX.init();

        // 测试空字符串
        validator.isDoctor("");
        assertTrue(validator.passed());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidaX validator = ValidaX.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isDoctor("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Invalid doctor qualification number"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidaX validator = ValidaX.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isDoctor("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("无效的医师资格证编号"));
    }
}