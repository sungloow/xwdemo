import axios from './http';

/** 首页总览（区县/美食/景点三个排名一次性返回，无需登录） */
export const getHomeOverview = async (limit = 10) => {
  return axios.get('/api/home/overview', { params: { limit } });
};

/** 区县打卡热度排名（无需登录） */
export const getDistrictRank = async (limit = 10) => {
  return axios.get('/api/home/districtRank', { params: { limit } });
};

/** 美食种类热度排名（无需登录） */
export const getFoodRank = async (limit = 10) => {
  return axios.get('/api/home/foodRank', { params: { limit } });
};

/** 风景地热度排名（无需登录） */
export const getScenicRank = async (limit = 10) => {
  return axios.get('/api/home/scenicRank', { params: { limit } });
};

/** 前台已发布打卡列表（无需登录） */
export const getPublishedCheckins = async (params: {
  current?: number;
  size?: number;
  districtId?: number;
  type?: 'food' | 'scenic' | '';
}) => {
  return axios.get('/api/home/publishedCheckins', { params });
};

/** 前台打卡详情 */
export const getCheckinDetail = async (id: number) => {
  return axios.get(`/api/home/checkin/${id}`);
};

/** 前台打卡评论列表 */
export const getCheckinComments = async (id: number) => {
  return axios.get(`/api/home/checkin/${id}/comments`);
};
