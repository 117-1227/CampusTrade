# CampusTrade — 校园二手交易平台

> **论文题目：** 基于 Spring Boot + Vue 的校园二手交易平台设计与实现
>
> 面向高校学生群体，设计并实现 B/S 架构的校园二手交易平台，涵盖用户端（商品发布/浏览/搜索/收藏/私信/下单）和管理端（审核/用户管理/分类管理/数据概览），旨在提高校园闲置物品流通效率，降低学生交易成本。

---

## 目录

- [技术栈](#技术栈)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [管理员账号](#管理员账号)
- [功能清单](#功能清单)
- [页面路由](#页面路由)
- [API 总览](#api-总览)
- [项目结构](#项目结构)
- [数据库表](#数据库表)
- [测试](#测试)
- [命令速查](#命令速查)
- [部署](#部署)
- [延后功能](#延后功能)
- [文档索引](#文档索引)

---

## 技术栈

| 层 | 技术 | 版本 | 说明 |
|----|------|------|------|
| 前端框架 | Vue 3 | 3.4+ | Composition API (`<script setup>`) |
| 前端语言 | TypeScript | 5.4+ | 全量类型覆盖 |
| 构建工具 | Vite | 5.2+ | 开发热更新 + 生产打包 |
| UI 组件库 | Element Plus | 2.7+ | 表格/表单/上传/图片/卡片 |
| 前端路由 | Vue Router | 4.3+ | ClientLayout + AdminLayout 双嵌套 |
| 前端状态 | Pinia | 2.1+ | authStore + categoryStore |
| HTTP 客户端 | Axios | 1.6+ | 拦截器自动注入 JWT token |
| 后端框架 | Spring Boot | 3.2.5 | Java 17+ |
| ORM | MyBatis-Plus | 3.5.6 | BaseMapper 零 SQL 基础 CRUD |
| 认证 | Spring Security + JWT | 6.2+ | 无状态 Bearer Token |
| JWT 库 | jjwt | 0.12.5 | HMAC-SHA256 签名 |
| 数据库 | MySQL | 8.0 | utf8mb4 编码 |
| 开发备选 | H2 | 2.x | 内存模式，免安装启动 |
| 构建工具 | Maven | 3.9+ | Maven Wrapper 免全局安装 |

## 环境要求

| 软件 | 最低版本 | 用途 |
|------|----------|------|
| JDK | 17+ | 后端编译运行 |
| Node.js | 18+ | 前端构建 |
| MySQL | 8.0 | 数据持久化（H2 可替代开发） |

---

## 快速开始

### 1. 数据库准备（MySQL 模式）

```sql
CREATE DATABASE IF NOT EXISTS campus_trade
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

配置连接信息：编辑 `server/src/main/resources/application-mysql.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trade?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

### 2. 启动后端（默认 MySQL）

```bash
cd server

# Windows CMD
mvnw.cmd spring-boot:run

# Linux / macOS / Git Bash
./mvnw spring-boot:run
```

后端默认监听 `http://localhost:8080`。首次启动自动创建表结构、分类种子数据和管理员账号。

### 3. 启动前端

```bash
cd client
npm install
npm run dev
```

前端监听 `http://localhost:5173`，Vite 自动代理 `/api` 到后端。

### 4. 可选：H2 免 MySQL 启动

```bash
# Windows CMD
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2

# Linux / macOS / Git Bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

H2 模式下数据存储在内存中，每次重启清空。H2 控制台：`http://localhost:8080/h2-console`（JDBC URL: `jdbc:h2:mem:campustrade`，用户名 `sa`，密码空）。

---

## 管理员账号

首次启动时 `DataInitializer` 自动创建：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `admin123` | 管理员 |

登录后自动跳转：普通用户 → 首页 `/`，管理员 → 管理后台 `/admin`。

---

## 功能清单

### 用户端

| 功能 | 说明 | 权限 |
|------|------|------|
| 注册 | 用户名（3-50 字符）+ 密码（≥6 位）+ 昵称 | 公开 |
| 登录 | JWT 认证，24 小时有效期，角色自动跳转 | 公开 |
| 首页 | 分类筛选（一级自动聚合二级）+ 关键词搜索 + 商品卡片流 | 公开 |
| 商品详情 | 图片轮播、价格、成色、描述、卖家信息、浏览量、联系/收藏/下单 | 公开 |
| 卖家主页 | 公开资料（昵称/头像/学校）+ 在售商品列表 | 公开 |
| 发布商品 | 标题/描述/价格/原价/成色/分类/多图上传，提交后待审核 | 需登录 |
| 编辑商品 | 修改已发布商品的信息（仅本人） | 需登录 |
| 删除商品 | 软删除（status → hidden） | 需登录 |
| 收藏 | 切换收藏/取消，收藏列表，商品卡片显示收藏数 | 需登录 |
| 私信 | 创建会话（从商品详情发起）、消息列表、发送消息 | 需登录 |
| 下单 | 点击"立即下单"→ 商品状态变为"已预定"，防自买+防重复下单 | 需登录 |
| 订单-我买的 | 待处理/已完成/已取消列表，买家可取消 | 需登录 |
| 订单-我卖的 | 待处理/已完成/已取消列表，卖家可确认成交或取消 | 需登录 |
| 个人中心 | 我的发布 / 我的收藏 / 我的订单 三个 Tab | 需登录 |
| 编辑资料 | 修改昵称/手机号/头像/学校 | 需登录 |

### 管理端

| 功能 | 说明 | 权限 |
|------|------|------|
| 管理端登录 | 独立登录页 `/admin/login`，仅 role=admin 可登录 | 公开 |
| 数据概览 | 总用户数 / 总商品数 / 待审核数 / 已通过数 | 管理员 |
| 商品审核 | 待审核列表，预览图片/标题/卖家，通过或驳回（填原因） | 管理员 |
| 用户管理 | 用户列表（分页/搜索），启用/禁用，禁用后不可登录 | 管理员 |
| 分类管理 | 树形表格，新增/编辑/删除，删除前检查是否有商品 | 管理员 |

---

## 页面路由

| 路径 | 页面 | 权限 | 布局 |
|------|------|------|------|
| `/` | 首页 | 公开 | ClientLayout |
| `/login` | 用户登录 | 公开 | 独立 |
| `/register` | 用户注册 | 公开 | 独立 |
| `/product/:id` | 商品详情 | 公开 | ClientLayout |
| `/user/:id` | 卖家主页 | 公开 | ClientLayout |
| `/publish` | 发布商品 | 需登录 | ClientLayout |
| `/profile` | 个人中心 | 需登录 | ClientLayout |
| `/chat` | 私信 | 需登录 | ClientLayout |
| `/orders` | 我的订单 | 需登录 | ClientLayout |
| `/admin/login` | 管理端登录 | 公开 | 独立 |
| `/admin` | 管理仪表盘 | 管理员 | AdminLayout |
| `/admin/audit` | 商品审核 | 管理员 | AdminLayout |
| `/admin/users` | 用户管理 | 管理员 | AdminLayout |
| `/admin/categories` | 分类管理 | 管理员 | AdminLayout |

---

## API 总览

### 认证 `/api/auth`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/auth/register` | 用户注册 | 公开 |
| POST | `/auth/login` | 用户登录，返回 JWT | 公开 |
| GET | `/auth/me` | 当前用户信息 | 需登录 |

### 商品 `/api/products`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/products` | 商品列表（keyword/categoryId/page/pageSize） | 公开 |
| GET | `/products/{id}` | 商品详情 + 浏览量 +1 | 公开 |
| POST | `/products` | 发布商品（audit_status=pending） | 需登录 |
| PUT | `/products/{id}` | 编辑商品（归属权校验） | 需登录 |
| DELETE | `/products/{id}` | 删除商品（软删除） | 需登录 |
| GET | `/products/my` | 我的发布列表 | 需登录 |

### 分类 `/api/categories`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/categories` | 树形分类结构 | 公开 |

### 收藏 `/api/products/{id}/favorite` + `/api/favorites`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/products/{id}/favorite` | 切换收藏/取消 | 需登录 |
| GET | `/favorites` | 我的收藏列表 | 需登录 |

### 私信 `/api/conversations`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/conversations` | 创建或获取会话 | 需登录 |
| GET | `/conversations` | 我的会话列表 | 需登录 |
| GET | `/conversations/{id}/messages` | 会话消息历史 | 需登录 |
| POST | `/conversations/{id}/messages` | 发送消息 | 需登录 |

### 订单 `/api/orders`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/orders` | 创建订单（商品→reserved） | 需登录 |
| PUT | `/orders/{id}/status` | 确认成交/取消 | 需登录 |
| GET | `/orders/buy` | 买家订单列表 | 需登录 |
| GET | `/orders/sell` | 卖家订单列表 | 需登录 |

### 用户 `/api/users`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/users/me` | 当前用户资料（去敏） | 需登录 |
| PUT | `/users/me` | 编辑个人资料 | 需登录 |
| GET | `/users/{id}/profile` | 卖家公开资料 + 在售商品 | 公开 |

### 上传 `/api/upload`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/upload/image` | 上传图片（jpg/png/webp/gif，≤5MB） | 需登录 |

### 管理端 `/api/admin`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/admin/auth/login` | 管理端登录 | 公开 |
| GET | `/admin/dashboard` | 数据概览 | 管理员 |
| GET | `/admin/products/pending` | 待审核商品列表 | 管理员 |
| PUT | `/admin/products/{id}/audit` | 审核通过/驳回 | 管理员 |
| GET | `/admin/users` | 用户列表（分页） | 管理员 |
| PUT | `/admin/users/{id}/status` | 启用/禁用用户 | 管理员 |
| GET | `/admin/categories` | 分类列表 | 管理员 |
| POST | `/admin/categories` | 新增分类 | 管理员 |
| PUT | `/admin/categories/{id}` | 编辑分类 | 管理员 |
| DELETE | `/admin/categories/{id}` | 删除分类 | 管理员 |

---

## 项目结构

```
CampusTrade/
├── client/                         # Vue 3 前端
│   ├── package.json
│   ├── vite.config.ts              # 构建配置 + API 代理
│   ├── tsconfig.json
│   ├── .eslintrc.cjs               # ESLint 配置
│   ├── .env.example                # 环境变量模板
│   └── src/
│       ├── main.ts                 # 应用入口（Pinia + Router 安装）
│       ├── App.vue                 # 根组件
│       ├── api/                    # Axios 封装 + 接口函数
│       │   ├── index.ts            # Axios 实例 + JWT 拦截器
│       │   ├── auth.ts             # 注册/登录
│       │   ├── product.ts          # 商品 CRUD
│       │   ├── category.ts         # 分类查询
│       │   ├── favorite.ts         # 收藏
│       │   ├── user.ts             # 用户资料
│       │   ├── chat.ts             # 私信
│       │   └── order.ts            # 订单
│       ├── components/
│       │   └── AppHeader.vue       # 客户端顶部导航栏
│       ├── views/
│       │   ├── ClientLayout.vue    # 客户端框架（Header + 内容）
│       │   ├── HomePage.vue        # 首页（分类 + 商品流）
│       │   ├── LoginPage.vue       # 登录（角色跳转）
│       │   ├── RegisterPage.vue    # 注册
│       │   ├── ProductDetailPage.vue  # 商品详情
│       │   ├── PublishPage.vue     # 发布商品
│       │   ├── ProfilePage.vue     # 个人中心
│       │   ├── SellerPage.vue      # 卖家主页
│       │   ├── ChatPage.vue        # 私信
│       │   ├── OrdersPage.vue      # 订单
│       │   ├── NotFoundPage.vue    # 404
│       │   └── admin/
│       │       ├── AdminLayout.vue     # 管理端侧边栏布局
│       │       ├── LoginPage.vue       # 管理端登录
│       │       ├── DashboardPage.vue   # 数据概览
│       │       ├── AuditPage.vue       # 商品审核
│       │       ├── UsersPage.vue       # 用户管理
│       │       └── CategoriesPage.vue  # 分类管理
│       ├── router/
│       │   └── index.ts            # 路由（双布局嵌套）
│       ├── stores/
│       │   ├── authStore.ts        # 认证状态（token + user）
│       │   └── categoryStore.ts    # 分类缓存
│       └── types/
│           └── index.ts            # 共享类型定义
├── server/                         # Spring Boot 后端
│   ├── pom.xml                     # Maven 依赖配置
│   ├── mvnw / mvnw.cmd             # Maven Wrapper 脚本
│   ├── .mvn/wrapper/               # Maven Wrapper JAR
│   └── src/
│       ├── main/java/com/campustrade/
│       │   ├── CampusTradeApplication.java   # Spring Boot 入口
│       │   ├── controller/                   # REST 控制器
│       │   │   ├── AuthController.java       # 认证
│       │   │   ├── UserController.java       # 用户
│       │   │   ├── ProductController.java    # 商品
│       │   │   ├── CategoryController.java   # 分类
│       │   │   ├── FavoriteController.java   # 收藏
│       │   │   ├── ChatController.java       # 私信
│       │   │   ├── OrderController.java      # 订单
│       │   │   ├── UploadController.java     # 上传
│       │   │   └── admin/                    # 管理端控制器
│       │   │       ├── AdminAuthController.java
│       │   │       ├── AdminProductController.java
│       │   │       ├── AdminUserController.java
│       │   │       ├── AdminCategoryController.java
│       │   │       └── DashboardController.java
│       │   ├── service/                      # 业务接口
│       │   │   ├── AuthService.java
│       │   │   ├── AdminProductService.java
│       │   │   ├── ProductService.java
│       │   │   ├── CategoryService.java
│       │   │   ├── FavoriteService.java
│       │   │   ├── ConversationService.java
│       │   │   ├── OrderService.java
│       │   │   └── impl/                     # 实现类
│       │   ├── mapper/                       # MyBatis-Plus Mapper
│       │   │   ├── UserMapper.java
│       │   │   ├── ProductMapper.java
│       │   │   ├── CategoryMapper.java
│       │   │   ├── FavoriteMapper.java
│       │   │   ├── ConversationMapper.java
│       │   │   ├── MessageMapper.java
│       │   │   └── OrderMapper.java
│       │   ├── entity/                       # 数据库实体
│       │   │   ├── User.java
│       │   │   ├── Product.java
│       │   │   ├── Category.java
│       │   │   ├── Favorite.java
│       │   │   ├── Conversation.java
│       │   │   ├── Message.java
│       │   │   └── Order.java
│       │   ├── dto/                          # 入参 DTO
│       │   │   ├── RegisterDTO.java
│       │   │   ├── LoginDTO.java
│       │   │   ├── CreateProductDTO.java
│       │   │   └── UpdateProductDTO.java
│       │   ├── vo/                           # 出参 VO
│       │   │   ├── UserVO.java
│       │   │   ├── LoginVO.java
│       │   │   ├── ProductVO.java
│       │   │   └── ProductDetailVO.java
│       │   ├── config/                       # Spring 配置
│       │   │   ├── SecurityConfig.java       # 安全规则
│       │   │   ├── CorsConfig.java           # 跨域
│       │   │   ├── JwtConfig.java            # JWT Bean
│       │   │   ├── WebConfig.java            # 静态资源
│       │   │   ├── DataInitializer.java      # 启动初始化
│       │   │   └── RateLimitConfig.java      # 限流
│       │   ├── security/                     # 认证组件
│       │   │   ├── JwtTokenProvider.java     # JWT 工具
│       │   │   ├── JwtAuthenticationFilter.java  # JWT 过滤器
│       │   │   └── UserDetailsServiceImpl.java   # 用户加载
│       │   ├── common/                       # 公共组件
│       │   │   ├── Result.java               # 统一响应
│       │   │   ├── BusinessException.java    # 业务异常
│       │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│       │   └── utils/
│       │       └── FileUploadUtil.java       # 文件上传
│       ├── main/resources/
│       │   ├── application.yml               # 基础配置
│       │   ├── application-h2.yml            # H2 profile
│       │   ├── application-mysql.yml         # MySQL profile
│       │   ├── application.example.yml       # MySQL 配置模板
│       │   ├── schema-h2.sql                 # H2 DDL + 种子
│       │   └── schema-mysql.sql              # MySQL DDL + 种子
│       └── test/java/com/campustrade/        # 测试
│           ├── config/SecurityConfigTest.java
│           ├── security/JwtTokenProviderTest.java
│           └── service/
│               ├── AuthServiceTest.java
│               ├── CategoryServiceTest.java
│               ├── AdminProductServiceTest.java
│               ├── FavoriteServiceTest.java
│               ├── ConversationServiceTest.java
│               └── OrderServiceTest.java
└── docs/
    ├── README.md                    # 本文档
    ├── PRD.md                       # 产品需求文档
    ├── ARCHITECTURE.md              # 架构设计文档
    ├── TASKS.md                     # 开发任务拆分
    ├── CLAUDE.md                    # AI 开发指引
    ├── AGENTS.md                    # AI 行为规范
    ├── analysis/                    # 毕设分析文档
    │   ├── 01-需求分析.md
    │   ├── 02-功能模块设计.md
    │   └── 03-数据库设计.md
    └── deploy/
        └── nginx.conf               # Nginx 部署配置
```

---

## 数据库表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户 | username(UK), password(BCrypt), role(user/admin), status(active/disabled) |
| `category` | 分类 | name, parent_id(树形), sort_order |
| `product` | 商品 | title, price, images(JSON), audit_status(pending/approved/rejected), status(active/reserved/sold/hidden), view_count, fav_count, FULLTEXT 索引 |
| `favorite` | 收藏 | user_id + product_id 联合唯一 |
| `conversation` | 会话 | buyer_id + seller_id + product_id 联合唯一, last_message 冗余 |
| `message` | 消息 | conversation_id, sender_id, is_read |
| `order` | 订单 | product_id(pending 唯一由生成列保证), buyer_id, seller_id, status(pending/completed/cancelled) |

完整 DDL 和 ER 图见 `docs/analysis/03-数据库设计.md`。

### 关键业务规则

- **订单 pending 唯一由 DB 层保证**：MySQL 生成列 `pending_product_id` + UNIQUE 索引，H2 计算列 + UNIQUE 索引。同一商品同时最多一个 pending 订单。
- **商品审核**：发布后默认 pending，管理员通过后 approved 才在首页展示，驳回后可查看原因。
- **一级分类自动聚合**：用户点击一级分类时，后端自动包含该分类下所有二级分类商品，避免空列表。
- **私信参与者校验**：读写消息前校验当前用户是 buyer 或 seller，防止越权访问。
- **返回去敏**：`/me` 和 `/users/{id}/profile` 均不返回 password hash；卖家公开资料不返回 phone/role/status。

---

## 测试

后端 61 个测试，覆盖 8 个测试类：

```bash
cd server

# 全量测试（H2 环境）
./mvnw test            # Linux/macOS/Git Bash
mvnw.cmd test          # Windows CMD
```

| 测试类 | 数量 | 覆盖范围 |
|--------|------|----------|
| `JwtTokenProviderTest` | 8 | JWT 生成/验证/解析/过期/篡改 |
| `AuthServiceTest` | 10 | 注册/登录/去重/密码校验/禁用用户 |
| `CategoryServiceTest` | 3 | 树形结构/排序 |
| `AdminProductServiceTest` | 5 | 审核列表/通过/驳回/状态校验 |
| `FavoriteServiceTest` | 5 | 收藏/取消/列表/状态 |
| `ConversationServiceTest` | 9 | 会话创建/消息收发/参与者校验 |
| `OrderServiceTest` | 9 | 下单/确认/取消/防自买/权限校验 |
| `SecurityConfigTest` | 12 | 匿名访问/认证访问/管理员拦截/去敏 |
| **合计** | **61** | |

前端检查：

```bash
cd client
npm run typecheck      # TypeScript 类型检查
npm run lint           # ESLint（0 errors）
npm run build          # 生产构建
```

---

## 命令速查

```bash
# ===== 后端 =====
cd server

# 启动（MySQL，默认）
./mvnw spring-boot:run

# 启动（H2 免 MySQL）
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# 全量测试
./mvnw test

# 仅编译
./mvnw compile

# 打包 JAR
./mvnw clean package -DskipTests

# ===== 前端 =====
cd client

# 安装依赖
npm install

# 开发启动
npm run dev

# 类型检查
npm run typecheck

# 代码检查
npm run lint

# 生产构建
npm run build
```

> Windows CMD 下将 `./mvnw` 替换为 `mvnw.cmd`

---

## 部署

生产部署建议：前端 build 产物由 Nginx 托管，API 反向代理到 Spring Boot。

```nginx
# 参考 docs/deploy/nginx.conf
server {
    listen 80;
    root /opt/campustrade/client/dist;

    location /api/ {
        proxy_pass http://127.0.0.1:8080;
    }
    location /uploads/ {
        alias /opt/campustrade/server/uploads/;
    }
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

后端 JAR 部署：

```bash
cd server
./mvnw clean package -DskipTests
java -jar target/campustrade-server-0.1.0.jar --spring.profiles.active=mysql
```

---

## 延后功能

| 功能 | 归属版本 | 原因 |
|------|----------|------|
| 闲置租赁 | v2 | 独立数据模型和归还确认流程 |
| 技能服务交易 | v3 | 与实物交易模型差异大 |
| 在线支付 | v2 | 支付资质和合规复杂度高 |
| 评价/信用体系 | v2 | 需订单完成后触发 |
| 微信小程序 | v2 | 需独立前端工程 |
| WebSocket 实时推送 | v2 | 当前 HTTP 轮询够 MVP 使用 |
| Redis 缓存 | v2 | 当前单机数据量不需要 |

---

## 文档索引

| 文档 | 内容 |
|------|------|
| [PRD.md](docs/PRD.md) | 产品需求文档（P0/P1/P2 功能分级） |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | 系统架构/组件树/分层设计/安全方案 |
| [TASKS.md](docs/TASKS.md) | 8 阶段任务拆分 + 验收标准 |
| [01-需求分析.md](docs/analysis/01-需求分析.md) | 项目背景/可行性/功能性需求/非功能性需求 |
| [02-功能模块设计.md](docs/analysis/02-功能模块设计.md) | 系统功能模块图（Mermaid） |
| [03-数据库设计.md](docs/analysis/03-数据库设计.md) | ER 图 + 7 张表 DDL + 索引设计 |
| [nginx.conf](docs/deploy/nginx.conf) | Nginx 生产部署配置 |
| [CLAUDE.md](CLAUDE.md) | AI 编码助手项目指引 |
| [AGENTS.md](AGENTS.md) | AI 编码助手行为规范 |

---

## License

MIT
