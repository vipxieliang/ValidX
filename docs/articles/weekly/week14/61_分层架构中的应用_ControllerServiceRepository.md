# ValidX 在分层架构中的应用：Controller、Service、Repository

## 📋 目录
- [概述](#概述)
- [一、分层架构中的"验证"到底归哪一层](#一分层架构中的验证到底归哪一层)
- [二、Controller 层：注解 + `@Valid` 拦截入口格式](#二controller-层-注解--valid-拦截入口格式)
- [三、Service 层：链式 API 处理动态规则与跨字段校验](#三service-层链式-api-处理动态规则与跨字段校验)
- [四、Repository 层：不做应用层校验，但保留数据库约束作为最后防线](#四repository-层不做应用层校验但保留数据库约束作为最后防线)
- [五、分组验证：Create / Update / Query 各取所需](#五分组验证create--update--query-各取所需)
- [六、完整实战：用户管理模块](#六完整实战用户管理模块)
- [七、统一异常拦截：把验证错误包装成标准响应](#七统一异常拦截把验证错误包装成标准响应)
- [八、各层之间的数据契约与边界规则](#八各层之间的数据契约与边界规则)
- [九、容易踩的坑与反模式](#九容易踩的坑与反模式)
- [总结](#总结)
- [项目地址](#项目地址)

---

## 概述

任何一个 Java 后端项目搭起来之后，遇到的第一个工程问题往往是："**这条用户输入，到底应该在哪一层校验？**"

- 全部塞进 Controller？一个表单动辄几十个字段，方法签名长到换行；
- 全部塞进 Service？注解用不起来，`@Valid` 不生效，每个字段都要手写一遍；
- 在 Repository 层用 SQL `CHECK` 约束？错误信息又长又难翻译，500 抛给用户。

正确的做法不是"选哪一层"，而是**让每一层做自己最擅长的事**：

| 层 | 验证手段 | 职责 |
|----|---------|------|
| Controller | `@Valid` + ValidX 注解 | 拦截**格式**错误的请求（边界检查） |
| Service | ValidX 链式 API + 业务规则 | 拦截**业务**逻辑错误（跨字段、动态、外部依赖） |
| Repository | 数据库约束（兜底） | 防止**绕过应用层**的脏数据落库 |

本文用 ValidX 给出一个完整可落地的三层验证方案：每层用什么 API、处理什么场景、不处理什么场景，并给出一个"用户管理模块"的端到端示例——从 Controller 的 DTO 校验，到 Service 的链式业务规则，到 Repository 的最后防线。

> 本文对照 ValidX v1.2.x 源码与 Bean Validation 2.0 (`javax.validation`) 标准实践编写。

---

## 一、分层架构中的"验证"到底归哪一层

### 1.1 先把"验证"拆开

**"验证"其实不是一个动作，而是四件性质完全不同的事**：

| 类别 | 含义 | 例子 | 适合放在哪一层 |
|------|------|------|--------------|
| **格式校验** | 字符串长什么样 | 邮箱是不是合法格式、手机号是不是 11 位、身份证校验位 | Controller |
| **必填校验** | 字段是否提供 | 姓名必填、密码必填 | Controller |
| **业务规则校验** | 数据是否合理 | "用户名不能与密码相同"、"提现金额不能超过余额"、"下单时间必须在营业时间内" | Service |
| **完整性校验** | 是否违反数据库结构约束 | 唯一键冲突、外键不存在、`ENUM` 越界 | Repository（兜底） |

把"格式 / 必填"放到 Controller 是最便宜的——这一层只要看见不合法的请求就拒掉，根本不会让请求进 Service、查数据库、走 RPC。

### 1.2 三层职责对照

```
┌───────────────────────────────────────────────────────────────────┐
│  ① Controller 层                                                   │
│     工具：@Valid + ValidX 注解（@ChinesePhone / @Email / @QQ ...）   │
│     职责：格式、必填、字符集、长度                                  │
│     失败响应：400 + 字段级错误列表（自动）                          │
├───────────────────────────────────────────────────────────────────┤
│  ② Service 层                                                      │
│     工具：ValidX 链式 API（isChinesePhone / isEmail / isQQ ...）    │
│     职责：跨字段规则、外部依赖查询后的动态校验、依赖上下文的判断    │
│     失败响应：抛业务异常 → 业务错误码（40001/40002 等）              │
├───────────────────────────────────────────────────────────────────┤
│  ③ Repository 层                                                   │
│     工具：数据库约束（UNIQUE / NOT NULL / CHECK / FOREIGN KEY）     │
│     职责：兜底，防止绕过应用层的脏数据                              │
│     失败响应：DataIntegrityViolationException → 友好翻译            │
└───────────────────────────────────────────────────────────────────┘
```

**关键认知**：这三层不是"互斥"，而是"接力"。Controller 拒掉的请求不会再到 Service；Service 拒掉的请求不会再到 Repository；Repository 兜底的脏数据理论上不该到达——但出现时一定要让系统**安全失败**，而不是把脏数据偷偷落库。

### 1.3 反模式速查

| 反模式 | 问题 |
|--------|------|
| 只在 Controller 校验，Repository 不加任何约束 | 后台脚本、Job 直接调 Repository 落库时无任何防线 |
| 只在 Service 校验，Controller 不校验 | 大量无效请求穿透到 Service，白白消耗 CPU、查数据库、占连接 |
| Repository 用应用层校验替代数据库约束 | 多服务共享一个库、A 服务的 Service 没校验就调 B 服务的 Repository，脏数据照样进库 |
| 业务规则写在 Controller | 跨字段规则一旦依赖"数据库查询"或"时间"，Controller 就无法承担（不应持有 DAO） |

---

## 二、Controller 层：注解 + `@Valid` 拦截入口格式

### 2.1 三件套：DTO + `@Valid` + ValidX 注解

Controller 层最标准的写法是 **Bean Validation** 体系。ValidX 的所有注解都符合 `javax.validation.Constraint` 规范，可以和 `@Valid` / `@Validated` 直接配合：

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor()
public class UserController {

    private final UserService userService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateUserRequest request) {
        // 到这一行时，request 里的字段已经全部校验通过
        Long userId = userService.create(request);
        return Result.ok(userId);
    }
}
```

`CreateUserRequest` 用 ValidX 注解做"格式 + 必填"校验：

```java
public class CreateUserRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度需在 4-20 位之间")
    @ChineseAlphaDash(message = "用户名仅支持中文、字母、数字、下划线和短横线")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "身份证号不能为空")
    @ChineseIdCard(message = "身份证号格式不正确")
    private String idCard;

    @NotBlank(message = "密码不能为空")
    @Password(minLength = 8, requireSpecialChar = true,
              message = "密码至少 8 位，包含大小写字母、数字和特殊字符")
    private String password;
}
```

校验失败时 Spring 自动抛 `MethodArgumentNotValidException`，由全局异常处理器统一返回（详见第七节）。

### 2.2 为什么格式校验要在 Controller 完成

四件事是 Controller 层"擅长做、且应该做"的：

1. **拦截畸形请求** —— 邮箱没 `@`、手机号 10 位、身份证 14 位、密码 5 位：这些请求根本不需要进 Service 才"发现"它是脏数据。
2. **错误可被框架自动序列化** —— Spring MVC + `@Valid` 自动把字段级错误整理成 `BindingResult`，前端能拿到结构化的错误信息直接展示给用户。
3. **节省后续层的开销** —— Service 查一次用户、调用一次 RPC、查一次缓存都是成本，畸形请求被 Controller 提前拒掉，这些成本根本不会发生。
4. **代码即文档** —— 注解直接挂在 DTO 字段上，新人 review 一眼就能看出"这个接口需要哪些字段、各自的格式约束"。

### 2.3 `@RequestBody` 与 `@RequestParam` / `@PathVariable` 的差异

很多人写 Controller 时只对 `@RequestBody` 校验，忽略了 `@RequestParam` 和 `@PathVariable`：

```java
// ❌ 错误：路径变量未启用校验
@GetMapping("/{id}")
public UserDTO get(@PathVariable Long id) {
    return userService.getById(id);   // id = -1、id = 9999999999 都能进
}

// ✅ 正确：在 Controller 类上启用 @Validated，路径变量也能校验
@RestController
@RequestMapping("/api/users")
@Validated   // 关键：让 @PathVariable/@RequestParam 的注解生效
public class UserController {

    @GetMapping("/{id}")
    public UserDTO get(@PathVariable @Min(1) Long id) {
        return userService.getById(id);
    }

    @GetMapping("/search")
    public List<UserDTO> search(@RequestParam @Size(max = 50) String keyword) {
        return userService.search(keyword);
    }
}
```

注意 `@Validated` 加在类上才能让方法参数上的注解生效（Spring 的设计，与 Bean Validation 标准 `@Valid` 不完全等价）。

### 2.4 空值与可选字段的处理

很多接口里"邮箱"是可选的（手机号登录的用户没邮箱）。这时正确写法是：

```java
public class UpdateProfileRequest {

    @Size(max = 50)
    private String nickname;       // 昵称可选

    @Email(message = "邮箱格式不正确")
    private String email;          // 邮箱可选（null 通过校验）

    @Size(min = 11, max = 11)
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;          // 手机号可选，但填了就必须合法
}
```

关键认知：**Bean Validation 默认不校验 `null` 值**（这是规范规定的）。所以"邮箱可选"自然实现——`null` 不触发 `@Email`；"邮箱填了必须合法"也自然实现——非 null 时会触发 `@Email`。

如果想要"必填 + 格式"，叠一层 `@NotBlank` 即可。

---

## 三、Service 层：链式 API 处理动态规则与跨字段校验

### 3.1 为什么 Service 层需要链式 API

Controller 层的注解无法承担三类校验：

| 场景 | 为什么注解做不了 | 例子 |
|------|----------------|------|
| 跨字段规则 | 注解只能挂在单个字段上 | "密码不能与用户名相同"、"起止日期范围"、"身份证与出生日期一致" |
| 依赖外部查询 | 注解执行时还没有上下文 | "用户名未被占用"、"邮箱未注册"、"提现 ≤ 余额" |
| 业务上下文判断 | 规则随场景变化 | "VIP 用户密码强度可降低"、"营业时间外拒绝下单" |

这些都该在 Service 层用 **ValidX 链式 API** 处理。

### 3.2 三种典型 Service 层校验场景

#### 场景 1：跨字段规则

```java
@Service
@RequiredArgsConstructor()
public class UserService {

    private final UserRepository userRepository;

    public Long create(CreateUserRequest req) {
        // Controller 已经验过格式，这里验业务规则
        ValidX chain = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

        // 规则 1：用户名不能与密码相同
        if (req.getUsername().equalsIgnoreCase(req.getPassword())) {
            chain.field("用户名").fail("用户名不能与密码相同");
        }

        // 规则 2：用户名不能是保留字
        if (RESERVED_USERNAMES.contains(req.getUsername().toLowerCase())) {
            chain.field("用户名").fail("该用户名为系统保留字");
        }

        if (!chain.passed()) {
            throw new BizException(chain.getErrors());
        }
        // ... 落库逻辑
    }
}
```

#### 场景 2：依赖数据库查询的动态校验

"用户名未被占用"是经典的动态规则——必须在查库之后才能判断：

```java
public Long create(CreateUserRequest req) {
    // 唯一性校验
    ValidX chain = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

    if (userRepository.existsByUsername(req.getUsername())) {
        chain.field("用户名").fail("该用户名已被占用");
    }
    if (userRepository.existsByEmail(req.getEmail())) {
        chain.field("邮箱").fail("该邮箱已被注册");
    }
    if (userRepository.existsByPhone(req.getPhone())) {
        chain.field("手机号").fail("该手机号已被注册");
    }
    if (userRepository.existsByIdCard(req.getIdCard())) {
        chain.field("身份证号").fail("该身份证号已实名");
    }

    if (!chain.passed()) {
        throw new BizException(chain.getErrors());
    }
    // ... 落库逻辑
}
```

注解无法做"先查再验"——查询 DB 是有副作用的，必须在 Service 层用代码表达。

#### 场景 3：依赖外部上下文

```java
public void updatePassword(Long userId, String oldPwd, String newPwd) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new BizException("用户不存在"));

    // 规则：新密码不能与最近 3 次用过的密码相同
    List<String> recentPasswords = passwordHistoryRepository.findRecentHashes(userId, 3);
    for (String oldHash : recentPasswords) {
        if (passwordEncoder.matches(newPwd, oldHash)) {
            throw new BizException("新密码不能与最近 3 次用过的密码相同");
        }
    }

    // 规则：VIP 用户可以设弱密码（这是业务上下文判断）
    if (!user.isVip()) {
        ValidX chain = ValidX.init()
                .withLocale(Locale.SIMPLIFIED_CHINESE)
                .field("新密码")
                .isPassword(newPwd, 8);   // 非 VIP 必须 8 位 + 四要素
        if (!chain.passed()) {
            throw new BizException(chain.getErrors());
        }
    }
    // ... 改密逻辑
}
```

### 3.3 ValidX 链式 API 在 Service 层的优势

| 优势 | 说明 |
|------|------|
| **依赖注入查询结果** | 链式代码里可以直接调用 `userRepository.findXxx()` 拿到的对象 |
| **动态分支** | `if (user.isVip())` 这种条件分支用注解完全无法表达 |
| **复用单字段校验** | 链式的 `isEmail()` / `isChinesePhone()` / `isQQ()` 与注解是同一套规则，**不会出现"注解和链式两边规则不一致"** |
| **错误自动累积** | `chain.getErrors()` 一行拿到所有错误，不需要手动 `add()` |
| **可与控制器层共享配置** | 国际化、`field()` 标签、消息键全部一致 |

### 3.4 一个关键原则：**不要把格式校验下放到 Service**

很多人图省事，把所有验证都丢到 Service 层用链式 API。问题是：

- 前端拿不到结构化的字段级错误（注解的 `BindingResult` 是结构化的）；
- Service 跑了一堆无意义的逻辑（已经查出数据库才知道"邮箱格式不对"）；
- 事务回滚成本更高（很多 Service 方法带 `@Transactional`，无效请求也会触发事务回滚）。

正确的分工是：**Controller 拦"格式错"，Service 拦"业务错"**。

---

## 四、Repository 层：不做应用层校验，但保留数据库约束作为最后防线

### 4.1 Repository 层"不"做应用层校验

Repository 层在分层架构中只承担数据访问职责，**不应该**调用 ValidX 或任何业务规则：

```java
// ❌ 错误：Repository 里写业务规则
@Repository
public class UserRepository {

    public User save(User user) {
        // 不该在这里做"用户名唯一"检查
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BizException("用户名重复");
        }
        return userRepository.save(user);
    }
}

// ✅ 正确：Repository 只做持久化
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    // ... 仅查询方法
}
```

为什么不让 Repository 承担校验？

| 原因 | 说明 |
|------|------|
| **绕过问题** | 后台 Job、批处理脚本、定时任务、数据导入工具可能直接调用 Repository，绕过 Service |
| **事务边界** | 唯一性检查 + 写入必须在**同一事务**内才有意义，但跨多个写入源时这一约束很难维持 |
| **测试困难** | Repository 测试应只关心数据落库是否正确，不应混入业务规则 |
| **性能问题** | Repository 频繁调用时，业务规则执行反而成性能瓶颈 |

### 4.2 但必须有数据库约束兜底

**真正能在所有路径下生效的"最后一道防线"，是数据库约束**：

```sql
CREATE TABLE users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL,
    email        VARCHAR(100) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    id_card      VARCHAR(32)  NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at   DATETIME     NOT NULL,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_id_card (id_card)
);
```

或者用 JPA 的字段注解：

```java
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_email",    columnNames = "email"),
           @UniqueConstraint(name = "uk_phone",    columnNames = "phone"),
           @UniqueConstraint(name = "uk_id_card",  columnNames = "id_card")
       })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    // ...
}
```

这一层兜底的意义是：**即使 Service 校验漏掉了某一路径、或者后台脚本绕过了 Service，数据库依然会拒绝脏数据**。代价是错误信息不友好，需要在异常处理器里翻译。

### 4.3 应用层校验 vs 数据库约束的边界

| 维度 | 应用层（Service） | 数据库约束 |
|------|------------------|-----------|
| **响应速度** | 毫秒级 | 通常更快（无需回滚事务） |
| **错误信息** | 业务友好（中文、可定位字段） | 原始异常（需要翻译） |
| **多端入口保护** | 仅 Service 路径 | **所有路径**（包括直接调 Repository 的脚本） |
| **跨字段规则** | ✅ 支持 | ❌ 单字段为主 |
| **动态规则** | ✅ 支持 | ❌ 不支持 |
| **跨服务一致性** | 各服务重复实现 | **天然一致**（库就是真理之源） |

**结论**：两者不是替代，而是**互补**。Service 校验负责"快速反馈 + 业务友好"，数据库约束负责"最终一致性 + 兜底安全"。

### 4.4 数据库异常的统一翻译

数据库约束抛出的 `DataIntegrityViolationException` 是不友好的——通常长这样：

```
Duplicate entry 'zhangsan' for key 'uk_username'
```

需要在异常处理器里翻译：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null) {
            if (msg.contains("uk_username")) {
                return Result.fail(409, "用户名已被占用");
            }
            if (msg.contains("uk_email")) {
                return Result.fail(409, "邮箱已被注册");
            }
            if (msg.contains("uk_phone")) {
                return Result.fail(409, "手机号已被注册");
            }
            if (msg.contains("uk_id_card")) {
                return Result.fail(409, "身份证号已实名");
            }
        }
        return Result.fail(500, "数据冲突");
    }
}
```

**更好的做法**是用错误码常量 + 异常码映射，避免字符串 `contains` 的脆弱匹配（生产环境数据库错误信息可能因驱动而异）。

---

## 五、分组验证：Create / Update / Query 各取所需

### 5.1 为什么需要分组

Create 和 Update 用的是同一个 DTO，但规则往往不同：

| 场景 | 规则 |
|------|------|
| 创建用户 | 必填字段：用户名、密码、邮箱、手机号；密码强度校验 |
| 更新用户 | 上述字段全部**不**必填；密码**不能**在这个接口里改 |

如果一个 DTO 通吃，Create 时 `password` 必填没问题，Update 时用户没填密码却要求必填就不对了。Bean Validation 的**分组（groups）**机制正是为这个场景设计的。

### 5.2 定义分组接口

分组在 Bean Validation 中是**空接口（Marker Interface）**：

```java
public final class Groups {

    private Groups() {}

    /** 创建场景 */
    public interface Create {}

    /** 更新场景 */
    public interface Update {}

    /** 查询场景 */
    public interface Query {}
}
```

### 5.3 在 DTO 上声明分组

```java
public class UserDTO {

    @NotNull(groups = Groups.Create.class, message = "用户ID不能为空")
    private Long id;   // Update / Query 必填，Create 不参与（生成策略决定）

    @NotBlank(groups = Groups.Create.class, message = "用户名不能为空")
    @Size(min = 4, max = 20, groups = {Groups.Create.class, Groups.Update.class},
          message = "用户名长度需在 4-20 位之间")
    @ChineseAlphaDash(groups = {Groups.Create.class, Groups.Update.class},
                      message = "用户名仅支持中文、字母、数字、下划线和短横线")
    private String username;

    @NotBlank(groups = Groups.Create.class, message = "邮箱不能为空")
    @Email(groups = {Groups.Create.class, Groups.Update.class},
           message = "邮箱格式不正确")
    private String email;

    @NotBlank(groups = Groups.Create.class, message = "手机号不能为空")
    @ChinesePhone(groups = {Groups.Create.class, Groups.Update.class},
                  message = "手机号格式不正确")
    private String phone;

    @NotBlank(groups = Groups.Create.class, message = "密码不能为空")
    @Password(minLength = 8, requireSpecialChar = true,
              groups = Groups.Create.class,
              message = "密码至少 8 位，包含大小写字母、数字和特殊字符")
    private String password;   // Update 接口绝不能带上这个字段
}
```

ValidX 的注解都遵循标准 `Class<?>[] groups() default {}` 设计，所以可以无缝对接分组。

### 5.4 在 Controller 中触发分组

```java
@PostMapping
public Result<Long> create(
        @Validated(Groups.Create.class) @RequestBody UserDTO request) {
    // 只跑 groups = Create.class 的注解
    return Result.ok(userService.create(request));
}

@PutMapping
public Result<Void> update(
        @Validated(Groups.Update.class) @RequestBody UserDTO request) {
    // 只跑 groups = Update.class 的注解
    userService.update(request);
    return Result.ok();
}
```

### 5.5 分组的进阶用法：组序列

Bean Validation 还支持**组序列（GroupSequence）**——按顺序校验，前一组失败就不再跑后一组：

```java
@GroupSequence({Groups.First.class, Groups.Second.class, Groups.Default.class})
public interface OrderedCheck {}
```

适用场景：

- "格式"组失败时直接返回，不再跑"业务"组（业务组可能依赖数据库，节省 IO）；
- "必填"组失败时不再跑"格式"组（既然字段为空，格式校验无意义）；
- "权限"组失败时不再跑后续组。

这个能力常用于**复杂接口**（如大表单），普通 CRUD 接口用 Create/Update 两个分组足够。

---

## 六、完整实战：用户管理模块

把上面的所有内容整合，给出一个完整的"用户管理"三层验证示例。

### 6.1 目录结构

```
src/main/java/com/example/user/
├── controller/
│   └── UserController.java           ← @Valid + ValidX 注解
├── service/
│   └── UserService.java              ← ValidX 链式 API
├── repository/
│   ├── UserRepository.java           ← 仅数据访问
│   └── entity/User.java              ← 数据库约束
└── dto/
    ├── CreateUserRequest.java
    ├── UpdateUserRequest.java
    └── UserDTO.java
```

### 6.2 Controller 层

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor()
public class UserController {

    private final UserService userService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateUserRequest req) {
        return Result.ok(userService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable @Min(1) Long id,
                                @Valid @RequestBody UpdateUserRequest req) {
        req.setId(id);    // id 由路径决定，合并到 DTO
        userService.update(req);
        return Result.ok();
    }

    @GetMapping("/{id}")
    public Result<UserDTO> get(@PathVariable @Min(1) Long id) {
        return Result.ok(userService.getById(id));
    }
}
```

### 6.3 DTO 与注解

```java
public class CreateUserRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度需在 4-20 位之间")
    @ChineseAlphaDash(message = "用户名仅支持中文、字母、数字、下划线和短横线")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "手机号不能为空")
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "身份证号不能为空")
    @ChineseIdCard(message = "身份证号格式不正确")
    private String idCard;

    @NotBlank(message = "密码不能为空")
    @Password(minLength = 8, requireSpecialChar = true,
              message = "密码至少 8 位，包含大小写字母、数字和特殊字符")
    private String password;
}
```

```java
public class UpdateUserRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Size(min = 4, max = 20, message = "用户名长度需在 4-20 位之间")
    @ChineseAlphaDash(message = "用户名仅支持中文、字母、数字、下划线和短横线")
    private String username;     // 可选

    @Email(message = "邮箱格式不正确")
    private String email;        // 可选

    @Size(min = 11, max = 11)
    @ChinesePhone(message = "手机号格式不正确")
    private String phone;        // 可选

    @Min(1) @Max(150)
    private Integer age;         // 可选，年龄有范围
}
```

注意 Create 用 `@NotBlank` 强制必填，Update 用 `@Size`/`@Email` 仅做格式——Update 不传字段就跳过校验，传了就必须合法。这是 Bean Validation"null 默认不校验"带来的天然好处。

### 6.4 Service 层

```java
@Service
@RequiredArgsConstructor()
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long create(CreateUserRequest req) {
        // —— Service 层校验：跨字段、依赖查询的规则 ——
        ValidX chain = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

        if (req.getUsername().equalsIgnoreCase(req.getPassword())) {
            chain.field("用户名").fail("用户名不能与密码相同");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            chain.field("用户名").fail("该用户名已被占用");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            chain.field("邮箱").fail("该邮箱已被注册");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            chain.field("手机号").fail("该手机号已被注册");
        }
        if (userRepository.existsByIdCard(req.getIdCard())) {
            chain.field("身份证号").fail("该身份证号已实名");
        }

        if (!chain.passed()) {
            throw new BizException(chain.getErrors());
        }

        // —— 业务落库 ——
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setIdCard(req.getIdCard());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user).getId();
    }

    public void update(UpdateUserRequest req) {
        User user = userRepository.findById(req.getId())
                .orElseThrow(() -> new BizException("用户不存在"));

        // Service 层校验：用户名变更时不能撞库
        if (req.getUsername() != null
                && !req.getUsername().equals(user.getUsername())
                && userRepository.existsByUsernameExcluding(req.getUsername(), req.getId())) {
            throw new BizException("用户名已被他人占用");
        }
        // ... 其他字段更新 + 落库
    }

    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::from)
                .orElseThrow(() -> new BizException("用户不存在"));
    }
}
```

### 6.5 Repository 层 + 数据库约束

```java
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_email",    columnNames = "email"),
           @UniqueConstraint(name = "uk_phone",    columnNames = "phone"),
           @UniqueConstraint(name = "uk_id_card",  columnNames = "id_card")
       })
