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


import io.github.vipxieliang.validx.annotations.WorkCopyright;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WorkCopyrightValidatorTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestModel {
        @WorkCopyright
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
    public void testValidWorkCopyright() {
        // 测试有效的著作权登记号
        TestModel model = new TestModel("作登字22-2023-A-0018号");
        Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的著作权登记号应该通过验证");

        // 测试最短地区编号
        model = new TestModel("作登字1-2023-A-1号");
        violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的著作权登记号应该通过验证");

        // 测试最长地区编号和顺序号
        model = new TestModel("作登字1234-2023-A-123456号");
        violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的著作权登记号应该通过验证");
    }

    @Test
    public void testInvalidWorkCopyright() {
        // 测试缺少"号"后缀
        TestModel model = new TestModel("作登字22-2023-A-0018");
        Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "缺少'号'后缀应该验证失败");

        // 测试年份格式不正确
        model = new TestModel("作登字22-23-A-0018号");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "年份格式不正确应该验证失败");

        // 测试地区编号过长
        model = new TestModel("作登字12345-2023-A-0018号");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "地区编号过长应该验证失败");

        // 测试顺序号过长
        model = new TestModel("作登字22-2023-A-1234567号");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "顺序号过长应该验证失败");

        // 测试作品分类号格式不正确
        model = new TestModel("作登字22-2023-1-0018号");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "作品分类号格式不正确应该验证失败");
    }

    @Test
    public void testNullAndEmptyWorkCopyright() {
        // 直接测试验证器，null 和空字符串应该返回 true
        WorkCopyrightValidator copyrightValidator = new WorkCopyrightValidator();
        assertTrue(copyrightValidator.isValid(null, null), "null should return true");
        assertTrue(copyrightValidator.isValid("", null), "empty string should return true");
    }
}