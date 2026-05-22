# ARCHITECTURE.md — CampusTrade 架构设计文档

> 版本：v1.0 MVP | 最后更新：2026-05-22

## 1. 系统架构总览

```
┌─────────────────────────────────────────────────┐
│                   客户端                         │
│  Vue 3 + TypeScript + Vite + Element Plus       │
│  Pinia (状态管理) + Vue Router 4 (路由)         │
└────────────────────┬────────────────────────────┘
                     │ HTTP/REST + WebSocket
                     ▼
┌─────────────────────────────────────────────────┐
│                Spring Boot 3.x                   │
│  ┌──────────────────────────────────────────┐   │
│  │        Spring Security + JWT Filter       │   │
│  ├────────┬────────┬────────┼───────────────┤   │
│  │Controller│Service│Mapper  │  FileUpload   │   │
│  │(接口层)  │(业务层)│(持久层) │   Utils      │   │
│  └────────┴────────┴────────┴───────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │  GlobalExceptionHandler + ResultWrapper   │   │
│  └──────────────────────────────────────────┘   │
└───────────────────┬──────────────────────────────┘
                    │
        ┌───────────┼───────────┐
        ▼           ▼           ▼
   ┌────────┐ ┌────────┐ ┌──────────┐
   │ MySQL  │ │ Redis  │ │ 文件存储  │
   │  8.0   │ │   7    │ │ (本地/OSS)│
   └────────┘ └────────┘ └──────────┘
```

## 2. 前端架构

### 2.1 技术选型

| 项 | 选择 | 理由 |
|----|------|------|
| 框架 | Vue 3 Composition API | 毕设主流，中文社区强 |
| 语言 | TypeScript | 类型安全 |
| 构建 | Vite | 开发体验快 |
| UI 库 | Element Plus | Vue 3 生态最成熟的组件库，表格/表单/上传开箱即用 |
| 状态 | Pinia | Vue 3 官方推荐 |
| 路由 | Vue Router 4 | 官方路由 |
| 请求 | Axios | 拦截器 + token 注入 |
| 实时 | 原生 WebSocket | Spring Boot 内置支持 |

### 2.2 页面路由设计

```
用户端路由                    管理端路由
/                            /admin
├── /login                   ├── /admin/login
├── /register                ├── /admin/dashboard      # 数据概览
├── /                        # 首页                     ├── /admin/products       # 商品审核
├── /products                # 商品列表                 ├── /admin/users          # 用户管理
├── /products/:id            # 商品详情                 └── /admin/categories     # 分类管理
├── /publish                 # 发布商品（需登录）
├── /publish/:id             # 编辑商品
├── /chat                    # 私信列表
├── /chat/:conversationId    # 聊天窗口
├── /favorites               # 我的收藏
├── /profile                 # 个人中心
│   ├── /profile/products    # 我的发布
│   └── /profile/orders      # 我的订单
└── /user/:id                # 卖家主页
```

### 2.3 组件树（核心）

```
App
├── UserLayout
│   ├── AppHeader (Logo + 搜索 + 导航 + 用户菜单)
│   └── RouterView
│       ├── HomePage            # 分类导航 + 商品卡片
│       ├── ProductListPage     # 搜索栏 + 筛选 + 商品列表
│       ├── ProductDetailPage   # 图片轮播 + 详情 + 操作按钮
│       ├── PublishPage         # 发布表单
│       ├── ChatPage            # 会话列表 + 聊天窗
│       ├── FavoritesPage       # 收藏列表
│       └── ProfilePage         # 个人中心
└── AdminLayout
    ├── AdminSidebar (菜单导航)
    └── RouterView
        ├── DashboardPage       # 数据概览
        ├── AuditProductPage    # 商品审核
        ├── UserManagePage      # 用户管理
        └── CategoryManagePage  # 分类管理
```

### 2.4 状态管理（Pinia Stores）

```
stores/
├── authStore.ts       # 用户信息、token、role（user/admin 共用，role 字段区分路由）
└── categoryStore.ts   # 分类树缓存
```

## 3. 后端架构

### 3.1 技术选型

