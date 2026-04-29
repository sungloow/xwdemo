<template>
  <el-breadcrumb class="app-breadcrumb" separator-icon="ArrowRight">
      <el-breadcrumb-item v-for="(item, index) in matched" :key="item.path">
        <span v-if="index === matched.length - 1" class="no-redirect">
          {{ item.meta.title }}
        </span>
        <a v-else>{{ item.meta.title }}</a>
      </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script lang="ts">
  import { defineComponent, computed } from 'vue';
  import { useRoute } from 'vue-router';

  export default defineComponent({
    setup() {
      const route = useRoute();

      const matched = computed(() =>
        route.matched.filter(
          (item) =>
            item.meta &&
            item.meta.title &&
            item.children.length !== 1,
        ),
      );

      return {
        matched,
      };
    },
  });
</script>

<style scoped lang="scss">
  .app-breadcrumb {
    margin-left: 20px;
  }

  ::v-deep(.el-breadcrumb__inner) {
    display: flex;

    > i {
      margin-right: 3px;
    }
  }
</style>
