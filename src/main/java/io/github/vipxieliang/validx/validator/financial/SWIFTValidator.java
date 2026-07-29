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

package io.github.vipxieliang.validx.validator.financial;

import io.github.vipxieliang.validx.annotations.SWIFT;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * SWIFT/BIC代码验证器
 * 验证SWIFT/BIC银行代码的格式
 * 
 * SWIFT/BIC格式:
 * 1. 8位或11位长度
 * 2. 前4位: 银行代码 (字母)
 * 3. 接下来2位: 国家代码 (字母)
 * 4. 接下来2位: 位置代码 (字母或数字)
 * 5. 可选的3位: 分行代码 (字母或数字)
 */
public class SWIFTValidator implements ConstraintValidator<SWIFT, String> {
    
    // SWIFT/BIC格式正则表达式
    // 8位基本格式: AAAA BB CC DDD (其中DDD是可选的分行代码)
    // 前4位银行代码: 只能是字母
    // 2位国家代码: 只能是字母
    // 2位位置代码: 字母或数字
    // 3位分行代码(可选): 字母或数字
    private static final Pattern SWIFT_PATTERN = Pattern.compile("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([A-Z0-9]{3})?$");
    
    // 有效的国家代码列表 (ISO 3166-1 alpha-2)
    private static final java.util.Set<String> VALID_COUNTRY_CODES = new java.util.HashSet<>();
    
    static {
        // 初始化有效的国家代码列表 (部分常见国家)
        String[] countryCodes = {
            "AD", "AE", "AF", "AG", "AI", "AL", "AM", "AO", "AQ", "AR", "AS", "AT", "AU", "AW", "AX", "AZ",
            "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BL", "BM", "BN", "BO", "BQ", "BR", "BS",
            "BT", "BV", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL", "CM", "CN",
            "CO", "CR", "CU", "CV", "CW", "CX", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ", "EC", "EE",
            "EG", "EH", "ER", "ES", "ET", "FI", "FJ", "FK", "FM", "FO", "FR", "GA", "GB", "GD", "GE", "GF",
            "GG", "GH", "GI", "GL", "GM", "GN", "GP", "GQ", "GR", "GS", "GT", "GU", "GW", "GY", "HK", "HM",
            "HN", "HR", "HT", "HU", "ID", "IE", "IL", "IM", "IN", "IO", "IQ", "IR", "IS", "IT", "JE", "JM",
            "JO", "JP", "KE", "KG", "KH", "KI", "KM", "KN", "KP", "KR", "KW", "KY", "KZ", "LA", "LB", "LC",
            "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME", "MF", "MG", "MH", "MK",
            "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT", "MU", "MV", "MW", "MX", "MY", "MZ", "NA",
            "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "OM", "PA", "PE", "PF", "PG",
            "PH", "PK", "PL", "PM", "PN", "PR", "PS", "PT", "PW", "PY", "QA", "RE", "RO", "RS", "RU", "RW",
            "SA", "SB", "SC", "SD", "SE", "SG", "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "SS",
            "ST", "SV", "SX", "SY", "SZ", "TC", "TD", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO",
            "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "UM", "US", "UY", "UZ", "VA", "VC", "VE", "VG", "VI",
            "VN", "VU", "WF", "WS", "YE", "YT", "ZA", "ZM", "ZW"
        };
        
        for (String code : countryCodes) {
            VALID_COUNTRY_CODES.add(code);
        }
    }

    @Override
    public void initialize(SWIFT constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格并转换为大写
        String cleanValue = value.replaceAll("\\s+", "").toUpperCase();
        
        // 基本格式检查 (8位或11位)
        if (cleanValue.length() != 8 && cleanValue.length() != 11) {
            return false;
        }
        
        // 正则表达式检查
        if (!SWIFT_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 国家代码检查
        String countryCode = cleanValue.substring(4, 6);
        return VALID_COUNTRY_CODES.contains(countryCode);
    }
}