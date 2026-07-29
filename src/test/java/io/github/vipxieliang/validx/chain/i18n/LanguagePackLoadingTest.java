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

package io.github.vipxieliang.validx.chain.i18n;

import io.github.vipxieliang.validx.i18n.MessageManager;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class LanguagePackLoadingTest {

    @Test
    public void testLanguagePackLoading() {
        // 测试英文消息是否能正确加载
        String englishMessage = MessageManager.getMessage("io.github.vipxieliang.validx.annotation.email", Locale.ENGLISH);
        assertEquals("Invalid email address format", englishMessage);

        // 测试中文消息是否能正确加载
        String chineseMessage = MessageManager.getMessage("io.github.vipxieliang.validx.annotation.email", Locale.SIMPLIFIED_CHINESE);
        assertEquals("邮箱地址格式不正确", chineseMessage);
    }
}