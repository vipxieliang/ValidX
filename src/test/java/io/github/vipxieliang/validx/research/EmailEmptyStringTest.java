package io.github.vipxieliang.validx.research;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Email;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 Hibernate Validator 的 @Email 对空字符串的处理
 */
public class EmailEmptyStringTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    static class TestDTO {
        @Email
        private String email;

        public TestDTO(String email) {
            this.email = email;
        }
    }

    @Test
    public void testEmailWithNull() {
        TestDTO dto = new TestDTO(null);
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Email with null: violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testEmailWithEmptyString() {
        TestDTO dto = new TestDTO("");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Email with empty string: violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testEmailWithValidEmail() {
        TestDTO dto = new TestDTO("test@example.com");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Email with 'test@example.com': violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testEmailWithInvalidEmail() {
        TestDTO dto = new TestDTO("invalid-email");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Email with 'invalid-email': violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }

    @Test
    public void testEmailWithBlankString() {
        TestDTO dto = new TestDTO("   ");
        Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
        System.out.println("@Email with '   ' (spaces): violations.size() = " + violations.size());
        violations.forEach(v -> System.out.println("  Message: " + v.getMessage()));
    }
}
