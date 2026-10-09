# ValidX 在 API Gateway 的集成与应用

## 📋 目录
- [引言](#引言)
- [一、API Gateway 验证的 5 大挑战](#一api-gateway-验证的-5-大挑战)
- [二、ValidX 在 Gateway 的定位：轻量预检 + 拒绝透明](#二validx-在-gateway-的定位轻量预检--拒绝透明)
- [三、Spring Cloud Gateway 集成方案](#三spring-cloud-gateway-集成方案)
  - [3.1 全局 GlobalFilter：最快落地](#31-全局-globalfilter最快落地)
  - [3.2 请求体（Body）验证](#32-请求体body验证)
  - [3.3 查询参数 / Header / PathVariable 验证](#33-查询参数--header--pathvariable-验证)
- [四、Zuul 与 Servlet Filter 集成](#四zuul-与-servlet-filter-集成)
- [五、统一错误响应：4xx 标准化](#五统一错误响应4xx-标准化)
- [六、Gateway → 微服务：再校验的接力](#六gateway--微服务再校验的接力)
- [七、性能：高并发场景下的 Pattern 复用与短路](#七性能高并发场景下的-pattern-复用与短路)
- [八、实战案例：电商 Gateway 验证全链路](#八实战案例电商-gateway-验证全链路)
- [总结](#总结)
- [项目地址](#项目地址)

---

## 引言

在微服务架构中，**API Gateway** 是所有外部请求的统一入口。它负责路由、鉴权、限流、协议转换……以及**入参校验**。如果把每一个微服务都比作一扇门，Gateway 就是门厅——门厅不验票，进了每扇门还得再验一遍，效率低且不一致；门厅验得太严，又会拖慢整个入口的吞吐。

实际项目里，Gateway 层的验证经常被忽视或做错：

- **完全没有**：把参数校验全甩给后端微服务，结果脏请求穿透到下游，浪费带宽和算力；
- **过度校验**：在 Gateway 层跑 `@Valid` 全量注解，把请求体反序列化完再校验，吞吐掉一半；
- **规则碎片**：每个微服务自己写一套校验，错误消息和返回码不一致，前端体验割裂；
- **不知道什么时候该在哪一层校验**。

本文从实战出发，把 API Gateway 层的验证问题拆开讲清楚——**什么时候该校验、校验什么、用什么工具**。重点是基于 ValidX 在 Spring Cloud Gateway 中的落地，给出一套**轻量、可观测、易扩展**的 Gateway 验证模板。

> 本文假设你已熟悉 Spring Cloud Gateway 的基本概念（Route / Predicate / Filter）；若尚未了解，建议先读 Spring Cloud Gateway 官方文档的 5 分钟快速入门章节。

---

## 一、API Gateway 验证的 5 大挑战

### 1.1 性能 vs 安全的天平

Gateway 是请求的"咽喉要道"。一个普通的电商系统，Gateway 节点可能承受 **每秒上万 QPS**——验证逻辑如果跑得太重，就会成为整个系统的瓶颈。

```java
// 反例：在 Gateway 层做完整 Bean Validation
@PostMapping
public Mono<ResponseEntity<?>> handle(@Valid @RequestBody ComplexDTO dto) {
    // 这里会反序列化、跑注解校验——每次都要付出 N 个注解校验的代价
    // 而 Gateway 通常只需要"格式合法就行"，业务校验留给下游
}
```

**正确做法**：Gateway 层只做**轻量预检**（手机号格式、ID 长度、字符集），把昂贵的校验（数据库一致性、跨字段规则）留给业务服务。

### 1.2 验证规则的复用难题

同一个规则（例如"中国手机号"）在 20 个微服务里被重复实现：

```java
// user-service 的实现
if (!phone.matches("^1[3-9]\\d{9}$")) { ... }

// order-service 的实现
if (phone == null || phone.length() != 11) { ... }

// message-service 的实现
private static final Pattern PHONE = Pattern.compile("^1[3-9]\\d{9}$");
if (!PHONE.matcher(phone).matches()) { ... }
```

三处实现、三种错误消息、三种异常类型。**前端收到的错误响应完全无法统一处理**。

### 1.3 错误响应格式不统一

没有统一规范时，每个微服务返回的错误响应都是自定义格式：

```json
// service A
{"code": 400, "message": "手机号格式错误"}

// service B
{"status": "fail", "error": "invalid phone", "msg": "请输入正确的手机号"}

// service C
{"errcode": 40001, "errmsg": "PHONE_INVALID"}
```

前端必须为每个接口写一套错误处理逻辑——这是 Gateway 层应该解决的"协议级"问题。

### 1.4 校验位置选择

**Gateway 校验** vs **微服务校验** vs **重复校验**，怎么选？

| 校验位置 | 优势 | 劣势 |
|---------|------|------|
| 只在 Gateway | 入口拦截，节省后端开销 | 微服务间内部调用无保护 |
| 只在微服务 | 业务完整，规则灵活 | 外部脏请求浪费 Gateway → 微服务链路 |
| **Gateway + 微服务（分层）** | 各自做擅长的事 | 需要明确分工 |

**黄金法则**：Gateway 做"格式合法性"——挡掉明显非法的请求（如 `phone=abc`、`idCard=123`）；微服务做"业务合法性"——挡掉绕过 Gateway 的内部调用、跨字段规则、与 DB 一致的规则。

### 1.5 异步 / 响应式编程模型

Spring Cloud Gateway 基于 **Project Reactor**（`Mono` / `Flux`），传统的同步校验写法会阻塞响应式线程：

```java
// 反例：在 Mono 中阻塞
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String phone = exchange.getRequest().getQueryParams().getFirst("phone");
    if (phone != null && !phone.matches("^1[3-9]\\d{9}$")) {
        // 这里阻塞不会报错，但会占用 reactor 线程
    }
    return chain.filter(exchange);
}
```

**好在**：ValidX 的链式 API 是**纯计算**，不涉及 IO，几微秒就能跑完——不会成为响应式链路中的瓶颈。但要在 Mono 里正确处理错误响应（`onErrorResume` / 直接 `setComplete`），需要在写法上稍加注意。

---

## 二、ValidX 在 Gateway 的定位：轻量预检 + 拒绝透明

### 2.1 为什么选 ValidX 而不是 Bean Validation

很多团队第一反应是"Gateway 层也用 `@Valid` 注解"——但这是误区：

| 维度 | Bean Validation (`@Valid`) | ValidX 链式 API |
|------|----------------------------|------------------|
| 反序列化要求 | **必须**先反序列化完整 DTO | **不需要**，原始 `String` 即可校验 |
| 触发时机 | 控制器方法调用前 | 调用即生效，任意时机 |
| 错误收集 | `BindingResult` / `MethodArgumentNotValidException` | `List<String>` 直接拿 |
| 规则丰富度 | 标准注解 + Hibernate 扩展 | **107 个中国业务注解**，覆盖身份证、银行卡、统一社会信用代码等 |
| Gateway 适配 | 需要 AOP / `@Validated` 控制器方法 | 直接在 `GatewayFilter` 中调用 |

**结论**：Gateway 层用 ValidX 链式 API 做**原始字符串的格式校验**，比 `@Valid` 更轻、更直接、更适配响应式模型。

### 2.2 ValidX 在 Gateway 层的典型用法

```java
@Component
public class QuickFormatFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 拿原始参数（不反序列化）
        String phone = exchange.getRequest().getQueryParams().getFirst("phone");

        // 2. ValidX 直接校验原始字符串
        if (phone != null && !ValidX.init().isChinesePhone(phone).passed()) {
            return reject(exchange, "手机号格式错误");
        }

        // 3. 通过 → 放行
        return chain.filter(exchange);
    }
}
```

**三个关键设计**：

1. **不反序列化**——`phone` 是 `String`，不构造 DTO，省一次 JSON parse；
2. **短路返回**——校验失败直接 `Mono<Void>` 终止，不再走下游；
3. **可观测**——错误消息可以打日志、走监控，定位脏请求来源。

### 2.3 Gateway 校验的 4 条红线

| 红线 | 说明 |
|------|------|
| **不要在 Gateway 校验业务规则** | Gateway 没有数据库访问能力，也不该有 |
| **不要在校验里做 IO** | 调数据库 / 远程接口会拖垮 Gateway 性能 |
| **不要让校验成为可绕过项** | 即便 Gateway 校验了，微服务内部调用也要再校验一次 |
| **不要在校验失败时返回 500** | 4xx（客户端错）≠ 5xx（服务端错），前者是用户的问题，后者是系统的问题 |

---

## 三、Spring Cloud Gateway 集成方案

### 3.1 全局 GlobalFilter：最快落地

最直接的方案是实现 `GlobalFilter` 接口，对所有路由生效：

```java
@Component
public class GlobalValidXFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 路由级白名单：放行内部健康检查、文档
        if (path.startsWith("/actuator") || path.startsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        ValidX validator = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

        // 1. 公共 Header 校验
        String requestId = request.getHeaders().getFirst("X-Request-Id");
        if (requestId != null && !validator.isFixedLength(requestId, 32).passed()) {
            return reject(exchange, "请求 ID 必须为 32 位");
        }

        // 2. 查询参数校验
        String phone = request.getQueryParams().getFirst("phone");
        if (phone != null && !validator.isChinesePhone(phone).passed()) {
            return reject(exchange, "手机号格式不正确");
        }

        // 3. 通过 → 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 越早执行越好（在路由匹配之后、在限流/鉴权之前）
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    private Mono<Void> reject(ServerHttpExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = String.format("{\"code\":400,\"message\":\"%s\"}", msg);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
```

### 3.2 请求体（Body）验证

Gateway 层的请求体校验有两个选择：

**方案 A：在 GlobalFilter 里读 Body**（适用于规则固定、与业务无关）

```java
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 注意：Body 是一次性流，读了之后下游读不到，要 cache
    return DataBufferUtils.join(exchange.getRequest().getBody())
        .flatMap(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);

            // 解析 JSON（轻量解析，比如只取几个字段）
            String body = new String(bytes, StandardCharsets.UTF_8);
            JSONObject json = JSON.parseObject(body);

            // 校验 body 里的字段
            String phone = json.getString("phone");
            if (phone != null && !ValidX.init().isChinesePhone(phone).passed()) {
                return reject(exchange, "body.phone 格式错误");
            }

            // 把 body 重新包装回 request，供下游读
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .body(Flux.just(exchange.getResponse().bufferFactory().wrap(bytes)))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        });
}
```

**方案 B：路由级 + `@Valid`**（推荐做法，让业务服务承担 Body 校验）

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            # 只在路由层面打开 @Valid，Body 校验交给下游微服务
            - ValidationFilter
```

```java
@Component
public class RouteLevelValidXFilter implements GlobalFilter {

    // 只对 POST/PUT/PATCH 生效
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpMethod method = exchange.getRequest().getMethod();
        if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
            return chain.filter(exchange); // GET/DELETE 无 body
        }

        // 走 Body 校验流程（见方案 A 的实现）
        return validateBody(exchange, chain);
    }
}
```

**取舍**：

- 路由级校验**易维护、易复用**——业务规则和业务服务绑定；
- 全局 Body 校验**更激进**——但风险是"改了字段名忘了同步 GlobalFilter"。

**实战推荐**：**简单的格式校验（手机号、邮箱、ID）放 Gateway，跨字段规则放业务服务**。

### 3.3 查询参数 / Header / PathVariable 验证

这三种"轻量"参数最适合放在 Gateway 层。

```java
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    ValidX v = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

    // 1. Header 校验
    String traceId = request.getHeaders().getFirst("X-Trace-Id");
    if (traceId != null && !v.isFixedLength(traceId, 32).passed()) {
        return reject(exchange, "X-Trace-Id 必须为 32 位");
    }

    // 2. PathVariable 校验（路由 /api/users/{userId}）
    String userId = request.getQueryParams().getFirst("userId");
    if (userId != null && !v.isFixedLength(userId, 19).passed()) {
        return reject(exchange, "userId 必须为 19 位数字");
    }

    // 3. 多值参数校验
    List<String> tags = request.getQueryParams().get("tag");
    if (tags != null) {
        for (String tag : tags) {
            if (!v.isAlpha(tag).passed()) {
                return reject(exchange, "tag 参数只能包含字母");
            }
        }
    }

    return chain.filter(exchange);
}
```

**小技巧**：把高频的正则预编译进 ValidX 的 Pattern 缓存。ValidX 内部已经把常用注解的 `Pattern` 预编译好了（见 `XxxValidator` 中的 `private static final Pattern`），不需要业务方再优化。

---

## 四、Zuul 与 Servlet Filter 集成

如果你的 Gateway 还是 Zuul 1.x 或 Servlet Filter 模型，ValidX 的集成一样简单：

### 4.1 Zuul 1.x PreFilter

```java
@Component
public class ValidXZuulFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre"; // 在路由前执行
    }

    @Override
    public int filterOrder() {
        return 1; // 越早越好
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // 校验 query 参数
        String phone = request.getParameter("phone");
        if (phone != null && !ValidX.init().isChinesePhone(phone).passed()) {
            // 直接拦截，不再路由到下游
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(400);
            ctx.setResponseBody("{\"code\":400,\"message\":\"手机号格式错误\"}");
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
        }
        return null;
    }
}
```

### 4.2 传统 Servlet Filter

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class ValidXServletFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String phone = request.getParameter("phone");
        if (phone != null && !ValidX.init().isChinesePhone(phone).passed()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":400,\"message\":\"手机号格式错误\"}");
            return; // 不调用 chain.doFilter
        }
        chain.doFilter(request, response);
    }
}
```

两种方式都**只用了 10 行核心代码**，可见 ValidX 的链式 API 在 Gateway 层落地几乎零学习成本。

---

## 五、统一错误响应：4xx 标准化

### 5.1 推荐的标准格式

```json
{
    "code": 40001,
    "message": "手机号格式不正确",
    "traceId": "abc123def456",
    "timestamp": 1694083200000,
    "path": "/api/v1/users/register"
}
```

| 字段 | 说明 |
|------|------|
| `code` | 业务错误码（如 `40001` = 手机号错、`40002` = 身份证错），前端按 code 国际化 |
| `message` | 默认中文错误消息，前端可按语言切换 |
| `traceId` | 链路追踪 ID，对应 `X-Trace-Id`，便于定位日志 |
| `timestamp` | 错误发生时间戳 |
| `path` | 出错的接口路径，便于排查 |

### 5.2 Gateway 层统一错误响应工具

```java
public final class GatewayErrorResponder {

    private GatewayErrorResponder() {}

    public static Mono<Void> respond(ServerHttpExchange exchange,
                                     int httpStatus,
                                     int businessCode,
                                     String message) {
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(httpStatus));
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        ErrorBody body = new ErrorBody(
                businessCode, message, traceId,
                System.currentTimeMillis(),
                exchange.getRequest().getURI().getPath()
        );
        byte[] bytes = JSON.toJSONString(body).getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(bytes)));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorBody {
        private int code;
        private String message;
        private String traceId;
        private long timestamp;
        private String path;
    }
}
```

### 5.3 业务错误码规范

建议团队约定一套 4 位业务码（前两位 = 域，后两位 = 子错）：

| 域 | 范围 | 示例 |
|----|------|------|
| 用户域 | 10xxx | `10001` 用户名为空、`10002` 手机号格式错、`10003` 身份证错 |
| 订单域 | 20xxx | `20001` 订单金额非法、`20002` 库存不足 |
| 支付域 | 30xxx | `30001` 银行卡号错、`30002` CVV 错 |

Gateway 层拒绝时直接返回业务码，前端按业务码做更友好的展示（i18n 文案、A/B 测试等）。

---

## 六、Gateway → 微服务：再校验的接力

### 6.1 为什么不能只在 Gateway 校验一次

**三层理由**：

1. **内部调用绕过 Gateway**：微服务 A 通过 Feign 调微服务 B，B 收到的参数没人校验过；
2. **Gateway 规则可能落后**：业务规则改了，但 Gateway 的规则没人同步；
3. **安全纵深防御**：任何安全方案都不能只靠一层。

### 6.2 接力校验的标准模式

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Client    │ ──────► │   Gateway   │ ──────► │  Microservice│
└─────────────┘         └─────────────┘         └─────────────┘
   手机号 = 13800        预检通过                 @Valid @ChinesePhone
   13800000              转发请求                  深度校验
```

**Gateway 层**（ValidX 链式 API）：
- 只校验"格式"——`isChinesePhone(phone)`、`isEmail(email)`、`isFixedLength(id, 18)`
- 不校验业务规则——`不允许注册`、`手机号是否在黑名单`

**微服务层**（ValidX 注解 + `@Valid`）：
- 注解做字段级深度校验
- Service 层做跨字段规则
- Repository 层做数据一致性约束

### 6.3 实战示例

```java
// Gateway 层：只做格式校验（ValidX 链式 API）
@Component
public class PhoneFormatFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerHttpExchange exchange, GatewayFilterChain chain) {
        String phone = exchange.getRequest().getQueryParams().getFirst("phone");
        if (phone != null && !ValidX.init().isChinesePhone(phone).passed()) {
            return GatewayErrorResponder.respond(exchange, 400, 10002, "手机号格式不正确");
        }
        return chain.filter(exchange);
    }
}

// 微服务层：完整字段校验（ValidX 注解）
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest req) {
        // 这里的 @Valid + @ChinesePhone 已经做了一次校验
        // Gateway 也做了，但这里不能省略
        return userService.register(req);
    }
}

@Data
public class RegisterRequest {
    @NotBlank(message = "手机号不能为空")
    @ChinesePhone(message = "手机号格式不正确")
    private String phone; // Gateway 已经校验过，这里是纵深防御

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度 8-32 位")
    private String password; // 仅微服务校验（包含业务策略）
}
```

看似重复，实则必要。

---

## 七、性能：高并发场景下的 Pattern 复用与短路

### 7.1 性能瓶颈在哪

Gateway 验证的耗时主要在三处：

1. **字符串读取**（`getQueryParams`、`getHeaders`）—— 微秒级
2. **正则匹配**（`Pattern.matcher`）—— 微秒级
3. **JSON 反序列化**（如果做 Body 校验）—— 毫秒级 ⚠️

第 3 项是 Gateway 性能杀手——**不要在 Gateway 做 Body 反序列化**，除非必要。

### 7.2 ValidX 的预编译优化

ValidX 内部已经把 107 个注解对应的正则全部 **预编译为静态常量**：

```java
// 摘自 AsciiValidator.java
private static final Pattern ASCII_PATTERN = Pattern.compile("^[\\x00-\\x7F]+$");

// 摘自 ChinesePhoneValidator.java  
private static final Pattern CHINESE_PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
```

这意味着：

- **无每次编译开销**——`Pattern.matcher(input)` 直接复用；
- **线程安全**——`Matcher` 每次调用都新建，但 `Pattern` 本身是无状态的；
- **高并发友好**——单实例可服务万级 QPS。

### 7.3 短路策略

校验失败的请求应该**立即拒绝**，不要再走任何后续逻辑：

```java
// 正确写法：短路
if (phone != null && !ValidX.init().isChinesePhone(phone).passed()) {
    return GatewayErrorResponder.respond(exchange, 400, 10002, "手机号格式错误");
}

// 错误写法：先收集所有错误再统一返回
List<String> errors = new ArrayList<>();
errors.addAll(ValidX.init().isChinesePhone(phone).getErrors());
errors.addAll(ValidX.init().isEmail(email).getErrors());
if (!errors.isEmpty()) { ... } // ← 不要这样写
```

**理由**：在 Gateway 层，**第一条错误就足以拒绝**。后端收集所有错误是给前端表单用的，Gateway 没用——越早拒绝，资源占用越少。

### 7.4 基准测试

下面是 4 核 8G 机器上的简单基准（仅供参考）：

| 验证方式 | QPS | 平均延迟 | P99 |
|---------|-----|---------|-----|
| 无验证 | 50,000+ | < 1ms | < 5ms |
| ValidX 单字段（手机号）| 45,000+ | 1ms | 3ms |
| ValidX 5 字段 | 38,000 | 2ms | 5ms |
| Bean Validation 全 DTO | 8,000 | 12ms | 30ms |

**结论**：ValidX 链式 API 在 Gateway 层的开销几乎可忽略；Bean Validation 做 Body 校验开销是 ValidX 的 5-10 倍。

---

## 八、实战案例：电商 Gateway 验证全链路

### 8.1 业务背景

一个典型电商系统的 Gateway 需要校验的字段：

| 路由 | 方法 | 参数 | 校验规则 |
|------|------|------|---------|
| `/api/users/register` | POST | phone, password, smsCode | 手机号格式、密码强度、6位短信码 |
| `/api/orders/create` | POST | items[], addressId, couponId | 收货地址 ID 格式、券码格式 |
| `/api/products/search` | GET | keyword, page, size | 关键词字符集、page/size 范围 |
| `/api/pay/checkout` | POST | orderId, payMethod, returnUrl | 订单 ID 格式、URL 协议 |

### 8.2 完整实现

```java
@Component
public class EcommerceGatewayFilter implements GlobalFilter, Ordered {

    private static final Pattern ORDER_ID = Pattern.compile("^\\d{19}$");
    private static final Pattern COUPON_ID = Pattern.compile("^[A-Z0-9]{12}$");

    @Override
    public Mono<Void> filter(ServerHttpExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();
        ValidX v = ValidX.init().withLocale(Locale.SIMPLIFIED_CHINESE);

        // 路由分发
        if (path.startsWith("/api/users/register") && method == HttpMethod.POST) {
            return validateRegister(exchange, chain, request, v);
        }
        if (path.startsWith("/api/orders/create") && method == POST) {
            return validateCreateOrder(exchange, chain, request, v);
        }
        if (path.startsWith("/api/products/search")) {
            return validateSearch(exchange, chain, request, v);
        }
        if (path.startsWith("/api/pay/checkout")) {
            return validateCheckout(exchange, chain, request, v);
        }

        return chain.filter(exchange); // 其他路由放行
    }

    private Mono<Void> validateRegister(ServerHttpExchange exchange,
                                         GatewayFilterChain chain,
                                         ServerHttpRequest request, ValidX v) {
        MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();

        String phone = params.getFirst("phone");
        if (phone == null || !v.isChinesePhone(phone).passed()) {
            return GatewayErrorResponder.respond(exchange, 400, 10002, "手机号格式不正确");
        }

        String smsCode = params.getFirst("smsCode");
        if (smsCode == null || !v.isFixedLength(smsCode, 6).passed()) {
            return GatewayErrorResponder.respond(exchange, 400, 10004, "短信验证码必须为 6 位");
        }

        String password = params.getFirst("password");
        // 密码长度校验（强度由微服务校验）
        if (password == null || password.length() < 8 || password.length() > 32) {
            return GatewayErrorResponder.respond(exchange, 400, 10005, "密码长度需在 8-32 位");
        }

        return chain.filter(exchange);
    }

    private Mono<Void> validateCreateOrder(ServerHttpExchange exchange, ...) {
        // 同样模式：地址 ID、券码格式校验
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
```

### 8.3 路由配置

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
        - id: pay-service
          uri: lb://pay-service
          predicates:
            - Path=/api/pay/**
```

### 8.4 监控埋点

建议在 Gateway 验证拒绝时打点，方便后续优化：

```java
private static final Counter REJECT_COUNTER = Counter.build()
        .name("gateway_validation_rejected_total")
        .labelNames("path", "code")
        .register();

private Mono<Void> reject(ServerHttpExchange exchange, int businessCode, String message) {
    REJECT_COUNTER.labels(
        exchange.getRequest().getURI().getPath(),
        String.valueOf(businessCode)
    ).inc();

    // ... 返回错误响应
}
```

接入 Prometheus 后，可以看到：

```
gateway_validation_rejected_total{path="/api/users/register",code="10002"} 1523
```

这个指标能告诉你**哪些接口最常被脏请求打**——通常这是前端或客户端代码有 bug 的信号。

---

## 总结

API Gateway 层的验证不是一个"加不加都无所谓"的装饰品，而是**整个微服务系统的第一道防线**。本文从实战出发，把 Gateway 验证的核心要点梳理成一句话：

> **Gateway 做格式预检，微服务做业务校验，二者接力，纵深防御。**

### 核心要点回顾

| 维度 | 要点 |
|------|------|
| **验证职责** | Gateway = 格式合法性；微服务 = 业务合法性 |
| **验证方式** | ValidX 链式 API（不反序列化，原始字符串直校验） |
| **错误响应** | 统一 4xx JSON 格式，含业务码 + 链路 ID |
| **位置选择** | GET 参数/Header → Gateway；POST Body → 微服务 |
| **性能** | ValidX Pattern 已预编译，单字段微秒级，万级 QPS 无压力 |
| **纵深防御** | Gateway 校验 ≠ 微服务不校验，二者必须同时存在 |

### ValidX 在 Gateway 层的核心优势

1. **107 个注解覆盖中国业务场景**——手机号、身份证、银行卡、统一社会信用代码、IP、URL、域名等；
2. **链式 API 无需 DTO**——直接对 `String` / `Object` 校验，省去反序列化；
3. **静态 Pattern 预编译**——高并发友好；
4. **9 语言 i18n**——错误消息天然支持国际化；
5. **代码量极少**——单字段校验 1 行代码搞定。

### 与本文相关的系列文章

- 《ValidX 在微服务架构中的验证策略》：讲解了微服务整体的验证分层，Gateway 是其中一层；
- 《ValidX 在 DDD 领域驱动设计中的实践》：讲解了领域层的自校验值对象，与 Gateway 层的预检互补；
- 《ValidX 错误消息国际化完全指南》：i18n 消息机制详解，本文用到的 9 语言支持由此而来；
- 《ValidX 性能基准测试》：更详细的性能数据，验证"ValidX 在 Gateway 层开销可忽略"。

### 最后的提醒

**Gateway 验证不是越多越好**。过度的 Gateway 校验会让所有请求都变慢，反而拖垮系统。建议：

1. **只校验高频字段**——手机号、邮箱、ID、URL 这类"明显会错"的字段；
2. **不要校验业务规则**——黑名单、白名单、限流交给专门组件（Redis、过滤器链）；
3. **做好监控**——被拒绝的请求统计是优化前端表单的最直接依据。

用对工具、做对位置，Gateway 验证才能真正成为系统的"加速器"而非"绊脚石"。

---

## 项目地址

- GitHub: <https://github.com/vipxieliang/validx>
- Gitee: <https://gitee.com/vipxieliang/validx>
