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
 * 教师资格类型代码枚举
 * <p>
 * 对应教师资格证编号第 10 位教师资格类型代码。
 * </p>
 */
public enum TeacherType {

    /**
     * 1 - 幼儿园教师资格
     */
    KINDERGARTEN("1", "幼儿园教师资格"),

    /**
     * 2 - 小学教师资格
     */
    PRIMARY_SCHOOL("2", "小学教师资格"),

    /**
     * 3 - 初级中学教师资格
     */
    JUNIOR_HIGH_SCHOOL("3", "初级中学教师资格"),

    /**
     * 4 - 高级中学教师资格
     */
    SENIOR_HIGH_SCHOOL("4", "高级中学教师资格"),

    /**
     * 5 - 中等职业学校教师资格
     */
    SECONDARY_VOCATIONAL_SCHOOL("5", "中等职业学校教师资格"),

    /**
     * 6 - 中等职业学校实习指导教师资格
     */
    SECONDARY_VOCATIONAL_SCHOOL_INTERN_GUIDE("6", "中等职业学校实习指导教师资格"),

    /**
     * 7 - 高等学校教师资格
     */
    HIGHER_EDUCATION_INSTITUTION("7", "高等学校教师资格");

    private static final Map<String, TeacherType> CODE_MAP = new HashMap<>();

    static {
        for (TeacherType type : values()) {
            CODE_MAP.put(type.code, type);
        }
    }

    private final String code;
    private final String name;

    TeacherType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取教师资格类型代码
     * @return 教师资格类型代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取教师资格类型名称
     * @return 教师资格类型名称
     */
    public String getName() {
        return name;
    }

    /**
     * 根据教师资格类型代码查找枚举（null 安全）
     * @param code 教师资格类型代码，如 "1"、"2"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<TeacherType> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
