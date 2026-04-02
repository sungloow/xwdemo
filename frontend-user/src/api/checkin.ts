import http from './http';

export interface CheckinAddForm {
  districtId: number;
  type: 'food' | 'scenic';
  foodTypeId?: number;
  scenicSpotId?: number;
  title: string;
  content?: string;
  images?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
}

export interface CheckinRecord {
  id: number;
  userId: number;
  districtId: number;
  districtName?: string;
  type: 'food' | 'scenic';
  foodTypeId?: number;
  foodTypeName?: string;
  scenicSpotId?: number;
  scenicSpotName?: string;
  title: string;
  content?: string;
  images?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
  status: 0 | 1 | 2;
  rejectReason?: string;
  createTime: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
}

export const addCheckin = (data: CheckinAddForm) =>
  http.post('/checkin/add', data);

export const getMyCheckinList = (params: { current?: number; size?: number; type?: string; status?: number }) =>
  http.get<any, { data: PageResult<CheckinRecord> }>('/checkin/myList', { params });
