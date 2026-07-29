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

import io.github.vipxieliang.validx.annotations.TradeOrderNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TradeOrderNumberValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class TradeOrderNumberDTO {
        @TradeOrderNumber
        private String orderNumber;

        public TradeOrderNumberDTO(String orderNumber) {
            this.orderNumber = orderNumber;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        TradeOrderNumberDTO nullDto = new TradeOrderNumberDTO(null);
        Set<ConstraintViolation<TradeOrderNumberDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        TradeOrderNumberDTO emptyDto = new TradeOrderNumberDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidTradeOrderNumber() {
        String[] validNumbers = {
            "T202510171234567890",
            "202510171234567890",
            "550e8400-e29b-41d4-a716-446655440000",
            "550e8400e29b41d4a716446655440000"
        };

        for (String number : validNumbers) {
            TradeOrderNumberDTO dto = new TradeOrderNumberDTO(number);
            Set<ConstraintViolation<TradeOrderNumberDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "Order number '" + number + "' should be valid");
        }
    }

    @Test
    public void testInvalidTradeOrderNumber() {
        String[] invalidNumbers = {
            "T12345",
            "12345",
            "ABC123",
            "T20251017123456789",
            "20251017123456789"
        };

        for (String number : invalidNumbers) {
            TradeOrderNumberDTO dto = new TradeOrderNumberDTO(number);
            Set<ConstraintViolation<TradeOrderNumberDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "Order number '" + number + "' should be invalid");
        }
    }
}
