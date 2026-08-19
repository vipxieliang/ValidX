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

package io.github.vipxieliang.validx.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * IBAN 支持的国家/地区代码枚举
 * <p>
 * 对应 IBAN 国际银行账户号码前 2 位国家代码及该国 IBAN 标准长度。
 * </p>
 */
public enum IbanCountry {

    /**
     * AD - 安道尔 (24)
     */
    ANDORRA("AD", "安道尔", 24),

    /**
     * AE - 阿联酋 (23)
     */
    UNITED_ARAB_EMIRATES("AE", "阿联酋", 23),

    /**
     * AL - 阿尔巴尼亚 (28)
     */
    ALBANIA("AL", "阿尔巴尼亚", 28),

    /**
     * AT - 奥地利 (20)
     */
    AUSTRIA("AT", "奥地利", 20),

    /**
     * AZ - 阿塞拜疆 (28)
     */
    AZERBAIJAN("AZ", "阿塞拜疆", 28),

    /**
     * BA - 波黑 (20)
     */
    BOSNIA_AND_HERZEGOVINA("BA", "波黑", 20),

    /**
     * BE - 比利时 (16)
     */
    BELGIUM("BE", "比利时", 16),

    /**
     * BG - 保加利亚 (22)
     */
    BULGARIA("BG", "保加利亚", 22),

    /**
     * BH - 巴林 (22)
     */
    BAHRAIN("BH", "巴林", 22),

    /**
     * BR - 巴西 (29)
     */
    BRAZIL("BR", "巴西", 29),

    /**
     * BY - 白俄罗斯 (28)
     */
    BELARUS("BY", "白俄罗斯", 28),

    /**
     * CH - 瑞士 (21)
     */
    SWITZERLAND("CH", "瑞士", 21),

    /**
     * CR - 哥斯达黎加 (22)
     */
    COSTA_RICA("CR", "哥斯达黎加", 22),

    /**
     * CY - 塞浦路斯 (28)
     */
    CYPRUS("CY", "塞浦路斯", 28),

    /**
     * CZ - 捷克 (24)
     */
    CZECHIA("CZ", "捷克", 24),

    /**
     * DE - 德国 (22)
     */
    GERMANY("DE", "德国", 22),

    /**
     * DK - 丹麦 (18)
     */
    DENMARK("DK", "丹麦", 18),

    /**
     * DO - 多米尼加 (28)
     */
    DOMINICAN_REPUBLIC("DO", "多米尼加", 28),

    /**
     * EE - 爱沙尼亚 (20)
     */
    ESTONIA("EE", "爱沙尼亚", 20),

    /**
     * EG - 埃及 (29)
     */
    EGYPT("EG", "埃及", 29),

    /**
     * ES - 西班牙 (24)
     */
    SPAIN("ES", "西班牙", 24),

    /**
     * FI - 芬兰 (18)
     */
    FINLAND("FI", "芬兰", 18),

    /**
     * FO - 法罗群岛 (18)
     */
    FAROE_ISLANDS("FO", "法罗群岛", 18),

    /**
     * FR - 法国 (27)
     */
    FRANCE("FR", "法国", 27),

    /**
     * GB - 英国 (22)
     */
    UNITED_KINGDOM("GB", "英国", 22),

    /**
     * GE - 格鲁吉亚 (22)
     */
    GEORGIA("GE", "格鲁吉亚", 22),

    /**
     * GI - 直布罗陀 (23)
     */
    GIBRALTAR("GI", "直布罗陀", 23),

    /**
     * GL - 格陵兰 (18)
     */
    GREENLAND("GL", "格陵兰", 18),

    /**
     * GR - 希腊 (27)
     */
    GREECE("GR", "希腊", 27),

    /**
     * GT - 危地马拉 (28)
     */
    GUATEMALA("GT", "危地马拉", 28),

    /**
     * HR - 克罗地亚 (21)
     */
    CROATIA("HR", "克罗地亚", 21),

    /**
     * HU - 匈牙利 (28)
     */
    HUNGARY("HU", "匈牙利", 28),

    /**
     * IE - 爱尔兰 (22)
     */
    IRELAND("IE", "爱尔兰", 22),

    /**
     * IL - 以色列 (23)
     */
    ISRAEL("IL", "以色列", 23),

    /**
     * IS - 冰岛 (26)
     */
    ICELAND("IS", "冰岛", 26),

    /**
     * IT - 意大利 (27)
     */
    ITALY("IT", "意大利", 27),

    /**
     * JO - 约旦 (30)
     */
    JORDAN("JO", "约旦", 30),

    /**
     * KW - 科威特 (30)
     */
    KUWAIT("KW", "科威特", 30),

