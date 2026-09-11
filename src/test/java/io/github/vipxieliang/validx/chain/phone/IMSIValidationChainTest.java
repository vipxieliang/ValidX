/*
 * Copyright 2025-2026 vipxieliang
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

package io.github.vipxieliang.validx.chain.phone;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IMSIValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isIMSI(null);
        assertTrue(chain.passed(), "null值应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isIMSI("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidIMSI() {
        ValidX chain = ValidX.init();
        chain = chain.isIMSI("460001234567890"); // Valid IMSI (China Mobile)
        assertTrue(chain.passed(), "有效的IMSI应该通过验证");

        chain = ValidX.init();
        chain = chain.isIMSI("4600-0123-4567-890"); // Valid IMSI with dashes
        assertTrue(chain.passed(), "带连字符的有效IMSI应该通过验证");

        chain = ValidX.init();
        chain = chain.isIMSI("460 001 234 567 890"); // Valid IMSI with spaces
        assertTrue(chain.passed(), "带空格的有效IMSI应该通过验证");

        chain = ValidX.init();
        chain = chain.isIMSI("46001123456789"); // 14-digit valid IMSI
        assertTrue(chain.passed(), "14位的有效IMSI应该通过验证");
    }

    @Test
    public void testInvalidIMSI() {
        ValidX chain = ValidX.init();
        chain = chain.isIMSI("4600012345678901"); // Too long (16 digits)
        assertFalse(chain.passed(), "过长的IMSI不应该通过验证");
        assertEquals(1, chain.getErrors().size());

        chain = ValidX.init();
        chain = chain.isIMSI("4600012345678"); // Too short (13 digits)
        assertFalse(chain.passed(), "过短的IMSI不应该通过验证");
        assertEquals(1, chain.getErrors().size());

        chain = ValidX.init();
        chain = chain.isIMSI("46000123456789a"); // Contains non-digits
        assertFalse(chain.passed(), "包含非数字字符的IMSI不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }
}