| 项 | 选择 | 版本 |
|----|------|------|
| 框架 | Spring Boot | 3.x |
| JDK | Java | 17 LTS |
| ORM | MyBatis-Plus | 3.5+ |
| 安全 | Spring Security + JWT | |
| 校验 | Hibernate Validator | |
| JSON | Jackson | |
| 文件 | Spring MultipartFile | |
| 实时 | Spring WebSocket | |
| 构建 | Maven | |

### 3.2 分层架构与包结构

```
com.campustrade
├── CampusTradeApplication.java        # 启动类
├── controller/                         # 控制器层
│   ├── AuthController.java            # /api/auth/*
│   ├── UserController.java            # /api/users/*
│   ├── ProductController.java         # /api/products/*
│   ├── CategoryController.java        # /api/categories/*
│   ├── ConversationController.java    # /api/conversations/*
│   ├── OrderController.java           # /api/orders/*
│   ├── UploadController.java          # /api/upload/*
│   └── admin/
│       ├── AdminAuthController.java   # /api/admin/auth/*
│       ├── AdminProductController.java # /api/admin/products/*
│       ├── AdminUserController.java   # /api/admin/users/*
│       └── AdminCategoryController.java # /api/admin/categories/*
├── service/                            # 业务接口
│   ├── AuthService.java
│   ├── UserService.java
│   ├── ProductService.java
│   ├── CategoryService.java
│   ├── ConversationService.java
│   ├── OrderService.java
│   ├── FileService.java
│   └── impl/                           # 实现类（命名: XxxServiceImpl）
├── mapper/                             # MyBatis-Plus Mapper
│   ├── UserMapper.java
│   ├── ProductMapper.java
│   ├── CategoryMapper.java
│   ├── FavoriteMapper.java
│   ├── ConversationMapper.java
│   ├── MessageMapper.java
│   └── OrderMapper.java
├── entity/                             # 数据库实体（对应表）
│   ├── User.java
│   ├── Product.java
│   ├── Category.java
│   ├── Favorite.java
│   ├── Conversation.java
│   ├── Message.java
│   └── Order.java
├── dto/                                # 入参 DTO
│   ├── RegisterDTO.java
│   ├── LoginDTO.java
│   ├── ProductDTO.java
│   ├── AuditDTO.java
│   └── ...
├── vo/                                 # 出参 VO
│   ├── UserVO.java
│   ├── ProductVO.java
│   ├── ProductDetailVO.java
│   └── PageVO.java
├── config/                             # 配置类
│   ├── SecurityConfig.java            # Spring Security 配置
│   ├── CorsConfig.java                # 跨域配置
│   ├── WebSocketConfig.java           # WebSocket 配置
│   ├── MyBatisPlusConfig.java         # MyBatis-Plus 分页插件
│   └── RedisConfig.java               # Redis 序列化配置
├── security/                           # 安全相关
│   ├── JwtTokenProvider.java          # JWT 生成/验证
│   ├── JwtAuthenticationFilter.java   # JWT 过滤器
│   └── UserDetailsServiceImpl.java    # 用户加载
├── common/                             # 通用组件
│   ├── Result.java                    # 统一响应 {code, data, message}
│   ├── GlobalExceptionHandler.java    # 全局异常处理
│   └── BusinessException.java         # 业务异常类
└── utils/                              # 工具类
    ├── FileUploadUtil.java
    └── RedisUtil.java
```

### 3.3 统一响应格式

```json
// 成功
{ "code": 200, "data": { ... }, "message": "操作成功" }

// 分页
{ "code": 200, "data": { "records": [...], "total": 100, "page": 1, "pageSize": 20 }, "message": "操作成功" }

// 业务异常
{ "code": 400, "data": null, "message": "商品不存在" }

// 认证异常（Spring Security 自动返回 401）
```

### 3.4 异常处理

- `BusinessException(message)` — 业务异常，返回 HTTP 400 + 错误信息
- `Result.success(data)` / `Result.error(message)` — 静态工厂方法，统一响应格式
- `GlobalExceptionHandler` — `@RestControllerAdvice` 统一捕获
- Spring Security 异常 → 返回 401

## 4. 数据库设计

### 4.1 ER 图（文字描述）

