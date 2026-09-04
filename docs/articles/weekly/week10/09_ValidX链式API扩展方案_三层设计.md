# ValidX 链式 API 扩展方案：三层设计

## 引言

ValidX 的链式 API（`ValidX.init().field("省份").isIn(value, new String[]{...})`）使用体验极佳，但它有一个天然的边界：**链式方法定义在 ValidX 库内部**（`ValidX.java`、`BaseValidation.java`），普通业务项目不能直接往库里加方法。

于是问题来了：业务侧有自定义规则（公司工单号、颜色值、订单状态机）想走链式 API，怎么办？

本文给出三层递进的扩展方案，覆盖从"临时用一次"到"生态级 SPI"的全部场景。方案设计原则是：**开放门面上的通用入口 + 引入规则注册中心，而不是让用户改库**——现有 10 个分组类完全不动，兼容性零风险。

---

## 一、现状回顾：为什么业务项目加不了方法

先看当前链式 API 的架构（对照 ValidX v1.2.0 源码）：

```
ValidX（门面，持有 10 个分组）
 ├── base.BaseValidation        → validateIn / validateEmail ...
 ├── china.ChinaValidation      → validateChineseIdCard ...
 ├── finance.FinanceValidation  → validateBankCard ...
 └── ...（共 10 个分组，每个分组硬编码 validateXxx()）
```

业务项目想加一个 `isHexColor` 链式方法，需要：

1. 改 `BaseValidation` 加 `validateHexColor`；
2. 改 `ValidX` 加 `isHexColor` 入口；
3. 重新编译、发布新版本 ValidX。

这显然不可行——**库代码不能跟着每个业务方的需求走**。扩展的关键，是把"规则"从"库代码"中剥离出来，让它变成可以动态注册的东西。

---

## 二、三层方案总览

| 层级 | 方案 | 适用场景 | 复杂度 | 可复用性 |
|------|------|---------|:---:|:---:|
| 第 1 层 | 通用检查入口 `check` | 一次性判断、临时规则 | 低 | 无（即写即用） |
| 第 2 层 | 规则对象 + 注册表 | 公司内部统一规则 | 中 | 高（名字化复用） |
| 第 3 层 | SPI 服务发现 | 对外发布扩展包 / 生态建设 | 高 | 极高（加 jar 即接入） |

三层的实现互不冲突：第 2 层 `rule()` 内部先查注册表再走第 1 层的 `check` 逻辑，第 3 层 SPI 只是第 2 层注册表的自动填充器。可以按需选用，也可以从第 1 层起步逐步演进。

---

## 三、第 1 层：通用检查入口（零注册，覆盖 80% 场景）

### 3.1 设计

在 `ValidX` 门面上直接加两个通用方法，Predicate 即插即用：

```java
/** 规则检查：不满足 rule 则记错误 */
public ValidX check(Object value, Predicate<Object> rule, String message) {
    if (rule.test(value)) {
        return this;
    }
    addError(message);        // 复用现有的 errors/locale 机制
    return this;
}

/** 布尔断言：false 则记错误 */
public ValidX check(boolean condition, String message) {
    if (condition) {
        return this;
    }
    addError(message);
    return this;
}
```

### 3.2 用法

```java
ValidX.init()
    .field("颜色").check(color, v -> COLOR_PATTERN.matcher((String) v).matches(), "颜色格式不正确")
    .field("库存").check(stock >= 0, "库存不能为负");
```

### 3.3 优劣

**优点**：
- 零注册、零类定义，一行搞定；
- `check(boolean, message)` 把 if-else 断言变成链式，可读性更好；
- 复用门面现有的 `addError` / 国际化机制，行为与内置方法一致。

**代价**：
- 规则不可复用（写在哪就用在哪）；
- 没有规则名字，无法集中管理、无法换文案。

---

## 四、第 2 层：规则对象 + 注册表（可复用、类型安全）

### 4.1 设计

对"这个规则我要在多处用"的场景，加一个注册中心，让规则**有名字、可复用**：

```java
/** 自定义规则：校验 + 消息 */
public interface CustomRule {
    boolean isValid(Object value);
    String getMessage();     // 或 getMessageKey()，走 MessageManager 支持国际化
}

/** 规则注册中心 */
public final class ValidXRules {
    private static final Map<String, CustomRule> RULES = new ConcurrentHashMap<>();

    public static void register(String name, CustomRule rule) {
        RULES.put(name, rule);
    }

    public static CustomRule get(String name) {
        CustomRule rule = RULES.get(name);
        if (rule == null) {
            throw new IllegalArgumentException("未注册的验证规则: " + name);
        }
        return rule;
    }
}
```

门面上对应一个入口：

```java
public ValidX rule(String ruleName, Object value) {
    CustomRule rule = ValidXRules.get(ruleName);
    if (!rule.isValid(value)) {
        addError(rule.getMessage());
    }
    return this;
}
```

### 4.2 用法

```java
// 启动时注册一次
ValidXRules.register("hexColor", new HexColorRule());
ValidXRules.register("orderNo", new OrderNoRule());

// 到处复用
ValidX.init()
    .field("颜色").rule("hexColor", color)
    .field("订单号").rule("orderNo", orderNo);
```

### 4.3 优化点

