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
 * 测试日期验证器的国际化消息是否在所有语言包中正确加载
 */
public class DateValidatorI18nTest {

    private static final String DATE_PATTERN_CONTAINS_TIME_KEY = "io.github.vipxieliang.validx.validator.date.pattern.contains.time";
    private static final String DATETIME_PATTERN_MISSING_TIME_KEY = "io.github.vipxieliang.validx.validator.datetime.pattern.missing.time";

    @Test
    public void testEnglishMessages() {
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, Locale.ENGLISH);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, Locale.ENGLISH);

        String expectedDateMessage = "Date validation pattern must not contain time format symbols (H, h, K, k, m, s, S, a, A, n, N). Use the corresponding DateTime annotation for date-time validation";
        String expectedDateTimeMessage = "DateTime validation pattern must contain time format symbols (H, h, K, k, m, s, S, a, A, n, N). Use the corresponding Date annotation for date-only validation";

        assertEquals(expectedDateMessage, dateMessage, "English date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "English datetime pattern error message mismatch");
    }

    @Test
    public void testDefaultMessages() {
        // Test default language (no locale specified) - should fallback to ValidationMessages.properties (Chinese)
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, Locale.getDefault());
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, Locale.getDefault());

        assertNotNull(dateMessage, "Default date pattern error message should exist");
        assertNotNull(dateTimeMessage, "Default datetime pattern error message should exist");
        assertFalse(dateMessage.isEmpty(), "Default date pattern error message should not be empty");
        assertFalse(dateTimeMessage.isEmpty(), "Default datetime pattern error message should not be empty");

        // Verify it contains Chinese characters (default is Chinese)
        assertTrue(dateMessage.contains("日期验证"), "Default message should be in Chinese");
        assertTrue(dateTimeMessage.contains("日期时间验证"), "Default message should be in Chinese");
    }

    @Test
    public void testChineseMessages() {
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, Locale.SIMPLIFIED_CHINESE);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, Locale.SIMPLIFIED_CHINESE);

        String expectedDateMessage = "\u65e5\u671f\u9a8c\u8bc1\u7684 pattern \u4e0d\u80fd\u5305\u542b\u65f6\u95f4\u683c\u5f0f\u7b26\u53f7 (H, h, K, k, m, s, S, a, A, n, N)\u3002\u5982\u9700\u9a8c\u8bc1\u65e5\u671f\u65f6\u95f4\uff0c\u8bf7\u4f7f\u7528\u5bf9\u5e94\u7684 DateTime \u6ce8\u89e3";
        String expectedDateTimeMessage = "\u65e5\u671f\u65f6\u95f4\u9a8c\u8bc1\u7684 pattern \u5fc5\u987b\u5305\u542b\u65f6\u95f4\u683c\u5f0f\u7b26\u53f7 (H, h, K, k, m, s, S, a, A, n, N)\u3002\u5982\u53ea\u9700\u9a8c\u8bc1\u65e5\u671f\uff0c\u8bf7\u4f7f\u7528\u5bf9\u5e94\u7684 Date \u6ce8\u89e3";

        assertEquals(expectedDateMessage, dateMessage, "Chinese date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "Chinese datetime pattern error message mismatch");
    }

    @Test
    public void testJapaneseMessages() {
        Locale japaneseLocale = Locale.JAPANESE;
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, japaneseLocale);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, japaneseLocale);

        String expectedDateMessage = "\u65e5\u4ed8\u691c\u8a3c\u306e\u30d1\u30bf\u30fc\u30f3\u306b\u306f\u6642\u523b\u30d5\u30a9\u30fc\u30de\u30c3\u30c8\u8a18\u53f7 (H, h, K, k, m, s, S, a, A, n, N) \u3092\u542b\u3081\u308b\u3053\u3068\u304c\u3067\u304d\u307e\u305b\u3093\u3002\u65e5\u6642\u691c\u8a3c\u306b\u306f\u5bfe\u5fdc\u3059\u308bDateTime\u30a2\u30ce\u30c6\u30fc\u30b7\u30e7\u30f3\u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044";
        String expectedDateTimeMessage = "\u65e5\u6642\u691c\u8a3c\u306e\u30d1\u30bf\u30fc\u30f3\u306b\u306f\u6642\u523b\u30d5\u30a9\u30fc\u30de\u30c3\u30c8\u8a18\u53f7 (H, h, K, k, m, s, S, a, A, n, N) \u304c\u5fc5\u8981\u3067\u3059\u3002\u65e5\u4ed8\u306e\u307f\u306e\u691c\u8a3c\u306b\u306f\u5bfe\u5fdc\u3059\u308bDate\u30a2\u30ce\u30c6\u30fc\u30b7\u30e7\u30f3\u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044";

        assertEquals(expectedDateMessage, dateMessage, "Japanese date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "Japanese datetime pattern error message mismatch");
    }

    @Test
    public void testFrenchMessages() {
        Locale frenchLocale = Locale.FRENCH;
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, frenchLocale);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, frenchLocale);

        String expectedDateMessage = "Le modèle de validation de date ne doit pas contenir de symboles de format d'heure (H, h, K, k, m, s, S, a, A, n, N). Utilisez l'annotation DateTime correspondante pour la validation de date-heure";
        String expectedDateTimeMessage = "Le modèle de validation de date-heure doit contenir des symboles de format d'heure (H, h, K, k, m, s, S, a, A, n, N). Utilisez l'annotation Date correspondante pour la validation de date uniquement";

        assertEquals(expectedDateMessage, dateMessage, "French date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "French datetime pattern error message mismatch");
    }

    @Test
    public void testGermanMessages() {
        Locale germanLocale = Locale.GERMAN;
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, germanLocale);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, germanLocale);

        String expectedDateMessage = "Das Datumsvalidierungsmuster darf keine Zeitformatsymbole (H, h, K, k, m, s, S, a, A, n, N) enthalten. Verwenden Sie die entsprechende DateTime-Annotation für die Datums-Zeit-Validierung";
        String expectedDateTimeMessage = "Das DateTime-Validierungsmuster muss Zeitformatsymbole (H, h, K, k, m, s, S, a, A, n, N) enthalten. Verwenden Sie die entsprechende Date-Annotation für die reine Datumsvalidierung";

        assertEquals(expectedDateMessage, dateMessage, "German date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "German datetime pattern error message mismatch");
    }

    @Test
    public void testSpanishMessages() {
        Locale spanishLocale = new Locale("es");
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, spanishLocale);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, spanishLocale);

        String expectedDateMessage = "El patrón de validación de fecha no debe contener símbolos de formato de hora (H, h, K, k, m, s, S, a, A, n, N). Use la anotación DateTime correspondiente para la validación de fecha-hora";
        String expectedDateTimeMessage = "El patrón de validación de fecha-hora debe contener símbolos de formato de hora (H, h, K, k, m, s, S, a, A, n, N). Use la anotación Date correspondiente para la validación solo de fecha";

        assertEquals(expectedDateMessage, dateMessage, "Spanish date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "Spanish datetime pattern error message mismatch");
    }

    @Test
    public void testRussianMessages() {
        Locale russianLocale = new Locale("ru");
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, russianLocale);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, russianLocale);

        String expectedDateMessage = "\u0428\u0430\u0431\u043b\u043e\u043d \u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0438 \u0434\u0430\u0442\u044b \u043d\u0435 \u0434\u043e\u043b\u0436\u0435\u043d \u0441\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c \u0441\u0438\u043c\u0432\u043e\u043b\u044b \u0444\u043e\u0440\u043c\u0430\u0442\u0430 \u0432\u0440\u0435\u043c\u0435\u043d\u0438 (H, h, K, k, m, s, S, a, A, n, N). \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 \u0441\u043e\u043e\u0442\u0432\u0435\u0442\u0441\u0442\u0432\u0443\u044e\u0449\u0443\u044e \u0430\u043d\u043d\u043e\u0442\u0430\u0446\u0438\u044e DateTime \u0434\u043b\u044f \u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0438 \u0434\u0430\u0442\u044b-\u0432\u0440\u0435\u043c\u0435\u043d\u0438";
        String expectedDateTimeMessage = "\u0428\u0430\u0431\u043b\u043e\u043d \u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0438 \u0434\u0430\u0442\u044b-\u0432\u0440\u0435\u043c\u0435\u043d\u0438 \u0434\u043e\u043b\u0436\u0435\u043d \u0441\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c \u0441\u0438\u043c\u0432\u043e\u043b\u044b \u0444\u043e\u0440\u043c\u0430\u0442\u0430 \u0432\u0440\u0435\u043c\u0435\u043d\u0438 (H, h, K, k, m, s, S, a, A, n, N). \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 \u0441\u043e\u043e\u0442\u0432\u0435\u0442\u0441\u0442\u0432\u0443\u044e\u0449\u0443\u044e \u0430\u043d\u043d\u043e\u0442\u0430\u0446\u0438\u044e Date \u0434\u043b\u044f \u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0438 \u0442\u043e\u043b\u044c\u043a\u043e \u0434\u0430\u0442\u044b";

        assertEquals(expectedDateMessage, dateMessage, "Russian date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "Russian datetime pattern error message mismatch");
    }

    @Test
    public void testKoreanMessages() {
        Locale koreanLocale = Locale.KOREAN;
        String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, koreanLocale);
        String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, koreanLocale);

        String expectedDateMessage = "\ub0a0\uc9dc \uac80\uc99d \ud328\ud134\uc5d0\ub294 \uc2dc\uac04 \ud615\uc2dd \uae30\ud638 (H, h, K, k, m, s, S, a, A, n, N)\uac00 \ud3ec\ud568\ub418\uc5b4\uc11c\ub294 \uc548 \ub429\ub2c8\ub2e4. \ub0a0\uc9dc-\uc2dc\uac04 \uac80\uc99d\uc5d0\ub294 \ud574\ub2f9 DateTime \uc5b4\ub178\ud14c\uc774\uc158\uc744 \uc0ac\uc6a9\ud558\uc138\uc694";
        String expectedDateTimeMessage = "\ub0a0\uc9dc-\uc2dc\uac04 \uac80\uc99d \ud328\ud134\uc5d0\ub294 \uc2dc\uac04 \ud615\uc2dd \uae30\ud638 (H, h, K, k, m, s, S, a, A, n, N)\uac00 \ud3ec\ud568\ub418\uc5b4\uc57c \ud569\ub2c8\ub2e4. \ub0a0\uc9dc\ub9cc \uac80\uc99d\ud558\ub824\uba74 \ud574\ub2f9 Date \uc5b4\ub178\ud14c\uc774\uc158\uc744 \uc0ac\uc6a9\ud558\uc138\uc694";

        assertEquals(expectedDateMessage, dateMessage, "Korean date pattern error message mismatch");
        assertEquals(expectedDateTimeMessage, dateTimeMessage, "Korean datetime pattern error message mismatch");
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
            String dateMessage = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, locale);
            String dateTimeMessage = MessageManager.getMessage(DATETIME_PATTERN_MISSING_TIME_KEY, locale);

            assertNotNull(dateMessage,
                "Date pattern error message should exist for locale: " + locale.getDisplayName());
            assertNotNull(dateTimeMessage,
                "DateTime pattern error message should exist for locale: " + locale.getDisplayName());

            assertFalse(dateMessage.isEmpty(),
                "Date pattern error message should not be empty for locale: " + locale.getDisplayName());
            assertFalse(dateTimeMessage.isEmpty(),
                "DateTime pattern error message should not be empty for locale: " + locale.getDisplayName());
        }
    }

    @Test
    public void testMessagesDifferBetweenLanguages() {
        // 测试不同语言的消息内容是否不同（确保真的翻译了，而不是都用英文）
        String englishDateMsg = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, Locale.ENGLISH);
        String chineseDateMsg = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, Locale.SIMPLIFIED_CHINESE);
        String japaneseDateMsg = MessageManager.getMessage(DATE_PATTERN_CONTAINS_TIME_KEY, Locale.JAPANESE);

        // 中文和英文应该不同
        assertNotEquals(englishDateMsg, chineseDateMsg,
            "Chinese and English messages should be different");

        // 日文和英文应该不同
        assertNotEquals(englishDateMsg, japaneseDateMsg,
            "Japanese and English messages should be different");

        // 中文和日文应该不同
        assertNotEquals(chineseDateMsg, japaneseDateMsg,
            "Chinese and Japanese messages should be different");
    }
}
