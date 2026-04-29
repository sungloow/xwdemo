import { HOME_URL, LOGIN_URL } from '@/config/config';

/**
 * @description 静态路由
 */
export const staticRoutes: any[] = [
  {
    path: '/',
    redirect: HOME_URL,
    meta: { show: false },
  },
  {
    path: LOGIN_URL,
    name: 'login',
    meta: { show: false },
    component: () => import('@/views/login/index.vue'),
  },
  {
    path: HOME_URL,
    name: 'frontHome',
    meta: { show: false },
    component: () => import('@/views/front/home.vue'),
  },
  {
    path: '/register',
    name: 'register',
    meta: { show: false },
    component: () => import('@/views/login/register.vue'),
  },
  {
    path: '/404',
    name: '404',
    meta: { show: false },
    component: () => import('@/views/error/error-404.vue'),
  },
  // 此路由防止控制台出现No match found for location with path的警告
  {
    path: '/:catchAll(.*)',
    meta: { show: false },
    component: () => import('@/views/error/error-404.vue')
  }
];

/**
 * @description 路由未找到
 */
export const notFoundRouter = {
  path: '/:pathMatch(.*)*',
  name: 'notFound',
  redirect: '404',
};
