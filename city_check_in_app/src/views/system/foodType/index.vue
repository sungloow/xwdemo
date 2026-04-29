<template>
  <div>
    <ProTable
        ref="proTable"
        :columns="columns"
        :requestApi="getFoodTypeList"
        :toolButton="false"
        :searchCol="5"
        :pagination="false"
    >
      <!-- 表头 -->
      <template #tableHeader="scope">
        <permission-btn
            permission="system:foodType:add"
            type="primary"
            @btnSubmit="openDrawer('新增')"
        />
<!--        <permission-btn-->
<!--            permission="system:foodType:delete"-->
<!--            type="danger" plain-->
<!--            :disabled="!scope.isSelected"-->
<!--            @btnSubmit="batchDelete(scope.selectedListIds)"-->
<!--        />-->
      </template>

      <!-- 表格操作 -->
      <template #operation="scope">
        <permission-btn
            permission="system:foodType:update"
            type="primary" link
            @btnSubmit="openDrawer('编辑', scope.row)"
        />
        <permission-btn
            permission="system:foodType:delete"
            type="primary" link
            @btnSubmit="handleDelete(scope.row)"
        />
      </template>
    </ProTable>

    <FoodTypeDrawer ref="drawerRef"/>

  </div>
</template>

<script setup lang="ts" name="FoodType">
  import { ref } from 'vue';
  import { ElMessage } from 'element-plus';
  import { useHandleData } from '@/hooks/useHandleData';
  import { getFoodTypeList, updateFoodType, deleteFoodType, addFoodType, getFoodTypeDetail } from '@/apis';
  import FoodTypeDrawer from './FoodTypeDrawer.vue';

  // 获取 ProTable 元素，调用其数据方法
  const proTable = ref();
  // 获取新增编辑组件元素，调用其数据方法
  const drawerRef = ref();

  // *表格配置项
  const columns = [
    // { type: 'selection', fixed: 'left', width: 80 },
    {
      prop: 'name', label: '美食种类名称',
      search: { el: 'input', key: 'name', props: { placeholder: '输入美食种类名称' } }
    },
    { prop: 'description', label: '美食种类描述' },
    { prop: 'operation', label: '操作', fixed: 'right', width: 200 },
  ];

  /**
   * 新增、编辑美食种类信息
   * @param title
   * @param rowData
   */
  const openDrawer = async (title: string, rowData: any = {}) => {
    let foodType;
    if (title === '新增') {
      foodType = { ...rowData };
    } else {
      // 获取美食种类详情
      const res: any = await getFoodTypeDetail(rowData.id);
      foodType = res.data;
    }
    const params = {
      title,
      rowData: foodType,
      api: title === '新增' ? addFoodType : updateFoodType,
      getTableList: proTable.value.getTableList,
    };
    drawerRef.value.acceptParams(params);
  };

  /**
   * 根据id删除美食种类
   * @param row
   */
  const handleDelete = async (row: any) => {
    await useHandleData(deleteFoodType, row.id, `删除${row.name}美食种类`);
    proTable.value.getTableList();
  };

  // *批量删除美食种类
  const batchDelete = async (ids: string[]) => {
    await useHandleData(deleteFoodType, ids, '删除所选美食种类信息');
    proTable.value.clearSelection();
    proTable.value.getTableList();
  };

</script>
