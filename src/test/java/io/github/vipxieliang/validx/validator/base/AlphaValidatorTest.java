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

import io.github.vipxieliang.validx.annotations.Alpha;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AlphaValidatorTest {

    @Test
    void testValidAlpha() {
        AlphaValidator validator = new AlphaValidator();
        Alpha alphaAnnotation = mock(Alpha.class);
        validator.initialize(alphaAnnotation);

        // 测试有效的字母字符串
        assertTrue(validator.isValid("abc", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("ABC", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("AbCdEf", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("a", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("Z", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testInvalidAlpha() {
        AlphaValidator validator = new AlphaValidator();
        Alpha alphaAnnotation = mock(Alpha.class);
        validator.initialize(alphaAnnotation);

        // 测试无效的字符串（包含数字或特殊字符）
        assertFalse(validator.isValid("abc123", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("123", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("abc-def", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("abc def", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("abc_def", mock(ConstraintValidatorContext.class)));
        assertFalse(validator.isValid("abc@def", mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testNullAndEmpty() {
        AlphaValidator validator = new AlphaValidator();
        Alpha alphaAnnotation = mock(Alpha.class);
        validator.initialize(alphaAnnotation);

        // 测试null和空值（应该返回true，交给@NotNull等注解处理）
        assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("", mock(ConstraintValidatorContext.class)));
    }
}
