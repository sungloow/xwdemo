<template>
  <div class="tabs-bar-container">
    <div class="tabs-content">
      <el-tabs
          type="card"
          v-model="activeTabsValue"
          @tab-click="tabClick"
          @tab-remove="removeTab"
      >
        <el-tab-pane
            v-for="item in visitedViews"
            type="card"
            :key="item.path"
            :path="item.path"
            :label="item.title"
            :name="item.path"
            :closable="!(item.meta && item.meta.affix)"
        >
          <template #label>
            <el-icon
                size="16"
                class="tabs-icon"
                v-if="item.meta && item.meta.icon">
              <component :is="item.meta.icon"></component>
            </el-icon>
            {{ item.title }}
          </template>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="tabs-action">
      <el-dropdown trigger="hover">
        <el-icon color="rgba(0, 0, 0, 0.65)" :size="20">
          <Menu/>
        </el-icon>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="closeCurrentTab">
              <el-icon :size="14">
                <FolderRemove/>
              </el-icon>
              关闭当前
            </el-dropdown-item>
            <el-dropdown-item @click="closeOtherTab">
              <el-icon :size="14">
                <Close/>
              </el-icon>
              关闭其他
            </el-dropdown-item>
            <el-dropdown-item @click="closeAllTab">
              <el-icon :size="14">
                <FolderDelete/>
              </el-icon>
              关闭所有
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script lang="ts">
  import { computed, defineComponent, ref, onMounted, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import path from 'path-browserify';
  import { useTabsBarStore } from '@/store/tabsBar';
  import { useAuthStore } from '@/store/auth';

  export default defineComponent({
    setup() {
      const tabsBarStore = useTabsBarStore();
      const authStore = useAuthStore();
      const routes = computed(() => authStore.authMenuList);
      const visitedViews = computed(() => tabsBarStore.visitedViews);
      const route = useRoute();
      const router = useRouter();
      let affixTags = ref([]);

      // 添加当前路由
      const addTags = () => {
        const { name } = route;
        if (name === 'Login') {
          return;
        }
        if (name) {
          tabsBarStore.addView(route);
        }
        return false;
      };

      const filterAffixTags = (routes: any[], basePath = '/') => {
        let tags = [] as any;
        routes.forEach((route) => {
          if (route.meta && route.meta.affix) {
            // 获取 path
            const tagPath = path.resolve(basePath, route.path);
            tags.push({
              fullPath: tagPath,
              path: tagPath,
              name: route.name,
              meta: { ...route.meta },
            });
          }
          if (route.children) {
            const tempTags = filterAffixTags(route.children, route.path);
            if (tempTags.length >= 1) {
              tags = [...tags, ...tempTags];
            }
          }
        });
        return tags;
      };

      /**
       * @description: 拿到需要固定的路由表，添加进 store
       * @author: codeBo
       */
      const initTags = () => {
        let routesNew = routes.value;
        let affixTag = (affixTags.value = filterAffixTags(routesNew));
        for (const tag of affixTag) {
          if (tag.name) {
            tabsBarStore.addVisitedView(tag);
          }
        }
      };

      onMounted(() => {
        initTags();
        addTags();
      });

      watch(route, () => addTags());

      const activeTabsValue = computed({
        get: () => {
          return tabsBarStore.activeTabsValue;
        },
        set: (val) => {
          tabsBarStore.setTabsMenuValue(val);
        },
      });

      // 删除以后切换到下一个
      function toLastView(activeTabPath: string) {
        let index = visitedViews.value.findIndex((item: any) => item.path === activeTabPath);
        const nextTab =
          visitedViews.value[index + 1] || visitedViews.value[index - 1];
        if (!nextTab) return;
        router.push(nextTab.path);
        tabsBarStore.addVisitedView(nextTab);
      }

      // 点击事件
      const tabClick = (tabItem: any) => {
        let path = tabItem.props.name as string;
        router.push(path);
      };

      const isActive = (path: string): boolean => {
        return path === route.path;
      };

      const removeTab = async (activeTabPath: any): Promise<any> => {
        if (isActive(activeTabPath as string)) {
          toLastView(activeTabPath as string);
        }
        await tabsBarStore.delView(activeTabPath);
      };

      /**
       * 关闭当前tab
       */
      const closeCurrentTab = () => {
        tabsBarStore.toLastView(route.path);
        tabsBarStore.delView(route.path);
      };

      /**
       * 关闭其他tabs
       */
      const closeOtherTab = () => {
        tabsBarStore.delOtherViews(route.path);
      };

      /**
       * 关闭所有tabs
       */
      const closeAllTab = async () => {
        tabsBarStore.delAllViews();
        tabsBarStore.goHome();
      };
      return {
        activeTabsValue,
        tabClick,
        removeTab,
        visitedViews,
        closeCurrentTab,
        closeOtherTab,
        closeAllTab,
      };
    },
  });
</script>

<style scoped lang="scss">
  @import 'index';
</style>
