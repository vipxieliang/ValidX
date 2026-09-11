# ValidX与Maven/Gradle集成配置指南

## 📋 目录
- [引言](#引言)
- [一、坐标与版本](#一坐标与版本)
- [二、Maven 集成](#二maven-集成)
- [三、Gradle 集成](#三gradle-集成)
- [四、Spring Boot 场景的依赖细节](#四spring-boot-场景的依赖细节)
- [五、验证安装成功：冒烟测试](#五验证安装成功冒烟测试)
- [六、常见集成问题排查](#六常见集成问题排查)
- [总结](#总结)
- [项目地址](#项目地址)

---

## 引言

大多数"快速开始"只丢给你一行依赖坐标，但在真实工程里，集成 ValidX 往往要处理三件文档之外的事：

1. **坐标怎么填**——groupId、artifactId、当前版本是什么？中央仓库与镜像怎么选？
2. **传递依赖带来了什么**——ValidX 是一个"自带引擎"的验证库，引入它会把 Bean Validation API、Hibernate Validator、EL 实现一并带进依赖树，这些和 Spring Boot 自带的会不会打架？
3. **装完怎么证明装好了**——如何用一条命令/一个测试确认集成没有版本错乱？

本文面向 Maven 与 Gradle 两个生态，把 ValidX 的集成配置从头讲透，并给出可照抄的完整配置。

---

## 一、坐标与版本

ValidX 已发布到 **Maven Central（Sonatype OSSRH）**，坐标信息如下：

| 项 | 值 |
|----|----|
| groupId | `io.github.vipxieliang` |
| artifactId | `validx` |
| 最新版本 | `1.2.0` |
| 最低 JDK | Java 8（字节码兼容 1.8） |
| 许可证 | Apache License 2.0 |
| JAR 体积 | 约 300KB |

两个版本管理建议：

- **固定版本号**：生产项目务必写死具体版本（如 `1.2.0`），不要使用 `LATEST`/`RELEASE` 这类动态版本——ValidX 在 1.1.0、1.2.0 都有过链式 API 的参数调整（如 `isStartsWith(String[])` 改为 `isStartsWith(String)`），动态版本会把破坏性变更悄悄带到生产环境。
- **升级前看迁移指南**：跨大版本升级前，先在中央仓库核对发布说明（`docs/version/<版本>/` 下同步有中文更新日志与迁移指南）。

---

## 二、Maven 集成

### 1. 添加依赖

在 `pom.xml` 中加入：

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.2.0</version>
</dependency>
```

**不需要**再手动添加 `validation-api`、`hibernate-validator`、`javax.el`——ValidX 内部已将它们作为编译依赖打包进传递依赖（版本经过其自身测试对齐），见下节依赖树。

### 2. 查看真实依赖树

添加后执行：

```bash
mvn dependency:tree -Dincludes=io.github.vipxieliang,org.hibernate.validator,javax.validation,org.glassfish
```

可以看到 ValidX 带入的完整依赖：

```
[INFO] +- io.github.vipxieliang:validx:jar:1.2.0:compile
[INFO] |  +- javax.validation:validation-api:jar:2.0.1.Final:compile
[INFO] |  +- org.hibernate.validator:hibernate-validator:jar:6.1.5.Final:compile
[INFO] |  \- org.glassfish:javax.el:jar:3.0.0:compile
```

这正是"只加一个依赖、开箱即用"的由来：注解模式要跑起来需要三样东西——JSR-380 规范 API（`validation-api`）、校验器实现（Hibernate Validator）、以及 Hibernate Validator 计算消息表达式所依赖的 EL 实现（`javax.el`），ValidX 全部帮你带齐了。

### 3. 中国大陆网络加速（推荐）

从 Maven Central 拉取在国内经常超时，建议在 `~/.m2/settings.xml` 配置阿里云公共镜像：

```xml
<settings>
    <mirrors>
        <mirror>
            <id>aliyunmaven</id>
            <mirrorOf>central</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
```

镜像对 Gradle 同样生效（Gradle 复用了同一套本地 Maven 仓库时）。

### 4. 多模块工程

建议在**父 POM 的 `dependencyManagement`** 中声明版本，各子模块按需引入，避免版本漂移：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.vipxieliang</groupId>
            <artifactId>validx</artifactId>
            <version>1.2.0</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

之后子模块引入时无需再写版本号，升级只改父 POM 一处。

---

## 三、Gradle 集成

### 1. Groovy DSL（build.gradle）

```groovy
dependencies {
    implementation 'io.github.vipxieliang:validx:1.2.0'
}
```

### 2. Kotlin DSL（build.gradle.kts）

```kotlin
dependencies {
    implementation("io.github.vipxieliang:validx:1.2.0")
}
```

同样地，`validation-api`、`hibernate-validator`、`javax.el` 会作为传递依赖自动进入编译与运行 classpath，无需手工逐个声明。

### 3. 查看依赖树

```bash
gradle dependencies --configuration compileClasspath
```

输出中会看到：

```
compileClasspath
+--- io.github.vipxieliang:validx:1.2.0
|    +--- javax.validation:validation-api:2.0.1.Final
|    +--- org.hibernate.validator:hibernate-validator:6.1.5.Final
|    \--- org.glassfish:javax.el:3.0.0
```

### 4. 仓库配置

Gradle 默认仓库通常只有 `mavenCentral()`，国内项目建议叠加阿里云镜像加快拉取：

```groovy
repositories {
    // 优先国内镜像，回退中央仓库
    maven { url 'https://maven.aliyun.com/repository/public' }
    mavenCentral()
}
```

内网环境如自建了 Nexus/Artifactory 代理，把上述 URL 换成内部代理地址即可，坐标不变。

### 5. 版本锁定（可选）

Gradle 7+ 可用 `implementation` + 严格版本约束锁定：

```groovy
dependencies {
    implementation('io.github.vipxieliang:validx:1.2.0') {
        version { strictly '1.2.0' }
    }
}
```

一般固定版本号就够用，严格版本仅在企业安全审计场景需要。

---

## 四、Spring Boot 场景的依赖细节

### 1. 两种使用方式对依赖的需求不同

| 使用方式 | 需要什么 | 典型场景 |
|----------|---------|---------|
| 注解（`@Email`、`@ChineseIdCard`…） | Bean Validation 引擎 + 容器触发 `@Valid` | Spring Boot Controller 参数校验 |
| 链式 API（`ValidX.init().isEmail(...)`） | 仅 ValidX 本体，纯 Java 调用 | Service 层、工具类、脚本 |

**链式 API 不依赖 Spring**，普通 Java 项目引入坐标即可用；注解方式在 Spring Boot 里开箱即用——ValidX 自带 Hibernate Validator，Spring Boot 会自动装配它作为 JSR-380 实现，`@Valid @RequestBody` 无需任何额外配置。

### 2. 与 Spring Boot 自带校验器是否冲突？

Spring Boot 2.x 通过 `spring-boot-starter-web`/`spring-boot-starter-validation` 也会引入 Hibernate Validator。由于 Spring Boot 的依赖管理（BOM）会**统一锁定** `hibernate-validator` 的版本，多数情况下最终只会解析出一个版本（6.1.x 或 6.2.x，与 ValidX 的 6.1.5 同代），两者并不冲突，甚至无需引入 `spring-boot-starter-validation`——ValidX 已把校验器带进来了。

个别场景若出现版本打架（如 `NoSuchMethodError`、奇怪的 `ConstraintViolation` 行为），优先用构建工具统一版本：

**Maven 排除 ValidX 内置的 Hibernate Validator，交给 Spring Boot BOM 统一管理：**

```xml
<dependency>
    <groupId>io.github.vipxieliang</groupId>
    <artifactId>validx</artifactId>
    <version>1.2.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

**Gradle 排除：**

```groovy
dependencies {
    implementation('io.github.vipxieliang:validx:1.2.0') {
        exclude group: 'org.hibernate.validator', module: 'hibernate-validator'
    }
}
```

> 提示：排除后请确保你的工程仍能解析到一个 Hibernate Validator 版本（例如通过 `spring-boot-starter-validation`），否则注解模式会因缺少校验器实现而在运行时抛 `ValidationException`。

### 3. javax 与 jakarta 生态提示

ValidX 1.2.0 构建于 JSR-380（`javax.validation`）体系，与 **Spring Boot 2.x / Java 8~17 的 javax 生态完全匹配**。若你的工程已迁移到 `jakarta.validation`（如 Spring Boot 3.x），引入前请先做一次注解模式的冒烟测试确认兼容性（或锁定在仍使用 javax 的 Spring Boot 2.x 版本线上），不要想当然直接升 Boot 3。

---

## 五、验证安装成功：冒烟测试

集成是否成功，用一个 JUnit 5 测试三分钟见分晓——分别覆盖注解模式与链式模式：

```java
import jakarta.validation.Validator; // Spring Boot 3 使用 jakarta；javax 生态换成 javax.validation.Validator

class ValidXSmokeTest {

    // 注解模式：需要 Validation API + Hibernate Validator（ValidX 已传递带入）
    @Test
    void annotationMode() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        UserDTO dto = new UserDTO();
        dto.setIdCard("11010519491231002X");   // 合法身份证
        dto.setPhone("13800138000");           // 合法手机号
        dto.setEmail("test@example.com");

        assertTrue(validator.validate(dto).isEmpty());

        dto.setPhone("12345");                 // 非法手机号
        assertFalse(validator.validate(dto).isEmpty());
    }

    // 链式模式：仅依赖 ValidX 本体
    @Test
    void chainMode() {
        ValidX v = ValidX.init()
                .field("手机号").isChinesePhone("13800138000")
                .field("身份证").isChineseIdCard("11010519491231002X");
        assertTrue(v.passed());
    }
}
```

其中 `UserDTO` 用上一节最常见的声明：

```java
public class UserDTO {
    @NotBlank
    @ChineseIdCard
    private String idCard;

    @NotBlank
    @ChinesePhone
    private String phone;

    @Email
    private String email;
    // getters / setters
}
```

运行命令：

```bash
# Maven
mvn test -Dtest=ValidXSmokeTest

# Gradle
gradle test --tests ValidXSmokeTest
```

两个用例全绿，说明坐标、传递依赖、校验器装配全部正常；红灯则按下一节排查。

---

## 六、常见集成问题排查

| 现象 | 原因 | 解决 |
|------|------|------|
| `Could not find io.github.vipxieliang:validx` / 拉取 401、404 | 版本号拼写错误、或私有仓库未配置 Central 代理 | 核对坐标为 `1.2.0`；确认仓库含 `mavenCentral()`/镜像 |
| 下载极慢或超时 | 直连 Maven Central 网络不稳 | 配置阿里云镜像（见上文 Maven/Gradle 镜像段） |
| `NoClassDefFoundError: javax/validation/...` | `validation-api` 被意外排除或作用域错误 | 检查排除项；ValidX 已带 `validation-api:2.0.1.Final`，不要手动排除 |
| `NoSuchMethodError: org.hibernate.validator...` | 与 Spring Boot BOM 锁定的 HV 版本不一致 | 用 BOM 统一版本，或排除 ValidX 内置 HV 交给容器管理 |
| 升级到 1.2.0 后行为没变 | 本地仓库/IDE 缓存了旧版本（1.1.0） | Maven：`mvn -U`；Gradle：`--refresh-dependencies`；IDE 重新导入 |
| 只在父模块加了依赖，子模块报类找不到 | 传递依赖只对本模块生效 | 子模块显式声明，或在父 POM `dependencyManagement` 统一管理后各子模块引入 |
| 注解模式运行时报缺少 EL 实现 | Hibernate Validator 6.x 需要 EL 表达式实现 | 确认 `org.glassfish:javax.el:3.0.0` 未被排除（ValidX 默认已带） |

---

## 总结

| 主题 | 结论 |
|------|------|
| 坐标 | `io.github.vipxieliang:validx:1.2.0`（Maven Central，Java 8+） |
| 一条依赖 | ValidX 自带 `validation-api` + Hibernate Validator + `javax.el`，无需重复引入 |
| Maven | 直接加 `<dependency>`，国内配阿里云镜像，多模块用 `dependencyManagement` |
| Gradle | `implementation 'io.github.vipxieliang:validx:1.2.0'`，镜像同 Maven |
| Spring Boot | 注解模式开箱即用；链式 API 不依赖 Spring；版本打架时用 BOM/排除统一 |
| 验证 | 一个 JUnit 冒烟测试同时验证注解与链式两条链路 |
| 升级 | 固定版本号，跨版本先查迁移指南 |

集成 ValidX 的核心心法是：**你只需要声明一个坐标，引擎、规范、EL 实现它都替你管好了**；真正要操心的只剩"我的 Spring Boot 版本管理会不会和它内置的 Hibernate Validator 打架"这一件事，而这件事用 BOM 或 exclude 一行就能解决。

---

## 项目地址

- **GitHub**：https://github.com/vipxieliang/ValidX
- **Gitee**：https://gitee.com/vipxieliang/ValidX
- **Maven Central**：https://central.sonatype.com/artifact/io.github.vipxieliang/validx
