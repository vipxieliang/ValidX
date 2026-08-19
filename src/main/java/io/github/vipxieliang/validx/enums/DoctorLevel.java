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
 * 执业医师级别代码枚举
 * <p>
 * 对应医师资格证编号第 7 位执业医师级别代码。
 * </p>
 */
public enum DoctorLevel {

    /**
     * 1 - 执业医师
     */
    PRACTICING("1", "执业医师"),

    /**
     * 2 - 执业助理医师
     */
    PRACTICING_ASSISTANT("2", "执业助理医师"),

    /**
     * 3 - 师承或确有专长执业医师
     */
    APPRENTICESHIP_PRACTICING("3", "师承或确有专长执业医师"),

    /**
     * 4 - 师承或确有专长执业助理医师
     */
    APPRENTICESHIP_PRACTICING_ASSISTANT("4", "师承或确有专长执业助理医师");

    private static final Map<String, DoctorLevel> CODE_MAP = new HashMap<>();

    static {
        for (DoctorLevel level : values()) {
            CODE_MAP.put(level.code, level);
        }
    }

    private final String code;
    private final String name;

    DoctorLevel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取执业医师级别代码
     * @return 级别代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取执业医师级别名称
     * @return 级别名称
     */
    public String getName() {
        return name;
    }

    /**
     * 根据级别代码查找枚举（null 安全）
     * @param code 级别代码，如 "1"、"2"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<DoctorLevel> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
