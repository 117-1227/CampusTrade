# TASKS.md — CampusTrade 开发任务拆分

> 版本：v1.0 MVP | 最后更新：2026-05-22
> 技术栈：Spring Boot 3 + Vue 3 + MyBatis-Plus + MySQL
> 实际工时：~120 小时 | 状态：✅ 全部完成

---

## 阶段 0：项目初始化 ✅ 已完成

### Task 0.1 Maven 项目脚手架 ✅
- Spring Boot 3.2.5 + MyBatis-Plus + Spring Security + JWT
- 统一响应 `Result.java`、全局异常处理 `GlobalExceptionHandler`
- H2 开发环境（零依赖启动）+ MySQL 生产 profile
- 验收：`./mvnw spring-boot:run` → `curl /api/hello` 返回 `{"code":200,"data":"CampusTrade API is running!"}`

### Task 0.2 Vue 项目脚手架 ✅
- Vue 3 + TypeScript + Vite + Element Plus + Pinia + Vue Router
- Axios 封装 + JWT 拦截器 + 请求序列号防竞态
- 验收：`npm run dev` + `npm run build` + `npm run lint` + `vue-tsc --noEmit` 全通过

### Task 0.3 数据库初始化 ✅
- 7 张表：user / category / product / favorite / conversation / message / order
- 分类种子数据（6 个一级分类）+ DataInitializer 自动创建管理员
- H2 和 MySQL 双 schema 维护

---

## 阶段 1：认证系统 ✅ 已完成（18 tests）

### Task 1.1 JWT 登录 ✅
- JwtTokenProvider（生成/验证/解析）+ JwtAuthenticationFilter
- UserDetailsServiceImpl（加载用户+权限 ROLE_user/ROLE_admin）
- SecurityConfig（认证路由白名单 + admin 路由 hasRole）

### Task 1.2 用户注册 ✅
- AuthService.register() + 用户名唯一校验 + BCrypt 密码加密
- POST /api/auth/register

### Task 1.3 用户登录 ✅
- AuthService.login() + 返回 JWT token + UserVO
- POST /api/auth/login → {token, user}

### Task 1.4 管理员登录 ✅
- POST /api/admin/auth/login（仅 role=admin 放行）
- DataInitializer 首次启动自动创建 admin/admin123
- 前端登录后角色自动跳转（admin→/admin, user→/）

---

## 阶段 2：商品模块 ✅ 已完成（21 tests）

### Task 2.1 图片上传 ✅
- FileUploadUtil（类型校验 jpg/png/webp/gif，5MB 限制，UUID 命名）
- POST /api/upload/image → 返回访问 URL
- WebConfig 静态资源映射 `/uploads/**`

### Task 2.2 商品发布 ✅
- Product entity + CreateProductDTO + ProductServiceImpl.create()
- POST /api/products（auditStatus = 'pending'）
- 前端 PublishPage（表单 + 图片上传 + 分类选择）

### Task 2.3 商品编辑与删除 ✅
- PUT /api/products/{id}（归属权校验：只能改自己的）
- DELETE /api/products/{id}（软删除：status → 'hidden'）

### Task 2.4 商品详情 ✅
- GET /api/products/{id}（含卖家信息 + 分类名 + viewCount +1）
- 前端 ProductDetailPage（联系卖家/收藏/下单）

### Task 2.5 分类管理 ✅
- GET /api/categories（树形结构）+ 前端分类导航
- 一级分类点击自动聚合子分类商品（ProductServiceImpl.listByKeyword in 子类 IDs）

---

## 阶段 3：商品审核与后台管理 ✅ 已完成（33 tests）

### Task 3.1 商品审核 ✅
- GET /api/admin/products/pending + PUT /api/admin/products/{id}/audit
- AdminProductServiceImpl：通过→approved 上架 / 驳回→rejected+原因
- 用户端列表仅展示 auditStatus='approved' 的商品

### Task 3.2 用户管理 ✅
- GET /api/admin/users（分页）+ PUT /api/admin/users/{id}/status（启用/禁用）
- 禁用用户不能登录（UserDetailsServiceImpl 检查 status）

### Task 3.3 分类管理 ✅
- AdminCategoryController CRUD（删除前检查是否有商品）

### Task 3.4 数据概览 ✅
- GET /api/admin/dashboard → {totalUsers, totalProducts, pendingAudits, approvedProducts}

