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

package io.github.vipxieliang.validx.chain.phone;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ICCIDValidationChainTest {

    @Test
    public void testNullAndEmptyValue() {
        ValidX nullChain = ValidX.init();
        nullChain.isICCID(null);
        assertTrue(nullChain.passed(), "null值应该通过验证");

        ValidX emptyChain = ValidX.init();
        emptyChain.isICCID("");
        assertTrue(emptyChain.passed(), "空字符串应该通过验证");
    }

    @Test
    public void testValidICCID() {
        ValidX standard = ValidX.init();
        standard.isICCID("89860115244939092661");
        assertTrue(standard.passed(), "有效的ICCID应该通过验证");

        ValidX hyphen = ValidX.init();
        hyphen.isICCID("8986-0115-2449-3909-2661");
        assertTrue(hyphen.passed(), "带连字符的有效ICCID应该通过验证");

        ValidX space = ValidX.init();
        space.isICCID("8986 0115 2449 3909 2661");
        assertTrue(space.passed(), "带空格的有效ICCID应该通过验证");

        ValidX another = ValidX.init();
        another.isICCID("89866604876475938248");
        assertTrue(another.passed(), "另一个有效的ICCID应该通过验证");
    }

    @Test
    public void testInvalidICCID() {
        ValidX tooShort = ValidX.init();
        tooShort.isICCID("8986011524493909266");
        assertFalse(tooShort.passed(), "过短的ICCID不应该通过验证");
        assertEquals(1, tooShort.getErrors().size());

        ValidX tooLong = ValidX.init();
        tooLong.isICCID("898601152449390926611");
        assertFalse(tooLong.passed(), "过长的ICCID不应该通过验证");
        assertEquals(1, tooLong.getErrors().size());

        ValidX badCheckDigit = ValidX.init();
        badCheckDigit.isICCID("89860115244939092662");
        assertFalse(badCheckDigit.passed(), "Luhn校验位错误的ICCID不应该通过验证");
        assertEquals(1, badCheckDigit.getErrors().size());

        ValidX nonDigit = ValidX.init();
        nonDigit.isICCID("8986011524493909266X");
        assertFalse(nonDigit.passed(), "包含非数字字符的ICCID不应该通过验证");
        assertEquals(1, nonDigit.getErrors().size());
    }
}
