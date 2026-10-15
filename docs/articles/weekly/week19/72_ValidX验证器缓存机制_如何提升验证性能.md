# ValidX 验证器缓存机制：如何提升验证性能

## 📋 目录
- [引言](#引言)
- [一、为什么验证需要缓存](#一为什么验证需要缓存)
  - [1.1 一次验证请求背后发生了什么](#11-一次验证请求背后发生了什么)
  - [1.2 没有缓存时的性能灾难](#12-没有缓存时的性能灾难)
  - [1.3 ValidX 的缓存哲学](#13-validx-的缓存哲学)
- [二、正则表达式预编译：最基础也最重要的缓存](#二正则表达式预编译最基础也最重要的缓存)
  - [2.1 Pattern.compile 的代价](#21-patterncompile-的代价)
  - [2.2 ValidX 的做法：static final Pattern](#22-validx-的做法static-final-pattern)
  - [2.3 反面教材：每次调用都编译](#23-反面教材每次调用都编译)
  - [2.4 性能实测](#24-性能实测)
- [三、静态数据表缓存：权重因子、校验码映射、行政区划](#三静态数据表缓存权重因子校验码映射行政区划)
  - [3.1 身份证校验：权重因子 + 校验码映射](#31-身份证校验权重因子--校验码映射)
  - [3.2 统一社会信用代码：字符值映射 + 加权因子](#32-统一社会信用代码字符值映射--加权因子)
  - [3.3 行政区划编码：枚举查找表](#33-行政区划编码枚举查找表)
  - [3.4 设计原则：不可变数据 + 静态初始化](#34-设计原则不可变数据--静态初始化)
- [四、国际化消息缓存：ResourceBundle 的 ConcurrentHashMap 策略](#四国际化消息缓存resourcebundle-的-concurrenthashmap-策略)
  - [4.1 问题：每次校验都要查错误消息](#41-问题每次校验都要查错误消息)
  - [4.2 ValidX 的方案：computeIfAbsent 懒加载](#42-validx-的方案computeifabsent-懒加载)
  - [4.3 ThreadLocal 与线程安全](#43-threadlocal-与线程安全)
- [五、验证器实例的生命周期管理](#五验证器实例的生命周期管理)
  - [5.1 注解模式：JSR-303 的验证器缓存](#51-注解模式jsr-303-的验证器缓存)
  - [5.2 链式 API 模式：模块实例复用](#52-链式-api-模式模块实例复用)
  - [5.3 两种模式的性能差异](#53-两种模式的性能差异)
- [六、缓存策略全景图](#六缓存策略全景图)
- [七、实战：如何编写高性能的自定义验证器](#七实战如何编写高性能的自定义验证器)
- [八、常见性能陷阱与规避](#八常见性能陷阱与规避)
- [九、总结](#九总结)
- [项目地址](#项目地址)

---

## 引言

当我们使用 `@ChineseIdCard`、`@ChinesePhone`、`@BankCard` 这些注解时，很少会去想：**每次校验背后到底发生了什么？**

一个身份证号码的校验，涉及正则匹配、省份编码查找、出生日期解析、mod 11 校验位计算——如果每一步都从零开始，在高并发场景下，验证本身就会成为性能瓶颈。

实际上，ValidX 内部大量使用了**缓存机制**来避免重复计算。从正则表达式的预编译、到静态数据表的初始化、再到国际化消息的懒加载，每一层缓存都经过精心设计。

本文从源码出发，深入剖析 ValidX 的 5 种缓存策略，帮你理解**为什么这样设计**，以及**如何在自己的自定义验证器中复用这些模式**。

---

## 一、为什么验证需要缓存

### 1.1 一次验证请求背后发生了什么

以身份证校验为例，一次完整的 `@ChineseIdCard` 校验涉及以下步骤：

```
┌──────────────────────────────────────────────────────────────┐
│  一次 @ChineseIdCard 校验的完整流程                           │
│                                                              │
│  ① 正则匹配：18 位格式校验 ─────────── 需要 Pattern 对象      │
│  ② 省份编码查找：前 2 位 → 省份 ────── 需要查找表             │
│  ③ 出生日期解析：第 7-14 位 → 日期 ─── 需要 SimpleDateFormat │
│  ④ 权重计算：前 17 位 × 权重因子 ───── 需要权重数组           │
│  ⑤ 校验码比对：mod 11 → 校验码 ─────── 需要校验码映射表       │
│                                                              │
│  如果每一步都重新创建对象，一次校验可能需要：                   │
│  · 1 次正则编译（最耗时）                                     │
│  · 2 次 HashMap 查找                                        │
│  · 1 次日期格式化器创建                                       │
│  · 数组遍历 + 数学计算                                       │
└──────────────────────────────────────────────────────────────┘
```

在低并发场景下，这些开销可以忽略不计。但在高并发场景下（如每秒 10 万次校验），**正则编译**会成为致命的性能瓶颈。

### 1.2 没有缓存时的性能灾难

假设每次校验都重新编译正则表达式：

```java
// 反面教材：每次调用都重新编译正则
public boolean isValid(String value) {
    // ❌ 每次调用都编译正则——极其昂贵！
    Pattern pattern = Pattern.compile("[0-9]{17}[0-9X]");
    return pattern.matcher(value).matches();
}
```

`Pattern.compile()` 的内部实现涉及：

1. **词法分析**：将正则字符串解析为 Token 序列；
2. **语法分析**：构建 AST（抽象语法树）；
3. **状态机生成**：将 AST 转换为 NFA/DFA 状态机；
4. **优化**：对状态机进行优化（如合并等价状态）。

这个过程通常需要 **0.1~1 毫秒**——对于一次内存操作来说，这是一个巨大的开销。

| 场景 | 每次校验耗时 | 10 万次/秒的 CPU 占用 |
|------|------------|---------------------|
| 预编译 Pattern（复用） | ~0.001ms | 约 1% CPU |
| 每次重新编译 Pattern | ~0.5ms | 约 500% CPU（需要 5 个核心） |

### 1.3 ValidX 的缓存哲学

ValidX 的缓存设计遵循三条原则：

| 原则 | 说明 | 典型应用 |
|------|------|---------|
| **编译期缓存** | 类加载时一次性初始化，运行时零开销 | `static final Pattern`、`static final Map` |
| **懒加载缓存** | 首次使用时加载，后续复用 | `ConcurrentHashMap` 缓存 ResourceBundle |
| **线程安全优先** | 缓存对象必须不可变或使用线程安全容器 | 不可变数组、`ConcurrentHashMap`、`ThreadLocal` |

---

## 二、正则表达式预编译：最基础也最重要的缓存

### 2.1 Pattern.compile 的代价

Java 的 `String.matches()` 方法看起来很方便，但它的内部实现是**每次都重新编译正则**：

```java
// String.matches() 的源码
public boolean matches(String regex) {
    return Pattern.matches(regex, this);
}

// Pattern.matches() 的源码
public static boolean matches(String regex, CharSequence input) {
    Pattern p = Pattern.compile(regex);  // ← 每次都编译！
    Matcher m = p.matcher(input);
    return m.matches();
}
```

这意味着 `value.matches("\\d{18}")` 每次调用都会编译一次正则——在循环或高频调用场景下，这是灾难性的。

### 2.2 ValidX 的做法：static final Pattern

ValidX 的所有正则校验器都遵循同一个模式——**将 Pattern 声明为 `static final` 常量**：

```java
// ✅ ValidX ChinesePhoneValidator 的实际写法
public class ChinesePhoneValidator implements ConstraintValidator<ChinesePhone, String> {

    // 预编译正则表达式以提高性能
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        String cleanValue = value.replaceAll("[\\s-]", "");
        return cleanValue.length() == 11 && MOBILE_PATTERN.matcher(cleanValue).matches();
    }
}
```

`static final` 保证了：

- **`static`**：Pattern 在类加载时编译一次，所有实例共享；
- **`final`**：引用不可变，不会被意外替换；
- **线程安全**：`Pattern` 对象本身是不可变的（immutable），天然线程安全。

### 2.3 反面教材：每次调用都编译

来看一个更极端的反面例子——在身份证校验器中：

```java
// ❌ 反面教材：在 isValid 内部使用 String.matches()
public boolean isValid(String value, ConstraintValidatorContext context) {
    // ...
    // 下面这行每次调用都会重新编译正则！
    if (!firstStr.matches("^\\d{17}")) {
        return false;
    }
    // ...
}
```

> **注意**：这是真实存在于某些版本代码中的写法。`String.matches()` 的便利性掩盖了底层的性能问题。在高并发场景下，应该替换为预编译的 `Pattern`。

**正确写法**：

```java
// ✅ 正确写法：预编译 Pattern
private static final Pattern DIGITS_17_PATTERN = Pattern.compile("^\\d{17}");

public boolean isValid(String value, ConstraintValidatorContext context) {
    // ...
    if (!DIGITS_17_PATTERN.matcher(firstStr).matches()) {
        return false;
    }
    // ...
}
```

### 2.4 性能实测

来看一个更复杂的例子——快递单号验证器。ValidX 支持 10 家快递公司，每家公司有独立的正则表达式：

```java
// ✅ ValidX ExpressNumberValidator 的实际写法
public class ExpressNumberValidator implements ConstraintValidator<ExpressNumber, Object> {

    // 10 个预编译的 Pattern——类加载时一次性编译
    private static final Pattern SF_EXPRESS_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern YTO_EXPRESS_PATTERN = Pattern.compile("^(YT\\d{11,13}|\\d{10,13})$", Pattern.CASE_INSENSITIVE);
    private static final Pattern STO_EXPRESS_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern ZTO_EXPRESS_PATTERN = Pattern.compile("^[A-Za-z0-9]{12,}$");
    private static final Pattern YUNDA_EXPRESS_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern EMS_PATTERN = Pattern.compile("^(E\\d{9}CN|[A-Z]{2}\\d{9}CN)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern JD_LOGISTICS_PATTERN = Pattern.compile("^JD\\d{13,15}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DEPPON_PATTERN = Pattern.compile("^\\d{8,9}$");
    private static final Pattern TTKD_EXPRESS_PATTERN = Pattern.compile("^\\d{12,14}$");
    private static final Pattern BEST_EXPRESS_PATTERN = Pattern.compile("^[A-Za-z0-9]{10,12}$");

    // ...
}
```

**10 个 Pattern 在类加载时编译一次**，之后每次校验只是做 `matcher().matches()`——纯内存操作，纳秒级完成。

| 方案 | 10 万次校验耗时（估算） | 说明 |
|------|---------------------|------|
| 预编译 10 个 Pattern | ~100ms | 类加载时编译一次，运行时复用 |
| 每次 `Pattern.compile()` 10 个 | ~5000ms | 每次校验编译 10 个正则 |
| 每次 `String.matches()` 10 次 | ~5000ms | 同上，底层实现一样 |

**性能差距约 50 倍**——这就是预编译的价值。

---

## 三、静态数据表缓存：权重因子、校验码映射、行政区划

### 3.1 身份证校验：权重因子 + 校验码映射

身份证校验位算法（mod 11）需要两组静态数据：

```
权重因子：[7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
校验码映射：余数 → 校验码 {0→"1", 1→"0", 2→"X", 3→"9", ...}
```

ValidX 的做法是将这两组数据声明为 `static` 常量，在类加载时初始化：

```java
// ✅ ValidX ChineseIdCardValidator 的实际写法
public class ChineseIdCardValidator implements ConstraintValidator<ChineseIdCard, String> {

    // 权重因子——静态常量，类加载时初始化一次
    public static final Integer[] idCardWeight = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};

    // 校验码——静态常量
    public static final String[] idCardCheck = {"1","0","X","9","8","7","6","5","4","3","2"};

    // 余数→校验码的映射表——静态初始化块中构建
    static Map<Integer,String> idCardMap = new HashMap<>();

    static {
        for(int i = 0; i < idCardCheck.length; i++){
            idCardMap.put(i, idCardCheck[i]);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // ... 直接使用 idCardWeight 和 idCardMap，无需重新创建
        char[] idCardCharNumber = firstStr.toCharArray();
        int resultSum = 0;
        for(int i = 0; i < idCardCharNumber.length; i++){
            resultSum += Character.getNumericValue(idCardCharNumber[i]) * idCardWeight[i];
        }
        int lastResult = resultSum % 11;
        return idCardMap.get(lastResult).equals(lastStr);
    }
}
```

**关键设计**：

- `idCardWeight` 和 `idCardCheck` 是**不可变数组**——声明后不会被修改；
- `idCardMap` 虽然是 `HashMap`（非线程安全），但在 `static` 块中初始化完成后**只读不写**——在 Java 内存模型中，`static` 块的写入 happens-before 任何线程的读取，因此是安全的。

### 3.2 统一社会信用代码：字符值映射 + 加权因子

统一社会信用代码的校验算法（mod 31）同样需要静态数据：

```java
// ✅ ValidX UnifiedSocialCreditCodeValidator 的实际写法
public class UnifiedSocialCreditCodeValidator implements ConstraintValidator<UnifiedSocialCreditCode, String> {

    // 字符→值映射——静态初始化
    private static final Map<Character, Integer> CODE_MAP = new HashMap<>();

    // 加权因子——静态常量
    private static final int[] WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};

    // 校验码字符集
    private static final char[] CHECK_CODES = "0123456789ABCDEFGHJKLMNPQRTUWXY".toCharArray();

    static {
        for (int i = 0; i < CHECK_CODES.length; i++) {
            CODE_MAP.put(CHECK_CODES[i], i);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        if (value.length() != 18) return false;

        // 直接使用预加载的 CODE_MAP 做字符合法性检查
        for (int i = 0; i < 18; i++) {
            if (!CODE_MAP.containsKey(value.charAt(i))) return false;
        }

        // 使用预加载的 WEIGHT 做加权求和
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += CODE_MAP.get(value.charAt(i)) * WEIGHT[i];
        }

        int remainder = sum % 31;
        int checkCodeIndex = 31 - remainder;
        if (checkCodeIndex == 31) checkCodeIndex = 0;

        return CHECK_CODES[checkCodeIndex] == value.charAt(17);
    }
}
```

**性能分析**：一次完整的统一社会信用代码校验涉及 18 次 `Map.get()` + 17 次乘法 + 1 次取模——全部是纯内存操作，没有对象创建，没有 IO。

### 3.3 行政区划编码：枚举查找表

身份证校验需要验证前 2 位是否为合法的省级行政区划编码。ValidX 使用枚举 + 静态 HashMap 实现 O(1) 查找：

```java
// ✅ ValidX ChinaProvince 枚举的实际写法
public enum ChinaProvince {
    BEIJING("11", "北京市"),
    TIANJIN("12", "天津市"),
    // ... 31 个省级行政区

    private final String code;
    private final String name;

    // 静态查找表——类加载时构建
    private static final Map<String, ChinaProvince> CODE_MAP = new HashMap<>();

    static {
        for (ChinaProvince province : values()) {
            CODE_MAP.put(province.code, province);
        }
    }

    // O(1) 查找
    public static Optional<ChinaProvince> fromCode(String code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }
}
```

**为什么不用 `switch` 或 `if-else`？**

| 方案 | 查找时间复杂度 | 可维护性 |
|------|-------------|---------|
| `if-else` 链 | O(n) | 差——每增加一个省份要改代码 |
| `switch` | O(1)（编译优化） | 中——同上 |
| 枚举 + HashMap | O(1) | 好——新增枚举值自动更新查找表 |

枚举 + 静态 HashMap 的组合兼顾了**性能**和**可维护性**。

### 3.4 设计原则：不可变数据 + 静态初始化

总结 ValidX 在静态数据缓存方面的设计原则：

```
┌──────────────────────────────────────────────────────────────┐
│  ValidX 静态数据缓存的设计原则                                │
│                                                              │
│  ① 数据不可变：声明为 static final（数组/Map/枚举查找表）     │
│  ② 初始化时机：static 块或字段初始化器（类加载时执行一次）    │
│  ③ 线程安全保证：                                           │
│     · 不可变对象天然线程安全                                 │
│     · static 块的写入 happens-before 任何线程的读取          │
│     · 初始化完成后只读不写                                   │
│  ④ 查找效率：HashMap O(1)、数组索引 O(1)、枚举 O(1)         │
└──────────────────────────────────────────────────────────────┘
```

---

## 四、国际化消息缓存：ResourceBundle 的 ConcurrentHashMap 策略

### 4.1 问题：每次校验都要查错误消息

每次校验失败时，ValidX 需要获取对应语言的错误消息。ValidX 支持 9 种语言（中文、英文、日文、韩文、法文、德文、西班牙文、俄文），如果每次校验都重新加载语言包文件，IO 开销会非常大。

### 4.2 ValidX 的方案：computeIfAbsent 懒加载

ValidX 的 `MessageManager` 使用 `ConcurrentHashMap` + `computeIfAbsent` 实现 ResourceBundle 的懒加载缓存：

```java
// ✅ ValidX MessageManager 的实际写法
public class MessageManager {
    private static final String BASE_NAME = "ValidationMessages";

    // 缓存：Locale → ResourceBundle
    private static final Map<Locale, ResourceBundle> BUNDLES = new ConcurrentHashMap<>();

    // 默认 Bundle（英文）——类加载时初始化
    private static final ResourceBundle DEFAULT_BUNDLE =
        ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH, new UTF8Control());

    public static String getMessage(String key, Locale locale) {
        try {
            // computeIfAbsent：首次访问时加载，后续复用缓存
            ResourceBundle bundle = BUNDLES.computeIfAbsent(locale,
                l -> ResourceBundle.getBundle(BASE_NAME, l, new UTF8Control()));
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // 回退到默认英文包
            try {
                return DEFAULT_BUNDLE.getString(key);
            } catch (MissingResourceException ex) {
                return key;  // 最终回退：返回 key 本身
            }
        }
    }
}
```

**关键设计**：

- **`ConcurrentHashMap`**：线程安全的 Map，支持并发读写；
- **`computeIfAbsent`**：原子操作——如果 key 不存在，计算 value 并放入；如果已存在，直接返回缓存值。避免了竞态条件；
- **懒加载**：不是启动时加载所有 9 种语言，而是首次使用时才加载——减少启动时间。

```
首次请求中文消息：
  BUNDLES.computeIfAbsent(Locale.SIMPLIFIED_CHINESE, ...)
  → 缓存未命中 → 加载 ValidationMessages_zh.properties → 缓存

后续请求中文消息：
  BUNDLES.computeIfAbsent(Locale.SIMPLIFIED_CHINESE, ...)
  → 缓存命中 → 直接返回 → 零 IO
```

### 4.3 ThreadLocal 与线程安全

`MessageManager` 还提供了 `ThreadLocal` 支持，用于设置当前线程的语言环境：

```java
// ✅ 线程安全的 Locale 管理
private static final ThreadLocal<Locale> CURRENT_LOCALE = new ThreadLocal<Locale>() {
    @Override
    protected Locale initialValue() {
        return null;  // 默认使用系统语言
    }
};

public static void setCurrentLocale(Locale locale) {
    CURRENT_LOCALE.set(locale);
}

public static Locale getCurrentLocale() {
    Locale locale = CURRENT_LOCALE.get();
    return locale != null ? locale : Locale.getDefault();
}

public static void clearCurrentLocale() {
    CURRENT_LOCALE.remove();  // ← 重要：防止线程池场景下的内存泄漏
}
```

> **`remove()` 的重要性**：在使用线程池（如 Tomcat 的 Worker 线程）的场景下，如果不调用 `remove()`，线程复用时 `ThreadLocal` 中残留的 Locale 会影响后续请求。这是经典的 **ThreadLocal 内存泄漏**问题。

---

## 五、验证器实例的生命周期管理

### 5.1 注解模式：JSR-303 的验证器缓存

在注解模式下，ValidX 遵循 **JSR-303/380 规范**的验证器生命周期：

```
┌──────────────────────────────────────────────────────────────┐
│  JSR-303 验证器生命周期                                       │
│                                                              │
│  ValidatorFactory                                            │
│    └── Validator                                             │
│          └── ConstraintValidator 实例（按注解类型缓存）       │
│                ├── initialize(annotation)  ← 调用一次         │
│                └── isValid(value, context) ← 每次校验调用     │
│                                                              │
│  同一个 @ChineseIdCard 注解对应的 Validator 实例被缓存复用    │
│  不会每次校验都创建新实例                                     │
└──────────────────────────────────────────────────────────────┘
```

这意味着：

- `initialize()` 方法**只调用一次**——适合做初始化操作（如读取注解参数）；
- `isValid()` 方法**每次校验都调用**——必须是线程安全的；
- 验证器实例被 `ValidatorFactory` 缓存——不会重复创建。

这就是为什么 ValidX 的验证器可以将 `Pattern` 声明为 `static final`——因为即使有多个实例，它们共享同一份 `static` 数据。

### 5.2 链式 API 模式：模块实例复用

ValidX 的链式 API 采用了不同的设计——**模块实例复用**：

```java
// ✅ ValidX 链式 API 的实际架构
public class ValidX {

    // 10 个验证模块——作为实例字段，随 ValidX 对象一起创建
    private final ChinaValidation chinaValidation = new ChinaValidation();
    private final NetworkValidation networkValidation = new NetworkValidation();
    private final BaseValidation baseValidation = new BaseValidation();
    private final FinancialValidation financialValidation = new FinancialValidation();
    private final VehicleValidation vehicleValidation = new VehicleValidation();
    private final BookValidation bookValidation = new BookValidation();
    private final EducationValidation educationValidation = new EducationValidation();
    private final ForeignValidation foreignValidation = new ForeignValidation();
    private final PhoneValidation phoneValidation = new PhoneValidation();
    private final CertificationValidation certificationValidation = new CertificationValidation();

    private ValidX() {}

    public static ValidX init() {
        return new ValidX();  // 创建时自动初始化 10 个模块
    }
}
```

每个模块（如 `ChinaValidation`）是一个**无状态的协调器**——它不包含校验逻辑，而是创建具体的 Validator 来执行校验：

```java
// ChinaValidation 的写法
public class ChinaValidation {

    public void validateIdCard(Object value, List<String> errors, Locale locale) {
        // 每次调用创建新的 Validator 实例
        ChineseIdCardValidator validator = new ChineseIdCardValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage(
                "io.github.vipxieliang.validx.annotation.chinese.idcard", locale));
        }
    }
}
```

> **为什么要每次创建新实例？** 因为链式 API 场景下，Validator 不需要 `initialize()` 步骤（参数直接传入 `isValid()`），创建实例的开销极小（只是分配一个空对象）。而真正的"重资源"（Pattern、Map 等）都是 `static` 的，所有实例共享。

### 5.3 两种模式的性能差异

| 维度 | 注解模式 | 链式 API 模式 |
|------|---------|-------------|
| Validator 实例 | 被 ValidatorFactory 缓存，复用 | 每次调用创建新实例 |
| 初始化开销 | `initialize()` 调用一次 | 无 `initialize()` 步骤 |
| 静态资源 | 共享 `static final Pattern/Map` | 共享 `static final Pattern/Map` |
| 对象分配 | 无（复用） | 极小（空对象） |
| 适用场景 | 声明式校验（DTO 注解） | 编程式校验（动态逻辑） |

**关键认知**：两种模式的核心性能开销（正则编译、数据表查找）都通过 `static` 缓存解决了。实例创建的差异在纳秒级，对总体性能影响微乎其微。

---

## 六、缓存策略全景图

把 ValidX 的所有缓存策略汇总成一张图：

```
┌──────────────────────────────────────────────────────────────────────┐
│                     ValidX 缓存策略全景                              │
│                                                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  编译期缓存（类加载时初始化，零运行时开销）                   │   │
│  │                                                             │   │
│  │  · static final Pattern ─── 正则表达式预编译                │   │
│  │    示例：ChinesePhoneValidator.MOBILE_PATTERN               │   │
│  │    示例：ExpressNumberValidator.SF_EXPRESS_PATTERN (×10)    │   │
│  │    示例：ChineseIdCardValidator.FIFTEEN_ID_PATTERN          │   │
│  │                                                             │   │
│  │  · static final 数组/Map ── 权重因子、校验码映射            │   │
│  │    示例：ChineseIdCardValidator.idCardWeight (17个整数)     │   │
│  │    示例：ChineseIdCardValidator.idCardMap (11个映射)        │   │
│  │    示例：UnifiedSocialCreditCodeValidator.WEIGHT (17个整数) │   │
│  │    示例：UnifiedSocialCreditCodeValidator.CODE_MAP (31个)   │   │
│  │                                                             │   │
│  │  · static 枚举查找表 ────── O(1) 编码查找                   │   │
│  │    示例：ChinaProvince.CODE_MAP (31个省份)                  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  懒加载缓存（首次使用时加载，后续复用）                       │   │
│  │                                                             │   │
│  │  · ConcurrentHashMap ──── ResourceBundle 缓存               │   │
│  │    示例：MessageManager.BUNDLES (最多 9 个 Locale)          │   │
│  │    机制：computeIfAbsent 原子操作                            │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  框架级缓存（JSR-303 ValidatorFactory 管理）                 │   │
│  │                                                             │   │
│  │  · ConstraintValidator 实例缓存                             │   │
│  │    机制：同一注解类型复用同一实例                             │   │
│  │    效果：initialize() 只调用一次                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  线程级缓存（ThreadLocal）                                   │   │
│  │                                                             │   │
│  │  · 当前线程 Locale ──── MessageManager.CURRENT_LOCALE       │   │
│  │    机制：ThreadLocal，线程隔离                               │   │
│  │    注意：必须在使用完毕后 remove()，防止内存泄漏             │   │
│  └─────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 七、实战：如何编写高性能的自定义验证器

基于 ValidX 的缓存设计模式，总结编写高性能自定义验证器的最佳实践：

### 7.1 正则表达式：永远预编译

```java
// ✅ 推荐：预编译 Pattern
public class MyValidator implements ConstraintValidator<MyAnnotation, String> {
    private static final Pattern MY_PATTERN = Pattern.compile("^\\d{6}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return MY_PATTERN.matcher(value).matches();
    }
}

// ❌ 避免：每次编译
public boolean isValid(String value, ConstraintValidatorContext context) {
    return value.matches("^\\d{6}$");  // 内部每次调用 Pattern.compile()
}
```

### 7.2 查找表：静态初始化

```java
// ✅ 推荐：静态 Map 查找
public class CodeValidator implements ConstraintValidator<Code, String> {
    private static final Map<String, String> VALID_CODES = new HashMap<>();

    static {
        VALID_CODES.put("A", "Type A");
        VALID_CODES.put("B", "Type B");
        // ...
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return VALID_CODES.containsKey(value);
    }
}
```

### 7.3 日期格式化器：复用 vs 线程安全

`SimpleDateFormat` 是**线程不安全**的——ValidX 在 `ChineseIdCardValidator` 中使用了 `static` 的 `SimpleDateFormat`：

```java
// ⚠️ 注意：SimpleDateFormat 不是线程安全的
private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

static {
    DATE_FORMAT.setLenient(false);
}
```

在多线程并发调用 `DATE_FORMAT.parse()` 时，可能出现解析错误。解决方案有三种：

| 方案 | 线程安全 | 性能 | 推荐度 |
|------|---------|------|-------|
| `ThreadLocal<SimpleDateFormat>` | ✅ | 高 | ⭐⭐⭐ |
| `DateTimeFormatter`（Java 8+） | ✅ | 高 | ⭐⭐⭐⭐⭐ |
| 每次 `new SimpleDateFormat()` | ✅ | 低 | ⭐⭐ |

**推荐方案**：使用 Java 8 的 `DateTimeFormatter`——它是**不可变的、线程安全的**：

```java
// ✅ 最佳方案：DateTimeFormatter 不可变、线程安全
private static final DateTimeFormatter DATE_FORMATTER =
    DateTimeFormatter.ofPattern("yyyyMMdd").withResolverStyle(ResolverStyle.STRICT);
```

### 7.4 完整示例：高性能验证器模板

```java
public class HighPerformanceValidator implements ConstraintValidator<MyAnnotation, String> {

    // ① 预编译正则
    private static final Pattern PATTERN = Pattern.compile("^[A-Z]{2}\\d{6}$");

    // ② 静态查找表
    private static final Set<String> VALID_PREFIXES = new HashSet<>(Arrays.asList(
        "AB", "CD", "EF", "GH"
    ));

    // ③ 线程安全的日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void initialize(MyAnnotation annotation) {
        // 只在初始化时调用一次——适合做参数处理
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;  // 空值交给 @NotNull 处理
        }

        // ④ 快速失败：先做最便宜的检查
        if (value.length() != 8) {
            return false;
        }

        // ⑤ 正则匹配（使用预编译的 Pattern）
        if (!PATTERN.matcher(value).matches()) {
            return false;
        }

        // ⑥ 查找表检查（O(1)）
        String prefix = value.substring(0, 2);
        if (!VALID_PREFIXES.contains(prefix)) {
            return false;
        }

        // ⑦ 日期校验（使用线程安全的 DateTimeFormatter）
        try {
            LocalDate.parse(value.substring(2), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }
}
```

---

## 八、常见性能陷阱与规避

### 8.1 陷阱一：在 isValid() 中创建 Pattern

```java
// ❌ 每次调用都编译正则
public boolean isValid(String value, ConstraintValidatorContext context) {
    Pattern p = Pattern.compile(regex);  // 正则来自注解参数
    return p.matcher(value).matches();
}

// ✅ 在 initialize() 中预编译
private Pattern compiledPattern;

public void initialize(MyAnnotation annotation) {
    this.compiledPattern = Pattern.compile(annotation.pattern());
}

public boolean isValid(String value, ConstraintValidatorContext context) {
    return compiledPattern.matcher(value).matches();
}
```

### 8.2 陷阱二：在 isValid() 中读取文件

```java
// ❌ 每次校验都读取文件
public boolean isValid(String value, ConstraintValidatorContext context) {
    List<String> blacklist = Files.readAllLines(Paths.get("blacklist.txt"));
    return blacklist.contains(value);
}

// ✅ 在 initialize() 或 static 块中加载
private static final Set<String> BLACKLIST;

static {
    try {
        BLACKLIST = new HashSet<>(Files.readAllLines(Paths.get("blacklist.txt")));
    } catch (IOException e) {
        throw new ExceptionInInitializerError(e);
    }
}

public boolean isValid(String value, ConstraintValidatorContext context) {
    return BLACKLIST.contains(value);
}
```

### 8.3 陷阱三：忽略 ThreadLocal 清理

```java
// ❌ 没有清理 ThreadLocal
public void validate(Object data) {
    MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
    // ... 校验逻辑
    // 忘记调用 clearCurrentLocale()！
}

// ✅ 在 finally 块中清理
public void validate(Object data) {
    try {
        MessageManager.setCurrentLocale(Locale.SIMPLIFIED_CHINESE);
        // ... 校验逻辑
    } finally {
        MessageManager.clearCurrentLocale();  // ← 确保清理
    }
}
```

### 8.4 陷阱四：用 String.matches() 做高频校验

```java
// ❌ 便利但慢——每次编译正则
if (value.matches("\\d{18}")) { ... }

// ✅ 预编译 Pattern
private static final Pattern DIGITS_18 = Pattern.compile("\\d{18}");
if (DIGITS_18.matcher(value).matches()) { ... }
```

### 8.5 陷阱五：缓存了可变对象

```java
// ❌ 缓存了可变的 HashMap——可能被意外修改
private static final Map<String, String> CACHE = new HashMap<>();

static {
    CACHE.put("A", "value_a");
}

// 其他代码可能意外修改 CACHE
// CACHE.put("B", "value_b");  // 不应该！

// ✅ 使用 Collections.unmodifiableMap 包装
private static final Map<String, String> CACHE;

static {
    Map<String, String> temp = new HashMap<>();
    temp.put("A", "value_a");
    CACHE = Collections.unmodifiableMap(temp);
}
```

---

## 九、总结

### 核心知识点回顾

| 缓存类型 | 机制 | 适用场景 | 性能提升 |
|---------|------|---------|---------|
| **Pattern 预编译** | `static final Pattern` | 所有正则校验 | 50~100 倍 |
| **静态数据表** | `static final Map/数组` | 权重因子、校验码映射、编码查找 | 10~20 倍 |
| **ResourceBundle 缓存** | `ConcurrentHashMap.computeIfAbsent` | 国际化消息 | 避免重复 IO |
| **Validator 实例缓存** | JSR-303 ValidatorFactory | 注解模式 | 避免重复创建 |
| **ThreadLocal** | `ThreadLocal<Locale>` | 线程级语言环境 | 无锁线程隔离 |

### ValidX 缓存设计的核心原则

> **能用 `static final` 解决的，不要用运行时缓存；能用编译期初始化的，不要延迟到运行时。**

这条原则的本质是：**把开销从"每次"降低到"一次"**。

| 层级 | 策略 | 开销频率 |
|------|------|---------|
| 类加载时 | `static final Pattern/Map/数组` | **一次**（JVM 启动） |
| 首次使用时 | `ConcurrentHashMap.computeIfAbsent` | **一次**（首次访问） |
| 每次调用时 | `new Validator()` / `new HashMap()` | **每次**（应该避免） |

### 性能优化检查清单

在编写自定义验证器时，对照以下清单检查：

- [ ] 正则表达式是否使用了 `static final Pattern`？
- [ ] 是否避免了 `String.matches()`？
- [ ] 查找表是否在 `static` 块中初始化？
- [ ] 缓存的数据是否不可变（或 `unmodifiable`）？
- [ ] `SimpleDateFormat` 是否替换为 `DateTimeFormatter`？
- [ ] `ThreadLocal` 是否在使用后 `remove()`？
- [ ] `initialize()` 中是否做了可以提前计算的初始化？
- [ ] `isValid()` 中是否避免了对象创建和 IO 操作？

遵循这些原则，你的验证器在高并发场景下的性能可以提升**一到两个数量级**。


## 项目地址

- GitHub: <https://github.com/vipxieliang/validx>
- Gitee: <https://gitee.com/vipxieliang/validx>
