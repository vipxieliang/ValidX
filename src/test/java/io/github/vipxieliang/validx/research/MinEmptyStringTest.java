package io.github.vipxieliang.validx.research;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 Hibernate Validator 的 @Min 对空字符串的处理
 */
public class MinEmptyStringTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    static class TestDTO {
        @Min(1)
        private String value;

        public TestDTO(String value) {
            this.value = value;
        }
    }

    @Test
    public void testMinWithNull() {
        TestDTO dto = new TestDTO(null);
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Min with null: violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testMinWithEmptyString() {
        TestDTO dto = new TestDTO("");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Min with empty string: violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testMinWithValidString() {
        TestDTO dto = new TestDTO("5");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Min with '5': violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testMinWithInvalidString() {
        TestDTO dto = new TestDTO("0");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Min with '0': violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testMinWithInvalidFormat() {
        TestDTO dto = new TestDTO("abc");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Min with 'abc': violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }
}
