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

import io.github.vipxieliang.validx.annotations.JSON;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * JSON格式验证器
 * 使用轻量级解析器验证JSON格式，不依赖第三方库
 *
 * @author vipxieliang
 * @since 1.0.0
 */
public class JSONValidator implements ConstraintValidator<JSON, String> {

    private JSON.JSONType type;
    private boolean strict;
    private int maxDepth;
    private int maxLength;

    @Override
    public void initialize(JSON constraintAnnotation) {
        initialize(constraintAnnotation.type(), constraintAnnotation.strict(),
                   constraintAnnotation.maxDepth(), constraintAnnotation.maxLength());
    }

    /**
     * 直接使用参数初始化验证器（用于链式调用）
     *
     * @param type JSON类型
     * @param strict 是否严格模式
     * @param maxDepth 最大深度
     * @param maxLength 最大长度
     */
    public void initialize(JSON.JSONType type, boolean strict, int maxDepth, int maxLength) {
        this.type = type;
        this.strict = strict;
        this.maxDepth = maxDepth;
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null或空字符串由@NotNull/@NotEmpty处理
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        String trimmed = value.trim();

        // 检查长度限制
        if (maxLength > 0 && trimmed.length() > maxLength) {
            return false;
        }

        try {
            JSONParser parser = new JSONParser(trimmed, strict, maxDepth);
            Object result = parser.parse();

            // 检查JSON类型
            if (type == JSON.JSONType.OBJECT && !(result instanceof JSONObject)) {
                return false;
            }
            if (type == JSON.JSONType.ARRAY && !(result instanceof JSONArray)) {
                return false;
            }

            return true;
        } catch (JSONParseException e) {
            return false;
        }
    }

    /**
     * 静态验证方法，供链式调用使用
     *
     * @param value 要验证的JSON字符串
     * @param type JSON类型（OBJECT/ARRAY/ANY）
     * @param strict 是否启用严格模式
     * @param maxDepth 最大嵌套深度（0表示不限制）
     * @param maxLength 最大字符串长度（0表示不限制）
     * @return 如果值为有效的JSON格式则返回true，否则返回false
     */
    public static boolean isValidJSON(String value, JSON.JSONType type, boolean strict,
                                     int maxDepth, int maxLength) {
        JSONValidator validator = new JSONValidator();
        validator.type = type;
        validator.strict = strict;
        validator.maxDepth = maxDepth;
        validator.maxLength = maxLength;
        return validator.isValid(value, null);
    }

    // ==================== 内部类：轻量级JSON解析器 ====================

    /**
     * JSON解析异常
     */
    private static class JSONParseException extends Exception {
        public JSONParseException(String message) {
            super(message);
        }
    }

    /**
     * JSON对象标记类
     */
    private static class JSONObject {}

    /**
     * JSON数组标记类
     */
    private static class JSONArray {}

    /**
     * 轻量级JSON解析器
     */
    private static class JSONParser {
        private final String json;
        private final boolean strict;
        private final int maxDepth;
        private int pos;
        private int currentDepth;

        public JSONParser(String json, boolean strict, int maxDepth) {
            this.json = json;
            this.strict = strict;
            this.maxDepth = maxDepth;
            this.pos = 0;
            this.currentDepth = 0;
        }

        public Object parse() throws JSONParseException {
            skipWhitespace();
            Object result = parseValue();
            skipWhitespace();

            // 确保解析完整个字符串
            if (pos < json.length()) {
                throw new JSONParseException("Extra characters after JSON value");
            }

            return result;
        }

        private Object parseValue() throws JSONParseException {
            skipWhitespace();

            if (pos >= json.length()) {
                throw new JSONParseException("Unexpected end of JSON");
            }

            char c = json.charAt(pos);

            switch (c) {
                case '{':
                    return parseObject();
                case '[':
                    return parseArray();
                case '"':
                    return parseString();
                case 't':
                case 'f':
                    return parseBoolean();
                case 'n':
                    return parseNull();
                default:
                    if (c == '-' || (c >= '0' && c <= '9')) {
                        return parseNumber();
                    }
                    throw new JSONParseException("Unexpected character: " + c);
            }
        }

        private JSONObject parseObject() throws JSONParseException {
            // 检查深度
            if (maxDepth > 0 && currentDepth >= maxDepth) {
                throw new JSONParseException("Maximum depth exceeded");
            }

            currentDepth++;
            pos++; // 跳过 '{'
            skipWhitespace();

            // 空对象
            if (pos < json.length() && json.charAt(pos) == '}') {
                pos++;
                currentDepth--;
                return new JSONObject();
            }

            while (pos < json.length()) {
                skipWhitespace();

                // 解析键
                if (json.charAt(pos) != '"') {
                    throw new JSONParseException("Expected property name in quotes");
                }
                parseString();

                skipWhitespace();

                // 期望冒号
                if (pos >= json.length() || json.charAt(pos) != ':') {
                    throw new JSONParseException("Expected ':' after property name");
                }
                pos++;

                // 解析值
                parseValue();

                skipWhitespace();

                if (pos >= json.length()) {
                    throw new JSONParseException("Unexpected end of JSON in object");
                }

                char next = json.charAt(pos);
                if (next == '}') {
                    pos++;
                    currentDepth--;
                    return new JSONObject();
                } else if (next == ',') {
                    pos++;
                    skipWhitespace();
                    // 检查尾随逗号
                    if (strict && pos < json.length() && json.charAt(pos) == '}') {
                        throw new JSONParseException("Trailing comma not allowed in strict mode");
                    }
                } else {
                    throw new JSONParseException("Expected ',' or '}' in object");
                }
            }

            throw new JSONParseException("Unexpected end of JSON in object");
        }

