-- ========================
-- 城市打卡后台管理系统 DDL
-- ========================

DROP TABLE IF EXISTS checkin_comment;
DROP TABLE IF EXISTS checkin_like;
DROP TABLE IF EXISTS checkin;
DROP TABLE IF EXISTS scenic_spot;
DROP TABLE IF EXISTS food_type;
DROP TABLE IF EXISTS district;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

-- 用户表
CREATE TABLE sys_user (
    ID          INT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    USERNAME    VARCHAR(100)          COMMENT '用户名',
    PASSWORD    VARCHAR(255)          COMMENT '密码',
    REAL_NAME   VARCHAR(255)          COMMENT '用户真实姓名',
    EMAIL       VARCHAR(100)          COMMENT '邮箱',
    PHONE       VARCHAR(11)           COMMENT '电话',
    ENABLED     VARCHAR(10) DEFAULT '1' COMMENT '是否可用 1可用 0禁用',
    IS_SUPER    INT(1) DEFAULT 0      COMMENT '是否超级管理员 1是',
    SALT        VARCHAR(255)          COMMENT '盐',
    DISTRICT_ID INT                   COMMENT '关联区县ID（区县管理员用）',
    UID         INT                   COMMENT '创建人ID',
    CREATOR     VARCHAR(255)          COMMENT '创建人',
    CREATE_TIME DATETIME              COMMENT '创建时间',
    UPDATER     VARCHAR(32)           COMMENT '更新人',
    UPDATE_TIME DATETIME              COMMENT '更新时间',
    PRIMARY KEY (ID)
);

-- 角色表
CREATE TABLE sys_role (
    ID          INT          NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    CODE        VARCHAR(255)          COMMENT '角色码',
    ROLE_NAME   VARCHAR(255)          COMMENT '角色名',
    ROLE_TYPE   VARCHAR(255)          COMMENT '角色类型',
    DESCRIPTION VARCHAR(255)          COMMENT '描述',
    ENABLED     VARCHAR(10) DEFAULT '1' COMMENT '是否可用',
    UID         INT                   COMMENT '创建人ID',
    CREATE_TIME DATETIME              COMMENT '创建时间',
    UPDATE_TIME DATETIME              COMMENT '更新时间',
    UPDATER     VARCHAR(255)          COMMENT '修改人',
    CREATOR     VARCHAR(255)          COMMENT '创建人',
    PRIMARY KEY (ID)
);

-- 用户角色关联表
CREATE TABLE sys_user_role (
    ID   INT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    R_ID INT                         COMMENT '角色ID',
    U_ID INT                         COMMENT '用户ID',
    PRIMARY KEY (ID)
);

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    ID   INT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    R_ID INT                         COMMENT '角色ID',
    M_ID INT                         COMMENT '菜单ID',
    PRIMARY KEY (ID)
);

-- 菜单表
CREATE TABLE sys_menu (
    ID             INT          NOT NULL AUTO_INCREMENT COMMENT 'ID',
    PID            INT                   COMMENT '父ID',
    TYPE           VARCHAR(255)          COMMENT '类型 menu/button',
    LEVEL          INT                   COMMENT '等级',
    CODE           VARCHAR(255)          COMMENT '编码',
    NAME           VARCHAR(100)          COMMENT '名称',
    COMPONENT_NAME VARCHAR(100)          COMMENT '组件名称',
    ICON           VARCHAR(50)           COMMENT '图标名称',
    IS_SHOW        INT(1) DEFAULT 1      COMMENT '是否展示 0不展示 1展示',
    COMPONENT      VARCHAR(100)          COMMENT '组件路径',
    PATH           VARCHAR(100)          COMMENT '访问路径',
    SORT_ORDER     INT                   COMMENT '排序',
    AFFIX          INT(1) DEFAULT 0      COMMENT '是否固定tab 0否 1是',
    KEEP_ALIVE     INT(1) DEFAULT 0      COMMENT '是否缓存页面 0否 1是',
    ACTIVE_MENU    VARCHAR(255)          COMMENT '当前活动菜单',
    REDIRECT       VARCHAR(255)          COMMENT '重定向路径',
    CREATE_TIME    DATETIME              COMMENT '创建时间',
    UPDATE_TIME    DATETIME              COMMENT '更新时间',
    CREATOR        VARCHAR(255)          COMMENT '创建人',
    UPDATER        VARCHAR(255)          COMMENT '修改人',
    PRIMARY KEY (ID)
);

