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

public class ComprehensiveLanguageSupportTest {

    @Test
    public void testEmailValidationInAllLanguages() {
        // 保存当前默认语言环境
        Locale originalLocale = Locale.getDefault();
        
        try {
            // 测试各种语言环境下的邮箱验证错误消息
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidationPlus chainEn = ValidationPlus.init().isEmail((Object)"invalid-email");
            assertEquals("Invalid email address format", chainEn.getErrors().get(0));
            
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidationPlus chainZh = ValidationPlus.init().isEmail((Object)"invalid-email");
            assertEquals("邮箱地址格式不正确", chainZh.getErrors().get(0));
        } finally {
            // 恢复原来的语言环境
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
    
    @Test
    public void testUrlValidationInAllLanguages() {
        // 保存当前默认语言环境
        Locale originalLocale = Locale.getDefault();
        
        try {
            // 测试各种语言环境下的URL验证错误消息
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidationPlus chainEn = ValidationPlus.init().isUrl((Object)"invalid-url");
            assertEquals("Invalid URL format", chainEn.getErrors().get(0));
            
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidationPlus chainZh = ValidationPlus.init().isUrl((Object)"invalid-url");
            assertEquals("URL格式不正确", chainZh.getErrors().get(0));
        } finally {
            // 恢复原来的语言环境
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
    
    @Test
    public void testChineseIdCardValidationInAllLanguages() {
        // 保存当前默认语言环境
        Locale originalLocale = Locale.getDefault();
        
        try {
            // 测试各种语言环境下的身份证验证错误消息
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidationPlus chainEn = ValidationPlus.init().isChineseIdCard((Object)"123456789012345678");
            assertEquals("Invalid Chinese ID card number", chainEn.getErrors().get(0));
            
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidationPlus chainZh = ValidationPlus.init().isChineseIdCard((Object)"123456789012345678");
            assertEquals("身份证号码不正确", chainZh.getErrors().get(0));
        } finally {
            // 恢复原来的语言环境
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
}