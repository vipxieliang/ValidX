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

import io.github.vipxieliang.validx.annotations.Domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Domain验证器
 * 验证字符串是否是有效的域名
 */
public class DomainValidator implements ConstraintValidator<Domain, String> {
    
    // 域名正则表达式
    private static final String DOMAIN_PATTERN = 
        "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$";
    
    private final Pattern pattern = Pattern.compile(DOMAIN_PATTERN);

    @Override
    public void initialize(Domain constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }

        // 首先使用正则表达式进行快速检查
        if (!pattern.matcher(value).matches()) {
            return false;
        }
        
        // 对于更严格的验证，我们可以尝试解析域名
        try {
            InetAddress.getByName(value);
            return true;
        } catch (UnknownHostException e) {
            // 如果无法解析，我们仍然认为格式正确的域名是有效的
            // 因为域名可能在某些网络环境下无法解析，但格式本身是正确的
            return true;
        }
    }
}