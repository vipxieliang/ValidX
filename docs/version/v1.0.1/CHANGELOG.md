# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-07-31

### Added
- **新增 @Contains 验证注解** - 支持字符串包含子串验证
  - 支持 OR 逻辑（匹配任意一个子字符串）和 AND 逻辑（必须匹配所有子字符串）
  - 支持忽略大小写选项 (`ignoreCase`)
  - 支持多个子字符串匹配 (`value` 数组)
  - 提供链式 API：`isContains()` 方法
  - 完整的国际化支持（8 种语言）
  - 完善的单元测试覆盖
- **版本追踪** - 在文档速查表中为每个验证注解添加版本号标识
- **开源协议** - 添加 Apache License 2.0

### Changed
- **类名重构** - 将 `ValidaX` 重命名为 `ValidX`，统一项目命名
  - 更新所有文档和示例代码
  - 重命名测试类 `ValidaXConfigTest` → `ValidXConfigTest`
- **文档优化**
  - 优化 README 描述，降低营销化语言
  - 改进文档专业性和客观性
  - 更新所有依赖版本号为 1.0.1

### Fixed
- 修复文档中类名不一致的问题

## [1.0.0] - 2026-07-30

### Added
- **首次发布** - ValidX Java 验证库正式发布
- **90+ 验证注解** - 覆盖中国业务场景的常用验证
  - 基础验证（37个）：字符类型、日期时间、文件、密码等
  - 身份验证（16个）：身份证、护照、手机号、邮箱等
  - 金融验证（7个）：银行卡、股票代码、交易订单号等
  - 教育/职业资格（7个）：学位证书、医师、教师、律师等
  - 网络相关（5个）：IP、域名、MAC地址、URL等
  - 中国特定（11个）：车牌号、快递单号、QQ、微信等
  - 汽车相关（2个）：VIN码、发动机号
  - 图书相关（7个）：ISBN、DOI、ORCID等
  - 手机相关（1个）：IMEI
- **双模式验证** - 支持注解式和链式 API 两种使用方式
- **多语言支持** - 支持 8 种语言的错误消息
  - 简体中文、英语、日语、韩语、法语、德语、西班牙语、俄语
- **灵活的 Null 处理** - 提供全局和局部的 null/空字符串处理策略
  - `ValidXConfig.GLOBAL_NOT_NULL` - 全局拒绝 null
  - `ValidXConfig.GLOBAL_NOT_EMPTY` - 全局拒绝 null 和空字符串
  - `.notNull()`, `.notEmpty()`, `.allowNull()`, `.allowEmpty()` - 局部控制
- **完整文档** - 提供详细的中英文文档
  - 快速开始指南
  - 完整的 API 文档
  - 使用示例和最佳实践
- **企业级质量** - 1300+ 单元测试覆盖

### Features
- 基于 JSR-380 (Bean Validation 2.0) 标准
- 与 Spring Boot 无缝集成
- 轻量级设计（约 300KB）
- 零外部依赖（仅依赖 Bean Validation API）
- 支持自定义字段标签
- 线程安全的验证器设计

---

## 版本说明

### 版本号规则
ValidX 遵循 [语义化版本](https://semver.org/lang/zh-CN/) 规范：

- **主版本号（Major）**：不兼容的 API 修改
- **次版本号（Minor）**：向下兼容的功能性新增
- **修订号（Patch）**：向下兼容的问题修正

### 升级指南

#### 从 1.0.0 升级到 1.0.1

**破坏性变更：**
- 类名从 `ValidaX` 更改为 `ValidX`

**迁移步骤：**
1. 更新 Maven 依赖版本到 1.0.1
2. 全局替换：`ValidaX` → `ValidX`
3. 全局替换：`ValidaXConfigTest` → `ValidXConfigTest`（如果使用了测试类）

**代码示例：**
```java
// 1.0.0 版本
ValidaX validator = ValidaX.init()
    .isEmail("test@example.com");

// 1.0.1 版本
ValidX validator = ValidX.init()
    .isEmail("test@example.com");
```

**新功能：**
- 可以使用新的 @Contains 注解进行字符串包含验证

---

## 贡献指南

如果您在使用过程中发现问题或有改进建议，欢迎：
- 提交 [Issue](https://github.com/vipxieliang/ValidX/issues)
- 提交 [Pull Request](https://github.com/vipxieliang/ValidX/pulls)

## 许可证

ValidX 采用 [Apache License 2.0](LICENSE) 开源协议。
