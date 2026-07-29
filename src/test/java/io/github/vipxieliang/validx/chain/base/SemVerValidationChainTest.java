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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SemVer链式验证测试类
 */
public class SemVerValidationChainTest {

    // === 基本版本号测试 ===

    @Test
    public void testValidBasicVersion() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0");

        assertTrue(validator.passed(), "基本版本号应该通过验证");
    }

    @Test
    public void testValidVersionWithLargeNumbers() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("10.20.30");

        assertTrue(validator.passed(), "大数字版本号应该通过验证");
    }

    @Test
    public void testValidVersionZero() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("0.0.0");

        assertTrue(validator.passed(), "0.0.0版本号应该通过验证");
    }

    // === 预发布版本测试 ===

    @Test
    public void testValidVersionWithAlpha() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0-alpha");

        assertTrue(validator.passed(), "带alpha预发布标签应该通过验证");
    }

    @Test
    public void testValidVersionWithBeta() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0-beta.1");

        assertTrue(validator.passed(), "带beta.1预发布标签应该通过验证");
    }

    @Test
    public void testValidVersionWithRc() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("2.1.3-rc.2");

        assertTrue(validator.passed(), "带rc.2预发布标签应该通过验证");
    }

    @Test
    public void testValidVersionWithComplexPrerelease() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0-alpha.beta.1");

        assertTrue(validator.passed(), "复杂预发布标签应该通过验证");
    }

    // === 构建元数据测试 ===

    @Test
    public void testValidVersionWithBuildMetadata() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0+20130313144700");

        assertTrue(validator.passed(), "带构建元数据应该通过验证");
    }

    @Test
    public void testValidVersionWithShortBuild() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0+001");

        assertTrue(validator.passed(), "带简短构建元数据应该通过验证");
    }

    // === 完整格式测试 ===

    @Test
    public void testValidVersionComplete() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0-beta+exp.sha.5114f85");

        assertTrue(validator.passed(), "完整格式版本号应该通过验证");
    }

    @Test
    public void testValidVersionWithPrereleaseAndBuild() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0-alpha.1+001");

        assertTrue(validator.passed(), "带预发布和构建元数据应该通过验证");
    }

    // === 无效格式测试 ===

    @Test
    public void testInvalidVersionWithLeadingZero() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("01.0.0");

        assertFalse(validator.passed(), "前导零版本号不应该通过验证");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidVersionMissingParts() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0");

        assertFalse(validator.passed(), "缺少部分的版本号不应该通过验证");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidVersionWithText() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0.RELEASE");

        assertFalse(validator.passed(), "带非标准后缀的版本号不应该通过验证");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testInvalidVersionWithSpaces() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0 ");

        assertFalse(validator.passed(), "带空格的版本号不应该通过验证");
        assertEquals(1, validator.getErrors().size());
    }

    // === v前缀测试 ===

    @Test
    public void testVersionWithVPrefixNotAllowed() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("v1.0.0");

        assertFalse(validator.passed(), "默认不允许v前缀");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testVersionWithVPrefixAllowed() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("v1.0.0", true);

        assertTrue(validator.passed(), "允许v前缀时应该通过验证");
    }

    @Test
    public void testVersionWithoutVPrefixWhenAllowed() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0", true);

        assertTrue(validator.passed(), "允许v前缀时，无前缀也应该通过验证");
    }

    @Test
    public void testVersionWithVPrefixAndPrerelease() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("v2.1.0-beta.1", true);

        assertTrue(validator.passed(), "v前缀+预发布版本应该通过验证");
    }

    @Test
    public void testVersionWithVPrefixAndBuild() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("v1.0.0+20220101", true);

        assertTrue(validator.passed(), "v前缀+构建元数据应该通过验证");
    }

    // === 链式调用测试 ===

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0")
                .isSemVer("2.1.3")
                .isSemVer("3.0.0-alpha");

        assertTrue(validator.passed(), "多个有效版本号应该全部通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0.0")
                .isSemVer("invalid")
                .isSemVer("2.0.0");

        assertFalse(validator.passed(), "一个无效版本号应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_AllFailing() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("1.0")
                .isSemVer("v1.0.0")  // v前缀未允许
                .isSemVer("01.0.0");

        assertFalse(validator.passed(), "多个无效版本号应该全部验证失败");
        assertEquals(3, validator.getErrors().size());
    }

    // === 实际应用场景测试 ===

    @Test
    public void testRealWorldVersions() {
        ValidationPlus validator = ValidationPlus.init();

        // 常见的实际版本号
        validator.isSemVer("1.0.0")
                .isSemVer("2.1.3")
                .isSemVer("3.0.0-alpha.1")
                .isSemVer("4.2.1-beta.2")
                .isSemVer("5.0.0-rc.1")
                .isSemVer("1.0.0+20130313144700");

        assertTrue(validator.passed(), "实际常用版本号应该通过验证");
    }

    @Test
    public void testGitTagVersions() {
        ValidationPlus validator = ValidationPlus.init();

        // Git标签风格的版本号（带v前缀）
        validator.isSemVer("v1.0.0", true)
                .isSemVer("v2.1.3", true)
                .isSemVer("v3.0.0-alpha", true);

        assertTrue(validator.passed(), "Git标签风格版本号应该通过验证");
    }

    // === Null和空值测试 ===

    @Test
    public void testNullVersion() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer(null);

        assertTrue(validator.passed(), "null值应该通过验证（由@NotNull处理）");
    }

    @Test
    public void testEmptyVersion() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("");

        assertTrue(validator.passed(), "空字符串应该通过验证（由@NotEmpty处理）");
    }

    // === 边界情况测试 ===

    @Test
    public void testVersionWithVeryLargeNumbers() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("999.999.999");

        assertTrue(validator.passed(), "非常大的版本号应该通过验证");
    }

    @Test
    public void testMinimalVersion() {
        ValidationPlus validator = ValidationPlus.init();
        validator.isSemVer("0.0.1");

        assertTrue(validator.passed(), "最小版本号应该通过验证");
    }
}
