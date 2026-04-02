# frontend-user-v2

重新设计后的「重庆城市打卡」用户端前端（Vue3 + Vite + TS + Tailwind + Pinia + Vue Router）。

## 启动

在仓库根目录执行：

```bash
cd frontend-user-v2
npm install
npm run dev
```

默认启动端口：`5175`

## 对接后端

该前端默认通过 Vite 代理对接本地后端：

- `/api` → `http://localhost:8080`
- `/uploads` → `http://localhost:8080`

配置位置：`frontend-user-v2/vite.config.ts`

## 鉴权与接口约定

- **Token 存储**：`localStorage.token`
- **自动带 Token**：`src/api/http.ts` 请求拦截器自动注入 `Authorization: Bearer <token>`
- **统一响应处理**：约定后端返回 `{ code, msg, data }`，`code !== 200` 会抛错
- **401 处理**：自动清理 token/userInfo，并跳转 `/login`

## 页面

- `/` 首页（热度榜：`/api/home/overview`）
- `/login` 登录
- `/register` 注册
- `/checkin/new` 发起打卡（需要登录）
- `/my-checkins` 我的打卡（需要登录）
- `/profile` 个人中心（需要登录）

# frontend-user-v2

新的用户端前端（`CityCheckin`），用于替代旧的 `frontend-user`。

## 启动

在仓库根目录：

```bash
cd frontend-user-v2
npm install
npm run dev
```

默认端口：`5175`

## 后端对接

本项目通过 Vite 代理对接后端（见 `vite.config.ts`）：

- `/api` → `http://localhost:8080`
- `/uploads` → `http://localhost:8080`

因此前端请求只需要使用相对路径（例如：`/api/auth/login`、`/uploads/...`）。

## 功能页面

- `/` 首页（热度榜）
- `/login` 登录
- `/register` 注册
- `/checkin/new` 发起打卡（需要登录）
- `/my-checkins` 我的打卡（需要登录）
- `/profile` 个人中心（需要登录）

# Vue 3 + TypeScript + Vite

This template should help get you started developing with Vue 3 and TypeScript in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about the recommended Project Setup and IDE Support in the [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup).
