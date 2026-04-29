import { useAuthStore } from '@/store/auth';
import type { Directive, DirectiveBinding } from 'vue';

/**
 * 按钮鉴权指令 v-permission
 */
const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding;
    const authStore = useAuthStore();

    // 当前用户的权限列表
    const currentPageRoles = authStore.authButtonList.map((item: any) => item.code) ?? [];

    if (value instanceof Array && value.length) {
      // 是否有权限展示
      const hasAuth = value.every((key: string) => currentPageRoles.includes(key));
      if (!hasAuth) {
        el.remove();
      }
    } else {
      if (!currentPageRoles.includes(value)) {
        el.remove();
      }
    }
  }
};

export default permission;
