# 迁移指南：v1.0.0/v1.0.1 → v1.1.0

本文档描述从 v1.0.0 或 v1.0.1 升级到 v1.1.0 时的破坏性变更和迁移步骤。

---

## 概述

版本 1.1.0 对 `@FutureDate` 和 `@PastDate` 注解的日期时间格式支持引入了**破坏性变更**。

**影响级别**：🔴 **高** - 使用日期时间字符串（如 `"2025-12-31 12:00:00"`）与 `@FutureDate` 或 `@PastDate` 的应用会受到影响。

---

## 破坏性变更

### 1. @FutureDate - 不再支持时间组件

#### v1.0.0/v1.0.1 行为
```java
@FutureDate
private String date;

// ✅ 两种格式都自动支持：
date = "2025-12-31";           // 解析为 LocalDate
date = "2025-12-31 12:00:00";  // 解析为 LocalDateTime，然后转换为 LocalDate
```

**工作原理：**
1. 首先尝试解析为 `yyyy-MM-dd` 格式
2. 如果失败，再尝试解析为 `yyyy-MM-dd HH:mm:ss` 格式
3. 自动支持纯日期和日期时间字符串

#### v1.1.0 行为
```java
@FutureDate
private String date;

// ✅ 纯日期格式仍然有效：
date = "2025-12-31";

// ❌ 日期时间格式不再有效：
date = "2025-12-31 12:00:00";  // 验证失败
```

**变更内容：**
- 仅支持纯日期格式（无时间组件）
- 默认 pattern：`yyyy-MM-dd`
- 可通过 `pattern` 参数指定自定义格式，但**不能包含时间符号**（HH、mm、ss 等）
- 如果 `pattern` 包含时间符号，初始化时会抛出 `IllegalArgumentException`

---

### 2. @PastDate - 不再支持时间组件

#### v1.0.0/v1.0.1 行为
```java
@PastDate
private String date;

// ✅ 两种格式都自动支持：
date = "2020-01-01";           // 解析为 LocalDate
date = "2020-01-01 12:00:00";  // 解析为 LocalDateTime，然后转换为 LocalDate
```

#### v1.1.0 行为
```java
@PastDate
private String date;

// ✅ 纯日期格式仍然有效：
date = "2020-01-01";

// ❌ 日期时间格式不再有效：
date = "2020-01-01 12:00:00";  // 验证失败
```

**变更内容：**
- 与 `@FutureDate` 相同 - 仅支持纯日期格式
- pattern 参数不能包含时间符号

---

## 迁移步骤

### 步骤 1：识别受影响的代码

在代码库中搜索使用日期时间字符串的 `@FutureDate` 和 `@PastDate`：

```bash
# 搜索潜在的日期时间使用模式
grep -r "FutureDate\|PastDate" --include="*.java" your-project/
```

查找：
- 使用 `@FutureDate` 或 `@PastDate` 注解的 String 字段
- 包含时间组件的值（如 `"2025-12-31 12:00:00"`）
- 使用 `isFutureDate()` 或 `isPastDate()` 的链式验证

### 步骤 2：选择迁移策略

对于每个受影响的使用场景，选择以下策略之一：

#### **策略 A：切换到 @FutureDateTime / @PastDateTime** ⭐ **推荐**

使用 v1.1.0 新增的专用日期时间注解：

**迁移前（v1.0.0/v1.0.1）：**
```java
public class EventDTO {
    @FutureDate
    private String eventTime;  // "2025-12-31 12:00:00"
}
```

**迁移后（v1.1.0）：**
```java
public class EventDTO {
    @FutureDateTime  // ← 使用新注解
    private String eventTime;  // "2025-12-31 12:00:00"
}
```

**优点：**
- ✅ 专为日期时间验证设计的注解
- ✅ 默认 pattern 为 `yyyy-MM-dd HH:mm:ss`
- ✅ 语义更清晰
- ✅ 常见场景无需配置

---

