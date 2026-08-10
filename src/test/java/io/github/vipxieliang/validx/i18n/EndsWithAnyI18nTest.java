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
 * EndsWithAny 多语言国际化测试
 *
 * @author vipxieliang
 * @since 1.1.1
 */
public class EndsWithAnyI18nTest {

    @Test
    public void testEndsWithAnyInEnglish() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Does not end with any of the specified strings", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testEndsWithAnyInChinese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("不是以指定的任意一个字符串结尾", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testEndsWithAnyInJapanese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.JAPANESE);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("指定されたいずれかの文字列で終わっていません", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testEndsWithAnyInKorean() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.KOREAN);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("지정된 문자열 중 하나로 끝나지 않습니다", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testEndsWithAnyInFrench() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.FRENCH);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Ne se termine pas par l'une des chaînes spécifiées", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testEndsWithAnyInGerman() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.GERMAN);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Endet nicht mit einer der angegebenen Zeichenfolgen", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testEndsWithAnyInRussian() {
        Locale originalLocale = Locale.getDefault();

        try {
            Locale russian = new Locale("ru");
            MessageManager.setCurrentLocale(russian);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Не заканчивается любой из указанных строк", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testEndsWithAnyInSpanish() {
        Locale originalLocale = Locale.getDefault();

        try {
            Locale spanish = new Locale("es");
            MessageManager.setCurrentLocale(spanish);
            ValidX validator = ValidX.init().isEndsWithAny("document.pdf", new String[]{".jpg", ".png"});

            assertFalse(validator.passed(), "Should fail when string doesn't end with any suffix");
            assertEquals(1, validator.getErrors().size());
            assertEquals("No termina con ninguna de las cadenas especificadas", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
}
