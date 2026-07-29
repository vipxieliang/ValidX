package io.github.vipxieliang.validx.annotations;

import io.github.vipxieliang.validx.validator.base.PhoneNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 国际电话号码验证注解
 * 支持多种国际电话号码格式，包括E.164标准格式
 *
 * 支持的格式示例：
 * - +8613812345678 (E.164格式)
 * - +86 138 1234 5678 (带空格)
 * - +1-555-123-4567 (带连字符)
 * - (555) 123-4567 (美国格式)
 * - +44 20 7123 4567 (英国格式)
 * - +81-3-1234-5678 (日本格式)
 *
 * @author vipxieliang
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface PhoneNumber {

    /**
     * 国家代码（可选）
     * 如果指定，则只接受该国家代码的电话号码
     * 例如："+86", "+1", "+44" 等
     * 默认为空字符串，表示接受所有国家代码
     */
    String countryCode() default "";

    /**
     * 是否允许分机号
     * 支持的分机号格式：ext. 123, ext 123, x123, #123
     * 默认为 true
     */
    boolean allowExtension() default true;

    /**
     * 是否严格模式
     * true: 必须包含国家代码（以+开头）
     * false: 也接受本地格式的电话号码
     * 默认为 false
     */
    boolean strict() default false;

    /**
     * 错误消息
     */
    String message() default "{io.github.vipxieliang.validx.annotation.phonenumber}";

    /**
     * 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};
}
