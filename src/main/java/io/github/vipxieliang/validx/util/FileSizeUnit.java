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

/**
 * 文件大小单位枚举
 */
public enum FileSizeUnit {
    /**
     * 字节
     */
    B(1L),

    /**
     * 千字节 (1KB = 1024 Bytes)
     */
    KB(1024L),

    /**
     * 兆字节 (1MB = 1024 KB)
     */
    MB(1024L * 1024L),

    /**
     * 千兆字节 (1GB = 1024 MB)
     */
    GB(1024L * 1024L * 1024L),

    /**
     * 太字节 (1TB = 1024 GB)
     */
    TB(1024L * 1024L * 1024L * 1024L);

    private final long bytes;

    FileSizeUnit(long bytes) {
        this.bytes = bytes;
    }

    /**
     * 获取该单位对应的字节数
     * @return 字节数
     */
    public long getBytes() {
        return bytes;
    }

    /**
     * 将指定值转换为字节
     * @param value 值
     * @return 字节数
     */
    public long toBytes(long value) {
        return value * bytes;
    }
}
