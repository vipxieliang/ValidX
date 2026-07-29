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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vipxieliang.validx.validator.vehicle;

import io.github.vipxieliang.validx.annotations.VIN;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VINValidatorTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestModel {
        @VIN
        private String vin;

        public TestModel(String vin) {
            this.vin = vin;
        }

        public String getVin() {
            return vin;
        }
    }

    @Test
    public void testValidVin() {
        // 测试有效的VIN码
        TestModel model1 = new TestModel("1M8GDM9AXKP042788");
        Set<ConstraintViolation<TestModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "有效的VIN码应该通过验证: 1M8GDM9AXKP042788");

        TestModel model2 = new TestModel("WP0AJ2972LL122844");
        Set<ConstraintViolation<TestModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "有效的VIN码应该通过验证: WP0AJ2972LL122844");
    }

    @Test
    public void testInvalidVinFormat() {
        // 测试无效的VIN码格式
        TestModel model1 = new TestModel("1M8GDM9AIKP042788"); // 包含I
        Set<ConstraintViolation<TestModel>> violations1 = validator.validate(model1);
        assertFalse(violations1.isEmpty(), "包含I的VIN码应该验证失败");

        TestModel model2 = new TestModel("1M8GDM9AOKP042788"); // 包含O
        Set<ConstraintViolation<TestModel>> violations2 = validator.validate(model2);
        assertFalse(violations2.isEmpty(), "包含O的VIN码应该验证失败");

        TestModel model3 = new TestModel("1M8GDM9AQKP042788"); // 包含Q
        Set<ConstraintViolation<TestModel>> violations3 = validator.validate(model3);
        assertFalse(violations3.isEmpty(), "包含Q的VIN码应该验证失败");

        TestModel model4 = new TestModel("1M8GDM9A KP042788"); // 包含空格
        Set<ConstraintViolation<TestModel>> violations4 = validator.validate(model4);
        assertFalse(violations4.isEmpty(), "包含空格的VIN码应该验证失败");

        TestModel model5 = new TestModel("1M8GDM9AKP042788"); // 长度不足17位
        Set<ConstraintViolation<TestModel>> violations5 = validator.validate(model5);
        assertFalse(violations5.isEmpty(), "长度不足17位的VIN码应该验证失败");

        TestModel model6 = new TestModel("1M8GDM9AKP042788123"); // 长度超过17位
        Set<ConstraintViolation<TestModel>> violations6 = validator.validate(model6);
        assertFalse(violations6.isEmpty(), "长度超过17位的VIN码应该验证失败");
    }

    @Test
    public void testInvalidCheckDigit() {
        // 测试校验位错误的VIN码
        TestModel model = new TestModel("1M8GDM9A0KP042788"); // 校验位错误
        Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
        assertFalse(violations.isEmpty(), "校验位错误的VIN码应该验证失败");
    }

    @Test
    public void testNullAndEmptyVIN() {
        // 测试null和空字符串
        TestModel model1 = new TestModel(null);
        Set<ConstraintViolation<TestModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "null值应该通过验证");

        TestModel model2 = new TestModel("");
        Set<ConstraintViolation<TestModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "空字符串应该通过验证");
    }

    @Test
    public void testVinValidatorDirect() {
        // 直接测试验证器的逻辑
        VINValidator validator = new VINValidator();

        // 测试有效的VIN码
        assertTrue(validator.isValid("1M8GDM9AXKP042788", null), "有效的VIN码应该通过验证: 1M8GDM9AXKP042788");
        assertTrue(validator.isValid("WP0AJ2972LL122844", null), "有效的VIN码应该通过验证: WP0AJ2972LL122844");

        // 测试无效的VIN码
        assertFalse(validator.isValid("1M8GDM9A0KP042788", null), "校验位错误的VIN码应该验证失败");
        assertFalse(validator.isValid("1M8GDM9A IKP042788", null), "包含空格的VIN码应该验证失败");
        assertTrue(validator.isValid(null, null), "null值应该返回true");
        assertTrue(validator.isValid("", null), "空字符串应该返回true");
    }
}