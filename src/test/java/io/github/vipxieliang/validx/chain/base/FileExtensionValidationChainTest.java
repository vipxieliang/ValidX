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

import io.github.vipxieliang.validx.chain.ValidX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileExtensionValidationChainTest {

    @Test
    public void testNullValue() {
        // 测试 null 值
        ValidX chain = ValidX.init();
        chain = chain.isFileExtension(null, new String[]{"xls", "xlsx"});
        assertTrue(chain.passed(), "null值应该通过验证");
    }

    @Test
    public void testValidFileExtension() {
        ValidX chain = ValidX.init();
        chain = chain.isFileExtension((Object)"test.xls", new String[]{"xls"});
        assertTrue(chain.passed(), "有效的文件后缀名应该通过验证");
        
        chain = chain.isFileExtension((Object)"document.XLS", new String[]{"xlsx"});
        assertFalse(chain.passed(), "无效的文件后缀名不应该通过验证");
    }

    @Test
    public void testInvalidFileExtension() {
        ValidX chain = ValidX.init();
        chain = chain.isFileExtension((Object)"test.doc", new String[]{"xls"});
        assertFalse(chain.passed(), "无效的文件后缀名不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("文件后缀名不在允许的范围内", chain.getErrors().get(0));
        
        chain = ValidX.init(); // 创建新的实例
        chain = chain.isFileExtension((Object)"test", new String[]{"xls"});
        assertFalse(chain.passed(), "没有后缀名的文件不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("文件后缀名不在允许的范围内", chain.getErrors().get(0));
    }
    
    
    @Test
    public void testValidFileExtensionWithDirectValue() {
        ValidX chain = ValidX.init();
        chain = chain.isFileExtension((Object)"test.xls", new String[]{"xls", "xlsx"});
        assertTrue(chain.passed(), "有效的文件后缀名应该通过验证");
        
        chain = ValidX.init(); // 创建新的实例
        chain = chain.isFileExtension((Object)"document.XLS", new String[]{"xlsx", "xls"});
        assertTrue(chain.passed(), "有效的文件后缀名应该通过验证（忽略大小写）");
    }

    @Test
    public void testInvalidFileExtensionWithDirectValue() {
        ValidX chain = ValidX.init();
        chain = chain.isFileExtension((Object)"test.doc", new String[]{"xls", "xlsx"});
        assertFalse(chain.passed(), "无效的文件后缀名不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("文件后缀名不在允许的范围内", chain.getErrors().get(0));
        
        chain = ValidX.init(); // 创建新的实例
        chain = chain.isFileExtension((Object)"test", new String[]{"xls"});
        assertFalse(chain.passed(), "没有后缀名的文件不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("文件后缀名不在允许的范围内", chain.getErrors().get(0));
    }
    
    @Test
    public void testValidFileExtensionWithIgnoreCase() {
        ValidX chain = ValidX.init();
        chain = chain.isFileExtension((Object)"test.xls", new String[]{"XLS"}, true);
        assertTrue(chain.passed(), "有效的文件后缀名应该通过验证（忽略大小写）");
        
        chain = ValidX.init(); // 创建新的实例
        chain = chain.isFileExtension((Object)"test.XLS", new String[]{"xls"}, true);
        assertTrue(chain.passed(), "有效的文件后缀名应该通过验证（忽略大小写）");
    }
    
    @Test
    public void testInvalidFileExtensionWithIgnoreCase() {
        ValidX chain = ValidX.init();
        chain = chain.isFileExtension((Object)"test.xls", new String[]{"XLS"}, false);
        assertFalse(chain.passed(), "大小写不匹配的文件后缀名不应该通过验证");
        assertEquals(1, chain.getErrors().size());
        assertEquals("文件后缀名不在允许的范围内", chain.getErrors().get(0));
    }
}