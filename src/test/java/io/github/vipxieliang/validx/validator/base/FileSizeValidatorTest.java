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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件大小验证器测试类
 */
public class FileSizeValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === File 类型测试 ===

    @Test
    public void testValidFileSize_WithStringFormat() throws IOException {
        File tempFile = createTempFile(5 * 1024); // 5KB
        try {
            TestDTOFile dto = new TestDTOFile();
            dto.file = tempFile;

            Set<ConstraintViolation<TestDTOFile>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "5KB文件应该通过验证（1KB-10MB）");
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testInvalidFileSize_TooSmall() throws IOException {
        File tempFile = createTempFile(512); // 512B < 1KB
        try {
            TestDTOFile dto = new TestDTOFile();
            dto.file = tempFile;

            Set<ConstraintViolation<TestDTOFile>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "文件太小应该验证失败");
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testInvalidFileSize_TooLarge() throws IOException {
        File tempFile = createTempFile(11 * 1024 * 1024); // 11MB > 10MB
        try {
            TestDTOFile dto = new TestDTOFile();
            dto.file = tempFile;

            Set<ConstraintViolation<TestDTOFile>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "文件太大应该验证失败");
        } finally {
            tempFile.delete();
        }
    }

    // === Path 类型测试 ===

    @Test
    public void testValidPathSize() throws IOException {
        Path tempPath = Files.createTempFile("test", ".tmp");
        try {
            Files.write(tempPath, new byte[2 * 1024]); // 2KB

            TestDTOPath dto = new TestDTOPath();
            dto.path = tempPath;

            Set<ConstraintViolation<TestDTOPath>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "2KB文件应该通过验证");
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    @Test
    public void testInvalidPathSize() throws IOException {
        Path tempPath = Files.createTempFile("test", ".tmp");
        try {
            Files.write(tempPath, new byte[6 * 1024]); // 6KB > 5KB

            TestDTOPath dto = new TestDTOPath();
            dto.path = tempPath;

            Set<ConstraintViolation<TestDTOPath>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "6KB文件应该验证失败（max 5KB）");
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    // === byte[] 类型测试 ===

    @Test
    public void testValidByteArraySize() {
        TestDTOByteArray dto = new TestDTOByteArray();
        dto.data = new byte[512 * 1024]; // 512KB

        Set<ConstraintViolation<TestDTOByteArray>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "512KB数据应该通过验证（max 1MB）");
    }

    @Test
    public void testInvalidByteArraySize() {
        TestDTOByteArray dto = new TestDTOByteArray();
        dto.data = new byte[2 * 1024 * 1024]; // 2MB > 1MB

        Set<ConstraintViolation<TestDTOByteArray>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "2MB数据应该验证失败（max 1MB）");
    }

    // === Null 值测试 ===

    @Test
    public void testNullFile() {
        TestDTOFile dto = new TestDTOFile();
        dto.file = null;

        Set<ConstraintViolation<TestDTOFile>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null文件应该通过验证（由@NotNull处理）");
    }

    // === 辅助方法 ===

    private File createTempFile(int size) throws IOException {
        File file = File.createTempFile("test", ".tmp");
        Files.write(file.toPath(), new byte[size]);
        return file;
    }

    // === 测试DTO类 ===

    static class TestDTOFile {
        @FileSize(min = "1KB", max = "10MB")
        File file;
    }

    static class TestDTOPath {
        @FileSize(max = "5KB")
        Path path;
    }

    static class TestDTOByteArray {
        @FileSize(max = "1MB")
        byte[] data;
    }
}
