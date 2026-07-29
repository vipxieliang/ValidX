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

package io.github.vipxieliang.validx.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 消息管理器，用于处理国际化消息
 */
public class MessageManager {
    private static final String BASE_NAME = "ValidationMessages";
    private static final UTF8Control UTF8_CONTROL = new UTF8Control();
    private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH, UTF8_CONTROL);
    private static final Map<Locale, ResourceBundle> BUNDLES = new ConcurrentHashMap<>();
    
    // 添加ThreadLocal支持，用于设置当前线程的语言环境
    private static final ThreadLocal<Locale> CURRENT_LOCALE = new ThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return null; // 默认为null，表示使用系统默认语言环境
        }
    };
    
    private MessageManager() {
        // 私有构造函数，防止实例化
    }
    
    /**
     * 设置当前线程的语言环境
     * @param locale 语言环境
     */
    public static void setCurrentLocale(Locale locale) {
        CURRENT_LOCALE.set(locale);
    }
    
    /**
     * 获取当前线程的语言环境
     * @return 语言环境
     */
    public static Locale getCurrentLocale() {
        Locale locale = CURRENT_LOCALE.get();
        if (locale != null) {
            return locale;
        }
        // 如果没有设置线程本地语言环境，则使用系统默认语言环境
        return Locale.getDefault();
    }
    
    /**
     * 清除当前线程的语言环境设置，恢复默认
     */
    public static void clearCurrentLocale() {
        CURRENT_LOCALE.remove();
    }
    
    /**
     * 获取指定键和语言环境的消息
     * @param key 消息键
     * @param locale 语言环境
     * @return 对应的消息文本
     */
    public static String getMessage(String key, Locale locale) {
        try {
            ResourceBundle bundle = BUNDLES.computeIfAbsent(locale, 
                l -> ResourceBundle.getBundle(BASE_NAME, l, UTF8_CONTROL));
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // 回退到默认英文包
            try {
                return DEFAULT_BUNDLE.getString(key);
            } catch (MissingResourceException ex) {
                // 如果还是找不到，返回键本身
                return key;
            }
        }
    }
    
    /**
     * 获取指定键的消息（使用当前线程或系统默认语言环境）
     * @param key 消息键
     * @return 对应的消息文本
     */
    public static String getMessage(String key) {
        Locale locale = getCurrentLocale();
        return getMessage(key, locale);
    }
    
    /**
     * 获取注解默认消息
     * @param key 消息键
     * @param locale 语言环境
     * @return 对应的消息文本
     */
    public static String getAnnotationMessage(String key, Locale locale) {
        return getMessage(key, locale);
    }
    
    /**
     * 获取注解默认消息（使用当前线程或系统默认语言环境）
     * @param key 消息键
     * @return 对应的消息文本
     */
    public static String getAnnotationMessage(String key) {
        return getMessage(key);
    }
    
    /**
     * 自定义ResourceBundle.Control以支持UTF-8编码
     */
    private static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            
            if (stream != null) {
                try {
                    // 使用UTF-8编码读取资源文件
                    return new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                } finally {
                    stream.close();
                }
            }
            
            return super.newBundle(baseName, locale, format, loader, reload);
        }
        
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            // 对于中文环境，不回退到默认语言环境
            if (locale.equals(Locale.CHINESE) || locale.equals(Locale.SIMPLIFIED_CHINESE)) {
                return null;
            }
            return super.getFallbackLocale(baseName, locale);
        }
    }
}