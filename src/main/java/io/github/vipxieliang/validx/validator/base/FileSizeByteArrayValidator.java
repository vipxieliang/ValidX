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

/**
 * 文件大小验证器 - byte[]版本
 * 验证字节数组大小是否在指定范围内
 * 支持byte[]类型的对象
 */
public class FileSizeByteArrayValidator implements ConstraintValidator<FileSize, byte[]> {

    private long minBytes;
    private long maxBytes;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        initialize(constraintAnnotation.min(), constraintAnnotation.max());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param min 最小文件大小（如 "1KB"、"10MB"）
     * @param max 最大文件大小（如 "10MB"、"5GB"）
     */
    public void initialize(String min, String max) {
        this.minBytes = FileSizeUtils.parseSize(min);

        // 如果max为空字符串，表示无最大限制
        if (max != null && !max.trim().isEmpty()) {
            this.maxBytes = FileSizeUtils.parseSize(max);
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
    public boolean isValid(byte[] data, ConstraintValidatorContext context) {
        // 如果数据为空，则视为通过验证（将由@NotNull等其他注解处理）
        if (data == null) {
            return true;
        }

        // 获取数据大小
        long dataSize = data.length;

        // 检查数据大小是否在指定范围内
        return FileSizeUtils.isWithinRange(dataSize, minBytes, maxBytes);
    }
}
