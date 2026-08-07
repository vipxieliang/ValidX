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

package io.github.vipxieliang.validx.chain.i18n;

import io.github.vipxieliang.validx.chain.ValidX;
import io.github.vipxieliang.validx.i18n.MessageManager;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotContains 多语言国际化测试
 *
 * @author vipxieliang
 * @since 2026/08/07
 */
public class NotContainsI18nTest {

    @Test
    public void testNotContainsInEnglish() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Contains forbidden substring", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsInChinese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("包含禁止的字符串", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsInJapanese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.JAPANESE);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("禁止された部分文字列を含んでいます", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsInKorean() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.KOREAN);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("금지된 부분 문자열을 포함합니다", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsInFrench() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.FRENCH);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Contient une sous-chaîne interdite", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsInGerman() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.GERMAN);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Enthält verbotene Teilzeichenfolge", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsInRussian() {
        Locale originalLocale = Locale.getDefault();

        try {
            Locale russian = new Locale("ru");
            MessageManager.setCurrentLocale(russian);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Содержит запрещенную подстроку", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsInSpanish() {
        Locale originalLocale = Locale.getDefault();

        try {
            Locale spanish = new Locale("es");
            MessageManager.setCurrentLocale(spanish);
            ValidX validator = ValidX.init().isNotContains("admin_user", new String[]{"admin"});

            assertFalse(validator.passed(), "Should fail when string contains forbidden substring");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Contiene subcadena prohibida", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsPassInAllLanguages() {
        Locale originalLocale = Locale.getDefault();

        try {
            // Test that passing validation works in all languages
            Locale[] locales = {
                Locale.ENGLISH,
                Locale.SIMPLIFIED_CHINESE,
                Locale.JAPANESE,
                Locale.KOREAN,
                Locale.FRENCH,
                Locale.GERMAN,
                new Locale("ru"),
                new Locale("es")
            };

            for (Locale locale : locales) {
                MessageManager.setCurrentLocale(locale);
                ValidX validator = ValidX.init().isNotContains("normaluser", new String[]{"admin", "root"});

                assertTrue(validator.passed(),
                    "Should pass for locale: " + locale.getDisplayName());
                assertEquals(0, validator.getErrors().size());
            }
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testNotContainsWithLocaleSwitch() {
        Locale originalLocale = Locale.getDefault();

        try {
            // Test switching languages mid-validation
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidX validator1 = ValidX.init().isNotContains("admin_user", new String[]{"admin"});
            assertEquals("Contains forbidden substring", validator1.getErrors().get(0));

            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidX validator2 = ValidX.init().isNotContains("admin_user", new String[]{"admin"});
            assertEquals("包含禁止的字符串", validator2.getErrors().get(0));

            MessageManager.setCurrentLocale(Locale.JAPANESE);
            ValidX validator3 = ValidX.init().isNotContains("admin_user", new String[]{"admin"});
            assertEquals("禁止された部分文字列を含んでいます", validator3.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
}
