<template>
  <section class="app-mian-height">
    <router-view v-slot="{ Component, route }" v-if="isShow">
      <transition appear name="fade-transform" mode="out-in">
        <!-- include/exclude 值是组件中的 name 命名，而不是路由中的组件 name 命名； -->
        <keep-alive :include="cachedViewsRef" max="10">
          <component :is="Component" :key="route.path"/>
        </keep-alive>
      </transition>
    </router-view>
  </section>
</template>

<script lang="ts">
  import { defineComponent, nextTick, ref, watch } from 'vue';
  import { useSettingsStore } from '@/store/settings';
  import { useTabsBarStore } from '@/store/tabsBar';

  export default defineComponent({
    setup() {
      const tabsBarStore = useTabsBarStore();
      const cachedViewsRef = ref(tabsBarStore.cachedViews);
      watch(() => tabsBarStore.cachedViews, () => {
        cachedViewsRef.value = (tabsBarStore.cachedViews).map((item: any) => item.componentName);
      }, { deep: true });

      const settingsStore = useSettingsStore();
      const isShow = ref(true);
      watch(
        () => settingsStore.refresh,
        () => {
          isShow.value = false;
          nextTick(() => {
            isShow.value = true;
          });
        },
      );
      return { isShow, cachedViewsRef };
    },
  });
</script>

<style scoped lang="scss">
  .app-mian-height {
    min-height: $base-app-main-height;

    padding: 20px;
    background-color: inherit;
  }
</style>
