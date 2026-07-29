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

import io.github.vipxieliang.validx.annotations.DrugApproval;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 药品批准文号验证器测试类
 */
public class DrugApprovalValidatorTest {

    private final DrugApprovalValidator validator = new DrugApprovalValidator();

    @Test
    public void testValidDrugApproval() {
        // 有效的药品批准文号
        assertTrue(validator.isValid("国药准字H20210039", null), "国药准字H20210039 should be valid");
        assertTrue(validator.isValid("国药准字Z20200001", null), "国药准字Z20200001 should be valid");
        assertTrue(validator.isValid("国药准字S20190001", null), "国药准字S20190001 should be valid");
        assertTrue(validator.isValid("国药准字J20180001", null), "国药准字J20180001 should be valid");
        assertTrue(validator.isValid("国药准字HC20171003", null), "国药准字HC20171003 should be valid");
        assertTrue(validator.isValid("国药准字ZC20171003", null), "国药准字ZC20171003 should be valid");
        assertTrue(validator.isValid("国药准字SC20171003", null), "国药准字SC20171003 should be valid");
        assertTrue(validator.isValid("国药准字HJ20233150", null), "国药准字HJ20233150 should be valid");
        assertTrue(validator.isValid("国药准字ZJ20233150", null), "国药准字ZJ20233150 should be valid");
    }

    @Test
    public void testInvalidDrugApproval() {
        // 无效的药品批准文号
        assertFalse(validator.isValid("国药准字X20210039", null), "无效的药品类别X应该返回false");
        assertFalse(validator.isValid("国药准子H20210039", null), "错别字应该返回false");
        assertFalse(validator.isValid("国药准字H210039", null), "数字位数不正确应该返回false");
        assertFalse(validator.isValid("国药准字H202100391", null), "数字位数不正确应该返回false");
        assertFalse(validator.isValid("药准字H20210039", null), "缺少国字应该返回false");
        assertFalse(validator.isValid("国药准字", null), "缺少详细信息应该返回false");
        assertFalse(validator.isValid("国药准字H", null), "缺少数字应该返回false");
    }

    @Test
    public void testNullAndEmptyDrugApproval() {
        // 直接测试验证器，null 和空字符串应该返回 true
        assertTrue(validator.isValid(null, null), "null should return true");
        assertTrue(validator.isValid("", null), "empty string should return true");
    }
}