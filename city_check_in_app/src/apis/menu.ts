import axios from './http';

export interface MenuForm {
  id?: number;
  pid?: number;
  type?: string;
  level?: number;
  code?: string;
  name?: string;
  componentName?: string;
  icon?: string;
  isShow?: number;
  component?: string;
  path?: string;
  sortOrder?: number;
  affix?: number;
  keepAlive?: number;
  activeMenu?: string;
  redirect?: string;
}

/** 获取完整菜单树（超级管理员用） */
export const getMenuTree = async () => {
  return axios.get('/api/menu/tree');
};

/** 获取当前登录用户可见菜单树 */
export const getUserMenuTree = async () => {
  return axios.get('/api/menu/user');
};

/** 新增菜单（超级管理员） */
export const addMenu = async (data: MenuForm) => {
  return axios.post('/api/menu/add', data);
};

/** 编辑菜单（超级管理员） */
export const updateMenu = async (data: MenuForm) => {
  return axios.put('/api/menu/update', data);
};

/** 删除菜单（超级管理员），递归删除子菜单 */
export const deleteMenu = async (id: number) => {
  return axios.delete(`/api/menu/${id}`);
};
