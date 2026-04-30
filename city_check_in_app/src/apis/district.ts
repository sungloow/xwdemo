import axios from './http';

export interface DistrictForm {
  id?: number;
  name?: string;
  code?: string;
  description?: string;
}

/** 区县列表（全量） */
export const getDistrictList = async () => {
  return axios.get('/api/district/list');
};

/** 区县详情 */
export const getDistrictDetail = async (id: number) => {
  return axios.get(`/api/district/${id}`);
};

/** 新增区县（超级管理员） */
export const addDistrict = async (data: DistrictForm) => {
  return axios.post('/api/district/add', data);
};

/** 编辑区县（超级管理员） */
export const updateDistrict = async (data: DistrictForm) => {
  return axios.put('/api/district/update', data);
};

/** 删除区县（超级管理员） */
export const deleteDistrict = async (id: number) => {
  return axios.delete(`/api/district/${id}`);
};
