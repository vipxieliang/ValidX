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

package io.github.vipxieliang.validx.i18n;

import io.github.vipxieliang.validx.chain.ValidX;
import io.github.vipxieliang.validx.i18n.MessageManager;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StartsWithAny 多语言国际化测试
 *
 * @author vipxieliang
 * @since 1.1.1
 */
public class StartsWithAnyI18nTest {

    @Test
    public void testStartsWithAnyInEnglish() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Does not start with any of the specified strings", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testStartsWithAnyInChinese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("不是以指定的任意一个字符串开头", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testStartsWithAnyInJapanese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.JAPANESE);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("指定されたいずれかの文字列で始まっていません", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testStartsWithAnyInKorean() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.KOREAN);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("지정된 문자열 중 하나로 시작하지 않습니다", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testStartsWithAnyInFrench() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.FRENCH);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Ne commence pas par l'une des chaînes spécifiées", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testStartsWithAnyInGerman() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.GERMAN);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Beginnt nicht mit einer der angegebenen Zeichenfolgen", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testStartsWithAnyInRussian() {
        Locale originalLocale = Locale.getDefault();

        try {
            Locale russian = new Locale("ru");
            MessageManager.setCurrentLocale(russian);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Не начинается с любой из указанных строк", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testStartsWithAnyInSpanish() {
        Locale originalLocale = Locale.getDefault();

        try {
            Locale spanish = new Locale("es");
            MessageManager.setCurrentLocale(spanish);
            ValidX validator = ValidX.init().isStartsWithAny("ftp://example.com", new String[]{"http://", "https://"});

            assertFalse(validator.passed(), "Should fail when string doesn't start with any prefix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("No comienza con ninguna de las cadenas especificadas", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
}
