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

import io.github.vipxieliang.validx.annotations.JSON;
import io.github.vipxieliang.validx.chain.ValidaX;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON链式验证测试类
 */
public class JSONValidationChainTest {

    // === 基本JSON验证 ===

    @Test
    public void testValidJSON_Object() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"name\":\"John\",\"age\":30}");

        assertTrue(validator.passed(), "有效的JSON对象应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testValidJSON_Array() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("[1,2,3,4,5]");

        assertTrue(validator.passed(), "有效的JSON数组应该通过验证");
    }

    @Test
    public void testValidJSON_NestedStructure() {
        ValidaX validator = ValidaX.init();
        String json = "{\"users\":[{\"name\":\"Alice\",\"age\":25},{\"name\":\"Bob\",\"age\":30}]}";
        validator.isJSON(json);

        assertTrue(validator.passed(), "嵌套结构的JSON应该通过验证");
    }

    @Test
    public void testInvalidJSON_MalformedString() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{invalid json}");

        assertFalse(validator.passed(), "格式错误的JSON应该验证失败");
        List<String> errors = validator.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("JSON"), "错误信息应该包含JSON相关内容");
    }

    // === JSON类型限制测试 ===

    @Test
    public void testValidJSON_OnlyObject() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"key\":\"value\"}", JSON.JSONType.OBJECT);

        assertTrue(validator.passed(), "指定OBJECT类型时，对象应该通过验证");
    }

    @Test
    public void testInvalidJSON_ArrayWhenObjectRequired() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("[1,2,3]", JSON.JSONType.OBJECT);

        assertFalse(validator.passed(), "指定OBJECT类型时，数组应该验证失败");
    }

    @Test
    public void testValidJSON_OnlyArray() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("[1,2,3]", JSON.JSONType.ARRAY);

        assertTrue(validator.passed(), "指定ARRAY类型时，数组应该通过验证");
    }

    @Test
    public void testInvalidJSON_ObjectWhenArrayRequired() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"key\":\"value\"}", JSON.JSONType.ARRAY);

        assertFalse(validator.passed(), "指定ARRAY类型时，对象应该验证失败");
    }

    // === 深度限制测试 ===

    @Test
    public void testValidJSON_WithinDepthLimit() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"a\":{\"b\":{\"c\":1}}}", JSON.JSONType.ANY, true, 5, 0);

        assertTrue(validator.passed(), "深度在限制内应该通过验证");
    }

    @Test
    public void testInvalidJSON_ExceedDepthLimit() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"f\":1}}}}}}",
                        JSON.JSONType.ANY, true, 5, 0);

        assertFalse(validator.passed(), "超过深度限制应该验证失败");
    }

    // === 长度限制测试 ===

    @Test
    public void testValidJSON_WithinLengthLimit() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"key\":\"value\"}", JSON.JSONType.ANY, true, 0, 100);

        assertTrue(validator.passed(), "长度在限制内应该通过验证");
    }

    @Test
    public void testInvalidJSON_ExceedLengthLimit() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"key\":\"this is a very long value that exceeds the limit\"}",
                        JSON.JSONType.ANY, true, 0, 20);

        assertFalse(validator.passed(), "超过长度限制应该验证失败");
    }

    // === 空值测试 ===

    @Test
    public void testNullJSON() {
        ValidaX validator = ValidaX.init();
        validator.isJSON(null);

        assertTrue(validator.passed(), "null值应该通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testEmptyStringJSON() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("");

        assertTrue(validator.passed(), "空字符串应该通过验证");
    }

    // === 链式调用测试 ===

    @Test
    public void testChainedValidation_MultiplePassing() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"name\":\"Alice\"}")
                .isJSON("[1,2,3]")
                .isJSON("{\"nested\":{\"value\":true}}");

        assertTrue(validator.passed(), "多个有效JSON应该全部通过验证");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_OneFailing() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"name\":\"Alice\"}")
                .isJSON("{invalid json}")
                .isJSON("[1,2,3]");

        assertFalse(validator.passed(), "一个无效JSON应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    @Test
    public void testChainedValidation_MixedTypes() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"key\":\"value\"}", JSON.JSONType.OBJECT)
                .isJSON("[1,2,3]", JSON.JSONType.ARRAY)
                .isJSON("{}");

        assertTrue(validator.passed(), "混合类型验证应该通过");
    }

    @Test
    public void testChainedValidation_AllFailing() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{invalid1}")
                .isJSON("[invalid2]}")
                .isJSON("not json");

        assertFalse(validator.passed(), "多个无效JSON应该全部验证失败");
        assertEquals(3, validator.getErrors().size());
    }

    // === 与其他验证混合测试 ===

    @Test
    public void testMixedValidation_JSONAndEmail() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{\"email\":\"test@example.com\"}")
                .isEmail("test@example.com");

        assertTrue(validator.passed(), "JSON和Email验证都应该通过");
        assertEquals(0, validator.getErrors().size());
    }

    @Test
    public void testMixedValidation_InvalidJSON_ValidEmail() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{invalid json}")
                .isEmail("test@example.com");

        assertFalse(validator.passed(), "无效JSON应该导致验证失败");
        assertEquals(1, validator.getErrors().size());
    }

    // === 实际应用场景测试 ===

    @Test
    public void testRealWorld_APIRequest() {
        ValidaX validator = ValidaX.init();
        String apiRequest = "{\"method\":\"POST\",\"url\":\"/api/users\",\"body\":{\"name\":\"John\"}}";
        validator.isJSON(apiRequest, JSON.JSONType.OBJECT);

        assertTrue(validator.passed(), "API请求JSON应该通过验证");
    }

    @Test
    public void testRealWorld_ConfigFile() {
        ValidaX validator = ValidaX.init();
        String config = "{\"database\":{\"host\":\"localhost\",\"port\":3306},\"cache\":{\"enabled\":true}}";
        validator.isJSON(config, JSON.JSONType.OBJECT);

        assertTrue(validator.passed(), "配置文件JSON应该通过验证");
    }

    @Test
    public void testRealWorld_DataList() {
        ValidaX validator = ValidaX.init();
        String dataList = "[{\"id\":1,\"name\":\"Item1\"},{\"id\":2,\"name\":\"Item2\"}]";
        validator.isJSON(dataList, JSON.JSONType.ARRAY);

        assertTrue(validator.passed(), "数据列表JSON应该通过验证");
    }

    @Test
    public void testRealWorld_EmptyResponse() {
        ValidaX validator = ValidaX.init();
        validator.isJSON("{}");

        assertTrue(validator.passed(), "空响应JSON应该通过验证");
    }

    @Test
    public void testRealWorld_LargePayload() {
        ValidaX validator = ValidaX.init();
        StringBuilder largeJson = new StringBuilder("{\"data\":[");
        for (int i = 0; i < 100; i++) {
            if (i > 0) largeJson.append(",");
            largeJson.append("{\"id\":").append(i).append(",\"value\":\"item").append(i).append("\"}");
        }
        largeJson.append("]}");

        validator.isJSON(largeJson.toString());

        assertTrue(validator.passed(), "大型JSON payload应该通过验证");
    }

    @Test
    public void testRealWorld_SpecialCharacters() {
        ValidaX validator = ValidaX.init();
        String jsonWithSpecial = "{\"message\":\"Hello\\nWorld\\t!\",\"emoji\":\"😀\"}";
        validator.isJSON(jsonWithSpecial);

        assertTrue(validator.passed(), "包含特殊字符的JSON应该通过验证");
    }
}
