# 更新日志

本文档记录 ValidX 项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.1] - 2026-07-31

### 新增 ✨
- **@Contains 验证注解** - 字符串包含子串验证
  - 支持 OR 逻辑（包含任意一个子字符串即可）
  - 支持 AND 逻辑（必须包含所有子字符串）
  - 支持忽略大小写匹配 (`ignoreCase`)
  - 支持多个子字符串数组匹配
  - 提供链式调用方法：`isContains()`
  - 完整的多语言支持（8 种语言）
  - 完善的测试用例覆盖

  **使用示例：**
  ```java
  // 注解方式 - OR 逻辑（任意匹配）
  @Contains({"@"})
  private String email;

  // 注解方式 - AND 逻辑（全部匹配）
  @Contains(value = {"@", "."}, matchAll = true)
  private String strictEmail;

  // 链式方式
  ValidX.init()
      .isContains("test@example.com", new String[]{"@", "."});
  ```

- **版本追踪功能** - 在文档速查表中标注每个验证注解的引入版本
- **开源协议文件** - 添加 Apache License 2.0 许可证

### 变更 🔄
- **核心类重命名** - `ValidaX` → `ValidX`
  - 更简洁、更统一的命名
  - 更新所有相关文档、示例代码
  - 测试类同步重命名：`ValidaXConfigTest` → `ValidXConfigTest`

- **文档优化**
  - 降低营销化语言，提升专业性
  - 优化描述的客观性和准确性
  - 统一更新依赖版本号为 1.0.1
  - 改进代码示例和使用说明

### 修复 🐛
- 修复文档中类名不一致的问题
- 统一项目命名规范

## [1.0.0] - 2026-07-30

### 新增 ✨
- **🎉 首次发布** - ValidX Java 验证库正式版
- **90+ 验证注解** - 全面覆盖中国业务场景

  **基础验证（37个）**
  - 字符类型：Alpha, AlphaNumber, Chinese, ChineseAlpha 等
  - 日期时间：FutureDate, PastDate, HourMinute, Timestamp, CronExpression 等
  - 文件相关：FileExtension, FileSize
  - 其他：Password, UUID, Base64, JSON, JWT, SemVer, Color 等

  **身份验证相关（16个）**
  - 中国证件：身份证、护照、军官证、士兵证
  - 港澳台证件：港澳居住证、台湾居住证、通行证
  - 联系方式：手机号、座机号、邮箱
  - 其他：外国人永居证、工作许可证、统一社会信用代码

  **金融验证（7个）**
  - 银行卡号（支持 Luhn 算法）
  - CVV/CVC 安全码
  - IBAN、SWIFT 国际代码
  - 股票代码、交易订单号、金融产品代码

  **教育/职业资格（7个）**
  - 学位证书、医师资格证、教师资格证
  - 法律职业资格证、PMP 证书
  - 建造师证书、会计资格证书

  **网络相关（5个）**
  - IP 地址（IPv4/IPv6）
  - 域名、MAC 地址、URL、子网掩码

  **中国特定验证（11个）**
  - 车牌号、邮政编码、专利号
  - 快递单号、QQ、微信
  - 商标注册号、软件著作权、作品著作权
  - 药品批准文号、药品本位码、医疗器械注册证号

  **其他分类**
  - 汽车相关（2个）：VIN 码、发动机号
  - 图书相关（7个）：ISBN、ISSN、DOI、CLC、DDC、ORCID、IPC
  - 手机相关（1个）：IMEI

- **双模式验证** - 灵活适应不同场景
  - **注解模式** - 适用于 DTO 对象验证
    ```java
    public class UserDTO {
        @ChineseIdCard
        private String idCard;

        @ChinesePhone
        private String phone;
    }
    ```
  - **链式 API 模式** - 适用于动态验证
    ```java
    ValidX validator = ValidX.init()
        .field("身份证").isChineseIdCard(idCard)
        .field("手机号").isChinesePhone(phone);
    ```

- **国际化支持** - 8 种语言的错误消息
  - 🇨🇳 简体中文（默认）
  - 🇺🇸 英语
  - 🇯🇵 日语
  - 🇰🇷 韩语
  - 🇫🇷 法语
  - 🇩🇪 德语
  - 🇪🇸 西班牙语
  - 🇷🇺 俄语

