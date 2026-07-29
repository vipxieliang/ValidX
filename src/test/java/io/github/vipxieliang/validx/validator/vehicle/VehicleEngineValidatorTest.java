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

package io.github.vipxieliang.validx.validator.vehicle;

import io.github.vipxieliang.validx.annotations.VehicleEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 车辆发动机编码验证器测试类
 */
public class VehicleEngineValidatorTest {

    private final VehicleEngineValidator validator = new VehicleEngineValidator();

    @Test
    public void testValidEngineNumbers() {
        // 测试有效的发动机编号
        assertTrue(validator.isValid("123456", null));
        assertTrue(validator.isValid("ABC123", null));
        assertTrue(validator.isValid("123ABC456", null));
        assertTrue(validator.isValid("12345678901234567", null)); // 17位
    }

    @Test
    public void testInvalidEngineNumbers() {
        // 测试无效的发动机编号
        assertFalse(validator.isValid("12345", null)); // 少于6位
        assertFalse(validator.isValid("123456789012345678", null)); // 超过17位
        assertFalse(validator.isValid("12345-", null)); // 包含特殊字符
        assertFalse(validator.isValid("12345_", null)); // 包含下划线
        assertFalse(validator.isValid("12345 ", null)); // 包含空格
    }

    @Test
    public void testNullAndEmptyVehicleEngine() {
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}