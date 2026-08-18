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

package io.github.vipxieliang.validx.validator.network;

import io.github.vipxieliang.validx.annotations.Url;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * URL验证器
 * 验证字符串是否是有效的URL，支持协议白名单配置
 */
public class UrlValidator implements ConstraintValidator<Url, String> {

    // 默认协议白名单（http / https / ftp），与历史版本行为保持一致
    private static final String[] DEFAULT_PROTOCOLS = {"http", "https", "ftp"};

    // URL主机名 + 路径部分正则
    private static final String URL_PART_PATTERN =
            "[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";

    private Pattern pattern;

    @Override
    public void initialize(Url constraintAnnotation) {
        initialize(constraintAnnotation.protocols());
    }

    /**
     * 直接使用协议白名单初始化验证器（用于链式调用）
     *
     * @param protocols 允许的协议列表（如 {"http", "https"}）；为空时使用默认 http/https/ftp
     */
    public void initialize(String[] protocols) {
        if (protocols == null || protocols.length == 0) {
            protocols = DEFAULT_PROTOCOLS;
        }
        StringBuilder protocolPart = new StringBuilder("(?i:");
        for (int i = 0; i < protocols.length; i++) {
            if (i > 0) {
                protocolPart.append("|");
            }
            protocolPart.append(Pattern.quote(protocols[i].trim().toLowerCase(Locale.ROOT)));
        }
        protocolPart.append(")");
        this.pattern = Pattern.compile("^" + protocolPart + "://" + URL_PART_PATTERN);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        if (pattern == null) {
            initialize(DEFAULT_PROTOCOLS); // 未初始化时使用默认白名单
        }

        // 首先使用正则表达式进行快速检查
        if (!pattern.matcher(value).matches()) {
            return false;
        }

        // 然后使用URL类进行更严格的验证
        try {
            new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
