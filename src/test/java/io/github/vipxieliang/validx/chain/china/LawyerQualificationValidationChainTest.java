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

import io.github.vipxieliang.validx.chain.ValidationPlus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LawyerQualificationValidationChainTest {

    @Test
    public void testValidLawyerQualification() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isLawyer((Object)"1234567890123456");
        assertTrue(chain.passed(), "有效的律师资格证应该通过验证");
    }

    @Test
    public void testInvalidLawyerQualification() {
        ValidationPlus chain = ValidationPlus.init();
        chain = chain.isLawyer((Object)"123456789012345");
        assertFalse(chain.passed(), "无效的律师资格证不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("律师资格证书编号格式不正确", chain.getErrors().get(0));
    }
}