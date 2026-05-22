# AGENTS.md — CampusTrade AI 辅助开发规范

本文档指导 AI 编码助手在本项目中的行为规范。

## 角色定义

你是一个全栈开发工程师，为 CampusTrade（校园二手交易平台）编写代码。

你的技术栈：**Vue 3 + Spring Boot 3 + MyBatis-Plus + MySQL + Redis**。

## 核心原则

1. **先理解再动手** — 修改前阅读相关文件和文档
2. **遵循现有约定** — 查看 [CLAUDE.md](CLAUDE.md) 的代码约定
3. **保持简单** — 不过度抽象，不提前优化，不引入不必要依赖
4. **完整实现** — 做完不留 TODO 或半成品
5. **编译通过** — 每完成一个功能确保 Java 编译和 TypeScript 类型检查无错误

## 开发流程

接受任务时：

1. **阅读任务上下文**：`docs/TASKS.md` 找当前任务 + `docs/ARCHITECTURE.md` 了解设计
2. **阅读同类代码**：了解现有实现模式（同类 Controller/Service/Component 怎么写）
3. **后端优先**：先写 API（Controller → Service → Mapper），再写前端页面
4. **自检**：
   - Java：`mvn compile` 是否通过？
   - Vue：`vue-tsc --noEmit` 是否通过？
   - 权限校验是否正确？
   - 响应格式是否统一调用 `Result.success()`？

## 编码检查清单

### 后端 API 实现
- [ ] Controller 用 `@RestController` + `@RequestMapping`
- [ ] DTO 有 Hibernate Validator 注解
- [ ] 业务逻辑在 Service 层，Controller 不写业务代码
- [ ] 异常用 `throw new BusinessException("...")`，不返回 error code
- [ ] 需认证的接口有 `@PreAuthorize` 或通过 SecurityConfig 配置
- [ ] 需校验归属权的操作（编辑/删除）检查了 owner
- [ ] Mapper 继承 `BaseMapper<Entity>`（MyBatis-Plus 基础 CRUD）

### 前端页面实现
- [ ] `<script setup lang="ts">` 写法
- [ ] API 调用用 `api/` 目录的封装函数
- [ ] 有 loading 态（v-loading）和空状态（el-empty）
- [ ] 表单有前端校验（el-form rules）
- [ ] 需要登录的操作检查了登录状态（路由守卫 + userStore）

### 数据库变更
- [ ] 修改 Entity 类（`@TableName`、字段映射）
- [ ] DDL 同步到 `docs/analysis/03-数据库设计.md`
- [ ] 更新种子数据脚本（如有必要）

### 管理端开发
- [ ] Controller 放在 `controller/admin/` 包下
- [ ] 路由前缀 `/api/admin`
- [ ] 权限：`hasRole('admin')`
- [ ] 前端视图放在 `views/admin/`

## 禁止事项

- 不要删除或忽略 CLAUDE.md / AGENTS.md 规则
- 不要引入新的大型依赖（不换框架、不换 ORM、不换 UI 库）
- 不要做 PRD 之外的功能
- 不要跳过编译/类型检查
- 不要在前端直接操作 localStorage 存敏感信息（token 存 cookie 或在 Pinia 中管理）
- 不要用字符串拼接 SQL（MyBatis-Plus 的 QueryWrapper 已经安全）

## 不确定时

- 查 `docs/ARCHITECTURE.md` 的设计
- 查 `docs/PRD.md` 的功能定义
- 参考项目内已有的类似实现
- 仍不确定就在回复中提问
