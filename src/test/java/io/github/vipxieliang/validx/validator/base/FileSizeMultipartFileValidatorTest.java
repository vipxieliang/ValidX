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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FileSizeMultipartFileValidator 单元测试
 *
 * 测试文件大小验证器（MultipartFile版本）的基本功能：
 * 1. null 应该返回 true
 * 2. 有效的文件大小应该返回 true
 * 3. 无效的文件大小应该返回 false
 * 4. MIME类型限制应该正确工作
 */
public class FileSizeMultipartFileValidatorTest {

    private final FileSizeMultipartFileValidator validator = new FileSizeMultipartFileValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    /**
     * 创建一个模拟的MultipartFile对象
     * 使用动态代理避免直接依赖Spring
     */
    private Object createMockMultipartFile(final Long size, final String contentType) {
        try {
            Class<?> multipartFileClass = Class.forName("org.springframework.web.multipart.MultipartFile");
            return Proxy.newProxyInstance(
                multipartFileClass.getClassLoader(),
                new Class<?>[]{multipartFileClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("getSize".equals(method.getName())) {
                            return size;
                        } else if ("getContentType".equals(method.getName())) {
                            return contentType;
                        }
                        return null;
                    }
                }
            );
        } catch (ClassNotFoundException e) {
            // Spring不可用，返回null
            return null;
        }
    }

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
    public void testValidFileSize() {
        // 准备注解：0B - 10KB
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 模拟MultipartFile
        Object emptyFile = createMockMultipartFile(0L, null);
        if (emptyFile != null) {
            assertTrue(validator.isValid(emptyFile, context), "0 bytes file should be valid");
        }

        Object smallFile = createMockMultipartFile(1024L, null); // 1KB
        if (smallFile != null) {
            assertTrue(validator.isValid(smallFile, context), "1KB file should be valid");
        }

        Object mediumFile = createMockMultipartFile(5120L, null); // 5KB
        if (mediumFile != null) {
            assertTrue(validator.isValid(mediumFile, context), "5KB file should be valid");
        }

        Object maxFile = createMockMultipartFile(10240L, null); // 10KB
        if (maxFile != null) {
            assertTrue(validator.isValid(maxFile, context), "10KB file should be valid");
        }
    }

    @Test
    public void testInvalidFileSize() {
        // 准备注解：1KB - 10KB
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("1KB");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // 模拟MultipartFile
        Object tooSmall = createMockMultipartFile(512L, null); // 512B < 1KB
        if (tooSmall != null) {
            assertFalse(validator.isValid(tooSmall, context), "512 bytes file should be invalid");
        }

        Object tooLarge = createMockMultipartFile(10241L, null); // 10KB + 1 byte
        if (tooLarge != null) {
            assertFalse(validator.isValid(tooLarge, context), "10KB+1byte file should be invalid");
        }
    }

    @Test
    public void testMimeTypeRestriction() {
        // 准备注解：只允许 image/jpeg 和 image/png
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png"});
        validator.initialize(annotation);

        // 允许的MIME类型
        Object jpegFile = createMockMultipartFile(5120L, "image/jpeg");
        if (jpegFile != null) {
            assertTrue(validator.isValid(jpegFile, context), "image/jpeg should be valid");
        }

        Object pngFile = createMockMultipartFile(5120L, "image/png");
        if (pngFile != null) {
            assertTrue(validator.isValid(pngFile, context), "image/png should be valid");
        }

        // 不允许的MIME类型
        Object pdfFile = createMockMultipartFile(5120L, "application/pdf");
        if (pdfFile != null) {
            assertFalse(validator.isValid(pdfFile, context), "application/pdf should be invalid");
        }

        // null MIME类型
        Object noTypeFile = createMockMultipartFile(5120L, null);
        if (noTypeFile != null) {
            assertFalse(validator.isValid(noTypeFile, context), "null content type should be invalid when types are restricted");
        }
    }

    @Test
    public void testFileSizeNullFromMultipartFile() {
        // 准备注解
        FileSize annotation = mock(FileSize.class);
        when(annotation.min()).thenReturn("0B");
        when(annotation.max()).thenReturn("10KB");
        when(annotation.allowedTypes()).thenReturn(new String[]{});
        validator.initialize(annotation);

        // getSize() 返回 null
        Object nullSizeFile = createMockMultipartFile(null, null);
        if (nullSizeFile != null) {
            assertFalse(validator.isValid(nullSizeFile, context), "file with null size should be invalid");
        }
    }
}
