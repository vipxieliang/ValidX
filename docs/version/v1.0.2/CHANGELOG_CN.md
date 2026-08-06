# ValidX v1.0.2 更新日志

**发布日期：** 2026年8月4日

本文档记录从 v1.0.1 到 v1.0.2 的变更内容。

## 变更概览

- ✨ 新增 @ChineseName 验证注解
- 🔧 增强日期/日期时间验证器，支持自定义格式
- 📖 新增完整的国际化测试
- 🎯 改进日期验证错误提示

---

## 新增功能 ✨

### @ChineseName 验证注解

新增中国人姓名验证器，符合中国姓名规范。

**功能特性：**
- 仅允许中文字符
- 长度在 2-50 个字符之间（覆盖所有中文姓名，包括极长的少数民族姓名）
- 支持少数民族姓名中的间隔号 "·"
- 不允许数字、字母或特殊字符
- 完整的国际化支持（9 种语言）

**注解方式示例：**

```java
// 示例 1：基本使用
public class UserDTO {
    @ChineseName
    private String realName;
}

// 示例 2：必填字段
public class RegistrationDTO {
    @NotBlank(message = "姓名不能为空")
    @ChineseName
    private String name;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 汉族姓名
validator.field("姓名").isChineseName("张三");
validator.field("姓名").isChineseName("欧阳修");
validator.field("姓名").isChineseName("诸葛亮");

// 少数民族姓名（带间隔号）
validator.field("姓名").isChineseName("买买提·吐尔逊");
validator.field("姓名").isChineseName("迪丽热巴·迪力木拉提");

// 历史人物名
validator.field("姓名").isChineseName("爱新觉罗·玄烨");

// 检查验证结果
if (!validator.passed()) {
    System.out.println(validator.getErrors());
}
```

**实际应用场景：**

```java
// 场景 1：用户注册
public class UserRegistrationDTO {
    @NotBlank(message = "真实姓名不能为空")
    @ChineseName
    private String realName;

    @ChineseIdCard
    private String idCard;
}

// 场景 2：身份验证
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("真实姓名").isChineseName(realName)
    .field("身份证号").isChineseIdCard(idCard);

// 场景 3：表单验证
@RestController
public class UserController {
    @PostMapping("/verify")
    public Result verify(@Valid @RequestBody UserVerifyDTO dto) {
        return verifyService.verify(dto);
    }
}
```

---

## 功能增强 🔧

### 日期/日期时间验证器 - 自定义格式支持

增强了 `@FutureDate`、`@PastDate`、`@FutureDateTime` 和 `@PastDateTime` 验证器，支持自定义日期格式。

**新增特性：**

1. **自定义格式参数**
   - 用户现在可以指定自定义的日期/日期时间格式
   - 默认格式保持不变，保证向后兼容
   - 严格验证模式确保日期有效性（如：拒绝 2024-02-30）

2. **新增注解：@PastDateTime 和 @FutureDateTime**
   - 专门用于日期时间验证（必须包含时间部分）
   - 与纯日期验证器分离，提供更好的类型安全
   - 格式验证确保包含时间组件

3. **三级 API 设计**
   - 无参数：使用默认值（includeToday=false，默认格式）
   - 单参数：自定义 includeToday，使用默认格式
   - 完整参数：自定义 includeToday 和自定义格式

**注解方式示例：**

```java
public class EventDTO {
    // 示例 1：默认格式（yyyy-MM-dd）
    @FutureDate
    private String eventDate;

    // 示例 2：自定义格式
    @FutureDate(pattern = "MM/dd/yyyy")
    private String usDate;

    // 示例 3：包含今天
    @FutureDate(includeToday = true, pattern = "yyyy-MM-dd")
    private String deadline;

    // 示例 4：过去日期时间，自定义格式
    @PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String createdAt;
}
```

**链式 API 方式示例：**