public class User {
    // 字段同 6.3，省略
}
```

```java
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByIdCard(String idCard);
    boolean existsByUsernameExcluding(String username, Long excludeId);
}
```

Service 校验 + 数据库约束**双重保护**：正常路径下 Service 拦截并友好返回；万一后台脚本、并发竞态、Service 漏校验，数据库约束兜底。

### 6.6 完整调用链路

```
POST /api/users
  ↓ Spring MVC 反序列化 + @Valid
  ↓ CreateUserRequest 的 @NotBlank / @Email / @ChinesePhone 等全部跑一遍
  ↓ 任一失败 → MethodArgumentNotValidException → 400 + 字段错误列表
  ↓ 全部通过 → 进入 UserController.create()
  ↓ 调用 userService.create(req)
  ↓ Service 层：ValidX 链式 API 跑"用户名不能与密码相同""邮箱未被占用"等
  ↓ 任一失败 → BizException → 业务错误码 40001 等
  ↓ 全部通过 → UserRepository.save()
  ↓ 数据库 UNIQUE 约束兜底 → DataIntegrityViolationException → 友好翻译
```

---

## 七、统一异常拦截：把验证错误包装成标准响应

### 7.1 Controller 层注解失败

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** @Valid / @RequestBody 失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleBodyValidation(MethodArgumentNotValidException ex) {
        BindingResult binding = ex.getBindingResult();
        List<FieldError> fieldErrors = binding.getFieldErrors();
        List<String> messages = fieldErrors.stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        return Result.fail(400, "参数校验失败").data(messages);
    }

    /** @PathVariable / @RequestParam 失败 */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleParamValidation(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
        return Result.fail(400, "参数校验失败").data(messages);
    }
}
```

