package io.github.vipxieliang.validx.research;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import java.util.Set;

/**
 * 测试信用卡号是否能用 Long 类型存储
 */
public class CreditCardNumberLengthTest {

    @Test
    public void testCreditCardNumberRange() {
        System.out.println("========== 数值类型范围分析 ==========\n");

        // Long 的最大值
        long longMax = Long.MAX_VALUE;
        System.out.println("Long.MAX_VALUE: " + longMax);
        System.out.println("Long.MAX_VALUE 位数: " + String.valueOf(longMax).length() + " 位");
        System.out.println();

        // 信用卡号的长度
        System.out.println("信用卡号长度范围: 13-19 位");
        System.out.println();

        // 测试不同长度的信用卡号
        String[] cardNumbers = {
            "4111111111111",        // 13位 Visa (最短)
            "4111111111111111",     // 16位 Visa (最常见)
            "371449635398431",      // 15位 AmEx
            "6011111111111117",     // 16位 Discover
            "1234567890123456789"   // 19位 (最长)
        };

        System.out.println("信用卡号示例：");
        for (String cardNumber : cardNumbers) {
            System.out.println(String.format("  %s (%d位)", cardNumber, cardNumber.length()));

            try {
                long value = Long.parseLong(cardNumber);
                System.out.println(String.format("    → 可以转换为 Long: %d", value));
            } catch (NumberFormatException e) {
                System.out.println("    → ❌ 无法转换为 Long: " + e.getMessage());
            }
        }

        System.out.println();
        System.out.println("========== 结论 ==========");

        // 19位数字的最大值
        String nineteenDigits = "9999999999999999999";  // 19个9
        System.out.println("19位数字最大值: " + nineteenDigits);

        try {
            long value = Long.parseLong(nineteenDigits);
            System.out.println("✅ 可以转换为 Long: " + value);
        } catch (NumberFormatException e) {
            System.out.println("❌ 超出 Long 范围！");
            System.out.println("原因: " + e.getMessage());
        }

        // 比较数值
        System.out.println();
        System.out.println("Long.MAX_VALUE =     " + longMax);
        System.out.println("19位最大值     = 9999999999999999999");
        System.out.println();

        // 18位数字测试
        String eighteenDigits = "999999999999999999";  // 18个9
        try {
            long value = Long.parseLong(eighteenDigits);
            System.out.println("18位数字最大值: " + eighteenDigits);
            System.out.println("✅ 可以转换为 Long: " + value);
        } catch (NumberFormatException e) {
            System.out.println("❌ 18位也超出范围");
        }

        System.out.println();
        System.out.println("结论:");
        System.out.println("- Long.MAX_VALUE 是 " + String.valueOf(longMax).length() + " 位数字");
        System.out.println("- 信用卡号最长 19 位");
        System.out.println("- 19位数字最大值 > Long.MAX_VALUE");
        System.out.println("- 因此信用卡号 **不能用 Long 类型存储**");
        System.out.println("- 必须使用 String 类型");
    }

    public static class TestMinEmptyString {
        static class TestDTO {
            @Min(1)
            String value;
        }

        public static void main(String[] args) {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            // 测试空字符串
            TestDTO dto1 = new TestDTO();
            dto1.value = "";
            Set<ConstraintViolation<TestDTO>> violations1 = validator.validate(dto1);
            System.out.println("Empty string (\"\") violations: " + violations1.size());

            // 测试 null
            TestDTO dto2 = new TestDTO();
            dto2.value = null;
            Set<ConstraintViolation<TestDTO>> violations2 = validator.validate(dto2);
            System.out.println("Null violations: " + violations2.size());

            // 测试有效值
            TestDTO dto3 = new TestDTO();
            dto3.value = "5";
            Set<ConstraintViolation<TestDTO>> violations3 = validator.validate(dto3);
            System.out.println("Valid value (\"5\") violations: " + violations3.size());
        }
    }
}
