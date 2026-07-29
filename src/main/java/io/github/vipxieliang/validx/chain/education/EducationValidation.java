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

package io.github.vipxieliang.validx.chain.education;

import io.github.vipxieliang.validx.annotations.Doctor;
import io.github.vipxieliang.validx.annotations.Teacher;
import io.github.vipxieliang.validx.validator.education.DegreeCertificateValidator;
import io.github.vipxieliang.validx.validator.education.TeacherValidator;
import io.github.vipxieliang.validx.validator.education.DoctorValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.Payload;
import java.util.List;
import java.util.Locale;

public class EducationValidation {
    
    public void validateDegreeCertificate(Object value, List<String> errors, Locale locale) {
        DegreeCertificateValidator validator = new DegreeCertificateValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.degree.certificate", locale));
        }
    }
    
    public void validateTeacher(Object value, List<String> errors, Locale locale) {
        TeacherValidator validator = new TeacherValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.teacher", locale));
        }
    }
    
    public void validateDoctor(Object value, List<String> errors, Locale locale) {
        DoctorValidator validator = new DoctorValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.doctor", locale));
        }
    }
}