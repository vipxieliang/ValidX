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


import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SoftwareCopyrightValidationChainTest {

    @Test
    public void testValidSoftwareCopyright() {
        ValidX chain = ValidX.init();
        chain = chain.isSoftwareCopyright("软著登字第2023001234号");
        assertTrue(chain.passed(), "有效的软件著作权登记号应该通过验证");
        
        chain = ValidX.init();
        chain = chain.isSoftwareCopyright("10-2023-001234");
        assertTrue(chain.passed(), "有效的软件著作权登记号应该通过验证");
    }

    @Test
    public void testInvalidSoftwareCopyright() {
        ValidX chain = ValidX.init();
        chain = chain.isSoftwareCopyright("软著登字第20230012345678号"); // 位数太多
        assertFalse(chain.passed(), "无效的软件著作权登记号应该验证失败");

        chain = ValidX.init();
        chain = chain.isSoftwareCopyright("10-2023-0012345"); // 顺序号位数不对
        assertFalse(chain.passed(), "无效的软件著作权登记号应该验证失败");
    }

    @Test
    public void testNullAndEmptySoftwareCopyright() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isSoftwareCopyright(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isSoftwareCopyright("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}