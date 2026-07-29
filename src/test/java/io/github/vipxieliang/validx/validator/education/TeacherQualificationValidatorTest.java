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

package io.github.vipxieliang.validx.validator.education;

import io.github.vipxieliang.validx.annotations.Teacher;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * 教师资格证验证器测试类
 */
public class TeacherQualificationValidatorTest {

    @Test
    public void testValidTeacherQualificationNumbers() {
        TeacherValidator validator = new TeacherValidator();
        Teacher teacherQualification = mock(Teacher.class);
        validator.initialize(teacherQualification);

        // 测试有效的教师资格证编号 (17位数字，符合各部分规则)
        assertTrue(validator.isValid("20251112312345678", mock(ConstraintValidatorContext.class))); // 北京市，小学教师，男性
        assertTrue(validator.isValid("20203212322345678", mock(ConstraintValidatorContext.class))); // 江苏省，中学教师，女性
        assertTrue(validator.isValid("20194212332345678", mock(ConstraintValidatorContext.class))); // 湖北省，初级中学教师，未知性别
    }

    @Test
    public void testInvalidTeacherQualificationNumbers() {
        TeacherValidator validator = new TeacherValidator();
        Teacher teacherQualification = mock(Teacher.class);
        validator.initialize(teacherQualification);

        // 测试无效的教师资格证编号（不是17位）
        assertFalse(validator.isValid("2025341234567890", mock(ConstraintValidatorContext.class))); // 16位
        assertFalse(validator.isValid("202534123456789012", mock(ConstraintValidatorContext.class))); // 18位
        
        // 测试包含非数字字符
        assertFalse(validator.isValid("2025341234567890A", mock(ConstraintValidatorContext.class))); // 包含字母
        assertFalse(validator.isValid("2025341234567890!", mock(ConstraintValidatorContext.class))); // 包含特殊字符
        
        // 测试无效的年份
        assertFalse(validator.isValid("18001112312345678", mock(ConstraintValidatorContext.class))); // 年份过早
        assertFalse(validator.isValid("21001112312345678", mock(ConstraintValidatorContext.class))); // 年份过晚
        
        // 测试无效的省级代码
        assertFalse(validator.isValid("20259912312345678", mock(ConstraintValidatorContext.class))); // 无效省级代码99
        
        // 测试无效的教师资格类型代码
        assertFalse(validator.isValid("20251112382345678", mock(ConstraintValidatorContext.class))); // 无效类型代码8
        assertFalse(validator.isValid("20251112302345678", mock(ConstraintValidatorContext.class))); // 无效类型代码0
        
        // 测试无效的性别代码
        assertFalse(validator.isValid("20251112313345678", mock(ConstraintValidatorContext.class))); // 无效性别代码3
    }

    @Test
    public void testNullAndEmptyTeacher() {
        // 直接测试验证器，null 和空字符串应该返回 true
        TeacherValidator validator = new TeacherValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}