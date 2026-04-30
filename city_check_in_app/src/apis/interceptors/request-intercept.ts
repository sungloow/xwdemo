import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';

/**
 * requestIntercept
 * @param Ajax
 */
export const requestIntercept = (Ajax: any) => {
  // 添加请求拦截器
  Ajax.interceptors.request.use((config: any) => {
    // 添加时间戳
    config.headers['timeStamp'] = new Date().getTime();
    let baseUri = import.meta.env.VITE_BASE_URI;
    config.url = baseUri + config.url;
    const userStore = useUserStore();
    const token = userStore.token;
    if (token) {
      config.headers.authorization = token;
    }
    if (config.method === 'post') {
      if (config.data) {
        Object.keys(config.data).forEach((key) => {
          if (Array.isArray(config.data[key]) && config.data[key].length === 0) {
            delete config.data[key];
          }
        });
      }
    }
    // 文件传输的请求格式
    if (config.url?.endsWith('/upload')) {
      config.headers['Content-Type'] = 'multipart/form-data';
    }
    return config;
  }, function (error: any) {
    // 对请求错误做些什么
    ElMessage.error(error.message);
    return Promise.reject(error);
  });
};
