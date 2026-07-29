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

public class ForeignerPermanentResidenceIdentityValidationChainTest {

    @Test
    public void testValidForeignerPermanentResidenceIdentity() {
        ValidaX chain = ValidaX.init();
        chain = chain.isForeignerPermanentResidenceIdentity("911124198108030024");
        assertTrue(chain.passed(), "有效的外国人永久居留身份证号码应该通过验证");

        chain = ValidaX.init();
        chain = chain.isForeignerPermanentResidenceIdentity("931012199012010018");
        assertTrue(chain.passed(), "有效的外国人永久居留身份证号码应该通过验证");
    }

    @Test
    public void testInvalidForeignerPermanentResidenceIdentity() {
        ValidaX chain = ValidaX.init();
        chain = chain.isForeignerPermanentResidenceIdentity("111124198108030024"); // 不是以9开头
        assertFalse(chain.passed(), "无效的外国人永久居留身份证号码应该验证失败");

        chain = ValidaX.init();
        chain = chain.isForeignerPermanentResidenceIdentity("91112419810803002"); // 长度不足
        assertFalse(chain.passed(), "无效的外国人永久居留身份证号码应该验证失败");
    }

    @Test
    public void testNullAndEmptyForeignerPermanentResidenceIdentity() {
        // 测试 null 值
        ValidaX chain = ValidaX.init();
        chain = chain.isForeignerPermanentResidenceIdentity(null);
        assertTrue(chain.passed(), "null 应该通过验证");

        // 测试空字符串
        chain = ValidaX.init();
        chain = chain.isForeignerPermanentResidenceIdentity("");
        assertTrue(chain.passed(), "空字符串应该通过验证");
    }
}
