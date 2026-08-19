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
 * 律师执业类别代码枚举
 * <p>
 * 对应律师执业证编号第 10 位律师执业类别代码。
 * </p>
 */
public enum LawyerCategory {

    /**
     * 1 - 专职律师
     */
    FULL_TIME("1", "专职律师"),

    /**
     * 2 - 兼职律师
     */
    PART_TIME("2", "兼职律师"),

    /**
     * 3 - 香港居民律师
     */
    HONG_KONG_RESIDENT("3", "香港居民律师"),

    /**
     * 4 - 澳门居民律师
     */
    MACAO_RESIDENT("4", "澳门居民律师"),

    /**
     * 5 - 台湾居民律师
     */
    TAIWAN_RESIDENT("5", "台湾居民律师"),

    /**
     * 6 - 公职律师
     */
    PUBLIC_OFFICE("6", "公职律师"),

    /**
     * 7 - 公司律师
     */
    CORPORATE("7", "公司律师"),

    /**
     * 8 - 法律援助律师
     */
    LEGAL_AID("8", "法律援助律师"),

    /**
     * 9 - 军队律师
     */
    MILITARY("9", "军队律师");

    private static final Map<String, LawyerCategory> CODE_MAP = new HashMap<>();

    static {
        for (LawyerCategory category : values()) {
            CODE_MAP.put(category.code, category);
        }
    }

    private final String code;
    private final String name;

    LawyerCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取律师执业类别代码
     * @return 类别代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取律师执业类别名称
     * @return 类别名称
     */
    public String getName() {
        return name;
    }

    /**
     * 根据律师执业类别代码查找枚举（null 安全）
     * @param code 类别代码，如 "1"、"2"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<LawyerCategory> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
