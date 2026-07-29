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

package io.github.vipxieliang.validx.chain.phone;

import io.github.vipxieliang.validx.i18n.MessageManager;
import io.github.vipxieliang.validx.validator.phone.IMEIValidator;

import java.util.List;
import java.util.Locale;

public class PhoneValidation {
    
    public void validateIMEI(Object value, List<String> errors, Locale locale) {
        IMEIValidator validator = new IMEIValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.imei", locale));
        }
    }

}