# ValidX错误消息国际化完全指南：8种语言9个语言包与三级回退机制

## 引言

假设你的系统准备出海：

- 日本用户提交了非法手机号，收到的提示是中文"手机号码格式不正确"；
- 德国用户填写生日填错格式，报错信息是一串英文+中文混杂的乱码；
- 更常见的是：为了给海外版单独做一套报错文案，开发在 Controller 里写满 `if (lang == EN) return "..."`，改一次文案要改三个语言分支。

这些都是"验证错误消息国际化"没做好的典型表现。一个成熟的开源验证库，错误消息应该**开箱即用、随用户语言自动切换**——不用你翻译、不用你配置语言分支。

ValidX 的多语言支持正是这个思路：内置 **9 个语言包文件、覆盖 8 种语言**（简体中文、英文、日文、韩文、法文、德文、西班牙文、俄文；中文由无后缀默认包与 `_zh` 包两个文件承载），注解方式与链式 API **两条路径共用一套消息体系**，并提供**三级回退**保证任何语言环境下都能拿到可读的报错。本文对照源码逐层拆解这套机制，并给出可直接照抄的实战用法。

> 说明：文中所有实现细节均对照 ValidX v1.2.0 源码（`MessageManager`、`ValidX`、`ValidationMessages_*.properties`）逐一核实；示例代码与测试用例引自项目测试目录，可自行复现。

---

## 一、国际化机制全景

### 1.1 支持的语言

| 语言 | 资源文件 | 说明 |
|------|---------|------|
| 简体中文 | `ValidationMessages_zh.properties` | 中文包 |
| 简体中文（默认） | `ValidationMessages.properties` | 无后缀兜底文件，内容为中文 |
| 英文 | `ValidationMessages_en.properties` | 兜底语言（回退目标） |
| 日文 | `ValidationMessages_ja.properties` | |
| 韩文 | `ValidationMessages_ko.properties` | |
| 法文 | `ValidationMessages_fr.properties` | |
| 德文 | `ValidationMessages_de.properties` | |
| 西班牙文 | `ValidationMessages_es.properties` | |
| 俄文 | `ValidationMessages_ru.properties` | |

> 冷知识：`ValidationMessages.properties`（无语言后缀）虽然名字是"默认"，内容其实是**中文**；而英文是单独一个 `_en` 包。这意味着"未覆盖的语言环境最终会落到中文"，"兜底兜底再兜底"才到英文。

### 1.2 两条消费路径，一套消息体系

```
┌──────────────────────────────┐   ┌──────────────────────────────┐
│  注解验证（@Email 等）          │   │  链式 API（ValidX.init()）     │
│  message()="{完全限定key}"     │   │  withLocale(locale)          │
└──────────────┬───────────────┘   └──────────────┬───────────────┘
               │                                  │
               ▼                                  ▼
  Hibernate Validator                  MessageManager
  ResourceBundleMessageInterpolator    .getMessage(key, locale)
  （解析 {key}，按线程 Locale 取）       ├─ 语言包缓存 ConcurrentHashMap
               │                       ├─ UTF8Control 强制 UTF-8 读取
               └──────────┬───────────┼─ 三级回退：指定语言→英文→key
                          │           └─ ThreadLocal 线程级语言切换
                          ▼
        ValidationMessages_*.properties（9 个语言包）
```

- **注解方式**：默认消息写成 `{key}` 花括号占位符，交给标准 Bean Validation 的 `MessageInterpolator` 解析替换；
- **链式方式**：直接调用自研 `MessageManager.getMessage(key, locale)` 生成对应语言的文本。

两条路径最终都从**同一套 properties 语言包**取文案，所以不存在"注解是中文、链式变英文"的口径分裂。

### 1.3 消息 key 的命名规范

消息 key 采用**完全限定名**，前缀即包名，可读性好且天然避免冲突：

| 类别 | key 格式 | 示例 |
|------|---------|------|
| 注解消息 | `io.github.vipxieliang.validx.annotation.<name>` | `...annotation.chinese.idcard` |
| 验证器消息 | `io.github.vipxieliang.validx.validator.<name>` | `...validator.date.pattern.contains.time` |
| 通用值消息 | `io.github.vipxieliang.validx.value.<name>` | `...value.null` |

---

## 二、语言包与消息对照

### 2.1 中英文对照示例

`ValidationMessages_en.properties`：

```properties
io.github.vipxieliang.validx.value.null=Value cannot be null
io.github.vipxieliang.validx.annotation.chinese.idcard=Invalid Chinese ID card number
io.github.vipxieliang.validx.annotation.chinese.phone=Invalid mobile phone number format
io.github.vipxieliang.validx.annotation.email=Invalid email address format
io.github.vipxieliang.validx.annotation.date.format=Invalid date format
```

