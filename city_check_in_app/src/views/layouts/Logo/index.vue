<template>
  <div class="logo-container flex-center">
    <a href="/">
      <!--      <img class="logo" alt="logo" src="../../../assets/logo.svg" />-->
      <h1 class="title" v-if="!collapse">城市打卡后台管理系统</h1>
    </a>
  </div>
</template>

<script lang="ts">
  import { defineComponent, computed } from 'vue';
  import { useSettingsStore } from '@/store/settings';

  export default defineComponent({
    setup() {
      const settingsStore = useSettingsStore();
      const collapse = computed(() => settingsStore.collapse);
      return { collapse };
    },
  });
</script>

<style scoped lang="scss">
  .logo-container {
    position: relative;
    display: flex;
    justify-content: center;

    height: 60px;
    overflow: hidden;
    line-height: 60px;
    background: transparent;

    .title {
      display: inline-block;
      font-family: Avenir, 'Helvetica Neue', Arial, Helvetica, sans-serif;
      font-size: 20px;
      font-weight: 600;
      color: #fff;
      vertical-align: middle;
    }

    .logo {
      display: inline-block;
      width: 32px;
      height: 32px;
      vertical-align: middle;
    }
  }
</style>
