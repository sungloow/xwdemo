import { defineStore } from 'pinia'

export const useSettingsStore = defineStore({
  id: 'app-settings',
  state: () => ({
    collapse: false,
    // 刷新页面
    refresh: false,
  }),

  actions: {
    changeCollapse() {
      this.collapse = !this.collapse
    },
    setRefresh() {
      this.refresh = !this.refresh
    }
  },
  persist: true
});
