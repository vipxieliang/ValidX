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


import io.github.vipxieliang.validx.annotations.Ip;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * Ip验证器
 * 验证字符串是否是有效的IP地址（支持IPv4和IPv6）
 */
public class IpValidator implements ConstraintValidator<Ip, String> {

    private Ip.IpVersion version = Ip.IpVersion.ANY; // 默认为ANY

    @Override
    public void initialize(Ip ip) {
        initialize(ip.version());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param version IP版本
     */
    public void initialize(Ip.IpVersion version) {
        this.version = version;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        try {
            InetAddress address = InetAddress.getByName(value);

            // 根据版本参数验证，如果version为null则默认为ANY
            Ip.IpVersion ver = (version != null) ? version : Ip.IpVersion.ANY;
            switch (ver) {
                case V4:
                    return address instanceof Inet4Address;
                case V6:
                    return address instanceof Inet6Address;
                case ANY:
                default:
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}