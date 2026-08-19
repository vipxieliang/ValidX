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
 * 注册建造师级别枚举
 * <p>
 * 对应建造师证书编号第 2 位注册建造师级别代码。
 * </p>
 */
public enum ConstructorLevel {

    /**
     * 1 - 一级建造师
     */
    LEVEL_1("1", "一级建造师"),

    /**
     * 2 - 二级建造师
     */
    LEVEL_2("2", "二级建造师");

    private static final Map<String, ConstructorLevel> CODE_MAP = new HashMap<>();

    static {
        for (ConstructorLevel level : values()) {
            CODE_MAP.put(level.code, level);
        }
    }

    private final String code;
    private final String name;

    ConstructorLevel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取注册建造师级别代码
     * @return 级别代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取注册建造师级别名称
     * @return 级别名称
     */
    public String getName() {
        return name;
    }

    /**
     * 根据注册建造师级别代码查找枚举（null 安全）
     * @param code 级别代码，如 "1"、"2"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<ConstructorLevel> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
