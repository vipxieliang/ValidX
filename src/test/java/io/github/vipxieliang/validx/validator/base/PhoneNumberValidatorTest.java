package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PhoneNumber注解验证器测试
 */
public class PhoneNumberValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === E.164格式测试 ===

    static class TestDTOBasic {
        @PhoneNumber
        String phoneNumber;
    }

    @Test
    public void testValidPhoneNumber_E164_China() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+8613812345678";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效的中国E.164格式电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_E164_USA() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+14155552671";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效的美国E.164格式电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_E164_UK() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+442071231234";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效的英国E.164格式电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_E164_Japan() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+81312345678";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效的日本E.164格式电话号码应该通过验证");
    }

    // === 带格式的电话号码测试 ===

    @Test
    public void testValidPhoneNumber_WithSpaces() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+86 138 1234 5678";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带空格的电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_WithHyphens() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+1-415-555-2671";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带连字符的电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_WithParentheses() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+1 (415) 555-2671";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带括号的电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_USFormat() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "(555) 123-4567";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "美国本地格式电话号码应该通过验证");
    }

    // === 本地格式测试 ===

    @Test
    public void testValidPhoneNumber_LocalFormat_China() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "13812345678";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "中国本地格式电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_LocalFormat_WithSpaces() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "138 1234 5678";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "本地格式带空格的电话号码应该通过验证");
    }

    // === 分机号测试 ===

    static class TestDTOWithExtension {
        @PhoneNumber(allowExtension = true)
        String phoneNumber;
    }

    @Test
    public void testValidPhoneNumber_WithExtension_ext() {
        TestDTOWithExtension dto = new TestDTOWithExtension();
        dto.phoneNumber = "+1-415-555-2671 ext. 123";
        Set<ConstraintViolation<TestDTOWithExtension>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带分机号(ext.)的电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_WithExtension_x() {
        TestDTOWithExtension dto = new TestDTOWithExtension();
        dto.phoneNumber = "+14155552671 x123";
        Set<ConstraintViolation<TestDTOWithExtension>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带分机号(x)的电话号码应该通过验证");
    }

    @Test
    public void testValidPhoneNumber_WithExtension_hash() {
        TestDTOWithExtension dto = new TestDTOWithExtension();
        dto.phoneNumber = "+14155552671#456";
        Set<ConstraintViolation<TestDTOWithExtension>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "带分机号(#)的电话号码应该通过验证");
    }

    static class TestDTONoExtension {
        @PhoneNumber(allowExtension = false)
        String phoneNumber;
    }

    @Test
    public void testInvalidPhoneNumber_ExtensionNotAllowed() {
        TestDTONoExtension dto = new TestDTONoExtension();
        dto.phoneNumber = "+1-415-555-2671 ext. 123";
        Set<ConstraintViolation<TestDTONoExtension>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "不允许分机号时应该验证失败");
    }

    // === 国家代码限制测试 ===

    static class TestDTOCountryCode {
        @PhoneNumber(countryCode = "+86")
        String phoneNumber;
    }

    @Test
    public void testValidPhoneNumber_CountryCode_Match() {
        TestDTOCountryCode dto = new TestDTOCountryCode();
        dto.phoneNumber = "+8613812345678";
        Set<ConstraintViolation<TestDTOCountryCode>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "匹配国家代码的电话号码应该通过验证");
    }

    @Test
    public void testInvalidPhoneNumber_CountryCode_Mismatch() {
        TestDTOCountryCode dto = new TestDTOCountryCode();
        dto.phoneNumber = "+14155552671";
        Set<ConstraintViolation<TestDTOCountryCode>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "不匹配国家代码的电话号码应该验证失败");
    }

    // === 严格模式测试 ===

    static class TestDTOStrict {
        @PhoneNumber(strict = true)
        String phoneNumber;
    }

    @Test
    public void testValidPhoneNumber_Strict_WithCountryCode() {
        TestDTOStrict dto = new TestDTOStrict();
        dto.phoneNumber = "+8613812345678";
        Set<ConstraintViolation<TestDTOStrict>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "严格模式下带国家代码的电话号码应该通过验证");
    }

    @Test
    public void testInvalidPhoneNumber_Strict_NoCountryCode() {
        TestDTOStrict dto = new TestDTOStrict();
        dto.phoneNumber = "13812345678";
        Set<ConstraintViolation<TestDTOStrict>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "严格模式下不带国家代码的电话号码应该验证失败");
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidPhoneNumber_TooShort() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "123";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "太短的电话号码应该验证失败");
    }

    @Test
    public void testInvalidPhoneNumber_TooLong() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+12345678901234567890";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "太长的电话号码应该验证失败");
    }

    @Test
    public void testInvalidPhoneNumber_WithLetters() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "+86138abc5678";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "包含字母的电话号码应该验证失败");
    }

    @Test
    public void testInvalidPhoneNumber_OnlyLetters() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "abcdefghij";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "纯字母的电话号码应该验证失败");
    }

    // === Null和空值测试 ===

    @Test
    public void testNullPhoneNumber() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = null;
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证");
    }

    @Test
    public void testEmptyPhoneNumber() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证");
    }

    @Test
    public void testWhitespacePhoneNumber() {
        TestDTOBasic dto = new TestDTOBasic();
        dto.phoneNumber = "   ";
        Set<ConstraintViolation<TestDTOBasic>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "纯空格应该通过验证");
    }

    // === 复杂场景测试 ===

    static class TestDTOComplex {
        @PhoneNumber(countryCode = "+1", allowExtension = true, strict = true)
        String phoneNumber;
    }

    @Test
    public void testValidPhoneNumber_ComplexRules() {
        TestDTOComplex dto = new TestDTOComplex();
        dto.phoneNumber = "+1-415-555-2671 ext. 123";
        Set<ConstraintViolation<TestDTOComplex>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "符合所有复杂规则的电话号码应该通过验证");
    }

    @Test
    public void testInvalidPhoneNumber_ComplexRules_WrongCountry() {
        TestDTOComplex dto = new TestDTOComplex();
        dto.phoneNumber = "+8613812345678 ext. 123";
        Set<ConstraintViolation<TestDTOComplex>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "国家代码不匹配应该验证失败");
    }

    @Test
    public void testInvalidPhoneNumber_ComplexRules_NoCountryCode() {
        TestDTOComplex dto = new TestDTOComplex();
        dto.phoneNumber = "4155552671 ext. 123";
        Set<ConstraintViolation<TestDTOComplex>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "严格模式下缺少国家代码应该验证失败");
    }
}
