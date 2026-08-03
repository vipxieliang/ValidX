/*
 * Copyright 2026-2026 vipxieliang
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

package io.github.vipxieliang.validx.validator.china;

import io.github.vipxieliang.validx.annotations.ChineseName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 中国人姓名验证器
 * 验证字符串是否符合中国人姓名规范
 *
 * <p>验证规则：</p>
 * <ul>
 *   <li>只能包含中文字符</li>
 *   <li>长度在 2-50 个字符之间</li>
 *   <li>支持少数民族姓名中的间隔号 "·"</li>
 * </ul>
 *
 * @author vipxieliang
 * @since 2026/08/03
 */
public class ChineseNameValidator implements ConstraintValidator<ChineseName, String> {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;
    private static final Pattern PATTERN = Pattern.compile("^[\u4e00-\u9fa5]+(\u00B7[\u4e00-\u9fa5]+)*$");

    @Override
    public void initialize(ChineseName annotation) {
        // 无需初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 或空字符串认为有效，使用 @NotNull 或 @NotBlank 来验证非空
        if (value == null || value.isEmpty()) {
            return true;
        }

        // 检查长度
        int length = value.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            return false;
        }

        // 检查格式：只允许中文和间隔号，间隔号不能在开头/结尾/连续
        return PATTERN.matcher(value).matches();
    }
}