```java
ValidX validator = ValidX.init();

// 级别 1：无参数（默认：includeToday=false，pattern="yyyy-MM-dd"）
validator.isFutureDate("2025-12-31");
validator.isPastDate("2020-01-01");

// 级别 2：自定义 includeToday（pattern="yyyy-MM-dd"）
validator.isFutureDate("2025-12-31", true);
validator.isPastDate("2020-01-01", false);

// 级别 3：完全自定义
validator.isFutureDate("12/31/2025", false, "MM/dd/yyyy");
validator.isPastDate("01/01/2020", false, "MM/dd/yyyy");

// 日期时间验证器（默认 pattern="yyyy-MM-dd HH:mm:ss"）
validator.isFutureDateTime("2025-12-31 23:59:59");
validator.isPastDateTime("2020-01-01 12:30:45");

// 日期时间自定义格式
validator.isFutureDateTime("12/31/2025 23:59:59", false, "MM/dd/yyyy HH:mm:ss");
validator.isPastDateTime("01/01/2020 12:30:45", false, "MM/dd/yyyy HH:mm:ss");
```

**支持的日期格式符号：**

| 符号 | 含义 | 示例 |
|------|------|------|
| `yyyy` | 年份（4位） | `2024` |
| `MM` | 月份（补零） | `01`, `12` |
| `dd` | 日期（补零） | `05`, `25` |
| `HH` | 小时（24小时制，补零） | `00`, `23` |
| `mm` | 分钟（补零） | `00`, `59` |
| `ss` | 秒（补零） | `00`, `59` |

**常用格式示例：**
- 美国格式：`MM/dd/yyyy`
- 欧洲格式：`dd/MM/yyyy`
- ISO 8601：`yyyy-MM-dd'T'HH:mm:ss`
- 中文格式：`yyyy年MM月dd日`
- 紧凑格式：`yyyyMMdd`

### 改进错误提示

增强了日期验证错误信息，提供更好的国际化支持。

**改进内容：**

1. **格式验证错误**
   - 当日期 pattern 包含时间组件时，提供清晰的错误信息（针对纯日期验证器）
   - 当日期时间 pattern 缺少时间组件时，提供清晰的错误信息（针对日期时间验证器）

2. **9 种语言支持**
   - 所有错误信息支持 9 种语言
   - 属性文件采用正确的 Unicode 编码
   - 所有语言的消息格式保持一致

**错误信息示例：**

```java
// 简体中文
"日期验证的 pattern 不能包含时间部分（H、m、s、a）。如果需要验证日期时间，请使用 PastDateTime 或 FutureDateTime"

// 英文
"The date validation pattern cannot contain time components (H, m, s, a). Please use PastDateTime or FutureDateTime for datetime validation"

// 日语
"日付検証のパターンには時間部分（H、m、s、a）を含めることはできません。日時を検証する場合は、PastDateTime または FutureDateTime を使用してください"
```

---

## 文档更新 📖

### README 文档增强

更新了中英文 README 文件：

1. **@ChineseName 文档**
   - 验证规则
   - 示例格式
   - 注解和链式 API 的使用示例
   - 实际应用场景

2. **增强日期/日期时间验证文档**
   - 所有日期验证器的参数说明
   - 三级 API 使用示例
   - 支持的格式符号表
   - 常用格式示例

3. **版本追踪**
   - 为新功能添加版本 1.0.2 标签
   - 更新快速参考表

### 完整的测试覆盖

为新功能添加了完整的测试套件：

**新增测试文件：**
- `ChineseNameValidatorTest.java` - 注解方式验证测试
- `ChineseNameValidationChainTest.java` - 链式 API 验证测试
- `DateValidatorI18nTest.java` - 国际化测试（9 种语言）
- `PastDateValidationChainTest.java` - 过去日期链式 API 测试
- `FutureDateValidationChainTest.java` - 未来日期链式 API 测试
- `PastDateTimeValidationChainTest.java` - 过去日期时间链式 API 测试
- `FutureDateTimeValidationChainTest.java` - 未来日期时间链式 API 测试
- `DateTimeChainPatternTest.java` - 自定义格式验证测试

**测试统计：**
- 新增测试：50+ 个测试用例
- 总测试覆盖：1554 个测试
- 所有测试通过 ✅

---

## 兼容性 🔄

### 向后兼容 ✅

**与 v1.0.1 版本 100% 向后兼容**

