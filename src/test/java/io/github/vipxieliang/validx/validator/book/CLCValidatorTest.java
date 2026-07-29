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

package io.github.vipxieliang.validx.validator.book;

import io.github.vipxieliang.validx.annotations.CLC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLC验证器测试类
 */
public class CLCValidatorTest {

    private final CLCValidator validator = new CLCValidator();

    @Test
    public void testValidCLC() {
        // 有效的CLC分类号
        assertTrue(validator.isValid("A", null), "A should be valid");
        assertTrue(validator.isValid("B", null), "B should be valid");
        assertTrue(validator.isValid("TP", null), "TP should be valid");
        assertTrue(validator.isValid("TP3", null), "TP3 should be valid");
        assertTrue(validator.isValid("TP311", null), "TP311 should be valid");
        assertTrue(validator.isValid("TP311.1", null), "TP311.1 should be valid");
        assertTrue(validator.isValid("TP311.138", null), "TP311.138 should be valid");
        assertTrue(validator.isValid("TP311.138.S6", null), "TP311.138.S6 should be valid");
        assertTrue(validator.isValid("O175.2", null), "O175.2 should be valid");
        assertTrue(validator.isValid("R329.2", null), "R329.2 should be valid");
        assertTrue(validator.isValid("F272.3", null), "F272.3 should be valid");
        assertTrue(validator.isValid("TP311.138.S63", null), "TP311.138.S63 should be valid");
    }

    @Test
    public void testInvalidCLC() {
        // 无效的CLC分类号
        assertFalse(validator.isValid("123", null), "不以字母开头应该返回false"); // 不以字母开头
        assertFalse(validator.isValid("tp311", null), "小写字母开头应该返回false"); // 小写字母开头
        assertFalse(validator.isValid("TP.311", null), "点号位置错误应该返回false"); // 点号位置错误
        assertFalse(validator.isValid("TP311.", null), "以点号结尾应该返回false"); // 以点号结尾
        assertFalse(validator.isValid(".TP311", null), "以点号开头应该返回false"); // 以点号开头
        assertFalse(validator.isValid("TP311.138.", null), "以点号结尾应该返回false"); // 以点号结尾
        assertFalse(validator.isValid("TP-311", null), "包含连字符应该返回false"); // 包含连字符
        assertFalse(validator.isValid("TP 311", null), "包含空格应该返回false"); // 包含空格
        assertFalse(validator.isValid("TP311.S", null), "字母后没有数字应该返回false"); // 字母后没有数字
    }

    @Test
    public void testNullAndEmptyCLC() {
        // 直接测试验证器，null 和空字符串应该返回 true
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}