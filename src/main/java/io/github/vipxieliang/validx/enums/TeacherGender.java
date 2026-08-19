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
 * 教师资格证性别代码枚举
 * <p>
 * 对应教师资格证编号第 11 位性别代码。
 * </p>
 */
public enum TeacherGender {

    /**
     * 0 - 男性
     */
    MALE("0", "男性"),

    /**
     * 1 - 女性
     */
    FEMALE("1", "女性"),

    /**
     * 2 - 未知
     */
    UNKNOWN("2", "未知");

    private static final Map<String, TeacherGender> CODE_MAP = new HashMap<>();

    static {
        for (TeacherGender gender : values()) {
            CODE_MAP.put(gender.code, gender);
        }
    }

    private final String code;
    private final String name;

    TeacherGender(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取性别代码
     * @return 性别代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取性别名称
     * @return 性别名称
     */
    public String getName() {
        return name;
    }

    /**
     * 根据性别代码查找枚举（null 安全）
     * @param code 性别代码，如 "0"、"1"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<TeacherGender> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
