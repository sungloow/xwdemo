import { useUserStore } from '@/store/user';
import { useAuthStore } from '@/store/auth';
import { useTabsBarStore } from '@/store/tabsBar';

/**
 * 清除stores中数据
 */
export const resetStore = () => {
  const userStore = useUserStore();
  const authStore = useAuthStore();
  const tabsBarStore = useTabsBarStore();

  userStore.$reset();
  authStore.$reset();
  tabsBarStore.$reset();
  // 清除用户信息缓存
  localStorage.removeItem('app-user');
};
