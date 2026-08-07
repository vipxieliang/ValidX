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

import io.github.vipxieliang.validx.annotations.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 @DateTime 注解对于格式不匹配的处理
 *
 * 边界场景：pattern 包含时间格式，但输入值只有日期部分
 *
 * @author vipxieliang
 * @since 1.1.0
 */
class DateTimeFormatMismatchTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class TestEntity {
        @DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
        private String dateTimeField;

        public void setDateTimeField(String dateTimeField) {
            this.dateTimeField = dateTimeField;
        }
    }

    @Test
    void testDateTimePattern_WithDateOnly_ShouldFail() {
        // pattern 是 yyyy-MM-dd HH:mm:ss，但输入值只有日期部分
        TestEntity entity = new TestEntity();
        entity.setDateTimeField("2026-01-01");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        // 应该验证失败，因为缺少时间部分
        assertFalse(violations.isEmpty(), "pattern包含时间，输入只有日期应该验证失败");
        assertEquals(1, violations.size());

        System.out.println("验证结果：失败");
        System.out.println("错误信息：" + violations.iterator().next().getMessage());
    }

    @Test
    void testDateTimePattern_WithCompleteDateTime_ShouldPass() {
        // pattern 和输入都是完整的日期时间
        TestEntity entity = new TestEntity();
        entity.setDateTimeField("2026-01-01 12:30:45");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        // 应该验证通过
        assertTrue(violations.isEmpty(), "完整的日期时间应该验证通过");

        System.out.println("验证结果：通过");
    }

    @Test
    void testDateTimePattern_WithPartialTime_ShouldFail() {
        // pattern 是 yyyy-MM-dd HH:mm:ss，但输入只有部分时间
        TestEntity entity = new TestEntity();
        entity.setDateTimeField("2026-01-01 12:30");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        assertFalse(violations.isEmpty(), "缺少秒部分应该验证失败");

        System.out.println("验证结果：失败");
        System.out.println("错误信息：" + violations.iterator().next().getMessage());
    }

    @Test
    void testDateTimePattern_WithDateAndExtraText_ShouldFail() {
        // pattern 是 yyyy-MM-dd HH:mm:ss，但输入有额外字符
        TestEntity entity = new TestEntity();
        entity.setDateTimeField("2026-01-01 abc");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        assertFalse(violations.isEmpty(), "日期后跟非法字符应该验证失败");

        System.out.println("验证结果：失败");
        System.out.println("错误信息：" + violations.iterator().next().getMessage());
    }

    @Test
    void testDateTimePattern_WithOnlyTime_ShouldFail() {
        // pattern 是 yyyy-MM-dd HH:mm:ss，但输入只有时间
        TestEntity entity = new TestEntity();
        entity.setDateTimeField("12:30:45");

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        assertFalse(violations.isEmpty(), "只有时间没有日期应该验证失败");

        System.out.println("验证结果：失败");
        System.out.println("错误信息：" + violations.iterator().next().getMessage());
    }
}
