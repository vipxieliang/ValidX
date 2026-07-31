/*
 * Copyright 2025-2025 vipxieliang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vipxieliang.validx.chain;

import io.github.vipxieliang.validx.annotations.Ip;
import io.github.vipxieliang.validx.chain.china.ChinaValidation;
import io.github.vipxieliang.validx.chain.phone.PhoneValidation;
import io.github.vipxieliang.validx.i18n.MessageManager;
import io.github.vipxieliang.validx.chain.network.NetworkValidation;
import io.github.vipxieliang.validx.chain.base.BaseValidation;
import io.github.vipxieliang.validx.chain.financial.FinancialValidation;
import io.github.vipxieliang.validx.chain.vehicle.VehicleValidation;
import io.github.vipxieliang.validx.chain.book.BookValidation;
import io.github.vipxieliang.validx.chain.education.EducationValidation;
import io.github.vipxieliang.validx.chain.foreign.ForeignValidation;
import io.github.vipxieliang.validx.chain.certification.CertificationValidation;
import io.github.vipxieliang.validx.annotations.StockCode;
import io.github.vipxieliang.validx.annotations.FinancialProductCode;
import io.github.vipxieliang.validx.annotations.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 链式验证器
 * 提供流畅的API用于连续验证多个条件
 * 使用示例:
 * ValidaX validator = ValidaX.init();
 * validator.isEmail("test@example.com")
 *          .isChineseIdCard("11010119900307211X")
 *          .isISBN("9780306406157");
 * if (!validator.passed()) {
 *     List&lt;String&gt; errors = validator.getErrors();
 *     // 处理错误信息
 * }
 */
public class ValidaX {

    private final List<String> errors = new ArrayList<>();
    private Locale locale = null; // 修改为可以为null，表示使用当前线程或系统默认语言环境

    // 新增：配置和局部状态
    private ValidXConfig config = ValidXConfig.DEFAULT;
    private LocalRequirement localRequirement = LocalRequirement.UNSET;
    private String currentFieldLabel = null;  // 新增：当前字段标识（可选）

    // 专门的验证器实例
    private final ChinaValidation chinaValidation = new ChinaValidation();
    private final NetworkValidation networkValidation = new NetworkValidation();
    private final BaseValidation baseValidation = new BaseValidation();
    private final FinancialValidation financialValidation = new FinancialValidation();
    private final VehicleValidation vehicleValidation = new VehicleValidation();
    private final BookValidation bookValidation = new BookValidation();
    private final EducationValidation educationValidation = new EducationValidation();
    private final ForeignValidation foreignValidation = new ForeignValidation();
    private final PhoneValidation phoneValidation = new PhoneValidation();
    private final CertificationValidation certificationValidation = new CertificationValidation();

    private ValidaX() {
    }

    public static ValidaX init() {
        return new ValidaX();
    }

    /**
     * 设置全局配置
     * @param config 验证配置
     * @return ValidaX实例
     */
    public ValidaX config(ValidXConfig config) {
        this.config = config;
        return this;
    }

    /**
     * 设置字段标识（可选）
     * @param fieldLabel 字段标识
     * @return ValidaX实例
     */
    public ValidaX field(String fieldLabel) {
        this.currentFieldLabel = fieldLabel;
        return this;
    }

    /**
     * 要求非null（局部状态）
     * @return ValidaX实例
     */
    public ValidaX notNull() {
        this.localRequirement = LocalRequirement.NOT_NULL;
        return this;
    }

    /**
     * 要求非null且非空字符串（局部状态）
     * @return ValidaX实例
     */
    public ValidaX notEmpty() {
        this.localRequirement = LocalRequirement.NOT_EMPTY;
        return this;
    }

    /**
     * 允许null和空字符串（局部覆盖）
     * @return ValidaX实例
     */
    public ValidaX allowNull() {
        this.localRequirement = LocalRequirement.ALLOW_NULL;
        return this;
    }

    /**
     * 允许空字符串但不允许null（局部覆盖）
     * @return ValidaX实例
     */
    public ValidaX allowEmpty() {
        this.localRequirement = LocalRequirement.ALLOW_EMPTY;
        return this;
    }

    /**
     * 设置语言环境
     * @param locale 语言环境
     * @return ValidaX实例
     */
    public ValidaX withLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * 获取当前使用的语言环境
     * @return 语言环境
     */
    private Locale getLocale() {
        if (locale != null) {
            return locale;
        }
        return MessageManager.getCurrentLocale();
    }

