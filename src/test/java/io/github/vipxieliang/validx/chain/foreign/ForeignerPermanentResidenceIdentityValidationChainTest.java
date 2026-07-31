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

package io.github.vipxieliang.validx.chain.foreign;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ForeignerPermanentResidenceIdentityValidationChainTest {

    @Test
    public void testValidForeignerPermanentResidenceIdentity() {
        ValidX chain = ValidX.init();
        chain = chain.isForeignerPermanentResidenceIdentity((Object)"911124198108030024");
        assertTrue(chain.passed(), "有效的外国人永久居留身份证应该通过验证");
    }

    @Test
    public void testInvalidForeignerPermanentResidenceIdentity() {
        ValidX chain = ValidX.init();
        chain.withLocale(Locale.SIMPLIFIED_CHINESE);
        chain = chain.isForeignerPermanentResidenceIdentity((Object)"91112419810803002");
        assertFalse(chain.passed(), "无效的外国人永久居留身份证不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("外国人永久居留身份证号码格式不正确", chain.getErrors().get(0));
    }
}