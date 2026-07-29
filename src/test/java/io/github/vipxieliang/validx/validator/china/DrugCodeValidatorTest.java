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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.DrugCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 药品本位码验证器测试类
 */
public class DrugCodeValidatorTest {

    private final DrugCodeValidator validator = new DrugCodeValidator();

    @Test
    public void testValidDrugCode() {
        // 有效的药品本位码 (示例数据，校验位计算正确)
        assertTrue(validator.isValid("69012345678901234563", null), "69012345678901234563 should be valid");
        assertTrue(validator.isValid("69123456789012345678", null), "69123456789012345678 should be valid");
    }

    @Test
    public void testInvalidDrugCode() {
        // 无效的药品本位码
        assertFalse(validator.isValid("69012345678901234565", null), "校验位不正确应该返回false"); // 校验位错误
        assertFalse(validator.isValid("68012345678901234565", null), "不是69开头应该返回false"); // 不是69开头
        assertFalse(validator.isValid("6901234567890123456", null), "位数不足应该返回false"); // 位数不足
        assertFalse(validator.isValid("690123456789012345678", null), "位数过多应该返回false"); // 位数过多
        assertFalse(validator.isValid("6901234567890123456a", null), "包含非数字字符应该返回false"); // 包含非数字字符
    }

    @Test
    public void testNullAndEmptyDrugCode() {
        // 直接测试验证器，null 和空字符串应该返回 true
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}