# ValidX vs Google Guava Preconditions：验证 vs 断言

## 📋 目录
- [概述](#概述)
- [快速对比表](#快速对比表)
- [一、核心哲学：断言 vs 验证](#一核心哲学断言-vs-验证)
- [二、API 形态对比](#二api-形态对比)
- [三、失败方式：异常中断 vs 错误收集](#三失败方式异常中断-vs-错误收集)
- [四、空值语义对比](#四空值语义对比)
- [五、消息与国际化](#五消息与国际化)
- [六、性能与依赖](#六性能与依赖)
- [七、实战对照：同一个注册接口](#七实战对照同一个注册接口)
- [八、协作：什么时候用谁](#八协作什么时候用谁)
- [使用场景建议](#使用场景建议)
- [总结](#总结)

---

## 概述

在 Java 世界里，"先检查、再执行"几乎是每个方法的第一行。这件事有两个主流工具：

- **Google Guava Preconditions**：`com.google.common.base.Preconditions`，提供 `checkArgument`、`checkNotNull`、`checkState` 等静态方法，条件不满足**立刻抛异常**；
- **ValidX**：一个面向中国业务场景的开源验证库，既提供 `@NotBlank` 这类注解，也提供 `ValidX.init()` 链式 API，条件不满足**收集错误**，由业务决定如何处理。

两者都做"前置检查"，但哲学截然不同——这正是本文标题"**验证 vs 断言**"要讲清楚的事：**Guava 是断言，ValidX 是验证**。断言管的是"我的代码假设错了"（开发者违约），验证管的是"用户给的数据不合法"（输入不合格）。搞混它们，代码会变得要么过度防御、要么对用户输入毫无招架。

---

## 快速对比表

| 维度 | Guava Preconditions | ValidX 链式 API |
|------|---------------------|-----------------|
| **定位** | 编程契约 / 前置条件断言 | 业务数据 / 用户输入验证 |
| **失败方式** | 抛异常（fail-fast） | 收集错误，返回 `List<String>` |
| **异常类型** | `IllegalArgumentException` / `NPE` / `IllegalStateException` / `IndexOutOfBoundsException` | 不抛（可由调用方自行 `throw ValidationException`） |
| **消息受众** | 开发者（调试定位） | 最终用户（可直接展示） |
| **消息模板** | `%s` 占位符 | 8 种语言 + 字段标签 |
| **空值语义** | `checkNotNull`：null 即失败 | 默认放行，`notNull()`/`notEmpty()` 按需收紧 |
| **批量校验** | 一次只能断一个条件 | 一条链校验多个字段，错误累积 |
| **可读性** | 连续 `if` + 多行 `checkArgument` | 声明式链式调用 |
| **适用层** | 库内部 / 方法边界防御 | Controller / Service 入口、批量导入 |
| **依赖** | 整个 Guava（含大量无关类） | ValidX 单一依赖 |

---

## 一、核心哲学：断言 vs 验证

### 1.1 断言（Guava）：假设错了，程序就别跑了

`Preconditions.checkArgument` 的隐含语义是：**这个条件必须为真，如果为假，说明调用方违反了约定，即程序有 bug**。所以它选择 fail-fast——立刻抛异常、立刻停止，把问题暴露在最靠近源头的地方。

```java
// 库的内部实现：假设调用方传了合法参数
public BigDecimal calculateInterest(BigDecimal principal, double rate) {
    checkArgument(principal != null && principal.signum() >= 0, "本金不能为负: %s", principal);
    checkArgument(rate >= 0 && rate <= 1, "利率必须在 [0,1] 区间: %s", rate);
    // ... 从这里开始，假设前提都成立
}
```

"断言失败" ≈ "我的代码有 bug"——这是**开发者之间**的契约。

### 1.2 验证（ValidX）：数据不对，提示用户改

ValidX 的隐含语义是：**这个输入来自外部（用户、上游系统、文件导入），它不合法是常态，需要被当成业务事件处理**——要么返回错误提示让用户重填，要么记录日志、跳过这一行继续处理。

```java
// 业务入口：验证用户提交的数据
ValidX v = ValidX.init()
    .field("手机号").notEmpty().isChinesePhone(phone)
    .field("邮箱").notEmpty().isEmail(email)
    .field("QQ（可选）").allowNull().isQQ(qq);

if (!v.passed()) {
    // 错误累积成列表，交给 Controller 返回给前端
    throw new ValidationException(v.getErrors());
}
```

"验证失败" ≈ "用户输入不合格"——这是**应用与用户之间**的契约。

### 1.3 一张图看懂区别

| | 断言（Guava） | 验证（ValidX） |
|---|---|---|
| 问的问题 | "我的代码假设错了吗？" | "用户给的数据合法吗？" |
| 失败的含义 | 程序 bug | 业务事件 |
| 应该怎么处理 | 崩溃、尽早暴露 | 提示、重试、降级、跳过 |
| 消息写给谁 | 开发者 | 最终用户 |
| 典型场景 | 库参数防御、状态机校验 | 表单提交、API 入参、Excel 导入 |

> 一句话记忆：**断言失败是你的问题，验证失败是用户的问题。**

---

## 二、API 形态对比

### 2.1 Guava：四个静态方法打天下

```java
import static com.google.common.base.Preconditions.*;

// 条件必须为真，否则 IllegalArgumentException
checkArgument(user.getAge() >= 18, "年龄必须大于等于 18：%s", user.getAge());

// 引用不能为 null，否则 NullPointerException（返回原对象便于链式）
User u = checkNotNull(user, "user 不能为 null");

// 状态必须为真，否则 IllegalStateException
checkState(connection.isOpen(), "连接未打开");

// 下标必须在 [0, size) 内，否则 IndexOutOfBoundsException
checkElementIndex(index, list.size(), "index");
```

### 2.2 ValidX：一条链搞定多个字段

```java
import io.github.vipxieliang.validx.chain.ValidX;

ValidX v = ValidX.init()
    .field("手机号").notEmpty().isChinesePhone(phone)
    .field("身份证").notEmpty().isChineseIdCard(idCard)
    .field("金额").notNull().isIn(amount, new String[]{"USD", "CNY"})
    .field("QQ（可选）").allowNull().isQQ(qq);

if (!v.passed()) {
    System.out.println(v.getErrorMessage());  // "手机号: ..., 身份证: ..."
}
```

**核心差异**：Guava 是"一次一个断言"，N 个条件要 N 行 + N 个异常分支；ValidX 是"一条链声明全部规则"，错误自然累积，无需逐个 catch。

---

## 三、失败方式：异常中断 vs 错误收集

这是两者最直观的差异，直接影响代码控制流。

### 3.1 Guava：抛异常 = 中断执行

```java
public void transfer(Account from, Account to, BigDecimal amount) {
    checkArgument(amount != null && amount.signum() > 0, "转账金额必须为正: %s", amount);
    checkState(from.getBalance().compareTo(amount) >= 0, "余额不足");

    // 走到这里，所有前提都成立，不需要再判断
    from.debit(amount);
    to.credit(amount);
}
```

好处：后续代码**无需任何 if**，因为前提已在入口断言；坏处：第一个失败即终止，**拿不到全部问题**。

### 3.2 ValidX：收集错误 = 一次告诉你所有问题

```java
ValidX v = ValidX.init()
    .field("用户名").notEmpty().isAlphaNumber(username)
    .field("邮箱").notEmpty().isEmail(email)
    .field("密码").notEmpty().isPassword(password, 8);

if (!v.passed()) {
    // 三个字段的问题一次全拿到
    List<String> errors = v.getErrors();
    // ["用户名: 值不能为空", "邮箱: 邮箱格式不正确", "密码: 长度必须在8到20之间"]
}
```

好处：**一次交互反馈全部问题**（对用户体验尤其重要——没人想改完邮箱又被告知密码也错了）；坏处：需要显式判断 `passed()`。

### 3.3 什么时候异常反而更好？

当"校验失败 = 程序 bug"时，异常（断言）就是正确的选择。一个典型反例是把用户输入校验写成 `checkArgument`——一旦抛错，Spring 默认返回 500 而不是 400，前端拿到的是"服务器内部错误"而不是"邮箱格式不对"。**错误的数据流向错误的处理通道，是断言/验证混用的最大坑。**

---

## 四、空值语义对比

| 场景 | Guava | ValidX |
|------|-------|--------|
| 不允许 null | `checkNotNull(x, "msg")` | `.notNull().isXxx(x)` 或全局 `GLOBAL_NOT_NULL` |
| 不允许 null 和空串 | 需自己再写 `checkArgument(!x.isEmpty())` | `.notEmpty().isXxx(x)` |
| 允许 null（可选字段） | `if (x != null) checkArgument(...)` | `.allowNull().isXxx(x)`（默认行为） |

```java
// Guava：可选字段要手动 if 包裹
if (qq != null) {
    checkArgument(qq.matches("^[1-9]\\d{4,10}$"), "QQ 格式不正确");
}

// ValidX：allowNull() 声明式表达"可选"
ValidX.init().field("QQ").allowNull().isQQ(qq);
```

> 关键认知：ValidX 链式校验**默认允许 null/空**（因为 Map/JSON 中字段缺失返回 null 是常态），必须用 `notNull()`/`notEmpty()` 显式收紧；Guava 则是"默认严格"，`checkNotNull` 是最高频方法。两者方向相反，迁移时最容易踩坑。

---

## 五、消息与国际化

| | Guava | ValidX |
|---|---|---|
| 模板 | `"余额不足: %s"`（`%s` 占位） | 内置 8 种语言消息 |
| 字段名 | 手写进模板 | `.field("手机号")` 自动拼进错误 |
| 语言切换 | 手动 | `withLocale(Locale.SIMPLIFIED_CHINESE)` 一行切换 |

```java
// Guava：消息靠手写，面向调试
checkArgument(age >= 18, "age 不合法: %s", age);

// ValidX：字段标签 + 语言切换，面向用户
ValidX v = ValidX.init()
    .withLocale(Locale.ENGLISH)
    .field("Age").notEmpty().isAge(age, 0, 120);   // 错误消息自动为英文
```

对出海应用来说，ValidX 的"一套校验规则、多语言错误消息"能省掉大量硬编码文案。

---

## 六、性能与依赖

- **Guava Preconditions**：本身是纯静态方法调用，开销极小；但为了用它引入整个 Guava（数 MB、几十个模块），对只想要"几个检查方法"的项目是杀鸡用牛刀。轻量替代：JDK 自带 `Objects.requireNonNull` + `Objects.checkIndex`（Java 9+）。
- **ValidX 链式 API**：一次实例一个校验器，无反射（注解方式才涉及反射），链式场景开销可忽略；单一依赖、约 300KB，且内置 100+ 业务验证器（身份证、手机号、银行卡……）——这是 Guava 给不了的。

**性能建议**：两者在"判断一个布尔值"上的开销都可忽略，真正的性能差异来自**引入了多少你根本用不上的代码**。

---

## 七、实战对照：同一个注册接口

用同一个"用户注册"场景，看两种思路的完整落地：

### 7.1 Guava 写法：断言风格

```java
public void register(UserDTO dto) {
    // 每个条件一个断言，第一个失败即抛异常
    checkArgument(dto != null, "dto 不能为 null");
    checkArgument(dto.getUsername() != null && !dto.getUsername().isBlank(),
            "用户名不能为空");
    checkArgument(dto.getEmail() != null && EMAIL_PATTERN.matcher(dto.getEmail()).matches(),
            "邮箱格式不正确: %s", dto.getEmail());
    checkArgument(dto.getPhone() == null || PHONE_PATTERN.matcher(dto.getPhone()).matches(),
            "手机号格式不正确: %s", dto.getPhone());

    userService.create(dto);
    // 问题：用户改完用户名，又被告知邮箱也错了（一次只能报一个错）
}
```

### 7.2 ValidX 写法：验证风格

```java
public void register(UserDTO dto) {
    ValidX v = ValidX.init()
            .field("用户名").notEmpty().isAlphaNumber(dto.getUsername())
            .field("邮箱").notEmpty().isEmail(dto.getEmail())
            .field("手机号").allowNull().isChinesePhone(dto.getPhone());  // 可选字段

    if (!v.passed()) {
        // 一次返回全部错误，直接拼进响应体
        throw new ValidationException(v.getErrors());
    }

    userService.create(dto);
}
```

**直观差异**：
- Guava 版每多一个字段就要多写一行正则 + 一个异常；ValidX 版是字段标签 + 现成验证器；
- 手机号/身份证这类**中国业务格式**，Guava 没有内置，必须手写正则；ValidX 直接 `isChinesePhone`/`isChineseIdCard`；
- Guava 一次只能报一个错，ValidX 一次报全部。

---

## 八、协作：什么时候用谁

正确的姿势不是二选一，而是**各司其职**：

| 层级 | 用谁 | 原因 |
|------|------|------|
| 库 / 工具类内部参数防御 | Guava `checkArgument` / `checkNotNull` | 调用方是开发者，违约即 bug，应 fail-fast |
| 状态机 / 不可逆操作前置 | Guava `checkState` | "状态不对"说明程序流转出错，是 bug |
| Controller / Service 入参 | ValidX 链式 / 注解 | 输入来自用户，需要友好、聚合的错误提示 |
| 批量导入（Excel/CSV） | ValidX 链式 | 需要"这行有问题跳过，继续处理下一行" |

**混合使用示例**：业务入口用 ValidX 验证用户输入，通过后进入内部方法用 Guava 断言内部契约。

```java
public void submitOrder(OrderRequest req) {
    // 第一层：用户输入验证（ValidX，错误返回给前端）
    ValidX v = ValidX.init()
            .field("商品ID").notEmpty().isAlphaNumber(req.getProductId())
            .field("数量").notNull().isIn(req.getQuantity(), new String[]{"1", "2", "3", "4", "5"});
    if (!v.passed()) {
        throw new ValidationException(v.getErrors());
    }

    // 第二层：内部契约断言（Guava，违约即 bug）
    checkState(inventory.isLocked(req.getProductId()), "库存未锁定，流程状态异常");
    checkArgument(orderService.calcPrice(req).signum() > 0, "计算出的价格非法");

    orderService.submit(req);
}
```

---

## 使用场景建议

| 你的场景 | 推荐 | 一句话理由 |
|---------|------|-----------|
| 写公共库 / SDK，防御非法参数 | Guava `checkArgument` | 调用方违约即 bug，fail-fast 最合适 |
| Controller 接收用户表单/JSON | ValidX 链式或注解 | 错误要聚合、要友好、要可多语言 |
| 校验中国手机号/身份证/银行卡 | ValidX | 内置验证器，不用手写正则 |
| Excel 批量导入，行级容错 | ValidX 链式 | 收集全部问题行，跳过继续 |
| 只需要"非空 + 不越界" | JDK `Objects.requireNonNull` | 零依赖即可，不必引入任何库 |
| 状态机 / 操作前置条件 | Guava `checkState` | "状态不对"是程序 bug 信号 |
| 框架内部 DTO 自动校验 | ValidX 注解（`@NotBlank` 等） | 与 Bean Validation 无缝集成 |

---

## 总结

- **Guava Preconditions 是断言**：面向开发者契约，失败即抛异常（fail-fast），消息是给调试看的。它的价值在于把"前提条件"压缩到方法入口，让后续代码免于判断。
- **ValidX 是验证**：面向用户输入，失败收集错误（可聚合、可多语言、可带字段标签），消息是给用户看的。它的价值在于用声明式链式 API 覆盖中国业务场景的海量格式规则。
- **别混用**：把用户输入校验写成 `checkArgument`，会把"输入不合法"误报成"服务器错误"；把内部契约写成业务验证，又会让 bug 被"友好提示"掩盖。
- **最佳实践是分层协作**：入口用 ValidX 验证数据，内部用 Guava 断言契约——**验证管用户，断言管自己**。

> 关联阅读：本系列对比文章《ValidX vs Apache Commons Validator：功能与性能对比》《ValidX vs Hutool Validator：专业框架 vs 工具库》，帮你把 ValidX 放进整个 Java 验证生态中选型。
