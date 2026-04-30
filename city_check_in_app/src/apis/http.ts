import axios from 'axios';
import { requestIntercept, responseIntercept } from './interceptors';

// 用户中心api默认header
export const headers: any = {
  'Accept-Language': 'zh-CN',
  'Content-Type': 'application/json',
  Accept: 'application/json',
  'Access-Control-Allow-Origin': '',
  'Access-Control-Allow-Credentials': true,
  'Market-Country-Code': 'CN',
  'Device-Id': '1'
};

// headers
axios.defaults.headers = headers;
// timeout 30S
axios.defaults.timeout = 30000;

// 添加请求拦截器
requestIntercept(axios);
// 添加响应拦截器
responseIntercept(axios);

export default axios;
