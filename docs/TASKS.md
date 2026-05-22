# TASKS.md — CampusTrade 开发任务拆分

> 版本：v1.0 MVP | 最后更新：2026-05-22
> 技术栈：Spring Boot 3 + Vue 3 + MyBatis-Plus + MySQL + Redis
> 总工时估算：~160 小时（含管理端），个人开发约 6-7 周

---

## 阶段 0：项目初始化（预计 10 小时）

### Task 0.1 Maven 项目脚手架 [3h]
- [ ] 创建 Spring Boot 项目（Spring Initializr 或 IDEA 向导）
- [ ] 依赖：spring-boot-starter-web, mybatis-plus-boot-starter, mysql-connector, redis, spring-security, jwt (jjwt), lombok, validation
- [ ] 配置 application.yml（数据源、Redis、文件上传路径）
- [ ] 包结构创建（controller/service/mapper/entity/dto/vo/config/common/utils）
- [ ] 统一响应类 `Result.java`
- [ ] 全局异常处理 `GlobalExceptionHandler.java`
- **验收：** `mvn spring-boot:run` 正常启动，访问 `/api/hello` 返回统一格式

### Task 0.2 Vue 项目脚手架 [3h]
- [ ] `npm create vite@latest client -- --template vue-ts`
- [ ] 安装依赖：element-plus, vue-router, pinia, axios
- [ ] 配置路由（用户端 + 管理端布局）
- [ ] Axios 封装（baseURL + 拦截器 + token 注入）
- [ ] Element Plus 按需导入配置
- **验收：** `npm run dev` 正常启动，可访问首页空白壳

### Task 0.3 数据库初始化 [4h]
- [ ] 创建数据库 `campus_trade`（UTF-8mb4）
- [ ] 创建所有表（对应 8 张表的 DDL）
- [ ] 编写 MyBatis-Plus 实体类和 Mapper 接口
- [ ] 配置 MyBatis-Plus 分页插件
- [ ] 种子数据：6 个分类 + 1 个管理员 + 3 个测试用户 + 10 条商品
- **验收：** 启动项目可查询种子数据

---

## 阶段 1：认证系统（预计 14 小时）

### Task 1.1 Spring Security + JWT 配置 [4h]
- [ ] `SecurityConfig.java` — 放行公共接口、JWT 过滤器注册
- [ ] `JwtTokenProvider.java` — 生成 token、解析 token、校验过期
- [ ] `JwtAuthenticationFilter.java` — 从 Header 提取 token → 设置 SecurityContext
- [ ] `UserDetailsServiceImpl.java` — 实现 UserDetailsService
- [ ] CORS 跨域配置
- **验收：** 无 token 访问受保护接口返回 401

### Task 1.2 用户注册 [3h]
- [ ] `POST /api/auth/register` — 校验用户名唯一、密码长度、BCrypt 加密
- [ ] DTO + Hibernate Validator 校验
- [ ] 前端注册页面（Element Plus Form）
- **验收：** 注册成功入库，重复用户名返回错误

### Task 1.3 用户登录 [3h]
- [ ] `POST /api/auth/login` — 用户名+密码验证 → 返回 JWT
- [ ] `GET /api/auth/me` — 根据 token 返回当前用户信息
- [ ] 前端登录页 + Pinia userStore（存 token + 用户信息）
- [ ] 前端路由守卫（未登录跳转登录页）
- **验收：** 登录后前端存储 token，刷新页面保持登录态

### Task 1.4 管理员认证 [4h]
- [ ] `POST /api/admin/auth/login` — 仅 role=admin 的用户可登录管理端
- [ ] 管理员种子数据（初始化脚本创建 admin/admin123）
- [ ] 前端管理端登录页
- [ ] 管理端路由守卫
- **验收：** 普通用户无法登录管理端

---

## 阶段 2：商品模块（预计 18 小时）

### Task 2.1 图片上传 [4h]
- [ ] 后端：MultipartFile 接收，类型校验（jpg/png/webp），大小限制 5MB，随机文件名
- [ ] `FileUploadUtil.java` 工具类
- [ ] `POST /api/upload/image` — 返回图片访问 URL
- [ ] 前端：Element Plus Upload 组件 + 预览 + 删除 + 前端压缩
- **验收：** 上传图片返回可访问 URL

### Task 2.2 商品发布 [4h]
- [ ] `POST /api/products` — 参数校验 + 写入数据库（audit_status = 'pending'）
- [ ] 前端发布页面（表单：标题/描述/价格/原价/成色/分类/图片）
- [ ] 前端表单校验 + 后端 Hibernate Validator 双保险
- **验收：** 发布后商品进入待审核状态

### Task 2.3 商品编辑与删除 [3h]
- [ ] `PUT /api/products/{id}` — 校验归属权
- [ ] `DELETE /api/products/{id}` — status → 'hidden'
- [ ] 前端编辑页（复用发布表单，数据回填）
- [ ] 我的发布列表中的编辑/删除按钮
- **验收：** 只能操作自己的商品

