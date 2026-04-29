import { useFileStore } from '@/store/file';

/**
 * @param response
 * @param name 传入的文件名 包括后缀
 */
export const exportFile = async (response: any, name?: string): Promise<any> => {
  const { headerFileName, clearFileName } = useFileStore();
  // 构造一个blob对象来处理数据
  const blob = new Blob([response]);
  if ('download' in document.createElement('a')) {
    // 支持a标签download的浏览器
    const link = document.createElement('a');
    // a标签添加属性
    link.download = headerFileName ?? name;
    link.style.display = 'none';
    link.href = URL.createObjectURL(blob);
    document.body.appendChild(link);
    // 执行下载
    link.click();
    // 清空文件名缓存
    clearFileName();
    // 释放url
    URL.revokeObjectURL(link.href);
    // 释放标签
    document.body.removeChild(link);
  }
};
