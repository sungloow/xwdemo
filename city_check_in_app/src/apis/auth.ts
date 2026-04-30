import axios from './http';

/** 登录 */
export const login = async (params: { username: string; password: string }) => {
  return axios.post('/api/auth/login', params);
};

/** 注册 */
export const register = async (params: {
  username: string;
  password: string;
  realName?: string;
  email?: string;
  phone?: string;
}) => {
  return axios.post('/api/auth/register', params);
};

/** 退出登录 */
export const logout = async () => {
  return axios.post('/api/auth/logout');
};

/** 获取当前登录用户信息（含角色） */
export const getUserInfo = async () => {
  return axios.get('/api/auth/info');
};

/**
 * 忘记密码
 * @param params
 */
// export const updatePasswd = async (params: { username: string; password: string }) => {
//   return axios.post('/api/auth/updatePasswd', params);
// };

/**
 * 忘记密码（自助重置，无需登录）
 * 通过「用户名 + 注册手机号」验证身份后设置新密码
 */
export const forgotPassword = async (params: {
  username: string;
  phone: string;
  newPassword: string;
}) => {
  return axios.post('/api/auth/forgotPassword', params);
};

/**
 * 管理员重置用户密码（需超级管理员登录）
 * 直接传 userId + newPassword，无需原密码
 */
export const resetPasswordByAdmin = async (params: {
  userId: number;
  newPassword: string;
}) => {
  return axios.post('/api/auth/resetPassword', params);
};
