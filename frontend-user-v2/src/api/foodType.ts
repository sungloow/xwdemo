import http from './http';

export interface FoodType {
  id: number;
  name: string;
  description?: string;
}

export const getFoodTypeList = () => http.get<any, { data: FoodType[] }>('/foodType/list');