-- 区县表
CREATE TABLE district (
    ID          INT          NOT NULL AUTO_INCREMENT COMMENT '区县ID',
    NAME        VARCHAR(100)          COMMENT '区县名称',
    CODE        VARCHAR(50)           COMMENT '区县编码',
    DESCRIPTION VARCHAR(500)          COMMENT '描述',
    CREATE_TIME DATETIME              COMMENT '创建时间',
    UPDATE_TIME DATETIME              COMMENT '更新时间',
    PRIMARY KEY (ID)
);

-- 美食种类表
CREATE TABLE food_type (
    ID          INT          NOT NULL AUTO_INCREMENT COMMENT '美食种类ID',
    NAME        VARCHAR(100)          COMMENT '种类名称',
    DESCRIPTION VARCHAR(500)          COMMENT '描述',
    CREATE_TIME DATETIME              COMMENT '创建时间',
    UPDATE_TIME DATETIME              COMMENT '更新时间',
    PRIMARY KEY (ID)
);

-- 风景地表
CREATE TABLE scenic_spot (
    ID          INT          NOT NULL AUTO_INCREMENT COMMENT '风景地ID',
    NAME        VARCHAR(255)          COMMENT '风景地名称',
    DISTRICT_ID INT                   COMMENT '所属区县ID',
    DESCRIPTION VARCHAR(1000)         COMMENT '描述',
    ADDRESS     VARCHAR(500)          COMMENT '地址',
    CREATE_TIME DATETIME              COMMENT '创建时间',
    UPDATE_TIME DATETIME              COMMENT '更新时间',
    CREATOR     VARCHAR(255)          COMMENT '创建人',
    UPDATER     VARCHAR(255)          COMMENT '修改人',
    PRIMARY KEY (ID)
);

-- 打卡记录表
CREATE TABLE checkin (
    ID             INT          NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
    USER_ID        INT                   COMMENT '用户ID',
    DISTRICT_ID    INT                   COMMENT '区县ID',
    TYPE           VARCHAR(20)           COMMENT '类型 food/scenic',
    FOOD_TYPE_ID   INT                   COMMENT '美食种类ID（type=food时填写）',
    SCENIC_SPOT_ID INT                   COMMENT '风景地ID（type=scenic时填写）',
    TITLE          VARCHAR(255)          COMMENT '标题',
    CONTENT        TEXT                  COMMENT '内容描述',
    IMAGES         VARCHAR(2000)         COMMENT '图片URL列表（逗号分隔）',
    ADDRESS        VARCHAR(500)          COMMENT '打卡地址',
    LATITUDE       DECIMAL(10, 6)        COMMENT '纬度',
    LONGITUDE      DECIMAL(10, 6)        COMMENT '经度',
    STATUS         INT(1) DEFAULT 0      COMMENT '状态 0待审核 1已发布 2已拒绝',
    REJECT_REASON  VARCHAR(500)          COMMENT '拒绝原因',
    CREATE_TIME    DATETIME              COMMENT '打卡时间',
    UPDATE_TIME    DATETIME              COMMENT '更新时间',
    PRIMARY KEY (ID)
);

-- 打卡点赞表
CREATE TABLE checkin_like (
    ID          INT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    CHECKIN_ID  INT                         COMMENT '打卡ID',
    USER_ID     INT                         COMMENT '用户ID',
    CREATE_TIME DATETIME                    COMMENT '点赞时间',
    PRIMARY KEY (ID)
);

-- 打卡评论表
CREATE TABLE checkin_comment (
    ID          INT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    CHECKIN_ID  INT                         COMMENT '打卡ID',
    USER_ID     INT                         COMMENT '用户ID',
    CONTENT     VARCHAR(200)                COMMENT '评论内容',
    CREATE_TIME DATETIME                    COMMENT '评论时间',
    PRIMARY KEY (ID)
);
