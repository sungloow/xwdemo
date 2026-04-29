<template>
  <div>
    <ProTable
        ref="proTable"
        :columns="columns"
        :pagination="false"
        :requestApi="getScenicSpotList"
        :toolButton="false"
        :searchCol="5"
    >
      <!-- 表头 -->
      <template #tableHeader="scope">
        <permission-btn
            permission="scenic:add"
            type="primary"
            @btnSubmit="openDrawer('新增')"
        />
<!--        <permission-btn-->
<!--            permission="system:scenicSpot:remove"-->
<!--            type="danger" plain-->
<!--            :disabled="!scope.isSelected"-->
<!--            @btnSubmit="batchDelete(scope.selectedListIds)"-->
<!--        />-->
      </template>

      <!-- 表格操作 -->
      <template #operation="scope">
        <permission-btn
            permission="scenic:update"
            type="primary" link
            @btnSubmit="openDrawer('编辑', scope.row)"
        />
        <permission-btn
            permission="scenic:delete"
            type="primary" link
            @btnSubmit="handleDelete(scope.row)"
        />
      </template>

      <!-- 所属区县 -->
      <template #districtName="scope">
        {{ scope.row.districtName || '-' }}
      </template>

    </ProTable>

    <ScenicSpotDrawer ref="drawerRef"/>

  </div>
</template>

<script setup lang="ts" name="ScenicSpot">
  import { ref } from 'vue';
  import { ElMessage } from 'element-plus';
  import { useHandleData } from '@/hooks/useHandleData';
  import { getScenicSpotList, updateScenicSpot, deleteScenicSpot, addScenicSpot, getScenicSpotDetail } from '@/apis';
  import ScenicSpotDrawer from './ScenicSpotDrawer.vue';

  // 获取 ProTable 元素，调用其数据方法
  const proTable = ref();
  // 获取新增编辑组件元素，调用其数据方法
  const drawerRef = ref();

  // *表格配置项
  const columns = [
    // { type: 'selection', fixed: 'left', width: 80 },
    {
      prop: 'name', label: '景点名称',
      search: { el: 'input', key: 'name', props: { placeholder: '输入景点名称' } }
    },
    // { prop: 'districtName', label: '所属区县' },
    { prop: 'description', label: '景点描述', width: 400 },
    { prop: 'address', label: '景点地址' },
    { prop: 'operation', label: '操作', fixed: 'right', width: 200 },
  ];

  /**
   * 新增、编辑景点信息
   * @param title
   * @param rowData
   */
  const openDrawer = async (title: string, rowData: any = {}) => {
    let scenicSpot;
    if (title === '新增') {
      scenicSpot = { ...rowData };
    } else {
      // 获取景点详情
      const res: any = await getScenicSpotDetail(rowData.id);
      scenicSpot = res.data;
    }
    const params = {
      title,
      rowData: scenicSpot,
      api: title === '新增' ? addScenicSpot : updateScenicSpot,
      getTableList: proTable.value.getTableList,
    };
    drawerRef.value.acceptParams(params);
  };

  /**
   * 根据id删除景点
   * @param row
   */
  const handleDelete = async (row: any) => {
    await useHandleData(deleteScenicSpot, row.id, `删除${row.name}景点`);
    proTable.value.getTableList();
  };

  // *批量删除景点
  const batchDelete = async (ids: string[]) => {
    await useHandleData(deleteScenicSpot, ids, '删除所选景点信息');
    proTable.value.clearSelection();
    proTable.value.getTableList();
  };

</script>