### 7.2 Service 层业务校验失败

Service 层抛 `BizException`，对应拦截：

```java
@ExceptionHandler(BizException.class)
public Result<?> handleBiz(BizException ex) {
    return Result.fail(ex.getCode(), ex.getMessage());
}
```

业务错误码可以设计成 `40001` "用户名已被占用"、`40002` "邮箱已被注册" 这种语义化编号——前端可以根据错误码做差异化展示。

### 7.3 数据库约束兜底失败

见 4.4 节。关键点是**错误信息翻译**——数据库原始错误不可见用户。

---

## 八、各层之间的数据契约与边界规则

### 8.1 不要把 Service 对象暴露给 Controller

很多新手把 Service 返回的 `User` 实体直接 `@ResponseBody` 返回，结果：

- `passwordHash`、`createdBy`、`updatedBy` 这些敏感字段被序列化出去；
- 修改实体类加了字段后，接口契约也跟着变，前端无法预期；
- 无法做"对外 DTO / 对内 Entity"的字段映射，权限隔离、字段脱敏都做不了。

正确做法：

```
Controller  ← CreateUserRequest / UpdateUserRequest / UserDTO  （入参/出参 DTO）
Service     ← User 实体 / 内部 VO  （业务对象）
Repository  ← User 实体              （数据库对象）
```

