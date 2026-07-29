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


import io.github.vipxieliang.validx.annotations.ChineseTrademark;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChineseTrademarkValidatorTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestModel {
        @ChineseTrademark
        @NotNull
        private String trademarkNumber;

        public TestModel(String trademarkNumber) {
            this.trademarkNumber = trademarkNumber;
        }

        public String getTrademarkNumber() {
            return trademarkNumber;
        }

        public void setTrademarkNumber(String trademarkNumber) {
            this.trademarkNumber = trademarkNumber;
        }
    }

    @Test
    public void testValidTrademarkNumbers() {
        // 测试有效的商标注册号
        String[] validTrademarks = {
            "1234567",      // 7位数字
            "12345678",     // 8位数字
            "123456789",    // 9位数字
            "第1234567号",   // 完整格式7位
            "第12345678号",  // 完整格式8位
            "第123456789号"  // 完整格式9位
        };

        for (String trademark : validTrademarks) {
            TestModel model = new TestModel(trademark);
            Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
            assertEquals(0, violations.size(), "应该通过验证: " + trademark);
        }
    }

    @Test
    public void testInvalidTrademarkNumbers() {
        // 测试无效的商标注册号
        String[] invalidTrademarks = {
            "123456",          // 6位数字，太短
            "1234567890",      // 10位数字，太长
            "第123456号",       // 完整格式但只有6位数字
            "第1234567890号",   // 完整格式但有10位数字
            "ABC123456",       // 包含字母
            "123456A",         // 包含字母
            "第ABC123456号",    // 包含字母
            "1234567 ",        // 包含空格
            " 12345678",       // 包含空格
            "第 1234567号",     // 包含空格
            "第1234567 号"     // 包含空格
        };

        for (String trademark : invalidTrademarks) {
            TestModel model = new TestModel(trademark);
            Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
            assertTrue(violations.size() > 0, "应该验证失败: " + trademark);
        }
    }

    @Test
    public void testNullAndEmptyTrademarkNumbers() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ChineseTrademarkValidator trademarkValidator = new ChineseTrademarkValidator();
        assertTrue(trademarkValidator.isValid(null, null), "null should return true");
        assertTrue(trademarkValidator.isValid("", null), "empty string should return true");
    }
}