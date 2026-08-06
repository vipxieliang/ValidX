import io.github.vipxieliang.validx.annotations.Date;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import java.util.Set;

public class TestPatternValidation {

    public static class TestDTO {
        @Date(pattern = "yyyy-MM-dd HH:mm:ss")  // 错误：包含时间符号
        private String date1;

        @Date  // 正确
        private String date2;

        public void setDate1(String value) { this.date1 = value; }
        public void setDate2(String value) { this.date2 = value; }
    }
    
    public static void main(String[] args) {
        System.out.println("=== 测试 pattern 验证行为 ===\n");
        
        try {
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            TestDTO dto = new TestDTO();
            dto.setDate1("2024-01-15");
            dto.setDate2("invalid");
            
            System.out.println("开始验证...");
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(dto);
            
            System.out.println("验证完成！共 " + violations.size() + " 个错误：");
            for (ConstraintViolation<TestDTO> v : violations) {
                System.out.println("- " + v.getPropertyPath() + ": " + v.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ 抛出异常：" + e.getClass().getSimpleName());
            System.out.println("   消息：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
