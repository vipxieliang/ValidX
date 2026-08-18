# ValidX 迁移指南：v1.1.0 → v1.2.0

本文档描述从 v1.1.0 升级到 v1.2.0 时的破坏性变更和迁移步骤。

---

## 概述

版本 1.2.0 对链式 API 引入了两处**破坏性变更**：

1. `isStartsWith()` 和 `isEndsWith()` 方法：参数类型从 `String[]` 改为 `String`，使其更贴合单值验证的语义。
2. `isAlphaNum()` 重命名为 `isAlphaNumber()`、`isMacAddress()` 重命名为 `isMac()`，与对应注解命名 1:1 对齐。

**影响级别**：🟡 **中** - 仅影响使用链式 API 中 `isStartsWith()`、`isEndsWith()`、`isAlphaNum()` 或 `isMacAddress()` 方法的用户；基于注解的验证（`@StartsWith`、`@EndsWith`、`@AlphaNumber`、`@Mac`）完全不受影响。

---

## 破坏性变更

### isStartsWith() / isEndsWith() - 参数从 String[] 改为 String

#### v1.1.0 行为
```java
ValidX validator = ValidX.init();

// 旧 API - 接受 String[] 用于单个前缀/后缀
validator.isStartsWith("http://example.com", new String[]{"http://"});
validator.isEndsWith("photo.jpg", new String[]{".jpg"});
```

#### v1.2.0 行为
```java
ValidX validator = ValidX.init();

// 新 API - 接受 String 用于单个前缀/后缀
validator.isStartsWith("http://example.com", "http://");
validator.isEndsWith("photo.jpg", ".jpg");

// 多个前缀/后缀：使用新的 *Any 方法
validator.isStartsWithAny("http://example.com", new String[]{"http://", "https://"});
validator.isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
```

**变更内容：**
- `isStartsWith(Object value, String prefix)` - 第一个参数为待验证值，第二个参数为单个前缀
- `isEndsWith(Object value, String suffix)` - 第一个参数为待验证值，第二个参数为单个后缀
- 新增 `isStartsWithAny(Object value, String[] prefixes)` / `isEndsWithAny(Object value, String[] suffixes)` 处理多值场景
- 所有方法均新增 `ignoreCase` 重载（如 `isStartsWith(value, prefix, true)`）

---

### isAlphaNum() → isAlphaNumber()、isMacAddress() → isMac() - 链式方法重命名

链式 API 中的 `isAlphaNum()` 和 `isMacAddress()` 方法已重命名，以与对应注解命名 1:1 对齐（`@AlphaNumber` ↔ `isAlphaNumber`、`@Mac` ↔ `isMac`）。

#### v1.1.0 行为
```java
ValidX validator = ValidX.init();

// 旧 API - 方法名与注解名不一致
validator.isAlphaNum("abc123");
validator.isMacAddress("00:1A:2B:3C:4D:5E");
```

#### v1.2.0 行为
```java
ValidX validator = ValidX.init();

// 新 API - 方法名与注解名 1:1 对齐
validator.isAlphaNumber("abc123");
validator.isMac("00:1A:2B:3C:4D:5E");
```

**变更内容：**
- `isAlphaNum(Object value)` 重命名为 `isAlphaNumber(Object value)`，与 `@AlphaNumber` 注解对齐
- `isMacAddress(Object value)` 重命名为 `isMac(Object value)`，与 `@Mac` 注解对齐
- 参数与验证行为完全不变，仅方法名变更

**迁移指南：**

```java
// v1.1.0 代码
validator.isAlphaNum(value);
validator.isMacAddress(value);

// v1.2.0 迁移 - 直接替换方法名，参数与行为不变
validator.isAlphaNumber(value);
validator.isMac(value);
```

---

## 迁移步骤

### 步骤 1：识别受影响的代码

在代码库中搜索链式 API 中的待迁移调用：

