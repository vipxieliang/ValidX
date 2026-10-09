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
 * 字符串长度计数模式。
 *
 * <p>用于 {@code @FixedLength} 等需要按"长度"判定但长度计算口径不同的场景。</p>
 *
 * @author vipxieliang
 * @since 2026/09/17
 */
public enum LengthCountMode {

    /**
     * UTF-16 代码单元（{@code String.length()}）。
     *
     * <p>性能最高；覆盖 95% 的常规场景（验证码、流水号、EAN、ISBN、十六进制摘要均落在 ASCII 区）。</p>
     */
    CHAR,

    /**
     * Unicode 码点（{@code String.codePointCount(0, length())}）。
     *
     * <p>处理 BMP 之外的字符（如 😀 U+1F600），一个码点计 1，避免"5 个 emoji 算 10"。</p>
     */
    CODE_POINT,

    /**
     * 字素簇（{@code BreakIterator.getCharacterInstance()}）。
     *
     * <p>最贴近用户感知：带肤色 emoji、ZWJ 序列、家庭组合等都按"1 个字符"计。</p>
     */
    GRAPHEME
}