DTO 是接口契约，Entity 是业务内部对象，两者**不能混用**。

### 8.2 各层验证职责对照表

| 验证类型 | Controller | Service | Repository | 备注 |
|---------|------------|---------|-----------|------|
| **格式**（邮箱、手机号、身份证） | ✅ | ✗ | ✗ | 拦截畸形请求 |
| **必填** | ✅ | ✗ | ✗ | 拦截空请求 |
| **长度 / 范围** | ✅ | ✗ | ✗ | 拦截超界 |
| **跨字段规则** | ✗ | ✅ | ✗ | Service 才看得到多个字段 |
| **唯一性 / 存在性** | ✗ | ✅ | ✗ | Service 才查得到库 |
| **外部依赖（RPC / 配置）** | ✗ | ✅ | ✗ | Controller 不持有这些 |
| **业务上下文** | ✗ | ✅ | ✗ | 上下文在 Service 才完整 |
| **数据库约束兜底** | ✗ | ✗ | ✅ | 防止绕过应用层 |
| **权限 / 角色** | ✗ | ✅ | ✗ | 鉴权是 Service 的事 |

### 8.3 一个常见争议：唯一性校验放哪

"用户名唯一"既出现在 Service（`existsByUsername`）又出现在数据库（`UNIQUE` 约束），看起来重复。**这不是冗余，而是分层**：

