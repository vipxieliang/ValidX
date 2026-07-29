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
import org.junit.jupiter.api.io.TempDir;

import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FileSizePathValidator 单元测试
 *
 * 测试文件大小验证器（Path版本）的基本功能：
 * 1. null 应该返回 true
 * 2. 有效的文件大小应该返回 true
 * 3. 无效的文件大小应该返回 false
 * 4. 不存在的文件应该返回 false
 */
public class FileSizePathValidatorTest {

    private final FileSizePathValidator validator = new FileSizePathValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @TempDir
    Path tempDir;

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
    public void testValidFileSize() throws IOException {
        // 准备注解：0B - 10KB
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 创建临时文件并写入数据
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.write(emptyFile, new byte[0]);
        assertTrue(validator.isValid(emptyFile, context), "0 bytes file should be valid");

        Path smallFile = tempDir.resolve("small.txt");
        Files.write(smallFile, new byte[1024]); // 1KB
        assertTrue(validator.isValid(smallFile, context), "1KB file should be valid");

        Path mediumFile = tempDir.resolve("medium.txt");
        Files.write(mediumFile, new byte[5120]); // 5KB
        assertTrue(validator.isValid(mediumFile, context), "5KB file should be valid");

        Path maxFile = tempDir.resolve("max.txt");
        Files.write(maxFile, new byte[10240]); // 10KB
        assertTrue(validator.isValid(maxFile, context), "10KB file should be valid");
    }

    @Test
    public void testInvalidFileSize() throws IOException {
        // 准备注解：1KB - 10KB
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("1KB");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 创建临时文件并写入数据
        Path tooSmall = tempDir.resolve("too_small.txt");
        Files.write(tooSmall, new byte[512]); // 512B < 1KB
        assertFalse(validator.isValid(tooSmall, context), "512 bytes file should be invalid");

        Path tooLarge = tempDir.resolve("too_large.txt");
        Files.write(tooLarge, new byte[10241]); // 10KB + 1 byte
        assertFalse(validator.isValid(tooLarge, context), "10KB+1byte file should be invalid");
    }

    @Test
    public void testNonExistentFile() {
        // 准备注解
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 不存在的文件应该返回 false
        Path nonExistent = tempDir.resolve("non_existent.txt");
        assertFalse(validator.isValid(nonExistent, context), "non-existent file should be invalid");
    }

    @Test
    public void testDirectory() throws IOException {
        // 准备注解
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 目录应该返回 false
        Path directory = tempDir.resolve("test_dir");
        Files.createDirectory(directory);
        assertFalse(validator.isValid(directory, context), "directory should be invalid");
    }
}
