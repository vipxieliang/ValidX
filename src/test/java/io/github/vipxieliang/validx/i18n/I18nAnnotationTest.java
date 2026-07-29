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

package io.github.vipxieliang.validx.i18n;

import io.github.vipxieliang.validx.annotations.Email;
import io.github.vipxieliang.validx.annotations.ChineseIdCard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import java.util.Locale;
import java.util.Set;

public class I18nAnnotationTest {

    private static final ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(new ResourceBundleMessageInterpolator())
        .buildValidatorFactory();
    private static final Validator validator = factory.getValidator();
    
    private Locale originalLocale;

    @BeforeEach
    public void setUp() {
        // 保存当前默认语言环境
        originalLocale = Locale.getDefault();
    }

    @AfterEach
    public void tearDown() {
        // 恢复原来的语言环境
        Locale.setDefault(originalLocale);
    }

    public static class TestEntity {
        @Email
        private String email;
        
        @ChineseIdCard
        private String idCard;

        public TestEntity(String email, String idCard) {
            this.email = email;
            this.idCard = idCard;
        }

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }
    }

    @Test
    public void testEmailAnnotationMessageKey() {
        TestEntity entity = new TestEntity("invalid-email", "123456789012345678");
        
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        
        assertEquals(2, violations.size(), "应该有两个验证错误");
        
        for (ConstraintViolation<TestEntity> violation : violations) {
            String message = violation.getMessage();
            
            // 处理可能的编码问题
            String decodedMessage = message;
            
            // 验证消息不是空的
            assertNotNull(decodedMessage, "错误消息不应该为null");
            assertFalse(decodedMessage.isEmpty(), "错误消息不应该为空");
            // 验证消息不是原始的消息键（这表明消息键已被解析）
            assertFalse(decodedMessage.equals("{io.github.vipxieliang.validx.annotation.email}"), "错误消息不应该是原始的消息键: " + decodedMessage);
            assertFalse(decodedMessage.equals("{io.github.vipxieliang.validx.annotation.chinese.idcard}"), "错误消息不应该是原始的消息键: " + decodedMessage);
        }
    }
    
    @Test
    public void testEnglishMessages() {
        // 设置默认语言环境为英语
        Locale.setDefault(Locale.ENGLISH);
        
        // 创建一个使用英语语言环境的验证器
        ValidatorFactory englishFactory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ResourceBundleMessageInterpolator())
            .buildValidatorFactory();
        
        Validator englishValidator = englishFactory.getValidator();
        
        TestEntity entity = new TestEntity("invalid-email", "123456789012345678");
        
        Set<ConstraintViolation<TestEntity>> violations = englishValidator.validate(entity);
        
        assertEquals(2, violations.size(), "Should have two validation errors");
        
        for (ConstraintViolation<TestEntity> violation : violations) {
            String message = violation.getMessage();
            
            // 处理可能的编码问题
            String decodedMessage = message;
            
            // 验证消息不是空的
            assertNotNull(decodedMessage, "Error message should not be null");
            assertFalse(decodedMessage.isEmpty(), "Error message should not be empty");
            // 验证消息不是原始的消息键
            assertFalse(decodedMessage.equals("{io.github.vipxieliang.validx.annotation.email}"), "Error message should not be the raw message key: " + decodedMessage);
            assertFalse(decodedMessage.equals("{io.github.vipxieliang.validx.annotation.chinese.idcard}"), "Error message should not be the raw message key: " + decodedMessage);
            // 验证消息包含英语字符
            assertTrue(decodedMessage.contains("Invalid") || decodedMessage.contains("invalid") || 
                      decodedMessage.contains("must") || decodedMessage.contains("Must") ||
                      decodedMessage.contains("format") || decodedMessage.contains("Format"),
                      "Message should contain English words: " + decodedMessage);
        }
    }
    
    @Test
    public void testChineseMessages() {
        // 设置默认语言环境为中文
        Locale.setDefault(Locale.CHINESE);
        
        // 创建一个使用中文语言环境的验证器
        ValidatorFactory chineseFactory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ResourceBundleMessageInterpolator())
            .buildValidatorFactory();
        
        Validator chineseValidator = chineseFactory.getValidator();
        
        TestEntity entity = new TestEntity("invalid-email", "123456789012345678");
        
        Set<ConstraintViolation<TestEntity>> violations = chineseValidator.validate(entity);
        
        assertEquals(2, violations.size(), "应该有两个验证错误");
        
        for (ConstraintViolation<TestEntity> violation : violations) {
            String message = violation.getMessage();
            
            // 处理可能的编码问题
            String decodedMessage = message;
            
            // 验证消息不是空的
            assertNotNull(decodedMessage, "错误消息不应该为null");
            assertFalse(decodedMessage.isEmpty(), "错误消息不应该为空");
            // 验证消息不是原始的消息键
            assertFalse(decodedMessage.equals("{io.github.vipxieliang.validx.annotation.email}"), "错误消息不应该是原始的消息键: " + decodedMessage);
            assertFalse(decodedMessage.equals("{io.github.vipxieliang.validx.annotation.chinese.idcard}"), "错误消息不应该是原始的消息键: " + decodedMessage);
            // 验证消息包含中文关键词
            assertTrue(decodedMessage.contains("无效") || 
                       decodedMessage.contains("邮箱") || 
                       decodedMessage.contains("地址") ||
                       decodedMessage.contains("身份证") ||
                       decodedMessage.contains("不正确") ||
                       decodedMessage.contains("格式"),
                       "Message should contain Chinese keywords: " + decodedMessage);
        }
    }
}