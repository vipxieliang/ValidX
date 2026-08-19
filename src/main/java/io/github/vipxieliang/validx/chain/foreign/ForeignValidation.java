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

package io.github.vipxieliang.validx.chain.foreign;

import io.github.vipxieliang.validx.annotations.NationalityCode;
import io.github.vipxieliang.validx.validator.foreign.ForeignerWorkPermitValidator;
import io.github.vipxieliang.validx.validator.foreign.NationalityCodeValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import java.util.List;
import java.util.Locale;

public class ForeignValidation {
    
    public void validateForeignerWorkPermit(Object value, List<String> errors, Locale locale) {
        ForeignerWorkPermitValidator validator = new ForeignerWorkPermitValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.foreigner.work.permit", locale));
        }
    }
    /**
     * 验证国籍国代码（指定允许的编码形式）
     *
     * @param value   要验证的国籍国代码
     * @param formats 允许的编码形式数组，与 {@link NationalityCode#formats()} 保持一致
     * @param errors  错误列表
     * @param locale  语言环境
     */
    public void validateNationalityCode(Object value, NationalityCode.NationalityCodeType[] formats, List<String> errors, Locale locale) {
        NationalityCodeValidator validator = new NationalityCodeValidator();
        validator.initialize(formats);
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.nationality.code", locale));
        }
    }
}