#### **策略 B：移除时间组件**

如果只需要日期部分，在验证前去掉时间：

**迁移前（v1.0.0/v1.0.1）：**
```java
@FutureDate
private String eventDate;  // "2025-12-31 12:00:00"
```

**迁移后（v1.1.0）：**
```java
@FutureDate
private String eventDate;  // "2025-12-31"（已移除时间）

// 或在代码中：
String dateTime = "2025-12-31 12:00:00";
String dateOnly = dateTime.substring(0, 10);  // 提取 "2025-12-31"
```

**优点：**
- ✅ 无需更改注解
- ✅ 明确只验证日期部分

**缺点：**
- ⚠️ 丢失时间信息
- ⚠️ 需要数据转换

---

#### **策略 C：继续使用 @FutureDate（不推荐）**

如果必须使用 `@FutureDate` 处理纯日期字符串和自定义格式：

**示例：**
```java
// v1.1.0 - 仅用于纯日期格式
@FutureDate(pattern = "MM/dd/yyyy")
private String usDate;  // "12/31/2025" - 可以

@FutureDate(pattern = "yyyy-MM-dd HH:mm:ss")  // ❌ 会抛出 IllegalArgumentException！
private String dateTime;  // 这不会工作！
```

**重要：**
- ⚠️ 此策略不能用于日期时间字符串
- ⚠️ pattern 参数不支持时间符号

---

### 步骤 3：更新链式验证调用

如果使用链式验证 API：

**迁移前（v1.0.0/v1.0.1）：**
```java
ValidX validator = ValidX.init();
validator.isFutureDate("2025-12-31 12:00:00");  // v1.0.0 中有效
```

**迁移后（v1.1.0）：**
```java
ValidX validator = ValidX.init();

// 选项 A：使用新的日期时间方法
validator.isFutureDateTime("2025-12-31 12:00:00");  // ✅ 推荐

// 选项 B：移除时间组件
validator.isFutureDate("2025-12-31");  // ✅ 有效
```

---

### 步骤 4：更新测试

更新测试用例以反映新行为：

**迁移前（v1.0.0/v1.0.1）：**
```java
@Test
void testFutureDate() {
    UserDTO dto = new UserDTO();
    dto.setEventDate("2025-12-31 12:00:00");  // 以前有效

    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty());  // 通过
}
```

**迁移后（v1.1.0）：**
```java
@Test
void testFutureDateTime() {
    UserDTO dto = new UserDTO();
    dto.setEventTime("2025-12-31 12:00:00");

    // 将 UserDTO 中的注解更新为 @FutureDateTime
    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty());  // 仍然通过
}

@Test
void testFutureDateWithTimeFormat_ShouldFail() {
    UserDTO dto = new UserDTO();
    dto.setEventDate("2025-12-31 12:00:00");  // 日期时间字符串

    // 如果仍使用 @FutureDate，现在会失败
    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty());  // v1.1.0 中失败
}
```

---

## 快速参考：注解映射

| 使用场景 | v1.0.0/v1.0.1 | v1.1.0 | 说明 |
|----------|---------------|--------|------|
| 纯日期，未来 | `@FutureDate` | `@FutureDate` | ✅ 无需更改 |
| 纯日期，过去 | `@PastDate` | `@PastDate` | ✅ 无需更改 |
| 日期时间，未来 | `@FutureDate` | `@FutureDateTime` | ⚠️ **需要更改** |
| 日期时间，过去 | `@PastDate` | `@PastDateTime` | ⚠️ **需要更改** |
| 自定义日期格式 | 不支持（自动检测） | `@FutureDate(pattern="...")` | ✅ 新功能 |
| 自定义日期时间格式 | 不支持 | `@FutureDateTime(pattern="...")` | ✅ 新功能 |

---

## 示例：完整迁移

### 迁移前（v1.0.0/v1.0.1）

