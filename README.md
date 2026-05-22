# CampusTrade — 校园二手交易平台

> **论文题目：** 基于 Spring Boot + Vue 的校园二手交易平台设计与实现
>
> 面向高校学生群体，设计并实现一个校园二手交易平台，支持用户注册登录、商品发布、商品浏览、分类搜索、收藏管理、商品审核和后台管理等功能，旨在提高校园闲置物品流通效率，降低学生交易成本。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus + Pinia |
| 后端 | Spring Boot 3.x + MyBatis-Plus + Spring Security + JWT |
| 数据库 | MySQL 8.0 (开发可用 H2 内存库零依赖启动) |
| 缓存 | Redis 7.x (可选，Phase 7 缓存热点数据时启用) |
| 文件存储 | 本地存储 |

## 功能

### 用户端
- 注册 / 登录 / JWT 认证
- 商品发布（图片上传 / 分类 / 价格 / 成色）
- 商品浏览（关键词搜索 / 分类筛选 / 首页瀑布流）
- 商品详情（卖家信息 / 分类名 / 浏览量）
- 商品收藏（切换收藏 / 收藏列表）
- 私信聊天（会话列表 / 发送消息 / 未读）
- 订单流转（下单 → 卖家确认成交 / 任意方取消）
- 个人中心（我的发布 / 我的收藏 / 我的订单）
- 卖家主页（公开资料 + 在售商品）

### 管理端
- 管理员登录
- 商品审核（通过 / 驳回 + 原因）
- 用户管理（列表 / 启用 / 禁用）
- 分类管理（增删改）
- 数据概览（用户数 / 商品数 / 待审核数）

### 延后

| 功能 | 归属 |
|------|------|
| 闲置租赁 | v2 |
| 技能服务交易 | v3 |
| 在线支付 | v2 |
| 评价/信用体系 | v2 |
| 微信小程序 | v2 |
| WebSocket 实时推送 | v2 |
| Redis 缓存 | v2 |

## 快速开始

```bash
# 1. 启动后端（默认 H2，零外部依赖）
cd server
./mvnw spring-boot:run                    # http://localhost:8080

# 管理员账号首次启动自动创建: admin / admin123

# 切换到 MySQL（可选）
# cp src/main/resources/application.example.yml src/main/resources/application-mysql.yml
# 编辑填数据库密码后:
# ./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql

# 2. 启动前端
cd client
npm install
npm run dev                                # http://localhost:5173
```

## 页面路由

| 路径 | 页面 | 权限 |
|------|------|------|
| `/` | 首页（搜索 + 分类 + 商品列表） | 公开 |
| `/login` | 用户登录 | 公开 |
| `/register` | 用户注册 | 公开 |
| `/product/:id` | 商品详情 | 公开 |
| `/user/:id` | 卖家主页 | 公开 |
| `/publish` | 发布商品 | 需登录 |
| `/profile` | 个人中心 | 需登录 |
| `/chat` | 私信 | 需登录 |
| `/orders` | 我的订单 | 需登录 |
| `/admin/login` | 管理端登录 | 公开 |
| `/admin` | 管理仪表盘 | 管理员 |

## 项目结构

```
CampusTrade/
├── client/                  # Vue 3 前端
│   └── src/
│       ├── api/             # Axios 封装 + 接口函数 (auth/product/user/favorite/chat/order)
│       ├── views/           # 10 个页面 (Home/Login/Register/ProductDetail/Publish/
│       │   │                #   Profile/Seller/Chat/Orders/admin-Login/admin-Dashboard)
│       ├── router/          # Vue Router + beforeEach 守卫
│       ├── stores/          # Pinia (authStore / categoryStore)
│       └── types/           # TypeScript 类型
├── server/                  # Spring Boot 后端
│   └── src/main/java/com/campustrade/
│       ├── controller/      # REST 控制器 + admin/ 子包
│       ├── service/         # 业务接口 + impl/
│       ├── mapper/          # MyBatis-Plus Mapper
│       ├── entity/          # 数据库实体
│       ├── dto/             # 入参对象
│       ├── vo/              # 出参对象
│       ├── config/          # Spring 配置 (Security/CORS/JWT)
│       ├── security/        # JWT Filter + UserDetailsService
│       ├── common/          # Result / BusinessException / GlobalExceptionHandler
│       └── utils/           # FileUploadUtil
├── docs/
│   ├── analysis/            # 需求分析 / 功能模块图 / 数据库设计
│   ├── deploy/              # Nginx 部署配置
│   ├── PRD.md
│   ├── ARCHITECTURE.md
│   └── TASKS.md
├── CLAUDE.md
└── AGENTS.md
```

## 测试

后端 61 个单元/集成测试，覆盖认证、商品、审核、收藏、私信、订单、安全配置。

```bash
cd server
./mvnw test
```

## License

MIT