`ValidationMessages_zh.properties`：

```properties
io.github.vipxieliang.validx.value.null=值不能为空
io.github.vipxieliang.validx.annotation.chinese.idcard=身份证号码不正确
io.github.vipxieliang.validx.annotation.chinese.phone=手机号码格式不正确
io.github.vipxieliang.validx.annotation.email=邮箱地址格式不正确
io.github.vipxieliang.validx.annotation.date.format=日期格式不正确
```

### 2.2 注解的默认消息长什么样

每个注解的 `message()` 默认值就是一个 `{完全限定key}`：

```java
// ChineseIdCard.java
String message() default "{io.github.vipxieliang.validx.annotation.chinese.idcard}";

// Email.java
String message() default "{io.github.vipxieliang.validx.annotation.email}";

// Date.java
String message() default "{io.github.vipxieliang.validx.annotation.date.format}";
```

花括号中的 key 与 properties 文件的键一一对应。这就是"开箱即用"的来源：**你只要写上 `@Email`，报错文案就已经是 8 种语言的了。**

---

## 三、注解方式的国际化

### 3.1 不写 message，自动跟随语言环境

```java
public class UserDTO {
    @Email
    private String email;

    @ChineseIdCard
    private String idCard;
}
```

在中文系统环境下验证失败，得到"邮箱地址格式不正确"；在英文系统环境下，得到"Invalid email address format"。**同一行代码，零配置，消息自动切换。**

### 3.2 显式指定语言（Hibernate Validator 配置）

注意：`new ResourceBundleMessageInterpolator()` 只负责把 `{key}` 解析为具体文案，**语言仍由线程 / 默认 Locale 决定**，光配它并不能"固定"语言。要固定语言，正确做法是配合设置默认 Locale：

```java
Locale.setDefault(Locale.ENGLISH);   // 影响整个 JVM 的默认语言（生产环境慎用，注意还原）

ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator englishValidator = factory.getValidator();

Set<ConstraintViolation<UserDTO>> violations = englishValidator.validate(dto);
```

更推荐的做法：在 Spring 中配置固定的 `LocaleResolver`，或用 ValidX 链式 API 的 `withLocale()` / 线程级 `MessageManager.setCurrentLocale()`（见下文 §4），作用范围更可控。

### 3.3 Spring Boot：自动跟随 Accept-Language

在 Spring Boot 中，验证由 `LocalValidatorFactoryBean` 托管，Hibernate Validator 的消息插值会**读取当前线程的 Locale**，而 Spring 的 `LocaleResolver`（默认 `AcceptHeaderLocaleResolver`）会解析请求头 `Accept-Language` 设置线程 Locale。因此：

- 中文用户浏览器发 `Accept-Language: zh-CN` → 报错中文；
- 日文用户发 `Accept-Language: ja` → 报错日文；
- 无需在 Controller 里写任何语言判断代码。

### 3.4 局部覆盖：自己写 message

不想用默认文案时，直接写死或自定义 key 即可，覆盖优先级最高：

```java
public class UserDTO {
    // 硬编码覆盖
    @Email(message = "邮箱格式不对，请检查")
    private String email;

    // 自定义 key，放到自己的 ValidationMessages.properties 里
    @Email(message = "{myapp.msg.email}")
    private String email2;
}
```

> 标准 Bean Validation 注解（`@NotBlank`、`@Size` 等）的消息同样走这套机制，key 为 `jakarta.validation.constraints.NotBlank.message` 等，Hibernate Validator 自带英文默认值。

---

## 四、链式 API 的国际化

### 4.1 方式一：withLocale() 显式指定

```java
import java.util.Locale;

// 系统默认语言
ValidX chain1 = ValidX.init().isEmail("invalid-email");

// 显式中文
ValidX chain2 = ValidX.init()
        .withLocale(Locale.SIMPLIFIED_CHINESE)
        .isEmail("invalid-email");

// 显式英文
ValidX chain3 = ValidX.init()
        .withLocale(Locale.ENGLISH)
        .isEmail("invalid-email");

System.out.println(chain3.getErrorMessage());   // Invalid email address format
```

### 4.2 方式二：线程级语言环境（自动切换）

不想每次 `withLocale`，可以用 `MessageManager` 设置**当前线程**的语言，该线程后续所有验证自动跟随：

