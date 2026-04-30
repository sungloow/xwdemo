import axios from './http';

export interface CheckinAddForm {
  id?: number;
  districtId: number;
  /** food / scenic */
  type: 'food' | 'scenic';
  foodTypeId?: number;
  scenicSpotId?: number;
  title: string;
  content?: string;
  /** 图片URL，逗号分隔 */
  images?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
}

export interface CheckinDetail {
  id: number;
  userId: number;
  districtId: number;
  type: 'food' | 'scenic';
  foodTypeId?: number;
  scenicSpotId?: number;
  title: string;
  content?: string;
  images?: string;
  address?: string;
  createTime?: string;
  districtName?: string;
  foodTypeName?: string;
  scenicSpotName?: string;
  username?: string;
  likeCount?: number;
  commentCount?: number;
  liked?: boolean;
  status?: number;
  rejectReason?: string;
  latitude?: number;
  longitude?: number;
}

export interface CheckinComment {
  id: number;
  checkinId: number;
  userId: number;
  content: string;
  createTime?: string;
  username?: string;
}

export interface CheckinQuery {
  current?: number;
  size?: number;
  districtId?: number;
  type?: 'food' | 'scenic';
  /** 0-待审核 1-已发布 2-已拒绝 */
  status?: number;
}

export interface ReviewForm {
  checkinId: number;
  /** 1-通过 2-拒绝 */
  status: 1 | 2;
  rejectReason?: string;
}

/** 发起打卡（普通用户，每天最多10次） */
export const addCheckin = async (data: CheckinAddForm) => {
  return axios.post('/api/checkin/add', data);
};

/** 编辑我的打卡（仅待审核/已拒绝） */
export const updateMyCheckin = async (data: CheckinAddForm) => {
  return axios.put('/api/checkin/myUpdate', data);
};

/** 打卡列表（管理端，超级管理员/区县管理员） */
export const getCheckinList = async (params: CheckinQuery) => {
  return axios.get('/api/checkin/list', { params });
};

/** 我的打卡记录 */
export const getMyCheckinList = async (params: Omit<CheckinQuery, 'districtId'>) => {
  return axios.get('/api/checkin/myList', { params });
};

/** 我的打卡详情（可查看任意审核状态） */
export const getMyCheckinDetail = async (id: number) => {
  return axios.get(`/api/checkin/myDetail/${id}`);
};

/** 待审核列表（区县管理员只看本区县，超级管理员看全部） */
export const getPendingCheckinList = async (params: { current?: number; size?: number }) => {
  return axios.get('/api/checkin/pending', { params });
};

/** 审核打卡内容（status=1通过，status=2拒绝） */
export const reviewCheckin = async (data: ReviewForm) => {
  return axios.put('/api/checkin/review', data);
};

/** 点赞打卡 */
export const likeCheckin = async (id: number) => {
  return axios.post(`/api/checkin/${id}/like`);
};

/** 取消点赞 */
export const unlikeCheckin = async (id: number) => {
  return axios.delete(`/api/checkin/${id}/like`);
};

/** 发布评论 */
export const addCheckinComment = async (id: number, data: { content: string }) => {
  return axios.post(`/api/checkin/${id}/comment`, data);
};