```java
public class EventDTO {
    @NotNull
    @FutureDate
    private String eventDate;  // 接受 "2025-12-31" 或 "2025-12-31 12:00:00"

    @NotNull
    @PastDate
    private String registrationDate;  // 接受 "2020-01-01" 或 "2020-01-01 09:30:00"
}

@Service
public class EventService {
    public void validateEvent(Map<String, Object> data) {
        ValidX validator = ValidX.init();
        validator.isFutureDate(data.get("startTime"))  // 对时间字符串有效
                 .isPastDate(data.get("createdAt"));    // 对时间字符串有效

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

### 迁移后（v1.1.0）

```java
public class EventDTO {
    // 变更：现在使用 @FutureDateTime 进行时间感知验证
    @NotNull
    @FutureDateTime  // ← 从 @FutureDate 更改
    private String eventDate;  // "2025-12-31 12:00:00"

    // 变更：现在使用 @PastDateTime 进行时间感知验证
    @NotNull
    @PastDateTime  // ← 从 @PastDate 更改
    private String registrationDate;  // "2020-01-01 09:30:00"
}

@Service
public class EventService {
    public void validateEvent(Map<String, Object> data) {
        ValidX validator = ValidX.init();
        // 变更：使用新的日期时间方法
        validator.isFutureDateTime(data.get("startTime"))  // ← 已更改
                 .isPastDateTime(data.get("createdAt"));    // ← 已更改

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

---

## 常见问题

### Q1：为什么要引入这个破坏性变更？

**A：** 为了提供更清晰的语义和更好的验证：
- `@FutureDate` / `@PastDate` → 纯日期验证（无时间）
- `@FutureDateTime` / `@PastDateTime` → 日期时间验证（有时间）

这种分离使意图明确，避免歧义。

### Q2：v1.1.0 会验证我现有的纯日期字符串吗？

**A：** ✅ 会！如果你使用的是纯日期字符串（如 `"2025-12-31"`），无需更改。

### Q3：我可以对日期时间使用自定义格式吗？

**A：** 可以，但要使用正确的注解：
```java
// ✅ 对于自定义格式的日期时间：
@FutureDateTime(pattern = "MM/dd/yyyy HH:mm:ss")
private String usDateTime;

// ❌ 这会抛出异常：
@FutureDate(pattern = "MM/dd/yyyy HH:mm:ss")  // IllegalArgumentException！
```

### Q4：如果我不迁移会怎样？

如果你的代码将日期时间字符串传递给 `@FutureDate` 或 `@PastDate`：
- ❌ 验证会**失败**（字符串不匹配纯日期格式）
- ❌ 你的应用可能会拒绝有效数据
- ❌ 测试可能开始失败

### Q5：有过渡期吗？

**没有。** 这是 v1.1.0 中的即时破坏性变更。我们建议：
1. 升级前检查你的代码库
2. 升级后运行全面测试
3. 使用上述迁移策略

---

## 需要帮助？

如果在迁移过程中遇到问题：

1. **查看文档**：参考 README.md 中更新的注解文档
2. **查看示例**：参考本指南中的使用示例
3. **联系支持**：发送邮件至 vipxieliang@126.com，包含：
   - 你的当前版本
   - 显示问题的代码片段
   - 错误消息（如果有）

---

## 总结检查清单

在将 v1.1.0 部署到生产环境之前：

- [ ] 在代码库中搜索 `@FutureDate` 和 `@PastDate` 的使用
- [ ] 识别所有使用日期时间字符串的字段/验证
- [ ] 在需要的地方将注解更新为 `@FutureDateTime` / `@PastDateTime`
- [ ] 更新链式验证调用（`.isFutureDate()` → `.isFutureDateTime()`）
- [ ] 更新所有测试用例
- [ ] 运行完整测试套件以验证无回归
- [ ] 在预发环境中测试验证行为
- [ ] 如果暴露这些字段，更新 API 文档

---

**最后更新：** 2026-08-06
**适用于：** ValidX v1.1.0+
