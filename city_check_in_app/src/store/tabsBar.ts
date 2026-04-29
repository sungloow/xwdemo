import { defineStore } from 'pinia';
import router from '@/router/index';

export const useTabsBarStore = defineStore({
  // id: 必须的，在所有 Store 中唯一
  id: 'app-tabsBar',
  // state: 返回对象的函数
  state: (): any => ({
    activeTabsValue: '/index',
    // 选中过的路由表
    visitedViews: [],
    // 缓存的组件name 使用 keepAlive 时的缓存
    cachedViews: []
  }),

  getters: {},
  // 可以同步 也可以异步
  actions: {
    // 设置当前选中的tab
    setTabsMenuValue(val: string) {
      this.activeTabsValue = val;
    },

    addView(view: any) {
      this.addVisitedView(view);
    },

    addVisitedView(view: any) {
      this.setTabsMenuValue(view.path);
      if (this.visitedViews.some((v: any) => v.path === view.path) || !view.meta) {
        return;
      }
      this.visitedViews.push(
        Object.assign({}, view, {
          title: view.meta.title || 'no-name',
        }),
      );
      // 新增缓存
      if (view.name && view.meta.keepAlive) {
        this.cachedViews.push({ componentName: view.meta.componentName, path: view.path });
      }
    },

    delVisitedView(path: string) {
      return new Promise((resolve) => {
        this.visitedViews = this.visitedViews.filter((v: any) => {
          if (!v.meta) return;
          return v.path !== path || v.meta.affix;
        });
        // 删除缓存
        this.cachedViews = this.cachedViews.filter((v: any) => {
          return v.path !== path || v.meta?.affix;
        });
        resolve([...this.visitedViews]);
        resolve([...this.cachedViews]);
      });
    },

    /**
     * 关闭当前tab，重置tabs数组
     * @param activeTabPath
     */
    delView(activeTabPath: any) {
      return new Promise((resolve) => {
        this.delVisitedView(activeTabPath);
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews],
        });
      });
    },
    /**
     * 关闭当前tab，找到下一个选中的tab
     * @param activeTabPath
     */
    toLastView(activeTabPath: string) {
      const index = this.visitedViews.findIndex((item: any) => item.path === activeTabPath);
      const nextTab = this.visitedViews[index + 1] || this.visitedViews[index - 1];
      if (!nextTab) return;
      router.push(nextTab.path);
      this.addVisitedView(nextTab);
    },

    /**
     * 关闭所有tab
     */
    delAllViews() {
      this.visitedViews = this.visitedViews.filter(
        (v: any) => v.meta && v.meta.affix,
      );
      this.cachedViews = [];
    },

    /**
     * 关闭其他tab
     * @param path
     */
    delOtherViews(path: string) {
      this.visitedViews = this.visitedViews.filter((item: any) => {
        return item.path === path || (item.meta && item.meta.affix);
      });
      this.cachedViews = this.visitedViews.filter((item: any) => {
        return item.path === path || (item.meta && item.meta.affix);
      });
    },

    goHome() {
      this.activeTabsValue = '/index';
      router.push({ path: '/index' });
    }
  },
});
