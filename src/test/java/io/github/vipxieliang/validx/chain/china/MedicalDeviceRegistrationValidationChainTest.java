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

package io.github.vipxieliang.validx.chain.china;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MedicalDeviceRegistrationValidationChainTest {

    @Test
    public void testValidMedicalDeviceRegistration() {
        ValidaX chain = ValidaX.init();
        chain = chain.isMedicalDeviceRegistration("国械注准20243010001");
        assertTrue(chain.passed(), "有效的医疗器械注册证号应该通过验证");

        chain = ValidaX.init();
        chain = chain.isMedicalDeviceRegistration("粤械注准20242020002");
        assertTrue(chain.passed(), "有效的医疗器械注册证号应该通过验证");
    }

    @Test
    public void testInvalidMedicalDeviceRegistration() {
        ValidaX chain = ValidaX.init();
        chain = chain.isMedicalDeviceRegistration("国械注准20244010001"); // 错误的管理类别
        assertFalse(chain.passed(), "无效的医疗器械注册证号应该验证失败");

        chain = ValidaX.init();
        chain = chain.isMedicalDeviceRegistration("国械注出20243010001"); // 错误的注册形式
        assertFalse(chain.passed(), "无效的医疗器械注册证号应该验证失败");
    }

    @Test
    public void testNullAndEmptyMedicalDeviceRegistration() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isMedicalDeviceRegistration(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidaX.init();
        chain = chain.isMedicalDeviceRegistration("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
