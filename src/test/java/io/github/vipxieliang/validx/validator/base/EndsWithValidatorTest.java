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

import io.github.vipxieliang.validx.annotations.EndsWith;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * EndsWithValidator 单元测试
 *
 * 测试字符串后缀验证器的基本功能：
 * 1. null 和空字符串应该返回 true
 * 2. 有效的后缀应该返回 true
 * 3. 无效的后缀应该返回 false
 */
public class EndsWithValidatorTest {

    private final EndsWithValidator validator = new EndsWithValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    public void testNullAndEmptyString() {
        // 准备注解
        EndsWith annotation = mock(EndsWith.class);
        when(annotation.endsWith()).thenReturn(".txt");
        validator.initialize(annotation);

        // null 应该返回 true（由 @NotNull 注解处理）
        assertTrue(validator.isValid(null, context), "null should return true");

        // 空字符串应该返回 true（由 @NotEmpty 注解处理）
        assertTrue(validator.isValid("", context), "empty string should return true");
    }

    @Test
    public void testValidEndsWith() {
        // 准备注解
        EndsWith annotation = mock(EndsWith.class);
        when(annotation.endsWith()).thenReturn(".txt");
        validator.initialize(annotation);

        // 有效的后缀
        assertTrue(validator.isValid("file.txt", context), "file.txt should be valid");
        assertTrue(validator.isValid("document.txt", context), "document.txt should be valid");
        assertTrue(validator.isValid("test_data.txt", context), "test_data.txt should be valid");
        assertTrue(validator.isValid(".txt", context), ".txt should be valid");
    }

    @Test
    public void testInvalidEndsWith() {
        // 准备注解
        EndsWith annotation = mock(EndsWith.class);
        when(annotation.endsWith()).thenReturn(".txt");
        validator.initialize(annotation);

        // 无效的后缀
        assertFalse(validator.isValid("file.pdf", context), "file.pdf should be invalid");
        assertFalse(validator.isValid("document.doc", context), "document.doc should be invalid");
        assertFalse(validator.isValid("txt_file", context), "txt_file should be invalid");
        assertFalse(validator.isValid("file.TXT", context), "file.TXT should be invalid (case sensitive)");
    }

    @Test
    public void testDifferentSuffixes() {
        // 测试不同的后缀
        EndsWith annotation1 = mock(EndsWith.class);
        when(annotation1.endsWith()).thenReturn(".jpg");
        validator.initialize(annotation1);

        assertTrue(validator.isValid("image.jpg", context), "image.jpg should be valid");
        assertFalse(validator.isValid("image.png", context), "image.png should be invalid");

        // 测试另一个后缀
        EndsWith annotation2 = mock(EndsWith.class);
        when(annotation2.endsWith()).thenReturn("_backup");
        validator.initialize(annotation2);

        assertTrue(validator.isValid("file_backup", context), "file_backup should be valid");
        assertFalse(validator.isValid("backup_file", context), "backup_file should be invalid");
    }

    @Test
    public void testIgnoreCaseTrue() {
        // 准备注解 - 忽略大小写
        EndsWith annotation = mock(EndsWith.class);
        when(annotation.endsWith()).thenReturn(".txt");
        when(annotation.ignoreCase()).thenReturn(true);
        validator.initialize(annotation);

        // 大小写不同应该通过验证
        assertTrue(validator.isValid("file.TXT", context), "file.TXT should be valid with ignoreCase");
        assertTrue(validator.isValid("file.txt", context), "file.txt should be valid with ignoreCase");
        assertTrue(validator.isValid("file.TxT", context), "file.TxT should be valid with ignoreCase");
    }

    @Test
    public void testIgnoreCaseFalse() {
        // 准备注解 - 区分大小写
        EndsWith annotation = mock(EndsWith.class);
        when(annotation.endsWith()).thenReturn(".txt");
        when(annotation.ignoreCase()).thenReturn(false);
        validator.initialize(annotation);

        // 大小写不同应该失败
        assertFalse(validator.isValid("file.TXT", context), "file.TXT should be invalid with case sensitive");
        assertTrue(validator.isValid("file.txt", context), "file.txt should be valid");
        assertFalse(validator.isValid("file.TxT", context), "file.TxT should be invalid with case sensitive");
    }

    @Test
    public void testDirectInitializeWithIgnoreCase() {
        // 直接初始化验证器（用于链式调用）- 忽略大小写
        validator.initialize(".jpg", true);

        assertTrue(validator.isValid("image.JPG", context), "image.JPG should be valid with ignoreCase");
        assertTrue(validator.isValid("image.Jpg", context), "image.Jpg should be valid with ignoreCase");
        assertTrue(validator.isValid("image.jpg", context), "image.jpg should be valid with ignoreCase");
        assertFalse(validator.isValid("image.png", context), "image.png should be invalid");
    }

    @Test
    public void testDirectInitializeWithoutIgnoreCase() {
        // 直接初始化验证器（用于链式调用）- 区分大小写
        validator.initialize(".jpg", false);

        assertFalse(validator.isValid("image.JPG", context), "image.JPG should be invalid with case sensitive");
        assertFalse(validator.isValid("image.Jpg", context), "image.Jpg should be invalid with case sensitive");
        assertTrue(validator.isValid("image.jpg", context), "image.jpg should be valid");
    }
}
