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

import io.github.vipxieliang.validx.annotations.StartsWith;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * StartsWithValidator 单元测试
 *
 * 测试字符串前缀验证器的基本功能：
 * 1. null 和空字符串应该返回 true
 * 2. 有效的前缀应该返回 true
 * 3. 无效的前缀应该返回 false
 */
public class StartsWithValidatorTest {

    private final StartsWithValidator validator = new StartsWithValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    public void testNullAndEmptyString() {
        // 准备注解
        StartsWith annotation = mock(StartsWith.class);
        when(annotation.startsWith()).thenReturn("test");
        validator.initialize(annotation);

        // null 应该返回 true（由 @NotNull 注解处理）
        assertTrue(validator.isValid(null, context), "null should return true");

        // 空字符串应该返回 true（由 @NotEmpty 注解处理）
        assertTrue(validator.isValid("", context), "empty string should return true");
    }

    @Test
    public void testValidStartsWith() {
        // 准备注解
        StartsWith annotation = mock(StartsWith.class);
        when(annotation.startsWith()).thenReturn("test");
        validator.initialize(annotation);

        // 有效的前缀
        assertTrue(validator.isValid("test", context), "test should be valid");
        assertTrue(validator.isValid("test123", context), "test123 should be valid");
        assertTrue(validator.isValid("testfile.txt", context), "testfile.txt should be valid");
        assertTrue(validator.isValid("test_data", context), "test_data should be valid");
    }

    @Test
    public void testInvalidStartsWith() {
        // 准备注解
        StartsWith annotation = mock(StartsWith.class);
        when(annotation.startsWith()).thenReturn("test");
        validator.initialize(annotation);

        // 无效的前缀
        assertFalse(validator.isValid("file", context), "file should be invalid");
        assertFalse(validator.isValid("mytest", context), "mytest should be invalid");
        assertFalse(validator.isValid("_test", context), "_test should be invalid");
        assertFalse(validator.isValid("Test", context), "Test should be invalid (case sensitive)");
    }

    @Test
    public void testDifferentPrefixes() {
        // 测试不同的前缀
        StartsWith annotation1 = mock(StartsWith.class);
        when(annotation1.startsWith()).thenReturn("http://");
        validator.initialize(annotation1);

        assertTrue(validator.isValid("http://example.com", context), "http://example.com should be valid");
        assertFalse(validator.isValid("https://example.com", context), "https://example.com should be invalid");

        // 测试另一个前缀
        StartsWith annotation2 = mock(StartsWith.class);
        when(annotation2.startsWith()).thenReturn("file_");
        validator.initialize(annotation2);

        assertTrue(validator.isValid("file_001.txt", context), "file_001.txt should be valid");
        assertFalse(validator.isValid("doc_001.txt", context), "doc_001.txt should be invalid");
    }
}
