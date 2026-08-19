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

package io.github.vipxieliang.validx.util;

import io.github.vipxieliang.validx.enums.FileSizeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件大小工具类
 */
public class FileSizeUtils {

    /**
     * 文件大小字符串格式正则表达式
     * 支持格式：10MB, 1.5GB, 500KB, 100B, 2TB
     */
    private static final Pattern SIZE_PATTERN = Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*(B|KB|MB|GB|TB)$", Pattern.CASE_INSENSITIVE);

    /**
     * 解析文件大小字符串为字节数
     *
     * @param sizeStr 文件大小字符串，如 "10MB", "1.5GB", "500KB"
     * @return 字节数
     * @throws IllegalArgumentException 如果格式不正确
     */
    public static long parseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("File size string cannot be null or empty");
        }

        sizeStr = sizeStr.trim();
        Matcher matcher = SIZE_PATTERN.matcher(sizeStr);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid file size format: " + sizeStr + ". Expected format: 10MB, 1.5GB, etc.");
        }

        double value = Double.parseDouble(matcher.group(1));
        String unitStr = matcher.group(2).toUpperCase();

        FileSizeUnit unit = FileSizeUnit.valueOf(unitStr);
        return (long) (value * unit.getBytes());
    }

    /**
     * 格式化字节数为人类可读的字符串
     *
     * @param bytes 字节数
     * @return 格式化后的字符串，如 "10.5 MB", "2.3 GB"
     */
    public static String formatSize(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("Bytes cannot be negative");
        }

        if (bytes < FileSizeUnit.KB.getBytes()) {
            return bytes + " B";
        } else if (bytes < FileSizeUnit.MB.getBytes()) {
            return String.format("%.2f KB", bytes / (double) FileSizeUnit.KB.getBytes());
        } else if (bytes < FileSizeUnit.GB.getBytes()) {
            return String.format("%.2f MB", bytes / (double) FileSizeUnit.MB.getBytes());
        } else if (bytes < FileSizeUnit.TB.getBytes()) {
            return String.format("%.2f GB", bytes / (double) FileSizeUnit.GB.getBytes());
        } else {
            return String.format("%.2f TB", bytes / (double) FileSizeUnit.TB.getBytes());
        }
    }

    /**
     * 验证文件大小是否在指定范围内
     *
     * @param fileSize 文件大小（字节）
     * @param minSize 最小大小（字节）
     * @param maxSize 最大大小（字节）
     * @return 是否在范围内
     */
    public static boolean isWithinRange(long fileSize, long minSize, long maxSize) {
        return fileSize >= minSize && fileSize <= maxSize;
    }
}
