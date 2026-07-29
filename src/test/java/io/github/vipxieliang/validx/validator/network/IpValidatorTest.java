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
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IP地址验证器测试类（包含版本控制）
 */
public class IpValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    // 测试默认行为（ANY）
    public static class TestEntityAny {
        @Ip
        private String ip;

        public TestEntityAny(String ip) {
            this.ip = ip;
        }

        public String getIp() {
            return ip;
        }
    }

    // 测试IPv4
    public static class TestEntityV4 {
        @Ip(version = Ip.IpVersion.V4)
        private String ip;

        public TestEntityV4(String ip) {
            this.ip = ip;
        }

        public String getIp() {
            return ip;
        }
    }

    // 测试IPv6
    public static class TestEntityV6 {
        @Ip(version = Ip.IpVersion.V6)
        private String ip;

        public TestEntityV6(String ip) {
            this.ip = ip;
        }

        public String getIp() {
            return ip;
        }
    }

    @Test
    public void testValidIPv4() {
        // 测试有效的IPv4地址
        String[] validIPv4 = {
            "192.168.1.1",
            "10.0.0.1",
            "172.16.0.1",
            "127.0.0.1",
            "255.255.255.255",
            "0.0.0.0"
        };

        for (String ip : validIPv4) {
            TestEntityV4 entity = new TestEntityV4(ip);
            Set<ConstraintViolation<TestEntityV4>> violations = validator.validate(entity);
            assertTrue(violations.isEmpty(), "有效的IPv4地址应该通过验证: " + ip);
        }
    }

    @Test
    public void testValidIPv6() {
        // 测试有效的IPv6地址
        String[] validIPv6 = {
            "2001:0db8:85a3:0000:0000:8a2e:0370:7334",
            "2001:db8:85a3::8a2e:370:7334",
            "::1",
            "fe80::1",
            "2001:db8::1"
        };

        for (String ip : validIPv6) {
            TestEntityV6 entity = new TestEntityV6(ip);
            Set<ConstraintViolation<TestEntityV6>> violations = validator.validate(entity);
            assertTrue(violations.isEmpty(), "有效的IPv6地址应该通过验证: " + ip);
        }
    }

    @Test
    public void testInvalidIP() {
        // 测试无效的IP地址
        String[] invalidIPs = {
            "256.1.1.1",        // 超出范围
            "192.168.1.1.1",    // 太多段
            "abc.def.ghi.jkl",  // 非数字
            "192.168.-1.1",     // 负数
            "gggg::1"           // 无效的IPv6
        };

        for (String ip : invalidIPs) {
            TestEntityAny entity = new TestEntityAny(ip);
            Set<ConstraintViolation<TestEntityAny>> violations = validator.validate(entity);
            assertFalse(violations.isEmpty(), "无效的IP地址不应该通过验证: " + ip);
        }
    }

    @Test
    public void testIPv4OnlyValidator() {
        // 测试只接受IPv4的验证器

        // IPv4应该通过
        TestEntityV4 entity1 = new TestEntityV4("192.168.1.1");
        Set<ConstraintViolation<TestEntityV4>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "IPv4地址应该通过IPv4验证器");

        // IPv6不应该通过
        TestEntityV4 entity2 = new TestEntityV4("2001:db8::1");
        Set<ConstraintViolation<TestEntityV4>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "IPv6地址不应该通过IPv4验证器");

        TestEntityV4 entity3 = new TestEntityV4("::1");
        Set<ConstraintViolation<TestEntityV4>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "IPv6回环地址不应该通过IPv4验证器");
    }

    @Test
    public void testIPv6OnlyValidator() {
        // 测试只接受IPv6的验证器

        // IPv6应该通过
        TestEntityV6 entity1 = new TestEntityV6("2001:db8::1");
        Set<ConstraintViolation<TestEntityV6>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "IPv6地址应该通过IPv6验证器");

        // IPv4不应该通过
        TestEntityV6 entity2 = new TestEntityV6("192.168.1.1");
        Set<ConstraintViolation<TestEntityV6>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "IPv4地址不应该通过IPv6验证器");

        TestEntityV6 entity3 = new TestEntityV6("127.0.0.1");
        Set<ConstraintViolation<TestEntityV6>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "IPv4回环地址不应该通过IPv6验证器");
    }

    @Test
    public void testAnyVersionValidator() {
        // 测试接受任意版本的验证器

        // IPv4应该通过
        TestEntityAny entity1 = new TestEntityAny("192.168.1.1");
        Set<ConstraintViolation<TestEntityAny>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "IPv4地址应该通过ANY验证器");

        // IPv6应该通过
        TestEntityAny entity2 = new TestEntityAny("2001:db8::1");
        Set<ConstraintViolation<TestEntityAny>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "IPv6地址应该通过ANY验证器");

        TestEntityAny entity3 = new TestEntityAny("::1");
        Set<ConstraintViolation<TestEntityAny>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "IPv6回环地址应该通过ANY验证器");

        TestEntityAny entity4 = new TestEntityAny("127.0.0.1");
        Set<ConstraintViolation<TestEntityAny>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "IPv4回环地址应该通过ANY验证器");
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试空值和null值
        IpValidator ipValidator = new IpValidator();
        assertTrue(ipValidator.isValid(null, null), "null值应该通过验证");
        assertTrue(ipValidator.isValid("", null), "空字符串应该通过验证");
    }

    @Test
    public void testSpecialIPAddresses() {
        // 测试特殊IP地址

        // 回环地址
        TestEntityV4 entity1 = new TestEntityV4("127.0.0.1");
        Set<ConstraintViolation<TestEntityV4>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "IPv4回环地址应该通过验证");

        TestEntityV6 entity2 = new TestEntityV6("::1");
        Set<ConstraintViolation<TestEntityV6>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "IPv6回环地址应该通过验证");

        // 广播地址
        TestEntityV4 entity3 = new TestEntityV4("255.255.255.255");
        Set<ConstraintViolation<TestEntityV4>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "IPv4广播地址应该通过验证");

        // 任意地址
        TestEntityV4 entity4 = new TestEntityV4("0.0.0.0");
        Set<ConstraintViolation<TestEntityV4>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "IPv4任意地址应该通过验证");

        TestEntityV6 entity5 = new TestEntityV6("::");
        Set<ConstraintViolation<TestEntityV6>> violations5 = validator.validate(entity5);
        assertTrue(violations5.isEmpty(), "IPv6任意地址应该通过验证");
    }

    @Test
    public void testPrivateIPAddresses() {
        // 测试私有IP地址
        String[] privateIPv4 = {
            "10.0.0.1",
            "172.16.0.1",
            "192.168.1.1"
        };

        for (String ip : privateIPv4) {
            TestEntityV4 entity = new TestEntityV4(ip);
            Set<ConstraintViolation<TestEntityV4>> violations = validator.validate(entity);
            assertTrue(violations.isEmpty(), "私有IPv4地址应该通过验证: " + ip);
        }
    }

    @Test
    public void testIPv6ShorthandNotation() {
        // 测试IPv6简写形式
        String[] ipv6Shorthand = {
            "2001:db8::1",
            "::1",
            "fe80::"
        };

        for (String ip : ipv6Shorthand) {
            TestEntityV6 entity = new TestEntityV6(ip);
            Set<ConstraintViolation<TestEntityV6>> violations = validator.validate(entity);
            assertTrue(violations.isEmpty(), "IPv6简写形式应该通过验证: " + ip);
        }
    }

    @Test
    public void testWithValidationFramework() {
        // 测试与验证框架集成
        TestEntityV4 entity1 = new TestEntityV4("192.168.1.1");
        Set<ConstraintViolation<TestEntityV4>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的IPv4地址应该通过验证");

        TestEntityV4 entity2 = new TestEntityV4("invalid-ip");
        Set<ConstraintViolation<TestEntityV4>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的IP地址不应该通过验证");

        // 检查错误消息
        if (!violations2.isEmpty()) {
            ConstraintViolation<TestEntityV4> violation = violations2.iterator().next();
            assertNotNull(violation.getMessage(), "错误消息不应该为空");
        }
    }
}