- **消息国际化**：`CustomRule.getMessage()` 可以换成 `getMessageKey()`，在 `ValidX.rule()` 里交给 `MessageManager.getMessage(key, locale)` 处理，与 ValidX 的 9 语言机制无缝衔接；
- **编译期安全**：规则名用字符串有拼写风险，可以定义常量类 `RuleNames.HEX_COLOR = "hexColor"` 缓解；
- **ConcurrentHashMap** 保证注册线程安全，启动期注册、运行期只读，性能零损耗。

### 4.4 优劣

**优点**：规则集中管理、可复用、可换文案、可加参数。
**代价**：需要注册步骤；字符串名字无编译期检查。

---

## 五、第 3 层：SPI 服务发现（库生态级，第三方扩展包）

### 5.1 设计

如果想让**别人写的扩展包**能自动接入 ValidX（比如 `validx-ext-color`、`validx-ext-crypto`），用 JDK 自带的 `ServiceLoader`：

```java
/** 规则提供者：扩展包实现它，打包进 META-INF/services */
public interface RuleProvider {
    Map<String, CustomRule> provide();
}

// ValidXRules 里增加 SPI 加载（static 块或显式 init）
public static synchronized void loadProviders() {
    ServiceLoader<RuleProvider> loader = ServiceLoader.load(RuleProvider.class);
    for (RuleProvider provider : loader) {
        provider.provide().forEach(ValidXRules::register);
    }
}
```

### 5.2 扩展包的写法

以 `validx-ext-color` 为例，扩展包只需三步：

**① 实现 `RuleProvider`**：

```java
public class ColorRuleProvider implements RuleProvider {
    @Override
    public Map<String, CustomRule> provide() {
        Map<String, CustomRule> rules = new HashMap<>();
        rules.put("hexColor", new HexColorRule());
        rules.put("rgbColor", new RgbColorRule());
        return rules;
    }
}
```

**② 在 `META-INF/services` 下注册**（文件名 = 接口全限定名）：

```
io.github.vipxieliang.validx.chain.spi.RuleProvider
```

文件内容：

```
com.example.validxext.color.ColorRuleProvider
```

**③ 打包发布**，用户引入依赖即自动生效：

```java
// 用户只需加一个依赖：validx-ext-color
// 无需任何注册代码
ValidX.init().field("颜色").rule("hexColor", color);
```

### 5.3 效果与边界

**效果**：加一个 jar 就多一组链式规则，业务代码零改动——这是标准的 Java SPI 生态玩法（和 JDBC 驱动、SLF4J 绑定同一个套路）。

**边界**：
- `loadProviders()` 要在应用启动时调用一次（Spring 下可放 `ApplicationRunner` 或 `@PostConstruct`）；
- 扩展包之间的规则名冲突要约定命名空间（如 `ext-color.hexColor`）；
- 规则在运行期注册，注册时机晚于使用时会报"未注册的验证规则"，需要做好初始化时序。

---

## 六、选型建议

| 场景 | 用哪层 |
|------|------|
| 一次性判断、临时规则 | 第 1 层 `check` |
| 公司内部统一规则（订单号、颜色、工号） | 第 2 层注册表 |
| 对外发布扩展包 / 生态建设 | 第 3 层 SPI |

演进路线：先在第 1 层写起来 → 发现多处复用，抽成 `CustomRule` 注册到第 2 层 → 规则够通用想开源，打包成扩展 jar 走第 3 层 SPI。每一层都是上一层的自然升级，不需要推翻重来。

---

## 七、落地实施步骤

如果要在 ValidX 源码里真正实现这套方案，改动清单如下：

| # | 改动 | 文件 | 说明 |
|---|------|------|------|
| 1 | 新增 `CustomRule` 接口 | `chain/spi/CustomRule.java` | 校验 + 消息 |
| 2 | 新增 `ValidXRules` 注册中心 | `chain/spi/ValidXRules.java` | ConcurrentHashMap + SPI 加载 |
| 3 | 新增 `RuleProvider` 接口 | `chain/spi/RuleProvider.java` | SPI 提供者 |
| 4 | 新增 `check` 两个重载 | `chain/ValidX.java` | 第 1 层通用入口 |
| 5 | 新增 `rule` 入口 | `chain/ValidX.java` | 第 2 层注册表入口 |
| 6 | 单元测试 | `src/test/...` | 三层各自 + 组合场景 |

**兼容性**：现有 10 个分组类、`BaseValidation.validateXxx()` 全部不动；新增文件全部在新包 `chain/spi` 下，版本升级零破坏。

---

## 总结

- 链式 API 扩展的瓶颈是"方法定义在库内"，解法是**开放通用入口 + 动态注册规则**，把"规则"从"库代码"里解放出来；
- **第 1 层 `check`**：零注册即写即用，覆盖 80% 一次性场景；
- **第 2 层 `ValidXRules` 注册表**：规则名字化、集中管理、可复用，公司内部统一规则的首选；
- **第 3 层 SPI**：第三方扩展包加 jar 即接入，是 ValidX 走向生态的正确姿势；
- 三层互不冲突、可渐进演进，且对现有架构完全向后兼容。

> ValidX 是基于 Jakarta Bean Validation 规范的 Java 验证库，注解与链式 API 双模式，内置 100+ 验证规则。

## 项目地址

- **GitHub**：<https://github.com/vipxieliang/ValidX>
- **Gitee**：<https://gitee.com/vipxieliang/ValidX>
- **Maven Central**：<https://central.sonatype.com/artifact/io.github.vipxieliang/validx>

Maven 引入：

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.2.0</version>
</dependency>
```
