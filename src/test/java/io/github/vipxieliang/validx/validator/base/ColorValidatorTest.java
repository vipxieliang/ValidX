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

import io.github.vipxieliang.validx.annotations.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * ColorValidator 测试类
 * 测试HEX颜色格式验证功能
 * </p>
 *
 * @author vipxieliang
 * @since 2025/10/13
 */
public class ColorValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidHexColors() {
        TestEntity entity = new TestEntity();

        // 测试6位十六进制颜色
        entity.setColor("#FF0000");  // 红色
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#FF0000 应该是有效的HEX颜色");

        entity.setColor("#00FF00");  // 绿色
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#00FF00 应该是有效的HEX颜色");

        entity.setColor("#0000FF");  // 蓝色
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#0000FF 应该是有效的HEX颜色");

        entity.setColor("#ffffff");  // 白色小写
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#ffffff 应该是有效的HEX颜色");

        entity.setColor("#000000");  // 黑色
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#000000 应该是有效的HEX颜色");

        // 测试3位十六进制颜色
        entity.setColor("#F00");  // 红色简写
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#F00 应该是有效的HEX颜色");

        entity.setColor("#0F0");  // 绿色简写
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#0F0 应该是有效的HEX颜色");

        entity.setColor("#00F");  // 蓝色简写
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#00F 应该是有效的HEX颜色");

        entity.setColor("#fff");  // 白色简写小写
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#fff 应该是有效的HEX颜色");

        entity.setColor("#000");  // 黑色简写
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#000 应该是有效的HEX颜色");
    }

    @Test
    public void testInvalidHexColors() {
        TestEntity entity = new TestEntity();

        // 测试无效的颜色值
        entity.setColor("FF0000");  // 缺少#
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty(), "FF0000 应该是无效的HEX颜色");

        entity.setColor("#GG0000");  // 包含非法字符
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty(), "#GG0000 应该是无效的HEX颜色");

        entity.setColor("#FF000");  // 5位数字
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty(), "#FF000 应该是无效的HEX颜色");

        entity.setColor("#FF00000");  // 7位数字
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty(), "#FF00000 应该是无效的HEX颜色");

        entity.setColor("#FF0");  // 3位数字实际上是有效的
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "#FF0 应该是有效的HEX颜色");

        entity.setColor("#FF000000");  // 8位数字
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty(), "#FF000000 应该是无效的HEX颜色");

        entity.setColor("#");  // 只有#
        violations = validator.validate(entity);
        assertFalse(violations.isEmpty(), "# 应该是无效的HEX颜色");
    }

    @Test
    public void testNullAndEmptyValues() {
        TestEntity entity = new TestEntity();

        // 测试null值应该通过验证
        entity.setColor(null);
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "null值应该是有效的（可以配合@NotNull使用）");

        // 测试空字符串应该通过验证
        entity.setColor("");
        violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "空字符串应该是有效的（可以配合@NotEmpty使用）");
    }

    @Test
    public void testNullValue() {
        TestEntity entity = new TestEntity();
        entity.setColor(null);  // null值应该被允许（除非使用@NotNull）

        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "null 值应该是有效的（可以配合@NotNull使用）");
    }

    @Test
    public void testNotNullWithNullValue() {
        TestEntityWithNotNull entity = new TestEntityWithNotNull();
        entity.setColor(null);  // null值不应该被允许

        Set<ConstraintViolation<TestEntityWithNotNull>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty(), "当使用@NotNull时，null 值应该是无效的");
    }

    /**
     * 测试实体类
     */
    public static class TestEntity {
        @Color
        private String color;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    /**
     * 测试实体类（带@NotNull注解）
     */
    public static class TestEntityWithNotNull {
        @Color
        @NotNull
        private String color;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}