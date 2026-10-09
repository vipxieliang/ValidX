# ValidX 在 DDD 领域驱动设计中的实践

## 📋 目录
- [概述](#概述)
- [一、DDD 核心概念速览：从贫血模型到富血模型](#一ddd-核心概念速览从贫血模型到富血模型)
- [二、值对象（Value Object）：不可变 + 自校验](#二值对象value-object不可变--自校验)
- [三、实体（Entity）：标识 + 状态校验](#三实体entity标识--状态校验)
- [四、领域服务（Domain Service）：跨实体规则校验](#四领域服务domain-service跨实体规则校验)
- [五、应用服务（Application Service）：编排 + 入口校验](#五应用服务application-service编排--入口校验)
- [六、聚合（Aggregate）：一致性边界内的验证](#六聚合aggregate一致性边界内的验证)
- [七、仓储（Repository）：查询与持久化的校验边界](#七仓储repository查询与持久化的校验边界)
- [八、完整实战：订单领域建模](#八完整实战订单领域建模)
- [九、ValidX 与 DDD 的契合点总结](#九validx-与-ddd-的契合点总结)
- [总结](#总结)
- [项目地址](#项目地址)

---

## 概述

上一篇文章《ValidX 在分层架构中的应用》讲的是传统的 Controller → Service → Repository 三层结构。这种结构在大多数项目中足够用了，但随着业务复杂度上升，一个常见的现象是：**Service 层越来越臃肿，业务逻辑散落在各处，领域概念被稀释在字符串和 SQL 中**。

领域驱动设计（Domain-Driven Design，DDD）的核心思想是：**让业务规则回归到"领域对象"本身，而不是散落在 Service 的方法里**。在 DDD 中：

- 手机号不是 `String`，而是一个**值对象** `PhoneNumber`，它自己知道怎么校验；
- 订单不是 `Map<String, Object>`，而是一个**实体** `Order`，它自己保证状态转换的合法性；
- 复杂的跨实体规则（如"用户信用额度是否足够下单"）由**领域服务** `CreditDomainService` 承载，而不是某个 `if` 散落在 Controller 里。

但 DDD 不是银弹。如果验证逻辑没有统一的工具和标准，很容易出现"每个值对象手写校验、每个实体各自为政、错误信息不统一"的混乱局面。**ValidX 的注解和链式 API，恰好可以作为 DDD 中"自校验"能力的统一基础设施**。

本文从一个完整的 DDD 订单领域模型出发，讲解 ValidX 在值对象、实体、领域服务、聚合、应用服务、仓储各层的具体落地方式，并对比传统分层架构，帮你决定"什么时候该用 DDD、什么时候传统三层就够了"。

---

## 一、DDD 核心概念速览：从贫血模型到富血模型

### 1.1 贫血模型 vs 富血模型

| 特征 | 贫血模型（传统三层） | 富血模型（DDD） |
|------|-------------------|---------------|
| 业务逻辑位置 | 全在 Service | 分散在实体、值对象、领域服务 |
| 数据对象 | 只有 getter/setter | 包含业务行为和校验 |
| 可维护性 | 差（Service 越来越臃肿） | 好（领域概念内聚） |
| 学习成本 | 低 | 较高 |
| 适用场景 | 简单 CRUD | 复杂业务规则 |

### 1.2 DDD 的核心概念

| 概念 | 定义 | 例子 | ValidX 的角色 |
|------|------|------|-------------|
| **值对象** | 无标识、不可变、通过属性定义 | `PhoneNumber`、`Address`、`Money` | 在构造函数中自校验 |
| **实体** | 有唯一标识、可变、有生命周期 | `Order`、`User`、`Product` | 在状态变更时校验 |
| **聚合** | 一组相关实体和值对象的集合 | `Order` + `OrderItem` + `ShippingAddress` | 在聚合根统一校验 |
| **领域服务** | 跨实体或不适合放在实体中的业务逻辑 | `PaymentService`、`CreditCheckService` | 调用链式 API 校验 |
| **应用服务** | 编排领域对象完成用例 | `OrderApplicationService` | 用注解校验入参 |
| **仓储** | 聚合的持久化接口 | `OrderRepository` | 不校验，只做持久化 |

### 1.3 ValidX 在 DDD 中的定位

ValidX 在 DDD 中扮演的是**"领域规则的可复现校验"**工具：

- **值对象**：在构造函数中用 ValidX 链式 API 做**自校验**，不合法的值无法创建；
- **实体**：在状态变更方法中用 ValidX 链式 API 做**状态校验**，保证实体始终处于有效状态；
- **领域服务**：用 ValidX 链式 API 做**跨实体规则校验**，如信用检查、库存校验；
- **应用服务**：用 ValidX 注解做**入参格式校验**，拦截非法请求。

---

## 二、值对象（Value Object）：不可变 + 自校验

### 2.1 什么是值对象

值对象是 DDD 中最小的业务概念单元，它的核心特征是：

- **不可变**：创建后不能修改，如果需要变更，创建新的实例；
- **无标识**：不通过 ID 区分，通过属性值判断相等；
- **自校验**：构造时即保证合法性，外部无法创建"不合法"的值对象。

### 2.2 用 ValidX 实现自校验的值对象

#### 示例 1：手机号值对象

```java
import io.github.vipxieliang.validx.chain.ValidX;

/**
 * 手机号值对象
 * 不可变，构造时即校验格式
 */
public final class PhoneNumber {

    private final String value;

    public PhoneNumber(String value) {
        // 自校验：不合法的手机号无法创建
        ValidX chain = ValidX.init()
                .withLocale(Locale.SIMPLIFIED_CHINESE)
                .field("手机号")
                .isChinesePhone(value);
        
        if (!chain.passed()) {
            throw new IllegalArgumentException(chain.getErrors().get(0));
        }
        
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 值对象通过属性判断相等
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
```

#### 示例 2：身份证号值对象

```java
/**
 * 身份证号值对象
 * 封装身份证的格式校验和基本信息提取
 */
public final class IdCard {

    private final String value;

    public IdCard(String value) {
        ValidX chain = ValidX.init()
                .withLocale(Locale.SIMPLIFIED_CHINESE)
                .field("身份证号")
                .isChineseIdCard(value);
        
        if (!chain.passed()) {
            throw new IllegalArgumentException(chain.getErrors().get(0));
        }
        
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 从身份证号提取出生日期
     */
    public LocalDate getBirthDate() {
        String birthStr = value.substring(6, 14);
        return LocalDate.parse(birthStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 从身份证号提取性别
     */
    public Gender getGender() {
        int genderCode = Character.getNumericValue(value.charAt(16));
        return genderCode % 2 == 0 ? Gender.FEMALE : Gender.MALE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdCard idCard = (IdCard) o;
        return Objects.equals(value, idCard.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

### 2.3 值对象的优势

```java
// 传统写法：String 到处飞，格式校验散落在各处
public class UserService {
    public void createUser(String phone, String idCard) {
        // 校验逻辑重复写在每个方法里
        if (!isValidPhone(phone)) throw new RuntimeException("手机号格式不对");
        if (!isValidIdCard(idCard)) throw new RuntimeException("身份证格式不对");
        // ...
    }
}

// DDD 写法：值对象自校验，构造即保证合法
public class UserService {
    public void createUser(String phoneStr, String idCardStr) {
        PhoneNumber phone = new PhoneNumber(phoneStr);   // 不合法就抛异常
        IdCard idCard = new IdCard(idCardStr);           // 不合法就抛异常
        
        // 到这里，phone 和 idCard 一定合法
        User user = new User(phone, idCard);
        // ...
    }
}
```

### 2.4 常见误区

| 误区 | 问题 | 正确做法 |
|------|------|---------|
| 值对象用 `@Valid` 注解 | `@Valid` 只在 Bean Validation 框架中生效，普通对象构造时不触发 | 在构造函数中手动调用 ValidX 链式 API |
| 值对象可变 | 破坏了值对象的不可变性，导致哈希表等集合行为异常 | 所有字段 `final`，不提供 setter |
| 值对象包含业务状态 | 值对象只应封装"值"本身，不应包含状态机 | 状态机放到实体中 |

---

## 三、实体（Entity）：标识 + 状态校验

### 3.1 什么是实体

实体是 DDD 中**有唯一标识、可变、有生命周期**的对象。与值对象不同：

- 两个 `PhoneNumber("13800138000")` 是相等的（值对象）；
- 两个 `Order` 即使属性完全相同，只要 ID 不同就是不同的实体。

### 3.2 实体的状态校验

实体的核心职责是**维护自己的不变量（Invariants）**——无论外部怎么操作，实体始终处于有效状态。

#### 示例：订单实体

```java
public class Order {

    private final OrderId id;                    // 唯一标识
    private OrderStatus status;                  // 状态
    private final List<OrderItem> items;         // 订单项
    private Money totalAmount;                   // 总金额
    private ShippingAddress shippingAddress;     // 收货地址

    public Order(OrderId id, List<OrderItem> items, ShippingAddress address) {
        this.id = id;
        this.status = OrderStatus.CREATED;
        this.items = new ArrayList<>(items);
        this.shippingAddress = address;
        this.totalAmount = calculateTotal();
        
        // 构造时校验不变量
        validateInvariants();
    }

    /**
     * 支付订单
     */
    public void pay() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有待支付订单才能支付");
        }
        this.status = OrderStatus.PAID;
    }

    /**
     * 发货
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("只有已支付订单才能发货");
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * 修改收货地址（仅限未发货状态）
     */
    public void changeShippingAddress(ShippingAddress newAddress) {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("已发货订单不能修改地址");
        }
        this.shippingAddress = newAddress;
    }

    /**
     * 校验不变量
     */
    private void validateInvariants() {
        ValidX chain = ValidX.init()
                .withLocale(Locale.SIMPLIFIED_CHINESE);

        // 规则 1：订单至少有一个商品
        if (items.isEmpty()) {
            chain.field("订单项").fail("订单至少包含一个商品");
        }

        // 规则 2：总金额必须大于 0
        if (totalAmount == null || totalAmount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            chain.field("总金额").fail("订单总金额必须大于 0");
        }

        // 规则 3：收货地址不能为空
        if (shippingAddress == null) {
            chain.field("收货地址").fail("收货地址不能为空");
        }

        // 规则 4：每个订单项的数量必须大于 0
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getQuantity() <= 0) {
                chain.field("订单项[" + i + "]").fail("商品数量必须大于 0");
            }
        }

        if (!chain.passed()) {
            throw new IllegalArgumentException(String.join("; ", chain.getErrors()));
        }
    }

    private Money calculateTotal() {
        return items.stream()
                .map(item -> item.getPrice().multiply(item.getQuantity()))
                .reduce(Money.ZERO, Money::add);
    }
}
```

### 3.3 实体 vs 值对象的校验差异

| 维度 | 值对象 | 实体 |
|------|--------|------|
| 校验时机 | 构造时 | 构造时 + 状态变更时 |
| 校验内容 | 格式、范围 | 格式、范围 + 状态机 + 跨字段规则 |
| 校验工具 | ValidX 链式 API | ValidX 链式 API + 自定义逻辑 |
| 失败处理 | 抛 `IllegalArgumentException` | 抛 `IllegalStateException` 或 `IllegalArgumentException` |
| 可变性 | 不可变 | 可变，但状态转换受控 |

---

## 四、领域服务（Domain Service）：跨实体规则校验

### 4.1 什么是领域服务

当业务规则**不适合放在任何一个实体或值对象中**时，就需要领域服务。常见场景：

- 规则涉及多个实体（如"下单时检查用户信用额度"）；
- 规则需要外部依赖（如"调用库存服务检查库存"）；
- 规则是纯粹的业务策略（如"满减优惠计算"）。

### 4.2 用 ValidX 实现领域服务

#### 示例：信用检查领域服务

```java
@Service
public class CreditCheckDomainService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CreditCheckDomainService(UserRepository userRepository, 
                                    OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 检查用户是否有足够的信用额度下单
     */
    public void checkCreditLimit(UserId userId, Money orderAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        ValidX chain = ValidX.init()
                .withLocale(Locale.SIMPLIFIED_CHINESE);

        // 规则 1：用户状态必须正常
        if (user.getStatus() != UserStatus.ACTIVE) {
            chain.field("用户状态").fail("用户状态异常，无法下单");
        }

        // 规则 2：信用额度检查
        Money creditLimit = user.getCreditLimit();
        Money usedCredit = orderRepository.calculateUnpaidAmount(userId);
        Money availableCredit = creditLimit.subtract(usedCredit);

        if (orderAmount.compareTo(availableCredit) > 0) {
            chain.field("信用额度")
                 .fail(String.format("可用信用额度不足，当前可用：%s，订单金额：%s",
                       availableCredit, orderAmount));
        }

        // 规则 3：单笔订单金额上限
        if (orderAmount.compareTo(new Money(new BigDecimal("50000"))) > 0) {
            chain.field("订单金额").fail("单笔订单金额不能超过 5 万元");
        }

        if (!chain.passed()) {
            throw new InsufficientCreditException(chain.getErrors());
        }
    }
}
```

### 4.3 领域服务 vs 应用服务

| 维度 | 领域服务 | 应用服务 |
|------|---------|---------|
| 关注点 | 业务规则 | 用例编排 |
| 是否知道 HTTP/事务 | 否 | 是（可能有 `@Transactional`） |
| 校验工具 | ValidX 链式 API | ValidX 注解（入参）+ 链式 API（业务） |
| 返回值 | 领域对象 | DTO / Result |
| 例子 | `CreditCheckDomainService` | `OrderApplicationService` |

---

## 五、应用服务（Application Service）：编排 + 入口校验

### 5.1 什么是应用服务

应用服务是 DDD 中的**用例层**，它负责：

- 接收外部请求（Controller 调用）；
- 编排领域对象完成业务用例；
- 协调领域服务、仓储等基础设施；
- 事务管理。

### 5.2 应用服务的入口校验

应用服务的入参通常是 DTO，这里用 ValidX 注解做**格式校验**，和分层架构中的 Controller 层类似：

```java
public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "收货地址不能为空")
    private String shippingAddress;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @ChinesePhone(message = "收货人手机号格式不正确")
    private String receiverPhone;

    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemDTO> items;
}
```

### 5.3 应用服务的业务编排

```java
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final CreditCheckDomainService creditCheckService;
    private final InventoryDomainService inventoryService;
    private final PaymentDomainService paymentService;

    @Transactional
    public OrderDTO createOrder(@Valid CreateOrderRequest request) {
        // 1. 构造值对象（自校验）
        ShippingAddress address = new ShippingAddress(
                request.getShippingAddress(),
                request.getReceiverName(),
                new PhoneNumber(request.getReceiverPhone())
        );

        // 2. 构造订单项
        List<OrderItem> items = request.getItems().stream()
                .map(item -> new OrderItem(
                        new ProductId(item.getProductId()),
                        item.getQuantity(),
                        new Money(item.getPrice())
                ))
                .collect(Collectors.toList());

        // 3. 构造订单实体（构造时自动校验不变量）
        Order order = new Order(
                OrderId.generate(),
                items,
                address
        );

        // 4. 领域服务：信用检查
        UserId userId = new UserId(request.getUserId());
        creditCheckService.checkCreditLimit(userId, order.getTotalAmount());

        // 5. 领域服务：库存扣减
        inventoryService.decreaseStock(order);

        // 6. 持久化
        orderRepository.save(order);

        // 7. 返回 DTO
        return OrderDTO.from(order);
    }
}
```

### 5.4 应用服务的职责边界

```
应用服务（OrderApplicationService）
    ├── 入参校验（ValidX 注解）
    ├── 构造值对象（PhoneNumber、Money 自校验）
    ├── 构造实体（Order 构造时校验不变量）
    ├── 调用领域服务（CreditCheckDomainService）
    ├── 调用领域服务（InventoryDomainService）
    ├── 持久化（OrderRepository）
    └── 返回 DTO
```

**应用服务不直接处理业务规则，而是编排领域对象完成用例**。业务规则在值对象、实体、领域服务中完成。

---

## 六、聚合（Aggregate）：一致性边界内的验证

### 6.1 什么是聚合

聚合是一组相关实体和值对象的集合，其中有一个**聚合根（Aggregate Root）**，外部只能通过聚合根访问聚合内的对象。

### 6.2 聚合内的校验

聚合的职责是**保证内部一致性**。所有对聚合内对象的修改，都必须通过聚合根进行，聚合根负责统一校验。

```java
public class Order extends AggregateRoot<OrderId> {

    private final List<OrderItem> items;
    private ShippingAddress shippingAddress;
    private OrderStatus status;
    private Money totalAmount;

    /**
     * 添加商品（必须通过聚合根）
     */
    public void addItem(ProductId productId, int quantity, Money price) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有待支付订单才能添加商品");
        }
        
        items.add(new OrderItem(productId, quantity, price));
        recalculateTotal();
        validateInvariants();
    }

    /**
     * 移除商品
     */
    public void removeItem(ProductId productId) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有待支付订单才能移除商品");
        }
        
        items.removeIf(item -> item.getProductId().equals(productId));
        recalculateTotal();
        validateInvariants();
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money.ZERO, Money::add);
    }

    private void validateInvariants() {
        ValidX chain = ValidX.init()
                .withLocale(Locale.SIMPLIFIED_CHINESE);

        // 不变量 1：订单不能为空
        if (items.isEmpty()) {
            chain.field("订单项").fail("订单必须包含至少一个商品");
        }

        // 不变量 2：总金额一致
        Money calculatedTotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money.ZERO, Money::add);
        
        if (!calculatedTotal.equals(totalAmount)) {
            chain.field("总金额").fail("订单总金额计算错误");
        }

        // 不变量 3：每个商品数量大于 0
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getQuantity() <= 0) {
                chain.field("商品[" + i + "]").fail("商品数量必须大于 0");
            }
        }

        if (!chain.passed()) {
            throw new IllegalStateException(String.join("; ", chain.getErrors()));
        }
    }
}
```

### 6.3 聚合的设计原则

| 原则 | 说明 | ValidX 应用 |
|------|------|-----------|
| 聚合内强一致性 | 聚合内所有对象一起成功或失败 | 在聚合根统一校验 |
| 聚合间最终一致性 | 通过领域事件异步处理 | 不直接校验，发事件 |
| 聚合根是唯一点 | 外部只能通过聚合根访问 | 校验逻辑集中在聚合根 |
| 小聚合优先 | 聚合不要太大，避免性能问题 | 校验范围可控 |

---

## 七、仓储（Repository）：查询与持久化的校验边界

### 7.1 仓储的职责

仓储在 DDD 中负责聚合的持久化，但它**不承载业务校验**，只负责：

- 保存聚合；
- 根据 ID 查询聚合；
- 根据条件查询聚合列表。

### 7.2 仓储的校验边界

```java
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaRepository;

    @Override
    public void save(Order order) {
        // 仓储不校验业务规则，只负责持久化
        // 但可以用数据库约束兜底
        jpaRepository.save(OrderEntity.from(order));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.getValue())
                .map(OrderEntity::toDomain);
    }

    @Override
    public List<Order> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue())
                .stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList());
    }
}
```

### 7.3 仓储与分层架构中的 Repository 的区别

| 维度 | 分层架构 Repository | DDD 仓储 |
|------|-------------------|---------|
| 返回对象 | Entity（数据库实体） | 聚合根（领域对象） |
| 校验职责 | 无（或兜底） | 无 |
| 查询能力 | 任意查询 | 只暴露聚合根查询 |
| 事务管理 | 可能在 Service | 在应用服务 |

---

## 八、完整实战：订单领域建模

### 8.1 领域模型图

```
┌─────────────────────────────────────────────────────────────┐
│                      订单聚合（Order Aggregate）              │
│                                                             │
│  ┌──────────────┐                                          │
│  │   OrderId    │                                          │
│  │  (值对象)     │                                          │
│  └──────┬───────┘                                          │
│         │                                                   │
│  ┌──────▼───────┐    ┌──────────────┐    ┌──────────────┐  │
│  │    Order     │1  * │  OrderItem   │    │ShippingAddress│  │
│  │  (聚合根/实体) │────│  (实体)       │    │  (值对象)     │  │
│  │              │    │              │    │              │  │
│  │ - status     │    │ - productId  │    │ - address    │  │
│  │ - totalAmount│    │ - quantity   │    │ - receiver   │  │
│  │ - createdAt  │    │ - price      │    │ - phone      │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                            │
                    ┌───────▼────────┐
                    │  OrderRepository│
                    │   (仓储接口)     │
                    └─────────────────┘
