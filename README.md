# 城市打卡后台管理系统

> 以重庆为例的城市打卡后台管理系统，支持美食/风景地打卡、内容审核、角色权限管理等功能。

---

## 技术栈


| 技术                | 版本      | 说明                |
| ----------------- | ------- | ----------------- |
| Java              | 1.8     | 开发语言              |
| Spring Boot       | 2.7.18  | Web 框架            |
| MyBatis Plus      | 3.5.3.2 | ORM 框架，提供自动 CRUD  |
| H2 Database       | 内嵌      | 内存数据库，每次启动自动初始化数据 |
| Springdoc OpenAPI | 1.7.0   | Swagger 文档生成      |
| Hutool            | 5.8.20  | Java 工具库          |
| Lombok            | 最新      | 简化实体类代码           |
| Maven             | 3.x     | 构建工具              |


---

## 快速启动

### 方式一：直接运行 jar（推荐，指定 Java 8）

```bash
mvn clean package -DskipTests
/opt/java/jdk1.8.0_261/bin/java -jar target/city-checkin-1.0.0.jar
```

### 方式二：Maven 插件启动（需确保当前 JDK 为 Java 8）

```bash
mvn spring-boot:run
```

启动后访问：

- **应用地址**：[http://localhost:8080](http://localhost:8080)
- **Swagger 文档**：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 控制台**：[http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:citycheckin`，用户名: `sa`，密码为空

> 注意：H2 为内存数据库，重启后数据自动恢复为初始示例数据。

---

## 演示账号


| 账号         | 密码         | 角色    | 说明             |
| ---------- | ---------- | ----- | -------------- |
| `admin`    | `admin123` | 超级管理员 | 拥有全部权限         |
| `jb_admin` | `admin123` | 区县管理员 | 负责江北区内容审核与景点维护 |
| `yz_admin` | `admin123` | 区县管理员 | 负责渝中区内容审核与景点维护 |
| `user1`    | `user123`  | 普通用户  | 可发起打卡          |
| `user2`    | `user123`  | 普通用户  | 可发起打卡          |


---

## 项目结构

```
src/main/java/com/citycheckin/
├── CityCheckinApplication.java       # 启动类
├── common/                           # 公共工具类
│   ├── Result.java                   # 统一响应体
│   ├── PageResult.java               # 分页响应体
│   ├── TokenInfo.java                # Token 载荷
│   ├── TokenManager.java             # 内存 Token 管理
│   ├── UserContext.java              # ThreadLocal 当前用户
│   └── exception/                   # 全局异常处理
├── config/
│   ├── MybatisPlusConfig.java        # 分页插件
│   ├── SwaggerConfig.java            # OpenAPI 配置
│   └── WebConfig.java               # CORS + 拦截器注册
├── interceptor/
│   └── AuthInterceptor.java         # Token 认证拦截器
├── entity/                          # 数据库实体（9个）
├── mapper/                          # MyBatis Plus Mapper（9个）
├── service/                         # Service 接口 + 实现
├── controller/                      # REST Controller（9个）
└── dto/                             # 请求/响应 DTO

src/main/resources/
├── application.yml                  # 配置文件
└── sql/
    ├── schema.sql                   # 建表 DDL（启动自动执行）
    └── data.sql                     # 初始化示例数据（启动自动执行）
```

---

## 数据库设计

### 系统表


| 表名              | 说明                       |
| --------------- | ------------------------ |
| `sys_user`      | 用户表（含区县关联字段 district_id） |
| `sys_role`      | 角色表                      |
| `sys_menu`      | 菜单表（树形结构，支持按钮级权限）        |
| `sys_user_role` | 用户-角色关联表                 |
| `sys_role_menu` | 角色-菜单关联表                 |


### 业务表


| 表名            | 说明                     |
| ------------- | ---------------------- |
| `district`    | 区县表（重庆10个区县）           |
| `food_type`   | 美食种类表（火锅、小面等8种）        |
| `scenic_spot` | 风景地表（洪崖洞、磁器口等10个景点）    |
| `checkin`     | 打卡记录表（含状态：待审核/已发布/已拒绝） |


---

## 角色权限说明


| 角色    | CODE             | 权限范围                      |
| ----- | ---------------- | ------------------------- |
| 超级管理员 | `SUPER_ADMIN`    | 全部功能，包括用户/角色/菜单/区县/美食种类管理 |
| 区县管理员 | `DISTRICT_ADMIN` | 审核本区县内容、维护本区县风景地          |
| 普通用户  | `NORMAL_USER`    | 发起打卡（每天最多10次）、查看个人打卡记录    |


---

## 认证方式

采用简单 **Bearer Token** 方案（无需 Spring Security，Demo 级别）：

1. 调用 `POST /api/auth/login` 获取 token
2. 后续所有 `/api/`** 请求在 Header 中携带：
  ```
   Authorization: Bearer {token}
  ```
3. Token 有效期 **24 小时**，存储在服务内存中（重启失效）

> 生产环境需要替换为 **Redis + JWT** 方案。

---

## 主要接口一览

> 完整文档请访问 Swagger：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 认证（无需 Token）


| 方法   | 路径                   | 说明               |
| ---- | -------------------- | ---------------- |
| POST | `/api/auth/login`    | 用户登录，返回 token    |
| POST | `/api/auth/register` | 用户注册（自动分配普通用户角色） |
| POST | `/api/auth/logout`   | 退出登录             |
| GET  | `/api/auth/info`     | 获取当前登录用户信息       |


### 首页统计（无需 Token）


| 方法  | 路径                       | 说明          |
| --- | ------------------------ | ----------- |
| GET | `/api/home/overview`     | 一次性返回三个排名数据 |
| GET | `/api/home/districtRank` | 区县打卡热度排名    |
| GET | `/api/home/foodRank`     | 美食种类热度排名    |
| GET | `/api/home/scenicRank`   | 风景地热度排名     |


### 打卡管理（需登录）


| 方法   | 路径                     | 说明            | 权限   |
| ---- | ---------------------- | ------------- | ---- |
| POST | `/api/checkin/add`     | 发起打卡（每天最多10次） | 普通用户 |
| GET  | `/api/checkin/myList`  | 我的打卡记录        | 登录用户 |
| GET  | `/api/checkin/list`    | 打卡列表（管理端）     | 管理员  |
| GET  | `/api/checkin/pending` | 待审核列表         | 管理员  |
| PUT  | `/api/checkin/review`  | 审核内容（通过/拒绝）   | 管理员  |


### 系统管理（需超级管理员）


| 方法                  | 路径                   | 说明                  |
| ------------------- | -------------------- | ------------------- |
| GET/POST/PUT/DELETE | `/api/user/`**       | 人员管理                |
| GET/POST/PUT/DELETE | `/api/role/**`       | 角色管理                |
| GET/POST/PUT/DELETE | `/api/menu/**`       | 菜单管理                |
| GET/POST/PUT/DELETE | `/api/district/**`   | 区县管理                |
| GET/POST/PUT/DELETE | `/api/foodType/**`   | 美食种类管理              |
| GET/POST/PUT/DELETE | `/api/scenicSpot/**` | 风景地管理（区县管理员也可维护本区县） |


---

## 接口调用示例

### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

返回：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "ed19969cc2a94a9f...",
    "username": "admin",
    "isSuper": 1,
    "roles": ["SUPER_ADMIN"]
  }
}
```

### 2. 查看首页热度排名（无需 Token）

```bash
curl http://localhost:8080/api/home/overview
```

### 3. 发起打卡（美食类）

```bash
curl -X POST http://localhost:8080/api/checkin/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "districtId": 1,
    "type": "food",
    "foodTypeId": 1,
    "title": "渝中区火锅打卡",
    "content": "超好吃的火锅！",
    "address": "重庆市渝中区某街道",
    "latitude": 29.557,
    "longitude": 106.571
  }'
```

### 4. 审核打卡内容（区县管理员）

```bash
curl -X PUT http://localhost:8080/api/checkin/review \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin_token}" \
  -d '{"checkinId": 7, "status": 1}'
```

---

## 统一响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```


| code | 含义            |
| ---- | ------------- |
| 200  | 成功            |
| 400  | 参数错误 / 业务校验失败 |
| 401  | 未登录或 Token 过期 |
| 403  | 无权限           |
| 404  | 资源不存在         |
| 500  | 服务器内部错误       |


