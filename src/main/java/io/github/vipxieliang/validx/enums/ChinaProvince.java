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
 * 中国行政区划省份编码枚举
 * <p>
 * 对应身份证号码前两位的省级行政区划代码（GB/T 2260 行政区划代码前两位）
 */
public enum ChinaProvince {

    /**
     * 北京市
     */
    BEIJING("11", "北京市"),

    /**
     * 天津市
     */
    TIANJIN("12", "天津市"),

    /**
     * 河北省
     */
    HEBEI("13", "河北省"),

    /**
     * 山西省
     */
    SHANXI("14", "山西省"),

    /**
     * 内蒙古自治区
     */
    INNER_MONGOLIA("15", "内蒙古自治区"),

    /**
     * 辽宁省
     */
    LIAONING("21", "辽宁省"),

    /**
     * 吉林省
     */
    JILIN("22", "吉林省"),

    /**
     * 黑龙江省
     */
    HEILONGJIANG("23", "黑龙江省"),

    /**
     * 上海市
     */
    SHANGHAI("31", "上海市"),

    /**
     * 江苏省
     */
    JIANGSU("32", "江苏省"),

    /**
     * 浙江省
     */
    ZHEJIANG("33", "浙江省"),

    /**
     * 安徽省
     */
    ANHUI("34", "安徽省"),

    /**
     * 福建省
     */
    FUJIAN("35", "福建省"),

    /**
     * 江西省
     */
    JIANGXI("36", "江西省"),

    /**
     * 山东省
     */
    SHANDONG("37", "山东省"),

    /**
     * 河南省
     */
    HENAN("41", "河南省"),

    /**
     * 湖北省
     */
    HUBEI("42", "湖北省"),

    /**
     * 湖南省
     */
    HUNAN("43", "湖南省"),

    /**
     * 广东省
     */
    GUANGDONG("44", "广东省"),

    /**
     * 广西壮族自治区
     */
    GUANGXI("45", "广西壮族自治区"),

    /**
     * 海南省
     */
    HAINAN("46", "海南省"),

    /**
     * 重庆市
     */
    CHONGQING("50", "重庆市"),

    /**
     * 四川省
     */
    SICHUAN("51", "四川省"),

    /**
     * 贵州省
     */
    GUIZHOU("52", "贵州省"),

    /**
     * 云南省
     */
    YUNNAN("53", "云南省"),

    /**
     * 西藏自治区
     */
    TIBET("54", "西藏自治区"),

    /**
     * 陕西省
     */
    SHAANXI("61", "陕西省"),

    /**
     * 甘肃省
     */
    GANSU("62", "甘肃省"),

    /**
     * 青海省
     */
    QINGHAI("63", "青海省"),

    /**
     * 宁夏回族自治区
     */
    NINGXIA("64", "宁夏回族自治区"),

    /**
     * 新疆维吾尔自治区
     */
    XINJIANG("65", "新疆维吾尔自治区"),

    /**
     * 台湾省
     */
    TAIWAN("71", "台湾省"),

    /**
     * 香港特别行政区
     */
    HONG_KONG("81", "香港特别行政区"),

    /**
     * 澳门特别行政区
     */
    MACAU("82", "澳门特别行政区");

    private final String code;

    private final String name;

    // 编码查找表
    private static final Map<String, ChinaProvince> CODE_MAP = new HashMap<>();

    static {
        for (ChinaProvince province : values()) {
            CODE_MAP.put(province.code, province);
        }
    }

    ChinaProvince(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取行政区划编码（前两位）
     * @return 行政区划编码，如 "11"
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取省级行政区名称
     * @return 省级行政区名称，如 "北京市"
     */
    public String getName() {
        return name;
    }

    /**
     * 根据行政区划编码（前两位）查找省份枚举
     * @param code 两位行政区划编码，如 "11"
     * @return 对应的省份枚举，不存在时返回 empty
     */
    public static Optional<ChinaProvince> fromCode(String code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
