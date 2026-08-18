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
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UrlValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @Url
        private String url;

        public TestEntity(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    @Test
    public void testUrlValidatorDirect() {
        // 直接测试验证器的逻辑
        UrlValidator validator = new UrlValidator();

        // 测试有效的URL
        assertTrue(validator.isValid("http://www.example.com", null));
        assertTrue(validator.isValid("https://www.example.com", null));
        assertTrue(validator.isValid("http://example.com", null));
        assertTrue(validator.isValid("https://example.com/path", null));
        assertTrue(validator.isValid("http://example.com/path?query=value", null));
        assertTrue(validator.isValid("https://example.com/path#section", null));

        // 默认白名单（http/https/ftp）下 ftp 应通过（与历史版本向下兼容）
        assertTrue(validator.isValid("ftp://example.com/resource", null));

        // 显式收紧白名单：仅 https 时 http 不应通过
        UrlValidator httpsOnly = new UrlValidator();
        httpsOnly.initialize(new String[]{"https"});
        assertTrue(httpsOnly.isValid("https://example.com", null));
        assertFalse(httpsOnly.isValid("http://example.com", null));

        // 测试无效的URL
        assertFalse(validator.isValid("invalid-url", null));
        assertFalse(validator.isValid("http://", null));
        assertFalse(validator.isValid("https://", null));
        assertFalse(validator.isValid("ftp://", null));
        assertFalse(validator.isValid("http://.", null));
        assertFalse(validator.isValid("http://..", null));
        assertFalse(validator.isValid("http://../", null));
        assertFalse(validator.isValid("http://?", null));
        assertFalse(validator.isValid("http://??", null));
        assertFalse(validator.isValid("http://#", null));
        assertFalse(validator.isValid("http://##", null));

        // 测试null和空字符串
        assertTrue(validator.isValid(null, null), "null应该通过验证");
        assertTrue(validator.isValid("", null), "空字符串应该通过验证");
    }

    @Test
    public void testValidUrls() {
        // 测试有效的URL
        TestEntity entity1 = new TestEntity("http://www.example.com");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的URL应该通过验证: http://www.example.com");

        TestEntity entity2 = new TestEntity("https://example.com/path?query=value");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的URL应该通过验证: https://example.com/path?query=value");
        
        // 默认白名单包含 ftp，直接使用默认注解即可通过（向下兼容）
        TestEntity entity3 = new TestEntity("ftp://files.example.com/resource");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的URL应该通过验证: ftp://files.example.com/resource");
    }

    @Test
    public void testInvalidUrls() {
        // 测试无效的URL
        TestEntity entity1 = new TestEntity("invalid-url");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "无效的URL不应该通过验证: invalid-url");

        TestEntity entity2 = new TestEntity("http://");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的URL不应该通过验证: http://");
    }

    @Test
    public void testNullAndEmptyUrl() {
        // 测试null和空字符串
        TestEntity entity1 = new TestEntity("");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "空字符串应该通过验证");

        TestEntity entity2 = new TestEntity(null);
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "null值应该通过验证");
    }

    public static class TestEntityHttpsOnly {
        @Url(protocols = {"https"})
        private String url;

        public TestEntityHttpsOnly(String url) {
            this.url = url;
        }
    }

    public static class TestEntityFtp {
        @Url(protocols = {"http", "https", "ftp"})
        private String url;

        public TestEntityFtp(String url) {
            this.url = url;
        }
    }

    @Test
    public void testProtocolWhitelist() {
        // 直接验证：仅 https
        UrlValidator httpsOnly = new UrlValidator();
        httpsOnly.initialize(new String[]{"https"});
        assertTrue(httpsOnly.isValid("https://example.com", null), "https 应在白名单内通过");
        assertFalse(httpsOnly.isValid("http://example.com", null), "http 不应通过 https 白名单");
        assertFalse(httpsOnly.isValid("ftp://example.com", null), "ftp 不应通过 https 白名单");

        // 注解验证：@Url(protocols = {"https"})
        TestEntityHttpsOnly entity1 = new TestEntityHttpsOnly("https://example.com");
        assertTrue(validator.validate(entity1).isEmpty(), "https 应通过 @Url(protocols={\"https\"})");

        TestEntityHttpsOnly entity2 = new TestEntityHttpsOnly("http://example.com");
        assertFalse(validator.validate(entity2).isEmpty(), "http 不应通过 @Url(protocols={\"https\"})");

        // 注解验证：@Url(protocols = {"http", "https", "ftp"})
        TestEntityFtp entity3 = new TestEntityFtp("ftp://files.example.com/resource");
        assertTrue(validator.validate(entity3).isEmpty(), "ftp 应通过 @Url(protocols={\"http\",\"https\",\"ftp\"})");
    }
}