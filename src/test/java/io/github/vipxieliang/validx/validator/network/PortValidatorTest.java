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

package io.github.vipxieliang.validx.validator.network;

import io.github.vipxieliang.validx.annotations.Port;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PortValidator测试类
 */
public class PortValidatorTest {
    
    private final PortValidator validator = new PortValidator();
    
    @Test
    public void testValidPortValues() {
        // 创建一个模拟的Port注解实例
        Port portAnnotation = new Port() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Port.class;
            }
            
            @Override
            public String message() {
                return "端口号必须在0-65535之间";
            }
            
            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }
            
            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };
        
        validator.initialize(portAnnotation);
        
        // 测试有效的端口号
        assertTrue(validator.isValid(80, null));       // HTTP端口
        assertTrue(validator.isValid(443, null));      // HTTPS端口
        assertTrue(validator.isValid(22, null));       // SSH端口
        assertTrue(validator.isValid(0, null));        // 最小端口号
        assertTrue(validator.isValid(65535, null));    // 最大端口号
        assertTrue(validator.isValid("8080", null));   // 字符串形式的端口号
    }
    
    @Test
    public void testInvalidPortValues() {
        // 创建一个模拟的Port注解实例
        Port portAnnotation = new Port() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Port.class;
            }
            
            @Override
            public String message() {
                return "端口号必须在0-65535之间";
            }
            
            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }
            
            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };
        
        validator.initialize(portAnnotation);
        
        // 测试无效的端口号
        assertFalse(validator.isValid(-1, null));      // 负数端口号
        assertFalse(validator.isValid(65536, null));   // 超出范围的端口号
        assertFalse(validator.isValid("invalid", null)); // 无效的字符串
    }

    @Test
    public void testNullAndEmptyPort() {
        // 创建一个模拟的Port注解实例
        Port portAnnotation = new Port() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Port.class;
            }

            @Override
            public String message() {
                return "端口号必须在0-65535之间";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };

        validator.initialize(portAnnotation);

        // 测试null和空字符串
        assertTrue(validator.isValid(null, null), "null值应该通过验证");
        assertFalse(validator.isValid("", null), "空字符串应该验证失败（与Hibernate Validator行为一致）");
    }
    
    @Test
    public void testEdgeCases() {
        // 创建一个模拟的Port注解实例
        Port portAnnotation = new Port() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Port.class;
            }
            
            @Override
            public String message() {
                return "端口号必须在0-65535之间";
            }
            
            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }
            
            @Override
            public Class<? extends javax.validation.Payload>[] payload() {
                return new Class[0];
            }
        };
        
        validator.initialize(portAnnotation);
        
        // 测试边界情况
        assertTrue(validator.isValid(0, null));        // 边界值：最小端口号
        assertTrue(validator.isValid(65535, null));    // 边界值：最大端口号
        assertFalse(validator.isValid(-1, null));      // 边界值之下
        assertFalse(validator.isValid(65536, null));   // 边界值之上
    }
}