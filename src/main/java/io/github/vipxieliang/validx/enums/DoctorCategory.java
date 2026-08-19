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
 * 执业医师类别代码枚举
 * <p>
 * 对应医师资格证编号第 8-9 位执业医师类别代码。
 * </p>
 */
public enum DoctorCategory {

    /**
     * 10 - 临床
     */
    CLINICAL("10", "临床"),

    /**
     * 20 - 口腔
     */
    STOMATOLOGY("20", "口腔"),

    /**
     * 30 - 公共卫生
     */
    PUBLIC_HEALTH("30", "公共卫生"),

    /**
     * 41 - 中医
     */
    TRADITIONAL_CHINESE_MEDICINE("41", "中医"),

    /**
     * 42 - 中西医结合
     */
    INTEGRATED_CHINESE_WESTERN_MEDICINE("42", "中西医结合"),

    /**
     * 43 - 蒙医
     */
    MONGOLIAN_MEDICINE("43", "蒙医"),

    /**
     * 44 - 藏医
     */
    TIBETAN_MEDICINE("44", "藏医"),

    /**
     * 45 - 维医
     */
    UYGHUR_MEDICINE("45", "维医"),

    /**
     * 46 - 傣医
     */
    DAI_MEDICINE("46", "傣医"),

    /**
     * 47 - 朝医
     */
    KOREAN_MEDICINE("47", "朝医"),

    /**
     * 48 - 壮医
     */
    ZHUANG_MEDICINE("48", "壮医");

    private static final Map<String, DoctorCategory> CODE_MAP = new HashMap<>();

    static {
        for (DoctorCategory category : values()) {
            CODE_MAP.put(category.code, category);
        }
    }

    private final String code;
    private final String name;

    DoctorCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取执业医师类别代码
     * @return 类别代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取执业医师类别名称
     * @return 类别名称
     */
    public String getName() {
        return name;
    }

    /**
     * 根据类别代码查找枚举（null 安全）
     * @param code 类别代码，如 "10"、"41"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<DoctorCategory> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
