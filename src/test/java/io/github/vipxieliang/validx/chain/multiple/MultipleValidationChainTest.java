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

package io.github.vipxieliang.validx.chain.multiple;

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultipleValidationChainTest {

    @Test
    public void testMultipleValidationsTogether() {
        // 测试同时验证身份证、URL和IP地址
        ValidX chain = ValidX.init()
                .isChineseIdCard((Object)"440608197310039910")
                .isUrl((Object)"http://example.com")
                .isIp((Object)"192.168.1.1");
                
        assertTrue(chain.passed(), "所有验证都应该通过");
        assertEquals(0, chain.getErrors().size());
    }
    
    @Test
    public void testMultipleValidationsTogetherFailure() {
        // 测试同时验证身份证、URL和IP地址，所有都校验不通过的情况
        ValidX chain = ValidX.init()
                .isChineseIdCard((Object)"invalid-id-card")
                .isUrl((Object)"invalid-url")
                .isIp((Object)"999.999.999.999");
                
        assertFalse(chain.passed(), "所有验证都不应该通过");
        assertEquals(3, chain.getErrors().size());
    }
    
    @Test
    public void testInstanceMethodWithDirectValue() {
        // 测试使用实例方法直接传入值进行验证
        ValidX chain = ValidX.init(); // 创建一个空的链
        chain = chain.isChineseIdCard((Object)"440608197310039910")
                    .isUrl((Object)"http://example.com")
                    .isIp((Object)"192.168.1.1");

        assertTrue(chain.passed(), "所有验证都应该通过");
    }
}