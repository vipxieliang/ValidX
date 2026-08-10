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

package io.github.vipxieliang.validx.annotations;

import io.github.vipxieliang.validx.validator.base.FileSizeByteArrayValidator;
import io.github.vipxieliang.validx.validator.base.FileSizeMultipartFileValidator;
import io.github.vipxieliang.validx.validator.base.FileSizePathValidator;
import io.github.vipxieliang.validx.validator.base.FileSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 文件大小验证注解
 * 验证文件大小是否在指定范围内
 *
 * 支持的类型：
 * - java.io.File
 * - java.nio.file.Path
 * - byte[]
 * - org.springframework.web.multipart.MultipartFile (需要Spring依赖)
 *
 * 使用示例：
 * <pre>
 * // 指定最小和最大值
 * {@literal @}FileSize(min = "1KB", max = "10MB")
 * private File file;
 *
 * // 只指定最大值
 * {@literal @}FileSize(max = "5MB")
 * private Path filePath;
 *
 * // 限制MIME类型（仅对MultipartFile有效）
 * {@literal @}FileSize(max = "5MB", allowedTypes = {"image/jpeg", "image/png"})
 * private MultipartFile avatar;
 * </pre>
 */
@Documented
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = {
    FileSizeValidator.class,           // java.io.File
    FileSizePathValidator.class,       // java.nio.file.Path
    FileSizeByteArrayValidator.class,  // byte[]
    FileSizeMultipartFileValidator.class  // org.springframework.web.multipart.MultipartFile
})
public @interface FileSize {

    /**
     * 最小文件大小（支持单位：B、KB、MB、GB、TB）
     * 默认值为 "0B"，表示无最小限制
     * 示例: "1KB", "10MB", "1.5GB"
     *
     * @return 最小文件大小字符串
     */
    String min() default "0B";

    /**
     * 最大文件大小（支持单位：B、KB、MB、GB、TB）
     * 默认值为空字符串，表示无最大限制
     * 示例: "1KB", "10MB", "1.5GB"
     *
     * @return 最大文件大小字符串
     */
    String max() default "";

    /**
     * 允许的MIME类型（可选）
     * 仅对支持MIME类型的文件有效（如 MultipartFile）
     * 示例: {"image/jpeg", "image/png", "application/pdf"}
     *
     * @return 允许的MIME类型数组
     */
    String[] allowedTypes() default {};

    /**
     * 错误消息
     *
     * @return 错误消息模板
     */
    String message() default "{io.github.vipxieliang.validx.annotation.file.size}";

    /**
     * @return 验证组
     */
    Class<?>[] groups() default {};

    /**
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}