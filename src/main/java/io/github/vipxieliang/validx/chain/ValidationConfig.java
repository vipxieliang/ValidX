/*
 * Copyright 2025-2025 vipxieliang
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

package io.github.vipxieliang.validx.chain;

/**
 * 验证配置类
 * 用于配置全局验证要求
 */
public class ValidationConfig {

    // 预定义配置常量
    public static final ValidationConfig DEFAULT = new ValidationConfig(RequirementMode.NONE);
    public static final ValidationConfig GLOBAL_NOT_NULL = new ValidationConfig(RequirementMode.NOT_NULL);
    public static final ValidationConfig GLOBAL_NOT_EMPTY = new ValidationConfig(RequirementMode.NOT_EMPTY);

    private final RequirementMode requirementMode;

    private ValidationConfig(RequirementMode mode) {
        this.requirementMode = mode;
    }

    public RequirementMode getRequirementMode() {
        return requirementMode;
    }

    /**
     * 要求模式枚举
     */
    public enum RequirementMode {
        NONE,       // 无要求（默认）
        NOT_NULL,   // 要求非null
        NOT_EMPTY   // 要求非null且非空字符串
    }
}
