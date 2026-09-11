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

package io.github.vipxieliang.validx.validator.phone;

import io.github.vipxieliang.validx.annotations.ICCID;
import io.github.vipxieliang.validx.util.LuhnUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ICCID 验证器
 * <p>
 * 验证字符串是否为有效的集成电路卡识别码（ICCID）。
 * 现代 UICC / eSIM 的 ICCID 为 20 位纯数字，末位为 Luhn 校验位
 * （对前 19 位计算得出，可防输入笔误），中国大陆 SIM 卡通常以 {@code 8986} 开头
 * （89 电信行业标识 + 86 中国国家码）。
 * </p>
 * <p>
 * 校验规则：去除空格与连字符后为 20 位纯数字，且整串通过 Luhn 校验。
 * </p>
 */
public class ICCIDValidator implements ConstraintValidator<ICCID, String> {

    @Override
    public void initialize(ICCID constraintAnnotation) {
        // 无参数，无需初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }

        // 移除可能存在的分隔符（空格、连字符等）
        String cleanICCID = value.replaceAll("[\\s\\-]+", "");

        // ICCID 为 20 位
        if (cleanICCID.length() != 20) {
            return false;
        }

        // 检查是否全为数字
        if (!cleanICCID.matches("\\d+")) {
            return false;
        }

        // 末位为 Luhn 校验位，整串通过 Luhn 校验即合法
        return LuhnUtils.isValid(cleanICCID);
    }
}
