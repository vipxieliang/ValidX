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

import io.github.vipxieliang.validx.annotations.CVV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CVVValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class CVVDTO {
        @CVV
        private String cvv;

        public CVVDTO(String cvv) {
            this.cvv = cvv;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        CVVDTO nullDto = new CVVDTO(null);
        Set<ConstraintViolation<CVVDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        CVVDTO emptyDto = new CVVDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidCVV() {
        String[] validCVVs = {
            "123",
            "456",
            "1234",
            "9999",
            "000"
        };

        for (String cvv : validCVVs) {
            CVVDTO dto = new CVVDTO(cvv);
            Set<ConstraintViolation<CVVDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "CVV '" + cvv + "' should be valid");
        }
    }

    @Test
    public void testInvalidCVV() {
        String[] invalidCVVs = {
            "12",
            "12345",
            "abc",
            "12a",
            "1 23"
        };

        for (String cvv : invalidCVVs) {
            CVVDTO dto = new CVVDTO(cvv);
            Set<ConstraintViolation<CVVDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "CVV '" + cvv + "' should be invalid");
        }
    }
}
