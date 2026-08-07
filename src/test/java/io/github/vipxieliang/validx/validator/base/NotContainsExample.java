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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.NotContains;
import io.github.vipxieliang.validx.chain.ValidX;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * NotContains 使用示例
 *
 * @author vipxieliang
 * @since 2026/08/07
 */
public class NotContainsExample {

    // 示例1: 注解方式 - 用户名验证
    static class UserRegistration {
        @NotContains(value = {"admin", "root", "system"}, ignoreCase = true,
                     message = "用户名不能包含敏感词")
        private String username;

        @NotContains(value = {"<script", "javascript:", "onerror="}, ignoreCase = true,
                     message = "内容包含非法字符")
        private String bio;

        public UserRegistration(String username, String bio) {
            this.username = username;
            this.bio = bio;
        }
    }

    // 示例2: 链式验证方式
    public static void chainValidationExample() {
        System.out.println("\n=== 链式验证示例 ===");

        // 安全的输入
        ValidX validator1 = ValidX.init()
                .field("用户名").isNotContains("john_doe", new String[]{"admin", "root"}, true)
                .field("评论").isNotContains("这是一条正常评论", new String[]{"垃圾", "广告"}, true);

        System.out.println("安全输入验证: " + (validator1.passed() ? "✅ 通过" : "❌ 失败"));

        // 包含敏感词的输入
        ValidX validator2 = ValidX.init()
                .field("用户名").isNotContains("admin_user", new String[]{"admin", "root"}, true)
                .field("评论").isNotContains("这是一条正常评论", new String[]{"垃圾", "广告"}, true);

        System.out.println("敏感词输入验证: " + (validator2.passed() ? "✅ 通过" : "❌ 失败"));
        if (!validator2.passed()) {
            System.out.println("  错误信息: " + validator2.getErrors());
        }
    }

    // 示例3: 安全验证场景
    public static void securityValidationExample() {
        System.out.println("\n=== 安全验证示例 ===");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // 正常用户注册
        UserRegistration user1 = new UserRegistration("john_doe", "Hello, I'm a developer");
        Set<ConstraintViolation<UserRegistration>> violations1 = validator.validate(user1);
        System.out.println("正常注册: " + (violations1.isEmpty() ? "✅ 通过" : "❌ 失败"));

        // 尝试使用敏感用户名
        UserRegistration user2 = new UserRegistration("admin123", "Hello world");
        Set<ConstraintViolation<UserRegistration>> violations2 = validator.validate(user2);
        System.out.println("敏感用户名: " + (violations2.isEmpty() ? "✅ 通过" : "❌ 失败"));
        if (!violations2.isEmpty()) {
            violations2.forEach(v -> System.out.println("  - " + v.getMessage()));
        }

        // 尝试XSS攻击
        UserRegistration user3 = new UserRegistration("hacker", "<script>alert('xss')</script>");
        Set<ConstraintViolation<UserRegistration>> violations3 = validator.validate(user3);
        System.out.println("XSS攻击尝试: " + (violations3.isEmpty() ? "✅ 通过" : "❌ 失败"));
        if (!violations3.isEmpty()) {
            violations3.forEach(v -> System.out.println("  - " + v.getMessage()));
        }
    }

    // 示例4: matchAll 参数的使用
    public static void matchAllExample() {
        System.out.println("\n=== matchAll 参数示例 ===");

        // matchAll=true: 必须全部不包含（默认）
        ValidX validator1 = ValidX.init()
                .isNotContains("https://example.com",
                              new String[]{"javascript:", "data:", "vbscript:"},
                              false, true);
        System.out.println("安全URL (matchAll=true): " + (validator1.passed() ? "✅ 通过" : "❌ 失败"));

        ValidX validator2 = ValidX.init()
                .isNotContains("javascript:alert(1)",
                              new String[]{"javascript:", "data:", "vbscript:"},
                              false, true);
        System.out.println("危险URL (matchAll=true): " + (validator2.passed() ? "✅ 通过" : "❌ 失败"));

        // matchAll=false: 只要有一个不包含即可
        ValidX validator3 = ValidX.init()
                .isNotContains("hello world",
                              new String[]{"script", "alert"},
                              false, false);
        System.out.println("普通文本 (matchAll=false): " + (validator3.passed() ? "✅ 通过" : "❌ 失败"));

        ValidX validator4 = ValidX.init()
                .isNotContains("script alert",
                              new String[]{"script", "alert"},
                              false, false);
        System.out.println("同时包含 (matchAll=false): " + (validator4.passed() ? "✅ 通过" : "❌ 失败"));
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    NotContains 验证器使用示例");
        System.out.println("========================================");

        chainValidationExample();
        securityValidationExample();
        matchAllExample();

        System.out.println("\n========================================");
        System.out.println("           示例演示完成");
        System.out.println("========================================");
    }
}
