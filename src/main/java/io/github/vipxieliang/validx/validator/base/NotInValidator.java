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


import io.github.vipxieliang.validx.annotations.NotIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Objects;

/**
 * NotIn验证器
 * 验证对象或集合中每个元素是否不在指定的数组中
 */
public class NotInValidator implements ConstraintValidator<NotIn, Object> {
    private String[] arrays;

    @Override
    public void initialize(NotIn constraintAnnotation) {
        arrays = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 如果值为null，认为验证通过（与大多数验证器保持一致）
        if (value == null) {
            return true;
        }
        
        // 如果值是集合类型，验证每个元素都不在数组中
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            for (Object item : collection) {
                if (isInArray(item)) {
                    return false;
                }
            }
            return true;
        }
        
        // 如果值是数组，验证每个元素都不在数组中
        if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                Object[] array = (Object[]) value;
                for (Object item : array) {
                    if (isInArray(item)) {
                        return false;
                    }
                }
                return true;
            }
            
            // 处理基本类型数组
            if (value instanceof int[]) {
                int[] array = (int[]) value;
                for (int item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof long[]) {
                long[] array = (long[]) value;
                for (long item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof double[]) {
                double[] array = (double[]) value;
                for (double item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof float[]) {
                float[] array = (float[]) value;
                for (float item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof boolean[]) {
                boolean[] array = (boolean[]) value;
                for (boolean item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof byte[]) {
                byte[] array = (byte[]) value;
                for (byte item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof char[]) {
                char[] array = (char[]) value;
                for (char item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof short[]) {
                short[] array = (short[]) value;
                for (short item : array) {
                    if (isInArray(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
        }
        
        // 如果不是集合或数组，行为类似于原来的NotIn验证器
        return !isInArray(value);
    }
    
    private boolean isInArray(Object value) {
        if (value == null) {
            for (String s : arrays) {
                if (s == null) {
                    return true;
                }
            }
            return false;
        }
        
        for (String s : arrays) {
            if (Objects.equals(s, String.valueOf(value))) {
                return true;
            }
        }
        return false;
    }
}