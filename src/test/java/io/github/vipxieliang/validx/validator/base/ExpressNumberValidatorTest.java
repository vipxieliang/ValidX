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

import io.github.vipxieliang.validx.annotations.ExpressNumber;
import io.github.vipxieliang.validx.annotations.ExpressNumber.ExpressCompany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExpressNumber验证器测试类
 */
public class ExpressNumberValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === 顺丰速运测试 ===

    @Test
    public void testValidSFExpress() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "123456789012";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "12位数字应该是有效的顺丰快递单号");
    }

    @Test
    public void testInvalidExpressNumber_SpecialChars() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "12345@#$%^&*";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "包含特殊字符应该无效");
    }

    // === 圆通速递测试 ===

    @Test
    public void testValidYTOExpress_WithPrefix() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "YT1234567890123";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "YT开头的单号应该有效");
    }

    @Test
    public void testValidYTOExpress_PureNumbers() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "1234567890";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "10位纯数字应该有效");
    }

    // === 申通快递测试 ===

    @Test
    public void testValidSTOExpress() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "123456789012";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "12位数字应该是有效的申通快递单号");
    }

    // === 中通快递测试 ===

    @Test
    public void testValidZTOExpress_Numbers() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "123456789012";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "12位数字应该是有效的中通快递单号");
    }

    @Test
    public void testValidZTOExpress_AlphaNumeric() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "ZT1234567890";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "字母数字组合应该有效");
    }

    // === 韵达快递测试 ===

    @Test
    public void testValidYundaExpress() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "1234567890123";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "13位数字应该是有效的韵达快递单号");
    }

    @Test
    public void testInvalidYundaExpress_TooShort() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "123456";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "6位数字应该无效");
    }

    // === 邮政EMS测试 ===

    @Test
    public void testValidEMS_EFormat() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "E123456789CN";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "E+9位数字+CN应该有效");
    }

    @Test
    public void testValidEMS_TwoLetterFormat() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "EA123456789CN";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "2位字母+9位数字+CN应该有效");
    }

    @Test
    public void testInvalidEMS_WrongFormat() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "E12345678"; // 只有8位数字

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "E+8位数字应该无效");
    }

    // === 京东物流测试 ===

    @Test
    public void testValidJDLogistics() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "JD1234567890123";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "JD+13位数字应该有效");
    }

    @Test
    public void testInvalidJDLogistics_NoPrefix() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "1234567"; // 只有7位数字，太短

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "7位数字应该无效");
    }

    // === 德邦快递测试 ===

    @Test
    public void testValidDeppon_8Digits() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "12345678";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "8位数字应该是有效的德邦快递单号");
    }

    @Test
    public void testValidDeppon_9Digits() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "123456789";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "9位数字应该是有效的德邦快递单号");
    }

    // === 天天快递测试 ===

    @Test
    public void testValidTTKDExpress() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "123456789012";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "12位数字应该是有效的天天快递单号");
    }

    // === 百世快递测试 ===

    @Test
    public void testValidBestExpress() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "1234567890";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "10位数字或字母应该是有效的百世快递单号");
    }

    // === 指定快递公司测试 ===

    @Test
    public void testSpecificCompany_SFOnly() {
        SpecificCompanyDTO dto = new SpecificCompanyDTO();
        dto.sfNumber = "123456789012";

        Set<ConstraintViolation<SpecificCompanyDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "顺丰格式应该有效");
    }

    @Test
    public void testSpecificCompany_SFOnly_Invalid() {
        SpecificCompanyDTO dto = new SpecificCompanyDTO();
        dto.sfNumber = "YT1234567890123"; // 圆通格式

        Set<ConstraintViolation<SpecificCompanyDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "圆通格式对于仅顺丰验证应该无效");
    }

    // === Null和空值测试 ===

    @Test
    public void testValidExpressNumber_Null() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = null;

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证");
    }

    @Test
    public void testValidExpressNumber_Empty() {
        TestDTO dto = new TestDTO();
        dto.expressNumber = "";

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证");
    }

    // === 静态方法测试 ===

    @Test
    public void testStaticMethod_Valid() {
        assertTrue(ExpressNumberValidator.isValid("123456789012"), "静态方法应该验证有效快递单号");
    }

    @Test
    public void testStaticMethod_Invalid() {
        assertFalse(ExpressNumberValidator.isValid("invalid"), "静态方法应该拒绝无效快递单号");
    }

    @Test
    public void testStaticMethod_WithCompany() {
        assertTrue(ExpressNumberValidator.isValid("E123456789CN", ExpressCompany.EMS),
            "静态方法应该验证指定公司的快递单号");
    }

    @Test
    public void testStaticMethod_Null() {
        assertTrue(ExpressNumberValidator.isValid(null), "静态方法应该接受null");
    }

    // === 测试DTO类 ===

    static class TestDTO {
        @ExpressNumber
        public String expressNumber;
    }

    static class SpecificCompanyDTO {
        @ExpressNumber(companies = {ExpressCompany.SF_EXPRESS})
        public String sfNumber;
    }
}