---

## 阶段 4：收藏与个人中心 ✅ 已完成（41 tests）

### Task 4.1 收藏功能 ✅
- FavoriteService.toggle() + isFavorited() + listByUser()
- POST /api/products/{id}/favorite（收藏/取消）
- 前端收藏按钮 + 商品卡片收藏状态

### Task 4.2 个人中心 ✅
- GET/PUT /api/users/me（查看/编辑个人资料）
- 前端 ProfilePage（我的发布/收藏/订单 Tab）

### Task 4.3 卖家主页 ✅
- GET /api/users/{id}/profile（公开卖家信息+在售商品，去敏）
- 前端 SellerPage

---

## 阶段 5：私信聊天 ✅ 已完成（50 tests）

### Task 5.1 会话管理 ✅
- ConversationService.getOrCreate() + listByUser()
- 并发起会话与已有会话的 buyer-seller-product 唯一约束

### Task 5.2 消息收发 ✅
- sendMessage + getMessages（含参与者校验：非 buyer/seller 不可读写）
- POST /api/conversations/{id}/messages + GET /api/conversations/{id}/messages

### Task 5.3 WebSocket ✅
- 延后至 v2，MVP 用 HTTP 轮询

### Task 5.4 前端聊天 ✅
- ChatPage（会话列表 + 聊天窗口 + 发送消息）

---

## 阶段 6：订单流程 ✅ 已完成（59 tests）

### Task 6.1 下单 ✅
- POST /api/orders（创建订单 → 商品 status → 'reserved'）
- 防自买 + 防重复下单 + pending 唯一约束（MySQL 生成列 + H2 计算列）

### Task 6.2 确认成交/取消 ✅
- PUT /api/orders/{id}/status（卖家确认→商品 sold / 任意方取消→商品 active）
- 状态权限校验（买家可取消、卖家可确认/取消）

### Task 6.3 订单列表 ✅
- GET /api/orders/buy（买家视角）+ GET /api/orders/sell（卖家视角）
- 前端 OrdersPage（我买的/我卖的双 Tab + 确认/取消按钮）

---

## 阶段 7：优化与部署 ✅ 已完成（61 tests）

### Task 7.1 搜索优化 ✅
- 首页搜索栏 + 分类筛选 + 请求序列号防竞态
- 一级分类自动聚合子分类商品

### Task 7.2 前端打磨 ✅
- ClientLayout + AppHeader（全局导航栏）
- AdminLayout（侧边栏 + 路由子嵌套）
- 登录后角色自动跳转（admin→/admin, user→/）
- safeFirstImage() 防异常 JSON 崩页面

### Task 7.3 安全加固 ✅
- RateLimitConfig（登录限流框架）
- 会话参与者校验、卖家接口去敏（password/phone/role/status 不暴露）
- SecurityConfigTest 12 个集成测试

### Task 7.4 部署 ✅
- Nginx 配置（docs/deploy/nginx.conf）
- 前端 build + Nginx 静态服务 + API 反向代理

---

## 测试覆盖

| 测试类 | 测试数 | 覆盖范围 |
|--------|--------|----------|
| JwtTokenProviderTest | 8 | JWT 生成/验证/解析/过期/篡改 |
| AuthServiceTest | 10 | 注册/登录/去重/密码校验/禁用用户 |
| CategoryServiceTest | 3 | 分类列表/树形结构/排序 |
| AdminProductServiceTest | 5 | 审核列表/通过/驳回/状态校验 |
| FavoriteServiceTest | 5 | 收藏/取消/列表/状态查询 |
| ConversationServiceTest | 9 | 会话创建/消息发送/参与者校验 |
| OrderServiceTest | 9 | 下单/确认/取消/权限校验 |
| SecurityConfigTest | 12 | 匿名访问/认证访问/管理员拦截/卖家去敏 |
| **合计** | **61** | |

---

## 管理端页面

| 路径 | 页面 | 功能 |
|------|------|------|
| `/admin/login` | LoginPage | 管理员登录（仅 role=admin 可通过） |
| `/admin` | DashboardPage | 4 个统计卡片 |
| `/admin/audit` | AuditPage | 待审核商品表格 + 通过/驳回 |
| `/admin/users` | UsersPage | 用户列表 + 启用/禁用 |
| `/admin/categories` | CategoriesPage | 分类 CRUD 弹窗 |
