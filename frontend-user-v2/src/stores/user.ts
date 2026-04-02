import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import type { UserInfo } from '@/api/auth';
import { getUserInfo, logout as apiLogout } from '@/api/auth';

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '');
  const userInfo = ref<UserInfo | null>(
    (() => {
      try {
        const s = localStorage.getItem('userInfo');
        return s ? (JSON.parse(s) as UserInfo) : null;
      } catch {
        return null;
      }
    })()
  );

  const isLoggedIn = computed(() => !!token.value);
  const displayName = computed(() => userInfo.value?.realName || userInfo.value?.username || '用户');

  function setToken(t: string) {
    token.value = t;
    localStorage.setItem('token', t);
  }

  function setUserInfo(info: UserInfo) {
    userInfo.value = info;
    localStorage.setItem('userInfo', JSON.stringify(info));
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo();
      setUserInfo(res.data);
    } catch {
      // ignore
    }
  }

  async function logout() {
    try {
      await apiLogout();
    } catch {
      // ignore
    }
    token.value = '';
    userInfo.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
  }

  return { token, userInfo, isLoggedIn, displayName, setToken, setUserInfo, fetchUserInfo, logout };
});