        private JSONArray parseArray() throws JSONParseException {
            // 检查深度
            if (maxDepth > 0 && currentDepth >= maxDepth) {
                throw new JSONParseException("Maximum depth exceeded");
            }

            currentDepth++;
            pos++; // 跳过 '['
            skipWhitespace();

            // 空数组
            if (pos < json.length() && json.charAt(pos) == ']') {
                pos++;
                currentDepth--;
                return new JSONArray();
            }

            while (pos < json.length()) {
                parseValue();
                skipWhitespace();

                if (pos >= json.length()) {
                    throw new JSONParseException("Unexpected end of JSON in array");
                }

                char next = json.charAt(pos);
                if (next == ']') {
                    pos++;
                    currentDepth--;
                    return new JSONArray();
                } else if (next == ',') {
                    pos++;
                    skipWhitespace();
                    // 检查尾随逗号
                    if (strict && pos < json.length() && json.charAt(pos) == ']') {
                        throw new JSONParseException("Trailing comma not allowed in strict mode");
                    }
                } else {
                    throw new JSONParseException("Expected ',' or ']' in array");
                }
            }

            throw new JSONParseException("Unexpected end of JSON in array");
        }

        private String parseString() throws JSONParseException {
            pos++; // 跳过开始的引号
            StringBuilder sb = new StringBuilder();

            while (pos < json.length()) {
                char c = json.charAt(pos);

                if (c == '"') {
                    pos++;
                    return sb.toString();
                } else if (c == '\\') {
                    // 转义字符
                    pos++;
                    if (pos >= json.length()) {
                        throw new JSONParseException("Unexpected end of JSON in string escape");
                    }
                    char escaped = json.charAt(pos);
                    switch (escaped) {
                        case '"':
                        case '\\':
                        case '/':
                            sb.append(escaped);
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            // Unicode转义
                            pos++;
                            if (pos + 3 >= json.length()) {
                                throw new JSONParseException("Invalid unicode escape");
                            }
                            String hex = json.substring(pos, pos + 4);
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                                pos += 3;
                            } catch (NumberFormatException e) {
                                throw new JSONParseException("Invalid unicode escape");
                            }
                            break;
                        default:
                            throw new JSONParseException("Invalid escape character: \\" + escaped);
                    }
                    pos++;
                } else if (c < 0x20) {
                    throw new JSONParseException("Unescaped control character in string");
                } else {
                    sb.append(c);
                    pos++;
                }
            }

            throw new JSONParseException("Unterminated string");
        }

        private Boolean parseBoolean() throws JSONParseException {
            if (json.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            } else if (json.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            throw new JSONParseException("Invalid boolean value");
        }

        private Object parseNull() throws JSONParseException {
            if (json.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            throw new JSONParseException("Invalid null value");
        }

        private Number parseNumber() throws JSONParseException {
            int start = pos;

            // 负号
            if (json.charAt(pos) == '-') {
                pos++;
            }

            // 整数部分
            if (pos >= json.length() || !isDigit(json.charAt(pos))) {
                throw new JSONParseException("Invalid number");
            }

            if (json.charAt(pos) == '0') {
                pos++;
            } else {
                while (pos < json.length() && isDigit(json.charAt(pos))) {
                    pos++;
                }
            }

            // 小数部分
            if (pos < json.length() && json.charAt(pos) == '.') {
                pos++;
                if (pos >= json.length() || !isDigit(json.charAt(pos))) {
                    throw new JSONParseException("Invalid number");
                }
                while (pos < json.length() && isDigit(json.charAt(pos))) {
                    pos++;
                }
            }

            // 指数部分
            if (pos < json.length() && (json.charAt(pos) == 'e' || json.charAt(pos) == 'E')) {
                pos++;
                if (pos < json.length() && (json.charAt(pos) == '+' || json.charAt(pos) == '-')) {
                    pos++;
                }
                if (pos >= json.length() || !isDigit(json.charAt(pos))) {
                    throw new JSONParseException("Invalid number");
                }
                while (pos < json.length() && isDigit(json.charAt(pos))) {
                    pos++;
                }
            }

            String numberStr = json.substring(start, pos);
            try {
                if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
                    return Double.parseDouble(numberStr);
                } else {
                    return Long.parseLong(numberStr);
                }
            } catch (NumberFormatException e) {
                throw new JSONParseException("Invalid number format");
            }
        }

        private void skipWhitespace() {
            while (pos < json.length()) {
                char c = json.charAt(pos);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    pos++;
                } else {
                    break;
                }
            }
        }

        private boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }
    }
}