- **灵活的 Null 处理策略**
  - **全局配置**
    ```java
    ValidX.init()
        .config(ValidXConfig.GLOBAL_NOT_NULL)  // 全局拒绝 null
        .config(ValidXConfig.GLOBAL_NOT_EMPTY) // 全局拒绝 null 和空字符串
    ```
  - **局部控制**
    ```java
    ValidX.init()
        .notNull().isEmail(email)      // 此字段不能为 null
        .allowNull().isQQ(qq)          // 此字段允许 null
        .notEmpty().isWeChat(wechat)   // 此字段不能为 null 或空字符串
    ```

- **完整文档体系**
  - 📘 中英文 README
  - 🚀 快速开始指南
  - 📖 完整 API 文档
  - 💡 使用示例和最佳实践
  - 🆚 与其他库的对比（如 Hutool）

- **企业级质量保障**
  - ✅ 1300+ 单元测试
  - ✅ 完整的测试覆盖
  - ✅ 生产环境验证

### 特性亮点 🌟
- 基于 JSR-380 (Bean Validation 2.0) 标准
- 与 Spring Boot 无缝集成，开箱即用
- 轻量级设计（约 300KB）
- 零外部依赖（仅依赖 Bean Validation API）
- 支持自定义字段标签和错误消息
- 线程安全的验证器设计
- 类型安全的注解验证

---

## 📋 版本说明

### 版本号规则
ValidX 遵循 [语义化版本 2.0.0](https://semver.org/lang/zh-CN/) 规范：

- **主版本号（Major）**：当你做了不兼容的 API 修改
- **次版本号（Minor）**：当你做了向下兼容的功能性新增
- **修订号（Patch）**：当你做了向下兼容的问题修正

### 🔄 升级指南

#### 从 1.0.0 升级到 1.0.1

**⚠️ 破坏性变更：**
- 核心类名从 `ValidaX` 更改为 `ValidX`

**升级步骤：**

1. **更新 Maven 依赖**
   ```xml
   <dependency>
       <groupId>io.github.vipxieliang</groupId>
       <artifactId>validx</artifactId>
       <version>1.0.1</version>  <!-- 更新版本号 -->
   </dependency>
   ```

2. **全局替换类名**
   - 查找并替换：`ValidaX` → `ValidX`
   - 如果有测试类引用：`ValidaXConfigTest` → `ValidXConfigTest`

3. **验证和测试**
   ```bash
   # 重新编译项目
   mvn clean compile

   # 运行测试
   mvn test
   ```

**迁移示例：**
```java
// ❌ 1.0.0 版本（旧）
ValidaX validator = ValidaX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail("test@example.com");

// ✅ 1.0.1 版本（新）
ValidX validator = ValidX.init()
    .config(ValidXConfig.GLOBAL_NOT_NULL)
    .isEmail("test@example.com");
```

**新功能体验：**
```java
// 🎉 使用新增的 @Contains 注解
@Contains(value = {"@", "."}, matchAll = true)
private String email;

// 或使用链式 API
ValidX.init()
    .isContains("test@example.com", new String[]{"@", "."}, false, true);
```

---

## 🤝 贡献

欢迎参与 ValidX 项目的开发和改进：

- 🐛 **报告问题** - [提交 Issue](https://github.com/vipxieliang/ValidX/issues)
- 💡 **功能建议** - [功能请求](https://github.com/vipxieliang/ValidX/issues/new)
- 🔧 **代码贡献** - [提交 Pull Request](https://github.com/vipxieliang/ValidX/pulls)
- 📖 **文档改进** - 帮助完善文档和示例

## 📜 许可证

ValidX 采用 [Apache License 2.0](LICENSE) 开源协议。

---

## 🔗 相关链接

- **GitHub 仓库**: https://github.com/vipxieliang/ValidX
- **Maven Central**: https://central.sonatype.com/artifact/io.github.vipxieliang/validx
- **问题反馈**: https://github.com/vipxieliang/ValidX/issues
- **文档首页**: [README.cn.md](README.cn.md)
