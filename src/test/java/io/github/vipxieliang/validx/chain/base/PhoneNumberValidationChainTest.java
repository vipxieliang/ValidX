package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PhoneNumber链式验证测试
 */
public class PhoneNumberValidationChainTest {

    // === 基本E.164格式测试 ===

    @Test
    public void testValidPhoneNumber_E164_China() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678");

        assertTrue(validator.passed(), "有效的中国E.164格式电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_E164_USA() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+14155552671");

        assertTrue(validator.passed(), "有效的美国E.164格式电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_E164_UK() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+442071231234");

        assertTrue(validator.passed(), "有效的英国E.164格式电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_E164_Japan() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+81312345678");

        assertTrue(validator.passed(), "有效的日本E.164格式电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 带格式的电话号码测试 ===

    @Test
    public void testValidPhoneNumber_WithSpaces() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+86 138 1234 5678");

        assertTrue(validator.passed(), "带空格的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_WithHyphens() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+1-415-555-2671");

        assertTrue(validator.passed(), "带连字符的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_WithParentheses() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+1 (415) 555-2671");

        assertTrue(validator.passed(), "带括号的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_USFormat() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("(555) 123-4567");

        assertTrue(validator.passed(), "美国本地格式电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 本地格式测试 ===

    @Test
    public void testValidPhoneNumber_LocalFormat_China() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("13812345678");

        assertTrue(validator.passed(), "中国本地格式电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_LocalFormat_WithSpaces() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("138 1234 5678");

        assertTrue(validator.passed(), "本地格式带空格的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 国家代码限制测试 ===

    @Test
    public void testValidPhoneNumber_CountryCode_China() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678", "+86");

        assertTrue(validator.passed(), "匹配国家代码的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testInvalidPhoneNumber_CountryCode_Mismatch() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+14155552671", "+86");

        assertFalse(validator.passed(), "不匹配国家代码的电话号码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_CountryCode_USA() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+14155552671", "+1");

        assertTrue(validator.passed(), "匹配美国国家代码的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 分机号测试 ===

    @Test
    public void testValidPhoneNumber_WithExtension_ext() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+1-415-555-2671 ext. 123", "", true);

        assertTrue(validator.passed(), "带分机号(ext.)的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_WithExtension_x() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+14155552671 x123", "", true);

        assertTrue(validator.passed(), "带分机号(x)的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidPhoneNumber_WithExtension_hash() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+14155552671#456", "", true);

        assertTrue(validator.passed(), "带分机号(#)的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testInvalidPhoneNumber_ExtensionNotAllowed() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+1-415-555-2671 ext. 123", "", false);

        assertFalse(validator.passed(), "不允许分机号时应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    // === 严格模式测试 ===

    @Test
    public void testValidPhoneNumber_Strict_WithCountryCode() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678", "", true, true);

        assertTrue(validator.passed(), "严格模式下带国家代码的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testInvalidPhoneNumber_Strict_NoCountryCode() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("13812345678", "", true, true);

        assertFalse(validator.passed(), "严格模式下不带国家代码的电话号码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidPhoneNumber_TooShort() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("123");

        assertFalse(validator.passed(), "太短的电话号码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidPhoneNumber_TooLong() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+12345678901234567890");

        assertFalse(validator.passed(), "太长的电话号码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidPhoneNumber_WithLetters() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+86138abc5678");

        assertFalse(validator.passed(), "包含字母的电话号码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidPhoneNumber_OnlyLetters() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("abcdefghij");

        assertFalse(validator.passed(), "纯字母的电话号码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    // === Null和空值测试 ===

    @Test
    public void testNullPhoneNumber() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber(null);

        assertTrue(validator.passed(), "null值应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testEmptyPhoneNumber() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("");

        assertTrue(validator.passed(), "空字符串应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 链式验证测试 ===

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678")
                .isPhoneNumber("+14155552671")
                .isPhoneNumber("+442071231234");

        assertTrue(validator.passed(), "多个有效电话号码应该全部通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678")
                .isPhoneNumber("abc123")
                .isPhoneNumber("+442071231234");

        assertFalse(validator.passed(), "包含无效电话号码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_MixedFormats() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678")
                .isPhoneNumber("+1-415-555-2671")
                .isPhoneNumber("(555) 123-4567");

        assertTrue(validator.passed(), "混合格式验证应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    // === 与其他验证器混合测试 ===

    @Test
    public void testMixedValidation_PhoneNumberAndEmail() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678")
                .isEmail("test@example.com");

        assertTrue(validator.passed(), "电话号码和邮箱验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testMixedValidation_PhoneNumberAndChineseIdCard() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678")
                .isChineseIdCard("110101199003072113");

        assertTrue(validator.passed(), "电话号码和身份证验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    // === 实际应用场景测试 ===

    @Test
    public void testRealWorld_UserRegistration() {
        ValidX validator = ValidX.init();
        // 用户注册：验证中国手机号
        validator.isPhoneNumber("+8613812345678", "+86");

        assertTrue(validator.passed(), "用户注册场景的中国手机号应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testRealWorld_InternationalContact() {
        ValidX validator = ValidX.init();
        // 国际联系人：接受任何国家的电话号码
        validator.isPhoneNumber("+14155552671")
                .isPhoneNumber("+442071231234")
                .isPhoneNumber("+8613812345678");

        assertTrue(validator.passed(), "国际联系人场景应该接受多国电话号码");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testRealWorld_CompanyPhone() {
        ValidX validator = ValidX.init();
        // 公司电话：需要分机号
        validator.isPhoneNumber("+1-415-555-2671 ext. 123", "", true);

        assertTrue(validator.passed(), "公司电话带分机号应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testRealWorld_StrictInternational() {
        ValidX validator = ValidX.init();
        // 严格国际格式：必须带国家代码
        validator.isPhoneNumber("+8613812345678", "", true, true)
                .isPhoneNumber("+14155552671", "", true, true);

        assertTrue(validator.passed(), "严格国际格式应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    // === 复杂规则测试 ===

    @Test
    public void testComplexRules_AllOptions() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+1-415-555-2671 ext. 123", "+1", true, true);

        assertTrue(validator.passed(), "符合所有复杂规则的电话号码应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testComplexRules_WrongCountry() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("+8613812345678 ext. 123", "+1", true, true);

        assertFalse(validator.passed(), "国家代码不匹配应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testComplexRules_NoCountryCodeInStrict() {
        ValidX validator = ValidX.init();
        validator.isPhoneNumber("4155552671 ext. 123", "+1", true, true);

        assertFalse(validator.passed(), "严格模式下缺少国家代码应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }
}
