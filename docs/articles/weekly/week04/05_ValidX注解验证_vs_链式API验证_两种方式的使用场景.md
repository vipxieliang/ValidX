# ValidX注解验证 vs 链式API验证：两种方式的使用场景

## 引言

ValidX 提供两套验证方式：

```java
// 方式一：注解验证（声明式）
public class RegisterDTO {
    @NotBlank(message = "手机号不能为空")
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;
}

// 方式二：链式 API（命令式）
ValidX validator = ValidX.init()
        .field("手机号").isChinesePhone(phone);
```

同一套规则，两种写法，都能完成验证。但也正因如此，很多刚接触 ValidX 的开发者会困惑：**到底该用哪种？两套都有，是不是重复建设？**

本文不吹捧任何一种，而是把两种方式的适用场景摊开对比，最后给出一套可以直接照抄的决策框架。

---

## 一、两种方式的本质区别

### 1.1 注解验证：声明式

注解验证把"验证规则"写在**数据模型的字段上**，规则与数据结构绑定：

```java
@Data
public class UserCreateDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度需在 2-20 位之间")
    private String username;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
}
```

验证由框架在**合适的时机自动触发**（如 Spring 的 `@Valid`），你不需要写任何调用代码。它回答的问题是：**"这个对象应该是怎样的"**。

### 1.2 链式 API：命令式

链式 API 把验证规则写成**一段按顺序执行的代码**，规则与调用点绑定：

```java
public void validateRegistration(Map<String, Object> data) {
    ValidX validator = ValidX.init()
            .field("邮箱").isEmail(data.get("email"))
            .field("手机号").isChinesePhone(data.get("phone"))
            .field("身份证").isChineseIdCard(data.get("idCard"));

    if (!validator.passed()) {
        throw new BusinessException(validator.getErrors());
    }
}
```

验证在**你主动调用的那一刻执行**，你可以自己决定何时、对谁、按什么顺序验证。它回答的问题是：**"这个值此刻是否满足要求"**。

方法参数不一定是 DTO——单个参数、基本类型、多个分散参数，链式 API 同样就地校验；多个参数都需要必填时，用全局配置最省事：

```java
public Order createOrder(String orderNo, String receiverPhone, String expressNo) {
    ValidX validator = ValidX.init()
            .config(ValidXConfig.GLOBAL_NOT_EMPTY)  // 三个参数均必填：拒绝 null 和空串
            .field("订单号").isAlphaNumber(orderNo)
            .field("收货手机号").isChinesePhone(receiverPhone)
            .field("快递单号").isExpressNumber(expressNo);

    if (!validator.isValid()) {
        throw new BusinessException(validator.getErrors());
    }
    // 业务逻辑...
}
```

> `config(GLOBAL_NOT_EMPTY)` 作用于本次验证的所有字段；个别参数可选时用 `allowNull()` 局部覆盖，优先级：**局部 > 全局 > 默认**。

### 1.3 一张总表

| 对比维度 | 注解验证 | 链式 API |
|---------|---------|---------|
| 编程范式 | 声明式 | 命令式 |
| 规则载体 | 类字段 | 任意代码位置 |
| 触发时机 | 框架自动（`@Valid` 等） | 调用时手动 |
| 适用数据 | POJO 的字段 | 任意值（含 Map、JSON、方法参数） |
| 规则是否可动态 | 编译期写死 | 可动态构建（条件分支） |
| 必填控制 | `@NotBlank` / `@NotEmpty` | `notNull()` / `notEmpty()` / `config()` |
| 跨字段校验 | 困难（需类级注解） | 简单（任意组合） |
| 错误处理 | 框架统一抛异常 | `getErrors()` 手动收集 |
| 分组支持 | `groups` 原生支持 | 无，需自己用 `if` 模拟 |
| 依赖 Spring | 依赖 Bean Validation 集成 | 完全无依赖，纯 Java 可用 |
| 可读性 | 规则随数据定义，一目了然 | 规则随逻辑走，灵活但有样板 |

本质结论：**注解是"数据契约"，链式是"过程规则"。** 前者管"数据结构长什么样"，后者管"这次调用要求什么"。

