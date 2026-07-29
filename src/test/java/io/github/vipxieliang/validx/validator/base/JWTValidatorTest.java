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

import io.github.vipxieliang.validx.annotations.JWT;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT验证器测试类
 */
public class JWTValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @JWT
        private String token;

        public TestEntity(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    @Test
    public void testValidJWT() {
        // 测试有效的JWT Token
        // 标准JWT格式：header.payload.signature
        TestEntity entity1 = new TestEntity("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的JWT Token应该通过验证");

        // 短JWT Token
        TestEntity entity2 = new TestEntity("abc.def.ghi");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "简单的JWT格式应该通过验证");

        // 包含下划线和连字符的JWT Token（Base64URL编码）
        TestEntity entity3 = new TestEntity("abc-123_xyz.def-456_uvw.ghi-789_rst");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "包含下划线和连字符的JWT Token应该通过验证");
    }

    @Test
    public void testInvalidJWT() {
        // 测试无效的JWT Token

        // 只有两部分
        TestEntity entity1 = new TestEntity("abc.def");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "只有两部分的Token不应该通过验证");

        // 四部分
        TestEntity entity2 = new TestEntity("abc.def.ghi.jkl");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "四部分的Token不应该通过验证");

        // 包含非法字符（=填充符）
        TestEntity entity3 = new TestEntity("abc=.def.ghi");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "包含等号的Token不应该通过验证");

        // 包含空格
        TestEntity entity4 = new TestEntity("abc .def.ghi");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "包含空格的Token不应该通过验证");

        // 空部分
        TestEntity entity5 = new TestEntity(".def.ghi");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertFalse(violations5.isEmpty(), "有空部分的Token不应该通过验证");

        TestEntity entity6 = new TestEntity("abc..ghi");
        Set<ConstraintViolation<TestEntity>> violations6 = validator.validate(entity6);
        assertFalse(violations6.isEmpty(), "中间有空部分的Token不应该通过验证");

        // 包含特殊字符
        TestEntity entity7 = new TestEntity("abc+def.ghi.jkl");
        Set<ConstraintViolation<TestEntity>> violations7 = validator.validate(entity7);
        assertFalse(violations7.isEmpty(), "包含加号的Token不应该通过验证（非Base64URL）");

        TestEntity entity8 = new TestEntity("abc/def.ghi.jkl");
        Set<ConstraintViolation<TestEntity>> violations8 = validator.validate(entity8);
        assertFalse(violations8.isEmpty(), "包含斜杠的Token不应该通过验证（非Base64URL）");
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试空值和null值应该通过验证（交给@NotNull/@NotEmpty处理）
        JWTValidator jwtValidator = new JWTValidator();
        assertTrue(jwtValidator.isValid(null, null), "null值应该通过验证");
        assertTrue(jwtValidator.isValid("", null), "空字符串应该通过验证");
    }

    @Test
    public void testEdgeCases() {
        // 测试边界情况

        // 最小有效JWT（每部分只有一个字符）
        TestEntity entity1 = new TestEntity("a.b.c");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "最小有效JWT应该通过验证");

        // 很长的JWT
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longPart = sb.toString();
        TestEntity entity2 = new TestEntity(longPart + "." + longPart + "." + longPart);
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "很长的JWT应该通过验证");

        // 只有点号
        TestEntity entity3 = new TestEntity("..");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "只有点号的Token不应该通过验证");
    }

    @Test
    public void testRealWorldJWTExamples() {
        // 测试真实世界的JWT示例

        // JWT.io 示例 (HS256)
        String jwtHS256 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U";
        TestEntity entity1 = new TestEntity(jwtHS256);
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "真实的HS256 JWT应该通过验证");

        // 包含更多claims的JWT
        String jwtWithClaims = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.9xJf4FgF5R5dJf5F7F8F8F9F1F2F3F4F5F6F7F8F9F0";
        TestEntity entity2 = new TestEntity(jwtWithClaims);
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "包含多个claims的JWT应该通过验证");
    }

    @Test
    public void testWithValidationFramework() {
        // 测试与验证框架集成
        TestEntity entity1 = new TestEntity("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的JWT应该通过验证");

        TestEntity entity2 = new TestEntity("invalid.jwt");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "无效的JWT不应该通过验证");

        // 检查错误消息
        ConstraintViolation<TestEntity> violation = violations2.iterator().next();
        assertNotNull(violation.getMessage(), "错误消息不应该为空");
    }
}
