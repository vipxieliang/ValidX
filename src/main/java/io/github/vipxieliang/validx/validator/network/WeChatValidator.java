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

package io.github.vipxieliang.validx.validator.network;

import io.github.vipxieliang.validx.annotations.WeChat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 微信账号验证器实现
 * 微信账号规则:
 * 1. 可使用6-20个字母、数字、下划线和减号
 * 2. 必须以字母开头（字母不区分大小写）
 * 3. 不支持设置中文
 */
public class WeChatValidator implements ConstraintValidator<WeChat, String> {
    
    /**
     * 微信账号正则表达式
     * ^[a-zA-Z]     - 必须以字母开头
     * [a-zA-Z\d_-]{5,19} - 后跟5到19位字母、数字、下划线或减号
     * $             - 字符串结束
     */
    private static final Pattern WECHAT_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z\\d_-]{5,19}$");
    
    @Override
    public void initialize(WeChat constraintAnnotation) {
        // 初始化操作
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空值应该由@NotNull等其他注解处理
        }
        
        // 使用正则表达式验证格式（正则表达式已包含长度检查）
        return WECHAT_PATTERN.matcher(value).matches();
    }
}