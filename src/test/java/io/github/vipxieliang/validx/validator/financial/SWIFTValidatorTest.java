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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SWIFTValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class SWIFTDTO {
        @SWIFT
        private String swift;

        public SWIFTDTO(String swift) {
            this.swift = swift;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        SWIFTDTO nullDto = new SWIFTDTO(null);
        Set<ConstraintViolation<SWIFTDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        SWIFTDTO emptyDto = new SWIFTDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidSWIFT() {
        String[] validSWIFTs = {
            "DEUTDEFF",
            "DEUTDEFF500",
            "BNPAFRPP",
            "BNPAFRPPXXX",
            "CHASUS33",
            "CHASUS33XXX"
        };

        for (String swift : validSWIFTs) {
            SWIFTDTO dto = new SWIFTDTO(swift);
            Set<ConstraintViolation<SWIFTDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "SWIFT '" + swift + "' should be valid");
        }
    }

    @Test
    public void testInvalidSWIFT() {
        String[] invalidSWIFTs = {
            "DEUT",
            "DEUTDEFF50",
            "DEUTDEFF5000",
            "DEUTXXFF",
            "1EUTDEFF",
            "DEUT-DEFF"
        };

        for (String swift : invalidSWIFTs) {
            SWIFTDTO dto = new SWIFTDTO(swift);
            Set<ConstraintViolation<SWIFTDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "SWIFT '" + swift + "' should be invalid");
        }
    }
}