- Service 校验：快速、友好，能在事务提交前发现，给前端友好提示；
- 数据库约束：兜底所有路径，处理并发竞态（两个请求同时 INSERT，Service 都看到"不存在"，都通过了校验，但数据库 UNIQUE 约束保证只有一个能成功）。

这两层缺一不可。

---

## 九、容易踩的坑与反模式

### 9.1 注解只挂在 Controller 的 Service 接口上

```java
// ❌ 错误：Service 接口参数用了 @Valid，但 Controller 没启 @Valid
public interface UserService {
    Long create(@Valid CreateUserRequest req);   // 这条注解不会生效
}

// ✅ 正确：Controller 上加 @Valid / @Validated
@PostMapping
public Result<Long> create(@Valid @RequestBody CreateUserRequest req) {
    return Result.ok(userService.create(req));
}
```

Service 接口上的 `@Valid` 用于**Service 间调用**（一个 Service 调另一个 Service），与 Controller 的 `@Valid` 是两个层级的概念。

### 9.2 Service 校验失败抛 `IllegalArgumentException`

```java
// ❌ 错误：用 JDK 内置异常，错误信息语义不清
if (userRepository.existsByUsername(req.getUsername())) {
    throw new IllegalArgumentException("用户名已存在");
}

// ✅ 正确：抛业务异常，全局拦截统一处理
throw new BizException(40001, "用户名已被占用");
```

