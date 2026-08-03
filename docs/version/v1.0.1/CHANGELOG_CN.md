# ValidX v1.0.1 更新日志

**发布日期：** 2026年7月31日

本文档记录从 v1.0.0 到 v1.0.1 的变更内容。

## 变更概览

- ✨ 新增 @Contains 验证注解
- 🔄 核心类重命名 ValidaX → ValidX
- 📖 文档优化和版本追踪
- 📜 添加开源协议

---

## 新增功能 ✨

### @Contains 验证注解

新增字符串包含子串验证器，支持灵活的匹配策略。

**功能特性：**
- OR 逻辑：匹配任意一个子字符串即可
- AND 逻辑：必须匹配所有子字符串
- 大小写忽略选项
- 完整的国际化支持（8 种语言）

**注解方式示例：**

```java
// 示例 1：OR 逻辑 - 包含 "@" 或 ".com" 任意一个即可
@Contains({"@", ".com"})
private String email;

// 示例 2：AND 逻辑 - 必须同时包含 "@" 和 "."
@Contains(value = {"@", "."}, matchAll = true)
private String strictEmail;

// 示例 3：忽略大小写匹配
@Contains(value = {"HELLO", "WORLD"}, ignoreCase = true)
private String greeting;

// 示例 4：自定义错误消息
@Contains(value = {"产品", "服务"}, message = "描述必须包含'产品'或'服务'")
private String description;
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// OR 逻辑（默认）
validator.field("邮箱")
    .isContains("test@example.com", new String[]{"@", ".com"});

// AND 逻辑
validator.field("邮箱")
    .isContains("test@example.com", new String[]{"@", "."}, false, true);

// 忽略大小写
validator.field("问候语")
    .isContains("Hello World", new String[]{"hello", "world"}, true, false);

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：邮箱格式简单验证
public class UserDTO {
    @Contains(value = {"@", "."}, matchAll = true)
    private String email;
}

// 场景 2：密码强度检查 - 必须包含字母和数字
ValidX.init()
    .field("密码")
    .isContains(password, new String[]{"[a-zA-Z]", "[0-9]"}, false, true);

// 场景 3：内容关键词过滤
@Contains({"敏感词1", "敏感词2", "敏感词3"})
private String content; // 包含任意一个关键词即触发

// 场景 4：URL 参数验证
ValidX.init()
    .field("回调地址")
    .isContains(callbackUrl, new String[]{"https://", "callback"}, false, true);
```

### 版本追踪

在文档快速参考表中添加"版本"列，标注每个验证注解的引入版本。

**示例：**

| 注解 | 说明 | 版本 |
|------|------|------|
| @Contains | 字符串包含验证 | 1.0.1 |
| @ChineseIdCard | 身份证验证 | 1.0.0 |
| @ChinesePhone | 手机号验证 | 1.0.0 |

### 开源协议

添加 Apache License 2.0 许可证文件。

---

## 变更内容 🔄

### 核心类重命名

将主验证类从 `ValidaX` 重命名为 `ValidX`，命名更简洁统一。

**影响范围：**
- 核心类：`ValidaX` → `ValidX`
- 测试类：`ValidaXConfigTest` → `ValidXConfigTest`

**迁移示例：**

```java
// ❌ v1.0.0（旧）
ValidaX validator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("邮箱").isEmail(email)
    .field("手机").isChinesePhone(phone);

// ✅ v1.0.1（新）
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("邮箱").isEmail(email)
    .field("手机").isChinesePhone(phone);
```

### 文档优化

- 降低营销化语言，增强专业性和客观性
- 统一更新所有依赖版本号为 1.0.1
- 改进代码示例的可读性和实用性

---

## 升级指南 📋

### 破坏性变更 ⚠️

**类名变更：** `ValidaX` → `ValidX`

这是唯一的破坏性变更，需要修改代码中的类名引用。

### 升级步骤

**1. 更新 Maven 依赖**

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.0.1</version>
</dependency>
```

**2. 全局替换类名**

使用 IDE 的全局查找替换功能：
- 查找：`ValidaX`
- 替换为：`ValidX`

**3. 重新编译和测试**

```bash
mvn clean compile
mvn test
```

### 升级后体验新功能

```java
// 使用新增的 @Contains 注解
public class ArticleDTO {

    @Contains(value = {"技术", "开发", "编程"})
    private String title; // 标题必须包含技术相关关键词

    @Contains(value = {"http://", "https://"}, message = "必须是有效的 URL")
    private String link;

    @Contains(value = {"<script", "javascript:"}, matchAll = false,
              message = "内容不能包含脚本代码")
    private String content;
}
```

---

## 技术统计 📊

- **新增代码：** 673 行
- **新增文件：** 17 个
- **新增测试：** 185+ 个测试用例
- **文档更新：** 50+ 处

---

## 相关链接 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.0.1)
- 📖 [完整文档](../../../README.cn.md)
- 🐛 [问题反馈](https://github.com/vipxieliang/ValidX/issues)
- 💡 [功能建议](https://github.com/vipxieliang/ValidX/issues/new)

---

由 ValidX 团队用 ❤️ 发布
