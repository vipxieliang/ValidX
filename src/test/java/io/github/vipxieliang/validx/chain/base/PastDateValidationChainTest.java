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

public class PastDateValidationChainTest {

    @Test
    public void testValidPastDate() {
        ValidaX chain = ValidaX.init();
        // 使用昨天的日期进行测试
        String pastDate = java.time.LocalDate.now().minusDays(1).toString();
        chain = chain.isPastDate((Object)pastDate,false);
        assertTrue(chain.passed(), "有效的过去日期应该通过验证");
        
        // 使用过去的日期时间进行测试
        String pastDateTime = java.time.LocalDateTime.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        chain = ValidaX.init();
        chain = chain.isPastDate((Object)pastDateTime,false);
        assertTrue(chain.passed(), "有效的过去日期时间应该通过验证");
    }

    @Test
    public void testInvalidPastDate() {
        ValidaX chain = ValidaX.init();
        // 使用明天的日期进行测试
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        chain = chain.isPastDate((Object)futureDate,false);
        assertFalse(chain.passed(), "未来的日期不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是过去的日期", chain.getErrors().get(0));
        
        // 使用今天的日期进行测试（不是过去日期）
        String today = java.time.LocalDate.now().toString();
        chain = ValidaX.init();
        chain = chain.isPastDate((Object)today,false);
        assertFalse(chain.passed(), "今天的日期不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是过去的日期", chain.getErrors().get(0));
        
        // 使用无效的日期格式进行测试
        chain = ValidaX.init();
        chain = chain.isPastDate((Object)"invalid-date",false);
        assertFalse(chain.passed(), "无效的日期格式不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是过去的日期", chain.getErrors().get(0));
    }

    @Test
    public void testNullAndEmptyPastDate() {
        // 测试null值应该通过验证
        ValidaX chain = ValidaX.init();
        chain = chain.isPastDate(null, false);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串应该通过验证
        chain = ValidaX.init();
        chain = chain.isPastDate((Object)"", false);
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}