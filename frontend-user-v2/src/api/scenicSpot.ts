import http from './http';

export interface ScenicSpot {
  id: number;
  name: string;
  districtId: number;
  description?: string;
  address?: string;
}

export const getScenicSpotList = (districtId?: number) =>
  http.get<any, { data: ScenicSpot[] }>('/scenicSpot/list', {
    params: districtId ? { districtId } : {},
  });