```bash
# 搜索链式 API 的调用位置
grep -rn "isStartsWith\|isEndsWith\|isAlphaNum\|isMacAddress" --include="*.java" your-project/
```

查找：
- 使用 `isStartsWith()` 或 `isEndsWith()` 的链式验证调用
- 传入 `new String[]{...}` 数组作为第二个参数的调用（这些是需要迁移的）
- 使用 `isAlphaNum()` 或 `isMacAddress()` 的调用（这些需要方法重命名迁移，见步骤 4）
- 同时可以顺手搜索 `@StartsWith` / `@EndsWith` / `@AlphaNumber` / `@Mac` 注解的使用（无需迁移，但可确认范围）

### 步骤 2：选择迁移策略

根据验证需求，对每个受影响的调用选择以下策略之一：

#### **策略 A：单个前缀/后缀 - 移除数组包装** ⭐ **最常见**

如果只需验证一个前缀或后缀，直接移除数组包装：

**迁移前（v1.1.0）：**
```java
// 单个前缀验证
validator.isStartsWith(url, new String[]{"http://"});

// 单个后缀验证
validator.isEndsWith(file, new String[]{".jpg"});
```

**迁移后（v1.2.0）：**
```java
// 单个前缀验证 - 直接传字符串
validator.isStartsWith(url, "http://");

// 单个后缀验证 - 直接传字符串
validator.isEndsWith(file, ".jpg");
```

---

#### **策略 B：多个前缀/后缀 - 使用新的 *Any 方法**

如果验证多个候选值，改用新增的 `isStartsWithAny()` / `isEndsWithAny()` 方法，**API 签名保持不变**：

**迁移前（v1.1.0）：**
```java
// 多个前缀验证
validator.isStartsWith(url, new String[]{"http://", "https://"});

// 多个后缀验证
validator.isEndsWith(file, new String[]{".jpg", ".jpeg", ".png"});
```

**迁移后（v1.2.0）：**
```java
// 多个前缀验证 - 仅方法名变化
validator.isStartsWithAny(url, new String[]{"http://", "https://"});

// 多个后缀验证 - 仅方法名变化
validator.isEndsWithAny(file, new String[]{".jpg", ".jpeg", ".png"});
```

**优点：**
- ✅ 参数结构不变，改动最小
- ✅ 语义更清晰：`isStartsWith` 单值 / `isStartsWithAny` 多值
- ✅ 与注解行为一致（`@StartsWith` vs `@StartsWithAny`）

---

#### **策略 C：结合 ignoreCase 参数（v1.2.0 新能力）**

v1.2.0 同步新增了大小写不敏感匹配，迁移时可一并升级：

```java
// 大小写不敏感的单值验证
validator.isStartsWith("HTTP://example.com", "http://", true);  // 通过
validator.isEndsWith("file.TXT", ".txt", true);                // 通过

// 大小写不敏感的多值验证
validator.isStartsWithAny("HTTP://example.com", new String[]{"http://", "https://"}, true);
validator.isEndsWithAny("photo.JPG", new String[]{".jpg", ".jpeg"}, true);
```

### 步骤 3：更新测试

更新链式验证相关的测试用例：

**迁移前（v1.1.0）：**
```java
@Test
void testStartsWith() {
    ValidX validator = ValidX.init();
    validator.isStartsWith("http://example.com", new String[]{"http://"});
    assertTrue(validator.passed());
}

@Test
void testEndsWithMultiple() {
    ValidX validator = ValidX.init();
    validator.isEndsWith("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
    assertTrue(validator.passed());
}
```

**迁移后（v1.2.0）：**
```java
@Test
void testStartsWith() {
    ValidX validator = ValidX.init();
    validator.isStartsWith("http://example.com", "http://");  // 移除数组包装
    assertTrue(validator.passed());
}

@Test
void testEndsWithMultiple() {
    ValidX validator = ValidX.init();
    // 多值场景改用 *Any 方法
    validator.isEndsWithAny("photo.jpg", new String[]{".jpg", ".jpeg", ".png"});
    assertTrue(validator.passed());
}

@Test
void testStartsWithIgnoreCase() {
    ValidX validator = ValidX.init();
    validator.isStartsWith("HTTP://example.com", "http://", true);  // 新能力
    assertTrue(validator.passed());
}
```

