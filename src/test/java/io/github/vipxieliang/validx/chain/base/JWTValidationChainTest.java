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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JWTValidationChainTest {

    @Test
    public void testValidJWT() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        assertTrue(chain.passed(), "有效的JWT Token应该通过验证");
    }

    @Test
    public void testInvalidJWT() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)"invalid.jwt");
        assertFalse(chain.passed(), "无效的JWT Token不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("JWT Token格式不正确", chain.getErrors().get(0));
    }

    @Test
    public void testJWTWithTwoParts() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)"abc.def");
        assertFalse(chain.passed(), "只有两部分的Token不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testJWTWithFourParts() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)"abc.def.ghi.jkl");
        assertFalse(chain.passed(), "有四部分的Token不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testJWTWithEmptyParts() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)".def.ghi");
        assertFalse(chain.passed(), "有空部分的Token不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testJWTWithInvalidCharacters() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)"abc+def.ghi.jkl");
        assertFalse(chain.passed(), "包含无效字符的Token不应该通过验证");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testNullJWT() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT(null);
        assertTrue(chain.passed(), "null值应该通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testEmptyJWT() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testMultipleValidations() {
        ValidaX chain = ValidaX.init();
        chain = chain.isJWT((Object)"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U")
                     .isJWT((Object)"abc.def.ghi");
        assertTrue(chain.passed(), "两个有效的JWT应该都通过验证");

        ValidaX chain2 = ValidaX.init();
        chain2 = chain2.isJWT((Object)"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U")
                      .isJWT((Object)"invalid");
        assertFalse(chain2.passed(), "一个无效的JWT应该导致验证失败");
        assertEquals(1, chain2.getErrors().size());
    }
}
