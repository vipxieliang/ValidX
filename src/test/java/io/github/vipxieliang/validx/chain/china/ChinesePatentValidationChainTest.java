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

public class ChinesePatentValidationChainTest {

    @Test
    public void testValidChinesePatent() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChinesePatent("ZL2013106997442");
        assertTrue(chain.passed(), "有效的中国专利号应该通过验证");
    }

    @Test
    public void testInvalidChinesePatent() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChinesePatent("ZL2013106997449"); // 错误的校验位
        assertFalse(chain.passed(), "无效的中国专利号应该验证失败");
    }

    @Test
    public void testInvalidChinesePatentType() {
        ValidaX chain = ValidaX.init();
        chain = chain.isChinesePatent("ZL2013506997442"); // 无效的专利类型
        assertFalse(chain.passed(), "无效的中国专利号应该验证失败");
    }

    @Test
    public void testNullAndEmptyValues() {
        // 测试null值
        ValidaX validator = ValidaX.init();
        validator.isChinesePatent((String)null);
        assertTrue(validator.passed(), "null should pass validation");

        // 测试空字符串
        validator = ValidaX.init();
        validator.isChinesePatent("");
        assertTrue(validator.passed(), "empty string should pass validation");
    }
}