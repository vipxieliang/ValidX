# ValidX常见问题FAQ：新手最容易踩的10个坑

## 📋 目录
- [引言](#引言)
- [坑位总览](#坑位总览)
- [坑 1：以为加了 @Email 就算必填](#坑-1以为加了-email-就算必填)
- [坑 2：链式 isEmail(...) 对空值直接放行](#坑-2链式-isemail-对空值直接放行)
- [坑 3：notEmpty()/allowNull() 只管一次调用](#坑-3notempty-与-allownull-只管一次调用)
- [坑 4：config() 混在验证链中间反复调用](#坑-4config-混在验证链中间反复调用)
- [坑 5：把 ValidX 实例当静态工具缓存复用](#坑-5把-validx-实例当静态工具缓存复用)
- [坑 6：沿用 v1.1.0 的旧链式 API](#坑-6沿用-v110-的旧链式-api)
- [坑 7：想校验前缀/后缀匹配却用错方法](#坑-7想校验前缀后缀匹配却用错方法)
- [坑 8：Controller 忘了加 @Valid](#坑-8controller-忘了加-valid)
- [坑 9：jakarta 与 javax 生态错配](#坑-9jakarta-与-javax-生态错配)
- [坑 10：全局必填 + 个别可选 写成一团乱麻](#坑-10全局必填--个别可选-写成一团乱麻)
- [总结](#总结)
- [项目地址](#项目地址)

---

## 引言

ValidX 上手很容易——加一个依赖，`@Email` 或 `.isEmail()` 一写就跑。但真正把它用到生产环境，新手往往会在一些"看似会报错、实际却静默通过"的地方翻车：校验没触发、空值溜进数据库、线程并发结果错乱……

这篇文章整理新手最容易踩的 10 个坑。**每个坑都遵循同一结构：症状 → 根因 → 修复**，让你看完就能对照排查，少走弯路。

> 本文基于 ValidX **v1.2.0** 编写。涉及历史版本迁移的地方会特别标注。

---

## 坑位总览

| # | 一句话症状 | 一句话根因 |
|---|-----------|-----------|
| 1 | `@Email` 拦不住空邮箱 | 判空与格式是两件事，格式注解放行 `null`/`""` |
| 2 | 链式校验必填字段悄悄通过 | 链式默认 `DEFAULT`，同样放行 `null`/`""` |
| 3 | `notEmpty()` 只对第一个字段生效 | 局部状态每次验证后自动重置 |
| 4 | 链中间改 `config()` 后结果诡异 | 全局配置应固定在开头一次 |
| 5 | 高并发下校验结果时对时错 | ValidX 实例非线程安全，不可共享 |
| 6 | 用 v1.1.0 旧写法编译不过 | v1.2.0 有破坏性 API 变更 |
| 7 | 前缀/后缀匹配结果不对 | 单值与多值（Any）方法用混了 |
| 8 | 加了注解却不报错 | Controller 少了 `@Valid` 触发 |
| 9 | 抛 `ValidationException` / 校验无效 | javax/jakarta 生态或校验器装配错配 |
| 10 | "必填+可选"写成一长串魔法 | 全局与局部覆盖策略没用好 |

下面逐一拆解。

---

## 坑 1：以为加了 @Email 就算必填

**症状**：字段是必填的，于是只加了 `@Email`，结果前端传 `null` 或 `""` 竟然通过了。

**根因**：ValidX 所有**格式验证注解**（`@Email`、`@ChineseIdCard`、`@ChinesePhone`…）对 `null` 和空字符串 `""` **都返回通过**。这不是 bug，而是遵循 Bean Validation（JSR-380）的**关注点分离**设计：

- `@NotBlank` / `@NotEmpty` / `@NotNull`：负责检查"字段**是否**存在"
- `@Email` 等格式注解：负责检查"字段**如果有值**，格式是否正确"

```java
// ❌ 只有格式注解：email=null 或 "" 都会通过
public class UserDTO {
    @Email
    private String email;
}

// ✅ 必填 + 格式正确，两者缺一不可
public class UserDTO {
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;
}
```

**修复**：必填字段一定用 `@NotBlank`（或 `@NotNull`/`@NotEmpty`）+ 格式注解**组合**，参考下表：

| 需求 | 注解组合 |
|------|---------|
| 必填且格式正确（推荐） | `@NotBlank` + `@Email` |
| 允许空字符串但非空格式 | `@NotNull` + `@Email` |
| 可选，但填了必须格式正确 | 仅 `@Email` |

---

## 坑 2：链式 isEmail(...) 对空值直接放行

**症状**：用链式校验一个"必须填写"的手机号，`data.get("phone")` 是 `null`，`isChinesePhone(...)` 却通过了。

**根因**：链式校验（`ValidX.init()`）的默认行为与注解一致——**`null` 和空字符串会通过验证**。因为链式常用于解析外部 API / Map 数据，字段为 `null` 通常表示"该字段不存在，不需要校验"，而不是错误。

```java
// ❌ phone 为 null 时静默通过，后续用了一个空值去查询/入库
ValidX validator = ValidX.init()
        .field("手机号").isChinesePhone(userData.get("phone"));

// ✅ 开头声明全局要求：所有字段都拒绝 null 和空字符串
ValidX validator = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_EMPTY)   // 不能为 null 或 ""
        .field("手机号").isChinesePhone(userData.get("phone"));
```

**修复**：字段"必须非空且格式正确"时，用 `config(ValidXConfig.GLOBAL_NOT_EMPTY)`（全部必填）或局部 `.notEmpty()`（单个必填）前置拦截。

三种全局策略：

| 配置 | 行为 |
|------|------|
| `ValidXConfig.DEFAULT` | 允许 `null` 和 `""`（默认） |
| `ValidXConfig.GLOBAL_NOT_NULL` | 所有字段不能为 `null` |
| `ValidXConfig.GLOBAL_NOT_EMPTY` | 所有字段不能为 `null` 或 `""` |

---

## 坑 3：notEmpty()/allowNull() 只管一次调用

**症状**：明明写了 `.notEmpty().isQQ(a).isWeChat(b)`，想当然以为 `b` 也必填，结果 `b=null` 通过了。

**根因**：**局部状态（`notNull`/`notEmpty`/`allowNull`/`allowEmpty`）在每次验证方法调用后会自动重置**，确保每个字段的校验相互独立。所以 `.notEmpty()` 只作用于紧跟它的那一个校验。

```java
// ❌ 以为 notEmpty 管到链尾
validator.notEmpty().isEmail(email1)
                    .isEmail(email2);   // email2 实际用默认行为，null/"" 会通过

// ✅ 每个必填字段都要单独标
validator.notEmpty().isEmail(email1)
        .notEmpty().isEmail(email2);
```

**修复**：把局部状态理解为"**一次性修饰符**"，每个需要非空的字段都单独调用一次 `notEmpty()`（需要允许空则 `allowNull()`）。想对"绝大多数字段"统一必填，应改用坑 2 的全局 `config()`，再对个别可选字段用 `.allowNull()` 覆盖。

---

## 坑 4：config() 混在验证链中间反复调用

**症状**：写了一大段链式校验，中间为了"某些字段必填、某些字段可不填"，在链中间几次调用 `config()`，结果行为混乱，甚至报错的地方根本不是你以为的那段。

**根因**：全局配置在链中间反复切换会很难跟踪"此刻到底是哪种策略"，维护成本极高。

```java
// ❌ 令人困惑：难以知道每个字段当前用的是哪种策略
ValidX validator = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_NULL)
        .isEmail(email)
        .config(ValidXConfig.DEFAULT)      // 中途改回默认
        .isPhone(phone);
```

**修复**：`config()` **只在验证链开头调用一次**；当需要不同策略时，拆成多个独立验证器实例，互不干扰。

```java
// ✅ 开头设一次全局策略
ValidX strict = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_EMPTY)
        .field("邮箱").isEmail(email)
        .field("手机").isChinesePhone(phone)
        .field("QQ（可选）").allowNull().isQQ(qq);   // 例外用局部方法覆盖

// ✅ 需要另一套宽松策略，新建一个实例
ValidX lenient = ValidX.init()
        .field("昵称").isChineseName(nickname);
```

---

## 坑 5：把 ValidX 实例当静态工具缓存复用

**症状**：定义了一个 `private static final ValidX` 全局复用，并发请求一多，校验结果时对时错、错误消息串了字段。

**根因**：**ValidX 实例不是线程安全的**。它内部用可变状态（局部要求标志、字段标识、错误列表）在校验链执行过程中被修改，跨线程共享会产生竞态条件。

```java
// ❌ 跨线程共享一个实例
private static final ValidX VALIDATOR = ValidX.init();

public void validate(User user) {
    VALIDATOR.isEmail(user.getEmail());   // 线程不安全！
}
```

**修复**：遵循"创建 - 使用 - 丢弃"模式，**每次校验都新建实例**（与 `StringBuilder`、Java 8 `Stream`、Lombok `Builder` 同一套路）。

```java
// ✅ 每次校验新建
public void validate(User user) {
    ValidX validator = ValidX.init()
            .field("邮箱").isEmail(user.getEmail())
            .field("手机").isChinesePhone(user.getPhone());
    if (!validator.passed()) {
        throw new ValidationException(validator.getErrors());
    }
}
```

可以安全共享的是**不可变/无状态**组件：`ValidXConfig` 对象，以及各验证器类（如 `ChineseIdCardValidator`）都是线程安全的。

---

## 坑 6：沿用 v1.1.0 的旧链式 API

**症状**：把 v1.1.0 时代写好的链式校验原样带到新项目，要么编译不过，要么编译通过但行为不符合预期。

**根因**：**v1.2.0 引入了破坏性变更**，链式 API 有两类调整（与注解命名 1:1 对齐）：

| 变更 | v1.1.0（旧） | v1.2.0（新） |
|------|-------------|-------------|
| 单值前缀/后缀 | `isStartsWith(value, String[])` | `isStartsWith(value, "prefix")` |
| 方法重命名 | `isAlphaNum()` | `isAlphaNumber()` |
| 方法重命名 | `isMacAddress()` | `isMac()` |

```java
// ❌ v1.1.0 老写法：参数是数组
validator.isStartsWith(str, new String[]{"http"});

// ✅ v1.2.0：单值用 isStartsWith(str, "http")
validator.isStartsWith(str, "http");

// ✅ 想要多值匹配，改用 isStartsWithAny
validator.isStartsWithAny(str, new String[]{"http", "https"});
```

**修复**：升级到 v1.2.0 时按迁移指南逐处核对链式调用。`isAlphaNum` 与 `isMacAddress` 是**纯改名**，参数与行为不变，全局替换方法名即可。

> 从 v1.1.0 升级可先读 `docs/version/v1.2.0/MIGRATION_v1.2.0.cn.md` 迁移指南，里面列出了全部变更点。

---

## 坑 7：想校验前缀/后缀匹配却用错方法

**症状**：想校验"字符串必须以 `http` 或 `https` 之一开头"，用了 `.isStartsWith(url, "http")`，结果 `https://...` 被判为非法。

**根因**：v1.2.0 把 `isStartsWith`/`isEndsWith` 收敛为**单值**方法——它只判断"是否以**这一个**前缀开头"。要匹配多个候选前缀，得用专门的多值方法（对应注解 `@StartsWithAny` / `@EndsWithAny`）。

```java
// ❌ isStartsWith 只认单个前缀，"https://" 会失败
validator.isStartsWith(url, "http");

// ✅ 多个候选前缀用 isStartsWithAny
validator.isStartsWithAny(url, new String[]{"http", "https"});

// 注解版同理
public class UrlDTO {
    @NotBlank
    @StartsWithAny(value = {"http", "https"}, message = "URL必须以http或https开头")
    private String url;
}
```

**修复**：看清"单值 vs 多值"的命名规律——带 `Any` 的方法/注解（`isStartsWithAny`、`@StartsWithAny`、`isEndsWithAny`、`@EndsWithAny`）接收一组候选值，不带 `Any` 的只接收单值。按需选对，别只用一个写到底。

---

## 坑 8：Controller 忘了加 @Valid

**症状**：DTO 上注解写得整整齐齐，请求却"畅通无阻"地进到 Service，脏数据一路跑到底层才报错。

**根因**：注解方式需要**容器触发校验**。Spring MVC 只有在参数上用 `@Valid`（或 `@Validated`）时才会在校验失败后返回 400。注解只做**声明**，触发靠 `@Valid`。

```java
// ❌ 少了 @Valid，dto 不会自动校验
@PostMapping("/register")
public Result register(@RequestBody UserRegistrationDTO dto) { ... }

// ✅ 加 @Valid，Spring 自动校验，失败返回 400
@PostMapping("/register")
public Result register(@Valid @RequestBody UserRegistrationDTO dto) { ... }
```

**修复**：Controller 方法参数务必带上 `@Valid`。若用分组校验，用 Spring 的 `@Validated` + 分组接口。排查时按"注解声明 → @Valid 触发 → 校验器实现就位"三层核对，缺哪层都会出现"不报错"的假象。

---

## 坑 9：jakarta 与 javax 生态错配

**症状**：项目升级到 Spring Boot 3，把 DTO 注解改成 `jakarta.validation` 之后，ValidX 的注解要么不生效，要么直接抛 `ValidationException`。

**根因**：ValidX **构建于 JSR-380（`javax.validation`）体系**，天然匹配 **Spring Boot 2.x / Java 8~17 的 javax 生态**。Spring Boot 3 已切换到 `jakarta.validation` 命名空间，两套体系不通用——把 `javax.validation` 的注解包拷贝给 jakarta 校验器，解析不到实现就会出问题。

| 生态 | 校验包 | 典型 Spring Boot | 是否匹配 ValidX |
|------|--------|-----------------|----------------|
| javax | `javax.validation.*` | Boot 2.x / Java 8~17 | ✅ 直接匹配 |
| jakarta | `jakarta.validation.*` | Boot 3.x | ⚠️ 需先冒烟验证 |

**修复**：
1. **首选**：若项目是 Boot 3.x 又重度依赖 ValidX 注解，先在独立小工程里做一次注解冒烟测试，确认 `jakarta.validation` 能正确触发 ValidX 校验器再接入。
2. **稳妥**：锁定仍使用 javax 的 Spring Boot 2.x 版本线上使用。
3. **别想当然**：不要以为"把 import 从 javax 改成 jakarta 就能直接跑"，应先跑通冒烟测试再大范围替换。

另外注意：若为了跟 Spring Boot 的 Hibernate Validator 统一版本而 `exclude` 掉了 ValidX 内置的校验器，请务必确保工程仍能解析到另一个校验器实现 + EL 实现，否则注解模式会在运行时因找不到实现而抛 `ValidationException`（详见集成配置指南第六节排查表）。

---

## 坑 10：全局必填 + 个别可选 写成一团乱麻

**症状**：要校验的表单"90% 字段必填，只有 QQ、网址可选"。有人对每个必填字段逐个写 `notEmpty()`，有人在中途反复 `config()`，代码又长又脆。

**根因**：没用好"**全局配置打底 + 局部状态覆盖例外**"的组合拳——这是 ValidX 为这种场景提供的标准姿势。

**修复**：用全局 `config(GLOBAL_NOT_EMPTY)` 覆盖绝大多数必填字段，只对可选字段用 `.allowNull()`（允许为 null 时跳过校验）或 `.allowEmpty()`（非 null 但可为空串）做局部覆盖。

```java
ValidX validator = ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_EMPTY)          // 大部分字段必填非空
        .field("邮箱").isEmail(userData.get("email"))    // 必填
        .field("手机").isChinesePhone(userData.get("phone"))  // 必填
        .field("身份证").isChineseIdCard(userData.get("idCard")) // 必填
        .field("QQ").allowNull().isQQ(userData.get("qq"))       // 可选，填了就校验格式
        .field("网址").allowNull().isUrl(userData.get("website")); // 可选
```

**优先级规则**（务必记牢）：**局部状态 > 全局配置 > 默认行为**。所以 `.allowNull()` 能覆盖掉 `GLOBAL_NOT_EMPTY` 对某个可选字段的要求。

补充两个可读性建议：
- `.field("中文标签")` 让错误消息带上业务字段名，排查定位更快。
- 结合坑 3 的"状态每次重置"与这里的"局部覆盖优先级最高"，就能用"全局必填 + 局部可选"写出简洁、明确、不靠魔法数字的校验链。

---

## 总结

| 主题 | 结论 |
|------|------|
| 判空与格式 | 格式注解/链式方法默认放行 `null`/`""`；必填要叠 `@NotBlank` 或 `config(GLOBAL_NOT_EMPTY)`/`notEmpty()` |
| 局部状态 | `notNull/notEmpty/allowNull/allowEmpty` 每次验证后自动重置，只管紧跟的一个方法 |
| 全局配置 | `config()` 只在链开头一次；不同策略拆多个实例 |
| 线程安全 | ValidX 实例非线程安全，每次校验新建；`ValidXConfig` 与校验器类可安全共享 |
| 版本迁移 | v1.2.0 破坏性变更：`isStartsWith/isEndsWith` 改单值，`isAlphaNum→isAlphaNumber`、`isMacAddress→isMac` |
| 多值匹配 | 前缀/后缀多候选用 `isStartsWithAny/@StartsWithAny`、`isEndsWithAny/@EndsWithAny` |
| 注解触发 | Controller 参数必须加 `@Valid`，否则校验不执行 |
| 生态匹配 | ValidX 走 javax（Boot 2.x/Java 8~17）；Boot 3 的 jakarta 需先冒烟验证 |

最后送一句口诀：**"判空要叠加，实例不共享，config 放开头，javax 对得上"**——把这四句记住，10 个坑基本都能绕开。

---

## 项目地址

- **GitHub**：https://github.com/vipxieliang/ValidX
- **Gitee**：https://gitee.com/vipxieliang/ValidX
- **Maven Central**：https://central.sonatype.com/artifact/io.github.vipxieliang/validx
