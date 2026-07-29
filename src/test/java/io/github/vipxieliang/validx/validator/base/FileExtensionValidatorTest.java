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
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileExtensionValidator测试类
 */
public class FileExtensionValidatorTest {
    
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    
    // 使用注解的测试模型类
    public static class TestModel {
        @FileExtension(value = {"xls", "xlsx"}, ignoreCase = true)
        @NotNull
        private String fileName;

        public TestModel(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
    
    // 区分大小写的测试模型类
    public static class CaseSensitiveTestModel {
        @FileExtension(value = {"xls", "xlsx"}, ignoreCase = false)
        @NotNull
        private String fileName;

        public CaseSensitiveTestModel(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    @Test
    public void testValidExtensionsWithIgnoreCase() {
        // 测试有效的文件后缀名（不区分大小写）
        TestModel model1 = new TestModel("test.xls");
        Set<ConstraintViolation<TestModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "小写.xls应该通过验证");
        
        TestModel model2 = new TestModel("test.XLS");
        Set<ConstraintViolation<TestModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "大写.XLS应该通过验证");
        
        TestModel model3 = new TestModel("test.xlsx");
        Set<ConstraintViolation<TestModel>> violations3 = validator.validate(model3);
        assertTrue(violations3.isEmpty(), "小写.xlsx应该通过验证");
        
        TestModel model4 = new TestModel("test.XLSX");
        Set<ConstraintViolation<TestModel>> violations4 = validator.validate(model4);
        assertTrue(violations4.isEmpty(), "大写.XLSX应该通过验证");
        
        TestModel model5 = new TestModel("document.xls");
        Set<ConstraintViolation<TestModel>> violations5 = validator.validate(model5);
        assertTrue(violations5.isEmpty(), "包含路径的.xls应该通过验证");
        
        TestModel model6 = new TestModel(null);
        Set<ConstraintViolation<TestModel>> violations6 = validator.validate(model6);
        assertFalse(violations6.isEmpty(), "null值应该不通过@NotNull验证");
    }
    
    @Test
    public void testInvalidExtensions() {
        // 测试无效的文件后缀名
        TestModel model1 = new TestModel("test.doc");
        Set<ConstraintViolation<TestModel>> violations1 = validator.validate(model1);
        assertFalse(violations1.isEmpty(), ".doc应该验证失败");
        
        TestModel model2 = new TestModel("test.txt");
        Set<ConstraintViolation<TestModel>> violations2 = validator.validate(model2);
        assertFalse(violations2.isEmpty(), ".txt应该验证失败");
        
        TestModel model3 = new TestModel("test");
        Set<ConstraintViolation<TestModel>> violations3 = validator.validate(model3);
        assertFalse(violations3.isEmpty(), "无后缀名应该验证失败");
        
        TestModel model4 = new TestModel("test.");
        Set<ConstraintViolation<TestModel>> violations4 = validator.validate(model4);
        assertFalse(violations4.isEmpty(), "仅有.应该验证失败");
    }
    
    @Test
    public void testCaseSensitive() {
        // 测试区分大小写的情况
        CaseSensitiveTestModel model1 = new CaseSensitiveTestModel("test.xls");
        Set<ConstraintViolation<CaseSensitiveTestModel>> violations1 = validator.validate(model1);
        assertTrue(violations1.isEmpty(), "小写.xls应该通过验证");
        
        CaseSensitiveTestModel model2 = new CaseSensitiveTestModel("test.xlsx");
        Set<ConstraintViolation<CaseSensitiveTestModel>> violations2 = validator.validate(model2);
        assertTrue(violations2.isEmpty(), "小写.xlsx应该通过验证");
        
        CaseSensitiveTestModel model3 = new CaseSensitiveTestModel("test.XLS");
        Set<ConstraintViolation<CaseSensitiveTestModel>> violations3 = validator.validate(model3);
        assertFalse(violations3.isEmpty(), "大写.XLS应该验证失败");
        
        CaseSensitiveTestModel model4 = new CaseSensitiveTestModel("test.XLSX");
        Set<ConstraintViolation<CaseSensitiveTestModel>> violations4 = validator.validate(model4);
        assertFalse(violations4.isEmpty(), "大写.XLSX应该验证失败");
    }
}