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

import io.github.vipxieliang.validx.annotations.BankCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BankCardValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class BankCardDTO {
        @BankCard
        private String cardNumber;

        public BankCardDTO(String cardNumber) {
            this.cardNumber = cardNumber;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        BankCardDTO nullDto = new BankCardDTO(null);
        Set<ConstraintViolation<BankCardDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        BankCardDTO emptyDto = new BankCardDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidBankCards() {
        String[] validCards = {
            "4532015112830366",
            "5425233430109903",
            "6011111111111117",
            "4532 0151 1283 0366",
            "5425-2334-3010-9903"
        };

        for (String card : validCards) {
            BankCardDTO dto = new BankCardDTO(card);
            Set<ConstraintViolation<BankCardDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "Bank card '" + card + "' should be valid");
        }
    }

    @Test
    public void testInvalidBankCards() {
        String[] invalidCards = {
            "4532015112830367",
            "123456789012",
            "12345678901234567890",
            "453201511283036a",
            "5425233430109904"
        };

        for (String card : invalidCards) {
            BankCardDTO dto = new BankCardDTO(card);
            Set<ConstraintViolation<BankCardDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "Bank card '" + card + "' should be invalid");
        }
    }
}
