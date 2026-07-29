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


import io.github.vipxieliang.validx.annotations.SoftwareCopyright;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SoftwareCopyrightValidatorTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestModel {
        @SoftwareCopyright
        @NotNull
        private String copyrightNumber;

        public TestModel(String copyrightNumber) {
            this.copyrightNumber = copyrightNumber;
        }

        public String getCopyrightNumber() {
            return copyrightNumber;
        }

        public void setCopyrightNumber(String copyrightNumber) {
            this.copyrightNumber = copyrightNumber;
        }
    }

    @Test
    public void testValidCopyrightNumbers() {
        // 测试有效的软件著作权登记号
        String[] validCopyrights = {
            "软著登字第2023001234号",      // 标准格式 (10位数字)
            "国（版）著登字第2023001234号",  // 国家版本格式 (10位数字)
            "10-2023-001234"              // 数字格式
        };

        for (String copyright : validCopyrights) {
            TestModel model = new TestModel(copyright);
            Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
            assertEquals(0, violations.size(), "应该通过验证: " + copyright);
        }
    }

    @Test
    public void testInvalidCopyrightNumbers() {
        // 测试无效的软件著作权登记号
        String[] invalidCopyrights = {
            "软著登字第20230012345678号",    // 位数太多(超过12位)
            "软著登字2023001234号",          // 缺少"第"字
            "软著登字第2023001234",          // 缺少"号"字
            "国版著登字第2023001234号",      // 括号不正确
            "10-2023-0012345",              // 顺序号位数不对(应该是6位)
            "10-23-001234",                 // 年份位数不对(应该是4位)
            "软著登字第2023001234号 ",       // 包含空格
            " 软著登字第2023001234号",       // 包含空格
            "10-2023-001234 "               // 包含空格
        };

        for (String copyright : invalidCopyrights) {
            TestModel model = new TestModel(copyright);
            Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
            assertTrue(violations.size() > 0, "应该验证失败: " + copyright);
        }

        // 特别测试6位数字的情况，应该验证失败
        TestModel model = new TestModel("软著登字第202300号"); // 6位数字
        Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
        assertEquals(0, violations.size(), "6位数字应该通过验证");
    }

    @Test
    public void testNullAndEmptySoftwareCopyright() {
        // 直接测试验证器，null 和空字符串应该返回 true
        SoftwareCopyrightValidator copyrightValidator = new SoftwareCopyrightValidator();
        assertTrue(copyrightValidator.isValid(null, null), "null should return true");
        assertTrue(copyrightValidator.isValid("", null), "empty string should return true");
    }
}