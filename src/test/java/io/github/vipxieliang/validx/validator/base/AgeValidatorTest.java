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

import io.github.vipxieliang.validx.annotations.Age;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Age验证器测试类
 */
public class AgeValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === LocalDate类型测试 ===

    @Test
    public void testValidAge_LocalDate_Adult() {
        TestDTOLocalDate dto = new TestDTOLocalDate();
        dto.birthDate = LocalDate.now().minusYears(25);  // 25岁

        Set<ConstraintViolation<TestDTOLocalDate>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "25岁应该通过18-65岁的验证");
    }

    @Test
    public void testValidAge_LocalDate_MinAge() {
        TestDTOLocalDate dto = new TestDTOLocalDate();
        dto.birthDate = LocalDate.now().minusYears(18);  // 刚好18岁

        Set<ConstraintViolation<TestDTOLocalDate>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "刚好18岁应该通过验证");
    }

    @Test
    public void testValidAge_LocalDate_MaxAge() {
        TestDTOLocalDate dto = new TestDTOLocalDate();
        dto.birthDate = LocalDate.now().minusYears(65);  // 刚好65岁

        Set<ConstraintViolation<TestDTOLocalDate>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "刚好65岁应该通过验证");
    }

    @Test
    public void testInvalidAge_LocalDate_TooYoung() {
        TestDTOLocalDate dto = new TestDTOLocalDate();
        dto.birthDate = LocalDate.now().minusYears(17);  // 17岁

        Set<ConstraintViolation<TestDTOLocalDate>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "17岁应该验证失败");
    }

    @Test
    public void testInvalidAge_LocalDate_TooOld() {
        TestDTOLocalDate dto = new TestDTOLocalDate();
        dto.birthDate = LocalDate.now().minusYears(66);  // 66岁

        Set<ConstraintViolation<TestDTOLocalDate>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "66岁应该验证失败");
    }

    @Test
    public void testInvalidAge_LocalDate_FutureBirthDate() {
        TestDTOLocalDate dto = new TestDTOLocalDate();
        dto.birthDate = LocalDate.now().plusYears(1);  // 未来日期

        Set<ConstraintViolation<TestDTOLocalDate>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "未来出生日期应该验证失败");
    }

    // === String类型（日期字符串）测试 ===

    @Test
    public void testValidAge_String_StandardFormat() {
        TestDTOStringDate dto = new TestDTOStringDate();
        dto.birthDateStr = "1990-01-01";  // 约34岁

        Set<ConstraintViolation<TestDTOStringDate>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效日期字符串应该通过验证");
    }

    @Test
    public void testValidAge_String_SlashFormat() {
        TestDTOStringSlashDate dto = new TestDTOStringSlashDate();
        dto.birthDate = "1990/06/15";  // 约34岁

        Set<ConstraintViolation<TestDTOStringSlashDate>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "斜杠格式日期应该通过验证");
    }

    @Test
    public void testInvalidAge_String_InvalidFormat() {
        TestDTOStringDate dto = new TestDTOStringDate();
        dto.birthDateStr = "1990-13-32";  // 无效日期

        Set<ConstraintViolation<TestDTOStringDate>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "无效日期格式应该验证失败");
    }

    @Test
    public void testInvalidAge_String_WrongFormat() {
        TestDTOStringDate dto = new TestDTOStringDate();
        dto.birthDateStr = "01/01/1990";  // 错误的格式

        Set<ConstraintViolation<TestDTOStringDate>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "错误的日期格式应该验证失败");
    }

    // === 身份证号码测试 ===

    @Test
    public void testValidAge_IdCard18_Adult() {
        TestDTOIdCard dto = new TestDTOIdCard();
        dto.idCard = "11010119900101001X";  // 1990年出生

        Set<ConstraintViolation<TestDTOIdCard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效的18位身份证号应该通过验证");
    }

    @Test
    public void testValidAge_IdCard15_Adult() {
        TestDTOIdCard dto = new TestDTOIdCard();
        dto.idCard = "110101800101001";  // 1980年出生（15位）

        Set<ConstraintViolation<TestDTOIdCard>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效的15位身份证号应该通过验证");
    }

    @Test
    public void testInvalidAge_IdCard_TooYoung() {
        TestDTOIdCard dto = new TestDTOIdCard();
        int currentYear = LocalDate.now().getYear();
        int birthYear = currentYear - 10;  // 10岁
        dto.idCard = String.format("110101%d0101001X", birthYear);

        Set<ConstraintViolation<TestDTOIdCard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "年龄不足18岁的身份证应该验证失败");
    }

    @Test
    public void testInvalidAge_IdCard_InvalidFormat() {
        TestDTOIdCard dto = new TestDTOIdCard();
        dto.idCard = "invalid-id-card";

        Set<ConstraintViolation<TestDTOIdCard>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "无效的身份证格式应该验证失败");
    }

    // === 只验证最小年龄测试 ===

    @Test
    public void testValidAge_MinOnly_Pass() {
        TestDTOMinOnly dto = new TestDTOMinOnly();
        dto.birthDate = LocalDate.now().minusYears(30);

        Set<ConstraintViolation<TestDTOMinOnly>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "30岁应该通过最小年龄18岁的验证");
    }

    @Test
    public void testValidAge_MinOnly_VeryOld() {
        TestDTOMinOnly dto = new TestDTOMinOnly();
        dto.birthDate = LocalDate.now().minusYears(100);

        Set<ConstraintViolation<TestDTOMinOnly>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "只设置最小年龄时，年龄上限不受限");
    }

    @Test
    public void testInvalidAge_MinOnly_TooYoung() {
        TestDTOMinOnly dto = new TestDTOMinOnly();
        dto.birthDate = LocalDate.now().minusYears(16);

        Set<ConstraintViolation<TestDTOMinOnly>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "16岁应该不满足最小年龄18岁");
    }

    // === 只验证最大年龄测试 ===

    @Test
    public void testValidAge_MaxOnly_Pass() {
        TestDTOMaxOnly dto = new TestDTOMaxOnly();
        dto.birthDate = LocalDate.now().minusYears(50);

        Set<ConstraintViolation<TestDTOMaxOnly>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "50岁应该通过最大年龄60岁的验证");
    }

    @Test
    public void testValidAge_MaxOnly_VeryYoung() {
        TestDTOMaxOnly dto = new TestDTOMaxOnly();
        dto.birthDate = LocalDate.now().minusDays(1);

        Set<ConstraintViolation<TestDTOMaxOnly>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "只设置最大年龄时，年龄下限不受限");
    }

    @Test
    public void testInvalidAge_MaxOnly_TooOld() {
        TestDTOMaxOnly dto = new TestDTOMaxOnly();
        dto.birthDate = LocalDate.now().minusYears(61);

        Set<ConstraintViolation<TestDTOMaxOnly>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "61岁应该超过最大年龄60岁");
    }

    // === 空值测试 ===

    @Test
    public void testNullAge() {
        TestDTOLocalDate dto = new TestDTOLocalDate();
        dto.birthDate = null;

        Set<ConstraintViolation<TestDTOLocalDate>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "null值应该通过验证");
    }

    @Test
    public void testEmptyStringAge() {
        TestDTOStringDate dto = new TestDTOStringDate();
        dto.birthDateStr = "";

        Set<ConstraintViolation<TestDTOStringDate>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证");
    }

    // === 测试DTO类 ===

    static class TestDTOLocalDate {
        @Age(min = 18, max = 65)
        LocalDate birthDate;
    }

    static class TestDTOStringDate {
        @Age(min = 18, max = 65)
        String birthDateStr;
    }

    static class TestDTOStringSlashDate {
        @Age(min = 18, max = 65, dateFormat = "yyyy/MM/dd")
        String birthDate;
    }

    static class TestDTOIdCard {
        @Age(min = 18, max = 65, fromIdCard = true)
        String idCard;
    }

    static class TestDTOMinOnly {
        @Age(min = 18)
        LocalDate birthDate;
    }

    static class TestDTOMaxOnly {
        @Age(max = 60)
        LocalDate birthDate;
    }
}
