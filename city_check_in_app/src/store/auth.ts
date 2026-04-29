import { defineStore } from 'pinia';

export const useAuthStore = defineStore({
  // id: 必须的，在所有 Store 中唯一
  id: 'app-auth',
  state: () => ({
    // 当前页面的 router name，用来做按钮权限筛选
    routeName: '',
    // 后端返回的用户角色列表
    roles: [],
    // 后端返回的按钮权限列表
    authButtonList: [],
    // 后端返回的路由列表
    authRouterList: [],
    // 处理之后侧边栏菜单列表
    authMenuList: [],
    // 用户隐私信息 角色类型 角色id 用户id等
    authInfo: {}
  }),

  actions: {
    // setRouteName
    setRouteName(name: string) {
      this.routeName = name;
    },

    resetAuth() {
      this.authRouterList = [];
    },

    // 设置用户权限信息
    setAuth(authInfo: any) {
      // this.roles = authInfo.roles;
      this.authButtonList = authInfo.buttons;
      this.authRouterList = authInfo.routes;
    },
    // 设置侧边栏菜单
    setAuthMenuList(list: any) {
      this.authMenuList = list;
    },

    // 设置用户信息
    setAuthInfo(info: any) {
      this.authInfo = info;
    }
  }
});
