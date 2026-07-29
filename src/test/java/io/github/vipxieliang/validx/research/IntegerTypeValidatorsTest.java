package io.github.vipxieliang.validx.research;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.*;
import java.util.Set;

/**
 * 测试 Hibernate Validator 中所有可能接受整型的注解对空字符串的处理
 */
public class IntegerTypeValidatorsTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    // ========== 数值范围约束 ==========

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

    // ========== 数字格式验证 ==========

    static class DigitsDTO {
        @Digits(integer = 3, fraction = 2)
        private String value;
        public DigitsDTO(String value) { this.value = value; }
    }

    // ========== 大小/长度约束 ==========

    static class SizeDTO {
        @Size(min = 1, max = 10)
        private String value;
        public SizeDTO(String value) { this.value = value; }
    }

    // ========== 正负数约束 ==========

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

    // ========== 测试方法 ==========

    @Test
    public void testMinWithEmptyString() {
        MinDTO dto = new MinDTO("");
        Set<ConstraintViolation<MinDTO>> violations = validator.validate(dto);
        System.out.println("@Min with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testMaxWithEmptyString() {
        MaxDTO dto = new MaxDTO("");
        Set<ConstraintViolation<MaxDTO>> violations = validator.validate(dto);
        System.out.println("@Max with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testDecimalMinWithEmptyString() {
        DecimalMinDTO dto = new DecimalMinDTO("");
        Set<ConstraintViolation<DecimalMinDTO>> violations = validator.validate(dto);
        System.out.println("@DecimalMin with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testDecimalMaxWithEmptyString() {
        DecimalMaxDTO dto = new DecimalMaxDTO("");
        Set<ConstraintViolation<DecimalMaxDTO>> violations = validator.validate(dto);
        System.out.println("@DecimalMax with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testDigitsWithEmptyString() {
        DigitsDTO dto = new DigitsDTO("");
        Set<ConstraintViolation<DigitsDTO>> violations = validator.validate(dto);
        System.out.println("@Digits with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testSizeWithEmptyString() {
        SizeDTO dto = new SizeDTO("");
        Set<ConstraintViolation<SizeDTO>> violations = validator.validate(dto);
        System.out.println("@Size with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testPositiveWithEmptyString() {
        PositiveDTO dto = new PositiveDTO("");
        Set<ConstraintViolation<PositiveDTO>> violations = validator.validate(dto);
        System.out.println("@Positive with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testPositiveOrZeroWithEmptyString() {
        PositiveOrZeroDTO dto = new PositiveOrZeroDTO("");
        Set<ConstraintViolation<PositiveOrZeroDTO>> violations = validator.validate(dto);
        System.out.println("@PositiveOrZero with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testNegativeWithEmptyString() {
        NegativeDTO dto = new NegativeDTO("");
        Set<ConstraintViolation<NegativeDTO>> violations = validator.validate(dto);
        System.out.println("@Negative with empty string: violations.size() = " + violations.size());
    }

    @Test
    public void testNegativeOrZeroWithEmptyString() {
        NegativeOrZeroDTO dto = new NegativeOrZeroDTO("");
        Set<ConstraintViolation<NegativeOrZeroDTO>> violations = validator.validate(dto);
        System.out.println("@NegativeOrZero with empty string: violations.size() = " + violations.size());
    }
}
