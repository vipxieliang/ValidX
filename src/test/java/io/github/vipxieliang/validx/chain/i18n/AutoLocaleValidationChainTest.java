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

package io.github.vipxieliang.validx.chain.i18n;

import io.github.vipxieliang.validx.chain.ValidationPlus;
import io.github.vipxieliang.validx.i18n.MessageManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class AutoLocaleValidationChainTest {

    @BeforeEach
    public void setUp() {
        // 测试前清理语言环境设置
        MessageManager.clearCurrentLocale();
    }

    @AfterEach
    public void tearDown() {
        // 测试后清理语言环境设置
        MessageManager.clearCurrentLocale();
    }

    @Test
    public void testAutoLocaleWithSystemDefault() {
        // 使用系统默认语言环境
        ValidationPlus chain = ValidationPlus.init()
                .isEmail("invalid-email");
        
        assertFalse(chain.passed());
        assertEquals(1, chain.getErrors().size());
        // 消息会根据系统默认语言环境自动切换
        assertNotNull(chain.getErrors().get(0));
        assertFalse(chain.getErrors().get(0).isEmpty());
    }

    @Test
    public void testAutoLocaleWithThreadLocal() {
        // 设置线程级别的语言环境为中文
        MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
        
        ValidationPlus chain = ValidationPlus.init()
                .isEmail("invalid-email");
        
        assertFalse(chain.passed());
        assertEquals(1, chain.getErrors().size());
        // 应该使用中文消息
        assertNotNull(chain.getErrors().get(0));
        assertFalse(chain.getErrors().get(0).isEmpty());
        
        // 清理设置
        MessageManager.clearCurrentLocale();
    }

    @Test
    public void testOverrideThreadLocalWithExplicitLocale() {
        // 设置线程级别的语言环境为中文
        MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
        
        // 但显式指定使用英文
        ValidationPlus chain = ValidationPlus.init()
                .withLocale(Locale.ENGLISH)
                .isEmail("invalid-email");
        
        assertFalse(chain.passed());
        assertEquals(1, chain.getErrors().size());
        // 应该使用英文消息，因为显式指定优先级更高
        assertNotNull(chain.getErrors().get(0));
        assertFalse(chain.getErrors().get(0).isEmpty());
        
        // 清理设置
        MessageManager.clearCurrentLocale();
    }
}