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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.annotations.FixedLength;
import io.github.vipxieliang.validx.enums.LengthCountMode;
import io.github.vipxieliang.validx.util.StringStripUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.BreakIterator;

/**
 * 定长字符串验证器。
 *
 * <p>支持：</p>
 * <ul>
 *     <li>{@link LengthCountMode#CHAR} —— {@code String.length()}</li>
 *     <li>{@link LengthCountMode#CODE_POINT} —— {@code String.codePointCount(...)}</li>
 *     <li>{@link LengthCountMode#GRAPHEME} —— {@code BreakIterator} 字素簇</li>
 * </ul>
 *
 * <p>支持 trimStart / trimEnd 开关，校验器默认不改输入。</p>
 */
public class FixedLengthValidator implements ConstraintValidator<FixedLength, String> {

    private int length;
    private boolean trimStart;
    private boolean trimEnd;
    private LengthCountMode countMode;

    @Override
    public void initialize(FixedLength constraintAnnotation) {
        this.length = constraintAnnotation.length();
        this.trimStart = constraintAnnotation.trimStart();
        this.trimEnd = constraintAnnotation.trimEnd();
        this.countMode = constraintAnnotation.countMode();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值放行，由 @NotBlank / @NotNull 处理
        }

        String v = StringStripUtils.applyTrim(value, trimStart, trimEnd);

        return count(v, countMode) == length;
    }

    /**
     * 按指定口径计算字符串长度。
     */
    private static int count(String value, LengthCountMode mode) {
        switch (mode) {
            case CODE_POINT:
                return value.codePointCount(0, value.length());
            case GRAPHEME:
                return countGraphemes(value);
            case CHAR:
            default:
                return value.length();
        }
    }

    /**
     * 使用 {@link BreakIterator} 统计字素簇个数。
     * 该实现对 emoji、组合字符（带肤色 emoji、ZWJ 序列、家庭组合等）按"1 个字素"计。
     */
    public static int countGraphemes(String value) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(value);
        int n = 0;
        int pos = it.first();
        while (pos != BreakIterator.DONE) {
            int next = it.next();
            if (next == BreakIterator.DONE) {
                break;
            }
            n++;
            pos = next;
        }
        return n;
    }
}