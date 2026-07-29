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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件大小链式验证测试类
 */
public class FileSizeValidationChainTest {

    @Test
    public void testValidFileSizeWithMaxOnly() throws IOException {
        // 创建一个大小为1KB的临时文件
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[1024]; // 1KB
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "2KB");
            assertTrue(chain.passed(), "1KB文件应该通过2KB最大值验证");
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testInvalidFileSizeWithMaxOnly() throws IOException {
        // 创建一个大小为3KB的临时文件
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[3 * 1024]; // 3KB
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "2KB");
            assertFalse(chain.passed(), "3KB文件不应该通过2KB最大值验证");
            assertEquals(1, chain.getErrors().size());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testValidFileSizeWithRange() throws IOException {
        // 创建一个大小为1KB的临时文件
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[1024]; // 1KB
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "512B", "2KB");
            assertTrue(chain.passed(), "1KB文件应该通过512B-2KB范围验证");
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testInvalidFileSizeTooSmall() throws IOException {
        // 创建一个大小为256B的临时文件
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[256]; // 256B
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "512B", "2KB");
            assertFalse(chain.passed(), "256B文件不应该通过512B最小值验证");
            assertEquals(1, chain.getErrors().size());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testInvalidFileSizeTooLarge() throws IOException {
        // 创建一个大小为3KB的临时文件
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[3 * 1024]; // 3KB
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "512B", "2KB");
            assertFalse(chain.passed(), "3KB文件不应该通过2KB最大值验证");
            assertEquals(1, chain.getErrors().size());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testFileSizeWithPath() throws IOException {
        // 测试Path类型
        Path tempPath = Files.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[1024]; // 1KB
            Files.write(tempPath, content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempPath, "2KB");
            assertTrue(chain.passed(), "Path对象应该正确验证");
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    @Test
    public void testFileSizeWithByteArray() {
        // 测试byte[]类型
        byte[] data = new byte[1024]; // 1KB

        ValidaX chain = ValidaX.init();
        chain = chain.isFileSize(data, "2KB");
        assertTrue(chain.passed(), "byte[]应该正确验证");

        chain = ValidaX.init();
        chain = chain.isFileSize(data, "512B");
        assertFalse(chain.passed(), "byte[]超过大小限制应该失败");
    }

    @Test
    public void testFileSizeWithMBUnit() throws IOException {
        // 测试MB单位
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[2 * 1024 * 1024]; // 2MB
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "1MB", "5MB");
            assertTrue(chain.passed(), "2MB文件应该通过1MB-5MB范围验证");
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testFileSizeWithDecimalValue() throws IOException {
        // 测试小数值
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[(int)(1.5 * 1024 * 1024)]; // 1.5MB
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "2MB");
            assertTrue(chain.passed(), "1.5MB文件应该通过2MB最大值验证");
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testMultipleFileSizeValidations() throws IOException {
        // 测试多个验证
        File tempFile = File.createTempFile("test", ".tmp");
        try {
            byte[] content = new byte[1024]; // 1KB
            Files.write(tempFile.toPath(), content);

            ValidaX chain = ValidaX.init();
            chain = chain.isFileSize(tempFile, "512B", "2KB")
                    .isFileSize(tempFile, "5KB"); // 第二个验证也应该通过
            assertTrue(chain.passed(), "所有验证都应该通过");
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testFileSizeWithNullValue() {
        // 测试null值
        ValidaX chain = ValidaX.init();
        chain = chain.isFileSize((Object) null, "10MB");
        assertTrue(chain.passed(), "null值应该通过验证（由@NotNull处理）");
    }
}
