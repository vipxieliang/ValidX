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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.HourMinuteSecond;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HourMinuteSecondValidator测试类
 */
public class HourMinuteSecondValidatorTest {
    
    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestModel {
        @HourMinuteSecond
        @NotNull
        private String time;

        public TestModel(String time) {
            this.time = time;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    @Test
    public void testValidHourMinuteSecond() {
        // 测试有效的时间格式
        TestModel model = new TestModel("23:50:29");
        Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的时间格式应该通过验证");

        // 测试边界值 - 最小时间
        model = new TestModel("00:00:00");
        violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的时间格式应该通过验证");

        // 测试边界值 - 最大时间
        model = new TestModel("23:59:59");
        violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的时间格式应该通过验证");

        // 测试小时为个位数（需要补零）
        model = new TestModel("09:30:05");
        violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的时间格式应该通过验证");

        // 测试分钟为个位数（需要补零）
        model = new TestModel("12:05:30");
        violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的时间格式应该通过验证");

        // 测试秒为个位数（需要补零）
        model = new TestModel("12:30:05");
        violations = validator.validate(model);
        assertTrue(violations.isEmpty(), "有效的时间格式应该通过验证");
    }

    @Test
    public void testNullAndEmptyValues() {
        // 直接测试验证器，不使用带@NotNull的TestModel
        HourMinuteSecondValidator validator = new HourMinuteSecondValidator();

        // 测试null值应该通过验证
        assertTrue(validator.isValid(null, null), "null值应该通过验证");

        // 测试空字符串应该通过验证
        assertTrue(validator.isValid("", null), "空字符串应该通过验证");
    }

    @Test
    public void testInvalidHourMinuteSecond() {
        // 测试小时超出范围
        TestModel model = new TestModel("24:00:00");
        Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "小时超出范围应该验证失败");

        model = new TestModel("30:15:30");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "小时超出范围应该验证失败");

        // 测试分钟超出范围
        model = new TestModel("12:60:00");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "分钟超出范围应该验证失败");

        model = new TestModel("12:75:30");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "分钟超出范围应该验证失败");

        // 测试秒超出范围
        model = new TestModel("12:30:60");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "秒超出范围应该验证失败");

        model = new TestModel("12:30:75");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "秒超出范围应该验证失败");

        // 测试缺少冒号
        model = new TestModel("123045");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "缺少冒号应该验证失败");

        // 测试只包含一个冒号
        model = new TestModel("12:3045");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "格式不正确应该验证失败");

        model = new TestModel("1230:45");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "格式不正确应该验证失败");

        // 测试包含非数字字符
        model = new TestModel("ab:cd:ef");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "包含非数字字符应该验证失败");

        // 测试格式不正确
        model = new TestModel("12:");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "格式不正确应该验证失败");

        model = new TestModel(":30");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "格式不正确应该验证失败");

        model = new TestModel("12:30:");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "格式不正确应该验证失败");

        model = new TestModel(":12:30");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "格式不正确应该验证失败");
        
        // 测试个位数小时不补零
        model = new TestModel("9:30:25");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "小时为个位数但未补零应该验证失败");
        
        // 测试个位数分钟不补零
        model = new TestModel("12:5:30");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "分钟为个位数但未补零应该验证失败");
        
        // 测试个位数秒不补零
        model = new TestModel("12:30:5");
        violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "秒为个位数但未补零应该验证失败");
    }
}