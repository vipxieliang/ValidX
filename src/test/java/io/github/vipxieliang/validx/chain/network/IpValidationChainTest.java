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
import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IpValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIp(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isIp((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidIp() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIp((Object)"192.168.1.1");
        assertTrue(chain.passed(), "有效的IP地址应该通过验证");
    }

    @Test
    public void testInvalidIp() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIp((Object)"192.168.1.256");
        assertFalse(chain.passed(), "无效的IP地址不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("IP地址格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testValidIpv4WithVersion() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIp((Object)"192.168.1.1", Ip.IpVersion.V4);
        assertTrue(chain.passed(), "有效的IPv4地址应该通过IPv4验证");
    }

    @Test
    public void testValidIpv6WithVersion() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIp((Object)"2001:db8::1", Ip.IpVersion.V6);
        assertTrue(chain.passed(), "有效的IPv6地址应该通过IPv6验证");
    }

    @Test
    public void testIpv4FailsIpv6Validation() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIp((Object)"192.168.1.1", Ip.IpVersion.V6);
        assertFalse(chain.passed(), "IPv4地址不应该通过IPv6验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("IP地址格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testIpv6FailsIpv4Validation() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isIp((Object)"2001:db8::1", Ip.IpVersion.V4);
        assertFalse(chain.passed(), "IPv6地址不应该通过IPv4验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("IP地址格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testAnyVersionAcceptsBoth() {
        ValidationPlus chain1 = ValidationPlus.init();
        chain1 = chain1.isIp((Object)"192.168.1.1", Ip.IpVersion.ANY);
        assertTrue(chain1.passed(), "IPv4地址应该通过ANY验证");

        ValidationPlus chain2 = ValidationPlus.init();
        chain2 = chain2.isIp((Object)"2001:db8::1", Ip.IpVersion.ANY);
        assertTrue(chain2.passed(), "IPv6地址应该通过ANY验证");
    }
}