## 民航数字监管系统
提供数据上传、数据比对和结果展示以及下载等功能；对不同的用户进行权限管理。

## 使用方式

### install

```
npm install
```

### run

```
npm run dev
```

### build

```
npm run build
```

## 功能介绍
采用 vue3 全家桶（vite+pinia+element-plus+vue-router@4）
- 使用 Vue3 + TypeScript 开发
- 采用 Vite4.0 作为项目开发、打包工具（配置 Gzip 打包、TSX 语法、跨域代理……）
- 使用 Prettier 统一格式化代码，集成 Eslint、Stylelint 代码校验规范
- 使用 Pinia 替代 Vuex，轻量、简单、易用， 集成 Pinia 持久化插件
- 使用 TypeScript 对 Axios 整个二次封装
- 支持菜单权限的配置和管理
- 自定义指令开发

## 文件目录介绍

```
├─ public                 # 静态资源文件（该文件夹不会被打包）
├─ src
│  ├─ apis                # API 接口管理
│  ├─ assets              # 静态资源文件
│  ├─ components          # 全局组件
│  ├─ config              # 全局配置项
│  ├─ directives          # 全局指令文件
│  ├─ layouts             # 框架布局模块
│  ├─ router              # 路由管理
│  ├─ store               # pinia store
│  ├─ styles              # 全局样式文件
│  ├─ typings             # 全局 ts 声明
│  ├─ utils               # 常用工具库
│  ├─ views               # 项目所有页面
│  ├─ App.vue             # 项目主组件
│  └─ main.ts             # 项目入口文件
├─ .env.development       # 开发环境配置
├─ .env.production        # 生产环境配置
├─ .eslintignore          # 忽略 Eslint 校验
├─ .eslintrc.cjs          # Eslint 校验配置文件
├─ .gitignore             # 忽略 git 提交
├─ .prettierrc.json       # Prettier 格式化配置
├─ index.html             # 入口 html
├─ package.json           # 依赖包管理
├─ README.md              # README 介绍
├─ tsconfig.json          # typescript 全局配置
└─ vite.config.ts         # vite 全局配置文件
```
