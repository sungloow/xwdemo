<template>
  <div class="card table-search" v-if="columns.length">
    <el-form ref="formRef" :model="searchParam">
      <Grid :gap="[20, 0]" :cols="searchCol">
        <GridItem
            v-for="(item, index) in columns"
            :key="item.prop"
            v-bind="getResponsive(item)"
            :index="index"
        >
          <el-form-item :label="`${item.label} :`">
            <SearchFormItem :column="item" :searchParam="searchParam"/>
          </el-form-item>
        </GridItem>

        <GridItem suffix>
          <div class="operation">
            <el-button type="primary" icon="Search" @click="search">搜索</el-button>
            <el-button icon="Delete" @click="reset">重置</el-button>
          </div>
        </GridItem>
      </Grid>
    </el-form>
  </div>
</template>

<script setup lang="ts" name="SearchForm">
  import { ref, computed } from 'vue';
  import { Search } from '@element-plus/icons-vue';
  import type { ColumnProps } from '@/components/ProTable/src/types';
  import Grid from '@/components/Grid/src/Grid.vue';
  import GridItem from '@/components/Grid/src/components/GridItem.vue';
  import SearchFormItem from './components/SearchFormItem.vue';

  interface ProTableProps {
    columns?: ColumnProps[] // 搜索配置列
    searchParam?: { [key: string]: any } // 搜索参数
    searchCol: number | Record<any, number>
    search: (params: any) => void // 搜索方法
    reset: (params: any) => void // 重置方法
  }

  // 默认值
  const props = withDefaults(defineProps<ProTableProps>(), {
    columns: () => [],
    searchParam: () => ({}),
  });

  // 获取响应式设置
  const getResponsive = (item: ColumnProps) => {
    return {
      span: item.search?.span,
      offset: item.search?.offset ?? 0,
      xs: item.search?.xs,
      sm: item.search?.sm,
      md: item.search?.md,
      lg: item.search?.lg,
      xl: item.search?.xl,
    };
  };


</script>

<style scoped lang="scss">
  @import './style/index';
</style>
