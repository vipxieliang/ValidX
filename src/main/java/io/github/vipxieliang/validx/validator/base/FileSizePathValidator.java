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
import io.github.vipxieliang.validx.util.FileSizeUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件大小验证器 - Path版本
 * 验证文件大小是否在指定范围内
 * 支持java.nio.file.Path类型的对象
 */
public class FileSizePathValidator implements ConstraintValidator<FileSize, Path> {

    private long minBytes;
    private long maxBytes;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        // 解析字符串格式的文件大小
        String minStr = constraintAnnotation.min();
        String maxStr = constraintAnnotation.max();

        this.minBytes = FileSizeUtils.parseSize(minStr);

        // 如果max为空字符串，表示无最大限制
        if (maxStr != null && !maxStr.trim().isEmpty()) {
            this.maxBytes = FileSizeUtils.parseSize(maxStr);
        } else {
            this.maxBytes = Long.MAX_VALUE;
        }

        // 验证配置的合法性
        if (minBytes < 0) {
            throw new IllegalArgumentException("Minimum file size cannot be negative");
        }
        if (maxBytes < minBytes) {
            throw new IllegalArgumentException("Maximum file size cannot be less than minimum file size");
        }
    }

    @Override
    public boolean isValid(Path path, ConstraintValidatorContext context) {
        // 如果路径为空，则视为通过验证（将由@NotNull等其他注解处理）
        if (path == null) {
            return true;
        }

        // 检查文件是否存在
        if (!Files.exists(path)) {
            return false;
        }

        // 检查是否为常规文件（不是目录）
        if (!Files.isRegularFile(path)) {
            return false;
        }

        try {
            // 获取文件大小
            long fileSize = Files.size(path);

            // 检查文件大小是否在指定范围内
            return FileSizeUtils.isWithinRange(fileSize, minBytes, maxBytes);
        } catch (IOException e) {
            // 无法获取文件大小，视为验证失败
            return false;
        }
    }
}
