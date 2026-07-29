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

import io.github.vipxieliang.validx.annotations.StockCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StockCodeValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class StockCodeDTO {
        @StockCode
        private String stockCode;

        public StockCodeDTO(String stockCode) {
            this.stockCode = stockCode;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        StockCodeDTO nullDto = new StockCodeDTO(null);
        Set<ConstraintViolation<StockCodeDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        StockCodeDTO emptyDto = new StockCodeDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidStockCode() {
        String[] validCodes = {
            "600000",
            "000001",
            "300001",
            "00700",
            "AAPL"
        };

        for (String code : validCodes) {
            StockCodeDTO dto = new StockCodeDTO(code);
            Set<ConstraintViolation<StockCodeDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "Stock code '" + code + "' should be valid");
        }
    }

    @Test
    public void testInvalidStockCode() {
        String[] invalidCodes = {
            "500000",
            "123",
            "1234567",
            "ABCDEF",
            "12A456"
        };

        for (String code : invalidCodes) {
            StockCodeDTO dto = new StockCodeDTO(code);
            Set<ConstraintViolation<StockCodeDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "Stock code '" + code + "' should be invalid");
        }
    }
}
