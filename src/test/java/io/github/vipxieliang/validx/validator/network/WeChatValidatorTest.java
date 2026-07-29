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

import io.github.vipxieliang.validx.annotations.WeChat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WeChatValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class WeChatDTO {
        @WeChat
        private String wechatId;

        public WeChatDTO(String wechatId) {
            this.wechatId = wechatId;
        }
    }

    @Test
    public void testNullAndEmptyString() {
        WeChatDTO nullDto = new WeChatDTO(null);
        Set<ConstraintViolation<WeChatDTO>> violations = validator.validate(nullDto);
        assertEquals(0, violations.size(), "null should pass validation");

        WeChatDTO emptyDto = new WeChatDTO("");
        violations = validator.validate(emptyDto);
        assertEquals(0, violations.size(), "empty string should pass validation");
    }

    @Test
    public void testValidWeChatIds() {
        String[] validIds = {
            "abcdef",
            "a12345",
            "Test_User-123",
            "a1234567890123456789",
            "WeChatID_2024"
        };

        for (String id : validIds) {
            WeChatDTO dto = new WeChatDTO(id);
            Set<ConstraintViolation<WeChatDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "WeChat ID '" + id + "' should be valid");
        }
    }

    @Test
    public void testInvalidWeChatIds() {
        String[] invalidIds = {
            "12345",              // starts with number
            "abc",                // too short
            "a123456789012345678901",  // too long
            "_abcdef",            // starts with underscore
            "-abcdef",            // starts with hyphen
            "测试账号",            // Chinese characters
            "test@user"           // invalid character
        };

        for (String id : invalidIds) {
            WeChatDTO dto = new WeChatDTO(id);
            Set<ConstraintViolation<WeChatDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size(), "WeChat ID '" + id + "' should be invalid");
        }
    }
}
