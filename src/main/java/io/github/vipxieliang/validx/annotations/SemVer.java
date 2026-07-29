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

import io.github.vipxieliang.validx.validator.base.SemVerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 语义化版本号验证注解
 * 验证字符串是否符合语义化版本规范 (Semantic Versioning 2.0.0)
 *
 * <p>语义化版本号格式：
 * <ul>
 *   <li>基础版本：MAJOR.MINOR.PATCH (例如：1.0.0)</li>
 *   <li>预发布版本：MAJOR.MINOR.PATCH-prerelease (例如：1.0.0-alpha, 1.0.0-beta.1)</li>
 *   <li>构建元数据：MAJOR.MINOR.PATCH+build (例如：1.0.0+20130313144700)</li>
 *   <li>完整格式：MAJOR.MINOR.PATCH-prerelease+build (例如：1.0.0-alpha+001)</li>
 * </ul>
 *
 * <p>示例：
 * <pre>
 * // 验证语义化版本号
 * {@code @SemVer}
 * private String version;
 *
 * // 允许带v前缀的版本号 (如 v1.0.0)
 * {@code @SemVer(allowVPrefix = true)}
 * private String versionWithPrefix;
 * </pre>
 *
 * @author vipxieliang
 * @since 1.0.6
 * @see <a href="https://semver.org/">Semantic Versioning 2.0.0</a>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SemVerValidator.class)
@Documented
public @interface SemVer {

    /**
     * 是否允许 'v' 前缀
     * 例如：v1.0.0, v2.1.3-beta
     *
     * @return true表示允许v前缀，false表示不允许
     */
    boolean allowVPrefix() default false;

    /**
     * 验证失败时的错误消息
     */
    String message() default "{io.github.vipxieliang.validx.annotation.semver}";

    /**
     * 验证分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};
}
