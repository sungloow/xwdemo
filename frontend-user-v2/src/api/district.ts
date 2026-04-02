import http from './http';

export interface District {
  id: number;
  name: string;
  code: string;
  description?: string;
}

export const getDistrictList = () => http.get<any, { data: District[] }>('/district/list');