`IllegalArgumentException` 会触发 Spring 的 500 处理器，对用户极不友好。

### 9.3 把所有字段都 `@NotNull` / `@NotBlank`

有些团队图省事，把所有字段都加 `@NotBlank`，结果 Update 接口根本无法用：

```java
// ❌ 错误：Update 也要求 password 必填
public class UpdateUserRequest {
    @NotBlank private String password;   // Update 时没传就报错
}

// ✅ 正确：Create 必填，Update 不必填，用两个 DTO
//   或者用 @Null(groups = Update.class) 显式说明"Update 时密码必须为 null，本接口不允许改密码"
```

**正确做法是用两个 DTO**——Create 和 Update 是不同的接口契约，强行复用同一 DTO + 分组是常见的过度设计。

### 9.4 在 Repository 里做业务校验

```java
// ❌ 错误
@Repository
public class UserRepository {
    public User save(User user) {
        if (user.getAge() < 18) {
            throw new RuntimeException("未成年禁止注册");
        }
        return entityManager.persist(user);
    }
}
```

Repository 测试时被迫 mock 业务规则，Job / 脚本绕过 Service 直接调用时绕过校验。Repository 只做持久化。

### 9.5 全局 `@Valid` 而非逐字段

```java
// ❌ 错误：DTO 加 @Valid 但没说清楚校验哪些字段
public class CreateUserRequest {
    @Valid private ProfileInfo profile;   // 嵌套对象也需要校验
    private Address address;              // 嵌套对象未加 @Valid
}

// ✅ 正确：嵌套对象显式声明
public class CreateUserRequest {
    @Valid private ProfileInfo profile;
    @Valid private Address address;       // 嵌套也要校验
}
```

