/*
 * Copyright 2026-2026 vipxieliang
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

import io.github.vipxieliang.validx.annotations.ChineseName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 中国人姓名验证器测试
 *
 * @author vipxieliang
 */
class ChineseNameValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 测试有效的中国人姓名
     */
    @Test
    void testValidChineseNames() {
        TestEntity entity = new TestEntity();

        // 常见姓名
        entity.name = "张三";
        assertTrue(validator.validate(entity).isEmpty());

        entity.name = "李四";
        assertTrue(validator.validate(entity).isEmpty());

        entity.name = "王五";
        assertTrue(validator.validate(entity).isEmpty());

        // 三个字的姓名
        entity.name = "欧阳修";
        assertTrue(validator.validate(entity).isEmpty());

        entity.name = "诸葛亮";
        assertTrue(validator.validate(entity).isEmpty());

        // 四个字的姓名
        entity.name = "爱新觉罗";
        assertTrue(validator.validate(entity).isEmpty());

        // 较长的姓名
        entity.name = "爱新觉罗·玄烨";
        assertTrue(validator.validate(entity).isEmpty());
    }

    /**
     * 测试少数民族姓名（带间隔号）
     */
    @Test
    void testMinorityNames() {
        TestEntity entity = new TestEntity();

        entity.name = "买买提·吐尔逊";
        assertTrue(validator.validate(entity).isEmpty());

        entity.name = "迪丽热巴·迪力木拉提";
        assertTrue(validator.validate(entity).isEmpty());

        entity.name = "古丽·艾合买提";
        assertTrue(validator.validate(entity).isEmpty());
    }

    /**
     * 测试无效的姓名 - 包含数字
     */
    @Test
    void testInvalidWithNumbers() {
        TestEntity entity = new TestEntity();
        entity.name = "张三123";

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
    }

    /**
     * 测试无效的姓名 - 包含字母
     */
    @Test
    void testInvalidWithLetters() {
        TestEntity entity = new TestEntity();
        entity.name = "张三ABC";

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
    }

    /**
     * 测试无效的姓名 - 包含特殊字符
     */
    @Test
    void testInvalidWithSpecialChars() {
        TestEntity entity = new TestEntity();
        entity.name = "张三@#";

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());

        entity.name = "张-三";
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
    }

    /**
     * 测试长度限制 - 太短
     */
    @Test
    void testTooShort() {
        TestEntity entity = new TestEntity();
        entity.name = "张"; // 只有1个字

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
    }

    /**
     * 测试长度限制 - 太长
     */
    @Test
    void testTooLong() {
        TestEntity entity = new TestEntity();
        // 超过50个字
        entity.name = "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一";

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
    }

    /**
     * 测试极长的少数民族姓名（在50字以内）
     */
    @Test
    void testVeryLongMinorityName() {
        TestEntity entity = new TestEntity();

        // 30个字的藏族宗教名（在限制内）
        entity.name = "罗桑丹增嘉措扎西顿珠强巴洛桑丹增嘉措扎西顿珠强巴洛桑";
        assertTrue(validator.validate(entity).isEmpty());

        // 带间隔号的长名字
        entity.name = "阿不都热西提·买买提明·艾则孜·吐尔逊";
        assertTrue(validator.validate(entity).isEmpty());
    }

    /**
     * 测试 null 值
     */
    @Test
    void testNullValue() {
        TestEntity entity = new TestEntity();
        entity.name = null;

        // null 值应该被认为是有效的（使用 @NotNull 来验证非空）
        assertTrue(validator.validate(entity).isEmpty());
    }

    /**
     * 测试空字符串
     */
    @Test
    void testEmptyString() {
        TestEntity entity = new TestEntity();
        entity.name = "";

        // 空字符串应该被认为是有效的（使用 @NotBlank 来验证非空）
        assertTrue(validator.validate(entity).isEmpty());
    }

    /**
     * 测试间隔号位置错误
     */
    @Test
    void testInvalidDotPosition() {
        TestEntity entity = new TestEntity();

        // 间隔号在开头
        entity.name = "·张三";
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());

        // 间隔号在结尾
        entity.name = "张三·";
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty());

        // 连续的间隔号
        entity.name = "张··三";
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
    }

    // 测试实体类
    static class TestEntity {
        @ChineseName
        private String name;
    }
}