```java
import io.github.vipxieliang.validx.i18n.MessageManager;

// 设置当前线程语言为中文（影响本线程所有验证）
MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);

ValidX chain = ValidX.init().isEmail("invalid-email");
// chain.getErrorMessage() → 邮箱地址格式不正确

// 用完清理，避免线程池复用导致语言串台
MessageManager.clearCurrentLocale();
```

### 4.3 Locale 优先级：显式 > 线程级 > 系统默认

源码中 `ValidX.getLocale()` 的逻辑：

```java
private Locale getLocale() {
    if (locale != null) return locale;              // 1. withLocale 显式指定
    return MessageManager.getCurrentLocale();       // 2. 线程级 → 3. 系统默认
}
```

| 优先级 | 设置方式 | 作用范围 |
|:---:|---------|---------|
| 1 | `withLocale(Locale)` | 单次验证链 |
| 2 | `MessageManager.setCurrentLocale(Locale)` | 当前线程全部验证 |
| 3 | `Locale.getDefault()` | 全进程默认 |

### 4.4 错误消息获取 API

```java
ValidX validator = ValidX.init()
        .field("邮箱").isEmail("invalid-email")
        .field("电话").isPhoneNumber("123");

validator.passed();             // false，是否全部通过
validator.isValid();            // false，同上
validator.getErrors();          // ["邮箱: Invalid email address format", ...]，错误列表（副本）
validator.getErrorMessage();    // "邮箱: Invalid email address format, ..."，逗号拼接
```

---

## 五、MessageManager 核心机制拆解

`MessageManager` 是链式 API 国际化的枢纽（`src/main/java/io/github/vipxieliang/validx/i18n/MessageManager.java`）。四个关键设计：

### 5.1 三级回退：指定语言 → 英文 → key 本身

```java
public static String getMessage(String key, Locale locale) {
    try {
        ResourceBundle bundle = BUNDLES.computeIfAbsent(locale,
            l -> ResourceBundle.getBundle(BASE_NAME, l, UTF8_CONTROL));
        return bundle.getString(key);
    } catch (MissingResourceException e) {
        try {
            return DEFAULT_BUNDLE.getString(key);   // 二级：英文兜底
        } catch (MissingResourceException ex) {
            return key;                              // 三级：返回 key 本身
        }
    }
}
```

| 层级 | 条件 | 结果 |
|:---:|------|------|
| 1 | 指定语言包中有该 key | 对应语言的文案 |
| 2 | 指定语言包缺失 → 英文包 | 英文文案 |
| 3 | 英文包也缺失 | 返回 key 本身（不会抛异常、不会空指针） |

这意味着：**新加一个验证规则却漏配某个语言包时，最坏情况是用户看到 key 字符串，而不是系统崩溃。**

### 5.2 UTF8Control：强制 UTF-8 读取

Java 的 `PropertyResourceBundle` 默认按 ISO-8859-1 读取 properties，中文会乱码。`MessageManager` 内部类 `UTF8Control` 重写了 `newBundle()`，用 `InputStreamReader(stream, StandardCharsets.UTF_8)` 强制按 UTF-8 加载：

```java
return new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
```

因此语言包文件**既可以**以 UTF-8 明文保存中文/日文/韩文（UTF8Control 可直接读取），**也可以**沿用 Java properties 的 `\uXXXX` 转义格式（当前仓库 9 个语言包文件即为此格式）——两种写法 UTF8Control 都能正确加载。

### 5.3 中文不回退的细节

`UTF8Control` 还重写了 `getFallbackLocale()`：**对中文 locale 返回 null（不回退）**。这是为了防止"中文系统环境下 `zh` 包缺某个 key 时回退到默认英文包"——确保中文环境拿到的始终是中文文案（或三级回退的 key）。

### 5.4 缓存：ConcurrentHashMap

```java
private static final Map<Locale, ResourceBundle> BUNDLES = new ConcurrentHashMap<>();
// ...
BUNDLES.computeIfAbsent(locale, l -> ResourceBundle.getBundle(BASE_NAME, l, UTF8_CONTROL));
```

每个 Locale 的语言包只加载一次，后续直接命中缓存，多语言验证没有重复 IO 开销。`DEFAULT_BUNDLE`（英文）在类加载时就预加载，保证回退路径永远可用。

### 5.5 动态参数替换

`MessageManager` 只负责取静态文本，带参数的消息由调用方在取到文案后做替换，例如 `validateAge`：

```java
String message = MessageManager.getMessage("...annotation.age", locale);
message = message.replace("{min}", String.valueOf(minAge))
                 .replace("{max}", String.valueOf(maxAge));
```

---

## 六、实战场景

