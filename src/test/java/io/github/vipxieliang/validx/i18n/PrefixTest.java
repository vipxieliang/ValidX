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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import java.util.Set;
import java.util.Locale;

public class PrefixTest {

    private static final ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(new ResourceBundleMessageInterpolator())
        .buildValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static class TestEntity {
        @Email
        private String email;

        public TestEntity(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @Test
    public void testPrefixInMessageKeys() {
        // 设置默认语言环境为英语
        Locale.setDefault(Locale.ENGLISH);
        
        // 创建一个使用英语语言环境的验证器
        ValidatorFactory englishFactory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ResourceBundleMessageInterpolator())
            .buildValidatorFactory();
        Validator englishValidator = englishFactory.getValidator();
        
        TestEntity entity = new TestEntity("invalid-email-format");
        
        Set<ConstraintViolation<TestEntity>> violations = englishValidator.validate(entity);
        
        assertEquals(1, violations.size(), "Should have one validation error");
        
        ConstraintViolation<TestEntity> violation = violations.iterator().next();
        String messageTemplate = violation.getMessageTemplate();
        String message = violation.getMessage();
        
        // 验证消息模板包含正确的前缀
        assertTrue(messageTemplate.startsWith("{io.github.vipxieliang."), 
                  "Message template should start with the prefix: " + messageTemplate);
        
        // 验证消息模板以'}'结尾
        assertTrue(messageTemplate.endsWith("}"), 
                  "Message template should end with '}'");
        
        // 验证实际的错误消息是英文提示语
        assertEquals("Invalid email address format", message, 
                    "Message should be 'Invalid email address format': " + message);
        
        System.out.println("Message template: " + messageTemplate);
        System.out.println("Actual message: " + message);
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
        
        TestEntity entity = new TestEntity("invalid-email-format");
        
        Set<ConstraintViolation<TestEntity>> violations = chineseValidator.validate(entity);
        
        assertEquals(1, violations.size(), "应该有一个验证错误");
        
        ConstraintViolation<TestEntity> violation = violations.iterator().next();
        String messageTemplate = violation.getMessageTemplate();
        String message = violation.getMessage();
        
        // 验证消息模板包含正确的前缀
        assertTrue(messageTemplate.startsWith("{io.github.vipxieliang."), 
                  "消息模板应以指定前缀开头: " + messageTemplate);
        
        // 验证实际的错误消息是中文提示语
        assertEquals("邮箱地址格式不正确", message, 
                    "消息应为'邮箱地址格式不正确': " + message);
        
        System.out.println("消息模板: " + messageTemplate);
        System.out.println("实际消息: " + message);
    }
}