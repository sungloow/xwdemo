-- ========================
-- 城市打卡后台管理系统示例数据
-- 每次启动自动恢复
-- 密码统一为明文存储（Demo用途），生产环境应使用加密存储
-- 默认密码：admin123 / user123
-- ========================

-- ===== 区县数据 =====
INSERT INTO district (ID, NAME, CODE, DESCRIPTION, CREATE_TIME, UPDATE_TIME) VALUES
(1,  '渝中区', 'YZ',  '重庆市渝中区，重庆母城，商业中心', NOW(), NOW()),
(2,  '江北区', 'JB',  '重庆市江北区，观音桥商圈所在地', NOW(), NOW()),
(3,  '渝北区', 'YB',  '重庆市渝北区，重庆国际机场所在地', NOW(), NOW()),
(4,  '南岸区', 'NA',  '重庆市南岸区，南山风景区所在地', NOW(), NOW()),
(5,  '沙坪坝区', 'SPB', '重庆市沙坪坝区，磁器口所在地', NOW(), NOW()),
(6,  '九龙坡区', 'JLP', '重庆市九龙坡区，杨家坪商圈所在地', NOW(), NOW()),
(7,  '大渡口区', 'DDK', '重庆市大渡口区', NOW(), NOW()),
(8,  '北碚区',  'BB',  '重庆市北碚区，缙云山所在地', NOW(), NOW()),
(9,  '巴南区',  'BN',  '重庆市巴南区', NOW(), NOW()),
(10, '长寿区',  'CS',  '重庆市长寿区，长寿湖所在地', NOW(), NOW());

-- ===== 美食种类数据 =====
INSERT INTO food_type (ID, NAME, DESCRIPTION, CREATE_TIME, UPDATE_TIME) VALUES
(1, '火锅',   '重庆最具代表性的美食，麻辣鲜香', NOW(), NOW()),
(2, '小面',   '重庆特色小吃，麻辣味重', NOW(), NOW()),
(3, '烤鱼',   '重庆特色烤鱼，香辣美味', NOW(), NOW()),
(4, '串串香', '重庆特色串串，品种多样', NOW(), NOW()),
(5, '酸辣粉', '重庆特色小吃，酸辣爽口', NOW(), NOW()),
(6, '麻辣烫', '重庆特色麻辣烫，选料丰富', NOW(), NOW()),
(7, '烧烤',   '重庆特色烧烤，豪放派', NOW(), NOW()),
(8, '江湖菜', '重庆江湖菜，独具地方特色', NOW(), NOW());

-- ===== 风景地数据 =====
INSERT INTO scenic_spot (ID, NAME, DISTRICT_ID, DESCRIPTION, ADDRESS, CREATE_TIME, UPDATE_TIME, CREATOR) VALUES
(1,  '解放碑',         1, '重庆市标志性建筑，购物天堂，夜景绚丽', '重庆市渝中区解放碑步行街', NOW(), NOW(), 'admin'),
(2,  '洪崖洞',         1, '重庆最具代表性的民俗风貌区，夜景一绝', '重庆市渝中区嘉陵江滨江路88号', NOW(), NOW(), 'admin'),
(3,  '鹅岭公园',       1, '重庆最早的公园之一，可俯瞰两江交汇', '重庆市渝中区鹅岭正街65号', NOW(), NOW(), 'admin'),
(4,  '磁器口古镇',     5, '重庆著名的历史文化名镇，明清风格建筑', '重庆市沙坪坝区磁器口古镇', NOW(), NOW(), 'admin'),
(5,  '南滨路',         4, '重庆网红景点，夜景壮观，餐饮集中', '重庆市南岸区南滨路', NOW(), NOW(), 'admin'),
(6,  '南山一棵树观景台', 4, '重庆最佳夜景观赏地之一，俯瞰全城', '重庆市南岸区南山植物园附近', NOW(), NOW(), 'admin'),
(7,  '观音桥步行街',   2, '重庆最繁华的商业街之一', '重庆市江北区观音桥', NOW(), NOW(), 'admin'),
(8,  '长江索道',       1, '重庆著名的空中交通工具，俯瞰长江两岸', '重庆市渝中区新华路滨江支路', NOW(), NOW(), 'admin'),
(9,  '缙云山',         8, '重庆著名的自然景区，佛教圣地', '重庆市北碚区缙云山', NOW(), NOW(), 'admin'),
(10, '长寿湖',         10, '重庆著名的湖泊景区，风景秀丽', '重庆市长寿区长寿湖镇', NOW(), NOW(), 'admin');

