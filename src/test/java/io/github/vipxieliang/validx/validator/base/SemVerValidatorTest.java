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

import io.github.vipxieliang.validx.annotations.SemVer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SemVer验证器测试类
 */
public class SemVerValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === 基本版本号测试 ===

    @Test
    public void testValidBasicVersion() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "基本版本号应该通过验证");
    }

    @Test
    public void testValidVersionWithLargeNumbers() {
        TestDTO dto = new TestDTO();
        dto.version = "10.20.30";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "大数字版本号应该通过验证");
    }

    @Test
    public void testValidVersionWithZero() {
        TestDTO dto = new TestDTO();
        dto.version = "0.0.0";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "0.0.0版本号应该通过验证");
    }

    // === 预发布版本测试 ===

    @Test
    public void testValidVersionWithAlphaPrerelease() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0-alpha";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带alpha预发布标签应该通过验证");
    }

    @Test
    public void testValidVersionWithBetaPrerelease() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0-beta.1";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带beta.1预发布标签应该通过验证");
    }

    @Test
    public void testValidVersionWithRcPrerelease() {
        TestDTO dto = new TestDTO();
        dto.version = "2.1.3-rc.2";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带rc.2预发布标签应该通过验证");
    }

    @Test
    public void testValidVersionWithComplexPrerelease() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0-alpha.beta.1";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "复杂预发布标签应该通过验证");
    }

    // === 构建元数据测试 ===

    @Test
    public void testValidVersionWithBuildMetadata() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0+20130313144700";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带构建元数据应该通过验证");
    }

    @Test
    public void testValidVersionWithShortBuildMetadata() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0+001";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带简短构建元数据应该通过验证");
    }

    // === 完整格式测试 ===

    @Test
    public void testValidVersionWithPrereleaseAndBuild() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0-beta+exp.sha.5114f85";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "完整格式版本号应该通过验证");
    }

    @Test
    public void testValidVersionWithCompleteFormat() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0-alpha.1+001";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带预发布和构建元数据应该通过验证");
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidVersionWithLeadingZero() {
        TestDTO dto = new TestDTO();
        dto.version = "01.0.0";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "前导零版本号不应该通过验证");
    }

    @Test
    public void testInvalidVersionMissingParts() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "缺少部分的版本号不应该通过验证");
    }

    @Test
    public void testInvalidVersionWithText() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0.RELEASE";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "带非标准后缀的版本号不应该通过验证");
    }

    @Test
    public void testInvalidVersionEmpty() {
        TestDTO dto = new TestDTO();
        dto.version = "";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串由其他注解处理");
    }

    @Test
    public void testInvalidVersionWithSpaces() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0 ";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "带空格的版本号不应该通过验证");
    }

    // === v前缀测试 ===

    @Test
    public void testVersionWithVPrefixNotAllowed() {
        TestDTO dto = new TestDTO();
        dto.version = "v1.0.0";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "默认不允许v前缀");
    }

    @Test
    public void testVersionWithVPrefixAllowed() {
        TestDTOWithVPrefix dto = new TestDTOWithVPrefix();
        dto.version = "v1.0.0";

        Set<ConstraintViolation<TestDTOWithVPrefix>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "允许v前缀时应该通过验证");
    }

    @Test
    public void testVersionWithoutVPrefixWhenAllowed() {
        TestDTOWithVPrefix dto = new TestDTOWithVPrefix();
        dto.version = "1.0.0";

        Set<ConstraintViolation<TestDTOWithVPrefix>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "允许v前缀时，无前缀也应该通过验证");
    }

    @Test
    public void testVersionWithVPrefixAndPrerelease() {
        TestDTOWithVPrefix dto = new TestDTOWithVPrefix();
        dto.version = "v2.1.0-beta.1";

        Set<ConstraintViolation<TestDTOWithVPrefix>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "v前缀+预发布版本应该通过验证");
    }

    // === Null值测试 ===

    @Test
    public void testNullVersion() {
        TestDTO dto = new TestDTO();
        dto.version = null;

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值由@NotNull注解处理");
    }

    // === 边界情况测试 ===

    @Test
    public void testVersionWithVeryLargeNumbers() {
        TestDTO dto = new TestDTO();
        dto.version = "999.999.999";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "非常大的版本号应该通过验证");
    }

    @Test
    public void testVersionWithZeroMinorAndPatch() {
        TestDTO dto = new TestDTO();
        dto.version = "1.0.0";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "零次版本号和修订号应该通过验证");
    }

    // === 测试DTO ===

    static class TestDTO {
        @SemVer
        String version;
    }

    static class TestDTOWithVPrefix {
        @SemVer(allowVPrefix = true)
        String version;
    }
}
