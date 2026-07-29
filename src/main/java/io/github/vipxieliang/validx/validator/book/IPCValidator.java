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

package io.github.vipxieliang.validx.validator.book;

import io.github.vipxieliang.validx.annotations.IPC;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * IPC验证器
 * 验证字符串是否是有效的IPC（国际专利分类号）
 * IPC格式规则：
 * 1. 部（Section）：1个大写字母（A-H）
 * 2. 大类（Class）：2位数字
 * 3. 小类（Subclass）：1个大写字母
 * 4. 主组（Main group）：1-3位数字 + "/00"
 * 5. 分组（Subgroup）：主组格式 + "/" + 2-4位数字
 */
public class IPCValidator implements ConstraintValidator<IPC, String> {

    // IPC格式的正则表达式
    // 基本格式：部(1字母) + 大类(2数字) + 小类(1字母) + 主组(1-3数字/00) + 分组(可选/2-4数字)
    private static final Pattern IPC_PATTERN = Pattern.compile("^[A-H]\\d{2}[A-Z](\\d{1,3}/[0-2]\\d{1,3})$");

    @Override
    public void initialize(IPC constraintAnnotation) {
        // 初始化逻辑（如果有必要）
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值处理交给@NotNull等其他注解处理
        }

        // 检查基本格式
        if (!IPC_PATTERN.matcher(value).matches()) {
            return false;
        }

        // 验证各部分的合法性
        return validateIPCSegments(value);
    }

    /**
     * 验证IPC各部分的合法性
     * @param ipc IPC字符串
     * @return 是否有效
     */
    private boolean validateIPCSegments(String ipc) {
        // 部（Section）：A-H
        char section = ipc.charAt(0);
        if (section < 'A' || section > 'H') {
            return false;
        }

        // 大类（Class）：00-99
        String clazz = ipc.substring(1, 3);
        try {
            int classNum = Integer.parseInt(clazz);
            if (classNum < 0 || classNum > 99) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        // 小类（Subclass）：A-Z
        char subclass = ipc.charAt(3);
        if (subclass < 'A' || subclass > 'Z') {
            return false;
        }

        // 分割主组和分组部分
        String groupPart = ipc.substring(4);
        String[] groups = groupPart.split("/");
        if (groups.length != 2) {
            return false;
        }

        // 主组（Main group）：1-3位数字
        String mainGroup = groups[0];
        if (mainGroup.length() < 1 || mainGroup.length() > 3) {
            return false;
        }

        try {
            int mainGroupNum = Integer.parseInt(mainGroup);
            if (mainGroupNum < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        // 分组（Subgroup）：00-9999
        String subGroup = groups[1];
        if (subGroup.length() < 1 || subGroup.length() > 4) {
            return false;
        }

        try {
            int subGroupNum = Integer.parseInt(subGroup);
            if (subGroupNum < 0 || subGroupNum > 9999) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}