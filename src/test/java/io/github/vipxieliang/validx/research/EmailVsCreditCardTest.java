package io.github.vipxieliang.validx.research;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.Email;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 对比 Email 和 CreditCardNumber 的验证逻辑差异
 */
public class EmailVsCreditCardTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    static class EmailDTO {
        @Email
        private String value;
        public EmailDTO(String value) { this.value = value; }
    }

    static class CreditCardDTO {
        @CreditCardNumber
        private String value;
        public CreditCardDTO(String value) { this.value = value; }
    }

    @Test
    public void compareValidationLogic() {
        System.out.println("========== 验证逻辑对比 ==========\n");

        System.out.println("1. 空值处理：");
        compareTest("", "空字符串");
        compareTest(null, "null");
        compareTest("   ", "空白字符串");

        System.out.println("\n2. 部分有效的输入：");
        compareTest("user", "user（没有@）");
        compareTest("@example.com", "@example.com（没有用户名）");
        compareTest("user@", "user@（没有域名）");
        compareTest("1234", "1234（数字太短）");
        compareTest("1111111111111111", "1111111111111111（16位1，校验位错误）");

        System.out.println("\n3. 格式变化：");
        compareTest("User@Example.COM", "User@Example.COM（大小写）");
        compareTest("user+tag@example.com", "user+tag@example.com（带标签）");
        compareTest("4111-1111-1111-1111", "4111-1111-1111-1111（带横线）");
        compareTest("4111 1111 1111 1111", "4111 1111 1111 1111（带空格）");

        System.out.println("\n4. 有效值：");
        compareTest("user@example.com", "user@example.com");
        compareTest("4111111111111111", "4111111111111111（Visa测试卡）");
    }

    private void compareTest(String value, String description) {
        EmailDTO emailDTO = new EmailDTO(value);
        Set<ConstraintViolation<EmailDTO>> emailViolations = validator.validate(emailDTO);

        CreditCardDTO cardDTO = new CreditCardDTO(value);
        Set<ConstraintViolation<CreditCardDTO>> cardViolations = validator.validate(cardDTO);

        String displayValue = value == null ? "null" : "\"" + value + "\"";

        System.out.println(String.format("%-35s | value: %-30s", description, displayValue));
        System.out.println(String.format("  @Email:            %s",
            emailViolations.size() == 0 ? "✅ PASS" : "❌ FAIL"));
        System.out.println(String.format("  @CreditCardNumber: %s",
            cardViolations.size() == 0 ? "✅ PASS" : "❌ FAIL"));
    }
}
