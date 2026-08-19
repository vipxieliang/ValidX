/*
 * Copyright 2025-2026 vipxieliang
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

package io.github.vipxieliang.validx.validator.foreign;

import io.github.vipxieliang.validx.annotations.NationalityCode;
import io.github.vipxieliang.validx.enums.IsoCountry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

/**
 * 国籍国代码验证器
 * 验证字符串是否为有效的 ISO 3166-1 国家/地区代码（两字母、三字母或三位数字）
 */
public class NationalityCodeValidator implements ConstraintValidator<NationalityCode, String> {

    private NationalityCode.NationalityCodeType[] formats;

    @Override
    public void initialize(NationalityCode constraintAnnotation) {
        this.initialize(constraintAnnotation.formats());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param formats 允许的编码形式数组
     */
    public void initialize(NationalityCode.NationalityCodeType[] formats) {
        this.formats = formats;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        String normalized = value.toUpperCase(Locale.ROOT);
        for (NationalityCode.NationalityCodeType format : formats) {
            if (matches(normalized, format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定编码形式是否匹配
     * @param normalized 已大写规范化的待校验值
     * @param format 编码形式
     * @return 是否匹配
     */
    private boolean matches(String normalized, NationalityCode.NationalityCodeType format) {
        switch (format) {
            case ALPHA_2:
                return IsoCountry.fromAlpha2(normalized).isPresent();
            case ALPHA_3:
                return IsoCountry.fromAlpha3(normalized).isPresent();
            case NUMERIC:
                return IsoCountry.fromNumeric(normalized).isPresent();
            default:
                return false;
        }
    }
}
