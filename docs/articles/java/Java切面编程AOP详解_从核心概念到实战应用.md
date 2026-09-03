# Java切面编程（AOP）详解：从核心概念到实战应用

## 📋 目录
- [什么是 AOP](#什么是-aop)
- [为什么需要 AOP：横切关注点](#为什么需要-aop横切关注点)
- [六大核心概念](#六大核心概念)
  - [1. 切面 Aspect](#1-切面-aspect)
  - [2. 连接点 JoinPoint](#2-连接点-joinpoint)
  - [3. 切入点 Pointcut](#3-切入点-pointcut)
  - [4. 通知 Advice](#4-通知-advice)
  - [5. 目标对象 Target](#5-目标对象-target)
  - [6. 织入 Weaving](#6-织入-weaving)
- [五种通知类型详解](#五种通知类型详解)
- [切点表达式：execution 完全指南](#切点表达式execution-完全指南)
- [Spring AOP vs AspectJ](#spring-aop-vs-aspectj)
- [代理原理：JDK 动态代理 vs CGLIB](#代理原理jdk-动态代理-vs-cglib)
- [实战案例：日志切面](#实战案例日志切面)
- [实战案例：接口耗时与性能监控](#实战案例接口耗时与性能监控)
- [实战案例：参数校验切面（与 ValidX 结合）](#实战案例参数校验切面与-validx-结合)
- [切面优先级：@Order](#切面优先级order)
- [常见坑与解决方案](#常见坑与解决方案)
- [最佳实践总结](#最佳实践总结)

---

## 什么是 AOP

AOP（Aspect Oriented Programming，面向切面编程）是一种编程范式，它通过**预编译方式和运行期动态代理**，在不修改源码的情况下，给程序动态统一添加功能。

它的核心思想是：**把散布在业务代码中的"横切关注点"抽取出来，统一管理**。

```java
// ❌ 没有 AOP：每个业务方法都要重复写日志
public Order createOrder(OrderDTO dto) {
    log.info("createOrder 开始，参数: {}", dto);        // ① 日志
    long start = System.currentTimeMillis();            // ② 耗时
    try {
        // ... 业务逻辑 ...
        log.info("createOrder 成功");                   // ① 日志
        return order;
    } catch (Exception e) {
        log.error("createOrder 失败", e);               // ① 日志
        throw e;
    } finally {
        long cost = System.currentTimeMillis() - start;
        log.info("createOrder 耗时: {}ms", cost);       // ② 耗时
    }
}

// 再来一个：同一个模板再写一遍
public Order cancelOrder(Long orderId) {
    log.info("cancelOrder 开始，参数: {}", orderId);
    long start = System.currentTimeMillis();
    try {
        // ... 业务逻辑 ...
        return order;
    } catch (Exception e) {
        log.error("cancelOrder 失败", e);
        throw e;
    } finally {
        log.info("cancelOrder 耗时: {}ms", System.currentTimeMillis() - start);
    }
}
```

如果系统里有 100 个这样的业务方法，日志和耗时统计的代码就要重复 100 遍——这就是"横切关注点"（cross-cutting concerns）带来的**代码重复**与**修改困难**（改日志格式要动 100 个方法）。

```java
// ✅ 用 AOP：日志和耗时逻辑只写一次，业务方法保持纯净
@Aspect
@Component
public class LogAspect {
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            log.info("{} 成功，耗时 {}ms", pjp.getSignature(), System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("{} 失败", pjp.getSignature(), e);
            throw e;
        }
    }
}

// 业务方法回归纯净，一行日志都不写
public Order createOrder(OrderDTO dto) {
    return orderService.save(dto);
}
```

**核心价值**：
- **解耦**：业务逻辑与横切逻辑（日志/事务/权限）彻底分离
- **复用**：横切逻辑只写一次，全局生效
- **易维护**：改日志策略只需动一个切面，而非所有业务类

---

## 为什么需要 AOP：横切关注点

| 横切关注点 | 典型需求 | 不用 AOP 的后果 |
|-----------|---------|----------------|
| **日志记录** | 记录方法入参、返回值、异常 | 每个方法重复 log，改格式要全量改 |
| **事务管理** | 方法级事务边界 | 每个方法 try/catch 事务代码 |
| **权限校验** | 方法调用前校验角色/权限 | 每个方法开头重复鉴权代码 |
| **性能监控** | 统计接口耗时、QPS | 埋点代码侵入业务 |
| **参数校验** | Controller 层参数统一校验 | 每个接口重复校验逻辑 |
| **缓存管理** | 查询结果缓存、缓存清理 | 缓存逻辑散落在各 DAO |
| **审计日志** | 记录谁在什么时候做了什么 | 每个写操作手动记录 |

没有 AOP 的时代，这些代码以"模板代码"的形式混入业务逻辑，代码可读性急剧下降，这就是所谓的**横切关注点问题**——它在横向方向切开类层次，又纵向贯穿所有业务方法。

---

## 六大核心概念

> 这一节是 AOP 的基础，术语来自 AspectJ / Spring AOP 规范，务必理解到位。

### 1. 切面 Aspect

**切面 = 切入点 + 通知**。它是横切关注点的模块化封装，通常用一个 `@Aspect` 注解的类表示。

```java
@Aspect                       // 声明这是一个切面
@Component                    // 交给 Spring 管理
public class LogAspect {
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    @Before("serviceLayer()")
    public void before() { /* 通知逻辑 */ }
}
```

### 2. 连接点 JoinPoint

程序执行的**某个特定位置**（方法调用、异常抛出、字段赋值等）。Spring AOP 只支持**方法执行**作为连接点。

```java
@Before("serviceLayer()")
public void before(JoinPoint jp) {
    String method = jp.getSignature().getName();   // 被调方法名
    Object[] args = jp.getArgs();                  // 被调方法的入参
    Object target = jp.getTarget();                // 目标对象
}
```

### 3. 切入点 Pointcut

**匹配连接点的表达式**。它决定切面"切在哪些方法上"。一个切入点可以被多个通知复用。

```java
@Pointcut("execution(* com.example.service.*.*(..))")
public void serviceLayer() {}          // 匹配 service 包下所有类的所有方法
```

### 4. 通知 Advice

切入点上要执行的**增强逻辑**，分五种（见下一节）。它定义了"切面在连接点的什么时候做什么"。

### 5. 目标对象 Target

被切面增强的**原始业务对象**。代理对象包装了它，调用链是：调用方 → 代理对象 → 目标对象。

### 6. 织入 Weaving

把切面应用到目标对象并创建新代理对象的**过程**。分为三种时机：

| 织入方式 | 时机 | 代表框架 | 特点 |
|---------|------|---------|------|
| **编译期织入** | 源码编译时 | AspectJ | 需要特殊编译器，性能最好 |
| **类加载期织入** | JVM 加载类时 | AspectJ LTW | 需要启动参数 `-javaagent` |
| **运行期织入** | 运行期动态生成代理 | **Spring AOP** | 无需特殊编译，最常用 |

---

## 五种通知类型详解

| 通知类型 | 注解 | 执行时机 | 能否控制方法执行 | 典型用途 |
|---------|------|---------|----------------|---------|
| **前置通知** | `@Before` | 方法执行前 | ❌ 不能 | 校验权限、记录开始日志 |
| **后置通知** | `@After` | 方法执行后（无论成败） | ❌ 不能 | 释放资源、清理现场 |
| **返回通知** | `@AfterReturning` | 方法正常返回后 | ❌ 不能 | 记录返回值、加工结果 |
| **异常通知** | `@AfterThrowing` | 方法抛出异常后 | ❌ 不能 | 记录异常、发送告警 |
| **环绕通知** | `@Around` | 方法执行前后全程 | ✅ 能（可拦截、可放行、可改返回） | 事务、限流、参数校验、耗时统计 |

```java
@Aspect
@Component
@Slf4j
public class AdviceAspect {

    @Pointcut("execution(* com.example.service.OrderService.*(..))")
    public void orderOps() {}

    /** ① 前置通知：执行前触发 */
    @Before("orderOps()")
    public void before(JoinPoint jp) {
        log.info("[Before] 即将调用: {}", jp.getSignature());
    }

    /** ② 后置通知：方法结束后触发（无论正常还是异常） */
    @After("orderOps()")
    public void after() {
        log.info("[After] 方法执行完毕");
    }

    /** ③ 返回通知：正常返回后触发，可拿到返回值 */
    @AfterReturning(pointcut = "orderOps()", returning = "result")
    public void afterReturning(Object result) {
        log.info("[AfterReturning] 返回值: {}", result);
    }

    /** ④ 异常通知：抛出异常后触发，可拿到异常对象 */
    @AfterThrowing(pointcut = "orderOps()", throwing = "ex")
    public void afterThrowing(Exception ex) {
        log.error("[AfterThrowing] 异常: {}", ex.getMessage());
    }

    /** ⑤ 环绕通知：最强，能控制方法是否执行、改变返回值 */
    @Around("orderOps()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();   // 放行，执行目标方法
            return result;                   // 也可以改返回值后返回
        } finally {
            log.info("[Around] {} 耗时 {}ms", pjp.getSignature(),
                    System.currentTimeMillis() - start);
        }
    }
}
```

**执行顺序**（方法正常返回时）：
```
@Around 前 → @Before → 目标方法 → @AfterReturning → @After → @Around 后
```

**执行顺序**（方法抛出异常时）：
```
@Around 前 → @Before → 目标方法（抛异常）→ @AfterThrowing → @After → @Around 捕获/重抛
```

> ⚠️ `@Around` 中**必须手动调用 `pjp.proceed()`**，否则目标方法不会执行。这是新手最容易犯的错误。

---

## 切点表达式：execution 完全指南

`execution()` 是最常用的切点表达式，语法：

```
execution(修饰符? 返回类型 类名.方法名(参数) 异常?)
```

```java
// ① 匹配所有 public 方法
execution(public * *(..))

// ② 匹配所有无参方法
execution(* *())

// ③ 匹配指定返回类型（String 返回类型）
execution(String com.example.service.UserService.*(..))

// ④ 匹配指定类的所有方法
execution(* com.example.service.UserService.*(..))

// ⑤ 匹配包下所有类的所有方法（含子包用 ..）
execution(* com.example.service..*(..))        // service 包及子包
execution(* com.example.service.*(..))         // 仅 service 包一层

// ⑥ 匹配指定方法名（支持 * 通配）
execution(* com.example.service.*.get*(..))    // 所有 getXxx 方法

// ⑦ 精确匹配一个参数
execution(* com.example.service.*.*(String))

// ⑧ 任意数量参数（第一个必须是 String）
execution(* com.example.service.*.*(String, ..))
```

**其他切点指示符（Pointcut Designator）**：

| 指示符 | 作用 | 示例 |
|-------|------|------|
| `within` | 按类型匹配 | `within(com.example.service.*)` |
| `bean` | 按 Spring Bean 名匹配 | `bean(orderService)` / `bean(*Service)` |
| `@annotation` | 匹配带指定注解的方法 | `@annotation(org.springframework.web.bind.annotation.GetMapping)` |
| `@within` | 匹配类上带注解的类 | `@within(org.springframework.stereotype.Service)` |
| `args` | 按参数类型匹配 | `args(String, Long)` |
| `@args` | 匹配带注解的参数 | `@args(com.example.Valid)` |
| `this` / `target` | 按代理对象/目标对象类型 | `this(UserService)` |
| `@target` | 匹配目标对象类上的注解 | `@target(com.example.Secured)` |

**组合表达式**：

```java
// 逻辑与（&&）：同时满足
@Pointcut("execution(* com.example.service.*.*(..)) && @annotation(com.example.annotation.Secured)")

// 逻辑或（||）：满足其一
@Pointcut("execution(* com.example.controller.*.*(..)) || execution(* com.example.api.*.*(..))")

// 逻辑非（!）：排除
@Pointcut("execution(* com.example.service.*.*(..)) && !execution(* com.example.service.CacheService.*(..))")
```

> ⚠️ 在 Spring XML 或注解表达式中，`&&` 写为 `&&` 可能被解析，推荐在 `@Pointcut` 方法内用 Java 符号，在字符串里用 `and` / `or` / `not` 更稳妥。

---

## Spring AOP vs AspectJ

| 对比维度 | Spring AOP | AspectJ |
|---------|-----------|---------|
| **实现方式** | 运行期动态代理（JDK/CGLIB） | 编译期/加载期字节码织入 |
| **连接点** | 仅方法执行 | 方法、构造器、字段、异常等全部 |
| **织入时机** | 运行期 | 编译期、加载期、运行期 |
| **是否需要特殊编译器** | ❌ 不需要 | ✅ 需要 ajc 编译器 |
| **代理对象** | 有（目标对象被包装） | 无（直接织入字节码） |
| **性能** | 有代理开销，微秒级 | 无运行时代理，最快 |
| **自调用 this 问题** | ❌ 失效（不走代理） | ✅ 生效（直接织入） |
| **使用难度** | 简单，Spring 生态标配 | 复杂，需特殊构建配置 |
| **应用场景** | 绝大多数企业应用 | 性能极致、需要字段级切面 |

**结论**：99% 的 Java 项目用 Spring AOP 就够了。AspectJ 只在你需要构造器切面、字段切面，或代理方案无法满足的性能要求时才需要。

---

## 代理原理：JDK 动态代理 vs CGLIB

Spring AOP 运行期织入依赖代理，两种实现：

| 对比维度 | JDK 动态代理 | CGLIB 代理 |
|---------|-------------|-----------|
| **原理** | 实现目标接口生成代理类 | 继承目标类生成子类 |
| **前提** | 目标类**必须实现接口** | 目标类可无接口 |
| **目标类要求** | 接口方法才能被增强 | **不能被 final 修饰** |
| **默认选择** | 目标有接口时默认 | 目标无接口时兜底 |
| **Spring Boot 2.x+ 默认** | — | 总是优先 CGLIB（`proxyTargetClass=true`） |

```java
// JDK 动态代理示意（简化版）
public class JdkProxyDemo {
    public static void main(String[] args) {
        UserService target = new UserServiceImpl();   // 目标对象
        UserService proxy = (UserService) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{UserService.class},
                (proxyObj, method, methodArgs) -> {
                    System.out.println("JDK 代理前置增强");
                    Object result = method.invoke(target, methodArgs);
                    System.out.println("JDK 代理后置增强");
                    return result;
                });
        proxy.createUser("张三");
    }
}

interface UserService { void createUser(String name); }
class UserServiceImpl implements UserService {
    public void createUser(String name) { System.out.println("创建用户: " + name); }
}
```

```java
// CGLIB 代理示意（继承方式）
// 本质：CGLIB 生成 UserServiceImpl 的子类，重写父类方法并织入增强
public class CglibProxyDemo {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(NoInterfaceService.class);   // 目标类没有接口
        enhancer.setCallback((MethodInterceptor) (obj, method, methodArgs, proxy) -> {
            System.out.println("CGLIB 前置增强");
            Object result = proxy.invokeSuper(obj, methodArgs);
            System.out.println("CGLIB 后置增强");
            return result;
        });
        NoInterfaceService proxy = (NoInterfaceService) enhancer.create();
        proxy.doWork();
    }
}

class NoInterfaceService {   // 没有接口的类也能被代理
    public void doWork() { System.out.println("干活"); }
}
```

**由此带来的两个经典限制**：
1. **final 类/方法无法被 CGLIB 增强** → 别给被切类加 final
2. **类内部 `this` 调用不走代理** → 自调用切面失效（见"常见坑"）

---

## 实战案例：日志切面

```java
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    /** 切点：所有 Controller 的 public 方法 */
    @Pointcut("execution(public * com.example.controller..*.*(..))")
    public void webLog() {}

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            log.info("请求: {}，入参: {}，耗时: {}ms",
                    pjp.getSignature().toShortString(),
                    Arrays.toString(pjp.getArgs()),
                    System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            log.error("请求: {}，异常: {}",
                    pjp.getSignature().toShortString(), e.getMessage(), e);
            throw e;
        }
    }
}
```

配合统一返回结构，还能自动包装结果：

```java
@Around("webLog()")
public Object wrapResult(ProceedingJoinPoint pjp) throws Throwable {
    Object result = pjp.proceed();
    // Controller 返回原始数据，切面统一包成 ApiResponse
    if (!(result instanceof ApiResponse)) {
        return ApiResponse.success(result);
    }
    return result;
}
```

---

## 实战案例：接口耗时与性能监控

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {
    String name() default "";          // 监控点名称
    long warnThreshold() default 500;  // 告警阈值（毫秒）
}
```

```java
@Aspect
@Component
@Slf4j
public class MonitorAspect {

    @Around("@annotation(monitor)")
    public Object monitor(ProceedingJoinPoint pjp, Monitor monitor) throws Throwable {
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long costMs = (System.nanoTime() - start) / 1_000_000;
            String name = monitor.name().isEmpty()
                    ? pjp.getSignature().toShortString()
                    : monitor.name();
            if (costMs > monitor.warnThreshold()) {
                log.warn("[SLOW] {} 耗时 {}ms，超过阈值 {}ms", name, costMs, monitor.warnThreshold());
                // 这里可扩展：发送告警、写入监控指标（Micrometer/Prometheus）
            } else {
                log.debug("[OK] {} 耗时 {}ms", name, costMs);
            }
        }
    }
}
```

使用：业务方法只需加一行注解

```java
@Service
public class OrderService {
    @Monitor(name = "createOrder", warnThreshold = 1000)
    public Order createOrder(OrderDTO dto) {
        // 业务逻辑，零监控代码
    }
}
```

**这就是 AOP 的精髓**：`@Monitor` 注解是"声明式"的，监控逻辑本身在切面里只写一次。

---

## 实战案例：参数校验切面（与 ValidX 结合）

> 呼应 ValidX 的文章体系：AOP 是"把校验规则变成切面"的绝佳载体。自定义校验注解 + 切面，可以做到"校验逻辑与业务完全分离"。

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validated {
    Class<?>[] groups() default {};   // 支持分组，复用 Bean Validation 分组语义
}
```

```java
@Aspect
@Component
public class ValidationAspect {

    // 注入 javax.validation.Validator（Hibernate Validator 实现）
    private final javax.validation.Validator validator;

    public ValidationAspect(javax.validation.Validator validator) {
        this.validator = validator;
    }

    @Around("@annotation(validated)")
    public Object doValidate(ProceedingJoinPoint pjp, Validated validated) throws Throwable {
        for (Object arg : pjp.getArgs()) {
            if (arg == null) {
                continue;
            }
            // 对每个需要校验的参数执行分组校验
            Set<ConstraintViolation<Object>> violations =
                    validator.validate(arg, validated.groups());
            if (!violations.isEmpty()) {
                String msg = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; "));
                throw new IllegalArgumentException("参数校验失败: " + msg);
            }
        }
        return pjp.proceed();
    }
}
```

Controller 使用：

```java
@RestController
@RequestMapping("/order")
public class OrderController {

    @PostMapping
    @Validated(groups = OrderCreateGroup.class)   // 注解声明，无需手写校验
    public ApiResponse<Order> create(@RequestBody OrderDTO dto) {
        return ApiResponse.success(orderService.create(dto));
    }
}
```

> 说明：Spring MVC 自带的 `@Valid` 只校验 `@RequestBody` 参数；用 AOP 切面可以校验**任意位置、任意分组**的参数，还能覆盖非 Spring MVC 场景（如 RPC 接口、定时任务、MQ 消费）。

---

## 切面优先级：@Order

多个切面作用于同一方法时，需要定义执行顺序：

```java
@Aspect
@Component
@Order(1)          // 数字越小优先级越高，先执行
public class SecurityAspect { ... }   // ① 权限校验先跑

@Aspect
@Component
@Order(2)
public class LogAspect { ... }        // ② 日志后跑

@Aspect
@Component
@Order(3)
public class TransactionAspect { ... } // ③ 事务最后
```

**执行顺序**（方法正常返回）：
```
Order(1) @Around前 → Order(2) @Around前 → Order(3) @Around前 → 目标方法
→ Order(3) @Around后 → Order(2) @Around后 → Order(1) @Around后
```

**经典场景**：权限校验（1）必须在日志（2）之前，事务（3）包最外层保证原子性。

---

## 常见坑与解决方案

### 坑 1：类内部自调用，切面失效 🔥

```java
@Service
public class UserService {

    @Transactional
    public void createUser(User user) {
        saveUser(user);        // ❌ this.saveUser() 自调用，事务切面不生效！
    }

    public void saveUser(User user) {
        userDao.insert(user);
    }
}
```

**原因**：Spring 事务基于代理，`this.saveUser()` 调用的是目标对象自身，没经过代理。

**解决**：
```java
// 方案一：注入自身代理（Spring Boot 2.6+ 需开启 allow-circular-references）
@Autowired
private UserService self;      // 注入的是代理对象

public void createUser(User user) {
    self.saveUser(user);       // ✅ 经过代理，事务生效
}

// 方案二：拆到另一个 Service
// 方案三：用 AopContext.currentProxy()（需 @EnableAspectJAutoProxy(exposeProxy = true)）
((UserService) AopContext.currentProxy()).saveUser(user);
```

### 坑 2：@Around 忘记调用 proceed() 🔥

```java
@Around("pointcut()")
public Object around(ProceedingJoinPoint pjp) {
    log.info("前置逻辑");
    // ❌ 忘了 pjp.proceed() → 目标方法静默不执行，返回 null
    return null;
}
```

**解决**：`@Around` 必须手动 `pjp.proceed()`，并处理其抛出的异常（要么捕获，要么重新抛出）。

### 坑 3：final 类/方法无法增强

```java
@Service
public final class OrderService {   // ❌ final 类：CGLIB 无法继承，切面全部失效
    ...
}
```

**解决**：不要给被切面的类/方法加 `final`。

### 坑 4：切点表达式写错，静默失效

`execution(* com.example.service.*(..))`（少写了 `*.*`）这类错误不会报编译错误，只是切面不生效。

**解决**：
- 用 `@Slf4j` + 启动时打一条 `log.info("切面已加载: {}", pointcut)` 确认生效
- 单元测试用 `@SpringBootTest` + 断言代理类型：`assertTrue(AopUtils.isAopProxy(bean))`

### 坑 5：异常通知 + 环绕通知的异常吞掉问题

```java
@Around("pointcut()")
public Object around(ProceedingJoinPoint pjp) {
    try {
        return pjp.proceed();
    } catch (Exception e) {
        return null;      // ❌ 吞掉异常，上层收不到，@AfterThrowing 也不触发
    }
}
```

**解决**：环绕通知中捕获异常后应**重新抛出**，或按业务显式转换为受检异常。

### 坑 6：代理对象类型强转失败

目标类有接口时默认走 JDK 代理，强转为具体类会 `ClassCastException`。

**解决**：Spring Boot 2.x+ 默认 `proxyTargetClass=true` 用 CGLIB；老项目可配置 `spring.aop.proxy-target-class=true`，或面向接口编程。

---

## 最佳实践总结

1. **优先使用注解式切点**（`@annotation(...)` / 自定义注解），比 `execution` 包路径更精确、更语义化
2. **一个切面只做一件事**：日志切面、权限切面、事务切面分开，用 `@Order` 控制顺序
3. **`@Around` 优先于散落的 `@Before/@After`**：逻辑集中，且能控制方法执行
4. **切面里不写业务逻辑**：切面只做横切增强，业务判断留在目标方法
5. **别让切面改变业务返回值**：除非是统一包装这类明确职责
6. **注意自调用与 final**：被切类不要 final，内部调用走代理（或拆类）
7. **生产环境加切面开关**：如 `@ConditionalOnProperty(prefix = "app.aop", name = "enabled", havingValue = "true")`，方便出问题时快速关闭
8. **监控切面本身**：切面代码出问题，影响面是全局的——`proceed()` 的异常务必 `throw` 出去，避免吞异常导致线上故障

---

## 总结

| 掌握维度 | 关键要点 |
|---------|---------|
| **概念** | 切面 = 切入点 + 通知；连接点、织入、目标对象、代理 |
| **五种通知** | Before / After / AfterReturning / AfterThrowing / Around（最强） |
| **切点表达式** | `execution` 为主，`@annotation` 最语义化，支持 `&&` / `\|\|` / `!` 组合 |
| **实现机制** | Spring AOP 运行期代理；JDK 代理（接口）vs CGLIB（继承） |
| **适用范围** | 日志、事务、权限、监控、参数校验、缓存、审计 |
| **三大纪律** | ① 自调用要绕代理 ② @Around 必须 proceed ③ 被切类禁 final |

AOP 是 Java 后端开发绕不开的核心能力——Spring 事务、Spring Security、`@Async`、缓存注解全都构建在它之上。理解了代理机制和五种通知，你就掌握了打开这些框架源码大门的钥匙。**学 AOP，核心就一句话：把横切关注点抽出来，用代理织入，让业务代码保持纯净。**

---

**文档版本**：v1.0
**创建日期**：2026-08-28
**适用范围**：Spring AOP / AspectJ / Bean Validation
