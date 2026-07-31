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

public class WorkCopyrightValidationChainTest {

    @Test
    public void testValidWorkCopyright() {
        ValidX chain = ValidX.init();
        chain = chain.isWorkCopyright("作登字22-2023-A-0018号");
        assertTrue(chain.passed(), "有效的一般作品著作权登记号应该通过验证");
    }

    @Test
    public void testInvalidWorkCopyright() {
        ValidX chain = ValidX.init();
        chain = chain.isWorkCopyright("作登字22-2023-A-0018"); // 缺少"号"后缀
        assertFalse(chain.passed(), "无效的一般作品著作权登记号应该验证失败");
    }

    @Test
    public void testNullAndEmptyWorkCopyright() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isWorkCopyright(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isWorkCopyright("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}