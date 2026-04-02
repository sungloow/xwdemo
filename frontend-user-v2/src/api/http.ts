import axios from 'axios';

const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
});

http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

http.interceptors.response.use(
  (response) => {
    const data = response.data;
    if (data?.code !== 200) {
      return Promise.reject(new Error(data?.msg || '请求失败'));
    }
    return data;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      window.location.href = '/login';
    }
    const msg = error.response?.data?.msg || error.message || '网络异常，请稍后再试';
    return Promise.reject(new Error(msg));
  }
);

export default http;

