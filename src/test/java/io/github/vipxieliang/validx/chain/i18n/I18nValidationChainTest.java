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
import org.junit.jupiter.api.Test;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class I18nValidationChainTest {

    @Test
    public void testEnglishLocale() {
        // 保存当前默认语言环境
        Locale originalLocale = Locale.getDefault();
        
        try {
            // 设置当前线程的语言环境为英语
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            
            ValidationPlus chain = ValidationPlus.init();
            chain = chain.isEmail((Object)"invalid-email");
            assertFalse(chain.passed());
            assertEquals(1, chain.getErrors().size());
            assertEquals("Invalid email address format", chain.getErrors().get(0));
        } finally {
            // 清除当前线程的语言环境设置
            MessageManager.clearCurrentLocale();
            // 恢复原来的语言环境
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void testChineseLocale() {
        // 保存当前默认语言环境
        Locale originalLocale = Locale.getDefault();
        
        try {
            // 设置当前线程的语言环境为中文
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            
            ValidationPlus chain = ValidationPlus.init();
            chain = chain.isEmail((Object)"invalid-email");
            assertFalse(chain.passed());
            assertEquals(1, chain.getErrors().size());
            assertEquals("邮箱地址格式不正确", chain.getErrors().get(0));
        } finally {
            // 清除当前线程的语言环境设置
            MessageManager.clearCurrentLocale();
            // 恢复原来的语言环境
            Locale.setDefault(originalLocale);
        }
    }
    
    @Test
    public void testDefaultLocale() {
        // 保存当前默认语言环境
        Locale originalLocale = Locale.getDefault();
        
        try {
            // 设置默认语言环境为中文
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
            MessageManager.clearCurrentLocale(); // 确保使用系统默认语言环境
            
            ValidationPlus chain = ValidationPlus.init();
            chain = chain.isEmail((Object)"invalid-email");
            assertFalse(chain.passed());
            assertEquals(1, chain.getErrors().size());
            assertEquals("邮箱地址格式不正确", chain.getErrors().get(0));
        } finally {
            // 恢复原来的语言环境
            Locale.setDefault(originalLocale);
        }
    }
    
    @Test
    public void testDifferentLocalesProduceDifferentMessages() {
        // 保存当前默认语言环境
        Locale originalLocale = Locale.getDefault();
        
        try {
            // 设置当前线程语言环境为英语
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidationPlus chainEn = ValidationPlus.init();
            chainEn = chainEn.isEmail((Object)"invalid-email");
            
            // 设置当前线程语言环境为中文
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidationPlus chainZh = ValidationPlus.init();
            chainZh = chainZh.isEmail((Object)"invalid-email");
            
            assertFalse(chainEn.passed());
            assertFalse(chainZh.passed());
            
            // 验证两个语言环境返回不同的消息
            assertNotEquals(chainEn.getErrors().get(0), chainZh.getErrors().get(0));
            
            // 额外验证消息内容
            assertEquals("Invalid email address format", chainEn.getErrors().get(0));
            assertEquals("邮箱地址格式不正确", chainZh.getErrors().get(0));
        } finally {
            // 恢复原来的语言环境
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
}