```
User(1) ──── (N)Product      # 一个用户发布多个商品
User(1) ──── (N)Favorite      # 一个用户收藏多个商品
User(1) ──── (N)Message       # 一个用户发送多条消息
User(1) ──── (N)Order(buyer)  # 一个用户创建多个订单（买家）
User(1) ──── (N)Order(seller) # 一个用户收到多个订单（卖家）
Category(1) ──── (N)Product   # 一个分类下有多个商品
Product(1) ──── (N)Favorite   # 一个商品被多人收藏
Product(1) ──── (N)Conversation # 一个商品有多个会话
Product(1) ──── (N)Order      # 一个商品可产生多次订单（取消后重下单）
Conversation(1) ──── (N)Message # 一个会话包含多条消息
```

详细 ER 图（Mermaid）见 [docs/analysis/03-数据库设计.md](analysis/03-数据库设计.md)。

### 4.2 核心表结构

**user — 用户表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | VARCHAR(255) | BCrypt 加密 |
| nickname | VARCHAR(50) | 昵称 |
| phone | VARCHAR(20) | 手机号 |
| avatar | VARCHAR(500) | 头像 URL |
| campus | VARCHAR(100) | 学校 |
| role | VARCHAR(20) | user / admin |
| status | VARCHAR(20) | active / disabled |
| created_at | DATETIME | 注册时间 |
| updated_at | DATETIME | 更新时间 |

**category — 分类表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| name | VARCHAR(50) | 分类名称 |
| icon | VARCHAR(50) | Element Plus 图标名 |
| parent_id | BIGINT | 父分类ID（NULL=一级） |
| sort_order | INT | 排序 |
| status | VARCHAR(20) | active / disabled |

**product — 商品表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| title | VARCHAR(200) | 标题 |
| description | TEXT | 描述 |
| price | DECIMAL(10,2) | 售价 |
| original_price | DECIMAL(10,2) | 原价 |
| condition | VARCHAR(20) | brand_new / like_new / used / worn |
| images | JSON | 图片URL数组 |
| category_id | BIGINT FK | 分类ID |
| seller_id | BIGINT FK | 卖家ID |
| status | VARCHAR(20) | active / reserved / sold / hidden |
| audit_status | VARCHAR(20) | pending / approved / rejected |
| audit_reason | VARCHAR(500) | 驳回原因 |
| view_count | INT | 浏览量 |
| fav_count | INT | 收藏数 |
| created_at | DATETIME | 发布时间 |
| updated_at | DATETIME | 更新时间 |

**favorite — 收藏表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 用户ID |
| product_id | BIGINT FK | 商品ID |
| created_at | DATETIME | 收藏时间 |
| UNIQUE(user_id, product_id) | | |

**conversation — 会话表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| buyer_id | BIGINT FK | 买家ID |
| seller_id | BIGINT FK | 卖家ID |
| product_id | BIGINT FK | 商品ID |
| last_message | TEXT | 最后一条消息 |
| last_message_at | DATETIME | 最后消息时间 |
| UNIQUE(buyer_id, seller_id, product_id) | | |

**message — 消息表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| conversation_id | BIGINT FK | 会话ID |
| sender_id | BIGINT FK | 发送者ID |
| content | TEXT | 消息内容 |
| is_read | TINYINT | 已读标记 |
| created_at | DATETIME | 发送时间 |

**order — 订单表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| product_id | BIGINT FK | 商品ID |
| buyer_id | BIGINT FK | 买家ID |
| seller_id | BIGINT FK | 卖家ID |
| status | VARCHAR(20) | pending / completed / cancelled |
| created_at | DATETIME | 下单时间 |
| updated_at | DATETIME | 更新时间 |

## 5. API 设计

### 5.1 用户端 API（前缀 `/api`）

**认证 — `/api/auth`**

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 注册 | 否 |
| POST | `/api/auth/login` | 登录，返回 JWT | 否 |
| GET | `/api/auth/me` | 获取当前用户信息 | 是 |

