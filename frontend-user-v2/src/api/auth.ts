import http from './http';

export interface LoginParams {
  username: string;
  password: string;
}

export interface RegisterParams {
  username: string;
  password: string;
  realName?: string;
  email?: string;
  phone?: string;
}

export interface UserInfo {
  id: number;
  username: string;
  realName?: string;
  email?: string;
  phone?: string;
  enabled: string;
  districtId?: number;
  roles?: { id: number; code: string; roleName: string }[];
}

export const login = (params: LoginParams) =>
  http.post<any, { data: { token: string; userInfo: UserInfo } }>('/auth/login', params);

export const register = (params: RegisterParams) => http.post('/auth/register', params);

export const logout = () => http.post('/auth/logout');

export const getUserInfo = () => http.get<any, { data: UserInfo }>('/auth/info');

export const forgotPassword = (params: { username: string; phone: string; newPassword: string }) =>
  http.post('/auth/forgotPassword', params);

