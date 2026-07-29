package io.github.vipxieliang.validx.research;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.EAN;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.util.Set;

/**
 * 完整测试 Hibernate Validator 所有约束对空字符串的处理
 */
public class AllValidatorsEmptyStringTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private void test(String annotationName, Object dto) {
        Set<ConstraintViolation<Object>> violations = (Set) validator.validate(dto);
        System.out.println(String.format("%-25s | empty string: violations = %d | %s",
            annotationName, violations.size(), violations.size() == 0 ? "✅ PASS" : "❌ FAIL"));
    }

    // ========== 1. 格式验证类 ==========

    static class EmailDTO {
        @Email
        private String value;
        public EmailDTO(String value) { this.value = value; }
    }

    static class PatternDTO {
        @Pattern(regexp = "\\d+")
        private String value;
        public PatternDTO(String value) { this.value = value; }
    }

    // ========== 2. 数值范围约束 ==========

    static class MinDTO {
        @Min(1)
        private String value;
        public MinDTO(String value) { this.value = value; }
    }

    static class MaxDTO {
        @Max(100)
        private String value;
        public MaxDTO(String value) { this.value = value; }
    }

    static class DecimalMinDTO {
        @DecimalMin("1.0")
        private String value;
        public DecimalMinDTO(String value) { this.value = value; }
    }

    static class DecimalMaxDTO {
        @DecimalMax("100.0")
        private String value;
        public DecimalMaxDTO(String value) { this.value = value; }
    }

    // ========== 3. 正负数约束 ==========

    static class PositiveDTO {
        @Positive
        private String value;
        public PositiveDTO(String value) { this.value = value; }
    }

    static class PositiveOrZeroDTO {
        @PositiveOrZero
        private String value;
        public PositiveOrZeroDTO(String value) { this.value = value; }
    }

    static class NegativeDTO {
        @Negative
        private String value;
        public NegativeDTO(String value) { this.value = value; }
    }

    static class NegativeOrZeroDTO {
        @NegativeOrZero
        private String value;
        public NegativeOrZeroDTO(String value) { this.value = value; }
    }

    // ========== 4. 数字格式 ==========

    static class DigitsDTO {
        @Digits(integer = 3, fraction = 2)
        private String value;
        public DigitsDTO(String value) { this.value = value; }
    }

    // ========== 5. 大小/长度 ==========

    static class SizeDTO {
        @Size(min = 1, max = 10)
        private String value;
        public SizeDTO(String value) { this.value = value; }
    }

    // ========== 6. 空值检查 ==========

    static class NotNullDTO {
        @NotNull
        private String value;
        public NotNullDTO(String value) { this.value = value; }
    }

    static class NotEmptyDTO {
        @NotEmpty
        private String value;
        public NotEmptyDTO(String value) { this.value = value; }
    }

    static class NotBlankDTO {
        @NotBlank
        private String value;
        public NotBlankDTO(String value) { this.value = value; }
    }

    // ========== 7. Hibernate 扩展验证器 ==========

    static class URLDTO {
        @URL
        private String value;
        public URLDTO(String value) { this.value = value; }
    }

    static class CreditCardNumberDTO {
        @CreditCardNumber
        private String value;
        public CreditCardNumberDTO(String value) { this.value = value; }
    }

    static class EANDTO {
        @EAN
        private String value;
        public EANDTO(String value) { this.value = value; }
    }

    static class ISBNDTO {
        @ISBN
        private String value;
        public ISBNDTO(String value) { this.value = value; }
    }

    static class LengthDTO {
        @Length(min = 1, max = 10)
        private String value;
        public LengthDTO(String value) { this.value = value; }
    }

    static class RangeDTO {
        @Range(min = 1, max = 100)
        private String value;
        public RangeDTO(String value) { this.value = value; }
    }

    // ========== 测试方法 ==========

    @Test
    public void testAllValidators() {
        System.out.println("\n========== 格式验证类 ==========");
        test("@Email", new EmailDTO(""));
        test("@Pattern", new PatternDTO(""));

        System.out.println("\n========== 数值范围约束 ==========");
        test("@Min", new MinDTO(""));
        test("@Max", new MaxDTO(""));
        test("@DecimalMin", new DecimalMinDTO(""));
        test("@DecimalMax", new DecimalMaxDTO(""));

        System.out.println("\n========== 正负数约束 ==========");
        test("@Positive", new PositiveDTO(""));
        test("@PositiveOrZero", new PositiveOrZeroDTO(""));
        test("@Negative", new NegativeDTO(""));
        test("@NegativeOrZero", new NegativeOrZeroDTO(""));

        System.out.println("\n========== 数字格式 ==========");
        test("@Digits", new DigitsDTO(""));

        System.out.println("\n========== 大小/长度 ==========");
        test("@Size", new SizeDTO(""));
        test("@Length", new LengthDTO(""));

        System.out.println("\n========== 空值检查 ==========");
        test("@NotNull", new NotNullDTO(""));
        test("@NotEmpty", new NotEmptyDTO(""));
        test("@NotBlank", new NotBlankDTO(""));

        System.out.println("\n========== Hibernate 扩展验证器 ==========");
        test("@URL", new URLDTO(""));
        test("@CreditCardNumber", new CreditCardNumberDTO(""));
        test("@EAN", new EANDTO(""));
        test("@ISBN", new ISBNDTO(""));
        test("@Range", new RangeDTO(""));
    }
}