---

## 二、注解验证的使用场景

### 2.1 场景一：Spring Boot 请求 DTO（最主流）

Web 接口的请求体是 POJO，字段结构稳定、规则明确，这是注解验证的主场：

```java
@RestController
public class UserController {

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserCreateDTO dto) {
        // Spring 自动完成全部校验，失败抛出 MethodArgumentNotValidException
        return userService.register(dto);
    }
}
```

配合全局异常处理器统一返回错误信息，Controller 里看不到一行验证代码。

### 2.2 场景二：分组验证（同一 DTO 不同规则）

创建和更新共用同一个 DTO，但必填规则不同——注解的 `groups` 特性天然支持：

```java
@Data
public class UserDTO {

    public interface CreateGroup {}
    public interface UpdateGroup {}

    @NotBlank(groups = CreateGroup.class, message = "创建时用户名必填")
    @Size(min = 2, max = 20)
    private String username;

    @Null(groups = UpdateGroup.class, message = "更新时 ID 不能修改")
    private Long id;
}
```

```java
@PostMapping("/create")
public Result create(@Validated(UserDTO.CreateGroup.class) @RequestBody UserDTO dto) { ... }

@PutMapping("/update")
public Result update(@Validated(UserDTO.UpdateGroup.class) @RequestBody UserDTO dto) { ... }
```

> 注意：指定分组必须用 Spring 的 `@Validated`，标准 `@Valid` 不支持传分组参数。

### 2.3 场景三：嵌套对象与集合

DTO 里嵌套对象、List、Map 的复杂结构，注解可以递归、逐元素校验：

```java
public class OrderCreateDTO {
    @NotBlank
    private String orderNo;

    @Valid                       // 触发嵌套校验
    private AddressDTO address;

    @NotEmpty
    @Size(max = 100)
    private List<@Valid @Positive Long> skuIds;   // 元素级校验
}
```

这类结构用链式 API 写会非常啰嗦，注解是唯一合理的选择。

### 2.4 场景四：非 Spring 环境手动触发

即使没有 Spring，注解依然可用——通过标准 Bean Validation API 手动触发：

```java
Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(dto);

if (!violations.isEmpty()) {
    String msg = violations.stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining("; "));
    // 处理错误
}
```

### 2.5 注解方式的边界

- **只能写在字段上**：Map 的 key、方法返回值、临时变量都无处安放注解；
- **规则静态**：同一字段的规则在编译期固定，无法"这次必填、下次可选"（除非分组，但分组也是有限的几套）；
- **跨字段校验困难**：如"两次密码必须一致"这种规则，标准注解做不到，需要自定义类级约束。

---

## 三、链式 API 的使用场景

### 3.1 场景一：动态数据结构（Map / JSON）

数据来自外部系统、配置中心或第三方回调时，往往是 Map 或 JSON，字段可能缺失。字段都不存在，注解自然无从谈起：

```java
public void validateCallback(Map<String, Object> payload) {
    ValidX validator = ValidX.init()
            .field("订单号").isAlphaNumber(payload.get("orderNo"))
            .field("身份证").isChineseIdCard(payload.get("idCard"))
            .field("手机号（可选）").allowNull().isChinesePhone(payload.get("phone"));

    if (!validator.passed()) {
        throw new BusinessException(validator.getErrors());
    }
}
```

`.field("标签")` 让错误消息带上字段名，`allowNull()` 表达"字段可能不存在"——这两点都是链式 API 处理动态数据的核心能力。

### 3.2 场景二：条件驱动的动态规则

规则本身依赖运行时条件时，链式 API 可以直接用代码表达：

```java
public void validateUser(UserPO user) {
    ValidX validator = ValidX.init()
            .field("邮箱").isEmail(user.getEmail());

    // 仅企业用户要求统一社会信用代码
    if (user.getUserType() == UserType.ENTERPRISE) {
        validator.field("统一社会信用代码")
                .isUnifiedSocialCreditCode(user.getCreditCode());
    }

    // 仅 APP 渠道要求 IMEI
    if ("APP".equals(user.getChannel())) {
        validator.field("IMEI").isIMEI(user.getImei());
    }

    if (!validator.passed()) {
        throw new BusinessException(validator.getErrors());
    }
}
```

