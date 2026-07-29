package io.github.vipxieliang.validx.research;

import org.hibernate.validator.constraints.*;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.Pattern;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 重新审视所有 String 类型验证器对空字符串的处理
 * 尝试找出分类规律
 */
public class StringValidatorsEmptyStringTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private void test(String annotationName, Object dto, String category) {
        Set<ConstraintViolation<Object>> violations = (Set) validator.validate(dto);
        System.out.println(String.format("%-25s | category: %-20s | empty string: %s",
            annotationName, category, violations.size() == 0 ? "✅ PASS" : "❌ FAIL"));
    }

    // ========== 所有 String 类型的验证器 ==========

    static class EmailDTO { @Email private String value; public EmailDTO(String v) { this.value = v; } }
    static class PatternDTO { @Pattern(regexp = "\\d+") private String value; public PatternDTO(String v) { this.value = v; } }
    static class URLDTO { @URL private String value; public URLDTO(String v) { this.value = v; } }
    static class CreditCardDTO { @CreditCardNumber private String value; public CreditCardDTO(String v) { this.value = v; } }
    static class EANDTO { @EAN private String value; public EANDTO(String v) { this.value = v; } }
    static class ISBNDTO { @ISBN private String value; public ISBNDTO(String v) { this.value = v; } }

    @Test
    public void testAllStringValidators() {
        System.out.println("\n========== String 类型验证器对空字符串的处理 ==========\n");

        System.out.println("【假设1：通用格式 vs 特定格式】");
        test("@Email", new EmailDTO(""), "通用文本格式");
        test("@URL", new URLDTO(""), "通用文本格式");
        test("@Pattern", new PatternDTO(""), "自定义正则");
        test("@CreditCardNumber", new CreditCardDTO(""), "特定数字格式");
        test("@EAN", new EANDTO(""), "特定数字格式");
        test("@ISBN", new ISBNDTO(""), "特定数字格式");

        System.out.println("\n【分析】让我们看看它们的共同特征...\n");

        // 进一步分类
        System.out.println("PASS的验证器特征：");
        System.out.println("  @Email - 邮箱格式，RFC 5322标准，格式非常灵活");
        System.out.println("  @URL   - URL格式，RFC 3986标准，格式灵活");

        System.out.println("\nFAIL的验证器特征：");
        System.out.println("  @CreditCardNumber - 13-19位纯数字 + Luhn算法");
        System.out.println("  @EAN              - 8或13位纯数字 + 校验位");
        System.out.println("  @ISBN             - 10或13位 + 校验位");
        System.out.println("  @Pattern          - 取决于正则表达式本身");
    }
}
