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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Age链式验证测试类
 */
public class AgeValidationChainTest {

    // === LocalDate类型测试 ===

    @Test
    public void testValidAge_LocalDate_Adult() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(25);  // 25岁
        validator.isAge(birthDate, 18, 65);

        assertTrue(validator.passed(), "25岁应该通过18-65岁的验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidAge_LocalDate_MinAge() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(18);
        validator.isAge(birthDate, 18, 65);

        assertTrue(validator.passed(), "刚好18岁应该通过验证");
    }

    @Test
    public void testValidAge_LocalDate_MaxAge() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(65);
        validator.isAge(birthDate, 18, 65);

        assertTrue(validator.passed(), "刚好65岁应该通过验证");
    }

    @Test
    public void testInvalidAge_LocalDate_TooYoung() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(17);
        validator.isAge(birthDate, 18, 65);

        assertFalse(validator.passed(), "17岁应该验证失败");
        List<String> errors = validator.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("年龄") || errors.get(0).contains("Age"),
                  "错误信息应该包含年龄相关内容");
    }

    @Test
    public void testInvalidAge_LocalDate_TooOld() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(66);
        validator.isAge(birthDate, 18, 65);

        assertFalse(validator.passed(), "66岁应该验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    // === String类型（日期字符串）测试 ===

    @Test
    public void testValidAge_String_StandardFormat() {
        ValidaX validator = ValidaX.init();
        validator.isAge("1990-01-01", 18, 65);

        assertTrue(validator.passed(), "有效日期字符串应该通过验证");
    }

    @Test
    public void testValidAge_String_CustomFormat() {
        ValidaX validator = ValidaX.init();
        validator.isAge("1990/06/15", 18, 65, false, "yyyy/MM/dd");

        assertTrue(validator.passed(), "自定义格式日期应该通过验证");
    }

    @Test
    public void testInvalidAge_String_InvalidFormat() {
        ValidaX validator = ValidaX.init();
        validator.isAge("1990-13-32", 18, 65);

        assertFalse(validator.passed(), "无效日期格式应该验证失败");
    }

    // === 身份证号码测试 ===

    @Test
    public void testValidAge_IdCard18() {
        ValidaX validator = ValidaX.init();
        validator.isAge("11010119900101001X", 18, 65, true);

        assertTrue(validator.passed(), "有效的18位身份证号应该通过验证");
    }

    @Test
    public void testValidAge_IdCard15() {
        ValidaX validator = ValidaX.init();
        validator.isAge("110101800101001", 18, 65, true);

        assertTrue(validator.passed(), "有效的15位身份证号应该通过验证");
    }

    @Test
    public void testInvalidAge_IdCard_TooYoung() {
        ValidaX validator = ValidaX.init();
        int currentYear = LocalDate.now().getYear();
        int birthYear = currentYear - 10;  // 10岁
        String idCard = String.format("110101%d0101001X", birthYear);
        validator.isAge(idCard, 18, 65, true);

        assertFalse(validator.passed(), "年龄不足18岁的身份证应该验证失败");
    }

    @Test
    public void testInvalidAge_IdCard_InvalidFormat() {
        ValidaX validator = ValidaX.init();
        validator.isAge("invalid-id-card", 18, 65, true);

        assertFalse(validator.passed(), "无效的身份证格式应该验证失败");
    }

    // === 只验证最小年龄测试 ===

    @Test
    public void testValidAge_MinOnly() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(30);
        validator.isAge(birthDate, 18);

        assertTrue(validator.passed(), "30岁应该通过最小年龄18岁的验证");
    }

    @Test
    public void testValidAge_MinOnly_VeryOld() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(100);
        validator.isAge(birthDate, 18);

        assertTrue(validator.passed(), "只设置最小年龄时，年龄上限不受限");
    }

    @Test
    public void testInvalidAge_MinOnly_TooYoung() {
        ValidaX validator = ValidaX.init();
        LocalDate birthDate = LocalDate.now().minusYears(16);
        validator.isAge(birthDate, 18);

        assertFalse(validator.passed(), "16岁应该不满足最小年龄18岁");
    }

    // === 空值测试 ===

    @Test
    public void testNullAge() {
        ValidaX validator = ValidaX.init();
        validator.isAge(null, 18, 65);

        assertTrue(validator.passed(), "null值应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testEmptyStringAge() {
        ValidaX validator = ValidaX.init();
        validator.isAge("", 18, 65);

        assertTrue(validator.passed(), "空字符串应该通过验证");
    }

    // === 链式调用测试 ===

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidaX validator = ValidaX.init();
        validator.isAge(LocalDate.now().minusYears(25), 18, 65)
                .isAge(LocalDate.now().minusYears(30), 18, 65)
                .isAge("1995-01-01", 18, 65);

        assertTrue(validator.passed(), "多个有效年龄应该全部通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidaX validator = ValidaX.init();
        validator.isAge(LocalDate.now().minusYears(25), 18, 65)
                .isAge(LocalDate.now().minusYears(17), 18, 65)  // 失败
                .isAge(LocalDate.now().minusYears(30), 18, 65);

        assertFalse(validator.passed(), "一个无效年龄应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_MixedTypes() {
        ValidaX validator = ValidaX.init();
        validator.isAge(LocalDate.now().minusYears(25), 18, 65)
                .isAge("1990-01-01", 18, 65)
                .isAge("11010119850101001X", 18, 65, true);

        assertTrue(validator.passed(), "混合类型验证应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_AllFailing() {
        ValidaX validator = ValidaX.init();
        validator.isAge(LocalDate.now().minusYears(17), 18, 65)
                .isAge(LocalDate.now().minusYears(16), 18, 65)
                .isAge(LocalDate.now().minusYears(15), 18, 65);

        assertFalse(validator.passed(), "多个无效年龄应该全部验证失败");
        assertEquals(3, validator.getErrors().size());
    }

    // === 与其他验证混合测试 ===

    @Test
    public void testMixedValidation_AgeAndEmail() {
        ValidaX validator = ValidaX.init();
        validator.isAge(LocalDate.now().minusYears(25), 18, 65)
                .isEmail("test@example.com");

        assertTrue(validator.passed(), "年龄和Email验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testMixedValidation_InvalidAge_ValidEmail() {
        ValidaX validator = ValidaX.init();
        validator.isAge(LocalDate.now().minusYears(17), 18, 65)
                .isEmail("test@example.com");

        assertFalse(validator.passed(), "无效年龄应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testMixedValidation_AgeAndIdCard() {
        ValidaX validator = ValidaX.init();
        validator.isAge("110101199003072113", 18, 65, true)
                .isChineseIdCard("110101199003072113");

        assertTrue(validator.passed(), "年龄和身份证验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    // === 实际应用场景测试 ===

    @Test
    public void testRealWorld_UserRegistration() {
        ValidaX validator = ValidaX.init();
        // 用户注册：验证年龄必须>=18岁
        validator.isAge("2000-05-15", 18);

        assertTrue(validator.passed(), "成年用户注册应该通过验证");
    }

    @Test
    public void testRealWorld_SeniorDiscount() {
        ValidaX validator = ValidaX.init();
        // 老年优惠：验证年龄>=60岁
        LocalDate birthDate = LocalDate.now().minusYears(65);
        validator.isAge(birthDate, 60);

        assertTrue(validator.passed(), "老年用户应该通过优惠资格验证");
    }

    @Test
    public void testRealWorld_ChildTicket() {
        ValidaX validator = ValidaX.init();
        // 儿童票：验证年龄<=12岁
        LocalDate birthDate = LocalDate.now().minusYears(8);
        validator.isAge(birthDate, 0, 12);

        assertTrue(validator.passed(), "儿童应该通过儿童票资格验证");
    }

    @Test
    public void testRealWorld_IdCardVerification() {
        ValidaX validator = ValidaX.init();
        // 身份证实名验证：从身份证提取年龄并验证
        validator.isAge("11010119851015001X", 18, 100, true);

        assertTrue(validator.passed(), "有效身份证应该通过实名验证");
    }
}
