import axios from './http';

export interface UserQuery {
  current?: number;
  size?: number;
  username?: string;
  realName?: string;
  enabled?: string;
}

export interface UserForm {
  id?: number;
  username?: string;
  password?: string;
  realName?: string;
  email?: string;
  phone?: string;
  enabled?: string;
  districtId?: number;
}

/** 分页查询用户列表 */
export const getUserList = async (params: UserQuery) => {
  return axios.get('/api/user/list', { params });
};

/** 用户详情 */
export const getUserDetail = async (id: number) => {
  return axios.get(`/api/user/${id}`);
};

/** 新增用户（超级管理员） */
export const addUser = async (data: UserForm) => {
  return axios.post('/api/user/add', data);
};

/** 编辑用户信息 */
export const updateUser = async (data: UserForm) => {
  return axios.put('/api/user/update', data);
};

/** 删除用户（超级管理员） */
export const deleteUser = async (id: number) => {
  return axios.delete(`/api/user/${id}`);
};

/** 为用户分配角色（超级管理员） */
export const assignRoles = async (data: { userId: number; roleIds: number[] }) => {
  return axios.post('/api/user/assignRoles', data);
};

/** 查询用户拥有的角色列表 */
export const getUserRoles = async (id: number) => {
  return axios.get(`/api/user/${id}/roles`);
};

/** 批量删除用户（超级管理员），ids 不能包含当前登录用户自己 */
export const deleteUserBatch = async (ids: number[]) => {
  return axios.delete('/api/user/batch', { data: ids });
};
