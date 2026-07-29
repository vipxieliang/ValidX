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

import io.github.vipxieliang.validx.annotations.ForeignerWorkPermit;
import io.github.vipxieliang.validx.validator.foreign.ForeignerWorkPermitValidator;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ForeignerWorkPermitValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @ForeignerWorkPermit
        private String permitNumber;

        public TestEntity(String permitNumber) {
            this.permitNumber = permitNumber;
        }

        public String getPermitNumber() {
            return permitNumber;
        }
    }

    @Test
    public void testForeignerWorkPermitValidatorDirect() {
        // 直接测试验证器的逻辑
        ForeignerWorkPermitValidator validator = new ForeignerWorkPermitValidator();

        // 测试有效的外国人工作许可证号码
        assertTrue(validator.isValid("FWP2021001", null), "有效的外国人工作许可证号码应该通过验证: FWP2021001");
        assertTrue(validator.isValid("ABC123456", null), "有效的外国人工作许可证号码应该通过验证: ABC123456");
        assertTrue(validator.isValid("XYZ987654321", null), "有效的外国人工作许可证号码应该通过验证: XYZ987654321");
    }

    @Test
    public void testValidForeignerWorkPermits() {
        // 测试有效的外国人工作许可证号码
        TestEntity entity1 = new TestEntity("FWP2021001");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的外国人工作许可证号码应该通过验证: FWP2021001");

        TestEntity entity2 = new TestEntity("ABC123456");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的外国人工作许可证号码应该通过验证: ABC123456");

        TestEntity entity3 = new TestEntity("XYZ987654321");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的外国人工作许可证号码应该通过验证: XYZ987654321");
    }

    @Test
    public void testInvalidForeignerWorkPermits() {
        // 测试无效的外国人工作许可证号码（包含特殊字符）
        TestEntity entity1 = new TestEntity("FWP2021-001");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "包含特殊字符的外国人工作许可证号码应该不通过验证: FWP2021-001");

        // 测试无效的外国人工作许可证号码（长度不足）
        TestEntity entity2 = new TestEntity("FW12");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "长度不足的外国人工作许可证号码应该不通过验证: FW12");

        // 测试无效的外国人工作许可证号码（包含中文）
        TestEntity entity3 = new TestEntity("工作证123456");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "包含中文的外国人工作许可证号码应该不通过验证: 工作证123456");
    }

    @Test
    public void testNullAndEmptyForeignerWorkPermit() {
        // 测试null值
        TestEntity entity1 = new TestEntity(null);
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "null值应该通过验证");

        // 测试空字符串
        TestEntity entity2 = new TestEntity("");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "空字符串应该通过验证");
    }
}