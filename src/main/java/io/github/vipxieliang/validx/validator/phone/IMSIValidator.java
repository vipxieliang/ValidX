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

import io.github.vipxieliang.validx.annotations.IMSI;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * IMSI 验证器
 * <p>
 * 验证字符串是否为有效的国际移动用户识别码（IMSI）。
 * IMSI 结构：{@code MCC(3位) + MNC(2~3位) + MSIN(≤10位)}，最长 15 位纯数字；
 * 部分场景（MNC 2 位 + MSIN 9 位）为 14 位，故放行 14~15 位。
 * IMSI 无内置校验位，仅做长度与字符集校验。
 * </p>
 */
public class IMSIValidator implements ConstraintValidator<IMSI, String> {

    @Override
    public void initialize(IMSI constraintAnnotation) {
        // 无参数，无需初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }

        // 移除可能存在的分隔符（空格、连字符等）
        String cleanIMSI = value.replaceAll("[\\s\\-]+", "");

        // IMSI 长度为 14~15 位
        if (cleanIMSI.length() < 14 || cleanIMSI.length() > 15) {
            return false;
        }

        // 检查是否全为数字
        return cleanIMSI.matches("\\d+");
    }
}
