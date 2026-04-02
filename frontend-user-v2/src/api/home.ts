import http from './http';

export interface RankItem {
  id: number;
  name: string;
  count: number;
  districtName?: string;
}

export interface HomeOverview {
  districtRank: RankItem[];
  foodRank: RankItem[];
  scenicRank: RankItem[];
}

export const getHomeOverview = (limit = 10) =>
  http.get<any, { data: HomeOverview }>('/home/overview', { params: { limit } });

export const getDistrictRank = (limit = 10) =>
  http.get<any, { data: RankItem[] }>('/home/districtRank', { params: { limit } });

export const getFoodRank = (limit = 10) =>
  http.get<any, { data: RankItem[] }>('/home/foodRank', { params: { limit } });

export const getScenicRank = (limit = 10) =>
  http.get<any, { data: RankItem[] }>('/home/scenicRank', { params: { limit } });

