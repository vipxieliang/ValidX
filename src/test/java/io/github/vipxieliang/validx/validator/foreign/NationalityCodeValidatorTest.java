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

package io.github.vipxieliang.validx.validator.foreign;

import io.github.vipxieliang.validx.annotations.NationalityCode;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

public class NationalityCodeValidatorTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public static class TestEntity {
        @NationalityCode
        private String countryCode;

        public TestEntity(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    public static class NumericOnlyEntity {
        @NationalityCode(formats = NationalityCode.NationalityCodeType.NUMERIC)
        private String nationalityCode;

        public NumericOnlyEntity(String nationalityCode) {
            this.nationalityCode = nationalityCode;
        }

        public String getNationalityCode() {
            return nationalityCode;
        }
    }

    public static class Alpha2OnlyEntity {
        @NationalityCode(formats = NationalityCode.NationalityCodeType.ALPHA_2)
        private String countryCode;

        public Alpha2OnlyEntity(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    public static class Alpha2Alpha3Entity {
        @NationalityCode(formats = {NationalityCode.NationalityCodeType.ALPHA_2, NationalityCode.NationalityCodeType.ALPHA_3})
        private String countryCode;

        public Alpha2Alpha3Entity(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    /**
     * 通过默认注解（三种形式均可）校验
     */
    private boolean isValid(String value) {
        return validator.validate(new TestEntity(value)).isEmpty();
    }

    /**
     * 通过 {@code formats = NUMERIC} 注解校验
     */
    private boolean isNumericOnlyValid(String value) {
        return validator.validate(new NumericOnlyEntity(value)).isEmpty();
    }

    /**
     * 通过 {@code formats = ALPHA_2} 注解校验
     */
    private boolean isAlpha2OnlyValid(String value) {
        return validator.validate(new Alpha2OnlyEntity(value)).isEmpty();
    }

    /**
     * 通过 {@code formats = {ALPHA_2, ALPHA_3}} 注解校验
     */
    private boolean isAlpha2Alpha3Valid(String value) {
        return validator.validate(new Alpha2Alpha3Entity(value)).isEmpty();
    }

    @Test
    public void testValidNationalityCodes() {
        // 默认三种形式均有效
        assertTrue(isValid("CA"), "两字母代码 CA（加拿大）应该通过验证");
        assertTrue(isValid("CAN"), "三字母代码 CAN（加拿大）应该通过验证");
        assertTrue(isValid("124"), "三位数字代码 124（加拿大）应该通过验证");
        assertTrue(isValid("CN"), "两字母代码 CN（中国）应该通过验证");
        assertTrue(isValid("156"), "三位数字代码 156（中国）应该通过验证");
        assertTrue(isValid("US"), "两字母代码 US（美国）应该通过验证");
        assertTrue(isValid("840"), "三位数字代码 840（美国）应该通过验证");

        // 大小写不敏感
        assertTrue(isValid("ca"), "小写 ca 应该通过验证");
        assertTrue(isValid("Can"), "混合大小写 Can 应该通过验证");
    }

    @Test
    public void testInvalidNationalityCodes() {
        // 无效代码均不通过
        assertFalse(isValid("999"), "未分配的 999 应该不通过验证");
        assertFalse(isValid("000"), "未分配的 000 应该不通过验证");
        assertFalse(isValid("12"), "不足两位的 12 应该不通过验证");
        assertFalse(isValid("CA1"), "CA1 不是有效的两字母或三字母代码");
        assertFalse(isValid("ZZ"), "ZZ 是用户自定义保留代码，应不通过验证");
        assertFalse(isValid("XX"), "XX 不是有效的国家代码");
        assertFalse(isValid("CNH"), "CNH 不是有效的三字母代码（中国为 CHN）");
        assertFalse(isValid("1234"), "四位数 1234 应该不通过验证");
        assertFalse(isValid("ABC"), "ABC 不是有效的三字母代码");
    }

    @Test
    public void testNumericOnlyFormat() {
        // 仅 NUMERIC 形式：字母代码应被拒绝
        assertTrue(isNumericOnlyValid("124"), "仅 NUMERIC 时，三位数字 124 应该通过验证");
        assertFalse(isNumericOnlyValid("CA"), "仅 NUMERIC 时，两字母 CA 应不通过验证");
        assertFalse(isNumericOnlyValid("CAN"), "仅 NUMERIC 时，三字母 CAN 应不通过验证");
    }

    @Test
    public void testAlpha2OnlyFormat() {
        // 仅 ALPHA_2 形式
        assertTrue(isAlpha2OnlyValid("CA"), "仅 ALPHA_2 时，两字母 CA 应该通过验证");
        assertFalse(isAlpha2OnlyValid("124"), "仅 ALPHA_2 时，数字 124 应不通过验证");
    }

    @Test
    public void testAlpha2Alpha3Format() {
        // 仅 ALPHA_2 和 ALPHA_3 两种字母形式：数字应被拒绝
        assertTrue(isAlpha2Alpha3Valid("CA"), "两种字母形式下，两字母 CA 应该通过验证");
        assertTrue(isAlpha2Alpha3Valid("CAN"), "两种字母形式下，三字母 CAN 应该通过验证");
        assertFalse(isAlpha2Alpha3Valid("124"), "两种字母形式下，数字 124 应不通过验证");
    }

    @Test
    public void testNullAndEmptyNationalityCode() {
        // null 和空字符串应视为未填写，不产生约束违规
        assertTrue(isValid(null), "null 应该不产生约束违规");
        assertTrue(isValid(""), "空字符串应该不产生约束违规");
    }
}