这是注解方式做不到的：注解只能靠分组表达"编译期写死的几套规则"，而链式 API 可以把 `if`、`switch`、循环任意编织进验证逻辑。

### 3.3 场景三：文件批量导入（Excel / CSV）

用户上传 Excel、CSV，或从外部文件批量同步数据时，每一行都是一个独立的校验单元：**失败只应跳过该行并记录行号，而不是中断整个导入**。行数据是数组或 `Map`，没有类可以挂注解；而链式 API 每次校验都是独立实例、错误各自收集，正好匹配逐行处理的模式：

```java
public class ExcelImportService {
    public List<String> importRows(List<String[]> rows) {
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            ValidX validator = ValidX.init()
                    .field("手机号").isChinesePhone(row[3])
                    .field("身份证").isChineseIdCard(row[4]);

            if (!validator.passed()) {
                // 行号 + 字段名 + 具体原因，收集起来最后统一回显给用户
                errors.add("第 " + (i + 1) + " 行：" + validator.getErrorMessage());
                continue;   // 跳过脏数据行，不中断整个导入
            }
            saveRow(row);   // 写入合法行
        }
        return errors;
    }
}
```

`field()` 让错误带上字段名，`getErrorMessage()` 汇总本次该行的全部错误，行号由循环提供——三者组合，导出的错误报告天然就是"第 N 行：手机号格式不正确；身份证校验失败"这样的可读格式，用户对照 Excel 就能定位修改。

### 3.4 场景四：历史数据清洗（定时任务）

存量数据里往往积累着历史脏数据：手机号录入错误、身份证过期、邮箱乱填。用定时任务把历史数据捞出来逐条校验，合规的做补全、重算，不合规的标记待人工处理。调度框架不限——xxl-job、Quartz、Spring 自带的 `@Scheduled` 都可以，校验逻辑本身与调度框架无关：

```java
@Component
public class DataCleanJob {

    // 定时触发入口：xxl-job / Quartz / Spring @Scheduled 均可，此处省略调度注解
    public void clean() {
        List<UserPO> users = userMapper.selectAll();   // 查询历史数据
        for (UserPO user : users) {
            ValidX validator = ValidX.init()
                    .field("手机号").isChinesePhone(user.getPhone())
                    .field("身份证").isChineseIdCard(user.getIdCard());

            if (validator.passed()) {
                enrich(user);   // 数据合规：执行补全、重算等清洗动作
            } else {
                user.setStatus(Status.DIRTY);  // 标记脏数据，待人工处理
                log.warn("用户 {} 数据不合规: {}", user.getId(), validator.getErrorMessage());
            }
        }
        userMapper.batchUpdate(users);
    }
}
```

与文件导入不同，**清洗 ≠ 拒绝**：导入对脏行是"跳过"，而清洗往往是"标记后继续"——同一套链式 API 两种用法都能表达，区别只在于 `if/else` 分支的处理逻辑。

### 3.5 场景五：无 Spring 的纯 Java 环境

工具类、定时任务、批处理、命令行程序，以及 Micronaut、Quarkus 等非 Spring 框架，注解验证依赖 Bean Validation 集成，未必开箱可用；链式 API 不需要任何框架支持，`init()` 即用：

```java
public class IdCardCheckUtil {
    // 工具方法：任意调用点直接校验，无需 Spring 容器
    public static boolean isValidIdCard(String idCard) {
        return ValidX.init()
                .field("身份证").isChineseIdCard(idCard)
                .isValid();
    }
}
```

> 注解方式在纯 Java 环境也能用（标准 Bean Validation API 手动触发），但要先建 `ValidatorFactory`、再 `validate()`、再遍历 `ConstraintViolation`，样板代码明显更多；链式 API 则是开箱即用。

### 3.6 链式方式的边界

- **样板代码较多**：每次都要 `init()` + 逐条调用 + 判断 `passed()`；
- **没有"数据契约"**：规则不随数据结构走，可读性依赖调用处的组织水平；
- **无分组**：同一字段在不同流程的不同规则，得靠 `if` 自己维护。

