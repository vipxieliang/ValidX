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

import io.github.vipxieliang.validx.chain.ValidX;
import io.github.vipxieliang.validx.chain.ValidXConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CronExpression链式验证测试类
 */
public class CronExpressionValidationChainTest {

    // === 有效的Cron表达式测试 ===

    @Test
    public void testValidCronExpression_EveryDayAtNoon() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 * * ?");

        assertTrue(chain.passed(), "每天中午12点的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_EveryMinute() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 * * * * ?");

        assertTrue(chain.passed(), "每分钟执行的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_Every15Minutes() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0/15 * * * ?");

        assertTrue(chain.passed(), "每15分钟执行的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_WeekdaysAt9AM() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 9 ? * MON-FRI");

        assertTrue(chain.passed(), "工作日早上9点的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_FirstDayOfMonth() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 0 1 * ?");

        assertTrue(chain.passed(), "每月第一天的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_LastDayOfMonth() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 0 L * ?");

        assertTrue(chain.passed(), "每月最后一天的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_WithYear() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 * * ? 2025");

        assertTrue(chain.passed(), "带年份的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_SpecificWeekday() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 10 ? * 6#3");

        assertTrue(chain.passed(), "每月第三个星期五的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_Workday() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 15W * ?");

        assertTrue(chain.passed(), "最接近15号的工作日的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_MultipleValues() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 8,12,18 * * ?");

        assertTrue(chain.passed(), "多个时间点的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_Range() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 9-17 * * ?");

        assertTrue(chain.passed(), "时间范围的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_MonthNames() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 1 JAN,FEB,MAR ?");

        assertTrue(chain.passed(), "使用月份英文缩写的Cron表达式应该通过验证");
    }

    @Test
    public void testValidCronExpression_DayNames() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 ? * SUN,SAT");

        assertTrue(chain.passed(), "使用星期英文缩写的Cron表达式应该通过验证");
    }

    // === 无效的Cron表达式测试 ===

    @Test
    public void testInvalidCronExpression_TooFewFields() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 * *");

        assertFalse(chain.passed(), "字段数不足的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_TooManyFields() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 * * ? 2025 extra");

        assertFalse(chain.passed(), "字段数过多的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_InvalidSecond() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"60 0 12 * * ?");

        assertFalse(chain.passed(), "秒数超出范围的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_InvalidMinute() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 60 12 * * ?");

        assertFalse(chain.passed(), "分钟超出范围的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_InvalidHour() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 24 * * ?");

        assertFalse(chain.passed(), "小时超出范围的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_InvalidDay() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 32 * ?");

        assertFalse(chain.passed(), "日期超出范围的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_InvalidMonth() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 1 13 ?");

        assertFalse(chain.passed(), "月份超出范围的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_InvalidWeek() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 ? * 8");

        assertFalse(chain.passed(), "星期超出范围的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_BothDayAndWeek() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 15 * MON");

        assertFalse(chain.passed(), "日和周同时指定的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    @Test
    public void testInvalidCronExpression_InvalidCharacter() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 * * @");

        assertFalse(chain.passed(), "包含无效字符的Cron表达式应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    // === Null和空值测试 ===

    @Test
    public void testValidCronExpression_Null() {
        ValidX chain = ValidX.init();
        chain.isCronExpression(null);

        assertTrue(chain.passed(), "null值应该通过验证");
    }

    @Test
    public void testValidCronExpression_Empty() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"");

        assertTrue(chain.passed(), "空字符串应该通过验证");
    }

    // === 多个验证链式调用测试 ===

    @Test
    public void testMultipleValidations_AllValid() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 * * ?")
             .isCronExpression((Object)"0 0/15 * * * ?")
             .isCronExpression((Object)"0 0 9 ? * MON-FRI");

        assertTrue(chain.passed(), "所有有效的Cron表达式应该通过验证");
        assertEquals(0, chain.getErrors().size());
    }

    @Test
    public void testMultipleValidations_SomeInvalid() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 * * ?")
             .isCronExpression((Object)"invalid cron")
             .isCronExpression((Object)"0 0 9 ? * MON-FRI");

        assertFalse(chain.passed(), "包含无效Cron表达式的链式调用应该失败");
        assertEquals(1, chain.getErrors().size());
    }

    // === 与配置API结合测试 ===

    @Test
    public void testWithGlobalNotNullConfig() {
        ValidX chain = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL);

        chain.isCronExpression(null);

        assertFalse(chain.passed(), "全局NOT_NULL配置下，null值应该失败");
        assertTrue(chain.getErrors().size() > 0);
    }

    @Test
    public void testWithLocalAllowNull() {
        ValidX chain = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL);

        chain.allowNull().isCronExpression(null);

        assertTrue(chain.passed(), "局部allowNull应该允许null值通过");
    }

    @Test
    public void testWithGlobalNotEmptyConfig() {
        ValidX chain = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY);

        chain.isCronExpression((Object)"");

        assertFalse(chain.passed(), "全局NOT_EMPTY配置下，空字符串应该失败");
        assertTrue(chain.getErrors().size() > 0);
    }

    @Test
    public void testWithLocalAllowEmpty() {
        ValidX chain = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY);

        chain.allowEmpty().isCronExpression((Object)"");

        assertTrue(chain.passed(), "局部allowEmpty应该允许空字符串通过");
    }

    @Test
    public void testWithFieldLabel() {
        ValidX chain = ValidX.init();
        chain.field("TaskSchedule").isCronExpression((Object)"invalid cron");

        assertFalse(chain.passed(), "带字段标签的验证应该失败");
        assertEquals(1, chain.getErrors().size(), "应该有一个错误消息");
    }

    // === 边界值测试 ===

    @Test
    public void testCronExpression_AllWildcards() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"* * * * * ?");

        assertTrue(chain.passed(), "全通配符的Cron表达式应该通过验证");
    }

    @Test
    public void testCronExpression_ComplexExpression() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0/5 14,18 ? * MON-FRI 2025");

        assertTrue(chain.passed(), "复杂的Cron表达式应该通过验证");
    }

    @Test
    public void testCronExpression_LastWorkingDay() {
        ValidX chain = ValidX.init();
        chain.isCronExpression((Object)"0 0 12 LW * ?");

        assertTrue(chain.passed(), "最后一个工作日的Cron表达式应该通过验证");
    }
}