所有现有代码无需任何修改即可继续工作：

```java
// ✅ 所有 v1.0.1 代码在 v1.0.2 中正常工作
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .field("邮箱").isEmail(email)
    .field("手机").isChinesePhone(phone);

// ✅ 现有日期验证器使用默认格式
@FutureDate  // 仍然默认使用 "yyyy-MM-dd"
private String date;

@PastDate    // 仍然默认使用 "yyyy-MM-dd"
private String birthDate;
```

### 新增可选参数

所有日期验证器的 `pattern` 参数都是**可选的**：

```java
// ✅ 与之前完全一样（使用默认格式）
@FutureDate
private String date;

// ✅ 新功能（自定义格式）
@FutureDate(pattern = "MM/dd/yyyy")
private String usDate;
```

### 链式 API - 方法重载

链式 API 通过方法重载保持完全向后兼容：

```java
// ✅ v1.0.1 代码 - 仍然有效
validator.isFutureDate(date, false);

// ✅ v1.0.2 增强 - 新功能
validator.isFutureDate(date, false, "MM/dd/yyyy");
```

### 无破坏性变更 ⚠️

本版本包含**零破坏性变更**：
- ✅ 无 API 移除
- ✅ 无参数类型变更
- ✅ 现有功能行为无变化
- ✅ 所有默认值保持不变
- ✅ 所有错误信息不变（仅新增）

---

## 升级指南 📋

### 无需迁移 ✅

如果你正在使用 v1.0.1，可以直接升级到 v1.0.2，无需任何代码修改。

### 升级步骤

**1. 更新 Maven 依赖**

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.0.2</version>
</dependency>
```

**2. 重新编译项目**

```bash
mvn clean install
```

**3. 体验新功能（可选）**

```java
// 新功能 1：中国人姓名验证
public class UserDTO {
    @ChineseName
    private String realName;
}

// 新功能 2：自定义日期格式
@FutureDate(pattern = "MM/dd/yyyy")
private String usDate;

// 新功能 3：日期时间验证器
@PastDateTime(pattern = "yyyy-MM-dd'T'HH:mm:ss")
private String createdAt;

// 新功能 4：三级链式 API
ValidX.init()
    .isFutureDate("12/31/2025", false, "MM/dd/yyyy")
    .isPastDateTime("2020-01-01T12:30:45", false, "yyyy-MM-dd'T'HH:mm:ss");
```

---

## 技术统计 📊

- **新增代码：** ~800 行
- **新增文件：** 15 个
- **新增测试：** 50+ 个测试用例
- **总测试数：** 1554 个测试（全部通过 ✅）
- **文档更新：** 100+ 处
- **新增验证器：** 5 个（ChineseName + 4 个日期时间链式方法）
- **增强验证器：** 4 个（FutureDate、PastDate、FutureDateTime、PastDateTime）

---

## 代码质量改进 🎨

### 委托模式

在验证器中应用委托模式以消除代码重复：

```java
// 之前：两个方法中有重复逻辑
@Override
public void initialize(PastDate annotation) {
    this.includeToday = annotation.includeToday();
    this.pattern = annotation.pattern();
    // ... 验证逻辑
}

// 之后：委托消除重复
@Override
public void initialize(PastDate annotation) {
    initialize(annotation.includeToday(), annotation.pattern());
}

public void initialize(boolean includeToday, String pattern) {
    this.includeToday = includeToday;
    this.pattern = pattern;
    // ... 验证逻辑（单一真实来源）
}
```

### 清晰架构

简化了 BaseValidation.java，移除了重载方法，在 API 层（ValidX.java）处理默认值。

---

## 相关链接 🔗

- 📦 [Maven Central](https://central.sonatype.com/artifact/io.github.vipxieliang/validx/1.0.2)
- 📖 [完整文档](../../../README.cn.md)
- 🐛 [问题反馈](https://github.com/vipxieliang/ValidX/issues)
- 💡 [功能建议](https://github.com/vipxieliang/ValidX/issues/new)
- 📝 [v1.0.1 更新日志](../v1.0.1/CHANGELOG_CN.md)

---

由 ValidX 团队用 ❤️ 发布
