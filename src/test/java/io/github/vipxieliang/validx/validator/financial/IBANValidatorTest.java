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

import io.github.vipxieliang.validx.annotations.IBAN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class IBANValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class IBANDTO {
        @IBAN
        private String iban;

        public IBANDTO(String iban) {
            this.iban = iban;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        IBANDTO nullDto = new IBANDTO(null);
        Set<ConstraintViolation<IBANDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        IBANDTO emptyDto = new IBANDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidIBAN() {
        String[] validIBANs = {
            "GB82WEST12345698765432",
            "DE89370400440532013000",
            "FR1420041010050500013M02606",
            "IT60X0542811101000000123456",
            "GB82 WEST 1234 5698 7654 32"
        };

        for (String iban : validIBANs) {
            IBANDTO dto = new IBANDTO(iban);
            Set<ConstraintViolation<IBANDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "IBAN '" + iban + "' should be valid");
        }
    }

    @Test
    public void testInvalidIBAN() {
        String[] invalidIBANs = {
            "GB82WEST1234569876543",
            "DE89370400440532013001",
            "XX1420041010050500013M02606",
            "GB82WEST123456987654321",
            "GB82WEST123456987654"
        };

        for (String iban : invalidIBANs) {
            IBANDTO dto = new IBANDTO(iban);
            Set<ConstraintViolation<IBANDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "IBAN '" + iban + "' should be invalid");
        }
    }
}
