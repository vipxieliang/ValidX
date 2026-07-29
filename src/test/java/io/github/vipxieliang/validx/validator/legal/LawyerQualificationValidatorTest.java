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

package io.github.vipxieliang.validx.validator.legal;

import io.github.vipxieliang.validx.annotations.Lawyer;
import io.github.vipxieliang.validx.validator.china.LawyerValidator;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LawyerQualificationValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @Lawyer
        private String certificateNumber;

        public TestEntity(String certificateNumber) {
            this.certificateNumber = certificateNumber;
        }

        public String getCertificateNumber() {
            return certificateNumber;
        }
    }

    @Test
    public void testLawyerQualificationValidatorDirect() {
        // 直接测试验证器的逻辑
        LawyerValidator validator = new LawyerValidator();

        // 测试有效的法律职业资格证书编号
        assertTrue(validator.isValid("2010130103210001", null), "有效的法律职业资格证书编号应该通过验证: 2010130103210001");
        assertTrue(validator.isValid("2015370881000001", null), "有效的法律职业资格证书编号应该通过验证: 2015370881000001");
        
        // 测试有效的律师执业证编号
        assertTrue(validator.isValid("11101201810123456", null), "有效的律师执业证编号应该通过验证: 11101201810123456");
        assertTrue(validator.isValid("13201202011012345", null), "有效的律师执业证编号应该通过验证: 13201202011012345");
    }

    @Test
    public void testValidLawyerQualifications() {
        // 测试有效的法律职业资格证书编号
        TestEntity entity1 = new TestEntity("2010130103210001");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的法律职业资格证书编号应该通过验证: 2010130103210001");

        TestEntity entity2 = new TestEntity("2015370881000001");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的法律职业资格证书编号应该通过验证: 2015370881000001");
        
        // 测试有效的律师执业证编号
        TestEntity entity3 = new TestEntity("11101201810123456");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的律师执业证编号应该通过验证: 11101201810123456");
        
        TestEntity entity4 = new TestEntity("13201202011012345");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "有效的律师执业证编号应该通过验证: 13201202011012345");
    }

    @Test
    public void testInvalidLawyerQualifications() {
        // 测试无效的证书编号（长度不足）
        TestEntity entity1 = new TestEntity("201013010321000");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "长度不足的证书编号应该不通过验证: 201013010321000");

        // 测试无效的证书编号（长度过长）
        TestEntity entity2 = new TestEntity("20101301032100011");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "长度过长的证书编号应该不通过验证: 20101301032100011");

        // 测试无效的证书编号（包含字母）
        TestEntity entity3 = new TestEntity("201013010321000A");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "包含非法字母的证书编号应该不通过验证: 201013010321000A");

        // 测试无效的律师执业证编号（不以1开头）
        TestEntity entity4 = new TestEntity("21101201810123456");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "不以1开头的律师执业证编号应该不通过验证: 21101201810123456");
        
        // 测试无效的律师执业证编号（长度不足）
        TestEntity entity5 = new TestEntity("111012018101234567");
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertFalse(violations5.isEmpty(), "长度过长的律师执业证编号应该不通过验证: 111012018101234567");
    }

    @Test
    public void testNullLawyerQualification() {
        // 测试null值
        // 注意：新行为中，LawyerValidator对null返回true，空值校验由@NotNull注解处理
        TestEntity entity = new TestEntity(null);
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "null值现在由LawyerValidator返回true（空值由@NotNull处理）");
    }

    @Test
    public void testEmptyLawyerQualification() {
        // 测试空字符串
        // 注意：新行为中，LawyerValidator对空字符串返回true，空值校验由@NotNull或@NotEmpty注解处理
        TestEntity entity = new TestEntity("");
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "空字符串现在由LawyerValidator返回true（空值由@NotNull处理）");
    }
}