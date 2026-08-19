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
 * 会计资格证书类别（等级）代码枚举
 * <p>
 * 对应会计资格证书编号第 11 位证书类别（等级）代码。
 * </p>
 */
public enum AccountantCategory {

    /**
     * 1 - 初级会计师
     */
    JUNIOR("1", "初级会计师"),

    /**
     * 2 - 中级会计师
     */
    INTERMEDIATE("2", "中级会计师"),

    /**
     * 3 - 高级会计师
     */
    SENIOR("3", "高级会计师"),

    /**
     * 4 - 注册会计师
     */
    CERTIFIED_PUBLIC_ACCOUNTANT("4", "注册会计师"),

    /**
     * 5 - 税务师
     */
    TAX_ADVISER("5", "税务师");

    private static final Map<String, AccountantCategory> CODE_MAP = new HashMap<>();

    static {
        for (AccountantCategory category : values()) {
            CODE_MAP.put(category.code, category);
        }
    }

    private final String code;
    private final String name;

    AccountantCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取证书类别代码
     * @return 证书类别代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取证书类别名称
     * @return 证书类别名称
     */
    public String getName() {
        return name;
    }

    /**
     * 根据证书类别代码查找枚举（null 安全）
     * @param code 证书类别代码，如 "1"、"2"
     * @return 匹配的枚举，未找到返回 Optional.empty()
     */
    public static Optional<AccountantCategory> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