### Task 2.4 商品详情 [3h]
- [ ] `GET /api/products/{id}` — 关联查询卖家信息、分类名、view_count +1
- [ ] 前端详情页：图片轮播(el-carousel) + 商品信息 + 卖家卡片(可点击进入卖家主页) + 收藏/聊天/下单按钮
- **验收：** 详情展示完整，浏览量增加

### Task 2.5 分类管理 [4h]
- [ ] 用户端 `GET /api/categories` — 返回树形结构
- [ ] 前端分类导航（Header 下拉或首页侧边栏）
- [ ] 管理端分类 CRUD（`/api/admin/categories`）
- [ ] 前端管理端分类管理页（树形表格）
- **验收：** 用户可按分类筛选，管理员可增删改分类

---

## 阶段 3：商品审核与后台管理（预计 16 小时）

### Task 3.1 商品审核 [5h]
- [ ] 管理端 `GET /api/admin/products?auditStatus=pending` — 待审核列表
- [ ] `PUT /api/admin/products/{id}/audit` — 通过（approved → 上架）/ 驳回（rejected + 原因）
- [ ] 用户端列表仅展示 audit_status = 'approved' 的商品
- [ ] 前端审核页面（商品信息预览 + 通过/驳回按钮 + 驳回原因弹窗）
- **验收：** 审核通过后用户端可见，驳回后用户"我的发布"显示驳回原因

### Task 3.2 用户管理 [4h]
- [ ] 管理端 `GET /api/admin/users` — 分页列表
- [ ] `PUT /api/admin/users/{id}/status` — 启用/禁用
- [ ] 禁用用户不能登录
- [ ] 前端用户管理页（Table + 启用/禁用开关）
- **验收：** 禁用用户登录失败

### Task 3.3 管理端数据概览 [4h]
- [ ] `GET /api/admin/dashboard` — 用户总数、商品总数、今日新增商品、交易总数
- [ ] 前端 Dashboard 页面（统计卡片 + 最近注册用户列表 + 最近发布商品列表）
- **验收：** 数据实时统计正确

### Task 3.4 管理端 Layout [3h]
- [ ] AdminLayout（侧边栏菜单 + 顶栏 + 内容区）
- [ ] 菜单项：数据概览、商品审核、用户管理、分类管理
- [ ] 退出登录
- **验收：** 管理端导航完整

---

## 阶段 4：商品浏览与搜索（预计 12 小时）

### Task 4.1 商品列表 [4h]
- [ ] `GET /api/products` — 分页 + 筛选（分类/成色/价格区间）+ 排序（最新/价格升降）+ 关键词搜索
- [ ] MyBatis-Plus 动态条件查询（QueryWrapper）
- [ ] 仅返回 audit_status = 'approved' 且 status = 'active' 的商品
- [ ] 前端列表页：Element Plus Card 网格 + 分页器
- [ ] 前端筛选栏（el-select / el-input-number / el-radio-group）
- **验收：** 筛选条件联动正确，分页正常

### Task 4.2 首页 [4h]
- [ ] 搜索焦点 + 分类入口区 + 最新商品 + 热门商品（按浏览量）
- [ ] 复用 ProductCard 组件
- [ ] 骨架屏 loading（el-skeleton）
- **验收：** 首页展示正常

### Task 4.3 搜索与边界处理 [4h]
- [ ] 全局搜索框（Header）
- [ ] 搜索结果页（复用列表页 + 关键词高亮）
- [ ] 空状态（el-empty）、网络错误重试
- [ ] 已售商品手气展示标记
- **验收：** 各边界有友好提示

---

## 阶段 5：收藏与个人中心（预计 10 小时）

### Task 5.1 收藏功能 [3h]
- [ ] `POST /api/products/{id}/favorite` — 幂等
- [ ] `DELETE /api/products/{id}/favorite` — 取消收藏
- [ ] 同步更新 product.fav_count
- [ ] 前端：详情页收藏按钮（星星切换）+ 列表页悬浮收藏
- **验收：** 收藏状态正确切换

### Task 5.2 收藏列表 [2h]
- [ ] `GET /api/users/me/favorites` — 分页
- [ ] 前端收藏列表页（支持取消收藏）
- **验收：** 列表正确展示

### Task 5.3 个人中心 [3h]
- [ ] 个人信息展示 + 编辑（头像、昵称、手机号）
- [ ] 我的发布 Tab（分状态：在售/待审核/已驳回/已售）
- [ ] 我买到的 / 我卖出的 Tab
- **验收：** 各 Tab 数据正确

### Task 5.4 卖家主页 [2h]
- [ ] `GET /api/users/{id}` — 公开信息 + 在售商品列表
- [ ] 前端卖家主页
- **验收：** 点击卖家头像进入卖家主页

---

## 阶段 6：私信系统（预计 16 小时）

