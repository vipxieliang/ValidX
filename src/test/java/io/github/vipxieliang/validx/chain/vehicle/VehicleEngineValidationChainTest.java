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

package io.github.vipxieliang.validx.chain.vehicle;

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 车辆发动机编码链式验证测试类
 */
public class VehicleEngineValidationChainTest {

    @Test
    public void testValidEngineNumbers() {
        // 测试有效的发动机编号
        Assertions.assertTrue(ValidationPlus.init().isVehicleEngine("123456").passed());
        assertTrue(ValidationPlus.init().isVehicleEngine("ABC123").passed());
        assertTrue(ValidationPlus.init().isVehicleEngine("123ABC456").passed());
        assertTrue(ValidationPlus.init().isVehicleEngine("12345678901234567").passed()); // 17位
    }

    @Test
    public void testInvalidEngineNumbers() {
        // 测试无效的发动机编号
        assertFalse(ValidationPlus.init().isVehicleEngine("12345").passed()); // 少于6位
        assertFalse(ValidationPlus.init().isVehicleEngine("123456789012345678").passed()); // 超过17位
        assertFalse(ValidationPlus.init().isVehicleEngine("12345-").passed()); // 包含特殊字符
        assertFalse(ValidationPlus.init().isVehicleEngine("12345_").passed()); // 包含下划线
        assertFalse(ValidationPlus.init().isVehicleEngine("12345 ").passed()); // 包含空格
    }

    @Test
    public void testNullAndEmptyVehicleEngine() {
        // 测试 null 值
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isVehicleEngine(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidationPlus.init();
        chain = chain.isVehicleEngine("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}