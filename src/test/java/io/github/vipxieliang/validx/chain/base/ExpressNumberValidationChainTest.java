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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidaX;
import io.github.vipxieliang.validx.chain.ValidXConfig;
import io.github.vipxieliang.validx.annotations.ExpressNumber.ExpressCompany;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExpressNumber链式验证测试类
 */
public class ExpressNumberValidationChainTest {

    // === 顺丰速运测试 ===

    @Test
    public void testValidSFExpress() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012");

        assertTrue(chain.passed(), "12位数字应该是有效的顺丰快递单号");
    }

    @Test
    public void testInvalidSFExpress() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"12345");

        assertFalse(chain.passed(), "5位数字应该无效");
        assertEquals(1, chain.getErrors().size());
    }

    // === 圆通速递测试 ===

    @Test
    public void testValidYTOExpress_WithPrefix() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"YT1234567890123");

        assertTrue(chain.passed(), "YT开头的单号应该有效");
    }

    @Test
    public void testValidYTOExpress_PureNumbers() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"1234567890");

        assertTrue(chain.passed(), "10位纯数字应该有效");
    }

    // === 申通快递测试 ===

    @Test
    public void testValidSTOExpress() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012");

        assertTrue(chain.passed(), "12位数字应该是有效的申通快递单号");
    }

    // === 中通快递测试 ===

    @Test
    public void testValidZTOExpress_Numbers() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012");

        assertTrue(chain.passed(), "12位数字应该是有效的中通快递单号");
    }

    @Test
    public void testValidZTOExpress_AlphaNumeric() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"ZT1234567890");

        assertTrue(chain.passed(), "字母数字组合应该有效");
    }

    // === 韵达快递测试 ===

    @Test
    public void testValidYundaExpress() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"1234567890123");

        assertTrue(chain.passed(), "13位数字应该是有效的韵达快递单号");
    }

    @Test
    public void testInvalidYundaExpress() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"12345"); // 5位太短

        assertFalse(chain.passed(), "5位数字应该无效");
        assertEquals(1, chain.getErrors().size());
    }

    // === 邮政EMS测试 ===

    @Test
    public void testValidEMS_EFormat() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"E123456789CN");

        assertTrue(chain.passed(), "E+9位数字+CN应该有效");
    }

    @Test
    public void testValidEMS_TwoLetterFormat() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"EA123456789CN");

        assertTrue(chain.passed(), "2位字母+9位数字+CN应该有效");
    }

    @Test
    public void testValidEMS_LowerCase() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"e123456789cn");

        assertTrue(chain.passed(), "小写EMS格式应该有效");
    }

    @Test
    public void testInvalidEMS_WrongFormat() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"E12345678"); // 只有8位数字

        assertFalse(chain.passed(), "E+8位数字应该无效");
        assertEquals(1, chain.getErrors().size());
    }

    // === 京东物流测试 ===

    @Test
    public void testValidJDLogistics() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"JD1234567890123");

        assertTrue(chain.passed(), "JD+13位数字应该有效");
    }

    @Test
    public void testValidJDLogistics_15Digits() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"JD123456789012345");

        assertTrue(chain.passed(), "JD+15位数字应该有效");
    }

    @Test
    public void testInvalidJDLogistics_TooShort() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"JD12345"); // JD+5位数字，太短

        assertFalse(chain.passed(), "JD+5位数字应该无效");
        assertEquals(1, chain.getErrors().size());
    }

    // === 德邦快递测试 ===

    @Test
    public void testValidDeppon_8Digits() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"12345678");

        assertTrue(chain.passed(), "8位数字应该是有效的德邦快递单号");
    }

    @Test
    public void testValidDeppon_9Digits() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789");

        assertTrue(chain.passed(), "9位数字应该是有效的德邦快递单号");
    }

    @Test
    public void testInvalidDeppon_TooShort() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"1234567"); // 7位数字，太短

        assertFalse(chain.passed(), "7位数字应该无效");
        assertEquals(1, chain.getErrors().size());
    }

    // === 天天快递测试 ===

    @Test
    public void testValidTTKDExpress() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012");

        assertTrue(chain.passed(), "12位数字应该是有效的天天快递单号");
    }

    @Test
    public void testValidTTKDExpress_14Digits() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"12345678901234");

        assertTrue(chain.passed(), "14位数字应该是有效的天天快递单号");
    }

    // === 百世快递测试 ===

    @Test
    public void testValidBestExpress() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"1234567890");

        assertTrue(chain.passed(), "10位数字或字母应该是有效的百世快递单号");
    }

    @Test
    public void testValidBestExpress_AlphaNumeric() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"BE12345678");

        assertTrue(chain.passed(), "10位字母数字组合应该有效");
    }

    // === 指定快递公司测试 ===

    @Test
    public void testSpecificCompany_SFOnly() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012", ExpressCompany.SF_EXPRESS);

        assertTrue(chain.passed(), "顺丰格式应该有效");
    }

    @Test
    public void testSpecificCompany_SFOnly_Invalid() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"YT1234567890123", ExpressCompany.SF_EXPRESS);

        assertFalse(chain.passed(), "圆通格式对于仅顺丰验证应该无效");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testSpecificCompany_EMSOnly() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"E123456789CN", ExpressCompany.EMS);

        assertTrue(chain.passed(), "EMS格式应该有效");
    }

    @Test
    public void testSpecificCompany_MultipleCompanies() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012", ExpressCompany.SF_EXPRESS, ExpressCompany.STO_EXPRESS);

        assertTrue(chain.passed(), "12位数字应该匹配顺丰或申通");
    }

    // === Null和空值测试 ===

    @Test
    public void testValidExpressNumber_Null() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber(null);

        assertTrue(chain.passed(), "null值应该通过验证");
    }

    @Test
    public void testValidExpressNumber_Empty() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"");

        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    // === 多个验证链式调用测试 ===

    @Test
    public void testMultipleValidations_AllValid() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012")
             .isExpressNumber((Object)"E123456789CN")
             .isExpressNumber((Object)"JD1234567890123");

        assertTrue(chain.passed(), "所有有效的快递单号应该通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testMultipleValidations_SomeInvalid() {
        ValidaX chain = ValidaX.init();
        chain.isExpressNumber((Object)"123456789012")
             .isExpressNumber((Object)"invalid")
             .isExpressNumber((Object)"E123456789CN");

        assertFalse(chain.passed(), "包含无效快递单号的链式调用应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    // === 与配置API结合测试 ===

    @Test
    public void testWithGlobalNotNullConfig() {
        ValidaX chain = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL);

        chain.isExpressNumber(null);

        assertFalse(chain.passed(), "全局NOT_NULL配置下，null值应该失败");
        assertTrue(chain.getErrors().size() > 0);
    }

    @Test
    public void testWithLocalAllowNull() {
        ValidaX chain = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL);

        chain.allowNull().isExpressNumber(null);

        assertTrue(chain.passed(), "局部allowNull应该允许null值通过");
    }

    @Test
    public void testWithGlobalNotEmptyConfig() {
        ValidaX chain = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY);

        chain.isExpressNumber((Object)"");

        assertFalse(chain.passed(), "全局NOT_EMPTY配置下，空字符串应该失败");
        assertTrue(chain.getErrors().size() > 0);
    }

    @Test
    public void testWithLocalAllowEmpty() {
        ValidaX chain = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY);

        chain.allowEmpty().isExpressNumber((Object)"");

        assertTrue(chain.passed(), "局部allowEmpty应该允许空字符串通过");
    }

    @Test
    public void testWithFieldLabel() {
        ValidaX chain = ValidaX.init();
        chain.field("TrackingNumber").isExpressNumber((Object)"invalid");

        assertFalse(chain.passed(), "带字段标签的验证应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    // === 边界值测试 ===

    @Test
    public void testEdgeCase_AllCompanies() {
        ValidaX chain = ValidaX.init();

        // 测试各个快递公司的有效单号
        chain.isExpressNumber((Object)"123456789012")      // 顺丰/申通/中通
             .isExpressNumber((Object)"YT1234567890123")   // 圆通
             .isExpressNumber((Object)"1234567890123")     // 韵达
             .isExpressNumber((Object)"E123456789CN")      // EMS
             .isExpressNumber((Object)"JD1234567890123")   // 京东
             .isExpressNumber((Object)"12345678")          // 德邦
             .isExpressNumber((Object)"123456789012")      // 天天
             .isExpressNumber((Object)"1234567890");       // 百世

        assertTrue(chain.passed(), "所有快递公司的有效单号应该通过验证");
    }
}
