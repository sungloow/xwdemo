import axios from './http';

export interface ScenicSpotForm {
  id?: number;
  name?: string;
  districtId?: number;
  description?: string;
  address?: string;
}

/** 风景地列表，可按区县过滤 */
// export const getScenicSpotList = async (districtId?: number) => {
//   return axios.get('/api/scenicSpot/list', { params: districtId ? { districtId } : {} });
// };
export const getScenicSpotList = async () => {
  return axios.get('/api/scenicSpot/list');
};

/** 风景地详情 */
export const getScenicSpotDetail = async (id: number) => {
  return axios.get(`/api/scenicSpot/${id}`);
};

/** 新增风景地（超级管理员或本区县管理员） */
export const addScenicSpot = async (data: ScenicSpotForm) => {
  return axios.post('/api/scenicSpot/add', data);
};

/** 编辑风景地 */
export const updateScenicSpot = async (data: ScenicSpotForm) => {
  return axios.put('/api/scenicSpot/update', data);
};

/** 删除风景地 */
export const deleteScenicSpot = async (id: number) => {
  return axios.delete(`/api/scenicSpot/${id}`);
};
