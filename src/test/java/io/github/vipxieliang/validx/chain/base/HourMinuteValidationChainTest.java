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
import static org.junit.jupiter.api.Assertions.*;

public class HourMinuteValidationChainTest {

    @Test
    public void testValidHourMinute() {
        ValidaX chain = ValidaX.init();
        chain = chain.isHourMinute("23:20");
        assertTrue(chain.passed(), "有效的时间格式应该通过验证");
    }

    @Test
    public void testInvalidHourMinute() {
        ValidaX chain = ValidaX.init();
        chain = chain.isHourMinute("25:00"); // 小时超出范围
        assertFalse(chain.passed(), "无效的时间格式应该验证失败");
    }

    @Test
    public void testNullAndEmptyHourMinute() {
        // 测试null值应该通过验证（由@NotNull处理）
        ValidaX chain = ValidaX.init();
        chain = chain.isHourMinute(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证（由@NotEmpty处理）
        chain = ValidaX.init();
        chain = chain.isHourMinute("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}