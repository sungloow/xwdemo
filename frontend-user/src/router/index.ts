import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: '首页' },
    },
    {
      path: '/login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录', guest: true },
    },
    {
      path: '/register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { title: '注册', guest: true },
    },
    {
      path: '/checkin/new',
      component: () => import('@/views/CheckinNewView.vue'),
      meta: { title: '发起打卡', requiresAuth: true },
    },
    {
      path: '/my-checkins',
      component: () => import('@/views/MyCheckinsView.vue'),
      meta: { title: '我的打卡', requiresAuth: true },
    },
    {
      path: '/profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { title: '个人中心', requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
  scrollBehavior() {
    return { top: 0 };
  },
});

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title ?? '山城打卡'} - 重庆城市打卡`;
  const token = localStorage.getItem('token');
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } });
  } else if (to.meta.guest && token) {
    next('/');
  } else {
    next();
  }
});

export default router;
