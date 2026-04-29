import router from './index';
import NProgress from '@/config/nprogress';
import { HOME_URL, LOGIN_URL, ROUTER_WHITE_LIST } from '@/config/config';
import { useAuthStore } from '@/store/auth';
import { useUserStore } from '@/store/user';
import { notFoundRouter, staticRoutes } from './constantRoutes';
import { resetStore } from '@/utils/storeUtil';
import { ElNotification } from 'element-plus';

let modules = import.meta.glob("../views/**/**.vue");

/**
 * @description 路由拦截
 */
router.beforeEach(async (to: any, from: any, next: any) => {
  NProgress.start();
  const userStore: any = useUserStore();
  // 1.判断是访问登陆页，有 Token 就在当前页面，没有 Token 重置路由并放行到登陆页
  if (to.path === LOGIN_URL) {
    if (userStore.token) return next(HOME_URL);
    return next();
  }
  // 2.判断访问页面是否在路由白名单(不需要登陆)地址中，如果存在直接放行
  if (ROUTER_WHITE_LIST.includes(to.path)) return next();

  // 3.判断是否有 Token，没有token跳转到登陆页面并且携带原目标路径
  if (!userStore.token) {
    return next({ path: LOGIN_URL, query: { redirect: to.fullPath } });
  }
  // 4.如果没有菜单列表，就重新请求菜单列表并添加动态路由
  const authStore = useAuthStore();
  authStore.setRouteName(to.name);
  if (!authStore.authRouterList.length) {
    await initDynamicRouter();
    return next({ ...to, replace: true });
  }
  // 5.上述条件都不满足，直接放行
  return next();
});

/**
 * @description 路由跳转结束
 * */
router.afterEach(() => {
  NProgress.done();
});

/**
 * @description 路由跳转错误
 * */
router.onError((error) => {
  NProgress.done();
  console.warn('路由错误', error.message);
});

/**
 * @description 获取动态路由
 */
const initDynamicRouter = async () => {
  const authStore: any = useAuthStore();
  const userStore: any = useUserStore();

  try {
    // 1.请求用户信息，携带路由权限信息
    await userStore.GetInfoAction();
    // 判断当前用户有没有菜单权限
    if (!authStore.authRouterList.length) {
      ElNotification({
        title: '无权限访问',
        message: '当前账号无任何菜单权限，请联系系统管理员！',
        type: 'warning',
        duration: 3000,
      });
      resetStore();
      router.replace(LOGIN_URL);
      return Promise.reject('No permission');
    }

    // 2.处理原始路由信息
    const routes = processRoutes(authStore.authRouterList, false);

    // 3.添加动态路由
    routes.forEach((route: any) => {
      router.addRoute(route);
    });

    // 4.添加notFound路由
    router.addRoute(notFoundRouter);

    // 5. 添加静态路由
    staticRoutes.forEach((route: any) => {
      router.addRoute(route);
    });

    // 5.处理subMenu数据,静态路由和动态路由拼接
    const allMenus = processRoutes(authStore.authRouterList, true);
    const menuList = getMenuList([...staticRoutes, ...allMenus]);

    authStore.setAuthMenuList(menuList);
  } catch (error) {
    // 当按钮 || 菜单请求出错时，重定向到登陆页
    resetStore();
    router.replace(LOGIN_URL);
    return Promise.reject(error);
  }
};


/**
 * @description menu过滤
 */
function getMenuList(menuList: any[]) {
  const newMenuList: any = JSON.parse(JSON.stringify(menuList));
  return newMenuList.filter((item: any) => {
    item.children?.length && (item.children = getMenuList(item.children));
    return item.meta?.isShow;
  });
}

/**
 * 处理路由信息 包含菜单和路由
 * @param routes
 * @param isMenu
 */
const processRoutes = (routes: any, isMenu: boolean) => {
  let filterRoutes = [];
  // 筛选出菜单类型
  if (isMenu) {
    filterRoutes = routes.filter((item: any) => item.type === 'menu' && item.isShow);
  } else {
    filterRoutes = routes.filter((item: any) => item.type !== 'button' && item.isShow);
  }
  return filterRoutes.map((route: any) => {
    const { name, path, component, children, type, redirect, ...others } = route;
    const newChildren = processRoutes(children, isMenu);
    return {
      name, path, redirect,
      component: modules[`../views/${component}.vue`],
      children: newChildren, meta: { ...others, title: name }
    };
  });
};
