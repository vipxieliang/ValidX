package io.github.vipxieliang.validx.research;

import org.hibernate.validator.constraints.EAN;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 测试 EAN (European Article Number) 格式
 */
public class EANFormatTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    static class EANDTO {
        @EAN
        private String value;
        public EANDTO(String value) { this.value = value; }
    }

    @Test
    public void testEANFormats() {
        // EAN-13 标准格式（最常见）
        test("5901234123457", "EAN-13 标准条码");
        test("4006381333931", "EAN-13 德国条码");
        test("6901234567892", "EAN-13 中国条码");

        // EAN-8 格式
        test("12345670", "EAN-8 短格式");
        test("96385074", "EAN-8 示例");

        // 无效格式
        test("", "空字符串");
        test(null, "null值");
        test("123", "过短");
        test("12345678901234", "过长");
        test("123456789012", "EAN-12 (无效)");
        test("abcdefghijklm", "非数字");
        test("1234567890123", "EAN-13 校验位错误");
    }

    private void test(String value, String description) {
        EANDTO dto = new EANDTO(value);
        Set<ConstraintViolation<EANDTO>> violations = validator.validate(dto);
        System.out.println(String.format("%-25s | value: %-20s | violations: %d | %s",
            description,
            value == null ? "null" : "\"" + value + "\"",
            violations.size(),
            violations.size() == 0 ? "✅ VALID" : "❌ INVALID"
        ));
    }
}
