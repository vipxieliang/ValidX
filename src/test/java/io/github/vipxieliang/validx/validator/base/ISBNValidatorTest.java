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

package io.github.vipxieliang.validx.validator.base;

import io.github.vipxieliang.validx.validator.book.ISBNValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ISBN验证器测试类
 */
public class ISBNValidatorTest {

    private final ISBNValidator validator = new ISBNValidator();

    @Test
    public void testValidISBN10() {
        // 有效的10位ISBN
        assertTrue(validator.isValid("0306406152", null));
        assertTrue(validator.isValid("0-306-40615-2", null));
        assertTrue(validator.isValid("080442957X", null));
        assertTrue(validator.isValid("0-8044-2957-X", null));
        assertTrue(validator.isValid("0596009208", null));
        assertTrue(validator.isValid("0-596-00920-8", null));
    }

    @Test
    public void testInvalidISBN10() {
        // 无效的10位ISBN
        assertFalse(validator.isValid("0306406151", null)); // 校验位错误
        assertFalse(validator.isValid("0-306-40615-1", null)); // 校验位错误
        assertFalse(validator.isValid("030640615X", null)); // 校验位错误
        assertFalse(validator.isValid("1234567890", null)); // 校验位错误
        assertFalse(validator.isValid("030640615", null)); // 长度不足
        assertFalse(validator.isValid("03064061521", null)); // 长度过长
        assertFalse(validator.isValid("03064O6152", null)); // 包含字母O
        // 注意：新行为中，空字符串和null被视为有效（由其他注解如@NotNull处理）
        assertTrue(validator.isValid("", null)); // 空字符串现在返回true
        assertTrue(validator.isValid(null, null)); // null值现在返回true
    }

    @Test
    public void testValidISBN13() {
        // 有效的13位ISBN
        assertTrue(validator.isValid("9780306406157", null));
        assertTrue(validator.isValid("978-0-306-40615-7", null));
        assertTrue(validator.isValid("9781566199094", null));
        assertTrue(validator.isValid("978-1-56619-909-4", null));
        assertTrue(validator.isValid("9780471958697", null));
        assertTrue(validator.isValid("978-0-471-95869-7", null));
    }

    @Test
    public void testInvalidISBN13() {
        // 无效的13位ISBN
        assertFalse(validator.isValid("9780306406156", null)); // 校验位错误
        assertFalse(validator.isValid("978-0-306-40615-6", null)); // 校验位错误
        assertFalse(validator.isValid("9781566199095", null)); // 校验位错误
        assertFalse(validator.isValid("9780471958698", null)); // 校验位错误
        assertFalse(validator.isValid("978047195869", null)); // 长度不足
        assertFalse(validator.isValid("97804719586971", null)); // 长度过长
        assertFalse(validator.isValid("978047195O697", null)); // 包含字母O
    }

    @Test
    public void testInvalidFormat() {
        // 格式错误的ISBN
        assertFalse(validator.isValid("invalid-isbn", null));
        assertFalse(validator.isValid("123", null));
        assertFalse(validator.isValid("ISBN 1234567890", null));
        assertFalse(validator.isValid("978-0-306-40615-7-1", null)); // 多余部分
    }
}