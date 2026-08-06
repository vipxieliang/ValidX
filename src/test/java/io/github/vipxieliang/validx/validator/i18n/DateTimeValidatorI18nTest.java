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

package io.github.vipxieliang.validx.validator.i18n;

import io.github.vipxieliang.validx.i18n.MessageManager;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试日期时间验证器的国际化消息是否在所有语言包中正确加载
 * Tests PastDateTime and FutureDateTime annotation message keys
 */
public class DateTimeValidatorI18nTest {

    private static final String PAST_DATETIME_KEY = "io.github.vipxieliang.validx.annotation.past.datetime";
    private static final String FUTURE_DATETIME_KEY = "io.github.vipxieliang.validx.annotation.future.datetime";

    @Test
    public void testDefaultMessages() {
        // Test default language (no locale specified) - should fallback to ValidationMessages.properties (Chinese)
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, Locale.getDefault());
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, Locale.getDefault());

        assertNotNull(pastMessage, "Default past datetime message should exist");
        assertNotNull(futureMessage, "Default future datetime message should exist");
        assertFalse(pastMessage.isEmpty(), "Default past datetime message should not be empty");
        assertFalse(futureMessage.isEmpty(), "Default future datetime message should not be empty");

        // Verify it contains Chinese characters (default is Chinese)
        assertTrue(pastMessage.contains("日期时间"), "Default message should be in Chinese");
        assertTrue(futureMessage.contains("日期时间"), "Default message should be in Chinese");
    }

    @Test
    public void testEnglishMessages() {
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, Locale.ENGLISH);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, Locale.ENGLISH);

        String expectedPastMessage = "Must be a past date time";
        String expectedFutureMessage = "Must be a future date time";

        assertEquals(expectedPastMessage, pastMessage, "English past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "English future datetime message mismatch");
    }

    @Test
    public void testChineseMessages() {
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, Locale.SIMPLIFIED_CHINESE);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, Locale.SIMPLIFIED_CHINESE);

        String expectedPastMessage = "\u65e5\u671f\u65f6\u95f4\u5fc5\u987b\u662f\u8fc7\u53bb\u7684\u65e5\u671f\u65f6\u95f4";
        String expectedFutureMessage = "\u65e5\u671f\u65f6\u95f4\u5fc5\u987b\u662f\u672a\u6765\u7684\u65e5\u671f\u65f6\u95f4";

        assertEquals(expectedPastMessage, pastMessage, "Chinese past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "Chinese future datetime message mismatch");
    }

    @Test
    public void testJapaneseMessages() {
        Locale japaneseLocale = Locale.JAPANESE;
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, japaneseLocale);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, japaneseLocale);

        String expectedPastMessage = "\u904e\u53bb\u306e\u65e5\u6642\u3067\u306a\u3051\u308c\u3070\u306a\u308a\u307e\u305b\u3093";
        String expectedFutureMessage = "\u672a\u6765\u306e\u65e5\u6642\u3067\u306a\u3051\u308c\u3070\u306a\u308a\u307e\u305b\u3093";

        assertEquals(expectedPastMessage, pastMessage, "Japanese past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "Japanese future datetime message mismatch");
    }

    @Test
    public void testKoreanMessages() {
        Locale koreanLocale = Locale.KOREAN;
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, koreanLocale);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, koreanLocale);

        String expectedPastMessage = "\uacfc\uac70\uc758 \ub0a0\uc9dc \uc2dc\uac04\uc774\uc5b4\uc57c \ud569\ub2c8\ub2e4";
        String expectedFutureMessage = "\ubbf8\ub798\uc758 \ub0a0\uc9dc \uc2dc\uac04\uc774\uc5b4\uc57c \ud569\ub2c8\ub2e4";

        assertEquals(expectedPastMessage, pastMessage, "Korean past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "Korean future datetime message mismatch");
    }

    @Test
    public void testFrenchMessages() {
        Locale frenchLocale = Locale.FRENCH;
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, frenchLocale);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, frenchLocale);

        String expectedPastMessage = "Doit être une date et heure passée";
        String expectedFutureMessage = "Doit être une date et heure future";

        assertEquals(expectedPastMessage, pastMessage, "French past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "French future datetime message mismatch");
    }

    @Test
    public void testGermanMessages() {
        Locale germanLocale = Locale.GERMAN;
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, germanLocale);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, germanLocale);

        String expectedPastMessage = "Muss ein vergangenes Datum und Uhrzeit sein";
        String expectedFutureMessage = "Muss ein zukünftiges Datum und Uhrzeit sein";

        assertEquals(expectedPastMessage, pastMessage, "German past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "German future datetime message mismatch");
    }

    @Test
    public void testSpanishMessages() {
        Locale spanishLocale = new Locale("es");
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, spanishLocale);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, spanishLocale);

        String expectedPastMessage = "Debe ser una fecha y hora pasada";
        String expectedFutureMessage = "Debe ser una fecha y hora futura";

        assertEquals(expectedPastMessage, pastMessage, "Spanish past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "Spanish future datetime message mismatch");
    }

    @Test
    public void testRussianMessages() {
        Locale russianLocale = new Locale("ru");
        String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, russianLocale);
        String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, russianLocale);

        String expectedPastMessage = "\u0414\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u043f\u0440\u043e\u0448\u0435\u0434\u0448\u0435\u0439 \u0434\u0430\u0442\u043e\u0439 \u0438 \u0432\u0440\u0435\u043c\u0435\u043d\u0435\u043c";
        String expectedFutureMessage = "\u0414\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u0431\u0443\u0434\u0443\u0449\u0435\u0439 \u0434\u0430\u0442\u043e\u0439 \u0438 \u0432\u0440\u0435\u043c\u0435\u043d\u0435\u043c";

        assertEquals(expectedPastMessage, pastMessage, "Russian past datetime message mismatch");
        assertEquals(expectedFutureMessage, futureMessage, "Russian future datetime message mismatch");
    }

    @Test
    public void testAllLanguagesHaveMessages() {
        // 测试所有支持的语言都有这两个消息键
        Locale[] locales = {
            Locale.ENGLISH,
            Locale.SIMPLIFIED_CHINESE,
            Locale.JAPANESE,
            Locale.FRENCH,
            Locale.GERMAN,
            new Locale("es"),  // Spanish
            new Locale("ru"),  // Russian
            Locale.KOREAN
        };

        for (Locale locale : locales) {
            String pastMessage = MessageManager.getMessage(PAST_DATETIME_KEY, locale);
            String futureMessage = MessageManager.getMessage(FUTURE_DATETIME_KEY, locale);

            assertNotNull(pastMessage,
                "Past datetime message should exist for locale: " + locale.getDisplayName());
            assertNotNull(futureMessage,
                "Future datetime message should exist for locale: " + locale.getDisplayName());

            assertFalse(pastMessage.isEmpty(),
                "Past datetime message should not be empty for locale: " + locale.getDisplayName());
            assertFalse(futureMessage.isEmpty(),
                "Future datetime message should not be empty for locale: " + locale.getDisplayName());
        }
    }

    @Test
    public void testMessagesDifferBetweenLanguages() {
        // 测试不同语言的消息内容是否不同（确保真的翻译了，而不是都用英文）
        String englishPastMsg = MessageManager.getMessage(PAST_DATETIME_KEY, Locale.ENGLISH);
        String chinesePastMsg = MessageManager.getMessage(PAST_DATETIME_KEY, Locale.SIMPLIFIED_CHINESE);
        String japanesePastMsg = MessageManager.getMessage(PAST_DATETIME_KEY, Locale.JAPANESE);

        // 中文和英文应该不同
        assertNotEquals(englishPastMsg, chinesePastMsg,
            "Chinese and English past datetime messages should be different");

        // 日文和英文应该不同
        assertNotEquals(englishPastMsg, japanesePastMsg,
            "Japanese and English past datetime messages should be different");

        // 中文和日文应该不同
        assertNotEquals(chinesePastMsg, japanesePastMsg,
            "Chinese and Japanese past datetime messages should be different");
    }
}