    // China Validation Methods
    public ValidaX isChineseIdCard(Object value) {
        if (checkRequirement(value, "Chinese ID Card", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateIdCard(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseLicensePlate(Object value) {
        if (checkRequirement(value, "License Plate", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateLicensePlate(value, errors, getLocale());
        return this;
    }

    public ValidaX isQQ(Object value) {
        if (checkRequirement(value, "QQ", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateQQ(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseMilitaryOfficer(Object value) {
        if (checkRequirement(value, "Chinese Military Officer", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateMilitaryOfficer(value, errors, getLocale());
        return this;
    }

    public ValidaX isChinesePassport(Object value) {
        if (checkRequirement(value, "Chinese Passport", errors, getLocale())) {
            return this;
        }
        chinaValidation.validatePassport(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseSoldier(Object value) {
        if (checkRequirement(value, "Chinese Soldier", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateSoldier(value, errors, getLocale());
        return this;
    }

    public ValidaX isForeignerPermanentResidenceIdentity(Object value) {
        if (checkRequirement(value, "Foreigner Permanent Residence Identity", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateForeignerPermanentResidenceIdentity(value, errors, getLocale());
        return this;
    }

    public ValidaX isHKMacauPass(Object value) {
        if (checkRequirement(value, "HK Macau Pass", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateHKMacauPass(value, errors, getLocale());
        return this;
    }

    public ValidaX isHKMacauResidence(Object value) {
        if (checkRequirement(value, "HK Macau Residence", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateHKMacauResidence(value, errors, getLocale());
        return this;
    }

    public ValidaX isTaiwanPass(Object value) {
        if (checkRequirement(value, "Taiwan Pass", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateTaiwanPass(value, errors, getLocale());
        return this;
    }

    public ValidaX isTaiwanResidence(Object value) {
        if (checkRequirement(value, "Taiwan Residence", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateTaiwanResidence(value, errors, getLocale());
        return this;
    }

    public ValidaX isUnifiedSocialCreditCode(Object value) {
        if (checkRequirement(value, "Unified Social Credit Code", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateUnifiedSocialCreditCode(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseZipCode(Object value) {
        if (checkRequirement(value, "Chinese Zip Code", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateZipCode(value, errors, getLocale());
        return this;
    }

    public ValidaX isChinesePatent(Object value) {
        if (checkRequirement(value, "Chinese Patent", errors, getLocale())) {
            return this;
        }
        chinaValidation.validatePatent(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseTrademark(Object value) {
        if (checkRequirement(value, "Chinese Trademark", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateTrademark(value, errors, getLocale());
        return this;
    }

    public ValidaX isSoftwareCopyright(Object value) {
        if (checkRequirement(value, "Software Copyright", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateSoftwareCopyright(value, errors, getLocale());
        return this;
    }

    public ValidaX isWorkCopyright(Object value) {
        if (checkRequirement(value, "Work Copyright", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateWorkCopyright(value, errors, getLocale());
        return this;
    }

    public ValidaX isLawyer(Object value) {
        if (checkRequirement(value, "Lawyer", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateLawyer(value, errors, getLocale());
        return this;
    }

    public ValidaX isChinesePhone(Object value) {
        if (checkRequirement(value, "Chinese Phone", errors, getLocale())) {
            return this;
        }
        chinaValidation.validatePhone(value, errors, getLocale());
        return this;
    }

    public ValidaX isChinesePhoneOrLandline(Object value) {
        if (checkRequirement(value, "Chinese Phone Or Landline", errors, getLocale())) {
            return this;
        }
        chinaValidation.validatePhoneOrLandline(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseLandline(Object value) {
        if (checkRequirement(value, "Chinese Landline", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateLandline(value, errors, getLocale());
        return this;
    }

    public ValidaX isDrugApproval(Object value) {
        if (checkRequirement(value, "Drug Approval", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateDrugApproval(value, errors, getLocale());
        return this;
    }

    public ValidaX isDrugCode(Object value) {
        if (checkRequirement(value, "Drug Code", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateDrugCode(value, errors, getLocale());
        return this;
    }

    public ValidaX isMedicalDeviceRegistration(Object value) {
        if (checkRequirement(value, "Medical Device Registration", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateMedicalDeviceRegistration(value, errors, getLocale());
        return this;
    }

    // Network Validation Methods
    public ValidaX isMacAddress(Object value) {
        if (checkRequirement(value, "MAC Address", errors, getLocale())) {
            return this;
        }
        networkValidation.validateMacAddress(value, errors, getLocale());
        return this;
    }

    public ValidaX isEmail(Object value) {
        if (checkRequirement(value, "Email", errors, getLocale())) {
            return this;
        }
        networkValidation.validateEmail(value, errors, getLocale());
        return this;
    }

    public ValidaX isIp(Object value) {
        if (checkRequirement(value, "Ip", errors, getLocale())) {
            return this;
        }
        networkValidation.validateIp(value, errors, getLocale());
        return this;
    }

    /**
     * 验证IP地址（指定版本）
     * @param value 待验证的IP地址
     * @param version IP版本（Ip.IpVersion.V4/Ip.IpVersion.V6/Ip.IpVersion.ANY）
     * @return ValidaX实例
     */
    public ValidaX isIp(Object value, Ip.IpVersion version) {
        if (checkRequirement(value, "Ip", errors, getLocale())) {
            return this;
        }
        networkValidation.validateIp(value, version, errors, getLocale());
        return this;
    }

    public ValidaX isSubnetMask(Object value) {
        if (checkRequirement(value, "Subnet Mask", errors, getLocale())) {
            return this;
        }
        networkValidation.validateSubnetMask(value, errors, getLocale());
        return this;
    }

    public ValidaX isUrl(Object value) {
        if (checkRequirement(value, "URL", errors, getLocale())) {
            return this;
        }
        networkValidation.validateUrl(value, errors, getLocale());
        return this;
    }

    public ValidaX isDomain(Object value) {
        if (checkRequirement(value, "Domain", errors, getLocale())) {
            return this;
        }
        networkValidation.validateDomain(value, errors, getLocale());
        return this;
    }

    public ValidaX isWeChat(Object value) {
        if (checkRequirement(value, "WeChat", errors, getLocale())) {
            return this;
        }
        chinaValidation.validateWeChat(value, errors, getLocale());
        return this;
    }

    public ValidaX isPort(Object value) {
        if (checkRequirement(value, "Port", errors, getLocale())) {
            return this;
        }
        networkValidation.validatePort(value, errors, getLocale());
        return this;
    }

    // Base Validation Methods
    public ValidaX isChineseAlpha(Object value) {
        if (checkRequirement(value, "Chinese Alpha", errors, getLocale())) {
            return this;
        }
        baseValidation.validateChineseAlpha(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseAlphaNum(Object value) {
        if (checkRequirement(value, "Chinese Alpha Num", errors, getLocale())) {
            return this;
        }
        baseValidation.validateChineseAlphaNum(value, errors, getLocale());
        return this;
    }

    public ValidaX isChineseAlphaDash(Object value) {
        if (checkRequirement(value, "Chinese Alpha Dash", errors, getLocale())) {
            return this;
        }
        baseValidation.validateChineseAlphaDash(value, errors, getLocale());
        return this;
    }

    public ValidaX isLower(Object value) {
        if (checkRequirement(value, "Lower", errors, getLocale())) {
            return this;
        }
        baseValidation.validateLower(value, errors, getLocale());
        return this;
    }

    public ValidaX isUpper(Object value) {
        if (checkRequirement(value, "Upper", errors, getLocale())) {
            return this;
        }
        baseValidation.validateUpper(value, errors, getLocale());
        return this;
    }

    public ValidaX isXdigit(Object value) {
        if (checkRequirement(value, "Xdigit", errors, getLocale())) {
            return this;
        }
        baseValidation.validateXdigit(value, errors, getLocale());
        return this;
    }

    public ValidaX isIn(Object value, String[] values) {
        if (checkRequirement(value, "In", errors, getLocale())) {
            return this;
        }
        baseValidation.validateIn(value, values, errors, getLocale());
        return this;
    }

    public ValidaX isNotIn(Object value, String[] values) {
        if (checkRequirement(value, "Not In", errors, getLocale())) {
            return this;
        }
        baseValidation.validateNotIn(value, values, errors, getLocale());
        return this;
    }

    public ValidaX isPastDate(Object value, boolean includeToday) {
        if (checkRequirement(value, "Past Date", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePastDate(value, includeToday, errors, getLocale());
        return this;
    }

    public ValidaX isFutureDate(Object value, boolean includeToday) {
        if (checkRequirement(value, "Future Date", errors, getLocale())) {
            return this;
        }
        baseValidation.validateFutureDate(value, includeToday, errors, getLocale());
        return this;
    }

    public ValidaX isFileExtension(Object value, String[] extensions) {
        if (checkRequirement(value, "File Extension", errors, getLocale())) {
            return this;
        }
        baseValidation.validateFileExtension(value, extensions, errors, getLocale());
        return this;
    }
    
    public ValidaX isFileExtension(Object value, String[] extensions, boolean ignoreCase) {
        if (checkRequirement(value, "File Extension", errors, getLocale())) {
            return this;
        }
        baseValidation.validateFileExtension(value, extensions, ignoreCase, errors, getLocale());
        return this;
    }

    public ValidaX isHourMinute(Object value) {
        if (checkRequirement(value, "Hour Minute", errors, getLocale())) {
            return this;
        }
        baseValidation.validateHourMinute(value, errors, getLocale());
        return this;
    }

    public ValidaX isHourMinuteSecond(Object value) {
        if (checkRequirement(value, "Hour Minute Second", errors, getLocale())) {
            return this;
        }
        baseValidation.validateHourMinuteSecond(value, errors, getLocale());
        return this;
    }

    public ValidaX isEnum(Object value, Class<? extends java.lang.Enum<?>> target, String field) {
        if (checkRequirement(value, "Enum", errors, getLocale())) {
            return this;
        }
        baseValidation.validateEnum(value, target, field, errors, getLocale());
        return this;
    }

    public ValidaX isEnum(Object value, Class<? extends java.lang.Enum<?>> target) {
        if (checkRequirement(value, "Enum", errors, getLocale())) {
            return this;
        }
        return isEnum(value, target, null);
    }
    
    public ValidaX isAlpha(Object value) {
        if (checkRequirement(value, "Alpha", errors, getLocale())) {
            return this;
        }
        baseValidation.validateAlpha(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isAlphaNum(Object value) {
        if (checkRequirement(value, "Alpha Num", errors, getLocale())) {
            return this;
        }
        baseValidation.validateAlphaNum(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isAlphaDash(Object value) {
        if (checkRequirement(value, "Alpha Dash", errors, getLocale())) {
            return this;
        }
        baseValidation.validateAlphaDash(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isChinese(Object value) {
        if (checkRequirement(value, "Chinese", errors, getLocale())) {
            return this;
        }
        baseValidation.validateChinese(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isLongitude(Object value) {
        if (checkRequirement(value, "Longitude", errors, getLocale())) {
            return this;
        }
        baseValidation.validateLongitude(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isLatitude(Object value) {
        if (checkRequirement(value, "Latitude", errors, getLocale())) {
            return this;
        }
        baseValidation.validateLatitude(value, errors, getLocale());
        return this;
    }

    public ValidaX isGeoPoint(Object value) {
        if (checkRequirement(value, "GeoPoint", errors, getLocale())) {
            return this;
        }
        baseValidation.validateGeoPoint(value, errors, getLocale());
        return this;
    }

    public ValidaX isGeoPoint(Object value, boolean latitudeFirst) {
        if (checkRequirement(value, "GeoPoint", errors, getLocale())) {
            return this;
        }
        baseValidation.validateGeoPoint(value, latitudeFirst, errors, getLocale());
        return this;
    }

    public ValidaX isGeoPoint(Object value, boolean latitudeFirst, io.github.vipxieliang.validx.annotations.GeoPoint.SeparatorType separatorType) {
        if (checkRequirement(value, "GeoPoint", errors, getLocale())) {
            return this;
        }
        baseValidation.validateGeoPoint(value, latitudeFirst, separatorType, errors, getLocale());
        return this;
    }

    public ValidaX isColor(Object value) {
        if (checkRequirement(value, "Color", errors, getLocale())) {
            return this;
        }
        baseValidation.validateColor(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isEndsWith(Object value, String[] suffixes) {
        if (checkRequirement(value, "Ends With", errors, getLocale())) {
            return this;
        }
        baseValidation.validateEndsWith(value, suffixes, errors, getLocale());
        return this;
    }
    
    public ValidaX isStartsWith(Object value, String[] prefixes) {
        if (checkRequirement(value, "Starts With", errors, getLocale())) {
            return this;
        }
        baseValidation.validateStartsWith(value, prefixes, errors, getLocale());
        return this;
    }

    // Base Validation Methods - Contains
    public ValidaX isContains(Object value, String[] substrings) {
        if (checkRequirement(value, "Contains", errors, getLocale())) {
            return this;
        }
        baseValidation.validateContains(value, substrings, errors, getLocale());
        return this;
    }

    public ValidaX isContains(Object value, String[] substrings, boolean ignoreCase) {
        if (checkRequirement(value, "Contains", errors, getLocale())) {
            return this;
        }
        baseValidation.validateContains(value, substrings, ignoreCase, errors, getLocale());
        return this;
    }

    public ValidaX isContains(Object value, String[] substrings, boolean ignoreCase, boolean matchAll) {
        if (checkRequirement(value, "Contains", errors, getLocale())) {
            return this;
        }
        baseValidation.validateContains(value, substrings, ignoreCase, matchAll, errors, getLocale());
        return this;
    }

    // Base Validation Methods - Password
    public ValidaX isPassword(Object value) {
        if (checkRequirement(value, "Password", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePassword(value, 8, true, true, true, true, errors, getLocale());
        return this;
    }
    
    public ValidaX isPassword(Object value, int minLength) {
        if (checkRequirement(value, "Password", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePassword(value, minLength, true, true, true, true, errors, getLocale());
        return this;
    }
    
    public ValidaX isPassword(Object value, int minLength, boolean requireUppercase,
                              boolean requireLowercase, boolean requireDigit, boolean requireSpecialChar) {
        if (checkRequirement(value, "Password", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePassword(value, minLength, requireUppercase, requireLowercase, requireDigit, requireSpecialChar, errors, getLocale());
        return this;
    }

    // Base Validation Methods - FileSize
    /**
     * 验证文件大小（只指定最大值）
     * @param value 文件对象（File、Path、byte[]、MultipartFile）
     * @param max 最大大小（如 "10MB"、"5GB"）
     * @return ValidaX实例
     */
    public ValidaX isFileSize(Object value, String max) {
        if (checkRequirement(value, "File Size", errors, getLocale())) {
            return this;
        }
        baseValidation.validateFileSize(value, max, errors, getLocale());
        return this;
    }

    /**
     * 验证文件大小（指定最小和最大值）
     * @param value 文件对象（File、Path、byte[]、MultipartFile）
     * @param min 最小大小（如 "1KB"、"100KB"）
     * @param max 最大大小（如 "10MB"、"5GB"）
     * @return ValidaX实例
     */
    public ValidaX isFileSize(Object value, String min, String max) {
        if (checkRequirement(value, "File Size", errors, getLocale())) {
            return this;
        }
        baseValidation.validateFileSize(value, min, max, errors, getLocale());
        return this;
    }

    /**
     * 验证 UUID 格式（只允许标准格式）
     * @param value UUID 字符串
     * @return ValidaX实例
     */
    public ValidaX isUUID(Object value) {
        if (checkRequirement(value, "UUID", errors, getLocale())) {
            return this;
        }
        baseValidation.validateUUID(value, errors, getLocale());
        return this;
    }

    /**
     * 验证 UUID 格式
     * @param value UUID 字符串
     * @param allowWithoutHyphens 是否允许不带连字符的格式
     * @return ValidaX实例
     */
    public ValidaX isUUID(Object value, boolean allowWithoutHyphens) {
        if (checkRequirement(value, "U U I D", errors, getLocale())) {
            return this;
        }
        baseValidation.validateUUID(value, allowWithoutHyphens, errors, getLocale());
        return this;
    }

    /**
     * 验证 Base64 格式（标准格式）
     * @param value Base64 字符串
     * @return ValidaX实例
     */
    public ValidaX isBase64(Object value) {
        if (checkRequirement(value, "Base64", errors, getLocale())) {
            return this;
        }
        baseValidation.validateBase64(value, errors, getLocale());
        return this;
    }

    /**
     * 验证 Base64 格式
     * @param value Base64 字符串
     * @param urlSafe 是否为 URL-safe 格式
     * @return ValidaX实例
     */
    public ValidaX isBase64(Object value, boolean urlSafe) {
        if (checkRequirement(value, "Base64", errors, getLocale())) {
            return this;
        }
        baseValidation.validateBase64(value, urlSafe, errors, getLocale());
        return this;
    }

    /**
     * 验证 Base64 格式
     * @param value Base64 字符串
     * @param urlSafe 是否为 URL-safe 格式
     * @param allowNoPadding 是否允许不带填充符
     * @return ValidaX实例
     */
    public ValidaX isBase64(Object value, boolean urlSafe, boolean allowNoPadding) {
        if (checkRequirement(value, "Base64", errors, getLocale())) {
            return this;
        }
        baseValidation.validateBase64(value, urlSafe, allowNoPadding, errors, getLocale());
        return this;
    }

    /**
     * 验证年龄（只验证最小年龄）
     * @param value 待验证的值（LocalDate、Date或String）
     * @param minAge 最小年龄
     * @return ValidaX实例
     */
    public ValidaX isAge(Object value, int minAge) {
        if (checkRequirement(value, "Age", errors, getLocale())) {
            return this;
        }
        baseValidation.validateAge(value, minAge, errors, getLocale());
        return this;
    }

    /**
     * 验证年龄范围
     * @param value 待验证的值（LocalDate、Date或String）
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return ValidaX实例
     */
    public ValidaX isAge(Object value, int minAge, int maxAge) {
        if (checkRequirement(value, "Age", errors, getLocale())) {
            return this;
        }
        baseValidation.validateAge(value, minAge, maxAge, errors, getLocale());
        return this;
    }

    /**
     * 验证年龄（从身份证提取）
     * @param value 身份证号码
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @param fromIdCard 是否从身份证提取
     * @return ValidaX实例
     */
    public ValidaX isAge(Object value, int minAge, int maxAge, boolean fromIdCard) {
        if (checkRequirement(value, "Age", errors, getLocale())) {
            return this;
        }
        baseValidation.validateAge(value, minAge, maxAge, fromIdCard, errors, getLocale());
        return this;
    }

    /**
     * 验证年龄（完整参数）
     * @param value 待验证的值
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @param fromIdCard 是否从身份证提取
     * @param dateFormat 日期格式
     * @return ValidaX实例
     */
    public ValidaX isAge(Object value, int minAge, int maxAge, boolean fromIdCard, String dateFormat) {
        if (checkRequirement(value, "Age", errors, getLocale())) {
            return this;
        }
        baseValidation.validateAge(value, minAge, maxAge, fromIdCard, dateFormat, errors, getLocale());
        return this;
    }

    /**
     * 验证 JSON 格式（任意类型）
     * @param value JSON 字符串
     * @return ValidaX实例
     */
    public ValidaX isJSON(Object value) {
        if (checkRequirement(value, "JSON", errors, getLocale())) {
            return this;
        }
        baseValidation.validateJSON(value, errors, getLocale());
        return this;
    }

    /**
     * 验证 JSON 格式（指定类型）
     * @param value JSON 字符串
     * @param type JSON类型（ANY/OBJECT/ARRAY）
     * @return ValidaX实例
     */
    public ValidaX isJSON(Object value, io.github.vipxieliang.validx.annotations.JSON.JSONType type) {
        if (checkRequirement(value, "J S O N", errors, getLocale())) {
            return this;
        }
        baseValidation.validateJSON(value, type, errors, getLocale());
        return this;
    }

    /**
     * 验证 JSON 格式（指定类型和严格模式）
     * @param value JSON 字符串
     * @param type JSON类型
     * @param strict 是否严格模式
     * @return ValidaX实例
     */
    public ValidaX isJSON(Object value, io.github.vipxieliang.validx.annotations.JSON.JSONType type,
                          boolean strict) {
        if (checkRequirement(value, "J S O N", errors, getLocale())) {
            return this;
        }
        baseValidation.validateJSON(value, type, strict, errors, getLocale());
        return this;
    }

    /**
     * 验证 JSON 格式（完整参数）
     * @param value JSON 字符串
     * @param type JSON类型
     * @param strict 是否严格模式
     * @param maxDepth 最大深度
     * @param maxLength 最大长度
     * @return ValidaX实例
     */
    public ValidaX isJSON(Object value, io.github.vipxieliang.validx.annotations.JSON.JSONType type,
                          boolean strict, int maxDepth, int maxLength) {
        if (checkRequirement(value, "J S O N", errors, getLocale())) {
            return this;
        }
        baseValidation.validateJSON(value, type, strict, maxDepth, maxLength, errors, getLocale());
        return this;
    }

    /**
     * 验证国际电话号码（简单版本）
     *
     * @param value 要验证的电话号码
     * @return 当前验证链实例
     */
    public ValidaX isPhoneNumber(Object value) {
        if (checkRequirement(value, "Phone Number", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePhoneNumber(value, errors, getLocale());
        return this;
    }

    /**
     * 验证国际电话号码（指定国家代码）
     *
     * @param value       要验证的电话号码
     * @param countryCode 国家代码（如 "+86", "+1"）
     * @return 当前验证链实例
     */
    public ValidaX isPhoneNumber(Object value, String countryCode) {
        if (checkRequirement(value, "Phone Number", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePhoneNumber(value, countryCode, errors, getLocale());
        return this;
    }

    /**
     * 验证国际电话号码（指定国家代码和是否允许分机号）
     *
     * @param value          要验证的电话号码
     * @param countryCode    国家代码（如 "+86", "+1"）
     * @param allowExtension 是否允许分机号
     * @return 当前验证链实例
     */
    public ValidaX isPhoneNumber(Object value, String countryCode, boolean allowExtension) {
        if (checkRequirement(value, "Phone Number", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePhoneNumber(value, countryCode, allowExtension, errors, getLocale());
        return this;
    }

    /**
     * 验证国际电话号码（完整版本）
     *
     * @param value          要验证的电话号码
     * @param countryCode    国家代码（如 "+86", "+1"）
     * @param allowExtension 是否允许分机号
     * @param strict         是否严格模式（必须包含国家代码）
     * @return 当前验证链实例
     */
    public ValidaX isPhoneNumber(Object value, String countryCode, boolean allowExtension, boolean strict) {
        if (checkRequirement(value, "Phone Number", errors, getLocale())) {
            return this;
        }
        baseValidation.validatePhoneNumber(value, countryCode, allowExtension, strict, errors, getLocale());
        return this;
    }

    /**
     * 验证JWT Token格式
     * @param value 待验证的JWT Token
     * @return ValidaX实例
     */
    public ValidaX isJWT(Object value) {
        if (checkRequirement(value, "JWT", errors, getLocale())) {
            return this;
        }
        baseValidation.validateJWT(value, errors, getLocale());
        return this;
    }

    public ValidaX isSemVer(Object value) {
        if (checkRequirement(value, "Sem Ver", errors, getLocale())) {
            return this;
        }
        baseValidation.validateSemVer(value, false, errors, getLocale());
        return this;
    }

    public ValidaX isSemVer(Object value, boolean allowVPrefix) {
        if (checkRequirement(value, "Sem Ver", errors, getLocale())) {
            return this;
        }
        baseValidation.validateSemVer(value, allowVPrefix, errors, getLocale());
        return this;
    }

    /**
     * 验证Unix时间戳格式（秒或毫秒均可）
     * @param value 待验证的值
     * @return ValidaX实例
     */
    public ValidaX isTimestamp(Object value) {
        if (checkRequirement(value, "Timestamp", errors, getLocale())) {
            return this;
        }
        baseValidation.validateTimestamp(value, errors, getLocale());
        return this;
    }

    /**
     * 验证Unix时间戳格式（指定单位）
     * @param value 待验证的值
     * @param unit 时间戳单位（SECONDS/MILLISECONDS/ANY）
     * @return ValidaX实例
     */
    public ValidaX isTimestamp(Object value, Timestamp.TimestampUnit unit) {
        if (checkRequirement(value, "Timestamp", errors, getLocale())) {
            return this;
        }
        baseValidation.validateTimestamp(value, unit, errors, getLocale());
        return this;
    }

    /**
     * 验证Cron表达式格式
     *
     * @param value 待验证的值（String）
     * @return 当前验证链实例
     */
    public ValidaX isCronExpression(Object value) {
        if (checkRequirement(value, "CronExpression", errors, getLocale())) {
            return this;
        }
        baseValidation.validateCronExpression(value, errors, getLocale());
        return this;
    }

    /**
     * 验证时间段格式
     *
     * @param value 待验证的值（String）
     * @return 当前验证链实例
     */
    public ValidaX isDuration(Object value) {
        if (checkRequirement(value, "Duration", errors, getLocale())) {
            return this;
        }
        baseValidation.validateDuration(value, errors, getLocale());
        return this;
    }

    /**
     * 验证时间段格式（指定格式）
     *
     * @param value 待验证的值（String）
     * @param format 时间段格式类型
     * @return 当前验证链实例
     */
    public ValidaX isDuration(Object value, io.github.vipxieliang.validx.annotations.Duration.DurationFormat format) {
        if (checkRequirement(value, "Duration", errors, getLocale())) {
            return this;
        }
        baseValidation.validateDuration(value, format, errors, getLocale());
        return this;
    }

    /**
     * 验证快递单号格式
     *
     * @param value 待验证的值（String）
     * @return 当前验证链实例
     */
    public ValidaX isExpressNumber(Object value) {
        if (checkRequirement(value, "ExpressNumber", errors, getLocale())) {
            return this;
        }
        baseValidation.validateExpressNumber(value, errors, getLocale());
        return this;
    }

    /**
     * 验证快递单号格式（指定快递公司）
     *
     * @param value 待验证的值（String）
     * @param companies 快递公司类型
     * @return 当前验证链实例
     */
    public ValidaX isExpressNumber(Object value, io.github.vipxieliang.validx.annotations.ExpressNumber.ExpressCompany... companies) {
        if (checkRequirement(value, "ExpressNumber", errors, getLocale())) {
            return this;
        }
        baseValidation.validateExpressNumber(value, companies, errors, getLocale());
        return this;
    }

    // Financial Validation Methods
    public ValidaX isBankCard(Object value) {
        if (checkRequirement(value, "Bank Card", errors, getLocale())) {
            return this;
        }
        financialValidation.validateBankCard(value, errors, getLocale());
        return this;
    }

    public ValidaX isCVV(Object value) {
        if (checkRequirement(value, "CVV", errors, getLocale())) {
            return this;
        }
        financialValidation.validateCVV(value, errors, getLocale());
        return this;
    }

    public ValidaX isIBAN(Object value) {
        if (checkRequirement(value, "IBAN", errors, getLocale())) {
            return this;
        }
        financialValidation.validateIBAN(value, errors, getLocale());
        return this;
    }

    public ValidaX isSWIFT(Object value) {
        if (checkRequirement(value, "SWIFT", errors, getLocale())) {
            return this;
        }
        financialValidation.validateSWIFT(value, errors, getLocale());
        return this;
    }

    public ValidaX isStockCode(Object value) {
        if (checkRequirement(value, "Stock Code", errors, getLocale())) {
            return this;
        }
        StockCode.Exchange[] exchanges = StockCode.Exchange.values();
        financialValidation.validateStockCode(value, exchanges, errors, getLocale());
        return this;
    }
    
    public ValidaX isStockCode(Object value, StockCode.Exchange... exchanges) {
        if (checkRequirement(value, "Stock Code", errors, getLocale())) {
            return this;
        }
        financialValidation.validateStockCode(value, exchanges, errors, getLocale());
        return this;
    }

    public ValidaX isFinancialProductCode(Object value) {
        if (checkRequirement(value, "Financial Product Code", errors, getLocale())) {
            return this;
        }
        FinancialProductCode.ProductType[] productTypes = FinancialProductCode.ProductType.values();
        financialValidation.validateFinancialProductCode(value,productTypes, errors, getLocale());
        return this;
    }
    
    public ValidaX isFinancialProductCode(Object value, FinancialProductCode.ProductType... productTypes) {
        if (checkRequirement(value, "Financial Product Code", errors, getLocale())) {
            return this;
        }
        financialValidation.validateFinancialProductCode(value, productTypes, errors, getLocale());
        return this;
    }
    
    public ValidaX isTradeOrderNumber(Object value) {
        if (checkRequirement(value, "Trade Order Number", errors, getLocale())) {
            return this;
        }
        financialValidation.validateTradeOrderNumber(value, errors, getLocale());
        return this;
    }

    // Vehicle Validation Methods
    public ValidaX isVIN(Object value) {
        if (checkRequirement(value, "VIN", errors, getLocale())) {
            return this;
        }
        vehicleValidation.validateVIN(value, errors, getLocale());
        return this;
    }

    public ValidaX isVehicleEngine(Object value) {
        if (checkRequirement(value, "Vehicle Engine", errors, getLocale())) {
            return this;
        }
        vehicleValidation.validateVehicleEngine(value, errors, getLocale());
        return this;
    }

    // Book Validation Methods
    public ValidaX isISBN(Object value) {
        if (checkRequirement(value, "ISBN", errors, getLocale())) {
            return this;
        }
        bookValidation.validateISBN(value, errors, getLocale());
        return this;
    }

    public ValidaX isISSN(Object value) {
        if (checkRequirement(value, "ISSN", errors, getLocale())) {
            return this;
        }
        bookValidation.validateISSN(value, errors, getLocale());
        return this;
    }

    public ValidaX isDOI(Object value) {
        if (checkRequirement(value, "DOI", errors, getLocale())) {
            return this;
        }
        bookValidation.validateDOI(value, errors, getLocale());
        return this;
    }

    public ValidaX isCLC(Object value) {
        if (checkRequirement(value, "CLC", errors, getLocale())) {
            return this;
        }
        bookValidation.validateCLC(value, errors, getLocale());
        return this;
    }

    public ValidaX isDDC(Object value) {
        if (checkRequirement(value, "DDC", errors, getLocale())) {
            return this;
        }
        bookValidation.validateDDC(value, errors, getLocale());
        return this;
    }

    public ValidaX isORCID(Object value) {
        if (checkRequirement(value, "ORCID", errors, getLocale())) {
            return this;
        }
        bookValidation.validateORCID(value, errors, getLocale());
        return this;
    }

    public ValidaX isIPC(Object value) {
        if (checkRequirement(value, "IPC", errors, getLocale())) {
            return this;
        }
        bookValidation.validateIPC(value, errors, getLocale());
        return this;
    }

    // Education Validation Methods
    public ValidaX isDegreeCertificate(Object value) {
        if (checkRequirement(value, "Degree Certificate", errors, getLocale())) {
            return this;
        }
        educationValidation.validateDegreeCertificate(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isTeacher(Object value) {
        if (checkRequirement(value, "Teacher", errors, getLocale())) {
            return this;
        }
        educationValidation.validateTeacher(value, errors, getLocale());
        return this;
    }
    
    public ValidaX isDoctor(Object value) {
        if (checkRequirement(value, "Doctor", errors, getLocale())) {
            return this;
        }
        educationValidation.validateDoctor(value, errors, getLocale());
        return this;
    }

    // Foreign Validation Methods
    public ValidaX isForeignerWorkPermit(Object value) {
        if (checkRequirement(value, "Foreigner Work Permit", errors, getLocale())) {
            return this;
        }
        foreignValidation.validateForeignerWorkPermit(value, errors, getLocale());
        return this;
    }

    // Phone Validation Methods
    public ValidaX isIMEI(Object value) {
        if (checkRequirement(value, "IMEI", errors, getLocale())) {
            return this;
        }
        phoneValidation.validateIMEI(value, errors, getLocale());
        return this;
    }

    /**
     * 验证PMP证书编号
     * 
     * @param value PMP证书编号
     * @return ValidaX实例，支持链式调用
     */
    public ValidaX isPMP(Object value) {
        if (checkRequirement(value, "PMP", errors, getLocale())) {
            return this;
        }
        certificationValidation.validatePMP(value, errors, getLocale());
        return this;
    }
    
    /**
     * 验证建造师证书编号
     * 
     * @param value 建造师证书编号
     * @return ValidaX实例，支持链式调用
     */
    public ValidaX isConstructor(Object value) {
        if (checkRequirement(value, "Constructor", errors, getLocale())) {
            return this;
        }
        certificationValidation.validateConstructor(value, errors, getLocale());
        return this;
    }

    /**
     * 验证会计资格证书编号
     * 
     * @param value 会计资格证书编号
     * @return ValidaX实例，支持链式调用
     */
    public ValidaX isAccountant(Object value) {
        if (checkRequirement(value, "Accountant", errors, getLocale())) {
            return this;
        }
        certificationValidation.validateAccountant(value, errors, getLocale());
        return this;
    }

    /**
     * 判断值是否为空（根据类型智能判断）
     * @param value 待判断的值
     * @return true表示为空，false表示不为空
     */
    private boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof String) {
            return ((String) value).isEmpty();
        }

        if (value instanceof java.util.Collection) {
            return ((java.util.Collection<?>) value).isEmpty();
        }

        if (value instanceof java.util.Map) {
            return ((java.util.Map<?, ?>) value).isEmpty();
        }

        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value) == 0;
        }

        return false;
    }

    /**
     * 检查必填要求的公共方法
     * @param value 待校验的值（Object类型，支持多种类型）
     * @param defaultFieldLabel 默认字段标识（如 "Phone"）
     * @param errors 错误列表
     * @param locale 语言环境
     * @return true表示应该跳过格式校验（已处理），false表示应该继续格式校验
     */
    public boolean checkRequirement(Object value, String defaultFieldLabel,
                                   List<String> errors, Locale locale) {
        // 1. 确定当前校验的要求
        LocalRequirement requirement = this.localRequirement;
        if (requirement == LocalRequirement.UNSET) {
            // 使用全局配置
            requirement = convertGlobalToLocal(this.config.getRequirementMode());
        }

        // 2. 重置局部状态（使用后立即重置）
        this.localRequirement = LocalRequirement.UNSET;

        // 3. 确定字段标识（优先使用用户指定的，否则使用默认值）
        String fieldLabel = this.currentFieldLabel != null ? this.currentFieldLabel : defaultFieldLabel;
        this.currentFieldLabel = null;  // 重置字段标识

        // 4. 检查必填要求并添加错误消息
        if (requirement == LocalRequirement.NOT_NULL) {
            if (value == null) {
                String message = MessageManager.getMessage("io.github.vipxieliang.validx.value.null", locale);
                errors.add(fieldLabel + ": " + message);
                return true;  // 已处理，跳过格式校验
            }
        }

        if (requirement == LocalRequirement.NOT_EMPTY) {
            if (value == null) {
                String message = MessageManager.getMessage("io.github.vipxieliang.validx.value.null", locale);
                errors.add(fieldLabel + ": " + message);
                return true;  // 已处理，跳过格式校验
            }
            if (isEmpty(value)) {
                String message = MessageManager.getMessage("io.github.vipxieliang.validx.value.empty", locale);
                errors.add(fieldLabel + ": " + message);
                return true;  // 已处理，跳过格式校验
            }
        }

        if (requirement == LocalRequirement.ALLOW_NULL) {
            if (isEmpty(value)) {
                return true;  // 允许为空，跳过格式校验
            }
        }

        if (requirement == LocalRequirement.ALLOW_EMPTY) {
            if (value == null) {
                String message = MessageManager.getMessage("io.github.vipxieliang.validx.value.null", locale);
                errors.add(fieldLabel + ": " + message);
                return true;  // null不允许，已处理
            }
            if (isEmpty(value)) {
                return true;  // 空值允许，跳过格式校验
            }
        }

        // 5. 默认行为：如果值为空，跳过格式校验
        return isEmpty(value);// 继续执行格式校验
    }

    private LocalRequirement convertGlobalToLocal(ValidXConfig.RequirementMode global) {
        switch (global) {
            case NOT_NULL: return LocalRequirement.NOT_NULL;
            case NOT_EMPTY: return LocalRequirement.NOT_EMPTY;
            default: return LocalRequirement.UNSET;
        }
    }

    public boolean passed() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public String getErrorMessage() {
        return errors.isEmpty() ? "" : String.join(", ", errors);
    }

    /**
     * 局部要求枚举
     */
    public enum LocalRequirement {
        UNSET,        // 未设置（使用全局配置或默认）
        NOT_NULL,     // 要求非null
        NOT_EMPTY,    // 要求非null且非空字符串
        ALLOW_NULL,   // 允许null和空字符串（覆盖全局配置）
        ALLOW_EMPTY   // 允许空字符串但不允许null（覆盖全局配置）
    }
}