---

### 步骤 4：链式方法重命名迁移

将 `isAlphaNum()` / `isMacAddress()` 的调用直接替换为新方法名：

```bash
# 全局搜索旧方法名
grep -rn "isAlphaNum\|isMacAddress" --include="*.java" your-project/
```

**迁移前（v1.1.0）：**
```java
validator.isAlphaNum("abc123");
validator.isMacAddress("00:1A:2B:3C:4D:5E");
```

**迁移后（v1.2.0）：**
```java
validator.isAlphaNumber("abc123");
validator.isMac("00:1A:2B:3C:4D:5E");
```

> 💡 纯方法名变更：参数与验证行为完全不变，全局替换即可，无需调整调用逻辑。

---

## 快速参考：API 映射

| 使用场景 | v1.1.0 | v1.2.0 | 说明 |
|----------|--------|--------|------|
| 单个前缀 | `isStartsWith(value, new String[]{p})` | `isStartsWith(value, p)` | ⚠️ **移除数组包装** |
| 单个后缀 | `isEndsWith(value, new String[]{s})` | `isEndsWith(value, s)` | ⚠️ **移除数组包装** |
| 多个前缀 | `isStartsWith(value, new String[]{p1, p2})` | `isStartsWithAny(value, new String[]{p1, p2})` | ⚠️ **改用 *Any 方法** |
| 多个后缀 | `isEndsWith(value, new String[]{s1, s2})` | `isEndsWithAny(value, new String[]{s1, s2})` | ⚠️ **改用 *Any 方法** |
| 大小写不敏感 | 不支持 | `isStartsWith(value, p, true)` / `isStartsWithAny(value, prefixes, true)` | ✅ 新功能 |
| 字母数字 | `isAlphaNum(value)` | `isAlphaNumber(value)` | ⚠️ **方法重命名** |
| MAC 地址 | `isMacAddress(value)` | `isMac(value)` | ⚠️ **方法重命名** |
| 注解（单值） | `@StartsWith` / `@EndsWith` | `@StartsWith` / `@EndsWith` | ✅ 无需更改 |
| 注解（多值） | 不支持 | `@StartsWithAny` / `@EndsWithAny` | ✅ 新功能 |
| 注解（字母数字） | `@AlphaNumber` | `@AlphaNumber` | ✅ 无需更改 |
| 注解（MAC） | `@Mac` | `@Mac` | ✅ 无需更改 |

---

## 示例：完整迁移

### 迁移前（v1.1.0）

```java
@Service
public class UrlService {
    private final ValidX validator = ValidX.init();

    public void validateLink(String url, String fileName) {
        validator
            // 单个前缀（数组包装）
            .isStartsWith(url, new String[]{"http://"})
            // 多个后缀
            .isEndsWith(fileName, new String[]{".jpg", ".jpeg", ".png"});

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

### 迁移后（v1.2.0）

```java
@Service
public class UrlService {
    private final ValidX validator = ValidX.init();

