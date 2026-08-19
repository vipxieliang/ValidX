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

/**
 * PMP 认证证书前缀枚举
 * <p>
 * 对应 PMP 证书编号中表示认证类型或机构标识的前缀。
 * </p>
 */
public enum PMPPrefix {

    /**
     * PMP - 项目管理专业人士认证
     */
    PMP("PMP"),

    /**
     * PMI - 项目管理协会
     */
    PMI("PMI"),

    /**
     * CITEF - 特定机构标识
     */
    CITEF("CITEF"),

    /**
     * CAPM - 项目管理助理认证
     */
    CAPM("CAPM"),

    /**
     * PgMP - 项目集管理专业人士认证
     */
    PGMP("PgMP"),

    /**
     * PfMP - 项目组合管理专业人士认证
     */
    PFMP("PfMP");

    private final String value;

    PMPPrefix(String value) {
        this.value = value;
    }

    /**
     * 获取前缀字符串
     * @return 前缀，如 "PMP"、"PgMP"
     */
    public String getValue() {
        return value;
    }
}
