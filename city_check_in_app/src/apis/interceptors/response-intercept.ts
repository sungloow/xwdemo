import { ElMessage } from 'element-plus';
import router from '@/router';
import { resetStore } from '@/utils';
import { LOGIN_URL } from '@/config/config';
import { ResultEnum } from './httpEnums';
import { useFileStore } from '@/store/file';

/**
 * 从blobResponse的请求头获取到文件的名字
 * 解决乱码问题
 */
const getFileNameByHeader = (headers: any): string => {
  const dispositionStr = headers['content-disposition'];
  if (!dispositionStr) {
    return '';
  }
  // 获取filename字段的值
  const undecodeFileName = dispositionStr.split(";")[1].split("filename*=utf-8''")[1];
  // 解码
  return decodeURI(undecodeFileName);
};

/**
 * 响应拦截
 * @param Ajax
 */
export const responseIntercept = (Ajax: any) => {
  Ajax.interceptors.response.use((response: any) => {
    if (!response) {
      ElMessage.error('请刷新页面重试');
      return;
    }
    // 接口路径
    const requestPath = response.config.url;
    const { data } = response;
    // 登陆失效
    if (data?.code && (data.code === ResultEnum.EXPIRE || data.code === 401)) {
      resetStore();
      ElMessage.error(data.msg || ResultEnum.ERRMESSAGE);
      router.replace(LOGIN_URL);
      return Promise.reject(response);
    }
    // 其他报错情况
    if (data?.code && data.code !== ResultEnum.SUCCESS) {
        ElMessage.error(data.msg || ResultEnum.ERRMESSAGE);
      return Promise.reject(response);
    }
    // 处理下载文件情况 获取文件名称
    if (response.headers['content-disposition']) {
      const { setFileName } = useFileStore();
      const fileName = getFileNameByHeader(response.headers);
      if (fileName && fileName !== 'undefined' && fileName !== 'null'){
        // 暂时把文件名称放入缓存中
        setFileName(fileName);
      }
    }
    return data;
  }, (error: any) => {
    // 接口路径
    const requestPath = error.config.url;
    let message = '';
    // HTTP 状态码
    const status = error.response?.status;
    switch (status) {
      case 401:
        message = 'token失效，请重新登录';
        break;
      case 403:
        message = '拒绝访问';
        break;
      case 404:
        message = '请求地址错误';
        break;
      case 500:
        message = '服务器故障';
        break;
      default:
        message = '网络连接故障';
    }
    ElMessage.error(message);
    return Promise.reject(error);
  });
};
