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

package io.github.vipxieliang.validx.validator.network;

import io.github.vipxieliang.validx.annotations.Mac;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MacValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class MacDTO {
        @Mac
        private String macAddress;

        public MacDTO(String macAddress) {
            this.macAddress = macAddress;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        MacDTO nullDto = new MacDTO(null);
        Set<ConstraintViolation<MacDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        MacDTO emptyDto = new MacDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidMacAddresses() {
        String[] validMacs = {
            "00:1B:44:11:3A:B7",
            "00:1b:44:11:3a:b7",
            "00:1B:44:11:3a:B7",
            "00-1B-44-11-3A-B7",
            "00-1b-44-11-3a-b7"
        };

        for (String mac : validMacs) {
            MacDTO dto = new MacDTO(mac);
            Set<ConstraintViolation<MacDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "MAC address '" + mac + "' should be valid");
        }
    }

    @Test
    public void testInvalidMacAddresses() {
        String[] invalidMacs = {
            "00:1B:44:11:3A",
            "00:1B:44:11:3A:B7:C8",
            "00:1B:44:11:3A:G7",
            "001B44113AB7",
            "00:1B:44:11:3A:B",
            "not-a-mac"
        };

        for (String mac : invalidMacs) {
            MacDTO dto = new MacDTO(mac);
            Set<ConstraintViolation<MacDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "MAC address '" + mac + "' should be invalid");
        }
    }
}
