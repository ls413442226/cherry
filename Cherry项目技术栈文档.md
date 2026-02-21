# Cherry项目技术栈文档

## 项目概述
Cherry是一个基于微服务架构的博客平台，采用前后端分离的设计模式，包含认证服务、博客服务和网关服务等多个模块。

## 核心技术栈

### 后端技术
- **Java 17** - 主要开发语言
- **Spring Boot 3.2.4** - 微服务基础框架
- **Spring Cloud Gateway** - API网关组件
- **Apache Dubbo 3.2.12** - RPC服务框架
- **MyBatis-Plus** - ORM持久层框架
- **MySQL 8.0+** - 关系型数据库
- **Redis** - 缓存和会话存储
- **Zookeeper** - 服务注册与发现

### 安全认证
- **JWT (JSON Web Token)** - 基于jjwt 0.12.6实现
- **HMAC-SHA256** - JWT签名算法
- **全局认证过滤器** - 基于Spring Cloud Gateway实现

### 开发工具与依赖管理
- **Maven** - 项目构建和依赖管理
- **Lombok** - 简化Java代码编写
- **Swagger/OpenAPI 3.0** - API文档生成

### 序列化与通信
- **FastJSON2 2.0.47** - JSON序列化框架
- **Jackson** - JSON处理库

### 日志与监控
- **SLF4J + Logback** - 日志框架
- **MDC (Mapped Diagnostic Context)** - 请求追踪上下文

## 项目模块结构

### 1. cherry-api
- 定义Dubbo服务接口
- 包含AuthService等核心服务接口

### 2. cherry-auth (认证服务)
- 用户登录认证
- JWT令牌生成与验证
- Redis会话管理
- 端口: 18081

### 3. cherry-blog (博客服务)
- 文章管理功能
- CRUD操作实现
- 端口: 18082

### 4. cherry-gateway (网关服务)
- 请求路由转发
- 身份认证拦截
- 请求追踪ID生成
- 端口: 8080

### 5. cherry-commons (公共模块)
- 工具类集合
- JWT工具类
- MD5加密工具
- 验证码生成工具

### 6. cherry-domain (领域模型)
- 数据库实体类
- VO/DTO对象
- 统一响应结果封装

## 核心功能特性

### 认证授权
- 基于JWT的无状态认证
- 刷新令牌机制
- 设备绑定验证
- 角色权限控制

### 微服务架构
- 服务间RPC调用
- 负载均衡
- 服务熔断与降级
- 配置中心集成

### API网关
- 统一路由管理
- 请求拦截与过滤
- 跨域处理
- 限流控制

## 部署环境
- **操作系统**: Windows/Linux
- **JDK版本**: Java 17+
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **服务发现**: Zookeeper

## 开发规范
- 统一的异常处理机制
- 全局响应结果封装
- 请求参数校验
- 日志追踪ID支持
- RESTful API设计风格

---
*文档更新时间: 2024年*
*项目版本: 1.0-SNAPSHOT*
