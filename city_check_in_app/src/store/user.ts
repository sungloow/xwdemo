import { defineStore } from 'pinia';
import { getUserInfo, logout } from '@/apis';
import { useAuthStore } from './auth';
import { resetStore } from '@/utils/storeUtil';

export const useUserStore = defineStore({
  id: 'app-user',
  state: () => ({
    token: '',
    userInfo: null,
  }),
  actions: {
    setToken(token: string) {
      this.token = token;
    },

    setUserInfo(info: any) {
      this.userInfo = info;
    },

    async GetInfoAction() {
        const res: any = await getUserInfo();
        const { buttons, menus, realName, username, ...others } = res?.data ?? {};
        const authStore = useAuthStore();
        // 存储用户信息
        this.setUserInfo({
          username: username?? 'XXX',
          roleName: realName ?? ''
        });
        // 存储用户权限信息
        authStore.setAuth({ buttons, routes: menus });
        // 存储用户隐私信息
        authStore.setAuthInfo({ ...others });
    },

    async Logout() {
      await logout();
      resetStore();
    },
  },
  // 设置为true，缓存state
  persist: true
});
