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


import io.github.vipxieliang.validx.annotations.ChinesePatent;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChinesePatentValidatorTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestModel {
        @ChinesePatent
        @NotNull
        private String patentNumber;

        public TestModel(String patentNumber) {
            this.patentNumber = patentNumber;
        }

        public String getPatentNumber() {
            return patentNumber;
        }

        public void setPatentNumber(String patentNumber) {
            this.patentNumber = patentNumber;
        }
    }

    @Test
    public void testValidPatentNumbers() {
        // 测试有效的专利号 (根据校验算法构造正确的测试数据)
        String[] validPatents = {
            "ZL2013106997442", // 发明专利，校验位为2
            "ZL2017216075789", // 实用新型专利，校验位为9
            "ZL2015301234562"  // 外观设计专利，校验位为2
        };

        for (String patent : validPatents) {
            TestModel model = new TestModel(patent);
            Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
            assertEquals(0, violations.size(), "应该通过验证: " + patent);
        }
    }

    @Test
    public void testInvalidPatentNumbers() {
        // 测试无效的专利号
        String[] invalidPatents = {
            "ZL2013106997449",      // 错误的校验位
            "ZL201310699744",       // 缺少校验位
            "ZL20131069974421",     // 多了一位
            "ZL2013A06997442",      // 包含非数字字符
            "ZL201306997442",       // 缺少年份位数
            "zl2013106997442",      // 小写zl
            "2013106997442",        // 缺少ZL前缀
            "ZL2013506997442"       // 无效的专利类型(5)
        };

        for (String patent : invalidPatents) {
            TestModel model = new TestModel(patent);
            Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
            assertTrue(violations.size() > 0, "应该验证失败: " + patent);
        }
    }

    @Test
    public void testNullAndEmptyPatentNumbers() {
        // 直接测试验证器，null 和空字符串应该返回 true
        ChinesePatentValidator patentValidator = new ChinesePatentValidator();
        assertTrue(patentValidator.isValid(null, null), "null should return true");
        assertTrue(patentValidator.isValid("", null), "empty string should return true");
    }

    @Test
    public void testPatentTypes() {
        // 测试各种专利类型
        String[] correctedTypes = {
            "ZL2013106997442", // 发明专利(1) - 校验位2
            "ZL2013206997448", // 实用新型(2) - 校验位8
            "ZL2013306997443", // 外观设计(3) - 校验位3
            "ZL2013806997440", // PCT发明专利(8) - 校验位0
            "ZL2013906997446"  // PCT实用新型(9) - 校验位6
        };

        for (String patent : correctedTypes) {
            TestModel model = new TestModel(patent);
            Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
            assertEquals(0, violations.size(), "应该通过验证: " + patent);
        }
    }
}