**商品 — `/api/products`**

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/products` | 商品列表（分页+搜索+筛选+排序） | 否 |
| GET | `/api/products/{id}` | 商品详情 | 否 |
| POST | `/api/products` | 发布商品 | 是 |
| PUT | `/api/products/{id}` | 编辑商品 | 是（本人） |
| DELETE | `/api/products/{id}` | 删除商品 | 是（本人） |
| POST | `/api/products/{id}/favorite` | 收藏 | 是 |
| DELETE | `/api/products/{id}/favorite` | 取消收藏 | 是 |

**分类 — `/api/categories`**

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/categories` | 全部分类（树形） | 否 |

**用户 — `/api/users`**

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/users/{id}` | 用户公开信息 | 否 |
| PUT | `/api/users/me` | 修改个人资料 | 是 |
| GET | `/api/users/me/products` | 我发布的商品 | 是 |
| GET | `/api/users/me/favorites` | 我的收藏 | 是 |
| GET | `/api/users/me/orders/bought` | 我买到的 | 是 |
| GET | `/api/users/me/orders/sold` | 我卖出的 | 是 |

**私信 — `/api/conversations`**

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/conversations` | 会话列表 | 是 |
| POST | `/api/conversations` | 创建会话 | 是 |
| GET | `/api/conversations/{id}/messages` | 历史消息 | 是 |
| POST | `/api/conversations/{id}/messages` | 发送消息 | 是 |

**订单 — `/api/orders`**

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/orders` | 下单 | 是 |
| PUT | `/api/orders/{id}/status` | 更新订单状态 | 是 |

**文件 — `/api/upload`**

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/upload/image` | 上传图片 | 是 |

### 5.2 管理端 API（前缀 `/api/admin`）

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/admin/auth/login` | 管理员登录 | 否 |
| GET | `/api/admin/products` | 待审核/全部商品 | 是(admin) |
| PUT | `/api/admin/products/{id}/audit` | 审核商品（通过/驳回） | 是(admin) |
| GET | `/api/admin/users` | 用户列表 | 是(admin) |
| PUT | `/api/admin/users/{id}/status` | 启用/禁用用户 | 是(admin) |
| GET | `/api/admin/categories` | 分类管理列表 | 是(admin) |
| POST | `/api/admin/categories` | 新增分类 | 是(admin) |
| PUT | `/api/admin/categories/{id}` | 编辑分类 | 是(admin) |
| DELETE | `/api/admin/categories/{id}` | 删除分类 | 是(admin) |
| GET | `/api/admin/dashboard` | 数据概览 | 是(admin) |

## 6. 安全设计

| 场景 | 措施 |
|------|------|
| 密码存储 | BCryptPasswordEncoder |
| 接口认证 | JWT（Header: Authorization: Bearer xxx） |
| 权限控制 | Spring Security + @PreAuthorize 注解 |
| SQL 注入 | MyBatis-Plus 参数化查询 |
| XSS | Vue 默认转义 + 后端输入清洗 |
| CSRF | Spring Security 默认启用（前后端分离可关闭） |
| 文件上传 | 类型白名单 + 大小限制 |
| CORS | CorsConfig 配置允许前端域名 |

## 7. 关键架构决策

| 决策 | 理由 |
|------|------|
| MyBatis-Plus 而非 JPA | 中文社区更常用，复杂查询更灵活，毕设参考资源多 |
| JWT 而非 Session | 前后端分离天然适合 JWT，无状态易扩展 |
| 商品审核机制 | 毕设亮点功能，区别于普通 CRUD 项目 |
| 不做在线支付 | 支付资质复杂度高，毕设论文中可作为"未来展望" |
| 图片本地存储 | 减少外部依赖，方便答辩演示 |
| images 用 JSON 字段 | MySQL 8.0 原生支持 JSON，9 张以内小数组无性能问题，避免多表 JOIN。已知权衡：JSON 内字段无法建索引 |
| category.icon 存图标名 | MVP 阶段前端图标名稳定，V2 如需多端可改为通用 icon_url |
| authStore 统一管理 user/admin | 同一物理用户可能同时是卖家和管理员，用 role 字段区分路由，避免双 store 同步问题 |
| Redis 开发环境可选 | Phase 1-7 不依赖 Redis（JWT 无状态），Phase 8 用 Redis 做缓存优化时才需要 |
