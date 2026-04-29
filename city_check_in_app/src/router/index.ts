import { createRouter, createWebHashHistory } from 'vue-router';
import { staticRoutes } from './constantRoutes';

const router = createRouter({
  history: createWebHashHistory(),
  routes: staticRoutes,
  // 刷新时，滚动条位置还原
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

export default router
