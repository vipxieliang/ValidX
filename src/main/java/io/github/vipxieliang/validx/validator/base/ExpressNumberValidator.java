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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.ExpressNumber;
import io.github.vipxieliang.validx.annotations.ExpressNumber.ExpressCompany;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 快递单号验证器
 * 验证值是否为有效的快递单号格式
 *
 * <p>支持的快递公司：</p>
 * <ul>
 *   <li>顺丰速运: 12位数字</li>
 *   <li>圆通速递: YT开头+11-13位数字，或10-13位纯数字</li>
 *   <li>申通快递: 12位数字</li>
 *   <li>中通快递: 12位数字或字母+数字组合</li>
 *   <li>韵达快递: 13位数字</li>
 *   <li>邮政EMS: E字母+9位数字+CN，或2位字母+9位数字+CN</li>
 *   <li>京东物流: JD开头+13-15位数字</li>
 *   <li>德邦快递: 8-9位数字</li>
 *   <li>天天快递: 12-14位数字</li>
 *   <li>百世快递: 10-12位数字或字母</li>
 * </ul>
 *
 * @author vipxieliang
 * @since 2025/01/20
 */
public class ExpressNumberValidator implements ConstraintValidator<ExpressNumber, Object> {

    // 顺丰速运：12位数字
    private static final Pattern SF_EXPRESS_PATTERN = Pattern.compile("^\\d{12}$");

    // 圆通速递：YT开头+11-13位数字，或10-13位纯数字
    private static final Pattern YTO_EXPRESS_PATTERN = Pattern.compile("^(YT\\d{11,13}|\\d{10,13})$", Pattern.CASE_INSENSITIVE);

    // 申通快递：12位数字
    private static final Pattern STO_EXPRESS_PATTERN = Pattern.compile("^\\d{12}$");

    // 中通快递：12位数字或字母+数字组合（至少12位）
    private static final Pattern ZTO_EXPRESS_PATTERN = Pattern.compile("^[A-Za-z0-9]{12,}$");

    // 韵达快递：13位数字
    private static final Pattern YUNDA_EXPRESS_PATTERN = Pattern.compile("^\\d{13}$");

    // 邮政EMS：E字母+9位数字+CN，或2位字母+9位数字+CN
    private static final Pattern EMS_PATTERN = Pattern.compile("^(E\\d{9}CN|[A-Z]{2}\\d{9}CN)$", Pattern.CASE_INSENSITIVE);

    // 京东物流：JD开头+13-15位数字
    private static final Pattern JD_LOGISTICS_PATTERN = Pattern.compile("^JD\\d{13,15}$", Pattern.CASE_INSENSITIVE);

    // 德邦快递：8-9位数字
    private static final Pattern DEPPON_PATTERN = Pattern.compile("^\\d{8,9}$");

    // 天天快递：12-14位数字
    private static final Pattern TTKD_EXPRESS_PATTERN = Pattern.compile("^\\d{12,14}$");

    // 百世快递：10-12位数字或字母
    private static final Pattern BEST_EXPRESS_PATTERN = Pattern.compile("^[A-Za-z0-9]{10,12}$");

    private Set<ExpressCompany> companies;

    @Override
    public void initialize(ExpressNumber constraintAnnotation) {
        ExpressCompany[] companiesArray = constraintAnnotation.companies();
        if (companiesArray.length == 0) {
            // 默认支持所有快递公司
            this.companies = new HashSet<>(Arrays.asList(ExpressCompany.values()));
        } else {
            this.companies = new HashSet<>(Arrays.asList(companiesArray));
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 空值由@NotNull等其他注解处理
        }

        if (!(value instanceof String)) {
            return false;
        }

        String expressNumber = (String) value;

        if (expressNumber.trim().isEmpty()) {
            return true; // 空字符串由@NotEmpty等其他注解处理
        }

        return isValidExpressNumber(expressNumber, companies);
    }

    /**
     * 验证快递单号格式（静态方法，供链式调用使用）
     */
    public static boolean isValid(Object value) {
        return isValid(value, new HashSet<>(Arrays.asList(ExpressCompany.values())));
    }

    /**
     * 验证快递单号格式（静态方法，指定快递公司）
     */
    public static boolean isValid(Object value, ExpressCompany... companies) {
        Set<ExpressCompany> companySet;
        if (companies.length == 0) {
            companySet = new HashSet<>(Arrays.asList(ExpressCompany.values()));
        } else {
            companySet = new HashSet<>(Arrays.asList(companies));
        }
        return isValid(value, companySet);
    }

    /**
     * 验证快递单号格式（静态方法，使用Set）
     */
    private static boolean isValid(Object value, Set<ExpressCompany> companies) {
        if (value == null) {
            return true;
        }

        if (!(value instanceof String)) {
            return false;
        }

        String expressNumber = (String) value;

        if (expressNumber.trim().isEmpty()) {
            return true;
        }

        return isValidExpressNumber(expressNumber, companies);
    }

    /**
     * 验证快递单号格式
     */
    private static boolean isValidExpressNumber(String expressNumber, Set<ExpressCompany> companies) {
        if (expressNumber == null || expressNumber.trim().isEmpty()) {
            return false;
        }

        expressNumber = expressNumber.trim();

        // 检查是否匹配任何指定的快递公司格式
        for (ExpressCompany company : companies) {
            if (matchesCompanyPattern(expressNumber, company)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查快递单号是否匹配指定快递公司的格式
     */
    private static boolean matchesCompanyPattern(String expressNumber, ExpressCompany company) {
        switch (company) {
            case SF_EXPRESS:
                return SF_EXPRESS_PATTERN.matcher(expressNumber).matches();
            case YTO_EXPRESS:
                return YTO_EXPRESS_PATTERN.matcher(expressNumber).matches();
            case STO_EXPRESS:
                return STO_EXPRESS_PATTERN.matcher(expressNumber).matches();
            case ZTO_EXPRESS:
                return ZTO_EXPRESS_PATTERN.matcher(expressNumber).matches();
            case YUNDA_EXPRESS:
                return YUNDA_EXPRESS_PATTERN.matcher(expressNumber).matches();
            case EMS:
                return EMS_PATTERN.matcher(expressNumber).matches();
            case JD_LOGISTICS:
                return JD_LOGISTICS_PATTERN.matcher(expressNumber).matches();
            case DEPPON:
                return DEPPON_PATTERN.matcher(expressNumber).matches();
            case TTKD_EXPRESS:
                return TTKD_EXPRESS_PATTERN.matcher(expressNumber).matches();
            case BEST_EXPRESS:
                return BEST_EXPRESS_PATTERN.matcher(expressNumber).matches();
            default:
                return false;
        }
    }
}
