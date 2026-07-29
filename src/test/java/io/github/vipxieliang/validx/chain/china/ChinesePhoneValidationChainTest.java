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

public class ChinesePhoneValidationChainTest {

    // isChinesePhone 测试用例
    @Test
    public void testValidChinesePhone() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChinesePhone((Object)"13812345678");
        assertTrue(chain.passed(), "有效的中国手机号码应该通过验证");
    }

    @Test
    public void testInvalidChinesePhone() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChinesePhone((Object)"12345678901");
        assertFalse(chain.passed(), "无效的中国手机号码不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testNullAndEmptyValuesForPhone() {
        // 测试null值
        ValidaX validator = ValidaX.init();
        validator.isChinesePhone((Object)null);
        assertTrue(validator.passed(), "null should pass validation");

        // 测试空字符串
        validator = ValidaX.init();
        validator.isChinesePhone((Object)"");
        assertTrue(validator.passed(), "empty string should pass validation");
    }

    // isChinesePhoneOrLandline 测试用例
    @Test
    public void testValidChinesePhoneOrLandline() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChinesePhoneOrLandline((Object)"13812345678");
        assertTrue(chain.passed(), "有效的中国手机号码应该通过验证");
        
        chain = ValidaX.init();
        chain = chain.isChinesePhoneOrLandline((Object)"010-12345678");
        assertTrue(chain.passed(), "有效的中国座机号码应该通过验证");
    }

    @Test
    public void testInvalidChinesePhoneOrLandline() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChinesePhoneOrLandline((Object)"12345");
        assertFalse(chain.passed(), "无效的号码不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testNullAndEmptyValuesForPhoneOrLandline() {
        // 测试null值
        ValidaX validator = ValidaX.init();
        validator.isChinesePhoneOrLandline((Object)null);
        assertTrue(validator.passed(), "null should pass validation");

        // 测试空字符串
        validator = ValidaX.init();
        validator.isChinesePhoneOrLandline((Object)"");
        assertTrue(validator.passed(), "empty string should pass validation");
    }

    // isChineseLandline 测试用例
    @Test
    public void testValidChineseLandline() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChineseLandline((Object)"010-12345678");
        assertTrue(chain.passed(), "有效的中国座机号码应该通过验证");
        
        chain = ValidaX.init();
        chain = chain.isChineseLandline((Object)"0512-87654321");
        assertTrue(chain.passed(), "有效的中国座机号码应该通过验证");
    }

    @Test
    public void testInvalidChineseLandline() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChineseLandline((Object)"12345");
        assertFalse(chain.passed(), "无效的座机号码不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testNullAndEmptyValuesForLandline() {
        // 测试null值
        ValidaX validator = ValidaX.init();
        validator.isChineseLandline((Object)null);
        assertTrue(validator.passed(), "null should pass validation");

        // 测试空字符串
        validator = ValidaX.init();
        validator.isChineseLandline((Object)"");
        assertTrue(validator.passed(), "empty string should pass validation");
    }
}