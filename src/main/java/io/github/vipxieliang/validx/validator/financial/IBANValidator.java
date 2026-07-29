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

package io.github.vipxieliang.validx.validator.financial;

import io.github.vipxieliang.validx.annotations.IBAN;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * IBAN国际银行账户号码验证器
 * 验证国际银行账户号码(IBAN)的格式和校验位
 */
public class IBANValidator implements ConstraintValidator<IBAN, String> {
    
    // IBAN格式正则表达式: 2个字母的国家代码 + 2位校验码 + 最多30位的BBAN
    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]{0,30}$");
    
    // 各国IBAN长度映射表 (部分常见国家)
    private static final java.util.Map<String, Integer> IBAN_LENGTHS = new java.util.HashMap<>();
    
    static {
        IBAN_LENGTHS.put("AD", 24); // 安道尔
        IBAN_LENGTHS.put("AE", 23); // 阿联酋
        IBAN_LENGTHS.put("AL", 28); // 阿尔巴尼亚
        IBAN_LENGTHS.put("AT", 20); // 奥地利
        IBAN_LENGTHS.put("AZ", 28); // 阿塞拜疆
        IBAN_LENGTHS.put("BA", 20); // 波黑
        IBAN_LENGTHS.put("BE", 16); // 比利时
        IBAN_LENGTHS.put("BG", 22); // 保加利亚
        IBAN_LENGTHS.put("BH", 22); // 巴林
        IBAN_LENGTHS.put("BR", 29); // 巴西
        IBAN_LENGTHS.put("BY", 28); // 白俄罗斯
        IBAN_LENGTHS.put("CH", 21); // 瑞士
        IBAN_LENGTHS.put("CR", 22); // 哥斯达黎加
        IBAN_LENGTHS.put("CY", 28); // 塞浦路斯
        IBAN_LENGTHS.put("CZ", 24); // 捷克
        IBAN_LENGTHS.put("DE", 22); // 德国
        IBAN_LENGTHS.put("DK", 18); // 丹麦
        IBAN_LENGTHS.put("DO", 28); // 多米尼加
        IBAN_LENGTHS.put("EE", 20); // 爱沙尼亚
        IBAN_LENGTHS.put("EG", 29); // 埃及
        IBAN_LENGTHS.put("ES", 24); // 西班牙
        IBAN_LENGTHS.put("FI", 18); // 芬兰
        IBAN_LENGTHS.put("FO", 18); // 法罗群岛
        IBAN_LENGTHS.put("FR", 27); // 法国
        IBAN_LENGTHS.put("GB", 22); // 英国
        IBAN_LENGTHS.put("GE", 22); // 格鲁吉亚
        IBAN_LENGTHS.put("GI", 23); // 直布罗陀
        IBAN_LENGTHS.put("GL", 18); // 格陵兰
        IBAN_LENGTHS.put("GR", 27); // 希腊
        IBAN_LENGTHS.put("GT", 28); // 危地马拉
        IBAN_LENGTHS.put("HR", 21); // 克罗地亚
        IBAN_LENGTHS.put("HU", 28); // 匈牙利
        IBAN_LENGTHS.put("IE", 22); // 爱尔兰
        IBAN_LENGTHS.put("IL", 23); // 以色列
        IBAN_LENGTHS.put("IS", 26); // 冰岛
        IBAN_LENGTHS.put("IT", 27); // 意大利
        IBAN_LENGTHS.put("JO", 30); // 约旦
        IBAN_LENGTHS.put("KW", 30); // 科威特
        IBAN_LENGTHS.put("KZ", 20); // 哈萨克斯坦
        IBAN_LENGTHS.put("LB", 28); // 黎巴嫩
        IBAN_LENGTHS.put("LC", 32); // 圣卢西亚
        IBAN_LENGTHS.put("LI", 21); // 列支敦士登
        IBAN_LENGTHS.put("LT", 20); // 立陶宛
        IBAN_LENGTHS.put("LU", 20); // 卢森堡
        IBAN_LENGTHS.put("LV", 21); // 拉脱维亚
        IBAN_LENGTHS.put("MC", 27); // 摩纳哥
        IBAN_LENGTHS.put("MD", 24); // 摩尔多瓦
        IBAN_LENGTHS.put("ME", 22); // 黑山
        IBAN_LENGTHS.put("MK", 19); // 北马其顿
        IBAN_LENGTHS.put("MR", 27); // 毛里塔尼亚
        IBAN_LENGTHS.put("MT", 31); // 马耳他
        IBAN_LENGTHS.put("MU", 30); // 毛里求斯
        IBAN_LENGTHS.put("NL", 18); // 荷兰
        IBAN_LENGTHS.put("NO", 15); // 挪威
        IBAN_LENGTHS.put("PK", 24); // 巴基斯坦
        IBAN_LENGTHS.put("PL", 28); // 波兰
        IBAN_LENGTHS.put("PS", 29); // 巴勒斯坦
        IBAN_LENGTHS.put("PT", 25); // 葡萄牙
        IBAN_LENGTHS.put("QA", 29); // 卡塔尔
        IBAN_LENGTHS.put("RO", 24); // 罗马尼亚
        IBAN_LENGTHS.put("RS", 22); // 塞尔维亚
        IBAN_LENGTHS.put("SA", 24); // 沙特阿拉伯
        IBAN_LENGTHS.put("SE", 24); // 瑞典
        IBAN_LENGTHS.put("SI", 19); // 斯洛文尼亚
        IBAN_LENGTHS.put("SK", 24); // 斯洛伐克
        IBAN_LENGTHS.put("SM", 27); // 圣马力诺
        IBAN_LENGTHS.put("TN", 24); // 突尼斯
        IBAN_LENGTHS.put("TR", 26); // 土耳其
        IBAN_LENGTHS.put("UA", 29); // 乌克兰
        IBAN_LENGTHS.put("VG", 24); // 英属维尔京群岛
        IBAN_LENGTHS.put("XK", 20); // 科索沃
    }

    @Override
    public void initialize(IBAN constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }
        
        // 移除所有空格并转换为大写
        String cleanValue = value.replaceAll("\\s+", "").toUpperCase();
        
        // 基本格式检查
        if (!IBAN_PATTERN.matcher(cleanValue).matches()) {
            return false;
        }
        
        // 长度检查
        String countryCode = cleanValue.substring(0, 2);
        Integer expectedLength = IBAN_LENGTHS.get(countryCode);
        if (expectedLength == null || cleanValue.length() != expectedLength) {
            return false;
        }
        
        // 校验位检查 (MOD-97-10算法)
        return validateIBANChecksum(cleanValue);
    }
    
    /**
     * 使用MOD-97-10算法验证IBAN校验位
     * @param iban IBAN字符串
     * @return 校验结果
     */
    private boolean validateIBANChecksum(String iban) {
        // 将前4位移到末尾
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        
        // 将字母转换为数字 (A=10, B=11, ..., Z=35)
        StringBuilder numericString = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numericString.append((int) c - 'A' + 10);
            } else {
                numericString.append(c);
            }
        }
        
        // 使用MOD-97算法计算校验结果
        return mod97(numericString.toString()) == 1;
    }
    
    /**
     * MOD-97算法实现
     * @param input 数字字符串
     * @return 余数
     */
    private int mod97(String input) {
        int remainder = 0;
        // 分块处理以避免大数问题
        for (int i = 0; i < input.length(); i += 7) {
            int endIndex = Math.min(i + 7, input.length());
            String chunk = input.substring(i, endIndex);
            // 根据块的长度计算乘数
            int power = endIndex - i;
            long multiplier = (long) Math.pow(10, power);
            remainder = (int) ((remainder * multiplier + Long.parseLong(chunk)) % 97);
        }
        return remainder;
    }
}