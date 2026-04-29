<template>
  <div>
    <ProTable
        ref="proTable"
        :columns="columns"
        :requestApi="getDistrictList"
        :toolButton="false"
        :pagination="false"
        :searchCol="5"
    >
      <!-- 表头 -->
      <template #tableHeader="scope">
        <permission-btn
            permission="system:district:add"
            type="primary"
            @btnSubmit="openDrawer('新增')"
        />
<!--        <permission-btn-->
<!--            permission="system:district:remove"-->
<!--            type="danger" plain-->
<!--            :disabled="!scope.isSelected"-->
<!--            @btnSubmit="batchDelete(scope.selectedListIds)"-->
<!--        />-->
      </template>

      <!-- 表格操作 -->
      <template #operation="scope">
        <permission-btn
            permission="system:district:update"
            type="primary" link
            @btnSubmit="openDrawer('编辑', scope.row)"
        />
        <permission-btn
            permission="system:district:delete"
            type="primary" link
            @btnSubmit="handleDelete(scope.row)"
        />
      </template>
    </ProTable>

    <DistrictDrawer ref="drawerRef"/>

  </div>
</template>

<script setup lang="ts" name="District">
  import { ref } from 'vue';
  import { ElMessage } from 'element-plus';
  import { useHandleData } from '@/hooks/useHandleData';
  import { getDistrictList, updateDistrict, deleteDistrict, addDistrict, getDistrictDetail } from '@/apis';
  import DistrictDrawer from './DistrictDrawer.vue';

  // 获取 ProTable 元素，调用其数据方法
  const proTable = ref();
  // 获取新增编辑组件元素，调用其数据方法
  const drawerRef = ref();

  // *表格配置项
  const columns = [
    // { type: 'selection', fixed: 'left', width: 80 },
    {
      prop: 'name', label: '区县名称',
      search: { el: 'input', key: 'name', props: { placeholder: '输入区县名称' } }
    },
    {
      prop: 'code', label: '区县代码',
      search: { el: 'input', key: 'code', props: { placeholder: '输入区县代码' } }
    },
    { prop: 'description', label: '区县描述' },
    { prop: 'operation', label: '操作', fixed: 'right', width: 200 },
  ];

  /**
   * 新增、编辑区县信息
   * @param title
   * @param rowData
   */
  const openDrawer = async (title: string, rowData: any = {}) => {
    let district;
    if (title === '新增') {
      district = { ...rowData };
    } else {
      // 获取区县详情
      const res: any = await getDistrictDetail(rowData.id);
      district = res.data;
    }
    const params = {
      title,
      rowData: district,
      api: title === '新增' ? addDistrict : updateDistrict,
      getTableList: proTable.value.getTableList,
    };
    drawerRef.value.acceptParams(params);
  };

  /**
   * 根据id删除区县
   * @param row
   */
  const handleDelete = async (row: any) => {
    await useHandleData(deleteDistrict, row.id, `删除${row.name}区县`);
    proTable.value.getTableList();
  };

  // *批量删除区县
  const batchDelete = async (ids: string[]) => {
    await useHandleData(deleteDistrict, ids, '删除所选区县信息');
    proTable.value.clearSelection();
    proTable.value.getTableList();
  };

</script>
