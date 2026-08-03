/*
 * Copyright 2026-2026 vipxieliang
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

package io.github.vipxieliang.validx.annotations;

import io.github.vipxieliang.validx.validator.china.ChineseNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * 中国人姓名验证器
 * 验证字符串是否符合中国人姓名规范
 * </p>
 *
 * <p>验证规则：</p>
 * <ul>
 *   <li>只能包含中文字符</li>
 *   <li>长度在 2-50 个字符之间，覆盖所有中文姓名包括极长的少数民族姓名</li>
 *   <li>支持少数民族姓名中的间隔号 "·"</li>
 *   <li>不能包含数字、字母、特殊字符</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code
 * // 基本使用，支持所有中文姓名
 * @ChineseName
 * private String realName;
 * }
 * </pre>
 *
 * <p>支持的姓名示例：</p>
 * <ul>
 *   <li>汉族姓名：张三、李四、欧阳修、诸葛亮</li>
 *   <li>少数民族姓名：买买提·吐尔逊、迪丽热巴·迪力木拉提</li>
 *   <li>历史人物名：爱新觉罗·玄烨</li>
 * </ul>
 *
 * @author vipxieliang
 * @since 2026/08/03
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ChineseNameValidator.class)
public @interface ChineseName {

    /**
     * 错误消息
     */
    String message() default "{io.github.vipxieliang.validx.annotation.chinese.name}";

    /**
     * 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};
}