    /**
     * KZ - 哈萨克斯坦 (20)
     */
    KAZAKHSTAN("KZ", "哈萨克斯坦", 20),

    /**
     * LB - 黎巴嫩 (28)
     */
    LEBANON("LB", "黎巴嫩", 28),

    /**
     * LC - 圣卢西亚 (32)
     */
    SAINT_LUCIA("LC", "圣卢西亚", 32),

    /**
     * LI - 列支敦士登 (21)
     */
    LIECHTENSTEIN("LI", "列支敦士登", 21),

    /**
     * LT - 立陶宛 (20)
     */
    LITHUANIA("LT", "立陶宛", 20),

    /**
     * LU - 卢森堡 (20)
     */
    LUXEMBOURG("LU", "卢森堡", 20),

    /**
     * LV - 拉脱维亚 (21)
     */
    LATVIA("LV", "拉脱维亚", 21),

    /**
     * MC - 摩纳哥 (27)
     */
    MONACO("MC", "摩纳哥", 27),

    /**
     * MD - 摩尔多瓦 (24)
     */
    MOLDOVA("MD", "摩尔多瓦", 24),

    /**
     * ME - 黑山 (22)
     */
    MONTENEGRO("ME", "黑山", 22),

    /**
     * MK - 北马其顿 (19)
     */
    NORTH_MACEDONIA("MK", "北马其顿", 19),

    /**
     * MR - 毛里塔尼亚 (27)
     */
    MAURITANIA("MR", "毛里塔尼亚", 27),

    /**
     * MT - 马耳他 (31)
     */
    MALTA("MT", "马耳他", 31),

    /**
     * MU - 毛里求斯 (30)
     */
    MAURITIUS("MU", "毛里求斯", 30),

    /**
     * NL - 荷兰 (18)
     */
    NETHERLANDS("NL", "荷兰", 18),

    /**
     * NO - 挪威 (15)
     */
    NORWAY("NO", "挪威", 15),

    /**
     * PK - 巴基斯坦 (24)
     */
    PAKISTAN("PK", "巴基斯坦", 24),

    /**
     * PL - 波兰 (28)
     */
    POLAND("PL", "波兰", 28),

    /**
     * PS - 巴勒斯坦 (29)
     */
    PALESTINE("PS", "巴勒斯坦", 29),

    /**
     * PT - 葡萄牙 (25)
     */
    PORTUGAL("PT", "葡萄牙", 25),

    /**
     * QA - 卡塔尔 (29)
     */
    QATAR("QA", "卡塔尔", 29),

    /**
     * RO - 罗马尼亚 (24)
     */
    ROMANIA("RO", "罗马尼亚", 24),

    /**
     * RS - 塞尔维亚 (22)
     */
    SERBIA("RS", "塞尔维亚", 22),

    /**
     * SA - 沙特阿拉伯 (24)
     */
    SAUDI_ARABIA("SA", "沙特阿拉伯", 24),

    /**
     * SE - 瑞典 (24)
     */
    SWEDEN("SE", "瑞典", 24),

    /**
     * SI - 斯洛文尼亚 (19)
     */
    SLOVENIA("SI", "斯洛文尼亚", 19),

    /**
     * SK - 斯洛伐克 (24)
     */
    SLOVAKIA("SK", "斯洛伐克", 24),

    /**
     * SM - 圣马力诺 (27)
     */
    SAN_MARINO("SM", "圣马力诺", 27),

    /**
     * TN - 突尼斯 (24)
     */
    TUNISIA("TN", "突尼斯", 24),

    /**
     * TR - 土耳其 (26)
     */
    TURKEY("TR", "土耳其", 26),

    /**
     * UA - 乌克兰 (29)
     */
    UKRAINE("UA", "乌克兰", 29),

    /**
     * VG - 英属维尔京群岛 (24)
     */
    BRITISH_VIRGIN_ISLANDS("VG", "英属维尔京群岛", 24),

    /**
     * XK - 科索沃 (20)
     */
    KOSOVO("XK", "科索沃", 20);

    private static final Map<String, IbanCountry> CODE_MAP = new HashMap<>();

    static {
        for (IbanCountry country : values()) {
            CODE_MAP.put(country.code, country);
        }
    }

    private final String code;
    private final String name;
    private final int length;

    IbanCountry(String code, String name, int length) {
        this.code = code;
        this.name = name;
        this.length = length;
    }

    /**
     * 获取国家/地区代码
     * @return 两位国家/地区代码，如 "DE"
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取国家/地区名称
     * @return 国家/地区中文名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取该国/地区 IBAN 标准长度
     * @return IBAN 长度
     */
    public int getLength() {
        return length;
    }

    /**
     * 根据国家/地区代码查找枚举（null 安全）
     * @param code 两位国家/地区代码，如 "DE"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<IbanCountry> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
