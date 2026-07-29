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

package io.github.vipxieliang.validx.chain.config;

import io.github.vipxieliang.validx.chain.ValidXConfig;
import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationConfigTest {

    // 测试默认行为（无配置）
    @Test
    public void testDefaultBehavior() {
        ValidaX validator = ValidaX.init()
            .isChinesePhone((Object)null)
            .isEmail((Object)"");

        assertTrue(validator.isValid(), "默认情况下null和空字符串应该通过验证");
    }

    // 测试局部状态 - notNull()
    @Test
    public void testLocalNotNull() {
        ValidaX validator = ValidaX.init()
            .notNull().isChinesePhone((Object)null);

        assertFalse(validator.isValid(), "notNull()要求字段非null");
        assertFalse(validator.getErrorMessage().isEmpty(), "应该有错误消息");
        // 错误消息可能是中文或英文，检查是否包含关键词
        String errorMsg = validator.getErrorMessage();
        assertTrue(errorMsg.contains("空") || errorMsg.contains("null"),
            "错误消息应该提到null或空: " + errorMsg);
    }

    @Test
    public void testLocalNotNullAllowsEmpty() {
        ValidaX validator = ValidaX.init()
            .notNull().isChinesePhone((Object)"");

        assertTrue(validator.isValid(), "notNull()允许空字符串");
    }

    // 测试局部状态 - notEmpty()
    @Test
    public void testLocalNotEmpty() {
        ValidaX validator = ValidaX.init()
            .notEmpty().isChinesePhone((Object)null);

        assertFalse(validator.isValid(), "notEmpty()要求字段非null");
    }

    @Test
    public void testLocalNotEmptyRejectsEmpty() {
        ValidaX validator = ValidaX.init()
            .notEmpty().isChinesePhone((Object)"");

        assertFalse(validator.isValid(), "notEmpty()不允许空字符串");
        assertFalse(validator.getErrorMessage().isEmpty(), "应该有错误消息");
        // 错误消息可能是中文或英文
        String errorMsg = validator.getErrorMessage();
        assertTrue(errorMsg.contains("空字符串") || errorMsg.contains("empty"),
            "错误消息应该提到empty或空字符串: " + errorMsg);
    }

    // 测试全局配置 - GLOBAL_NOT_NULL
    @Test
    public void testGlobalNotNull() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL)
            .isChinesePhone((Object)null)
            .isEmail((Object)null);

        assertFalse(validator.isValid(), "全局NOT_NULL配置要求所有字段非null");
        assertEquals(2, validator.getErrors().size(), "两个null值应该产生两个错误");
    }

    @Test
    public void testGlobalNotNullAllowsEmpty() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL)
            .isChinesePhone((Object)"")
            .isEmail((Object)"");

        assertTrue(validator.isValid(), "全局NOT_NULL允许空字符串");
    }

    // 测试全局配置 - GLOBAL_NOT_EMPTY
    @Test
    public void testGlobalNotEmpty() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)
            .isChinesePhone((Object)null)
            .isEmail((Object)"");

        assertFalse(validator.isValid(), "全局NOT_EMPTY不允许null和空字符串");
        assertEquals(2, validator.getErrors().size());
    }

    // 测试局部覆盖全局 - allowNull()
    @Test
    public void testAllowNullOverridesGlobal() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL)
            .isChinesePhone((Object)"13812345678")  // 使用全局配置
            .allowNull().isEmail((Object)null);     // 局部覆盖

        assertTrue(validator.isValid(), "allowNull()应该覆盖全局配置");
    }

    // 测试局部覆盖全局 - allowEmpty()
    @Test
    public void testAllowEmptyOverridesGlobal() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)
            .isChinesePhone((Object)"13812345678")  // 使用全局配置
            .allowEmpty().isEmail((Object)"");      // 局部覆盖（允许空字符串）

        assertTrue(validator.isValid(), "allowEmpty()应该允许空字符串");
    }

    @Test
    public void testAllowEmptyStillRejectsNull() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)
            .allowEmpty().isEmail((Object)null);

        assertFalse(validator.isValid(), "allowEmpty()仍然要求非null");
    }

    // 测试 allowEmpty() 的独立行为
    @Test
    public void testAllowEmptyWithoutGlobalConfig() {
        // allowEmpty() 允许空字符串
        ValidaX validator1 = ValidaX.init()
            .allowEmpty().isEmail((Object)"");
        assertTrue(validator1.isValid(), "allowEmpty()应该允许空字符串");

        // allowEmpty() 不允许 null
        ValidaX validator2 = ValidaX.init()
            .allowEmpty().isEmail((Object)null);
        assertFalse(validator2.isValid(), "allowEmpty()不允许null");
    }

    // 测试优先级：局部 > 全局 > 默认
    @Test
    public void testPriorityOrder() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL)
            .isEmail((Object)"test@example.com")      // 使用全局配置
            .notEmpty().isChinesePhone((Object)"")    // 局部状态覆盖全局
            .allowNull().isQQ((Object)null);          // 局部覆盖全局

        assertFalse(validator.isValid(), "notEmpty()应该拒绝空字符串");
        assertEquals(1, validator.getErrors().size(), "只有isChinesePhone应该失败");
    }

    // 测试field()方法
    @Test
    public void testCustomFieldLabel() {
        ValidaX validator = ValidaX.init()
            .notNull().field("用户邮箱").isEmail((Object)null);

        assertFalse(validator.isValid());
        assertTrue(validator.getErrorMessage().contains("用户邮箱"), "错误消息应该包含自定义字段标识");
    }

    @Test
    public void testDefaultFieldLabel() {
        ValidaX validator = ValidaX.init()
            .notNull().isEmail((Object)null);

        assertFalse(validator.isValid());
        assertTrue(validator.getErrorMessage().contains("Email"), "错误消息应该包含默认字段标识");
    }

    // 测试状态重置
    @Test
    public void testLocalStateReset() {
        ValidaX validator = ValidaX.init()
            .notNull().isEmail((Object)"test@example.com")  // notNull()影响这个
            .isChinesePhone((Object)null);                   // 状态已重置，允许null

        assertTrue(validator.isValid(), "局部状态应该在使用后重置");
    }

    // 测试混合场景
    @Test
    public void testMixedScenario() {
        ValidaX validator = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL)
            .field("邮箱").isEmail((Object)"test@example.com")
            .field("手机").isChinesePhone((Object)"13812345678")
            .field("身份证").notEmpty().isChineseIdCard((Object)"")
            .field("QQ").allowNull().isQQ((Object)null)
            .field("微信").isWeChat((Object)"wechat123");

        assertFalse(validator.isValid(), "身份证为空应该失败");
        assertEquals(1, validator.getErrors().size());
        assertTrue(validator.getErrorMessage().contains("身份证"));
    }

    // 测试isEmpty()对不同类型的处理
    @Test
    public void testIsEmptyWithDifferentTypes() {
        // String
        ValidaX validator1 = ValidaX.init()
            .notEmpty().isEmail((Object)"");
        assertFalse(validator1.isValid(), "空字符串应该被识别为空");

        // 有效字符串
        ValidaX validator2 = ValidaX.init()
            .notEmpty().isEmail((Object)"test@example.com");
        assertTrue(validator2.isValid(), "非空字符串应该通过");
    }
}
