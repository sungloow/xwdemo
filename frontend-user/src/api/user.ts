import http from './http';

export interface UserUpdateForm {
  id: number;
  realName?: string;
  email?: string;
  phone?: string;
}

export const updateUser = (data: UserUpdateForm) =>
  http.put('/user/update', data);
