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

import io.github.vipxieliang.validx.annotations.ICCID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ICCIDValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class ICCIDDTO {
        @ICCID
        private String iccid;

        public ICCIDDTO(String iccid) {
            this.iccid = iccid;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        ICCIDDTO nullDto = new ICCIDDTO(null);
        Set<ConstraintViolation<ICCIDDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        ICCIDDTO emptyDto = new ICCIDDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidICCIDs() {
        String[] validICCIDs = {
            "89866604876475938248",          // 20 位，8986(89 电信 + 86 中国) 开头，Luhn 通过
            "89862914177763170667",          // 20 位，Luhn 通过
            "89860115244939092661",          // 20 位，Luhn 通过
            "89863982597919074833",          // 20 位，Luhn 通过
            "89863416721106840386",          // 20 位，Luhn 通过
            "8986-0115-2449-3909-2661",      // 带连字符分隔
            "8986 0115 2449 3909 2661"       // 带空格分隔
        };

        for (String iccid : validICCIDs) {
            ICCIDDTO dto = new ICCIDDTO(iccid);
            Set<ConstraintViolation<ICCIDDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "ICCID '" + iccid + "' should be valid");
        }
    }

    @Test
    public void testInvalidICCIDs() {
        String[] invalidICCIDs = {
            "8986011524493909266",           // 19 位，过短
            "898601152449390926611",         // 21 位，过长
            "8986011524493909266X",          // 含字母
            "89860115244939092662",          // 破坏 Luhn 校验位（末位 1→2）
            "8986-0115-2449-3909-26A1",      // 含字母
            "8986011524493909266123",        // 22 位，过长
            "\uFF18\uFF19\uFF18\uFF16\uFF10\uFF11\uFF11\uFF15\uFF12\uFF14\uFF14\uFF19\uFF13\uFF19\uFF10\uFF19\uFF12\uFF16\uFF16\uFF11" // 全角数字
        };

        for (String iccid : invalidICCIDs) {
            ICCIDDTO dto = new ICCIDDTO(iccid);
            Set<ConstraintViolation<ICCIDDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "ICCID '" + iccid + "' should be invalid");
        }
    }
}
