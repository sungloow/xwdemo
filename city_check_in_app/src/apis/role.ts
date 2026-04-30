import axios from './http';

export interface RoleForm {
  id?: number;
  code?: string;
  roleName?: string;
  roleType?: string;
  description?: string;
  enabled?: string;
}

/** 获取所有角色 */
export const getRoleList = async () => {
  return axios.get('/api/role/list');
};

/** 角色详情（含已分配菜单ID列表） */
export const getRoleDetail = async (id: number) => {
  return axios.get(`/api/role/${id}`);
};

/** 新增角色（超级管理员） */
export const addRole = async (data: RoleForm) => {
  return axios.post('/api/role/add', data);
};

/** 编辑角色（超级管理员） */
export const updateRole = async (data: RoleForm) => {
  return axios.put('/api/role/update', data);
};

/** 删除角色（超级管理员） */
export const deleteRole = async (id: number) => {
  return axios.delete(`/api/role/${id}`);
};

/** 为角色分配菜单（超级管理员） */
export const assignMenus = async (data: { roleId: number; menuIds: number[] }) => {
  return axios.post('/api/role/assignMenus', data);
};
