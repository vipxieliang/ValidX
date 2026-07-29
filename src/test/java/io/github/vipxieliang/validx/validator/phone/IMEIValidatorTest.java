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

package io.github.vipxieliang.validx.validator.phone;

import io.github.vipxieliang.validx.annotations.IMEI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class IMEIValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class IMEIDTO {
        @IMEI
        private String imei;

        public IMEIDTO(String imei) {
            this.imei = imei;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        IMEIDTO nullDto = new IMEIDTO(null);
        Set<ConstraintViolation<IMEIDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        IMEIDTO emptyDto = new IMEIDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidIMEIs() {
        String[] validIMEIs = {
            "490154203237518",
            "35-209900-176148-1",
            "352099001761481",
            "35209900176148100"
        };

        for (String imei : validIMEIs) {
            IMEIDTO dto = new IMEIDTO(imei);
            Set<ConstraintViolation<IMEIDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "IMEI '" + imei + "' should be valid");
        }
    }

    @Test
    public void testInvalidIMEIs() {
        String[] invalidIMEIs = {
            "490154203237519",
            "12345678901234",
            "1234567890123456",
            "49015420323751a",
            "352099001761482"
        };

        for (String imei : invalidIMEIs) {
            IMEIDTO dto = new IMEIDTO(imei);
            Set<ConstraintViolation<IMEIDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "IMEI '" + imei + "' should be invalid");
        }
    }
}
