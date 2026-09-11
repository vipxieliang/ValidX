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

package io.github.vipxieliang.validx.util;

/**
 * Luhn 算法工具类
 * <p>
 * Luhn（模 10）算法用于校验含校验位的数字串，广泛用于银行卡号（{@code @BankCard}）、
 * ICCID（{@code @ICCID}）、IMEI（{@code @IMEI}）等场景。
 * </p>
 * <p>
 * 通用规则：从右往左遍历，从右边数第二位开始每隔一位翻倍，翻倍结果大于 9 则减 9，
 * 全部累加后对 10 取余为 0 即合法。
 * </p>
 */
public final class LuhnUtils {

    private LuhnUtils() {
    }

    /**
     * 对含校验位的完整数字串执行 Luhn 校验
     *
     * @param digits 仅含数字且已去除分隔符的完整号码（含末位校验位）
     * @return 是否通过 Luhn 校验
     */
    public static boolean isValid(String digits) {
        int sum = 0;
        boolean doubleDigit = false; // 最右侧（校验位）不翻倍
        for (int i = digits.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(digits.charAt(i));
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }
}
