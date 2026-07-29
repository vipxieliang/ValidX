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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Latitude;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 纬度验证器测试类
 */
public class LatitudeValidatorTest {

    @Test
    public void testValidLatitude() {
        LatitudeValidator validator = new LatitudeValidator();
        Latitude latitude = mock(Latitude.class);
        validator.initialize(latitude);

        // 测试有效的纬度值
        assertTrue(validator.isValid("0", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("39.9042", mock(ConstraintValidatorContext.class))); // 北京纬度
        assertTrue(validator.isValid("-39.9042", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("90", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("-90", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("89.999999", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("-89.999999", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void testInvalidLatitude() {
        LatitudeValidator validator = new LatitudeValidator();
        Latitude latitude = mock(Latitude.class);
        validator.initialize(latitude);

        // 测试无效的纬度值
        assertFalse(validator.isValid("91", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("-91", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("100", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("-100", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("invalid", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("90.1", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("-90.1", mock(ConstraintValidatorContext.class)));
    }
}