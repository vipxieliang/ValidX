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

import io.github.vipxieliang.validx.annotations.SubnetMask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SubnetMaskValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class SubnetMaskDTO {
        @SubnetMask
        private String subnetMask;

        public SubnetMaskDTO(String subnetMask) {
            this.subnetMask = subnetMask;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        SubnetMaskDTO nullDto = new SubnetMaskDTO(null);
        Set<ConstraintViolation<SubnetMaskDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        SubnetMaskDTO emptyDto = new SubnetMaskDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidSubnetMasks() {
        String[] validMasks = {
            "255.255.255.0",
            "255.255.0.0",
            "255.0.0.0",
            "255.255.255.255",
            "255.255.255.128",
            "255.255.248.0"
        };

        for (String mask : validMasks) {
            SubnetMaskDTO dto = new SubnetMaskDTO(mask);
            Set<ConstraintViolation<SubnetMaskDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "Subnet mask '" + mask + "' should be valid");
        }
    }

    @Test
    public void testInvalidSubnetMasks() {
        String[] invalidMasks = {
            "255.255.255.1",
            "255.255.255.255.255",
            "255.255.255",
            "abc.def.ghi.jkl",
            "256.0.0.0",
            "255.1.255.0"
        };

        for (String mask : invalidMasks) {
            SubnetMaskDTO dto = new SubnetMaskDTO(mask);
            Set<ConstraintViolation<SubnetMaskDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "Subnet mask '" + mask + "' should be invalid");
        }
    }
}