### 6.1 Web 接口：按用户语言返回报错

```java
@RestController
public class RegisterController {

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success();
    }
}
```

**注解方式**：Spring Boot 自动按 `Accept-Language` 切换，无需任何代码。

**链式方式**（比如验证动态 Map 数据时）：在请求入口读取用户语言，绑定到当前线程：

```java
@Component
public class RequestLocaleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String lang = req.getHeader("Accept-Language");
        if (lang != null && lang.startsWith("zh")) {
            MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
        } else if (lang != null && lang.startsWith("ja")) {
            MessageManager.setCurrentLocale(Locale.JAPANESE);
        } else {
            MessageManager.setCurrentLocale(Locale.ENGLISH);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            MessageManager.clearCurrentLocale();   // 清理，防止线程池串语言
        }
    }
}
```

### 6.2 纯 Java 工具类：不依赖 Spring

```java
public class CertNoCheckUtil {
    public static String checkCertNo(String certNo, Locale locale) {
        ValidX validator = ValidX.init()
                .withLocale(locale)
                .field("证件号").isChineseIdCard(certNo);
        return validator.isValid() ? "OK" : validator.getErrorMessage();
    }
}
```

### 6.3 前端拿到可展示的错误

```java
// 后端返回结构化错误，前端直接展示
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<List<String>> handleValid(MethodArgumentNotValidException e) {
    List<String> msgs = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)   // 已是当前语言
            .collect(Collectors.toList());
    return Result.fail(400, msgs);
}
```

---

## 七、测试保障：i18n 行为是锁死的

项目测试目录（`src/test/java/.../i18n/`）对国际化行为做了完整锁定，也是文章所有结论的验证依据：

### 7.1 语言包完整性测试

`DateValidatorI18nTest`、`DateTimeValidatorI18nTest` 断言：**8 种语言包里验证器级 key 都存在、非空，且中/英/日消息互不相同**——确保每个语言包都是"真翻译"，而不是全部回退英文充数。

### 7.2 Locale 优先级测试

`AutoLocaleValidationChainTest` 覆盖三种场景：

| 场景 | 设置 | 结果 |
|------|------|------|
| 不设置任何 Locale | — | 用系统默认语言 |
| 设置线程级 | `setCurrentLocale(zh)` | 中文消息 |
| 显式覆盖线程级 | `setCurrentLocale(zh)` + `withLocale(en)` | **英文**（显式优先） |

---

## 八、最佳实践清单

1. **注解方式零配置**：默认 `{key}` 消息已内置 8 种语言，别急着写死 `message`；
2. **局部覆盖用自定义 key**：需要自定义文案时，优先用 `"{myapp.xxx}"` 放进自己的 `ValidationMessages.properties`，而不是硬编码中文；
3. **Spring Boot 里用注解验证**：自动跟随 `Accept-Language`，不要自己写语言分支；
4. **链式 API 场景**：单次指定用 `withLocale()`；线程内统一用 `MessageManager.setCurrentLocale()`，**用完务必 `clearCurrentLocale()`**（尤其线程池环境）；
5. **新增语言包不要漏 key**：三级回退保证不崩，但漏 key 会让用户看到 key 字符串，上线前用测试遍历所有 key × 所有语言；
6. **语言包文件两种格式皆可**：`UTF8Control` 支持 UTF-8 明文与 `\uXXXX` 转义两种写法（仓库现有文件为转义格式），改动时与既有格式保持一致即可；
7. **优先级记牢**：`withLocale` > 线程级 > 系统默认，别让显式设置被覆盖。

---

## 总结

- ValidX 内置 **9 个语言包文件、8 种语言**（中文默认、英文兜底 + 日/韩/法/德/西/俄），注解与链式 API **共用一套消息体系**；
- 注解方式通过 `{完全限定key}` + Bean Validation 的 `MessageInterpolator` 解析，Spring Boot 下**自动跟随 `Accept-Language`**；
- 链式 API 通过 `MessageManager` 实现，`withLocale()` 显式指定、线程级 `setCurrentLocale()` 自动切换；
- 核心机制四件套：**三级回退**（指定语言→英文→key）、**UTF8Control**（强制 UTF-8）、**语言包缓存**（ConcurrentHashMap）、**中文不回退**（避免中文环境拿英文）；
- 国际化做得好的标志：**用户永远看到自己语言的可读报错，开发者永远不需要写语言分支。**

> ValidX 是基于 Jakarta Bean Validation 规范的 Java 验证库，注解与链式 API 双模式，内置 100+ 验证规则。项目地址：`github.com/vipxieliang/validx`
