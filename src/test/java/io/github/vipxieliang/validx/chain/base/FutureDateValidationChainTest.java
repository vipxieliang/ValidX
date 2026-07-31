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

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FutureDateValidationChainTest {

    @Test
    public void testValidFutureDate() {
        ValidX chain = ValidX.init();
        // 使用明天的日期进行测试
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        chain = chain.isFutureDate((Object)futureDate, false);
        assertTrue(chain.passed(), "有效的未来日期应该通过验证");
        
        // 使用未来的日期时间进行测试
        String futureDateTime = java.time.LocalDateTime.now().plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        chain = ValidX.init();
        chain = chain.isFutureDate((Object)futureDateTime, false);
        assertTrue(chain.passed(), "有效的未来日期时间应该通过验证");
    }

    @Test
    public void testInvalidFutureDate() {
        ValidX chain = ValidX.init();
        // 使用昨天的日期进行测试
        String pastDate = java.time.LocalDate.now().minusDays(1).toString();
        chain = chain.isFutureDate((Object)pastDate, false);
        assertFalse(chain.passed(), "过去的日期不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是未来的日期", chain.getErrors().get(0));
        
        // 使用今天的日期进行测试（不是未来日期）
        String today = java.time.LocalDate.now().toString();
        chain = ValidX.init();
        chain = chain.isFutureDate((Object)today, false);
        assertFalse(chain.passed(), "今天的日期不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是未来的日期", chain.getErrors().get(0));
        
        // 使用无效的日期格式进行测试
        chain = ValidX.init();
        chain = chain.isFutureDate((Object)"invalid-date", false);
        assertFalse(chain.passed(), "无效的日期格式不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是未来的日期", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyFutureDate() {
        // 测试null值应该通过验证
        ValidX chain = ValidX.init();
        chain = chain.isFutureDate(null, false);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证
        chain = ValidX.init();
        chain = chain.isFutureDate((Object)"", false);
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}