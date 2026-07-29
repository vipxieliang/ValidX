package io.github.vipxieliang.validx.research;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 测试 CreditCardNumber 格式
 */
public class CreditCardNumberFormatTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    static class CreditCardDTO {
        @CreditCardNumber
        private String value;
        public CreditCardDTO(String value) { this.value = value; }
    }

    @Test
    public void testCreditCardFormats() {
        // Visa (以4开头，13或16位)
        test("4532015112830366", "Visa - 16位");
        test("4111111111111111", "Visa - 测试卡号");

        // MasterCard (以51-55开头，16位)
        test("5425233430109903", "MasterCard - 16位");
        test("5555555555554444", "MasterCard - 测试卡号");

        // American Express (以34或37开头，15位)
        test("371449635398431", "AmEx - 15位");
        test("378282246310005", "AmEx - 测试卡号");

        // Discover (以6011开头，16位)
        test("6011111111111117", "Discover - 16位");

        // 带空格/横线的格式
        test("4532 0151 1283 0366", "Visa - 带空格");
        test("4532-0151-1283-0366", "Visa - 带横线");

        // 边界情况
        test("", "空字符串");
        test(null, "null值");
        test("   ", "空白字符串");
        test("1234567890123456", "16位数字但校验位错误");
        test("123", "过短");
        test("abcd1234abcd1234", "包含字母");
    }

    private void test(String value, String description) {
        CreditCardDTO dto = new CreditCardDTO(value);
        Set<ConstraintViolation<CreditCardDTO>> violations = validator.validate(dto);
        System.out.println(String.format("%-30s | value: %-25s | violations: %d | %s",
            description,
            value == null ? "null" : "\"" + value + "\"",
            violations.size(),
            violations.size() == 0 ? "✅ VALID" : "❌ INVALID"
        ));
        if (violations.size() > 0) {
            violations.forEach(v -> System.out.println("    Error: " + v.getMessage()));
        }
    }
}
