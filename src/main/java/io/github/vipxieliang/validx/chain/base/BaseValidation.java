/*
 * Copyright 2025-2025 vipxieliang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.validator.base.*;
import io.github.vipxieliang.validx.validator.network.PortValidator;
import io.github.vipxieliang.validx.annotations.In;
import io.github.vipxieliang.validx.annotations.NotIn;
import io.github.vipxieliang.validx.annotations.Port;
import io.github.vipxieliang.validx.annotations.PastDate;
import io.github.vipxieliang.validx.annotations.PastDateTime;
import io.github.vipxieliang.validx.annotations.FutureDate;
import io.github.vipxieliang.validx.annotations.FutureDateTime;
import io.github.vipxieliang.validx.annotations.FileExtension;
import io.github.vipxieliang.validx.annotations.Timestamp;
import io.github.vipxieliang.validx.i18n.MessageManager;

import java.util.List;
import java.util.Locale;

public class BaseValidation {
    
    public void validateChineseAlpha(Object value, List<String> errors, Locale locale) {
        ChineseAlphaValidator validator = new ChineseAlphaValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.alpha", locale));
        }
    }
    
    public void validateChineseAlphaNum(Object value, List<String> errors, Locale locale) {
        ChineseAlphaNumValidator validator = new ChineseAlphaNumValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.alpha.num", locale));
        }
    }
    
    public void validateChineseAlphaDash(Object value, List<String> errors, Locale locale) {
        ChineseAlphaDashValidator validator = new ChineseAlphaDashValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.alpha.dash", locale));
        }
    }
    
    public void validateLower(Object value, List<String> errors, Locale locale) {
        LowerValidator validator = new LowerValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.lower", locale));
        }
    }
    
    public void validateUpper(Object value, List<String> errors, Locale locale) {
        UpperValidator validator = new UpperValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.upper", locale));
        }
    }
    
    public void validateXdigit(Object value, List<String> errors, Locale locale) {
        XdigitValidator validator = new XdigitValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.xdigit", locale));
        }
    }
    
    public void validateIn(Object value, String[] values, List<String> errors, Locale locale) {
        InValidator validator = new InValidator();
        validator.initialize(values);
        if (!validator.isValid(value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.in", locale));
        }
    }
    
    public void validateNotIn(Object value, String[] values, List<String> errors, Locale locale) {
        NotInValidator validator = new NotInValidator();
        validator.initialize(values);
        if (!validator.isValid(value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.not.in", locale));
        }
    }

    public void validatePort(Object value, List<String> errors, Locale locale) {
        PortValidator validator = new PortValidator();
        if (!validator.isValid(value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.port", locale));
        }
    }
    
    public void validatePastDate(Object value, boolean includeToday, String pattern, List<String> errors, Locale locale) {
        PastDateValidator validator = new PastDateValidator();
        validator.initialize(includeToday, pattern);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.past.date", locale));
        }
    }

    public void validateFutureDate(Object value, boolean includeToday, String pattern, List<String> errors, Locale locale) {
        FutureDateValidator validator = new FutureDateValidator();
        validator.initialize(includeToday, pattern);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.future.date", locale));
        }
    }
    
    public void validateFileExtension(Object value, String[] extensions, List<String> errors, Locale locale) {
        validateFileExtension(value, extensions, true, errors, locale);
    }

    public void validateFileExtension(Object value, String[] extensions, boolean ignoreCase, List<String> errors, Locale locale) {
        FileExtensionValidator validator = new FileExtensionValidator();
        validator.initialize(extensions, ignoreCase);
        if (!validator.isValid(value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.file.extension", locale));
        }
    }
    
    public void validateHourMinute(Object value, List<String> errors, Locale locale) {
        HourMinuteValidator validator = new HourMinuteValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.hour.minute", locale));
        }
    }
    
    public void validateHourMinuteSecond(Object value, List<String> errors, Locale locale) {
        HourMinuteSecondValidator validator = new HourMinuteSecondValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.hour.minute.second", locale));
        }
    }
    
    public void validateEnum(Object value, Class<? extends java.lang.Enum<?>> target, String field, List<String> errors, Locale locale) {
        EnumValidator validator = new EnumValidator();

        // 将字段名转换为方法名（例如"code"转换为"getCode"）
        String methodName = field;
        if (field != null && !field.isEmpty() && !field.startsWith("get") && !field.startsWith("is")) {
            methodName = "get" + Character.toUpperCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) : "");
        }
        String finalMethodName = methodName != null ? methodName : "name";

        validator.initialize(target, finalMethodName);
        if (!validator.isValid(value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.enum", locale));
        }
    }
    
    // 添加缺失的验证方法
    
    public void validateAlpha(Object value, List<String> errors, Locale locale) {
        AlphaValidator validator = new AlphaValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.alpha", locale));
        }
    }
    
    public void validateAlphaNum(Object value, List<String> errors, Locale locale) {
        AlphaNumberValidator validator = new AlphaNumberValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.alpha.number", locale));
        }
    }
    
    public void validateAlphaDash(Object value, List<String> errors, Locale locale) {
        AlphaDashValidator validator = new AlphaDashValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.alpha.dash", locale));
        }
    }
    
    public void validateChinese(Object value, List<String> errors, Locale locale) {
        ChineseValidator validator = new ChineseValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese", locale));
        }
    }
    
    public void validateColor(Object value, List<String> errors, Locale locale) {
        ColorValidator validator = new ColorValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.color", locale));
        }
    }
    
    public void validateEndsWith(Object value, String suffix, List<String> errors, Locale locale) {
        EndsWithValidator validator = new EndsWithValidator();
        validator.initialize(suffix != null ? suffix : "");
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.ends.with", locale));
        }
    }

    public void validateStartsWith(Object value, String prefix, List<String> errors, Locale locale) {
        StartsWithValidator validator = new StartsWithValidator();
        validator.initialize(prefix != null ? prefix : "");
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.starts.with", locale));
        }
    }

    public void validateContains(Object value, String[] substrings, boolean ignoreCase, boolean matchAll, List<String> errors, Locale locale) {
        io.github.vipxieliang.validx.validator.base.ContainsValidator validator = new io.github.vipxieliang.validx.validator.base.ContainsValidator();
        validator.initialize(substrings, ignoreCase, matchAll);
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.contains", locale));
        }
    }

    public void validateNotContains(Object value, String[] substrings, boolean ignoreCase, boolean matchAll, List<String> errors, Locale locale) {
        io.github.vipxieliang.validx.validator.base.NotContainsValidator validator = new io.github.vipxieliang.validx.validator.base.NotContainsValidator();
        validator.initialize(substrings, ignoreCase, matchAll);
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.not.contains", locale));
        }
    }
    
    public void validateLongitude(Object value, List<String> errors, Locale locale) {
        LongitudeValidator validator = new LongitudeValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.longitude", locale));
        }
    }
    
    public void validateLatitude(Object value, List<String> errors, Locale locale) {
        LatitudeValidator validator = new LatitudeValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.latitude", locale));
        }
    }

    public void validateGeoPoint(Object value, List<String> errors, Locale locale) {
        validateGeoPoint(value, false, io.github.vipxieliang.validx.annotations.GeoPoint.SeparatorType.ANY, errors, locale);
    }

    public void validateGeoPoint(Object value, boolean latitudeFirst, List<String> errors, Locale locale) {
        validateGeoPoint(value, latitudeFirst, io.github.vipxieliang.validx.annotations.GeoPoint.SeparatorType.ANY, errors, locale);
    }

    public void validateGeoPoint(Object value, boolean latitudeFirst,
                                  io.github.vipxieliang.validx.annotations.GeoPoint.SeparatorType separatorType,
                                  List<String> errors, Locale locale) {
        if (!io.github.vipxieliang.validx.validator.base.GeoPointValidator.isValid(value, latitudeFirst, separatorType)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.geopoint", locale));
        }
    }

    public void validatePassword(Object value, int minLength, boolean requireUppercase,
                                boolean requireLowercase, boolean requireDigit, boolean requireSpecialChar,
                                List<String> errors, Locale locale) {
        PasswordValidator validator = new PasswordValidator();
        validator.initialize(minLength, requireUppercase, requireLowercase, requireDigit, requireSpecialChar);
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.password", locale));
        }
    }

    /**
     * 验证文件大小
     * @param value 文件对象（File、Path、byte[]等）
     * @param max 最大大小（如 "10MB"、"5GB"）
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateFileSize(Object value, String max, List<String> errors, Locale locale) {
        validateFileSize(value, "0B", max, errors, locale);
    }

    /**
     * 验证文件大小
     * @param value 文件对象（File、Path、byte[]等）
     * @param min 最小大小（如 "1KB"、"10MB"）
     * @param max 最大大小（如 "10MB"、"5GB"）
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateFileSize(Object value, String min, String max, List<String> errors, Locale locale) {
        validateFileSize(value, min, max, new String[0], errors, locale);
    }

    /**
     * 验证文件大小（支持MIME类型限制）
     * @param value 文件对象（File、Path、byte[]、MultipartFile等）
     * @param min 最小大小（如 "1KB"、"10MB"）
     * @param max 最大大小（如 "10MB"、"5GB"）
     * @param allowedTypes 允许的MIME类型（仅对MultipartFile有效），如 {"image/jpeg", "image/png"}
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateFileSize(Object value, String min, String max, String[] allowedTypes,
                                 List<String> errors, Locale locale) {
        // 根据value类型选择对应的验证器
        if (value instanceof java.io.File) {
            validateFileSizeForFile((java.io.File) value, min, max, errors, locale);
        } else if (value instanceof java.nio.file.Path) {
            validateFileSizeForPath((java.nio.file.Path) value, min, max, errors, locale);
        } else if (value instanceof byte[]) {
            validateFileSizeForByteArray((byte[]) value, min, max, errors, locale);
        } else if (value != null) {
            // 尝试MultipartFile（通过反射）
            validateFileSizeForMultipartFile(value, min, max, allowedTypes, errors, locale);
        }
    }

    private void validateFileSizeForFile(java.io.File file, String min, String max,
                                         List<String> errors, Locale locale) {
        FileSizeValidator validator = new FileSizeValidator();
        validator.initialize(min, max);
        if (!validator.isValid(file, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.file.size", locale));
        }
    }

    private void validateFileSizeForPath(java.nio.file.Path path, String min, String max,
                                         List<String> errors, Locale locale) {
        FileSizePathValidator validator = new FileSizePathValidator();
        validator.initialize(min, max);
        if (!validator.isValid(path, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.file.size", locale));
        }
    }

    private void validateFileSizeForByteArray(byte[] data, String min, String max,
                                               List<String> errors, Locale locale) {
        FileSizeByteArrayValidator validator = new FileSizeByteArrayValidator();
        validator.initialize(min, max);
        if (!validator.isValid(data, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.file.size", locale));
        }
    }

    private void validateFileSizeForMultipartFile(Object file, String min, String max,
                                                   String[] allowedTypes, List<String> errors, Locale locale) {
        FileSizeMultipartFileValidator validator = new FileSizeMultipartFileValidator();
        validator.initialize(min, max, allowedTypes);
        if (!validator.isValid(file, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.file.size", locale));
        }
    }

    /**
     * 验证UUID格式
     * @param value 待验证的值
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateUUID(Object value, List<String> errors, Locale locale) {
        validateUUID(value, false, errors, locale);
    }

    /**
     * 验证UUID格式
     * @param value 待验证的值
     * @param allowWithoutHyphens 是否允许不带连字符的格式
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateUUID(Object value, boolean allowWithoutHyphens, List<String> errors, Locale locale) {
        // null值应该通过验证（由@NotNull处理）
        if (value == null) {
            return;
        }

        UUIDValidator validator = new UUIDValidator();
        validator.initialize(allowWithoutHyphens);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.uuid", locale));
        }
    }

    /**
     * 验证Base64格式（标准格式）
     * @param value 待验证的值
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateBase64(Object value, List<String> errors, Locale locale) {
        validateBase64(value, false, false, errors, locale);
    }

    /**
     * 验证Base64格式
     * @param value 待验证的值
     * @param urlSafe 是否为URL-safe格式
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateBase64(Object value, boolean urlSafe, List<String> errors, Locale locale) {
        validateBase64(value, urlSafe, false, errors, locale);
    }

    /**
     * 验证Base64格式
     * @param value 待验证的值
     * @param urlSafe 是否为URL-safe格式
     * @param allowNoPadding 是否允许不带填充符
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateBase64(Object value, boolean urlSafe, boolean allowNoPadding,
                               List<String> errors, Locale locale) {
        // null值应该通过验证（由@NotNull处理）
        if (value == null) {
            return;
        }

        Base64Validator validator = new Base64Validator();
        validator.initialize(urlSafe, allowNoPadding);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.base64", locale));
        }
    }

    /**
     * 验证年龄（只验证最小年龄）
     * @param value 待验证的值（LocalDate、Date或String）
     * @param minAge 最小年龄
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateAge(Object value, int minAge, List<String> errors, Locale locale) {
        validateAge(value, minAge, 0, false, "yyyy-MM-dd", errors, locale);
    }

    /**
     * 验证年龄范围
     * @param value 待验证的值（LocalDate、Date或String）
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateAge(Object value, int minAge, int maxAge, List<String> errors, Locale locale) {
        validateAge(value, minAge, maxAge, false, "yyyy-MM-dd", errors, locale);
    }

    /**
     * 验证年龄（从身份证提取）
     * @param value 身份证号码
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @param fromIdCard 是否从身份证提取
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateAge(Object value, int minAge, int maxAge, boolean fromIdCard,
                           List<String> errors, Locale locale) {
        validateAge(value, minAge, maxAge, fromIdCard, "yyyy-MM-dd", errors, locale);
    }

    /**
     * 验证年龄（完整参数）
     * @param value 待验证的值
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @param fromIdCard 是否从身份证提取
     * @param dateFormat 日期格式
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateAge(Object value, int minAge, int maxAge, boolean fromIdCard,
                           String dateFormat, List<String> errors, Locale locale) {
        AgeValidator validator = new AgeValidator();
        validator.initialize(minAge, maxAge, fromIdCard, dateFormat);

        if (!validator.isValid(value, null)) {
            String message = MessageManager.getMessage("io.github.vipxieliang.validx.annotation.age", locale);
            // 替换占位符
            message = message.replace("{min}", String.valueOf(minAge))
                           .replace("{max}", String.valueOf(maxAge));
            errors.add(message);
        }
    }

    /**
     * 验证JSON格式（任意类型）
     * @param value 待验证的JSON字符串
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateJSON(Object value, List<String> errors, Locale locale) {
        validateJSON(value, io.github.vipxieliang.validx.annotations.JSON.JSONType.ANY,
                    true, 0, 0, errors, locale);
    }

    /**
     * 验证JSON格式（指定类型）
     * @param value 待验证的JSON字符串
     * @param type JSON类型
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateJSON(Object value, io.github.vipxieliang.validx.annotations.JSON.JSONType type,
                            List<String> errors, Locale locale) {
        validateJSON(value, type, true, 0, 0, errors, locale);
    }

    /**
     * 验证JSON格式（指定类型和严格模式）
     * @param value 待验证的JSON字符串
     * @param type JSON类型
     * @param strict 是否严格模式
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateJSON(Object value, io.github.vipxieliang.validx.annotations.JSON.JSONType type,
                            boolean strict, List<String> errors, Locale locale) {
        validateJSON(value, type, strict, 0, 0, errors, locale);
    }

    /**
     * 验证JSON格式（完整参数）
     * @param value 待验证的JSON字符串
     * @param type JSON类型
     * @param strict 是否严格模式
     * @param maxDepth 最大深度
     * @param maxLength 最大长度
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateJSON(Object value, io.github.vipxieliang.validx.annotations.JSON.JSONType type,
                            boolean strict, int maxDepth, int maxLength,
                            List<String> errors, Locale locale) {
        // null值应该通过验证（由@NotNull处理）
        if (value == null) {
            return;
        }

        JSONValidator validator = new JSONValidator();
        validator.initialize(type, strict, maxDepth, maxLength);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.json", locale));
        }
    }

    /**
     * 验证国际电话号码（简单版本）
     *
     * @param value  要验证的电话号码
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validatePhoneNumber(Object value, List<String> errors, Locale locale) {
        validatePhoneNumber(value, "", true, false, errors, locale);
    }

    /**
     * 验证国际电话号码（指定国家代码）
     *
     * @param value       要验证的电话号码
     * @param countryCode 国家代码（如 "+86", "+1"）
     * @param errors      错误列表
     * @param locale      语言环境
     */
    public void validatePhoneNumber(Object value, String countryCode, List<String> errors, Locale locale) {
        validatePhoneNumber(value, countryCode, true, false, errors, locale);
    }

    /**
     * 验证国际电话号码（指定国家代码和是否允许分机号）
     *
     * @param value          要验证的电话号码
     * @param countryCode    国家代码（如 "+86", "+1"）
     * @param allowExtension 是否允许分机号
     * @param errors         错误列表
     * @param locale         语言环境
     */
    public void validatePhoneNumber(Object value, String countryCode, boolean allowExtension,
                                   List<String> errors, Locale locale) {
        validatePhoneNumber(value, countryCode, allowExtension, false, errors, locale);
    }

    /**
     * 验证国际电话号码（完整版本）
     *
     * @param value          要验证的电话号码
     * @param countryCode    国家代码（如 "+86", "+1"）
     * @param allowExtension 是否允许分机号
     * @param strict         是否严格模式（必须包含国家代码）
     * @param errors         错误列表
     * @param locale         语言环境
     */
    public void validatePhoneNumber(Object value, String countryCode, boolean allowExtension,
                                   boolean strict, List<String> errors, Locale locale) {
        PhoneNumberValidator validator = new PhoneNumberValidator();
        io.github.vipxieliang.validx.annotations.PhoneNumber annotation =
            createPhoneNumberAnnotation(countryCode, allowExtension, strict, locale);
        validator.initialize(annotation);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.phonenumber", locale));
        }
    }

    private io.github.vipxieliang.validx.annotations.PhoneNumber createPhoneNumberAnnotation(
            String countryCode, boolean allowExtension, boolean strict, Locale locale) {
        return new io.github.vipxieliang.validx.annotations.PhoneNumber() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return io.github.vipxieliang.validx.annotations.PhoneNumber.class;
            }

            @Override
            public String countryCode() {
                return countryCode;
            }

            @Override
            public boolean allowExtension() {
                return allowExtension;
            }

            @Override
            public boolean strict() {
                return strict;
            }

            @Override
            public String message() {
                return MessageManager.getMessage("io.github.vipxieliang.validx.annotation.phonenumber", locale);
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };
    }

    /**
     * 验证JWT Token格式
     * @param value 待验证的JWT Token
     * @param errors 错误列表
     * @param locale 语言环境
     */
    public void validateJWT(Object value, List<String> errors, Locale locale) {
        JWTValidator validator = new JWTValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.jwt", locale));
        }
    }

    /**
     * 验证语义化版本号
     *
     * @param value 待验证的值
     * @param allowVPrefix 是否允许v前缀
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateSemVer(Object value, boolean allowVPrefix, List<String> errors, Locale locale) {
        if (!SemVerValidator.isValidSemVer((String) value, allowVPrefix)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.semver", locale));
        }
    }

    /**
     * 验证Unix时间戳格式（秒或毫秒均可）
     *
     * @param value 待验证的值（String或Long/Number）
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateTimestamp(Object value, List<String> errors, Locale locale) {
        validateTimestamp(value, Timestamp.TimestampUnit.ANY, errors, locale);
    }

    /**
     * 验证Unix时间戳格式（指定单位）
     *
     * @param value 待验证的值（String或Long/Number）
     * @param unit 时间戳单位
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateTimestamp(Object value, Timestamp.TimestampUnit unit, List<String> errors, Locale locale) {
        if (value == null) {
            return; // null值由@NotNull处理
        }

        TimestampValidator validator = new TimestampValidator();

        // 创建一个模拟的Timestamp注解实例
        Timestamp timestampAnnotation = new Timestamp() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Timestamp.class;
            }

            @Override
            public Timestamp.TimestampUnit unit() {
                return unit;
            }

            @Override
            public String message() {
                return MessageManager.getMessage("io.github.vipxieliang.validx.annotation.timestamp", locale);
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };

        validator.initialize(timestampAnnotation);
        if (!validator.isValid(value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.timestamp", locale));
        }
    }

    /**
     * 验证Cron表达式格式
     *
     * @param value 待验证的值（String）
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateCronExpression(Object value, List<String> errors, Locale locale) {
        if (!CronExpressionValidator.isValid(value)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.cron.expression", locale));
        }
    }

    /**
     * 验证时间段格式
     *
     * @param value 待验证的值（String）
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateDuration(Object value, List<String> errors, Locale locale) {
        if (!DurationValidator.isValid(value)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.duration", locale));
        }
    }

    /**
     * 验证时间段格式（指定格式）
     *
     * @param value 待验证的值（String）
     * @param format 时间段格式类型
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateDuration(Object value, io.github.vipxieliang.validx.annotations.Duration.DurationFormat format, List<String> errors, Locale locale) {
        if (!DurationValidator.isValid(value, format)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.duration", locale));
        }
    }

    /**
     * 验证快递单号格式
     *
     * @param value 待验证的值（String）
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateExpressNumber(Object value, List<String> errors, Locale locale) {
        if (!ExpressNumberValidator.isValid(value)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.express.number", locale));
        }
    }

    /**
     * 验证快递单号格式（指定快递公司）
     *
     * @param value 待验证的值（String）
     * @param companies 快递公司类型
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateExpressNumber(Object value, io.github.vipxieliang.validx.annotations.ExpressNumber.ExpressCompany[] companies, List<String> errors, Locale locale) {
        if (!ExpressNumberValidator.isValid(value, companies)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.express.number", locale));
        }
    }

    /**
     * 验证日期格式
     *
     * @param value 待验证的值
     * @param pattern 日期格式模式
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateDateFormat(Object value, String pattern, List<String> errors, Locale locale) {
        if (!DateValidator.isValidDateFormat((String) value, pattern)) {
            errors.add(MessageManager.getMessage(
                "io.github.vipxieliang.validx.annotation.date.format",
                locale
            ));
        }
    }

    /**
     * 验证日期时间格式
     *
     * @param value 待验证的值
     * @param pattern 日期时间格式模式
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateDateTimeFormat(Object value, String pattern, List<String> errors, Locale locale) {
        if (!DateTimeValidator.isValidDateTimeFormat((String) value, pattern)) {
            errors.add(MessageManager.getMessage(
                "io.github.vipxieliang.validx.annotation.datetime.format",
                locale
            ));
        }
    }

    /**
     * 验证过去的日期时间
     *
     * @param value 待验证的值
     * @param includeToday 是否包含今天
     * @param pattern 日期时间格式
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validatePastDateTime(Object value, boolean includeToday, String pattern, List<String> errors, Locale locale) {
        PastDateTimeValidator validator = new PastDateTimeValidator();
        validator.initialize(includeToday, pattern);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.past.datetime", locale));
        }
    }

    /**
     * 验证未来的日期时间
     *
     * @param value 待验证的值
     * @param includeToday 是否包含今天
     * @param pattern 日期时间格式
     * @param errors 错误消息列表
     * @param locale 语言环境
     */
    public void validateFutureDateTime(Object value, boolean includeToday, String pattern, List<String> errors, Locale locale) {
        FutureDateTimeValidator validator = new FutureDateTimeValidator();
        validator.initialize(includeToday, pattern);

        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.future.datetime", locale));
        }
    }
}