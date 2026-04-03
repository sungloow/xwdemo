import http from './http';

export interface RankItem {
  id: number;
  name: string;
  count: number;
  districtName?: string;
}

/** 与后端 /home/overview 返回字段一致（旧名 foodRank/scenicRank 已废弃） */
export interface HomeOverview {
  districtRank: RankItem[];
  foodTypeRank: RankItem[];
  scenicSpotRank: RankItem[];
}

export const getHomeOverview = (limit = 10) =>
  http.get<any, { data: HomeOverview }>('/home/overview', { params: { limit } });

export const getDistrictRank = (limit = 10) =>
  http.get<any, { data: RankItem[] }>('/home/districtRank', { params: { limit } });

export const getFoodRank = (limit = 10) =>
  http.get<any, { data: RankItem[] }>('/home/foodRank', { params: { limit } });

export const getScenicRank = (limit = 10) =>
  http.get<any, { data: RankItem[] }>('/home/scenicRank', { params: { limit } });

