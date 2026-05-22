# CLAUDE.md — CampusTrade 项目指引

## 项目概述

**CampusTrade 校园二手交易平台** — 基于 Spring Boot + Vue 的校园二手商品交易系统。

论文题目：**基于 Spring Boot + Vue 的校园二手交易平台设计与实现**

当前 MVP 阶段，包含用户端（二手交易）+ 管理端（商品审核/用户管理/分类管理/数据概览）。

详见 [docs/PRD.md](docs/PRD.md) 和 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus + Pinia + Vue Router 4 |
| 后端 | Spring Boot 3.x + Java 17 + MyBatis-Plus + Spring Security + JWT |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| 实时 | Spring WebSocket |

## 项目结构

```
CampusTrade/
├── client/                  # Vue 3 前端
│   └── src/
│       ├── api/             # Axios 封装 + 接口
│       ├── components/      # 通用组件
│       ├── views/           # 页面（user/ 和 admin/ 子目录）
│       ├── router/          # 路由配置
│       ├── stores/          # Pinia stores
│       └── types/           # TypeScript 类型
├── server/                  # Spring Boot 后端
│   └── src/main/java/com/campustrade/
│       ├── controller/      # REST 控制器（含 admin/ 子包）
│       ├── service/         # 业务接口 + impl/
│       ├── mapper/          # MyBatis-Plus Mapper
│       ├── entity/          # 实体类
│       ├── dto/             # 入参对象
│       ├── vo/              # 出参对象
│       ├── config/          # Spring 配置
│       ├── security/        # JWT + Spring Security
│       ├── common/          # Result、异常处理
│       └── utils/           # 工具类
└── docs/
    ├── analysis/            # 需求分析、功能模块图、ER 图
    ├── PRD.md
    ├── ARCHITECTURE.md
    └── TASKS.md
```

## 常用命令

```bash
# 后端
cd server
./mvnw spring-boot:run                     # 启动开发服务器 :8080（默认 H2）
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql  # 使用 MySQL
./mvnw clean package -DskipTests           # 打包
./mvnw test                                # 运行测试

# 前端
cd client
npm run dev                              # 启动开发服务器 :5173
npm run build                            # 生产构建
npm run lint                             # ESLint
npm run typecheck                        # TypeScript 类型检查（vue-tsc --noEmit）
```

## 代码约定

### 后端（Java）

- **命名：**
  - Controller: `XxxController.java`，类上 `@RequestMapping("/api/xxx")`
  - Service 接口: `XxxService.java`，实现类: `XxxServiceImpl.java`
  - Mapper: `XxxMapper.java`
  - Entity: 类名=表名（驼峰），`@TableName` 指定表名
  - DTO: `XxxDTO.java`（入参），VO: `XxxVO.java`（出参）
- **分层原则：** Controller 只做参数接收和结果返回，Service 做业务逻辑，Mapper 做数据访问
- **参数校验：** DTO 上用 `@NotBlank`、`@NotNull`、`@Size` 等注解
- **异常处理：** `throw new BusinessException("错误信息")` → GlobalExceptionHandler 捕获
- **响应格式：** `Result.success(data)` / `Result.error("错误信息")`
- **权限控制：** 需要登录的接口加 `@PreAuthorize("isAuthenticated()")`，管理员接口加 `@PreAuthorize("hasRole('admin')")`

### 前端（Vue 3）

- **使用 Composition API（`<script setup lang="ts">`）**
- **组件命名：** PascalCase（`ProductCard.vue`）
- **页面放 `views/`，通用组件放 `components/`**
- **API 调用放 `api/` 目录：** 按模块分文件（`api/auth.ts`、`api/product.ts`）
- **状态管理：** 服务端数据用组件内 ref/reactive，全局状态用 Pinia
- **Element Plus 组件：** 优先用 el-xxx，不做过度自定义样式

### 全栈通用

- **TypeScript strict mode**
- **禁止 any 类型**（两端）
- **文件命名：** kebab-case（`product-service.ts`）
- **目录命名：** kebab-case 或复数

## 关键架构决策

1. **MyBatis-Plus 而非 JPA** — 中文社区主流，复杂查询灵活
2. **JWT 认证** — 前后端分离标准方案
3. **商品审核机制** — 毕设亮点，区别于纯 CRUD
4. **Element Plus** — Vue 3 最成熟组件库，中文文档完善
5. **不做在线支付** — 复杂度高，论文中作为"未来展望"

## 当前进度

项目处于需求分析阶段（2026-05-22），尚未开始编码。

## 注意事项

- Controller 按功能模块 + 管理端分 admin/ 子包
- SQL 建表用 UTF-8mb4，支持 emoji
- 所有金额用 DECIMAL(10,2)
- 图片 URL 存完整路径（方便后续迁 OSS）
- 前端请求统一通过 `api/` 目录的封装函数，不在组件里直接调 axios
- 每完成一个 Phase 做一次 git commit
