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

public class HourMinuteSecondValidationChainTest {

    @Test
    public void testValidHourMinuteSecond() {
        ValidaX chain = ValidaX.init();
        chain = chain.isHourMinuteSecond("23:50:29");
        assertTrue(chain.passed(), "有效的时间格式应该通过验证");
    }

    @Test
    public void testInvalidHourMinuteSecond() {
        ValidaX chain = ValidaX.init();
        chain = chain.isHourMinuteSecond("25:00:00"); // 小时超出范围
        assertFalse(chain.passed(), "无效的时间格式应该验证失败");
    }

    @Test
    public void testNullAndEmptyHourMinuteSecond() {
        // 测试null值应该通过验证（由@NotNull处理）
        ValidaX chain = ValidaX.init();
        chain = chain.isHourMinuteSecond(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证（由@NotEmpty处理）
        chain = ValidaX.init();
        chain = chain.isHourMinuteSecond("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}