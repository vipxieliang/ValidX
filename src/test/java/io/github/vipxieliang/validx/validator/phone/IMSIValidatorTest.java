/*
 * Copyright 2025-2026 vipxieliang
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

import io.github.vipxieliang.validx.annotations.IMSI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class IMSIValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class IMSIDTO {
        @IMSI
        private String imsi;

        public IMSIDTO(String imsi) {
            this.imsi = imsi;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        IMSIDTO nullDto = new IMSIDTO(null);
        Set<ConstraintViolation<IMSIDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        IMSIDTO emptyDto = new IMSIDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidIMSIs() {
        String[] validIMSIs = {
            "460001234567890",          // 中国移动：MCC 460 + MNC 00 + MSIN 10 位
            "460031234567890",          // 中国电信：MCC 460 + MNC 03
            "310260123456789",          // 美国 AT&T：MCC 310 + MNC 260
            "46001123456789",           // 14 位：MNC 2 位 + MSIN 9 位
            "4600-0123-4567-890",       // 带连字符分隔
            "460 001 234 567 890"       // 带空格分隔
        };

        for (String imsi : validIMSIs) {
            IMSIDTO dto = new IMSIDTO(imsi);
            Set<ConstraintViolation<IMSIDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "IMSI '" + imsi + "' should be valid");
        }
    }

    @Test
    public void testInvalidIMSIs() {
        String[] invalidIMSIs = {
            "4600012345678",            // 13 位，过短
            "4600012345678901",         // 16 位，过长
            "46000123456789a",          // 含字母
            "4600-0123-4567-89A",       // 含字母
            "4600012345678X0",          // 含字母
            "\uFF14\uFF16\uFF10\uFF10\uFF10\uFF11\uFF12\uFF13\uFF14\uFF15\uFF16\uFF17\uFF18\uFF19\uFF10" // 全角数字
        };

        for (String imsi : invalidIMSIs) {
            IMSIDTO dto = new IMSIDTO(imsi);
            Set<ConstraintViolation<IMSIDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "IMSI '" + imsi + "' should be invalid");
        }
    }
}