```

### 8.2 完整代码

#### 值对象

```java
// 金额值对象
public final class Money {
    
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount) {
        this(amount, Currency.CNY);
    }

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("币种不一致，无法相加");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("币种不一致，无法相减");
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    // ... getter、equals、hashCode
}
```

#### 实体

```java
// 订单项实体
public class OrderItem {
    
    private final ProductId productId;
    private final int quantity;
    private final Money price;

    public OrderItem(ProductId productId, int quantity, Money price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于 0");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Money getSubtotal() {
        return price.multiply(quantity);
    }

    // ... getter
}
```

#### 聚合根

```java
// 订单聚合根
public class Order extends AggregateRoot<OrderId> {

    private OrderStatus status;
    private final List<OrderItem> items;
    private Money totalAmount;
    private ShippingAddress shippingAddress;
    private final LocalDateTime createdAt;

    public Order(OrderId id, List<OrderItem> items, ShippingAddress shippingAddress) {
        super(id);
        this.status = OrderStatus.CREATED;
        this.items = new ArrayList<>(items);
        this.shippingAddress = shippingAddress;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = calculateTotal();
        
        validateInvariants();
    }

    // ... 业务方法和不变量校验见前文
}
```

#### 应用服务

```java
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final CreditCheckDomainService creditCheckService;
    private final InventoryDomainService inventoryService;

    @Transactional
    public OrderDTO createOrder(@Valid CreateOrderRequest request) {
        // 构造值对象
        ShippingAddress address = new ShippingAddress(
                request.getShippingAddress(),
                request.getReceiverName(),
                new PhoneNumber(request.getReceiverPhone())
        );

        // 构造订单项
        List<OrderItem> items = request.getItems().stream()
                .map(item -> new OrderItem(
                        new ProductId(item.getProductId()),
                        item.getQuantity(),
                        new Money(item.getPrice())
                ))
                .collect(Collectors.toList());

        // 构造聚合根（构造时自动校验不变量）
        Order order = new Order(OrderId.generate(), items, address);

        // 领域服务：信用检查
        creditCheckService.checkCreditLimit(
                new UserId(request.getUserId()),
                order.getTotalAmount()
        );

        // 领域服务：库存扣减
        inventoryService.decreaseStock(order);

        // 持久化
        orderRepository.save(order);

        return OrderDTO.from(order);
    }
}
```

---

## 九、ValidX 与 DDD 的契合点总结

### 9.1 ValidX 在 DDD 各层的作用

| DDD 层 | 校验内容 | ValidX 工具 |
|--------|---------|-----------|
| **值对象** | 格式、范围、业务规则 | 链式 API（构造函数中） |
| **实体** | 不变量、状态机 | 链式 API（业务方法中） |
| **领域服务** | 跨实体规则、外部依赖 | 链式 API |
| **应用服务** | 入参格式 | 注解（`@Valid`） |
| **聚合** | 内部一致性 | 链式 API（聚合根中） |
| **仓储** | 无（数据库约束兜底） | 无 |

### 9.2 DDD vs 传统分层架构

| 维度 | 传统分层架构 | DDD |
|------|-----------|-----|
| 业务逻辑位置 | Service | 实体、值对象、领域服务 |
| 复杂度 | 低 | 较高 |
| 可维护性（复杂业务） | 差 | 好 |
| 团队规模要求 | 小团队即可 | 需要领域专家 |
| ValidX 使用方式 | 注解为主 | 链式 API 为主 |
| 适用场景 | CRUD、简单业务 | 复杂业务规则 |

### 9.3 什么时候用 DDD

| 场景 | 推荐 |
|------|------|
| 简单 CRUD，无复杂业务规则 | 传统三层 |
| 业务规则内聚在单个表 | 传统三层 + ValidX 注解 |
| 业务规则复杂，涉及多实体协作 | **DDD + ValidX 链式 API** |
| 需要频繁重构业务逻辑 | **DDD**（领域对象独立，重构影响面小） |
| 团队有领域专家 | **DDD** |

---

## 总结

本文从 DDD 的核心概念出发，讲解了 ValidX 在值对象、实体、领域服务、聚合、应用服务、仓储各层的具体应用：

- **值对象**：在构造函数中用 ValidX 链式 API 实现**自校验**，保证不可变性；
- **实体**：在状态变更方法中用 ValidX 链式 API 维护**不变量**；
- **领域服务**：用 ValidX 链式 API 处理**跨实体规则**；
- **聚合**：在聚合根中统一校验**内部一致性**；
- **应用服务**：用 ValidX 注解做**入参格式校验**，编排领域对象完成用例；
- **仓储**：不承载业务校验，只做持久化。

ValidX 的注解和链式 API，恰好覆盖了 DDD 中"格式校验"和"业务规则校验"的两个层面。在值对象和实体中用链式 API 保证内聚性，在应用服务中用注解保证入口安全——两者结合，构成了一套完整的 DDD 校验体系。

```
┌─────────────────────────────────────────────────────────────┐
│                      ValidX + DDD                            │
│                                                             │
│   应用服务层    @Valid + ValidX 注解  →  入口格式拦截         │
│       ↓                                                     │
│   领域服务层    ValidX 链式 API        →  跨实体规则校验       │
│       ↓                                                     │
│   实体/聚合层   ValidX 链式 API        →  不变量维护          │
│       ↓                                                     │
│   值对象层      ValidX 链式 API        →  自校验             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

本文与系列其他文章相互衔接：
- 《ValidX 在分层架构中的应用：Controller、Service、Repository》（week14 第 61 篇）讲解了传统三层架构中的验证分工，可与本文的 DDD 实践对照阅读；
- 《ValidX 自定义验证注解开发入门》（week10）讲解了如何自定义注解，可用于扩展 DDD 中的领域特定校验；
- 《ValidX 分组验证详解：不同场景使用不同验证规则》（week12）讲解了分组验证，可用于 DDD 中不同用例的校验策略。

## 项目地址

- GitHub: <https://github.com/vipxieliang/validx>
- Gitee: <https://gitee.com/vipxieliang/validx>
