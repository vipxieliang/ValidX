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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PastDateIncludeTodayValidationChainTest {

    @Test
    public void testValidPastDateWithoutIncludeToday() {
        ValidaX chain = ValidaX.init();
        // 使用昨天的日期进行测试
        String pastDate = LocalDate.now().minusDays(1).toString();
        chain = chain.isPastDate((Object)pastDate,false);
        assertTrue(chain.passed(), "有效的过去日期应该通过验证");
    }

    @Test
    public void testInvalidTodayWithoutIncludeToday() {
        ValidaX chain = ValidaX.init();
        // 使用今天的日期进行测试
        String today = LocalDate.now().toString();
        chain = chain.isPastDate((Object)today,false);
        assertFalse(chain.passed(), "今天的日期不应该通过验证（不包含今天）");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是过去的日期", chain.getErrors().get(0));
    }
    
    @Test
    public void testValidTodayWithIncludeToday() {
        ValidaX chain = ValidaX.init();
        // 使用今天的日期进行测试
        String today = LocalDate.now().toString();
        chain = chain.isPastDate((Object)today, true);
        assertTrue(chain.passed(), "今天的日期应该通过验证（包含今天）");
    }
    
    @Test
    public void testValidPastDateWithIncludeToday() {
        ValidaX chain = ValidaX.init();
        // 使用昨天的日期进行测试
        String pastDate = LocalDate.now().minusDays(1).toString();
        chain = chain.isPastDate((Object)pastDate, true);
        assertTrue(chain.passed(), "有效的过去日期应该通过验证（包含今天）");
    }
    
    @Test
    public void testInvalidTomorrowWithIncludeToday() {
        ValidaX chain = ValidaX.init();
        // 使用明天的日期进行测试
        String tomorrow = LocalDate.now().plusDays(1).toString();
        chain = chain.isPastDate((Object)tomorrow, true);
        assertFalse(chain.passed(), "明天的日期不应该通过验证（即使包含今天）");
        assertEquals(1, chain.getErrors().size());
        assertEquals("日期必须是过去的日期", chain.getErrors().get(0));
    }
}