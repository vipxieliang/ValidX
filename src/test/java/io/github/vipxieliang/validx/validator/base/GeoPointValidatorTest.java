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

import io.github.vipxieliang.validx.annotations.GeoPoint;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GeoPointValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @GeoPoint
        private String coordinate;

        public TestEntity(String coordinate) {
            this.coordinate = coordinate;
        }
    }

    public static class TestEntityLatitudeFirst {
        @GeoPoint(latitudeFirst = true)
        private String coordinate;

        public TestEntityLatitudeFirst(String coordinate) {
            this.coordinate = coordinate;
        }
    }

    public static class TestEntityCommaOnly {
        @GeoPoint(separator = GeoPoint.SeparatorType.COMMA)
        private String coordinate;

        public TestEntityCommaOnly(String coordinate) {
            this.coordinate = coordinate;
        }
    }

    public static class TestEntitySpaceOnly {
        @GeoPoint(separator = GeoPoint.SeparatorType.SPACE)
        private String coordinate;

        public TestEntitySpaceOnly(String coordinate) {
            this.coordinate = coordinate;
        }
    }

    @Test
    public void testValidGeoPoints() {
        // 测试有效的经纬度坐标（经度在前）
        TestEntity entity1 = new TestEntity("116.4074,39.9042"); // 北京
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "有效的坐标应该通过验证: 116.4074,39.9042");

        TestEntity entity2 = new TestEntity("121.4737,31.2304"); // 上海
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "有效的坐标应该通过验证: 121.4737,31.2304");

        TestEntity entity3 = new TestEntity("-74.0060,40.7128"); // 纽约
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertTrue(violations3.isEmpty(), "有效的坐标应该通过验证: -74.0060,40.7128");

        TestEntity entity4 = new TestEntity("0,0"); // 赤道和本初子午线交点
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertTrue(violations4.isEmpty(), "有效的坐标应该通过验证: 0,0");

        TestEntity entity5 = new TestEntity("180,90"); // 边界值
        Set<ConstraintViolation<TestEntity>> violations5 = validator.validate(entity5);
        assertTrue(violations5.isEmpty(), "有效的坐标应该通过验证: 180,90");

        TestEntity entity6 = new TestEntity("-180,-90"); // 边界值
        Set<ConstraintViolation<TestEntity>> violations6 = validator.validate(entity6);
        assertTrue(violations6.isEmpty(), "有效的坐标应该通过验证: -180,-90");
    }

    @Test
    public void testValidGeoPointsWithSpace() {
        // 测试空格分隔的坐标
        TestEntity entity1 = new TestEntity("116.4074 39.9042");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "空格分隔的坐标应该通过验证");

        TestEntity entity2 = new TestEntity("116.4074  39.9042"); // 多个空格
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "多个空格分隔的坐标应该通过验证");
    }

    @Test
    public void testValidGeoPointsWithCommaAndSpace() {
        // 测试逗号+空格分隔的坐标
        TestEntity entity = new TestEntity("116.4074, 39.9042");
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "逗号+空格分隔的坐标应该通过验证");
    }

    @Test
    public void testLatitudeFirstOrder() {
        // 测试纬度在前的坐标
        TestEntityLatitudeFirst entity1 = new TestEntityLatitudeFirst("39.9042,116.4074"); // 北京（纬度在前）
        Set<ConstraintViolation<TestEntityLatitudeFirst>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "纬度在前的坐标应该通过验证: 39.9042,116.4074");

        TestEntityLatitudeFirst entity2 = new TestEntityLatitudeFirst("40.7128,-74.0060"); // 纽约（纬度在前）
        Set<ConstraintViolation<TestEntityLatitudeFirst>> violations2 = validator.validate(entity2);
        assertTrue(violations2.isEmpty(), "纬度在前的坐标应该通过验证: 40.7128,-74.0060");
    }

    @Test
    public void testInvalidLongitude() {
        // 测试无效的经度（超出范围）
        TestEntity entity1 = new TestEntity("181,39.9042");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "经度超出范围应该不通过验证: 181");

        TestEntity entity2 = new TestEntity("-181,39.9042");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "经度超出范围应该不通过验证: -181");
    }

    @Test
    public void testInvalidLatitude() {
        // 测试无效的纬度（超出范围）
        TestEntity entity1 = new TestEntity("116.4074,91");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "纬度超出范围应该不通过验证: 91");

        TestEntity entity2 = new TestEntity("116.4074,-91");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "纬度超出范围应该不通过验证: -91");
    }

    @Test
    public void testInvalidFormat() {
        // 测试无效的格式
        TestEntity entity1 = new TestEntity("116.4074");
        Set<ConstraintViolation<TestEntity>> violations1 = validator.validate(entity1);
        assertFalse(violations1.isEmpty(), "只有一个数值应该不通过验证");

        TestEntity entity2 = new TestEntity("116.4074,39.9042,100");
        Set<ConstraintViolation<TestEntity>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "三个数值应该不通过验证");

        TestEntity entity3 = new TestEntity("abc,def");
        Set<ConstraintViolation<TestEntity>> violations3 = validator.validate(entity3);
        assertFalse(violations3.isEmpty(), "非数字应该不通过验证");

        TestEntity entity4 = new TestEntity("116.4074,");
        Set<ConstraintViolation<TestEntity>> violations4 = validator.validate(entity4);
        assertFalse(violations4.isEmpty(), "缺少第二个值应该不通过验证");
    }

    @Test
    public void testCommaOnlySeparator() {
        // 测试仅逗号分隔
        TestEntityCommaOnly entity1 = new TestEntityCommaOnly("116.4074,39.9042");
        Set<ConstraintViolation<TestEntityCommaOnly>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "逗号分隔应该通过验证");

        TestEntityCommaOnly entity2 = new TestEntityCommaOnly("116.4074 39.9042");
        Set<ConstraintViolation<TestEntityCommaOnly>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "仅逗号模式下空格分隔应该不通过验证");
    }

    @Test
    public void testSpaceOnlySeparator() {
        // 测试仅空格分隔
        TestEntitySpaceOnly entity1 = new TestEntitySpaceOnly("116.4074 39.9042");
        Set<ConstraintViolation<TestEntitySpaceOnly>> violations1 = validator.validate(entity1);
        assertTrue(violations1.isEmpty(), "空格分隔应该通过验证");

        TestEntitySpaceOnly entity2 = new TestEntitySpaceOnly("116.4074,39.9042");
        Set<ConstraintViolation<TestEntitySpaceOnly>> violations2 = validator.validate(entity2);
        assertFalse(violations2.isEmpty(), "仅空格模式下逗号分隔应该不通过验证");
    }

    @Test
    public void testNullValue() {
        // 测试null值
        TestEntity entity = new TestEntity(null);
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "null值应该通过验证（由@NotNull处理）");
    }

    @Test
    public void testEmptyString() {
        // 测试空字符串
        TestEntity entity = new TestEntity("");
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty(), "空字符串应该通过验证（由@NotEmpty处理）");
    }

    @Test
    public void testDirectValidatorCall() {
        // 直接测试验证器的逻辑
        GeoPointValidator validator = new GeoPointValidator();

        // 测试有效坐标
        assertTrue(GeoPointValidator.isValid("116.4074,39.9042", false, GeoPoint.SeparatorType.ANY));
        assertTrue(GeoPointValidator.isValid("116.4074 39.9042", false, GeoPoint.SeparatorType.ANY));
        assertTrue(GeoPointValidator.isValid("39.9042,116.4074", true, GeoPoint.SeparatorType.ANY));

        // 测试无效坐标
        assertFalse(GeoPointValidator.isValid("181,39.9042", false, GeoPoint.SeparatorType.ANY));
        assertFalse(GeoPointValidator.isValid("116.4074,91", false, GeoPoint.SeparatorType.ANY));
        assertFalse(GeoPointValidator.isValid("116.4074", false, GeoPoint.SeparatorType.ANY));
        assertFalse(GeoPointValidator.isValid("abc,def", false, GeoPoint.SeparatorType.ANY));

        // 测试null和空字符串
        assertTrue(GeoPointValidator.isValid(null, false, GeoPoint.SeparatorType.ANY));
        assertTrue(GeoPointValidator.isValid("", false, GeoPoint.SeparatorType.ANY));
    }
}
