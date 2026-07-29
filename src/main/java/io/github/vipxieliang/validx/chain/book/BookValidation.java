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

package io.github.vipxieliang.validx.chain.book;

import io.github.vipxieliang.validx.validator.book.ISBNValidator;
import io.github.vipxieliang.validx.validator.book.ISSNValidator;
import io.github.vipxieliang.validx.validator.book.DOIValidator;
import io.github.vipxieliang.validx.validator.book.CLCValidator;
import io.github.vipxieliang.validx.validator.book.DDCValidator;
import io.github.vipxieliang.validx.validator.book.ORCIDValidator;
import io.github.vipxieliang.validx.validator.book.IPCValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import java.util.List;
import java.util.Locale;

public class BookValidation {
    
    public void validateISBN(Object value, List<String> errors, Locale locale) {
        ISBNValidator validator = new ISBNValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.isbn", locale));
        }
    }
    
    public void validateISSN(Object value, List<String> errors, Locale locale) {
        ISSNValidator validator = new ISSNValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.issn", locale));
        }
    }
    
    public void validateDOI(Object value, List<String> errors, Locale locale) {
        DOIValidator validator = new DOIValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.doi", locale));
        }
    }
    
    public void validateCLC(Object value, List<String> errors, Locale locale) {
        CLCValidator validator = new CLCValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.clc", locale));
        }
    }
    
    public void validateDDC(Object value, List<String> errors, Locale locale) {
        DDCValidator validator = new DDCValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.ddc", locale));
        }
    }
    
    public void validateORCID(Object value, List<String> errors, Locale locale) {
        ORCIDValidator validator = new ORCIDValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.orcid", locale));
        }
    }
    
    public void validateIPC(Object value, List<String> errors, Locale locale) {
        IPCValidator validator = new IPCValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.ipc", locale));
        }
    }
}