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

package io.github.vipxieliang.validx.chain.base;

import io.github.vipxieliang.validx.chain.ValidationPlus;
import io.github.vipxieliang.validx.chain.ValidationConfig;
import io.github.vipxieliang.validx.annotations.Duration.DurationFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Duration链式验证测试类
 */
public class DurationValidationChainTest {

    // === ISO 8601格式测试 ===

    @Test
    public void testValidIso8601_Hours() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT2H");

        assertTrue(chain.passed(), "PT2H应该通过验证");
    }

    @Test
    public void testValidIso8601_HoursMinutes() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT2H30M");

        assertTrue(chain.passed(), "PT2H30M应该通过验证");
    }

    @Test
    public void testValidIso8601_Full() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT1H30M15S");

        assertTrue(chain.passed(), "PT1H30M15S应该通过验证");
    }

    @Test
    public void testValidIso8601_WithDays() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"P1DT2H30M");

        assertTrue(chain.passed(), "P1DT2H30M应该通过验证");
    }

    // === 简化格式测试 ===

    @Test
    public void testValidSimple_Hours() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"2h");

        assertTrue(chain.passed(), "2h应该通过验证");
    }

    @Test
    public void testValidSimple_HoursMinutes() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"2h30m");

        assertTrue(chain.passed(), "2h30m应该通过验证");
    }

    @Test
    public void testValidSimple_Full() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"1h30m15s");

        assertTrue(chain.passed(), "1h30m15s应该通过验证");
    }

    @Test
    public void testValidSimple_WithDays() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"1d12h30m");

        assertTrue(chain.passed(), "1d12h30m应该通过验证");
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidFormat_NoUnits() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"123");

        assertFalse(chain.passed(), "纯数字应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidFormat_EmptyPT() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT");

        assertFalse(chain.passed(), "PT应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidFormat_InvalidCharacters() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"2h30x");

        assertFalse(chain.passed(), "无效字符应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    // === 格式限制测试 ===

    @Test
    public void testIso8601Only_AcceptsIso() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT2H30M", DurationFormat.ISO_8601);

        assertTrue(chain.passed(), "ISO_8601模式应该接受ISO格式");
    }

    @Test
    public void testIso8601Only_RejectsSimple() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"2h30m", DurationFormat.ISO_8601);

        assertFalse(chain.passed(), "ISO_8601模式应该拒绝简化格式");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testSimpleOnly_AcceptsSimple() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"2h30m", DurationFormat.SIMPLE);

        assertTrue(chain.passed(), "SIMPLE模式应该接受简化格式");
    }

    @Test
    public void testSimpleOnly_RejectsIso() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT2H30M", DurationFormat.SIMPLE);

        assertFalse(chain.passed(), "SIMPLE模式应该拒绝ISO格式");
        assertEquals(1, chain.getErrors().size());
    }

    // === Null和空值测试 ===

    @Test
    public void testValidDuration_Null() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration(null);

        assertTrue(chain.passed(), "null值应该通过验证");
    }

    @Test
    public void testValidDuration_Empty() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"");

        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    // === 多个验证链式调用测试 ===

    @Test
    public void testMultipleValidations_AllValid() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT2H30M")
             .isDuration((Object)"2h30m")
             .isDuration((Object)"P1DT12H");

        assertTrue(chain.passed(), "所有有效的时间段应该通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testMultipleValidations_SomeInvalid() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT2H30M")
             .isDuration((Object)"invalid")
             .isDuration((Object)"2h30m");

        assertFalse(chain.passed(), "包含无效时间段的链式调用应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    // === 与配置API结合测试 ===

    @Test
    public void testWithGlobalNotNullConfig() {
        ValidationPlus chain = ValidationPlus.init()
            .config(ValidationConfig.GLOBAL_NOT_NULL);

        chain.isDuration(null);

        assertFalse(chain.passed(), "全局NOT_NULL配置下，null值应该失败");
        assertTrue(chain.getErrors().size() > 0);
    }

    @Test
    public void testWithLocalAllowNull() {
        ValidationPlus chain = ValidationPlus.init()
            .config(ValidationConfig.GLOBAL_NOT_NULL);

        chain.allowNull().isDuration(null);

        assertTrue(chain.passed(), "局部allowNull应该允许null值通过");
    }

    @Test
    public void testWithGlobalNotEmptyConfig() {
        ValidationPlus chain = ValidationPlus.init()
            .config(ValidationConfig.GLOBAL_NOT_EMPTY);

        chain.isDuration((Object)"");

        assertFalse(chain.passed(), "全局NOT_EMPTY配置下，空字符串应该失败");
        assertTrue(chain.getErrors().size() > 0);
    }

    @Test
    public void testWithLocalAllowEmpty() {
        ValidationPlus chain = ValidationPlus.init()
            .config(ValidationConfig.GLOBAL_NOT_EMPTY);

        chain.allowEmpty().isDuration((Object)"");

        assertTrue(chain.passed(), "局部allowEmpty应该允许空字符串通过");
    }

    @Test
    public void testWithFieldLabel() {
        ValidationPlus chain = ValidationPlus.init();
        chain.field("TaskDuration").isDuration((Object)"invalid");

        assertFalse(chain.passed(), "带字段标签的验证应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    // === 边界值测试 ===

    @Test
    public void testEdgeCase_LargeValues() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"P999DT23H59M59S");

        assertTrue(chain.passed(), "大数值的时间段应该通过验证");
    }

    @Test
    public void testEdgeCase_MinimalIso() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"PT1S");

        assertTrue(chain.passed(), "最小ISO格式应该通过验证");
    }

    @Test
    public void testEdgeCase_MinimalSimple() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"1s");

        assertTrue(chain.passed(), "最小简化格式应该通过验证");
    }

    @Test
    public void testMixedCase_Iso() {
        ValidationPlus chain = ValidationPlus.init();
        chain.isDuration((Object)"pt2h30m");

        assertTrue(chain.passed(), "小写ISO格式应该通过验证");
    }
}
