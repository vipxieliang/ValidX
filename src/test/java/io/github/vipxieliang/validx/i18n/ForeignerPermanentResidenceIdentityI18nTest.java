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
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 外国人永久居留身份证（五星卡）多语言国际化测试
 * 覆盖新增的第4~6位国籍国代码（ISO 3166-1 numeric）校验失败时的错误消息
 *
 * @author vipxieliang
 * @since 1.2.0
 */
public class ForeignerPermanentResidenceIdentityI18nTest {

    // 无效号码：第4-6位国籍国代码 000 不存在于 ISO 3166-1，其余字段（申领地、出生日期、校验码）均正确
    private static final String INVALID_NATIONALITY_CODE_NUMBER = "931000199012010012";

    @Test
    public void testForeignerPermanentResidenceIdentityInEnglish() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.ENGLISH);
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Invalid foreigner permanent residence ID format", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityInChinese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("外国人永久居留身份证号码格式不正确", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityInJapanese() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.JAPANESE);
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("外国人永住居留身份証番号の形式が正しくありません", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityInKorean() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.KOREAN);
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("외국인 영주 거주 신분증 번호 형식이 유효하지 않습니다", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityInFrench() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.FRENCH);
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Format de numéro de carte de résidence permanente d'étranger invalide", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityInGerman() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(Locale.GERMAN);
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Ungültiges ausländisches Daueraufenthalts-ID-Format", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityInRussian() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(new Locale("ru"));
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Недействительный формат номера вида на жительство иностранца", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }

    @Test
    public void testForeignerPermanentResidenceIdentityInSpanish() {
        Locale originalLocale = Locale.getDefault();

        try {
            MessageManager.setCurrentLocale(new Locale("es"));
            ValidX validator = ValidX.init().isForeignerPermanentResidenceIdentity(INVALID_NATIONALITY_CODE_NUMBER);

            assertFalse(validator.passed(), "Should fail when nationality code is invalid");
            assertEquals(1, validator.getErrors().size());
            assertEquals("Formato de número de tarjeta de residencia permanente de extranjero inválido", validator.getErrors().get(0));
        } finally {
            Locale.setDefault(originalLocale);
            MessageManager.clearCurrentLocale();
        }
    }
}