### Task 6.1 会话管理 [3h]
- [ ] `POST /api/conversations` — 同一商品同对用户去重
- [ ] `GET /api/conversations` — 会话列表（按最后消息时间倒序）
- [ ] 前端：商品详情页"联系卖家"创建会话
- **验收：** 不重复创建会话

### Task 6.2 消息发送与历史 [3h]
- [ ] `POST /api/conversations/{id}/messages` — 保存消息 + 更新最后消息
- [ ] `GET /api/conversations/{id}/messages` — 分页历史消息
- [ ] 前端聊天窗口 UI（消息气泡 + 发送框）
- **验收：** 可收发消息

### Task 6.3 WebSocket 实时推送 [6h]
- [ ] Spring WebSocket 配置 + 拦截器（从 URL 参数取 token 认证）
- [ ] 在线用户管理（ConcurrentHashMap 存 userId → WebSocketSession）
- [ ] 发送消息时推送至接收方
- [ ] 前端 WebSocket 连接 + 接收消息 + 未读标记
- [ ] 断线重连机制
- **验收：** 双端在线时消息实时送达

### Task 6.4 会话列表完善 [4h]
- [ ] 会话列表附带对方信息 + 商品摘要 + 未读数
- [ ] 前端会话列表页 + 未读红点
- [ ] 新消息时列表自动排序
- **验收：** 会话列表实时更新

---

## 阶段 7：订单交易（预计 10 小时）

### Task 7.1 下单 [3h]
- [ ] `POST /api/orders` — 校验：买家≠卖家、商品在售、不重复下单
- [ ] 商品状态 → 'reserved'
- [ ] 前端"我想要"按钮 + 确认弹窗
- **验收：** 下单成功后商品显示已预定

### Task 7.2 订单状态流转 [4h]
- [ ] `PUT /api/orders/{id}/status` — pending → completed / cancelled
- [ ] completed → 商品 status = 'sold'
- [ ] cancelled → 商品 status = 'active'
- [ ] 前端：卖家确认成交按钮 / 买家取消按钮
- **验收：** 状态正确联动

### Task 7.3 订单列表 [3h]
- [ ] 买到的 / 卖出的列表
- [ ] 前端订单卡片（商品缩略图 + 对方昵称 + 状态标签 + 时间）
- **验收：** 双视角数据正确

---

## 阶段 8：打磨与部署（预计 14 小时）

### Task 8.1 UI/UX 打磨 [4h]
- [ ] 统一 loading / empty / error 状态处理
- [ ] Element Plus 消息提示统一（el-message）
- [ ] 页面过渡动画（可选）
- **验收：** 核心路径操作体验流畅

### Task 8.2 性能优化 [3h]
- [ ] 前端路由懒加载
- [ ] 图片懒加载
- [ ] Redis 缓存热点数据（分类列表、首页商品）
- [ ] 数据库索引检查
- **验收：** 首屏加载 < 1s

### Task 8.3 安全加固 [3h]
- [ ] 接口限流（登录敏感接口）
- [ ] JWT 过期刷新（P1，非 P0 阻塞项）
- [ ] XSS 过滤
- [ ] 文件上传安全复查
- **验收：** 常见攻击面有防护

### Task 8.4 部署 [4h]
- [ ] 服务器环境：JDK 17 + MySQL + Nginx（Redis 可选）
- [ ] 后端 jar 包部署 + systemd 开机自启
- [ ] 前端 build + Nginx 配置（静态资源 + API 代理）
- [ ] 数据库初始化脚本
- **验收：** 公网可访问

---

## 依赖关系图

```
0.1 + 0.2 + 0.3 (初始化，可并行)
    ↓
1.1 → 1.2 → 1.3 → 1.4 (认证链)
    ↓
2.1 → 2.2 → 2.3 → 2.4 (商品 CRUD)
    ↘
    2.5 (分类，可与2.2并行)
    ↓
3.1 → 3.2 → 3.3 → 3.4 (管理端，可与阶段4并行)
    ↓
4.1 → 4.2 → 4.3 (浏览搜索，可与阶段5并行)
    ↓
5.1 → 5.2 → 5.3 → 5.4 (收藏+个人中心)
    ↓
6.1 → 6.2 → 6.3 → 6.4 (私信)
    ↓
7.1 → 7.2 → 7.3 (订单)
    ↓
8.1 → 8.2 → 8.3 → 8.4 (打磨部署)
```

## 第一周执行计划

| 天 | 任务 | 产出 |
|----|------|------|
| Day 1 | Task 0.1 + 0.2 | Spring Boot 脚手架 + Vue 脚手架跑通 |
| Day 2 | Task 0.3 | 数据库建表 + 种子数据 |
| Day 3 | Task 1.1 | Spring Security + JWT 配置完成 |
| Day 4 | Task 1.2 + 1.3 | 注册 + 登录 API + 前端页面 |
| Day 5 | Task 1.4 | 管理员登录 |
| Day 6 | Task 2.1 | 图片上传 |
| Day 7 | Task 2.2 + 2.5 | 商品发布 + 分类（闭环形成！） |
