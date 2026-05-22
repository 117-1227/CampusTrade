# CLAUDE.md — CampusTrade 项目指引

## 项目概述

**CampusTrade 校园二手交易平台** — 基于 Spring Boot + Vue 的校园二手商品交易系统。

论文题目：**基于 Spring Boot + Vue 的校园二手交易平台设计与实现**

7 个开发阶段全部完成，61 个后端测试，前端 10+ 页面。

详见 [docs/PRD.md](docs/PRD.md) 和 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus + Pinia + Vue Router 4 |
| 后端 | Spring Boot 3.x + Java 17 + MyBatis-Plus + Spring Security + JWT |
| 数据库 | MySQL 8.0（开发默认 H2 零依赖） |

## 项目结构

```
CampusTrade/
├── client/                  # Vue 3 前端
│   └── src/
│       ├── api/             # Axios 封装 + JWT 拦截器 + 接口函数
│       ├── components/      # AppHeader 等通用组件
│       ├── views/           # 页面（client/ 和 admin/）
│       │   ├── ClientLayout.vue    # 客户端框架（导航栏+内容）
│       │   ├── HomePage.vue        # 首页（搜索+分类+商品流）
│       │   ├── LoginPage.vue       # 登录（角色自动跳转）
│       │   ├── RegisterPage.vue    # 注册
│       │   ├── ProductDetailPage.vue  # 商品详情+联系/收藏/下单
│       │   ├── PublishPage.vue     # 发布商品
│       │   ├── ProfilePage.vue     # 个人中心
│       │   ├── SellerPage.vue      # 卖家主页
│       │   ├── ChatPage.vue        # 私信聊天
│       │   ├── OrdersPage.vue      # 订单管理
│       │   ├── NotFoundPage.vue    # 404
│       │   └── admin/
│       │       ├── AdminLayout.vue     # 管理端侧边栏布局
│       │       ├── LoginPage.vue       # 管理端登录
│       │       ├── DashboardPage.vue   # 数据概览
│       │       ├── AuditPage.vue       # 商品审核
│       │       ├── UsersPage.vue       # 用户管理
│       │       └── CategoriesPage.vue  # 分类管理
│       ├── router/          # 路由（ClientLayout+AdminLayout 双嵌套）
│       ├── stores/          # Pinia（authStore / categoryStore）
│       └── types/           # TypeScript 类型
├── server/                  # Spring Boot 后端
│   └── src/main/java/com/campustrade/
│       ├── controller/      # REST 控制器 + admin/ 子包（6 个 Controller）
│       ├── service/         # 业务接口 + impl/（7 个 Service）
│       ├── mapper/          # MyBatis-Plus Mapper（7 个）
│       ├── entity/          # 实体类（7 个）
│       ├── dto/             # 入参对象
│       ├── vo/              # 出参对象
│       ├── config/          # Spring 配置（Security/CORS/JWT）
│       ├── security/        # JwtTokenProvider + JwtAuthFilter + UserDetailsServiceImpl
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

## 常用命令

```bash
# 后端
cd server
./mvnw spring-boot:run                     # 启动 :8080（默认 MySQL）
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2    # 使用 H2（免 MySQL）
./mvnw test                                # 运行测试（61 tests）

# 前端
cd client
npm run dev                                # 启动 :5173
npm run build                              # 生产构建
npm run lint                               # ESLint
npm run typecheck                          # vue-tsc --noEmit
```

## 页面路由

| 路径 | 页面 | 权限 | 布局 |
|------|------|------|------|
| `/` | 首页（搜索+分类+商品流） | 公开 | ClientLayout |
| `/login` | 用户登录 | 公开 | 独立 |
| `/register` | 用户注册 | 公开 | 独立 |
| `/product/:id` | 商品详情 | 公开 | ClientLayout |
| `/user/:id` | 卖家主页 | 公开 | ClientLayout |
| `/publish` | 发布商品 | 需登录 | ClientLayout |
| `/profile` | 个人中心 | 需登录 | ClientLayout |
| `/chat` | 私信列表 | 需登录 | ClientLayout |
| `/orders` | 我的订单 | 需登录 | ClientLayout |
| `/admin/login` | 管理端登录 | 公开 | 独立 |
| `/admin` | 管理概览 | 管理员 | AdminLayout |
| `/admin/audit` | 商品审核 | 管理员 | AdminLayout |
| `/admin/users` | 用户管理 | 管理员 | AdminLayout |
| `/admin/categories` | 分类管理 | 管理员 | AdminLayout |

**登录后自动跳转：** `role=admin` → `/admin`，`role=user` → `/`

## 代码约定

### 后端（Java）

- **命名：**
  - Controller: `XxxController.java`，`@RequestMapping("/api/xxx")`
  - Service 接口: `XxxService.java`，实现: `XxxServiceImpl.java`
  - Mapper: `XxxMapper.java`（继承 `BaseMapper<Entity>`）
  - Entity: `@TableName` 指定表名，`@TableId(type = IdType.AUTO)` 主键
  - DTO: `XxxDTO.java`（入参），VO: `XxxVO.java`（出参）
- **分层原则：** Controller 只做参数接收和结果返回，Service 做业务逻辑+权限校验，Mapper 做数据访问
- **异常处理：** `throw new BusinessException("错误信息")` → GlobalExceptionHandler 捕获返回 `{code:400,message:"..."}`
- **响应格式：** `Result.success(data)` / `Result.error("错误信息")`
- **权限控制：** SecurityConfig 集中管理路由权限，不以 `@PreAuthorize` 注解为准

### 前端（Vue 3）

- **使用 `<script setup lang="ts">`**
- **页面放 `views/`，通用组件放 `components/`**
- **API 调用放 `api/` 目录：** 按模块分文件
- **客户端页面套 ClientLayout**（含 AppHeader 导航栏）
- **管理端页面套 AdminLayout**（含侧边栏）
- **登录/注册页独立布局**（无导航栏）
- **状态管理：** 全局状态用 Pinia，页面数据用 ref/reactive
- **Element Plus 组件：** 优先用 el-xxx

## 关键架构决策

| 决策 | 理由 |
|------|------|
| MyBatis-Plus 而非 JPA | 中文社区主流，复杂查询灵活 |
| JWT 认证 | 前后端分离标准方案 |
| 商品审核机制 | 毕设亮点功能 |
| 客户端+管理端双布局 | 同项目内独立路由树，共享组件和 API |
| 一级分类自动聚合子分类商品 | 避免用户点一级分类看到空列表 |
| 订单 pending 唯一由应用层保证 | MySQL 生成列实现 pending 唯一索引 |
| images 用 JSON 字段 | MySQL 8.0 原生支持，小数组无性能问题 |
| 不做在线支付 | 毕设论文中作为"未来展望" |

## 注意事项

- Controller 按功能模块 + 管理端分 admin/ 子包
- SQL 建表用 UTF-8mb4，支持 emoji
- 所有金额用 DECIMAL(10,2)
- 图片 URL 存完整路径（方便后续迁 OSS）
- 前端请求统一通过 `api/` 目录的封装函数
- 开发默认 H2 内存数据库，零外部依赖即可启动
- 管理员账号首次启动自动创建: `admin` / `admin123`