嵌套对象的校验默认不级联，必须显式 `@Valid`。

### 9.6 数据库异常直接抛给用户

```java
// ❌ 错误：数据库原始异常透传
try {
    userRepository.save(user);
} catch (DataIntegrityViolationException e) {
    return Result.fail(500, e.getMessage());   // 用户看到 SQL 错误
}

// ✅ 正确：翻译为业务错误
@ExceptionHandler(DataIntegrityViolationException.class)
public Result<?> handleDb(DataIntegrityViolationException ex) {
    // 翻译逻辑，见 4.4
    return Result.fail(409, "用户名已被占用");
}
```

### 9.7 把"非空"判断散落到各处

```java
// ❌ 错误：每个字段都手写 if
if (req.getUsername() == null || req.getUsername().isBlank()) {
    throw new BizException("用户名不能为空");
}
if (req.getEmail() == null || req.getEmail().isBlank()) {
    throw new BizException("邮箱不能为空");
}

// ✅ 正确：交给 @NotBlank
public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空") private String username;
    @NotBlank(message = "邮箱不能为空")  private String email;
}
```

`@NotBlank` 已经覆盖 `null`、`""`、`"   "` 三种空场景，业务代码里**不应该**再写 `null/blank` 判断——把格式相关的逻辑交给注解，Service 只做"注解做不了的事"。

