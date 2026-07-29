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


import io.github.vipxieliang.validx.annotations.FileExtension;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class FileExtensionValidator implements ConstraintValidator<FileExtension, Object> {
    private String[] extensions;
    private boolean ignoreCase;

    @Override
    public void initialize(FileExtension constraintAnnotation) {
        extensions = constraintAnnotation.value();
        ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        String fileName = String.valueOf(value);
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return false; // 没有后缀名或以点结尾
        }
        
        String fileExtension = fileName.substring(lastDotIndex + 1);
        
        for (String extension : extensions) {
            if (ignoreCase) {
                if (extension.equalsIgnoreCase(fileExtension)) {
                    return true;
                }
            } else {
                if (extension.equals(fileExtension)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}