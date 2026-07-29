package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.PhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 国际电话号码验证器
 *
 * 支持的格式：
 * - E.164标准格式：+8613812345678
 * - 带空格：+86 138 1234 5678
 * - 带连字符：+1-555-123-4567
 * - 带括号：(555) 123-4567, +1 (555) 123-4567
 * - 多种国际格式
 *
 * @author vipxieliang
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private String countryCode;
    private boolean allowExtension;
    private boolean strict;

    /**
     * 电话号码基本格式
     * 允许：+ 数字 空格 连字符 括号 点号
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,5}[-\\s.]?[0-9]{1,5}[-\\s.]?[0-9]{0,5}$"
    );

    /**
     * 分机号格式
     * 支持：ext. 123, ext 123, x123, #123, extension 123
     */
    private static final Pattern EXTENSION_PATTERN = Pattern.compile(
        "(?i)\\s*(ext\\.?|extension|x|#)\\s*[0-9]{1,6}$"
    );

    /**
     * E.164格式（国际标准）
     * + 后跟 4-15 位数字
     */
    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    @Override
    public void initialize(PhoneNumber annotation) {
        this.countryCode = annotation.countryCode();
        this.allowExtension = annotation.allowExtension();
        this.strict = annotation.strict();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 或空字符串视为有效（由 @NotNull/@NotEmpty 处理）
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        String phoneNumber = value.trim();
        String extension = null;

        // 处理分机号
        if (allowExtension && containsExtension(phoneNumber)) {
            int extIndex = findExtensionIndex(phoneNumber);
            if (extIndex > 0) {
                extension = phoneNumber.substring(extIndex);
                phoneNumber = phoneNumber.substring(0, extIndex).trim();
            }
        }

        // 严格模式：必须以+开头（国际格式）
        if (strict && !phoneNumber.startsWith("+")) {
            return false;
        }

        // 检查国家代码
        if (countryCode != null && !countryCode.isEmpty()) {
            String normalizedCountryCode = countryCode.startsWith("+") ? countryCode : "+" + countryCode;
            // 规范化电话号码（移除空格、连字符等）
            String normalizedPhone = normalizePhone(phoneNumber);
            if (!normalizedPhone.startsWith(normalizedCountryCode)) {
                return false;
            }
        }

        // 验证电话号码格式
        if (!isValidPhoneFormat(phoneNumber)) {
            return false;
        }

        // 验证分机号格式（如果有）
        if (extension != null && !isValidExtension(extension)) {
            return false;
        }

        return true;
    }

    /**
     * 检查是否包含分机号
     */
    private boolean containsExtension(String phone) {
        return EXTENSION_PATTERN.matcher(phone).find();
    }

    /**
     * 查找分机号起始位置
     */
    private int findExtensionIndex(String phone) {
        java.util.regex.Matcher matcher = EXTENSION_PATTERN.matcher(phone);
        if (matcher.find()) {
            return matcher.start();
        }
        return -1;
    }

    /**
     * 验证电话号码格式
     */
    private boolean isValidPhoneFormat(String phone) {
        // 基本格式检查
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return false;
        }

        // 规范化处理：移除所有非数字字符（除了开头的+）
        String normalized = normalizePhone(phone);

        // 如果是E.164格式，使用E.164验证
        if (normalized.startsWith("+")) {
            return E164_PATTERN.matcher(normalized).matches();
        }

        // 本地格式：只包含数字，长度在4-15之间
        String digitsOnly = normalized.replaceAll("[^0-9]", "");
        return digitsOnly.length() >= 4 && digitsOnly.length() <= 15;
    }

    /**
     * 规范化电话号码
     * 移除空格、连字符、括号、点号等格式字符，保留开头的+
     */
    private String normalizePhone(String phone) {
        if (phone.startsWith("+")) {
            return "+" + phone.substring(1).replaceAll("[^0-9]", "");
        }
        return phone.replaceAll("[^0-9]", "");
    }

    /**
     * 验证分机号格式
     */
    private boolean isValidExtension(String extension) {
        return EXTENSION_PATTERN.matcher(extension).find();
    }
}
