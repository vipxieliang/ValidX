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


import io.github.vipxieliang.validx.annotations.Enum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 枚举验证器
 * 验证字符串或集合中每个元素是否为指定枚举类的值
 */
public class EnumValidator implements ConstraintValidator<Enum, Object> {

    String field;
    Class<?>[] cls; //枚举类

    @Override
    public void initialize(Enum constraintAnnotation) {
        cls = new Class<?>[] { constraintAnnotation.target() };
        field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 如果值为null，认为验证通过（与大多数验证器保持一致）
        if (value == null) {
            return true;
        }
        
        // 如果值是集合类型，验证每个元素
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            for (Object item : collection) {
                if (!isEnumValue(item)) {
                    return false;
                }
            }
            return true;
        }
        
        // 如果值是数组，验证每个元素
        if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                Object[] array = (Object[]) value;
                for (Object item : array) {
                    if (!isEnumValue(item)) {
                        return false;
                    }
                }
                return true;
            }
            
            // 处理基本类型数组
            if (value instanceof int[]) {
                int[] array = (int[]) value;
                for (int item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof long[]) {
                long[] array = (long[]) value;
                for (long item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof double[]) {
                double[] array = (double[]) value;
                for (double item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof float[]) {
                float[] array = (float[]) value;
                for (float item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof boolean[]) {
                boolean[] array = (boolean[]) value;
                for (boolean item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof byte[]) {
                byte[] array = (byte[]) value;
                for (byte item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof char[]) {
                char[] array = (char[]) value;
                for (char item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
            
            if (value instanceof short[]) {
                short[] array = (short[]) value;
                for (short item : array) {
                    if (!isEnumValue(String.valueOf(item))) {
                        return false;
                    }
                }
                return true;
            }
        }
        
        // 如果不是集合或数组，行为类似于原来的验证器
        return isEnumValue(value);
    }
    
    private boolean isEnumValue(Object value) {
        // 处理null值情况
        if (value == null) {
            return true;
        }
        
        // 处理空字符串情况
        String stringValue = value.toString();
        if (stringValue.isEmpty()) {
            return true; // 根据测试，空字符串应该返回true
        }
        
        if (cls.length > 0) {
            for (Class<?> cl : cls) {
                try {
                    if (cl.isEnum()) {
                        //枚举类验证
                        Object[] objs = cl.getEnumConstants();
                        Method codeMethod = cl.getMethod(field);
                        for (Object obj : objs) {
                            Object code = codeMethod.invoke(obj, null);
                            if (stringValue.equals(code.toString())) {
                                return true;
                            }
                        }
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}