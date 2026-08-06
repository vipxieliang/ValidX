package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.Date;
import io.github.vipxieliang.validx.annotations.DateTime;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试当注解的 pattern 配置错误时，是否会影响其他字段的验证
 *
 * 场景：一个 DTO 有多个字段，其中一个字段的 @Date pattern 配置错误（包含时间符号）
 * 期望：能够获取所有字段的验证错误，而不是因为一个字段配置错误就导致整个验证崩溃
 */
public class PatternValidationBehaviorTest {

    /**
     * 测试用例 1：@Date 配置错误（pattern 包含时间符号）
     */
    public static class TestDTO1 {
        @Date(pattern = "yyyy-MM-dd HH:mm:ss")  // ❌ 错误：包含时间符号
        private String birthDate;

        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotNull(message = "邮箱不能为空")
        private String email;

        public void setBirthDate(String value) { this.birthDate = value; }
        public void setUsername(String value) { this.username = value; }
        public void setEmail(String value) { this.email = value; }
    }

    /**
     * 测试用例 2：@DateTime 配置错误（pattern 不包含时间符号）
     */
    public static class TestDTO2 {
        @DateTime(pattern = "yyyy-MM-dd")  // ❌ 错误：不包含时间符号
        private String timestamp;

        @NotBlank(message = "名称不能为空")
        private String name;

        public void setTimestamp(String value) { this.timestamp = value; }
        public void setName(String value) { this.name = value; }
    }

    /**
     * 测试用例 3：多个字段配置错误
     */
    public static class TestDTO3 {
        @Date(pattern = "yyyy-MM-dd HH:mm:ss")  // ❌ 错误
        private String date1;

        @Date  // ✅ 正确
        private String date2;

        @NotBlank
        private String field3;

        public void setDate1(String value) { this.date1 = value; }
        public void setDate2(String value) { this.date2 = value; }
        public void setField3(String value) { this.field3 = value; }
    }

    @Test
    public void testDateFormatWithWrongPattern_ShouldNotBlockOtherFieldValidation() {
        System.out.println("\n=== 测试 1：@Date pattern 配置错误是否影响其他字段验证 ===");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        TestDTO1 dto = new TestDTO1();
        dto.setBirthDate("2000-01-01");  // 值本身是有效的
        dto.setUsername("");              // 空用户名（应该报错）
        dto.setEmail(null);               // null 邮箱（应该报错）

        try {
            Set<ConstraintViolation<TestDTO1>> violations = validator.validate(dto);

            System.out.println("✅ 验证完成！没有抛出异常");
            System.out.println("   共发现 " + violations.size() + " 个验证错误：");

            for (ConstraintViolation<TestDTO1> v : violations) {
                System.out.println("   - " + v.getPropertyPath() + ": " + v.getMessage());
            }

            // 断言：应该能看到 username 和 email 的错误
            assertTrue(violations.size() >= 2,
                "期望至少有 2 个错误（username 和 email），实际有 " + violations.size() + " 个");

            boolean hasUsernameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
            boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));

            assertTrue(hasUsernameError, "应该包含 username 的验证错误");
            assertTrue(hasEmailError, "应该包含 email 的验证错误");

        } catch (Exception e) {
            System.out.println("❌ 抛出异常：" + e.getClass().getSimpleName());
            System.out.println("   消息：" + e.getMessage());
            e.printStackTrace();

            fail("验证过程不应该抛出异常，但抛出了：" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @Test
    public void testDateTimeFormatWithWrongPattern_ShouldNotBlockOtherFieldValidation() {
        System.out.println("\n=== 测试 2：@DateTime pattern 配置错误是否影响其他字段验证 ===");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        TestDTO2 dto = new TestDTO2();
        dto.setTimestamp("2024-01-15");
        dto.setName("");  // 空名称（应该报错）

        try {
            Set<ConstraintViolation<TestDTO2>> violations = validator.validate(dto);

            System.out.println("✅ 验证完成！没有抛出异常");
            System.out.println("   共发现 " + violations.size() + " 个验证错误：");

            for (ConstraintViolation<TestDTO2> v : violations) {
                System.out.println("   - " + v.getPropertyPath() + ": " + v.getMessage());
            }

            // 断言：应该能看到 name 的错误
            assertTrue(violations.size() >= 1,
                "期望至少有 1 个错误（name），实际有 " + violations.size() + " 个");

            boolean hasNameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));

            assertTrue(hasNameError, "应该包含 name 的验证错误");

        } catch (Exception e) {
            System.out.println("❌ 抛出异常：" + e.getClass().getSimpleName());
            System.out.println("   消息：" + e.getMessage());
            e.printStackTrace();

            fail("验证过程不应该抛出异常，但抛出了：" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @Test
    public void testMultipleFieldsWithMixedConfiguration() {
        System.out.println("\n=== 测试 3：多个字段混合配置（有错有对） ===");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        TestDTO3 dto = new TestDTO3();
        dto.setDate1("2024-01-15");     // date1 配置错误
        dto.setDate2("invalid-date");    // date2 配置正确但值无效
        dto.setField3("");               // field3 值无效

        try {
            Set<ConstraintViolation<TestDTO3>> violations = validator.validate(dto);

            System.out.println("✅ 验证完成！没有抛出异常");
            System.out.println("   共发现 " + violations.size() + " 个验证错误：");

            for (ConstraintViolation<TestDTO3> v : violations) {
                System.out.println("   - " + v.getPropertyPath() + ": " + v.getMessage());
            }

            // 断言：应该能看到 date2 和 field3 的错误
            assertTrue(violations.size() >= 2,
                "期望至少有 2 个错误（date2 和 field3），实际有 " + violations.size() + " 个");

        } catch (Exception e) {
            System.out.println("❌ 抛出异常：" + e.getClass().getSimpleName());
            System.out.println("   消息：" + e.getMessage());
            e.printStackTrace();

            fail("验证过程不应该抛出异常，但抛出了：" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
