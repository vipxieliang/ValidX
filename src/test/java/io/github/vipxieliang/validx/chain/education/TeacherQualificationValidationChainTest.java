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

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 教师资格证链式验证测试类
 */
public class TeacherQualificationValidationChainTest {

    @Test
    public void testValidTeacherQualification() {
        ValidationPlus validator = ValidationPlus.init();
        
        // 测试有效的教师资格证编号 (符合所有规则)
        validator.isTeacher("20251112312345678"); // 北京市，小学教师，男性
        assertTrue(validator.passed());
    }

    @Test
    public void testInvalidTeacherQualification() {
        ValidationPlus validator = ValidationPlus.init();
        
        // 测试无效的教师资格证编号
        validator.isTeacher("2025341234567890"); // 16位
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testNullValue() {
        ValidationPlus validator = ValidationPlus.init();

        // 测试null值
        validator.isTeacher(null);
        assertTrue(validator.passed());
    }

    @Test
    public void testEmptyValue() {
        ValidationPlus validator = ValidationPlus.init();

        // 测试空字符串
        validator.isTeacher("");
        assertTrue(validator.passed());
    }

    @Test
    public void testEnglishErrorMessage() {
        ValidationPlus validator = ValidationPlus.init().withLocale(Locale.ENGLISH);
        
        // 测试英文错误消息
        validator.isTeacher("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("Invalid teacher qualification number"));
    }

    @Test
    public void testChineseErrorMessage() {
        ValidationPlus validator = ValidationPlus.init().withLocale(Locale.CHINESE);
        
        // 测试中文错误消息
        validator.isTeacher("invalid");
        assertFalse(validator.passed());
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrors().get(0).contains("无效的教师资格证编号"));
    }
}