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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件大小验证器 - MultipartFile版本
 * 验证文件大小是否在指定范围内
 * 支持org.springframework.web.multipart.MultipartFile类型的对象
 * 使用反射实现，不需要强依赖Spring
 */
public class FileSizeMultipartFileValidator implements ConstraintValidator<FileSize, Object> {

    private long minBytes;
    private long maxBytes;
    private Set<String> allowedTypes;

    // MultipartFile类的引用（通过反射获取）
    private static Class<?> multipartFileClass;
    private static Method getSizeMethod;
    private static Method getContentTypeMethod;

    static {
        try {
            // 尝试加载MultipartFile类
            multipartFileClass = Class.forName("org.springframework.web.multipart.MultipartFile");
            getSizeMethod = multipartFileClass.getMethod("getSize");
            getContentTypeMethod = multipartFileClass.getMethod("getContentType");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // Spring依赖不存在，验证器将不会被使用
            multipartFileClass = null;
        }
    }

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

        // 初始化允许的MIME类型
        String[] types = constraintAnnotation.allowedTypes();
        if (types != null && types.length > 0) {
            this.allowedTypes = new HashSet<>(Arrays.asList(types));
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
    public boolean isValid(Object file, ConstraintValidatorContext context) {
        // 如果文件为空，则视为通过验证（将由@NotNull等其他注解处理）
        if (file == null) {
            return true;
        }

        // 如果MultipartFile类不存在，跳过验证
        if (multipartFileClass == null) {
            return true;
        }

        // 检查是否为MultipartFile实例
        if (!multipartFileClass.isInstance(file)) {
            return true; // 不是MultipartFile类型，跳过验证
        }

        try {
            // 获取文件大小
            Long fileSize = (Long) getSizeMethod.invoke(file);
            if (fileSize == null) {
                return false;
            }

            // 检查文件大小是否在指定范围内
            if (!FileSizeUtils.isWithinRange(fileSize, minBytes, maxBytes)) {
                return false;
            }

            // 如果配置了MIME类型限制，进行验证
            if (allowedTypes != null && !allowedTypes.isEmpty()) {
                String contentType = (String) getContentTypeMethod.invoke(file);
                if (contentType == null || !allowedTypes.contains(contentType)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // 反射调用失败，视为验证失败
            return false;
        }
    }
}
