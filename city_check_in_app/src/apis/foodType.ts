import axios from './http';

export interface FoodTypeForm {
  id?: number;
  name?: string;
  description?: string;
}

/** 美食种类列表（全量） */
export const getFoodTypeList = async () => {
  return axios.get('/api/foodType/list');
};

/** 美食种类详情 */
export const getFoodTypeDetail = async (id: number) => {
  return axios.get(`/api/foodType/${id}`);
};

/** 新增美食种类（超级管理员） */
export const addFoodType = async (data: FoodTypeForm) => {
  return axios.post('/api/foodType/add', data);
};

/** 编辑美食种类（超级管理员） */
export const updateFoodType = async (data: FoodTypeForm) => {
  return axios.put('/api/foodType/update', data);
};

/** 删除美食种类（超级管理员） */
export const deleteFoodType = async (id: number) => {
  return axios.delete(`/api/foodType/${id}`);
};
