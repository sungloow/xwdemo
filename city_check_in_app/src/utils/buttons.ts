import { useAuthStore } from '@/store/auth';

/**
 * 获取按钮的文本内容
 */
export const getOpButtonText = (key: string) => {
  const authStore = useAuthStore();
  const obj: any = authStore.authButtonList.find((item: any) => item.code === key);
  return obj?.name ?? '';
};

/**
 * 获取按钮的icon
 */
export const getOpButtonIcon = (key: string) => {
  const authStore = useAuthStore();
  const obj: any = authStore.authButtonList.find((item: any) => item.code === key);
  return obj?.icon ?? '';
};
