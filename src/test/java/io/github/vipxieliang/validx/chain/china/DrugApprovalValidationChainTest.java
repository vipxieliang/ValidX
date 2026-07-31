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

public class DrugApprovalValidationChainTest {

    @Test
    public void testValidDrugApproval() {
        ValidX chain = ValidX.init();
        chain = chain.isDrugApproval("国药准字H20210039");
        assertTrue(chain.passed(), "有效的药品批准文号应该通过验证");

        chain = ValidX.init();
        chain = chain.isDrugApproval("国药准字ZC20171003");
        assertTrue(chain.passed(), "有效的药品批准文号应该通过验证");
    }

    @Test
    public void testInvalidDrugApproval() {
        ValidX chain = ValidX.init();
        chain = chain.isDrugApproval("国药准字X20210039"); // 无效类别
        assertFalse(chain.passed(), "无效的药品批准文号应该验证失败");

        chain = ValidX.init();
        chain = chain.isDrugApproval("国药准字H210039"); // 数字位数不正确
        assertFalse(chain.passed(), "无效的药品批准文号应该验证失败");
    }

    @Test
    public void testNullAndEmptyDrugApproval() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isDrugApproval(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidX.init();
        chain = chain.isDrugApproval("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
