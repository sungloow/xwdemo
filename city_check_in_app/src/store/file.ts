import { defineStore } from 'pinia';

/**
 * 下载文件接口时，获取响应头中的文件名并暂存
 */
export const useFileStore = defineStore({
  id: 'app-file',
  state: () => ({
    headerFileName: '',
  }),

  actions: {
    setFileName(name: string) {
      this.headerFileName = name;
    },
    clearFileName() {
      this.headerFileName = '';
    }
  },
  // persist: true
});
