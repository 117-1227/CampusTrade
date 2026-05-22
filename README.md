# CampusTrade — 校园二手交易平台

> **论文题目：** 基于 Spring Boot + Vue 的校园二手交易平台设计与实现
>
> 面向高校学生群体，设计并实现一个校园二手交易平台，支持用户注册登录、商品发布、商品浏览、分类搜索、收藏管理、商品审核和后台管理等功能，旨在提高校园闲置物品流通效率，降低学生交易成本。

## 技术栈

| 层 | 技术 | 版本 |
|----|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus | Vue 3.4+ |
| 后端 | Spring Boot + MyBatis-Plus + Spring Security | Spring Boot 3.x / Java 17 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 文件存储 | 本地存储（开发）/ 阿里云 OSS（生产） |
| 认证 | Spring Security + JWT | |
| 实时通信 | WebSocket（Spring 内置） | |

## MVP 功能

### 用户端
- 注册登录（JWT 认证）
- 商品发布（图片上传、分类、价格、成色描述）
- 商品浏览（分类、搜索、筛选、排序、分页）
- 商品详情（图片轮播、卖家信息）
- 商品收藏
- 买卖双方私信（WebSocket 实时消息）
- 基础交易流程（下单 → 确认成交）
- 个人中心（我的发布、我的收藏、我买到的/卖出的）

### 管理端
- 管理员登录
- 商品审核（通过/驳回，驳回需填原因）
- 用户管理（查看、启用/禁用）
- 分类管理（增删改）
- 数据概览（用户数、商品数、交易数）

## 延后功能

| 功能 | 归属版本 |
|------|----------|
| 闲置租赁 | v2 |
| 技能服务交易 | v3 |
| 在线支付 | v2 |
| 用户评价/信用体系 | v2 |
| 微信小程序 | v2 |
| 推送通知 | v2 |

## 快速开始

```bash
# 1. 启动后端（默认 H2 内存数据库，零依赖启动）
cd server
./mvnw spring-boot:run         # http://localhost:8080

# 切换到 MySQL（可选）：
# cp src/main/resources/application.example.yml src/main/resources/application-mysql.yml
# 编辑 application-mysql.yml 填数据库密码
# ./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql

# 2. 启动前端
cd ../client
npm install
npm run dev                     # http://localhost:5173
```

## 项目结构

```
CampusTrade/
├── client/                  # Vue 3 前端
│   ├── src/
│   │   ├── api/index.ts     # ✅ Axios 封装 + JWT 拦截器
│   │   ├── components/      # (Phase 2+)
│   │   ├── views/           # ✅ 占位页面 (Home/Login/404/Admin)
│   │   ├── router/index.ts  # ✅ Vue Router + beforeEach 守卫
│   │   ├── stores/          # ✅ Pinia authStore (user/admin 统一)
│   │   ├── utils/           # (Phase 2+)
│   │   └── types/index.ts   # ✅ 共享类型
│   └── ...
├── server/                  # Spring Boot 后端
│   └── src/main/java/com/campustrade/
│       ├── controller/      # ✅ HelloController (联调验证)
│       │   └── admin/       # (Phase 3)
│       ├── service/         # (Phase 1+)
│       │   └── impl/        # (Phase 1+)
│       ├── mapper/          # (Phase 1+)
│       ├── entity/          # (Phase 1+)
│       ├── dto/             # (Phase 1+)
│       ├── vo/              # (Phase 1+)
│       ├── config/          # ✅ SecurityConfig / CorsConfig
│       ├── security/        # (Phase 1: JWT filter)
│       ├── common/          # ✅ Result / BusinessException / GlobalExceptionHandler
│       └── utils/           # (Phase 2+)
├── docs/
│   ├── analysis/            # 需求分析文档
│   │   ├── 01-需求分析.md
│   │   ├── 02-功能模块设计.md
│   │   └── 03-数据库设计.md
│   ├── PRD.md
│   ├── ARCHITECTURE.md
│   └── TASKS.md
├── CLAUDE.md
└── AGENTS.md
```

## License

MIT