---

## 四、决策框架：一张表选对

遇到具体需求时，按下面的顺序自问：

| 自问 | 是 → 用 | 否 → |
|------|--------|------|
| 规则是写在 POJO 字段上吗？ | 注解 | 链式 |
| 数据结构动态（Map/JSON）吗？ | 链式 | 注解 |
| 需要创建/更新分组不同规则吗？ | 注解（`groups`） | 链式 |
| 规则依赖运行时条件（if/switch）吗？ | 链式 | 注解 |
| 需要跨字段校验吗？ | 链式（或自定义类级约束） | 注解 |
| 需要逐条批量处理（文件导入、历史清洗）吗？ | 链式 | 注解 |
| 应用没有 Spring 吗？ | 链式 | 注解 |

浓缩成一句话：

> **字段结构稳定 → 注解；数据形态动态 → 链式；分组需求 → 注解；条件规则 → 链式。**

两套方式不是互斥的——**同一项目里混用是常态**：Controller 入口用注解管 DTO，Service 内部用链式管动态数据与条件规则，各司其职。

---

## 五、两种方式共用的最佳实践

### 5.1 注解与链式共用同一套规则引擎

两套方式底层调用的是**同一批验证器**（`ChinesePhoneValidator`、`IdCardValidator` 等），规则完全一致，不存在"注解通过、链式不通过"的规则漂移。这也是混用安全的前提。

### 5.2 链式 API 的线程安全：每次新建实例

`ValidX` 实例持有错误列表，是**有状态**的，绝不能做成静态单例共享：

```java
// ❌ 错误：静态共享有状态实例，并发下错误列表互相污染
@Service
public class UserService {
    private static final ValidX VALIDATOR = ValidX.init();

    public void validate(User user) {
        VALIDATOR.isEmail(user.getEmail());  // 线程不安全！
    }
}

// ✅ 正确：每次校验创建新实例（与 Stream、StringBuilder 同模式）
@Service
public class UserService {
    public void validate(User user) {
        ValidX validator = ValidX.init()
                .field("邮箱").isEmail(user.getEmail())
                .field("手机号").isChinesePhone(user.getPhone());
        if (!validator.isValid()) {
            throw new BusinessException(validator.getErrors());
        }
    }
}
```

### 5.3 必填语义：两种方式都要显式声明

ValidX 的格式注解对 `null` / 空串**默认放行**（遵循 JSR-380），"必填"必须显式表达：

| 方式 | 必填写法 |
|------|---------|
| 注解 | `@NotBlank` + `@ChinesePhone` |
| 链式 | `notEmpty().isChinesePhone(value)` 或全局 `config(GLOBAL_NOT_EMPTY)` |

### 5.4 国际化：注解自动，链式手动指定

- 注解方式：错误消息由 `ValidationMessages_*.properties` 自动按 `Accept-Language` 切换；
- 链式方式：用 `withLocale(Locale.ENGLISH)` 指定本次校验的语言：

```java
ValidX validator = ValidX.init()
        .withLocale(Locale.ENGLISH)
        .field("Phone").isChinesePhone(phone);
```

---

## 六、总结

| | 注解验证 | 链式 API |
|--|---------|---------|
| **一句话** | 数据契约 | 过程规则 |
| **最适用** | 请求 DTO、嵌套结构、分组 | Map/JSON、条件规则、文件导入、历史清洗、纯 Java |
| **最不适** | 动态结构、跨字段、无字段可挂 | 字段结构稳定的标准 DTO |
| **触发** | 框架自动 | 手动调用 |

选择没有对错，只有合不合适。**规则长在数据上就用注解，规则长在逻辑上就用链式。**

如果你正在用 ValidX 写验证，建议先问一句：这条规则属于"数据的形状"，还是"本次调用的要求"？答案会直接告诉你该用哪种方式。

> ValidX 是面向中国业务场景的 Java 验证库，基于 Jakarta Bean Validation 规范，注解与链式 API 双模式，内置身份证、手机号、邮箱、姓名等 100+ 验证规则。项目地址：`github.com/vipxieliang/validx`
