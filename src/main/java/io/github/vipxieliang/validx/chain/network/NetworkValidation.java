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

package io.github.vipxieliang.validx.chain.network;

import io.github.vipxieliang.validx.annotations.Ip;
import io.github.vipxieliang.validx.validator.network.*;
import io.github.vipxieliang.validx.i18n.MessageManager;

import java.util.List;
import java.util.Locale;

public class NetworkValidation {
    
    public void validateMacAddress(Object value, List<String> errors, Locale locale) {
        MacValidator validator = new MacValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.mac", locale));
        }
    }
    
    public void validateEmail(Object value, List<String> errors, Locale locale) {
        EmailValidator validator = new EmailValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.email", locale));
        }
    }

    public void validateIp(Object value, List<String> errors, Locale locale) {
        validateIp(value, Ip.IpVersion.ANY, errors, locale);
    }

    /**
     * 验证IP地址（指定版本）
     * @param value 待验证的IP地址
     * @param version IP版本（V4/V6/ANY）
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateIp(Object value, Ip.IpVersion version, List<String> errors, Locale locale) {
        IpValidator validator = new IpValidator();
        validator.initialize(version);
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.ip", locale));
        }
    }
    
    public void validateSubnetMask(Object value, List<String> errors, Locale locale) {
        SubnetMaskValidator validator = new SubnetMaskValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.subnet.mask", locale));
        }
    }
    
    public void validateUrl(Object value, List<String> errors, Locale locale) {
        UrlValidator validator = new UrlValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.url", locale));
        }
    }

    /**
     * 验证URL（指定协议白名单）
     * @param value 待验证的URL
     * @param protocols 允许的协议列表（如 {"http", "https"}）
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateUrl(Object value, String[] protocols, List<String> errors, Locale locale) {
        UrlValidator validator = new UrlValidator();
        validator.initialize(protocols);
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.url", locale));
        }
    }
    
    public void validateDomain(Object value, List<String> errors, Locale locale) {
        DomainValidator validator = new DomainValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.domain", locale));
        }
    }
    
    public void validatePort(Object value, List<String> errors, Locale locale) {
        PortValidator validator = new PortValidator();
        if (!validator.isValid(value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.port", locale));
        }
    }
}