-- ===== 菜单数据 =====
-- INSERT INTO sys_menu (ID, PID, TYPE, LEVEL, CODE, NAME, COMPONENT_NAME, ICON, IS_SHOW, COMPONENT, PATH, SORT_ORDER, AFFIX, KEEP_ALIVE, CREATE_TIME, UPDATE_TIME, CREATOR) VALUES
-- -- 一级菜单
-- (1,  0,  'menu',   1, 'home',        '首页',     'Home',        'home',           1, 'views/home/index',         '/home',        1,  1, 0, NOW(), NOW(), 'admin'),
-- (2,  0,  'menu',   1, 'checkin',     '打卡管理', 'Checkin',     'location',       1, 'views/checkin/index',      '/checkin',     2,  0, 0, NOW(), NOW(), 'admin'),
-- (3,  0,  'menu',   1, 'content',     '内容审核', 'ContentReview','audit',         1, 'views/content/index',      '/content',     3,  0, 0, NOW(), NOW(), 'admin'),
-- (4,  0,  'menu',   1, 'system',      '系统管理', 'System',      'setting',        1, 'views/system/index',       '/system',      4,  0, 0, NOW(), NOW(), 'admin'),
-- (5,  0,  'menu',   1, 'scenic',      '景点管理', 'Scenic',      'picture',        1, 'views/scenic/index',       '/scenic',      5,  0, 0, NOW(), NOW(), 'admin'),
-- (6,  0,  'menu',   1, 'personal',    '个人中心', 'Personal',    'user',           1, 'views/personal/index',     '/personal',    6,  0, 0, NOW(), NOW(), 'admin'),
-- -- 系统管理子菜单
-- (7,  4,  'menu',   2, 'user',        '人员管理', 'User',        'peoples',        1, 'views/system/user',        '/system/user', 1,  0, 0, NOW(), NOW(), 'admin'),
-- (8,  4,  'menu',   2, 'role',        '角色管理', 'Role',        'role',           1, 'views/system/role',        '/system/role', 2,  0, 0, NOW(), NOW(), 'admin'),
-- (9,  4,  'menu',   2, 'menuManage',  '菜单管理', 'MenuManage',  'tree-table',     1, 'views/system/menu',        '/system/menu', 3,  0, 0, NOW(), NOW(), 'admin'),
-- (10, 4,  'menu',   2, 'district',    '区县管理', 'District',    'map',            1, 'views/system/district',    '/system/district', 4, 0, 0, NOW(), NOW(), 'admin'),
-- (11, 4,  'menu',   2, 'foodType',    '美食种类', 'FoodType',    'food',           1, 'views/system/foodtype',    '/system/foodtype', 5, 0, 0, NOW(), NOW(), 'admin'),
-- -- 按钮权限
-- (12, 7,  'button', 2, 'user:add',    '新增用户', NULL, NULL, 0, NULL, NULL, 1, 0, 0, NOW(), NOW(), 'admin'),
-- (13, 7,  'button', 2, 'user:edit',   '编辑用户', NULL, NULL, 0, NULL, NULL, 2, 0, 0, NOW(), NOW(), 'admin'),
-- (14, 7,  'button', 2, 'user:delete', '删除用户', NULL, NULL, 0, NULL, NULL, 3, 0, 0, NOW(), NOW(), 'admin'),
-- (15, 3,  'button', 2, 'content:review','审核内容',NULL, NULL, 0, NULL, NULL, 1, 0, 0, NOW(), NOW(), 'admin');


insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`,component,`path`,`level`, icon) values(
1, 0, '系统管理', 'system', 1, 'menu', 1, 'layouts/index', '/system', 1, 'Setting'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`,component,`path`, `level`, icon, COMPONENT_NAME, KEEP_ALIVE) values(
2, 1, '菜单管理', 'system:menu', 1, 'menu', 1, 'system/menu/index', '/system/menu', 2, 'Menu', 'Menu', 1
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
3, 2, '新增', 'system:menu:add', 3, 'button', 1, 3, 'Plus'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
4, 2, '编辑', 'system:menu:update', 3, 'button', 1, 3, 'Edit'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
5, 2, '是否可见', 'system:menu:show', 3, 'button', 1, 3, ''
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`,component,`path`, `level`, icon, COMPONENT_NAME, KEEP_ALIVE) values(
6, 1, '角色管理', 'system:role', 2, 'menu', 1, 'system/role/index', '/system/role', 2, 'Avatar', 'Role', 1
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
7, 6, '新增', 'system:role:add', 3, 'button', 1, 3, 'Plus'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
8, 6, '编辑', 'system:role:update', 3, 'button', 1, 3, 'Edit'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
9,6,'删除','system:role:delete',3,'button',1,3,'Delete'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
10,6,'批量删除','system:role:remove',3,'button',1,3,'Remove'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`,component,`path`, `level`, icon, COMPONENT_NAME, KEEP_ALIVE) values(
11, 1, '用户管理', 'system:user', 3, 'menu', 1, 'system/user/index', '/system/user', 2, 'UserFilled', 'User', 1
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
12, 11, '新增', 'system:user:add', 1, 'button', 1, 3, 'Plus'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
13, 11, '编辑', 'system:user:update', 2, 'button', 1, 3, 'Edit'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
14, 11, '批量删除', 'system:user:remove', 3, 'button', 1, 3, 'Remove'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
15, 11, '删除', 'system:user:delete', 4, 'button', 1, 3, 'Delete'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`, `level`, icon) values(
16, 11, '重置密码', 'system:user:reset', 4, 'button', 1, 3, 'Tools'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,`TYPE`,`IS_SHOW`,component,`path`, `level`, icon, redirect) values(
70,0,'首页','index:layouts',0,'menu',1,'layouts/index','/',1,'HomeFilled', '/index'
);
insert into sys_menu(ID,PID,NAME,CODE,`SORT_ORDER`,TYPE,`IS_SHOW`, component,path,`level`, AFFIX, icon) values(
71,70,'首页','index',0,'menu',1,'home/index','/index',2,1,'HomeFilled'
);

-- ===== 角色数据 =====
INSERT INTO sys_role (ID, CODE, ROLE_NAME, ROLE_TYPE, DESCRIPTION, ENABLED, CREATE_TIME, UPDATE_TIME, CREATOR) VALUES
(1, 'SUPER_ADMIN',    '超级管理员', 'system', '拥有所有权限的超级管理员', '1', NOW(), NOW(), 'system'),
(2, 'DISTRICT_ADMIN', '区县管理员', 'system', '负责审核所在区县内容，维护区县景点信息', '1', NOW(), NOW(), 'system'),
(3, 'NORMAL_USER',    '普通用户',   'system', '可以上传打卡内容，查看发布信息', '1', NOW(), NOW(), 'system');

-- ===== 角色菜单关联 =====
-- 超级管理员拥有所有菜单
INSERT INTO sys_role_menu (R_ID, M_ID) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15);
-- 区县管理员：首页、内容审核、景点管理、个人中心
INSERT INTO sys_role_menu (R_ID, M_ID) VALUES
(2,1),(2,3),(2,5),(2,6),(2,15);
-- 普通用户：首页、打卡、个人中心
INSERT INTO sys_role_menu (R_ID, M_ID) VALUES
(3,1),(3,2),(3,6);

-- ===== 用户数据 =====
-- 密码统一为 admin123 / user123（Demo明文存储）
INSERT INTO sys_user (ID, USERNAME, PASSWORD, REAL_NAME, EMAIL, PHONE, ENABLED, IS_SUPER, SALT, DISTRICT_ID, UID, CREATOR, CREATE_TIME, UPDATE_TIME) VALUES
(1, 'admin',    'admin123', '超级管理员', 'admin@citycheckin.com',    '13800000001', '1', 1, 'salt01', NULL, 1, 'system', NOW(), NOW()),
(2, 'jb_admin', 'admin123', '江北管理员', 'jb@citycheckin.com',       '13800000002', '1', 0, 'salt02', 2,    1, 'admin',  NOW(), NOW()),
(3, 'yz_admin', 'admin123', '渝中管理员', 'yz@citycheckin.com',       '13800000003', '1', 0, 'salt03', 1,    1, 'admin',  NOW(), NOW()),
(4, 'user1',    'user123',  '张三',       'zhangsan@example.com',     '13900000001', '1', 0, 'salt04', NULL, 1, 'admin',  NOW(), NOW()),
(5, 'user2',    'user123',  '李四',       'lisi@example.com',         '13900000002', '1', 0, 'salt05', NULL, 1, 'admin',  NOW(), NOW()),
(6, 'user3',    'user123',  '王五',       'wangwu@example.com',       '13900000003', '1', 0, 'salt06', NULL, 1, 'admin',  NOW(), NOW());

-- ===== 用户角色关联 =====
INSERT INTO sys_user_role (R_ID, U_ID) VALUES
(1, 1),  -- admin -> 超级管理员
(2, 2),  -- jb_admin -> 区县管理员
(2, 3),  -- yz_admin -> 区县管理员
(3, 4),  -- user1 -> 普通用户
(3, 5),  -- user2 -> 普通用户
(3, 6);  -- user3 -> 普通用户

-- ===== 打卡记录示例数据 =====
INSERT INTO checkin (USER_ID, DISTRICT_ID, TYPE, FOOD_TYPE_ID, SCENIC_SPOT_ID, TITLE, CONTENT, IMAGES, ADDRESS, LATITUDE, LONGITUDE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES
-- 已发布的打卡记录
(4, 1, 'scenic', NULL, 2, '打卡洪崖洞，夜景绝美！', '洪崖洞的夜景真的太美了，层叠的吊脚楼在灯光映衬下如梦似幻，就像宫崎骏动画里的场景。', 'http://example.com/img1.jpg,http://example.com/img2.jpg', '重庆市渝中区嘉陵江滨江路88号', 29.563761, 106.578015, 1, DATEADD('DAY', -2, NOW()), DATEADD('DAY', -2, NOW())),
(4, 5, 'scenic', NULL, 4, '磁器口古镇一日游', '磁器口古镇保存了大量明清时期的建筑，走在青石板路上，感受浓厚的历史文化氛围。', 'http://example.com/img3.jpg', '重庆市沙坪坝区磁器口古镇', 29.566229, 106.467855, 1, DATEADD('DAY', -3, NOW()), DATEADD('DAY', -3, NOW())),
(5, 1, 'food',   1, NULL, '解放碑附近的火锅必吃！', '在解放碑附近找到了一家超棒的火锅店，锅底麻辣鲜香，食材新鲜，绝对的重庆正宗味道！', 'http://example.com/img4.jpg,http://example.com/img5.jpg', '重庆市渝中区解放碑附近', 29.557478, 106.571264, 1, DATEADD('DAY', -1, NOW()), DATEADD('DAY', -1, NOW())),
(5, 2, 'food',   2, NULL, '观音桥旁的重庆小面', '这碗小面真的绝了，麻辣味十足，汤底香浓，配上码子，一口下去满嘴留香！', 'http://example.com/img6.jpg', '重庆市江北区观音桥附近', 29.576862, 106.532020, 1, DATEADD('DAY', -4, NOW()), DATEADD('DAY', -4, NOW())),
(6, 4, 'scenic', NULL, 5, '南滨路夜景超美！', '南滨路是看重庆夜景的最佳地点之一，对岸解放碑的灯光璀璨，江面倒影如画。', 'http://example.com/img7.jpg,http://example.com/img8.jpg,http://example.com/img9.jpg', '重庆市南岸区南滨路', 29.528516, 106.580384, 1, DATEADD('DAY', -5, NOW()), DATEADD('DAY', -5, NOW())),
(6, 1, 'scenic', NULL, 8, '乘长江索道体验空中飞渡', '长江索道是重庆的标志性体验，在空中俯瞰长江两岸的城市风貌，非常震撼！', 'http://example.com/img10.jpg', '重庆市渝中区新华路', 29.555433, 106.579208, 1, DATEADD('DAY', -6, NOW()), DATEADD('DAY', -6, NOW())),
-- 待审核的打卡记录
(4, 2, 'food',   3, NULL, '江北区烤鱼新发现', '发现了一家江北区的特色烤鱼店，香辣入味，强烈推荐！', 'http://example.com/img11.jpg', '重庆市江北区某街道', 29.578023, 106.534560, 0, NOW(), NOW()),
(5, 3, 'scenic', NULL, 3, '鹅岭公园赏秋', '秋天的鹅岭公园景色宜人，树叶变色，拍照效果极佳。', 'http://example.com/img12.jpg', '重庆市渝中区鹅岭正街65号', 29.553829, 106.542373, 0, NOW(), NOW()),
(6, 8, 'scenic', NULL, 9, '缙云山徒步', '缙云山的徒步路线很不错，植被茂密，空气清新，非常适合周末出行放松。', 'http://example.com/img13.jpg', '重庆市北碚区缙云山', 29.731455, 106.373562, 0, NOW(), NOW()),
-- 已拒绝的打卡记录
(4, 1, 'food',   1, NULL, '随便发个测试', '测试内容不够规范', '', '', NULL, NULL, 2, DATEADD('DAY', -7, NOW()), DATEADD('DAY', -7, NOW()));
