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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.UnifiedSocialCreditCode;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UnifiedSocialCreditCodeValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @UnifiedSocialCreditCode
        private String code;

        public TestEntity(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    @Test
    public void testUnifiedSocialCreditCodeValidatorDirect() {
        // 直接测试验证器的逻辑
        UnifiedSocialCreditCodeValidator validator = new UnifiedSocialCreditCodeValidator();

        // 测试有效的统一社会信用代码
        assertTrue(validator.isValid("91350100M000100Y43", null));
        // 根据搜索结果，这个代码是有效的：91350100M000100Y43

        // 测试无效的统一社会信用代码
        assertFalse(validator.isValid("91350100M000100Y4", null)); // 长度不足
        assertFalse(validator.isValid("91350100M000100Y433", null)); // 长度过长
        assertFalse(validator.isValid("91350100M000100Y4I", null)); // 包含非法字符I
        assertFalse(validator.isValid("91350100M000100Y4O", null)); // 包含非法字符O
    }

    @Test
    public void testNullAndEmptyUnifiedSocialCreditCode() {
        // 直接测试验证器，null 和空字符串应该返回 true
        UnifiedSocialCreditCodeValidator validator = new UnifiedSocialCreditCodeValidator();
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }

    @Test
    public void testValidUnifiedSocialCreditCodes() {
        // 测试有效的统一社会信用代码
        TestEntity entity1 = new TestEntity("91350100M000100Y43");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的统一社会信用代码应该通过验证: 91350100M000100Y43");
    }

    @Test
    public void testInvalidUnifiedSocialCreditCodes() {
        // 测试无效的统一社会信用代码
        TestEntity entity1 = new TestEntity("91350100M000100Y4"); // 长度不足
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "长度不足的统一社会信用代码不应该通过验证: 91350100M000100Y4");

        TestEntity entity2 = new TestEntity("91350100M000100Y4I"); // 包含非法字符I
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "包含非法字符的统一社会信用代码不应该通过验证: 91350100M000100Y4I");
    }
}