    public void validateLink(String url, String fileName) {
        validator
            // 单个前缀：直接传字符串，并支持忽略大小写
            .isStartsWith(url, "http://", true)
            // 多个后缀：改用 *Any 方法，并支持忽略大小写
            .isEndsWithAny(fileName, new String[]{".jpg", ".jpeg", ".png"}, true);

        if (!validator.passed()) {
            throw new ValidationException(validator.getErrors());
        }
    }
}
```

---

## 常见问题

### Q1：为什么要引入这个破坏性变更？

**A：** 为了更清晰的语义和更好的类型安全：
- `isStartsWith()` / `isEndsWith()` → 单值验证，直接接受 `String`
- `isStartsWithAny()` / `isEndsWithAny()` → 多值验证，接受 `String[]`

单值和多值方法分离，消除了二者之间的混淆，也与注解行为（`@StartsWith` vs `@StartsWithAny`）保持一致。

### Q2：我的注解代码（@StartsWith、@EndsWith）需要修改吗？

**A：** ❌ 不需要。本次变更**仅影响链式 API**（`isStartsWith()` / `isEndsWith()` 方法），基于注解的验证完全保持不变。

### Q3：多个前缀/后缀的场景必须改用 *Any 方法吗？

**A：** ✅ 是的。v1.2.0 中 `isStartsWith()` / `isEndsWith()` 只接受单个 `String`，多值验证必须使用 `isStartsWithAny()` / `isEndsWithAny()`。好消息是 `*Any` 方法的参数结构与旧 API 完全相同，只需修改方法名。

### Q4：如果我不迁移会怎样？

- ❌ 代码**无法编译**（`String` 无法匹配 `String[]` 参数）
- ❌ 应用构建失败

### Q5：升级后能否同时使用 ignoreCase？

**A：** ✅ 可以。所有四个方法（`isStartsWith` / `isEndsWith` / `isStartsWithAny` / `isEndsWithAny`）均提供 `ignoreCase` 重载，默认 `false`（区分大小写），原有行为不变。

### Q6：有过渡期吗？

**没有。** 这是 v1.2.0 中的即时破坏性变更。我们建议：
1. 升级前搜索并统计 `isStartsWith` / `isEndsWith` / `isAlphaNum` / `isMacAddress` 的调用数量
2. 按"单值移除数组包装、多值改用 *Any、方法名对齐"的规则批量替换
3. 升级后运行完整测试套件验证无回归

### Q7：isAlphaNum() 和 isMacAddress() 需要迁移吗？

**A：** ✅ 需要。这两个方法在 v1.2.0 中已重命名：`isAlphaNum()` → `isAlphaNumber()`、`isMacAddress()` → `isMac()`。这是纯方法名变更（参数与行为不变），直接全局替换即可。对应注解（`@AlphaNumber`、`@Mac`）无需更改。

---

## 需要帮助？

如果在迁移过程中遇到问题：

1. **查看文档**：参考 README.md 中更新的链式 API 文档
2. **查看变更日志**：参考 [CHANGELOG_CN.md](./CHANGELOG_CN.md) 中的破坏性变更说明
3. **查看示例**：参考测试代码 `StartsWithAnyValidationChainTest` 和 `EndsWithAnyValidationChainTest`
4. **联系支持**：发送邮件至 vipxieliang@126.com，包含：
   - 你的当前版本
   - 显示问题的代码片段
   - 编译错误消息（如果有）

---

## 总结检查清单

在将 v1.2.0 部署到生产环境之前：

- [ ] 在代码库中搜索 `isStartsWith`、`isEndsWith`、`isAlphaNum`、`isMacAddress` 的所有调用
- [ ] 识别所有传入 `new String[]{...}` 的调用
- [ ] 单个前缀/后缀：移除数组包装（`new String[]{"x"}` → `"x"`）
- [ ] 多个前缀/后缀：改用 `isStartsWithAny()` / `isEndsWithAny()`
- [ ] 方法重命名：`isAlphaNum()` → `isAlphaNumber()`、`isMacAddress()` → `isMac()`
- [ ] 如需大小写不敏感：追加 `ignoreCase` 参数
- [ ] 更新所有链式验证相关的测试用例
- [ ] 运行完整测试套件以验证无回归
- [ ] 在预发环境中测试验证行为
- [ ] 确认注解验证（`@StartsWith` / `@EndsWith` / `@AlphaNumber` / `@Mac`）不受影响

---

**最后更新：** 2026-08-17
**适用于：** ValidX v1.2.0+
