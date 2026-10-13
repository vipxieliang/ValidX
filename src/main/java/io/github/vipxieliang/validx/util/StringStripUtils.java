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

package io.github.vipxieliang.validx.util;

/**
 * Java 8 兼容的 strip 工具。
 *
 * <p>JDK 11 起 {@link String} 才提供 {@link String#strip()} /
 * {@link String#stripLeading()} / {@link String#stripTrailing()}，
 * 本项目编译基线为 Java 8，故在此提供语义等价的实现，供
 * {@code @FixedLength} 的 validator 与链式校验复用。</p>
 *
 * <p>与 {@code String.trim()} 的差异：{@code trim()} 仅去除码点 ≤ U+0020 的字符，
 * 而 strip 系列按 {@link Character#isWhitespace(char)} 判定，可正确处理
 * U+00A0（不换行空格）、U+2000~U+200A、U+3000（全角空格）等 Unicode 空白。</p>
 *
 * @author vipxieliang
 * @since 2026/09/17
 */
public final class StringStripUtils {

    private StringStripUtils() {
    }

    /**
     * 按 trimStart / trimEnd 开关对字符串做裁剪。
     *
     * <p>两个开关均为 {@code false} 时原样返回（默认行为：校验器不改输入）。</p>
     *
     * @param s         源字符串，可为 {@code null}
     * @param trimStart 是否去除首部空白
     * @param trimEnd   是否去除尾部空白
     * @return 裁剪后的字符串；{@code s} 为 {@code null} 时返回 {@code null}
     */
    public static String applyTrim(String s, boolean trimStart, boolean trimEnd) {
        if (s == null) {
            return null;
        }
        if (trimStart && trimEnd) {
            return strip(s);
        }
        if (trimStart) {
            return stripLeading(s);
        }
        if (trimEnd) {
            return stripTrailing(s);
        }
        return s;
    }

    /**
     * 等价于 JDK 11 的 {@code String.strip()}。
     */
    public static String strip(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        int len = s.length();
        int start = 0;
        int end = len;
        while (start < end && Character.isWhitespace(s.charAt(start))) {
            start++;
        }
        while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }
        return (start == 0 && end == len) ? s : s.substring(start, end);
    }

    /**
     * 等价于 JDK 11 的 {@code String.stripLeading()}。
     */
    public static String stripLeading(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        int len = s.length();
        int start = 0;
        while (start < len && Character.isWhitespace(s.charAt(start))) {
            start++;
        }
        return start == 0 ? s : s.substring(start);
    }

    /**
     * 等价于 JDK 11 的 {@code String.stripTrailing()}。
     */
    public static String stripTrailing(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        int end = s.length();
        while (end > 0 && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }
        return end == s.length() ? s : s.substring(0, end);
    }
}
