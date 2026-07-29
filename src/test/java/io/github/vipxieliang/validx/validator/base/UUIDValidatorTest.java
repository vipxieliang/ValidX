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

import io.github.vipxieliang.validx.annotations.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUID验证器测试类
 */
public class UUIDValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === 标准格式测试（带连字符） ===

    @Test
    public void testValidUUID_StandardFormat() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550e8400-e29b-41d4-a716-446655440000";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "标准UUID格式应该通过验证");
    }

    @Test
    public void testValidUUID_StandardFormat_Lowercase() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550e8400-e29b-41d4-a716-446655440000";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "小写UUID应该通过验证");
    }

    @Test
    public void testValidUUID_StandardFormat_Uppercase() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550E8400-E29B-41D4-A716-446655440000";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "大写UUID应该通过验证");
    }

    @Test
    public void testValidUUID_StandardFormat_MixedCase() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550e8400-E29b-41D4-a716-446655440000";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "大小写混合UUID应该通过验证");
    }

    @Test
    public void testInvalidUUID_NoHyphens_WhenNotAllowed() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550e8400e29b41d4a716446655440000";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "不带连字符的UUID在默认情况下应该验证失败");
    }

    @Test
    public void testInvalidUUID_WrongFormat() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550e8400-e29b-41d4-a716";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "错误格式的UUID应该验证失败");
    }

    @Test
    public void testInvalidUUID_ExtraCharacters() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550e8400-e29b-41d4-a716-446655440000-extra";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "带额外字符的UUID应该验证失败");
    }

    @Test
    public void testInvalidUUID_InvalidCharacters() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "550e8400-e29b-41d4-a716-44665544000g";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "包含非十六进制字符的UUID应该验证失败");
    }

    // === 允许不带连字符格式的测试 ===

    @Test
    public void testValidUUID_WithoutHyphens_WhenAllowed() {
        TestDTOFlexible dto = new TestDTOFlexible();
        dto.transactionId = "550e8400e29b41d4a716446655440000";

        Set<ConstraintViolation<TestDTOFlexible>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "允许时，不带连字符的UUID应该通过验证");
    }

    @Test
    public void testValidUUID_WithHyphens_WhenFlexible() {
        TestDTOFlexible dto = new TestDTOFlexible();
        dto.transactionId = "550e8400-e29b-41d4-a716-446655440000";

        Set<ConstraintViolation<TestDTOFlexible>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "允许不带连字符时，标准格式也应该通过验证");
    }

    @Test
    public void testInvalidUUID_WrongLength_WithoutHyphens() {
        TestDTOFlexible dto = new TestDTOFlexible();
        dto.transactionId = "550e8400e29b41d4a716";

        Set<ConstraintViolation<TestDTOFlexible>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "长度错误的UUID应该验证失败");
    }

    @Test
    public void testInvalidUUID_TooLong_WithoutHyphens() {
        TestDTOFlexible dto = new TestDTOFlexible();
        dto.transactionId = "550e8400e29b41d4a716446655440000extra";

        Set<ConstraintViolation<TestDTOFlexible>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "过长的UUID应该验证失败");
    }

    // === 空值测试 ===

    @Test
    public void testNullUUID() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = null;

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证（由@NotNull处理）");
    }

    @Test
    public void testEmptyUUID() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证（由@NotEmpty处理）");
    }

    @Test
    public void testWhitespaceUUID() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "   ";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空白字符串应该通过验证（由@NotBlank处理）");
    }

    // === 边界情况测试 ===

    @Test
    public void testUUID_AllZeros() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "00000000-0000-0000-0000-000000000000";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "全零UUID应该通过验证");
    }

    @Test
    public void testUUID_AllFs() {
        TestDTOStandard dto = new TestDTOStandard();
        dto.id = "ffffffff-ffff-ffff-ffff-ffffffffffff";

        Set<ConstraintViolation<TestDTOStandard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "全F UUID应该通过验证");
    }

    // === 测试DTO类 ===

    static class TestDTOStandard {
        @UUID
        String id;
    }

    static class TestDTOFlexible {
        @UUID(allowWithoutHyphens = true)
        String transactionId;
    }
}