---

## 总结

ValidX 在分层架构中的定位可以归纳为**两层入口、一条兜底线**：

```
┌─────────────────────────────────────────────────────────┐
│  Controller（注解入口）                                   │
│    @Valid + @ChinesePhone / @Email / @QQ ...            │
│    拦截：格式、必填、字符集、长度                          │
│    价值：脏请求不进入 Service 层                          │
├─────────────────────────────────────────────────────────┤
│  Service（链式 API 入口）                                 │
│    ValidX.init().field("x").isXxx(...)                  │
│    拦截：跨字段规则、动态查询、业务上下文                   │
│    价值：业务规则集中表达、可读性高                        │
├─────────────────────────────────────────────────────────┤
│  Repository（数据库约束兜底）                             │
│    UNIQUE / NOT NULL / CHECK / FK                       │
│    拦截：绕过应用层的脏数据                              │
│    价值：最后防线，所有路径生效                           │
└─────────────────────────────────────────────────────────┘
```

三层不是替代而是**接力**，每一层做自己最擅长的事：

- **Controller 用注解**：因为它是框架集成的，能自动序列化错误，能 0 行代码拦截脏请求；
- **Service 用链式**：因为业务规则需要分支、需要查询、需要上下文，注解表达不了；
- **Repository 用数据库约束**：因为它是唯一能被所有路径覆盖的兜底，应用层校验再严密也总有漏网之鱼。

把这套模式套到团队里，最容易看到的收益是：

1. **Controller 不再臃肿** —— 几十个字段的校验从一坨 `if` 变成一行注解；
2. **Service 不再纠结"这条规则要不要写"** —— 跨字段就放 Service，单字段格式就放 Controller；
3. **数据更干净** —— 数据库兜底挡住所有绕过应用层的写入路径；
4. **新人 onboarding 更快** —— DTO 上的注解就是接口契约文档，看一眼就知道每个字段的约束。

ValidX 的注解和链式 API 是**同一套规则**（注解底层调用的就是链式 `ValidX` 的同一组方法），所以不会出现"Controller 验过 `isChinesePhone`，Service 又验一次 `isChineseMobile` 不一致"的尴尬——这是 ValidX 在分层架构里最被低估的优势。

这套分层思路与系列其他文章相互衔接：
- 《订单数据验证：从购物车到支付的完整验证链路》（week09）展示了完整的端到端验证链路；
- 《用户登录验证方案：用户名、手机号、邮箱多种方式》（week10）展示了 Service 层链式 API 的实战；
- 《ValidX分组验证详解》（week08）专门展开分组验证的细节；
- 《ValidX自定义验证注解开发入门》（week10）讲解了如何为分层架构补充自定义注解。