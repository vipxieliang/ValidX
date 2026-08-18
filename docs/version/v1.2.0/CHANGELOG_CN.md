# ValidX v1.2.0 更新日志

**发布日期：** 待定

本文档记录从 v1.1.0 到 v1.2.0 的变更内容。

## 变更概览

- ⚠️ [破坏性变更](#破坏性变更-️)
  - `isStartsWith()` 和 `isEndsWith()` 链式 API 参数从 `String[]` 改为 `String`
- ✨ [新增功能](#新增功能-)
  - 新增 `@StartsWithAny` 多前缀验证注解
  - 新增 `@EndsWithAny` 多后缀验证注解
- 🔧 [功能增强](#功能增强-)
  - `@FileSize` 注解新增 MIME 类型验证，支持 `allowedTypes` 参数
  - `@StartsWith`、`@EndsWith` 新增 `ignoreCase` 参数支持大小写不敏感匹配
  - `@Url` 注解新增 `protocols` 参数，支持协议白名单配置（默认 http / https / ftp）
- 🎯 [代码重构](#代码重构-)
  - 简化多个验证器的初始化代码
  - 移除冗长的匿名注解实例创建
  - 提升代码可维护性和可读性

---

## 破坏性变更 ⚠️

### 链式 API 参数变更：isStartsWith() 和 isEndsWith()

链式 API 中的 `isStartsWith()` 和 `isEndsWith()` 方法已重构为接受单个 `String` 参数而不是 `String[]`，以更好地匹配其单值验证的目的。

**变更内容：**

**之前（v1.1.0）：**
```java
ValidX validator = ValidX.init();
// 旧 API - 接受 String[] 用于单个前缀/后缀
validator.isStartsWith("http://example.com", new String[]{"http://"});
validator.isEndsWith("photo.jpg", new String[]{".jpg"});
```

**之后（v1.2.0）：**
```java
ValidX validator = ValidX.init();
// 新 API - 接受 String 用于单个前缀/后缀
validator.isStartsWith("http://example.com", "http://");
validator.isEndsWith("photo.jpg", ".jpg");

// 对于多个前缀/后缀，使用新的 *Any 方法
validator.isStartsWithAny("http://example.com", new String[]{"http://", "https://"});
validator.isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
```

**迁移指南：**

1. **单个前缀/后缀验证：**
   ```java
   // v1.1.0 代码
   validator.isStartsWith(url, new String[]{"http://"});
   validator.isEndsWith(file, new String[]{".jpg"});

   // v1.2.0 迁移 - 移除数组包装
   validator.isStartsWith(url, "http://");
   validator.isEndsWith(file, ".jpg");
   ```

2. **多个前缀/后缀验证：**
   ```java
   // v1.1.0 代码
   validator.isStartsWith(url, new String[]{"http://", "https://"});
   validator.isEndsWith(file, new String[]{".jpg", ".jpeg", ".png"});

   // v1.2.0 迁移 - 使用新的 *Any 方法
   validator.isStartsWithAny(url, new String[]{"http://", "https://"});
   validator.isEndsWithAny(file, new String[]{".jpg", ".jpeg", ".png"});
   ```

**变更原因：**

- **语义清晰**：`isStartsWith()` 用于单值，`isStartsWithAny()` 用于多值
- **API 一致性**：与注解行为保持一致（`@StartsWith` vs `@StartsWithAny`）
- **更好的开发体验**：单值情况下更直观、更简洁
- **类型安全**：消除单值和多值验证之间的混淆

**影响范围：**
- 仅影响使用 `isStartsWith()` 或 `isEndsWith()` 方法的链式 API 用户
- 基于注解的验证（`@StartsWith`、`@EndsWith`）保持不变
- 简单迁移：单值情况移除数组包装，或使用 `*Any` 方法处理多值

---

## 新增功能 ✨

### 1. @StartsWithAny 多前缀验证注解

新增专用的多前缀验证注解，用于验证字符串是否以指定的任意一个前缀开头。

**功能特性：**
- 验证字符串是否以指定的任意一个前缀开头
- 支持多个前缀的验证（如：URL 协议验证、称谓验证）
- 支持 `ignoreCase` 参数进行大小写不敏感匹配
- null 和空字符串默认通过验证
- 完整的国际化支持（9 种语言）
- 与 `@StartsWith` 注解互补，提供灵活的前缀验证

**注解方式示例：**

```java
public class RequestDTO {
    // 示例 1：URL 协议验证
    @StartsWithAny({"http://", "https://"})
    private String url;

    // 示例 2：称谓验证
    @StartsWithAny({"Mr.", "Mrs.", "Ms.", "Dr."})
    private String title;

    // 示例 3：中文姓氏验证
    @StartsWithAny({"张", "王", "李", "赵"})
    private String chineseName;

    // 示例 4：文件路径验证
    @StartsWithAny({"/home/", "/usr/", "/opt/"})
    private String filePath;

    // 示例 5：忽略大小写验证
    @StartsWithAny(value = {"http://", "https://"}, ignoreCase = true)
    private String urlCaseInsensitive;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 基本用法
validator.field("URL").isStartsWithAny("http://example.com", new String[]{"http://", "https://"});

// 忽略大小写
validator.field("URL").isStartsWithAny("HTTP://example.com", new String[]{"http://", "https://"}, true);

// 多个前缀选项
validator.field("姓名").isStartsWithAny("Mr. Smith", new String[]{"Mr.", "Mrs.", "Ms.", "Dr."});

// 中文文本验证
validator.field("姓名").isStartsWithAny("张三", new String[]{"张", "王", "李", "赵"});

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：URL 安全验证
@RestController
public class LinkController {
    @PostMapping("/links")
    public Result addLink(@Valid @RequestBody LinkDTO dto) {
        return linkService.add(dto);
    }
}

public class LinkDTO {
    @NotBlank(message = "URL 不能为空")
    @StartsWithAny({"http://", "https://"})
    private String url;
}

// 场景 2：表单验证带称谓
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("称谓").isStartsWithAny(fullName, new String[]{"Mr.", "Mrs.", "Ms.", "Dr."});

// 场景 3：文件路径安全验证
public class FileAccessDTO {
    @StartsWithAny({"/home/", "/tmp/", "/var/log/"})
    private String allowedPath;
}
```

**注意事项：**
- 默认区分大小写（如："HTTP://" 不会匹配 "http://"），可通过 `ignoreCase = true` 进行大小写不敏感匹配
- null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty` 使用）
- 空前缀数组会导致验证失败
- 空字符串前缀会匹配所有字符串（任何字符串都以空字符串开头）
- 常见应用场景：URL 验证、文件路径验证、称谓/前缀验证、中文姓氏验证

---

### 2. @EndsWithAny 多后缀验证注解

新增专用的多后缀验证注解，用于验证字符串是否以指定的任意一个后缀结尾。

**功能特性：**
- 验证字符串是否以指定的任意一个后缀结尾
- 支持多个后缀的验证（如：文件扩展名验证、名称后缀验证）
- 支持 `ignoreCase` 参数进行大小写不敏感匹配
- null 和空字符串默认通过验证
- 完整的国际化支持（9 种语言）
- 与 `@EndsWith` 注解互补，提供灵活的后缀验证

**注解方式示例：**

```java
public class FileDTO {
    // 示例 1：图片文件验证
    @EndsWithAny({".jpg", ".jpeg", ".png", ".gif"})
    private String imageFile;

    // 示例 2：文档文件验证
    @EndsWithAny({".txt", ".doc", ".docx", ".pdf"})
    private String documentFile;

    // 示例 3：中文姓名后缀验证
    @EndsWithAny({"先生", "女士", "小姐"})
    private String chineseName;

    // 示例 4：压缩文件验证
    @EndsWithAny({".zip", ".rar", ".7z", ".tar.gz"})
    private String archiveFile;

    // 示例 5：忽略大小写验证
    @EndsWithAny(value = {".jpg", ".jpeg", ".png"}, ignoreCase = true)
    private String imageCaseInsensitive;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 基本用法
validator.field("文件").isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png", ".gif"});

// 忽略大小写
validator.field("文件").isEndsWithAny("photo.JPG", new String[]{".jpg", ".jpeg", ".png"}, true);

// 多个后缀选项
validator.field("文档").isEndsWithAny("report.pdf", new String[]{".txt", ".doc", ".docx", ".pdf"});

// 中文文本验证
validator.field("姓名").isEndsWithAny("张先生", new String[]{"先生", "女士", "小姐"});

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：文件上传验证
@RestController
public class UploadController {
    @PostMapping("/upload")
    public Result upload(@Valid @RequestBody UploadDTO dto) {
        return uploadService.handle(dto);
    }
}

public class UploadDTO {
    @NotBlank(message = "文件名不能为空")
    @EndsWithAny({".jpg", ".jpeg", ".png", ".gif"})
    private String fileName;
}

// 场景 2：文档类型验证
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("文档").isEndsWithAny(fileName, new String[]{".txt", ".doc", ".docx", ".pdf"});

// 场景 3：中文姓名后缀验证
public class PersonDTO {
    @EndsWithAny({"先生", "女士", "小姐", "教授", "博士"})
    private String fullName;
}
```

**注意事项：**
- 默认区分大小写（如：".JPG" 不会匹配 ".jpg"），可通过 `ignoreCase = true` 进行大小写不敏感匹配
- null 和空字符串默认通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty` 使用）
- 空后缀数组会导致验证失败
- 空字符串后缀会匹配所有字符串（任何字符串都以空字符串结尾）
- 常见应用场景：文件扩展名验证、压缩格式验证、名称后缀验证、中文称谓验证

---

## 功能增强 🔧

### 1. @StartsWith、@EndsWith 支持忽略大小写

为已有的前缀和后缀验证注解新增 `ignoreCase` 参数，支持大小写不敏感的字符串匹配。

**新功能：**
- 新增可选的 `ignoreCase` 参数，默认为 `false`（区分大小写）
- 为 v1.0.0 版本的 `@StartsWith` 和 `@EndsWith` 注解增加新功能
- 链式 API 同步支持 `ignoreCase` 参数
- 保持向后兼容，默认行为不变

**注：** 新增的 `@StartsWithAny` 和 `@EndsWithAny` 注解也支持 `ignoreCase` 参数，详见[新增功能](#新增功能-)部分。

**注解方式示例：**

```java
public class RequestDTO {
    // 示例 1：URL 协议验证（忽略大小写）
    @StartsWith(startsWith = "http://", ignoreCase = true)
    private String url;  // "HTTP://example.com" 或 "http://example.com" 都通过

    // 示例 2：文件扩展名验证（忽略大小写）
    @EndsWith(endsWith = ".jpg", ignoreCase = true)
    private String imageFile;  // "photo.JPG" 或 "photo.jpg" 都通过
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// StartsWith - 忽略大小写
validator.isStartsWith("HTTP://example.com", "http://", true);  // 通过

// EndsWith - 忽略大小写
validator.isEndsWith("file.TXT", ".txt", true);  // 通过

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：用户输入的 URL 验证（用户可能输入大写）
@RestController
public class LinkController {
    @PostMapping("/links")
    public Result addLink(@Valid @RequestBody LinkDTO dto) {
        return linkService.add(dto);
    }
}

public class LinkDTO {
    @NotBlank(message = "URL 不能为空")
    @StartsWith(startsWith = "http://", ignoreCase = true)
    private String url;  // 接受 "HTTP://", "Http://", "http://" 等
}

// 场景 2：文件扩展名验证（Windows 用户可能使用大写扩展名）
public class FileDTO {
    @NotBlank(message = "文件名不能为空")
    @EndsWith(endsWith = ".txt", ignoreCase = true)
    private String fileName;  // 接受 ".TXT", ".Txt", ".txt" 等
}

// 场景 3：链式验证文件路径
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("文件路径").isStartsWith(filePath, "/home/", true);  // 接受 "/HOME/", "/Home/" 等
```

**注意事项：**
- `ignoreCase` 参数默认为 `false`，保持原有的区分大小写行为
- 设置 `ignoreCase = true` 后，使用 `toLowerCase()` 进行大小写不敏感比较
- 适用于需要兼容用户输入大小写不一致的场景
- 对性能影响极小（仅增加一次 `toLowerCase()` 调用）

---

### 2. @FileSize 支持 MIME 类型验证

增强了 `@FileSize` 注解，新增 `allowedTypes` 参数用于验证文件 MIME 类型，特别适用于 Spring 应用中的 `MultipartFile` 验证。

**新功能：**
- 新增可选的 `allowedTypes` 参数，用于限制允许的 MIME 类型
- 与 Spring 的 `MultipartFile` 无缝集成
- 在单个注解中同时验证文件大小和 MIME 类型
- 继续支持 `java.io.File`、`java.nio.file.Path` 和 `byte[]` 类型

**注解方式示例：**

```java
public class FileUploadDTO {
    // 示例 1：图片文件带大小和类型验证
    @FileSize(max = "5MB", allowedTypes = {"image/jpeg", "image/png", "image/gif"})
    private MultipartFile avatar;

    // 示例 2：文档文件带类型限制
    @FileSize(min = "1KB", max = "10MB", allowedTypes = {"application/pdf", "application/msword"})
    private MultipartFile document;

    // 示例 3：多种图片格式
    @FileSize(max = "2MB", allowedTypes = {"image/jpeg", "image/jpg", "image/png", "image/webp"})
    private MultipartFile photo;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 带 MIME 类型验证
validator.field("头像").isFileSize(
    avatarFile,
    "0B",           // 最小大小
    "5MB",          // 最大大小
    new String[]{"image/jpeg", "image/png"}  // 允许的 MIME 类型
);
```

**实际应用场景：**

```java
// 场景 1：个人资料头像上传
@RestController
public class ProfileController {
    @PostMapping("/avatar")
    public Result uploadAvatar(@Valid @RequestBody AvatarDTO dto) {
        return profileService.updateAvatar(dto);
    }
}

public class AvatarDTO {
    @NotNull(message = "头像不能为空")
    @FileSize(max = "5MB", allowedTypes = {"image/jpeg", "image/png", "image/gif"})
    private MultipartFile avatar;
}

// 场景 2：文档上传带严格类型控制
public class DocumentDTO {
    @FileSize(
        min = "1KB",
        max = "20MB",
        allowedTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}
    )
    private MultipartFile document;
}
```

**注意事项：**
- `allowedTypes` 参数是可选的；为空时仅验证大小
- MIME 类型验证仅对提供 MIME 信息的文件类型有效（如 `MultipartFile`）
- 对于 `File`、`Path` 和 `byte[]` 类型，`allowedTypes` 会被忽略
- 常见 MIME 类型：`image/jpeg`、`image/png`、`image/gif`、`application/pdf`、`text/plain` 等

---

### 3. @Url 支持协议白名单

为 `@Url` 注解新增 `protocols` 参数，支持配置允许的 URL 协议白名单，用于限制或扩展可接受的 URL 协议。

**新功能：**
- 新增可选的 `protocols` 参数，用于配置允许的协议白名单
- 默认白名单为 `{"http", "https", "ftp"}`，与历史版本行为一致（向下兼容）
- 链式 API 同步支持 `isUrl(value, protocols...)` 重载
- 协议匹配大小写不敏感
- 可通过白名单收紧（如仅 HTTPS）或扩展允许的协议范围

**注解方式示例：**

```java
public class RequestDTO {
    // 默认白名单：http / https / ftp（向下兼容）
    @Url
    private String url;

    // 仅允许 HTTPS
    @Url(protocols = {"https"})
    private String secureUrl;

    // 仅允许 Web 协议
    @Url(protocols = {"http", "https"})
    private String webUrl;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 默认白名单（http / https / ftp）
validator.isUrl("http://example.com");

// 指定协议白名单（仅 https）
validator.isUrl("https://example.com", "https");
```

**注意事项：**
- `protocols` 参数是可选的；默认 `{"http", "https", "ftp"}` 与历史版本保持一致，无破坏性变更
- 协议匹配大小写不敏感
- null 和空字符串仍通过验证（如需必填请配合 `@NotNull` 或 `@NotEmpty` 使用）

---

## 代码重构 🎯

### 验证器初始化简化

在整个代码库中显著简化了验证器初始化代码，移除了冗长的匿名注解实例创建，提升了可维护性。

**改进内容：**

1. **移除冗长的匿名类**
   - 消除了重复的匿名注解实现
   - 验证器现在支持直接参数初始化
   - 减少代码量约 500+ 行

2. **受影响的验证器：**
   - `InValidator` 和 `NotInValidator`
   - `EnumValidator`
   - `FileExtensionValidator`
   - `FileSizeValidator` 家族（全部 4 个变体）
   - `PasswordValidator`
   - `StartsWith` 和 `EndsWithValidator`
   - `UUIDValidator`、`Base64Validator`、`AgeValidator`
   - `JSONValidator`、`PhoneNumberValidator`
   - `TimestampValidator`、`IpValidator`
   - `StockCodeValidator`、`FinancialProductCodeValidator`

3. **优势：**
   - **更简洁的代码**：平均每个验证器减少 30+ 行
   - **更好的可维护性**：更易于理解和修改
   - **性能提升**：初始化略微更快
   - **一致的模式**：所有验证器现在遵循相同的初始化方式

**之前（旧代码）：**

```java
public void validateIn(Object value, String[] values, List<String> errors, Locale locale) {
    InValidator validator = new InValidator();

    // 创建模拟注解实例（30+ 行样板代码）
    In inAnnotation = new In() {
        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return In.class;
        }

        @Override
        public String[] value() {
            return values != null ? values : new String[0];
        }

        // ... 更多样板方法
    };

    validator.initialize(inAnnotation);
    if (!validator.isValid(value, null)) {
        errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.in", locale));
    }
}
```

**之后（新代码）：**

```java
public void validateIn(Object value, String[] values, List<String> errors, Locale locale) {
    InValidator validator = new InValidator();
    validator.initialize(values);
    if (!validator.isValid(value, null)) {
        errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.in", locale));
    }
}
```

**影响：**
- 总计减少：500+ 行代码
- 重构文件：20+ 个验证器类
- 在整个验证链中提升了代码可读性
- 无功能变更 - 所有现有测试无需修改即可通过

---

## 国际化支持 🌍

两个新注解均支持以下 9 种语言：

- **简体中文** - `ValidationMessages.properties` 和 `ValidationMessages_zh.properties`
- **英语** - `ValidationMessages_en.properties`
- **日语** - `ValidationMessages_ja.properties`
- **韩语** - `ValidationMessages_ko.properties`
- **法语** - `ValidationMessages_fr.properties`
- **德语** - `ValidationMessages_de.properties`
- **西班牙语** - `ValidationMessages_es.properties`
- **俄语** - `ValidationMessages_ru.properties`

**错误消息：**
- `@StartsWithAny`: "不是以指定的任意一个字符串开头"
- `@EndsWithAny`: "不是以指定的任意一个字符串结尾"

所有语言包的消息格式保持一致，采用正确的 Unicode 编码。

---

## 测试覆盖 🧪

两个新功能均具有全面的测试覆盖：

**验证器测试（Bean Validation 框架）：**
- `StartsWithAnyValidatorTest`：9 个测试用例，涵盖有效/无效场景、null/空值、大小写敏感、空数组
- `EndsWithAnyValidatorTest`：9 个测试用例，具有相同的全面覆盖

**链式验证测试：**
- `StartsWithAnyValidationChainTest`：13 个测试用例，用于链式 API 使用
- `EndsWithAnyValidationChainTest`：13 个测试用例，用于链式 API 使用

**国际化测试：**
- `StartsWithAnyI18nTest`：8 个测试用例（每种语言一个）
- `EndsWithAnyI18nTest`：8 个测试用例（每种语言一个）

**总计：** 60 个新测试用例，全部通过 ✅

---

## 相关链接 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.2.0)
- 📖 [完整文档](../../../README.cn.md)
- 🐛 [问题反馈](https://github.com/vipxieliang/ValidX/issues)
- 💡 [功能建议](https://github.com/vipxieliang/ValidX/issues/new)

---

由 ValidX 团队用 ❤️ 发布
