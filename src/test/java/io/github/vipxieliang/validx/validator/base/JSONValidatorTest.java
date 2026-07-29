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

import io.github.vipxieliang.validx.annotations.JSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON验证器测试类
 */
public class JSONValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === JSON对象测试 ===

    @Test
    public void testValidJSON_EmptyObject() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空JSON对象应该通过验证");
    }

    @Test
    public void testValidJSON_SimpleObject() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"name\":\"John\",\"age\":30}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "简单JSON对象应该通过验证");
    }

    @Test
    public void testValidJSON_NestedObject() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"user\":{\"name\":\"John\",\"address\":{\"city\":\"Beijing\"}}}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "嵌套JSON对象应该通过验证");
    }

    @Test
    public void testValidJSON_WithWhitespace() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "  {  \"name\"  :  \"John\"  }  ";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带空白符的JSON应该通过验证");
    }

    // === JSON数组测试 ===

    @Test
    public void testValidJSON_EmptyArray() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "[]";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空JSON数组应该通过验证");
    }

    @Test
    public void testValidJSON_SimpleArray() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "[1,2,3,4,5]";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "简单JSON数组应该通过验证");
    }

    @Test
    public void testValidJSON_MixedArray() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "[\"string\",123,true,null,{\"key\":\"value\"}]";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "混合类型数组应该通过验证");
    }

    // === JSON类型限制测试 ===

    @Test
    public void testValidJSON_OnlyObject() {
        TestDTOObject dto = new TestDTOObject();
        dto.jsonObject = "{\"key\":\"value\"}";

        Set<ConstraintViolation<TestDTOObject>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "只允许对象时，对象应该通过验证");
    }

    @Test
    public void testInvalidJSON_ArrayWhenObjectRequired() {
        TestDTOObject dto = new TestDTOObject();
        dto.jsonObject = "[1,2,3]";

        Set<ConstraintViolation<TestDTOObject>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "只允许对象时，数组应该验证失败");
    }

    @Test
    public void testValidJSON_OnlyArray() {
        TestDTOArray dto = new TestDTOArray();
        dto.jsonArray = "[1,2,3]";

        Set<ConstraintViolation<TestDTOArray>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "只允许数组时，数组应该通过验证");
    }

    @Test
    public void testInvalidJSON_ObjectWhenArrayRequired() {
        TestDTOArray dto = new TestDTOArray();
        dto.jsonArray = "{\"key\":\"value\"}";

        Set<ConstraintViolation<TestDTOArray>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "只允许数组时，对象应该验证失败");
    }

    // === 特殊值测试 ===

    @Test
    public void testValidJSON_NullValue() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"value\":null}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含null值的JSON应该通过验证");
    }

    @Test
    public void testValidJSON_BooleanValue() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"isActive\":true,\"isDeleted\":false}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含布尔值的JSON应该通过验证");
    }

    @Test
    public void testValidJSON_NumberValue() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"int\":123,\"float\":45.67,\"negative\":-89,\"exp\":1.23e10}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含各种数字的JSON应该通过验证");
    }

    // === 转义字符测试 ===

    @Test
    public void testValidJSON_EscapedCharacters() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"text\":\"Line1\\nLine2\\tTabbed\"}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含转义字符的JSON应该通过验证");
    }

    @Test
    public void testValidJSON_UnicodeEscape() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"chinese\":\"\\u4e2d\\u6587\"}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "包含Unicode转义的JSON应该通过验证");
    }

    // === 深度限制测试 ===

    @Test
    public void testValidJSON_WithinDepthLimit() {
        TestDTODepth dto = new TestDTODepth();
        dto.data = "{\"a\":{\"b\":{\"c\":{\"d\":1}}}}";  // 深度4

        Set<ConstraintViolation<TestDTODepth>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "深度在限制内的JSON应该通过验证");
    }

    @Test
    public void testInvalidJSON_ExceedDepthLimit() {
        TestDTODepth dto = new TestDTODepth();
        dto.data = "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"f\":1}}}}}}";  // 深度6

        Set<ConstraintViolation<TestDTODepth>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "超过深度限制的JSON应该验证失败");
    }

    // === 长度限制测试 ===

    @Test
    public void testValidJSON_WithinLengthLimit() {
        TestDTOLength dto = new TestDTOLength();
        dto.data = "{\"key\":\"value\"}";  // 17个字符

        Set<ConstraintViolation<TestDTOLength>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "长度在限制内的JSON应该通过验证");
    }

    @Test
    public void testInvalidJSON_ExceedLengthLimit() {
        TestDTOLength dto = new TestDTOLength();
        dto.data = "{\"key\":\"this is a very long value that exceeds the limit\"}";

        Set<ConstraintViolation<TestDTOLength>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "超过长度限制的JSON应该验证失败");
    }

    // === 无效JSON测试 ===

    @Test
    public void testInvalidJSON_NotJSON() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "not json";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "非JSON字符串应该验证失败");
    }

    @Test
    public void testInvalidJSON_MissingClosingBrace() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"key\":\"value\"";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "缺少结束花括号的JSON应该验证失败");
    }

    @Test
    public void testInvalidJSON_TrailingComma_Strict() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{\"key\":\"value\",}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "严格模式下尾随逗号应该验证失败");
    }

    @Test
    public void testInvalidJSON_UnquotedKey() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "{key:\"value\"}";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "未加引号的键应该验证失败");
    }

    // === 空值测试 ===

    @Test
    public void testNullJSON() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = null;

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证");
    }

    @Test
    public void testEmptyString() {
        TestDTOAny dto = new TestDTOAny();
        dto.data = "";

        Set<ConstraintViolation<TestDTOAny>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证");
    }

    // === 测试DTO类 ===

    static class TestDTOAny {
        @JSON
        String data;
    }

    static class TestDTOObject {
        @JSON(type = JSON.JSONType.OBJECT)
        String jsonObject;
    }

    static class TestDTOArray {
        @JSON(type = JSON.JSONType.ARRAY)
        String jsonArray;
    }

    static class TestDTODepth {
        @JSON(maxDepth = 5)
        String data;
    }

    static class TestDTOLength {
        @JSON(maxLength = 50)
        String data;
    }
}
