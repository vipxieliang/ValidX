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

public class LawyerValidationChainTest {

    @Test
    public void testValidLawyer() {
        ValidX chain = ValidX.init();
        chain = chain.isLawyer("11101200010000001");
        assertTrue(chain.passed(), "有效的律师执业证编号应该通过验证");

        chain = ValidX.init();
        chain = chain.isLawyer("20201101010001");
        assertTrue(chain.passed(), "有效的法律职业资格证书应该通过验证");
    }

    @Test
    public void testInvalidLawyer() {
        ValidX chain = ValidX.init();
        chain = chain.isLawyer("1234567890"); // 长度不足
        assertFalse(chain.passed(), "无效的律师证编号应该验证失败");

        chain = ValidX.init();
        chain = chain.isLawyer("21101200010000001"); // 第1位不是1
        assertFalse(chain.passed(), "无效的律师证编号应该验证失败");
    }

    @Test
    public void testNullAndEmptyLawyer() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isLawyer(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isLawyer("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
