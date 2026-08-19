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

package io.github.vipxieliang.validx.chain.foreign;

import io.github.vipxieliang.validx.annotations.NationalityCode;
import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NationalityCodeValidationChainTest {

    @Test
    public void testValidNationalityCode() {
        ValidX chain = ValidX.init();
        chain = chain.isNationalityCode((Object)"CA");
        assertTrue(chain.passed(), "有效的两字母代码 CA 应该通过验证");

        chain = ValidX.init();
        chain = chain.isNationalityCode((Object)"CAN");
        assertTrue(chain.passed(), "有效的三字母代码 CAN 应该通过验证");

        chain = ValidX.init();
        chain = chain.isNationalityCode((Object)"124");
        assertTrue(chain.passed(), "有效的三位数字代码 124 应该通过验证");
    }

    @Test
    public void testInvalidNationalityCode() {
        ValidX chain = ValidX.init();
        chain = chain.isNationalityCode((Object)"999");
        assertFalse(chain.passed(), "无效的 999 不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("国籍国代码无效，必须是 ISO 3166-1 代码（两字母、三字母或三位数字）", chain.getErrors().get(0));
    }

    @Test
    public void testNationalityCodeWithNumericType() {
        // 五星卡复核场景：指定 NUMERIC 形式
        ValidX chain = ValidX.init();
        chain = chain.isNationalityCode((Object)"124", new NationalityCode.NationalityCodeType[]{NationalityCode.NationalityCodeType.NUMERIC});
        assertTrue(chain.passed(), "NUMERIC 形式下，数字 124 应该通过验证");

        chain = ValidX.init();
        chain = chain.isNationalityCode((Object)"CA", new NationalityCode.NationalityCodeType[]{NationalityCode.NationalityCodeType.NUMERIC});
        assertFalse(chain.passed(), "NUMERIC 形式下，两字母 CA 应不通过验证");
    }

    @Test
    public void testNullAndEmptyNationalityCode() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isNationalityCode((Object)null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isNationalityCode((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
