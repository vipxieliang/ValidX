/*
 * Copyright 2025-2026 vipxieliang
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

import io.github.vipxieliang.validx.validator.base.EndsWithAnyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证字符串是否以指定后缀数组中的任意一个结尾
 *
 * <p>使用场景：当需要验证字符串以多个可能的后缀之一结尾时使用</p>
 *
 * <p>示例：</p>
 * <pre>
 * public class FileDTO {
 *     // 可以是常见图片格式
 *     &#64;EndsWithAny({".jpg", ".jpeg", ".png", ".gif", ".bmp"})
 *     private String imageFile;
 * }
 * </pre>
 *
 * @author vipxieliang
 * @since 1.1.1
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndsWithAnyValidator.class)
public @interface EndsWithAny {

    /**
     * 后缀数组，只要匹配任意一个即验证通过
     *
     * @return 后缀数组
     */
    String[] value();

    /**
     * 是否忽略大小写，默认为 false（区分大小写）
     *
     * @return true 表示忽略大小写，false 表示区分大小写
     */
    boolean ignoreCase() default false;

    /**
     * 验证失败时的错误消息
     *
     * @return 错误消息模板
     */
    String message() default "{io.github.vipxieliang.validx.annotation.ends.with.any}";

    /**
     * 验证分组
     *
     * @return 分组数组
     */
    Class<?>[] groups() default {};

    /**
     * 负载信息
     *
     * @return 负载数组
     */
    Class<? extends Payload>[] payload() default {};
}
