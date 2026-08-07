/*
 * Copyright 2025-2026 vipxieliang
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

import io.github.vipxieliang.validx.annotations.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 @Date 注解对于格式不匹配的处理
 *
 * 边界场景：pattern 是纯日期格式，但输入值包含时间部分
 *
 * @author vipxieliang
 * @since 1.1.0
 */
class DateFormatMismatchTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class TestEntity {
        @Date(pattern = "yyyy-MM-dd")
        private String dateField;

        public void setDateField(String dateField) {
            this.dateField = dateField;
        }
    }

    @Test
    void testDatePattern_WithTimeInValue_ShouldFail() {
        // pattern 是 yyyy-MM-dd，但输入值包含时间部分
        TestEntity entity = new TestEntity();
        entity.setDateField("2026-02-01 11:11:11");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        // 应该验证失败，因为输入格式不匹配
        assertFalse(violations.isEmpty(), "pattern是yyyy-MM-dd，输入包含时间部分应该验证失败");
        assertEquals(1, violations.size());

        System.out.println("验证结果：失败");
        System.out.println("错误信息：" + violations.iterator().next().getMessage());
    }

    @Test
    void testDatePattern_WithExactMatch_ShouldPass() {
        // pattern 是 yyyy-MM-dd，输入值也是纯日期
        TestEntity entity = new TestEntity();
        entity.setDateField("2026-02-01");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        // 应该验证通过
        assertTrue(violations.isEmpty(), "pattern和输入都是yyyy-MM-dd应该验证通过");

        System.out.println("验证结果：通过");
    }

    @Test
    void testDatePattern_WithPartialTime_ShouldFail() {
        // pattern 是 yyyy-MM-dd，但输入值包含部分时间
        TestEntity entity = new TestEntity();
        entity.setDateField("2026-02-01 11:11");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        assertFalse(violations.isEmpty(), "输入包含部分时间也应该验证失败");

        System.out.println("验证结果：失败");
        System.out.println("错误信息：" + violations.iterator().next().getMessage());
    }

    @Test
    void testDatePattern_WithExtraCharacters_ShouldFail() {
        // pattern 是 yyyy-MM-dd，但输入值有额外字符
        TestEntity entity = new TestEntity();
        entity.setDateField("2026-02-01 abc");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        assertFalse(violations.isEmpty(), "输入有额外字符应该验证失败");

        System.out.println("验证结果：失败");
        System.out.println("错误信息：" + violations.iterator().next().getMessage());
    }
}
