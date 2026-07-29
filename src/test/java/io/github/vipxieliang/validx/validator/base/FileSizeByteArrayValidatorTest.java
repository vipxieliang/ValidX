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

import io.github.vipxieliang.validx.annotations.FileSize;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FileSizeByteArrayValidator 单元测试
 *
 * 测试字节数组大小验证器的基本功能：
 * 1. null 应该返回 true
 * 2. 有效的字节数组大小应该返回 true
 * 3. 无效的字节数组大小应该返回 false
 */
public class FileSizeByteArrayValidatorTest {

    private final FileSizeByteArrayValidator validator = new FileSizeByteArrayValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    public void testNullValue() {
        // 准备注解
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // null 应该返回 true（由 @NotNull 注解处理）
        assertTrue(validator.isValid(null, context), "null should return true");
    }

    @Test
    public void testValidByteArraySize() {
        // 准备注解：0B - 10KB
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 有效的字节数组大小
        assertTrue(validator.isValid(new byte[0], context), "0 bytes should be valid");
        assertTrue(validator.isValid(new byte[1024], context), "1KB should be valid");
        assertTrue(validator.isValid(new byte[5120], context), "5KB should be valid");
        assertTrue(validator.isValid(new byte[10240], context), "10KB should be valid");
    }

    @Test
    public void testInvalidByteArraySize() {
        // 准备注解：1KB - 10KB
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("1KB");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 无效的字节数组大小
        assertFalse(validator.isValid(new byte[512], context), "512 bytes should be invalid (< 1KB)");
        assertFalse(validator.isValid(new byte[10241], context), "10KB+1byte should be invalid");
        assertFalse(validator.isValid(new byte[20480], context), "20KB should be invalid");
    }

    @Test
    public void testDifferentSizeRanges() {
        // 测试不同的大小范围：100B - 1MB
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("100B");
        when(annotation.max()).thenReturn("1MB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        assertTrue(validator.isValid(new byte[100], context), "100B should be valid");
        assertTrue(validator.isValid(new byte[1024 * 512], context), "512KB should be valid");
        assertTrue(validator.isValid(new byte[1024 * 1024], context), "1MB should be valid");
        assertFalse(validator.isValid(new byte[99], context), "99B should be invalid");
        assertFalse(validator.isValid(new byte[1024 * 1024 + 1], context), "1MB+1byte should be invalid");
    }
}
