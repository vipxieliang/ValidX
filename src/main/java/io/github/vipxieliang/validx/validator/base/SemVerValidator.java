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

import io.github.vipxieliang.validx.annotations.SemVer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 语义化版本号验证器
 * 验证字符串是否符合语义化版本规范 (Semantic Versioning 2.0.0)
 *
 * <p>语义化版本号规范：
 * <ul>
 *   <li>版本号由三部分组成：MAJOR.MINOR.PATCH</li>
 *   <li>MAJOR：主版本号，不兼容的API修改</li>
 *   <li>MINOR：次版本号，向下兼容的功能性新增</li>
 *   <li>PATCH：修订号，向下兼容的问题修正</li>
 *   <li>可选预发布版本：-alpha, -beta, -rc.1 等</li>
 *   <li>可选构建元数据：+build, +20130313144700 等</li>
 * </ul>
 *
 * @author vipxieliang
 * @since 1.0.6
 * @see <a href="https://semver.org/">Semantic Versioning 2.0.0</a>
 */
public class SemVerValidator implements ConstraintValidator<SemVer, String> {

    /**
     * 语义化版本号正则表达式（符合 SemVer 2.0.0 规范）
     * 格式：MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]
     *
     * 说明：
     * - MAJOR, MINOR, PATCH 必须是非负整数，不能有前导零（除了0本身）
     * - PRERELEASE 由点号分隔的标识符组成，标识符可以是数字或字母数字
     * - BUILD 由点号分隔的标识符组成，标识符可以是数字或字母数字
     */
    private static final Pattern SEMVER_PATTERN = Pattern.compile(
            "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
            "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)" +
            "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
            "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    );

    /**
     * 带v前缀的语义化版本号正则表达式
     */
    private static final Pattern SEMVER_WITH_V_PATTERN = Pattern.compile(
            "^v(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
            "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)" +
            "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
            "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    );

    /**
     * 是否允许v前缀
     */
    private boolean allowVPrefix = false;

    @Override
    public void initialize(SemVer constraintAnnotation) {
        this.allowVPrefix = constraintAnnotation.allowVPrefix();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null或空字符串由@NotNull/@NotEmpty等注解处理
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // 检查是否匹配语义化版本号格式
        if (allowVPrefix) {
            // 允许v前缀，两种格式都可以
            return SEMVER_PATTERN.matcher(value).matches() ||
                   SEMVER_WITH_V_PATTERN.matcher(value).matches();
        } else {
            // 不允许v前缀，只能是标准格式
            return SEMVER_PATTERN.matcher(value).matches();
        }
    }

    /**
     * 静态验证方法，供链式调用使用
     *
     * @param value 待验证的字符串
     * @param allowVPrefix 是否允许v前缀
     * @return true表示是有效的语义化版本号，false表示无效
     */
    public static boolean isValidSemVer(String value, boolean allowVPrefix) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 空值由@NotNull/@NotEmpty等注解处理
        }

        if (allowVPrefix) {
            return SEMVER_PATTERN.matcher(value).matches() ||
                   SEMVER_WITH_V_PATTERN.matcher(value).matches();
        } else {
            return SEMVER_PATTERN.matcher(value).matches();
        }
    }
}
