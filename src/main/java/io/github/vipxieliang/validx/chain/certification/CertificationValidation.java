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

package io.github.vipxieliang.validx.chain.certification;

import io.github.vipxieliang.validx.annotations.PMP;
import io.github.vipxieliang.validx.annotations.Constructor;
import io.github.vipxieliang.validx.annotations.Accountant;
import io.github.vipxieliang.validx.validator.certification.PMPValidator;
import io.github.vipxieliang.validx.validator.certification.ConstructorValidator;
import io.github.vipxieliang.validx.validator.certification.AccountantValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.Payload;
import java.util.List;
import java.util.Locale;

public class CertificationValidation {
    
    public void validatePMP(Object value, List<String> errors, Locale locale) {
        PMPValidator validator = new PMPValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.pmp", locale));
        }
    }
    
    public void validateConstructor(Object value, List<String> errors, Locale locale) {
        ConstructorValidator validator = new ConstructorValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.constructor", locale));
        }
    }
    
    public void validateAccountant(Object value, List<String> errors, Locale locale) {
        AccountantValidator validator = new AccountantValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.accountant", locale));
        }
    }
}