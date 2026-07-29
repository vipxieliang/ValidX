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

import io.github.vipxieliang.validx.annotations.GeoPoint;
import io.github.vipxieliang.validx.chain.ValidXConfig;
import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeoPointValidationChainTest {

    @Test
    public void testValidGeoPointsDefault() {
        // 测试有效的坐标（默认：经度在前，任意分隔符）
        ValidaX chain1 = ValidaX.init();
        chain1.isGeoPoint((Object)"116.4074,39.9042"); // 北京
        assertTrue(chain1.passed(), "有效的坐标应该通过验证: 116.4074,39.9042");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"121.4737,31.2304"); // 上海
        assertTrue(chain2.passed(), "有效的坐标应该通过验证: 121.4737,31.2304");

        ValidaX chain3 = ValidaX.init();
        chain3.isGeoPoint((Object)"-74.0060,40.7128"); // 纽约
        assertTrue(chain3.passed(), "有效的坐标应该通过验证: -74.0060,40.7128");

        ValidaX chain4 = ValidaX.init();
        chain4.isGeoPoint((Object)"0,0"); // 原点
        assertTrue(chain4.passed(), "有效的坐标应该通过验证: 0,0");

        ValidaX chain5 = ValidaX.init();
        chain5.isGeoPoint((Object)"180,90"); // 边界值
        assertTrue(chain5.passed(), "有效的坐标应该通过验证: 180,90");

        ValidaX chain6 = ValidaX.init();
        chain6.isGeoPoint((Object)"-180,-90"); // 边界值
        assertTrue(chain6.passed(), "有效的坐标应该通过验证: -180,-90");
    }

    @Test
    public void testValidGeoPointsWithDifferentSeparators() {
        // 测试不同分隔符
        ValidaX chain1 = ValidaX.init();
        chain1.isGeoPoint((Object)"116.4074,39.9042"); // 逗号
        assertTrue(chain1.passed(), "逗号分隔的坐标应该通过验证");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"116.4074 39.9042"); // 空格
        assertTrue(chain2.passed(), "空格分隔的坐标应该通过验证");

        ValidaX chain3 = ValidaX.init();
        chain3.isGeoPoint((Object)"116.4074, 39.9042"); // 逗号+空格
        assertTrue(chain3.passed(), "逗号+空格分隔的坐标应该通过验证");

        ValidaX chain4 = ValidaX.init();
        chain4.isGeoPoint((Object)"116.4074  39.9042"); // 多个空格
        assertTrue(chain4.passed(), "多个空格分隔的坐标应该通过验证");
    }

    @Test
    public void testLatitudeFirstOrder() {
        // 测试纬度在前的坐标
        ValidaX chain1 = ValidaX.init();
        chain1.isGeoPoint((Object)"39.9042,116.4074", true); // 北京（纬度在前）
        assertTrue(chain1.passed(), "纬度在前的坐标应该通过验证: 39.9042,116.4074");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"40.7128,-74.0060", true); // 纽约（纬度在前）
        assertTrue(chain2.passed(), "纬度在前的坐标应该通过验证: 40.7128,-74.0060");

        ValidaX chain3 = ValidaX.init();
        chain3.isGeoPoint((Object)"0,0", true); // 原点
        assertTrue(chain3.passed(), "纬度在前的坐标应该通过验证: 0,0");
    }

    @Test
    public void testSpecificSeparatorType() {
        // 测试指定分隔符类型
        ValidaX chain1 = ValidaX.init();
        chain1.isGeoPoint((Object)"116.4074,39.9042", false, GeoPoint.SeparatorType.COMMA);
        assertTrue(chain1.passed(), "逗号分隔符应该通过验证");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"116.4074 39.9042", false, GeoPoint.SeparatorType.SPACE);
        assertTrue(chain2.passed(), "空格分隔符应该通过验证");

        ValidaX chain3 = ValidaX.init();
        chain3.isGeoPoint((Object)"116.4074 39.9042", false, GeoPoint.SeparatorType.COMMA);
        assertFalse(chain3.passed(), "指定逗号时空格分隔应该不通过验证");

        ValidaX chain4 = ValidaX.init();
        chain4.isGeoPoint((Object)"116.4074,39.9042", false, GeoPoint.SeparatorType.SPACE);
        assertFalse(chain4.passed(), "指定空格时逗号分隔应该不通过验证");
    }

    @Test
    public void testInvalidLongitude() {
        // 测试无效的经度
        ValidaX chain1 = ValidaX.init();
        chain1.isGeoPoint((Object)"181,39.9042");
        assertFalse(chain1.passed(), "经度超出范围应该不通过验证: 181");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"-181,39.9042");
        assertFalse(chain2.passed(), "经度超出范围应该不通过验证: -181");

        ValidaX chain3 = ValidaX.init();
        chain3.isGeoPoint((Object)"200,39.9042");
        assertFalse(chain3.passed(), "经度超出范围应该不通过验证: 200");
    }

    @Test
    public void testInvalidLatitude() {
        // 测试无效的纬度
        ValidaX chain1 = ValidaX.init();
        chain1.isGeoPoint((Object)"116.4074,91");
        assertFalse(chain1.passed(), "纬度超出范围应该不通过验证: 91");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"116.4074,-91");
        assertFalse(chain2.passed(), "纬度超出范围应该不通过验证: -91");

        ValidaX chain3 = ValidaX.init();
        chain3.isGeoPoint((Object)"116.4074,100");
        assertFalse(chain3.passed(), "纬度超出范围应该不通过验证: 100");
    }

    @Test
    public void testInvalidFormat() {
        // 测试无效的格式
        ValidaX chain1 = ValidaX.init();
        chain1.isGeoPoint((Object)"116.4074");
        assertFalse(chain1.passed(), "只有一个数值应该不通过验证");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"116.4074,39.9042,100");
        assertFalse(chain2.passed(), "三个数值应该不通过验证");

        ValidaX chain3 = ValidaX.init();
        chain3.isGeoPoint((Object)"abc,def");
        assertFalse(chain3.passed(), "非数字应该不通过验证");

        ValidaX chain4 = ValidaX.init();
        chain4.isGeoPoint((Object)"116.4074,");
        assertFalse(chain4.passed(), "缺少第二个值应该不通过验证");

        ValidaX chain5 = ValidaX.init();
        chain5.isGeoPoint((Object)",39.9042");
        assertFalse(chain5.passed(), "缺少第一个值应该不通过验证");
    }

    @Test
    public void testNullValue() {
        // 测试null值
        ValidaX chain = ValidaX.init();
        chain.isGeoPoint((Object)null);
        assertTrue(chain.passed(), "null值应该通过验证（默认行为）");
    }

    @Test
    public void testEmptyString() {
        // 测试空字符串
        ValidaX chain = ValidaX.init();
        chain.isGeoPoint((Object)"");
        assertTrue(chain.passed(), "空字符串应该通过验证（默认行为）");
    }

    @Test
    public void testChainWithMultipleValidations() {
        // 测试链式调用中的多个验证
        ValidaX chain = ValidaX.init();
        chain.isGeoPoint((Object)"116.4074,39.9042")
             .isGeoPoint((Object)"121.4737,31.2304")
             .isGeoPoint((Object)"-74.0060,40.7128");
        assertTrue(chain.passed(), "多个有效坐标应该都通过验证");

        ValidaX chain2 = ValidaX.init();
        chain2.isGeoPoint((Object)"116.4074,39.9042")
              .isGeoPoint((Object)"200,50") // 无效
              .isGeoPoint((Object)"121.4737,31.2304");
        assertFalse(chain2.passed(), "包含无效坐标的链应该不通过验证");
        assertEquals(1, chain2.getErrors().size(), "应该有1个错误");
    }

    @Test
    public void testWithConfigurationAPI() {
        // 测试与配置API结合使用
        ValidaX chain1 = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL);
        chain1.isGeoPoint((Object)null);
        assertFalse(chain1.passed(), "配置NOT_NULL时null应该不通过验证");

        ValidaX chain2 = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY);
        chain2.isGeoPoint((Object)"");
        assertFalse(chain2.passed(), "配置NOT_EMPTY时空字符串应该不通过验证");

        ValidaX chain3 = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL);
        chain3.field("Location").isGeoPoint((Object)"116.4074,39.9042");
        assertTrue(chain3.passed(), "有效坐标应该通过验证");

        ValidaX chain4 = ValidaX.init()
            .config(ValidXConfig.GLOBAL_NOT_NULL);
        chain4.field("Location").allowNull().isGeoPoint((Object)null);
        assertTrue(chain4.passed(), "allowNull覆盖全局配置后null应该通过验证");
    }
}
